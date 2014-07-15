trackerCapture.controller('DataEntryController',
        function($scope, 
                $filter,
                DateUtils,
                EventUtils,
                orderByFilter,
                storage,
                ProgramStageFactory,
                DHIS2EventFactory,
                ModalService,
                DialogService,
                CurrentSelection,
                TranslationService) {

    TranslationService.translate();
    
    //Data entry form
    $scope.dataEntryOuterForm = {};
    $scope.displayCustomForm = false;
    $scope.currentElement = {};
    
    var loginDetails = storage.get('LOGIN_DETAILS');
    var storedBy = '';
    if(loginDetails){
        storedBy = loginDetails.userCredentials.username;
    }
    var today = moment();
    today = Date.parse(today);
    today = $filter('date')(today, 'yyyy-MM-dd');
    $scope.invalidDate = false;
     
    //listen for the selected items
    $scope.$on('dashboardWidgets', function(event, args) {  
        $scope.showDataEntryDiv = false;
        $scope.showEventCreationDiv = false;
        $scope.showDummyEventDiv = false;        
        $scope.currentDummyEvent = null;
        $scope.currentEvent = null;
            
        $scope.allowEventCreation = false;
        $scope.repeatableStages = [];        
        $scope.dhis2Events = null;
        
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
            $scope.getEvents();
        }
    });
    
    $scope.getEvents = function(){
        
        $scope.dhis2Events = '';
        DHIS2EventFactory.getEventsByStatus($scope.selectedEntity.trackedEntityInstance, $scope.selectedOrgUnit.id, $scope.selectedProgram.id, 'ACTIVE').then(function(data){
            $scope.dhis2Events = data;
            
            if(angular.isObject($scope.dhis2Events)){
                angular.forEach($scope.dhis2Events, function(dhis2Event){
                    
                    var eventStage = $scope.selectedProgramWithStage[dhis2Event.programStage];
                    
                    if(angular.isObject(eventStage)){
                        
                        dhis2Event.name = eventStage.name;                        
                        dhis2Event.eventDate = DateUtils.format(dhis2Event.eventDate);
                        if(dhis2Event.dueDate){
                            dhis2Event.dueDate = DateUtils.format(dhis2Event.dueDate);
                        }
                        else{
                            dhis2Event.dueDate = EventUtils.getEventDueDate(eventStage, $scope.selectedEnrollment);
                        }                        
                        
                        dhis2Event.statusColor = EventUtils.getEventStatusColor(dhis2Event);  
                        dhis2Event = EventUtils.setEventOrgUnitName(dhis2Event);
                    } 
                                                                                 
                });
            }
            
            $scope.dummyEvents = $scope.checkForEventCreation($scope.dhis2Events, $scope.selectedProgram);
        });          
    };
    
    $scope.checkForEventCreation = function(availableEvents, program){
        
        var dummyEvents = [];        
        if($scope.selectedEnrollment.status === 'ACTIVE'){
            if(!angular.isObject(availableEvents)){
                angular.forEach($scope.selectedProgram.programStages, function(programStage){                                                        
                    var dummyEvent = EventUtils.createDummyEvent(programStage, $scope.selectedOrgUnit, $scope.selectedEnrollment);
                    dummyEvents.push(dummyEvent);                         
                });

                dummyEvents = orderByFilter(dummyEvents, '-eventDate');

                if(dummyEvents){
                    $scope.showEventCreationDiv = true;
                }

                return dummyEvents;
            }      
            else{
                for(var i=0; i<program.programStages.length; i++){
                    var stageHasEvent = false;
                    for(var j=0; j<availableEvents.length && !program.programStages[i].repeatable && !stageHasEvent; j++){
                        if(program.programStages[i].id === availableEvents[j].programStage){
                            stageHasEvent = true;
                        }
                    }

                    if(!stageHasEvent){
                        $scope.allowEventCreation = true;
                        var dummyEvent = EventUtils.createDummyEvent(program.programStages[i], $scope.selectedOrgUnit, $scope.selectedEnrollment);
                        dummyEvents.push(dummyEvent);
                    }
                }
            }
        }        
        return dummyEvents;
    };
    
    $scope.showEventCreation = function(){
        $scope.showEventCreationDiv = !$scope.showEventCreationDiv;
    };
    
    $scope.showDummyEventCreation = function(dummyEvent){

        if(dummyEvent){    
            
            if($scope.currentDummyEvent == dummyEvent){ 
                //clicked on the same stage, do toggling
                $scope.currentDummyEvent = null;
                $scope.showDummyEventDiv = !$scope.showDummyEventDiv;                
            }
            else{
                $scope.currentDummyEvent = dummyEvent;
                $scope.showDummyEventDiv = !$scope.showDummyEventDiv;
            }   
        }
    };   
    
    $scope.createEvent = function(){
        //check for form validity
        $scope.eventCreationForm.submitted = true;        
        if( $scope.eventCreationForm.$invalid ){
            return false;
        } 
        
        //form is valid, proceed to event creation
        var newEvent = 
                {
                    trackedEntityInstance: $scope.selectedEntity.trackedEntityInstance,
                    program: $scope.selectedProgram.id,
                    programStage: $scope.currentDummyEvent.programStage,
                    orgUnit: $scope.currentDummyEvent.orgUnit,
                    eventDate: $scope.currentDummyEvent.eventDate,
                    dueDate: $scope.currentDummyEvent.dueDate,
                    status: 'ACTIVE',
                    notes: [],
                    dataValues: []
                };
                
        DHIS2EventFactory.create(newEvent).then(function(data) {
            if (data.importSummaries[0].status === 'ERROR') {
                var dialogOptions = {
                    headerText: 'event_creation_error',
                    bodyText: data.importSummaries[0].description
                };

                DialogService.showDialog({}, dialogOptions);
            }
            else {
                newEvent.event = data.importSummaries[0].reference;
                newEvent.orgUnitName = $scope.currentDummyEvent.orgUnitName;
                newEvent.name = $scope.currentDummyEvent.name;
                newEvent.statusColor = $scope.currentDummyEvent.statusColor;
                
                if(!angular.isObject($scope.dhis2Events)){
                    $scope.dhis2Events = [newEvent];
                }
                else{
                    $scope.dhis2Events.splice(0,0,newEvent);
                    $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
                    $scope.dhis2Events.reverse();
                }                
                $scope.showDataEntry(newEvent);
            }
        });
    };   
    
    $scope.createDummyEvent = function(programStage, orgUnit, enrollment){
        
        var dueDate = EventUtils.getEventDueDate(programStage, enrollment);
        var dummyEvent = {programStage: programStage.id, 
                          orgUnit: orgUnit.id,
                          orgUnitName: orgUnit.name,
                          dueDate: dueDate,
                          name: programStage.name,
                          status: 'ACTIVE',
                          statusColor: 'alert alert-info'//'stage-on-time'
                        };        
        
        dummyEvent.statusColor = EventUtils.getEventStatusColor(dummyEvent);
        
        return dummyEvent;
    };
    
    $scope.showDataEntry = function(event){

        if(event){

            if($scope.currentEvent && $scope.currentEvent.event === event.event){
                //clicked on the same stage, do toggling
                $scope.currentEvent = null;
                $scope.currentElement = {id: '', saved: false};
                $scope.showDataEntryDiv = !$scope.showDataEntryDiv;      
            }
            else{
                $scope.currentElement = {};
                $scope.currentEvent = event;                
                $scope.showDataEntryDiv = true;   
                $scope.showDummyEventDiv = false;
                $scope.showEventCreationDiv = false;
                $scope.getDataEntryForm();  
                $scope.newEventDate = $scope.currentEvent.eventDate;
            }               
        }
    }; 
    
    $scope.switchDataEntryForm = function(){
        $scope.displayCustomForm = !$scope.displayCustomForm;
    };
    
    $scope.getDataEntryForm = function(){ 
        
        $scope.currentEvent.providedElsewhere = [];

        $scope.currentEvent.dueDate = DateUtils.format($scope.currentEvent.dueDate);
        $scope.currentEvent.eventDate = DateUtils.format($scope.currentEvent.eventDate);

        if(!angular.isUndefined( $scope.currentEvent.notes)){
            $scope.currentEvent.notes = orderByFilter($scope.currentEvent.notes, '-storedDate');            
            angular.forEach($scope.currentEvent.notes, function(note){
                note.storedDate = moment(note.storedDate).format('DD.MM.YYYY @ hh:mm A');
            });
        }

        ProgramStageFactory.get($scope.currentEvent.programStage).then(function(stage){
            $scope.currentStage = stage;

            $scope.programStageDataElements = [];                  
            angular.forEach($scope.currentStage.programStageDataElements, function(prStDe){
                $scope.programStageDataElements[prStDe.dataElement.id] = prStDe; 
            }); 

            $scope.customForm = $scope.currentStage.dataEntryForm ? $scope.currentStage.dataEntryForm.htmlCode : null; 
            $scope.displayCustomForm = $scope.customForm ? true:false;

            $scope.allowProvidedElsewhereExists = false;
            angular.forEach($scope.currentStage.programStageDataElements, function(prStDe){
                $scope.currentStage.programStageDataElements[prStDe.dataElement.id] = prStDe.dataElement;
                if(prStDe.allowProvidedElsewhere){
                    $scope.allowProvidedElsewhereExists = true;
                    $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] = '';   
                }                
            });

            angular.forEach($scope.currentEvent.dataValues, function(dataValue){
                var val = dataValue.value;
                if(val){
                    var de = $scope.currentStage.programStageDataElements[dataValue.dataElement];
                    if( de && de.type === 'int' && val){
                        val = parseInt(val);
                        dataValue.value = val;
                    }
                    $scope.currentEvent[dataValue.dataElement] = val;
                }                    
            });

            $scope.currentEvent.dataValues = [];
            $scope.currentEventOriginal = angular.copy($scope.currentEvent);
        });
    };
    
    $scope.saveDatavalue = function(prStDe){
        
        $scope.currentElement = {};
        
        //check for input validity
        $scope.dataEntryOuterForm.submitted = true;        
        if( $scope.dataEntryOuterForm.$invalid ){
            return false;
        }
         
        //input is valid
        $scope.updateSuccess = false;      
        
        if(!angular.isUndefined($scope.currentEvent[prStDe.dataElement.id])){

            if($scope.currentEventOriginal[prStDe.dataElement.id] !== $scope.currentEvent[prStDe.dataElement.id]){
                
                //get current element
                $scope.currentElement = {id: prStDe.dataElement.id, saved: false};

                //currentEvent.providedElsewhere[prStDe.dataElement.id];
                var value = $scope.currentEvent[prStDe.dataElement.id];
                var ev = {  event: $scope.currentEvent.event,
                            orgUnit: $scope.currentEvent.orgUnit,
                            program: $scope.currentEvent.program,
                            programStage: $scope.currentEvent.programStage,
                            status: $scope.currentEvent.status,
                            trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                            dataValues: [
                                            {
                                                dataElement: prStDe.dataElement.id, 
                                                value: value, 
                                                providedElseWhere: $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] ? $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] : false
                                            }
                                        ]
                         };
                DHIS2EventFactory.updateForSingleValue(ev).then(function(response){
                    $scope.currentElement.saved = true;
                });
            }
        }        
    };
    
    $scope.saveDatavalueLocation = function(prStDe){
        
        $scope.updateSuccess = false;
        
        if(!angular.isUndefined($scope.currentEvent.providedElsewhere[prStDe.dataElement.id])){

            //currentEvent.providedElsewhere[prStDe.dataElement.id];
            var value = $scope.currentEvent[prStDe.dataElement.id];
            var ev = {  event: $scope.currentEvent.event,
                        orgUnit: $scope.currentEvent.orgUnit,
                        program: $scope.currentEvent.program,
                        programStage: $scope.currentEvent.programStage,
                        status: $scope.currentEvent.status,
                        trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                        dataValues: [
                                        {
                                            dataElement: prStDe.dataElement.id, 
                                            value: value, 
                                            providedElseWhere: $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] ? $scope.currentEvent.providedElsewhere[prStDe.dataElement.id] : false
                                        }
                                    ]
                     };
            DHIS2EventFactory.updateForSingleValue(ev).then(function(response){
                $scope.updateSuccess = true;
            });            
        }        
    };
    
    $scope.saveEventDate = function(){
        
        if($scope.newEventDate != $scope.currentEvent.eventDate){
            if($scope.newEventDate == ''){
                $scope.invalidDate = true;
                return false;
            }
            else{
                var rawDate = $filter('date')($scope.newEventDate, 'yyyy-MM-dd'); 
                var convertedDate = moment($scope.newEventDate, 'YYYY-MM-DD')._d;
                convertedDate = $filter('date')(convertedDate, 'yyyy-MM-dd'); 

                if(rawDate !== convertedDate){
                    $scope.invalidDate = true;
                    return false;
                } 
                
                var e = {event: $scope.currentEvent.event,
                     program: $scope.currentEvent.program,
                     programStage: $scope.currentEvent.programStage,
                     orgUnit: $scope.currentEvent.orgUnit,
                     eventDate: $scope.newEventDate,
                     trackedEntityInstance: $scope.currentEvent.trackedEntityInstance
                    };
                    
                DHIS2EventFactory.updateForEventDate(e).then(function(data){
                    $scope.currentEvent.eventDate = $scope.newEventDate;
                    $scope.newEventDate = '';
                    $scope.invalidDate = false;
                });
            }
        }
        
    };
    
    $scope.addNote = function(){
        
        if(!angular.isUndefined($scope.note) && $scope.note != ""){
            
            var newNote = {value: $scope.note};

            if(angular.isUndefined( $scope.currentEvent.notes) ){
                $scope.currentEvent.notes = [{value: $scope.note, storedDate: today, storedBy: storedBy}];
            }
            else{
                $scope.currentEvent.notes.splice(0,0,{value: $scope.note, storedDate: today, storedBy: storedBy});
            }

            var e = {event: $scope.currentEvent.event,
                     program: $scope.currentEvent.program,
                     programStage: $scope.currentEvent.programStage,
                     orgUnit: $scope.currentEvent.orgUnit,
                     trackedEntityInstance: $scope.currentEvent.trackedEntityInstance,
                     notes: [newNote]
                    };

            DHIS2EventFactory.updateForNote(e).then(function(data){
                $scope.note = '';                
            });
        }        
    };    
    
    $scope.getInputNotifcationClass = function(id, custom){
        if($scope.currentElement.id){
            if($scope.currentElement.saved && ($scope.currentElement.id === id)){
                if(custom){
                    return 'input-success';
                }
                return 'form-control input-success';
            }            
            if(!$scope.currentElement.saved && ($scope.currentElement.id === id)){
                if(custom){
                    return 'input-error';
                }
                return 'form-control input-error';
            }            
        }  
        if(custom){
            return '';
        }
        return 'form-control';
    };  
    
    $scope.closeEventCreation = function(){
        $scope.currentDummyEvent = null;
        $scope.showDummyEventDiv = !$scope.showDummyEventDiv;
    };
    
    $scope.completeEvent = function(){
        
        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'complete',
            headerText: 'complete',
            bodyText: 'are_you_sure_to_complete_event'
        };

        ModalService.showModal({}, modalOptions).then(function(result){
            
            var dhis2Event = EventUtils.reconstruct($scope.currentEvent, $scope.currentStage);
            dhis2Event.status = 'COMPLETED';            
        
            DHIS2EventFactory.update(dhis2Event).then(function(data){
                
                $scope.currentEvent.status = 'COMPLETED';
                var statusColor = EventUtils.getEventStatusColor($scope.currentEvent);  
                var continueLoop = true;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === $scope.currentEvent.event ){
                        $scope.dhis2Events[i].statusColor = statusColor;
                        continueLoop = false;
                    }
                }                
            });
        });
    };
    
    $scope.incompleteEvent = function(){
        
        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'incomplete',
            headerText: 'incomplete',
            bodyText: 'are_you_sure_to_incomplete_event'
        };

        ModalService.showModal({}, modalOptions).then(function(result){
            
            var dhis2Event = EventUtils.reconstruct($scope.currentEvent, $scope.currentStage);
            dhis2Event.status = 'ACTIVE';
            $scope.currentEvent.status = 'ACTIVE';
        
            DHIS2EventFactory.update(dhis2Event).then(function(data){
                
                
                $scope.currentEvent.status = 'ACTIVE';
                var statusColor = EventUtils.getEventStatusColor($scope.currentEvent);  
                var continueLoop = true;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === $scope.currentEvent.event ){
                        $scope.dhis2Events[i].statusColor = statusColor;
                        continueLoop = false;
                    }
                }           
            });
        });
    };
    
    $scope.validateEvent = function(){
        
    };
    
    $scope.removeEvent = function(){
        
        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'remove',
            headerText: 'remove',
            bodyText: 'are_you_sure_to_remove_event'
        };

        ModalService.showModal({}, modalOptions).then(function(result){
            
            DHIS2EventFactory.delete($scope.currentEvent).then(function(data){
                
                var continueLoop = true, index = -1;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === $scope.currentEvent.event ){
                        $scope.dhis2Events[i] = $scope.currentEvent;
                        continueLoop = false;
                        index = i;
                    }
                }
                $scope.dhis2Events.splice(index,1);                
                $scope.currentEvent = null;
                if($scope.dhis2Events.length < 1){  
                    $scope.dhis2Events = '';
                    $scope.currentDummyEvent = null;
                    $scope.dummyEvents = $scope.checkForEventCreation($scope.dhis2Events, $scope.selectedProgram);
                }
            });
        });
    };
});