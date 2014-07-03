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
        var selections = CurrentSelection.get();
        $scope.selectedTei = angular.copy(selections.tei);
        $scope.trackedEntity = selections.te;
        $scope.selectedProgram = selections.pr;   
        $scope.selectedEnrollment = selections.enrollment;     

    });
    
    $scope.showAddRelationship = function() {
        
        var modalInstance = $modal.open({
            templateUrl: 'components/relationship/add-relationship.html',
            controller: 'AddRelationshipController',
            resolve: {
                relationshipTypes: function () {
                    return $scope.relationshipTypes;
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
            $modalInstance, 
            relationshipTypes,
            selectedTei){
    
    $scope.relationshipTypes = relationshipTypes;
    $scope.selectedTei = selectedTei;
    $scope.relationshipSources = ['search_from_existing','register_new'];
    
    $scope.close = function () {
      $modalInstance.close('');
    };
    
    $scope.add = function(){       
        console.log('I will add new relationship');     
    };    
});