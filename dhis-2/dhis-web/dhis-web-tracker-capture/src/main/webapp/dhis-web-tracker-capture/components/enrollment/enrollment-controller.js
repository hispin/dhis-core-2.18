trackerCapture.controller('EnrollmentController',
        function($rootScope,
                $scope,  
                $filter,
                storage,
                ProgramFactory,
                AttributesFactory,
                CurrentSelection,
                TEIService,
                EnrollmentService,
                TranslationService,
                DialogService) {

    //programs for enrollment
    $scope.enrollments = [];
    $scope.programs = []; 
    $scope.showEnrollmentDiv = false;
    $scope.showSchedulingDiv = false;
    
    TranslationService.translate();
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    
    //listen for the selected items
    $scope.$on('selectedEntity', function(event, args) {   
        $scope.newEnrollment = {};
        var selections = CurrentSelection.get();
        $scope.selectedEntity = selections.tei;    
        
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        
        angular.forEach(ProgramFactory.getAll(), function(program){
            if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id) &&
               program.trackedEntity.id === $scope.selectedEntity.trackedEntity){
                $scope.programs.push(program);
            }
        });
        
        EnrollmentService.get($scope.selectedEntity.trackedEntityInstance).then(function(data){
            $scope.enrollments = data.enrollmentList;  
        });
        
        if(selections.pr){       
            angular.forEach($scope.programs, function(program){
                if(selections.pr.id === program.id){
                    $scope.selectedProgram = program;
                    $scope.loadEvents();
                }
            });
        }
    }); 
    
    $scope.loadEvents = function() {
        
        if($scope.selectedProgram){
            
            //check for possible enrollment
            $scope.selectedEnrollment = '';
            angular.forEach($scope.enrollments, function(enrollment){
                if(enrollment.program === $scope.selectedProgram.id ){
                    $scope.selectedEnrollment = enrollment;
                }
            }); 
            
            if($scope.selectedEnrollment){//enrollment exists
                $scope.selectedEnrollment.dateOfIncident = $filter('date')($scope.selectedEnrollment.dateOfIncident, 'yyyy-MM-dd');
            }
            else{//prepare for possible enrollment
                $scope.attributesForEnrollment = AttributesFactory.getMissingAttributesForEnrollment($scope.selectedEntity, $scope.selectedProgram);
            }

            $scope.programStages = [];   
            var incidentDate = $scope.selectedEnrollment ? $scope.selectedEnrollment.dateOfIncident : new Date();
            
            angular.forEach($scope.selectedProgram.programStages, function(stage){
                var ps = storage.get(stage.id);
                ps.dueDate = moment(moment(incidentDate).add('d', ps.minDaysFromStart), 'YYYY-MM-DD')._d;
                ps.dueDate = Date.parse(ps.dueDate);
                ps.dueDate= $filter('date')(ps.dueDate, 'yyyy-MM-dd');
                $scope.programStages.push(ps);               
            });

            $rootScope.$broadcast('dashboard', {selectedEntity: $scope.selectedEntity,
                                                selectedOrgUnit: $scope.selectedOrgUnit,
                                                selectedProgramId: $scope.selectedProgram.id,
                                                selectedEnrollment: $scope.selectedEnrollment});
        }
        else{
            $scope.selectedProgram = '';
            $scope.selectedEnrollment = '';
            $rootScope.$broadcast('dashboard', {selectedEntity: $scope.selectedEntity,
                                                selectedOrgUnit: $scope.selectedOrgUnit,
                                                selectedProgramId: $scope.selectedProgram.id,
                                                selectedEnrollment: $scope.selectedEnrollment});
        }
    };
        
    $scope.showEnrollment = function(){        
        $scope.showEnrollmentDiv = !$scope.showEnrollmentDiv;
        
        console.log('Enrollment', $scope.selectedEntity, ' ', $scope.selectedProgram);
    };
    
    $scope.showScheduling = function(){        
        $scope.showSchedulingDiv = !$scope.showSchedulingDiv;
        
        console.log('Scheduling', $scope.selectedEntity, ' ', $scope.selectedProgram);
    };
    
    $scope.enroll = function(){    
        
        var tei = angular.copy($scope.selectedEntity);
        tei.attributes = [];
        
        //get enrollment attributes and their values - new attributes because of enrollment
        angular.forEach($scope.attributesForEnrollment, function(attribute){
            if(!angular.isUndefined(attribute.value)){
                //$scope.selectedEntity.attributes.push({attribute: attribute.id, value: attribute.value, type: attribute.valueType, displayName: attribute.name});
                tei.attributes.push({attribute: attribute.id, value: attribute.value});
            } 
        });
        
        //existing attributes
        angular.forEach($scope.selectedEntity.attributes, function(attribute){
            if(!angular.isUndefined(attribute.value)){
                tei.attributes.push({attribute: attribute.attribute, value: attribute.value});
            } 
        });
        
        var enrollment = {trackedEntityInstance: tei.trackedEntityInstance,
                            program: $scope.selectedProgram.id,
                            status: 'ACTIVE',
                            dateOfEnrollment: $scope.newEnrollment.dateOfEnrollment,
                            dateOfIncident: $scope.newEnrollment.dateOfIncident ? $scope.newEnrollment.dateOfIncident : $scope.newEnrollment.dateOfEnrollment
                        };
                        
        TEIService.update(tei).then(function(updateResponse){
            
            if(updateResponse.status === 'SUCCESS'){
                
                //registration is successful and continue for enrollment               
                EnrollmentService.enroll(enrollment).then(function(enrollmentResponse){
                    if(enrollmentResponse.status !== 'SUCCESS'){
                        //enrollment has failed
                        var dialogOptions = {
                                headerText: 'enrollment_error',
                                bodyText: data.description
                            };
                        DialogService.showDialog({}, dialogOptions);
                        return;
                    }
                    
                    //update tei attributes without refetching from the server
                    angular.forEach($scope.attributesForEnrollment, function(attribute){
                        if(!angular.isUndefined(attribute.value)){
                             if(attribute.type === 'number' && !isNaN(parseInt(attribute.value))){
                                 attribute.value = parseInt(attribute.value);
                             }
                            $scope.selectedEntity.attributes.push({attribute: attribute.id, value: attribute.value, type: attribute.valueType, displayName: attribute.name});                            
                        } 
                    });
                });
               
            }
            else{
                //update has failed
                var dialogOptions = {
                        headerText: 'registration_error',
                        bodyText: updateResponse.description
                    };
                DialogService.showDialog({}, dialogOptions);
                return;
            }            
        });
        
        console.log('tei', tei, ' ');
        console.log('scope', $scope.selectedEntity, ' ');
    };
    
    $scope.cancelEnrollment = function(){
        $scope.selectedProgram = '';
    };
});