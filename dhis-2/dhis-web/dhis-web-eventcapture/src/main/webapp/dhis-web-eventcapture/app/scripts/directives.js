'use strict';

/* Directives */

var eventCaptureDirectives = angular.module('eventCaptureDirectives', [])

.directive('ngDate', function($filter) {
    return {
        restrict: 'A',
        require: 'ngModel',        
        link: function(scope, element, attrs, ctrl) {
            element.datepicker({
                changeYear: true,
                changeMonth: true,
                dateFormat: 'yy-mm-dd',
                onSelect: function(date) {
                    //scope.date = date;
                    ctrl.$setViewValue(date);
                    $(this).change();                    
                    scope.$apply();
                }                
            })
            .change(function() {
                //var rawDate = $filter('date')(this.value, 'yyyy-MM-dd'); 
                var rawDate = this.value;
                var convertedDate = moment(this.value, 'YYYY-MM-DD')._d;
                convertedDate = $filter('date')(convertedDate, 'yyyy-MM-dd');       

                if(rawDate != convertedDate){
                    scope.invalidDate = true;
                    ctrl.$setViewValue(this.value);
                    scope.$apply();                    
                    ctrl.$setValidity('foo', false);
                }
                else{
                    scope.invalidDate = false;
                    ctrl.$setViewValue(this.value);
                    scope.$apply();                    
                    ctrl.$setValidity('foo', true);
                }
            });    
        }      
    };   
})

.directive('paginator', function factory() {
    return {
        restrict: 'E',
        controller: function ($scope, Paginator) {
            $scope.paginator = Paginator;
        },
        templateUrl: 'views/pagination.html'        
    };
});