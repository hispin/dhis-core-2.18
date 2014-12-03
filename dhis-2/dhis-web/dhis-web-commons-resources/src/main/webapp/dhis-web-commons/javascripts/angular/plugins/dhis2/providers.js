'use strict';

/* Providers */
var d2Providers = angular.module('d2Providers', [])

.provider('d2I18n', function ($translateProvider) {
    
    var getTranslations = function(locale){
        var translation = {locale: locale};
        var def = $.Deferred();

        $.ajax({
            url: 'i18n/i18n_app_' + locale + '.properties' ,
            type: 'GET'
        }).done(function(response) {
            translation.keys = response;
            localStorage['LOCALE'] = locale;
            def.resolve(translation);
        }).fail(function(){
            $.ajax({
                url: 'i18n/i18n_app_en.properties' ,
                type: 'GET'
            }).done(function(response) {  
                translation= {locale: 'en', keys: response};
                localStorage['LOCALE'] = 'en';
                def.resolve(translation);
            });                
        });

        return def.promise();
    };
    return {
        getLocale: function(){
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
        },
        initialize: function () {
            
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
                this.getLocale().done(function(response){
                    $translateProvider.translations(response.locale, dhis2.util.parseJavaProperties(response.keys));                    
                });
            }
        },
        $get: function () {
        }
    };
});