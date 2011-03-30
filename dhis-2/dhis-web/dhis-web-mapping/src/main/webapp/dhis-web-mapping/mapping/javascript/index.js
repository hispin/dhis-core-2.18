Ext.onReady( function() {
    Ext.BLANK_IMAGE_URL = '../resources/ext-ux/theme/gray-extend/gray-extend/s.gif';
	Ext.override(Ext.form.Field,{showField:function(){this.show();this.container.up('div.x-form-item').setDisplayed(true);},hideField:function(){this.hide();this.container.up('div.x-form-item').setDisplayed(false);}});
	Ext.QuickTips.init();
	document.body.oncontextmenu = function(){return false;};
    
	G.vars.map = new OpenLayers.Map({
        controls: [new OpenLayers.Control.MouseToolbar()],
        displayProjection: new OpenLayers.Projection("EPSG:4326")
    });
    
    G.vars.mask = new Ext.LoadMask(Ext.getBody(),{msg:G.i18n.loading,msgCls:'x-mask-loading2'});
    G.vars.parameter = G.util.getUrlParam('view') ? {id: G.util.getUrlParam('view')} : {id: null};
	
    Ext.Ajax.request({
        url: G.conf.path_mapping + 'initialize' + G.conf.type,
        method: 'POST',
        params: {id: G.vars.parameter.id || null},
        success: function(r) {
            var init = Ext.util.JSON.decode(r.responseText);
            G.vars.parameter.mapView = init.mapView;
            G.user.initOverlays = init.overlays;
            G.user.isAdmin = init.security.isAdmin;
            G.system.aggregationStrategy = init.systemSettings.aggregationStrategy;
            G.system.mapDateType.value = G.system.aggregationStrategy == G.conf.aggregation_strategy_batch ?
				G.conf.map_date_type_fixed : init.userSettings.mapDateType;

    /* Section: stores */
    var mapViewStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getAllMapViews' + G.conf.type,
        root: 'mapViews',
        fields: [ 'id', 'name', 'userId', 'featureType', 'mapValueType', 'indicatorGroupId', 'indicatorId', 'dataElementGroupId', 'dataElementId',
            'mapDateType', 'periodTypeId', 'periodId', 'startDate', 'endDate', 'parentOrganisationUnitId', 'parentOrganisationUnitName',
            'parentOrganisationUnitLevel', 'organisationUnitLevel', 'organisationUnitLevelName', 'mapLegendType', 'method', 'classes',
            'bounds', 'colorLow', 'colorHigh', 'mapLegendSetId', 'radiusLow', 'radiusHigh', 'longitude', 'latitude', 'zoom'
        ],
        autoLoad: false,
        isLoaded: false,
        sortInfo: {field: 'userId', direction: 'ASC'},
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
    var polygonMapViewStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getMapViewsByFeatureType' + G.conf.type,
        baseParams: {featureType: G.conf.map_feature_type_multipolygon},
        root: 'mapViews',
        fields: [ 'id', 'name', 'userId', 'featureType', 'mapValueType', 'indicatorGroupId', 'indicatorId', 'dataElementGroupId', 'dataElementId',
            'mapDateType', 'periodTypeId', 'periodId', 'startDate', 'endDate', 'parentOrganisationUnitId', 'parentOrganisationUnitName',
            'parentOrganisationUnitLevel', 'organisationUnitLevel', 'organisationUnitLevelName', 'mapLegendType', 'method', 'classes',
            'bounds', 'colorLow', 'colorHigh', 'mapLegendSetId', 'longitude', 'latitude', 'zoom'
        ],
        sortInfo: {field: 'userId', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
    var pointMapViewStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getMapViewsByFeatureType' + G.conf.type,
        baseParams: {featureType: G.conf.map_feature_type_point},
        root: 'mapViews',
        fields: [ 'id', 'name', 'userId', 'featureType', 'mapValueType', 'indicatorGroupId', 'indicatorId', 'dataElementGroupId', 'dataElementId',
            'mapDateType', 'periodTypeId', 'periodId', 'startDate', 'endDate', 'parentOrganisationUnitId', 'parentOrganisationUnitName',
            'parentOrganisationUnitLevel', 'organisationUnitLevel', 'organisationUnitLevelName', 'mapLegendType', 'method', 'classes',
            'bounds', 'colorLow', 'colorHigh', 'mapLegendSetId', 'radiusLow', 'radiusHigh', 'longitude', 'latitude', 'zoom'
        ],
        sortInfo: {field: 'userId', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });

    var indicatorGroupStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getAllIndicatorGroups' + G.conf.type,
        root: 'indicatorGroups',
        fields: ['id', 'name'],
        idProperty: 'id',
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
    var indicatorsByGroupStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getIndicatorsByIndicatorGroup' + G.conf.type,
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
        url: G.conf.path_mapping + 'getAllIndicators' + G.conf.type,
        root: 'indicators',
        fields: ['id', 'shortName'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
  
    var dataElementGroupStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getAllDataElementGroups' + G.conf.type,
        root: 'dataElementGroups',
        fields: ['id', 'name'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
    var dataElementsByGroupStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getDataElementsByDataElementGroup' + G.conf.type,
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
        url: G.conf.path_mapping + 'getAllDataElements' + G.conf.type,
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
        url: G.conf.path_mapping + 'getAllPeriodTypes' + G.conf.type,
        root: 'periodTypes',
        fields: ['name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
        
    var periodsByTypeStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getPeriodsByPeriodType' + G.conf.type,
        root: 'periods',
        fields: ['id', 'name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
	var predefinedMapLegendStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getAllMapLegends' + G.conf.type,
        root: 'mapLegends',
        fields: ['id', 'name', 'startValue', 'endValue', 'color', 'displayString'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });    
    
    var predefinedMapLegendSetStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getMapLegendSetsByType' + G.conf.type,
        baseParams: {type: G.conf.map_legend_type_predefined},
        root: 'mapLegendSets',
        fields: ['id', 'name', 'indicators', 'dataelements'],
        sortInfo: {field:'name', direction:'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
	var organisationUnitLevelStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getAllOrganisationUnitLevels' + G.conf.type,
        root: 'organisationUnitLevels',
        fields: ['id', 'level', 'name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
	var organisationUnitsAtLevelStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getOrganisationUnitsAtLevel' + G.conf.type,
        baseParams: {level: 1},
        root: 'organisationUnits',
        fields: ['id', 'name'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
	var geojsonFilesStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getGeoJsonFiles' + G.conf.type,
        root: 'files',
        fields: ['name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
	var wmsCapabilitiesStore = new GeoExt.data.WMSCapabilitiesStore({
        url: G.conf.path_geoserver + G.conf.ows,
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
    var overlayStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getMapLayersByType' + G.conf.type,
        baseParams: {type: G.conf.map_layer_type_overlay},
        root: 'mapLayers',
        fields: ['id', 'name', 'type', 'mapSource', 'layer', 'fillColor', 'fillOpacity', 'strokeColor', 'strokeWidth'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
    G.stores = {
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
        organisationUnitsAtLevel: organisationUnitsAtLevelStore,
        geojsonFiles: geojsonFilesStore,
        wmsCapabilities: wmsCapabilitiesStore,
        overlay: overlayStore
    };
	
	/* Thematic layers */
    polygonLayer = new OpenLayers.Layer.Vector('Polygon layer', {
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
    
    polygonLayer.layerType = G.conf.map_layer_type_thematic;
    G.vars.map.addLayer(polygonLayer);
    
    pointLayer = new OpenLayers.Layer.Vector('Point layer', {
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
    
    pointLayer.layerType = G.conf.map_layer_type_thematic;
    G.vars.map.addLayer(pointLayer);
    
    /* Init base layers */
	var gm_normal = new OpenLayers.Layer.Google("Google Normal", {
		type: G_NORMAL_MAP,
		sphericalMercator: true,
		maxExtent: new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
		numZoomLevels: 21
	});
    
	gm_normal.layerType = G.conf.map_layer_type_baselayer;
	G.vars.map.addLayer(gm_normal);
	
	var gm_hybrid = new OpenLayers.Layer.Google("Google Hybrid", {
		type: G_HYBRID_MAP,
		sphericalMercator: true,
		maxExtent: new OpenLayers.Bounds(-20037508.34,-20037508.34,20037508.34,20037508.34),
		numZoomLevels: 21
	});
    
	gm_hybrid.layerType = G.conf.map_layer_type_baselayer;
	G.vars.map.addLayer(gm_hybrid);
	
    var osm = new OpenLayers.Layer.OSM.Osmarender("OpenStreetMap");
    osm.layerType = G.conf.map_layer_type_baselayer;
    G.vars.map.addLayer(osm);
    
    /* Init overlays */
	function addOverlaysToMap(init) {
        function add(r) {
            if (r.length) {                
                for (var i = 0; i < r.length; i++) {
                    var overlay = G.util.createOverlay(
                        r[i].data.name, r[i].data.fillColor, 1, r[i].data.strokeColor, parseFloat(r[i].data.strokeWidth),
                        G.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + r[i].data.mapSource
                    );
                    
                    overlay.layerType = G.conf.map_layer_type_overlay;
                    
                    overlay.events.register('loadstart', null, G.func.loadStart);
                    overlay.events.register('loadend', null, G.func.loadEnd);
                    
                    G.vars.map.addLayer(overlay);
					G.vars.map.getLayersByName(r[i].data.name)[0].setZIndex(G.conf.defaultLayerZIndex);
                }
            }
        }
        
        if (init) {
            add(G.user.initOverlays);
        }
        else {
            G.stores.overlay.load({callback: function(r) {
                add(r);
            }});
        }
	}
	addOverlaysToMap(true);
			
	/* Section: mapview */
	var favoriteWindow = new Ext.Window({
        id: 'favorite_w',
        title: '<span id="window-favorites-title">' + G.i18n.favorite_map_views + '</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: G.conf.window_width,
        height: 205,
        items: [
            {
                xtype: 'form',
                bodyStyle: 'padding:8px',
                labelWidth: G.conf.label_width,
                items: [
                    {html: '<div class="window-info">Register current map as a favorite</div>'},
                    {
                        xtype: 'textfield',
                        id: 'favoritename_tf',
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.display_name,
                        width: G.conf.combo_width_fieldset,
                        autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '35'}
                    },
                    {
                        xtype: 'checkbox',
                        id: 'favoritesystem_chb',
                        disabled: !G.user.isAdmin,
                        fieldLabel: 'System',
                        labelSeparator: G.conf.labelseparator,
                        editable: false
                    },
                        
                    {html: '<div class="window-p"></div>'},
                    {html: '<div class="window-info">Delete favorite / Add to dashboard</div>'},
                    {
                        xtype: 'combo',
                        id: 'favorite_cb',
                        fieldLabel: G.i18n.favorite,
                        editable: false,
                        valueField: 'id',
                        displayField: 'name',
                        mode: 'remote',
                        forceSelection: true,
                        triggerAction: 'all',
                        emptyText: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        selectOnFocus: true,
                        width: G.conf.combo_width_fieldset,
                        minListWidth: G.conf.combo_width_fieldset,
                        store:G.stores.mapView
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
				text: G.i18n.register,
				handler: function() {
					var vn = Ext.getCmp('favoritename_tf').getValue();
                    var params;
                    
                    if (!vn) {
						Ext.message.msg(false, G.i18n.form_is_not_complete);
						return;
					}
                    
                    if (G.vars.activePanel.isPolygon()) {
                        if (!choropleth.formValidation.validateForm.apply(choropleth, [true])) {
                            return;
                        }
                        params = choropleth.formValues.getAllValues.call(choropleth);
                    }
                    else if (G.vars.activePanel.isPoint()) {
                        if (!symbol.formValidation.validateForm.apply(symbol, [true])) {
                            return;
                        }
                        params = symbol.formValues.getAllValues.call(symbol);
                    }
                    
                    params.name = vn;
                    params.system = Ext.getCmp('favoritesystem_chb').getValue();
                    
                    Ext.Ajax.request({
                        url: G.conf.path_mapping + 'addMapView' + G.conf.type,
                        method: 'POST',
                        params: params,
                        success: function(r) {
                            Ext.message.msg(true, G.i18n.favorite + ' <span class="x-msg-hl">' + vn + '</span> ' + G.i18n.registered);
                            G.stores.mapView.load();
                            if (params.featureType == G.conf.map_feature_type_multipolygon) {
								G.stores.polygonMapView.load();
							}
							else if (params.featureType == G.conf.map_feature_type_multipolygon) {
								G.stores.pointMapView.load();
							}
                            Ext.getCmp('favoritename_tf').reset();
                            Ext.getCmp('favoritesystem_chb').reset();
                        }
                    });
				}
			},
            {
                xtype: 'button',
                id: 'deleteview_b',
                iconCls: 'icon-remove',
				hideLabel: true,
				text: G.i18n.delete_,
				handler: function() {
					var v = Ext.getCmp('favorite_cb').getValue();
					var rw = Ext.getCmp('favorite_cb').getRawValue();
                    
                    if (v) {
                        var userId = G.stores.mapView.getAt(G.stores.mapView.findExact('id', v)).data.userId;
                        if (userId || G.user.isAdmin) {                            
                            Ext.Ajax.request({
                                url: G.conf.path_mapping + 'deleteMapView' + G.conf.type,
                                method: 'POST',
                                params: {id: v},
                                success: function(r) {
                                    Ext.message.msg(true, G.i18n.favorite + ' <span class="x-msg-hl">' + rw + '</span> ' + G.i18n.deleted);
                                    Ext.getCmp('favorite_cb').clearValue();
                                    
                                    var featureType = G.stores.mapView.getAt(G.stores.mapView.findExact('id', v)).data.featureType;
                                    if (featureType == G.conf.map_feature_type_multipolygon) {
                                        G.stores.polygonMapView.load();
                                    }
                                    else if (featureType == G.conf.map_feature_type_point) {
                                        G.stores.pointMapView.load();
                                    }
                                    
                                    G.stores.mapView.load();
                                    
                                    if (v == choropleth.form.findField('mapview').getValue()) {
                                        choropleth.form.findField('mapview').clearValue();
                                    }
                                    if (v == symbol.form.findField('mapview').getValue()) {
                                        symbol.form.findField('mapview').clearValue();
                                    }
                                }
                            });
                        }
                        else {
                            Ext.message.msg(false, 'Access denied');
                        }
                    }
                    else {
                        Ext.message.msg(false, G.i18n.please_select_a_map_view);
                        return;
                    }

				}
			},
            {
                xtype: 'button',
                id: 'dashboardview_b',
                iconCls: 'icon-assign',
				hideLabel: true,
				text: G.i18n.add,
				handler: function() {
					var v = Ext.getCmp('favorite_cb').getValue();
					var rv = Ext.getCmp('favorite_cb').getRawValue();
					
					if (!v) {
						Ext.message.msg(false, G.i18n.please_select_a_map_view);
						return;
					}
					
					Ext.Ajax.request({
						url: G.conf.path_mapping + 'addMapViewToDashboard' + G.conf.type,
						method: 'POST',
						params: {id: v},
						success: function(r) {
                            Ext.getCmp('favorite_cb').clearValue();
							Ext.message.msg(true, G.i18n.favorite + ' <span class="x-msg-hl">' + rv + '</span> ' + G.i18n.added_to_dashboard);
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
		width: G.conf.window_width,
        height: 194,
        items: [
            {
                xtype: 'form',
                bodyStyle: 'padding:8px',
                labelWidth: G.conf.label_width,
                items: [
                    {html: '<div class="window-info">Export thematic map to PNG</div>'},
                    {
                        xtype: 'textfield',
                        id: 'exportimagetitle_tf',
                        fieldLabel: G.i18n.title,
                        labelSeparator: G.conf.labelseparator,
                        editable: true,
                        valueField: 'id',
                        displayField: 'text',
                        width: G.conf.combo_width_fieldset,
                        mode: 'local',
                        triggerAction: 'all'
                    },
                    {
                        xtype: 'combo',
                        id: 'exportimagewidth_cb',
                        fieldLabel: 'Width',
                        labelSeparator: G.conf.labelseparator,
                        editable: false,
                        valueField: 'width',
                        displayField: 'text',
                        width: G.conf.combo_width_fieldset,
                        minListWidth: G.conf.combo_width_fieldset,
                        mode: 'local',
                        triggerAction: 'all',
                        value: 1190,
                        store: new Ext.data.ArrayStore({
                            fields: ['width', 'text'],
                            data: [[800, 'Small'], [1190, 'Medium'], [1920, 'Large']]
                        })
                    },
                    {
                        xtype: 'combo',
                        id: 'exportimageheight_cb',
                        fieldLabel: 'Height',
                        labelSeparator: G.conf.labelseparator,
                        editable: false,
                        valueField: 'height',
                        displayField: 'text',
                        width: G.conf.combo_width_fieldset,
                        minListWidth: G.conf.combo_width_fieldset,
                        mode: 'local',
                        triggerAction: 'all',
                        value: 880,
                        store: {
                            xtype: 'arraystore',
                            fields: ['height', 'text'],
                            data: [[600, 'Small'], [880, 'Medium'], [1200, 'Large']]
                        }
                    },
                    {
                        xtype: 'checkbox',
                        id: 'exportimageincludelegend_chb',
                        fieldLabel: G.i18n.legend,
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
				labelSeparator: G.conf.labelseparator,
                iconCls: 'icon-assign',
				text: G.i18n.export_,
				handler: function() {
                    var values, svg;
                    
                    if (polygonLayer.visibility && pointLayer.visibility) {
                        if (choropleth.formValidation.validateForm.call(choropleth)) {
                            if (symbol.formValidation.validateForm.call(symbol)) {
                                document.getElementById('layerField').value = 3;
                                document.getElementById('imageLegendRowsField').value = choropleth.imageLegend.length;
                                
                                values = choropleth.formValues.getImageExportValues.call(choropleth);
                                document.getElementById('periodField').value = values.dateValue;
                                document.getElementById('indicatorField').value = values.mapValueTypeValue;
                                document.getElementById('legendsField').value = G.util.getLegendsJSON.call(choropleth);
                                
                                values = symbol.formValues.getImageExportValues.call(symbol);
                                document.getElementById('periodField2').value = values.dateValue;
                                document.getElementById('indicatorField2').value = values.mapValueTypeValue;
                                document.getElementById('legendsField2').value = G.util.getLegendsJSON.call(symbol);
                                
                                var str1 = document.getElementById(polygonLayer.svgId).parentNode.innerHTML;
                                var str2 = document.getElementById(pointLayer.svgId).parentNode.innerHTML;
                                svg = G.util.mergeSvg(str1, [str2]);                                
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
                    else if (polygonLayer.visibility) {
                        if (choropleth.formValidation.validateForm.call(choropleth)) {
                            values = choropleth.formValues.getImageExportValues.call(choropleth);
                            document.getElementById('layerField').value = 1;
                            document.getElementById('periodField').value = values.dateValue;
                            document.getElementById('indicatorField').value = values.mapValueTypeValue;
                            document.getElementById('legendsField').value = G.util.getLegendsJSON.call(choropleth);
                            svg = document.getElementById(polygonLayer.svgId).parentNode.innerHTML;
                        }
                        else {
                            Ext.message.msg(false, 'Polygon layer not rendered');
                            return;
                        }
                    }
                    else if (pointLayer.visibility) {
                        if (symbol.formValidation.validateForm.call(symbol)) {
                            values = symbol.formValues.getImageExportValues.call(symbol);
                            document.getElementById('layerField').value = 2;
                            document.getElementById('periodField').value = values.dateValue;  
                            document.getElementById('indicatorField').value = values.mapValueTypeValue;
                            document.getElementById('legendsField').value = G.util.getLegendsJSON.call(symbol);
                            svg = document.getElementById(pointLayer.svgId).parentNode.innerHTML;
                        }
                        else {
                            Ext.message.msg(false, 'Point layer not rendered');
                            return;
                        }
                    }
                    else {                        
                        document.getElementById('layerField').value = 0;
                    }
                    
                    var overlays = G.util.getVisibleLayers(G.util.getLayersByType(G.conf.map_layer_type_overlay));
                    svg = G.util.mergeSvg(svg, G.util.getOverlaysSvg(overlays));
                    
                    if (!svg) {
                        Ext.message.msg(false, 'No layers to export');
                        return;
                    }                        
                    
                    var title = Ext.getCmp('exportimagetitle_tf').getValue();
                                        
                    if (!title) {
                        Ext.message.msg(false, G.i18n.form_is_not_complete);
                    }
                    else {
                        var exportForm = document.getElementById('exportForm');
                        exportForm.action = '../exportImage.action';
                        
                        document.getElementById('titleField').value = title;
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
        title: '<span id="window-predefinedlegendset-title">'+G.i18n.predefined_legend_sets+'</span>',
		layout: 'accordion',
        closeAction: 'hide',
		width: G.conf.window_width,
        items: [
            {
                id: 'newpredefinedmaplegend_p',
                title: G.i18n.legend,
                items: [
                    {
                        xtype: 'form',
                        bodyStyle: 'padding: 8px 8px 5px 8px',
                        labelWidth: G.conf.label_width,
                        items: [
                            {html: '<div class="window-info">Register new legend</div>'},
                            {
                                xtype: 'textfield',
                                id: 'predefinedmaplegendname_tf',
                                emptyText: G.conf.emptytext,
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.display_name,
                                width: G.conf.combo_width_fieldset
                            },
                            {
                                xtype: 'numberfield',
                                id: 'predefinedmaplegendstartvalue_nf',
                                emptyText: G.conf.emptytext,
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.start_value,
                                width: G.conf.combo_number_width_small
                            },
                            {
                                xtype: 'numberfield',
                                id: 'predefinedmaplegendendvalue_nf',
                                emptyText: G.conf.emptytext,
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.end_value,
                                width: G.conf.combo_number_width_small
                            },
                            {
                                xtype: 'colorfield',
                                id: 'predefinedmaplegendcolor_cp',
                                emptyText: G.conf.emptytext,
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.color,
                                allowBlank: false,
                                width: G.conf.combo_width_fieldset,
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
                                emptyText: G.conf.emptytext,
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.legend,
                                width: G.conf.combo_width_fieldset,
                                minListWidth: G.conf.combo_width_fieldset,
                                store: G.stores.predefinedMapLegend
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
                                        id: 'newpredefinedmaplegend_b',
                                        text: G.i18n.register,
                                        iconCls: 'icon-add',
                                        handler: function() {
                                            var mln = Ext.getCmp('predefinedmaplegendname_tf').getValue();
                                            var mlsv = parseFloat(Ext.getCmp('predefinedmaplegendstartvalue_nf').getValue());
                                            var mlev = parseFloat(Ext.getCmp('predefinedmaplegendendvalue_nf').getValue());
                                            var mlc = Ext.getCmp('predefinedmaplegendcolor_cp').getValue();
                                            
                                            if (!Ext.isNumber(parseFloat(mlsv)) || !Ext.isNumber(mlev)) {
                                                Ext.message.msg(false, G.i18n.form_is_not_complete);
                                                return;
                                            }
                                            
                                            if (!mln || !mlc) {
                                                Ext.message.msg(false, G.i18n.form_is_not_complete);
                                                return;
                                            }
                                            
                                            if (!G.util.validateInputNameLength(mln)) {
                                                Ext.message.msg(false, G.i18n.name + ': ' + G.i18n.max + ' 25 ' + G.i18n.characters);
                                                return;
                                            }
                                            
                                            if (G.stores.predefinedMapLegend.findExact('name', mln) !== -1) {
                                                Ext.message.msg(false, G.i18n.legend + ' <span class="x-msg-hl">' + mln + '</span> ' + G.i18n.already_exists);
                                                return;
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: G.conf.path_mapping + 'addOrUpdateMapLegend' + G.conf.type,
                                                method: 'POST',
                                                params: {name: mln, startValue: mlsv, endValue: mlev, color: mlc},
                                                success: function(r) {
                                                    Ext.message.msg(true, G.i18n.legend + ' <span class="x-msg-hl">' + mln + '</span> ' + G.i18n.was_registered);
                                                    G.stores.predefinedMapLegend.load();
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
                                        text: G.i18n.delete_,
                                        iconCls: 'icon-remove',
                                        handler: function() {
                                            var mlv = Ext.getCmp('predefinedmaplegend_cb').getValue();
                                            var mlrv = Ext.getCmp('predefinedmaplegend_cb').getRawValue();
                                            
                                            if (!mlv) {
                                                Ext.message.msg(false, G.i18n.please_select_a_legend);
                                                return;
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: G.conf.path_mapping + 'deleteMapLegend' + G.conf.type,
                                                method: 'POST',
                                                params: {id: mlv},
                                                success: function(r) {
                                                    Ext.message.msg(true, G.i18n.legend + ' <span class="x-msg-hl">' + mlrv + '</span> ' + G.i18n.was_deleted);
                                                    G.stores.predefinedMapLegend.load();
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
                        predefinedMapLegendSetWindow.setHeight(Ext.isChrome || (Ext.isWindows && Ext.isGecko) ? 348 : 346);
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(123);
                    }
                }
            },
            
            {
                title: G.i18n.legendset,
                items: [
                    {
                        xtype: 'form',
                        bodyStyle: 'padding: 8px 8px 5px 8px',
                        labelWidth: G.conf.label_width,
                        items: [
                            {html: '<div class="window-info">Register new legend set</div>'},
                            {
                                xtype: 'textfield',
                                id: 'predefinedmaplegendsetname_tf',
                                emptyText: G.conf.emptytext,
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.display_name,
                                width: G.conf.combo_width_fieldset
                            },
                            {html: '<div class="window-field-label">'+G.i18n.legends+'</div>'},
                            {
                                xtype: 'multiselect',
                                id: 'predefinednewmaplegend_ms',
                                hideLabel: true,
                                dataFields: ['id', 'name', 'startValue', 'endValue', 'color', 'displayString'],
                                valueField: 'id',
                                displayField: 'displayString',
                                width: G.conf.multiselect_width,
                                height: G.util.getMultiSelectHeight() / 2,
                                store: G.stores.predefinedMapLegend
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
                                emptyText: G.conf.emptytext,
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.legendset,
                                width: G.conf.combo_width_fieldset,
                                minListWidth: G.conf.combo_width_fieldset,
                                store:G.stores.predefinedMapLegendSet
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
                                        text: G.i18n.register,
                                        iconCls: 'icon-add',
                                        handler: function() {
                                            var mlsv = Ext.getCmp('predefinedmaplegendsetname_tf').getValue();
                                            var mlms = Ext.getCmp('predefinednewmaplegend_ms').getValue();
                                            var array = [];
                                            
                                            if (mlms) {
                                                array = mlms.split(',');
                                                if (array.length > 1) {
                                                    for (var i = 0; i < array.length; i++) {
                                                        var sv = G.stores.predefinedMapLegend.getById(array[i]).get('startValue');
                                                        var ev = G.stores.predefinedMapLegend.getById(array[i]).get('endValue');
                                                        for (var j = 0; j < array.length; j++) {
                                                            if (j != i) {
                                                                var temp_sv = G.stores.predefinedMapLegend.getById(array[j]).get('startValue');
                                                                var temp_ev = G.stores.predefinedMapLegend.getById(array[j]).get('endValue');
                                                                for (var k = sv+1; k < ev; k++) {
                                                                    if (k > temp_sv && k < temp_ev) {
                                                                        Ext.message.msg(false, G.i18n.overlapping_legends_are_not_allowed);
                                                                        return;
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                Ext.message.msg(false, G.i18n.form_is_not_complete);
                                                return;
                                            }
                                            
                                            if (!mlsv) {
                                                Ext.message.msg(false, G.i18n.form_is_not_complete);
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
                                                url: G.conf.path_mapping + 'addOrUpdateMapLegendSet.action' + params,
                                                method: 'POST',
                                                params: {name: mlsv, type: G.conf.map_legend_type_predefined},
                                                success: function(r) {
                                                    Ext.message.msg(true, G.i18n.new_legend_set+' <span class="x-msg-hl">' + mlsv + '</span> ' + G.i18n.was_registered);
                                                    G.stores.predefinedMapLegendSet.load();
                                                    Ext.getCmp('predefinedmaplegendsetname_tf').reset();
                                                    Ext.getCmp('predefinednewmaplegend_ms').reset();							
                                                }
                                            });
                                        }
                                    },
                                    {
                                        xtype: 'button',
                                        id: 'deletepredefinedmaplegendset_b',
                                        text: G.i18n.delete_,
                                        iconCls: 'icon-remove',
                                        handler: function() {
                                            var mlsv = Ext.getCmp('predefinedmaplegendsetindicator_cb').getValue();
                                            var mlsrv = Ext.getCmp('predefinedmaplegendsetindicator_cb').getRawValue();
                                            
                                            if (!mlsv) {
                                                Ext.message.msg(false, G.i18n.please_select_a_legend_set);
                                                return;
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: G.conf.path_mapping + 'deleteMapLegendSet' + G.conf.type,
                                                method: 'POST',
                                                params: {id: mlsv},
                                                success: function(r) {
                                                    Ext.message.msg(true, G.i18n.legendset + ' <span class="x-msg-hl">' + mlsrv + '</span> ' + G.i18n.was_deleted);
                                                    G.stores.predefinedMapLegendSet.load();
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
                        predefinedMapLegendSetWindow.setHeight((G.util.getMultiSelectHeight() / 2) + (Ext.isChrome || (Ext.isWindows && Ext.isGecko) ? 298:295));
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(123);
                    }
                }
            },
            
            {
                title: G.i18n.indicators,
                items: [
                    {
                        xtype: 'form',
                        bodyStyle: 'padding: 8px 8px 5px 8px',
                        labelWidth: G.conf.label_width,
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
                                emptyText: G.conf.emptytext,
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.legendset,
                                width: G.conf.combo_width_fieldset,
                                minListWidth: G.conf.combo_width_fieldset,
                                store: G.stores.predefinedMapLegendSet,
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
                            {html: '<div class="window-field-label">' + G.i18n.indicators + '</div>'},
                            {
                                xtype: 'multiselect',
                                id: 'predefinedmaplegendsetindicator_ms',
                                hideLabel:true,
                                dataFields: ['id', 'shortName'],
                                valueField: 'id',
                                displayField: 'shortName',
                                width:G.conf.multiselect_width,
                                height: G.util.getMultiSelectHeight(),
                                store:G.stores.indicator
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
                                        text: G.i18n.assign,
                                        iconCls: 'icon-assign',
                                        handler: function() {
                                            var ls = Ext.getCmp('predefinedmaplegendsetindicator2_cb').getValue();
                                            var lsrw = Ext.getCmp('predefinedmaplegendsetindicator2_cb').getRawValue();
                                            var lims = Ext.getCmp('predefinedmaplegendsetindicator_ms').getValue();
                                            
                                            if (!ls) {
                                                Ext.message.msg(false, G.i18n.please_select_a_legend_set);
                                                return;
                                            }
                                            
                                            if (!lims) {
                                                Ext.message.msg(false, G.i18n.please_select_at_least_one_indicator);
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
                                                url: G.conf.path_mapping + 'assignIndicatorsToMapLegendSet.action' + params,
                                                method: 'POST',
                                                params: {id: ls},
                                                success: function(r) {
                                                    Ext.message.msg(true, G.i18n.legendset+' <span class="x-msg-hl">' + lsrw + '</span> ' + G.i18n.was_updated);
                                                    G.stores.predefinedMapLegendSet.load();
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
                        predefinedMapLegendSetWindow.setHeight(G.util.getMultiSelectHeight() + (Ext.isChrome || (Ext.isWindows && Ext.isGecko) ? 242 : 240));
                        
                        if (!G.stores.indicator.isLoaded) {
                            G.stores.indicator.load();
                        }
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(123);
                    }
                }
            },
            
            {
                title: G.i18n.dataelements,
                items: [
                    {
                        xtype: 'form',
                        bodyStyle: 'padding: 8px 8px 5px 8px',
                        labelWidth: G.conf.label_width,
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
                                emptyText: G.conf.emptytext,
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.legendset,
                                width: G.conf.combo_width_fieldset,
                                minListWidth: G.conf.combo_width_fieldset,
                                store: G.stores.predefinedMapLegendSet,
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
                            {html: '<div class="window-field-label">' + G.i18n.dataelements + '</div>'},
                            {
                                xtype: 'multiselect',
                                id: 'predefinedmaplegendsetdataelement_ms',
                                hideLabel: true,
                                dataFields: ['id', 'shortName'],
                                valueField: 'id',
                                displayField: 'shortName',
                                width: G.conf.multiselect_width,
                                height: G.util.getMultiSelectHeight(),
                                store: G.stores.dataElement
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
                                        text: G.i18n.assign,
                                        iconCls: 'icon-assign',
                                        handler: function() {
                                            var ls = Ext.getCmp('predefinedmaplegendsetdataelement_cb').getValue();
                                            var lsrw = Ext.getCmp('predefinedmaplegendsetdataelement_cb').getRawValue();
                                            var lims = Ext.getCmp('predefinedmaplegendsetdataelement_ms').getValue();
                                            
                                            if (!ls) {
                                                Ext.message.msg(false, G.i18n.please_select_a_legend_set);
                                                return;
                                            }
                                            
                                            if (!lims) {
                                                Ext.message.msg(false, G.i18n.please_select_at_least_one_indicator);
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
                                                url: G.conf.path_mapping + 'assignDataElementsToMapLegendSet.action' + params,
                                                method: 'POST',
                                                params: {id: ls},
                                                success: function(r) {
                                                    Ext.message.msg(true, G.i18n.legendset+' <span class="x-msg-hl">' + lsrw + '</span> ' + G.i18n.was_updated);
                                                    G.stores.predefinedMapLegendSet.load();
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
                        predefinedMapLegendSetWindow.setHeight(G.util.getMultiSelectHeight() + (Ext.isChrome || (Ext.isWindows && Ext.isGecko) ? 240 : 238));
                        
                        if (!G.stores.dataElement.isLoaded) {
                            G.stores.dataElement.load();
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
                predefinedMapLegendSetWindow.setHeight(Ext.isChrome || (Ext.isWindows && Ext.isGecko) ? 348 : 346);
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
        title: '<span id="window-help-title">'+G.i18n.help+'</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: 579,
		height: 290,
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
							setHelpText(G.conf.thematicMap, tab);
                            helpWindow.setHeight(290);
                        }
                        else if (tab.id == 'help1') {
							setHelpText(G.conf.favorites, tab);
                            helpWindow.setHeight(290);
                        }
                        else if (tab.id == 'help2') {
                            setHelpText(G.conf.legendSets, tab);
                            helpWindow.setHeight(290);
                        }
						if (tab.id == 'help3') { 
                            setHelpText(G.conf.imageExport, tab);
                            helpWindow.setHeight(290);
                        }
                        else if (tab.id == 'help4') {
                            setHelpText(G.conf.administration, tab);
                            helpWindow.setHeight(290);
                        }
                        else if (tab.id == 'help5') {
                            setHelpText(G.conf.overlayRegistration, tab);
                            helpWindow.setHeight(530);
                        }
                        else if (tab.id == 'help6') {
                            setHelpText(G.conf.setup, tab);
                            helpWindow.setHeight(530);
                        }
                    }
                },
                items: [
                    {
                        id: 'help0',
                        title: '<span class="panel-tab-title">' + G.i18n.thematic_map + '</span>'
                    },
                    {
                        id: 'help1',
                        title: '<span class="panel-tab-title">' + G.i18n.favorites + '</span>'
                    },
                    {
                        id: 'help2',
                        title: '<span class="panel-tab-title">' + G.i18n.legendset + '</span>'
                    },
                    {
                        id: 'help3',
                        title: '<span class="panel-tab-title">' + G.i18n.image_export + '</span>'
                    },
                    {
                        id: 'help4',
                        title: '<span class="panel-tab-title">' + G.i18n.administrator + '</span>'
                    },
                    {
                        id: 'help5',
                        title: '<span class="panel-tab-title">' + G.i18n.overlays + '</span>'
                    },
                    {
                        id: 'help6',
                        title: '<span class="panel-tab-title">' + G.i18n.setup + '</span>'
                    }
                ]
            }
        ]
    });

    /* Section: overlays */
	var overlaysWindow = new Ext.Window({
        id: 'overlays_w',
        title: '<span id="window-maplayer-title">' + G.i18n.overlays + '</span>',
		layout: 'fit',
        closeAction: 'hide',
        height: 307,
		width: G.conf.window_width,
        items: [
            {
                xtype: 'form',
                bodyStyle: 'padding:8px',
                labelWidth: G.conf.label_width,
                items: [
                    {html: '<div class="window-info">Register new overlay</div>'},
                    {
                        xtype: 'textfield',
                        id: 'maplayername_tf',
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.display_name,
                        width: G.conf.combo_width_fieldset,
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
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.geojson_file,
                        width: G.conf.combo_width_fieldset,
                        store:G.stores.geojsonFiles
                    },
                    {
                        xtype: 'colorfield',
                        id: 'maplayerfillcolor_cf',
                        emptyText: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.fill_color,
                        allowBlank: false,
                        width: G.conf.combo_width_fieldset,
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
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.fill_opacity,
                        width: G.conf.combo_number_width,
                        minListWidth: G.conf.combo_number_width,
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
                        emptyText: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.stroke_color,
                        allowBlank: false,
                        width: G.conf.combo_width_fieldset,
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
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.stroke_width,
                        width: G.conf.combo_number_width,
                        minListWidth: G.conf.combo_number_width,
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
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.overlays,
                        width: G.conf.combo_width_fieldset,                
                        store:G.stores.overlay
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
                        Ext.message.msg(false, G.i18n.form_is_not_complete);
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: G.conf.path_mapping + 'addOrUpdateMapLayer' + G.conf.type,
                        method: 'POST',
                        params: {name: mln, type: 'overlay', mapSource: mlmsf, fillColor: mlfc, fillOpacity: 1, strokeColor: mlsc, strokeWidth: mlsw},
                        success: function(r) {
                            Ext.message.msg(true, 'Overlay <span class="x-msg-hl">' + mln + '</span> ' + G.i18n.registered);
                            G.stores.overlay.load();
                            
                            if (G.vars.map.getLayersByName(mln).length) {
                                G.vars.map.getLayersByName(mln)[0].destroy();
                            }
                            
                            var overlay = G.util.createOverlay(mln, mlfc, 1, mlsc, mlsw,
                                G.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + mlmsf);
                                
                            overlay.events.register('loadstart', null, G.func.loadStart);
                            overlay.events.register('loadend', null, G.func.loadEnd);
                            overlay.setOpacity(mlfo);
                            overlay.layerType = G.conf.map_layer_type_overlay;
                            
                            G.vars.map.addLayer(overlay);
                            G.vars.map.getLayersByName(mln)[0].setZIndex(G.conf.defaultLayerZIndex);
                            
                            Ext.getCmp('maplayername_tf').reset();
                            Ext.getCmp('maplayermapsourcefile_cb').clearValue();
                        }
                    });
                }
            },
            {
                xtype: 'button',
                id: 'deletemaplayer_b',
                text: G.i18n.delete_,
                iconCls: 'icon-remove',
                handler: function() {
                    var ml = Ext.getCmp('maplayer_cb').getValue();
                    var mln = Ext.getCmp('maplayer_cb').getRawValue();
                    
                    if (!ml) {
                        Ext.message.msg(false, G.i18n.please_select_an_overlay);
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: G.conf.path_mapping + 'deleteMapLayer' + G.conf.type,
                        method: 'POST',
                        params: {id: ml},
                        success: function(r) {
                            Ext.message.msg(true, 'Overlay <span class="x-msg-hl">' + mln + '</span> '+ G.i18n.deleted);
                            G.stores.overlay.load();
                            Ext.getCmp('maplayer_cb').clearValue();
                        }
                    });
                    
                    G.vars.map.getLayersByName(mln)[0].destroy();
                    
                    G.util.setZIndexByLayerType(G.conf.map_layer_type_overlay, G.conf.defaultLayerZIndex);
                }
            }
        ]
    });
            
    /* Section: base layers */
    var baselayersWindow = new Ext.Window({
        id: 'baselayers_w',
        title: '<span id="window-maplayer-title">' + G.i18n.baselayers + '</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: G.conf.window_width,
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
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.display_name,
                        width: G.conf.combo_width_fieldset,
                        autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '35'}
                    },
                    {
                        xtype: 'textfield',
                        id: 'maplayerbaselayersurl_tf',
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.url,
                        width: G.conf.combo_width_fieldset,
                    },
                    {
                        xtype: 'textfield',
                        id: 'maplayerbaselayerslayer_tf',
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.layer,
                        width: G.conf.combo_width_fieldset,
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
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.baselayer,
                        width: G.conf.combo_width_fieldset,                
                        store: G.stores.baseLayer
                    }
                ]
            }
        ],
        bbar: [
            '->',
            {
				xtype: 'button',
				id: 'newmaplayerbaselayers_b',
				text: G.i18n.register,
				iconCls: 'icon-add',
				handler: function() {
					var mlbn = Ext.getCmp('maplayerbaselayersname_tf').getValue();
					var mlbu = Ext.getCmp('maplayerbaselayersurl_tf').getValue();
					var mlbl = Ext.getCmp('maplayerbaselayerslayer_tf').getValue();
					
					if (!mlbn || !mlbu || !mlbl) {
						Ext.message.msg(false, G.i18n.form_is_not_complete);
						return;
					}
					
                    if (G.stores.baseLayer.find('name', mlbn) !== -1) {
                        Ext.message.msg(false, G.i18n.name + ' <span class="x-msg-hl">' + mlbn + '</span> ' + G.i18n.is_already_in_use);
                        return;
                    }
					
                    Ext.Ajax.request({
                        url: G.conf.path_mapping + 'addOrUpdateMapLayer' + G.conf.type,
                        method: 'POST',
                        params: {name: mlbn, type: G.conf.map_layer_type_baselayer, mapSource: mlbu, layer: mlbl, fillColor: '', fillOpacity: 0, strokeColor: '', strokeWidth: 0},
                        success: function(r) {
                            Ext.message.msg(true, G.i18n.baselayer + '<span class="x-msg-hl"> ' + mlbn + '</span> ' + G.i18n.registered);                            
                            G.vars.map.addLayers([
                                new OpenLayers.Layer.WMS(mlbn, mlbu, {layers: mlbl})
                            ]);

                            G.stores.baseLayer.load();
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
                text: G.i18n.delete_,
                iconCls: 'icon-remove',
                handler: function() {
                    var ml = Ext.getCmp('maplayerbaselayers_cb').getValue();
                    var mln = Ext.getCmp('maplayerbaselayers_cb').getRawValue();
                    
                    if (!ml) {
                        Ext.message.msg(false, G.i18n.please_select_a_baselayer);
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: G.conf.path_mapping + 'deleteMapLayer' + G.conf.type,
                        method: 'POST',
                        params: {id: ml},
                        success: function(r) {
                            Ext.message.msg(true, G.i18n.baselayer + ' <span class="x-msg-hl">' + mln + '</span> '+G.i18n.deleted);
                            G.stores.baseLayer.load({callback: function() {
                                Ext.getCmp('maplayerbaselayers_cb').clearValue();
                                var names = G.stores.baseLayer.collect('name');
                                
                                for (var i = 0; i < names.length; i++) {
                                    G.vars.map.getLayersByName(names[i])[0].setVisibility(false);
                                }
                                
                                G.vars.map.getLayersByName(mln)[0].destroy(false);
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
        width: G.conf.window_width,
        height: 145,
        minHeight: 77,
        items: [
            {
                title: 'Date',
                items: [
                    {
                        xtype: 'form',
                        bodyStyle: 'padding:8px',
                        labelWidth: G.conf.label_width,
                        items: [
                            {html: '<div class="window-info">Set thematic map date type</div>'},
                            {
                                xtype: 'combo',
                                id: 'mapdatetype_cb',
                                fieldLabel: G.i18n.date_type,
                                labelSeparator: G.conf.labelseparator,
                                disabled: G.system.aggregationStrategy === G.conf.aggregation_strategy_batch,
                                disabledClass: 'combo-disabled',
                                editable: false,
                                valueField: 'value',
                                displayField: 'text',
                                mode: 'local',
                                value: G.conf.map_date_type_fixed,
                                triggerAction: 'all',
                                width: G.conf.combo_width_fieldset,
                                minListWidth: G.conf.combo_width_fieldset,
                                store: {
                                    xtype: 'arraystore',
                                    fields: ['value', 'text'],
                                    data: [
                                        [G.conf.map_date_type_fixed, G.i18n.fixed_periods],
                                        [G.conf.map_date_type_start_end, G.i18n.start_end_dates]
                                    ]
                                },
                                listeners: {
                                    'select': function(cb) {
                                        if (cb.getValue() !== G.system.mapDateType.value) {
                                            G.system.mapDateType.value = cb.getValue();
                                            Ext.Ajax.request({
                                                url: G.conf.path_mapping + 'setMapUserSettings' + G.conf.type,
                                                method: 'POST',
                                                params: {mapDateType: G.system.mapDateType.value},
                                                success: function() {
                                                    Ext.message.msg(true, '<span class="x-msg-hl">' + cb.getRawValue() + '</span> '+ G.i18n.saved_as_date_type);
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
                ],
                listeners: {
                    expand: function() {
                        adminWindow.setHeight(Ext.isChrome || (Ext.isWindows && Ext.isGecko) ? 145 : 143);
                    },
                    collapse: function() {
                        adminWindow.setHeight(77);
                    }
                }
            },
            {
                title: 'Google Maps',
                items: [
                    {   
                        xtype: 'form',
                        bodyStyle: 'padding:8px',
                        labelWidth: G.conf.label_width,
                        items: [
                            {html: '<div class="window-info">Update Google Maps API key</div>'},
                            {
                                xtype: 'textfield',
                                id: 'googlemapsapikey_tf',
                                fieldLabel: G.i18n.api_key,
                                labelSeparator: G.conf.labelseparator,
                                width: G.conf.combo_width_fieldset,
                                minListWidth: G.conf.combo_width_fieldset
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
                                        text: G.i18n.update,
                                        iconCls: 'icon-assign',
                                        handler: function() {
                                            if (!Ext.getCmp('googlemapsapikey_tf').getValue()) {
                                                Ext.message.msg(false, G.i18n.form_is_not_complete);
                                                return;
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: G.conf.path_mapping + 'setMapSystemSettings' + G.conf.type,
                                                method: 'POST',
                                                params: {googleKey: Ext.getCmp('googlemapsapikey_tf').getValue()},
                                                success: function(r) {
                                                    window.location.reload();
                                                }
                                            });                                                
                                        }
                                    },
                                    {
                                        xtype: 'button',
                                        text: 'Use localhost',
                                        iconCls: 'icon-remove',
                                        handler: function() {
                                            Ext.Ajax.request({
                                                url: G.conf.path_mapping + 'deleteMapSystemSettings' + G.conf.type,
                                                method: 'POST',
                                                params: {googleKey: true},
                                                success: function() {
                                                    window.location.reload();
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
                        adminWindow.setHeight(Ext.isChrome || (Ext.isWindows && Ext.isGecko) ? 169 : 166);
                    },
                    collapse: function() {
                        adminWindow.setHeight(77);
                    }
                }
            }
        ],
        listeners: {
            afterrender: function() {
                adminWindow.setHeight(Ext.isChrome || (Ext.isWindows && Ext.isGecko) ? 145 : 143);
            }
        }
    });

    var layerTree = new Ext.tree.TreePanel({
        id: 'layertree_tp',
        title: '<span class="panel-title">' + G.i18n.map_layers + '</span>',
        bodyStyle: 'padding-bottom:5px',
        enableDD: false,
        rootVisible: false,
        collapsible: true,
        root: {
            nodeType: 'async',
            children: [
                {
                    nodeType: 'gx_baselayercontainer',
                    expanded: true,
                    text: 'Base layers'
                },
                {
                    nodeType: 'gx_overlaylayercontainer'
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
                    text: 'Opacity',
                    iconCls: 'menu-layeroptions-opacity',
                    menu: { 
                        items: G.conf.opacityItems,
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
                        items: G.conf.opacityItems,
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
                var layer = G.vars.map.getLayersByName(cm.contextNode.attributes.layer)[0];

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
                        width: G.conf.window_width,
                        height: G.util.getMultiSelectHeight() + 140,
                        items: [
                            {
                                xtype: 'form',
                                bodyStyle:'padding:8px',
                                labelWidth: G.conf.label_width,
                                items: [
                                    {html: '<div class="window-info">Locate an organisation unit in the map</div>'},
                                    {
                                        xtype: 'colorfield',
                                        id: 'highlightcolor_cf',
                                        emptyText: G.conf.emptytext,
                                        labelSeparator: G.conf.labelseparator,
                                        fieldLabel: G.i18n.highlight_color,
                                        allowBlank: false,
                                        width: G.conf.combo_width_fieldset,
                                        value: "#0000FF"
                                    },
                                    {
                                        xtype: 'textfield',
                                        id: 'locatefeature_tf',
                                        emptyText: G.conf.emptytext,
                                        labelSeparator: G.conf.labelseparator,
                                        fieldLabel: G.i18n.feature_filter,
                                        width: G.conf.combo_width_fieldset,
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
                                        height: G.util.getMultiSelectHeight(),
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
                                                    
                                                    if (feature.geometry.CLASS_NAME == G.conf.map_feature_type_multipolygon_class_name ||
                                                        feature.geometry.CLASS_NAME == G.conf.map_feature_type_polygon_class_name) {
                                                        symbolizer = new OpenLayers.Symbolizer.Polygon({
                                                            'strokeColor': color,
                                                            'fillColor': color
                                                        });
                                                    }
                                                    else if (feature.geometry.CLASS_NAME == G.conf.map_feature_type_point_class_name) {
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
                    locateFeatureWindow.setPagePosition(Ext.getCmp('east').x - (G.conf.window_width + 15 + 5), Ext.getCmp('center').y + 41);
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
                        var layer = G.vars.map.getLayersByName(item.parentMenu.contextNode.attributes.layer)[0];
                        
                        if (layer.features.length) {
                            if (layer.name == 'Polygon layer') {
                                G.util.labels.toggleFeatureLabels(layer.widget);
                            }
                            else if (layer.name == 'Point layer') {
                                G.util.labels.toggleFeatureLabels(layer.widget);
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
                        items: G.conf.opacityItems,
                        listeners: {
                            'itemclick': function(item) {
                                item.parentMenu.parentMenu.contextNode.layer.setOpacity(item.text);
                            }
                        }
                    }
                }
            ]
        }),
        clickEventFn: function(node, e) {
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
        },
		listeners: {
            'contextmenu': function(node, e) {
                node.getOwnerTree().clickEventFn(node, e);
            },
            'click': function(node, e) {
                node.getOwnerTree().clickEventFn(node, e);
            }
		},
        bbar: [
            {
                xtype: 'button',
                id: 'overlays_b',
                text: 'Overlays',
                iconCls: 'icon-overlay',
                handler: function() {
                    Ext.getCmp('overlays_w').setPagePosition(Ext.getCmp('east').x - (G.conf.window_width + 15 + 5), Ext.getCmp('center').y + 41);
                    Ext.getCmp('overlays_w').show();
                }
            }
        ]
	});
	
    /* Section: widgets */
    choropleth = new mapfish.widgets.geostat.Choropleth({
        id: 'choropleth',
		title: '<span class="panel-title">' + G.i18n.polygon_layer + '</span>',
        map: G.vars.map,
        layer: polygonLayer,
        featureSelection: false,
        legendDiv: 'polygonlegend',
        defaults: {width: 130},
        tools: [
            {
                id: 'refresh',
                qtip: 'Refresh layer',
                handler: function() {
                    choropleth.updateValues = true;
                    choropleth.classify();
                }
            },
            {
                id: 'close',
                qtip: 'Clear layer',
                handler: function() {
                    choropleth.formValues.clearForm.call(choropleth);
                }
            }
        ],
        listeners: {
            'expand': function() {
                G.vars.activePanel.setPolygon();
            },
            'afterrender': function() {
                this.layer.widget = this;
            }
        }
    });

    symbol = new mapfish.widgets.geostat.Symbol({
        id: 'symbol',
        map: G.vars.map,
        layer: pointLayer,
		title: '<span class="panel-title">' + G.i18n.point_layer + '</span>',
        featureSelection: false,
        legendDiv: 'pointlegend',
        defaults: {width: 130},
        tools: [
            {
                id: 'refresh',
                qtip: 'Refresh layer',
                handler: function() {
                    symbol.updateValues = true;
                    symbol.classify();
                }
            },
            {
                id: 'close',
                qtip: 'Clear layer',
                handler: function() {
                    symbol.formValues.clearForm.call(symbol);
                }
            }
        ],
        listeners: {
            'expand': function() {
                G.vars.activePanel.setPoint();
            },
            'afterrender': function() {
                this.layer.widget = this;
            }
        }
    });
    
	/* Section: map toolbar */
	var mapLabel = new Ext.form.Label({
		text: G.i18n.map,
		style: 'font:bold 11px arial; color:#333;'
	});
	
	var zoomInButton = new Ext.Button({
		iconCls: 'icon-zoomin',
		tooltip: G.i18n.zoom_in,
        style: 'margin-top:1px',
		handler: function() {
			G.vars.map.zoomIn();
		}
	});
	
	var zoomOutButton = new Ext.Button({
		iconCls: 'icon-zoomout',
		tooltip: G.i18n.zoom_out,
        style: 'margin-top:1px',
		handler:function() {
			G.vars.map.zoomOut();
		}
	});
	
	var zoomToVisibleExtentButton = new Ext.Button({
		iconCls: 'icon-zoommin',
		tooltip: G.i18n.zoom_to_visible_extent,
        style: 'margin-top:1px',
		handler: function() {
            if (G.vars.activePanel.isPolygon()) {
                if (choropleth.layer.getDataExtent()) {
                    G.vars.map.zoomToExtent(choropleth.layer.getDataExtent());
                }
            }
            else if (G.vars.activePanel.isPoint()) {
                if (symbol.layer.getDataExtent()) {
                    G.vars.map.zoomToExtent(symbol.layer.getDataExtent());
                }
            }
        }
	});
    
    var viewHistoryButton = new Ext.Button({
        id: 'viewhistory_b',
		iconCls: 'icon-history',
		tooltip: G.i18n.history,
        style: 'margin-top:1px',
        addMenu: function() {
            this.menu = new Ext.menu.Menu({
                id: 'viewhistory_m',
                defaults: {
                    itemCls: 'x-menu-item x-menu-item-custom'
                },
                items: [],
                listeners: {
                    'add': function(menu) {
                        var items = menu.items.items;
                        var keys = menu.items.keys;
                        items.unshift(items.pop());
                        keys.unshift(keys.pop());
						
						if (items.length > 10) {
							items[items.length-1].destroy();
						}
                    },
                    'click': function(menu, item, e) {
                        var mapView = item.mapView;
                        var scope = mapView.widget;                                            
                        scope.mapView = mapView;
                        scope.updateValues = true;
                        
                        scope.legend.value = mapView.mapLegendType;
                        scope.legend.method = mapView.method || scope.legend.method;
                        scope.legend.classes = mapView.classes || scope.legend.classes;
                        
                        G.vars.map.setCenter(new OpenLayers.LonLat(mapView.longitude, mapView.latitude), mapView.zoom);
                        G.system.mapDateType.value = mapView.mapDateType;
                        Ext.getCmp('mapdatetype_cb').setValue(G.system.mapDateType.value);

                        scope.valueType.value = mapView.mapValueType;
                        scope.form.findField('mapvaluetype').setValue(scope.valueType.value);
                        
                        G.util.expandWidget(scope);                        
                        scope.setMapView();
                    }
                }
            });
        },
        addItem: function(scope) {
            if (!this.menu) {
                this.addMenu();
            }

            var mapView = scope.formValues.getAllValues.call(scope);
            mapView.widget = scope;
            mapView.timestamp = G.date.getNowHMS();
            var c1 = '<span class="menu-item-inline-c1">';
            var c2 = '<span class="menu-item-inline-c2">';
            var spanEnd = '</span>';
            mapView.label = '<span class="menu-item-inline-bg">' +
                            c1 + mapView.timestamp + spanEnd +
                            c2 + mapView.parentOrganisationUnitName + spanEnd +
                            c1 + '( ' + mapView.organisationUnitLevelName + ' )' + spanEnd + 
                            c2 + (mapView.mapValueType == G.conf.map_value_type_indicator ? mapView.indicatorName : mapView.dataElementName) + spanEnd +
                            c1 + (mapView.mapDateType == G.conf.map_date_type_fixed ? mapView.periodName : (mapView.startDate + ' - ' + mapView.endDate)) + spanEnd +
                            spanEnd;
            
            for (var i = 0; i < this.menu.items.items.length; i++) {
                if (G.util.compareObjToObj(mapView, this.menu.items.items[i].mapView, ['longitude','latitude','zoom','widget','timestamp','label'])) {
                    this.menu.items.items[i].destroy();
                }
            }
            
            this.menu.addMenuItem({
                html: mapView.label,
                mapView: mapView
            });
        }
    });
	
	var favoritesButton = new Ext.Button({
		iconCls: 'icon-favorite',
		tooltip: G.i18n.favorite_map_views,
        style: 'margin-top:1px',
		handler: function() {
            if (!favoriteWindow.hidden) {
				favoriteWindow.hide();
			}
			else {
                var x = Ext.getCmp('center').x + G.conf.window_position_x;
                var y = Ext.getCmp('center').y + G.conf.window_position_y;    
                favoriteWindow.setPosition(x,y);
				favoriteWindow.show();
			}
		}
	});
	
	var exportImageButton = new Ext.Button({
		iconCls: 'icon-image',
		tooltip: G.i18n.export_map_as_image,
        style: 'margin-top:1px',
		handler: function() {
			if (Ext.isIE) {
				Ext.message.msg(false, 'SVG not supported by browser');
				return;
			}
            
            if (!exportImageWindow.hidden) {
				exportImageWindow.hide();
			}
			else {
                var x = Ext.getCmp('center').x + G.conf.window_position_x;
                var y = Ext.getCmp('center').y + G.conf.window_position_y;			
                exportImageWindow.setPosition(x,y);
				exportImageWindow.show();
			}
		}
	});
	
	var predefinedMapLegendSetButton = new Ext.Button({
		iconCls: 'icon-predefinedlegendset',
		tooltip: G.i18n.predefined_legend_sets,
		disabled: !G.user.isAdmin,
        style: 'margin-top:1px',
		handler: function() {		
			if (!predefinedMapLegendSetWindow.hidden) {
				predefinedMapLegendSetWindow.hide();
			}
			else {
                var x = Ext.getCmp('center').x + G.conf.window_position_x;
                var y = Ext.getCmp('center').y + G.conf.window_position_y;
                predefinedMapLegendSetWindow.setPosition(x,y);
				predefinedMapLegendSetWindow.show();
                if (!G.stores.predefinedMapLegend.isLoaded) {
                    G.stores.predefinedMapLegend.load();
                }
			}
		}
	});
	
	var adminButton = new Ext.Button({
		iconCls: 'icon-admin',
		tooltip: 'Administrator settings',
		disabled: !G.user.isAdmin,
        style: 'margin-top:1px',
		handler: function() {
            if (!adminWindow.hidden) {
                adminWindow.hide();
            }
            else {
                var x = Ext.getCmp('center').x + G.conf.window_position_x;
                var y = Ext.getCmp('center').y + G.conf.window_position_y;
                adminWindow.setPosition(x,y);
                adminWindow.show();
            }
		}
	});
	
	var helpButton = new Ext.Button({
		iconCls: 'icon-help',
		tooltip: G.i18n.help,
        style: 'margin-top:1px',
		handler: function() {
            if (!helpWindow.hidden) {
                helpWindow.hide();
            }
            else {
                var c = Ext.getCmp('center').x;
                var e = Ext.getCmp('east').x;
                helpWindow.setPagePosition(c+((e-c)/2)-280, Ext.getCmp('east').y + 100);
                helpWindow.show();
            }
		}
	});

	var exitButton = new Ext.Button({
		text: G.i18n.exit_gis,
        iconCls: 'icon-exit',
		tooltip: G.i18n.return_to_DHIS_2_dashboard,
        style: 'margin-top:1px',
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
			zoomOutButton,
			zoomToVisibleExtentButton,
			viewHistoryButton,
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
    var viewport = new Ext.Viewport({
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
                margins: '0 5px 0 5px',
                defaults: {
                    border: true,
                    frame: true,
                    collapsible: true
                },
                layout: 'anchor',
                items:
                [
                    layerTree,
                    {
                        title: '<span class="panel-title">' + G.i18n.overview_map + '</span>',
                        contentEl: 'overviewmap'
                    },
                    {
                        title: '<span class="panel-title">'+ G.i18n.cursor_position +'</span>',
                        contentEl: 'mouseposition'
                    },
					{
						title: '<span class="panel-title">' + G.i18n.feature_data + '</span>',
                        contentEl: 'featuredatatext'
					},
                    {
                        title: '<span class="panel-title">' + G.i18n.map_legend_polygon + '</span>',
                        contentEl: 'polygonlegend'
                    },
                    {
                        title: '<span class="panel-title">' + G.i18n.map_legend_point + '</span>',
                        contentEl: 'pointlegend'
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
                width: G.conf.west_width,
                minSize: 175,
                maxSize: 500,
                margins: '0 0 0 5px',
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
                map: G.vars.map,
                zoom: 3,
				tbar: mapToolbar
            }
        ],
        listeners: {
            'afterrender': function() {
                G.util.setOpacityByLayerType(G.conf.map_layer_type_overlay, G.conf.defaultLayerOpacity);
                G.util.setOpacityByLayerType(G.conf.map_layer_type_thematic, G.conf.defaultLayerOpacity);
                
                var svg = document.getElementsByTagName('svg');
                
                if (!Ext.isIE) {
                    polygonLayer.svgId = svg[0].id;
                    pointLayer.svgId = svg[1].id;
                }
                
                for (var i = 0, j = 2; i < G.vars.map.layers.length; i++) {
                    if (G.vars.map.layers[i].layerType == G.conf.map_layer_type_overlay) {
                        G.vars.map.layers[i].svgId = svg[j++].id;
                    }
                }
            
                Ext.getCmp('mapdatetype_cb').setValue(G.system.mapDateType.value);
                
                choropleth.prepareMapViewValueType();
                symbol.prepareMapViewValueType();
                
                choropleth.prepareMapViewDateType();
                symbol.prepareMapViewDateType();
                
                choropleth.prepareMapViewLegend();
                symbol.prepareMapViewLegend();
                
                G.vars.map.events.register('addlayer', null, function(e) {
                    var svg = document.getElementsByTagName('svg');
                    e.layer.svgId = svg[svg.length-1].id;
                });
                
                document.getElementById('featuredatatext').innerHTML = '<div style="color:#666">' + G.i18n.no_feature_selected + '</div>';
            }
        }
    });
    
    G.vars.map.addControl(new OpenLayers.Control.ZoomBox());
	
	G.vars.map.addControl(new OpenLayers.Control.MousePosition({
        displayClass: 'void', 
        div: $('mouseposition'), 
        prefix: '<span style="color:#666">x: &nbsp;</span>',
        separator: '<br/><span style="color:#666">y: &nbsp;</span>'
    }));
    
    G.vars.map.addControl(new OpenLayers.Control.OverviewMap({
        autoActivate: true,
        div: $('overviewmap'),
        size: new OpenLayers.Size(188, 97),
        minRectSize: 0,
        layers: [new OpenLayers.Layer.OSM.Osmarender("OSM Osmarender")]
    }));
    
    G.vars.map.addControl(new OpenLayers.Control.PanPanel({
        slideFactor: 100
    }));
    
	}});
});
