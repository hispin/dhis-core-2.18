'use strict';

/* Controllers */
var eventCaptureControllers = angular.module('eventCaptureControllers', [])

//Controller for settings page
.controller('MainController',
        function($scope,
                $modal,
                $timeout,
                storage,
                Paginator,
                TranslationService,
                OptionSetService,
                ProgramFactory,
                ProgramStageFactory,                
                DHIS2EventFactory,
                DHIS2EventService,
                GeoJsonFactory,
                ContextMenuSelectedItem,
                DateUtils,
                ModalService,
                DialogService) {   
   
                      
    //selected org unit
    $scope.selectedOrgUnit = '';
    
    //Paging
    $scope.pager = {pageSize: 50, page: 1, toolBarDisplay: 5};   
    
    //Editing
    $scope.eventRegistration = false;
    $scope.editGridColumns = false;
    $scope.editingEventInFull = false;
    $scope.editingEventInGrid = false;   
    $scope.updateSuccess = false;
    $scope.currentGridColumnId = '';  
    $scope.currentEventOrginialValue = '';   
    $scope.displayCustomForm = false;
    $scope.currentElement = {id: '', update: false};
    $scope.selectedOrgUnit = '';
    
    //notes
    $scope.note = {};
    $scope.today = DateUtils.getToday();
    
    var loginDetails = storage.get('LOGIN_DETAILS');
    var storedBy = '';
    if(loginDetails){
        storedBy = loginDetails.userCredentials.username;
    }
    $scope.noteExists = false;
        
    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function(newObj, oldObj) {
        
        $scope.dhis2Events = [];
        if( angular.isObject($scope.selectedOrgUnit)){            
            //apply translation - by now user's profile is fetched from server.
            TranslationService.translate(); 
            $scope.loadPrograms();
        }
    });
    
    GeoJsonFactory.getAll().then(function(geoJsons){
        $scope.geoJsons = geoJsons;
    });
            
    
    
    //load programs associated with the selected org unit.
    $scope.loadPrograms = function() {

        //$scope.selectedOrgUnit = orgUnit;
        $scope.selectedProgram = null;
        $scope.selectedProgramStage = null;
        $scope.dhis2Events = [];

        $scope.eventRegistration = false;
        $scope.editGridColumns = false;
        $scope.editingEventInFull = false;
        $scope.editingEventInGrid = false;   
        $scope.updateSuccess = false;
        $scope.currentGridColumnId = '';  
        $scope.currentEventOrginialValue = ''; 
        $scope.displayCustomForm = false;
        
        if (angular.isObject($scope.selectedOrgUnit)) {    
            
            ProgramFactory.getAll().then(function(programs){
                $scope.programs = [];
                angular.forEach(programs, function(program){                            
                    if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id)){                                
                        $scope.programs.push(program);
                    }
                });
                
                if(angular.isObject($scope.programs) && $scope.programs.length === 1){
                    $scope.selectedProgram = $scope.programs[0];
                    $scope.loadEvents();
                }
                
            });       
        }        
    
    };    
        
    //get events for the selected program (and org unit)
    $scope.loadEvents = function(){   
        
        $scope.noteExists = false;
        $scope.selectedProgramStage = null;
        
        //Filtering
        $scope.reverse = false;
        $scope.filterText = {}; 
    
        $scope.dhis2Events = [];
        $scope.eventLength = 0;

        $scope.eventFetched = false;
               
        if( $scope.selectedProgram && $scope.selectedProgram.programStages[0].id){
            
            $scope.optionSets = [];
            OptionSetService.getAll().then(function(optionSets){
                
                angular.forEach(optionSets, function(optionSet){
                    $scope.optionSets[optionSet.id] = optionSet;                    
                });                
                
                //because this is single event, take the first program stage
                ProgramStageFactory.get($scope.selectedProgram.programStages[0].id).then(function (programStage){

                    $scope.selectedProgramStage = programStage;   

                    angular.forEach($scope.selectedProgramStage.programStageSections, function(section){
                        section.open = true;
                    });

                    //$scope.customForm = CustomFormService.processCustomForm($scope.selectedProgramStage);
                    $scope.customForm = $scope.selectedProgramStage.dataEntryForm ? $scope.selectedProgramStage.dataEntryForm.htmlCode : null; 

                    $scope.prStDes = [];  
                    $scope.eventGridColumns = [];
                    $scope.filterTypes = {};

                    $scope.newDhis2Event = {dataValues: []};
                    $scope.currentEvent = {dataValues: []};

                    $scope.eventGridColumns.push({name: 'form_id', id: 'uid', type: 'string', compulsory: false, showFilter: false, show: false});
                    $scope.filterTypes['uid'] = 'string';                

                    $scope.eventGridColumns.push({name: $scope.selectedProgramStage.reportDateDescription ? $scope.selectedProgramStage.reportDateDescription : 'incident_date', id: 'event_date', type: 'date', compulsory: false, showFilter: false, show: true});
                    $scope.filterTypes['event_date'] = 'date';
                    $scope.filterText['event_date']= {};

                    angular.forEach($scope.selectedProgramStage.programStageDataElements, function(prStDe){
                        $scope.prStDes[prStDe.dataElement.id] = prStDe;                    

                        $scope.newDhis2Event.dataValues.push({id: prStDe.dataElement.id, value: ''});   
                        if($scope.selectedProgramStage.captureCoordinates){
                            $scope.newDhis2Event.coordinate = {};
                        }

                        //generate grid headers using program stage data elements
                        //create a template for new event
                        //for date type dataelements, filtering is based on start and end dates                    
                        $scope.eventGridColumns.push({name: prStDe.dataElement.formName ? prStDe.dataElement.formName : prStDe.dataElement.name, 
                                                      id: prStDe.dataElement.id, 
                                                      type: prStDe.dataElement.type, 
                                                      compulsory: prStDe.compulsory, 
                                                      showFilter: false, 
                                                      show: prStDe.displayInReports});

                        $scope.filterTypes[prStDe.dataElement.id] = prStDe.dataElement.type;

                        if(prStDe.dataElement.type === 'date' || prStDe.dataElement.type === 'int' ){
                            $scope.filterText[prStDe.dataElement.id]= {};
                        }
                    });           

                    //Load events for the selected program stage and orgunit
                    DHIS2EventFactory.getByStage($scope.selectedOrgUnit.id, $scope.selectedProgramStage.id, $scope.pager ).then(function(data){

                        if(data.events){
                            $scope.eventLength = data.events.length;
                        }                

                        $scope.dhis2Events = data.events; 

                        if( data.pager ){
                            $scope.pager = data.pager;
                            $scope.pager.toolBarDisplay = 5;

                            Paginator.setPage($scope.pager.page);
                            Paginator.setPageCount($scope.pager.pageCount);
                            Paginator.setPageSize($scope.pager.pageSize);
                            Paginator.setItemCount($scope.pager.total);                    
                        }

                        //process event list for easier tabular sorting
                        if( angular.isObject( $scope.dhis2Events ) ) {

                            for(var i=0; i < $scope.dhis2Events.length; i++){  

                                if($scope.dhis2Events[i].notes && !$scope.noteExists){
                                    $scope.noteExists = true;
                                }

                                //check if event is empty
                                if(!angular.isUndefined($scope.dhis2Events[i].dataValues)){                            

                                    angular.forEach($scope.dhis2Events[i].dataValues, function(dataValue){

                                        //converting event.datavalues[i].datavalue.dataelement = value to
                                        //event[dataElement] = value for easier grid display.                                
                                        if($scope.prStDes[dataValue.dataElement]){                                    

                                            var val = dataValue.value;
                                            if(angular.isObject($scope.prStDes[dataValue.dataElement].dataElement)){                               

                                                //converting int string value to integer for proper sorting.
                                                if($scope.prStDes[dataValue.dataElement].dataElement.type === 'int'){
                                                    if( !isNaN(parseInt(val)) ){
                                                        val = parseInt(val);
                                                    }
                                                    else{
                                                        val = '';
                                                    }                                        
                                                }
                                                if($scope.prStDes[dataValue.dataElement].dataElement.type === 'string'){
                                                    if($scope.prStDes[dataValue.dataElement].dataElement.optionSet ){
                                                        val = OptionSetService.getNameOrCode($scope.optionSets[$scope.prStDes[dataValue.dataElement].dataElement.optionSet.id].options, val);
                                                    }                                                
                                                }
                                                if($scope.prStDes[dataValue.dataElement].dataElement.type === 'date'){
                                                    val = DateUtils.formatFromApiToUser(val);                                               
                                                }
                                                if( $scope.prStDes[dataValue.dataElement].dataElement.type === 'trueOnly'){
                                                    if(val == 'true'){
                                                        val = true;
                                                    }
                                                    else{
                                                        val = false;
                                                    }
                                                }                                    
                                            }                                    
                                            $scope.dhis2Events[i][dataValue.dataElement] = val; 
                                        }

                                    });

                                    $scope.dhis2Events[i]['uid'] = $scope.dhis2Events[i].event;                                
                                    $scope.dhis2Events[i].eventDate = DateUtils.formatFromApiToUser($scope.dhis2Events[i].eventDate);                                
                                    $scope.dhis2Events[i]['event_date'] = $scope.dhis2Events[i].eventDate;

                                    delete $scope.dhis2Events[i].dataValues;
                                }
                                /*else{//event is empty, remove from grid
                                    var index = $scope.dhis2Events.indexOf($scope.dhis2Events[i]);                           
                                    $scope.dhis2Events.splice(index,1);
                                    i--;                           
                                }*/
                            }

                            if($scope.noteExists){
                                $scope.eventGridColumns.push({name: 'comment', id: 'comment', type: 'string', compulsory: false, showFilter: false, show: true});
                            }
                        }                
                        $scope.eventFetched = true;
                    });            

                });
            });
        }        
    };
    
    $scope.jumpToPage = function(){
        $scope.loadEvents();
    };
    
    $scope.resetPageSize = function(){
        $scope.pager.page = 1;        
        $scope.loadEvents();
    };
    
    $scope.getPage = function(page){    
        $scope.pager.page = page;
        $scope.loadEvents();
    };
    
    $scope.sortEventGrid = function(gridHeader){
        
        if ($scope.sortHeader === gridHeader.id){
            $scope.reverse = !$scope.reverse;
            return;
        }        
        $scope.sortHeader = gridHeader.id;
        $scope.reverse = false;    
    };
    
    $scope.showHideColumns = function(){
        
        $scope.hiddenGridColumns = 0;
        
        angular.forEach($scope.eventGridColumns, function(eventGridColumn){
            if(!eventGridColumn.show){
                $scope.hiddenGridColumns++;
            }
        });
        
        var modalInstance = $modal.open({
            templateUrl: 'views/column-modal.html',
            controller: 'ColumnDisplayController',
            resolve: {
                eventGridColumns: function () {
                    return $scope.eventGridColumns;
                },
                hiddenGridColumns: function(){
                    return $scope.hiddenGridColumns;
                }
            }
        });

        modalInstance.result.then(function (eventGridColumns) {
            $scope.eventGridColumns = eventGridColumns;
        }, function () {
        });
    };
    
    $scope.searchInGrid = function(gridColumn){
        
        $scope.currentFilter = gridColumn;
       
        for(var i=0; i<$scope.eventGridColumns.length; i++){
            
            //toggle the selected grid column's filter
            if($scope.eventGridColumns[i].id === gridColumn.id){
                $scope.eventGridColumns[i].showFilter = !$scope.eventGridColumns[i].showFilter;
            }            
            else{
                $scope.eventGridColumns[i].showFilter = false;
            }
        }
    };    
    
    $scope.removeStartFilterText = function(gridColumnId){
        $scope.filterText[gridColumnId].start = undefined;
    };
    
    $scope.removeEndFilterText = function(gridColumnId){
        $scope.filterText[gridColumnId].end = undefined;
    };
    
    $scope.showEventList = function(){
        $scope.eventRegistration = false;
        $scope.editingEventInFull = false;
        $scope.editingEventInGrid = false;
        $scope.currentElement.updated = false;
        
        $scope.outerForm.$valid = true;
        
        $scope.currentEvent = {};
    };
    
    $scope.showEventRegistration = function(){        
        $scope.displayCustomForm = $scope.customForm ? true:false;        
        $scope.currentEvent = {};
        $scope.eventRegistration = !$scope.eventRegistration;          
        $scope.currentEvent = angular.copy($scope.newDhis2Event);        
        $scope.outerForm.submitted = false;
        $scope.note = {};
        
        if($scope.selectedProgramStage.preGenerateUID){
            $scope.eventUID = dhis2.util.uid();
            $scope.currentEvent['uid'] = $scope.eventUID;
        }        
    };    
    
    $scope.showEditEventInGrid = function(){
        $scope.currentEvent = ContextMenuSelectedItem.getSelectedItem();  
        $scope.currentEventOrginialValue = angular.copy($scope.currentEvent);        
        $scope.editingEventInGrid = !$scope.editingEventInGrid;              
        //$scope.currentEvent['uid'] = $scope.currentEvent.event;
        
        $scope.outerForm.$valid = true;
    };
    
    $scope.showEditEventInFull = function(){       
        $scope.note = {};
        $scope.displayCustomForm = $scope.customForm ? true:false;                

        $scope.currentEvent = ContextMenuSelectedItem.getSelectedItem();  
        $scope.currentEventOrginialValue = angular.copy($scope.currentEvent);
        $scope.editingEventInFull = !$scope.editingEventInFull;   
        $scope.eventRegistration = false;
        
        angular.forEach($scope.selectedProgramStage.programStageDataElements, function(prStDe){
            if(!$scope.currentEvent.hasOwnProperty(prStDe.dataElement.id)){
                $scope.currentEvent[prStDe.dataElement.id] = '';
            }
        });        
    };
    
    $scope.switchDataEntryForm = function(){
        $scope.displayCustomForm = !$scope.displayCustomForm;
    };
    
    $scope.addEvent = function(addingAnotherEvent){                

        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid ){
            angular.forEach($scope.selectedProgramStage.programStageSections, function(section){
                section.open = true;
            });
            return false;
        }
        
        if(addingAnotherEvent){
            $scope.disableSaveAndAddNew = true;
        }
        
        //the form is valid, get the values
        //but there could be a case where all dataelements are non-mandatory and
        //the event form comes empty, in this case enforce at least one value
        var valueExists = false;
        var dataValues = [];        
        for(var dataElement in $scope.prStDes){            
            var val = $scope.currentEvent[dataElement];
            if(val){
                valueExists = true;            
                if($scope.prStDes[dataElement].dataElement.type === 'string'){
                    if($scope.prStDes[dataElement].dataElement.optionSet){
                        /*if($scope.optionCodesByName[  '"' + val + '"']){
                            val = $scope.optionCodesByName[  '"' + val + '"'];
                        }*/
                        val = OptionSetService.getNameOrCode($scope.optionSets[$scope.prStDes[dataElement].dataElement.optionSet.id].options,val); //$scope.optionSets[].options$scope.optionCodesByName[  '"' + val + '"'];
                    }
                }

                if($scope.prStDes[dataElement].dataElement.type === 'date'){
                    val = DateUtils.formatFromUserToApi(val);
                }
            }
            dataValues.push({dataElement: dataElement, value: val});
        }
        
        if(!valueExists){
            var dialogOptions = {
                headerText: 'empty_form',
                bodyText: 'please_fill_at_least_one_dataelement'
            };

            DialogService.showDialog({}, dialogOptions);
            return false;
        }        
        
        var newEvent = angular.copy($scope.currentEvent);        
        
        //prepare the event to be created
        var dhis2Event = {
                program: $scope.selectedProgram.id,
                programStage: $scope.selectedProgramStage.id,
                orgUnit: $scope.selectedOrgUnit.id,
                status: 'ACTIVE',            
                eventDate: DateUtils.formatFromUserToApi(newEvent.eventDate),
                dataValues: dataValues
        }; 
        
        if($scope.selectedProgramStage.preGenerateUID && !angular.isUndefined(newEvent['uid'])){
            dhis2Event.event = newEvent['uid'];
        }
        
        if(!angular.isUndefined($scope.note.value) && $scope.note.value != ''){
            dhis2Event.notes = [{value: $scope.note.value}];
            
            newEvent.notes = [{value: $scope.note.value, storedDate: $scope.today, storedBy: storedBy}];
            
            $scope.noteExists = true;
        }
        
        if($scope.selectedProgramStage.captureCoordinates){
            dhis2Event.coordinate = {latitude: $scope.currentEvent.coordinate.latitude ? $scope.currentEvent.coordinate.latitude : '',
                                     longitude: $scope.currentEvent.coordinate.longitude ? $scope.currentEvent.coordinate.longitude : ''};             
        }
        
        //send the new event to server
        DHIS2EventFactory.create(dhis2Event).then(function(data) {
            if (data.importSummaries[0].status === 'ERROR') {
                var dialogOptions = {
                    headerText: 'event_registration_error',
                    bodyText: data.importSummaries[0].description
                };

                DialogService.showDialog({}, dialogOptions);
            }
            else {
                
                //add the new event to the grid                
                newEvent.event = data.importSummaries[0].reference;                
                if( !$scope.dhis2Events ){
                    $scope.dhis2Events = [];                   
                }
                newEvent['uid'] = newEvent.event;
                newEvent['event_date'] = newEvent.eventDate; 
                $scope.dhis2Events.splice(0,0,newEvent);
                
                $scope.eventLength++;
                
                //decide whether to stay in the current screen or not.
                if(!addingAnotherEvent){
                    $scope.eventRegistration = false;
                    $scope.editingEventInFull = false;
                    $scope.editingEventInGrid = false;  
                }

                //reset form                
                $scope.currentEvent = angular.copy($scope.newDhis2Event); 
                $scope.note = {};
                $scope.outerForm.submitted = false;
                $scope.disableSaveAndAddNew = false;
                
                //this is to hide typeAheadPopUps - shouldn't be an issue in 
                //the first place.                
                $timeout(function() {
                    angular.element('#hideTypeAheadPopUp').trigger('click');
                }, 10);
            }
        });
    }; 
    
    $scope.updateEvent = function(){
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid ){
            angular.forEach($scope.selectedProgramStage.programStageSections, function(section){
                section.open = true;
            });
            return false;
        }
        
        //the form is valid, get the values
        var dataValues = [];        
        for(var dataElement in $scope.prStDes){
            var val = $scope.currentEvent[dataElement];
            
            if(val && $scope.prStDes[dataElement].dataElement.type === 'string'){
                if($scope.prStDes[dataElement].dataElement.optionSet){                        
                    /*if($scope.optionCodesByName[  '"' + val + '"']){
                        val = $scope.optionCodesByName[  '"' + val + '"'];
                    }*/ 
                    val = OptionSetService.getNameOrCode($scope.optionSets[$scope.prStDes[dataElement].dataElement.optionSet.id].options,val); 
                }    
            }
            if(val && $scope.prStDes[dataElement].dataElement.type === 'date'){
                val = DateUtils.formatFromUserToApi(val);    
            }
            dataValues.push({dataElement: dataElement, value: val});
        }
        
        var updatedEvent = {
                            program: $scope.currentEvent.program,
                            programStage: $scope.currentEvent.programStage,
                            orgUnit: $scope.currentEvent.orgUnit,
                            status: 'ACTIVE',                                        
                            eventDate: DateUtils.formatFromUserToApi($scope.currentEvent.eventDate),
                            event: $scope.currentEvent.event, 
                            dataValues: dataValues
                        };

        
        if($scope.selectedProgramStage.captureCoordinates){
            updatedEvent.coordinate = {latitude: $scope.currentEvent.coordinate.latitude ? $scope.currentEvent.coordinate.latitude : '',
                                     longitude: $scope.currentEvent.coordinate.longitude ? $scope.currentEvent.coordinate.longitude : ''};             
        }
        
        if(!angular.isUndefined($scope.note.value) && $scope.note.value != ''){
           
            updatedEvent.notes = [{value: $scope.note.value}];
            
            if($scope.currentEvent.notes){
                $scope.currentEvent.notes.splice(0,0,{value: $scope.note.value, storedDate: $scope.today, storedBy: storedBy});
            }
            else{
                $scope.currentEvent.notes = [{value: $scope.note.value, storedDate: $scope.today, storedBy: storedBy}];
            }   
            
            $scope.noteExists = true;
        }

        DHIS2EventFactory.update(updatedEvent).then(function(data){            
            
            //update original value
            var continueLoop = true;
            for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                if($scope.dhis2Events[i].event === $scope.currentEvent.event ){
                    $scope.dhis2Events[i] = $scope.currentEvent;
                    continueLoop = false;
                }
            }
                
            $scope.currentEventOrginialValue = angular.copy($scope.currentEvent); 
            $scope.outerForm.submitted = false;            
            $scope.editingEventInFull = false;
            $scope.currentEvent = {};
        });       
    };
       
    $scope.updateEventDataValue = function(currentEvent, dataElement){

        $scope.updateSuccess = false;
        
        //get current element
        $scope.currentElement = {id: dataElement};
        
        //get new and old values
        var newValue = currentEvent[dataElement];        
        var oldValue = $scope.currentEventOrginialValue[dataElement];
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid ){
            $scope.currentElement.updated = false;
            currentEvent[dataElement] = oldValue;
            return;
        }   
        
        if( $scope.prStDes[dataElement].compulsory && !newValue ) {            
            currentEvent[dataElement] = oldValue;
            $scope.currentElement.updated = false;
            return;
        }        
                
        if( newValue != oldValue ){
            
            if($scope.prStDes[dataElement].dataElement.type === 'string'){
                if($scope.prStDes[dataElement].dataElement.optionSet){
                    /*if($scope.optionCodesByName[  '"' + newValue + '"']){
                        newValue = $scope.optionCodesByName[  '"' + newValue + '"'];
                    }*/
                    newValue = OptionSetService.getNameOrCode($scope.optionSets[$scope.prStDes[dataElement].dataElement.optionSet.id].options, newValue);//$scope.optionCodesByName[  '"' + newValue + '"'];
                }
            }            
            if($scope.prStDes[dataElement].dataElement.type === 'date'){
                newValue = DateUtils.formatFromUserToApi(newValue);
            }
            
            var updatedSingleValueEvent = {event: currentEvent.event, dataValues: [{value: newValue, dataElement: dataElement}]};
            var updatedFullValueEvent = DHIS2EventService.reconstructEvent(currentEvent, $scope.selectedProgramStage.programStageDataElements);

            DHIS2EventFactory.updateForSingleValue(updatedSingleValueEvent, updatedFullValueEvent).then(function(data){
                
                var continueLoop = true;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === currentEvent.event ){
                        $scope.dhis2Events[i] = currentEvent;
                        continueLoop = false;
                    }
                }
                
                //update original value
                $scope.currentEventOrginialValue = angular.copy(currentEvent);      
                
                $scope.currentElement.updated = true;
                $scope.updateSuccess = true;
            });
        }
    };
    
    $scope.removeEvent = function(){
        
        var dhis2Event = ContextMenuSelectedItem.getSelectedItem();
        
        var modalOptions = {
            closeButtonText: 'cancel',
            actionButtonText: 'remove',
            headerText: 'remove',
            bodyText: 'are_you_sure_to_remove'
        };

        ModalService.showModal({}, modalOptions).then(function(result){
            
            DHIS2EventFactory.delete(dhis2Event).then(function(data){
                
                var continueLoop = true, index = -1;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === dhis2Event.event ){
                        $scope.dhis2Events[i] = dhis2Event;
                        continueLoop = false;
                        index = i;
                    }
                }
                $scope.dhis2Events.splice(index,1);                
                $scope.currentEvent = {};             
            });
        });        
    };
        
    $scope.showNotes = function(dhis2Event){
        
        var modalInstance = $modal.open({
            templateUrl: 'views/notes.html',
            controller: 'NotesController',
            resolve: {
                dhis2Event: function () {
                    return dhis2Event;
                }
            }
        });

        modalInstance.result.then(function (){
        });
    };
    
    $scope.getHelpContent = function(){
    };
    
    $scope.showMap = function(event){
        var modalInstance = $modal.open({
            templateUrl: 'views/map.html',
            controller: 'MapController',
            windowClass: 'modal-full-window',
            resolve: {
                location: function () {
                    return {lat: event.coordinate.latitude, lng: event.coordinate.longitude};
                },
                geoJsons: function(){
                    return $scope.geoJsons;
                }
            }
        });

        modalInstance.result.then(function (location) {
            if(angular.isObject(location)){
                event.coordinate.latitude = location.lat;
                event.coordinate.longitude = location.lng;
            }
        }, function () {
        });
    };
});