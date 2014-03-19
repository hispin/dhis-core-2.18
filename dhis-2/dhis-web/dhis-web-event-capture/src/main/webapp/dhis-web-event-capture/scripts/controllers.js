'use strict';

/* Controllers */
var eventCaptureControllers = angular.module('eventCaptureControllers', [])

//Controller for settings page
.controller('MainController',
        function($scope,
                $filter,
                Paginator,
                TranslationService,
                storage,
                DHIS2EventFactory,                
                orderByFilter,
                ContextMenuSelectedItem,
                ModalService,                
                DialogService) {   
   
    //selected org unit
    $scope.selectedOrgUnit = '';
    
    //Paging
    $scope.pager = {pageSize: 50, page: 1, toolBarDisplay: 5};
    
    
    //Filtering
    $scope.reverse = false;
    $scope.filterText = {}; 
    
    //Editing
    $scope.eventRegistration = false;
    $scope.editGridColumns = false;
    $scope.editingEventInFull = false;
    $scope.editingEventInGrid = false;   
    $scope.currentGridColumnId = '';    
    
    $scope.programStageDataElements = [];
                
    $scope.dhis2Events = [];
    $scope.eventGridColumns = [];
    $scope.hiddenGridColumns = 0;
    $scope.newDhis2Event = {dataValues: []};
    $scope.currentEvent = {dataValues: []};
    $scope.currentEventOrginialValue = '';   
    
    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function(newObj, oldObj) {
        
        if( angular.isObject($scope.selectedOrgUnit)){            
            
            //apply translation - by now user's profile is fetched from server.
            TranslationService.translate();
            
            var programs = storage.get('PROGRAMS');            
            if( programs ){                
                $scope.loadPrograms($scope.selectedOrgUnit);     
            }            
        }
    });
    
    //load programs associated with the selected org unit.
    $scope.loadPrograms = function(orgUnit) {        
                
        $scope.selectedOrgUnit = orgUnit;
        $scope.selectedProgram = null;
        $scope.selectedProgramStage = null;
        
        if (angular.isObject($scope.selectedOrgUnit)) {   

            $scope.programs = [];
            
            var programs = storage.get('PROGRAMS');
            
            if( programs ){
                for(var i=0; i<programs.length; i++){
                    var program = storage.get(programs[i].id);   
                    if(program.organisationUnits.hasOwnProperty(orgUnit.id)){
                        $scope.programs.push(program);
                    }
                }
                
                if( !angular.isUndefined($scope.programs)){                    
                    if($scope.programs.length === 1){
                        $scope.selectedProgram = $scope.programs[0];
                        $scope.pr = $scope.selectedProgram;
                        $scope.loadEvents($scope.pr, $scope.pager);
                    }                    
                }
            }
        }        
    };    
    
    //get events for the selected program (and org unit)
    $scope.loadEvents = function(program, pager){   
        
        $scope.dhis2Events = [];
               
        if( program ){
            
            //because this is single event, take the first program stage
            $scope.selectedProgramStage = storage.get(program.programStages[0].id);

            $scope.programStageDataElements = [];  
            $scope.eventGridColumns = [];
            $scope.hiddenGridColumns = 0;
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
                $scope.eventGridColumns.push({name: name, id: dataElement.id, type: dataElement.type, compulsory: prStDe.compulsory, showFilter: false, hide: false});

                if(dataElement.type === 'date'){
                     $scope.filterText[dataElement.id]= {start: '', end: ''};
                }
            });           

            //Load events for the selected program stage and orgunit
            DHIS2EventFactory.getByStage($scope.selectedOrgUnit.id, $scope.selectedProgramStage.id, pager ).then(function(data){
                
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
                //angular.forEach($scope.dhis2Events, function(dhis2Event){ 
                if( angular.isObject( $scope.dhis2Events ) ) {

                    for(var i=0; i < $scope.dhis2Events.length; i++){  
                        //check if event is empty
                        if(!angular.isUndefined($scope.dhis2Events[i].dataValues[0].dataElement)){
                            $scope.dhis2Events[i].dataValues = orderByFilter($scope.dhis2Events[i].dataValues, '-dataElement');
                            angular.forEach($scope.dhis2Events[i].dataValues, function(dataValue){

                                //converting int value to integer for proper sorting.
                                var dataElement = $scope.programStageDataElements[dataValue.dataElement].dataElement;
                                if(angular.isObject(dataElement)){                               
                                    if(dataElement.type == 'int'){
                                        if( !isNaN(parseInt(dataValue.value)) ){
                                            dataValue.value = parseInt(dataValue.value);
                                        }
                                        else{
                                            dataValue.value = '';
                                        }
                                        
                                    }
                                    $scope.dhis2Events[i][dataValue.dataElement] = dataValue.value; 
                                }                                
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
            });            
        }        
    };
    
    $scope.jumpToPage = function(){
        $scope.loadEvents($scope.selectedProgram, $scope.pager);
    };
    
    $scope.resetPageSize = function(){
        $scope.pager.page = 1;        
        $scope.loadEvents($scope.selectedProgram, $scope.pager);
    };
    
    $scope.getPage = function(page){    
        $scope.pager.page = page;
        $scope.loadEvents($scope.selectedProgram, $scope.pager);
    };
    
    $scope.sortEventGrid = function(gridHeader){
        
        if ($scope.sortHeader === gridHeader.id){
            $scope.reverse = !$scope.reverse;
            return;
        }        
        $scope.sortHeader = gridHeader.id;
        $scope.reverse = false;    
    };
    
    $scope.showHideColumns = function(gridColumn, showAllColumns){
        if(showAllColumns){
            angular.forEach($scope.eventGridColumns, function(gridHeader){
                gridHeader.hide = false;                
            });
            $scope.hiddenGridColumns = 0;
        }
        if(!showAllColumns){            
            if(gridColumn.hide){
                $scope.hiddenGridColumns++;
            }
            else{
                $scope.hiddenGridColumns--;
            }
        }      
    };
    
    $scope.searchInGrid = function(gridColumn){           
        
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
        $scope.filterText[gridColumnId].start = '';
    };
    
    $scope.removeEndFilterText = function(gridColumnId){
        $scope.filterText[gridColumnId].end = '';
    };
    
    $scope.showEventList = function(){
        $scope.eventRegistration = false;
        $scope.editingEventInFull = false;
        $scope.editingEventInGrid = false;
        
        $scope.outerForm.$valid = true;
        
        $scope.currentEvent = {};
    };
    
    $scope.showEventRegistration = function(){
        $scope.eventRegistration = !$scope.eventRegistration;  
        $scope.currentEvent = $scope.newDhis2Event;        
        $scope.outerForm.submitted = false;
        
        $scope.currentEvent = {};
    };    
    
    $scope.showEditEventInGrid = function(){
        $scope.currentEvent = ContextMenuSelectedItem.getSelectedItem();  
        $scope.currentEventOrginialValue = angular.copy($scope.currentEvent);
        $scope.editingEventInGrid = !$scope.editingEventInGrid;                
        $scope.outerForm.$valid = true;
    };
    
    $scope.showEditEventInFull = function(){
        
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
                
                //decide whether to stay in the current screen or not.
                if(!addingAnotherEvent){
                    $scope.eventRegistration = false;
                    $scope.editingEventInFull = false;
                    $scope.editingEventInGrid = false;                    
                }
                $scope.currentEvent = {};
            }
        });        
    }; 
       
    $scope.updateEventDataValue = function(currentEvent, dataElement){
        
        //get current column
        $scope.currentGridColumnId = dataElement;
        
        //get new and old values
        var newValue = currentEvent[dataElement];
        var oldValue = $scope.currentEventOrginialValue[dataElement];
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid ){
            $scope.updateSuccess = false;
            currentEvent[dataElement] = oldValue;
            return;
        }      
                
        if( newValue !== oldValue ){                     
            
            var updatedSingleValueEvent = {event: currentEvent.event, dataValues: [{value: newValue, dataElement: dataElement}]};
            var updatedFullValueEvent = reconstructEvent(currentEvent, $scope.selectedProgramStage.programStageDataElements);
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
    
    $scope.getHelpContent = function(){
        console.log('I will get help content');
    };
    
    //for simplicity of grid display, events were changed from
    //event.datavalues = [{dataElement: dataElement, value: value}] to
    //event[dataElement] = value
    //now they are changed back for the purpose of storage.    
    function reconstructEvent(event, programStageDataElements)
    {
        var e = {};
        
        e.event         = event.event;
        e.status        = event.status;
        e.program       = event.program;
        e.programStage  = event.programStage;
        e.orgUnit       = event.orgUnit;
        e.eventDate     = event.eventDate;
        
        var dvs = [];
        angular.forEach(programStageDataElements, function(prStDe){
            if(event.hasOwnProperty(prStDe.dataElement.id)){
                dvs.push({dataElement: prStDe.dataElement.id, value: event[prStDe.dataElement.id]});
            }
        });
        
        e.dataValues = dvs;
        
        return e;        
    }
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
    
});