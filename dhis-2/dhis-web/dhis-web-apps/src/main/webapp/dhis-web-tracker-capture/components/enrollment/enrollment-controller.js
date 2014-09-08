trackerCapture.controller('EnrollmentController',
        function($rootScope,
                $scope,  
                $timeout,
                $location,
                DateUtils,
                EventUtils,
                storage,
                DHIS2EventFactory,
                AttributesFactory,
                CurrentSelection,
                TEIService,
                EnrollmentService,
                TranslationService,
                ModalService,
                DialogService) {
    TranslationService.translate();
    
    $scope.today = DateUtils.format(moment());
    
    //listen for the selected items
    $scope.$on('selectedItems', function(event, args) {   
        //programs for enrollment        
        $scope.enrollments = [];
        $scope.showEnrollmentDiv = false;
        $scope.showReSchedulingDiv = false;
        $scope.showEnrollmentHistoryDiv = false;
        $scope.hasEnrollmentHistory = false;
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
                $scope.enrollments = data.enrollments;
                console.log('List of enrollments:  ', $scope.enrollments);
                $scope.loadEnrollmentDetails();                
            });
        }
        else{
            $scope.broadCastSelections('dashboardWidgets');
        }
    });
    
    $scope.loadEnrollmentDetails = function() {
        
        if($scope.selectedProgram){           
            
            $scope.selectedProgramWithStage = [];        
            angular.forEach($scope.selectedProgram.programStages, function(stage){
                $scope.selectedProgramWithStage[stage.id] = stage;
            });
          
            //check for possible enrollment, there is only one active enrollment
            $scope.terminatedEnrollments = [];
            $scope.completedEnrollments = [];
            angular.forEach($scope.enrollments, function(enrollment){
                if(enrollment.program === $scope.selectedProgram.id ){
                    if(enrollment.status === 'ACTIVE'){
                        $scope.selectedEnrollment = enrollment;
                    }
                    if(enrollment.status === 'CANCELLED'){//check for cancelled ones
                        $scope.terminatedEnrollments.push(enrollment);
                        $scope.hasEnrollmentHistory = true;
                    }
                    if(enrollment.status === 'COMPLETED'){//check for completed ones
                        $scope.completedEnrollments.push(enrollment);
                        $scope.hasEnrollmentHistory = true;
                    }
                }
            }); 
            
            if($scope.selectedEnrollment){//enrollment exists
                $scope.selectedEnrollment.dateOfIncident = DateUtils.format($scope.selectedEnrollment.dateOfIncident);
                $scope.selectedEnrollment.dateOfEnrollment = DateUtils.format($scope.selectedEnrollment.dateOfEnrollment);
                
                $scope.programStages = [];   
                
                var incidentDate = $scope.selectedEnrollment ? $scope.selectedEnrollment.dateOfIncident : new Date();

                angular.forEach($scope.selectedProgram.programStages, function(stage){                
                    stage.dueDate = moment(moment(incidentDate).add('d', stage.minDaysFromStart), 'YYYY-MM-DD')._d;
                    stage.dueDate = Date.parse(stage.dueDate);
                    stage.dueDate = DateUtils.format(stage.dueDate);//$filter('date')(stage.dueDate, 'yyyy-MM-dd');
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
        
        console.log('The enrollment is:  ', $scope.selectedEnrollment);
        $scope.broadCastSelections('dashboardWidgets');
    };
        
    $scope.showNewEnrollment = function(){       
        if($scope.showEnrollmentDiv){//this is hiding enrollment div
            /*currently the only way to cancel enrollment window is by going through
            * the main dashboard controller. Here I am mixing program and programId, 
            * as I didn't want to refetch program from server, the main dashboard
            * has already fetched the programs. With the ID passed to it, it will
            * pass back the actual program than ID. 
            */
           $scope.selectedProgram = ($location.search()).program;
           $scope.broadCastSelections('mainDashboard');
        }
        $scope.showEnrollmentDiv = !$scope.showEnrollmentDiv;
    };    
       
    $scope.showReScheduling = function(){        
        $scope.showReSchedulingDiv = !$scope.showReSchedulingDiv;
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
                    $scope.autoGenerateEvents();                    
                    $scope.broadCastSelections('dashboardWidgets'); 
                    
                    $scope.showEnrollmentDiv = false;
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
    
    $scope.broadCastSelections = function(listeners){
        CurrentSelection.set({tei: $scope.selectedTei, te: $scope.selectedEntity, pr: $scope.selectedProgram, enrollment: $scope.selectedEnrollment});
        $timeout(function() { 
            $rootScope.$broadcast(listeners, {});
        }, 100);
    };
    
    $scope.hideEnrollmentDiv = function(){
        
        /*currently the only way to cancel enrollment window is by going through
         * the main dashboard controller. Here I am mixing program and programId, 
         * as I didn't want to refetch program from server, the main dashboard
         * has already fetched the programs. With the ID passed to it, it will
         * pass back the actual program than ID. 
         */
        $scope.selectedProgram = ($location.search()).program;
        $scope.broadCastSelections('mainDashboard'); 
    };
    
    $scope.terminateEnrollment = function(){        

        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'terminate',
            headerText: 'terminate_enrollment',
            bodyText: 'are_you_sure_to_terminate_enrollment'
        };

        ModalService.showModal({}, modalOptions).then(function(result){
            
            EnrollmentService.cancel($scope.selectedEnrollment).then(function(data){                
                $scope.selectedEnrollment.status = 'CANCELLED';
                $scope.loadEnrollmentDetails();                
            });
        });
    };
    
    $scope.completeEnrollment = function(){        

        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'complete',
            headerText: 'complete_enrollment',
            bodyText: 'are_you_sure_to_complete_enrollment'
        };

        ModalService.showModal({}, modalOptions).then(function(result){
            
            EnrollmentService.complete($scope.selectedEnrollment).then(function(data){                
                $scope.selectedEnrollment.status = 'COMPLETED';
                $scope.loadEnrollmentDetails();                
            });
        });
    };
    
    $scope.showEnrollmentHistory = function(){
        //$scope.showEnrollmentHistoryDiv = !$scope.showEnrollmentHistoryDiv;
        console.log('need to figure out how to deal with previous enrollments'); 
    };
        
    $scope.autoGenerateEvents = function(){
        if($scope.selectedTei && $scope.selectedProgram && $scope.selectedOrgUnit && $scope.selectedEnrollment){            
            $scope.dhis2Events = {events: []};
            angular.forEach($scope.selectedProgram.programStages, function(stage){
                if(stage.autoGenerateEvent){
                    var newEvent = {
                            trackedEntityInstance: $scope.selectedTei.trackedEntityInstance,
                            program: $scope.selectedProgram.id,
                            programStage: stage.id,
                            orgUnit: $scope.selectedOrgUnit.id,                        
                            dueDate: EventUtils.getEventDueDate(stage, $scope.selectedEnrollment),
                            status: 'SCHEDULE'
                        };
                    $scope.dhis2Events.events.push(newEvent);    
                }
            });
            
            if($scope.dhis2Events.events.length > 0){
                DHIS2EventFactory.create($scope.dhis2Events).then(function(data) {
                });
            }
        }
    };
    
    $scope.markForFollowup = function(){
        EnrollmentService.update($scope.selectedEnrollment).then(function(data){         
        });
    };
});