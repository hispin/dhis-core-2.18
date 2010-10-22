/*
 * Copyright (C) 2007-2008  Camptocamp
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
 * In this file people will find :
 *   - GeoStat
 *   - Distribution
 *   - Classification
 *
 */

/**
 * @requires OpenLayers/Layer/Vector.js
 * @requires OpenLayers/Popup/AnchoredBubble.js
 * @requires OpenLayers/Feature/Vector.js
 * @requires OpenLayers/Format/GeoJSON.js
 * @requires OpenLayers/Control/SelectFeature.js
 * @requires OpenLayers/Ajax.js
 */

/**
 * Class: mapfish.GeoStat
 * Base class for geo-statistics. This class is not meant to be used directly, it serves
 * as the base for specific geo-statistics implementations.
 */
mapfish.GeoStat = OpenLayers.Class({

    /**
     * APIProperty: layer
     * {<OpenLayers.Layer.Vector>} The vector layer containing the features that
     *      are styled based on statistical values. If none is provided, one will
     *      be created.
     */
    layer: null,

    /**
     * APIProperty: format
     * {<OpenLayers.Format>} The OpenLayers format used to get features from
     *      the HTTP request response. GeoJSON is used if none is provided.
     */
    format: null,

    /**
     * APIProperty: url
     * {String} The URL to the web service. If none is provided, the features
     *      found in the provided vector layer will be used.
     */
    url: null,

    /**
     * APIProperty: requestSuccess
     * {Function} Function called upon success with the HTTP request.
     */
    requestSuccess: function(request) {},

    /**
     * APIProperty: requestFailure
     * {Function} Function called upon failure with the HTTP request.
     */
    requestFailure: function(request) {},

    /**
     * APIProperty: featureSelection
     * {Boolean} A boolean value specifying whether feature selection must
     *      be put in place. If true a popup will be displayed when the
     *      mouse goes over a feature.
     */
    featureSelection: true,

    /**
     * APIProperty: nameAttribute
     * {String} The feature attribute that will be used as the popup title.
     *      Only applies if featureSelection is true.
     */
    nameAttribute: null,

    /**
     * APIProperty: indicator
     * {String} Defines the attribute to apply classification on
     */
    indicator: null,

    /**
     * Property: defaultSymbolizer
     * {Object} This symbolizer is used in the constructor to define
     *      the default style in the style object associated with the
     *      "default" render intent. This symbolizer is extended with
     *      OpenLayers.Feature.Vector.style['default']. It can be
     *      overridden in subclasses.
     */
    defaultSymbolizer: {},

    /**
     * Property: selectSymbolizer
     * {Object} This symbolizer is used in the constructor to define
     *      the select style in the style object associated with the
     *      "select" render intent. When rendering selected features
     *      it is extended with the default symbolizer. It can be
     *      overridden in subclasses.
     */
    selectSymbolizer: {'strokeColor': '#000000'}, // neutral stroke color

    /**
     * Property: legendDiv
     * {Object} Reference to the DOM container for the legend to be
     *     generated.
     */
    legendDiv: null,

    /**
     * Constructor: mapfish.GeoStat
     *
     * Parameters:
     * map - {<OpenLayers.Map>} OpenLayers map object
     * options - {Object} Hashtable of extra options
     */
    initialize: function(map, options) {
        this.map = map;
        this.addOptions(options);
        if (!this.layer) {
            // no layer specified, create one
            var styleMap = new OpenLayers.StyleMap({
                'default': new OpenLayers.Style(
                    OpenLayers.Util.applyDefaults(
                        this.defaultSymbolizer,
                        OpenLayers.Feature.Vector.style['default']
                    )
                ),
                'select': new OpenLayers.Style(this.selectSymbolizer)
            });
            var layer = new OpenLayers.Layer.Vector('geostat', {
                'displayInLayerSwitcher': false,
                'visibility': false,
                'styleMap': styleMap
            });
            map.addLayer(layer);
            this.layer = layer;
        }

/*
        if (this.featureSelection) {
            // create select feature control so that popups can
            // be displayed on feature selection
            this.layer.events.on({
                'featureselected': this.showDetails,
                'featureunselected': this.hideDetails,
                scope: this
            });
            var selectFeature = new OpenLayers.Control.SelectFeature(
                this.layer,
                {'hover': true}
            );
            map.addControl(selectFeature);
            selectFeature.activate();
        }
*/        
        this.setUrl(this.url);
        this.legendDiv = Ext.get(options.legendDiv);
    },
 
    setUrl: function(url) {
        // get features from web service if a url is specified
        this.url = url;
        if (this.url) {
            OpenLayers.loadURL(
                this.url, '', this, this.onSuccess, this.onFailure);
        }
    },

    /**
     * Method: onSuccess
     *
     * Parameters:
     * request - {Object}
     */
    onSuccess: function(request) {
    
        var doc = request.responseXML;
        if (!doc || !doc.documentElement) {
            doc = request.responseText;
        }
        var format = this.format || new OpenLayers.Format.GeoJSON()
        this.layer.removeFeatures(this.layer.features);
        this.layer.addFeatures(format.read(doc));
        this.requestSuccess(request);
        
        if (ACTIVEPANEL == thematicMap) {
            if (!choropleth.validateForm()) {
                MASK.hide();
            }
            choropleth.classify(false);
        }
        else if (ACTIVEPANEL == thematicMap2) {
            if (!proportionalSymbol.validateForm()) {
                MASK.hide();
            }
            proportionalSymbol.classify(false);
        }
        else if (ACTIVEPANEL == organisationUnitAssignment) {
            mapping.classify(false);
        }
        
        

        // MASK.hide();
    },

    /**
     * Method: onFailure
     *
     * Parameters:
     * request - {Object}
     */
    onFailure: function(request) {
        this.requestFailure(request);
    },

    /**
     * Method: addOptions
     *
     * Parameters:
     * newOptions - {Object}
     */
    addOptions: function(newOptions) {
        if (newOptions) {
            if (!this.options) {
                this.options = {};
            }
            // update our copy for clone
            OpenLayers.Util.extend(this.options, newOptions);
            // add new options to this
            OpenLayers.Util.extend(this, newOptions);
        }
    },

    /**
     * Method: extendStyle
     *      Extent layer style for the default render intent and
     *      for the select render intent if featureSelection is
     *      set.
     *
     * Parameters:
     * renderIntent - {String} The render intent
     * rules - {Array({<OpenLayers.Rule>})} Array of new rules to add
     * symbolizer - {Object} Object with new styling options
     * context - {Object} Object representing the new context
     */
    extendStyle: function(rules, symbolizer, context) {
        var style = this.layer.styleMap.styles['default'];
        // replace rules entirely - the geostat object takes control
        // on the style rules of the "default" render intent
        if (rules) {
            style.rules = rules;
        }
        if (symbolizer) {
            style.setDefaultStyle(
                OpenLayers.Util.applyDefaults(
                    symbolizer,
                    style.defaultStyle
                )
            );
        }
        if (context) {
            if (!style.context) {
                style.context = {};
            }
            OpenLayers.Util.extend(style.context, context);
        }
    },

    /**
     * APIMethod: applyClassification
     *      To be overriden by subclasses.
     *
     * Parameters:
     * options - {Object}
     */
    applyClassification: function(options) {
        this.layer.renderer.clear();
        this.layer.redraw();
        this.updateLegend();
        this.layer.setVisibility(true);
    },

    /**
     * Method: showDetails
     *
     * Parameters:
     * obj - {Object}
     */
    showDetails: function(obj) {
        var feature = obj.feature;
        // popup html
        var html = typeof this.nameAttribute == 'string' ?
            '<h4 style="margin-top:5px">'
                + feature.attributes[this.nameAttribute] +'</h4>' : '';
        html += this.indicator + ": " + feature.attributes[this.indicator];
        // create popup located in the bottom right of the map
        var bounds = this.layer.map.getExtent();
        var lonlat = new OpenLayers.LonLat(bounds.right, bounds.bottom);
        var size = new OpenLayers.Size(200, 100);
        var popup = new OpenLayers.Popup.AnchoredBubble(
            feature.attributes[this.nameAttribute],
            lonlat, size, html, 0.5, false);
        var symbolizer = feature.layer.styleMap.createSymbolizer(feature, 'default');
        popup.setBackgroundColor(symbolizer.fillColor);
        this.layer.map.addPopup(popup);
    },

    /**
     * Method: hideDetails
     *
     * Parameters:
     * obj - {Object}
     */
    hideDetails: function(obj) {
        //remove all other popups from screen
        var map= this.layer.map;
        for (var i = map.popups.length - 1; i >= 0; --i) {
            map.removePopup(map.popups[i]);
        }
    },

    CLASS_NAME: "mapfish.GeoStat"

});

/**
 * Distribution Class
 */
mapfish.GeoStat.Distribution = OpenLayers.Class({

    /**
     * Property: labelGenerator
     *     Generator for bin labels
     */
    labelGenerator: function(bin, binIndex, nbBins) {
        return this.defaultLabelGenerator(bin, binIndex, nbBins)
    },

    values: null,

    nbVal: null,

    minVal: null,

    maxVal: null,

    initialize: function(values, options) {
        OpenLayers.Util.extend(this, options);
        this.values = values;
        this.nbVal = values.length;
        this.minVal = this.nbVal ? mapfish.Util.min(this.values) : 0;
        this.maxVal = this.nbVal ? mapfish.Util.max(this.values) : 0;
    },

    /**
     * Method: labelGenerator
     *    Generator for bin labels
     *
     * Parameters:
     *   bin - {<mapfish.GeoStat.Bin>} Lower bound limit value
     *   binIndex - {Integer} Current bin index
     *   nBins - {Integer} Total number of bins
     */
    defaultLabelGenerator: function(bin, binIndex, nbBins) {
		if (ACTIVEPANEL == organisationUnitAssignment) {
            if (bin.upperBound < 1) {
                return 'Available' + '&nbsp;&nbsp; ( ' + bin.nbVal + ' )';
            }
            else {
                return 'Assigned' + '&nbsp;&nbsp; ( ' + bin.nbVal + ' )';
            }
		}
		else {
            var upper = parseFloat(bin.upperBound);
            
            if (bin.upperBound > bin.lowerBound) {
                if (binIndex < nbBins-1) {
                    upper -= parseFloat("0.1");
                }
            }
			return parseFloat(bin.lowerBound).toFixed(1) + ' - ' + upper.toFixed(1) + '&nbsp;&nbsp; ( ' + bin.nbVal + ' )';
		}
    },

    classifyWithBounds: function(bounds) {
        var bins = [];
        var binCount = [];
        var sortedValues = [];
        for (var i = 0; i < this.values.length; i++) {
            sortedValues.push(this.values[i]);
        }
        sortedValues.sort(function(a,b) {return a-b;});
        var nbBins = bounds.length - 1;

        for (var i = 0; i < nbBins; i++) {
            binCount[i] = 0;
        }

        for (var i = 0; i < nbBins - 1; i) {
            if (sortedValues[0] < bounds[i + 1]) {
                binCount[i] = binCount[i] + 1;
                sortedValues.shift();
            } else {
                i++;
            }
        }

        binCount[nbBins - 1] = this.nbVal - mapfish.Util.sum(binCount);
		
		var imageLegend = new Array();
		
        for (var i = 0; i < nbBins; i++) {
            bins[i] = new mapfish.GeoStat.Bin(binCount[i], bounds[i], bounds[i + 1], i == (nbBins - 1));
            var labelGenerator = this.labelGenerator || this.defaultLabelGenerator;
            bins[i].label = labelGenerator(bins[i], i, nbBins);
			imageLegend[i] = new Object();
			imageLegend[i].label = bins[i].label;
        }
        
        if (ACTIVEPANEL == thematicMap) {   
            choropleth.imageLegend = imageLegend;
        }
        else if (ACTIVEPANEL == thematicMap2) {
            proportionalSymbol.imageLegend = imageLegend;
        }
        
        return new mapfish.GeoStat.Classification(bins);
    },

    classifyByEqIntervals: function(nbBins) {
        var bounds = [];

        for (var i = 0; i <= nbBins; i++) {
            bounds[i] = this.minVal + i*(this.maxVal - this.minVal) / nbBins;
        }

        return this.classifyWithBounds(bounds);
    },

    classifyByQuantils: function(nbBins) {
        var values = this.values;
        values.sort(function(a,b) {return a-b;});
        var binSize = Math.round(this.values.length / nbBins);

        var bounds = [];
        var binLastValPos = (binSize == 0) ? 0 : binSize;

        if (values.length > 0) {
            bounds[0] = values[0];
            for (i = 1; i < nbBins; i++) {
                bounds[i] = values[binLastValPos];
                binLastValPos += binSize;
            }
            bounds.push(values[values.length - 1]);
        }
        
        for (var i = 0; i < bounds.length; i++) {
            bounds[i] = parseFloat(bounds[i]);
        }

        return this.classifyWithBounds(bounds);
    },

    /**
     * Returns:
     * {Number} Maximal number of classes according to the Sturge's rule
     */
    sturgesRule: function() {
        return Math.floor(1 + 3.3 * Math.log(this.nbVal, 10));
    },
	
    /**
     * Method: classify
     *    This function calls the appropriate classifyBy... function.
     *    The name of classification methods are defined by class constants
     *
     * Parameters:
     * method - {Integer} Method name constant as defined in this class
     * nbBins - {Integer} Number of classes
     * bounds - {Array(Integer)} Array of bounds to be used for by bounds method
     *
     * Returns:
     * {<mapfish.GeoStat.Classification>} Classification
     */
    classify: function(method, nbBins, bounds) {
		var mlt;
        if (ACTIVEPANEL == thematicMap) {
            mlt = Ext.getCmp('maplegendtype_cb').getValue();
        }
        else if (ACTIVEPANEL == thematicMap2) {
            mlt = Ext.getCmp('maplegendtype_cb2').getValue();
        }
        else if (ACTIVEPANEL == organisationUnitAssignment) {
            mlt = map_legend_type_automatic;
        }

		if (mlt == map_legend_type_automatic) {
			if (method == mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS) {
				var str = ACTIVEPANEL == thematicMap ? Ext.getCmp('bounds_tf').getValue() : Ext.getCmp('bounds_tf2').getValue();
				
				for (var i = 0; i < str.length; i++) {
					str = str.replace(' ','');
				}
				
				if (str.charAt(str.length-1) == ',') {
					str = str.substring(0, str.length-1);
				}
				
				bounds = new Array();
				bounds = str.split(',');
				
				for (var i = 0; i < bounds.length; i++) {
					if (!Ext.num(parseFloat(bounds[i]), false)) {
						bounds.remove(bounds[i]);
						i--;
					}
				}
				
				var newInput = bounds.join(',');
                
                if (ACTIVEPANEL == thematicMap) {
                    Ext.getCmp('bounds_tf').setValue(newInput);
                }
                else {
                    Ext.getCmp('bounds_tf2').setValue(newInput);
                }
				
				for (var i = 0; i < bounds.length; i++)
				{
					bounds[i] = parseFloat(bounds[i]);
					
					if (bounds[i] < this.minVal || bounds[i] > this.maxVal)
					{
						Ext.message.msg(false, 'Class breaks must be higher than <span class="x-msg-hl">' + this.minVal + '</span> and lower than <span class="x-msg-hl">' + this.maxVal + '</span>.');
					}
				}
				
				bounds.unshift(this.minVal);
				bounds.push(this.maxVal);
			}
		}
		else if (mlt == map_legend_type_predefined) {
			bounds = ACTIVEPANEL == thematicMap ? choropleth.bounds : proportionalSymbol.bounds;
			if (bounds[0] <= this.minVal) {
				bounds[0] = this.minVal;
			}
			else {
				bounds.unshift(this.minVal);
                if (ACTIVEPANEL == thematicMap) {
                    choropleth.colorInterpolation.unshift(new mapfish.ColorRgb(240,240,240));
                }
                else {
                    proportionalSymbol.colorInterpolation.unshift(new mapfish.ColorRgb(240,240,240));
                }
			}
			
			if (bounds[bounds.length-1] >= this.maxVal) {
				bounds[bounds.length-1] = this.maxVal;
			}
			else {
				bounds.push(this.maxVal);
                if (ACTIVEPANEL == thematicMap) {
                    choropleth.colorInterpolation.push(new mapfish.ColorRgb(240,240,240));
                }
                else {
                    proportionalSymbol.colorInterpolation.push(new mapfish.ColorRgb(240,240,240));
                }
			}
			
			method = ACTIVEPANEL == organisationUnitAssignment ? mapfish.GeoStat.Distribution.CLASSIFY_BY_EQUAL_INTERVALS : mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS;
		}
        
        var classification = null;
        if (!nbBins) {
            nbBins = this.sturgesRule();
        }

        switch (method) {
        case mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS :
            classification = this.classifyWithBounds(bounds);
            break;
        case mapfish.GeoStat.Distribution.CLASSIFY_BY_EQUAL_INTERVALS :
            classification = this.classifyByEqIntervals(nbBins);
            break;
        case mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS :
            classification = this.classifyByQuantils(nbBins);
            break;
        default:
            OpenLayers.Console.error("Unsupported or invalid classification method");
        }
        return classification;
    },

    CLASS_NAME: "mapfish.GeoStat.Distribution"
});

/**
 * Constant: mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS
 */
mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS = 1;

/**
 * Constant: mapfish.GeoStat.Distribution.CLASSIFY_BY_EQUAL_INTERVALS
 */
mapfish.GeoStat.Distribution.CLASSIFY_BY_EQUAL_INTERVALS = 2;

/**
 * Constant: mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS
 */
mapfish.GeoStat.Distribution.CLASSIFY_BY_QUANTILS = 3;

/**
 * Bin is category of the Classification.
 * When they are defined, lowerBound is within the class
 * and upperBound is outside de the class.
 */
mapfish.GeoStat.Bin = OpenLayers.Class({
    label: null,
    nbVal: null,
    lowerBound: null,
    upperBound: null,
    isLast: false,

    initialize: function(nbVal, lowerBound, upperBound, isLast) {
        this.nbVal = nbVal;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.isLast = isLast;
    },

    CLASS_NAME: "mapfish.GeoStat.Bin"
});

/**
 * Classification summarizes a Distribution by regrouping data within several Bins.
 */
mapfish.GeoStat.Classification = OpenLayers.Class({
    bins: [],

    initialize: function(bins) {
        this.bins = bins;
    },

    getBoundsArray: function() {
        var bounds = [];
        for (var i = 0; i < this.bins.length; i++) {
            bounds.push(this.bins[i].lowerBound);
        }
        if (this.bins.length > 0) {
            bounds.push(this.bins[this.bins.length - 1].upperBound);
        }
        return bounds;
    },

    CLASS_NAME: "mapfish.GeoStat.Classification"
});
