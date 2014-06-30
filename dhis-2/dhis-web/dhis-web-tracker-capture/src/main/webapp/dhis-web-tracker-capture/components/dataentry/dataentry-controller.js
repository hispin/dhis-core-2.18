trackerCapture.controller('DataEntryController',
        function($scope, 
                $filter,
                DateUtils,
                EventUtils,
                orderByFilter,
                storage,
                ProgramStageFactory,
                DHIS2EventFactory,
                OrgUnitService,
                DialogService,
                CurrentSelection,
                TranslationService) {

    TranslationService.translate();
    
    var today = moment();
    today = Date.parse(today);
    today = $filter('date')(today, 'yyyy-MM-dd');
     
    //listen for the selected items
    $scope.$on('dashboard', function(event, args) {  
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
        
        DHIS2EventFactory.getByEntity($scope.selectedEntity.trackedEntityInstance, $scope.selectedOrgUnit.id, $scope.selectedProgram.id).then(function(data){
            $scope.dhis2Events = data;

            if(angular.isUndefined($scope.dhis2Events)){

                //create dummy events
                $scope.dummyEvents = [];

                if($scope.selectedEnrollment.status === 'ACTIVE'){
                    //create dummy events for the selected enrollment                        
                    angular.forEach($scope.selectedProgram.programStages, function(programStage){                                                        
                        var dummyEvent = EventUtils.createDummyEvent(programStage, $scope.selectedOrgUnit, $scope.selectedEnrollment);
                        $scope.dummyEvents.push(dummyEvent);                         
                    });

                    $scope.dummyEvents = orderByFilter($scope.dummyEvents, '-eventDate');
                    //$scope.dummyEvents.reverse();            
                    
                    if($scope.dummyEvents){
                        $scope.showEventCreationDiv = true;
                    }
                }
            }
            else{
                angular.forEach($scope.dhis2Events, function(dhis2Event){
                    
                    var eventStage = $scope.selectedProgramWithStage[dhis2Event.programStage];
                    if(angular.isObject(eventStage)){
                        
                        dhis2Event.name = eventStage.name;
                        if(dhis2Event.status === 'COMPLETED'){
                            dhis2Event.statusColor = 'stage-completed';
                        }
                        else{
                            dhis2Event.statusColor = 'stage-on-time';

                            if(dhis2Event.dueDate){
                                dhis2Event.dueDate = DateUtils.format(dhis2Event.dueDate);
                            }
                            else{
                                dhis2Event.dueDate = DateUtils.getDueDate(eventStage, $scope.selectedEnrollment);
                            }

                            if(moment(today).isAfter(dhis2Event.dueDate) && !dhis2Event.dataValues){
                                dhis2Event.statusColor = 'stage-overdue';
                            }
                        }
                    }                     
                    
                    dhis2Event.eventDate = DateUtils.format(dhis2Event.eventDate);
                    
                    //get orgunit name for the event
                    if(dhis2Event.orgUnit){
                        OrgUnitService.open().then(function(){
                            OrgUnitService.get(dhis2Event.orgUnit).then(function(ou){
                                if(ou){
                                    dhis2Event.orgUnitName = ou.n;
                                }                                                       
                            });                            
                        }); 
                    }                                                             
                });

                $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
                $scope.dhis2Events.reverse();   
                
                $scope.dummyEvents = $scope.checkForEventCreation($scope.dhis2Events, $scope.selectedProgram);
            }
        });          
    };
    
    $scope.checkForEventCreation = function(availableEvents, program){
        
        var dummyEvents = [];
        
        for(var i=0; i<program.programStages.length; i++){
            var stageHasEvent = false;
            for(var j=0; j<availableEvents.length && !program.programStages[i].repeatable && !stageHasEvent; j++){
                if(program.programStages[i].id === availableEvents[j].stage){
                    stageHasEvent = true;
                }
            }
            
            if(!stageHasEvent){
                $scope.allowEventCreation = true;
                var dummyEvent = EventUtils.createDummyEvent(program.programStages[i], $scope.selectedOrgUnit, $scope.selectedEnrollment);
                dummyEvents.push(dummyEvent);
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
                $scope.getEvents();
                newEvent.event = data.importSummaries[0].reference;
                $scope.showDataEntry(newEvent);
            }
        });
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
                $scope.showDataEntryDiv = !$scope.showDataEntryDiv;
                $scope.getDataEntryForm();                
            }               
        }
    }; 
    
    $scope.getDataEntryForm = function(){
        $scope.currentEvent.providedElsewhere = [];
            
        ProgramStageFactory.get($scope.currentEvent.programStage).then(function(stage){
            $scope.currentStage = stage;
            
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
        
        //check for input validity
        $scope.dataEntryOuterForm.submitted = true;        
        if( $scope.dataEntryOuterForm.$invalid ){
            return false;
        }
         
        //input is valid
        $scope.updateSuccess = false;      
   
        if(!angular.isUndefined($scope.currentEvent[prStDe.dataElement.id])){

            if($scope.currentEventOriginal[prStDe.dataElement.id] != $scope.currentEvent[prStDe.dataElement.id]){
                
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
            
            console.log('the event is:  ',$scope.currentEvent.providedElsewhere[prStDe.dataElement.id]);
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
    
    $scope.createDummyEvent = function(programStage, orgUnit, enrollment){
        
        var dueDate = DateUtils.getDueDate(programStage, enrollment);
        var dummyEvent = {programStage: programStage.id, 
                          orgUnit: orgUnit.id,
                          orgUnitName: orgUnit.name,
                          dueDate: dueDate,
                          name: programStage.name,
                          status: 'ACTIVE'};
        dummyEvent.statusColor = 'stage-on-time';
        if(moment(today).isAfter(dummyEvent.dueDate)){
            dummyEvent.statusColor = 'stage-overdue';
        }
        return dummyEvent;
    };
    
    $scope.getClass = function(id){
        if($scope.currentElement){
            if($scope.currentElement.saved && ($scope.currentElement.id === id)){
                return 'form-control input-success';
            }            
            if(!$scope.currentElement.saved && ($scope.currentElement.id === id)){
                return 'form-control input-error';
            }            
        }        
        return 'form-control';      
    };
    
    $scope.closeEventCreation = function(){
        $scope.currentDummyEvent = null;
        $scope.showDummyEventDiv = !$scope.showDummyEventDiv;
    };
});