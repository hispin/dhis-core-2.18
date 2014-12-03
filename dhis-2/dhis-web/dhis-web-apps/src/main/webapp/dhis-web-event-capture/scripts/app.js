'use strict';

/* App Module */

var eventCapture = angular.module('eventCapture',
		 ['ui.bootstrap', 
		  'ngRoute', 
		  'ngCookies', 
		  'eventCaptureDirectives', 
		  'eventCaptureControllers', 
		  'eventCaptureServices',
		  'd2Filters',
          'd2Directives',
          'd2Services',
          'd2Controllers',
		  'angularLocalStorage', 
		  'pascalprecht.translate',
          'd2Providers',
          'd2Menu'])
              
.value('DHIS2URL', '..')

.config(function($httpProvider, $routeProvider, d2I18nProvider) {    
            
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
    
    $routeProvider.when('/', {
        templateUrl: 'views/home.html',
        controller: 'MainController'/*,
        resolve: {
            geoJsons: function(GeoJsonFactory){
                return GeoJsonFactory.getAll();
            }
        }*/
    }).otherwise({
        redirectTo : '/'
    });
    
    
    d2I18nProvider.initialize();
    
});