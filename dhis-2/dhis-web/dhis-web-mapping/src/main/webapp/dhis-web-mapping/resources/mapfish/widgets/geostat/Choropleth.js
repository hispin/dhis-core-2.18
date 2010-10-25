/*
 * Copyright (C) 2007-2008  Camptocamp|
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

mapfish.widgets.geostat.Choropleth = Ext.extend(Ext.FormPanel, {

    layer: null,

    format: null,

    url: null,

    featureSelection: true,

    nameAttribute: null,

    indicator: null,

    indicatorText: null,

    coreComp: null,

    classificationApplied: false,

    ready: false,

    border: false,

    loadMask: false,

    labelGenerator: null,

    colorInterpolation: false,

    newUrl: false,

    legend: false,

	imageLegend: false,

	bounds: false,

    parentId: false,

    mapView: false,

    mapData: false,
    
    labels: false,
    
    valueType: false,
    
    selectFeatures: false,
    
    initComponent: function() {
    
        this.legend = {
            type: GLOBALS.conf.map_legend_type_automatic,
            method: GLOBALS.conf.classify_by_equal_intervals,
            classes: 5
        };
        
        this.valueType = GLOBALS.conf.map_value_type_indicator;
        
        this.createItems();
        
        this.createSelectFeatures();
        
        if (PARAMETER) {
            this.mapView = PARAMETER.mapView;
            this.legend = {
                type: this.mapView.mapLegendType,
                method: this.mapView.method || this.legend.method,
                classes: this.mapView.classes || this.legend.classes
            };
            
            PARAMETER = false;        
            MAP.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
                    
            Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
            Ext.getCmp('mapdatetype_cb').setValue(MAPDATETYPE);
            
            function mapViewStoreCallback() {
                Ext.getCmp('mapview_cb').setValue(this.mapView.id);
                this.valueType = this.mapView.mapValueType;
                Ext.getCmp('mapvaluetype_cb').setValue(this.valueType);

                this.setMapView();
            }
            
            if (GLOBALS.stores.mapView.isLoaded) {
                mapViewStoreCallback.call(this);
            }                    
            else {
                GLOBALS.stores.mapView.load({scope: this, callback: function() {
                    mapViewStoreCallback.call(this);
                }});
            }
        }
        
		mapfish.widgets.geostat.Choropleth.superclass.initComponent.apply(this);
    },
    
    setUrl: function(url) {
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    requestSuccess: function(request) {
        this.ready = true;

        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
    },

    requestFailure: function(request) {
        OpenLayers.Console.error(i18n_ajax_request_failed);
    },
    
    getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(Ext.getCmp('colorA_cf').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf').getValue());
        return [colorA, colorB];
    },
    
    createItems: function() {
        this.items = [
        {
            xtype: 'combo',
            id: 'mapview_cb',
            fieldLabel: i18n_favorite,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: i18n_optional,
            selectOnFocus: true,
			labelSeparator: GLOBALS.conf.labelseparator,
            width: GLOBALS.conf.combo_width,
            store: GLOBALS.stores.mapView,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        this.mapView = GLOBALS.stores.mapView.getAt(GLOBALS.stores.mapView.find('id', cb.getValue())).data;
                        
                        this.legend.type = this.mapView.mapLegendType;
                        this.legend.method = this.mapView.method || this.legend.method;
                        this.legend.classes = this.mapView.classes || this.legend.classes;

                        MAP.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);

                        Ext.getCmp('mapdatetype_cb').setValue(MAPDATETYPE);
                        Ext.getCmp('mapview_cb').setValue(this.mapView.id);

                        this.valueType = this.mapView.mapValueType;
                        Ext.getCmp('mapvaluetype_cb').setValue(this.valueType);

                        this.setMapView();
                    }
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
			id: 'mapvaluetype_cb',
            fieldLabel: i18n_mapvaluetype,
			labelSeparator: GLOBALS.conf.labelseparator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all',
            width: GLOBALS.conf.combo_width,
			value: GLOBALS.conf.map_value_type_indicator,
            store: new Ext.data.ArrayStore({
                fields: ['id', 'name'],
                data: [
                    [GLOBALS.conf.map_value_type_indicator, 'Indicators'],
                    [GLOBALS.conf.map_value_type_dataelement, 'Data elements']
                ]
            }),
			listeners: {
				'select': {
                    scope: this,
					fn: function(cb) {
                        this.valueType = cb.getValue();
                        this.prepareMapViewValueType();
                        this.classify(false, true);
					}
				}
			}
		},
        
        {
            xtype: 'combo',
            id: 'indicatorgroup_cb',
            fieldLabel: i18n_indicator_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.conf.combo_width,
            store: GLOBALS.stores.indicatorGroup,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb').clearValue();						
						Ext.getCmp('indicator_cb').clearValue();
                        GLOBALS.stores.indicatorsByGroup.setBaseParam('indicatorGroupId', cb.getValue());
                        GLOBALS.stores.indicatorsByGroup.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'indicator_cb',
            fieldLabel: i18n_indicator ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.conf.combo_width,
            store: GLOBALS.stores.indicatorsByGroup,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb').clearValue();
 
                        Ext.Ajax.request({
                            url: GLOBALS.conf.path_mapping + 'getMapLegendSetByIndicator' + GLOBALS.conf.type,
                            method: 'POST',
                            params: {indicatorId: cb.getValue()},
                            scope: this,
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    this.legend.type = GLOBALS.conf.map_legend_type_predefined;
                                    this.prepareMapViewLegend();
                                    
                                    function load() {
                                        Ext.getCmp('maplegendset_cb').setValue(mapLegendSet.id);
                                        this.applyPredefinedLegend();
                                    }
                                    
                                    if (!GLOBALS.stores.predefinedMapLegendSet.isLoaded) {
                                        GLOBALS.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                                            load.call(this);
                                        }});
                                    }
                                    else {
                                        load.call(this);
                                    }
                                }
                                else {
                                    this.legend.type = GLOBALS.conf.map_legend_type_automatic;
                                    this.prepareMapViewLegend();
                                    this.classify(false, true);
                                }
                            }
                        });
                    }
                }
            }
        },
		
		{
            xtype: 'combo',
            id: 'dataelementgroup_cb',
            fieldLabel: i18n_dataelement_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.conf.combo_width,
            store: GLOBALS.stores.dataElementGroup,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb').clearValue();
                        Ext.getCmp('dataelement_cb').clearValue();
						GLOBALS.stores.dataElementsByGroup.setBaseParam('dataElementGroupId', cb.getValue());
                        GLOBALS.stores.dataElementsByGroup.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'dataelement_cb',
            fieldLabel: i18n_dataelement,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.conf.combo_width,
            store: GLOBALS.stores.dataElementsByGroup,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb').clearValue();
 
                        Ext.Ajax.request({
                            url: GLOBALS.conf.path_mapping + 'getMapLegendSetByDataElement' + GLOBALS.conf.type,
                            method: 'POST',
                            params: {dataElementId: cb.getValue()},
                            scope: this,
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    this.legend.type = GLOBALS.conf.map_legend_type_predefined;
                                    this.prepareMapViewLegend();
                                    
                                    function load() {
                                        Ext.getCmp('maplegendset_cb').setValue(mapLegendSet.id);
                                        this.applyPredefinedLegend();
                                    }
                                    
                                    if (!GLOBALS.stores.predefinedMapLegendSet.isLoaded) {
                                        GLOBALS.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                                            load.call(this);
                                        }});
                                    }
                                    else {
                                        load.call(this);
                                    }
                                }
                                else {
                                    this.legend.type = GLOBALS.conf.map_legend_type_automatic;
                                    this.prepareMapViewLegend();
                                    this.classify(false, true);
                                }
                            }
                        });
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'periodtype_cb',
            fieldLabel: i18n_period_type,
            typeAhead: true,
            editable: false,
            valueField: 'name',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.conf.combo_width,
            store: GLOBALS.stores.periodType,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb').clearValue();                        
                        Ext.getCmp('period_cb').clearValue();
                        GLOBALS.stores.periodsByTypeStore.setBaseParam('name', cb.getValue());
                        GLOBALS.stores.periodsByTypeStore.load();
                    }
                }
            }
        },

        {
            xtype: 'combo',
            id: 'period_cb',
            fieldLabel: i18n_period ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.conf.combo_width,
            store: GLOBALS.stores.periodsByTypeStore,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        Ext.getCmp('mapview_cb').clearValue();
                        this.classify(false, true);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            id: 'startdate_df',
            fieldLabel: i18n_start_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            width: GLOBALS.conf.combo_width,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(df, date) {
                        Ext.getCmp('mapview_cb').clearValue();
                        Ext.getCmp('enddate_df').setMinValue(date);
                        this.classify(false, true);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            id: 'enddate_df',
            fieldLabel: i18n_end_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            width: GLOBALS.conf.combo_width,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(df, date) {
                        Ext.getCmp('mapview_cb').clearValue();
                        Ext.getCmp('startdate_df').setMaxValue(date);
                        this.classify(false, true);
                    }
                }
            }
        },                        
        
        {
            xtype: 'combo',
            id: 'map_cb',
            fieldLabel: i18n_map ,
            typeAhead: true,
            editable: false,
            valueField: 'mapLayerPath',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.conf.combo_width,
            store: GLOBALS.stores.map,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb').clearValue();
                        
                        if (cb.getValue() != this.newUrl) {
                            this.loadFromFile(cb.getValue());
                        }
                    }
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'map_tf',
            fieldLabel: i18n_parent_orgunit,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBALS.conf.combo_width,
            listeners: {
                'focus': {
                    scope: this,
                    fn: function(tf) {
                        function showTree() {
                            var value, rawvalue;
                            var w = new Ext.Window({
                                id: 'orgunit_w',
                                title: 'Select parent organisation unit',
                                closeAction: 'hide',
                                autoScroll: true,
                                width: 280,
                                autoHeight: true,
                                height: 'auto',
                                boxMaxHeight: 500,
                                items: [
                                    {
                                        xtype: 'treepanel',
                                        id: 'orgunit_tp',
                                        bodyStyle: 'padding:7px',
                                        height: GLOBALS.util.getMultiSelectHeight(),
                                        autoScroll: true,
                                        loader: new Ext.tree.TreeLoader({
                                            dataUrl: GLOBALS.conf.path_mapping + 'getOrganisationUnitChildren' + GLOBALS.conf.type
                                        }),
                                        root: {
                                            id: TOPLEVELUNIT.id,
                                            text: TOPLEVELUNIT.name,
                                            hasChildrenWithCoordinates: TOPLEVELUNIT.hasChildrenWithCoordinates,
                                            nodeType: 'async',
                                            draggable: false,
                                            expanded: true
                                        },
                                        listeners: {
                                            'click': {
                                                fn: function(n) {
                                                    if (n.hasChildNodes()) {
                                                        tf.setValue(n.attributes.text);
                                                        tf.value = n.attributes.id;
                                                        tf.node = n;
                                                    }
                                                }
                                            },
                                            'expandnode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w').syncSize();
                                                }
                                            },
                                            'collapsenode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w').syncSize();
                                                }
                                            }
                                        }
                                    },
                                    {
                                        xtype: 'panel',
                                        layout: 'table',
                                        items: [
                                            {
                                                xtype: 'button',
                                                text: 'Select',
                                                width: 133,
                                                scope: this,
                                                handler: function() {
                                                    if (tf.getValue() && tf.getValue() != this.parentId) {
                                                        this.loadFromDatabase(tf.value);
                                                    }
                                                    Ext.getCmp('orgunit_w').hide();
                                                }
                                            },
                                            {
                                                xtype: 'button',
                                                text: 'Cancel',
                                                width: 133,
                                                handler: function() {
                                                    Ext.getCmp('orgunit_w').hide();
                                                }
                                            }
                                        ]
                                    }
                                ]
                            });
                            
                            var x = Ext.getCmp('center').x + 15;
                            var y = Ext.getCmp('center').y + 41;
                            w.setPosition(x,y);
                            w.show();
                        }

                        if (TOPLEVELUNIT) {
                            showTree.call(this);
                        }
                        else {
                            Ext.Ajax.request({
                                url: GLOBALS.conf.path_commons + 'getOrganisationUnits' + GLOBALS.conf.type,
                                params: {level: 1},
                                method: 'POST',
                                scope: this,
                                success: function(r) {
                                    var rootNode = Ext.util.JSON.decode(r.responseText).organisationUnits[0];
                                    TOPLEVELUNIT = {
                                        id: rootNode.id,
                                        name: rootNode.name,
                                        hasChildrenWithCoordinates: rootNode.hasChildrenWithCoordinates
                                    };
                                    showTree.call(this);
                                }
                            });
                        }
                    }
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
            id: 'maplegendtype_cb',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            fieldLabel: i18n_legend_type,
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            value: this.legend.type,
            triggerAction: 'all',
            width: GLOBALS.conf.combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[GLOBALS.conf.map_legend_type_automatic, i18n_automatic],
					[GLOBALS.conf.map_legend_type_predefined, i18n_predefined]
				]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (cb.getValue() == GLOBALS.conf.map_legend_type_predefined && cb.getValue() != this.legend.type) {
							this.legend.type = GLOBALS.conf.map_legend_type_predefined;
                            this.prepareMapViewLegend();
							
							if (Ext.getCmp('maplegendset_cb').getValue()) {
                                this.applyPredefinedLegend();
							}
                        }
                        else if (cb.getValue() == GLOBALS.conf.map_legend_type_automatic && cb.getValue() != this.legend.type) {
							this.legend.type = GLOBALS.conf.map_legend_type_automatic;
							this.prepareMapViewLegend();                            
                            this.classify(false, true);
                        }
                    }
                }
            }
        },
		
		{
            xtype: 'combo',
            id: 'maplegendset_cb',
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            fieldLabel: i18n_legend_set,
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            triggerAction: 'all',
            width: GLOBALS.conf.combo_width,
			hidden: true,
            store: GLOBALS.stores.predefinedMapLegendSet,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
						this.applyPredefinedLegend();
                    }
                }
            }
        },

        {
            xtype: 'combo',
            fieldLabel: i18n_method,
            id: 'method_cb',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: GLOBALS.conf.emptytext,
			labelSeparator: GLOBALS.conf.labelseparator,
            value: this.legend.method,
            triggerAction: 'all',
            width: GLOBALS.conf.combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[2, i18n_equal_intervals],
					[3, i18n_equal_group_count],
					[1, i18n_fixed_breaks]
				]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (cb.getValue() == GLOBALS.conf.classify_with_bounds && cb.getValue() != this.legend.method) {
							this.legend.method = GLOBALS.conf.classify_with_bounds;
                            this.prepareMapViewLegend();
                        }
                        else if (cb.getValue() != this.legend.method) {
							this.legend.method = cb.getValue();
                            this.prepareMapViewLegend();
                            this.classify(false, true);
                        }
                    }
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'bounds_tf',
            fieldLabel: i18n_bounds,
			labelSeparator: GLOBALS.conf.labelseparator,
            emptyText: i18n_comma_separated_values,
            isFormField: true,
            width: GLOBALS.conf.combo_width,
            hidden: true
        },
        
        {
            xtype: 'combo',
            id: 'numClasses_cb',
            fieldLabel: i18n_classes,
			labelSeparator: GLOBALS.conf.labelseparator,
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: this.legend.classes,
            triggerAction: 'all',
            width: GLOBALS.conf.combo_width,
            store: new Ext.data.ArrayStore({
                fields: ['value'],
                data: [[1], [2], [3], [4], [5], [6], [7]]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        Ext.getCmp('mapview_cb').clearValue();
						
						if (cb.getValue() != this.legend.classes) {
							this.legend.classes = cb.getValue();
							this.classify(false, true);
						}
                    }
                }
            }
        },

        {
            xtype: 'colorfield',
            id: 'colorA_cf',
            fieldLabel: i18n_low_color,
			labelSeparator: GLOBALS.conf.labelseparator,
            allowBlank: false,
            isFormField: true,
            width: GLOBALS.conf.combo_width,
            value: "#FFFF00"
        },
        
        {
            xtype: 'colorfield',
            id: 'colorB_cf',
            fieldLabel: i18n_high_color,
			labelSeparator: GLOBALS.conf.labelseparator,
            allowBlank: false,
            isFormField: true,
            width: GLOBALS.conf.combo_width,
            value: "#FF0000"
        },
        
        { html: '<br>' },

        {
            xtype: 'button',
            text: i18n_refresh,
			cls: 'aa_med',
            isFormField: true,
            fieldLabel: '',
            labelSeparator: '',
            scope: this,
            handler: function() {
                if (this.validateForm()) {
                    this.layer.setVisibility(true);
                    this.classify(true, true);
                }
                else {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
            }
        }

        ];
    },
    
    createSelectFeatures: function() {
        var onHoverSelect = function onHoverSelect(feature) {
            if (ACTIVEPANEL == GLOBALS.conf.thematicMap) {
                Ext.getCmp('featureinfo_l').setText('<div style="color:black">' + feature.attributes[choropleth.mapData.nameColumn] + '</div><div style="color:#555">' + feature.attributes.value + '</div>', false);
            }
            else if (ACTIVEPANEL == GLOBALS.conf.organisationUnitAssignment) {
                Ext.getCmp('featureinfo_l').setText('<div style="color:black">' + feature.attributes[mapping.mapData.nameColumn] + '</div>', false);
            }
        };
        
        var onHoverUnselect = function onHoverUnselect(feature) {
            Ext.getCmp('featureinfo_l').setText('<span style="color:#666">' + i18n_no_feature_selected + '</span>', false);
        };
        
        var onClickSelect = function onClickSelect(feature) {
            var east_panel = Ext.getCmp('east');
            var x = east_panel.x - 210;
            var y = east_panel.y + 41;
            
            if (ACTIVEPANEL == GLOBALS.conf.thematicMap && MAPSOURCE == GLOBALS.conf.map_source_type_database) {
                if (feature.attributes.hasChildrenWithCoordinates) {
                    if (lfw) {
                        lfw.destroy();
                    }
                    
                    Ext.getCmp('map_tf').setValue(feature.data.name);
                    Ext.getCmp('map_tf').value = feature.attributes.id;
                    choropleth.loadFromDatabase(feature.attributes.id, true);
                }
                else {
                    Ext.message.msg(false, i18n_no_coordinates_found);
                }
            }
            
            if (ACTIVEPANEL == GLOBALS.conf.organisationUnitAssignment && MAPSOURCE != GLOBALS.conf.map_source_type_database) {
                if (selectFeaturePopup) {
                    selectFeaturePopup.destroy();
                }
                
                var popup = new Ext.Window({
                    title: '<span class="panel-title">Assign organisation unit</span>',
                    width: 180,
                    height: 65,
                    layout: 'fit',
                    plain: true,
                    html: '<div class="window-orgunit-text">' + feature.attributes[mapping.mapData.nameColumn] + '</div>',
                    x: x,
                    y: y,
                    listeners: {
                        'close': {
                            fn: function() {
                                mapping.relation = false;
                            }
                        }
                    }
                });
                
                selectFeaturePopup = popup;		
                popup.show();
                mapping.relation = feature.attributes[mapping.mapData.nameColumn];
            }
        };
        
        this.selectFeatures = new OpenLayers.Control.newSelectFeature(
            this.layer, {
                onHoverSelect: onHoverSelect,
                onHoverUnselect: onHoverUnselect,
                onClickSelect: onClickSelect,
            }
        );
        
        MAP.addControl(this.selectFeatures);
        this.selectFeatures.activate();
    },
    
    prepareMapViewValueType: function() {
        var obj = {};

        if (this.valueType == GLOBALS.conf.map_value_type_indicator) {
            Ext.getCmp('indicatorgroup_cb').showField();
            Ext.getCmp('indicator_cb').showField();
            Ext.getCmp('dataelementgroup_cb').hideField();
            Ext.getCmp('dataelement_cb').hideField();
            obj.components = {
                valueTypeGroup: Ext.getCmp('indicatorgroup_cb'),
                valueType: Ext.getCmp('indicator_cb')
            };
            obj.stores = {
                valueTypeGroup: GLOBALS.stores.indicatorGroup,
                valueType: GLOBALS.stores.indicatorsByGroup
            };
            obj.mapView = {
                valueTypeGroup: 'indicatorGroupId',
                valueType: 'indicatorId'
            };
        }
        else if (this.valueType == GLOBALS.conf.map_value_type_dataelement) {
            Ext.getCmp('indicatorgroup_cb').hideField();
            Ext.getCmp('indicator_cb').hideField();
            Ext.getCmp('dataelementgroup_cb').showField();
            Ext.getCmp('dataelement_cb').showField();
            obj.components = {
                valueTypeGroup: Ext.getCmp('dataelementgroup_cb'),
                valueType: Ext.getCmp('dataelement_cb')
            };
            obj.stores = {
                valueTypeGroup: GLOBALS.stores.dataElementGroup,
                valueType: GLOBALS.stores.dataElementsByGroup
            };
            obj.mapView = {
                valueTypeGroup: 'dataElementGroupId',
                valueType: 'dataElementId'
            };
        }
        return obj;
    },
    
    prepareMapViewDateType: function() {
        var obj = {};
        if (MAPDATETYPE == GLOBALS.conf.map_date_type_fixed) {
            Ext.getCmp('periodtype_cb').showField();
            Ext.getCmp('period_cb').showField();
            Ext.getCmp('startdate_df').hideField();
            Ext.getCmp('enddate_df').hideField();
            obj.components = {
                c1: Ext.getCmp('periodtype_cb'),
                c2: Ext.getCmp('period_cb')
            };
            obj.stores = {
                c1: GLOBALS.stores.periodType,
                c2: GLOBALS.stores.periodsByTypeStore
            };
            obj.mapView = {
                c1: 'periodTypeId',
                c2: 'periodId'
            };
        }
        else if (MAPDATETYPE == GLOBALS.conf.map_date_type_start_end) {
            Ext.getCmp('periodtype_cb').hideField();
            Ext.getCmp('period_cb').hideField();
            Ext.getCmp('startdate_df').showField();
            Ext.getCmp('enddate_df').showField();
            obj.components = {
                c1: Ext.getCmp('startdate_df'),
                c2: Ext.getCmp('enddate_df')
            };
            obj.mapView = {
                c1: 'startDate',
                c2: 'endDate'
            };
        }
        return obj;
    },
    
    prepareMapViewLegend: function() {
        Ext.getCmp('maplegendtype_cb').setValue(this.legend.type);
        
        if (this.legend.type == GLOBALS.conf.map_legend_type_automatic) {
            Ext.getCmp('method_cb').showField();
            Ext.getCmp('colorA_cf').showField();
            Ext.getCmp('colorB_cf').showField();
            Ext.getCmp('maplegendset_cb').hideField();
            
            if (this.legend.method == GLOBALS.conf.classify_with_bounds) {
                Ext.getCmp('numClasses_cb').hideField();
                Ext.getCmp('bounds_tf').showField();
            }
            else {
                Ext.getCmp('numClasses_cb').showField();
                Ext.getCmp('bounds_tf').hideField();
            }                
        }
        else if (this.legend.type == GLOBALS.conf.map_legend_type_predefined) {
            Ext.getCmp('method_cb').hideField();
            Ext.getCmp('numClasses_cb').hideField();
            Ext.getCmp('bounds_tf').hideField();
            Ext.getCmp('colorA_cf').hideField();
            Ext.getCmp('colorB_cf').hideField();
            Ext.getCmp('maplegendset_cb').showField();
        }
    },
    
    prepareMapViewMap: function() {
        if (MAPSOURCE == GLOBALS.conf.map_source_type_database) {
            Ext.getCmp('map_cb').hideField();
            Ext.getCmp('map_tf').showField();
        }
        else {
            Ext.getCmp('map_cb').showField();
            Ext.getCmp('map_tf').hideField();
        }
    },
    
    setMapView: function() {
        var obj = this.prepareMapViewValueType();
        
        function valueTypeGroupStoreCallback() {
            obj.components.valueTypeGroup.setValue(this.mapView[obj.mapView.valueTypeGroup]);
            
            obj.stores.valueType.setBaseParam(obj.mapView.valueTypeGroup, obj.components.valueTypeGroup.getValue());
            obj.stores.valueType.load({scope: this, callback: function() {
                obj.components.valueType.setValue(this.mapView[obj.mapView.valueType]);
                
                obj = this.prepareMapViewDateType();
                if (MAPDATETYPE == GLOBALS.conf.map_date_type_fixed) {
                    if (obj.stores.c1.isLoaded) {
                        dateTypeGroupStoreCallback.call(this);
                    }
                    else {
                        obj.stores.c1.load({scope: this, callback: function() {
                            dateTypeGroupStoreCallback.call(this);
                        }});
                    }
                }
                else if (MAPDATETYPE == GLOBALS.conf.map_date_type_start_end) {
                    obj.components.c1.setValue(new Date(this.mapView[obj.mapView.c1]));
                    obj.components.c2.setValue(new Date(this.mapView[obj.mapView.c2]));
                    
                    this.setMapViewLegend();
                }                
            }});
        }
        
        function dateTypeGroupStoreCallback() {
            obj.components.c1.setValue(this.mapView[obj.mapView.c1]);
            
            obj.stores.c2.setBaseParam('name', this.mapView[obj.mapView.c1]);
            obj.stores.c2.load({scope: this, callback: function() {
                obj.components.c2.setValue(this.mapView[obj.mapView.c2]);
                
                this.setMapViewLegend();
            }});
        }

        if (obj.stores.valueTypeGroup.isLoaded) {
            valueTypeGroupStoreCallback.call(this);
        }
        else {
            obj.stores.valueTypeGroup.load({scope: this, callback: function() {
                valueTypeGroupStoreCallback.call(this);
            }});
        }
    },
    
    setMapViewLegend: function() {
        this.prepareMapViewLegend();
        
        function predefinedMapLegendSetStoreCallback() {
            Ext.getCmp('maplegendset_cb').setValue(this  .mapView.mapLegendSetId);
            this.applyPredefinedLegend(true);
        }
        
        if (this.legend.type == GLOBALS.conf.map_legend_type_automatic) {
            Ext.getCmp('method_cb').setValue(this.mapView.method);
            Ext.getCmp('colorA_cf').setValue(this.mapView.colorLow);
            Ext.getCmp('colorB_cf').setValue(this.mapView.colorHigh);
            
            if (this.legend.method == GLOBALS.conf.classify_with_bounds) {
                Ext.getCmp('bounds_tf').setValue(this.mapView.bounds);
            }
            else {
                Ext.getCmp('numClasses_cb').setValue(this.mapView.classes);
            }
            
            this.setMapViewMap();
        }
        else if (this.legend.type == GLOBALS.conf.map_legend_type_predefined) {
            if (GLOBALS.stores.predefinedMapLegendSet.isLoaded) {
                predefinedMapLegendSetStoreCallback.call(this);
            }
            else {
                GLOBALS.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                    predefinedMapLegendSetStoreCallback.call(this);
                }});
            }
        }            
    },
    
    setMapViewMap: function() {
        this.prepareMapViewMap();

        if (MAPSOURCE == GLOBALS.conf.map_source_type_database) {
            Ext.getCmp('map_tf').setValue(this.mapView.parentOrganisationUnitName);
            Ext.getCmp('map_tf').value = this.mapView.mapSource;
            this.loadFromDatabase(this.mapView.mapSource);
        }
        else {
            Ext.getCmp('map_cb').setValue(this.mapView.mapSource);
            this.loadFromFile(this.mapView.mapSource);
        }
    },
	
	applyPredefinedLegend: function(isMapView) {
        this.legend.type = GLOBALS.conf.map_legend_type_predefined;
		var mls = Ext.getCmp('maplegendset_cb').getValue();
		var bounds = [];
		Ext.Ajax.request({
			url: GLOBALS.conf.path_mapping + 'getMapLegendsByMapLegendSet' + GLOBALS.conf.type,
			method: 'POST',
			params: {mapLegendSetId: mls},
            scope: this,
			success: function(r) {
				var mapLegends = Ext.util.JSON.decode(r.responseText).mapLegends;
				var colors = [];
				var bounds = [];
				for (var i = 0; i < mapLegends.length; i++) {
					if (bounds[bounds.length-1] != mapLegends[i].startValue) {
						if (bounds.length != 0) {
							colors.push(new mapfish.ColorRgb(240,240,240));
						}
						bounds.push(mapLegends[i].startValue);
					}
					colors.push(new mapfish.ColorRgb());
					colors[colors.length-1].setFromHex(mapLegends[i].color);
					bounds.push(mapLegends[i].endValue);
				}

				this.colorInterpolation = colors;
				this.bounds = bounds;
                
                if (isMapView) {
                    this.setMapViewMap();
                }
                else {
                    this.classify(false, true);
                }                   
			}
		});
	},
    
    loadFromDatabase: function(id, isDrillDown) {
        function load() {
            MASK.msg = i18n_loading_geojson;
            MASK.show();
            
            this.parentId = id;
            this.setUrl(GLOBALS.conf.path_mapping + 'getGeoJson.action?parentId=' + this.parentId);
        }
        
        if (isDrillDown) {
            load.call(this);
        }
        else if (id != this.parentId || this.mapView) {
            if (!this.mapView) {
                if (!Ext.getCmp('map_tf').node.attributes.hasChildrenWithCoordinates) {
                    Ext.message.msg(false, i18n_no_coordinates_found);
                    Ext.getCmp('map_tf').setValue(Ext.getCmp('orgunit_tp').getNodeById(this.parentId).attributes.text);                    
                    Ext.getCmp('map_tf').value = this.parentId;
                    Ext.getCmp('map_tf').node = Ext.getCmp('orgunit_tp').getNodeById(this.parentId);
                    return;
                }
            }
            load.call(this);
        }
    },
    
    loadFromFile: function(url) {
        if (url != this.newUrl) {
            this.newUrl = url;

            if (MAPSOURCE == GLOBALS.conf.map_source_type_geojson) {
                this.setUrl(GLOBALS.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + url);
            }
			else if (MAPSOURCE == GLOBALS.conf.map_source_type_shapefile) {
				this.setUrl(GLOBALS.conf.path_geoserver + GLOBALS.conf.wfs + url + GLOBALS.conf.output);
			}
        }
        else {
            this.classify(false, true);
        }
    },
    
    validateForm: function(exception) {
        if (Ext.getCmp('mapvaluetype_cb').getValue() == GLOBALS.conf.map_value_type_indicator) {
            if (!Ext.getCmp('indicator_cb').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        else if (Ext.getCmp('mapvaluetype_cb').getValue() == GLOBALS.conf.map_value_type_dataelement) {
            if (!Ext.getCmp('dataelement_cb').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        
        if (MAPDATETYPE == GLOBALS.conf.map_date_type_fixed) {
            if (!Ext.getCmp('period_cb').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        else {
            if (!Ext.getCmp('startdate_df').getValue() || !Ext.getCmp('enddate_df').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        
        var cmp = MAPSOURCE == GLOBALS.conf.map_source_type_database ? Ext.getCmp('map_tf') : Ext.getCmp('map_cb');
        if (!cmp.getValue()) {
            if (exception) {
                Ext.message.msg(false, i18n_form_is_not_complete);
            }
            return false;
        }
        
        if (Ext.getCmp('maplegendtype_cb').getValue() == GLOBALS.conf.map_legend_type_automatic) {
            if (Ext.getCmp('method_cb').getValue() == GLOBALS.conf.classify_with_bounds) {
                if (!Ext.getCmp('bounds_tf').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                    }
                    return false;
                }
            }
        }
        else if (Ext.getCmp('maplegendtype_cb').getValue() == GLOBALS.conf.map_legend_type_predefined) {
            if (!Ext.getCmp('maplegendset_cb').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }            
        
        return true;
    },
    
    getFormValues: function() {
        return {
            mapValueType: Ext.getCmp('mapvaluetype_cb').getValue(),
            indicatorGroupId: Ext.getCmp('indicatorgroup_cb').getValue() || '',
            indicatorId: Ext.getCmp('indicator_cb').getValue() || '',
            dataElementGroupId: Ext.getCmp('dataelementgroup_cb').getValue() || '',
            dataElementId: Ext.getCmp('dataelement_cb').getValue() || '',
            periodTypeId: Ext.getCmp('periodtype_cb').getValue() || '',
            periodId: Ext.getCmp('period_cb').getValue() || '',
            startDate: Ext.getCmp('startdate_df').getValue() || '',
            endDate: Ext.getCmp('enddate_df').getValue() || '',
            mapSource: MAPSOURCE == GLOBALS.conf.map_source_type_database ? Ext.getCmp('map_tf').value : Ext.getCmp('map_cb').getValue(),
            mapLegendType: Ext.getCmp('maplegendtype_cb').getValue(),
            method: this.legend.type == GLOBALS.conf.map_legend_type_automatic ? Ext.getCmp('method_cb').getValue() : '',
            classes: this.legend.type == GLOBALS.conf.map_legend_type_automatic ? Ext.getCmp('numClasses_cb').getValue() : '',
            bounds: this.legend.type == GLOBALS.conf.map_legend_type_automatic && this.legend.method == GLOBALS.conf.classify_with_bounds ? Ext.getCmp('bounds_tf').getValue() : '',
            colorLow: this.legend.type == GLOBALS.conf.map_legend_type_automatic ? Ext.getCmp('colorA_cf').getValue() : '',
            colorHigh: this.legend.type == GLOBALS.conf.map_legend_type_automatic ? Ext.getCmp('colorB_cf').getValue() : '',
            mapLegendSetId: Ext.getCmp('maplegendset_cb').getValue() || '',
            longitude: MAP.getCenter().lon,
            latitude: MAP.getCenter().lat,
            zoom: parseInt(MAP.getZoom())
        };
    },
    
    applyValues: function() {
        var options = {};
        this.indicator = 'value';
        options.indicator = this.indicator;
        options.method = Ext.getCmp('method_cb').getValue();
        options.numClasses = Ext.getCmp('numClasses_cb').getValue();
        options.colors = this.getColors();
        
        this.coreComp.updateOptions(options);
        this.coreComp.applyClassification();
        this.classificationApplied = true;
        
        MASK.hide();
    },

    classify: function(exception, position) {
        if (MAPSOURCE == GLOBALS.conf.map_source_type_database) {
            this.classifyDatabase(exception, position);
        }
        else {
            this.classifyFile(exception, position);
        }
    },
    
    classifyDatabase: function(exception, position) {
        if (this.validateForm(exception)) {
            MASK.msg = i18n_aggregating_map_values;
            MASK.show();

            if (!position) {
                MAP.zoomToExtent(this.layer.getDataExtent());
            }
            
            this.mapData = {
                nameColumn: 'name'
            };
            
            if (this.mapView) {
                if (this.mapView.longitude && this.mapView.latitude && this.mapView.zoom) {
                    MAP.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
                }
                else {
                    MAP.zoomToExtent(this.layer.getDataExtent());
                }
                this.mapView = false;
            }
            
            var dataUrl = this.valueType == GLOBALS.conf.map_value_type_indicator ?
                'getIndicatorMapValuesByParentOrganisationUnit' : 'getDataMapValuesByParentOrganisationUnit';
            
            var params = {
                id: this.valueType == GLOBALS.conf.map_value_type_indicator ? Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue(),
                periodId: MAPDATETYPE == GLOBALS.conf.map_date_type_fixed ? Ext.getCmp('period_cb').getValue() : null,
                startDate: MAPDATETYPE == GLOBALS.conf.map_date_type_start_end ? new Date(Ext.getCmp('startdate_df').getValue()).format('Y-m-d') : null,
                endDate: MAPDATETYPE == GLOBALS.conf.map_date_type_start_end ? new Date(Ext.getCmp('enddate_df').getValue()).format('Y-m-d') : null,
                parentId: this.parentId
            };
                
            Ext.Ajax.request({
                url: GLOBALS.conf.path_mapping + dataUrl + GLOBALS.conf.type,
                method: 'POST',
                params: params,
                scope: this,
                success: function(r) {
                    var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                    EXPORTVALUES = GLOBALS.util.getExportDataValueJSON(mapvalues);
                    
                    if (mapvalues.length == 0) {
                        Ext.message.msg(false, i18n_current_selection_no_data);
                        MASK.hide();
                        return;
                    }

                    for (var i = 0; i < mapvalues.length; i++) {
                        for (var j = 0; j < this.layer.features.length; j++) {
                            if (mapvalues[i].orgUnitName == this.layer.features[j].attributes.name) {
                                this.layer.features[j].attributes.value = parseFloat(mapvalues[i].value);
                                this.layer.features[j].attributes.labelString = this.layer.features[j].attributes.name + ' (' + this.layer.features[j].attributes.value + ')';
                                break;
                            }
                        }
                    }
                    
                    this.applyValues();
                }
            });
        }
    },
    
    classifyFile: function(exception, position) {
        if (this.validateForm(exception)) {
        
            MASK.msg = i18n_aggregating_map_values;
            MASK.show();
            
            Ext.Ajax.request({
                url: GLOBALS.conf.path_mapping + 'getMapByMapLayerPath' + GLOBALS.conf.type,
                method: 'POST',
                params: {mapLayerPath: this.newUrl},
                scope: this,
                success: function(r) {
                    this.mapData = Ext.util.JSON.decode(r.responseText).map[0];
                    
                    this.mapData.organisationUnitLevel = parseFloat(this.mapData.organisationUnitLevel);
                    this.mapData.longitude = parseFloat(this.mapData.longitude);
                    this.mapData.latitude = parseFloat(this.mapData.latitude);
                    this.mapData.zoom = parseFloat(this.mapData.zoom);
                    
                    if (!position) {
                        if (this.mapData.zoom != MAP.getZoom()) {
                            MAP.zoomTo(this.mapData.zoom);
                        }
                        MAP.setCenter(new OpenLayers.LonLat(this.mapData.longitude, this.mapData.latitude));
                    }
                    
                    if (this.mapView) {
                        if (this.mapView.longitude && this.mapView.latitude && this.mapView.zoom) {
                            MAP.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
                        }
                        else {
                            MAP.setCenter(new OpenLayers.LonLat(this.mapData.longitude, this.mapData.latitude), this.mapData.zoom);
                        }
                        this.mapView = false;
                    }
                    
                    var params = {
                        id: this.valueType == GLOBALS.conf.map_value_type_indicator ? Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue()
                    };
                        
            
                    var indicatorOrDataElementId = this.valueType == GLOBALS.conf.map_value_type_indicator ?
                        Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue();
                    var dataUrl = this.valueType == GLOBALS.conf.map_value_type_indicator ?
                        'getIndicatorMapValuesByMap' : 'getDataMapValuesByMap';
                    var periodId = Ext.getCmp('period_cb').getValue();
                    var mapLayerPath = this.newUrl;
                    
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + dataUrl + GLOBALS.conf.type,
                        method: 'POST',
                        params: {id:indicatorOrDataElementId, periodId:periodId, mapLayerPath:mapLayerPath},
                        scope: this,
                        success: function(r) {
                            var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                            EXPORTVALUES = GLOBALS.util.getExportDataValueJSON(mapvalues);
                            var mv = new Array();
                            var mour = new Array();
                            var nameColumn = this.mapData.nameColumn;
                            var options = {};
                            
                            if (mapvalues.length == 0) {
                                Ext.message.msg(false, i18n_current_selection_no_data );
                                MASK.hide();
                                return;
                            }
                            
                            for (var i = 0; i < mapvalues.length; i++) {
                                mv[mapvalues[i].orgUnitName] = mapvalues[i].orgUnitName ? mapvalues[i].value : '';
                            }
                            
                            Ext.Ajax.request({
                                url: GLOBALS.conf.path_mapping + 'getAvailableMapOrganisationUnitRelations' + GLOBALS.conf.type,
                                method: 'POST',
                                params: {mapLayerPath: mapLayerPath},
                                scope: this,
                                success: function(r) {
                                    var relations = Ext.util.JSON.decode(r.responseText).mapOrganisationUnitRelations;
                                   
                                    for (var i = 0; i < relations.length; i++) {
                                        mour[relations[i].featureId] = relations[i].organisationUnit;
                                    }

                                    for (var j = 0; j < this.layer.features.length; j++) {
                                        var value = mv[mour[this.layer.features[j].attributes[nameColumn]]];
                                        this.layer.features[j].attributes.value = value ? parseFloat(value) : '';
                                        this.layer.features[j].data.id = this.layer.features[j].attributes[nameColumn];
                                        this.layer.features[j].data.name = this.layer.features[j].attributes[nameColumn];
                                        this.layer.features[j].attributes.labelString = this.layer.features[j].attributes[nameColumn] + ' (' + this.layer.features[j].attributes.value + ')';
                                    }
                                    
                                    this.applyValues();
                                }
                            });
                        }
                    });
                }
            });
        }
    },
    
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Choropleth.superclass.onRender.apply(this, arguments);
        if (this.loadMask) {
            this.loadMask = new Ext.LoadMask(this.bwrap, this.loadMask);
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