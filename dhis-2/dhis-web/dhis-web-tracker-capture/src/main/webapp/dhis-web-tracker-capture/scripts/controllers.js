'use strict';

/* Controllers */
var trackerCaptureControllers = angular.module('trackerCaptureControllers', [])

//Controller for settings page
.controller('SelectionController',
        function($scope,
                $modal,
                $location,
                Paginator,
                TranslationService, 
                storage,
                OperatorFactory,
                ProgramFactory,
                AttributesFactory,
                EntityQueryFactory,
                TEIGridService,
                TEIService) {   
   
    //Selection
    $scope.selectedOrgUnit = '';
    $scope.selectedProgram = '';
    $scope.ouModes = [
                    {name: 'SELECTED', id: 1}, 
                    {name: 'CHILDREN', id: 2}, 
                    {name: 'DESCENDANTS', id: 3},
                    {name: 'ACCESSIBLE', id: 4}
                  ];                  
    $scope.ouMode = $scope.ouModes[0];
    
    //Paging
    $scope.pager = {pageSize: 50, page: 1, toolBarDisplay: 5};   
    
    //EntityList
    $scope.showTrackedEntityDiv = false;
    
    //Searching
    $scope.showSearchDiv = false;
    $scope.searchText = null;
    $scope.emptySearchText = false;
    $scope.searchFilterExists = false;   
    $scope.defaultOperators = OperatorFactory.defaultOperators;
    $scope.boolOperators = OperatorFactory.boolOperators;
    $scope.enrollment = {programStartDate: '', programEndDate: '', operator: $scope.defaultOperators[0]};
   
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
                
                $scope.attributes = atts;   
                $scope.attributes = $scope.generateAttributeFilters($scope.attributes);
                $scope.gridColumns = $scope.generateGridColumns($scope.attributes);
                $scope.search($scope.searchMode.listAll);                
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
                                $scope.attributes = $scope.generateAttributeFilters($scope.attributes);
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
                        $scope.attributes = $scope.generateAttributeFilters($scope.attributes);
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
                        $scope.attributes = $scope.generateAttributeFilters($scope.attributes);
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

        $scope.teiFetched = false;
        $scope.selectedSearchMode = mode;
        $scope.emptySearchText = false;
        $scope.emptySearchAttribute = false;
        //$scope.showSearchDiv = false;
        $scope.showRegistrationDiv = false;  
        $scope.showTrackedEntityDiv = false;
        $scope.trackedEntityList = null; 
        $scope.teiCount = null;
        
        $scope.queryUrl = null;
        $scope.programUrl = null;
        $scope.attributeUrl = {url: null, hasValue: false};
    
        if($scope.selectedProgram){
            $scope.programUrl = 'program=' + $scope.selectedProgram.id;
        }        
        
        //check search mode
        if( $scope.selectedSearchMode === $scope.searchMode.freeText ){     

            if(!$scope.searchText){                
                $scope.emptySearchText = true;
                $scope.teiFetched = false;   
                $scope.teiCount = null;
                return;
            }       
 
            $scope.queryUrl = 'query=' + $scope.searchText;                     
        }
        
        if( $scope.selectedSearchMode === $scope.searchMode.attributeBased ){
            
            $scope.attributeUrl = EntityQueryFactory.getQueryForAttributes($scope.attributes, $scope.enrollment);
            
            if(!$scope.attributeUrl.hasValue && !$scope.selectedProgram){
                $scope.emptySearchAttribute = true;
                $scope.teiFetched = false;   
                $scope.teiCount = null;
                return;
            }
        }
        
        $scope.doSearch();
    };
    
    $scope.doSearch = function(){
        
        //get events for the specified parameters
        TEIService.search($scope.selectedOrgUnit.id, 
                                            $scope.ouMode.name,
                                            $scope.queryUrl,
                                            $scope.programUrl,
                                            $scope.attributeUrl.url,
                                            $scope.pager).then(function(data){
            //$scope.trackedEntityList = data;            
            if(data.rows){
                $scope.teiCount = data.rows.length;
            }                    
            
            if( data.metaData.pager ){
                $scope.pager = data.metaData.pager;
                $scope.pager.toolBarDisplay = 5;

                Paginator.setPage($scope.pager.page);
                Paginator.setPageCount($scope.pager.pageCount);
                Paginator.setPageSize($scope.pager.pageSize);
                Paginator.setItemCount($scope.pager.total);                    
            }
            
            //process tei grid
            $scope.trackedEntityList = TEIGridService.format(data);
            $scope.showTrackedEntityDiv = true;
            $scope.teiFetched = true;  
        });
    };
    
    $scope.jumpToPage = function(){
        $scope.search();
    };
    
    $scope.resetPageSize = function(){
        $scope.pager.page = 1;        
        $scope.search();
    };
    
    $scope.getPage = function(page){    
        $scope.pager.page = page;
        $scope.search();
    };
    
    $scope.generateAttributeFilters = function(attributes){

        angular.forEach(attributes, function(attribute){
            if(attribute.valueType === 'number' || attribute.valueType === 'date'){
                attribute.operator = $scope.defaultOperators[0];
            }
        });
                    
        return attributes;
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
    
    $scope.showHideSearch = function(){
        $scope.showSearchDiv = !$scope.showSearchDiv;
    };
    
    $scope.showRegistration = function(){
        $scope.showRegistrationDiv = !$scope.showRegistrationDiv;
        $scope.showTrackedEntityDiv = false;
        $scope.showSearchDiv = false;
    };  
    
    $scope.hideSearch = function(){        
        //$scope.showSearchDiv = false;
        //$rootScope.showAdvancedSearchDiv = false;
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
        $location.path('/dashboard').search({tei: currentEntity.id,                                            
                                            program: $scope.selectedProgram ? $scope.selectedProgram.id: null});                                    
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