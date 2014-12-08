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
          'd2Menu'])
              
.value('DHIS2URL', '..')

.config(function($routeProvider, $translateProvider) {    
    
    $routeProvider.when('/', {
        templateUrl: 'views/home.html',
        controller: 'MainController'
    }).otherwise({
        redirectTo : '/'
    });
    
    $translateProvider.preferredLanguage('en');
    $translateProvider.useLoader('i18nLoader');
    
});