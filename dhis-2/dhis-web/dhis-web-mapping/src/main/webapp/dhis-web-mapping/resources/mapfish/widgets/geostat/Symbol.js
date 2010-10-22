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
    
    parentId: false,
    
    newUrl: false,
	
	applyPredefinedLegend: function() {
		var mls = Ext.getCmp('maplegendset_cb2').getValue();
		var bounds = [];
		Ext.Ajax.request({
			url: path_mapping + 'getMapLegendsByMapLegendSet' + type,
			method: 'POST',
			params: { mapLegendSetId: mls },
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

				proportionalSymbol.colorInterpolation = colors;
				proportionalSymbol.bounds = bounds;
				proportionalSymbol.classify(false);
			},
			failure: function() {
				alert('Error: getMapLegendsByMapLegendSet');
			}
		});
	},
    
    isFormComplete: function() {
        if (!Ext.getCmp('indicator_cb2').getValue() && !Ext.getCmp('dataelement_cb2').getValue()) {
            return false;
        }
        if (!Ext.getCmp('period_cb2').getValue()) {
            return false;
        }
        if (!Ext.getCmp('map_cb2').getValue() && !Ext.getCmp('map_tf2').getValue()) {
            return false;
        }
        if (Ext.getCmp('maplegendtype_cb2').getValue() == map_legend_type_predefined) {
            if (!Ext.getCmp('maplegendset_cb2').getValue()) {
                return false;
            }
        }
        else {
            if (Ext.getCmp('method_cb2').getValue() == classify_with_bounds) {
                if (!Ext.getCmp('bounds_tf2').getValue()) {
                    return false;
                }
            }
        }
        
        return true;
    },
	
    initComponent : function() {
    
        mapViewStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getAllMapViews' + type,
            root: 'mapViews',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
                        if (PARAMETER) {
                            Ext.Ajax.request({
                                url: path_mapping + 'getMapView' + type,
                                method: 'POST',
                                params: { id: PARAMETER },
								success: function(r) {
									PARAMETER = false;
                                    MAPVIEW = getNumericMapView(Ext.util.JSON.decode(r.responseText).mapView[0]);
                                    MAPSOURCE = MAPVIEW.mapSourceType;
                                    MAP.setCenter(new OpenLayers.LonLat(MAPVIEW.longitude, MAPVIEW.latitude), MAPVIEW.zoom);
									
									Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                                    Ext.getCmp('mapview_cb2').setValue(MAPVIEW.id);
                                    VALUETYPE.point = MAPVIEW.mapValueType;
									
									if (MAPVIEW.mapLegendType == map_legend_type_automatic) {
                                        LEGEND[thematicMap2].type = map_legend_type_automatic;
                                        Ext.getCmp('maplegendtype_cb2').setValue(map_legend_type_automatic);
                                        Ext.getCmp('maplegendset_cb2').hideField();
                                        Ext.getCmp('method_cb2').showField();
                                        Ext.getCmp('method_cb2').setValue(MAPVIEW.method);
                                        Ext.getCmp('colorA_cf2').showField();
                                        Ext.getCmp('colorA_cf2').setValue(MAPVIEW.colorLow);
                                        Ext.getCmp('colorB_cf2').showField();
                                        Ext.getCmp('colorB_cf2').setValue(MAPVIEW.colorHigh);
                                        
                                        if (MAPVIEW.method == classify_with_bounds) {
                                            Ext.getCmp('numClasses_cb2').hideField();
                                            Ext.getCmp('bounds_tf2').showField();
                                            Ext.getCmp('bounds_tf2').setValue(MAPVIEW.bounds);
                                        }
                                        else {
                                            Ext.getCmp('bounds_tf2').hideField();
                                            Ext.getCmp('numClasses_cb2').showField();
                                            Ext.getCmp('numClasses_cb2').setValue(MAPVIEW.classes);
                                        }
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
										
										predefinedMapLegendSetStore2.load();
									}
									
									Ext.getCmp('mapvaluetype_cb2').setValue(MAPVIEW.mapValueType);
										
									if (MAPVIEW.mapValueType == map_value_type_indicator) {
                                        Ext.getCmp('indicator_cb2').showField();
                                        Ext.getCmp('indicatorgroup_cb2').showField();
                                        Ext.getCmp('dataelementgroup_cb2').hideField();
                                        Ext.getCmp('dataelement_cb2').hideField();

                                        Ext.getCmp('indicatorgroup_cb2').setValue(MAPVIEW.indicatorGroupId);
                                    
                                        indicatorStore2.setBaseParam('indicatorGroupId', MAPVIEW.indicatorGroupId);
                                        indicatorStore2.load();
                                    }
                                    else if (MAPVIEW.mapValueType == map_value_type_dataelement) {
                                        Ext.getCmp('indicator_cb2').hideField();
                                        Ext.getCmp('indicatorgroup_cb2').hideField();
                                        Ext.getCmp('dataelementgroup_cb2').showField();
                                        Ext.getCmp('dataelement_cb2').showField();

                                        Ext.getCmp('dataelementgroup_cb2').setValue(MAPVIEW.dataElementGroupId);
                                    
                                        dataElementStore2.setBaseParam('dataElementGroupId', MAPVIEW.dataElementGroupId);
                                        dataElementStore2.load();
                                    }
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
            url: path_mapping + 'getAllIndicatorGroups' + type,
            root: 'indicatorGroups',
            fields: ['id', 'name'],
            idProperty: 'id',
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
        
        indicatorStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getIndicatorsByIndicatorGroup' + type,
            root: 'indicators',
            fields: ['id', 'name', 'shortName'],
            idProperty: 'id',
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
                            },
                            this
                        );
                        
                        Ext.getCmp('indicator_cb2').clearValue();

                        if (MAPVIEW) {
                            Ext.getCmp('indicator_cb2').setValue(MAPVIEW.indicatorId);
                            Ext.getCmp('periodtype_cb2').setValue(MAPVIEW.periodTypeId);
                            periodStore2.setBaseParam('name', MAPVIEW.periodTypeId);
                            periodStore2.load();
                        }
                    }
                }
            }
        });
		
		dataElementGroupStore2 = new Ext.data.JsonStore({
			url: path_mapping + 'getAllDataElementGroups' + type,
            root: 'dataElementGroups',
            fields: ['id', 'name'],
            sortInfo: { field: 'name', direction: 'ASC' },
            autoLoad: true
        });
		
		dataElementStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getDataElementsByDataElementGroup' + type,
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
                            periodStore2.setBaseParam('name', MAPVIEW.periodTypeId);
                            periodStore2.load();
                        }
                    },
                    scope: this
                }
            }
        });
        
        periodTypeStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getAllPeriodTypes' + type,
            root: 'periodTypes',
            fields: ['name'],
            autoLoad: true
        });
            
        periodStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getPeriodsByPeriodType' + type,
            root: 'periods',
            fields: ['id', 'name'],
            autoLoad: false,
            listeners: {
                'load': {
                    fn: function() {
                        if (MAPVIEW) {
                            Ext.getCmp('period_cb2').setValue(MAPVIEW.periodId);

                            Ext.Ajax.request({
                                url: path_mapping + 'setMapSourceTypeUserSetting' + type,
                                method: 'POST',
                                params: { mapSourceType: MAPVIEW.mapSourceType },
								success: function(r) {
                                    Ext.getCmp('map_cb2').getStore().load();
                                    Ext.getCmp('maps_cb').getStore().load();
                                    Ext.getCmp('mapsource_cb').setValue(MAPSOURCE);
                                },
                                failure: function() {
                                    alert( 'Error: setMapSourceTypeUserSetting' );
                                }
                            });
                        }
                    }
                }
            }
        });
            
        mapStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getAllMaps' + type,
            baseParams: { format: 'jsonmin' },
            root: 'maps',
            fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel'],
            idProperty: 'mapLayerPath',
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
                        if (MAPVIEW) {
                            if (MAPSOURCE == map_source_type_database) {
                                Ext.Ajax.request({
                                    url: path_commons + 'getOrganisationUnit' + type,
                                    method: 'POST',
                                    params: {id:MAPVIEW.mapSource},
                                    success: function(r) {
                                        var name = Ext.util.JSON.decode(r.responseText).organisationUnit.name;
                                        Ext.getCmp('map_tf2').setValue(name);
                                        Ext.getCmp('map_tf2').value = MAPVIEW.mapSource;
                                        proportionalSymbol.loadFromDatabase(MAPVIEW.mapSource);
                                    },
                                    failure: function() {
                                        alert('Error: getOrganisationUnit');
                                    }
                                });
                            }
                            else {
                                Ext.getCmp('map_cb2').setValue(MAPVIEW.mapSource);
                                proportionalSymbol.loadFromFile(MAPVIEW.mapSource);
                            }
                        }
                    }
                }
            }
        });
		
		predefinedMapLegendSetStore2 = new Ext.data.JsonStore({
            url: path_mapping + 'getMapLegendSetsByType' + type,
            baseParams: { type: map_legend_type_predefined },
            root: 'mapLegendSets',
            fields: ['id', 'name'],
            autoLoad: true,
            listeners: {
                'load': {
                    fn: function() {
						if (MAPVIEW) {
							Ext.Ajax.request({
								url: path_mapping + 'getMapLegendSet' + type,
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
                            url: path_mapping + 'getMapView' + type,
                            method: 'POST',
                            params: { id: mId },
                            success: function(r) {
                                MAPVIEW = getNumericMapView(Ext.util.JSON.decode(r.responseText).mapView[0]);
								MAPSOURCE = MAPVIEW.mapSourceType;
                                
                                Ext.getCmp('mapvaluetype_cb2').setValue(MAPVIEW.mapValueType);
								VALUETYPE.point = MAPVIEW.mapValueType;
                                
                                if (MAPVIEW.mapValueType == map_value_type_indicator) {
                                    Ext.getCmp('indicatorgroup_cb2').showField();
                                    Ext.getCmp('indicator_cb2').showField();
                                    Ext.getCmp('dataelementgroup_cb2').hideField();
                                    Ext.getCmp('dataelement_cb2').hideField();
                                    
                                    Ext.getCmp('indicatorgroup_cb2').setValue(MAPVIEW.indicatorGroupId);
                                    indicatorStore2.setBaseParam('indicatorGroupId', MAPVIEW.indicatorGroupId);
                                    indicatorStore2.load();
                                }
                                else if (MAPVIEW.mapValueType == map_value_type_dataelement) {
                                    Ext.getCmp('indicatorgroup_cb2').hideField();
                                    Ext.getCmp('indicator_cb2').hideField();
                                    Ext.getCmp('dataelementgroup_cb2').showField();
                                    Ext.getCmp('dataelement_cb2').showField();
                                    
                                    Ext.getCmp('dataelementgroup_cb2').setValue(MAPVIEW.dataElementGroupId);
                                    dataElementStore2.setBaseParam('dataElementGroupId', MAPVIEW.dataElementGroupId);
                                    dataElementStore2.load();
                                }                                        
								
								if (MAPVIEW.mapLegendType == map_legend_type_automatic) {
                                    LEGEND[thematicMap2].type = map_legend_type_automatic;
									Ext.getCmp('maplegendtype_cb2').setValue(map_legend_type_automatic);
                                    Ext.getCmp('maplegendset_cb2').hideField();
									Ext.getCmp('method_cb2').showField();
                                    Ext.getCmp('method_cb2').setValue(MAPVIEW.method);
                                    Ext.getCmp('colorA_cf2').showField();
									Ext.getCmp('colorA_cf2').setValue(MAPVIEW.colorLow);
                                    Ext.getCmp('colorB_cf2').showField();
									Ext.getCmp('colorB_cf2').setValue(MAPVIEW.colorHigh);
                                    
                                    if (MAPVIEW.method == classify_with_bounds) {
                                        Ext.getCmp('numClasses_cb2').hideField();
                                        Ext.getCmp('bounds_tf2').showField();
                                        Ext.getCmp('bounds_tf2').setValue(MAPVIEW.bounds);
                                    }
                                    else {
                                        Ext.getCmp('bounds_tf2').hideField();
                                        Ext.getCmp('numClasses_cb2').showField();
                                        Ext.getCmp('numClasses_cb2').setValue(MAPVIEW.classes);
                                    }
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
                        
                        proportionalSymbol.classify(false, true);
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
                        indicatorStore2.load();
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
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
 
                        var iId = Ext.getCmp('indicator_cb2').getValue();
                        
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapLegendSetByIndicator' + type,
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
                                
                                proportionalSymbol.classify(false, true);
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
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
                        Ext.getCmp('dataelement_cb2').clearValue();
						dataElementStore2.setBaseParam('dataElementGroupId', this.getValue());
                        dataElementStore2.load();
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
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
						
						proportionalSymbol.classify(false, true);
 
                        // var iId = Ext.getCmp('dataelement_cb2').getValue();
                        
						/* TODO legend set
						
                        Ext.Ajax.request({
                            url: path_mapping + 'getMapLegendSetByIndicator' + type,
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
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
                        
                        Ext.getCmp('period_cb2').clearValue();
                        Ext.getCmp('period_cb2').getStore().setBaseParam('name', this.getValue());
                        Ext.getCmp('period_cb2').getStore().load();
                    }
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
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
                        
                        this.classify(false, true);
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
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
                        
                        if (Ext.getCmp('map_cb2').getValue() != proportionalSymbol.newUrl) {
                            proportionalSymbol.loadFromFile(Ext.getCmp('map_cb2').getValue());
                        }
                    },
                    scope: this
                }
            }
        },
        
        {
            xtype: 'textfield',
            id: 'map_tf2',
            fieldLabel: i18n_parent_orgunit,
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
            listeners: {
                'focus': {
                    fn: function() {
                        function showTree() {
                            var value, rawvalue;
                            var w = new Ext.Window({
                                id: 'orgunit_w2',
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
                                        id: 'orgunit_tp2',
                                        bodyStyle: 'padding:7px',
                                        height: getMultiSelectHeight(),
                                        autoScroll: true,
                                        loader: new Ext.tree.TreeLoader({
                                            dataUrl: path_mapping + 'getOrganisationUnitChildren' + type
                                        }),
                                        root: {
                                            id: TOPLEVELUNIT.id,
                                            text: TOPLEVELUNIT.name,
                                            nodeType: 'async',
                                            draggable: false,
                                            expanded: true
                                        },
                                        listeners: {
                                            'click': {
                                                fn: function(n) {
                                                    if (n.hasChildNodes()) {
                                                        Ext.getCmp('map_tf2').setValue(n.attributes.text);
                                                        Ext.getCmp('map_tf2').value = n.attributes.id;
                                                        Ext.getCmp('map_tf2').node = n;
                                                    }
                                                }
                                            },
                                            'expandnode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w2').syncSize();
                                                }
                                            },
                                            'collapsenode': {
                                                fn: function(n) {
                                                    Ext.getCmp('orgunit_w2').syncSize();
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
                                                handler: function() {
                                                    if (Ext.getCmp('map_tf2').getValue() && Ext.getCmp('map_tf2').getValue() != choropleth.parentId) {
                                                        proportionalSymbol.loadFromDatabase(Ext.getCmp('map_tf2').value);
                                                    }
                                                    Ext.getCmp('orgunit_w2').hide();
                                                }
                                            },
                                            {
                                                xtype: 'button',
                                                text: 'Cancel',
                                                width: 133,
                                                handler: function() {
                                                    Ext.getCmp('orgunit_w2').hide();
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
                        
                        if (TOPLEVELUNIT.id) {
                            showTree();
                        }
                        else {
                            Ext.Ajax.request({
                                url: path_commons + 'getOrganisationUnits' + type,
                                params: { level: 1 },
                                method: 'POST',
                                success: function(r) {
                                    var rootNode = Ext.util.JSON.decode(r.responseText).organisationUnits[0];
                                    TOPLEVELUNIT.id = rootNode.id;
                                    TOPLEVELUNIT.name = rootNode.name;
                                    
                                    showTree();          
                                },
                                failure: function(r) {
                                    alert('getOrganisationUnits');
                                }
                            });
                        }
                    },
                    scope: this
                }
            }
        },
        
        { html: '<br>' },
		
		{
            xtype: 'combo',
            fieldLabel: i18n_legend_type,
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
								this.classify(false, true);
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
                            
                            this.classify(false, true);
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
					[2, i18n_equal_intervals],
					[3, i18n_equal_group_count],
					[1, i18n_fixed_breaks]
				]
            }),
            listeners: {
                'select': {
                    fn: function() {
                        if (Ext.getCmp('method_cb2').getValue() == classify_with_bounds && Ext.getCmp('method_cb2').getValue() != LEGEND[thematicMap2].method) {
							LEGEND[thematicMap2].method = classify_with_bounds;
                            Ext.getCmp('bounds_tf2').showField();
                            Ext.getCmp('numClasses_cb2').hideField();
                        }
                        else if (Ext.getCmp('method_cb2').getValue() != LEGEND[thematicMap2].method) {
							LEGEND[thematicMap2].method = Ext.getCmp('method_cb2').getValue();
                            Ext.getCmp('bounds_tf2').hideField();
                            Ext.getCmp('numClasses_cb2').showField();
                            
                            this.classify(false, true);
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
                            Ext.getCmp('mapview_cb2').clearValue();
                        }
						
						if (Ext.getCmp('numClasses_cb2').getValue() != LEGEND[thematicMap2].classes) {
							LEGEND[thematicMap2].classes = Ext.getCmp('numClasses_cb2').getValue();
							this.classify(false, true);
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
            labelSeparator: '',
            text: i18n_refresh,
            handler: function() {
                this.layer.setVisibility(true);
                this.classify(true, true);
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

    requestSuccess: function(request) {
        this.ready = true;

        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
    },

    requestFailure: function(request) {
        OpenLayers.Console.error( i18n_ajax_request_failed );
    },
    
    getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(Ext.getCmp('colorA_cf2').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf2').getValue());
        return [colorA, colorB];
    },
    
    loadFromDatabase: function(id) {
        if (id != proportionalSymbol.parentId || MAPVIEW) {
            MASK.msg = i18n_loading_geojson;
            MASK.show();

            proportionalSymbol.parentId = id;
            proportionalSymbol.setUrl(path_mapping + 'getGeoJson.action?parentId=' + proportionalSymbol.parentId);
        }
    },
    
    loadFromFile: function(url) {
        if (url != proportionalSymbol.newUrl) {
            proportionalSymbol.newUrl = url;

            if (MAPSOURCE == map_source_type_geojson) {
                proportionalSymbol.setUrl(path_mapping + 'getGeoJsonFromFile.action?name=' + url);
            }
			else if (MAPSOURCE == map_source_type_shapefile) {
				proportionalSymbol.setUrl(path_geoserver + wfs + url + output);
			}
        }
        else {
            proportionalSymbol.classify(false, true);
        }
    },
    
    displayMapLegendTypeFields: function() {
        if (LEGEND[thematicMap2].type == map_legend_type_automatic) {
			Ext.getCmp('maplegendset_cb2').hideField();
		}
		else if (LEGEND[thematicMap2].type == map_legend_type_predefined) {
			Ext.getCmp('maplegendset_cb2').showField();
		}
    },
    
    validateForm: function(exception) {
        if (Ext.getCmp('mapvaluetype_cb2').getValue() == map_value_type_indicator) {
            if (!Ext.getCmp('indicator_cb2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        else if (Ext.getCmp('mapvaluetype_cb2').getValue() == map_value_type_dataelement) {
            if (!Ext.getCmp('dataelement_cb2').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_form_is_not_complete);
                }
                return false;
            }
        }
        
        var cmp = MAPSOURCE == map_source_type_database ? Ext.getCmp('map_tf2') : Ext.getCmp('map_cb2');
        
        if (!Ext.getCmp('period_cb2').getValue() || !cmp.getValue()) {
            if (exception) {
                Ext.message.msg(false, i18n_form_is_not_complete);
            }
            return false;
        }
        
        return true;
    },
    
    getIndicatorOrDataElementId: function() {
        return VALUETYPE.point == map_value_type_indicator ?
            Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue();
    },
    
    applyValues: function() {
        var options = {};
        proportionalSymbol.indicator = options.indicator = 'value';
        options.method = Ext.getCmp('method_cb2').getValue();
        options.numClasses2 = Ext.getCmp('numClasses_cb2').getValue();
        options.colors = proportionalSymbol.getColors();
        
        proportionalSymbol.coreComp.updateOptions(options);
        proportionalSymbol.coreComp.applyClassification();
        proportionalSymbol.classificationApplied = true;
    
        MASK.hide();
    },

    classify: function(exception, position) {
        if (MAPSOURCE == map_source_type_database) {
            proportionalSymbol.classifyDatabase(exception, position);
        }
        else {
            proportionalSymbol.classifyFile(exception, position);
        }
    },
    
    classifyDatabase: function(exception, position) {
		proportionalSymbol.displayMapLegendTypeFields();
        if (proportionalSymbol.validateForm(exception)) {
        
            MASK.msg = i18n_aggregating_map_values;
            MASK.show();        
            
            MAPDATA[ACTIVEPANEL].name = Ext.getCmp('map_tf2').getValue();
            MAPDATA[ACTIVEPANEL].nameColumn = 'name';
            MAPDATA[ACTIVEPANEL].longitude = BASECOORDINATE.longitude;
            MAPDATA[ACTIVEPANEL].latitude = BASECOORDINATE.latitude;
            MAPDATA[ACTIVEPANEL].zoom = 7;
            
            if (!position) {
                if (MAPDATA[ACTIVEPANEL].zoom != MAP.getZoom()) {
                    MAP.zoomTo(MAPDATA[ACTIVEPANEL].zoom);
                }
                MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude));
            }
            
            if (MAPVIEW) {
                if (MAPVIEW.longitude && MAPVIEW.latitude && MAPVIEW.zoom) {
                    MAP.setCenter(new OpenLayers.LonLat(MAPVIEW.longitude, MAPVIEW.latitude), MAPVIEW.zoom);
                }
                else {
                    MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude), MAPDATA[ACTIVEPANEL].zoom);
                }
                MAPVIEW = false;
            }
            
            var pointLayer = MAP.getLayersByName('Point layer')[0];
            FEATURE[thematicMap2] = pointLayer.features;
            
            if (LABELS[thematicMap2]) {
                toggleFeatureLabelsPoints(false, pointLayer);
            }
            
            var indicatorOrDataElementId = VALUETYPE.point == map_value_type_indicator ?
                Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue();
            var dataUrl = VALUETYPE.point == map_value_type_indicator ?
                'getIndicatorMapValuesByParentOrganisationUnit' : 'getDataMapValuesByParentOrganisationUnit';
            var periodId = Ext.getCmp('period_cb2').getValue();
            var parentId = proportionalSymbol.parentId;
            
            Ext.Ajax.request({
                url: path_mapping + dataUrl + type,
                method: 'POST',
                params: {id:indicatorOrDataElementId, periodId:periodId, parentId:parentId},
                success: function(r) {
                    var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                    EXPORTVALUES = getExportDataValueJSON(mapvalues);
                    
                    if (mapvalues.length == 0) {
                        Ext.message.msg(false, i18n_current_selection_no_data );
                        MASK.hide();
                        return;
                    }
                    
                    for (var i = 0; i < mapvalues.length; i++) {
                        for (var j = 0; j < FEATURE[thematicMap2].length; j++) {
                            if (mapvalues[i].orgUnitName == FEATURE[thematicMap2][j].attributes.name) {
                                FEATURE[thematicMap2][j].attributes.value = parseFloat(mapvalues[i].value);
                                break;
                            }
                        }
                    }
                    
                    proportionalSymbol.applyValues();
                },
                failure: function(r) {
                    alert('Error: ' + dataUrl);
                }
            });
        }
    },
    
    classifyFile: function(exception, position) {
		proportionalSymbol.displayMapLegendTypeFields();
        if (proportionalSymbol.validateForm(exception)) {
        
            MASK.msg = i18n_aggregating_map_values;
            MASK.show();
            
            Ext.Ajax.request({
                url: path_mapping + 'getMapByMapLayerPath' + type,
                method: 'POST',
                params: { mapLayerPath: proportionalSymbol.newUrl },
                success: function(r) {
                    MAPDATA[ACTIVEPANEL] = Ext.util.JSON.decode(r.responseText).map[0];
                    
                    MAPDATA[ACTIVEPANEL].organisationUnitLevel = parseFloat(MAPDATA[ACTIVEPANEL].organisationUnitLevel);
                    MAPDATA[ACTIVEPANEL].longitude = parseFloat(MAPDATA[ACTIVEPANEL].longitude);
                    MAPDATA[ACTIVEPANEL].latitude = parseFloat(MAPDATA[ACTIVEPANEL].latitude);
                    MAPDATA[ACTIVEPANEL].zoom = parseFloat(MAPDATA[ACTIVEPANEL].zoom);
                    
                    if (!position) {
                        if (MAPDATA[ACTIVEPANEL].zoom != MAP.getZoom()) {
                            MAP.zoomTo(MAPDATA[ACTIVEPANEL].zoom);
                        }
                        MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude));
                    }
                    
                    if (MAPVIEW) {
                        if (MAPVIEW.longitude && MAPVIEW.latitude && MAPVIEW.zoom) {
                            MAP.setCenter(new OpenLayers.LonLat(MAPVIEW.longitude, MAPVIEW.latitude), MAPVIEW.zoom);
                        }
                        else {
                            MAP.setCenter(new OpenLayers.LonLat(MAPDATA[ACTIVEPANEL].longitude, MAPDATA[ACTIVEPANEL].latitude), MAPDATA[ACTIVEPANEL].zoom);
                        }
                        MAPVIEW = false;
                    }
            
                    var pointLayer = MAP.getLayersByName('Point layer')[0];
                    FEATURE[thematicMap2] = pointLayer.features;
                    
                    if (LABELS[thematicMap2]) {
                        toggleFeatureLabelsPoints(false, pointLayer);
                    }
            
                    var indicatorOrDataElementId = VALUETYPE.point == map_value_type_indicator ?
                        Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue();
                    var dataUrl = VALUETYPE.point == map_value_type_indicator ?
                        'getIndicatorMapValuesByMap' : 'getDataMapValuesByMap';
                    var periodId = Ext.getCmp('period_cb2').getValue();
                    var mapLayerPath = proportionalSymbol.newUrl;
                    
                    Ext.Ajax.request({
                        url: path_mapping + dataUrl + type,
                        method: 'POST',
                        params: {id:indicatorOrDataElementId, periodId:periodId, mapLayerPath:mapLayerPath},
                        success: function(r) {
                            var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
                            EXPORTVALUES = getExportDataValueJSON(mapvalues);
                            var mv = new Array();
                            var mour = new Array();
                            var nameColumn = MAPDATA[thematicMap2].nameColumn;
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
                                url: path_mapping + 'getAvailableMapOrganisationUnitRelations' + type,
                                method: 'POST',
                                params: { mapLayerPath: mapLayerPath },
                                success: function(r) {
                                    var relations = Ext.util.JSON.decode(r.responseText).mapOrganisationUnitRelations;
                                   
                                    for (var i = 0; i < relations.length; i++) {
                                        mour[relations[i].featureId] = relations[i].organisationUnit;
                                    }

                                    for (var j = 0; j < FEATURE[thematicMap2].length; j++) {
                                        FEATURE[thematicMap2][j].attributes.value = mv[mour[FEATURE[thematicMap2][j].attributes[nameColumn]]] || 0;
                                    }
                                    
                                    proportionalSymbol.applyValues();
                                }
                            });
                        }
                    });
                }
            });
        }
    },
            
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