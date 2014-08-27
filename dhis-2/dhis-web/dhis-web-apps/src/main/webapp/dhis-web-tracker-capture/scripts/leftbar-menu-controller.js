//Controller for column show/hide
trackerCapture.controller('LeftBarMenuController',
        function($scope,
                $location,
                TranslationService) {

    TranslationService.translate();
    
    $scope.showHome = function(){
        $location.path('/').search();
    }; 
    
    $scope.showReportTypes = function(){
        $location.path('/reports').search();
    };
});