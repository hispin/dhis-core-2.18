'use strict';

/* Controllers */
var trackerCaptureControllers = angular.module('trackerCaptureControllers', [])

//Controller for settings page
.controller('SelectionController',
        function($scope,
                $location,
                Paginator,
                TranslationService, 
                SelectedEntity,
                storage,
                AttributesFactory,
                TrackedEntityInstanceService) {   
   
    //Selection
    $scope.selectedOrgUnit = '';
    $scope.selectedProgram = '';
    
    //Filtering
    $scope.reverse = false;
    $scope.filterText = {}; 
    $scope.currentFilter;
    
    //Paging
    $scope.rowsPerPage = 50;
    $scope.currentPage = Paginator.getPage() + 1;   
    
    //Searching
    $scope.showSearchDiv = false;
    $scope.searchField = {title: 'search', isOpen: false};     
    
    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function() {
        
        if( angular.isObject($scope.selectedOrgUnit)){                  
            
            $scope.trackedEntityList = [];
            
            //apply translation - by now user's profile is fetched from server.
            TranslationService.translate();
            $scope.attributes = storage.get('ATTRIBUTES');
            
            var programs = storage.get('TRACKER_PROGRAMS');            
            if( programs ){                
                $scope.loadPrograms($scope.selectedOrgUnit);     
            }
        }
    });
    
    //load programs associated with the selected org unit.
    $scope.loadPrograms = function(orgUnit) {      
                                
        $scope.selectedOrgUnit = orgUnit;
        $scope.selectedProgram = null;
        $scope.trackedEntityList = [];
        
        if (angular.isObject($scope.selectedOrgUnit)) {   

            $scope.programs = [];
            
            var programs = storage.get('TRACKER_PROGRAMS');
            
            if( programs && programs != 'undefined'){
                for(var i=0; i<programs.length; i++){
                    var program = storage.get(programs[i].id);   
                    if(program.organisationUnits.hasOwnProperty(orgUnit.id)){
                        $scope.programs.push(program);
                    }
                }               
            }
        }        
    };   
    
    //get events for the selected program (and org unit)
    $scope.loadTrackedEntities = function(){
        
        $scope.searchField.isOpen = false;
        
        $scope.trackedEntityList = [];
        
        $scope.gridColumns = AttributesFactory.getForListing();

        //generate grid column for the selected program
        angular.forEach($scope.gridColumns, function(gridColumn){
            gridColumn.showFilter =  false;
            gridColumn.hide = false;                   
            if(gridColumn.type === 'date'){
                 $scope.filterText[gridColumn.id]= {start: '', end: ''};
            }
        });
            
        if( angular.isObject($scope.selectedProgram)){  
            
            //Load entities for the selected program and orgunit
            TrackedEntityInstanceService.getByOrgUnitAndProgram($scope.selectedOrgUnit.id, $scope.selectedProgram.id).then(function(data){
                $scope.trackedEntityList = data;                
            });            
        }
        else{
            
            //Load entities for the selected orgunit
            TrackedEntityInstanceService.getByOrgUnit($scope.selectedOrgUnit.id).then(function(data){
                $scope.trackedEntityList = data;                
            });
        }
    };
    
    $scope.clearEntities = function(){
        $scope.trackedEntityList = [];
    };
    
    $scope.showRegistration = function(){
        
    };  
    
    $scope.showSearch = function(){
        $scope.showSearchDiv = !$scope.showSearchDiv;
        $scope.searchField.isOpen = true;
    };
    
    $scope.closeSearch = function(){
        $scope.showSearchDiv = !$scope.showSearchDiv;
    };   
    
    $scope.sortGrid = function(gridHeader){
        
        if ($scope.sortHeader === gridHeader.id){
            $scope.reverse = !$scope.reverse;
            return;
        }
        
        $scope.sortHeader = gridHeader.id;
        $scope.reverse = false;    
    };    
    
    $scope.filterInGrid = function(gridColumn){
        
        $scope.currentFilter = gridColumn;
        for(var i=0; i<$scope.gridColumns.length; i++){
            
            //toggle the selected grid column's filter
            if($scope.gridColumns[i].id === gridColumn.id){
                $scope.gridColumns[i].showFilter = !$scope.gridColumns[i].showFilter;
            }            
            else{
                $scope.gridColumns[i].showFilter = false;
            }
        }
    };    
    
    $scope.showDashboard = function(currentEntity){       
        SelectedEntity.setSelectedEntity(currentEntity);
        storage.set('SELECTED_OU', $scope.selectedOrgUnit);        
        $location.path('/dashboard').search({selectedEntityId: currentEntity.id, selectedProgramId: $scope.selectedProgram ? $scope.selectedProgram.id : null});                                    
    };   
    
    $scope.search = function(){       
        console.log('the search is:  ', $scope.attributes);
    };
       
    $scope.getHelpContent = function(){
        console.log('I will get help content');
    };    
})

//Controller for dashboard
.controller('DashboardController',
        function($rootScope,
                $scope,
                $location,
                $modal,
                storage,
                TrackedEntityInstanceService,                
                TranslationService) {

    //do translation of the dashboard page
    TranslationService.translate();    
    
    //dashboard items   
    $rootScope.dashboardWidgets = {bigger: [], smaller: []};   
    $rootScope.profileWidget = {title: 'profile', view: "views/profile.html", show: true};
    $rootScope.dataentryWidget = {title: 'dataentry', view: "views/dataentry.html", show: true};
    //$rootScope.selectedWidget = {title: 'current_selections', view: "views/selected.html", show: true};
    $rootScope.enrollmentWidget = {title: 'enrollment', view: "views/enrollment.html", show: true};
    $rootScope.notesWidget = {title: 'notes', view: "views/notes.html", show: true};    
   
    $rootScope.dashboardWidgets.bigger.push($rootScope.profileWidget);
    $rootScope.dashboardWidgets.bigger.push($rootScope.dataentryWidget);
    //$rootScope.dashboardWidgets.smaller.push($rootScope.selectedWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.enrollmentWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.notesWidget);
    
    //selections
    $scope.selectedEntityId = ($location.search()).selectedEntityId;
    $scope.selectedProgramId = ($location.search()).selectedProgramId;    
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');     
        
    if( $scope.selectedEntityId ){
        
        //Fetch the selected entity
        TrackedEntityInstanceService.get($scope.selectedEntityId).then(function(data){
            $scope.selectedEntity = data;    
            
            //broadcast selections for dashboard controllers
            $rootScope.$broadcast('selectedItems', {selectedEntity: $scope.selectedEntity, 
                                                    selectedProgramId: $scope.selectedProgramId, 
                                                    selectedOrgUnitId: $scope.selectedOrgUnit.id});            
        });       
    }   
    
    $scope.back = function(){
        $location.path('/');
    };
    
    $scope.displayEnrollment = false;
    $scope.showEnrollment = function(){
        $scope.displayEnrollment = true;
    };
    
    $scope.removeWidget = function(widget){        
        widget.show = false;
    };
    
    $scope.showHideWidgets = function(){
        var modalInstance = $modal.open({
            templateUrl: "views/widgets.html",
            controller: "DashboardWidgetsController"
        });

        modalInstance.result.then(function () {
        });
    };   
})

//Controller for the profile section
.controller('ProfileController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    //attributes for profile    
    $scope.attributes = {};    
    angular.forEach(storage.get('ATTRIBUTES'), function(attribute){
        $scope.attributes[attribute.id] = attribute;
    }); 
    
    //listen for the selected items
    $scope.$on('selectedItems', function(event, args) {        
        $scope.selectedEntity = args.selectedEntity;        
        $scope.trackedEntity = storage.get($scope.selectedEntity.trackedEntity);
    });
})

//Controller for the enrollment section
.controller('EnrollmentController',
        function($rootScope,
                $scope,                
                storage,
                EnrollmentService,
                TranslationService) {

    TranslationService.translate();
    
    //programs for enrollment
    $scope.enrollments = [];
    $scope.programs = [];
    var programs = storage.get('TRACKER_PROGRAMS'); 
    
    //listen for the selected items
    $scope.$on('selectedItems', function(event, args) {
        
        $scope.selectedEntity = args.selectedEntity;
        $scope.selectedProgramId = args.selectedProgramId;        
        $scope.selectedOrgUnitId = args.selectedOrgUnitId;        
                   
        for(var i=0; i<programs.length; i++){                
            var program = storage.get(programs[i].id);
            if($scope.selectedProgramId == program.id){                
                $scope.selectedProgram = program;
            }

            if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnitId) && 
                    program.trackedEntity.id == $scope.selectedEntity.trackedEntity){
                $scope.programs.push(program);
            }
        } 
        
        EnrollmentService.get($scope.selectedEntity.trackedEntityInstance).then(function(data){
            $scope.enrollments = data.enrollmentList;
            
            if($scope.enrollments && $scope.enrollments.length == 1){
                
                $scope.selectedProgramId = $scope.enrollments[0].program;
                
                angular.forEach($scope.programs, function(program){
                    if(program.id == $scope.selectedProgramId ){
                        $scope.selectedProgram = program;
                    }
                });                
                
                $scope.loadEvents($scope.selectedProgramId);
            }
        });
    }); 
    
    $scope.loadEvents = function(prId) {

        var isEnrolled = false;
        var selectedEnrollment = ''
        angular.forEach($scope.enrollments, function(enrollment){
            if(enrollment.program == prId ){                
                isEnrolled = true;
                selectedEnrollment = enrollment;
            }
        });
        
        //if( isEnrolled && selectedEnrollment ){
           
            //broadcast current selections for data entry
            $rootScope.$broadcast('dataentry', {selectedEntity: $scope.selectedEntity, 
                                                selectedProgramId: prId,
                                                selectedOrgUnitId: $scope.selectedOrgUnitId,
                                                selectedEnrollment: selectedEnrollment});
        //}     
        
    };
    
    /*$scope.removeWidget = function(){        
        angular.forEach($rootScope.dashboardWidgets.bigger, function(widget){
            if(widget.title == 'enrollment'){
                widget.show = false;
            }
        });
    };*/
})

//Controller for the data entry section
.controller('DataEntryController',
        function($scope, 
                $filter,
                orderByFilter,
                storage,
                DHIS2EventFactory,
                OrgUnitService,
                TranslationService) {

    TranslationService.translate();
    
    $scope.attributes = storage.get('ATTRIBUTES');
    
    //listen for the selected items
    $scope.$on('dataentry', function(event, args) {  
        
        $scope.currentEvent = null;
        
        $scope.dhis2Events = '';       
    
        $scope.selectedEntity = args.selectedEntity;
        $scope.selectedProgramId = args.selectedProgramId;        
        $scope.selectedOrgUnitId = args.selectedOrgUnitId;  
        $scope.selectedEnrollment = args.selectedEnrollment;
        
        if($scope.selectedOrgUnitId && $scope.selectedProgramId && $scope.selectedEntity ){
            
            DHIS2EventFactory.getByEntity($scope.selectedEntity.trackedEntityInstance, $scope.selectedOrgUnitId, $scope.selectedProgramId).then(function(data){
                $scope.dhis2Events = data;
                
                if(angular.isUndefined($scope.dhis2Events)){
                    
                    $scope.dhis2Events = [];
                    
                    console.log('need to create new ones:  ', $scope.selectedEnrollment);
                    
                    if($scope.selectedEnrollment.status == 'ACTIVE'){
                        //create events for the selected enrollment
                        var program = storage.get($scope.selectedProgramId);
                        var programStages = [];
                        
                        angular.forEach(program.programStages, function(ps){  
                            
                            programStages.push(storage.get(ps.id));
                            var dhis2Event = {programStage: ps.id, 
                                              orgUnit: $scope.selectedOrgUnitId, 
                                              eventDate: moment(),
                                              name: ps.name,
                                              status: 'ACTIVE'};
                            
                            var date = moment();                        
                            
                            if( moment().add('days', ps.minDaysFromStart).isBefore(date)){
                                dhis2Event.statusColor = 'stage-overdue';
                            }
                            else{
                                dhis2Event.statusColor = 'stage-on-time';
                            }                      
                            
                            $scope.dhis2Events.push(dhis2Event);
                        });
                        
                        console.log('the stages are:  ', $scope.dhis2Events);
                    }
                }
                
                angular.forEach($scope.dhis2Events, function(dhis2Event){
                        
                    dhis2Event.name = storage.get(dhis2Event.programStage).name;

                    dhis2Event.eventDate = moment(dhis2Event.eventDate, 'YYYY-MM-DD')._d;
                    dhis2Event.eventDate = Date.parse(dhis2Event.eventDate);
                    dhis2Event.eventDate = $filter('date')(dhis2Event.eventDate, 'yyyy-MM-dd');



                    OrgUnitService.open().then(function(){
                        OrgUnitService.get(dhis2Event.orgUnit).then(function(ou){
                            if(ou){
                                dhis2Event.orgUnitName = ou.n;
                            }                                                       
                        });                            
                    }); 

                    if(dhis2Event.status == 'COMPLETED'){
                        dhis2Event.statusColor = 'stage-completed';
                    }
                    else{
                        var date = moment(dhis2Event.eventDate, 'yyyy-MM-dd');
                        if(moment().isAfter(date)){
                            dhis2Event.statusColor = 'stage-overdue';
                        }
                        else{
                            dhis2Event.statusColor = 'stage-on-time';
                        }
                    }                        
                });

                $scope.dhis2Events = orderByFilter($scope.dhis2Events, '-eventDate');
                $scope.dhis2Events.reverse();              
            });          
        }
    });
    
    $scope.createNewEvent = function(){        
        console.log('need to create new event');        
    };
    
    $scope.showDataEntry = function(event){
        
        if(event){
            
            $scope.currentEvent = event;   
            $scope.currentStage = storage.get($scope.currentEvent.programStage); 
            
            angular.forEach($scope.currentStage.programStageDataElements, function(prStDe){                
                $scope.currentStage.programStageDataElements[prStDe.dataElement.id] = prStDe.dataElement;
            });
            
            angular.forEach($scope.currentEvent.dataValues, function(dataValue){
                var val = dataValue.value;
                var de = $scope.currentStage.programStageDataElements[dataValue.dataElement];
                if( de && de.type == 'int' && val){
                    val = parseInt(val);
                }
        
                $scope.currentEvent[dataValue.dataElement] = val;
            });                   
        }     
    };    
})

//Controller for the dashboard widgets
.controller('DashboardWidgetsController', 
    function($scope, 
            $modalInstance,
            TranslationService){
    
    TranslationService.translate();
    
    $scope.close = function () {
        $modalInstance.close($scope.eventGridColumns);
    };       
})

//Controller for the notes section
.controller('NotesController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    $scope.attributes = storage.get('ATTRIBUTES');
    
})

//Controller for the selected section
.controller('SelectedInfoController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    //listen for the selected items
    $scope.$on('selectedItems', function(event, args) {
        
        $scope.selectedEntity = args.selectedEntity;
        $scope.selectedProgramId = args.selectedProgramId;        
        $scope.selectedOrgUnitId = args.selectedOrgUnitId;        
        
        if($scope.selectedProgramId){
            $scope.selectedProgram = storage.get($scope.selectedProgramId);        
        }
        
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        
        $scope.selected.selections.push({title: 'registering_unit', value: $scope.selectedOrgUnit ? $scope.selectedOrgUnit.name : 'not_selected'});
        $scope.selected.selections.push({title: 'program', value: $scope.selectedProgram ? $scope.selectedProgram.name : 'not_selected'});               
        
    });     
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