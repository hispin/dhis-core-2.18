'use strict';

/* Directives */

var eventCaptureDirectives = angular.module('eventCaptureDirectives', [])

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

.directive('selectedOrgUnit', function() {        

    return {        
        restrict: 'A',        
        link: function(scope, element, attrs){  
          
            //when tree has loaded, get selected orgunit - if there is any - and inform angular           
            $(function() {                 
                
                var adapters = [];
                var partial_adapters = [];

                if( dhis2.ou.memoryOnly ) {
                    adapters = [ dhis2.storage.InMemoryAdapter ];
                    partial_adapters = [ dhis2.storage.InMemoryAdapter ];
                } else {
                    adapters = [ dhis2.storage.IndexedDBAdapter, dhis2.storage.DomLocalStorageAdapter, dhis2.storage.InMemoryAdapter ];
                    partial_adapters = [ dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter ];
                }

                dhis2.ou.store = new dhis2.storage.Store({
                    name: OU_STORE_NAME,
                    objectStores: [
                        {
                            name: OU_KEY,
                            adapters: adapters
                        },
                        {
                            name: OU_PARTIAL_KEY,
                            adapters: partial_adapters
                        }
                    ]
                });

                dhis2.ou.store.open().done( function() {
                    selection.load();
                    $( "#orgUnitTree" ).one( "ouwtLoaded", function() {
                        var selected = selection.getSelected()[0];
                        selection.getOrganisationUnit(selected).done(function(data){                            
                            if( data ){
                                scope.selectedOrgUnit = {id: selected, name: data[selected].n};                                  
                                scope.$apply();
                            }                        
                        });
                    } );
                    
                });
            });
            
            //listen to user selection, and inform angular         
            selection.setListenerFunction( organisationUnitSelected );            
            selection.responseReceived();
            
            function organisationUnitSelected( orgUnits, orgUnitNames ) {
                scope.selectedOrgUnit = {id: orgUnits[0], name: orgUnitNames[0]};    
                scope.$apply();                
            }
        }  
    };
})

.directive('dhisContextMenu', function(ContextMenuSelectedItem) {
        
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
        elem.datepicker({
            onSelect: function() {
                scope.$apply(attrs.blurOrChange);
                $(this).change();                                        
            }
        }).change(function() {
            scope.$apply(attrs.blurOrChange);
        });
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

