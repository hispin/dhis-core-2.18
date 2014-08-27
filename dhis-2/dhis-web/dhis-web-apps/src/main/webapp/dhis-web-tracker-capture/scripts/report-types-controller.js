//Controller for the header section
trackerCapture.controller('ReportTypesController',
        function($scope,
                $location,
                TranslationService) {

    TranslationService.translate();
    
    $scope.showSummaryReport = function(){   
        $location.path('/summary-report').search();
    };
    
    $scope.showOverDueEvents = function(){   
        $location.path('/report').search();
    };   
    
    $scope.showUpcomingEvents = function(){
        $location.path('/report').search();
    };
});