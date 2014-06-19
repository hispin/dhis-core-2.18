//Controller for dashboard
trackerCapture.controller('DashboardController',
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
    $scope.selectedProgram = null;
    $scope.programs = []; 
    $scope.selectedEntity;
        
    if( $scope.selectedEntityId ){
        
        //Fetch the selected entity
        TEIService.get($scope.selectedEntityId).then(function(data){
            $scope.selectedEntity = data;
            
            ProgramFactory.getAll().then(function(programs){  
            
                angular.forEach(programs, function(program){
                    if(program.organisationUnits.hasOwnProperty($scope.selectedOrgUnit.id) &&
                       program.trackedEntity.id === $scope.selectedEntity.trackedEntity){
                        $scope.programs.push(program);
                    }
                    
                    if($scope.selectedProgramId && program.id === $scope.selectedProgramId){
                        $scope.selectedProgram = program;
                    }
                });
                
                //broadcast selected items for dashboard controllers
                $scope.broadCastProgram();                                    
            });
        });       
    }   
    
    $scope.broadCastProgram = function(){
        CurrentSelection.set({tei: $scope.selectedEntity, pr: $scope.selectedProgram});
        $timeout(function() { 
            $rootScope.$broadcast('selectedEntity', {});
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
    
    $scope.showHideWidgets = function(){
        var modalInstance = $modal.open({
            templateUrl: "views/widgets.html",
            controller: "DashboardWidgetsController"
        });

        modalInstance.result.then(function () {
        });
    };
});
