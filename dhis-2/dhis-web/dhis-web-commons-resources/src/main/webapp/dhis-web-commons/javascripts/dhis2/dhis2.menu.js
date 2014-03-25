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

/**
 * Created by Mark Polak on 28/01/14.
 *
 * @see Underscore.js (http://underscorejs.org)
 */
(function (dhis2, _, undefined) {
    var MAX_FAVORITES = 9,
        /**
         * Object that represents the list of menu items
         * and managers the order of the items to be saved.
         */
        menuItemsList = (function () {
            var menuOrder = [],
                menuItems = {};

            return {
                getItem: function (key) {
                    return menuItems[key];
                },
                setItem: function (key, item) {
                    menuOrder.push(key);
                    menuItems[key] = item;
                },
                list: function () {
                    var result = [];

                    menuOrder.forEach(function (element, index, array) {
                        result.push(menuItems[element]);
                    });

                    return result;
                },
                setOrder: function (order) {
                    menuOrder = order;
                },
                getOrder: function () {
                    return menuOrder;
                }
            }
        })();

    dhis2.util.namespace( 'dhis2.menu' );

    dhis2.menu = function () {
        var that = {},
            menuReady = false,
            menuItems = menuItemsList,
            callBacks = [], //Array of callbacks to call when serviced is updated
            onceCallBacks = [];

        /***********************************************************************
         * Private methods
         **********************************************************************/

        function processTranslations(translations) {
            var itemIndex,
                items = dhis2.menu.getApps();

            items.forEach(function (element, index, items) {
                if (element.id && translations[element.id]) {
                    items[index].name = translations.get(element.id);
                }
                if (element.description === '' && translations.get('intro_' + element.id) !== element.id){
                    element.description = translations['intro_' + element.id];
                }
            });

            setReady();
        }

        function setReady() {
            menuReady = true;
            executeCallBacks();
        }

        function isReady() {
            return menuReady;
        }

        /**
         * Execute any callbacks that are set onto the callbacks array
         */
        function executeCallBacks() {
            var onceCallBack, callBackIndex;

            //If not ready or no menu items
            if ( ! isReady() || menuItems === {})
                return false;

            //Execute the single time callbacks
            while (onceCallBacks.length !== 0) {
                onceCallBack = onceCallBacks.pop();
                onceCallBack(menuItems);
            }
            callBacks.forEach(function (callback, index, callBacks) {
               callback.apply(dhis2.menu, [menuItems]);
            });
        }

        //TODO: Function seems complicated and can be improved perhaps
        /**
         * Sort apps (objects with a name property) by name
         *
         * @param apps
         * @param inverse {boolean} Return the elements in an inverted order (DESC sort)
         * @returns {Array}
         */
        function sortAppsByName (apps, inverse) {
            var smaller = [],
                bigger = [],
                center = Math.floor(apps.length / 2),
                comparisonResult,
                result;

            //If nothing left to sort return the app list
            if (apps.length <= 1)
                return apps;

            center = apps[center];
            apps.forEach(function (app, index, apps) {
                comparisonResult = center.name.localeCompare(app.name);
                if (comparisonResult <= -1) {
                    bigger.push(app);
                }
                if (comparisonResult >= 1) {
                    smaller.push(app);
                }
            });

            smaller = sortAppsByName(smaller);
            bigger = sortAppsByName(bigger);

            result = smaller.concat([center]).concat(bigger);

            return inverse ? result.reverse() : result;
        }

        /***********************************************************************
         * Public methods
         **********************************************************************/

        that.displayOrder = 'custom';

        that.getMenuItems = function () {
            return menuItems;
        }

        /**
         * Get the max number of favorites
         */
        that.getMaxFavorites = function () {
            return MAX_FAVORITES;
        }

        /**
         * Order the menuItems by a given list
         *
         * @param orderedIdList
         * @returns {{}}
         */
        that.orderMenuItemsByList = function (orderedIdList) {
            menuItems.setOrder(orderedIdList);

            executeCallBacks();

            return that;
        };

        that.updateFavoritesFromList = function (orderedIdList) {
            var newFavsIds = orderedIdList.slice(0, MAX_FAVORITES),
                oldFavsIds = menuItems.getOrder().slice(0, MAX_FAVORITES),
                currentOrder = menuItems.getOrder(),
                newOrder;

            //Take the new favorites as the new order
            newOrder = newFavsIds;

            //Find the favorites that were pushed out and add  them to the list on the top of the order
            oldFavsIds.forEach(function (id, index, ids) {
                if (-1 === newFavsIds.indexOf(id)) {
                    newOrder.push(id);
                }
            });

            //Loop through the remaining current order to add the remaining apps to the new order
            currentOrder.forEach(function (id, index, ids) {
                //Add id to the order when it's not already in there
                if (-1 === newOrder.indexOf(id)) {
                    newOrder.push(id);
                }
            });

            menuItems.setOrder(newOrder);

            executeCallBacks();

            return that;
        }

        /**
         * Adds the menu items given to the menu
         */
        that.addMenuItems = function (items) {
            var keysToTranslate = [];

            items.forEach(function (item, index, items) {
                item.id = item.name;
                keysToTranslate.push(item.name);
                if(item.description === "") {
                    keysToTranslate.push("intro_" + item.name);
                }
                menuItems.setItem(item.id, item);
            });

            dhis2.translate.get(keysToTranslate, processTranslations);
        };

        /**
         * Subscribe to the service
         *
         * @param callback {function} Function that should be run when service gets updated
         * @param onlyOnce {boolean} Callback should only be run once on the next update
         * @returns boolean Returns false when callback is not a function
         */
        that.subscribe = function (callback, onlyOnce) {
            var once = onlyOnce ? true : false;

            if ( ! _.isFunction(callback)) {
                return false;
            }

            if (menuItems !== undefined) {
                callback(menuItems);
            }

            if (true === once) {
                onceCallBacks.push(callback);
            } else {
                callBacks.push(callback);
            }
            return true;
        };

        /**
         * Get the favorite apps
         *
         * @returns {Array}
         */
        that.getFavorites = function () {
            return menuItems.list().slice(0, MAX_FAVORITES);
        };

        /**
         * Get the current menuItems
         */
        that.getApps = function () {
            return menuItems.list();
        };

        /**
         * Get non favorite apps
         */
        that.getNonFavoriteApps = function () {
            return menuItems.list().slice(MAX_FAVORITES);
        };

        that.sortNonFavAppsByName = function (inverse) {
            return sortAppsByName(that.getNonFavoriteApps(), inverse);
        }

        /**
         * Gets the applist based on the current display order
         *
         * @returns {Array} Array of app objects
         */
        that.getOrderedAppList = function () {
            var favApps = dhis2.menu.getFavorites(),
                nonFavApps = that.getNonFavoriteApps();
            switch (that.displayOrder) {
                case 'name-asc':
                    nonFavApps = that.sortNonFavAppsByName();
                    break;
                case 'name-desc':
                    nonFavApps = that.sortNonFavAppsByName(true);
                    break;
            }
            return favApps.concat(nonFavApps);;
        }

        that.updateOrder = function (reorderedApps) {
            switch (dhis2.menu.displayOrder) {
                case 'name-asc':
                case 'name-desc':
                    that.updateFavoritesFromList(reorderedApps);
                    break;

                default:
                    //Update the menu object with the changed order
                    that.orderMenuItemsByList(reorderedApps);
                    break;
            }
        }

        that.save = function (saveMethod) {
            if ( ! _.isFunction(saveMethod)) {
                return false;
            }

            return saveMethod(that.getMenuItems().getOrder());
        }

        return that;
    }();
})(dhis2, _);

/**
 * Created by Mark Polak on 28/01/14.
 *
 * @description jQuery part of the menu
 *
 * @see jQuery (http://jquery.com)
 */
(function ($, menu, undefined) {
    var markup = '',
        selector = 'appsMenu';

    markup += '<li data-id="${id}" data-app-name="${name}" data-app-action="${defaultAction}">';
    markup += '  <a href="${defaultAction}" class="app-menu-item" title="${name}">';
    markup += '    <img src="${icon}" onError="javascript: this.onerror=null; this.src = \'../icons/program.png\';">';
    markup += '    <span>${name}</span>';
    markup += '    <div class="app-menu-item-description"><span>${name}</span><i class="fa fa-arrows"></i>${description}</div>';
    markup += '  </a>';
    markup += '</li>';

    $.template('appMenuItemTemplate', markup);

    function renderDropDownFavorites() {
        var selector = '#menuDropDown1 .menuDropDownBox',
            favorites = dhis2.menu.getFavorites();

        $(selector).parent().addClass('app-menu-dropdown ui-helper-clearfix');
        $(selector).html('');
        return $.tmpl( "appMenuItemTemplate", favorites).appendTo(selector);
    }

    function renderAppManager(selector) {
        var apps = dhis2.menu.getOrderedAppList();
        $('#' + selector).html('');
        $('#' + selector).append($('<ul></ul><hr class="app-separator">').addClass('ui-helper-clearfix'));
        $('#' + selector).addClass('app-menu');
        $.tmpl( "appMenuItemTemplate", apps).appendTo('#' + selector + ' ul');

        //Add favorites icon to all the menu items in the manager
        $('#' + selector + ' ul li').each(function (index, item) {
            $(item).children('a').append($('<i class="fa fa-bookmark"></i>'));
        });
    }

    /**
     * Saves the given order to the server using jquery ajax
     *
     * @param menuOrder {Array}
     */
    function saveOrder(menuOrder) {
        if (menuOrder.length !== 0) {
            //Persist the order on the server
            $.ajax({
                contentType:"application/json; charset=utf-8",
                data: JSON.stringify(menuOrder),
                dataType: "json",
                type:"POST",
                url: "../api/menu/"
            }).success(function () {
                //TODO: Give user feedback for successful save
            }).error(function () {
                //TODO: Give user feedback for failure to save
                //TODO: Translate this error message
                alert('Unable to save your app order to the server.');
            });
        }
    }

    /**
     * Render the menumanager and the dropdown meny and attach the update handler
     */
    //TODO: Rename this as the name is not very clear to what it does
    function renderMenu() {
        var options = {
                placeholder: 'app-menu-placeholder',
                connectWith: '.app-menu ul',
                update: function (event, ui) {
                    var reorderedApps = $("#" + selector + " ul"). sortable('toArray', {attribute: "data-id"});

                    dhis2.menu.updateOrder(reorderedApps);
                    dhis2.menu.save(saveOrder);

                    //Render the dropdown menu
                    renderDropDownFavorites();
                },
                //Constrict the draggable elements to the parent element
                containment: 'parent'
            };

        renderAppManager(selector);
        renderDropDownFavorites();

        $('.app-menu ul').sortable(options).disableSelection();
    }

    menu.subscribe(renderMenu);

    /**
     * jQuery events that communicate with the web api
     * TODO: Check the urls (they seem to be specific to the dev location atm)
     */
    $(function () {
        $.ajax('../dhis-web-commons/menu/getModules.action').success(function (data) {
            if (typeof data.modules === 'object') {
                menu.addMenuItems(data.modules);
            }
        }).error(function () {
            //TODO: Translate this error message
            alert('Can not load apps from server.');
        });

        /**
         * Event handler for the sort order box
         */
        $('#menuOrderBy').change(function (event) {
            var orderBy = $(event.target).val();

            dhis2.menu.displayOrder = orderBy;

            renderMenu();
        });
    });

})(jQuery, dhis2.menu);
