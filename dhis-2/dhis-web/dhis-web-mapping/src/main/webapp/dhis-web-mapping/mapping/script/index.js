Ext.onReady( function() {    
    Ext.BLANK_IMAGE_URL = '../resources/ext-ux/theme/gray-extend/gray-extend/s.gif';
	Ext.override(Ext.form.Field,{showField:function(){this.show();this.container.up('div.x-form-item').setDisplayed(true);},hideField:function(){this.hide();this.container.up('div.x-form-item').setDisplayed(false);}});
	Ext.QuickTips.init();
	document.body.oncontextmenu = function(){return false;};
	
	GLOBAL.vars.map = new OpenLayers.Map({controls:[new OpenLayers.Control.Navigation(),new OpenLayers.Control.ArgParser(),new OpenLayers.Control.Attribution()]});
	GLOBAL.vars.mask = new Ext.LoadMask(Ext.getBody(),{msg:i18n_loading,msgCls:'x-mask-loading2'});
    GLOBAL.vars.parameter = GLOBAL.util.getUrlParam('view') ? {id: GLOBAL.util.getUrlParam('view')} : {id: null};

    Ext.Ajax.request({
        url: GLOBAL.conf.path_mapping + 'initialize' + GLOBAL.conf.type,
        method: 'POST',
        params: {id: GLOBAL.vars.parameter.id || null},
        success: function(r) {
            var init = Ext.util.JSON.decode(r.responseText);
            GLOBAL.vars.parameter.mapView = init.mapView;
            GLOBAL.vars.parameter.baseLayers = init.baseLayers;
            GLOBAL.vars.parameter.overlays = init.overlays;
            GLOBAL.vars.mapDateType.value = init.userSettings.mapDateType;            
                        
    /* Section: stores */
    var mapViewStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getAllMapViews' + GLOBAL.conf.type,
        root: 'mapViews',
        fields: [ 'id', 'name', 'featureType', 'mapValueType', 'indicatorGroupId', 'indicatorId', 'dataElementGroupId', 'dataElementId',
            'mapDateType', 'periodTypeId', 'periodId', 'startDate', 'endDate', 'parentOrganisationUnitId', 'parentOrganisationUnitName',
            'parentOrganisationUnitLevel', 'organisationUnitLevel', 'organisationUnitLevelName', 'mapLegendType', 'method', 'classes',
            'bounds', 'colorLow', 'colorHigh', 'mapLegendSetId', 'radiusLow', 'radiusHigh', 'longitude', 'latitude', 'zoom'
        ],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
    var polygonMapViewStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getMapViewsByFeatureType' + GLOBAL.conf.type,
        baseParams: {featureType: GLOBAL.conf.map_feature_type_multipolygon},
        root: 'mapViews',
        fields: [ 'id', 'name', 'featureType', 'mapValueType', 'indicatorGroupId', 'indicatorId', 'dataElementGroupId', 'dataElementId',
            'mapDateType', 'periodTypeId', 'periodId', 'startDate', 'endDate', 'parentOrganisationUnitId', 'parentOrganisationUnitName',
            'parentOrganisationUnitLevel', 'organisationUnitLevel', 'organisationUnitLevelName', 'mapLegendType', 'method', 'classes',
            'bounds', 'colorLow', 'colorHigh', 'mapLegendSetId', 'longitude', 'latitude', 'zoom'
        ],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
    var pointMapViewStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getMapViewsByFeatureType' + GLOBAL.conf.type,
        baseParams: {featureType: GLOBAL.conf.map_feature_type_point},
        root: 'mapViews',
        fields: [ 'id', 'name', 'featureType', 'mapValueType', 'indicatorGroupId', 'indicatorId', 'dataElementGroupId', 'dataElementId',
            'mapDateType', 'periodTypeId', 'periodId', 'startDate', 'endDate', 'parentOrganisationUnitId', 'parentOrganisationUnitName',
            'parentOrganisationUnitLevel', 'organisationUnitLevel', 'organisationUnitLevelName', 'mapLegendType', 'method', 'classes',
            'bounds', 'colorLow', 'colorHigh', 'mapLegendSetId', 'radiusLow', 'radiusHigh', 'longitude', 'latitude', 'zoom'
        ],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });

    var indicatorGroupStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getAllIndicatorGroups' + GLOBAL.conf.type,
        root: 'indicatorGroups',
        fields: ['id', 'name'],
        idProperty: 'id',
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
    var indicatorsByGroupStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getIndicatorsByIndicatorGroup' + GLOBAL.conf.type,
        root: 'indicators',
        fields: ['id', 'name', 'shortName'],
        idProperty: 'id',
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
                store.each(
                    function fn(record) {
                        var name = record.get('name');
                        name = name.replace('&lt;', '<').replace('&gt;', '>');
                        record.set('name', name);
                    }
                );
            }
        }
    });
    
	var indicatorStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getAllIndicators' + GLOBAL.conf.type,
        root: 'indicators',
        fields: ['id', 'shortName'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
  
    var dataElementGroupStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getAllDataElementGroups' + GLOBAL.conf.type,
        root: 'dataElementGroups',
        fields: ['id', 'name'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
    var dataElementsByGroupStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getDataElementsByDataElementGroup' + GLOBAL.conf.type,
        root: 'dataElements',
        fields: ['id', 'name', 'shortName'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
                store.each(
                    function fn(record) {
                        var name = record.get('name');
                        name = name.replace('&lt;', '<').replace('&gt;', '>');
                        record.set('name', name);
                    }
                );
            }
        }
    });
    
    var dataElementStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getAllDataElements' + GLOBAL.conf.type,
        root: 'dataElements',
        fields: ['id', 'shortName'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
    var periodTypeStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getAllPeriodTypes' + GLOBAL.conf.type,
        root: 'periodTypes',
        fields: ['name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
        
    var periodsByTypeStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getPeriodsByPeriodType' + GLOBAL.conf.type,
        root: 'periods',
        fields: ['id', 'name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
	var predefinedMapLegendStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getAllMapLegends' + GLOBAL.conf.type,
        root: 'mapLegends',
        fields: ['id', 'name', 'startValue', 'endValue', 'color', 'displayString'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });    
    
    var predefinedMapLegendSetStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getMapLegendSetsByType' + GLOBAL.conf.type,
        baseParams: {type: GLOBAL.conf.map_legend_type_predefined},
        root: 'mapLegendSets',
        fields: ['id', 'name', 'indicators', 'dataelements'],
        sortInfo: {field:'name', direction:'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
	var organisationUnitLevelStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getAllOrganisationUnitLevels' + GLOBAL.conf.type,
        root: 'organisationUnitLevels',
        fields: ['id', 'level', 'name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
                
                if (!symbol.form.findField('level').getValue()) {
					if (this.isLoaded) {
						var data = this.getAt(this.getTotalCount()-1).data;
						symbol.organisationUnitSelection.setValues(null, null, null, data.level, data.name);
						symbol.form.findField('level').setValue(data.name);
					}
				}
                // Ext.getCmp('level_cb').mode = 'local';
            }
        }
    });
    
	var polygonOrganisationUnitLevelStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getOrganisationUnitLevelsByFeatureType' + GLOBAL.conf.type,
        baseParams: {featureType: GLOBAL.conf.map_feature_type_multipolygon},
        root: 'organisationUnitLevels',
        fields: ['id', 'level', 'name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
	var organisationUnitsAtLevelStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getOrganisationUnitsAtLevel' + GLOBAL.conf.type,
        baseParams: {level: 1},
        root: 'organisationUnits',
        fields: ['id', 'name'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
	var geojsonFilesStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getGeoJsonFiles' + GLOBAL.conf.type,
        root: 'files',
        fields: ['name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
	var wmsCapabilitiesStore = new GeoExt.data.WMSCapabilitiesStore({
        url: GLOBAL.conf.path_geoserver + GLOBAL.conf.ows,
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
	var baseLayerStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getMapLayersByType' + GLOBAL.conf.type,
        baseParams: {type: GLOBAL.conf.map_layer_type_baselayer},
        root: 'mapLayers',
        fields: ['id', 'name', 'type', 'mapSource', 'layer', 'fillColor', 'fillOpacity', 'strokeColor', 'strokeWidth'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
    var overlayStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getMapLayersByType' + GLOBAL.conf.type,
        baseParams: {type: GLOBAL.conf.map_layer_type_overlay},
        root: 'mapLayers',
        fields: ['id', 'name', 'type', 'mapSource', 'layer', 'fillColor', 'fillOpacity', 'strokeColor', 'strokeWidth'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
    var userSettingStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getMapUserSettings' + GLOBAL.conf.type,
        fields: ['mapDateType'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });	
    
    GLOBAL.stores = {
		mapView: mapViewStore,
        polygonMapView: polygonMapViewStore,
        pointMapView: pointMapViewStore,
        indicatorGroup: indicatorGroupStore,
        indicatorsByGroup: indicatorsByGroupStore,
        indicator: indicatorStore,
        dataElementGroup: dataElementGroupStore,
        dataElementsByGroup: dataElementsByGroupStore,
        dataElement: dataElementStore,
        periodType: periodTypeStore,
        periodsByTypeStore: periodsByTypeStore,
        predefinedMapLegend: predefinedMapLegendStore,
        predefinedMapLegendSet: predefinedMapLegendSetStore,
        organisationUnitLevel: organisationUnitLevelStore,
        polygonOrganisationUnitLevel: polygonOrganisationUnitLevelStore,
        organisationUnitsAtLevel: organisationUnitsAtLevelStore,
        geojsonFiles: geojsonFilesStore,
        wmsCapabilities: wmsCapabilitiesStore,
        baseLayer: baseLayerStore,
        overlay: overlayStore
    };
	
	/* Add base layers */	
	function addBaseLayersToMap(init) {
		GLOBAL.vars.map.addLayers([new OpenLayers.Layer.WMS('World', 'http://labs.metacarta.com/wms/vmap0', {layers: 'basic'})]);
		GLOBAL.vars.map.layers[0].setVisibility(false);
        
        if (init) {
            var layers = GLOBAL.vars.parameter.baseLayers || [];
			if (layers.length) {
				for (var i = 0; i < layers.length; i++) {
					GLOBAL.vars.map.addLayers([new OpenLayers.Layer.WMS(layers[i].data.name, layers[i].data.mapSource, {layers: layers[i].data.layer})]);
					GLOBAL.vars.map.layers[GLOBAL.vars.map.layers.length-1].setVisibility(false);
				}
			}
        }
        else {
            GLOBAL.stores.baseLayer.load({callback: function(r) {
                if (r.length) {
                    for (var i = 0; i < r.length; i++) {
                        GLOBAL.vars.map.addLayers([new OpenLayers.Layer.WMS(r[i].data.name, r[i].data.mapSource, {layers: r[i].data.layer})]);
                        GLOBAL.vars.map.layers[GLOBAL.vars.map.layers.length-1].setVisibility(false);
                    }
                }
            }});
        }
	}
	addBaseLayersToMap(true);
    
	function addOverlaysToMap(init) {
        function add(r) {
            if (r.length) {
                var loadStart = function() {
                    GLOBAL.vars.mask.msg = i18n_loading;
                    GLOBAL.vars.mask.show();
                };
                var loadEnd = function() {
                    GLOBAL.vars.mask.hide();
                };
                
                for (var i = 0; i < r.length; i++) {
                    var overlay = new OpenLayers.Layer.Vector(r[i].data.name, {
                        'visibility': false,
                        'styleMap': new OpenLayers.StyleMap({
                            'default': new OpenLayers.Style(
                                OpenLayers.Util.applyDefaults({
                                        'fillColor': r[i].data.fillColor,
                                        'fillOpacity': parseFloat(r[i].data.fillOpacity),
                                        'strokeColor': r[i].data.strokeColor,
                                        'strokeWidth': parseFloat(r[i].data.strokeWidth)
                                    },
                                    OpenLayers.Feature.Vector.style['default']
                                )
                            )
                        }),
                        'strategies': [new OpenLayers.Strategy.Fixed()],
                        'protocol': new OpenLayers.Protocol.HTTP({
                            'url': GLOBAL.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + r[i].data.mapSource,
                            'format': new OpenLayers.Format.GeoJSON()
                        })
                    });
                    
                    overlay.events.register('loadstart', null, loadStart);					
                    overlay.events.register('loadend', null, loadEnd);
                    overlay.isOverlay = true;
                    GLOBAL.vars.map.addLayer(overlay);
                }
            }
        }
        
        if (init) {
            add(GLOBAL.vars.parameter.overlays);
        }
        else {
            GLOBAL.stores.overlay.load({callback: function(r) {
                add(r);
            }});
        }
	}
	addOverlaysToMap(true);
			
	/* Section: mapview */
	var favoriteWindow = new Ext.Window({
        id: 'favorite_w',
        title: '<span id="window-favorites-title">' + i18n_favorites + '</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: GLOBAL.conf.window_width,
        height: 180,
        items: [
            {
                xtype: 'form',
                bodyStyle: 'padding:8px',
                items: [
                    {html: '<div class="window-info">Register current map as a favorite</div>'},
                    {
                        xtype: 'textfield',
                        id: 'favoritename_tf',
                        emptytext: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_display_name,
                        width: GLOBAL.conf.combo_width_fieldset,
                        autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '35'}
                    },
                    {html: '<div class="window-p"></div>'},
                    {html: '<div class="window-info">Delete / Add favorite to dashboard</div>'},
                    {
                        xtype: 'combo',
                        id: 'favorite_cb',
                        editable: false,
                        valueField: 'id',
                        displayField: 'name',
                        mode: 'remote',
                        forceSelection: true,
                        triggerAction: 'all',
                        emptyText: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_favorite,
                        selectOnFocus: true,
                        width: GLOBAL.conf.combo_width_fieldset,
                        minListWidth: GLOBAL.conf.combo_width_fieldset,
                        store:GLOBAL.stores.mapView
                    }
                ]
            }
        ],
        bbar: [
            '->',
            {
                xtype: 'button',
                id: 'newview_b',
                iconCls: 'icon-add',
				hideLabel: true,
				text: i18n_register,
				handler: function() {
					var vn = Ext.getCmp('favoritename_tf').getValue();
                    
                    if (!vn) {
						Ext.message.msg(false, i18n_form_is_not_complete);
						return;
					}
                    
                    var formValues;
                    
                    if (GLOBAL.vars.activePanel.isPolygon()) {
                        if (!choropleth.formValidation.validateForm(true)) {
                            return;
                        }
                        formValues = choropleth.formValues.getAllValues.call(choropleth);
                    }
                    else if (GLOBAL.vars.activePanel.isPoint()) {
                        if (!symbol.formValidation.validateForm(true)) {
                            return;
                        }
                        formValues = symbol.getFormValues();
                    }
                    
                    if (GLOBAL.stores.mapView.find('name', vn) !== -1) {
                        Ext.message.msg(false, i18n_there_is_already_a_map_view_called + ' <span class="x-msg-hl">' + vn + '</span>');
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: GLOBAL.conf.path_mapping + 'addOrUpdateMapView' + GLOBAL.conf.type,
                        method: 'POST',
                        params: {
                            name: vn,
							featureType: formValues.featureType,
                            mapValueType: formValues.mapValueType,
                            indicatorGroupId: formValues.indicatorGroupId,
                            indicatorId: formValues.indicatorId,
                            dataElementGroupId: formValues.dataElementGroupId,
                            dataElementId: formValues.dataElementId,
                            periodTypeId: formValues.periodTypeId,
                            periodId: formValues.periodId,
                            startDate: formValues.startDate,
                            endDate: formValues.endDate,
                            parentOrganisationUnitId: formValues.parentOrganisationUnitId,
                            organisationUnitLevel: formValues.organisationUnitLevel,
                            mapLegendType: formValues.mapLegendType,
                            method: formValues.method,
                            classes: formValues.classes,
                            bounds: formValues.bounds,
                            colorLow: formValues.colorLow,
                            colorHigh: formValues.colorHigh,
                            mapLegendSetId: formValues.mapLegendSetId,
                            radiusLow: formValues.radiusLow,
                            radiusHigh: formValues.radiusHigh,
                            longitude: formValues.longitude,
                            latitude: formValues.latitude,
                            zoom: formValues.zoom
                        },
                        success: function(r) {
                            Ext.message.msg(true, i18n_favorite + ' <span class="x-msg-hl">' + vn + '</span> ' + i18n_registered);
                            GLOBAL.stores.mapView.load();
                            if (formValues.featureType == GLOBAL.conf.map_feature_type_multipolygon) {
								GLOBAL.stores.polygonMapView.load();
							}
							else if (formValues.featureType == GLOBAL.conf.map_feature_type_multipolygon) {
								GLOBAL.stores.pointMapView.load();
							}
                            Ext.getCmp('favoritename_tf').reset();
                        }
                    });
				}
			},
            {
                xtype: 'button',
                id: 'deleteview_b',
                iconCls: 'icon-remove',
				hideLabel: true,
				text: i18n_delete,
				handler: function() {
					var v = Ext.getCmp('favorite_cb').getValue();
					
                    if (!v) {
						Ext.message.msg(false, i18n_please_select_a_map_view);
						return;
					}
                    
					var name = GLOBAL.stores.mapView.getById(v).get('name');				
					
					Ext.Ajax.request({
						url: GLOBAL.conf.path_mapping + 'deleteMapView' + GLOBAL.conf.type,
						method: 'POST',
						params: {id:v},
						success: function(r) {
							Ext.message.msg(true, i18n_favorite + ' <span class="x-msg-hl">' + name + '</span> ' + i18n_deleted);
                            GLOBAL.stores.mapView.load();
                            Ext.getCmp('favorite_cb').clearValue();
                            if (v == choropleth.form.findField('mapview').getValue()) {
                                choropleth.form.findField('mapview').clearValue();
                            }
                            if (v == symbol.form.findField('mapview').getValue()) {
                                symbol.form.findField('mapview').clearValue();
                            }
						}
					});
				}
			},
            {
                xtype: 'button',
                id: 'dashboardview_b',
                iconCls: 'icon-assign',
				hideLabel: true,
				text: i18n_add,
				handler: function() {
					var v = Ext.getCmp('favorite_cb').getValue();
					var rv = Ext.getCmp('favorite_cb').getRawValue();
					
					if (!v) {
						Ext.message.msg(false, i18n_please_select_a_map_view);
						return;
					}
					
					Ext.Ajax.request({
						url: GLOBAL.conf.path_mapping + 'addMapViewToDashboard' + GLOBAL.conf.type,
						method: 'POST',
						params: {id:v},
						success: function(r) {
							Ext.message.msg(true, i18n_favorite + ' <span class="x-msg-hl">' + rv + '</span> ' + i18n_added_to_dashboard);
                            Ext.getCmp('favorite_cb').clearValue();
						}
					});
				}
            }
        ]
    });
	
	/* Section: export map */	
	var exportImageWindow = new Ext.Window({
        id: 'exportimage_w',
        title: '<span id="window-image-title">Image export</span>',
        layout: 'fit',
        closeAction: 'hide',
		width: GLOBAL.conf.window_width,
        height: 220,
        items: [
            {
                xtype: 'form',
                bodyStyle: 'padding:8px',
                items: [
                    {html: '<div class="window-info">Export thematic map to PNG</div>'},
                    {
                        xtype: 'textfield',
                        id: 'exportimagetitle_tf',
                        fieldLabel: i18n_title,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        editable: true,
                        valueField: 'id',
                        displayField: 'text',
                        width: GLOBAL.conf.combo_width_fieldset,
                        mode: 'local',
                        triggerAction: 'all'
                    },
                    {
                        xtype: 'combo',
                        id: 'exportimagelayers_cb',
                        fieldLabel: 'Layers',
                        labelSeparator: GLOBAL.conf.labelseparator,
                        editable: false,
                        valueField: 'id',
                        displayField: 'layer',
                        width: GLOBAL.conf.combo_width_fieldset,
                        minListWidth: GLOBAL.conf.combo_width_fieldset,
                        mode: 'local',
                        triggerAction: 'all',
                        value: 1,
                        store: {
                            xtype: 'arraystore',
                            fields: ['id', 'layer'],
                            data: [[1, 'Polygon layer'], [2, 'Point layer'], [3, 'Both']]
                        }
                    },
                    {
                        xtype: 'combo',
                        id: 'exportimagewidth_cb',
                        fieldLabel: 'Width',
                        labelSeparator: GLOBAL.conf.labelseparator,
                        editable: true,
                        emptyText: 'Custom px',
                        valueField: 'width',
                        displayField: 'text',
                        width: GLOBAL.conf.combo_width_fieldset,
                        minListWidth: GLOBAL.conf.combo_width_fieldset,
                        mode: 'local',
                        triggerAction: 'all',
                        store: new Ext.data.ArrayStore({
                            fields: ['width', 'text'],
                            data: [[800, 'Small'], [1190, 'Medium'], [1920, 'Large']]
                        })
                    },
                    {
                        xtype: 'combo',
                        id: 'exportimageheight_cb',
                        fieldLabel: 'Height',
                        labelSeparator: GLOBAL.conf.labelseparator,
                        editable: true,
                        emptyText: 'Custom px',
                        valueField: 'height',
                        displayField: 'text',
                        width: GLOBAL.conf.combo_width_fieldset,
                        minListWidth: GLOBAL.conf.combo_width_fieldset,
                        mode: 'local',
                        triggerAction: 'all',
                        store: {
                            xtype: 'arraystore',
                            fields: ['height', 'text'],
                            data: [[600, 'Small'], [880, 'Medium'], [1200, 'Large']]
                        }
                    },
                    {
                        xtype: 'checkbox',
                        id: 'exportimageincludelegend_chb',
                        fieldLabel: i18n_include_legend,
                        labelSeparator: '',				
                        isFormField: true,
                        checked: true
                    }
                ]
            }
        ],
        bbar: [
            '->',
            {
                xtype: 'button',
                id: 'exportimage_b',
				labelSeparator: GLOBAL.conf.labelseparator,
                iconCls: 'icon-export',
				text: i18n_export,
				handler: function() {
                    var values, svgElement, svg;
                    if (Ext.getCmp('exportimagelayers_cb').getValue() == 1) {
						if (choropleth.formValidation.validateForm()) {
							values = choropleth.formValues.getImageExportValues.call(choropleth);
							document.getElementById('layerField').value = 1;
							document.getElementById('periodField').value = values.dateValue;
							document.getElementById('indicatorField').value = values.mapValueTypeValue;
							document.getElementById('legendsField').value = GLOBAL.util.getLegendsJSON.call(choropleth);
							svgElement = document.getElementsByTagName('svg')[0];
							svg = svgElement.parentNode.innerHTML;
						}
						else {
							Ext.message.msg(false, 'Polygon layer not rendered');
							return;
						}
					}
					else if (Ext.getCmp('exportimagelayers_cb').getValue() == 2) {
						if (symbol.formValidation.validateForm()) {
							values = symbol.formValues.getImageExportValues.call(symbol);
							document.getElementById('layerField').value = 2;
							document.getElementById('periodField').value = values.dateValue;  
							document.getElementById('indicatorField').value = values.mapValueTypeValue;
							document.getElementById('legendsField').value = GLOBAL.util.getLegendsJSON.call(symbol);
							svgElement = document.getElementsByTagName('svg')[1];
							svg = svgElement.parentNode.innerHTML;
						}
						else {
							Ext.message.msg(false, 'Point layer not rendered');
							return;
						}
					}
					else if (Ext.getCmp('exportimagelayers_cb').getValue() == 3) {
						if (choropleth.formValidation.validateForm()) {
							if (symbol.formValidation.validateForm()) {
								document.getElementById('layerField').value = 3;
								document.getElementById('imageLegendRowsField').value = choropleth.imageLegend.length;
								
								values = choropleth.formValues.getImageExportValues.call(choropleth);
								document.getElementById('periodField').value = values.dateValue;
								document.getElementById('indicatorField').value = values.mapValueTypeValue;
								document.getElementById('legendsField').value = GLOBAL.util.getLegendsJSON.call(choropleth);
								
								values = symbol.formValues.getImageExportValues.call(symbol);
								document.getElementById('periodField2').value = values.dateValue;
								document.getElementById('indicatorField2').value = values.mapValueTypeValue;
								document.getElementById('legendsField2').value = GLOBAL.util.getLegendsJSON.call(symbol);
								
								svgElement = document.getElementsByTagName('svg')[0];
								var str1 = svgElement.parentNode.innerHTML;
								str1 = svgElement.parentNode.innerHTML.replace('</svg>');
								var str2 = document.getElementsByTagName('svg')[1].parentNode.innerHTML;
								str2 = str2.substring(str2.indexOf('>')+1);
								svg = str1 + str2;
							}
							else {
								Ext.message.msg(false, 'Point layer not rendered');
								return;
							}
						}
						else {
							Ext.message.msg(false, 'Polygon layer not rendered');
							return;
						}
					}

					var title = Ext.getCmp('exportimagetitle_tf').getValue();
					
					if (!title) {
						Ext.message.msg(false, i18n_form_is_not_complete);
					}
					else {
						var exportForm = document.getElementById('exportForm');
						exportForm.action = '../exportImage.action';
						exportForm.target = '_blank';
						
						document.getElementById('titleField').value = title;
						document.getElementById('viewBoxField').value = svgElement.getAttribute('viewBox');  
						document.getElementById('svgField').value = svg;  
						document.getElementById('widthField').value = Ext.getCmp('exportimagewidth_cb').getValue();
						document.getElementById('heightField').value = Ext.getCmp('exportimageheight_cb').getValue();
						document.getElementById('includeLegendsField').value = Ext.getCmp('exportimageincludelegend_chb').getValue();

						exportForm.submit();
						Ext.getCmp('exportimagetitle_tf').reset();
					}
				}
            }
        ]    
    });
	
	/* Section: predefined map legend set */
    var predefinedMapLegendSetWindow = new Ext.Window({
        id: 'predefinedmaplegendset_w',
        title: '<span id="window-predefinedlegendset-title">'+i18n_predefined_legend_sets+'</span>',
		layout: 'accordion',
        closeAction: 'hide',
		width: GLOBAL.conf.window_width,
        height: Ext.isChrome ? 348:346,
        items: [
            {
                id: 'newpredefinedmaplegend_p',
                title: i18n_legend,
                items: [
                    {
                        xtype: 'form',
                        bodyStyle: 'padding: 8px 8px 5px 8px',
                        items: [
                            {html: '<div class="window-info">Register new legend</div>'},
                            {
                                xtype: 'textfield',
                                id: 'predefinedmaplegendname_tf',
                                emptyText: GLOBAL.conf.emptytext,
                                labelSeparator: GLOBAL.conf.labelseparator,
                                fieldLabel: i18n_display_name,
                                width: GLOBAL.conf.combo_width_fieldset
                            },
                            {
                                xtype: 'numberfield',
                                id: 'predefinedmaplegendstartvalue_nf',
                                emptyText: GLOBAL.conf.emptytext,
                                labelSeparator: GLOBAL.conf.labelseparator,
                                fieldLabel: i18n_start_value,
                                width: GLOBAL.conf.combo_number_width_small
                            },
                            {
                                xtype: 'numberfield',
                                id: 'predefinedmaplegendendvalue_nf',
                                emptyText: GLOBAL.conf.emptytext,
                                labelSeparator: GLOBAL.conf.labelseparator,
                                fieldLabel: i18n_end_value,
                                width: GLOBAL.conf.combo_number_width_small
                            },
                            {
                                xtype: 'colorfield',
                                id: 'predefinedmaplegendcolor_cp',
                                emptyText: GLOBAL.conf.emptytext,
                                labelSeparator: GLOBAL.conf.labelseparator,
                                fieldLabel: i18n_color,
                                allowBlank: false,
                                width: GLOBAL.conf.combo_width_fieldset,
                                value:"#C0C0C0"
                            },
                            {html: '<div class="window-p"></div>'},
                            {html: '<div class="window-info">Delete legend</div>'},
                            {
                                xtype: 'combo',
                                id: 'predefinedmaplegend_cb',
                                editable: false,
                                valueField: 'id',
                                displayField: 'name',
                                mode: 'remote',
                                forceSelection: true,
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText: GLOBAL.conf.emptytext,
                                labelSeparator: GLOBAL.conf.labelseparator,
                                fieldLabel: i18n_legend,
                                width: GLOBAL.conf.combo_width_fieldset,
                                minListWidth: GLOBAL.conf.combo_width_fieldset,
                                store: GLOBAL.stores.predefinedMapLegend
                            }
                        ]
                    },
                    {
                        xtypes: 'form',
                        items: [
                            {
                                xtype: 'toolbar',
                                style: 'padding-top:4px',
                                items: [
                                    '->',
                                    {
                                        xtype: 'button',
                                        id: 'newpredefinedmaplegend_b',
                                        text: i18n_register,
                                        iconCls: 'icon-add',
                                        handler: function() {
                                            var mln = Ext.getCmp('predefinedmaplegendname_tf').getValue();
                                            var mlsv = parseFloat(Ext.getCmp('predefinedmaplegendstartvalue_nf').getValue());
                                            var mlev = parseFloat(Ext.getCmp('predefinedmaplegendendvalue_nf').getValue());
                                            var mlc = Ext.getCmp('predefinedmaplegendcolor_cp').getValue();
                                            
                                            if (!Ext.isNumber(parseFloat(mlsv)) || !Ext.isNumber(mlev)) {
                                                Ext.message.msg(false, i18n_form_is_not_complete);
                                                return;
                                            }
                                            
                                            if (!mln || !mlsv || !mlev || !mlc) {
                                                Ext.message.msg(false, i18n_form_is_not_complete);
                                                return;
                                            }
                                            
                                            if (!GLOBAL.util.validateInputNameLength(mln)) {
                                                Ext.message.msg(false, i18n_name + ': ' + i18n_max + ' 25 ' + i18n_characters);
                                                return;
                                            }
                                            
                                            if (GLOBAL.stores.predefinedMapLegend.find('name', mln) !== -1) {
                                                Ext.message.msg(false, i18n_legend + '<span class="x-msg-hl">' + mln + '</span> ' + i18n_already_exists);
                                                return;
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: GLOBAL.conf.path_mapping + 'addOrUpdateMapLegend' + GLOBAL.conf.type,
                                                method: 'POST',
                                                params: {name: mln, startValue: mlsv, endValue: mlev, color: mlc},
                                                success: function(r) {
                                                    Ext.message.msg(true, i18n_legend + ' <span class="x-msg-hl">' + mln + '</span> ' + i18n_was_registered);
                                                    GLOBAL.stores.predefinedMapLegend.load();
                                                    Ext.getCmp('predefinedmaplegendname_tf').reset();
                                                    Ext.getCmp('predefinedmaplegendstartvalue_nf').reset();
                                                    Ext.getCmp('predefinedmaplegendendvalue_nf').reset();
                                                    Ext.getCmp('predefinedmaplegendcolor_cp').reset();
                                                }
                                            });
                                        }
                                    },
                                    {
                                        xtype: 'button',
                                        id: 'deletepredefinedmaplegend_b',
                                        text: i18n_delete,
                                        iconCls: 'icon-remove',
                                        handler: function() {
                                            var mlv = Ext.getCmp('predefinedmaplegend_cb').getValue();
                                            var mlrv = Ext.getCmp('predefinedmaplegend_cb').getRawValue();
                                            
                                            if (!mlv) {
                                                Ext.message.msg(false, i18n_please_select_a_legend);
                                                return;
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: GLOBAL.conf.path_mapping + 'deleteMapLegend' + GLOBAL.conf.type,
                                                method: 'POST',
                                                params: {id: mlv},
                                                success: function(r) {
                                                    Ext.message.msg(true, i18n_legend + ' <span class="x-msg-hl">' + mlrv + '</span> ' + i18n_was_deleted);
                                                    GLOBAL.stores.predefinedMapLegend.load();
                                                    Ext.getCmp('predefinedmaplegend_cb').clearValue();
                                                }
                                            });
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ],
                listeners: {
                    expand: function() {
                        predefinedMapLegendSetWindow.setHeight(Ext.isChrome ? 348:346);
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(123);
                    }
                }
            },
            
            {
                title: i18n_legendset,
                items: [
                    {
                        xtype: 'form',
                        bodyStyle: 'padding: 8px 8px 5px 8px',
                        items: [
                            {html: '<div class="window-info">Register new legend set</div>'},
                            {
                                xtype: 'textfield',
                                id: 'predefinedmaplegendsetname_tf',
                                emptyText: GLOBAL.conf.emptytext,
                                labelSeparator: GLOBAL.conf.labelseparator,
                                fieldLabel: i18n_display_name,
                                width: GLOBAL.conf.combo_width_fieldset
                            },
                            {html: '<div class="window-field-label">'+i18n_legends+'</div>'},
                            {
                                xtype: 'multiselect',
                                id: 'predefinednewmaplegend_ms',
                                hideLabel: true,
                                dataFields: ['id', 'name', 'startValue', 'endValue', 'color', 'displayString'],
                                valueField: 'id',
                                displayField: 'displayString',
                                width: GLOBAL.conf.multiselect_width,
                                height: GLOBAL.util.getMultiSelectHeight() / 2,
                                store: GLOBAL.stores.predefinedMapLegend
                            },
                            {html: '<div class="window-p"></div>'},
                            {html: '<div class="window-info">Delete legend set</div>'},
                            {
                                xtype: 'combo',
                                id: 'predefinedmaplegendsetindicator_cb',
                                editable: false,
                                valueField: 'id',
                                displayField: 'name',
                                mode: 'remote',
                                forceSelection: true,
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText: GLOBAL.conf.emptytext,
                                labelSeparator: GLOBAL.conf.labelseparator,
                                fieldLabel: i18n_legendset,
                                width: GLOBAL.conf.combo_width_fieldset,
                                minListWidth: GLOBAL.conf.combo_width_fieldset,
                                store:GLOBAL.stores.predefinedMapLegendSet
                            }
                        ]
                    },
                    {
                        xtype: 'form',
                        items: [
                            {
                                xtype: 'toolbar',
                                style: 'padding-top:4px',
                                items: [
                                    '->',
                                    {
                                        xtype: 'button',
                                        id: 'newpredefinedmaplegendset_b',
                                        text: i18n_register,
                                        iconCls: 'icon-add',
                                        handler: function() {
                                            var mlsv = Ext.getCmp('predefinedmaplegendsetname_tf').getValue();
                                            var mlms = Ext.getCmp('predefinednewmaplegend_ms').getValue();
                                            var array = [];
                                            
                                            if (mlms) {
                                                array = mlms.split(',');
                                                if (array.length > 1) {
                                                    for (var i = 0; i < array.length; i++) {
                                                        var sv = GLOBAL.stores.predefinedMapLegend.getById(array[i]).get('startValue');
                                                        var ev = GLOBAL.stores.predefinedMapLegend.getById(array[i]).get('endValue');
                                                        for (var j = 0; j < array.length; j++) {
                                                            if (j != i) {
                                                                var temp_sv = GLOBAL.stores.predefinedMapLegend.getById(array[j]).get('startValue');
                                                                var temp_ev = GLOBAL.stores.predefinedMapLegend.getById(array[j]).get('endValue');
                                                                for (var k = sv+1; k < ev; k++) {
                                                                    if (k > temp_sv && k < temp_ev) {
                                                                        Ext.message.msg(false, i18n_overlapping_legends_are_not_allowed);
                                                                        return;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                Ext.message.msg(false, i18n_form_is_not_complete);
                                                return;
                                            }
                                            
                                            if (!mlsv) {
                                                Ext.message.msg(false, i18n_form_is_not_complete);
                                                return;
                                            }
                                            
                                            array = mlms.split(',');
                                            var params = '?mapLegends=' + array[0];
                                            if (array.length > 1) {
                                                for (var l = 1; l < array.length; l++) {
                                                    array[l] = '&mapLegends=' + array[l];
                                                    params += array[l];
                                                }
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: GLOBAL.conf.path_mapping + 'addOrUpdateMapLegendSet.action' + params,
                                                method: 'POST',
                                                params: {name: mlsv, type: GLOBAL.conf.map_legend_type_predefined},
                                                success: function(r) {
                                                    Ext.message.msg(true, i18n_new_legend_set+' <span class="x-msg-hl">' + mlsv + '</span> ' + i18n_was_registered);
                                                    GLOBAL.stores.predefinedMapLegendSet.load();
                                                    Ext.getCmp('predefinedmaplegendsetname_tf').reset();
                                                    Ext.getCmp('predefinednewmaplegend_ms').reset();							
                                                }
                                            });
                                        }
                                    },
                                    {
                                        xtype: 'button',
                                        id: 'deletepredefinedmaplegendset_b',
                                        text: i18n_delete,
                                        iconCls: 'icon-remove',
                                        handler: function() {
                                            var mlsv = Ext.getCmp('predefinedmaplegendsetindicator_cb').getValue();
                                            var mlsrv = Ext.getCmp('predefinedmaplegendsetindicator_cb').getRawValue();
                                            
                                            if (!mlsv) {
                                                Ext.message.msg(false, i18n_please_select_a_legend_set);
                                                return;
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: GLOBAL.conf.path_mapping + 'deleteMapLegendSet' + GLOBAL.conf.type,
                                                method: 'POST',
                                                params: {id: mlsv},
                                                success: function(r) {
                                                    Ext.message.msg(true, i18n_legendset + ' <span class="x-msg-hl">' + mlsrv + '</span> ' + i18n_was_deleted);
                                                    GLOBAL.stores.predefinedMapLegendSet.load();
                                                    Ext.getCmp('predefinedmaplegendsetindicator_cb').clearValue();
                                                    if (mlsv == Ext.getCmp('predefinedmaplegendsetindicator2_cb').getValue) {
                                                        Ext.getCmp('predefinedmaplegendsetindicator2_cb').clearValue();
                                                    }
                                                    if (mlsv == Ext.getCmp('predefinedmaplegendsetindicator2_cb').getValue) {
                                                        Ext.getCmp('predefinedmaplegendsetdataelement_cb').clearValue();
                                                    }                            
                                                }
                                            });
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ],
                listeners: {
                    expand: function() {
                        predefinedMapLegendSetWindow.setHeight((GLOBAL.util.getMultiSelectHeight() / 2) + (Ext.isChrome ? 299:295));
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(123);
                    }
                }
            },
            
            {
                title: i18n_indicators,
                items: [
                    {
                        xtype: 'form',
                        bodyStyle: 'padding: 8px 8px 5px 8px',
                        items: [
                            {html: '<div class="window-info">Assign indicators to legend set</div>'},
                            {
                                xtype: 'combo',
                                id: 'predefinedmaplegendsetindicator2_cb',
                                editable: false,
                                valueField: 'id',
                                displayField: 'name',
                                mode: 'remote',
                                forceSelection: true,
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText: GLOBAL.conf.emptytext,
                                labelSeparator: GLOBAL.conf.labelseparator,
                                fieldLabel: i18n_legendset,
                                width: GLOBAL.conf.combo_width_fieldset,
                                minListWidth: GLOBAL.conf.combo_width_fieldset,
                                store: GLOBAL.stores.predefinedMapLegendSet,
                                listeners: {
                                    'select': {
                                        fn: function(cb, record) {
                                            var indicators = record.data.indicators || [];
                                            var indicatorString = '';
                                            
                                            for (var i = 0; i < indicators.length; i++) {
                                                indicatorString += indicators[i];
                                                if (i < indicators.length-1) {
                                                    indicatorString += ',';
                                                }
                                            }
                                            
                                            Ext.getCmp('predefinedmaplegendsetindicator_ms').setValue(indicatorString);
                                        }
                                    }
                                }	
                            },
                            {html: '<div class="window-field-label">' + i18n_indicators + '</div>'},
                            {
                                xtype: 'multiselect',
                                id: 'predefinedmaplegendsetindicator_ms',
                                hideLabel:true,
                                dataFields: ['id', 'shortName'],
                                valueField: 'id',
                                displayField: 'shortName',
                                width:GLOBAL.conf.multiselect_width,
                                height: GLOBAL.util.getMultiSelectHeight(),
                                store:GLOBAL.stores.indicator
                            }
                        ]
                    },
                    {
                        xtype: 'form',
                        items: [
                            {
                                xtype: 'toolbar',
                                style: 'padding-top:4px',
                                items: [
                                    '->',
                                    {
                                        xtype: 'button',
                                        id: 'assignpredefinedmaplegendsetindicator_b',
                                        text: i18n_assign,
                                        iconCls: 'icon-assign',
                                        handler: function() {
                                            var ls = Ext.getCmp('predefinedmaplegendsetindicator2_cb').getValue();
                                            var lsrw = Ext.getCmp('predefinedmaplegendsetindicator2_cb').getRawValue();
                                            var lims = Ext.getCmp('predefinedmaplegendsetindicator_ms').getValue();
                                            
                                            if (!ls) {
                                                Ext.message.msg(false, i18n_please_select_a_legend_set);
                                                return;
                                            }
                                            
                                            if (!lims) {
                                                Ext.message.msg(false, i18n_please_select_at_least_one_indicator);
                                                return;
                                            }
                                            
                                            var array = [];
                                            array = lims.split(',');
                                            var params = '?indicators=' + array[0];
                                            
                                            if (array.length > 1) {
                                                for (var i = 1; i < array.length; i++) {
                                                    array[i] = '&indicators=' + array[i];
                                                    params += array[i];
                                                }
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: GLOBAL.conf.path_mapping + 'assignIndicatorsToMapLegendSet.action' + params,
                                                method: 'POST',
                                                params: {id: ls},
                                                success: function(r) {
                                                    Ext.message.msg(true, i18n_legendset+' <span class="x-msg-hl">' + lsrw + '</span> ' + i18n_was_updated);
                                                    GLOBAL.stores.predefinedMapLegendSet.load();
                                                }
                                            });
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ],
                listeners: {
                    expand: function() {
                        predefinedMapLegendSetWindow.setHeight(GLOBAL.util.getMultiSelectHeight() + (Ext.isChrome ? 243:240));
                        
                        if (!GLOBAL.stores.indicator.isLoaded) {
                            GLOBAL.stores.indicator.load();
                        }
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(123);
                    }
                }
            },
            
            {
                title: i18n_dataelements,
                items: [
                    {
                        xtype: 'form',
                        bodyStyle: 'padding: 8px 8px 5px 8px',
                        items: [
                            {html: '<div class="window-info">Assign data elements to legend set</div>'},
                            {
                                xtype: 'combo',
                                id: 'predefinedmaplegendsetdataelement_cb',
                                editable: false,
                                valueField: 'id',
                                displayField: 'name',
                                mode: 'remote',
                                forceSelection: true,
                                triggerAction: 'all',
                                selectOnFocus: true,
                                emptyText: GLOBAL.conf.emptytext,
                                labelSeparator: GLOBAL.conf.labelseparator,
                                fieldLabel: i18n_legendset,
                                width: GLOBAL.conf.combo_width_fieldset,
                                minListWidth: GLOBAL.conf.combo_width_fieldset,
                                store: GLOBAL.stores.predefinedMapLegendSet,
                                listeners:{
                                    'select': {
                                        fn: function(cb, record) {
                                            var dataElements = record.data.dataElements || [];
                                            var dataElementString = '';

                                            for (var i = 0; i < dataElements.length; i++) {
                                                dataElementString += dataElements[i];
                                                if (i < dataElements.length-1) {
                                                    dataElementString += ',';
                                                }
                                            }
                                            
                                            Ext.getCmp('predefinedmaplegendsetdataelement_ms').setValue(dataElementString);
                                        }
                                    }
                                }					
                            },
                            {html: '<div class="window-field-label">' + i18n_dataelements + '</div>'},
                            {
                                xtype: 'multiselect',
                                id: 'predefinedmaplegendsetdataelement_ms',
                                hideLabel: true,
                                dataFields: ['id', 'shortName'],
                                valueField: 'id',
                                displayField: 'shortName',
                                width: GLOBAL.conf.multiselect_width,
                                height: GLOBAL.util.getMultiSelectHeight(),
                                store: GLOBAL.stores.dataElement
                            }
                        ]
                    },
                    {
                        xtype: 'form',
                        items: [
                            {
                                xtype: 'toolbar',
                                style: 'padding-top:4px',
                                items: [
                                    '->',
                                    {
                                        xtype: 'button',
                                        id: 'assignpredefinedmaplegendsetdataelement_b',
                                        text: i18n_assign,
                                        iconCls: 'icon-assign',
                                        handler: function() {
                                            var ls = Ext.getCmp('predefinedmaplegendsetdataelement_cb').getValue();
                                            var lsrw = Ext.getCmp('predefinedmaplegendsetdataelement_cb').getRawValue();
                                            var lims = Ext.getCmp('predefinedmaplegendsetdataelement_ms').getValue();
                                            
                                            if (!ls) {
                                                Ext.message.msg(false, i18n_please_select_a_legend_set);
                                                return;
                                            }
                                            
                                            if (!lims) {
                                                Ext.message.msg(false, i18n_please_select_at_least_one_indicator);
                                                return;
                                            }
                                            
                                            var array = [];
                                            array = lims.split(',');
                                            var params = '?dataElements=' + array[0];
                                            
                                            if (array.length > 1) {
                                                for (var i = 1; i < array.length; i++) {
                                                    array[i] = '&dataElements=' + array[i];
                                                    params += array[i];
                                                }
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: GLOBAL.conf.path_mapping + 'assignDataElementsToMapLegendSet.action' + params,
                                                method: 'POST',
                                                params: {id: ls},
                                                success: function(r) {
                                                    Ext.message.msg(true, i18n_legendset+' <span class="x-msg-hl">' + lsrw + '</span> ' + i18n_was_updated);
                                                    GLOBAL.stores.predefinedMapLegendSet.load();
                                                }
                                            });
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ],
                listeners: {
                    expand: function() {
                        predefinedMapLegendSetWindow.setHeight(GLOBAL.util.getMultiSelectHeight() + (Ext.isChrome ? 241:238));
                        
                        if (!GLOBAL.stores.dataElement.isLoaded) {
                            GLOBAL.stores.dataElement.load();
                        }
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(123);
                    }
                }
            }
        ],
        listeners: {
            afterrender: function() {
                predefinedMapLegendSetWindow.setHeight(Ext.isChrome ? 348:346);
            }
        }
    });
			
    /* Section: help */
	function setHelpText(topic, tab) {
		Ext.Ajax.request({
			url: '../../dhis-web-commons-about/getHelpContent.action',
			method: 'POST',
			params: {id: topic},
			success: function(r) {
				tab.body.update('<div id="help">' + r.responseText + '</div>');
			}
		});
	}
    
	var helpWindow = new Ext.Window({
        id: 'help_w',
        title: '<span id="window-help-title">'+i18n_help+'</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: 556,
		height: 236, 
        items: [
            {
                xtype: 'tabpanel',
                activeTab: 0,
				layoutOnTabChange: true,
                deferredRender: false,
                plain: true,
                defaults: {layout: 'fit'},
                listeners: {
                    tabchange: function(panel, tab) {
                        if (tab.id == 'help0') {
							setHelpText(GLOBAL.conf.thematicMap, tab);
                            helpWindow.setHeight(Ext.isChrome ? 242:223);
                        }
                        else if (tab.id == 'help1') {
							setHelpText(GLOBAL.conf.favorites, tab);
                            helpWindow.setHeight(Ext.isChrome ? 146:135);
                        }
                        else if (tab.id == 'help2') {
                            setHelpText(GLOBAL.conf.legendSets, tab);
                            helpWindow.setHeight(Ext.isChrome ? 161:150);
                        }
						if (tab.id == 'help3') { 
                            setHelpText(GLOBAL.conf.imageExport, tab);
                            helpWindow.setHeight(Ext.isChrome ? 235:215);
                        }
                        else if (tab.id == 'help4') {
                            setHelpText(GLOBAL.conf.administration, tab);
                            helpWindow.setHeight(Ext.isChrome ? 161:149);
                        }
                        else if (tab.id == 'help5') {
                            setHelpText(GLOBAL.conf.overlayRegistration, tab);
                            helpWindow.setHeight(Ext.isChrome ? 398:367);
                        }
                        else if (tab.id == 'help6') {
                            setHelpText(GLOBAL.conf.setup, tab);
                            helpWindow.setHeight(Ext.isChrome ? 537:485);
                        }
                    }
                },
                items: [
                    {
                        id: 'help0',
                        title: '<span class="panel-tab-title">' + i18n_thematic_map + '</span>'
                    },
                    {
                        id: 'help1',
                        title: '<span class="panel-tab-title">' + i18n_favorites + '</span>'
                    },
                    {
                        id: 'help2',
                        title: '<span class="panel-tab-title">' + i18n_legendset + '</span>'
                    },
                    {
                        id: 'help3',
                        title: '<span class="panel-tab-title">' + i18n_image_export + '</span>'
                    },
                    {
                        id: 'help4',
                        title: '<span class="panel-tab-title">' + i18n_administrator + '</span>'
                    },
                    {
                        id: 'help5',
                        title: '<span class="panel-tab-title">' + i18n_overlays + '</span>'
                    },
                    {
                        id: 'help6',
                        title: '<span class="panel-tab-title">' + i18n_setup + '</span>'
                    }
                ]
            }
        ]
    });

    /* Section: overlays */
	var overlaysWindow = new Ext.Window({
        id: 'overlays_w',
        title: '<span id="window-maplayer-title">' + i18n_overlays + '</span>',
		layout: 'fit',
        closeAction: 'hide',
        height: 307,
		width: GLOBAL.conf.window_width,
        items: [
            {
                xtype: 'form',
                bodyStyle: 'padding:8px',
                items: [
                    {html: '<div class="window-info">Register new overlay</div>'},
                    {
                        xtype: 'textfield',
                        id: 'maplayername_tf',
                        emptytext: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_display_name,
                        width: GLOBAL.conf.combo_width_fieldset,
                        autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '35'}
                    },
                    {
                        xtype: 'combo',
                        id:'maplayermapsourcefile_cb',
                        editable: false,
                        displayField: 'name',
                        valueField: 'name',
                        triggerAction: 'all',
                        mode: 'remote',
                        emptytext: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_geojson_file,
                        width: GLOBAL.conf.combo_width_fieldset,
                        store:GLOBAL.stores.geojsonFiles
                    },
                    {
                        xtype: 'colorfield',
                        id: 'maplayerfillcolor_cf',
                        emptyText: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_fill_color,
                        allowBlank: false,
                        width: GLOBAL.conf.combo_width_fieldset,
                        value:"#C0C0C0"
                    },
                    {
                        xtype: 'combo',
                        id: 'maplayerfillopacity_cb',
                        editable: true,
                        valueField: 'value',
                        displayField: 'value',
                        mode: 'local',
                        triggerAction: 'all',
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_fill_opacity,
                        width: GLOBAL.conf.combo_number_width,
                        minListWidth: GLOBAL.conf.combo_number_width,
                        value: 0.5,
                        store: {
                            xtype: 'arraystore',
                            fields: ['value'],
                            data: [['0'],['0.1'],['0.2'],['0.3'],['0.4'],['0.5'],['0.6'],['0.7'],['0.8'],['0.9'],['1.0']]
                        }
                    },
                    {
                        xtype: 'colorfield',
                        id: 'maplayerstrokecolor_cf',
                        emptyText: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_stroke_color,
                        allowBlank: false,
                        width: GLOBAL.conf.combo_width_fieldset,
                        value:"#000000"
                    },
                    {
                        xtype: 'combo',
                        id: 'maplayerstrokewidth_cb',
                        editable: true,
                        valueField: 'value',
                        displayField: 'value',
                        mode: 'local',
                        triggerAction: 'all',
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_stroke_width,
                        width: GLOBAL.conf.combo_number_width,
                        minListWidth: GLOBAL.conf.combo_number_width,
                        value: 2,
                        store: {
                            xtype: 'arraystore',
                            fields: ['value'],
                            data: [[0],[1],[2],[3],[4],[5]]
                        }
                    },
                    {html: '<div class="window-p"></div>'},
                    {html: '<div class="window-info">Delete overlay</div>'},
                    {
                        xtype: 'combo',
                        id: 'maplayer_cb',
                        editable:false,
                        valueField: 'id',
                        displayField: 'name',
                        mode: 'remote',
                        forceSelection: true,
                        triggerAction: 'all',
                        emptytext: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_overlays,
                        width: GLOBAL.conf.combo_width_fieldset,                
                        store:GLOBAL.stores.overlay
                    }
                ]
            }
        ],
        bbar: [
            '->',
            {
                xtype: 'button',
                id: 'newmaplayer_b',
                text: 'Register',
                iconCls: 'icon-add',
                handler: function() {
                    var mln = Ext.getCmp('maplayername_tf').getRawValue();
                    var mlfc = Ext.getCmp('maplayerfillcolor_cf').getValue();
                    var mlfo = Ext.getCmp('maplayerfillopacity_cb').getRawValue();
                    var mlsc = Ext.getCmp('maplayerstrokecolor_cf').getValue();
                    var mlsw = Ext.getCmp('maplayerstrokewidth_cb').getRawValue();
                    var mlmsf = Ext.getCmp('maplayermapsourcefile_cb').getValue();
                    
                    if (!mln || !mlmsf) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                        return;
                    }
                    
                    if (GLOBAL.stores.overlay.find('name', mln) !== -1) {
                        Ext.message.msg(false, i18n_name + ' <span class="x-msg-hl">' + mln + '</span> ' + i18n_is_already_in_use);
                        return;
                    }
                        
                    Ext.Ajax.request({
                        url: GLOBAL.conf.path_mapping + 'addOrUpdateMapLayer' + GLOBAL.conf.type,
                        method: 'POST',
                        params: {name: mln, type: 'overlay', mapSource: mlmsf, fillColor: mlfc, fillOpacity: mlfo, strokeColor: mlsc, strokeWidth: mlsw},
                        success: function(r) {
                            Ext.message.msg(true, 'Overlay <span class="x-msg-hl">' + mln + '</span> ' + i18n_registered);
                            GLOBAL.stores.overlay.load();
                    
                            GLOBAL.vars.map.addLayer(
                                new OpenLayers.Layer.Vector(mln, {
                                    'visibility': false,
                                    'styleMap': new OpenLayers.StyleMap({
                                        'default': new OpenLayers.Style(
                                            OpenLayers.Util.applyDefaults(
                                                {'fillColor': mlfc, 'fillOpacity': mlfo, 'strokeColor': mlsc, 'strokeWidth': mlsw},
                                                OpenLayers.Feature.Vector.style['default']
                                            )
                                        )
                                    }),
                                    'strategies': [new OpenLayers.Strategy.Fixed()],
                                    'protocol': new OpenLayers.Protocol.HTTP({
                                        'url': GLOBAL.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + mlmsf,
                                        'format': new OpenLayers.Format.GeoJSON()
                                    })
                                })
                            );
                            
                            Ext.getCmp('maplayername_tf').reset();
                            Ext.getCmp('maplayermapsourcefile_cb').clearValue();
                        }
                    });
                }
            },
            {
                xtype: 'button',
                id: 'deletemaplayer_b',
                text: i18n_delete,
                iconCls: 'icon-remove',
                handler: function() {
                    var ml = Ext.getCmp('maplayer_cb').getValue();
                    var mln = Ext.getCmp('maplayer_cb').getRawValue();
                    
                    if (!ml) {
                        Ext.message.msg(false, i18n_please_select_an_overlay);
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: GLOBAL.conf.path_mapping + 'deleteMapLayer' + GLOBAL.conf.type,
                        method: 'POST',
                        params: {id:ml},
                        success: function(r) {
                            Ext.message.msg(true, 'Overlay <span class="x-msg-hl">' + mln + '</span> '+ i18n_deleted);
                            GLOBAL.stores.overlay.load();
                            Ext.getCmp('maplayer_cb').clearValue();
                        }
                    });
                    
                    GLOBAL.vars.map.getLayersByName(mln)[0].destroy();
                }
            }
        ]
    });
            
    /* Section: base layers */
    var baselayersWindow = new Ext.Window({
        id: 'baselayers_w',
        title: '<span id="window-maplayer-title">' + i18n_baselayers + '</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: GLOBAL.conf.window_width,
        height: 229,
        items: [
            {
                xtype: 'form',
                bodyStyle: 'padding:8px',
                items: [
                    {html: '<div class="window-info">Register new base layer</div>'},
                    {
                        xtype: 'textfield',
                        id: 'maplayerbaselayersname_tf',
                        emptytext: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_display_name,
                        width: GLOBAL.conf.combo_width_fieldset,
                        autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '35'}
                    },
                    {
                        xtype: 'textfield',
                        id: 'maplayerbaselayersurl_tf',
                        emptytext: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_url,
                        width: GLOBAL.conf.combo_width_fieldset,
                    },
                    {
                        xtype: 'textfield',
                        id: 'maplayerbaselayerslayer_tf',
                        emptytext: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_layer,
                        width: GLOBAL.conf.combo_width_fieldset,
                    },
                    {html: '<div class="window-p"></div>'},
                    {html: '<div class="window-info">Delete overlay</div>'},
                    {
                        xtype: 'combo',
                        id: 'maplayerbaselayers_cb',
                        editable: false,
                        valueField: 'id',
                        displayField: 'name',
                        mode: 'remote',
                        forceSelection: true,
                        triggerAction: 'all',
                        emptytext: GLOBAL.conf.emptytext,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        fieldLabel: i18n_baselayer,
                        width: GLOBAL.conf.combo_width_fieldset,                
                        store: GLOBAL.stores.baseLayer
                    }
                ]
            }
        ],
        bbar: [
            '->',
            {
				xtype: 'button',
				id: 'newmaplayerbaselayers_b',
				text: i18n_register,
				iconCls: 'icon-add',
				handler: function() {
					var mlbn = Ext.getCmp('maplayerbaselayersname_tf').getValue();
					var mlbu = Ext.getCmp('maplayerbaselayersurl_tf').getValue();
					var mlbl = Ext.getCmp('maplayerbaselayerslayer_tf').getValue();
					
					if (!mlbn || !mlbu || !mlbl) {
						Ext.message.msg(false, i18n_form_is_not_complete);
						return;
					}
					
                    if (GLOBAL.stores.baseLayer.find('name', mlbn) !== -1) {
                        Ext.message.msg(false, i18n_name + ' <span class="x-msg-hl">' + mlbn + '</span> ' + i18n_is_already_in_use);
                        return;
                    }
					
                    Ext.Ajax.request({
                        url: GLOBAL.conf.path_mapping + 'addOrUpdateMapLayer' + GLOBAL.conf.type,
                        method: 'POST',
                        params: {name: mlbn, type: GLOBAL.conf.map_layer_type_baselayer, mapSource: mlbu, layer: mlbl, fillColor: '', fillOpacity: 0, strokeColor: '', strokeWidth: 0},
                        success: function(r) {
                            Ext.message.msg(true, i18n_baselayer + '<span class="x-msg-hl"> ' + mlbn + '</span> ' + i18n_registered);                            
                            GLOBAL.vars.map.addLayers([
                                new OpenLayers.Layer.WMS(mlbn, mlbu, {layers: mlbl})
                            ]);

                            GLOBAL.stores.baseLayer.load();
                            Ext.getCmp('maplayerbaselayersname_tf').reset();
                            Ext.getCmp('maplayerbaselayersurl_tf').reset();
                            Ext.getCmp('maplayerbaselayerslayer_tf').reset();
                        }
                    });
				}
			},
            {
                xtype: 'button',
                id: 'deletemaplayerbaselayers_b',
                text: i18n_delete,
                iconCls: 'icon-remove',
                handler: function() {
                    var ml = Ext.getCmp('maplayerbaselayers_cb').getValue();
                    var mln = Ext.getCmp('maplayerbaselayers_cb').getRawValue();
                    
                    if (!ml) {
                        Ext.message.msg(false, i18n_please_select_a_baselayer);
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: GLOBAL.conf.path_mapping + 'deleteMapLayer' + GLOBAL.conf.type,
                        method: 'POST',
                        params: {id: ml},
                        success: function(r) {
                            Ext.message.msg(true, i18n_baselayer + ' <span class="x-msg-hl">' + mln + '</span> '+i18n_deleted);
                            GLOBAL.stores.baseLayer.load({callback: function() {
                                Ext.getCmp('maplayerbaselayers_cb').clearValue();
                                var names = GLOBAL.stores.baseLayer.collect('name');
                                
                                for (var i = 0; i < names.length; i++) {
                                    GLOBAL.vars.map.getLayersByName(names[i])[0].setVisibility(false);
                                }
                                
                                GLOBAL.vars.map.getLayersByName(mln)[0].destroy(false);
                            }});
                        }
                    });
                    
                }
            }
        ]
    });

    /* Section: administrator settings */
    var adminWindow = new Ext.Window({
        id: 'admin_w',
        title: '<span id="window-admin-title">Administrator settings</span>',
        layout: 'accordion',
        closeAction: 'hide',
        width: GLOBAL.conf.window_width,
        height: 119,
        items: [
            {
                xtype: 'form',
                title: 'Date type',
                bodyStyle: 'padding:8px',
                items: [
                    {html: '<div class="window-info">Set thematic map date type</div>'},
                    {
                        xtype: 'combo',
                        id: 'mapdatetype_cb',
                        fieldLabel: i18n_date_type,
                        labelSeparator: GLOBAL.conf.labelseparator,
                        editable: false,
                        valueField: 'value',
                        displayField: 'text',
                        mode: 'local',
                        value: GLOBAL.conf.map_date_type_fixed,
                        triggerAction: 'all',
                        width: GLOBAL.conf.combo_width_fieldset,
                        minListWidth: GLOBAL.conf.combo_width_fieldset,
                        store: {
                            xtype: 'arraystore',
                            fields: ['value', 'text'],
                            data: [
                                [GLOBAL.conf.map_date_type_fixed, i18n_fixed_periods],
                                [GLOBAL.conf.map_date_type_start_end, i18n_start_end_dates]
                            ]
                        },
                        listeners: {
                            'select': function(cb) {
                                if (cb.getValue() != GLOBAL.vars.mapDateType.value) {
                                    GLOBAL.vars.mapDateType.value = cb.getValue();
                                    Ext.Ajax.request({
                                        url: GLOBAL.conf.path_mapping + 'setMapUserSettings' + GLOBAL.conf.type,
                                        method: 'POST',
                                        params: {mapDateType: GLOBAL.vars.mapDateType.value},
                                        success: function() {
                                            Ext.message.msg(true, '<span class="x-msg-hl">' + cb.getRawValue() + '</span> '+i18n_saved_as_date_type);
                                            choropleth.prepareMapViewDateType();
                                            symbol.prepareMapViewDateType();
                                        }
                                    });
                                }
                            }
                        }
                    }
                ]
            }
        ]
    });
	
	/* Section: layers */
    var choroplethLayer = new OpenLayers.Layer.Vector('Polygon layer', {
        'visibility': false,
        'displayInLayerSwitcher': false,
        'styleMap': new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
                    {'fillOpacity': 1, 'strokeColor': '#222222', 'strokeWidth': 1, 'pointRadius': 5},
                    OpenLayers.Feature.Vector.style['default']
                )
            ),
            'select': new OpenLayers.Style(
                {'strokeColor': '#000000', 'strokeWidth': 2, 'cursor': 'pointer'}
            )
        })
    });
    
    var symbolLayer = new OpenLayers.Layer.Vector('Point layer', {
        'visibility': false,
        'displayInLayerSwitcher': false,
        'styleMap': new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
                    {'fillOpacity': 1, 'strokeColor': '#222222', 'strokeWidth': 1, 'pointRadius': 5},
                    OpenLayers.Feature.Vector.style['default']
                )
            ),
            'select': new OpenLayers.Style(
                {'strokeColor': '#000000', 'strokeWidth': 2, 'cursor': 'pointer'}
            )
        })
    });
    
    GLOBAL.vars.map.addLayers([choroplethLayer, symbolLayer]);
        
    var layerTree = new Ext.tree.TreePanel({
        title: '<span class="panel-title">' + i18n_map_layers + '</span>',
        enableDD: true,
        bodyStyle: 'padding-bottom:5px;',
        rootVisible: false,
        root: {
            nodeType: 'async',
            children: [
                {
                    nodeType: 'gx_baselayercontainer',
                    singleClickExpand: true,
                    expanded: true,
                    text: 'Base layers',
                    iconCls: 'icon-background'
                },
                {
                    nodeType: 'gx_overlaylayercontainer',
                    singleClickExpand: true
                },
                {
                    nodeType: 'gx_layer',
                    layer: 'Polygon layer'
                },
                {
                    nodeType: 'gx_layer',
                    layer: 'Point layer'
                }
            ]
        },
        contextMenuBaselayer: new Ext.menu.Menu({
            items: [
                {
                    text: 'Show WMS legend',
                    iconCls: 'menu-layeroptions-wmslegend',
                    handler: function(item, e) {
                        var layer = item.parentMenu.contextNode.layer;

                        var frs = layer.getFullRequestString({
                            REQUEST: "GetLegendGraphic",
                            WIDTH: null,
                            HEIGHT: null,
                            EXCEPTIONS: "application/vnd.ogc.se_xml",
                            LAYERS: layer.params.LAYERS,
                            LAYER: layer.params.LAYERS,
                            SRS: null,
                            FORMAT: 'image/png'
                        });
                        
                        var wmsLayerLegendWindow = new Ext.Window({
                            title: 'WMS Legend: <span style="font-weight:normal;">' + layer.name + '</span>',
                            items: [
                                {
                                    xtype: 'panel',
                                    html: '<img src="' + frs + '">'
                                }
                            ]
                        });
                        wmsLayerLegendWindow.setPagePosition(Ext.getCmp('east').x - 500, Ext.getCmp('center').y + 50);
                        wmsLayerLegendWindow.show();
                    }
                },
                {
                    text: 'Opacity',
                    iconCls: 'menu-layeroptions-opacity',
                    menu: { 
                        items: GLOBAL.conf.opacityItems,
                        listeners: {
                            'itemclick': function(item) {
                                item.parentMenu.parentMenu.contextNode.layer.setOpacity(item.text);
                            }
                        }
                    }
                }
            ]
        }),
        contextMenuOverlay: new Ext.menu.Menu({
            items: [
                {
                    text: 'Opacity',
                    iconCls: 'menu-layeroptions-opacity',
                    menu: { 
                        items: GLOBAL.conf.opacityItems,
                        listeners: {
                            'itemclick': function(item) {
                                item.parentMenu.parentMenu.contextNode.layer.setOpacity(item.text);
                            }
                        }
                    }
                }
            ]
        }),
        contextMenuVector: new Ext.menu.Menu({
            showLocateFeatureWindow: function(cm) {
                var layer = GLOBAL.vars.map.getLayersByName(cm.contextNode.attributes.layer)[0];

                var data = [];
                for (var i = 0; i < layer.features.length; i++) {
                    data.push([layer.features[i].data.id || i, layer.features[i].data.name]);
                }
                
                if (data.length) {
                    var featureStore = new Ext.data.ArrayStore({
                        mode: 'local',
                        idProperty: 'id',
                        fields: ['id','name'],
                        sortInfo: {field: 'name', direction: 'ASC'},
                        autoDestroy: true,
                        data: data
                    });
                    
                    if (Ext.getCmp('locatefeature_w')) {
                        Ext.getCmp('locatefeature_w').destroy();
                    }
                    
                    var locateFeatureWindow = new Ext.Window({
                        id: 'locatefeature_w',
                        title: 'Locate features',
                        layout: 'fit',
                        width: GLOBAL.conf.window_width,
                        height: GLOBAL.util.getMultiSelectHeight() + 140,
                        items: [
                            {
                                xtype: 'form',
                                bodyStyle:'padding:8px',
                                items: [
                                    {html: '<div class="window-info">Locate an organisation unit in the map</div>'},
                                    {
                                        xtype: 'colorfield',
                                        id: 'highlightcolor_cf',
                                        emptyText: GLOBAL.conf.emptytext,
                                        labelSeparator: GLOBAL.conf.labelseparator,
                                        fieldLabel: i18n_highlight_color,
                                        allowBlank: false,
                                        width: GLOBAL.conf.combo_width_fieldset,
                                        value: "#0000FF"
                                    },
                                    {
                                        xtype: 'textfield',
                                        id: 'locatefeature_tf',
                                        emptyText: GLOBAL.conf.emptytext,
                                        labelSeparator: GLOBAL.conf.labelseparator,
                                        fieldLabel: i18n_feature_filter,
                                        width: GLOBAL.conf.combo_width_fieldset,
                                        enableKeyEvents: true,
                                        listeners: {
                                            'keyup': function(tf) {
                                                featureStore.filter('name', tf.getValue(), true, false);
                                            }
                                        }
                                    },
                                    {html: '<div class="window-p"></div>'},
                                    {
                                        xtype: 'grid',
                                        id: 'featuregrid_gp',
                                        height: GLOBAL.util.getMultiSelectHeight(),
                                        cm: new Ext.grid.ColumnModel({
                                            columns: [{id: 'name', header: 'Features', dataIndex: 'name', width: 250}]
                                        }),
                                        sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                                        viewConfig: {forceFit: true},
                                        sortable: true,
                                        autoExpandColumn: 'name',
                                        store: featureStore,
                                        listeners: {
                                            'cellclick': {
                                                fn: function(g, ri, ci, e) {
                                                    layer.redraw();
                                                    
                                                    var id, feature;
                                                    id = g.getStore().getAt(ri).data.id;
                                                    
                                                    for (var i = 0; i < layer.features.length; i++) {
                                                        if (layer.features[i].data.id == id) {
                                                            feature = layer.features[i];
                                                            break;
                                                        }
                                                    }
                                                    
                                                    var color = Ext.getCmp('highlightcolor_cf').getValue();
                                                    var symbolizer;
                                                    
                                                    if (feature.attributes.featureType == GLOBAL.conf.map_feature_type_multipolygon ||
                                                        feature.attributes.featureType == GLOBAL.conf.map_feature_type_polygon) {
                                                        symbolizer = new OpenLayers.Symbolizer.Polygon({
                                                            'strokeColor': color,
                                                            'fillColor': color
                                                        });
                                                    }
                                                    else if (feature.attributes.featureType == GLOBAL.conf.map_feature_type_point) {
                                                        symbolizer = new OpenLayers.Symbolizer.Point({
                                                            'pointRadius': 7,
                                                            'fillColor': color
                                                        });
                                                    }
                                                    
                                                    layer.drawFeature(feature,symbolizer);
                                                }
                                            }
                                        }
                                    }
                                ]
                            }
                        ],
                        listeners: {
                            'hide': function() {
                                layer.redraw();
                            }
                        }
                    });
                    locateFeatureWindow.setPagePosition(Ext.getCmp('east').x - (GLOBAL.conf.window_width + 15 + 5), Ext.getCmp('center').y + 41);
                    locateFeatureWindow.show();
                }
                else {
                    Ext.message.msg(false, '<span class="x-msg-hl">' + layer.name + '</span>: No features rendered');
                }
            },
            items: [
                {
                    text: 'Locate feature',
                    iconCls: 'menu-layeroptions-locate',
                    handler: function(item, e) {
                        item.parentMenu.showLocateFeatureWindow(item.parentMenu);
                    }
                },
                {
                    text: 'Show/hide labels',
                    iconCls: 'menu-layeroptions-labels',
                    handler: function(item, e) {
                        var layer = GLOBAL.vars.map.getLayersByName(item.parentMenu.contextNode.attributes.layer)[0];
                        
                        if (layer.features.length) {
                            if (layer.name == 'Polygon layer') {
                                GLOBAL.util.toggleFeatureLabels(choropleth);
                            }
                            else if (layer.name == 'Point layer') {
                                GLOBAL.util.toggleFeatureLabels(symbol);
                            }
                        }
                        else {
                            Ext.message.msg(false, '<span class="x-msg-hl">' + layer.name + '</span>: No features rendered');
                        }
                    }
                },
                {
                    text: 'Opacity',
                    iconCls: 'menu-layeroptions-opacity',
                    menu: { 
                        items: GLOBAL.conf.opacityItems,
                        listeners: {
                            'itemclick': function(item) {
                                item.parentMenu.parentMenu.contextNode.layer.setOpacity(item.text);
                            }
                        }
                    }
                }
            ]
        }),
		listeners: {
            'contextmenu': function(node, e) {
                if (node.attributes.text != 'Base layers' && node.attributes.text != 'Overlays') {
                    node.select();
                    
                    if (node.parentNode.attributes.text == 'Base layers') {
                        var cmb = node.getOwnerTree().contextMenuBaselayer;
                        cmb.contextNode = node;
                        cmb.showAt(e.getXY());
                    }
                    
                    else if (node.parentNode.attributes.text == 'Overlays') {
                        var cmo = node.getOwnerTree().contextMenuOverlay;
                        cmo.contextNode = node;
                        cmo.showAt(e.getXY());
                    }
                    
                    else {
                        var cmv = node.getOwnerTree().contextMenuVector;
                        cmv.contextNode = node;
                        cmv.showAt(e.getXY());
                    }
                }
            }
		},
        bbar: [
            {
                xtype: 'button',
                id: 'baselayers_b',
                text: 'Base layers',
                iconCls: 'icon-add',
                handler: function() {
                    Ext.getCmp('baselayers_w').setPagePosition(Ext.getCmp('east').x - (GLOBAL.conf.window_width + 15 + 5), Ext.getCmp('center').y + 41);
                    Ext.getCmp('baselayers_w').show();
                }
            },
            {
                xtype: 'button',
                id: 'overlays_b',
                text: 'Overlays',
                iconCls: 'icon-add',
                handler: function() {
                    Ext.getCmp('overlays_w').setPagePosition(Ext.getCmp('east').x - (GLOBAL.conf.window_width + 15 + 5), Ext.getCmp('center').y + 41);
                    Ext.getCmp('overlays_w').show();
                }
            }
        ]
	});
	
    /* Section: widgets */
    choropleth = new mapfish.widgets.geostat.Choropleth({
        id: 'choropleth',
        map: GLOBAL.vars.map,
        layer: choroplethLayer,
		title: '<span class="panel-title">' + i18n_polygon_layer + '</span>',
        featureSelection: false,
        legendDiv: 'polygonlegend',
        defaults: {width: 130},
        listeners: {
            'expand': function() {
                GLOBAL.vars.activePanel.setPolygon();
            }
        }
    });

    symbol = new mapfish.widgets.geostat.Symbol({
        id: 'symbol',
        map: GLOBAL.vars.map,
        layer: symbolLayer,
		title: '<span class="panel-title">' + i18n_point_layer + '</span>',
        featureSelection: false,
        legendDiv: 'pointlegend',
        defaults: {width: 130},
        listeners: {
            'expand': function() {
                GLOBAL.vars.activePanel.setPoint();
                
                if (!this.form.findField('level').getValue()) {
					if (!GLOBAL.stores.organisationUnitLevel.isLoaded) {
						GLOBAL.stores.organisationUnitLevel.load();
					}
				}
            }
        }
    });
    
    //mapping = new mapfish.widgets.geostat.Mapping({});    
	
	/* Section: map toolbar */
	var mapLabel = new Ext.form.Label({
		text: i18n_map,
		style: 'font:bold 11px arial; color:#333;'
	});
	
	var zoomInButton = new Ext.Button({
		iconCls: 'icon-zoomin',
		tooltip: i18n_zoom_in,
		handler: function() {
			GLOBAL.vars.map.zoomIn();
		}
	});
	
	var zoomOutButton = new Ext.Button({
		iconCls: 'icon-zoomout',
		tooltip: i18n_zoom_out,
		handler:function() {
			GLOBAL.vars.map.zoomOut();
		}
	});
	
	var zoomToVisibleExtentButton = new Ext.Button({
		iconCls: 'icon-zoommin',
		tooltip: i18n_zoom_to_visible_extent,
		handler: function() {
            if (GLOBAL.vars.activePanel.isPolygon()) {
                if (choropleth.layer.getDataExtent()) {
                    GLOBAL.vars.map.zoomToExtent(choropleth.layer.getDataExtent());
                }
            }
            else if (GLOBAL.vars.activePanel.isPoint()) {
                if (symbol.layer.getDataExtent()) {
                    GLOBAL.vars.map.zoomToExtent(symbol.layer.getDataExtent());
                }
            }
        }
	});
	
	var favoritesButton = new Ext.Button({
		iconCls: 'icon-favorite',
		tooltip: i18n_favorite_map_views,
		handler: function() {
			var x = Ext.getCmp('center').x + 15;
			var y = Ext.getCmp('center').y + 41;    
			favoriteWindow.setPosition(x,y);

			if (favoriteWindow.visible) {
				favoriteWindow.hide();
			}
			else {
				favoriteWindow.show();
			}
		}
	});
	
	var exportImageButton = new Ext.Button({
		iconCls: 'icon-image',
		tooltip: i18n_export_map_as_image,
		handler: function() {
			var x = Ext.getCmp('center').x + 15;
			var y = Ext.getCmp('center').y + 41;   
			
			exportImageWindow.setPosition(x,y);

			if (exportImageWindow.visible) {
				exportImageWindow.hide();
			}
			else {
				exportImageWindow.show();
			}
		}
	});
	
	var exportExcelButton = new Ext.Button({
		iconCls: 'icon-excel',
		tooltip: i18n_export_map_as_excel,
		handler: function() {
			var x = Ext.getCmp('center').x + 15;
			var y = Ext.getCmp('center').y + 41;   
			
			exportExcelWindow.setPosition(x,y);

			if (exportExcelWindow.visible) {
				exportExcelWindow.hide();
			}
			else {
				exportExcelWindow.show();
			}
		}
	});
	
	var predefinedMapLegendSetButton = new Ext.Button({
		iconCls: 'icon-predefinedlegendset',
		tooltip: i18n_create_predefined_legend_sets,
		handler: function() {
			var x = Ext.getCmp('center').x + 15;
			var y = Ext.getCmp('center').y + 41;
			predefinedMapLegendSetWindow.setPosition(x,y);
		
			if (predefinedMapLegendSetWindow.visible) {
				predefinedMapLegendSetWindow.hide();
			}
			else {
				predefinedMapLegendSetWindow.show();
                if (!GLOBAL.stores.predefinedMapLegend.isLoaded) {
                    GLOBAL.stores.predefinedMapLegend.load();
                }
			}
		}
	});
	
	var adminButton = new Ext.Button({
		iconCls: 'icon-admin',
		tooltip: 'Administrator settings',
		handler: function() {
			var x = Ext.getCmp('center').x + 15;
			var y = Ext.getCmp('center').y + 41;
			adminWindow.setPosition(x,y);
			adminWindow.show();
		}
	});
	
	var helpButton = new Ext.Button({
		iconCls: 'icon-help',
		tooltip: i18n_help ,
		handler: function() {
			var c = Ext.getCmp('center').x;
			var e = Ext.getCmp('east').x;
			helpWindow.setPagePosition(c+((e-c)/2)-280, Ext.getCmp('east').y + 100);
			helpWindow.show();
		}
	});
	
	var exitButton = new Ext.Button({
		text: i18n_exit_gis,
        iconCls: 'icon-exit',
		tooltip: i18n_return_to_DHIS_2_dashboard,
		handler: function() {
			window.location.href = '../../dhis-web-portal/redirect.action';
		}
	});
	
	var mapToolbar = new Ext.Toolbar({
		id: 'map_tb',
		items: [
			' ',' ',' ',' ',
			mapLabel,
			' ',' ',' ',' ',' ',
			zoomInButton,
			zoomOutButton, ' ',
			zoomToVisibleExtentButton,
			'-',
			favoritesButton,
            predefinedMapLegendSetButton,
			exportImageButton,
			'-',
            adminButton,
			helpButton,
			'->',
			exitButton,' ',' '
		]
	});
    
	/* Section: viewport */
    viewport = new Ext.Viewport({
        id: 'viewport',
        layout: 'border',
        margins: '0 0 5 0',
        items:
        [
            new Ext.BoxComponent(
            {
                region: 'north',
                id: 'north',
                el: 'north',
                height: 0
            }),
            {
                region: 'east',
                id: 'east',
                collapsible: true,
				header: false,
                width: 200,
                margins: '0 5 0 5',
                defaults: {
                    border: true,
                    frame: true
                },
                layout: 'anchor',
                items:
                [
                    layerTree,
                    {
                        title: '<span class="panel-title">' + i18n_overview_map + '</span>',
                        html:'<div id="overviewmap" style="height:97px; padding-top:0px;"></div>'
                    },
                    {
                        title: '<span class="panel-title">'+ i18n_cursor_position +'</span>',
                        height: 65,
                        contentEl: 'position',
                        anchor: '100%',
                        bodyStyle: 'padding-left: 4px;'
                    },
					{
						xtype: 'panel',
						title: '<span class="panel-title">' + i18n_feature_data + '</span>',
						height: 65,
						anchor: '100%',
						bodyStyle: 'padding-left: 4px;',
						items:
						[
							new Ext.form.Label({
								id: 'featureinfo_l',
								text: i18n_no_feature_selected,
								style: 'color:#666'
							})
						]
					},
                    {
                        title: '<span class="panel-title">' + i18n_map_legend_polygon + '</span>',
                        minHeight: 65,
                        autoHeight: true,
                        contentEl: 'polygonlegendpanel',
                        anchor: '100%',
						bodyStyle: 'padding-left: 4px;'
                    },
                    {
                        title: '<span class="panel-title">' + i18n_map_legend_point + '</span>',
                        minHeight: 65,
                        autoHeight: true,
                        contentEl: 'pointlegendpanel',
                        anchor: '100%',
						bodyStyle: 'padding-left: 4px;'
                    }
                ]
            },
            {
                region: 'west',
                id: 'west',
                split: true,
				header: false,
                collapsible: true,
				collapseMode: 'mini',
                width: GLOBAL.conf.west_width,
                minSize: 175,
                maxSize: 500,
                margins: '0 0 0 5',
                layout: 'accordion',
                defaults: {
                    border: true,
                    frame: true
                },
                items: [
                    choropleth,
                    symbol
                ]
            },
            {
                xtype: 'gx_mappanel',
                region: 'center',
                id: 'center',
                height: 1000,
                width: 800,
                map: GLOBAL.vars.map,
                zoom: 3,
				tbar: mapToolbar
            }
        ]
    });
	
	GLOBAL.vars.map.addControl(new OpenLayers.Control.MousePosition({
        displayClass: 'void', 
        div: $('mouseposition'), 
        prefix: '<span style="color:#666;">x: &nbsp;</span>',
        separator: '<br/><span style="color:#666;">y: &nbsp;</span>'
    }));
    
    GLOBAL.vars.map.addControl(new OpenLayers.Control.OverviewMap({
        div: $('overviewmap'),
        size: new OpenLayers.Size(188, 97),
        minRectSize: 0,
        layers: [
            new OpenLayers.Layer.WMS(
                "World",
                "http://labs.metacarta.com/wms/vmap0", 
                {layers: "basic"}
            )
        ]
    }));
    
    GLOBAL.vars.map.addControl(new OpenLayers.Control.ZoomBox());
    
    function toggleSelectFeatures(e) {
        if (GLOBAL.stores.overlay.find('name', e.layer.name) !== -1) {
            var names = GLOBAL.stores.overlay.collect('name');
            var visibleOverlays = false;
            
            for (var i = 0; i < names.length; i++) {
                if (GLOBAL.vars.map.getLayersByName(names[i])[0].visibility) {
                    visibleOverlays = true;
                }
            }
            
            var widget = GLOBAL.vars.activePanel.isPolygon() ? choropleth : symbol;
            
            if (visibleOverlays) {
                widget.selectFeatures.deactivate();
            }
            else {
                widget.selectFeatures.activate();
            }
        }
    }
	
	GLOBAL.vars.map.events.on({
        changelayer: function(e) {
            if (e.layer.name != 'Polygon layer' && e.layer.name != 'Point layer') {
                if (e.property == 'visibility') {
                    if (!GLOBAL.stores.overlay.isLoaded) {
                        GLOBAL.stores.overlay.load({callback: function() {
                            toggleSelectFeatures(e);
                        }});
                    }
                    else {
                        toggleSelectFeatures(e);
                    }
                }
            }
        }
    });
            
    Ext.getCmp('mapdatetype_cb').setValue(GLOBAL.vars.mapDateType.value);
    
    choropleth.prepareMapViewValueType();
    symbol.prepareMapViewValueType();
    
    choropleth.prepareMapViewDateType();
    symbol.prepareMapViewDateType();
    
    choropleth.prepareMapViewLegend();
    symbol.prepareMapViewLegend();
    
	}});
});
