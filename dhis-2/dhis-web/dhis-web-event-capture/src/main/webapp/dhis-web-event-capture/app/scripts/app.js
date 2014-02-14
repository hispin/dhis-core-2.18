'use strict';

/* App Module */

var eventCapture = angular.module('eventCapture',
		[ 'ui.bootstrap', 
		  'ngRoute', 
		  'ngCookies', 
		  'eventCaptureDirectives', 
		  'eventCaptureControllers', 
		  'eventCaptureServices',
		  'eventCaptureFilters',
		  'angularLocalStorage', 
		  'pascalprecht.translate'])

.config(function($translateProvider) {
    
	$translateProvider.useStaticFilesLoader({
		prefix: 'i18n/',
		suffix: '.json'
	});
	
	$translateProvider.preferredLanguage('en');	
})

.config(['$httpProvider',function ($httpProvider) {
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
 }]) 

.run(function (TrackerApp, storage) {    
    TrackerApp.getConfiguration().then(function(appConfiguration){     
        storage.set('CONFIG', appConfiguration);
    });
});
