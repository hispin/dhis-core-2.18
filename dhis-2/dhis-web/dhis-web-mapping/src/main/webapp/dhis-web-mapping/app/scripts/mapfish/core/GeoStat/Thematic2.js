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

mapfish.GeoStat.Thematic2 = OpenLayers.Class(mapfish.GeoStat, {

    colors: [
        new mapfish.ColorRgb(120, 120, 0),
        new mapfish.ColorRgb(255, 0, 0)
    ],

    method: mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS,

    numClasses: 5,
	
	minSize: 3,
	
	maxSize: 20,
	
	minVal: null,
	
	maxVal: null,

    defaultSymbolizer: {'fillOpacity': 1},

    classification: null,

    colorInterpolation: null,
    
    widget: null,

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
        var numColors = this.classification.bins.length,
			tmpView = this.widget.tmpView,
			legendType = tmpView.legendType;
        
        tmpView.extended.imageLegendConfig = [];
        
        if (legendType === GIS.conf.finals.widget.legendtype_automatic) {
			this.colorInterpolation = mapfish.ColorRgb.getColorsArrayByRgbInterpolation(this.colors[0], this.colors[1], numColors);
		}
		else {
			this.colorInterpolation = tmpView.extended.colorInterpolation;
		}
            
        for (var i = 0; i < this.classification.bins.length; i++) {
            tmpView.extended.imageLegendConfig.push({
                label: this.classification.bins[i].label.replace('&nbsp;&nbsp;', ' '),
                color: this.colorInterpolation[i].toHexString()
            });
        }
    },

    setClassification: function() {
        var values = [];
        for (var i = 0; i < this.layer.features.length; i++) {
            values.push(this.layer.features[i].attributes[this.indicator]);
        }
        
        var distOptions = {
            labelGenerator: this.options.labelGenerator
        };
        var dist = new mapfish.GeoStat.Distribution(values, distOptions);

		this.minVal = dist.minVal;
        this.maxVal = dist.maxVal;

        this.classification = dist.classify(
            this.method,
            this.numClasses,
            null,
            this.widget
        );

        this.createColorInterpolation();
    },

    applyClassification: function(options) {
        this.updateOptions(options);
        
		var calculateRadius = OpenLayers.Function.bind(
			function(feature) {
				var value = feature.attributes[this.indicator];
                var size = (value - this.minVal) / (this.maxVal - this.minVal) *
					(this.maxSize - this.minSize) + this.minSize;
                return size || this.minSize;
            },	this
		);
		this.extendStyle(null, {'pointRadius': '${calculateRadius}'}, {'calculateRadius': calculateRadius});
    
        var boundsArray = this.classification.getBoundsArray();
        var rules = new Array(boundsArray.length-1);        
        for (var i = 0; i < boundsArray.length-1; i++) {
            var rule = new OpenLayers.Rule({
                symbolizer: {fillColor: this.colorInterpolation[i].toHexString()},
                filter: new OpenLayers.Filter.Comparison({
                    type: OpenLayers.Filter.Comparison.BETWEEN,
                    property: this.indicator,
                    lowerBoundary: boundsArray[i],
                    upperBoundary: boundsArray[i + 1]
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
			element,
			legendType = this.widget.tmpView.legendType,
			automatic = GIS.conf.finals.widget.legendtype_automatic,
			predefined = GIS.conf.finals.widget.legendtype_predefined,
			legendNames = this.widget.tmpView.extended.legendNames;
			
        this.legendDiv.update("");
        
        for (var key in config) {
			if (config.hasOwnProperty(key)) {
				element = document.createElement("div");
				element.style.height = "14px";
				element.innerHTML = config[key];
				this.legendDiv.appendChild(element);
				
				element = document.createElement("div");
				element.style.clear = "left";
				this.legendDiv.appendChild(element);
			}
        }
        
        element = document.createElement("div");
        element.style.width = "1px";
        element.style.height = "5px";
        this.legendDiv.appendChild(element);
        
        if (legendType === automatic) {
            for (var i = 0; i < this.classification.bins.length; i++) {
                var element = document.createElement("div");
                element.style.backgroundColor = this.colorInterpolation[i].toHexString();
                element.style.width = "30px";
                element.style.height = "15px";
                element.style.cssFloat = "left";
                element.style.marginRight = "8px";
                this.legendDiv.appendChild(element);

                element = document.createElement("div");
                element.innerHTML = this.classification.bins[i].label;
                this.legendDiv.appendChild(element);

                element = document.createElement("div");
                element.style.clear = "left";
                this.legendDiv.appendChild(element);
            }
        }
        else if (legendType === predefined) {
            for (var i = 0; i < this.classification.bins.length; i++) {
                var element = document.createElement("div");
                element.style.backgroundColor = this.colorInterpolation[i].toHexString();
                element.style.width = "30px";
                element.style.height = legendNames[i] ? "25px" : "20px";
                element.style.cssFloat = "left";
                element.style.marginRight = "8px";
                this.legendDiv.appendChild(element);

                element = document.createElement("div");
                element.style.lineHeight = legendNames[i] ? "12px" : "7px";
                element.innerHTML = '<b style="color:#222; font-size:10px !important">' + (legendNames[i] || '') + '</b><br/>' + this.classification.bins[i].label;
                this.legendDiv.appendChild(element);

                element = document.createElement("div");
                element.style.clear = "left";
                this.legendDiv.appendChild(element);
            }
        }
    },

    CLASS_NAME: "mapfish.GeoStat.Thematic2"
});
