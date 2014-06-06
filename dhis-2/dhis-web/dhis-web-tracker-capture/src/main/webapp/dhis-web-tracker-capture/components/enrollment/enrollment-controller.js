trackerCapture.controller('EnrollmentController',
        function($rootScope,
                $scope,  
                $filter,
                storage,
                ProgramFactory,
                ProgramStageFactory,
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
    $scope.selectedProgram = '';
    $scope.selectedEnrollment = '';
    
    TranslationService.translate();
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    
    //listen for the selected items
    $scope.$on('selectedEntity', function(event, args) {   
        $scope.newEnrollment = {};
        var selections = CurrentSelection.get();
        $scope.selectedEntity = selections.tei;        
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        
        ProgramFactory.getAll().then(function(programs){  
            
            angular.forEach(programs, function(program){
                if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id) &&
                   program.trackedEntity.id === $scope.selectedEntity.trackedEntity){
                    $scope.programs.push(program);
                }
            });

            EnrollmentService.get($scope.selectedEntity.trackedEntityInstance).then(function(data){
                $scope.enrollments = data.enrollmentList;  
                if(selections.pr){   
                    angular.forEach($scope.programs, function(program){
                        if(selections.pr.id === program.id){
                            $scope.selectedProgram = program;
                            $scope.loadEvents();
                        }
                    });
                }

                CurrentSelection.set({tei: $scope.selectedEntity, pr: $scope.selectedProgram, enrollment: $scope.selectedEnrollment});
                $rootScope.$broadcast('dashboard', {});
            });                           
        });        
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
                AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){
                    
                    $scope.attributesForEnrollment = [];
                    for(var i=0; i<atts.length; i++){
                        var exists = false;
                        for(var j=0; j<$scope.selectedEntity.attributes.length && !exists; j++){
                            if(atts[i].id === $scope.selectedEntity.attributes[j].attribute){
                                exists = true;
                            }
                        }
                        if(!exists){
                            $scope.attributesForEnrollment.push(atts[i]);
                        }
                    }
                });                
            }

            $scope.programStages = [];   
            var incidentDate = $scope.selectedEnrollment ? $scope.selectedEnrollment.dateOfIncident : new Date();
            
            ProgramStageFactory.getByProgram($scope.selectedProgram).then(function(stages){
                angular.forEach(stages, function(stage){                
                    stage.dueDate = moment(moment(incidentDate).add('d', stage.minDaysFromStart), 'YYYY-MM-DD')._d;
                    stage.dueDate = Date.parse(stage.dueDate);
                    stage.dueDate= $filter('date')(stage.dueDate, 'yyyy-MM-dd');
                    $scope.programStages.push(stage);               
                });                
            });

            CurrentSelection.set({tei: $scope.selectedEntity, pr: $scope.selectedProgram, enrollment: $scope.selectedEnrollment});
            $rootScope.$broadcast('dashboard', {});
        }
        else{
            $scope.selectedProgram = '';
            $scope.selectedEnrollment = '';
            CurrentSelection.set({tei: $scope.selectedEntity, pr: $scope.selectedProgram, enrollment: $scope.selectedEnrollment});
            $rootScope.$broadcast('dashboard', {});
        }
    };
        
    $scope.showEnrollment = function(){        
        $scope.showEnrollmentDiv = !$scope.showEnrollmentDiv;
    };
    
    $scope.showScheduling = function(){        
        $scope.showSchedulingDiv = !$scope.showSchedulingDiv;
    };
    
    $scope.enroll = function(){    
        
        var tei = angular.copy($scope.selectedEntity);
        tei.attributes = [];
        
        //get enrollment attributes and their values - new attributes because of enrollment
        angular.forEach($scope.attributesForEnrollment, function(attribute){
            if(!angular.isUndefined(attribute.value)){
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
    };
    
    $scope.cancelEnrollment = function(){
        $scope.selectedProgram = '';
    };
});