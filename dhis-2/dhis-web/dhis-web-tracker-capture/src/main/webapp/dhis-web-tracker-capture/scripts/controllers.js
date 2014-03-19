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
                TrackedEntityService) {   
   
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
        $scope.trackedEntityList = [];
        
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
            TrackedEntityService.getByOrgUnitAndProgram($scope.selectedOrgUnit.id, $scope.selectedProgram.id).then(function(data){
                $scope.trackedEntityList = data;                
            });            
        }
        else{
            
            //Load entities for the selected orgunit
            TrackedEntityService.getByOrgUnit($scope.selectedOrgUnit.id).then(function(data){
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
                storage,
                TrackedEntityService,                
                TranslationService) {

    //do translation of the dashboard page
    TranslationService.translate();    
    
    //dashboard item   
    $scope.selected = {title: 'current_selections', isOpen: true, selections: []};
    
    //selections
    $scope.selectedEntityId = ($location.search()).selectedEntityId;
    $scope.selectedProgramId = ($location.search()).selectedProgramId;
    
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    
    $scope.selectedProgram = null;
    
    if($scope.selectedProgramId){
        $scope.selectedProgram = storage.get($scope.selectedProgramId);        
    }   
        
    if( $scope.selectedEntityId ){
        
        //Fetch the selected entity
        TrackedEntityService.get($scope.selectedEntityId).then(function(data){
            $scope.selectedEntity = data;    
            
            //broadcast for all the dashboard item
            $rootScope.$broadcast('selectedItems', {selectedEntity: $scope.selectedEntity, 
                                                    selectedProgramId: $scope.selectedProgramId, 
                                                    selectedOrgUnitId: $scope.selectedOrgUnit.id});            
        });       
    }
    
    $scope.selected.selections.push({title: 'registering_unit', value: $scope.selectedOrgUnit ? $scope.selectedOrgUnit.name : 'not_selected'});
    $scope.selected.selections.push({title: 'program', value: $scope.selectedProgram ? $scope.selectedProgram.name : 'not_selected'});    
    
    $scope.back = function(){
        $location.path('/');
    };
    
    $scope.displayEnrollment = false;
    $scope.showEnrollment = function(){
        $scope.displayEnrollment = true;
    };
})

//Controller for the profile section
.controller('ProfileController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    $scope.profile = {title: 'profile', isOpen: true};
    
    //attributes for profile    
    $scope.attributes = {};    
    angular.forEach(storage.get('ATTRIBUTES'), function(attribute){
        $scope.attributes[attribute.id] = attribute;
    }); 
    
    //listen for the selected items
    $scope.$on('selectedItems', function(event, args) {        
        $scope.selectedEntity = args.selectedEntity;                 
    });    
})

//Controller for the enrollment section
.controller('EnrollmentController',
        function($scope,                
                storage,
                EnrollmentService,
                TranslationService) {

    TranslationService.translate();
    
    $scope.enrollment = {title: 'enrollment', isOpen: true};
    
    //listen for the selected items
    $scope.$on('selectedItems', function(event, args) {        
        $scope.selectedEntity = args.selectedEntity;
        $scope.selectedProgramId = args.selectedProgramId;
        $scope.selectedOrgUnitId = args.selectedOrgUnitId;
        
        //programs for enrollment
        $scope.programs = [];
        var programs = storage.get('PROGRAMS');            
        if( programs ){
            
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
        } 
        
        EnrollmentService.get($scope.selectedEntity.trackedEntityInstance).then(function(data){
            console.log('enrollment:  ', data);
        });
    });        
})

//Controller for the notes section
.controller('NotesController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    $scope.notes = {title: 'notes', isOpen: true};
    
    $scope.attributes = storage.get('ATTRIBUTES');
    
})

//Controller for the data entry section
.controller('DataEntryController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    $scope.dataEntry = {title: 'dataentry', isOpen: true};  
    
    $scope.attributes = storage.get('ATTRIBUTES');
    
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