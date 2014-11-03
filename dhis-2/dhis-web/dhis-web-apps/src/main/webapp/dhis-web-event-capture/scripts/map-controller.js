//controller for dealing with google map
eventCaptureControllers.controller('MapController',
        function($scope, 
                $modalInstance,
                DHIS2URL,
                TranslationService,
                geoJsons,
                location) {

    TranslationService.translate();
    
    $scope.home = function(){        
        window.location = DHIS2URL;
    };
    
    $scope.location = location;
    $scope.geoJsons = geoJsons;
    
    $scope.close = function () {
        $modalInstance.close();
    };
    
    $scope.captureCoordinate = function(){        
        $modalInstance.close($scope.location);
    };
});