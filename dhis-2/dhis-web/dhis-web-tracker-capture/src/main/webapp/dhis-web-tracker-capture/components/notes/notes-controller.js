trackerCapture.controller('NotesController',
        function($scope,                
                storage,
                TranslationService) {

    TranslationService.translate();
    
    $scope.attributes = storage.get('ATTRIBUTES');
    
});