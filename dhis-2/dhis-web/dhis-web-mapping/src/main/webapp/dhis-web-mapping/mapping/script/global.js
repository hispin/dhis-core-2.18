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
    gridpanel_width: 270 - 15,
    multiselect_width: 219,
	combo_width: 150,
	combo_width_fieldset: 112,
	combo_list_width_fieldset: 112 + 17,
	combo_number_width: 65,
	combo_number_width_small: 40,
    window_width: 251,
    
	emptytext: '',
	labelseparator: '',
	
//	Styles

	assigned_row_color: '#90ee90',
	unassigned_row_color: '#ffffff',
	
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
    map_feature_type_polygon: 'Polygon',
    map_feature_type_point: 'Point',
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
    ]
};

G.util = {
    
    /* Detect mapview parameter in URL */
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

    /* Get all properties in an object */
    getKeys: function(obj) {
        var temp = [];
        for (var k in obj) {
            if (obj.hasOwnProperty(k)) {
                temp.push(k);
            }
        }
        return temp;
    },

    /* Input validation */
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

    /* Make map view numbers numeric */
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

    /* Get number of decimals */
    getNumberOfDecimals: function(x,dec_sep) {
        var tmp = new String();
        tmp = x;
        return tmp.indexOf(dec_sep) > -1 ? tmp.length-tmp.indexOf(dec_sep) - 1 : 0;
    },

    /* Feature labels */
    labels: {    
        getActivatedOpenLayersStyleMap: function() {
            return new OpenLayers.StyleMap({
                'default' : new OpenLayers.Style(
                    OpenLayers.Util.applyDefaults({
                        'fillOpacity': 1,
                        'strokeColor': '#222222',
                        'strokeWidth': 1,
                        'label': '${labelString}',
                        'fontFamily': 'arial,lucida sans unicode',
                        'fontWeight': 'bold',
                        'fontSize': 14
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
        }
    },

    toggleFeatureLabels: function(widget) {
        function activateLabels(scope) {
            widget.layer.styleMap = scope.labels.getActivatedOpenLayersStyleMap();
            widget.labels = true;
        }
        function deactivateLabels(scope) {
            widget.layer.styleMap = scope.labels.getDeactivatedOpenLayersStyleMap();
            widget.labels = false;
        }
        
        if (widget.labels) {
            deactivateLabels(this);
        }
        else {
            activateLabels(this);
        }
        
        widget.applyValues();
    },

    toggleFeatureLabelsAssignment: function() {
        function activateLabels(scope) {
            mapping.layer.styleMap = scope.labels.getActivatedOpenLayersStyleMap();
            mapping.labels = true;
        }
        function deactivateLabels(scope) {
            mapping.layer.styleMap = scope.labels.getDeactivatedOpenLayersStyleMap();
            mapping.labels = false;
        }
        
        if (mapping.labels) {
            deactivateLabels(this);
        }
        else {
            activateLabels(this);
        }
        
        mapping.classify(false, true);
    },

    /* Sort values */
    sortByValue: function(a,b) {
        return b.value-a.value;
    },

    /* Create JSON for map export */
    getExportDataValueJSON: function(mapValues) {
        var json = '{';
        json += '"datavalues": ';
        json += '[';
        mapValues.sort(this.sortByValue);
        for (var i = 0; i < mapValues.length; i++) {
            json += '{';
            json += '"organisation": "' + mapValues[i].orgUnitId + '",';
            json += '"value": "' + mapValues[i].value + '"';
            json += i < mapValues.length - 1 ? '},' : '}';
        }
        json += ']';
        json += '}';
        return json;
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
        if (!cb.keepPosition) {
            cb.keepPosition = true;
        }
    },
 
    getTransformedPointByXY: function(x, y) {
		var p = new OpenLayers.Geometry.Point(parseFloat(x), parseFloat(y));
        return p.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
     },

    getTransformedFeatureArray: function(features) {
        for (var i = 0; i < features.length; i++) {
            features[i].geometry.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
        }
        return features;
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
    }
};

G.vars = {
    map: null,
    
    parameter: null,
    
    mapSourceType: {
        value: null,
        setDatabase: function() {
            this.value = G.conf.map_source_type_database;
        },
        setGeojson: function() {
            this.value = G.conf.map_source_type_geojson;
        },
        setShapefile: function() {
            this.value = G.conf.map_source_type_shapefile;
        },
        isDatabase: function() {
            return this.value == G.conf.map_source_type_database;
        },
        isGeojson: function() {
            return this.value == G.conf.map_source_type_geojson;
        },
        isShapefile: function() {
            return this.value == G.conf.map_source_type_shapefile;
        }
    },
    
    mapDateType: {
        value: null,
        setFixed: function() {
            this.value = G.conf.map_date_type_fixed;
        },
        setStartEnd: function() {
            this.value = G.conf.map_date_type_start_end;
        },
        isFixed: function() {
            return this.value == G.conf.map_date_type_fixed;
        },
        isStartEnd: function() {
            return this.value == G.conf.map_date_type_start_end;
        }
    },
    
    activePanel: {
        value: G.conf.thematicMap,
        setPolygon: function() {
            this.value = G.conf.thematicMap;
        },
        setPoint: function() {
            this.value = G.conf.thematicMap2;
        },
        isPolygon: function() {
            return this.value == G.conf.thematicMap;
        },
        isPoint: function() {
            return this.value == G.conf.thematicMap2;
        }
    },
    
    mask: null,
    
    exportValues: null,
    
    topLevelUnit: null
};

G.user = {
    isAdmin: false
};

G.system = {
    aggregationStrategy: null
};

G.func = {
	storeLoadListener: function() {
		this.isLoaded = true;
	}
};
