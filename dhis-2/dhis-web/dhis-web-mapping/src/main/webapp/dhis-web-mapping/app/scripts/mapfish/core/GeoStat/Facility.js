/*
 * Copyright (C) 2007  Camptocamp
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @requires core/GeoStat.js
 */

mapfish.GeoStat.Facility = OpenLayers.Class(mapfish.GeoStat, {

    classification: null,

    initialize: function(map, options) {
        mapfish.GeoStat.prototype.initialize.apply(this, arguments);
    },

    updateOptions: function(newOptions) {
        this.addOptions(newOptions);
    },

    applyClassification: function(options) {
        this.updateOptions(options);
        
        var items = GIS.store.groupsByGroupSet.data.items;
			
        var rules = new Array(items.length);
        for (var i = 0; i < items.length; i++) {
            var rule = new OpenLayers.Rule({                
                symbolizer: {
                    'pointRadius': 8,
                    'externalGraphic': '../../images/orgunitgroup/' + items[i].data.symbol
                },                
                filter: new OpenLayers.Filter.Comparison({
                    type: OpenLayers.Filter.Comparison.EQUAL_TO,
                    property: this.indicator,
                    value: items[i].data.name
                })
            });
            rules[i] = rule;
        }
        
        this.extendStyle(rules);
        mapfish.GeoStat.prototype.applyClassification.apply(this, arguments);
    },

    updateLegend: function() {
        if (!this.legendDiv) {
            return;
        }
        
        var config = this.widget.getLegendConfig(),
			items = GIS.store.groupsByGroupSet.data.items,
			element;
			
        this.legendDiv.update("");
        
        var element = document.createElement("div");
        element.style.height = "14px";
        element.innerHTML = config.where;
        this.legendDiv.appendChild(element);
        
        element = document.createElement("div");
        element.style.clear = "left";
        this.legendDiv.appendChild(element);
        
        element = document.createElement("div");
        element.style.width = "1px";
        element.style.height = "5px";
        this.legendDiv.appendChild(element);

        for (var i = 0; i < items.length; i++) {
            var element = document.createElement("div");
            element.style.backgroundImage = 'url(../../images/orgunitgroup/' + items[i].data.symbol + ')';
            element.style.backgroundRepeat = 'no-repeat';
            element.style.width = "25px";
            element.style.height = "18px";
            element.style.cssFloat = "left";
            element.style.marginLeft = "3px";
            this.legendDiv.appendChild(element);

            element = document.createElement("div");
            element.innerHTML = items[i].data.name;
            this.legendDiv.appendChild(element);

            element = document.createElement("div");
            element.style.clear = "left";
            this.legendDiv.appendChild(element);
        }
    },

    CLASS_NAME: "mapfish.GeoStat.Facility"
});
