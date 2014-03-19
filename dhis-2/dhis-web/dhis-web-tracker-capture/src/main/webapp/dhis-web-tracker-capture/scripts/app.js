'use strict';

/* App Module */

var trackerCapture = angular.module('trackerCapture',
		 ['ui.bootstrap', 
		  'ngRoute', 
		  'ngCookies',  
		  'trackerCaptureServices',
		  'trackerCaptureFilters',
                  'trackerCaptureDirectives', 
                  'trackerCaptureControllers',
		  'angularLocalStorage', 
		  'pascalprecht.translate'])
              
.value('DHIS2URL', '../')

.config(function($httpProvider, $routeProvider, $translateProvider) {    
            
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
    
    $routeProvider.when('/', {
        templateUrl:'views/selection.html',
        controller: 'SelectionController'
    }).when('/dashboard',{
        templateUrl:'views/dashboard.html',
        controller: 'DashboardController'
    }).otherwise({
        redirectTo : '/'
    });  
    
    $translateProvider.useStaticFilesLoader({
        prefix: 'i18n/',
        suffix: '.json'
    });
    
    $translateProvider.preferredLanguage('en');	
    
});
