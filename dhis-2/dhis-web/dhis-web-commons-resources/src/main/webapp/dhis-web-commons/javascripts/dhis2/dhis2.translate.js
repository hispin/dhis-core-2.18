"use strict";
/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


// Make sure that dhis2 object exists
var dhis2 = dhis2 || {};
dhis2.translate = dhis2.translate || {};

/**
 * Created by Mark Polak on 28/01/14.
 *
 * @see jQuery (http://jquery.com)
 * @see Underscore.js (http://underscorejs.org)
 */
(function ($,  _, translate, undefined) {
    var translationCache = {
        get: function (key) {
            if (this.hasOwnProperty(key))
                return this[key];
            return key;
        }
    };

    /**
     * Adds translations to the translation cache (overrides already existing ones)
     *
     * @param translations {Object}
     */
    function  addToCache(translations) {
        translationCache = _.extend(translationCache, translations);
    }

    /**
     * Asks the server for the translations of the given {translatekeys} and calls {callback}
     * when a successful response is received.
     *
     * @param translateKeys {Array}
     * @param callback {function}
     */
    function getTranslationsFromServer(translateKeys, callback) {
        $.ajax({
            url:"../api/i18n",
            type:"POST",
            data: JSON.stringify(translateKeys),
            contentType:"application/json; charset=utf-8",
            dataType:"json"
        }).success(function (data) {
                addToCache(data);
                if (typeof callback === 'function') {
                    callback(translationCache);
                }
            });
    }

    /**
     * Translates the given keys in the {translate} array and calls callback when request is successful
     * callback currently gets passed an object with all translations that are in the local cache
     *
     * @param translate {Array}
     * @param callback {function}
     */
    translate.get = function (translate, callback) {
        var translateKeys = [],
            key;

        //Only ask for the translations that we do not already have
        translate.forEach(function (text, index, translate) {
            if ( ! (text in translationCache)) {
                translateKeys.push(text);
            }
        });

        if (translateKeys.length > 0) {
            //Ask for translations of the app names
            getTranslationsFromServer(translateKeys, callback);
        } else {
            //Call backback right away when we have everything in cache
            callback(translationCache);
        }

    };

})(jQuery, _, dhis2.translate);
