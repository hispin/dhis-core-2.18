var GLOBALS = {};

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
            json += i < mapValues.length - 1 ? '},' : '}'
        }
        json += ']';
        json += '}';
        return json
    },

    getLegendsJSON: function() {
        var widget = ACTIVEPANEL == thematicMap ? choropleth : proportionalSymbol;
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
    }
};