'use strict';

/* Controllers */
var eventCaptureControllers = angular.module('eventCaptureControllers', [])

//Controller for settings page
.controller('MainController',
        function($scope,
                CurrentUserProfile,
                OrgUnitFactory,
                ProgramFactory,
                ProgramStageFactory,
                DHIS2EventFactory,
                Paginator,
                ContextMenuSelectedItem,
                ModalService,
                DialogService,                
                orderByFilter,
                $filter,
                $translate) {   
    
    //Get current locale            
    CurrentUserProfile.get().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });    
    
    $scope.rowsPerPage = 50;
    $scope.currentPage = Paginator.getPage() + 1;
    
    //selections
    $scope.selectedOrgUnit = {name: 'please_select'};
    $scope.selectedProgram = '';
    $scope.selectedProgramStage = '';
    $scope.programStageDataElements = [];
    $scope.dhis2Events = [];
    $scope.eventRegistration = false;
    $scope.editGridColumns = false;
    $scope.eventGridColumns = [];
    $scope.newDhis2Event = {dataValues: []};
    $scope.currentEvent = {dataValues: []};
    $scope.currentEventOrginialValue = '';
    
    $scope.editingEventInFull = false;
    $scope.editingEventInGrid = false;
    
    $scope.sortHeader = '';
    $scope.reverse = false;
    $scope.filterText = {};   
   
    //Get orgunits for the logged in user
    OrgUnitFactory.getMine().then(function(orgUnits) {
        $scope.orgUnits = orgUnits;
        
        //expand root orgunit (the expansion is only if we have one root)
        if($scope.orgUnits.length === 1 ){
            $scope.orgUnits[0].show = true;
        }
    });
    
    //controll expand/collapse of orgunit tree
    $scope.expandCollapse = function(orgUnit) {

        if( !angular.isUndefined( orgUnit.hasChildren ) ){
            
            //Get children for the selected orgUnit
            OrgUnitFactory.get(orgUnit.id).then(function(data) {
                orgUnit.show = !orgUnit.show;  
                delete orgUnit.hasChildren;
                orgUnit.children = data.children;                   
            });           
        }
        else{
            orgUnit.show = !orgUnit.show;   
        }        
    };
    
    //load programs of type=3 for the selected orgunit
    $scope.loadPrograms = function(orgUnit){            
        $scope.selectedOrgUnit = orgUnit;
        $scope.selectedProgram = null;
        $scope.selectedProgramStage = null;
        $scope.dhis2Events = [];
        $scope.newDhis2Event = {dataValues: []};
        if (angular.isObject($scope.selectedOrgUnit)) {   

            $scope.programs = [];
            ProgramFactory.getMine(3).then(function(data) {
                angular.forEach(data.organisationUnits, function(ou) {
                    if (ou.id === $scope.selectedOrgUnit.id) {
                        angular.forEach(ou.programs, function(p) {
                            $scope.programs.push(p);                                                   
                        });                        
                    }
                });
                
                if($scope.programs.length === 1){
                    $scope.pr = $scope.programs[0];
                    $scope.loadEvents($scope.pr);
                }
            });            
        }
    };    
    
    //fetch contents of selected program from server - with full details
    $scope.loadEvents = function(program){

        ProgramFactory.get(program.id).then(function(data){
            $scope.selectedProgram = data;
            
            //a single program stage is expected - as it is single event
            ProgramStageFactory.get($scope.selectedProgram.programStages[0].id).then(function(data){
               $scope.selectedProgramStage = data;
               
               angular.forEach($scope.selectedProgramStage.programStageDataElements, function(prStDe){
                   $scope.programStageDataElements[prStDe.dataElement.id] = prStDe; 
               });
               
               //Load events for the selected program stage and orgunit
               DHIS2EventFactory.getByStage($scope.selectedOrgUnit.id, $scope.selectedProgramStage.id).then(function(data){
                   $scope.dhis2Events = data;

                   //process event list for easier tabular sorting
                   //angular.forEach($scope.dhis2Events, function(dhis2Event){ 
                   for(var i=0; i < $scope.dhis2Events.length; i++){     
                       //check if event is empty
                       if(!angular.isUndefined($scope.dhis2Events[i].dataValues[0].dataElement)){
                           $scope.dhis2Events[i].dataValues = orderByFilter($scope.dhis2Events[i].dataValues, '-dataElement');
                           angular.forEach($scope.dhis2Events[i].dataValues, function(dataValue){
                               
                               //converting int value to integer for proper sorting.
                               var dataElement = $scope.programStageDataElements[dataValue.dataElement].dataElement;
                               if(angular.isObject(dataElement)){                               
                                   if(dataElement.type == 'int'){
                                       dataValue.value = parseInt(dataValue.value);
                                   }
                                   $scope.dhis2Events[i][dataValue.dataElement] = dataValue.value; 
                               }                                
                            });  
                            
                            delete $scope.dhis2Events[i].dataValues;
                       }
                       else{//event is empty, remove from display list
                           var index = $scope.dhis2Events.indexOf($scope.dhis2Events[i]);                           
                           $scope.dhis2Events.splice(index,1);
                           i--;                           
                       }
                   }
                   
                   //generate grid headers using program stage data elements
                   //also, create a template for new event.
                   for(var dataElement in $scope.programStageDataElements){
                       var dataElement = $scope.programStageDataElements[dataElement].dataElement;
                       var name = dataElement.formName || dataElement.name;
                       $scope.newDhis2Event.dataValues.push({id: dataElement.id, value: '', name: name});                       
                       $scope.eventGridColumns.push({name: name, id: dataElement.id, filter: '', hide: false});
                   }                   
               });
            });            
        });
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
    
    $scope.showHideColumns = function(showAllColumns){
        if(showAllColumns){
            angular.forEach($scope.eventGridColumns, function(gridHeader){
                gridHeader.hide = false;
            });
        }
        else{
            $scope.editGridColumns = !$scope.editGridColumns;
        }        
    };
    
    $scope.searchInGrid = function(gridColumn){        
        $scope.filteringGrid = gridColumn;    
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
        
        if($scope.currentEvent.dataValues.length !== $scope.selectedProgramStage.programStageDataElements.length){
            angular.forEach($scope.selectedProgramStage.programStageDataElements, function(prStDe){
                if(!$scope.currentEvent.hasOwnProperty(prStDe.dataElement.id)){
                    $scope.currentEvent[prStDe.dataElement.id] = '';
                }
            });
        }
    };
    
    $scope.addEvent = function(addingAnotherEvent){                
        
        //check for form validity
        $scope.outerForm.submitted = true;        
        if( $scope.outerForm.$invalid ){
            console.log('the form is invalid');
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
            if (data.importSummaries[0].status == 'ERROR') {
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
    
    $scope.showEventDetails = function(){
        console.log('The event is:  ', ContextMenuSelectedItem.getSelectedItem().event);
    };
    
    $scope.getHelpContent = function(){
        console.log('I will get help content');
    };
    
    $scope.generateReport = function(){
        console.log('I need to generate the report....');  
    };     
})

//Controller for the main page
.controller('HeaderController',
        function($scope,
                CurrentUserProfile,                
                storage,
                $translate) {

    //Get current locale            
    CurrentUserProfile.get().then(function(profile) {
        $translate.uses(profile.settings.keyUiLocale);
    });    
    
    $scope.home = function(){
        var dhis2Url = storage.get('CONFIG').activities.dhis.href;   
        window.location = dhis2Url;
    };
});