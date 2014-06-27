trackerCapture.controller('EnrollmentController',
        function($rootScope,
                $scope,  
                $filter,
                $timeout,
                storage,
                ProgramStageFactory,
                AttributesFactory,
                CurrentSelection,
                TEIService,
                EnrollmentService,
                TranslationService,
                DialogService) {
    TranslationService.translate();

    
    //listen for the selected items
    $scope.$on('selectedEntity', function(event, args) {   
        //programs for enrollment
        $scope.enrollments = [];
        $scope.showEnrollmentDiv = false;
        $scope.showSchedulingDiv = false;    
        $scope.selectedProgram = null;
        $scope.selectedEnrollment = null;
        $scope.newEnrollment = {};
        
        var selections = CurrentSelection.get();
        $scope.selectedEntity = selections.tei; 
        $scope.selectedProgram = selections.pr;
        $scope.programExists = args.programExists;
        
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        
        if($scope.selectedProgram){ 
            EnrollmentService.getByEntityAndProgram($scope.selectedEntity.trackedEntityInstance, $scope.selectedProgram.id).then(function(data){
                $scope.enrollments = data.enrollmentList;
                $scope.loadEvents();                
            });
        }
        else{
            $scope.broadCastSelections();
        }
    }); 
    
    $scope.loadEvents = function() {
        
        if($scope.selectedProgram){
          
            //check for possible enrollment
            $scope.selectedEnrollment = null;
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

            $scope.broadCastSelections();
        }
        else{
            $scope.broadCastSelections();
        }
    };
        
    $scope.showEnrollment = function(){        
        $scope.showEnrollmentDiv = !$scope.showEnrollmentDiv;
    };
    
    $scope.broadCastSelections = function(){
        CurrentSelection.set({tei: $scope.selectedEntity, pr: $scope.selectedProgram, enrollment: $scope.selectedEnrollment});
        $timeout(function() { 
            $rootScope.$broadcast('dashboard', {});
            $rootScope.$broadcast('notesController', {});
        }, 100);
    };
    
    $scope.showScheduling = function(){        
        $scope.showSchedulingDiv = !$scope.showSchedulingDiv;
    };
    
    $scope.enroll = function(){    
        
        var tei = angular.copy($scope.selectedEntity);
        tei.attributes = [];
        
        //existing attributes
        angular.forEach($scope.selectedEntity.attributes, function(attribute){
            if(!angular.isUndefined(attribute.value)){
                tei.attributes.push({attribute: attribute.attribute, value: attribute.value});
            } 
        });
        
        //get enrollment attributes and their values - new attributes because of enrollment
        angular.forEach($scope.attributesForEnrollment, function(attribute){
            if(!angular.isUndefined(attribute.value)){
                tei.attributes.push({attribute: attribute.id, value: attribute.value});
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
                    
                    enrollment.enrollment = enrollmentResponse.reference;
                    CurrentSelection.set({tei: $scope.selectedEntity, pr: $scope.selectedProgram, enrollment: enrollment});                    
                    $timeout(function() { 
                        $rootScope.$broadcast('dashboard', {});
                    }, 100); 
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