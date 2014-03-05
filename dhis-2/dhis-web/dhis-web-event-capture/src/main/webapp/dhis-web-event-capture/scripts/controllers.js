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
    $scope.rowsPerPage = 50;
    $scope.currentPage = Paginator.getPage() + 1;
    
    //Filtering
    $scope.reverse = false;
    $scope.filterText = {}; 
    
    //Editing
    $scope.eventRegistration = false;
    $scope.editGridColumns = false;
    $scope.editingEventInFull = false;
    $scope.editingEventInGrid = false;   
    
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
            
            TranslationService.translate();
            
            var programs = storage.get('PROGRAMS');            
            if( programs ){                
                $scope.loadPrograms($scope.selectedOrgUnit);     
            }            
        }
    });
    
    //load programs of type=3 for the selected orgunit
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
                    }                    
                }
            }
            
            $scope.loadEvents($scope.selectedProgram);
        }        
    };    
    
    //get events for the selected program (and org unit)
    $scope.loadEvents = function(program){       
               
        if( program ){
            
            //because this is single event, take the first program stage
            $scope.selectedProgramStage = storage.get(program.programStages[0].id);

            $scope.programStageDataElements = [];               

            angular.forEach($scope.selectedProgramStage.programStageDataElements, function(prStDe){
                $scope.programStageDataElements[prStDe.dataElement.id] = prStDe; 
            });

            //Load events for the selected program stage and orgunit
            DHIS2EventFactory.getByStage($scope.selectedOrgUnit.id, $scope.selectedProgramStage.id).then(function(data){
                $scope.dhis2Events = data;

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
                                    if(dataElement.type === 'int'){
                                        dataValue.value = parseInt(dataValue.value);
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

                    //generate grid headers using program stage data elements
                    //create a template for new event
                    //for date type dataelements, filtering is based on start and end dates
                    $scope.eventGridColumns = [];        
                    for(var dataElement in $scope.programStageDataElements){
                        var dataElement = $scope.programStageDataElements[dataElement].dataElement;
                        var name = dataElement.formName || dataElement.name;
                        $scope.newDhis2Event.dataValues.push({id: dataElement.id, value: '', name: name});                       
                        $scope.eventGridColumns.push({name: name, id: dataElement.id, type: dataElement.type, showFilter: false, hide: false});

                        if(dataElement.type === 'date'){
                             $scope.filterText[dataElement.id]= {start: '', end: ''};
                        }                       
                    }                
                }                               
            });            
        }    
        
    };
    
    $scope.jumpToPage = function(page){
        $scope.currentPage = page;
        Paginator.setPage($scope.currentPage - 1);
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
        var dhis2Event = {program: $scope.selectedProgram.id,
            programStage: $scope.selectedProgramStage.id,
            orgUnit: $scope.selectedOrgUnit.id,
            status: 'ACTIVE',            
            eventDate: $filter('date')(new Date(), 'yyyy-MM-dd'),
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
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid ){
            console.log('the form is invalid');
            return false;
        }
       
        var newValue = currentEvent[dataElement];
        var oldValue = $scope.currentEventOrginialValue[dataElement];
        
        if( newValue !== oldValue ){                     
            
            var dhis2Event = {event: currentEvent.event, dataValues: [{value: newValue, dataElement: dataElement}]};
            
            DHIS2EventFactory.updateSingleValue(dhis2Event).then(function(data){
                
                var continueLoop = true;
                for(var i=0; i< $scope.dhis2Events.length && continueLoop; i++){
                    if($scope.dhis2Events[i].event === currentEvent.event ){
                        $scope.dhis2Events[i] = currentEvent;
                        continueLoop = false;
                    }
                }
                
                //update original value
                $scope.currentEventOrginialValue = angular.copy(currentEvent);                
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