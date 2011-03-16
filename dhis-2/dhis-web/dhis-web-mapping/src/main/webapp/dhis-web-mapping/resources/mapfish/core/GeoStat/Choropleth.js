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

mapfish.GeoStat.Choropleth = OpenLayers.Class(mapfish.GeoStat, {

    colors: [
        new mapfish.ColorRgb(120, 120, 0),
        new mapfish.ColorRgb(255, 0, 0)
    ],

    method: mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS,

    numClasses: 5,

    defaultSymbolizer: {'fillOpacity': 1},

    classification: null,

    colorInterpolation: null,

    initialize: function(map, options) {
        mapfish.GeoStat.prototype.initialize.apply(this, arguments);
    },

    updateOptions: function(newOptions) {
        var oldOptions = OpenLayers.Util.extend({}, this.options);
        this.addOptions(newOptions);
        if (newOptions) {
            this.setClassification();
        }
    },

    createColorInterpolation: function() {
        var initialColors = this.colors;
        var numColors = this.classification.bins.length;
		var mapLegendType = choropleth.form.findField('maplegendtype').getValue();
        
        if (mapLegendType == G.conf.map_legend_type_automatic) {
			this.colorInterpolation = mapfish.ColorRgb.getColorsArrayByRgbInterpolation(initialColors[0], initialColors[1], numColors);
            for (var i = 0; i < choropleth.imageLegend.length && i < this.colorInterpolation.length; i++) {
                choropleth.imageLegend[i].color = this.colorInterpolation[i].toHexString();
            }
		}
		else if (mapLegendType == G.conf.map_legend_type_predefined) {
			this.colorInterpolation = choropleth.colorInterpolation;
            for (var j = 0; j < choropleth.imageLegend.length && j < this.colorInterpolation.length; j++) {
                choropleth.imageLegend[j].color = this.colorInterpolation[j].toHexString();
            }
		}
    },
	
    setClassification: function() {
        var values = [];
        var features = this.layer.features;
        
        for (var i = 0; i < features.length; i++) {
            values.push(features[i].attributes[this.indicator]);
        }

        var distOptions = {
            'labelGenerator': this.options.labelGenerator
        };
        var dist = new mapfish.GeoStat.Distribution(values, distOptions);
        this.classification = dist.classify(
            this.method,
            this.numClasses,
            null
        );
        this.createColorInterpolation();
    },

    applyClassification: function(options) {
        this.updateOptions(options);
        var boundsArray = this.classification.getBoundsArray();
        var rules = [];

        for (var i = 0; i < boundsArray.length-1; i++) {
            if (this.colorInterpolation.length > i) {
                var rule = new OpenLayers.Rule({
                    symbolizer: {fillColor: this.colorInterpolation[i].toHexString()},
                    filter: new OpenLayers.Filter.Comparison({
                        type: OpenLayers.Filter.Comparison.BETWEEN,
                        property: this.indicator,
                        lowerBoundary: boundsArray[i],
                        upperBoundary: boundsArray[i + 1]
                    })
                });
                rules.push(rule);
            }
        }
        this.extendStyle(rules);
        mapfish.GeoStat.prototype.applyClassification.apply(this, arguments);
    },

    updateLegend: function() {
        if (!this.legendDiv) {
            return;
        }

        // TODO use css classes instead
        this.legendDiv.update("");
        for (var i = 0; i < this.classification.bins.length; i++) {
            if (this.colorInterpolation.length > i) {
                var element = document.createElement("div");
                element.style.backgroundColor = this.colorInterpolation[i].toHexString();
                element.style.width = "30px";
                element.style.height = "15px";
                element.style.cssFloat = "left";
                element.style.marginRight = "10px";
                this.legendDiv.appendChild(element);

                element = document.createElement("div");
                element.innerHTML = this.classification.bins[i].label;
                this.legendDiv.appendChild(element);

                element = document.createElement("div");
                element.style.clear = "left";
                this.legendDiv.appendChild(element);
            }
        }
    },

    CLASS_NAME: "mapfish.GeoStat.Choropleth"
});
