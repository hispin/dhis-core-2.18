'use strict';

/* Controllers */
var trackerCaptureControllers = angular.module('trackerCaptureControllers', [])

//Controller for settings page
.controller('SelectionController',
        function($rootScope,
                $scope,
                $modal,
                $location,
                Paginator,
                TranslationService, 
                storage,
                ProgramFactory,
                AttributesFactory,
                EntityQueryFactory,
                TEIService) {   
   
    //Selection
    $scope.selectedOrgUnit = '';
    $scope.selectedProgram = '';
    $scope.ouModes = [
                    {name: 'SELECTED', id: 1}, 
                    {name: 'CHILDREN', id: 2}, 
                    {name: 'DESCENDANTS', id: 3}
                  ];                  
    $scope.ouMode = $scope.ouModes[0];
    
    //Paging
    $scope.rowsPerPage = 50;
    $scope.currentPage = Paginator.getPage() + 1;   
    
    //EntityList
    $scope.showTrackedEntityDiv = false;
    
    //Searching
    $scope.showSearchDiv = false;
    $scope.searchText = null;
    $scope.emptySearchText = false;
    $scope.searchFilterExists = false;   
    
    $scope.searchMode = { 
                            listAll: 'LIST_ALL', 
                            freeText: 'FREE_TEXT', 
                            attributeBased: 'ATTRIBUTE_BASED'
                        };
    
    //Registration
    $scope.showRegistrationDiv = false;
    
    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function() {           
        
        if( angular.isObject($scope.selectedOrgUnit)){   
            
            storage.set('SELECTED_OU', $scope.selectedOrgUnit);
            
            $scope.trackedEntityList = [];
            $scope.selectedProgram = '';
            
            //apply translation - by now user's profile is fetched from server.
            TranslationService.translate();
            
            $scope.loadPrograms($scope.selectedOrgUnit); 
            
            AttributesFactory.getWithoutProgram().then(function(atts){
                setTimeout(function () {
                    $scope.$apply(function () {
                        $scope.attributes = atts;   
                        $scope.gridColumns = $scope.generateGridColumns($scope.attributes);
                        $scope.search($scope.searchMode.listAll);
                    });
                }, 100);
            });           
        }
    });
    
    //load programs associated with the selected org unit.
    $scope.loadPrograms = function(orgUnit) {        
                
        $scope.selectedOrgUnit = orgUnit;
        $scope.selectedProgram = null;
        $scope.selectedProgramStage = null;
        
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
                    AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){
                        setTimeout(function () {
                            $scope.$apply(function () {
                                $scope.attributes = atts;    
                                $scope.gridColumns = $scope.generateGridColumns($scope.attributes);
                            });
                        }, 100);
                    });
                }                
            });
        }        
    };
    
    $scope.getProgramAttributes = function(program, doSearch){ 
        $scope.trackedEntityList = null; 
        $scope.selectedProgram = program;
       
        if($scope.selectedProgram){
            AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){
                setTimeout(function () {
                    $scope.$apply(function () {
                        $scope.attributes = atts; 
                        $scope.gridColumns = $scope.generateGridColumns($scope.attributes);
                    });
                }, 100);
            });           
        }
        else{
            AttributesFactory.getWithoutProgram().then(function(atts){
                setTimeout(function () {
                    $scope.$apply(function () {
                        $scope.attributes = atts;    
                        $scope.gridColumns = $scope.generateGridColumns($scope.attributes);
                    });
                }, 100);
            });
        }        
        
        if(doSearch){
            $scope.search($scope.searchMode);
        }       
    };
    
    $scope.search = function(mode){ 

        $scope.emptySearchText = false;
        $scope.emptySearchAttribute = false;
        $scope.showSearchDiv = false;
        $scope.showRegistrationDiv = false;                          
        $scope.trackedEntityList = null; 
        
        var queryUrl = null, 
            programUrl = null, 
            attributeUrl = {url: null, hasValue: false};
    
        if($scope.selectedProgram){
            programUrl = 'program=' + $scope.selectedProgram.id;
        }        
        
        //check search mode
        if( mode === $scope.searchMode.freeText ){     
            if(!$scope.searchText){                
                $scope.emptySearchText = true;
                return;
            }       
            
            $scope.showTrackedEntityDiv = true;      
            queryUrl = 'query=' + $scope.searchText;                     
        }
        else if( mode === $scope.searchMode.attributeBased ){
            $scope.showTrackedEntityDiv = true;                  
            attributeUrl = EntityQueryFactory.getQueryForAttributes($scope.attributes);
            
            if(!attributeUrl.hasValue && !$scope.selectedProgram){
                $scope.emptySearchAttribute = true;
                $scope.showSearchDiv = true;
                return;
            }
        }
        else if( mode === $scope.searchMode.listAll ){   
            $scope.showTrackedEntityDiv = true;    
        }      

        $scope.gridColumns = $scope.generateGridColumns($scope.attributes);

        //get events for the specified parameters
        TEIService.search($scope.selectedOrgUnit.id, 
                                            $scope.ouMode.name,
                                            queryUrl,
                                            programUrl,
                                            attributeUrl.url).then(function(data){
            $scope.trackedEntityList = data;            
        });
    };
    
    //generate grid columns from teilist attributes
    $scope.generateGridColumns = function(attributes){
        var columns = attributes ? angular.copy(attributes) : [];
       
        //also add extra columns which are not part of attributes (orgunit for example)
        columns.push({id: 'orgUnitName', name: 'Organisation unit', type: 'string', displayInListNoProgram: false});
        columns.push({id: 'created', name: 'Registration date', type: 'string', displayInListNoProgram: false});
        
        //generate grid column for the selected program/attributes
        angular.forEach(columns, function(column){
            if(column.id === 'orgUnitName' && $scope.ouMode.name !== 'SELECTED'){
                column.show = true;    
            }
            
            if(column.displayInListNoProgram){
                column.show = true;
            }           
           
            if(column.type === 'date'){
                 $scope.filterText[column.id]= {start: '', end: ''};
            }
        });        
        return columns;        
    };
    
    $scope.clearEntities = function(){
        $scope.trackedEntityList = null;
    };
    
    $scope.showRegistration = function(){
        $scope.showRegistrationDiv = !$scope.showRegistrationDiv;
        $scope.showTrackedEntityDiv = false;
        $scope.showSearchDiv = false;
    };  
    
    $scope.hideSearch = function(){        
        $scope.showSearchDiv = false;
        $rootScope.showAdvancedSearchDiv = false;
    };
    
    $scope.closeSearch = function(){
        $scope.showSearchDiv = !$scope.showSearchDiv;
    };
    
    $scope.showHideColumns = function(){
        
        $scope.hiddenGridColumns = 0;
        
        angular.forEach($scope.gridColumns, function(gridColumn){
            if(!gridColumn.show){
                $scope.hiddenGridColumns++;
            }
        });
        
        var modalInstance = $modal.open({
            templateUrl: 'views/column-modal.html',
            controller: 'ColumnDisplayController',
            resolve: {
                gridColumns: function () {
                    return $scope.gridColumns;
                },
                hiddenGridColumns: function(){
                    return $scope.hiddenGridColumns;
                }
            }
        });

        modalInstance.result.then(function (gridColumns) {
            $scope.gridColumns = gridColumns;
        }, function () {
        });
    };
    
    $scope.showDashboard = function(currentEntity){   
        $location.path('/dashboard').search({selectedEntityId: currentEntity.id,                                            
                                            selectedProgramId: $scope.selectedProgram ? $scope.selectedProgram.id: null});                                    
    };
       
    $scope.getHelpContent = function(){
        console.log('I will get help content');
    };    
})

//Controller for column show/hide
.controller('ColumnDisplayController', 
    function($scope, 
            $modalInstance, 
            hiddenGridColumns,
            gridColumns){
    
    $scope.gridColumns = gridColumns;
    $scope.hiddenGridColumns = hiddenGridColumns;
    
    $scope.close = function () {
      $modalInstance.close($scope.gridColumns);
    };
    
    $scope.showHideColumns = function(gridColumn){
       
        if(gridColumn.show){                
            $scope.hiddenGridColumns--;            
        }
        else{
            $scope.hiddenGridColumns++;            
        }      
    };    
})

//Controller for registration section


//Controller for dashboard
.controller('DashboardController',
        function($rootScope,
                $scope,
                $location,
                $modal,
                $timeout,
                storage,
                TEIService,  
                ProgramFactory,
                CurrentSelection,
                TranslationService) {

    //do translation of the dashboard page
    TranslationService.translate();    
    
    //dashboard items   
    $rootScope.dashboardWidgets = {bigger: [], smaller: []};       
    $rootScope.enrollmentWidget = {title: 'enrollment', view: "components/enrollment/enrollment.html", show: true};
    $rootScope.dataentryWidget = {title: 'dataentry', view: "components/dataentry/dataentry.html", show: true};
    $rootScope.selectedWidget = {title: 'current_selections', view: "components/selected/selected.html", show: false};
    $rootScope.profileWidget = {title: 'profile', view: "components/profile/profile.html", show: true};
    $rootScope.relationshipWidget = {title: 'relationship', view: "components/relationship/relationship.html", show: true};
    $rootScope.notesWidget = {title: 'notes', view: "components/notes/notes.html", show: true};    
   
    $rootScope.dashboardWidgets.bigger.push($rootScope.enrollmentWidget);
    $rootScope.dashboardWidgets.bigger.push($rootScope.dataentryWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.selectedWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.profileWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.relationshipWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.notesWidget);
    
    //selections
    $scope.selectedEntityId = null;
    $scope.selectedProgramId = null;
    
    $scope.selectedEntityId = ($location.search()).selectedEntityId; 
    $scope.selectedProgramId = ($location.search()).selectedProgramId; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
        
    if( $scope.selectedEntityId ){
      
        //Fetch the selected entity
        TEIService.get($scope.selectedEntityId).then(function(data){     
            
            if($scope.selectedProgramId){
                ProgramFactory.get($scope.selectedProgramId).then(function(program){
                    $scope.selectedProgram = program;   
                    
                    //broadcast selected items for dashboard controllers
                    CurrentSelection.set({tei: data, pr: $scope.selectedProgram});
                    $timeout(function() { 
                        $rootScope.$broadcast('selectedEntity', {});
                    }, 100);
                });
            }
            else{                
                //broadcast selected items for dashboard controllers
                CurrentSelection.set({tei: data, pr: ''});
                $timeout(function() { 
                    $rootScope.$broadcast('selectedEntity', {});
                }, 100);
            }
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