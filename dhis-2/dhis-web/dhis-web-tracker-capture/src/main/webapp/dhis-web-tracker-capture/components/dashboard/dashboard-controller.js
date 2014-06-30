//Controller for dashboard
trackerCapture.controller('DashboardController',
        function($rootScope,
                $scope,
                $location,
                $modal,
                $timeout,
                storage,
                TEIService, 
                TEService,
                ProgramFactory,
                CurrentSelection,
                TranslationService) {

    //do translation of the dashboard page
    TranslationService.translate();    
    
    //dashboard items   
    $rootScope.dashboardWidgets = {bigger: [], smaller: []};       
    $rootScope.enrollmentWidget = {title: 'enrollment', view: "components/enrollment/enrollment.html", show: true, expand: true};
    $rootScope.dataentryWidget = {title: 'dataentry', view: "components/dataentry/dataentry.html", show: true, expand: true};
    $rootScope.selectedWidget = {title: 'current_selections', view: "components/selected/selected.html", show: false, expand: true};
    $rootScope.profileWidget = {title: 'profile', view: "components/profile/profile.html", show: true, expand: true};
    $rootScope.relationshipWidget = {title: 'relationship', view: "components/relationship/relationship.html", show: true, expand: true};
    $rootScope.notesWidget = {title: 'notes', view: "components/notes/notes.html", show: true, expand: true};    
   
    $rootScope.dashboardWidgets.bigger.push($rootScope.enrollmentWidget);
    $rootScope.dashboardWidgets.bigger.push($rootScope.dataentryWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.selectedWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.profileWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.relationshipWidget);
    $rootScope.dashboardWidgets.smaller.push($rootScope.notesWidget);
    
    //selections
    $scope.selectedTeiId = null;
    $scope.selectedProgramId = null;
    
    $scope.selectedTeiId = ($location.search()).tei; 
    $scope.selectedProgramId = ($location.search()).programId; 
    $scope.selectedOrgUnit = storage.get('SELECTED_OU');
    $scope.selectedProgram;
    $scope.programs = []; 
    $scope.selectedTei;
        
    if( $scope.selectedTeiId ){
        
        //Fetch the selected entity
        TEIService.get($scope.selectedTeiId).then(function(data){
            $scope.selectedTei = data;
            
            //get the entity type
            TEService.get($scope.selectedTei.trackedEntity).then(function(te){
                $scope.trackedEntity = te;
                
                ProgramFactory.getAll().then(function(programs){  
            
                    //get programs valid for the selected ou and tei
                    angular.forEach(programs, function(program){
                        if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id) &&
                           program.trackedEntity.id === $scope.selectedTei.trackedEntity){
                            $scope.programs.push(program);
                        }

                        if($scope.selectedProgramId && program.id === $scope.selectedProgramId){
                            $scope.selectedProgram = program;
                        }
                    });
                    
                    //broadcast selected items for dashboard controllers
                    CurrentSelection.set({tei: $scope.selectedTei, te: $scope.trackedEntity, pr: $scope.selectedProgram, enrollment: null});
                    $scope.broadCastSelections();                                    
                });
            });            
        });       
    }
    
    $scope.broadCastSelections = function(){
        
        var selections = CurrentSelection.get();
        $scope.selectedTei = selections.tei;
        $scope.trackedEntity = selections.te;
        $scope.selectedEnrollment = selections.enrollment;
        CurrentSelection.set({tei: $scope.selectedTei, te: $scope.trackedEntity, pr: $scope.selectedProgram, enrollment: null});
        $timeout(function() { 
            $rootScope.$broadcast('selectedItems', {programExists: $scope.programs.length > 0});            
        }, 100); 
    };
     
    
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
    
    $scope.expandCollapse = function(widget){
        widget.expand = !widget.expand;
    };
    
    $scope.showHideWidgets = function(){
        var modalInstance = $modal.open({
            templateUrl: "views/widgets.html",
            controller: "DashboardWidgetsController"
        });

        modalInstance.result.then(function () {
        });
    };
});
