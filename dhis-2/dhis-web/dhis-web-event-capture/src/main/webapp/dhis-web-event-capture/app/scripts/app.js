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

.config(function($routeProvider, $httpProvider, $translateProvider) {    
    
    /*$routeProvider.when('/', {
        templateUrl : 'index.html',
        resolve: {
            dhis2Url: function(TrackerApp) {
                return TrackerApp.getConfiguration().then(function(appConfiguration){
                    //ConfigurationService.set(appConfiguration);
                    return appConfiguration.activities.dhis.href;      
                });
            }
        }
            
    }).otherwise({
        redirectTo : '/'
    });*/
        
    $httpProvider.defaults.useXDomain = true;
    delete $httpProvider.defaults.headers.common['X-Requested-With'];
    
    $translateProvider.useStaticFilesLoader({
        prefix: 'i18n/',
        suffix: '.json'
    });
    $translateProvider.preferredLanguage('en');	
})

.run(function (TrackerApp, storage) {    
    
    TrackerApp.getConfiguration().then(function(appConfiguration){     
        storage.set('CONFIG', appConfiguration);
    });
});
