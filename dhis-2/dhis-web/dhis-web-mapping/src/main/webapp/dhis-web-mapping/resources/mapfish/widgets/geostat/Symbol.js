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
 * @requires core/GeoStat/Symbol.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

mapfish.widgets.geostat.Symbol = Ext.extend(Ext.FormPanel, {

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
        
        if (GLOBAL.vars.parameter) {
			if (GLOBAL.vars.parameter.mapView.featureType == GLOBAL.conf.map_feature_type_point) {
				this.mapView = GLOBAL.vars.parameter.mapView;
				this.updateValues = true;
				this.legend = {
					value: this.mapView.mapLegendType,
					method: this.mapView.method || this.legend.method,
					classes: this.mapView.classes || this.legend.classes
				};

				GLOBAL.vars.map.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
				
				function mapViewStoreCallback() {
					this.form.findField('mapview').setValue(this.mapView.id);
					this.valueType.value = this.mapView.mapValueType;
					this.form.findField('mapvaluetype').setValue(this.valueType.value);
					this.setMapView();
				}
				
				if (GLOBAL.stores.pointMapView.isLoaded) {
					mapViewStoreCallback.call(this);
				}
				else {
					GLOBAL.stores.pointMapView.load({scope: this, callback: function() {
						mapViewStoreCallback.call(this);
					}});
				}
			}
        }
        
		mapfish.widgets.geostat.Symbol.superclass.initComponent.apply(this);
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
        var startColor = new mapfish.ColorRgb();
        startColor.setFromHex(this.form.findField('startcolor').getValue());
        var endColor = new mapfish.ColorRgb();
        endColor.setFromHex(this.form.findField('endcolor').getValue());
        return [startColor, endColor];
    },
    
    initProperties: function() {
        this.legend = {
            value: GLOBAL.conf.map_legend_type_automatic,
            method: GLOBAL.conf.classify_by_equal_intervals,
            classes: 5
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
                this.level.name = GLOBAL.stores.organisationUnitLevel.getAt(
                    GLOBAL.stores.organisationUnitLevel.find('level', this.level.level)).data.name;
                
                return [this.parent.name, this.level.name];
            }                
        };
        
        this.valueType = {
            value: GLOBAL.conf.map_value_type_indicator,
            setIndicator: function() {
                this.value = GLOBAL.conf.map_value_type_indicator;
            },
            setDatElement: function() {
                this.value = GLOBAL.conf.map_value_type_dataelement;
            },
            isIndicator: function() {
                return this.value == GLOBAL.conf.map_value_type_indicator;
            },
            isDataElement: function() {
                return this.value == GLOBAL.conf.map_value_type_dataelement;
            }
        };
    },
    
    createItems: function() {
        this.items = [
        {
            xtype: 'combo',
            name: 'mapview',
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
			labelSeparator: GLOBAL.conf.labelseparator,
            width: GLOBAL.conf.combo_width,
            store: GLOBAL.stores.pointMapView,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        this.mapView = GLOBAL.stores.pointMapView.getAt(GLOBAL.stores.pointMapView.find('id', cb.getValue())).data;
                        this.updateValues = true;
                        
                        this.legend.value = this.mapView.mapLegendType;
                        this.legend.method = this.mapView.method || this.legend.method;
                        this.legend.classes = this.mapView.classes || this.legend.classes;

                        GLOBAL.vars.map.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
                        GLOBAL.vars.mapDateType.value = this.mapView.mapDateType;
                        Ext.getCmp('mapdatetype_cb').setValue(GLOBAL.vars.mapDateType.value);

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
            fieldLabel: i18n_mapvaluetype,
			labelSeparator: GLOBAL.conf.labelseparator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all',
            width: GLOBAL.conf.combo_width,
			value: GLOBAL.conf.map_value_type_indicator,
            store: new Ext.data.ArrayStore({
                fields: ['id', 'name'],
                data: [
                    [GLOBAL.conf.map_value_type_indicator, 'Indicator'],
                    [GLOBAL.conf.map_value_type_dataelement, 'Data element']
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
            fieldLabel: i18n_indicator_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBAL.conf.combo_width,
            store: GLOBAL.stores.indicatorGroup,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
						this.form.findField('indicator').clearValue();
                        GLOBAL.stores.indicatorsByGroup.setBaseParam('indicatorGroupId', cb.getValue());
                        GLOBAL.stores.indicatorsByGroup.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            name: 'indicator',
            fieldLabel: i18n_indicator,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBAL.conf.combo_width,
            store: GLOBAL.stores.indicatorsByGroup,
            currentValue: null,
            keepPosition: false,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (GLOBAL.util.setCurrentValue.call(this, cb, 'mapview')) {
                            return;
                        }
                        
                        this.updateValues = true;
                        Ext.Ajax.request({
                            url: GLOBAL.conf.path_mapping + 'getMapLegendSetByIndicator' + GLOBAL.conf.type,
                            method: 'POST',
                            params: {indicatorId: cb.getValue()},
                            scope: this,
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    this.legend.value = GLOBAL.conf.map_legend_type_predefined;
                                    this.prepareMapViewLegend();
                                    
                                    function load() {
                                        this.form.findField('maplegendset').setValue(mapLegendSet.id);
                                        this.applyPredefinedLegend();
                                    }
                                    
                                    if (!GLOBAL.stores.predefinedMapLegendSet.isLoaded) {
                                        GLOBAL.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                                            load.call(this);
                                        }});
                                    }
                                    else {
                                        load.call(this);
                                    }
                                }
                                else {
                                    this.legend.value = GLOBAL.conf.map_legend_type_automatic;
                                    this.prepareMapViewLegend();
                                    this.classify(false, cb.keepPosition);
                                    GLOBAL.util.setKeepPosition(cb);
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
            fieldLabel: i18n_dataelement_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBAL.conf.combo_width,
            store: GLOBAL.stores.dataElementGroup,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        this.form.findField('dataelement').clearValue();
						GLOBAL.stores.dataElementsByGroup.setBaseParam('dataElementGroupId', cb.getValue());
                        GLOBAL.stores.dataElementsByGroup.load();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            name: 'dataelement',
            fieldLabel: i18n_dataelement,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBAL.conf.combo_width,
            store: GLOBAL.stores.dataElementsByGroup,
            keepPosition: false,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (GLOBAL.util.setCurrentValue.call(this, cb, 'mapview')) {
                            return;
                        }
                        
                        this.updateValues = true;
                        Ext.Ajax.request({
                            url: GLOBAL.conf.path_mapping + 'getMapLegendSetByDataElement' + GLOBAL.conf.type,
                            method: 'POST',
                            params: {dataElementId: cb.getValue()},
                            scope: this,
                            success: function(r) {
                                var mapLegendSet = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
                                if (mapLegendSet.id) {
                                    this.legend.value = GLOBAL.conf.map_legend_type_predefined;
                                    this.prepareMapViewLegend();
                                    
                                    function load() {
                                        this.form.findField('maplegendset').setValue(mapLegendSet.id);
                                        this.applyPredefinedLegend();
                                    }
                                    
                                    if (!GLOBAL.stores.predefinedMapLegendSet.isLoaded) {
                                        GLOBAL.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                                            load.call(this);
                                        }});
                                    }
                                    else {
                                        load.call(this);
                                    }
                                }
                                else {
                                    this.legend.value = GLOBAL.conf.map_legend_type_automatic;
                                    this.prepareMapViewLegend();
                                    this.classify(false, cb.keepPosition);
                                    GLOBAL.util.setKeepPosition(cb);
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
            fieldLabel: i18n_period_type,
            typeAhead: true,
            editable: false,
            valueField: 'name',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBAL.conf.combo_width,
            store: GLOBAL.stores.periodType,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        GLOBAL.stores.periodsByTypeStore.setBaseParam('name', cb.getValue());
                        GLOBAL.stores.periodsByTypeStore.load();
                    }
                }
            }
        },

        {
            xtype: 'combo',
            name: 'period',
            fieldLabel: i18n_period,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBAL.conf.combo_width,
            store: GLOBAL.stores.periodsByTypeStore,
            keepPosition: false,
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (GLOBAL.util.setCurrentValue.call(this, cb, 'mapview')) {
                            return;
                        }
                        
                        this.updateValues = true;
                        this.classify(false, cb.keepPosition);                        
                        GLOBAL.util.setKeepPosition(cb);
                    }
                }
            }
        },
        
        {
            xtype: 'datefield',
            name: 'startdate',
            fieldLabel: i18n_start_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            width: GLOBAL.conf.combo_width,
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
            fieldLabel: i18n_end_date,
            format: 'Y-m-d',
            hidden: true,
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            width: GLOBAL.conf.combo_width,
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
            fieldLabel: i18n_boundary,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            selectOnFocus: true,
            width: GLOBAL.conf.combo_width,
            node: {attributes: {hasChildrenWithCoordinates: false}},
            selectedNode: null,
            style: 'cursor:pointer',
            listeners: {
                'focus': {
                    scope: this,
                    fn: function(tf) {
                        function showTree() {
                            var value, rawvalue;
                            var w = new Ext.Window({
                                id: 'tree_w2',
                                title: 'Boundary',
                                closeAction: 'hide',
                                autoScroll: true,
                                height: 'auto',
                                autoHeight: true,
                                width: 280,
                                boxMaxWidth: 280,
                                items: [
									{
										xtype: 'treepanel',
										height: screen.height / 3,
										bodyStyle: 'padding: 8px',
										autoScroll: true,
										loader: new Ext.tree.TreeLoader({
											dataUrl: GLOBAL.conf.path_mapping + 'getOrganisationUnitChildren' + GLOBAL.conf.type
										}),
										root: {
											id: GLOBAL.vars.topLevelUnit.id,
											text: GLOBAL.vars.topLevelUnit.name,
											hasChildrenWithCoordinates: GLOBAL.vars.topLevelUnit.hasChildrenWithCoordinates,
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
											}
										}
									},
                                    {
                                        xtype: 'panel',
                                        layout: 'table',
                                        items: [
                                            {
                                                xtype: 'button',
                                                text: i18n_select,
                                                width: 133,
                                                scope: this,
                                                handler: function() {
                                                    var node = this.form.findField('boundary').selectedNode;
                                                    if (!node) {
                                                        return;
                                                    }
                                                    
                                                    this.form.findField('mapview').clearValue();
                                                    this.updateValues = true;
                                                    this.organisationUnitSelection.setValues(node.attributes.id, node.attributes.text, node.attributes.level, null, null);
                                                        
                                                    this.form.findField('boundary').setValue(node.attributes.text);
                                                    Ext.getCmp('tree_w2').hide();
                                                    
                                                    this.loadGeoJson();
                                                }
                                            },
                                            {
                                                xtype: 'button',
                                                text: i18n_cancel,
                                                width: 133,
                                                scope: this,
                                                handler: function() {
                                                    Ext.getCmp('tree_w2').hide();
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

                        if (GLOBAL.vars.topLevelUnit) {
                            showTree.call(this);
                        }
                        else {
                            Ext.Ajax.request({
                                url: GLOBAL.conf.path_commons + 'getOrganisationUnits' + GLOBAL.conf.type,
                                params: {level: 1},
                                method: 'POST',
                                scope: this,
                                success: function(r) {
                                    var rootNode = Ext.util.JSON.decode(r.responseText).organisationUnits[0];
                                    GLOBAL.vars.topLevelUnit = {
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
        
        {
            xtype: 'textfield',
            name: 'level',
            disabled: true,
            disabledClass: 'combo-disabled',
            fieldLabel: i18n_level,
            editable: false,
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            width: GLOBAL.conf.combo_width,
            level: null,
            levelName: null
        },
        
        { html: '<div class="thematic-br">' },
		
		{
            xtype: 'combo',
            name: 'maplegendtype',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            fieldLabel: i18n_legend_type,
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            value: this.legend.value,
            triggerAction: 'all',
            width: GLOBAL.conf.combo_width,
            store: new Ext.data.ArrayStore({
                fields: ['value', 'text'],
                data: [
					[GLOBAL.conf.map_legend_type_automatic, i18n_automatic],
					[GLOBAL.conf.map_legend_type_predefined, i18n_predefined]
				]
            }),
            listeners: {
                'select': {
                    scope: this,
                    fn: function(cb) {
                        if (cb.getValue() == GLOBAL.conf.map_legend_type_predefined && cb.getValue() != this.legend.value) {
							this.legend.value = GLOBAL.conf.map_legend_type_predefined;
                            this.prepareMapViewLegend();
							
							if (this.form.findField('maplegendset').getValue()) {
                                this.applyPredefinedLegend();
							}
                        }
                        else if (cb.getValue() == GLOBAL.conf.map_legend_type_automatic && cb.getValue() != this.legend.value) {
							this.legend.value = GLOBAL.conf.map_legend_type_automatic;
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
            fieldLabel: i18n_legendset,
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            triggerAction: 'all',
            width: GLOBAL.conf.combo_width,
			hidden: true,
            store: GLOBAL.stores.predefinedMapLegendSet,
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
            fieldLabel: i18n_method,
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: GLOBAL.conf.emptytext,
			labelSeparator: GLOBAL.conf.labelseparator,
            value: this.legend.method,
            triggerAction: 'all',
            width: GLOBAL.conf.combo_width,
            store: new Ext.data.ArrayStore({
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
                        this.form.findField('mapview').clearValue();
                        if (cb.getValue() == GLOBAL.conf.classify_with_bounds && cb.getValue() != this.legend.method) {
							this.legend.method = GLOBAL.conf.classify_with_bounds;
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
            fieldLabel: i18n_bounds,
			labelSeparator: GLOBAL.conf.labelseparator,
            emptyText: i18n_comma_separated_values,
            width: GLOBAL.conf.combo_width,
            hidden: true
        },
        
        {
            xtype: 'combo',
            name: 'classes',
            fieldLabel: i18n_classes,
			labelSeparator: GLOBAL.conf.labelseparator,
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: this.legend.classes,
            triggerAction: 'all',
            width: GLOBAL.conf.combo_width,
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
            fieldLabel: i18n_low_color,
			labelSeparator: GLOBAL.conf.labelseparator,
            allowBlank: false,
            width: GLOBAL.conf.combo_width,
            value: "#FFFF00"
        },
        
        {
            xtype: 'colorfield',
            name: 'endcolor',
            fieldLabel: i18n_high_color,
			labelSeparator: GLOBAL.conf.labelseparator,
            allowBlank: false,
            width: GLOBAL.conf.combo_width,
            value: "#FF0000"
        },
        
        { html: '<div class="thematic-br">' },
        
        {
            xtype: 'numberfield',
            name: 'radiuslow',
            fieldLabel: 'Low radius',
			labelSeparator: GLOBAL.conf.labelseparator,
            width: GLOBAL.conf.combo_number_width_small,
            value: 5
        },
        
        {
            xtype: 'numberfield',
            name: 'radiushigh',
            fieldLabel: 'High radius',
			labelSeparator: GLOBAL.conf.labelseparator,
            width: GLOBAL.conf.combo_number_width_small,
            value: 20
        },
        
        { html: '<div class="thematic-br">' },

        {
            xtype: 'button',
            text: i18n_refresh,
            isFormField: true,
            fieldLabel: GLOBAL.conf.emptytext,
            labelSeparator: GLOBAL.conf.labelseparator,
            scope: this,
            handler: function() {
                if (this.formValidation.validateForm(true)) {
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
        var scope = this;
        
        var onHoverSelect = function onHoverSelect(feature) {
            if (feature.attributes.name) {
                Ext.getCmp('featureinfo_l').setText('<div style="color:black">' + feature.attributes.name + '</div><div style="color:#555">' + feature.attributes.value + '</div>', false);
            }
            else {
                Ext.getCmp('featureinfo_l').setText('', false);
            }
        };
        
        var onHoverUnselect = function onHoverUnselect(feature) {
            if (feature.attributes.name) {
                Ext.getCmp('featureinfo_l').setText('<span style="color:#666">' + i18n_no_feature_selected + '</span>', false);
            }
            else {
                Ext.getCmp('featureinfo_l').setText('', false);
            }
        };
        
        var onClickSelect = function onClickSelect(feature) {
            if (feature.attributes.hasChildrenWithCoordinates) {
                if (GLOBAL.vars.locateFeatureWindow) {
                    GLOBAL.vars.locateFeatureWindow.destroy();
                }
                         
                scope.updateValues = true;
                scope.isDrillDown = true;
                
                function organisationUnitLevelCallback() {
                    var names = this.organisationUnitSelection.setValuesOnDrillDown(feature.attributes.id, feature.attributes.name);
                    this.form.findField('boundary').setValue(names[0]);
                    this.form.findField('level').setValue(names[1]);
                    this.loadGeoJson();
                }
                
                if (GLOBAL.stores.organisationUnitLevel.isLoaded) {
                    organisationUnitLevelCallback.call(scope);
                }
                else {
                    GLOBAL.stores.organisationUnitLevel.load({scope: scope, callback: function() {
                        organisationUnitLevelCallback.call(this);
                    }});
                }
            }
            else {
                Ext.message.msg(false, i18n_no_coordinates_found);
            }
        };
        
        this.selectFeatures = new OpenLayers.Control.newSelectFeature(
            this.layer, {
                onHoverSelect: onHoverSelect,
                onHoverUnselect: onHoverUnselect,
                onClickSelect: onClickSelect
            }
        );
        
        GLOBAL.vars.map.addControl(this.selectFeatures);
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
                valueTypeGroup: GLOBAL.stores.indicatorGroup,
                valueType: GLOBAL.stores.indicatorsByGroup
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
                valueTypeGroup: GLOBAL.stores.dataElementGroup,
                valueType: GLOBAL.stores.dataElementsByGroup
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
        if (GLOBAL.vars.mapDateType.isFixed()) {
            this.form.findField('periodtype').showField();
            this.form.findField('period').showField();
            this.form.findField('startdate').hideField();
            this.form.findField('enddate').hideField();
            obj.components = {
                c1: this.form.findField('periodtype'),
                c2: this.form.findField('period')
            };
            obj.stores = {
                c1: GLOBAL.stores.periodType,
                c2: GLOBAL.stores.periodsByTypeStore
            };
            obj.mapView = {
                c1: 'periodTypeId',
                c2: 'periodId'
            };
        }
        else if (GLOBAL.vars.mapDateType.isStartEnd()) {
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
        
        if (this.legend.value == GLOBAL.conf.map_legend_type_automatic) {
            this.form.findField('method').showField();
            this.form.findField('startcolor').showField();
            this.form.findField('endcolor').showField();
            this.form.findField('maplegendset').hideField();
            
            if (this.legend.method == GLOBAL.conf.classify_with_bounds) {
                this.form.findField('classes').hideField();
                this.form.findField('bounds').showField();
            }
            else {
                this.form.findField('classes').showField();
                this.form.findField('bounds').hideField();
            }                
        }
        else if (this.legend.value == GLOBAL.conf.map_legend_type_predefined) {
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
                if (GLOBAL.vars.mapDateType.isFixed()) {
                    if (obj.stores.c1.isLoaded) {
                        dateTypeGroupStoreCallback.call(this);
                    }
                    else {
                        obj.stores.c1.load({scope: this, callback: function() {
                            dateTypeGroupStoreCallback.call(this);
                        }});
                    }
                }
                else if (GLOBAL.vars.mapDateType.isStartEnd()) {
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
        
        this.form.findField('radiuslow').setValue(this.mapView.radiusLow);
        this.form.findField('radiushigh').setValue(this.mapView.radiusHigh);

        if (this.legend.value == GLOBAL.conf.map_legend_type_automatic) {
            this.form.findField('method').setValue(this.mapView.method);
            this.form.findField('startcolor').setValue(this.mapView.colorLow);
            this.form.findField('endcolor').setValue(this.mapView.colorHigh);

            if (this.legend.method == GLOBAL.conf.classify_with_bounds) {
                this.form.findField('bounds').setValue(this.mapView.bounds);
            }
            else {
                this.form.findField('classes').setValue(this.mapView.classes);
            }

            this.setMapViewMap();
        }
        else if (this.legend.value == GLOBAL.conf.map_legend_type_predefined) {
            if (GLOBAL.stores.predefinedMapLegendSet.isLoaded) {
                predefinedMapLegendSetStoreCallback.call(this);
            }
            else {
                GLOBAL.stores.predefinedMapLegendSet.load({scope: this, callback: function() {
                    predefinedMapLegendSetStoreCallback.call(this);
                }});
            }
        }
    },
    
    setMapViewMap: function() {
        this.organisationUnitSelection.setValues(this.mapView.parentOrganisationUnitId, this.mapView.parentOrganisationUnitName,
            this.mapView.parentOrganisationUnitLevel, this.mapView.organisationUnitLevel, this.mapView.organisationUnitLevelName);
        
        this.form.findField('boundary').setValue(this.mapView.parentOrganisationUnitName);
        this.form.findField('level').setValue(this.mapView.organisationUnitLevelName);

        GLOBAL.vars.activePanel.setPoint();
        this.loadGeoJson();
    },
	
	applyPredefinedLegend: function(isMapView) {
        this.legend.value = GLOBAL.conf.map_legend_type_predefined;
		var mls = this.form.findField('maplegendset').getValue();
		var bounds = [];
		Ext.Ajax.request({
			url: GLOBAL.conf.path_mapping + 'getMapLegendsByMapLegendSet' + GLOBAL.conf.type,
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
            var scope = symbol;
            if (scope.form.findField('mapvaluetype').getValue() == GLOBAL.conf.map_value_type_indicator) {
                if (!scope.form.findField('indicator').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                    }
                    return false;
                }
            }
            else if (scope.form.findField('mapvaluetype').getValue() == GLOBAL.conf.map_value_type_dataelement) {
                if (!scope.form.findField('dataelement').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                    }
                    return false;
                }
            }

            if (GLOBAL.vars.mapDateType.isFixed()) {
                if (!scope.form.findField('period').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                    }
                    return false;
                }
            }
            else {
                if (!scope.form.findField('startdate').getValue() || !scope.form.findField('enddate').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                    }
                    return false;
                }
            }

            if (!scope.form.findField('boundary').getValue() || !scope.form.findField('level').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }

            if (scope.form.findField('maplegendtype').getValue() == GLOBAL.conf.map_legend_type_automatic) {
                if (scope.form.findField('method').getValue() == GLOBAL.conf.classify_with_bounds) {
                    if (!scope.form.findField('bounds').getValue()) {
                        if (exception) {
                            Ext.message.msg(false, i18n_form_is_not_complete);
                        }
                        return false;
                    }
                }
            }
            else if (scope.form.findField('maplegendtype').getValue() == GLOBAL.conf.map_legend_type_predefined) {
                if (!scope.form.findField('maplegendset').getValue()) {
                    if (exception) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                    }
                    return false;
                }
            }
            
            if (!scope.form.findField('radiuslow').getValue() || !scope.form.findField('radiushigh').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
            
            return true;
        },
        
        validateLevel: function(exception) {
            var scope = symbol;
            if (scope.mapView || scope.idDrillDown) {
                return true;
            }
            
            if (scope.form.findField('boundary').getValue() && scope.form.findField('level').getValue()) {
                if (scope.organisationUnitSelection.parent.level <= scope.organisationUnitSelection.level.level) {

                    return true;
                }
                else {
                    if (exception) {
                        Ext.message.msg(false, 'Level is higher than boundary level');
                    }
                    return false;
                }
            }

            return false;
        }
    },
    
    formValues: {
		getAllFormValues: function() {
			return {
				featureType: GLOBAL.conf.map_feature_type_point,
				mapValueType: this.form.findField('mapvaluetype').getValue(),
				indicatorGroupId: this.form.findField('indicatorgroup').getValue(),
				indicatorId: this.form.findField('indicator').getValue(),
				dataElementGroupId: this.form.findField('dataelementgroup').getValue(),
				dataElementId: this.form.findField('dataelement').getValue(),
				periodTypeId: this.form.findField('periodtype').getValue(),
				periodId: this.form.findField('period').getValue(),
				startDate: this.form.findField('startdate').getValue(),
				endDate: this.form.findField('enddate').getValue(),
				parentOrganisationUnitId: this.organisationUnitSelection.parent.id,
				organisationUnitLevel: this.organisationUnitSelection.level.level,
				mapLegendType: this.form.findField('maplegendtype').getValue(),
				method: this.legend.value == GLOBAL.conf.map_legend_type_automatic ? this.form.findField('method').getValue() : null,
				classes: this.legend.value == GLOBAL.conf.map_legend_type_automatic ? this.form.findField('classes').getValue() : null,
				bounds: this.legend.value == GLOBAL.conf.map_legend_type_automatic && this.legend.method == GLOBAL.conf.classify_with_bounds ? this.form.findField('bounds').getValue() : null,
				colorLow: this.legend.value == GLOBAL.conf.map_legend_type_automatic ? this.form.findField('startcolor').getValue() : null,
				colorHigh: this.legend.value == GLOBAL.conf.map_legend_type_automatic ? this.form.findField('endcolor').getValue() : null,
				mapLegendSetId: this.form.findField('maplegendset').getValue(),
				radiusLow: this.form.findField('radiuslow').getValue(),
				radiusHigh: this.form.findField('radiushigh').getValue(),
				longitude: GLOBAL.vars.map.getCenter().lon,
				latitude: GLOBAL.vars.map.getCenter().lat,
				zoom: parseFloat(GLOBAL.vars.map.getZoom())
			};
		},
		
		getImageExportValues: function() {
			return {
				mapValueTypeValue: this.form.findField('mapvaluetype').getValue() == GLOBAL.conf.map_value_type_indicator ?
					this.form.findField('indicator').getRawValue() : this.form.findField('dataelement').getRawValue(),
				dateValue: GLOBAL.vars.mapDateType.isFixed() ?
					this.form.findField('period').getRawValue() : new Date(this.form.findField('startdate').getRawValue()).format('Y M j') + ' - ' + new Date(this.form.findField('enddate').getRawValue()).format('Y M j')
			};
		}
	},
    
    loadGeoJson: function() {
        function load() {
            GLOBAL.vars.mask.msg = i18n_loading_geojson;
            GLOBAL.vars.mask.show();
            
            this.setUrl(GLOBAL.conf.path_mapping + 'getGeoJson.action?' +
                'parentId=' + this.organisationUnitSelection.parent.id +
                '&level=' + this.organisationUnitSelection.level.level
            );
        }
        
        if (this.isDrillDown || this.mapView) {
            load.call(this);
        }
        else { //TODO
            load.call(this);
        }
    },

    classify: function(exception, position) {
        if (this.formValidation.validateForm(exception)) {
            GLOBAL.vars.mask.msg = i18n_aggregating_map_values;
            GLOBAL.vars.mask.show();
            
            if (!position) {
                GLOBAL.vars.map.zoomToExtent(this.layer.getDataExtent());
            }
            
            if (this.mapView) {
                if (this.mapView.longitude && this.mapView.latitude && this.mapView.zoom) {
                    GLOBAL.vars.map.setCenter(new OpenLayers.LonLat(this.mapView.longitude, this.mapView.latitude), this.mapView.zoom);
                }
                else {
                    GLOBAL.vars.map.zoomToExtent(this.layer.getDataExtent());
                }
                this.mapView = false;
            }
            
            if (this.updateValues) {                
                var dataUrl = this.valueType.isIndicator() ? 'getIndicatorMapValues' : 'getDataElementMapValues';                
                var params = {
                    id: this.valueType.isIndicator() ? this.form.findField('indicator').getValue() : this.form.findField('dataelement').getValue(),
                    periodId: GLOBAL.vars.mapDateType.isFixed() ? this.form.findField('period').getValue() : null,
                    startDate: GLOBAL.vars.mapDateType.isStartEnd() ? new Date(this.form.findField('startdate').getValue()).format('Y-m-d') : null,
                    endDate: GLOBAL.vars.mapDateType.isStartEnd() ? new Date(this.form.findField('enddate').getValue()).format('Y-m-d') : null,
                    parentId: this.organisationUnitSelection.parent.id,
                    level: this.organisationUnitSelection.level.level
                };

                Ext.Ajax.request({
                    url: GLOBAL.conf.path_mapping + dataUrl + GLOBAL.conf.type,
                    method: 'POST',
                    params: params,
                    scope: this,
                    success: function(r) {
                        var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                        GLOBAL.vars.exportValues = GLOBAL.util.getExportDataValueJSON(mapvalues);
                        
                        if (mapvalues.length === 0) {
                            Ext.message.msg(false, i18n_current_selection_no_data);
                            GLOBAL.vars.mask.hide();
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
		var options = {
            indicator: 'value',
            method: this.form.findField('method').getValue(),
            numClasses: this.form.findField('classes').getValue(),
            colors: this.getColors(),
            minSize: parseInt(this.form.findField('radiuslow').getValue()),
            maxSize: parseInt(this.form.findField('radiushigh').getValue())
		};

		this.coreComp.updateOptions(options);       
        this.coreComp.applyClassification();
        this.classificationApplied = true;
        
        GLOBAL.vars.mask.hide();
    },
    
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Symbol.superclass.onRender.apply(this, arguments);
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

        this.coreComp = new mapfish.GeoStat.Symbol(this.map, coreOptions);
        
        if (GLOBAL.vars.parameter) {
			choropleth.collapse();
			this.expand();
			GLOBAL.vars.parameter = false;
		}
    }
});

Ext.reg('symbol', mapfish.widgets.geostat.Symbol);
