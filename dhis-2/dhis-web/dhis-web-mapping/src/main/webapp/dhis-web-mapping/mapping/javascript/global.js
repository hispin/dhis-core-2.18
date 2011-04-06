G.conf = {

//  Ajax requests

    path_mapping: '../',
    path_commons: '../../dhis-web-commons-ajax-json/',
    type: '.action',
	
//	Help strings
    
    setup: 'gisSetup',
	thematicMap: 'gisThematicMap',
    overlayRegistration: 'gisOverlay',
	administration: 'gisAdministration',
	favorites: 'gisFavoriteMapView',
	legendSets: 'gisLegendSet',
    imageExport: 'gisImageExport',

//  Layout

    west_width: 270,
    multiselect_width: 219,
    label_width: 85,
	combo_width: 150,
	combo_width_fieldset: 127,
	combo_list_width_fieldset: 127 + 17,
	combo_number_width: 65,
	combo_number_width_small: 40,
    window_width: 251,
    window_position_x: 55,
    window_position_y: 41,
    
	emptytext: '',
	labelseparator: '',
	
//	DHIS variables

	map_source_type_database: 'database',
	map_source_type_geojson: 'geojson',
	map_source_type_shapefile: 'shapefile',
	map_legend_type_automatic: 'automatic',
	map_legend_type_predefined: 'predefined',
    map_layer_type_baselayer: 'baselayer',
    map_layer_type_overlay: 'overlay',
    map_layer_type_thematic: 'thematic',
	map_value_type_indicator: 'indicator',
	map_value_type_dataelement: 'dataelement',
    map_date_type_fixed: 'fixed',
    map_date_type_start_end: 'start-end',
    map_selection_type_parent: 'parent',
    map_selection_type_level: 'level',
    map_feature_type_multipolygon: 'MultiPolygon',
    map_feature_type_multipolygon_class_name: 'OpenLayers.Geometry.MultiPolygon',
    map_feature_type_polygon: 'Polygon',
    map_feature_type_polygon_class_name: 'OpenLayers.Geometry.Polygon',
    map_feature_type_point: 'Point',
    map_feature_type_point_class_name: 'OpenLayers.Geometry.Point',
    map_view_access_level_user: 'user',
    map_view_access_level_system: 'system',
    aggregation_strategy_real_time: 'real_time',
    aggregation_strategy_batch: 'batch',
    
//  MapFish

    classify_with_bounds: 1,
    classify_by_equal_intervals: 2,
    classify_by_quantils: 3,

//  Layers

    opacityItems: [
        {text: '0.1', iconCls: 'menu-layeroptions-opacity-10'},
        {text: '0.2', iconCls: 'menu-layeroptions-opacity-20'},
        {text: '0.3', iconCls: 'menu-layeroptions-opacity-30'},
        {text: '0.4', iconCls: 'menu-layeroptions-opacity-40'},
        {text: '0.5', iconCls: 'menu-layeroptions-opacity-50'},
        {text: '0.6', iconCls: 'menu-layeroptions-opacity-60'},
        {text: '0.7', iconCls: 'menu-layeroptions-opacity-70'},
        {text: '0.8', iconCls: 'menu-layeroptions-opacity-80'},
        {text: '0.9', iconCls: 'menu-layeroptions-opacity-90'},
        {text: '1.0', iconCls: 'menu-layeroptions-opacity-100'}
    ],
    
    defaultLayerOpacity: 0.8,
    
    defaultLayerZIndex: 10000
};

G.util = {
    
    expandWidget: function(widget) {
        var collapsed = widget == choropleth ? symbol : choropleth;
        collapsed.collapse();
        widget.expand();
    },
    
    getUrlParam: function(strParam) {
        var output = '';
        var strHref = window.location.href;
        if (strHref.indexOf('?') > -1 ) {
            var strQueryString = strHref.substr(strHref.indexOf('?')).toLowerCase();
            var aQueryString = strQueryString.split('&');
            for (var iParam = 0; iParam < aQueryString.length; iParam++) {
                if (aQueryString[iParam].indexOf(strParam.toLowerCase() + '=') > -1) {
                    var aParam = aQueryString[iParam].split('=');
                    output = aParam[1];
                    break;
                }
            }
        }
        return unescape(output);
    },

    getKeys: function(obj) {
        var temp = [];
        for (var k in obj) {
            if (obj.hasOwnProperty(k)) {
                temp.push(k);
            }
        }
        return temp;
    },

    validateInputNameLength: function(name) {
        return (name.length <= 25);
    },
    
    getMultiSelectHeight: function() {
        var h = screen.height;
        return h <= 800 ? 220 :
            h <= 1050 ? 310 :
                h <= 1200 ? 470 : 900;
    },

    getGridPanelHeight: function() {
        var h = screen.height;
        return h <= 800 ? 180 :
            h <= 1050 ? 480 :
                h <= 1200 ? 600 : 900;
    },

    getNumericMapView: function(mapView) {
        mapView.id = parseFloat(mapView.id);
        mapView.indicatorGroupId = parseFloat(mapView.indicatorGroupId);
        mapView.indicatorId = parseFloat(mapView.indicatorId);
        mapView.periodId = parseFloat(mapView.periodId);
        mapView.method = parseFloat(mapView.method);
        mapView.classes = parseFloat(mapView.classes);
        mapView.mapLegendSetId = parseFloat(mapView.mapLegendSetId);
        mapView.longitude = parseFloat(mapView.longitude);
        mapView.latitude = parseFloat(mapView.latitude);
        mapView.zoom = parseFloat(mapView.zoom);
        return mapView;
    },

    getNumberOfDecimals: function(x,dec_sep) {
        var tmp = new String();
        tmp = x;
        return tmp.indexOf(dec_sep) > -1 ? tmp.length-tmp.indexOf(dec_sep) - 1 : 0;
    },

    labels: {    
        getActivatedOpenLayersStyleMap: function(fsize, fweight, fstyle) {
            return new OpenLayers.StyleMap({
                'default' : new OpenLayers.Style(
                    OpenLayers.Util.applyDefaults({
                        'fillOpacity': 1,
                        'strokeColor': '#222222',
                        'strokeWidth': 1,
                        'label': '${labelString}',
                        'fontFamily': 'arial,lucida sans unicode',
                        'fontSize': fsize ? fsize : 13,
                        'fontWeight': fweight ? 'bold' : 'normal',
                        'fontStyle':  fstyle ? 'italic' : 'normal'
                    },
                    OpenLayers.Feature.Vector.style['default'])
                ),
                'select': new OpenLayers.Style({
                    'strokeColor': '#000000',
                    'strokeWidth': 2,
                    'cursor': 'pointer'
                })
            });
        },
        getDeactivatedOpenLayersStyleMap: function() {
            return new OpenLayers.StyleMap({
                'default': new OpenLayers.Style(
                    OpenLayers.Util.applyDefaults({
                        'fillOpacity': 1,
                        'strokeColor': '#222222',
                        'strokeWidth': 1
                    },
                    OpenLayers.Feature.Vector.style['default'])
                ),
                'select': new OpenLayers.Style({
                    'strokeColor': '#000000',
                    'strokeWidth': 2,
                    'cursor': 'pointer'
                })
            });
        },
        toggleFeatureLabels: function(widget, fsize, fweight, fstyle) {
            function activateLabels() {
                widget.layer.styleMap = this.getActivatedOpenLayersStyleMap(fsize, fweight, fstyle);
                widget.labels = true;
            }
            function deactivateLabels(scope) {
                widget.layer.styleMap = this.getDeactivatedOpenLayersStyleMap();
                widget.labels = false;
            }
            
            if (widget.labels) {
                deactivateLabels.call(this);
            }
            else {
                activateLabels.call(this);
            }
        
            widget.applyValues();
        }
    },

    sortByValue: function(a,b) {
        return b.value-a.value;
    },

    getLegendsJSON: function() {
        var json = '{';
        json += '"legends":';
        json += '[';
        for(var i = 0; i < this.imageLegend.length; i++) {
            json += '{';
            json += '"label": "' + this.imageLegend[i].label + '",';
            json += '"color": "' + this.imageLegend[i].color + '"';
            json += i < this.imageLegend.length-1 ? '},' : '}';
        }
        json += ']';
        json += '}';
        return json;
    },
    
    setCurrentValue: function(cb, mv) {
        if (cb.getValue() == cb.currentValue) {
            return true;
        }
        else {
            cb.currentValue = cb.getValue();
            this.form.findField(mv).clearValue();
            return false;
        }
    },
    
    setKeepPosition: function(cb) {
        cb.keepPosition = !cb.keepPosition ? true : cb.keepPosition;
    },
    
    mergeSvg: function(str, ext) {
        if (ext.length) {
            str = str || '<svg>';
            for (var i = 0; i < ext.length; i++) {
                str = str.replace('</svg>');
                ext[i] = ext[i].substring(ext[i].indexOf('>')+1);
                str += ext[i];
            }
        }
        return str;
    },
    
    getOverlaysSvg: function(overlays) {
        if (overlays.length) {
            for (var i = 0; i < overlays.length; i++) {
                overlays[i] = document.getElementById(overlays[i].svgId).parentNode.innerHTML;
            }
        }
        return overlays;
    },

    getTransformedFeatureArray: function(features) {
        var sourceProjection = new OpenLayers.Projection("EPSG:4326");
        var destinationProjection = new OpenLayers.Projection("EPSG:900913");
        for (var i = 0; i < features.length; i++) {
            features[i].geometry.transform(sourceProjection, destinationProjection);
        }
        return features;
    },
 
    getTransformedPointByXY: function(x, y) {
		var p = new OpenLayers.Geometry.Point(parseFloat(x), parseFloat(y));
        return p.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
    },
    
    createOverlay: function(name, fillColor, fillOpacity, strokeColor, strokeWidth, url) {
        return new OpenLayers.Layer.Vector(name, {
            'visibility': false,
            'styleMap': new OpenLayers.StyleMap({
                'default': new OpenLayers.Style(
                    OpenLayers.Util.applyDefaults(
                        {'fillColor': fillColor, 'fillOpacity': fillOpacity, 'strokeColor': strokeColor, 'strokeWidth': strokeWidth},
                        OpenLayers.Feature.Vector.style['default']
                    )
                )
            }),
            'strategies': [new OpenLayers.Strategy.Fixed()],
            'protocol': new OpenLayers.Protocol.HTTP({
                'url': url,
                'format': new OpenLayers.Format.GeoJSON()
            })
        });
    },
    
    getVisibleLayers: function(layers) {
        var vLayers = [];
        for (var i = 0; i < layers.length; i++) {
            if (layers[i].visibility) {
                vLayers.push(layers[i]);
            }
        }
        return vLayers;
    },
    
    getVectorLayers: function() {
        var layers = [];
        for (var i = 0; i < G.vars.map.layers.length; i++) {
            if (G.vars.map.layers[i].layerType == G.conf.map_layer_type_thematic ||
            G.vars.map.layers[i].layerType == G.conf.map_layer_type_overlay) {
                layers.push(G.vars.map.layers[i]);
            }
        }
        return layers;
    },
    
    getLayersByType: function(type) {
        var layers = [];
        for (var i = 0; i < G.vars.map.layers.length; i++) {
            if (G.vars.map.layers[i].layerType == type) {
                layers.push(G.vars.map.layers[i]);
            }
        }
        return layers;
    },
    
    setZIndexByLayerType: function(type, index) {
        for (var i = 0; i < G.vars.map.layers.length; i++) {
            if (G.vars.map.layers[i].layerType == type) {
                G.vars.map.layers[i].setZIndex(index);
            }
        }
    },
    
    setOpacityByLayerType: function(type, opacity) {
        for (var i = 0; i < G.vars.map.layers.length; i++) {
            if (G.vars.map.layers[i].layerType == type) {
                G.vars.map.layers[i].setOpacity(opacity);
            }
        }
    },
    
    findArrayValue: function(array, value) {
        for (var i = 0; i < array.length; i++) {
            if (value == array[i]) {
                return true;
            }
        }
        return false;
    },
    
    compareObjToObj: function(obj1, obj2, exceptions) {
        for (p in obj1) {
            if (obj1[p] !== obj2[p]) {
                if (!G.util.findArrayValue(exceptions, p)) {
                    return false;
                }
            }
        }
        return true;
    }
};

G.date = {
    getNowHMS: function(date) {
        date = date || new Date();      
        return G.date.getDoubleDigit(date.getHours()) + ':' +
               G.date.getDoubleDigit(date.getMinutes()) + ':' +
               G.date.getDoubleDigit(date.getSeconds());
    },
    
    getDoubleDigit: function(unit) {
        unit = '' + unit;
        return unit.length < 2 ? '0' + unit : unit;
    }
};

G.vars = {
    map: null,
    
    parameter: null,
    
    mask: null,
    
    activePanel: {
        value: G.conf.thematicMap,
        setPolygon: function() {
            this.value = G.conf.thematicMap;
        },
        setPoint: function() {
            this.value = G.conf.thematicMap2;
        },
        isPolygon: function() {
            return this.value === G.conf.thematicMap;
        },
        isPoint: function() {
            return this.value === G.conf.thematicMap2;
        }
    }
};

G.user = {
    isAdmin: false
};

G.system = {
    aggregationStrategy: null,
    
    mapDateType: {
        value: null,
        setFixed: function() {
            this.value = G.conf.map_date_type_fixed;
        },
        setStartEnd: function() {
            this.value = G.conf.map_date_type_start_end;
        },
        isFixed: function() {
            return this.value === G.conf.map_date_type_fixed;
        },
        isStartEnd: function() {
            return this.value === G.conf.map_date_type_start_end;
        }
    }
};

G.func = {
	storeLoadListener: function() {
		this.isLoaded = true;
	},
    
    loadStart: function() {
        G.vars.mask.msg = G.i18n.loading;
        G.vars.mask.show();
    },
    
    loadEnd: function() {
        G.vars.mask.hide();
    }
};
