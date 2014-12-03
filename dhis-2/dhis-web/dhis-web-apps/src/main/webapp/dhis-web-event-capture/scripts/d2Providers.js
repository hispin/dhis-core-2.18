'use strict';

/* Providers */
var d2Providers = angular.module('d2Providers', [])

.provider('d2I18n', function ($translateProvider) {
    
    return {

        initialize: function () {
            
            var getTranslations = function(locale){
                
                var defaultUrl = 'i18n/i18n_app.properties';
                var url = '';
                if(locale === 'en' || !locale){
                    url = defaultUrl;
                }
                else{
                    url = 'i18n/i18n_app_' + locale + '.properties';
                }
                
                var translation = {locale: locale};
                var def = $.Deferred();

                $.ajax({
                    url: url,
                    type: 'GET'
                }).done(function(response) {
                    translation.keys = response;
                    localStorage['LOCALE'] = locale;
                    def.resolve(translation);
                }).fail(function(){
                    $.ajax({
                        url: defaultUrl ,
                        type: 'GET'
                    }).done(function(response) {  
                        translation= {locale: 'en', keys: response};
                        localStorage['LOCALE'] = 'en';
                        def.resolve(translation);
                    });                
                });

                return def.promise();
            };

            var getLocale = function(){
                var locale = '';
                var def = $.Deferred();

                $.ajax({
                    url: '../api/me/profile.json',
                    type: 'GET'
                }).done( function(response) {
                    localStorage['USER_PROFILE'] = JSON.stringify(response);
                    if(response && response.settings && response.settings.keyUiLocale){
                        locale = response.settings.keyUiLocale;
                    }
                    else{
                        locale = 'en';
                    }
                }).always(function(){                
                    getTranslations(locale).then(function(response){
                        def.resolve(response);
                    });
                });

                return def.promise();
            };
    
            var userProfile = localStorage['USER_PROFILE'];
            if(userProfile){
                userProfile = JSON.parse(userProfile);                
            }
            
            if(userProfile && userProfile.settings && userProfile.settings.keyUiLocale){
                getTranslations(userProfile.settings.keyUiLocale).done(function(response){
                    $translateProvider.translations(response.locale, dhis2.util.parseJavaProperties(response.keys));
                });
            }
            else{
                getLocale().done(function(response){
                    $translateProvider.translations(response.locale, dhis2.util.parseJavaProperties(response.keys));
                });
            }
        },
        $get: function () {
        }
    };
});