'use strict';

/* App Module */

var eventCapture = angular.module('eventCapture',
		 ['ui.bootstrap', 
		  'ngRoute', 
		  'ngCookies', 
		  'eventCaptureDirectives', 
		  'eventCaptureControllers', 
		  'eventCaptureServices',
		  'eventCaptureFilters',
		  'angularLocalStorage', 
		  'pascalprecht.translate'])
              
.value('DHIS2URL', '../..')

.config(function($routeProvider, $httpProvider, $translateProvider) {    
    
    
    $routeProvider.when('/',{
        templateUrl:'index.html',
        controller: 'MainController'
    }).otherwise({
        redirectTo : '/'
    });       
            
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
    
    $translateProvider.useStaticFilesLoader({
        prefix: 'i18n/',
        suffix: '.json'
    });
    
    $translateProvider.preferredLanguage('en');	
    
});