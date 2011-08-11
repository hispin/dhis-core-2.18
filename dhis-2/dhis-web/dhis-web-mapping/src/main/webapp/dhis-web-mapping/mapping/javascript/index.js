Ext.onReady( function() {
    Ext.BLANK_IMAGE_URL = '../resources/ext-ux/theme/gray-extend/gray-extend/s.gif';
	Ext.override(Ext.form.Field,{showField:function(){this.show();this.container.up('div.x-form-item').setDisplayed(true);},hideField:function(){this.hide();this.container.up('div.x-form-item').setDisplayed(false);}});
	Ext.QuickTips.init();
	document.body.oncontextmenu = function(){return false;};
    
	G.vars.map = new OpenLayers.Map({
        controls: [new OpenLayers.Control.MouseToolbar()],
        displayProjection: new OpenLayers.Projection("EPSG:4326"),
        maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508)
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
            G.user.initBaseLayers = init.baseLayers;
            G.user.initOverlays = init.overlays;
            G.user.isAdmin = init.security.isAdmin;
            G.system.aggregationStrategy = init.systemSettings.aggregationStrategy;
            G.system.infrastructuralDataElements = init.systemSettings.infrastructuralDataElements;
            G.system.infrastructuralPeriodType = init.systemSettings.infrastructuralPeriodType;
            G.system.mapDateType.value = G.system.aggregationStrategy == G.conf.aggregation_strategy_batch ?
				G.conf.map_date_type_fixed : init.userSettings.mapDateType;

    /* Section: stores */
    var mapViewStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getAllMapViews' + G.conf.type,
        root: 'mapViews',
        fields: [ 'id', 'name', 'userId', 'mapValueType', 'indicatorGroupId', 'indicatorId', 'dataElementGroupId', 'dataElementId',
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
        fields: ['name', 'displayName'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
    var infrastructuralPeriodTypeStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getAllPeriodTypes' + G.conf.type,
        root: 'periodTypes',
        fields: ['name', 'displayName'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
        
    var infrastructuralPeriodsByTypeStore = new Ext.data.JsonStore({
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
        fields: ['id', 'name', 'startValue', 'endValue', 'color', 'image', 'displayString'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });    
    
    var predefinedMapLegendSetStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getMapLegendSetsByType' + G.conf.type,
        baseParams: {type: G.conf.map_legendset_type_predefined},
        root: 'mapLegendSets',
        fields: ['id', 'name', 'legendType', 'indicators', 'dataelements'],
        sortInfo: {field:'name', direction:'ASC'},
        autoLoad: false,
        isLoaded: false,
        legendType: null,
        listeners: {
            'load': G.func.storeLoadListener
        }
    }); 
    
    var predefinedColorMapLegendSetStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getMapLegendSetsByType' + G.conf.type,
        baseParams: {type: G.conf.map_legendset_type_predefined, symbolizer: G.conf.map_legend_symbolizer_color},
        root: 'mapLegendSets',
        fields: ['id', 'name', 'symbolizer', 'indicators', 'dataelements'],
        sortInfo: {field:'name', direction:'ASC'},
        autoLoad: false,
        isLoaded: false,
        legendType: null,
        listeners: {
            'load': G.func.storeLoadListener
        }
    }); 
    
    var predefinedImageMapLegendSetStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getMapLegendSetsByType' + G.conf.type,
        baseParams: {type: G.conf.map_legendset_type_predefined, symbolizer: G.conf.map_legend_symbolizer_image},
        root: 'mapLegendSets',
        fields: ['id', 'name', 'symbolizer', 'indicators', 'dataelements'],
        sortInfo: {field:'name', direction:'ASC'},
        autoLoad: false,
        isLoaded: false,
        legendType: null,
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
    
    var baseLayerStore = new Ext.data.JsonStore({
        url: G.conf.path_mapping + 'getMapLayersByType' + G.conf.type,
        baseParams: {type: G.conf.map_layer_type_baselayer},
        root: 'mapLayers',
        fields: ['id', 'name', 'url', 'layers'],
        sortInfo: {field: 'name', direction: 'ASC'},
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
        fields: ['id', 'name', 'type', 'url', 'fillColor', 'fillOpacity', 'strokeColor', 'strokeWidth'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
    var groupSetStore = new Ext.data.JsonStore({
        url: G.conf.path_commons + 'getOrganisationUnitGroupSets' + G.conf.type,
        root: 'organisationUnitGroupSets',
        fields: ['id', 'name'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
    var groupsByGroupSetStore = new Ext.data.JsonStore({
        url: G.conf.path_commons + 'getOrganisationUnitGroupsByGroupSet' + G.conf.type,
        root: 'organisationUnitGroups',
        fields: ['id', 'name'],
        sortInfo: {field: 'name', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': G.func.storeLoadListener
        }
    });
    
    var mapLegendTypeIconStore = new Ext.data.ArrayStore({
        fields: ['name', 'css'],
        data: [
            ['0','ux-ic-icon-maplegend-type-0'],
            ['1','ux-ic-icon-maplegend-type-1'],
            ['2','ux-ic-icon-maplegend-type-2'],
            ['3','ux-ic-icon-maplegend-type-3'],
            ['4','ux-ic-icon-maplegend-type-4'],
            ['5','ux-ic-icon-maplegend-type-5']
        ]
    });
    
    G.stores = {
		mapView: mapViewStore,
        indicatorGroup: indicatorGroupStore,
        indicator: indicatorStore,
        dataElementGroup: dataElementGroupStore,
        dataElement: dataElementStore,
        periodType: periodTypeStore,
        infrastructuralPeriodType: infrastructuralPeriodTypeStore,
        infrastructuralPeriodsByType: infrastructuralPeriodsByTypeStore,
        predefinedMapLegend: predefinedMapLegendStore,
        predefinedMapLegendSet: predefinedMapLegendSetStore,
        predefinedColorMapLegendSet: predefinedColorMapLegendSetStore,
        predefinedImageMapLegendSet: predefinedImageMapLegendSetStore,
        organisationUnitLevel: organisationUnitLevelStore,
        organisationUnitsAtLevel: organisationUnitsAtLevelStore,
        geojsonFiles: geojsonFilesStore,
        overlay: overlayStore,
        baseLayer: baseLayerStore,
        groupSet: groupSetStore,
        groupsByGroupSet: groupsByGroupSetStore,
        mapLegendTypeIcon: mapLegendTypeIconStore
    };
    
	/* Thematic layers */
    polygonLayer = new OpenLayers.Layer.Vector(G.conf.thematic_layer_1, {
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
    
    pointLayer = new OpenLayers.Layer.Vector(G.conf.thematic_layer_2, {
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
    
    symbolLayer = new OpenLayers.Layer.Vector('Symbol layer', {
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
    
    symbolLayer.layerType = G.conf.map_layer_type_thematic;
    G.vars.map.addLayer(symbolLayer);
    
    centroidLayer = new OpenLayers.Layer.Vector('Centroid layer', {
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
    
    centroidLayer.layerType = G.conf.map_layer_type_thematic;
    G.vars.map.addLayer(centroidLayer);    
    
    /* Init base layers */
    if (window.google) {
        var gmap = new OpenLayers.Layer.Google(
            "Google Streets", // the default
            {numZoomLevels: 20, animationEnabled: false}
        );        
        gmap.layerType = G.conf.map_layer_type_baselayer;
        G.vars.map.addLayer(gmap);
        
        var ghyb = new OpenLayers.Layer.Google(
            "Google Hybrid",
            {type: google.maps.MapTypeId.HYBRID, numZoomLevels: 20, animationEnabled: true}
        );        
        ghyb.layerType = G.conf.map_layer_type_baselayer;
        G.vars.map.addLayer(ghyb);
    }
	
    var osm = new OpenLayers.Layer.OSM.Osmarender("OpenStreetMap");
    osm.layerType = G.conf.map_layer_type_baselayer;
    G.vars.map.addLayer(osm);
    
    /* Init base layers */
	function addBaseLayersToMap(init) {
        function add(r) {
            if (r.length) {                
                for (var i = 0; i < r.length; i++) {
                    var baseLayer = G.util.createWMSLayer(r[i].data.name, r[i].data.url, r[i].data.layers);                    
                    baseLayer.layerType = G.conf.map_layer_type_baselayer;
                    baseLayer.setVisibility(false);                    
                    G.vars.map.addLayer(baseLayer);
                }
            }
        }
        
        if (init) {
            add(G.user.initBaseLayers);
        }
        else {
            G.stores.baseLayer.load({callback: function(r) {
                add(r);
            }});
        }
	}
	addBaseLayersToMap(true);
    
    /* Init overlays */
	function addOverlaysToMap(init) {
        function add(r) {
            if (r.length) {                
                for (var i = 0; i < r.length; i++) {
                    var overlay = G.util.createOverlay(
                        r[i].data.name, r[i].data.fillColor, 1, r[i].data.strokeColor, parseFloat(r[i].data.strokeWidth),
                        G.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + r[i].data.url
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
                        autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '255'}
                    },
                    {
                        xtype: 'checkbox',
                        id: 'favoritesystem_chb',
                        disabled: !G.user.isAdmin,
                        fieldLabel: G.i18n.system,
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
                        listWidth: 'auto',
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
                    if (!vn) {
						Ext.message.msg(false, G.i18n.form_is_not_complete);
						return;
					}
                    
                    var params;                    
                    
                    if (G.vars.activePanel.isPolygon()) {
                        if (!choropleth.formValidation.validateForm.apply(choropleth, [true])) {
                            return;
                        }
                        params = choropleth.formValues.getAllValues.call(choropleth);
                    }
                    else if (G.vars.activePanel.isPoint()) {
                        if (!point.formValidation.validateForm.apply(point, [true])) {
                            return;
                        }
                        params = point.formValues.getAllValues.call(point);
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
							else if (params.featureType == G.conf.map_feature_type_point) {
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
                                    if (v == point.form.findField('mapview').getValue()) {
                                        point.form.findField('mapview').clearValue();
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
        title: '<span id="window-image-title">Export image</span>',
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
                        triggerAction: 'all'
                    },
                    {
                        xtype: 'combo',
                        id: 'exportimagewidth_cb',
                        fieldLabel: G.i18n.width,
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
                        fieldLabel: G.i18n.height,
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
                            if (point.formValidation.validateForm.call(point)) {
                                document.getElementById('layerField').value = 3;
                                document.getElementById('imageLegendRowsField').value = choropleth.imageLegend.length;
                                
                                values = choropleth.formValues.getImageExportValues.call(choropleth);
                                document.getElementById('periodField').value = values.dateValue;
                                document.getElementById('indicatorField').value = values.mapValueTypeValue;
                                document.getElementById('legendsField').value = G.util.getLegendsJSON.call(choropleth);
                                
                                values = point.formValues.getImageExportValues.call(point);
                                document.getElementById('periodField2').value = values.dateValue;
                                document.getElementById('indicatorField2').value = values.mapValueTypeValue;
                                document.getElementById('legendsField2').value = G.util.getLegendsJSON.call(point);
                                
                                var str1 = document.getElementById(polygonLayer.svgId).parentNode.innerHTML;
                                var str2 = document.getElementById(pointLayer.svgId).parentNode.innerHTML;
                                svg = G.util.mergeSvg(str1, [str2]);                                
                            }
                            else {
                                Ext.message.msg(false, '<span class="x-msg-hl">' + G.conf.thematic_layer_1 + '</span> not rendered');
                                return;
                            }
                        }
                        else {
                            Ext.message.msg(false, '<span class="x-msg-hl">' + G.conf.thematic_layer_2 + '</span> not rendered');
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
                            Ext.message.msg(false, '<span class="x-msg-hl">' + G.conf.thematic_layer_1 + '</span> not rendered');
                            return;
                        }
                    }
                    else if (pointLayer.visibility) {
                        if (point.formValidation.validateForm.call(point)) {
                            values = point.formValues.getImageExportValues.call(point);
                            document.getElementById('layerField').value = 2;
                            document.getElementById('periodField').value = values.dateValue;  
                            document.getElementById('indicatorField').value = values.mapValueTypeValue;
                            document.getElementById('legendsField').value = G.util.getLegendsJSON.call(point);
                            svg = document.getElementById(pointLayer.svgId).parentNode.innerHTML;
                        }
                        else {
                            Ext.message.msg(false, '<span class="x-msg-hl">' + G.conf.thematic_layer_2 + '</span> not rendered');
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
        title: '<span id="window-predefinedlegendset-title">' + G.i18n.predefined_legend_sets + '</span>',
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
                                xtype: 'combo',
                                id: 'predefinedmaplegendtype_cb',
                                fieldLabel: G.i18n.legend_symbolizer,
                                labelSeparator: G.conf.labelseparator,
                                editable: false,
                                valueField: 'id',
                                displayField: 'symbolizer',
                                width: G.conf.combo_width_fieldset,
                                minListWidth: G.conf.combo_width_fieldset,
                                mode: 'local',
                                triggerAction: 'all',
                                value: 'color',
                                store: new Ext.data.ArrayStore({
                                    fields: ['id','symbolizer'],
                                    data: [
                                        [G.conf.map_legend_symbolizer_color, G.i18n.color],
                                        [G.conf.map_legend_symbolizer_image, G.i18n.image]
                                    ]
                                }),
                                listeners: {
                                    'select': function(cb) {
                                        if (cb.getValue() == G.conf.map_legend_symbolizer_color) {
                                            Ext.getCmp('predefinedmaplegendcolor_cf').showField();
                                            Ext.getCmp('predefinedmaplegendimage_cb').hideField();
                                        }
                                        else if (cb.getValue() == G.conf.map_legend_symbolizer_image) {
                                            Ext.getCmp('predefinedmaplegendcolor_cf').hideField();
                                            Ext.getCmp('predefinedmaplegendimage_cb').showField();
                                        }
                                    }
                                }
                            },
                            {
                                xtype: 'colorfield',
                                id: 'predefinedmaplegendcolor_cf',
                                emptyText: G.conf.emptytext,
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.color,
                                allowBlank: false,
                                width: G.conf.combo_width_fieldset,
                                value:"#C0C0C0"
                            },
                            {
                                xtype: 'combo',
                                id: 'predefinedmaplegendimage_cb',
                                plugins: new Ext.ux.plugins.IconCombo(),
                                valueField: 'name',
                                displayField: 'css',
                                iconClsField: 'css',
                                editable: false,
                                triggerAction: 'all',
                                mode: 'local',
                                labelSeparator: G.conf.labelseparator,
                                fieldLabel: G.i18n.image,
                                hidden: true,
                                width: G.conf.combo_number_width_small,
                                listWidth: G.conf.combo_number_width_small,
                                store: G.stores.mapLegendTypeIcon
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
                                store: G.stores.predefinedMapLegend,
                                listeners: {
                                    'focus': function(cb) {
                                        cb.getStore().clearFilter();
                                    }
                                }
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
                                            var type = Ext.getCmp('predefinedmaplegendtype_cb').getValue();
                                            var mlc = type == G.conf.map_legend_symbolizer_color ?
                                                Ext.getCmp('predefinedmaplegendcolor_cf').getValue() : null;
                                            var mli = type == G.conf.map_legend_symbolizer_image ?
                                                Ext.getCmp('predefinedmaplegendimage_cb').getRawValue() : null;
                                            
                                            if (!Ext.isNumber(parseFloat(mlsv)) || !Ext.isNumber(mlev)) {
                                                Ext.message.msg(false, G.i18n.form_is_not_complete);
                                                return;
                                            }
                                            
                                            if (!mln || (!mlc && !mli)) {
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
                                            
                                            var params = {};
                                            params.name = mln;
                                            params.startValue = mlsv;
                                            params.endValue = mlev;                                            
                                            if (type == G.conf.map_legend_symbolizer_color) {
                                                params.color = mlc;
                                            }
                                            else if (type == G.conf.map_legend_symbolizer_image) {
                                                params.image = mli;
                                            }
                                            
                                            Ext.Ajax.request({
                                                url: G.conf.path_mapping + 'addOrUpdateMapLegend' + G.conf.type,
                                                method: 'POST',
                                                params: params,
                                                success: function(r) {
                                                    Ext.message.msg(true, G.i18n.legend + ' <span class="x-msg-hl">' + mln + '</span> ' + G.i18n.was_registered);
                                                    G.stores.predefinedMapLegend.load();
                                                    Ext.getCmp('predefinedmaplegendname_tf').reset();
                                                    Ext.getCmp('predefinedmaplegendstartvalue_nf').reset();
                                                    Ext.getCmp('predefinedmaplegendendvalue_nf').reset();
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
                        predefinedMapLegendSetWindow.setHeight(G.conf.predefinedmaplegendsetwindow_expanded_1);
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(G.conf.predefinedmaplegendsetwindow_collapsed);
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
                            {
                                xtype: 'combo',
                                id: 'predefinedmaplegendsettype_cb',
                                fieldLabel: G.i18n.legend_symbolizer,
                                labelSeparator: G.conf.labelseparator,
                                editable: false,
                                valueField: 'id',
                                displayField: 'symbolizer',
                                width: G.conf.combo_width_fieldset,
                                minListWidth: G.conf.combo_width_fieldset,
                                mode: 'local',
                                triggerAction: 'all',
                                store: new Ext.data.ArrayStore({
                                    fields: ['id','symbolizer'],
                                    data: [
                                        [G.conf.map_legend_symbolizer_color, G.i18n.color],
                                        [G.conf.map_legend_symbolizer_image, G.i18n.image]
                                    ]
                                }),
                                listeners: {
                                    'select': function(cb) {
                                        G.stores.predefinedMapLegend.filterBy( function(r, rid) {
                                            if (cb.getValue() == G.conf.map_legend_symbolizer_color) {
                                                return r.data.color;
                                            }
                                            else if (cb.getValue() == G.conf.map_legend_symbolizer_image) {
                                                return r.data.image;
                                            }
                                        });
                                    }
                                }
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
                                            
                                            var symbolizer = Ext.getCmp('predefinedmaplegendsettype_cb').getValue();
                                            
                                            Ext.Ajax.request({
                                                url: G.conf.path_mapping + 'addOrUpdateMapLegendSet.action' + params,
                                                method: 'POST',
                                                params: {name: mlsv, type: G.conf.map_legendset_type_predefined, symbolizer: symbolizer},
                                                success: function(r) {
                                                    Ext.message.msg(true, G.i18n.new_legend_set+' <span class="x-msg-hl">' + mlsv + '</span> ' + G.i18n.was_registered);
                                                    Ext.getCmp('predefinedmaplegendsetname_tf').reset();
                                                    Ext.getCmp('predefinednewmaplegend_ms').reset();			
                                                    G.stores.predefinedMapLegendSet.load();
                                                    if (symbolizer == G.conf.map_legend_symbolizer_color) {
                                                        G.stores.predefinedColorMapLegendSet.load();
                                                    }
                                                    else if (symbolizer == G.conf.map_legend_symbolizer_image) {
                                                        G.stores.predefinedImageMapLegendSet.load();
                                                    }
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
                        predefinedMapLegendSetWindow.setHeight((G.util.getMultiSelectHeight() / 2) + G.conf.predefinedmaplegendsetwindow_expanded_2);
                        
                        var pmlst = Ext.getCmp('predefinedmaplegendsettype_cb');
                        if (pmlst.getValue()) {
                            G.stores.predefinedMapLegend.filterBy( function(r) {
                                if (pmlst.getValue() == G.conf.map_legend_symbolizer_color) {
                                    return r.data.color;
                                }
                                else if (pmlst.getValue() == G.conf.map_legend_symbolizer_image) {
                                    return r.data.image;
                                }
                            });
                        }
                        else {
                            pmlst.setValue(G.conf.map_legend_symbolizer_color);
                            G.stores.predefinedMapLegend.filterBy( function(r, rid) {
                                return r.data.color;
                            });
                        }                                                        
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(G.conf.predefinedmaplegendsetwindow_collapsed);
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
                        predefinedMapLegendSetWindow.setHeight(G.util.getMultiSelectHeight() + G.conf.predefinedmaplegendsetwindow_expanded_3);
                        
                        if (!G.stores.indicator.isLoaded) {
                            G.stores.indicator.load();
                        }
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(G.conf.predefinedmaplegendsetwindow_collapsed);
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
                        predefinedMapLegendSetWindow.setHeight(G.util.getMultiSelectHeight() + G.conf.predefinedmaplegendsetwindow_expanded_4);
                        
                        if (!G.stores.dataElement.isLoaded) {
                            G.stores.dataElement.load();
                        }
                    },
                    collapse: function() {
                        predefinedMapLegendSetWindow.setHeight(G.conf.predefinedmaplegendsetwindow_collapsed);
                    }
                }
            }
        ],
        listeners: {
            afterrender: function() {
                predefinedMapLegendSetWindow.setHeight(G.conf.predefinedmaplegendsetwindow_expanded_1);
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
                        title: '<span class="panel-tab-title">' + G.i18n.overlays_ + '</span>'
                    },
                    {
                        id: 'help6',
                        title: '<span class="panel-tab-title">' + G.i18n.setup + '</span>'
                    }
                ]
            }
        ]
    });

    /* Section: base layers */
	var baseLayersWindow = new Ext.Window({
        id: 'baselayers_w',
        title: '<span id="window-baselayer-title">WMS ' + G.i18n.overlays + '</span>',
		layout: 'fit',
        closeAction: 'hide',
        height: 230,
		width: G.conf.window_width,
        items: [
            {
                xtype: 'form',
                bodyStyle: 'padding:8px',
                labelWidth: G.conf.label_width,
                items: [
                    {html: '<div class="window-info">' + G.i18n.register_new_wms_overlay + '</div>'},
                    {
                        xtype: 'textfield',
                        id: 'baselayername_tf',
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.display_name,
                        width: G.conf.combo_width_fieldset,
                        autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '50'}
                    },
                    {
                        xtype: 'textfield',
                        id: 'baselayerurl_tf',
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.url,
                        width: G.conf.combo_width_fieldset
                    },
                    {
                        xtype: 'textfield',
                        id: 'baselayerlayer_tf',
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.layer,
                        width: G.conf.combo_width_fieldset
                    },
                    {html: '<div class="window-p"></div>'},
                    {html: '<div class="window-info">' + G.i18n.delete_ + ' WMS ' + G.i18n.overlay + '</div>'},
                    {
                        xtype: 'combo',
                        id: 'baselayer_cb',
                        editable: false,
                        valueField: 'id',
                        displayField: 'name',
                        mode: 'remote',
                        forceSelection: true,
                        triggerAction: 'all',
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.overlay_,
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
                id: 'newbaselayer_b',
                text: 'Register',
                iconCls: 'icon-add',
                handler: function() {
                    var bln = Ext.getCmp('baselayername_tf').getRawValue();
                    var blu = Ext.getCmp('baselayerurl_tf').getRawValue();
                    var bll = Ext.getCmp('baselayerlayer_tf').getRawValue();
                    
                    if (!bln || !blu || !bll) {
                        Ext.message.msg(false, G.i18n.form_is_not_complete);
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: G.conf.path_mapping + 'addOrUpdateMapLayer' + G.conf.type,
                        method: 'POST',
                        params: {name: bln, type: G.conf.map_layer_type_baselayer, url: blu, layers: bll},
                        success: function(r) {
                            Ext.message.msg(true, 'WMS ' + G.i18n.overlay + ' <span class="x-msg-hl">' + bln + '</span> ' + G.i18n.registered);
                            G.stores.baseLayer.load();
                            
                            if (G.vars.map.getLayersByName(bln).length) {
                                G.vars.map.getLayersByName(bln)[0].destroy();
                            }
                            
                            var baselayer = G.util.createWMSLayer(bln, blu, bll);  
                            baselayer.layerType = G.conf.map_layer_type_baselayer;
                            baselayer.setVisibility(false);                            
                            G.vars.map.addLayer(baselayer);
                            
                            Ext.getCmp('baselayername_tf').reset();
                        }
                    });
                }
            },
            {
                xtype: 'button',
                id: 'deletebaselayer_b',
                text: G.i18n.delete_,
                iconCls: 'icon-remove',
                handler: function() {
                    var bl = Ext.getCmp('baselayer_cb').getValue();
                    var bln = Ext.getCmp('baselayer_cb').getRawValue();
                    
                    if (!bl) {
                        Ext.message.msg(false, G.i18n.please_select_a_baselayer);
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: G.conf.path_mapping + 'deleteMapLayer' + G.conf.type,
                        method: 'POST',
                        params: {id: bl},
                        success: function(r) {
                            Ext.message.msg(true, 'WMS ' + G.i18n.overlay + ' <span class="x-msg-hl">' + bln + '</span> '+ G.i18n.deleted);
                            G.stores.baseLayer.load();
                            Ext.getCmp('baselayer_cb').clearValue();
                        }
                    });
                    
                    G.vars.map.getLayersByName(bln)[0].destroy();
                }
            }
        ]
    });

    /* Section: overlays */
	var overlaysWindow = new Ext.Window({
        id: 'overlays_w',
        title: '<span id="window-maplayer-title">Vector ' + G.i18n.overlays + '</span>',
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
                    {html: '<div class="window-info">' + G.i18n.register_new_vector_overlay + '</div>'},
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
                        width: G.conf.combo_number_width_small,
                        minListWidth: G.conf.combo_number_width_small,
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
                        width: G.conf.combo_number_width_small,
                        minListWidth: G.conf.combo_number_width_small,
                        value: 2,
                        store: {
                            xtype: 'arraystore',
                            fields: ['value'],
                            data: [[0],[1],[2],[3],[4],[5]]
                        }
                    },
                    {html: '<div class="window-p"></div>'},
                    {html: '<div class="window-info">' + G.i18n.delete_ + ' vector ' + G.i18n.overlay + '</div>'},
                    {
                        xtype: 'combo',
                        id: 'maplayer_cb',
                        editable: false,
                        valueField: 'id',
                        displayField: 'name',
                        mode: 'remote',
                        forceSelection: true,
                        triggerAction: 'all',
                        emptytext: G.conf.emptytext,
                        labelSeparator: G.conf.labelseparator,
                        fieldLabel: G.i18n.overlay_,
                        width: G.conf.combo_width_fieldset,                
                        store: G.stores.overlay
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
                        params: {name: mln, type: 'overlay', url: mlmsf, fillColor: mlfc, fillOpacity: 1, strokeColor: mlsc, strokeWidth: mlsw},
                        success: function(r) {
                            Ext.message.msg(true, 'Vector ' + G.i18n.overlay + ' <span class="x-msg-hl">' + mln + '</span> ' + G.i18n.registered);
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
                            Ext.message.msg(true, 'Vector ' + G.i18n.overlay + ' <span class="x-msg-hl">' + mln + '</span> '+ G.i18n.deleted);
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

    /* Section: administrator settings */
    var adminWindow = new Ext.Window({
        id: 'admin_w',
        title: '<span id="window-admin-title">Administrator settings</span>',
        layout: 'accordion',
        closeAction: 'hide',
        width: G.conf.window_width,
        height: G.conf.adminwindow_expanded_1,
        minHeight: G.conf.adminwindow_collapsed,
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
                                                    point.prepareMapViewDateType();
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
                        adminWindow.setHeight(G.conf.adminwindow_expanded_1);
                    },
                    collapse: function() {
                        adminWindow.setHeight(G.conf.adminwindow_collapsed);
                    }
                }
            }
        ],
        listeners: {
            afterrender: function() {
                adminWindow.setHeight(G.conf.adminwindow_expanded_1);
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
                    layer: G.conf.thematic_layer_1
                },
                {
                    nodeType: 'gx_layer',
                    layer: G.conf.thematic_layer_2
                },
                {
                    nodeType: 'gx_layer',
                    layer: 'Symbol layer'
                },
                {
                    nodeType: 'gx_layer',
                    layer: 'Centroid layer'
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
                        title: '<span id="window-locate-title">Locate features</span>',
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
                                        fieldLabel: G.i18n.text_filter,
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
                    locateFeatureWindow.setPagePosition(Ext.getCmp('east').x - (locateFeatureWindow.width + 15), Ext.getCmp('center').y + 41);
                    locateFeatureWindow.show();
                }
                else {
                    Ext.message.msg(false, '<span class="x-msg-hl">' + layer.name + '</span>: No features rendered');
                }
            },
            
            showLabelWindow: function(item) {
                var layer = G.vars.map.getLayersByName(item.parentMenu.contextNode.attributes.layer)[0];
                if (layer.features.length) {
                    if (item.labelWindow) {
                        item.labelWindow.show();
                    }
                    else {
                        item.labelWindow = new Ext.Window({
                            title: '<span id="window-labels-title">Labels</span>',
                            layout: 'fit',
                            closeAction: 'hide',
                            width: G.conf.window_width,
                            height: 200,
                            items: [
                                {
                                    xtype: 'form',
                                    bodyStyle: 'padding:8px',
                                    labelWidth: G.conf.label_width,
                                    items: [
                                        {html: '<div class="window-info">Show/hide feature labels</div>'},
                                        {
                                            xtype: 'numberfield',
                                            id: 'labelfontsize_nf',
                                            fieldLabel: G.i18n.font_size,
                                            labelSeparator: G.conf.labelseparator,
                                            width: G.conf.combo_number_width_small,
                                            enableKeyEvents: true,
                                            allowDecimals: false,
                                            allowNegative: false,
                                            value: 13,
                                            emptyText: 13,
                                            listeners: {
                                                'keyup': function(nf) {
                                                    if (layer.widget.labels) {
                                                        layer.widget.labels = false;
                                                        G.util.labels.toggleFeatureLabels(layer.widget, nf.getValue(), Ext.getCmp('labelstrong_chb').getValue(),
                                                            Ext.getCmp('labelitalic_chb').getValue(), Ext.getCmp('labelcolor_cf').getValue());
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            xtype: 'checkbox',
                                            id: 'labelstrong_chb',
                                            fieldLabel: '<b>' + G.i18n.bold_ + '</b>',
                                            labelSeparator: G.conf.labelseparator,
                                            listeners: {
                                                'check': function(chb, checked) {
                                                    if (layer.widget.labels) {
                                                        layer.widget.labels = false;
                                                        G.util.labels.toggleFeatureLabels(layer.widget, Ext.getCmp('labelfontsize_nf').getValue(),
                                                            checked, Ext.getCmp('labelitalic_chb').getValue(), Ext.getCmp('labelcolor_cf').getValue());
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            xtype: 'checkbox',
                                            id: 'labelitalic_chb',
                                            fieldLabel: '<i>' + G.i18n.italic + '</i>',
                                            labelSeparator: G.conf.labelseparator,
                                            listeners: {
                                                'check': function(chb, checked) {
                                                    if (layer.widget.labels) {
                                                        layer.widget.labels = false;
                                                        G.util.labels.toggleFeatureLabels(layer.widget, Ext.getCmp('labelfontsize_nf').getValue(),
                                                            Ext.getCmp('labelstrong_chb').getValue(), checked, Ext.getCmp('labelcolor_cf').getValue());
                                                    }
                                                }
                                            }
                                        },
                                        {
                                            xtype: 'colorfield',
                                            id: 'labelcolor_cf',
                                            fieldLabel: G.i18n.color,
                                            labelSeparator: G.conf.labelseparator,
                                            allowBlank: false,
                                            width: G.conf.combo_width_fieldset,
                                            value: "#000000",
                                            listeners: {
                                                'select': {
                                                    scope: this,
                                                    fn: function(cf) {
                                                        if (layer.widget.labels) {
                                                            layer.widget.labels = false;
                                                            G.util.labels.toggleFeatureLabels(layer.widget, Ext.getCmp('labelfontsize_nf').getValue(),
                                                                Ext.getCmp('labelstrong_chb').getValue(), Ext.getCmp('labelitalic_chb').getValue(), cf.getValue());
                                                        }
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
                                    id: 'labelshow_b',
                                    iconCls: 'icon-assign',
                                    hideLabel: true,
                                    text: G.i18n.toggle,
                                    handler: function() {
                                        var layer = G.vars.map.getLayersByName(item.parentMenu.contextNode.attributes.layer)[0];                                    
                                        if (layer.features.length) {
                                            G.util.labels.toggleFeatureLabels(layer.widget, Ext.getCmp('labelfontsize_nf').getValue(),
                                                Ext.getCmp('labelstrong_chb').getValue(), Ext.getCmp('labelitalic_chb').getValue(), Ext.getCmp('labelcolor_cf').getValue());
                                        }
                                        else {
                                            Ext.message.msg(false, '<span class="x-msg-hl">' + layer.name + '</span>: No features rendered');
                                        }
                                    }
                                }
                            ]
                        });
                        item.labelWindow.setPagePosition(Ext.getCmp('east').x - (item.labelWindow.width + 15), Ext.getCmp('center').y + 41);                        
                        item.labelWindow.show();
                    }
                }
                else {
                    Ext.message.msg(false, '<span class="x-msg-hl">' + layer.name + '</span>: No features rendered');
                }
            },
            items: [
                {
                    text: 'Locate feature',
                    iconCls: 'menu-layeroptions-locate',
                    handler: function(item) {
                        item.parentMenu.showLocateFeatureWindow(item.parentMenu);
                    }
                },
                {
                    text: 'Labels',
                    iconCls: 'menu-layeroptions-labels',
                    labelsWindow: null,
                    handler: function(item) {
                        item.parentMenu.showLabelWindow(item);
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
                id: 'baselayers_b',
                text: 'WMS ' + G.i18n.overlay,
                iconCls: 'icon-baselayer',
                handler: function() {
                    Ext.getCmp('baselayers_w').setPagePosition(Ext.getCmp('east').x - (Ext.getCmp('overlays_w').width + 15), Ext.getCmp('center').y + 41);
                    Ext.getCmp('baselayers_w').show();
                }
            },
            {
                xtype: 'button',
                id: 'overlays_b',
                text: 'Vector ' + G.i18n.overlay,
                iconCls: 'icon-overlay',
                handler: function() {
                    Ext.getCmp('overlays_w').setPagePosition(Ext.getCmp('east').x - (Ext.getCmp('overlays_w').width + 15), Ext.getCmp('center').y + 41);
                    Ext.getCmp('overlays_w').show();
                }
            }
        ]
	});
	
    /* Section: widgets */
    choropleth = new mapfish.widgets.geostat.Choropleth({
        id: 'choropleth',
		title: '<span class="panel-title">Thematic layer 1</span>',
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

    point = new mapfish.widgets.geostat.Point({
        id: 'point',
        map: G.vars.map,
        layer: pointLayer,
		title: '<span class="panel-title">Thematic layer 2</span>',
        featureSelection: false,
        legendDiv: 'pointlegend',
        defaults: {width: 130},
        tools: [
            {
                id: 'refresh',
                qtip: 'Refresh layer',
                handler: function() {
                    point.updateValues = true;
                    point.classify();
                }
            },
            {
                id: 'close',
                qtip: 'Clear layer',
                handler: function() {
                    point.formValues.clearForm.call(point);
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

    symbol = new mapfish.widgets.geostat.Symbol({
        id: 'symbol',
        map: G.vars.map,
        layer: symbolLayer,
		title: '<span class="panel-title">Symbol layer</span>',
        featureSelection: false,
        legendDiv: 'symbollegend',
        defaults: {width: 130},
        tools: [
            {
                id: 'refresh',
                qtip: 'Refresh layer',
                handler: function() {
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
                G.vars.activePanel.setSymbol();
            },
            'afterrender': function() {
                this.layer.widget = this;
            }
        }
    });
    
    centroid = new mapfish.widgets.geostat.Centroid({
        id: 'centroid',
		title: '<span class="panel-title">Centroid layer</span>',
        map: G.vars.map,
        layer: centroidLayer,
        featureSelection: false,
        legendDiv: 'centroidlegend',
        defaults: {width: 130},
        tools: [
            {
                id: 'refresh',
                qtip: 'Refresh layer',
                handler: function() {
                    centroid.updateValues = true;
                    centroid.classify();
                }
            },
            {
                id: 'close',
                qtip: 'Clear layer',
                handler: function() {
                    choropleth.formValues.clearForm.call(centroid);
                }
            }
        ],
        listeners: {
            'expand': function() {
                G.vars.activePanel.setCentroid();
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
		handler: function() {
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
                if (point.layer.getDataExtent()) {
                    G.vars.map.zoomToExtent(point.layer.getDataExtent());
                }
            }
            else if (G.vars.activePanel.isSymbol()) {
                if (symbol.layer.getDataExtent()) {
                    G.vars.map.zoomToExtent(symbol.layer.getDataExtent());
                }
            }
            else if (G.vars.activePanel.isCentroid()) {
                if (centroid.layer.getDataExtent()) {
                    G.vars.map.zoomToExtent(centroid.layer.getDataExtent());
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
    
    var measureDistanceButton = new Ext.Button({
        iconCls: 'icon-measure',
        tooltip: G.i18n.measure_distance,
        style: 'margin-top:1px',
        handler: function() {
            var control = G.vars.map.getControl('measuredistance');
            
            if (!control.active) {                
                if (!control.window) {
                    control.window = new Ext.Window({
                        title: '<span id="window-measure-title">' + G.i18n.measure_distance + '</span>',
                        layout: 'fit',
                        closeAction: 'hide',
                        width: 150,
                        height: 90,
                        items: [
                            {
                                xtype: 'panel',
                                layout: 'anchor',
                                bodyStyle: 'padding:8px',
                                items: [
                                    {html: '<div class="window-info">Total distance</div>'},
                                    {html: '<div id="measureDistanceDiv"></div>'}
                                ]
                            }
                        ],
                        listeners: {
                            'hide': function() {
                                G.vars.map.getControl('measuredistance').deactivate();
                            }
                        }
                    });
                }
                control.window.setPagePosition(Ext.getCmp('east').x - (control.window.width + 15), Ext.getCmp('center').y + 41);
                control.window.show();
                document.getElementById('measureDistanceDiv').innerHTML = '0 km';                
                control.setImmediate(true);
                control.geodesic = true;
                control.activate();
            }
            else {
                control.deactivate();
                control.window.hide();
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
            measureDistanceButton,
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
                        title: '<span class="panel-title">' + G.conf.thematic_layer_1 + ' legend</span>',
                        contentEl: 'polygonlegend'
                    },
                    {
                        title: '<span class="panel-title">' + G.conf.thematic_layer_2 + ' legend</span>',
                        contentEl: 'pointlegend'
                    },
                    {
                        title: '<span class="panel-title">Symbol layer legend</span>',
                        contentEl: 'symbollegend'
                    },
                    {
                        title: '<span class="panel-title">Centroid layer legend</span>',
                        contentEl: 'centroidlegend'
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
                    point,
                    symbol,
                    centroid
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
                symbolLayer.setOpacity(1);
                centroidLayer.setOpacity(1);
                
                var svg = document.getElementsByTagName('svg');
                
                if (!Ext.isIE) {
                    polygonLayer.svgId = svg[0].id;
                    pointLayer.svgId = svg[1].id;
                    symbolLayer.svgId = svg[2].id;
                }
                
                for (var i = 0, j = 3; i < G.vars.map.layers.length; i++) {
                    if (G.vars.map.layers[i].layerType == G.conf.map_layer_type_overlay) {
                        G.vars.map.layers[i].svgId = svg[j++].id;
                    }
                }
            
                Ext.getCmp('mapdatetype_cb').setValue(G.system.mapDateType.value);
                
                choropleth.prepareMapViewValueType();
                choropleth.prepareMapViewDateType();
                choropleth.prepareMapViewLegend();
                
                point.prepareMapViewValueType();
                point.prepareMapViewDateType();
                point.prepareMapViewLegend();
                
                centroid.prepareMapViewValueType();
                centroid.prepareMapViewDateType();                
                
                G.vars.map.events.register('addlayer', null, function(e) {
                    var svg = document.getElementsByTagName('svg');
                    e.layer.svgId = svg[svg.length-1].id;
                });
                
                G.vars.map.events.register('mousemove', null, function(e) {
                    G.vars.mouseMove.x = e.clientX;
                    G.vars.mouseMove.y = e.clientY;
                });
                
                G.vars.map.events.register('click', null, function(e) {
                    if (G.vars.relocate.active) {
                        var mp = document.getElementById('mouseposition');
                        var coordinates = '[' + mp.childNodes[1].data + ',' + mp.childNodes[4].data + ']';
                        var center = Ext.getCmp('center').x;
	
                        Ext.Ajax.request({
                            url: G.conf.path_mapping + 'updateOrganisationUnitCoordinates' + G.conf.type,
                            method: 'POST',
                            params: {id: G.vars.relocate.feature.attributes.id, coordinates: coordinates},
                            success: function(r) {
                                G.vars.relocate.active = false;
                                G.vars.relocate.widget.featureOptions.coordinate.destroy();
                                                                
                                G.vars.relocate.feature.move({x: parseFloat(e.clientX - center), y: parseFloat(e.clientY - 28)});
                                document.getElementById('OpenLayers.Map_3_OpenLayers_ViewPort').style.cursor = 'auto';
                                Ext.message.msg(true, '<span class="x-msg-hl">' + G.vars.relocate.feature.attributes.name + 
                                    ' </span>relocated to ' +
                                    '[<span class="x-msg-hl">' + mp.childNodes[1].data + '</span>,' + 
                                    '<span class="x-msg-hl">' + mp.childNodes[4].data + '</span>]');
                            }
                        });
                    }
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
        layers: [new OpenLayers.Layer.OSM.Osmarender('OSM Osmarender')]
    }));
    
    G.vars.map.addControl(new OpenLayers.Control.PanPanel({
        slideFactor: 100
    }));
    
    G.vars.map.addControl(new OpenLayers.Control.Measure( OpenLayers.Handler.Path, {
        id: 'measuredistance',
        persist: true,
        handlerOptions: {
            layerOptions: {styleMap: G.util.measureDistance.getMeasureStyleMap()}
        }
    }));
    
    G.vars.map.getControl('measuredistance').events.on({
        "measurepartial": G.util.measureDistance.handleMeasurements,
        "measure": G.util.measureDistance.handleMeasurements
    });
    
	}});
});
