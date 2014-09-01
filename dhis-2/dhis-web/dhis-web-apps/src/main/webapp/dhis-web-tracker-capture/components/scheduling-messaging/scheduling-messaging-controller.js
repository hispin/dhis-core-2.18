trackerCapture.controller('SchedulingMessagingController',
        function($scope,                
                storage,
                DateUtils,
                EventUtils,
                DHIS2EventFactory,
                EnrollmentService,
                CurrentSelection,
                orderByFilter,
                TranslationService) {

    TranslationService.translate();
    
    var loginDetails = storage.get('LOGIN_DETAILS');
    var storedBy = '';
    if(loginDetails){
        storedBy = loginDetails.userCredentials.username;
    }
    
    var today = DateUtils.format(moment());
    
    $scope.$on('dashboardWidgets', function(event, args) {
        var selections = CurrentSelection.get();          
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');        
        $scope.selectedEntity = selections.tei;      
        $scope.selectedProgram = selections.pr;        
        $scope.selectedEnrollment = selections.enrollment;   
        $scope.selectedProgramWithStage = [];
        
        if($scope.selectedOrgUnit && 
                $scope.selectedProgram && 
                $scope.selectedEntity && 
                $scope.selectedEnrollment){
            
            angular.forEach($scope.selectedProgram.programStages, function(stage){
                $scope.selectedProgramWithStage[stage.id] = stage;
            });

            /*EnrollmentService.get(selections.enrollment.enrollment).then(function(data){    
                $scope.selectedEnrollment = data;   
                if(!angular.isUndefined( $scope.selectedEnrollment.notes)){
                    $scope.selectedEnrollment.notes = orderByFilter($scope.selectedEnrollment.notes, '-storedDate');            
                    angular.forEach($scope.selectedEnrollment.notes, function(note){
                        note.storedDate = moment(note.storedDate).format('YYYY-MM-DD @ hh:mm A');
                    });
                }
            });*/
            $scope.dhis2Events = '';
            DHIS2EventFactory.getEventsByStatus($scope.selectedEntity.trackedEntityInstance, $scope.selectedOrgUnit.id, $scope.selectedProgram.id, 'ACTIVE').then(function(data){
                $scope.dhis2Events = data;
                if(angular.isObject($scope.dhis2Events)){
                    angular.forEach($scope.dhis2Events, function(dhis2Event){

                        if(dhis2Event.enrollment === $scope.selectedEnrollment.enrollment){
                            var eventStage = $scope.selectedProgramWithStage[dhis2Event.programStage];
                            if(angular.isObject(eventStage)){

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
                }
            });
        }
    });
    
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