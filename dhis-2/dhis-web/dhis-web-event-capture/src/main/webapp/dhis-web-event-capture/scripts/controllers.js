'use strict';

/* Controllers */
var eventCaptureControllers = angular.module('eventCaptureControllers', [])

//Controller for settings page
.controller('MainController',
        function($scope,
                $filter,
                $modal,
                storage,
                Paginator,
                TranslationService,                
                ProgramFactory,
                ProgramStageFactory,
                DHIS2EventFactory,       
                DHIS2EventService,
                ContextMenuSelectedItem,
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
    var today = moment();
    today = Date.parse(today);
    today = $filter('date')(today, 'yyyy-MM-dd');
    
    var loginDetails = storage.get('LOGIN_DETAILS');
    var storedBy = '';
    if(loginDetails){
        storedBy = loginDetails.userCredentials.username;
    }
    $scope.noteExists = false;
        
    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function(newObj, oldObj) {
        
        if( angular.isObject($scope.selectedOrgUnit)){            
            
            //apply translation - by now user's profile is fetched from server.
            TranslationService.translate();            

            $scope.loadPrograms();

        }
    });
    
    //load programs associated with the selected org unit.
    $scope.loadPrograms = function() {        
                
        //$scope.selectedOrgUnit = orgUnit;
        $scope.selectedProgram = null;
        $scope.selectedProgramStage = null;

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
            
            //because this is single event, take the first program stage
            ProgramStageFactory.get($scope.selectedProgram.programStages[0].id).then(function (programStage){
                
                $scope.selectedProgramStage = programStage;   
               
                //$scope.customForm = CustomFormService.processCustomForm($scope.selectedProgramStage);
                $scope.customForm = $scope.selectedProgramStage.dataEntryForm ? $scope.selectedProgramStage.dataEntryForm.htmlCode : null; 

                $scope.programStageDataElements = [];  
                $scope.eventGridColumns = [];
                $scope.filterTypes = {};

                $scope.newDhis2Event = {dataValues: []};
                $scope.currentEvent = {dataValues: []};

                angular.forEach($scope.selectedProgramStage.programStageDataElements, function(prStDe){
                    $scope.programStageDataElements[prStDe.dataElement.id] = prStDe; 

                    //generate grid headers using program stage data elements
                    //create a template for new event
                    //for date type dataelements, filtering is based on start and end dates
                    var dataElement = prStDe.dataElement;
                    var name = dataElement.formName || dataElement.name;
                    
                    $scope.newDhis2Event.dataValues.push({id: dataElement.id, value: ''});   
                    if($scope.selectedProgramStage.captureCoordinates){
                        $scope.newDhis2Event.coordinate = {};
                    }
                    
                    $scope.eventGridColumns.push({name: name, id: dataElement.id, type: dataElement.type, compulsory: prStDe.compulsory, showFilter: false, show: prStDe.displayInReports});

                    $scope.filterTypes[dataElement.id] = dataElement.type;

                    if(dataElement.type === 'date' || dataElement.type === 'int' ){
                         $scope.filterText[dataElement.id]= {};
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
                                    if($scope.programStageDataElements[dataValue.dataElement]){                                    

                                        var dataElement = $scope.programStageDataElements[dataValue.dataElement].dataElement;

                                        if(angular.isObject(dataElement)){                               

                                            //converting int string value to integer for proper sorting.
                                            if(dataElement.type == 'int'){
                                                if( !isNaN(parseInt(dataValue.value)) ){
                                                    dataValue.value = parseInt(dataValue.value);
                                                }
                                                else{
                                                    dataValue.value = '';
                                                }                                        
                                            }
                                            else if( dataElement.type == 'trueOnly'){
                                                if(dataValue.value == 'true'){
                                                    dataValue.value = true;
                                                }
                                                else{
                                                    dataValue.value = false;
                                                }
                                            }                                    
                                        }                                    
                                    }

                                    $scope.dhis2Events[i][dataValue.dataElement] = dataValue.value; 
                                });  

                                delete $scope.dhis2Events[i].dataValues;
                            }
                            else{//event is empty, remove from grid
                                var index = $scope.dhis2Events.indexOf($scope.dhis2Events[i]);                           
                                $scope.dhis2Events.splice(index,1);
                                i--;                           
                            }
                        }                                  
                    }                
                    $scope.eventFetched = true;
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
        
        $scope.eventRegistration = !$scope.eventRegistration;          
        $scope.currentEvent = angular.copy($scope.newDhis2Event);        
        $scope.outerForm.submitted = false;
        $scope.note = {};
        
        //$scope.currentEvent = {};
    };    
    
    $scope.showEditEventInGrid = function(){
        $scope.currentEvent = ContextMenuSelectedItem.getSelectedItem();  
        $scope.currentEventOrginialValue = angular.copy($scope.currentEvent);
        $scope.editingEventInGrid = !$scope.editingEventInGrid;                
        $scope.outerForm.$valid = true;
    };
    
    $scope.showEditEventInFull = function(){       
        $scope.note = {};
        $scope.displayCustomForm = $scope.customForm ? true:false;                

        $scope.currentEvent = ContextMenuSelectedItem.getSelectedItem();  
        $scope.currentEventOrginialValue = angular.copy($scope.currentEvent);
        $scope.editingEventInFull = !$scope.editingEventInFull;   
        $scope.eventRegistration = false;
        
        $scope.currentEvent.eventDate = moment($scope.currentEvent.eventDate, 'YYYY-MM-DD')._d;       
        $scope.currentEvent.eventDate = Date.parse($scope.currentEvent.eventDate);
        $scope.currentEvent.eventDate = $filter('date')($scope.currentEvent.eventDate, 'yyyy-MM-dd');
        
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
            return false;
        }
        
        //the form is valid, get the values
        var dataValues = [];        
        for(var dataElement in $scope.programStageDataElements){
            dataValues.push({dataElement: dataElement, value: $scope.currentEvent[dataElement]});
        }
        
        var newEvent = angular.copy($scope.currentEvent);        
        
        //prepare the event to be created
        var dhis2Event = {
                program: $scope.selectedProgram.id,
                programStage: $scope.selectedProgramStage.id,
                orgUnit: $scope.selectedOrgUnit.id,
                status: 'ACTIVE',            
                eventDate: $filter('date')(newEvent.eventDate, 'yyyy-MM-dd'),
                dataValues: dataValues
        }; 
        
        if(!angular.isUndefined($scope.note.value) && $scope.note.value != ''){
            dhis2Event.notes = [{value: $scope.note.value}];
            
            newEvent.notes = [{value: $scope.note.value, storedDate: today, storedBy: storedBy}];
            
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
                $scope.dhis2Events.splice(0,0,newEvent);
                
                $scope.eventLength++;
                
                //decide whether to stay in the current screen or not.
                if(!addingAnotherEvent){
                    $scope.eventRegistration = false;
                    $scope.editingEventInFull = false;
                    $scope.editingEventInGrid = false;  
                    $scope.outerForm.submitted = false;
                }
                //reset form
                $scope.currentEvent = angular.copy($scope.newDhis2Event); 
                $scope.note = {};
                $scope.outerForm.submitted = false;
            }
        });
    }; 
    
    $scope.updateEvent = function(){
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid ){
            return false;
        }
        
        //the form is valid, get the values
        var dataValues = [];        
        for(var dataElement in $scope.programStageDataElements){
            dataValues.push({dataElement: dataElement, value: $scope.currentEvent[dataElement]});
        }
        
        var updatedEvent = {
                            program: $scope.currentEvent.program,
                            programStage: $scope.currentEvent.programStage,
                            orgUnit: $scope.currentEvent.orgUnit,
                            status: 'ACTIVE',                                        
                            eventDate: $scope.currentEvent.eventDate,
                            event: $scope.currentEvent.event, 
                            dataValues: dataValues
                        };
                        
        updatedEvent.eventDate = moment(updatedEvent.eventDate, 'YYYY-MM-DD')._d;       
        updatedEvent.eventDate = Date.parse(updatedEvent.eventDate);
        updatedEvent.eventDate = $filter('date')(updatedEvent.eventDate, 'yyyy-MM-dd'); 
        
        if($scope.selectedProgramStage.captureCoordinates){
            updatedEvent.coordinate = {latitude: $scope.currentEvent.coordinate.latitude ? $scope.currentEvent.coordinate.latitude : '',
                                     longitude: $scope.currentEvent.coordinate.longitude ? $scope.currentEvent.coordinate.longitude : ''};             
        }
        
        if(!angular.isUndefined($scope.note.value) && $scope.note.value != ''){
           
            updatedEvent.notes = [{value: $scope.note.value}];
            
            if($scope.currentEvent.notes){
                $scope.currentEvent.notes.splice(0,0,{value: $scope.note.value, storedDate: today, storedBy: storedBy});
            }
            else{
                $scope.currentEvent.notes = [{value: $scope.note.value, storedDate: today, storedBy: storedBy}];
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
        
        if( $scope.programStageDataElements[dataElement].compulsory && !newValue ) {            
            currentEvent[dataElement] = oldValue;
            $scope.currentElement.updated = false;
            return;
        }        
                
        if( newValue != oldValue ){                     
            
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
})

//Controller for notes
.controller('NotesController', 
    function($scope, 
            $modalInstance, 
            dhis2Event){
    
    $scope.dhis2Event = dhis2Event;
    
    $scope.close = function () {
        $modalInstance.close();
    };      
})

//Controller for the header section
.controller('HeaderController',
        function($scope,                
                DHIS2URL,
                TranslationService) {

    TranslationService.translate();
    
    $scope.home = function(){        
        window.location = DHIS2URL;
    };
    
})

//Controller for column show/hide
.controller('ColumnDisplayController', 
    function($scope, 
            $modalInstance, 
            hiddenGridColumns,
            eventGridColumns){
    
    $scope.eventGridColumns = eventGridColumns;
    $scope.hiddenGridColumns = hiddenGridColumns;
    
    $scope.close = function () {
      $modalInstance.close($scope.eventGridColumns);
    };
    
    $scope.showHideColumns = function(gridColumn){
       
        if(gridColumn.show){                
            $scope.hiddenGridColumns--;            
        }
        else{
            $scope.hiddenGridColumns++;            
        }      
    };    
});
