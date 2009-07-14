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
 * @requires core/GeoStat/Choropleth.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

     
/**
 * Class: mapfish.widgets.geostat.Choropleth
 * Use this class to create a widget allowing to display choropleths
 * on the map.
 *
 * Inherits from:
 * - {Ext.FormPanel}
 */

mapfish.widgets.geostat.Choropleth = Ext.extend(Ext.FormPanel, {

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
     * {String} (read-only) The feature attribute currently chosen
     *     Useful if callbacks are registered on 'featureselected'
     *     and 'featureunselected' events
     */
    indicator: null,

    /**
     * APIProperty: indicatorText
     * {String} (read-only) The raw value of the currently chosen indicator
     *     (ie. human readable)
     *     Useful if callbacks are registered on 'featureselected'
     *     and 'featureunselected' events
     */
    indicatorText: null,

    /**
     * Property: coreComp
     * {<mapfish.GeoStat.ProportionalSymbol>} The core component object.
     */
    coreComp: null,

    /**
     * Property: classificationApplied
     * {Boolean} true if the classify was applied
     */
    classificationApplied: false,

    /**
     * Property: ready
     * {Boolean} true if the widget is ready to accept user commands.
     */
    ready: false,

    /**
     * Property: border
     *     Styling border
     */
    border: false,

    /**
     * APIProperty: loadMask
     *     An Ext.LoadMask config or true to mask the widget while loading (defaults to false).
     */
    loadMask : false,

    /**
     * APIProperty: labelGenerator
     *     Generator for bin labels
     */
    labelGenerator: null,

    /**
     * Constructor: mapfish.widgets.geostat.Choropleth
     *
     * Parameters:
     * config - {Object} Config object.
     */

    /**
     * Method: initComponent
     *    Inits the component
     */
     
    newUrl : false,
    
    initComponent : function() {
    
        mapViewStore = new Ext.data.JsonStore({
            url: path + 'getAllMapViews' + type,
            root: 'mapViews',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function()
                    {
                        if (URLACTIVE)
                        {
                            Ext.Ajax.request(
                            {
                                url: path + 'getMapView' + type,
                                method: 'POST',
                                params: { id: PARAMETER },

                                success: function( responseObject )
                                {
                                    MAPVIEWACTIVE = true;
                                    MAPVIEW = Ext.util.JSON.decode(responseObject.responseText).mapView[0];
                                    
                                    MAPSOURCE = MAPVIEW.mapSourceType;
                                    
                                    Ext.getCmp('mapview_cb').setValue(MAPVIEW.id);
                                    
                                    Ext.getCmp('numClasses').setValue(MAPVIEW.classes);
                                    Ext.getCmp('colorA_cf').setValue(MAPVIEW.colorLow);
                                    Ext.getCmp('colorB_cf').setValue(MAPVIEW.colorHigh);

                                    Ext.getCmp('indicatorgroup_cb').setValue(MAPVIEW.indicatorGroupId);
                                    
                                    var igId = MAPVIEW.indicatorGroupId;
                                    indicatorStore.baseParams = { indicatorGroupId: igId, format: 'json' };
                                    indicatorStore.reload();
                                },
                                failure: function()
                                {
                                  alert( 'Status', 'Error while retrieving data' );
                                } 
                            });
                        }
                    },
                    scope: this
                }
            }
        });
    
        indicatorGroupStore = new Ext.data.JsonStore({
            url: path + 'getAllIndicatorGroups' + type,
            baseParams: { format: 'json' },
            root: 'indicatorGroups',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
        
        indicatorStore = new Ext.data.JsonStore({
            url: path + 'getIndicatorsByIndicatorGroup' + type,
            baseParams: { indicatorGroupId: 0 },
            root: 'indicators',
            fields: ['id', 'name', 'shortName'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        indicatorStore.each( function fn(record) {
                                var name = record.get('name');
                                name = name.replace('&lt;', '<').replace('&gt;', '>');
                                record.set('name', name);
                            },  this
                        );
                        
                        Ext.getCmp('indicator_cb').reset();

                        if (MAPVIEWACTIVE) {
                            Ext.getCmp('indicator_cb').setValue(MAPVIEW.indicatorId);
                            
                            var name = MAPVIEW.periodTypeId;
                            Ext.getCmp('periodtype_cb').setValue(name);
                            
                            periodStore.baseParams = { name: name, format: 'json' };
                            periodStore.reload();
                        }
                    },
                    scope: this
                }
            }
        });
        
        periodTypeStore = new Ext.data.JsonStore({
            url: path + 'getAllPeriodTypes' + type,
            root: 'periodTypes',
            fields: ['name'],
            autoLoad: true
        });
            
        periodStore = new Ext.data.JsonStore({
            url: path + 'getPeriodsByPeriodType' + type,
            baseParams: { name: 0 },
            root: 'periods',
            fields: ['id', 'name'],
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        if (MAPVIEWACTIVE) {
                            Ext.getCmp('period_cb').setValue(MAPVIEW.periodId);
                            
                            var mst = MAPVIEW.mapSourceType;
                            
                            Ext.Ajax.request(
                            {
                                url: path + 'setMapSourceTypeUserSetting' + type,
                                method: 'POST',
                                params: { mapSourceType: mst },

                                success: function( responseObject )
                                {
                                    Ext.getCmp('map_cb').getStore().reload();
                                    Ext.getCmp('maps_cb').getStore().reload();
                                    
                                    Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                                },
                                failure: function()
                                {
                                    alert( 'Error: setMapSourceTypeUserSetting' );
                                }
                            });
                            
                            this.newUrl = MAPVIEW.mapSource;
                            choropleth.classify(false);
                        }
                    },
                    scope: this
                }
            }
        });
            
        mapStore = new Ext.data.JsonStore({
            url: path + 'getAllMaps' + type,
            baseParams: { format: 'jsonmin' },
            root: 'maps',
            fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel'],
            sortInfo: { field: 'organisationUnitLevel', direction: 'ASC' },
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
                        if (MAPVIEWACTIVE) {
                            Ext.getCmp('map_cb').setValue(MAPVIEW.mapSource);
                            MAPVIEWACTIVE = false;
                            choropleth.classify(false);
                        }
                    }
                }
            }
        });
        
        legendStore = new Ext.data.JsonStore({
            url: path + 'getMapLegendSet' + type,
            baseParams: { format: 'json' },
            root: 'mapLegendSet',
            fields: ['id', 'name'],
            autoLoad: false
        });
        
        this.items = [
         
        {
            xtype: 'combo',
            id: 'mapview_cb',
            fieldLabel: 'Map view',
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Optional',
            selectOnFocus: true,
            width: combo_width,
            store: mapViewStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        var mId = Ext.getCmp('mapview_cb').getValue();
                        
                        Ext.Ajax.request(
                        {
                            url: path + 'getMapView' + type,
                            method: 'POST',
                            params: { id: mId },

                            success: function( responseObject )
                            {
                                MAPVIEWACTIVE = true;
                                MAPVIEW = Ext.util.JSON.decode(responseObject.responseText).mapView[0];
                                
                                MAPSOURCE = MAPVIEW.mapSourceType;
                                
                                Ext.getCmp('numClasses').setValue(MAPVIEW.classes);
                                Ext.getCmp('colorA_cf').setValue(MAPVIEW.colorLow);
                                Ext.getCmp('colorB_cf').setValue(MAPVIEW.colorHigh);
                                Ext.getCmp('indicatorgroup_cb').setValue(MAPVIEW.indicatorGroupId);
                                
                                var igId = Ext.getCmp('indicatorgroup_cb').getValue();
                                indicatorStore.baseParams = { indicatorGroupId: igId, format: 'json' };
                                indicatorStore.reload();
                            },
                            failure: function()
                            {
                              alert( 'Status', 'Error while retrieving data' );
                            } 
                        });
                    },
                    scope: this
                }
            }
        },
        
        { html: '<br>' },
        
        {
            xtype: 'combo',
            id: 'indicatorgroup_cb',
            fieldLabel: 'Indicator group',
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Required',
            selectOnFocus: true,
            width: combo_width,
            store: indicatorGroupStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').reset();
                        }
                        
                        Ext.getCmp('indicator_cb').reset();
                        var igId = Ext.getCmp('indicatorgroup_cb').getValue();
                        indicatorStore.baseParams = { indicatorGroupId: igId, format: 'json' };
                        indicatorStore.reload();
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'indicator_cb',
            fieldLabel: 'Indicator',
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Required',
            selectOnFocus: true,
            width: combo_width,
            store: indicatorStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').reset();
                        }
 
                        var iId = Ext.getCmp('indicator_cb').getValue();
                        
                        Ext.Ajax.request(
                        {
                            url: path + 'getMapLegendSet' + type,
                            method: 'POST',
                            params: { indicatorId: iId, format: 'json' },

                            success: function( responseObject )
                            {
                                var data = Ext.util.JSON.decode(responseObject.responseText);
                                
                                if (data.mapLegendSet[0].id != '')
                                {
//                                    Ext.getCmp('method').setValue(data.mapLegendSet[0].method);
                                    Ext.getCmp('numClasses').setValue(data.mapLegendSet[0].classes);

                                    Ext.getCmp('colorA_cf').setValue(data.mapLegendSet[0].colorLow);
                                    Ext.getCmp('colorB_cf').setValue(data.mapLegendSet[0].colorHigh);
                                }
                                
                                choropleth.classify(false);
                            },
                            failure: function()
                            {
                              alert( 'Status', 'Error while retrieving data' );
                            } 
                        });
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'periodtype_cb',
            fieldLabel: 'Period type',
            typeAhead: true,
            editable: false,
            valueField: 'name',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Required',
            selectOnFocus: true,
            width: combo_width,
            store: periodTypeStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').reset();
                        }
                        
                        var pt = Ext.getCmp('periodtype_cb').getValue();
                        Ext.getCmp('period_cb').getStore().baseParams = { name: pt, format: 'json' };
                        Ext.getCmp('period_cb').getStore().reload();
                    },
                    scope: this
                }
            }
        },

        {
            xtype: 'combo',
            id: 'period_cb',
            fieldLabel: 'Period',
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Required',
            selectOnFocus: true,
            width: combo_width,
            store: periodStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').reset();
                        }
                        
                        this.classify(false);
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'map_cb',
            fieldLabel: 'Map',
            typeAhead: true,
            editable: false,
            valueField: 'mapLayerPath',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Required',
            selectOnFocus: true,
            width: combo_width,
            store: mapStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').reset();
                        }
                        
                        this.newUrl = Ext.getCmp('map_cb').getValue();
                        this.classify(false);
                    },
                    scope: this
                }
            }
        },
        
        { html: '<br>' },

        {
            xtype: 'combo',
            fieldLabel: 'Method',
            id: 'method',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: 'Required',
            value: 1,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [[2, 'Distributed values'], [1, 'Equal intervals'], [0, 'Fixed bounds']]
            }),
            listeners: {
                'select': {
                    fn: function()
                    {
                        if (Ext.getCmp('method').getValue() == 0)
                        {
                            Ext.getCmp('bounds').show();
                            Ext.getCmp('numClasses').hide();
                        }
                        else
                        {
                            Ext.getCmp('bounds').hide();
                            Ext.getCmp('numClasses').show();
                            
                            this.classify(false);
                        }
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'bounds',
            fieldLabel: 'Bounds',
            emptyText: 'Comma separated values',
            isFormField: true,
            width: combo_width,
            hidden: true
        },
        
        {
            xtype: 'combo',
            fieldLabel: 'Classes',
            id: 'numClasses',
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: 5,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value'],
                data: [[1], [2], [3], [4], [5], [6], [7], [8]]
            }),
            listeners: {
                'select': {
                    fn: function()
                    {
                        if (Ext.getCmp('mapview_cb').getValue() != '') {
                            Ext.getCmp('mapview_cb').reset();
                        }
                        
                        this.classify(false);
                    },
                    scope: this
                }
            }
        },
/*        
        {
            xtype: 'combo',
            id: 'maplegend_cb',
            fieldLabel: 'Legend set',
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: 'Optional',
            selectOnFocus: true,
            width: combo_width,
            store: legendStore,
            listeners: {
                'select': {
                    fn: function()
                    {
                        var iId = Ext.getCmp('indicator_cb').getValue();
                       
                        Ext.Ajax.request(
                        {
                            url: path + 'getMapLegendSet' + type,
                            method: 'GET',
                            params: { indicatorId: iId, format: 'json' },

                            success: function( responseObject )
                            {
                              var data = Ext.util.JSON.decode(responseObject.responseText);
                              var color1 = data.mapLegendSet[0]['colorLow'];
                              var color2 = data.mapLegendSet[0]['colorHigh'];

                              Ext.getCmp('colorA_cf').setValue(color1);
                              Ext.getCmp('colorB_cf').setValue(color2);
                            },
                            failure: function()
                            {
                              alert( 'Status', 'Error while retrieving data' );
                            } 
                        });
                        
                        this.classify(false);
                    },
                    scope: this
                }
            }

        },
 */
        {
            xtype: 'colorfield',
            fieldLabel: 'Low color',
            id: 'colorA_cf',
            allowBlank: false,
            isFormField: true,
            width: combo_width,
            value: "#FFFF00"
        },
        
        {
            xtype: 'colorfield',
            fieldLabel: 'High color',
            id: 'colorB_cf',
            allowBlank: false,
            isFormField: true,
            width: combo_width,
            value: "#FF0000"
        },
        
        { html: '<br>' },

        {
            xtype: 'button',
            isFormField: true,
            fieldLabel: '',
            labelStyle: 'color:#dfe8f6;',
            text: 'Refresh',
            handler: function()
            {
                this.layer.setVisibility(true);
                this.classify(true);
            },
            scope: this
        }

        ];

        mapfish.widgets.geostat.Choropleth.superclass.initComponent.apply(this);
    },
    
    setUrl: function(url) {
    
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    /**
     * Method: requestSuccess
     *      Calls onReady callback function and mark the widget as ready.
     *      Called on Ajax request success.
     */
    requestSuccess: function(request) {
        this.ready = true;

        // if widget is rendered, hide the optional mask
        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
    },

    /**
     * Method: requestFailure
     *      Displays an error message on the console.
     *      Called on Ajax request failure.
     */
    requestFailure: function(request) {
        OpenLayers.Console.error('Ajax request failed');
    },
    
    /**
     * Method: getColors
     *    Retrieves the colors from form elements
     *
     * Returns:
     * {Array(<mapfish.Color>)} an array of two colors (start, end)
     */
    getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(Ext.getCmp('colorA_cf').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf').getValue());
        return [colorA, colorB];
    },

    /**
     * Method: classify
     *
     * Parameters:
     * exception - {Boolean} If true show a message box to user if either
     *      the widget isn't ready, or no indicator is specified, or no
     *      method is specified.
     */
    classify: function(exception)
    {
        if (!this.ready) {
            Ext.MessageBox.alert('Error', 'Component init not complete');
            return;
        }
        
        if (this.newUrl)
        {
            URL = this.newUrl;
            this.newUrl = false;
            
            if (MAPSOURCE == MAP_SOURCE_TYPE_DATABASE)
            {
                if (URL == 4) {
                    this.setUrl(path + 'getPointShapefile.action?level=' + URL);
                }
                else {
                    this.setUrl(path + 'getPolygonShapefile.action?level=' + URL);
                }
            }
            else {
                this.setUrl('geojson/' + URL);
            }
        }
                
        if (!Ext.getCmp('indicator_cb').getValue() ||
            !Ext.getCmp('period_cb').getValue() ||
            !Ext.getCmp('map_cb').getValue()) {
                if (exception) {
                    Ext.messageRed.msg('Thematic map', 'Form is not complete.');
                }
                return;
        }

        mask.show();

        loadMapData('choropleth');
    },

    /**
     * Method: onRender
     * Called by EXT when the component is rendered.
     */
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Choropleth.superclass.onRender.apply(this, arguments);
        if(this.loadMask){
            this.loadMask = new Ext.LoadMask(this.bwrap,
                    this.loadMask);
            this.loadMask.show();
        }

        var coreOptions = {
            'layer': this.layer,
            'format': this.format,
            'url': this.url,
            'requestSuccess': this.requestSuccess.createDelegate(this),
            'requestFailure': this.requestFailure.createDelegate(this),
            'featureSelection': this.featureSelection,
            'nameAttribute': this.nameAttribute,
            'legendDiv': this.legendDiv,
            'labelGenerator': this.labelGenerator
        };

        this.coreComp = new mapfish.GeoStat.Choropleth(this.map, coreOptions);
    }   
});

Ext.reg('choropleth', mapfish.widgets.geostat.Choropleth);