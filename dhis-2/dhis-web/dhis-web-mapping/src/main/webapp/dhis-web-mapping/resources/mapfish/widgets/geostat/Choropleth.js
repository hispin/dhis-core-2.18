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

    mapView: false,

    mapData: false,
    
    labels: false,
    
    valueType: false,
    
    selectFeatures: false,
    
    organisationUnitSelection: false,
    
    updateValues: false,
    
    isDrillDown: false,
    
    initComponent: function() {
    
        this.initProperties();
        
        this.createItems();
        
        this.createSelectFeatures();

        if (G.vars.parameter.id) {
			if (G.vars.parameter.mapView.featureType == G.conf.map_feature_type_multipolygon) {
				this.mapView = G.vars.parameter.mapView;
				this.updateValues = true;
				this.legend = {
					value: this.mapView.mapLegendType,
					method: this.mapView.method || this.legend.method,
					classes: this.mapView.classes || this.legend.classes
				};
				
				G.vars.parameter.id = false;
				G.vars.map.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
				
				function mapViewStoreCallback() {
					this.form.findField('mapview').setValue(this.mapView.id);
					this.valueType.value = this.mapView.mapValueType;
					this.form.findField('mapvaluetype').setValue(this.valueType.value);
					this.setMapView();
				}
				
				if (G.stores.polygonMapView.isLoaded) {
					mapViewStoreCallback.call(this);
				}
				else {
					G.stores.polygonMapView.load({scope: this, callback: function() {
						mapViewStoreCallback.call(this);
					}});
				}
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
        OpenLayers.Console.error(G.i18n.ajax_request_failed);
    },
    
    getColors: function() {
        var startColor = new mapfish.ColorRgb();
        startColor.setFromHex(this.form.findField('startcolor').getValue());
        var endColor = new mapfish.ColorRgb();
        endColor.setFromHex(this.form.findField('endcolor').getValue());
        return [startColor, endColor];
    },
    
    initProperties: function() {
        this.legend = {
            value: G.conf.map_legend_type_automatic,
            method: G.conf.classify_by_equal_intervals,
            classes: 5,
            reset: function() {
                this.value = G.conf.map_legend_type_automatic;
                this.method = G.conf.classify_by_equal_intervals;
                this.classes = 5;
            }
        };
        
        this.organisationUnitSelection = {
            parent: {
                id: null,
                name: null,
                level: null
            },
            level: {
                level: null,
                name: null
            },
            setValues: function(pid, pn, pl, ll, ln) {
                this.parent.id = pid || this.parent.id;
                this.parent.name = pn || this.parent.name;
                this.parent.level = pl || this.parent.level;
                this.level.level = ll || this.level.level;
                this.level.name = ln || this.level.name;
            },
            getValues: function() {
                return {
                    parent: {
                        id: this.parent.id,
                        name: this.parent.name,
                        level: this.parent.level
                    },
                    level: {
                        level: this.level.level,
                        name: this.level.name
                    }                    
                };
            },
            setValuesOnDrillDown: function(pid, pn) {
                this.parent.id = pid;
                this.parent.name = pn;
                this.parent.level = this.level.level;
                this.level.level++;
                this.level.name = G.stores.organisationUnitLevel.getAt(
                    G.stores.organisationUnitLevel.find('level', this.level.level)).data.name;
                
                return [this.parent.name, this.level.name];
            }                
        };
        
        this.valueType = {
            value: G.conf.map_value_type_indicator,
            setIndicator: function() {
                this.value = G.conf.map_value_type_indicator;
            },
            setDatElement: function() {
                this.value = G.conf.map_value_type_dataelement;
            },
            isIndicator: function() {
                return this.value == G.conf.map_value_type_indicator;
            },
            isDataElement: function() {
                return this.value == G.conf.map_value_type_dataelement;
            }
        };
    },
    
    createItems: function() {
        this.items = [
        {
            xtype: 'combo',
            name: 'mapview',
            fieldLabel: G.i18n.favorite,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: G.i18n.optional,
            selectOnFocus: true,
			labelSeparator: G.conf.labelseparator,
            width: G.conf.combo_width,
            store: G.stores.polygonMapView,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        this.mapView = G.stores.polygonMapView.getAt(G.stores.polygonMapView.find('id', cb.getValue())).data;
                        this.updateValues = true;
                        
                        this.legend.value = this.mapView.mapLegendType;
                        this.legend.method = this.mapView.method || this.legend.method;
                        this.legend.classes = this.mapView.classes || this.legend.classes;

                        G.vars.map.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
                        G.system.mapDateType.value = this.mapView.mapDateType;
                        Ext.getCmp('mapdatetype_cb').setValue(G.system.mapDateType.value);

                        this.valueType.value = this.mapView.mapValueType;
                        this.form.findField('mapvaluetype').setValue(this.valueType.value);
                        this.setMapView();
                    }
                }
            }
        },
        
        { html: '<div class="thematic-br">' },
		
		{
            xtype: 'combo',
            name: 'mapvaluetype',
            fieldLabel: G.i18n.mapvaluetype,
			labelSeparator: G.conf.labelseparator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all',
            width: G.conf.combo_width,
			value: G.conf.map_value_type_indicator,
            store: new Ext.data.ArrayStore({
                fields: ['id', 'name'],
                data: [
                    [G.conf.map_value_type_indicator, 'Indicator'],
                    [G.conf.map_value_type_dataelement, 'Data element']
                ]
            }),
			listeners: {
				'select': {
                    scope: this,
					fn: function(cb) {
                        this.valueType.value = cb.getValue();
                        this.prepareMapViewValueType();
                        this.classify(false, true);
					}
				}
			}
		},
        
        {
            xtype: 'combo',
            name: 'indicatorgroup',
            fieldLabel: G.i18n.indicator_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            selectOnFocus: true,
            width: G.conf.combo_width,
            store: G.stores.indicatorGroup,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
						this.form.findField('indicator').clearValue();
                        G.stores.indicatorsByGroup.setBaseParam('indicatorGroupId', cb.getValue());
                        G.stores.indicatorsByGroup.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            name: 'indicator',
            fieldLabel: G.i18n.indicator,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            selectOnFocus: true,
            width: G.conf.combo_width,
            store: G.stores.indicatorsByGroup,
            currentValue: null,
            keepPosition: false,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (G.util.setCurrentValue.call(this, cb, 'mapview')) {
                            return;
                        }
                        
                        this.updateValues = true;
                        Ext.Ajax.request({
                            url: G.conf.path_mapping + 'getMapLegendSetByIndicator' + G.conf.type,
                            method: 'POST',
                            params: {indicatorId: cb.getValue()},
                            scope: this,
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    this.legend.value = G.conf.map_legend_type_predefined;
                                    this.prepareMapViewLegend();
                                    
                                    function load() {
                                        this.form.findField('maplegendset').setValue(mapLegendSet.id);
                                        this.applyPredefinedLegend();
                                    }
                                    
                                    if (!G.stores.predefinedMapLegendSet.isLoaded) {
                                        G.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                                            load.call(this);
                                        }});
                                    }
                                    else {
                                        load.call(this);
                                    }
                                }
                                else {
                                    this.legend.value = G.conf.map_legend_type_automatic;
                                    this.prepareMapViewLegend();
                                    this.classify(false, cb.keepPosition);
                                    G.util.setKeepPosition(cb);
                                }
                            }
                        });
                    }
                }
            }
        },
		
		{
            xtype: 'combo',
            name: 'dataelementgroup',
            fieldLabel: G.i18n.dataelement_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            selectOnFocus: true,
            width: G.conf.combo_width,
            store: G.stores.dataElementGroup,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        this.form.findField('dataelement').clearValue();
						G.stores.dataElementsByGroup.setBaseParam('dataElementGroupId', cb.getValue());
                        G.stores.dataElementsByGroup.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            name: 'dataelement',
            fieldLabel: G.i18n.dataelement,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            selectOnFocus: true,
            width: G.conf.combo_width,
            store: G.stores.dataElementsByGroup,
            keepPosition: false,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (G.util.setCurrentValue.call(this, cb, 'mapview')) {
                            return;
                        }
                        
                        this.updateValues = true;
                        Ext.Ajax.request({
                            url: G.conf.path_mapping + 'getMapLegendSetByDataElement' + G.conf.type,
                            method: 'POST',
                            params: {dataElementId: cb.getValue()},
                            scope: this,
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    this.legend.value = G.conf.map_legend_type_predefined;
                                    this.prepareMapViewLegend();
                                    
                                    function load() {
                                        this.form.findField('maplegendset').setValue(mapLegendSet.id);
                                        this.applyPredefinedLegend();
                                    }
                                    
                                    if (!G.stores.predefinedMapLegendSet.isLoaded) {
                                        G.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                                            load.call(this);
                                        }});
                                    }
                                    else {
                                        load.call(this);
                                    }
                                }
                                else {
                                    this.legend.value = G.conf.map_legend_type_automatic;
                                    this.prepareMapViewLegend();
                                    this.classify(false, cb.keepPosition);
                                    G.util.setKeepPosition(cb);
                                }
                            }
                        });
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            name: 'periodtype',
            fieldLabel: G.i18n.period_type,
            typeAhead: true,
            editable: false,
            valueField: 'name',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            selectOnFocus: true,
            width: G.conf.combo_width,
            store: G.stores.periodType,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        this.form.findField('period').clearValue();
                        G.stores.periodsByTypeStore.setBaseParam('name', cb.getValue());
                        G.stores.periodsByTypeStore.load();
                    }
                }
            }
        },

        {
            xtype: 'combo',
            name: 'period',
            fieldLabel: G.i18n.period,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            selectOnFocus: true,
            width: G.conf.combo_width,
            store: G.stores.periodsByTypeStore,
            keepPosition: false,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (G.util.setCurrentValue.call(this, cb, 'mapview')) {
                            return;
                        }
                        
                        this.updateValues = true;
                        this.classify(false, cb.keepPosition);                        
                        G.util.setKeepPosition(cb);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            name: 'startdate',
            fieldLabel: G.i18n.start_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            width: G.conf.combo_width,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(df, date) {
                        this.form.findField('mapview').clearValue();
                        this.updateValues = true;
                        this.form.findField('enddate').setMinValue(date);
                        this.classify(false, true);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            name: 'enddate',
            fieldLabel: G.i18n.end_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            width: G.conf.combo_width,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(df, date) {
                        this.form.findField('mapview').clearValue();
                        this.updateValues = true;
                        this.form.findField('startdate').setMaxValue(date);
                        this.classify(false, true);
                    }
                }
            }
        },
        
        { html: '<div class="thematic-br">' },
        
        {
            xtype: 'textfield',
            name: 'boundary',
            fieldLabel: G.i18n.boundary,
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            width: G.conf.combo_width,
            style: 'cursor:pointer',
            node: {attributes: {hasChildrenWithCoordinates: false}},
            selectedNode: null,
            treeWindow: null,
            treePanel: null,
            listeners: {
                'focus': {
                    scope: this,
                    fn: function(tf) {
                        if (tf.treeWindow) {
                            tf.treeWindow.show();
                        }
                        else {
							this.createSingletonCmp.treeWindow.call(this);
                        }
                    }
                }
            }
        },
        
        {
            xtype: 'textfield',
            name: 'level',
            fieldLabel: G.i18n.level,
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            width: G.conf.combo_width,
            style: 'cursor:pointer',
            levelComboBox: null,
            listeners: {
                'focus': {
                    scope: this,
                    fn: function() {
                        if (this.form.findField('boundary').treeWindow) {
                            this.form.findField('boundary').treeWindow.show();
                        }
                        else {
							this.createSingletonCmp.treeWindow.call(this);
                        }
                    }
                }
            }
        },
        
        { html: '<div class="thematic-br">' },
		
		{
            xtype: 'combo',
            name: 'maplegendtype',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            fieldLabel: G.i18n.legend_type,
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            value: this.legend.value,
            triggerAction: 'all',
            width: G.conf.combo_width,
            store: new Ext.data.ArrayStore({
                fields: ['value', 'text'],
                data: [
					[G.conf.map_legend_type_automatic, G.i18n.automatic],
					[G.conf.map_legend_type_predefined, G.i18n.predefined]
				]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (cb.getValue() == G.conf.map_legend_type_predefined && cb.getValue() != this.legend.value) {
							this.legend.value = G.conf.map_legend_type_predefined;
                            this.prepareMapViewLegend();
							
							if (this.form.findField('maplegendset').getValue()) {
                                this.applyPredefinedLegend();
							}
                        }
                        else if (cb.getValue() == G.conf.map_legend_type_automatic && cb.getValue() != this.legend.value) {
							this.legend.value = G.conf.map_legend_type_automatic;
							this.prepareMapViewLegend();                            
                            this.classify(false, true);
                        }
                    }
                }
            }
        },
		
		{
            xtype: 'combo',
            name: 'maplegendset',
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            fieldLabel: G.i18n.legendset,
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            triggerAction: 'all',
            width: G.conf.combo_width,
			hidden: true,
            store: G.stores.predefinedMapLegendSet,
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        this.form.findField('mapview').clearValue();
						this.applyPredefinedLegend();
                    }
                }
            }
        },

        {
            xtype: 'combo',
            name: 'method',
            fieldLabel: G.i18n.method,
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: G.conf.emptytext,
			labelSeparator: G.conf.labelseparator,
            value: this.legend.method,
            triggerAction: 'all',
            width: G.conf.combo_width,
            store: new Ext.data.ArrayStore({
                fields: ['value', 'text'],
                data: [
					[2, G.i18n.equal_intervals],
					[3, G.i18n.equal_group_count],
					[1, G.i18n.fixed_breaks]
				]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        this.form.findField('mapview').clearValue();
                        if (cb.getValue() == G.conf.classify_with_bounds && cb.getValue() != this.legend.method) {
							this.legend.method = G.conf.classify_with_bounds;
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
            name: 'bounds',
            fieldLabel: G.i18n.bounds,
			labelSeparator: G.conf.labelseparator,
            emptyText: G.i18n.comma_separated_values,
            width: G.conf.combo_width,
            hidden: true,
            listeners: {
                'change': {
                    scope: this,
                    fn: function() {
                        this.classify(false, true);
                    }
                }                
            }
        },
        
        {
            xtype: 'combo',
            name: 'classes',
            fieldLabel: G.i18n.classes,
			labelSeparator: G.conf.labelseparator,
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: this.legend.classes,
            triggerAction: 'all',
            width: G.conf.combo_width,
            store: new Ext.data.ArrayStore({
                fields: ['value'],
                data: [[1], [2], [3], [4], [5], [6], [7]]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        this.form.findField('mapview').clearValue();
						
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
            name: 'startcolor',
            fieldLabel: G.i18n.low_color,
			labelSeparator: G.conf.labelseparator,
            allowBlank: false,
            width: G.conf.combo_width,
            value: "#FF0000",
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        this.classify(false, true);
                    }
                }
            }
        },
        
        {
            xtype: 'colorfield',
            name: 'endcolor',
            fieldLabel: G.i18n.high_color,
			labelSeparator: G.conf.labelseparator,
            allowBlank: false,
            width: G.conf.combo_width,
            value: "#FFFF00",
            listeners: {
                'select': {
                    scope: this,
                    fn: function() {
                        this.classify(false, true);
                    }
                }
            }
        }

        ];
    },
    
	createSingletonCmp: {
		treeWindow: function() {
			Ext.Ajax.request({
				url: G.conf.path_commons + 'getOrganisationUnits' + G.conf.type,
				params: {level: 1},
				method: 'POST',
				scope: this,
				success: function(r) {
					var rootNode = Ext.util.JSON.decode(r.responseText).organisationUnits[0];
                    var rootUnit = {
						id: rootNode.id,
						name: rootNode.name,
                        level: 1,
						hasChildrenWithCoordinates: rootNode.hasChildrenWithCoordinates
					};
					
					var w = new Ext.Window({
						title: 'Boundary and level',
						closeAction: 'hide',
						autoScroll: true,
						height: 'auto',
						autoHeight: true,
						width: G.conf.window_width,
						items: [
							{
								xtype: 'panel',
								bodyStyle: 'padding:8px; background-color:#ffffff',
								items: [
									{html: '<div class="window-info">Select outer boundary</div>'},
									{
										xtype: 'treepanel',
										bodyStyle: 'background-color:#ffffff',
										height: screen.height / 3,
										autoScroll: true,
										lines: false,
										loader: new Ext.tree.TreeLoader({
											dataUrl: G.conf.path_mapping + 'getOrganisationUnitChildren' + G.conf.type
										}),
										root: {
											id: rootUnit.id,
											text: rootUnit.name,
                                            level: rootUnit.level,
											hasChildrenWithCoordinates: rootUnit.hasChildrenWithCoordinates,
											nodeType: 'async',
											draggable: false,
											expanded: true
										},
										clickedNode: null,
										listeners: {
											'click': {
												scope: this,
												fn: function(n) {
													this.form.findField('boundary').selectedNode = n;
												}
											},
                                            'afterrender': {
                                                scope: this,
                                                fn: function(tp) {
                                                    this.form.findField('boundary').treePanel = tp;
                                                }
                                            }
										}
									}
								]
							},
							{
								xtype: 'panel',
								layout: 'form',
								bodyStyle: 'padding:8px; background-color:#ffffff',
                                labelWidth: G.conf.label_width,
								items: [
									{html: '<div class="window-info">Select organisation unit level</div>'},
									{
										xtype: 'combo',
										fieldLabel: G.i18n.level,
										editable: false,
										valueField: 'level',
										displayField: 'name',
										mode: 'remote',
										forceSelection: true,
										triggerAction: 'all',
										selectOnFocus: true,
										emptyText: G.conf.emptytext,
										labelSeparator: G.conf.labelseparator,
										fieldLabel: 'Level',
										width: G.conf.combo_width_fieldset,
										minListWidth: G.conf.combo_width_fieldset,
										store: G.stores.organisationUnitLevel,
										listeners: {
											'afterrender': {
												scope: this,
												fn: function(cb) {
													this.form.findField('level').levelComboBox = cb;
												}
											}
										}
									}
								]
							}
						],
						bbar: [
							'->',
							{
								xtype: 'button',
								text: G.i18n.apply,
								iconCls: 'icon-assign',
								scope: this,
								handler: function() {
									var node = this.form.findField('boundary').selectedNode;
									if (!node || !this.form.findField('level').levelComboBox.getValue()) {
										return;
									}
									if (node.attributes.level > this.form.findField('level').levelComboBox.getValue()) {
										Ext.message.msg(false, 'Level is higher than boundary level');
										return;
									}
									
									if (Ext.getCmp('locatefeature_w')) {
										Ext.getCmp('locatefeature_w').destroy();
									}
									
									this.form.findField('mapview').clearValue();
									this.updateValues = true;
									this.organisationUnitSelection.setValues(node.attributes.id, node.attributes.text, node.attributes.level,
										this.form.findField('level').levelComboBox.getValue(), this.form.findField('level').levelComboBox.getRawValue());
										
									this.form.findField('boundary').setValue(node.attributes.text);
									this.form.findField('level').setValue(this.form.findField('level').levelComboBox.getRawValue());
									
									this.form.findField('boundary').treeWindow.hide();									
									this.loadGeoJson();
								}
							}
						]
					});
					
					var x = Ext.getCmp('center').x + G.conf.window_position_x;
					var y = Ext.getCmp('center').y + G.conf.window_position_y;
					w.setPosition(x,y);
					w.show();
					this.form.findField('boundary').treeWindow = w;
				}
			});
		}
	},
    
    createSelectFeatures: function() {
        var scope = this;
        
        var onHoverSelect = function onHoverSelect(feature) {
            if (feature.attributes.name) {
                document.getElementById('featuredatatext').innerHTML = '<div style="color:black">' + feature.attributes.name + '</div><div style="color:#555">' + feature.attributes.value + '</div>';
            }
            else {
                document.getElementById('featuredatatext').innerHTML = '';
            }
        };
        
        var onHoverUnselect = function onHoverUnselect(feature) {
            if (feature.attributes.name) {
                document.getElementById('featuredatatext').innerHTML = '<div style="color:#666">' + G.i18n.no_feature_selected + '</div>';
            }
            else {
                document.getElementById('featuredatatext').innerHTML = '';
            }
        };
        
        var onClickSelect = function onClickSelect(feature) {
            if (feature.attributes.hasChildrenWithCoordinates) {
                if (G.vars.locateFeatureWindow) {
                    G.vars.locateFeatureWindow.destroy();
                }
                         
                scope.updateValues = true;
                scope.isDrillDown = true;
                
                function organisationUnitLevelCallback() {
                    var names = this.organisationUnitSelection.setValuesOnDrillDown(feature.attributes.id, feature.attributes.name);
                    this.form.findField('boundary').setValue(names[0]);
                    this.form.findField('level').setValue(names[1]);
                    this.loadGeoJson();
                }
                
                if (G.stores.organisationUnitLevel.isLoaded) {
                    organisationUnitLevelCallback.call(scope);
                }
                else {
                    G.stores.organisationUnitLevel.load({scope: scope, callback: function() {
                        organisationUnitLevelCallback.call(this);
                    }});
                }
            }
            else {
                Ext.message.msg(false, G.i18n.no_coordinates_found);
            }
        };
        
        this.selectFeatures = new OpenLayers.Control.newSelectFeature(
            this.layer, {
                onHoverSelect: onHoverSelect,
                onHoverUnselect: onHoverUnselect,
                onClickSelect: onClickSelect
            }
        );
        
        G.vars.map.addControl(this.selectFeatures);
        this.selectFeatures.activate();
    },
    
    prepareMapViewValueType: function() {
        var obj = {};
        if (this.valueType.isIndicator()) {
            this.form.findField('indicatorgroup').showField();
            this.form.findField('indicator').showField();
            this.form.findField('dataelementgroup').hideField();
            this.form.findField('dataelement').hideField();
            obj.components = {
                valueTypeGroup: this.form.findField('indicatorgroup'),
                valueType: this.form.findField('indicator')
            };
            obj.stores = {
                valueTypeGroup: G.stores.indicatorGroup,
                valueType: G.stores.indicatorsByGroup
            };
            obj.mapView = {
                valueTypeGroup: 'indicatorGroupId',
                valueType: 'indicatorId'
            };
        }
        else if (this.valueType.isDataElement()) {
            this.form.findField('indicatorgroup').hideField();
            this.form.findField('indicator').hideField();
            this.form.findField('dataelementgroup').showField();
            this.form.findField('dataelement').showField();
            obj.components = {
                valueTypeGroup: this.form.findField('dataelementgroup'),
                valueType: this.form.findField('dataelement')
            };
            obj.stores = {
                valueTypeGroup: G.stores.dataElementGroup,
                valueType: G.stores.dataElementsByGroup
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
        if (G.system.mapDateType.isFixed()) {
            this.form.findField('periodtype').showField();
            this.form.findField('period').showField();
            this.form.findField('startdate').hideField();
            this.form.findField('enddate').hideField();
            obj.components = {
                c1: this.form.findField('periodtype'),
                c2: this.form.findField('period')
            };
            obj.stores = {
                c1: G.stores.periodType,
                c2: G.stores.periodsByTypeStore
            };
            obj.mapView = {
                c1: 'periodTypeId',
                c2: 'periodId'
            };
        }
        else if (G.system.mapDateType.isStartEnd()) {
            this.form.findField('periodtype').hideField();
            this.form.findField('period').hideField();
            this.form.findField('startdate').showField();
            this.form.findField('enddate').showField();
            obj.components = {
                c1: this.form.findField('startdate'),
                c2: this.form.findField('enddate')
            };
            obj.mapView = {
                c1: 'startDate',
                c2: 'endDate'
            };
        }
        return obj;
    },
    
    prepareMapViewLegend: function() {
        this.form.findField('maplegendtype').setValue(this.legend.value);
        
        if (this.legend.value == G.conf.map_legend_type_automatic) {
            this.form.findField('method').showField();
            this.form.findField('startcolor').showField();
            this.form.findField('endcolor').showField();
            this.form.findField('maplegendset').hideField();
            
            if (this.legend.method == G.conf.classify_with_bounds) {
                this.form.findField('classes').hideField();
                this.form.findField('bounds').showField();
            }
            else {
                this.form.findField('classes').showField();
                this.form.findField('bounds').hideField();
            }                
        }
        else if (this.legend.value == G.conf.map_legend_type_predefined) {
            this.form.findField('method').hideField();
            this.form.findField('classes').hideField();
            this.form.findField('bounds').hideField();
            this.form.findField('startcolor').hideField();
            this.form.findField('endcolor').hideField();
            this.form.findField('maplegendset').showField();
        }
    },
    
    setMapView: function() {
        var obj = this.prepareMapViewValueType();
        
        function valueTypeGroupStoreCallback() {
            obj.components.valueTypeGroup.setValue(this.mapView[obj.mapView.valueTypeGroup]);
            obj.stores.valueType.setBaseParam(obj.mapView.valueTypeGroup, obj.components.valueTypeGroup.getValue());
            obj.stores.valueType.load({scope: this, callback: function() {
                obj.components.valueType.setValue(this.mapView[obj.mapView.valueType]);
                obj.components.valueType.currentValue = this.mapView[obj.mapView.valueType];
                
                obj = this.prepareMapViewDateType();
                if (G.system.mapDateType.isFixed()) {
                    if (obj.stores.c1.isLoaded) {
                        dateTypeGroupStoreCallback.call(this);
                    }
                    else {
                        obj.stores.c1.load({scope: this, callback: function() {
                            dateTypeGroupStoreCallback.call(this);
                        }});
                    }
                }
                else if (G.system.mapDateType.isStartEnd()) {
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
                obj.components.c2.currentValue = this.mapView[obj.mapView.c2];
                obj.components.c2.keepPosition = true;
                
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
            this.form.findField('maplegendset').setValue(this.mapView.mapLegendSetId);
            this.applyPredefinedLegend(true);
        }

        if (this.legend.value == G.conf.map_legend_type_automatic) {
            this.form.findField('method').setValue(this.mapView.method);
            this.form.findField('startcolor').setValue(this.mapView.colorLow);
            this.form.findField('endcolor').setValue(this.mapView.colorHigh);

            if (this.legend.method == G.conf.classify_with_bounds) {
                this.form.findField('bounds').setValue(this.mapView.bounds);
            }
            else {
                this.form.findField('classes').setValue(this.mapView.classes);
            }

            this.setMapViewMap();
        }
        else if (this.legend.value == G.conf.map_legend_type_predefined) {
            if (G.stores.predefinedMapLegendSet.isLoaded) {
                predefinedMapLegendSetStoreCallback.call(this);
            }
            else {
                G.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                    predefinedMapLegendSetStoreCallback.call(this);
                }});
            }
        }
    },
    
    setMapViewMap: function() {
        this.organisationUnitSelection.setValues(this.mapView.parentOrganisationUnitId, this.mapView.parentOrganisationUnitName,
            this.mapView.parentOrganisationUnitLevel, this.mapView.organisationUnitLevel, this.mapView.organisationUnitLevelName);
        
        G.stores.organisationUnitLevel.load();
        this.form.findField('boundary').setValue(this.mapView.parentOrganisationUnitName);
        this.form.findField('level').setValue(this.mapView.organisationUnitLevelName);
        
        G.vars.activePanel.setPolygon();
        this.loadGeoJson();
    },
	
	applyPredefinedLegend: function(isMapView) {
        this.legend.value = G.conf.map_legend_type_predefined;
		var mls = this.form.findField('maplegendset').getValue();
		var bounds = [];
		Ext.Ajax.request({
			url: G.conf.path_mapping + 'getMapLegendsByMapLegendSet' + G.conf.type,
			method: 'POST',
			params: {mapLegendSetId: mls},
            scope: this,
			success: function(r) {
				var mapLegends = Ext.util.JSON.decode(r.responseText).mapLegends;
				var colors = [];
				var bounds = [];
				for (var i = 0; i < mapLegends.length; i++) {
					if (bounds[bounds.length-1] != mapLegends[i].startValue) {
						if (bounds.length !== 0) {
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
    
    formValidation: {
        validateForm: function(exception) {
            if (this.form.findField('mapvaluetype').getValue() == G.conf.map_value_type_indicator) {
                if (!this.form.findField('indicator').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, G.i18n.form_is_not_complete);
                    }
                    return false;
                }
            }
            else if (this.form.findField('mapvaluetype').getValue() == G.conf.map_value_type_dataelement) {
                if (!this.form.findField('dataelement').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, G.i18n.form_is_not_complete);
                    }
                    return false;
                }
            }

            if (G.system.mapDateType.isFixed()) {
                if (!this.form.findField('period').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, G.i18n.form_is_not_complete);
                    }
                    return false;
                }
            }
            else {
                if (!this.form.findField('startdate').getValue() || !this.form.findField('enddate').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, G.i18n.form_is_not_complete);
                    }
                    return false;
                }
            }

            if (!this.form.findField('boundary').getValue() || !this.form.findField('level').getValue()) {
                if (exception) {
                    Ext.message.msg(false, G.i18n.form_is_not_complete);
                }
                return false;
            }

            if (this.form.findField('maplegendtype').getValue() == G.conf.map_legend_type_automatic) {
                if (this.form.findField('method').getValue() == G.conf.classify_with_bounds) {
                    if (!this.form.findField('bounds').getValue()) {
                        if (exception) {
                            Ext.message.msg(false, G.i18n.form_is_not_complete);
                        }
                        return false;
                    }
                }
            }
            else if (this.form.findField('maplegendtype').getValue() == G.conf.map_legend_type_predefined) {
                if (!this.form.findField('maplegendset').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, G.i18n.form_is_not_complete);
                    }
                    return false;
                }
            }
            
            return true;
        }
    },
    
    formValues: {
		getAllValues: function() {
			return {
                featureType: G.conf.map_feature_type_multipolygon,
                mapValueType: this.form.findField('mapvaluetype').getValue(),
                indicatorGroupId: this.valueType.isIndicator() ? this.form.findField('indicatorgroup').getValue() : null,
                indicatorId: this.valueType.isIndicator() ? this.form.findField('indicator').getValue() : null,
				indicatorName: this.valueType.isIndicator() ? this.form.findField('indicator').getRawValue() : null,
                dataElementGroupId: this.valueType.isDataElement() ? this.form.findField('dataelementgroup').getValue() : null,
                dataElementId: this.valueType.isDataElement() ? this.form.findField('dataelement').getValue() : null,
				dataElementName: this.valueType.isDataElement() ? this.form.findField('dataelement').getRawValue() : null,
                mapDateType: G.system.mapDateType.value,
                periodTypeId: G.system.mapDateType.isFixed() ? this.form.findField('periodtype').getValue() : null,
                periodId: G.system.mapDateType.isFixed() ? this.form.findField('period').getValue() : null,
                periodName: G.system.mapDateType.isFixed() ? this.form.findField('period').getRawValue() : null,
                startDate: G.system.mapDateType.isStartEnd() ? this.form.findField('startdate').getRawValue() : null,
                endDate: G.system.mapDateType.isStartEnd() ? this.form.findField('enddate').getRawValue() : null,
                parentOrganisationUnitId: this.organisationUnitSelection.parent.id,
                parentOrganisationUnitLevel: this.organisationUnitSelection.parent.level,
                parentOrganisationUnitName: this.organisationUnitSelection.parent.name,
                organisationUnitLevel: this.organisationUnitSelection.level.level,
                organisationUnitLevelName: this.organisationUnitSelection.level.name,
                mapLegendType: this.form.findField('maplegendtype').getValue(),
                method: this.legend.value == G.conf.map_legend_type_automatic ? this.form.findField('method').getValue() : null,
                classes: this.legend.value == G.conf.map_legend_type_automatic ? this.form.findField('classes').getValue() : null,
                bounds: this.legend.value == G.conf.map_legend_type_automatic && this.legend.method == G.conf.classify_with_bounds ? this.form.findField('bounds').getValue() : null,
                colorLow: this.legend.value == G.conf.map_legend_type_automatic ? this.form.findField('startcolor').getValue() : null,
                colorHigh: this.legend.value == G.conf.map_legend_type_automatic ? this.form.findField('endcolor').getValue() : null,
                mapLegendSetId: this.legend.value == G.conf.map_legend_type_predefined ? this.form.findField('maplegendset').getValue() : null,
                radiusLow: null,
                radiusHigh: null,
                longitude: G.vars.map.getCenter().lon,
                latitude: G.vars.map.getCenter().lat,
                zoom: parseFloat(G.vars.map.getZoom())
			};
		},
        
        getImageExportValues: function() {
			return {
				mapValueTypeValue: this.form.findField('mapvaluetype').getValue() == G.conf.map_value_type_indicator ?
					this.form.findField('indicator').getRawValue() : this.form.findField('dataelement').getRawValue(),
				dateValue: G.system.mapDateType.isFixed() ?
					this.form.findField('period').getRawValue() : new Date(this.form.findField('startdate').getRawValue()).format('Y M j') + ' - ' + new Date(this.form.findField('enddate').getRawValue()).format('Y M j')
			};
		},
        
        clearForm: function() {            
            this.form.findField('mapview').clearValue();            
            
            this.form.findField('mapvaluetype').setValue(G.conf.map_value_type_indicator);
            this.valueType.setIndicator();
            this.prepareMapViewValueType();
            this.form.findField('indicatorgroup').clearValue();
            this.form.findField('indicator').clearValue();
            this.form.findField('dataelementgroup').clearValue();
            this.form.findField('dataelement').clearValue();
            
            G.system.mapDateType.setFixed();
            this.prepareMapViewDateType();
            this.form.findField('periodtype').clearValue();
            this.form.findField('period').clearValue();
            this.form.findField('startdate').reset();
            this.form.findField('enddate').reset();
            
            var boundary = this.form.findField('boundary')
            var level = this.form.findField('level');
            boundary.reset();
            level.reset();
            if (boundary.treePanel && level.levelComboBox) {
                boundary.treePanel.selectPath(boundary.treePanel.getRootNode().getPath());
                level.levelComboBox.clearValue();
            }
            
            this.legend.reset();
            this.prepareMapViewLegend();
            this.form.findField('method').setValue(this.legend.method);
            this.form.findField('classes').setValue(this.legend.classes);
            this.form.findField('bounds').reset();
            
            this.layer.destroyFeatures();
            this.layer.setVisibility(false);
        }
	},
    
    loadGeoJson: function() {
        G.vars.mask.msg = G.i18n.loading_geojson;
        G.vars.mask.show();
        
        this.setUrl(G.conf.path_mapping + 'getGeoJson.action?' +
            'parentId=' + this.organisationUnitSelection.parent.id +
            '&level=' + this.organisationUnitSelection.level.level
        );
    },

    classify: function(exception, position) {
        if (this.formValidation.validateForm.apply(this, [exception])) {
            G.vars.mask.msg = G.i18n.aggregating_map_values;
            G.vars.mask.show();
            
            if (!position && this.layer.features.length) {
                G.vars.map.zoomToExtent(this.layer.getDataExtent());
            }
            
            if (this.mapView) {
                if (this.mapView.longitude && this.mapView.latitude && this.mapView.zoom) {
                    var point = G.util.getTransformedPointByXY(this.mapView.longitude, this.mapView.latitude);
                    G.vars.map.setCenter(new OpenLayers.LonLat(point.x, point.y), this.mapView.zoom);
                }
                else {
                    G.vars.map.zoomToExtent(this.layer.getDataExtent());
                }
                this.mapView = false;
            }
            
            if (this.updateValues) {
                var dataUrl = this.valueType.isIndicator() ? 'getIndicatorMapValues' : 'getDataElementMapValues';
                var params = {
                    id: this.valueType.isIndicator() ? this.form.findField('indicator').getValue() : this.form.findField('dataelement').getValue(),
                    periodId: G.system.mapDateType.isFixed() ? this.form.findField('period').getValue() : null,
                    startDate: G.system.mapDateType.isStartEnd() ? new Date(this.form.findField('startdate').getValue()).format('Y-m-d') : null,
                    endDate: G.system.mapDateType.isStartEnd() ? new Date(this.form.findField('enddate').getValue()).format('Y-m-d') : null,
                    parentId: this.organisationUnitSelection.parent.id,
                    level: this.organisationUnitSelection.level.level
                };
                
                Ext.Ajax.request({
                    url: G.conf.path_mapping + dataUrl + G.conf.type,
                    method: 'POST',
                    params: params,
                    scope: this,
                    success: function(r) {
                        var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                        
                        if (!this.layer.features.length) {
                            Ext.message.msg(false, 'No coordinates found');
                            G.vars.mask.hide();
                            return;
                        }
                        
                        if (mapvalues.length === 0) {
                            Ext.message.msg(false, G.i18n.current_selection_no_data);
                            G.vars.mask.hide();
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
                        
                        this.updateValues = false;
                        this.applyValues();
                    }
                });
            }
            else {
                this.applyValues();
            }
        }
    },

    applyValues: function() {
        Ext.getCmp('viewhistory_b').addItem(this);
        
		var options = {
            indicator: 'value',
            method: this.form.findField('method').getValue(),
            numClasses: this.form.findField('classes').getValue(),
            colors: this.getColors()
        };
       
        this.coreComp.applyClassification(options);
        this.classificationApplied = true;
        
        G.vars.mask.hide();
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
