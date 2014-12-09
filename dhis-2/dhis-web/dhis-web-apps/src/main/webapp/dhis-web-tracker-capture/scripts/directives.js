'use strict';

/* Directives */

var trackerCaptureDirectives = angular.module('trackerCaptureDirectives', [])

.directive('inputValidator', function() {
    
    return {
        require: 'ngModel',
        link: function (scope, element, attrs, ctrl) {  

            ctrl.$parsers.push(function (value) {
                return parseFloat(value || '');
            });
        }
    };   
})

.directive('selectedOrgUnit', function($timeout, storage) {        

    return {        
        restrict: 'A',        
        link: function(scope, element, attrs){
            
            //once ou tree is loaded, start meta-data download
            $(function() {
                dhis2.ou.store.open().done( function() {
                    selection.load();
                    $( "#orgUnitTree" ).one( "ouwtLoaded", function(event, ids, names) {
                        console.log('Finished loading orgunit tree');                        
                        downloadMetaData();
                    });
                });
            });
            
            //listen to user selection, and inform angular         
            selection.setListenerFunction( setSelectedOu, true );
            
            function setSelectedOu( ids, names ) {
                var ou = {id: ids[0], name: names[0]};
                $timeout(function() {
                    scope.selectedOrgUnit = ou;
                    scope.$apply();
                });
            }
        }  
    };
})

.directive('d2CustomForm', function($compile) {
    return{ 
        restrict: 'E',
        link: function(scope, elm, attrs){            
            scope.$watch('customForm', function(){
                elm.html(scope.customForm);
                $compile(elm.contents())(scope);
            });
        }
    };
})

.directive('d2PopOver', function($compile, $templateCache){
    return {        
        restrict: 'EA',
        link: function(scope, element, attrs){
            var content = $templateCache.get("note.html");
            content = $compile(content)(scope);
            var options = {
                    content: content,
                    placement: 'bottom',
                    trigger: 'hover',
                    html: true,
                    title: scope.title               
                };            
            $(element).popover(options);
        },
        scope: {
            content: '=',
            title: '@details',
            template: "@template"
        }
    };
})

.directive('sortable', function() {        

    return {        
        restrict: 'A',        
        link: function(scope, element, attrs){
            element.sortable({
                connectWith: ".connectedSortable",
                placeholder: "ui-state-highlight",
                tolerance: "pointer",
                handle: '.handle'
            });
        }  
    };
})

.directive('d2ContextMenu', function(ContextMenuSelectedItem) {
        
    return {        
        restrict: 'A',
        link: function(scope, element, attrs){
            var contextMenu = $("#contextMenu");                   
            
            element.click(function (e) {
                var selectedItem = $.parseJSON(attrs.selectedItem);
                ContextMenuSelectedItem.setSelectedItem(selectedItem);
                
                var menuHeight = contextMenu.height();
                var menuWidth = contextMenu.width();
                var winHeight = $(window).height();
                var winWidth = $(window).width();

                var pageX = e.pageX;
                var pageY = e.pageY;

                contextMenu.show();

                if( (menuWidth + pageX) > winWidth ) {
                  pageX -= menuWidth;
                }

                if( (menuHeight + pageY) > winHeight ) {
                  pageY -= menuHeight;

                  if( pageY < 0 ) {
                      pageY = e.pageY;
                  }
                }
                
                contextMenu.css({
                    left: pageX,
                    top: pageY
                });

                return false;
            });
            
            contextMenu.on("click", "a", function () {                    
                contextMenu.hide();
            });

            $(document).click(function () {                                        
                contextMenu.hide();
            });
        }     
    };
})

.directive('d2Date', function(DateUtils, CalendarService, storage, $parse) {
    return {
        restrict: 'A',
        require: 'ngModel',        
        link: function(scope, element, attrs, ctrl) {    
            
            var calendarSetting = CalendarService.getSetting();            
            var dateFormat = 'yyyy-mm-dd';
            if(calendarSetting.keyDateFormat === 'dd-MM-yyyy'){
                dateFormat = 'dd-mm-yyyy';
            }            
            
            var minDate = $parse(attrs.minDate)(scope), 
                maxDate = $parse(attrs.maxDate)(scope),
                calendar = $.calendars.instance(calendarSetting.keyCalendar);
            
            element.calendarsPicker({
                changeMonth: true,
                dateFormat: dateFormat,
                yearRange: '-120:+30',
                minDate: minDate,
                maxDate: maxDate,
                calendar: calendar, 
                duration: "fast",
                showAnim: "",
                renderer: $.calendars.picker.themeRollerRenderer,
                onSelect: function(date) {
                    //scope.date = date;                    
                    ctrl.$setViewValue(date);
                    $(this).change();                    
                    scope.$apply();
                }
            })
            .change(function() {
                
                var rawDate = this.value;
                var convertedDate = DateUtils.format(this.value);
                
                console.log('raw date:  ', rawDate);
                console.log('cnv date:  ', convertedDate);

                if(rawDate != convertedDate){
                    console.log('It is invalid...');
                    scope.invalidDate = true;
                    ctrl.$setViewValue(this.value);                                   
                    ctrl.$setValidity('foo', false);                    
                    scope.$apply();     
                }
                else{
                    scope.invalidDate = false;
                    ctrl.$setViewValue(this.value);                                   
                    ctrl.$setValidity('foo', true);                    
                    scope.$apply();     
                }
            });
        }      
    };   
})

.directive('blurOrChange', function() {
    
    return function( scope, elem, attrs) {
        elem.calendarsPicker({
            onSelect: function() {
                scope.$apply(attrs.blurOrChange);
                $(this).change();                                        
            }
        }).change(function() {
            scope.$apply(attrs.blurOrChange);
        });
    };
})

.directive('d2TypeaheadValidation', function() {
    
    return {
        require: 'ngModel',
        restrict: 'A',
        link: function (scope, element, attrs, ctrl) {
            element.bind('blur', function () {                
                if(ctrl.$viewValue && !ctrl.$modelValue){
                    ctrl.$setViewValue();
                    ctrl.$render();
                }                
            });
        }
    };
})

.directive('typeaheadOpenOnFocus', function ($compile) {
  return {
    require: ['typeahead', 'ngModel'],
    link: function (scope, element, attr, ctrls) {        
        element.bind('focus', function () {          
            ctrls[0].getMatchesAsync(ctrls[1].$viewValue);
            scope.$watch(attr.ngModel, function(value) {
                if(value === '' || angular.isUndefined(value)){
                    ctrls[0].getMatchesAsync(ctrls[1].$viewValue);
                }                
            });
      });
    }
  };
})

.directive('serversidePaginator', function factory() {
    return {
        restrict: 'E',
        controller: function ($scope, Paginator) {
            $scope.paginator = Paginator;
        },
        templateUrl: '../dhis-web-commons/paging/serverside-pagination.html'
    };
})

.directive('d2Enter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if(event.which === 13) {
                scope.$apply(function (){
                    scope.$eval(attrs.d2Enter);
                });
                event.preventDefault();
            }
        });
    };
});