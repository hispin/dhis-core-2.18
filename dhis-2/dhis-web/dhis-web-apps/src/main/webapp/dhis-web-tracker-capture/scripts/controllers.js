'use strict';

/* Controllers */
var trackerCaptureControllers = angular.module('trackerCaptureControllers', [])

//Controller for settings page
.controller('SelectionController',
        function($scope,
                $modal,
                $location,
                $translate,
                $filter,
                orderByFilter,
                Paginator,
                storage,
                OptionSetService,
                OperatorFactory,
                ProgramFactory,
                AttributesFactory,
                EntityQueryFactory,
                CurrentSelection,
                TEIGridService,
                TEIService,
                DateUtils) {  
                    
    //Selection
    $scope.ouModes = [{name: 'SELECTED'}, {name: 'CHILDREN'}, {name: 'DESCENDANTS'}, {name: 'ACCESSIBLE'}];         
    $scope.selectedOuMode = $scope.ouModes[0];
    $scope.dashboardProgramId = ($location.search()).program; 
    $scope.treeLoaded = false;
    
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
    $scope.searchState = true;   
    $scope.searchMode = { listAll: 'LIST_ALL', freeText: 'FREE_TEXT', attributeBased: 'ATTRIBUTE_BASED' };    
    $scope.optionSets = null;
    
    //Registration
    $scope.showRegistrationDiv = false;
    
    //Reporting
    $scope.showReportDiv = false;
   
    //watch for selection of org unit from tree
    $scope.$watch('selectedOrgUnit', function() {           

        if( angular.isObject($scope.selectedOrgUnit)){   
            
            storage.set('SELECTED_OU', $scope.selectedOrgUnit);
            
            $scope.trackedEntityList = [];
            
            $scope.optionSets = CurrentSelection.getOptionSets();
            
            if(!$scope.optionSets){
                $scope.optionSets = [];
                OptionSetService.getAll().then(function(optionSets){
                    angular.forEach(optionSets, function(optionSet){                        
                        $scope.optionSets[optionSet.id] = optionSet;
                    });
                    
                    CurrentSelection.setOptionSets($scope.optionSets);
                });
            }
            $scope.loadPrograms($scope.selectedOrgUnit);                                
        }
    });
    
    //watch for changes in ou mode - mode could be selected without notifcation to grid column generator
    $scope.$watch('selectedOuMode.name', function() {           

        if( $scope.selectedOuMode.name && angular.isObject($scope.gridColumns)){
            var continueLoop = true;
            for(var i=0; i<$scope.gridColumns.length && continueLoop; i++){
                if($scope.gridColumns[i].id === 'orgUnitName' && $scope.selectedOuMode.name !== 'SELECTED'){
                    $scope.gridColumns[i].show = true;
                    continueLoop = false;
                }
            }           
        }
    });
        
    //watch for program feedback (this is when coming back from dashboard)
    if($scope.dashboardProgramId && $scope.dashboardProgramId !== 'null'){
        $scope.selectedOrgUnit = storage.get('SELECTED_OU');            
        ProgramFactory.get($scope.dashboardProgramId).then(function(program){
            $scope.selectedProgram = program;        
        });
    }
    
    //load programs associated with the selected org unit.
    $scope.loadPrograms = function(orgUnit) {
        
        $scope.selectedOrgUnit = orgUnit;
        
        if (angular.isObject($scope.selectedOrgUnit)) {   

            ProgramFactory.getAll().then(function(programs){
                $scope.programs = [];
                angular.forEach(programs, function(program){                            
                    if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id)){                                
                        $scope.programs.push(program);
                    }
                });

                if($scope.programs.length === 0){
                    $scope.selectedProgram = null;
                }
                else{
                    if($scope.selectedProgram){
                        angular.forEach($scope.programs, function(program){                            
                            if(program.id === $scope.selectedProgram.id){                                
                                $scope.selectedProgram = program;
                            }
                        });
                    }
                    else{                        
                        if($scope.programs.length === 1){
                            $scope.selectedProgram = $scope.programs[0];
                        }                        
                    }
                }                
                $scope.processAttributes();
                
                $scope.search($scope.searchMode.listAll);
            });
        }        
    };
    
    $scope.getProgramAttributes = function(program){ 

        $scope.trackedEntityList = null; 
        $scope.selectedProgram = program;
        
        $scope.processAttributes();
        
        if($scope.showRegistrationDiv || $scope.showReportDiv){
            $scope.doSearch = false;
        }
        
        if($scope.doSearch){
            $scope.search($scope.searchMode);
        }       
    };
    
    $scope.processAttributes = function(){        
        $scope.sortColumn = {};
        $scope.reverse = false;
        if($scope.selectedProgram){
            AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){
                $scope.attributes = atts;
                setTimeout(function () {
                    $scope.$apply(function () {                        
                        $scope.attributes = $scope.generateAttributeFilters($scope.attributes);
                        var grid = TEIGridService.generateGridColumns($scope.attributes, $scope.selectedOuMode.name);
                        $scope.gridColumns = grid.columns;                       
                    });
                }, 100);
            });           
        }
        else{            
            AttributesFactory.getWithoutProgram().then(function(atts){
                $scope.attributes = atts;
                setTimeout(function () {
                    $scope.$apply(function () {
                        $scope.attributes = orderByFilter(atts, '-sortOrderInListNoProgram').reverse();
                        $scope.attributes = $scope.generateAttributeFilters($scope.attributes);
                        var grid = TEIGridService.generateGridColumns($scope.attributes, $scope.selectedOuMode.name);
                        $scope.gridColumns = grid.columns;
                    });
                }, 100);
            });
        }
    };
    
    //sortGrid
    $scope.sortGrid = function(gridHeader){
        if ($scope.sortColumn && $scope.sortColumn.id === gridHeader.id){
            $scope.reverse = !$scope.reverse;
            return;
        }        
        $scope.sortColumn = gridHeader;
        if($scope.sortColumn.valueType === 'date'){
            $scope.reverse = true;
        }
        else{
            $scope.reverse = false;    
        }
    };
    
    $scope.d2Sort = function(tei){        
        if($scope.sortColumn && $scope.sortColumn.valueType === 'date'){            
            var d = tei[$scope.sortColumn.id];         
            return DateUtils.getDate(d);
        }
        return tei[$scope.sortColumn.id];
    };
   
    //$scope.searchParam = {bools: []};
    $scope.search = function(mode){
        $scope.teiFetched = false;
        $scope.selectedSearchMode = mode;
        $scope.emptySearchText = false;
        $scope.emptySearchAttribute = false;
        $scope.showRegistrationDiv = false;  
        $scope.showReportDiv = false;
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
            $scope.attributes = EntityQueryFactory.resetAttributesQuery($scope.attributes, $scope.enrollment);
        }
        
        if( $scope.selectedSearchMode === $scope.searchMode.attributeBased ){
            
            $scope.searchText = '';
            
            $scope.attributeUrl = EntityQueryFactory.getAttributesQuery($scope.attributes, $scope.enrollment);
            
            if(!$scope.attributeUrl.hasValue && !$scope.selectedProgram){
                $scope.emptySearchAttribute = true;
                $scope.teiFetched = false;   
                $scope.teiCount = null;
                return;
            }
        }
        
        if( $scope.selectedSearchMode === $scope.searchMode.listAll ){
            $scope.searchText = '';
            
            $scope.attributes = EntityQueryFactory.resetAttributesQuery($scope.attributes, $scope.enrollment);
        }
        
        $scope.fetchTeis();
    };
    
    $scope.fetchTeis = function(){
        
        //get events for the specified parameters
        TEIService.search($scope.selectedOrgUnit.id, 
                                            $scope.selectedOuMode.name,
                                            $scope.queryUrl,
                                            $scope.programUrl,
                                            $scope.attributeUrl.url,
                                            $scope.pager,
                                            true).then(function(data){
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
            $scope.trackedEntityList = TEIGridService.format(data,false, $scope.optionSets);
            $scope.showTrackedEntityDiv = true;
            $scope.teiFetched = true;  
            $scope.doSearch = true;
            
            if(!$scope.sortColumn.id){                                      
                $scope.sortGrid({id: 'created', name: $translate('registration_date'), valueType: 'date', displayInListNoProgram: false, showFilter: false, show: true});
            }
            
        });
    };
    
    $scope.generateAttributeFilters = function(attributes){

        angular.forEach(attributes, function(attribute){
            if(attribute.type === 'number' || attribute.type === 'date'){
                attribute.operator = $scope.defaultOperators[0];
            }
        });                    
        return attributes;
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
        $scope.searchState = false;
        
        if(!$scope.showRegistrationDiv){
            $scope.searchState = true;
            $scope.doSearch = true;
            $scope.getProgramAttributes($scope.selectedProgram);
        }
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
        var sortedTei = $filter('orderBy')($scope.trackedEntityList.rows, function(tei) {
            return $scope.d2Sort(tei);
        }, $scope.reverse);
        
        var sortedTeiIds = [];
        angular.forEach(sortedTei, function(tei){
            sortedTeiIds.push(tei.id);
        });
        
        CurrentSelection.setSortedTeiIds(sortedTeiIds);        
        $location.path('/dashboard').search({tei: currentEntity.id,                                            
                                            program: $scope.selectedProgram ? $scope.selectedProgram.id: null});                                    
    };    
    
    //paging        
    $scope.jumpToPage = function(){
        if($scope.pager && $scope.pager.page && $scope.pager.pageCount && $scope.pager.page > $scope.pager.pageCount){
            $scope.pager.page = $scope.pager.pageCount;
        }
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
    
    //help
    $scope.getHelpContent = function(){
        console.log('I will get help content');
    };    
});