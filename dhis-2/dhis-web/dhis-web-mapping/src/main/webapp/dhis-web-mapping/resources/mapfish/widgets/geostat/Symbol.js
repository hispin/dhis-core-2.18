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
 * @requires core/GeoStat/Symbol.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

/**
 * Class: mapfish.widgets.geostat.Symbol
 * Use this class to create a widget allowing to display choropleths
 * on the map.
 *
 * Inherits from:
 * - {Ext.FormPanel}
 */

mapfish.widgets.geostat.Symbol = Ext.extend(Ext.FormPanel, {

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
    loadMask: false,

    /**
     * APIProperty: labelGenerator
     *     Generator for bin labels
     */
    labelGenerator: null,

    /**
     * Constructor: mapfish.widgets.geostat.Symbol
     *
     * Parameters:
     * config - {Object} Config object.
     */

    /**
     * Method: initComponent
     *    Inits the component
     */
	 
	colorInterpolation: false,
	
	imageLegend: false,
	
	bounds: false,
     
    newUrl: false,
	
	applyPredefinedLegend: function() {
		var mls = Ext.getCmp('maplegendset_cb2').getValue();
		var bounds = [];
		Ext.Ajax.request({
			url: path + 'getMapLegendsByMapLegendSet' + type,
			method: 'POST',
			params: { mapLegendSetId: mls },
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

				proportionalSymbol.colorInterpolation = colors;
				proportionalSymbol.bounds = bounds;
				proportionalSymbol.classify(false);								
			},
			failure: function() {
				alert('Error: getMapLegendsByMapLegendSet');
			}
		});
	},
	
    initComponent : function() {
    
        mapViewStore2 = new Ext.data.JsonStore({
            url: path + 'getAllMapViews' + type,
            root: 'mapViews',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
                        if (PARAMETER) {
                            Ext.Ajax.request({
                                url: path + 'getMapView' + type,
                                method: 'POST',
                                params: { id: PARAMETER },
								success: function(r) {
									PARAMETER = false;
                                    MAPVIEW = Ext.util.JSON.decode(r.responseText).mapView[0];
                                    MAPSOURCE = MAPVIEW.mapSourceType;
									Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                                    Ext.getCmp('mapview_cb2').setValue(MAPVIEW.id);
									
									if (MAPVIEW.mapLegendType == map_legend_type_automatic) {
										Ext.getCmp('maplegendtype_cb2').setValue(map_legend_type_automatic);
										Ext.getCmp('numClasses_cb2').setValue(MAPVIEW.classes);
										Ext.getCmp('colorA_cf2').setValue(MAPVIEW.colorLow);
										Ext.getCmp('colorB_cf2').setValue(MAPVIEW.colorHigh);
										
										Ext.getCmp('method_cb2').showField();
										Ext.getCmp('bounds_tf2').hideField();
										Ext.getCmp('numClasses_cb2').showField();
										Ext.getCmp('colorA_cf2').showField();
										Ext.getCmp('colorB_cf2').showField();
										Ext.getCmp('maplegendset_cb2').hideField();
									}
									else if (MAPVIEW.mapLegendType == map_legend_type_predefined) {
                                        LEGEND[thematicMap2].type = map_legend_type_predefined;
										Ext.getCmp('maplegendtype_cb2').setValue(map_legend_type_predefined);
										Ext.getCmp('method_cb2').hideField();
										Ext.getCmp('bounds_tf2').hideField();
										Ext.getCmp('numClasses_cb2').hideField();
										Ext.getCmp('colorA_cf2').hideField();
										Ext.getCmp('colorB_cf2').hideField();
										Ext.getCmp('maplegendset_cb2').showField();
										
										predefinedMapLegendSetStore2.load();
									}										
										
									MAP.setCenter(new OpenLayers.LonLat(MAPVIEW.longitude, MAPVIEW.latitude), MAPVIEW.zoom);

                                    Ext.getCmp('indicatorgroup_cb2').setValue(MAPVIEW.indicatorGroupId);
                                    
                                    var igId = MAPVIEW.indicatorGroupId;
                                    indicatorStore2.baseParams = { indicatorGroupId: igId, format: 'json' };
                                    indicatorStore2.reload();
                                },
                                failure: function() {
                                  alert( i18n_status , i18n_error_while_retrieving_data );
                                }
                            });
                        }
                    },
                    scope: this
                }
            }
        });
    
        indicatorGroupStore2 = new Ext.data.JsonStore({
            url: path + 'getAllIndicatorGroups' + type,
            root: 'indicatorGroups',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
        
        indicatorStore2 = new Ext.data.JsonStore({
            url: path + 'getIndicatorsByIndicatorGroup' + type,
			baseParams: {indicatorGroupId:0},
            root: 'indicators',
            fields: ['id', 'name', 'shortName'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        indicatorStore2.each(
                            function fn(record) {
                                var name = record.get('name');
                                name = name.replace('&lt;', '<').replace('&gt;', '>');
                                record.set('name', name);
                            },  this
                        );
                        
                        Ext.getCmp('indicator_cb2').clearValue();

                        if (MAPVIEW) {
                            Ext.getCmp('indicator_cb2').setValue(MAPVIEW.indicatorId);
                            Ext.getCmp('periodtype_cb2').setValue(MAPVIEW.periodTypeId);
                            periodStore2.baseParams = {name: MAPVIEW.periodTypeId};
                            periodStore2.reload();
                        }
                    }
                }
            }
        });
		
		dataElementGroupStore2 = new Ext.data.JsonStore({
			url: path + 'getAllDataElementGroups' + type,
            root: 'dataElementGroups',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
		
		dataElementStore2 = new Ext.data.JsonStore({
            url: path + 'getDataElementsByDataElementGroup' + type,
            root: 'dataElements',
            fields: ['id', 'name', 'shortName'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        dataElementStore2.each(
                        function fn(record) {
                                var name = record.get('name');
                                name = name.replace('&lt;', '<').replace('&gt;', '>');
                                record.set('name', name);
                            },  this
                        );
                        
                        Ext.getCmp('dataelement_cb2').clearValue();

                        if (MAPVIEW) {
                            Ext.getCmp('dataelement_cb2').setValue(MAPVIEW.dataElementId);
                            Ext.getCmp('periodtype_cb2').setValue(MAPVIEW.periodTypeId);
                            periodStore2.baseParams = {name: MAPVIEW.periodTypeId};
                            periodStore2.reload();
                        }
                    },
                    scope: this
                }
            }
        });
        
        periodTypeStore2 = new Ext.data.JsonStore({
            url: path + 'getAllPeriodTypes' + type,
            root: 'periodTypes',
            fields: ['name'],
            autoLoad: true
        });
            
        periodStore2 = new Ext.data.JsonStore({
            url: path + 'getPeriodsByPeriodType' + type,
            baseParams: { name: 0 },
            root: 'periods',
            fields: ['id', 'name'],
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        if (MAPVIEW) {
                            Ext.getCmp('period_cb2').setValue(MAPVIEW.periodId);
                            var mst = MAPVIEW.mapSourceType;

                            Ext.Ajax.request({
                                url: path + 'setMapSourceTypeUserSetting' + type,
                                method: 'POST',
                                params: { mapSourceType: mst },
								success: function(r) {
                                    Ext.getCmp('map_cb2').getStore().reload();
                                    Ext.getCmp('maps_cb').getStore().reload();
                                    
                                    Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                                },
                                failure: function() {
                                    alert( 'Error: setMapSourceTypeUserSetting' );
                                }
                            });
                            
                            this.newUrl = MAPVIEW.mapSource;
                        }
                    },
                    scope: this
                }
            }
        });
            
        mapStore2 = new Ext.data.JsonStore({
            url: path + 'getAllMaps' + type,
            baseParams: { format: 'jsonmin' },
            root: 'maps',
            fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel'],
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
                        if (MAPVIEW) {
                            Ext.getCmp('map_cb2').setValue(MAPVIEW.mapSource);
                            proportionalSymbol.classify(false, true);
                        }
                    }
                }
            }
        });
		
		predefinedMapLegendSetStore2 = new Ext.data.JsonStore({
            url: path + 'getMapLegendSetsByType' + type,
            baseParams: { type: map_legend_type_predefined },
            root: 'mapLegendSets',
            fields: ['id', 'name'],
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
						if (MAPVIEW) {
							Ext.Ajax.request({
								url: path + 'getMapLegendSet' + type,
								method: 'POST',
								params: { id: MAPVIEW.mapLegendSetId },
								success: function(r) {
									var mls = Ext.util.JSON.decode(r.responseText).mapLegendSet[0];
									Ext.getCmp('maplegendset_cb2').setValue(mls.id);
									proportionalSymbol.applyPredefinedLegend();
								},
								failure: function() {
									alert('Error: getMapLegendSet');
								}
							});
						}
                    }
                }
            }
        });
        
        this.items = [
         
        {
            xtype: 'combo',
            id: 'mapview_cb2',
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
			labelSeparator: labelseparator,
            width: combo_width,
            store: mapViewStore2,
            listeners: {
                'select': {
                    fn: function() {
                        var mId = Ext.getCmp('mapview_cb2').getValue();
                        
                        Ext.Ajax.request({
                            url: path + 'getMapView' + type,
                            method: 'POST',
                            params: { id: mId },
                            success: function(r) {
                                MAPVIEW = Ext.util.JSON.decode(r.responseText).mapView[0];
								MAPSOURCE = MAPVIEW.mapSourceType;
                                
                                Ext.getCmp('mapvaluetype_cb2').setValue(MAPVIEW.mapValueType);
								VALUETYPE.polygon = MAPVIEW.mapValueType;
                                
                                if (MAPVIEW.mapValueType == map_value_type_indicator) {
                                    Ext.getCmp('indicatorgroup_cb2').showField();
                                    Ext.getCmp('indicator_cb2').showField();
                                    Ext.getCmp('dataelementgroup_cb2').hideField();
                                    Ext.getCmp('dataelement_cb2').hideField();
                                    
                                    Ext.getCmp('indicatorgroup_cb2').setValue(MAPVIEW.indicatorGroupId);
                                    indicatorStore2.baseParams = { indicatorGroupId: MAPVIEW.indicatorGroupId };
                                    indicatorStore2.reload();
                                }
                                else if (MAPVIEW.mapValueType == map_value_type_dataelement) {
                                    Ext.getCmp('indicatorgroup_cb2').hideField();
                                    Ext.getCmp('indicator_cb2').hideField();
                                    Ext.getCmp('dataelementgroup_cb2').showField();
                                    Ext.getCmp('dataelement_cb2').showField();
                                    
                                    Ext.getCmp('dataelementgroup_cb2').setValue(MAPVIEW.dataElementGroupId);
                                    dataElementStore2.baseParams = { dataElementGroupId: MAPVIEW.dataElementGroupId };
                                    dataElementStore2.reload();
                                }                                        
								
								if (MAPVIEW.mapLegendType == map_legend_type_automatic) {
									Ext.getCmp('maplegendtype_cb2').setValue(map_legend_type_automatic);
									Ext.getCmp('numClasses_cb2').setValue(MAPVIEW.classes);
									Ext.getCmp('colorA_cf2').setValue(MAPVIEW.colorLow);
									Ext.getCmp('colorB_cf2').setValue(MAPVIEW.colorHigh);
									
									Ext.getCmp('method_cb2').showField();
									Ext.getCmp('bounds_tf2').hideField();
									Ext.getCmp('numClasses_cb2').showField();
									Ext.getCmp('colorA_cf2').showField();
									Ext.getCmp('colorB_cf2').showField();
									Ext.getCmp('maplegendset_cb2').hideField();
								}
								else if (MAPVIEW.mapLegendType == map_legend_type_predefined) {
                                    LEGEND[thematicMap2].type = map_legend_type_predefined;
									Ext.getCmp('maplegendtype_cb2').setValue(map_legend_type_predefined);
									Ext.getCmp('method_cb2').hideField();
									Ext.getCmp('bounds_tf2').hideField();
									Ext.getCmp('numClasses_cb2').hideField();
									Ext.getCmp('colorA_cf2').hideField();
									Ext.getCmp('colorB_cf2').hideField();
									Ext.getCmp('maplegendset_cb2').showField();
									
                                    Ext.getCmp('maplegendset_cb2').setValue(MAPVIEW.mapLegendSetId);
                                    proportionalSymbol.applyPredefinedLegend();
								}
                            },
                            failure: function() {
                              alert( i18n_status , i18n_error_while_retrieving_data );
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
			id: 'mapvaluetype_cb2',
            fieldLabel: i18n_mapvaluetype,
			labelSeparator: labelseparator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'local',
            triggerAction: 'all',
            width: combo_width,
			value: map_value_type_indicator,
            store: new Ext.data.SimpleStore({
                fields: ['id', 'name'],
                data: [[map_value_type_indicator, 'Indicators'], [map_value_type_dataelement, 'Data elements']]
            }),
			listeners: {
				'select': {
					fn: function() {
						if (Ext.getCmp('mapvaluetype_cb2').getValue() == map_value_type_indicator) {
							Ext.getCmp('indicatorgroup_cb2').showField();
							Ext.getCmp('indicator_cb2').showField();
							Ext.getCmp('dataelementgroup_cb2').hideField();
							Ext.getCmp('dataelement_cb2').hideField();
							VALUETYPE.point = map_value_type_indicator;
						}
						else if (Ext.getCmp('mapvaluetype_cb2').getValue() == map_value_type_dataelement) {
							Ext.getCmp('indicatorgroup_cb2').hideField();
							Ext.getCmp('indicator_cb2').hideField();
							Ext.getCmp('dataelementgroup_cb2').showField();
							Ext.getCmp('dataelement_cb2').showField();
							VALUETYPE.point = map_value_type_dataelement;
						}
					}
				}
			}
		},
        
        {
            xtype: 'combo',
            id: 'indicatorgroup_cb2',
            fieldLabel: i18n_indicator_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: indicatorGroupStore2,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue()) {
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
						
						Ext.getCmp('indicator_cb2').clearValue();
						indicatorStore2.setBaseParam('indicatorGroupId', this.getValue());
                        indicatorStore2.reload();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'indicator_cb2',
            fieldLabel: i18n_indicator ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: indicatorStore2,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue()) {
                            Ext.getCmp('mapview_cb2').reset();
                        }
 
                        var iId = Ext.getCmp('indicator_cb2').getValue();
                        
                        Ext.Ajax.request({
                            url: path + 'getMapLegendSetByIndicator' + type,
                            method: 'POST',
                            params: { indicatorId: iId, format: 'json' },

                            success: function( responseObject ) {
                                var data = Ext.util.JSON.decode(responseObject.responseText);
                                
                                if (data.mapLegendSet[0].id != '') {
//                                    Ext.getCmp('method_cb2').setValue(data.mapLegendSet[0].method);
                                    Ext.getCmp('numClasses_cb2').setValue(data.mapLegendSet[0].classes);

                                    Ext.getCmp('colorA_cf2').setValue(data.mapLegendSet[0].colorLow);
                                    Ext.getCmp('colorB_cf2').setValue(data.mapLegendSet[0].colorHigh);
                                }
                                
                                proportionalSymbol.classify(false);
                            },
                            failure: function()
                            {
                              alert( i18n_status , i18n_error_while_retrieving_data );
                            } 
                        });
                    },
                    scope: this
                }
            }
        },
		
		{
            xtype: 'combo',
            id: 'dataelementgroup_cb2',
            fieldLabel: i18n_dataelement_group,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: dataElementGroupStore2,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue()) {
                            Ext.getCmp('mapview_cb2').reset();
                        }
                        Ext.getCmp('dataelement_cb2').reset();
						dataElementStore2.setBaseParam('dataElementGroupId', this.getValue());
                        dataElementStore2.reload();
                    }
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'dataelement_cb2',
            fieldLabel: i18n_dataelement ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'shortName',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: dataElementStore2,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue()) {
                            Ext.getCmp('mapview_cb2').reset();
                        }
						
						proportionalSymbol.classify(false);
 
                        // var iId = Ext.getCmp('dataelement_cb2').getValue();
                        
						/* TODO legend set
						
                        Ext.Ajax.request({
                            url: path + 'getMapLegendSetByIndicator' + type,
                            method: 'POST',
                            params: { indicatorId: iId },

                            success: function( responseObject ) {
                                var data = Ext.util.JSON.decode(responseObject.responseText);
                                
                                if (data.mapLegendSet[0].id != '') {
                                   // Ext.getCmp('method_cb2').setValue(data.mapLegendSet[0].method);
                                    Ext.getCmp('numClasses_cb2').setValue(data.mapLegendSet[0].classes);

                                    Ext.getCmp('colorA_cf2').setValue(data.mapLegendSet[0].colorLow);
                                    Ext.getCmp('colorB_cf2').setValue(data.mapLegendSet[0].colorHigh);
                                }
                                
                                proportionalSymbol.classify(false);
                            },
                            failure: function()
                            {
                              alert( i18n_status , i18n_error_while_retrieving_data );
                            } 
                        });
						*/
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'periodtype_cb2',
            fieldLabel: i18n_period_type,
            typeAhead: true,
            editable: false,
            valueField: 'name',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: periodTypeStore2,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue() != '') {
                            Ext.getCmp('mapview_cb2').reset();
                        }
                        
                        var pt = Ext.getCmp('periodtype_cb2').getValue();
                        Ext.getCmp('period_cb2').getStore().baseParams = { name: pt, format: 'json' };
                        Ext.getCmp('period_cb2').getStore().reload();
                    },
                    scope: this
                }
            }
        },

        {
            xtype: 'combo',
            id: 'period_cb2',
            fieldLabel: i18n_period ,
            typeAhead: true,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: periodStore2,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue() != '') {
                            Ext.getCmp('mapview_cb2').reset();
                        }
                        
                        this.classify(false);
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'combo',
            id: 'map_cb2',
            fieldLabel: i18n_map ,
            typeAhead: true,
            editable: false,
            valueField: 'mapLayerPath',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            triggerAction: 'all',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            selectOnFocus: true,
            width: combo_width,
            store: mapStore2,
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue() != '') {
                            Ext.getCmp('mapview_cb2').reset();
                        }
                        
                        this.newUrl = Ext.getCmp('map_cb2').getValue();
                        this.classify(false);
                    },
                    scope: this
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_type ,
            id: 'maplegendtype_cb2',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            value: LEGEND[thematicMap2].type,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[map_legend_type_automatic, i18n_automatic],
					[map_legend_type_predefined, i18n_predefined]
				]
            }),
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('maplegendtype_cb2').getValue() == map_legend_type_predefined && Ext.getCmp('maplegendtype_cb2').getValue() != LEGEND[thematicMap2].type ) {
							LEGEND[thematicMap2].type = map_legend_type_predefined;
							Ext.getCmp('method_cb2').hideField();
							Ext.getCmp('bounds_tf2').hideField();
                            Ext.getCmp('numClasses_cb2').hideField();
							Ext.getCmp('colorA_cf2').hideField();
							Ext.getCmp('colorB_cf2').hideField();
							Ext.getCmp('maplegendset_cb2').showField();
							
							if (Ext.getCmp('maplegendset_cb2').getValue()) {
								this.classify(false);
							}
                        }
                        else if (Ext.getCmp('maplegendtype_cb2').getValue() == map_legend_type_automatic && Ext.getCmp('maplegendtype_cb2').getValue() != LEGEND[thematicMap2].type) {
							LEGEND[thematicMap2].type = map_legend_type_automatic;
							Ext.getCmp('method_cb2').showField();
							if (Ext.getCmp('method_cb2').getValue() == 0) {
								Ext.getCmp('bounds_tf2').showField();
								Ext.getCmp('numClasses_cb2').hideField();
							}
							else {
								Ext.getCmp('bounds_tf2').hideField();
								Ext.getCmp('numClasses_cb2').showField();
							}
							Ext.getCmp('colorA_cf2').showField();
							Ext.getCmp('colorB_cf2').showField();
							Ext.getCmp('maplegendset_cb2').hideField();
                            
                            this.classify(false);
                        }
                    },
                    scope: this
                }
            }
        },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_set,
            id: 'maplegendset_cb2',
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            triggerAction: 'all',
            width: combo_width,
			hidden: true,
            store: predefinedMapLegendSetStore2,
            listeners: {
                'select': {
                    fn: function() {
						proportionalSymbol.applyPredefinedLegend();
                    },
                    scope: this
                }
            }
        },

        {
            xtype: 'combo',
            fieldLabel: i18n_method,
            id: 'method_cb2',
            editable: false,
            valueField: 'value',
            displayField: 'text',
            mode: 'local',
            emptyText: emptytext,
			labelSeparator: labelseparator,
            value: LEGEND[thematicMap2].method,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value', 'text'],
                data: [
					[1, i18n_equal_intervals],
					[2, i18n_equal_group_count],
					[0, i18n_fixed_breaks]
				]
            }),
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('method_cb2').getValue() == 0 && Ext.getCmp('method_cb2').getValue() != LEGEND[thematicMap2].method) {
							LEGEND[thematicMap2].method = 0;
                            Ext.getCmp('bounds_tf2').showField();
                            Ext.getCmp('numClasses_cb2').hideField();
                        }
                        else if (Ext.getCmp('method_cb2').getValue() != LEGEND[thematicMap2].method) {
							LEGEND[thematicMap2].method = Ext.getCmp('method_cb2').getValue();
                            Ext.getCmp('bounds_tf2').hideField();
                            Ext.getCmp('numClasses_cb2').showField();
                            
                            this.classify(false);
                        }
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'bounds_tf2',
            fieldLabel: i18n_bounds,
			labelSeparator: labelseparator,
            emptyText: i18n_comma_separated_values,
            isFormField: true,
            width: combo_width,
            hidden: true
        },
        
        {
            xtype: 'combo',
            fieldLabel: i18n_classes ,
			labelSeparator: labelseparator,
            id: 'numClasses_cb2',
            editable: false,
            valueField: 'value',
            displayField: 'value',
            mode: 'local',
            value: LEGEND[thematicMap2].classes,
            triggerAction: 'all',
            width: combo_width,
            store: new Ext.data.SimpleStore({
                fields: ['value'],
                data: [[1], [2], [3], [4], [5], [6], [7]]
            }),
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('mapview_cb2').getValue() != '') {
                            Ext.getCmp('mapview_cb2').reset();
                        }
						
						if (Ext.getCmp('numClasses_cb2').getValue() != LEGEND[thematicMap2].classes) {
							LEGEND[thematicMap2].classes = Ext.getCmp('numClasses_cb2').getValue();
							this.classify(false);
						}
                    },
                    scope: this
                }
            }
        },

        {
            xtype: 'colorfield',
            fieldLabel: i18n_low_color,
			labelSeparator: labelseparator,
            id: 'colorA_cf2',
            allowBlank: false,
            isFormField: true,
            width: combo_width,
            value: "#FFFF00"
        },
        
        {
            xtype: 'colorfield',
            fieldLabel: i18n_high_color,
			labelSeparator: labelseparator,
            id: 'colorB_cf2',
            allowBlank: false,
            isFormField: true,
            width: combo_width,
            value: "#FF0000"
        },
        
        { html: '<br>' },

        {
            xtype: 'button',
			cls: 'aa_med',
            isFormField: true,
            fieldLabel: '',
            labelSeparator: labelseparator,
            text: i18n_refresh,
            handler: function() {
                this.layer.setVisibility(true);
                this.classify(true);
            },
            scope: this
        }

        ];
	
		mapfish.widgets.geostat.Symbol.superclass.initComponent.apply(this);
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
        OpenLayers.Console.error( i18n_ajax_request_failed );
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
        colorA.setFromHex(Ext.getCmp('colorA_cf2').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf2').getValue());
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
    classify: function(exception, position) {
        if (!this.ready) {
            Ext.MessageBox.alert( i18n_error , i18n_component_init_not_complete );
            return;
        }
		
		if (Ext.getCmp('maplegendtype_cb2').getValue() == map_legend_type_automatic) {
			Ext.getCmp('maplegendset_cb2').hideField();
		}
		else if (Ext.getCmp('maplegendtype_cb2').getValue() == map_legend_type_predefined) {
			Ext.getCmp('maplegendset_cb2').showField();
		}
        
        if (this.newUrl) {
            URL = this.newUrl;
				
            if (MAPSOURCE == map_source_type_database) {
                if (URL == FACILITY_LEVEL) {
                    this.setUrl(path + 'getPointShapefile.action?level=' + URL);
                }
                else {
                    this.setUrl(path + 'getPolygonShapefile.action?level=' + URL);
                }
            }
            else if (MAPSOURCE == map_source_type_geojson) {
                this.setUrl(path + 'getGeoJson.action?name=' + URL);
            }
			else if (MAPSOURCE == map_source_type_shapefile) {
				this.setUrl(path_geoserver + wfs + URL + output);
			}
        }
        
        var cb = Ext.getCmp('mapvaluetype_cb2').getValue() == map_value_type_indicator ? Ext.getCmp('indicator_cb2').getValue : Ext.getCmp('dataelement_cb2').getValue;
                
        if (!cb ||
            !Ext.getCmp('period_cb2').getValue() ||
            !Ext.getCmp('map_cb2').getValue() ) {
            if (exception) {
                Ext.messageRed.msg( i18n_thematic_map , i18n_form_is_not_complete );
            }
            return;
        }

		MASK.msg = i18n_loading;
        MASK.show();

		if (!this.newUrl) {
			loadMapData(thematicMap2, position);
		}
    },

    /**
     * Method: onRender
     * Called by EXT when the component is rendered.
     */
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Symbol.superclass.onRender.apply(this, arguments);
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

        this.coreComp = new mapfish.GeoStat.Symbol(this.map, coreOptions);
    }   
});

Ext.reg('proportionalSymbol', mapfish.widgets.geostat.Symbol);