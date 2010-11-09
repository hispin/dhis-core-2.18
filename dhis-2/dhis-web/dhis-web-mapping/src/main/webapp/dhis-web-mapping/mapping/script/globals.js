var GLOBALS = {};

GLOBALS.conf = {

//  Ajax requests

    path_mapping: '../',
    path_commons: '../../dhis-web-commons-ajax-json/',
    path_geoserver: '../../../geoserver/',
    type: '.action',
	
	ows: 'ows?service=WMS&request=GetCapabilities',
	wfs: 'wfs?request=GetFeature&typename=',	
	output: '&outputformat=json&version=1.0.0',
	
//	Help strings

	thematicMap: 'gisThematicMap',
    thematicMap2: 'gisThematicMap2',
	mapRegistration: 'gisMap',
	organisationUnitAssignment: 'gisMapOrganisationUnitRelation',
    overlayRegistration: 'gisOverlay',
	administration: 'gisAdministration',
	favorites: 'gisFavoriteMapView',
	legendSets: 'gisLegendSet',
	pdfprint: 'gisPdfPrint',

//  Layout

    north_height: 0, // viewport north
    west_width: 270, // viewport west
    gridpanel_width: 270 - 15,
    multiselect_width: 210,
	combo_width: 150,
	combo_width_fieldset: 112,
	combo_list_width_fieldset: 112 + 17,
	combo_number_width: 65,
	
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
	map_value_type_indicator: 'indicator',
	map_value_type_dataelement: 'dataelement',
    map_date_type_fixed: 'fixed',
    map_date_type_start_end: 'start-end',
    map_selection_type_parent: 'parent',
    map_selection_type_level: 'level',
    
//  MapFish

    classify_with_bounds: 1,
    classify_by_equal_intervals: 2,
    classify_by_quantils: 3
};

GLOBALS.util = {
    
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
                    output  =aParam[1];
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

    /* Decide multiselect height based on screen resolution */
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
            json += '"value": "' + mapValues[i].value + '" ';
            json += i < mapValues.length - 1 ? '},' : '}';
        }
        json += ']';
        json += '}';
        return json;
    },

    getLegendsJSON: function() {
        var widget = GLOBALS.vars.activePanel.isPolygon() ? choropleth : proportionalSymbol;
        var json = '{';
        json += '"legends":';
        json += '[';
        for(var i = 0; i < widget.imageLegend.length; i++) {
            json += '{';
            json += '"label": "' + widget.imageLegend[i].label + '",';
            json += '"color": "' + widget.imageLegend[i].color + '" ';
            json += i < widget.imageLegend.length-1 ? '},' : '}';
        }
        json += ']';
        json += '}';
        return json;
    },
    
    /* Backwards mapview compatibility */
    mapViewNullSafe: function(mapView) {
        mapView.mapValueType = GLOBALS.conf.map_value_type_indicator;
        mapView.organisationUnitSelectionType = GLOBALS.conf.map_selection_type_parent;
        mapView.mapLegendType = GLOBALS.conf.map_legend_type_automatic;
        return mapView;
    }    
};

GLOBALS.vars = {    
    map: null,
    
    mapSourceType: {
        value: null,
        setDatabase: function() {
            this.value = GLOBALS.conf.map_source_type_database;
        },
        setGeojson: function() {
            this.value = GLOBALS.conf.map_source_type_geojson;
        },
        setShapefile: function() {
            this.value = GLOBALS.conf.map_source_type_shapefile;
        },
        isDatabase: function() {
            return this.value == GLOBALS.conf.map_source_type_database;
        },
        isGeojson: function() {
            return this.value == GLOBALS.conf.map_source_type_geojson;
        },
        isShapefile: function() {
            return this.value == GLOBALS.conf.map_source_type_shapefile;
        }
    },
    
    mapDateType: {
        value: null,
        setFixed: function() {
            this.value = GLOBALS.conf.map_date_type_fixed;
        },
        setStartEnd: function() {
            this.value = GLOBALS.conf.map_date_type_start_end;
        },
        isFixed: function() {
            return this.value == GLOBALS.conf.map_date_type_fixed;
        },
        isStartEnd: function() {
            return this.value == GLOBALS.conf.map_date_type_start_end;
        }
    },
    
    parameter: null,
    
    activePanel: {
        value: null,
        setPolygon: function() {
            this.value = GLOBALS.conf.thematicMap;
        },
        setPoint: function() {
            this.value = GLOBALS.conf.thematicMap2;
        },
        setAssignment: function() {
            this.value = GLOBALS.conf.organisationUnitAssignment;
        },
        isPolygon: function() {
            return this.value == GLOBALS.conf.thematicMap;
        },
        isPoint: function() {
            return this.value == GLOBALS.conf.thematicMap2;
        },
        isAssignment: function() {
            return this.value == GLOBALS.conf.organisationUnitAssignment;
        }
    },
    
    mask: null,
    
    exportValues: null,
    
    topLevelUnit: null,
    
    locateFeatureWindow: null,
    
    selectFeatureWindow: null
};