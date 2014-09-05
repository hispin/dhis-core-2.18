trackerCapture.controller('SchedulingMessagingController',
        function($scope,                
                storage,
                DateUtils,
                EventUtils,
                DHIS2EventFactory,
                TEIService,
                CurrentSelection,
                TranslationService) {

    TranslationService.translate();
    
    var loginDetails = storage.get('LOGIN_DETAILS');
    var storedBy = '';
    if(loginDetails){
        storedBy = loginDetails.userCredentials.username;
    }
    
    var today = DateUtils.format(moment());
    $scope.showSchedulingDiv = false;
    $scope.schedulingPossible = false;
    $scope.showMessagingDiv = false;
    $scope.messagingPossible = false;
    $scope.showEventSchedulingDiv = false;
    
    $scope.$on('dashboardWidgets', function(event, args) {
        var selections = CurrentSelection.get();          
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');  
        $scope.selectedTei = angular.copy(selections.tei);
        $scope.selectedProgram = selections.pr;        
        $scope.selectedEnrollment = selections.enrollment;   
        $scope.selectedProgramWithStage = [];
        $scope.dhis2Events = [];
        
        if($scope.selectedOrgUnit && 
                $scope.selectedProgram && 
                $scope.selectedTei && 
                $scope.selectedEnrollment){            
            
            angular.forEach($scope.selectedProgram.programStages, function(stage){
                $scope.selectedProgramWithStage[stage.id] = stage;
            });
            
            //check if the selected TEI has any of the contact attributes
            //that can be used for communication
            TEIService.processAttributes($scope.selectedTei, $scope.selectedProgram, $scope.selectedEnrollment).then(function(tei){
                $scope.selectedTei = tei; 
                var continueLoop = true;
                for(var i=0; i<$scope.selectedTei.attributes.length && continueLoop; i++){
                    if( ($scope.selectedTei.attributes[i].type === 'phoneNumber' && $scope.selectedTei.attributes[i].show) || 
                        ($scope.selectedTei.attributes[i].type === 'email' && $scope.selectedTei.attributes[i].show) ){
                        $scope.messagingPossible = true;
                        continueLoop = false;
                    }
                }
                        
                DHIS2EventFactory.getEventsByStatus($scope.selectedTei.trackedEntityInstance, $scope.selectedOrgUnit.id, $scope.selectedProgram.id, 'ACTIVE').then(function(eventList){                
                    angular.forEach(eventList, function(dhis2Event){                    
                        if( dhis2Event.enrollment === $scope.selectedEnrollment.enrollment && 
                            dhis2Event.status === 'SCHEDULE' &&
                            angular.isUndefined(dhis2Event.eventDate)){
                            var eventStage = $scope.selectedProgramWithStage[dhis2Event.programStage];
                            if(angular.isObject(eventStage)){

                                $scope.dhis2Events.push(dhis2Event);
                                dhis2Event.name = eventStage.name; 
                                dhis2Event.reportDateDescription = eventStage.reportDateDescription;
                                dhis2Event.dueDate = DateUtils.format(dhis2Event.dueDate);

                                if(dhis2Event.eventDate){
                                    dhis2Event.eventDate = DateUtils.format(dhis2Event.eventDate);
                                    dhis2Event.sortingDate = DateUtils.format(dhis2Event.eventDate);
                                }
                                else{
                                    dhis2Event.sortingDate = dhis2Event.dueDate;
                                }                            
                                dhis2Event.statusColor = EventUtils.getEventStatusColor(dhis2Event);  
                                dhis2Event = EventUtils.setEventOrgUnitName(dhis2Event);                            
                            } 
                        }
                    });                
                    $scope.schedulingPossible = $scope.dhis2Events.length > 0 ? true : false;
                    if($scope.schedulingPossible && $scope.messagingPossible){
                        
                    }
                });
            });
        }
    });
    
    $scope.showScheduling = function(){
        $scope.showSchedulingDiv = !$scope.showSchedulingDiv;
        //$scope.showMessagingDiv = !$scope.showMessagingDiv;
    };
    
    $scope.showMessaging = function(){
        //$scope.showSchedulingDiv = !$scope.showSchedulingDiv;
        $scope.showMessagingDiv = !$scope.showMessagingDiv;
    };
    
    $scope.showSchedulingDetails = function(dhis2Event){
        
        if( !$scope.currentEvent ){            
            $scope.currentEvent = dhis2Event;
            $scope.showEventSchedulingDiv = !$scope.showEventSchedulingDiv;
        }
        else{            
            if( dhis2Event.event === $scope.currentEvent.event ){                
                $scope.showEventSchedulingDiv = !$scope.showEventSchedulingDiv;
                $scope.currentEvent = null;
            }
            else{
                $scope.currentEvent = dhis2Event;
            }
        }
    };
    
    $scope.cancelScheduling = function(){
        $scope.showEventSchedulingDiv = !$scope.showEventSchedulingDiv;
        $scope.currentEvent = null;
    };
    
    $scope.saveDueDate = function(){
        $scope.dueDateSaved = false;

        if($scope.currentEvent.dueDate == ''){
            $scope.invalidDate = true;
            return false;
        }
        else{
            var rawDate = $filter('date')($scope.currentEvent.dueDate, 'yyyy-MM-dd'); 
            var convertedDate = moment($scope.currentEvent.dueDate, 'YYYY-MM-DD')._d;
            convertedDate = $filter('date')(convertedDate, 'yyyy-MM-dd'); 

            if(rawDate !== convertedDate){
                $scope.invalidDate = true;
                return false;
            } 

            var e = {event: $scope.currentEvent.event,
                 enrollment: $scope.currentEvent.enrollment,
                 dueDate: $scope.currentEvent.dueDate,
                 status: $scope.currentEvent.status,
                 program: $scope.currentEvent.program,
                 programStage: $scope.currentEvent.programStage,
                 orgUnit: $scope.currentEvent.orgUnit,
                 trackedEntityInstance: $scope.currentEvent.trackedEntityInstance
                };

            DHIS2EventFactory.update(e).then(function(data){            
                $scope.invalidDate = false;
                $scope.dueDateSaved = true;
                $scope.currentEvent.sortingDate = $scope.currentEvent.dueDate;                
                var statusColor = EventUtils.getEventStatusColor($scope.currentEvent);  
                var continueLoop = true;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === $scope.currentEvent.event ){
                        $scope.dhis2Events[i].statusColor = statusColor;
                        continueLoop = false;
                    }
                } 
            });
        }              
    };
});