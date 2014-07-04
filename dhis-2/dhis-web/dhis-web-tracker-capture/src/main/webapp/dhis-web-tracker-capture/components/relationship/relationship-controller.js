trackerCapture.controller('RelationshipController',
        function($scope,
                $modal,
                CurrentSelection,
                RelationshipFactory,
                TranslationService) {

    TranslationService.translate();        

    $scope.relationshipTypes = []; 
    
    RelationshipFactory.getAll().then(function(rels){
        $scope.relationshipTypes = rels;    
    }); 
    
    
    //listen for the selected entity       
    $scope.$on('dashboard', function(event, args) { 
        $scope.selections = CurrentSelection.get();
        $scope.selectedTei = angular.copy($scope.selections.tei);
        $scope.trackedEntity = $scope.selections.te;
        $scope.selectedProgram = $scope.selections.pr;   
        $scope.selectedEnrollment = $scope.selections.enrollment;     

    });
    
    $scope.showAddRelationship = function() {
        
        var modalInstance = $modal.open({
            templateUrl: 'components/relationship/add-relationship.html',
            controller: 'AddRelationshipController',
            resolve: {
                relationshipTypes: function () {
                    return $scope.relationshipTypes;
                },
                selections: function () {
                    return $scope.selections;
                },
                selectedTei: function(){
                    return $scope.selectedTei;
                }
            }
        });

        modalInstance.result.then(function (relationships) {
            $scope.selectedTei.relationships = relationships;
        });
    };   
    
})

//Controller for adding new relationship
.controller('AddRelationshipController', 
    function($scope, 
            OperatorFactory,
            AttributesFactory,
            EntityQueryFactory,
            TEIService,
            TEIGridService,
            Paginator,
            storage,
            $modalInstance, 
            relationshipTypes,
            selections,
            selectedTei){
    
    $scope.relationshipTypes = relationshipTypes;
    $scope.selectedTei = selectedTei;
    $scope.relationshipSources = ['search_from_existing','register_new'];
    
    //Selection
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.selectedProgram = selections.pr;
    $scope.selectedTei = selections.tei;
    
    
    $scope.ouModes = [{name: 'SELECTED'}, 
                    {name: 'CHILDREN'}, 
                    {name: 'DESCENDANTS'},
                    {name: 'ACCESSIBLE'}
                  ];         
    $scope.selectedOuMode = $scope.ouModes[0];
    
    //Paging
    $scope.pager = {pageSize: 50, page: 1, toolBarDisplay: 5};   
    
    //EntityList
    $scope.showTrackedEntityDiv = false;
    
    //Searching
    $scope.showSearchDiv = false;
    $scope.searchText = {value: null};
    $scope.emptySearchText = false;
    $scope.searchFilterExists = false;   
    $scope.defaultOperators = OperatorFactory.defaultOperators;
    $scope.boolOperators = OperatorFactory.boolOperators;
    
    $scope.trackedEntityList = null; 
    $scope.enrollment = {programStartDate: '', programEndDate: '', operator: $scope.defaultOperators[0]};
   
    $scope.searchMode = { 
                            listAll: 'LIST_ALL', 
                            freeText: 'FREE_TEXT', 
                            attributeBased: 'ATTRIBUTE_BASED'
                        };
    
    if($scope.selectedProgram){
        AttributesFactory.getByProgram($scope.selectedProgram).then(function(atts){
            $scope.attributes = atts; 
            $scope.attributes = $scope.generateAttributeFilters($scope.attributes);
            $scope.gridColumns = $scope.generateGridColumns($scope.attributes);
        });           
    }
    else{
        AttributesFactory.getWithoutProgram().then(function(atts){
            $scope.attributes = atts;  
            $scope.attributes = $scope.generateAttributeFilters($scope.attributes);
            $scope.gridColumns = $scope.generateGridColumns($scope.attributes);
        });
    }
   
    $scope.search = function(mode){ 
        
        $scope.teiForRelationship = null;
        $scope.teiFetched = false;    
        $scope.emptySearchText = false;
        $scope.emptySearchAttribute = false;
        $scope.showSearchDiv = false;
        $scope.showRegistrationDiv = false;  
        $scope.showTrackedEntityDiv = false;
        $scope.trackedEntityList = null; 
        $scope.teiCount = null;

        $scope.queryUrl = null;
        $scope.programUrl = null;
        $scope.attributeUrl = {url: null, hasValue: false};
        
        $scope.selectedSearchMode = mode;
        $scope.attributes = $scope.generateAttributeFilters($scope.attributes);
        $scope.gridColumns = $scope.generateGridColumns($scope.attributes);
   
        if($scope.selectedProgram){
            $scope.programUrl = 'program=' + $scope.selectedProgram.id;
        }        
        
        //check search mode
        if( $scope.selectedSearchMode === $scope.searchMode.freeText ){ 
            
            if(!$scope.searchText.value){                
                $scope.emptySearchText = true;
                $scope.teiFetched = false;   
                $scope.teiCount = null;
                return;
            }       
 
            $scope.queryUrl = 'query=' + $scope.searchText.value;                     
        }
        
        if( $scope.selectedSearchMode === $scope.searchMode.attributeBased ){            
            $scope.searchText.value = null;
            $scope.attributeUrl = EntityQueryFactory.getAttributesQuery($scope.attributes, $scope.enrollment);
            
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
                                            $scope.selectedOuMode.name,
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
        $scope.search($scope.selectedSearchMode);
    };
    
    $scope.resetPageSize = function(){
        $scope.pager.page = 1;        
        $scope.search($scope.selectedSearchMode);
    };
    
    $scope.getPage = function(page){    
        $scope.pager.page = page;
        $scope.search($scope.selectedSearchMode);
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
            if(column.id === 'orgUnitName' && $scope.selectedOuMode.name !== 'SELECTED'){
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
    
    $scope.showHideSearch = function(simpleSearch){
        if(simpleSearch){
            $scope.showSearchDiv = false;
        }
        else{
            $scope.showSearchDiv = !$scope.showSearchDiv;
        }        
    };    
    
    $scope.close = function () {
      $modalInstance.close('');
    };
    
    $scope.closeRegistration = function(){
        console.log('registration close');
        $scope.selectedRelationshipSource = '';
    };
    
    $scope.assignRelationship = function(selectedTei){
        $scope.teiForRelationship = selectedTei;     
        console.log('selected is:  ', $scope.teiForRelationship);
    };
    
    $scope.add = function(){       
        console.log('I will add new relationship');     
    };    
});