trackerCapture.controller('ProfileController',
        function($scope,                
                storage,
                CurrentSelection,
                TranslationService) {

    TranslationService.translate();
    
    //attributes for profile    
    $scope.attributes = {};    
    $scope.editProfile = false;    
    
    angular.forEach(storage.get('ATTRIBUTES'), function(attribute){
        $scope.attributes[attribute.id] = attribute;
    }); 
    
    //listen for the selected entity
    $scope.$on('selectedEntity', function(event, args) { 
        var selections = CurrentSelection.get();
        $scope.selectedEntity = selections.tei;        
      
        angular.forEach(storage.get('TRACKED_ENTITIES'), function(te){
            if($scope.selectedEntity.trackedEntity === te.id){
                $scope.trackedEntity = te;
            }
        }); 
        
        $scope.entityAttributes = angular.copy($scope.selectedEntity.attributes);
    });
    
    $scope.showEdit = function(){
      $scope.editProfile = !$scope.editProfile; 
    };
    
    $scope.save = function(){
        
        $scope.editProfile = !$scope.editProfile;
    };
    
    $scope.cancel = function(){
        $scope.selectedEntity.attributes = $scope.entityAttributes;  
        $scope.editProfile = !$scope.editProfile;
    };
});
