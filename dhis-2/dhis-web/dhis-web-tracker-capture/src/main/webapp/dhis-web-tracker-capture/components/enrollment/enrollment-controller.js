trackerCapture.controller('EnrollmentController',
        function($rootScope,
                $scope,  
                $filter,
                $timeout,
                storage,
                AttributesFactory,
                CurrentSelection,
                TEIService,
                EnrollmentService,
                TranslationService,
                DialogService) {
    TranslationService.translate();

    
    //listen for the selected items
    $scope.$on('selectedItems', function(event, args) {   
        //programs for enrollment
        $scope.enrollments = [];
        $scope.showEnrollmentDiv = false;
        $scope.showSchedulingDiv = false;    
        $scope.selectedEnrollment = null;
        $scope.newEnrollment = {};
        
        var selections = CurrentSelection.get();
        $scope.selectedTei = angular.copy(selections.tei); 
        $scope.selectedEntity = selections.te;
        $scope.selectedProgram = selections.pr;
        $scope.programExists = args.programExists;
        
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        
        if($scope.selectedProgram){ 
            EnrollmentService.getByEntityAndProgram($scope.selectedTei.trackedEntityInstance, $scope.selectedProgram.id).then(function(data){
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
            angular.forEach($scope.enrollments, function(enrollment){
                if(enrollment.program === $scope.selectedProgram.id ){
                    $scope.selectedEnrollment = enrollment;
                }
            }); 
            
            if($scope.selectedEnrollment){//enrollment exists
                $scope.selectedEnrollment.dateOfIncident = $filter('date')($scope.selectedEnrollment.dateOfIncident, 'yyyy-MM-dd');
                
                $scope.programStages = [];   
                
                var incidentDate = $scope.selectedEnrollment ? $scope.selectedEnrollment.dateOfIncident : new Date();

                angular.forEach($scope.selectedProgram.programStages, function(stage){                
                    stage.dueDate = moment(moment(incidentDate).add('d', stage.minDaysFromStart), 'YYYY-MM-DD')._d;
                    stage.dueDate = Date.parse(stage.dueDate);
                    stage.dueDate= $filter('date')(stage.dueDate, 'yyyy-MM-dd');
                    $scope.programStages.push(stage);               
                });
            }
            else{//prepare for possible enrollment
                AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){
                    $scope.attributesForEnrollment = [];
                    for(var i=0; i<atts.length; i++){
                        var exists = false;
                        for(var j=0; j<$scope.selectedTei.attributes.length && !exists; j++){
                            if(atts[i].id === $scope.selectedTei.attributes[j].attribute){
                                exists = true;                                
                            }
                        }
                        if(!exists){
                            $scope.attributesForEnrollment.push(atts[i]);
                        }
                    }
                });                
            }           
        }
        
        $scope.broadCastSelections();
    };
        
    $scope.showEnrollment = function(){        
        $scope.showEnrollmentDiv = !$scope.showEnrollmentDiv;
    };    
       
    $scope.showScheduling = function(){        
        $scope.showSchedulingDiv = !$scope.showSchedulingDiv;
    };
    
    $scope.enroll = function(){    
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid ){
            return false;
        }
        
        //form is valid, continue with enrollment
        var tei = angular.copy($scope.selectedTei);
        tei.attributes = [];
        
        //existing attributes
        angular.forEach($scope.selectedTei.attributes, function(attribute){
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
                
                //registration is successful, continue for enrollment               
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
                            $scope.selectedTei.attributes.push({attribute: attribute.id, value: attribute.value, type: attribute.valueType, displayName: attribute.name});                            
                        }
                    });
                    
                    enrollment.enrollment = enrollmentResponse.reference;
                    $scope.selectedEnrollment = enrollment;
                    
                    $scope.broadCastSelections(); 
                    
                    $scope.outerForm.submitted = false;      
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
    
    $scope.broadCastSelections = function(){
        CurrentSelection.set({tei: $scope.selectedTei, te: $scope.selectedEntity, pr: $scope.selectedProgram, enrollment: $scope.selectedEnrollment});
        $timeout(function() { 
            $rootScope.$broadcast('dashboard', {});
            $rootScope.$broadcast('notesController', {});
        }, 100);
    };
    
    $scope.cancelEnrollment = function(){
        $scope.selectedProgram = '';
    };
});