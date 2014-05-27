trackerCapture.controller('RelationshipController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();    
    $scope.attributes = storage.get('ATTRIBUTES');    
});