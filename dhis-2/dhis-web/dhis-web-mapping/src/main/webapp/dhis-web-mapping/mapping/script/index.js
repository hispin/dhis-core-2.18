Ext.onReady( function() {
    Ext.BLANK_IMAGE_URL = '../resources/ext-ux/theme/gray-extend/gray-extend/s.gif';
	Ext.override(Ext.form.Field,{showField:function(){this.show();this.container.up('div.x-form-item').setDisplayed(true);},hideField:function(){this.hide();this.container.up('div.x-form-item').setDisplayed(false);}});
	Ext.QuickTips.init();
	document.body.oncontextmenu = function(){return false;};
	
	GLOBALS.vars.map = new OpenLayers.Map({controls:[new OpenLayers.Control.Navigation(),new OpenLayers.Control.ArgParser(),new OpenLayers.Control.Attribution()]});
	GLOBALS.vars.mask = new Ext.LoadMask(Ext.getBody(),{msg:i18n_loading,msgCls:'x-mask-loading2'});
    GLOBALS.vars.parameter = GLOBALS.util.getUrlParam('view') ? {id: GLOBALS.util.getUrlParam('view')} : false;
    
    Ext.Ajax.request({
        url: GLOBALS.conf.path_mapping + 'getMapView' + GLOBALS.conf.type,
        method: 'GET',
        params: {id: GLOBALS.vars.parameter.id || 0},
        success: function(r) {
            var mv = Ext.util.JSON.decode(r.responseText).mapView[0];
            if (GLOBALS.vars.parameter) {
                if (!mv.id) {
                    GLOBALS.vars.parameter = false;
                }
                else {
                    GLOBALS.vars.parameter.mapView = mv;
                }
            }
            
            Ext.Ajax.request({
                url: GLOBALS.conf.path_mapping + 'getMapUserSettings' + GLOBALS.conf.type,
                method: 'GET',
                success: function(r) {
                    var us = Ext.util.JSON.decode(r.responseText);
                    GLOBALS.vars.mapSourceType.value = GLOBALS.vars.parameter ? GLOBALS.vars.parameter.mapView.mapSourceType : us.mapSource;
                    GLOBALS.vars.mapDateType.value = GLOBALS.vars.parameter ? GLOBALS.vars.parameter.mapView.mapDateType : us.mapDateType;
                    
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + 'setMapUserSettings' + GLOBALS.conf.type,
                        method: 'POST',
                        params: {mapSourceType: GLOBALS.vars.mapSourceType.value, mapDateType: GLOBALS.vars.mapDateType.value},
                        success: function() {
                        
    /* Section: stores */
    var mapViewStore = new Ext.data.JsonStore({
        url: GLOBALS.conf.path_mapping + 'getAllMapViews' + GLOBALS.conf.type,
        root: 'mapViews',
        fields: ['id', 'name', 'mapValueType', 'indicatorGroupId', 'indicatorId', 'dataElementGroupId', 'dataElementId', 'mapDateType', 'periodTypeId',
            'periodId', 'startDate', 'endDate', 'mapSourceType', 'organisationUnitSelectionType', 'mapSource', 'organisationUnitSelectionTypeName', 'mapLegendType',
			'method', 'classes', 'bounds', 'colorLow', 'colorHigh', 'mapLegendSetId', 'longitude', 'latitude', 'zoom'],
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
        url: GLOBALS.conf.path_mapping + 'getAllIndicatorGroups' + GLOBALS.conf.type,
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
        url: GLOBALS.conf.path_mapping + 'getIndicatorsByIndicatorGroup' + GLOBALS.conf.type,
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
        url: GLOBALS.conf.path_mapping + 'getAllIndicators' + GLOBALS.conf.type,
        root: 'indicators',
        fields: ['id','name','shortName'],
        sortInfo: {field: 'shortName', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
  
    var dataElementGroupStore = new Ext.data.JsonStore({
        url: GLOBALS.conf.path_mapping + 'getAllDataElementGroups' + GLOBALS.conf.type,
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
        url: GLOBALS.conf.path_mapping + 'getDataElementsByDataElementGroup' + GLOBALS.conf.type,
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
        url: GLOBALS.conf.path_mapping + 'getAllDataElements' + GLOBALS.conf.type,
        root: 'dataElements',
        fields: ['id','name','shortName'],
        sortInfo: {field: 'shortName', direction: 'ASC'},
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
    var periodTypeStore = new Ext.data.JsonStore({
        url: GLOBALS.conf.path_mapping + 'getAllPeriodTypes' + GLOBALS.conf.type,
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
        url: GLOBALS.conf.path_mapping + 'getPeriodsByPeriodType' + GLOBALS.conf.type,
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
        
    var mapStore = new Ext.data.JsonStore({
        url: GLOBALS.conf.path_mapping + 'getAllMaps' + GLOBALS.conf.type,
        root: 'maps',
        fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel', 'nameColumn'],
        idProperty: 'mapLayerPath',
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
	var predefinedMapLegendStore = new Ext.data.JsonStore({
        url: GLOBALS.conf.path_mapping + 'getAllMapLegends' + GLOBALS.conf.type,
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
        url: GLOBALS.conf.path_mapping + 'getMapLegendSetsByType' + GLOBALS.conf.type,
        baseParams: {type: GLOBALS.conf.map_legend_type_predefined},
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
        url: GLOBALS.conf.path_mapping + 'getOrganisationUnitLevels' + GLOBALS.conf.type,
        id: 'id',
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
        url: GLOBALS.conf.path_mapping + 'getOrganisationUnitsAtLevel' + GLOBALS.conf.type,
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
        url: GLOBALS.conf.path_mapping + 'getGeoJsonFiles' + GLOBALS.conf.type,
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
    
	var nameColumnStore = new Ext.data.ArrayStore({
        fields: ['name']
    });
    
	var wmsCapabilitiesStore = new GeoExt.data.WMSCapabilitiesStore({
        url: GLOBALS.conf.path_geoserver + GLOBALS.conf.ows,
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });
    
	var baseLayerStore = new Ext.data.JsonStore({
        url: GLOBALS.conf.path_mapping + 'getMapLayersByType' + GLOBALS.conf.type,
        baseParams: {type: GLOBALS.conf.map_layer_type_baselayer},
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
        url: GLOBALS.conf.path_mapping + 'getMapLayersByType' + GLOBALS.conf.type,
        baseParams: {type: GLOBALS.conf.map_layer_type_overlay},
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
        url: GLOBALS.conf.path_mapping + 'getMapUserSettings' + GLOBALS.conf.type,
        fields: ['mapSource', 'mapDateType'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
            }
        }
    });	
    
    GLOBALS.stores = {
        mapView: mapViewStore,
        indicatorGroup: indicatorGroupStore,
        indicatorsByGroup: indicatorsByGroupStore,
        indicator: indicatorStore,
        dataElementGroup: dataElementGroupStore,
        dataElementsByGroup: dataElementsByGroupStore,
        dataElement: dataElementStore,
        periodType: periodTypeStore,
        periodsByTypeStore: periodsByTypeStore,
        map: mapStore,
        predefinedMapLegend: predefinedMapLegendStore,
        predefinedMapLegendSet: predefinedMapLegendSetStore,
        organisationUnitLevel: organisationUnitLevelStore,
        organisationUnitsAtLevel: organisationUnitsAtLevelStore,
        geojsonFiles: geojsonFilesStore,
        nameColumn: nameColumnStore,
        wmsCapabilities: wmsCapabilitiesStore,
        baseLayer: baseLayerStore,
        overlay: overlayStore
    };
	
	/* Add base layers */	
	function addBaseLayersToMap() {
		GLOBALS.vars.map.addLayers([new OpenLayers.Layer.WMS('World', 'http://labs.metacarta.com/wms/vmap0', {layers: 'basic'})]);
		GLOBALS.vars.map.layers[0].setVisibility(false);
		
		GLOBALS.stores.baseLayer.load({callback: function(r) {
			if (r.length) {
				for (var i = 0; i < r.length; i++) {
					GLOBALS.vars.map.addLayers([new OpenLayers.Layer.WMS(r[i].data.name, r[i].data.mapSource, {layers: r[i].data.layer})]);
					GLOBALS.vars.map.layers[GLOBALS.vars.map.layers.length-1].setVisibility(false);
				}
			}
		}});
	}	
	addBaseLayersToMap();	
    
	function addOverlaysToMap() {
		GLOBALS.stores.overlay.load({callback: function(r) {
			if (r.length) {
				for (var i = 0; i < r.length; i++) {
					var url = GLOBALS.vars.mapSourceType.isShapefile() ? GLOBALS.conf.path_geoserver + GLOBALS.conf.wfs + r[i].data.mapSource + GLOBALS.conf.output : GLOBALS.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + r[i].data.mapSource;
					var fillColor = r[i].data.fillColor;
					var fillOpacity = parseFloat(r[i].data.fillOpacity);
					var strokeColor = r[i].data.strokeColor;
					var strokeWidth = parseFloat(r[i].data.strokeWidth);
					
					var overlay = new OpenLayers.Layer.Vector(r[i].data.name, {
						'visibility': false,
						'styleMap': new OpenLayers.StyleMap({
							'default': new OpenLayers.Style(
								OpenLayers.Util.applyDefaults(
									{'fillColor': fillColor, 'fillOpacity': fillOpacity, 'strokeColor': strokeColor, 'strokeWidth': strokeWidth},
									OpenLayers.Feature.Vector.style['default']
								)
							)
						}),
						'strategies': [new OpenLayers.Strategy.Fixed()],
						'protocol': new OpenLayers.Protocol.HTTP({
							'url': url,
							'format': new OpenLayers.Format.GeoJSON()
						})
					});
					
					overlay.events.register('loadstart', null, function() {
						GLOBALS.vars.mask.msg = i18n_loading;
						GLOBALS.vars.mask.show();
					});
					
					overlay.events.register('loadend', null, function() {
						GLOBALS.vars.mask.hide();
					});
                    
                    overlay.isOverlay = true;
						
					GLOBALS.vars.map.addLayer(overlay);
				}
			}
		}});
	}
	addOverlaysToMap();
			
	/* Section: mapview */
	var viewNameTextField=new Ext.form.TextField({id:'viewname_tf',emptytext:'',width:GLOBALS.conf.combo_width,hideLabel:true,autoCreate:{tag:'input',type:'text',size:'20',autocomplete:'off', maxlength:'35'}});
	var deleteMapViewComboBox=new Ext.form.ComboBox({id:'view_cb',isFormField:true,hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:GLOBALS.conf.emptytext,selectOnFocus:true,width:GLOBALS.conf.combo_width,minListWidth:GLOBALS.conf.combo_width,store:GLOBALS.stores.mapView});
	var dashboardMapViewComboBox=new Ext.form.ComboBox({id:'view2_cb',isFormField:true,hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:GLOBALS.conf.emptytext,selectOnFocus:true,width:GLOBALS.conf.combo_width,minListWidth:GLOBALS.conf.combo_width,store:GLOBALS.stores.mapView});
    
    var newViewPanel = new Ext.form.FormPanel({
        id: 'newview_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [
            {html: '<div class="window-info">' + i18n_saving_current_thematic_map_selection + '</div>'},
            {html: '<div class="window-field-label-first">' + i18n_display_name + '</div>'},
			viewNameTextField,
			{
				xtype: 'button',
                id: 'newview_b',
				isFormField: true,
				hideLabel: true,
				cls: 'window-button',
				text: i18n_save,
				handler: function() {
					var vn = Ext.getCmp('viewname_tf').getValue();
                    
                    if (!vn) {
						Ext.message.msg(false, i18n_form_is_not_complete);
						return;
					}
                    
                    var formValues;
                    
                    if (GLOBALS.vars.activePanel.isPolygon()) {
                        if (!choropleth.validateForm(true)) {
                            return;
                        }
                        formValues = choropleth.getFormValues();
                    }
                    else if (GLOBALS.vars.activePanel.isPoint()) {
                        if (!proportionalSymbol.validateForm(true)) {
                            return;
                        }
                        formValues = proportionalSymbol.getFormValues();
                    }
                    
                    if (GLOBALS.stores.mapView.find('name', vn) !== -1) {
                        Ext.message.msg(false, i18n_there_is_already_a_map_view_called + ' <span class="x-msg-hl">' + vn + '</span>');
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + 'addOrUpdateMapView' + GLOBALS.conf.type,
                        method: 'POST',
                        params: {
                            name: vn,
                            mapValueType: formValues.mapValueType,
                            indicatorGroupId: formValues.indicatorGroupId,
                            indicatorId: formValues.indicatorId,
                            dataElementGroupId: formValues.dataElementGroupId,
                            dataElementId: formValues.dataElementId,
                            periodTypeId: formValues.periodTypeId,
                            periodId: formValues.periodId,
                            startDate: formValues.startDate,
                            endDate: formValues.endDate,
                            organisationUnitSelectionType: formValues.organisationUnitSelectionType,
                            mapSource:formValues.mapSource,
                            mapLegendType: formValues.mapLegendType,
                            method: formValues.method,
                            classes: formValues.classes,
                            bounds: formValues.bounds,
                            colorLow: formValues.colorLow,
                            colorHigh: formValues.colorHigh,
                            mapLegendSetId: formValues.mapLegendSetId,
                            longitude: formValues.longitude,
                            latitude: formValues.latitude,
                            zoom: formValues.zoom
                        },
                        success: function(r) {
                            Ext.message.msg(true, 'The view <span class="x-msg-hl">' + vn + '</span> ' + i18n_was_registered);
                            GLOBALS.stores.mapView.load();
                            Ext.getCmp('viewname_tf').reset();
                        }
                    });
				}
			}
        ]
    });
    
    var deleteViewPanel = new Ext.form.FormPanel({   
        id: 'deleteview_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [
            { html: '<div class="window-field-label-first">' + i18n_view + '</div>' },
			deleteMapViewComboBox,
			{
				xtype: 'button',
                id: 'deleteview_b',
				isFormField: true,
				hideLabel: true,
				text: i18n_delete,
				cls: 'window-button',
				handler: function() {
					var v = Ext.getCmp('view_cb').getValue();
					
                    if (!v) {
						Ext.message.msg(false, i18n_please_select_a_map_view);
						return;
					}
                    
					var name = GLOBALS.stores.mapView.getById(v).get('name');				
					
					Ext.Ajax.request({
						url: GLOBALS.conf.path_mapping + 'deleteMapView' + GLOBALS.conf.type,
						method: 'POST',
						params: {id:v},
						success: function(r) {
							Ext.message.msg(true, 'The map view <span class="x-msg-hl">' + name + '</span> '+ i18n_was_deleted);
                            GLOBALS.stores.mapView.load();
                            if (v == Ext.getCmp('mapview_cb').getValue()) {
                                Ext.getCmp('mapview_cb').clearValue();
                            }
                            if (v == Ext.getCmp('mapview_cb2').getValue()) {
                                Ext.getCmp('mapview_cb2').clearValue();
                            }
						}
					});
				}
			}
        ]
    });
    
    var dashboardViewPanel = new Ext.form.FormPanel({   
        id: 'dashboardview_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [   
            { html: '<div class="window-field-label-first">'+i18n_view+'</div>' },
			dashboardMapViewComboBox,
			{
				xtype: 'button',
                id: 'dashboardview_b',
				isFormField: true,
				hideLabel: true,
				text: 'Add to DHIS dashboard',
				cls: 'window-button',
				handler: function() {
					var v = Ext.getCmp('view2_cb').getValue();
					var rv = Ext.getCmp('view2_cb').getRawValue();
					
					if (!v) {
						Ext.message.msg(false, i18n_please_select_a_map_view);
						return;
					}
					
					Ext.Ajax.request({
						url: GLOBALS.conf.path_mapping + 'addMapViewToDashboard' + GLOBALS.conf.type,
						method: 'POST',
						params: {id:v},
						success: function(r) {
							Ext.message.msg(true, i18n_the_view + ' <span class="x-msg-hl">' + rv + '</span> ' + i18n_was_added_to_dashboard);
						}
					});
				}
			}
        ]
    });
    
	var viewWindow = new Ext.Window({
        id: 'view_w',
        title: '<span id="window-favorites-title">' + i18n_favorite + '</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: 234,
        items:
        [
            {
                xtype: 'tabpanel',
                activeTab: 0,
				layoutOnTabChange: true,
                deferredRender: false,
                plain: true,
                defaults: {
                    layout: 'fit',
                    bodyStyle: 'padding:8px; border:0px'
                },
                listeners: {
                    tabchange: function(panel, tab)
                    {
                        if (tab.id == 'view0') { 
                            viewWindow.setHeight(188);
                        }
                        else if (tab.id == 'view1') {
                            viewWindow.setHeight(150);
                        }
                        else if (tab.id == 'view2') {
                            viewWindow.setHeight(150);
                        }
                    }
                },
                items: [
                    {
                        title: '<span class="panel-tab-title">' + i18n_new + '</span>',
                        id: 'view0',
                        items: [newViewPanel]
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_delete + '</span>',
                        id: 'view1',
                        items: [deleteViewPanel]
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_add_to_dashboard + '</span>',
                        id: 'view2',
                        items: [dashboardViewPanel]
                    }
                ]
            }
        ]
    });
	
	/* Section: export map */
	var exportImagePanel = new Ext.form.FormPanel({
        id: 'export_image_p',        
        items:
        [
			{
				xtype: 'textfield',
				id: 'exportimagetitle_tf',
				fieldLabel: i18n_title,
				labelSeparator: GLOBALS.conf.labelseparator,
				editable: true,
				valueField: 'id',
				displayField: 'text',
				isFormField: true,
				width: GLOBALS.conf.combo_width_fieldset,
				mode: 'local',
				triggerAction: 'all'
			},
			{
				xtype: 'combo',
				id: 'exportimagequality_cb',
				fieldLabel: i18n_image_resolution,
                fieldLabel: 'Image resolution',
				labelSeparator: GLOBALS.conf.labelseparator,
				editable: false,
				valueField: 'id',
				displayField: 'text',
				isFormField: true,
				width: GLOBALS.conf.combo_width_fieldset,
				minListWidth: GLOBALS.conf.combo_width_fieldset,
				mode: 'local',
				triggerAction: 'all',
				value: 1,
				store: new Ext.data.ArrayStore({
					fields: ['id', 'text'],
					data: [[1, i18n_medium], [2, i18n_large]]
				})					
			},
			{
				xtype: 'checkbox',
				id: 'exportimageincludelegend_chb',
				fieldLabel: i18n_include_legend,
				labelSeparator: '',				
				isFormField: true,
				checked: true
			},
			{
				xtype: 'button',
                id: 'exportimage_b',
				isFormField: true,
				labelSeparator: GLOBALS.conf.labelseparator,
				hideLabel: false,
				cls: 'window-button',
				text: i18n_export_image,
				handler: function() {
                    var vcb, dcb, mcb, lcb, period;
                    if (GLOBALS.vars.activePanel.isPolygon()) {
                        vcb = Ext.getCmp('mapvaluetype_cb').getValue() == GLOBALS.conf.map_value_type_indicator ? Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue();
                        dcb = GLOBALS.vars.mapDateType.isFixed() ? Ext.getCmp('period_cb').getValue() : Ext.getCmp('startdate_df').getValue() && Ext.getCmp('startdate_df').getValue() ? true : false;
                        period = GLOBALS.vars.mapDateType.isFixed() ? Ext.getCmp('period_cb').getRawValue() : new Date(Ext.getCmp('startdate_df').getRawValue()).format('Y M j') + ' - ' + new Date(Ext.getCmp('enddate_df').getRawValue()).format('Y M j');
                        mcb = GLOBALS.vars.mapSourceType.isDatabase() ? Ext.getCmp('map_tf').getValue() : Ext.getCmp('map_cb').getValue();
                        lcb = Ext.getCmp('maplegendtype_cb').getValue() == GLOBALS.conf.map_legend_type_automatic ? true : Ext.getCmp('maplegendset_cb').getValue() ? true : false;
                    }
                    else if (GLOBALS.vars.activePanel.isPoint()) {
                        Ext.message.msg(false, 'Please use <span class="x-msg-hl">polygon layer</span> for printing');
                        return;
                    }
                    else {
                        Ext.message.msg(false, i18n_please_expand_layer_panel);
                        return;
                    }
                    
                    if (vcb && dcb && mcb && lcb) {
						var svgElement = document.getElementsByTagName('svg')[0];
						var parentSvgElement = svgElement.parentNode;
						
						var svg = parentSvgElement.innerHTML;
						
                        var viewBox = svgElement.getAttribute('viewBox');
                        var title = Ext.getCmp('exportimagetitle_tf').getValue();
                    	
                        if (!title) {
                            Ext.message.msg(false, i18n_please_enter_map_title);
                        }
                        else {
                            var q = Ext.getCmp('exportimagequality_cb').getValue();
                            var w = svgElement.getAttribute('width') * q;
                            var h = svgElement.getAttribute('height') * q;
                            var includeLegend = Ext.getCmp('exportimageincludelegend_chb').getValue();
                            
                            Ext.getCmp('exportimagetitle_tf').reset();
                            
                            var exportForm = document.getElementById('exportForm');
                            exportForm.action = '../exportImage.action';
                            exportForm.target = '_blank';
                            
                            document.getElementById('titleField').value = title;   
                            document.getElementById('viewBoxField').value = viewBox;  
                            document.getElementById('svgField').value = svg;  
                            document.getElementById('widthField').value = w;  
                            document.getElementById('heightField').value = h;  
                            document.getElementById('includeLegendsField').value = includeLegend;  
                            document.getElementById('periodField').value = period;  
                            document.getElementById('indicatorField').value = vcb;
                            document.getElementById('legendsField').value = GLOBALS.util.getLegendsJSON();

                            exportForm.submit();
                        }
                    }
                    else {
                        Ext.message.msg(false, i18n_please_render_map_fist);
                    }
				}
			}	
		]
	});
	
	var exportExcelPanel = new Ext.form.FormPanel({
        id: 'export_excel_p',        
        items:
        [
			{
				xtype: 'textfield',
				id: 'exportexceltitle_ft',
				fieldLabel: i18n_title,
				labelSeparator: GLOBALS.conf.labelseparator,
				editable: true,
				valueField: 'id',
				displayField: 'text',
				isFormField: true,
				width: GLOBALS.conf.combo_width_fieldset,
				minListWidth: GLOBALS.conf.combo_list_width_fieldset,
				mode: 'local',
				triggerAction: 'all'
			},	
			{
				xtype: 'checkbox',
				id: 'exportexcelincludelegend_chb',
				fieldLabel: i18n_include_legend,
				labelSeparator: '',
				isFormField: true,
				checked: true
			},	
			{
				xtype: 'checkbox',
				id: 'exportexcelincludevalue_chb',
				fieldLabel: i18n_include_values,
				labelSeparator: '',
				isFormField: true,
				checked: true
			},
			{
				xtype: 'button',
                id: 'exportexcel_b',
				isFormField: true,
				labelSeparator: GLOBALS.conf.labelseparator,
				hideLabel: false,
				cls: 'window-button',
				text: i18n_export_excel,
				handler: function() {
                    var indicatorOrDataElement, period, mapOrOrganisationUnit;
					if (GLOBALS.vars.activePanel.isPolygon()) {
                        indicatorOrDataElement = Ext.getCmp('mapvaluetype_cb').getValue() == GLOBALS.conf.map_value_type_indicator ?
                            Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue();
                        period = Ext.getCmp('period_cb').getValue();
                        mapOrOrganisationUnit = GLOBALS.vars.mapSourceType.isDatabase() ?
                            Ext.getCmp('map_tf').getValue() : Ext.getCmp('map_cb').getValue();
                    }
                    else if (GLOBALS.vars.activePanel.isPoint()) {
                        indicatorOrDataElement = Ext.getCmp('mapvaluetype_cb2').getValue() == GLOBALS.conf.map_value_type_indicator ?
                            Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue();
                        period = Ext.getCmp('period_cb2').getValue();
                        mapOrOrganisationUnit = GLOBALS.vars.mapSourceType.isDatabase() ?
                            Ext.getCmp('map_tf2').getValue() : Ext.getCmp('map_cb2').getValue();
                    }
                    
                    if (indicatorOrDataElement && period && mapOrOrganisationUnit) {
                        var title = Ext.getCmp('exportexceltitle_ft').getValue();
                        var svg = document.getElementById('OpenLayers.Layer.Vector_17').innerHTML;	
                        var includeLegend = Ext.getCmp('exportexcelincludelegend_chb').getValue();
                        var includeValues = Ext.getCmp('exportexcelincludevalue_chb').getValue();
                        var period = Ext.getCmp('period_cb').getValue();
                        var indicator = Ext.getCmp('indicator_cb').getValue();
                        
                        Ext.getCmp('exportexceltitle_ft').clearValue();
                                            
                        var exportForm = document.getElementById('exportForm');
                        exportForm.action = '../exportExcel.action';
                        
                        document.getElementById('titleField').value = title;
                        document.getElementById('svgField').value = svg;  
                        document.getElementById('widthField').value = 500;  
                        document.getElementById('heightField').value = 500;  
                        document.getElementById('includeLegendsField').value = includeLegend;  
                        document.getElementById('includeValuesField').value = includeValues; 
                        document.getElementById('periodField').value = period;  
                        document.getElementById('indicatorField').value = indicator;   
                        document.getElementById('legendsField').value = GLOBALS.util.getLegendsJSON();
                        document.getElementById('dataValuesField').value = GLOBALS.vars.exportValues;

                        exportForm.submit();
                    }
                    else {
                        Ext.message.msg(false, i18n_please_render_map_fist );
                    }
				}
			}	
		]
	});
	
	var exportImageWindow=new Ext.Window({id:'exportimage_w',title:'<span id="window-image-title">' + i18n_export_map_as_image + '</span>',layout:'fit',closeAction:'hide',defaults:{layout:'fit',bodyStyle:'padding:8px; border:0px'},width:250,height:158,items:[{xtype:'panel',items:[exportImagePanel]}]});
	var exportExcelWindow=new Ext.Window({id:'exportexcel_w',title:'<span id="window-excel-title">' + i18n_export_excel + '</span>',layout:'fit',closeAction:'hide',defaults:{layout:'fit',bodyStyle:'padding:8px; border:0px'},width:260,height:157,items:[{xtype:'panel',items:[exportExcelPanel]}]});
	
	/* Section: predefined map legend set */
	var newPredefinedMapLegendPanel = new Ext.form.FormPanel({
        id: 'newpredefinedmaplegend_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [   
            { html: '<div class="window-field-label-first">'+i18n_display_name+'</div>' },
            new Ext.form.TextField({id:'predefinedmaplegendname_tf',isFormField:true,hideLabel:true,emptyText:GLOBALS.conf.emptytext,width:GLOBALS.conf.combo_width}),
            { html: '<div class="window-field-label">'+i18n_start_value+'</div>' },
            new Ext.form.TextField({id:'predefinedmaplegendstartvalue_tf',isFormField:true,hideLabel:true,emptyText:GLOBALS.conf.emptytext,width:GLOBALS.conf.combo_number_width,minListWidth:GLOBALS.conf.combo_number_width}),
            { html: '<div class="window-field-label">'+i18n_end_value+'</div>' },
            new Ext.form.TextField({id:'predefinedmaplegendendvalue_tf',isFormField:true,hideLabel:true,emptyText:GLOBALS.conf.emptytext,width:GLOBALS.conf.combo_number_width,minListWidth:GLOBALS.conf.combo_number_width}),
            { html: '<div class="window-field-label">'+i18n_color+'</div>' },
            new Ext.ux.ColorField({id:'predefinedmaplegendcolor_cp',isFormField:true,hideLabel:true,allowBlank:false,width:GLOBALS.conf.combo_width,minListWidth:GLOBALS.conf.combo_width,value:"#FFFF00"}),
            {
                xtype: 'button',
                id: 'newpredefinedmaplegend_b',
				isFormField: true,
				hideLabel: true,
                text: i18n_save,
				cls: 'window-button',
                handler: function() {
                    var mln = Ext.getCmp('predefinedmaplegendname_tf').getValue();
					var mlsv = Ext.getCmp('predefinedmaplegendstartvalue_tf').getValue();
					var mlev = Ext.getCmp('predefinedmaplegendendvalue_tf').getValue();
                    var mlc = Ext.getCmp('predefinedmaplegendcolor_cp').getValue();
					
					if (!mln || mlsv == "" || mlev == "" || !mlc) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                        return;
                    }
                    
                    if (!GLOBALS.util.validateInputNameLength(mln)) {
                        Ext.message.msg(false, i18n_name_can_not_longer_than_25);
                        return;
                    }
                    
                    if (GLOBALS.stores.predefinedMapLegend.find('name', mln) !== -1) {
                        Ext.message.msg(false, i18n_legend + '<span class="x-msg-hl">' + mln + '</span> ' + i18n_already_exists);
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + 'addOrUpdateMapLegend' + GLOBALS.conf.type,
                        method: 'POST',
                        params: {name: mln, startValue: mlsv, endValue: mlev, color: mlc},
                        success: function(r) {
                            Ext.message.msg(true, i18n_legend + ' <span class="x-msg-hl">' + mln + '</span> ' + i18n_was_registered);
                            GLOBALS.stores.predefinedMapLegend.load();
                            Ext.getCmp('predefinedmaplegendname_tf').reset();
                            Ext.getCmp('predefinedmaplegendstartvalue_tf').reset();
                            Ext.getCmp('predefinedmaplegendendvalue_tf').reset();
                            Ext.getCmp('predefinedmaplegendcolor_cp').reset();
                        }
                    });
                }
            }
        ]	
    });
	
	var deletePredefinedMapLegendPanel = new Ext.form.FormPanel({
        id: 'deletepredefinedmaplegend_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [   
            { html: '<div class="window-field-label-first">' + i18n_legend + '</p>' },
            {
                xtype: 'combo',
                id: 'predefinedmaplegend_cb',
                isFormField: true,
                hideLabel: true,
                typeAhead: true,
                editable: false,
                valueField: 'id',
                displayField: 'name',
                mode: 'remote',
                forceSelection: true,
                triggerAction: 'all',
                emptyText: GLOBALS.conf.emptytext,
                selectOnFocus: true,
                width: GLOBALS.conf.combo_width,
                minListWidth: GLOBALS.conf.combo_width,
                store: GLOBALS.stores.predefinedMapLegend
            },
            {
                xtype: 'button',
                id: 'deletepredefinedmaplegend_b',
                text: i18n_delete,
				cls: 'window-button',
                handler: function() {
                    var mlv = Ext.getCmp('predefinedmaplegend_cb').getValue();
                    var mlrv = Ext.getCmp('predefinedmaplegend_cb').getRawValue();
                    
                    if (!mlv) {
                        Ext.message.msg(false, i18n_please_select_a_legend);
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + 'deleteMapLegend' + GLOBALS.conf.type,
                        method: 'POST',
                        params: {id: mlv},
                        success: function(r) {
                            Ext.message.msg(true, i18n_legend + ' <span class="x-msg-hl">' + mlrv + '</span> ' + i18n_was_deleted);
                            GLOBALS.stores.predefinedMapLegend.load();
                            Ext.getCmp('predefinedmaplegend_cb').clearValue();
                        }
                    });
                }
            }
        ]
    });
	
	var newPredefinedMapLegendSetPanel = new Ext.form.FormPanel({   
        id: 'newpredefinedmaplegendset_p',
		bodyStyle: 'border:0px',
        items:
        [   
            { html: '<div class="window-field-label-first">'+i18n_display_name+'</div>' },
            new Ext.form.TextField({id:'predefinedmaplegendsetname_tf',hideLabel:true,emptyText:GLOBALS.conf.emptytext,width:GLOBALS.conf.combo_width}),
            { html: '<div class="window-field-label">'+i18n_legends+'</div>' },
			new Ext.ux.Multiselect({id:'predefinednewmaplegend_ms',hideLabel:true,dataFields:['id','name','startValue','endValue','color','displayString'],valueField:'id',displayField:'displayString',width:GLOBALS.conf.multiselect_width,height:GLOBALS.util.getMultiSelectHeight(),store:GLOBALS.stores.predefinedMapLegend}),
            {
                xtype: 'button',
                id: 'newpredefinedmaplegendset_b',
                text: i18n_save,
				cls: 'window-button',
                handler: function() {
                    var mlsv = Ext.getCmp('predefinedmaplegendsetname_tf').getValue();
                    var mlms = Ext.getCmp('predefinednewmaplegend_ms').getValue();
					var array = new Array();
					
					if (mlms) {
						array = mlms.split(',');
						if (array.length > 1) {
							for (var i = 0; i < array.length; i++) {
								var sv = GLOBALS.stores.predefinedMapLegend.getById(array[i]).get('startValue');
								var ev = GLOBALS.stores.predefinedMapLegend.getById(array[i]).get('endValue');
								for (var j = 0; j < array.length; j++) {
									if (j != i) {
										var temp_sv = GLOBALS.stores.predefinedMapLegend.getById(array[j]).get('startValue');
										var temp_ev = GLOBALS.stores.predefinedMapLegend.getById(array[j]).get('endValue');
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
						Ext.message.msg(false, i18n_please_select_at_least_one_legends);
                        return;
					}
					
                    if (!mlsv) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                        return;
                    }
                    
                    array = mlms.split(',');
                    var params = '?mapLegends=' + array[0];
                    if (array.length > 1) {
                        for (var i = 1; i < array.length; i++) {
                            array[i] = '&mapLegends=' + array[i];
                            params += array[i];
                        }
                    }
                    
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + 'addOrUpdateMapLegendSet.action' + params,
                        method: 'POST',
                        params: {name: mlsv, type: GLOBALS.conf.map_legend_type_predefined},
                        success: function(r) {
                            Ext.message.msg(true, i18n_new_legend_set+' <span class="x-msg-hl">' + mlsv + '</span> ' + i18n_was_registered);
                            GLOBALS.stores.predefinedMapLegendSet.load();
							Ext.getCmp('predefinedmaplegendsetname_tf').reset();
							Ext.getCmp('predefinednewmaplegend_ms').reset();							
                        }
                    });
                }
            }
        ]
    });
	
	var deletePredefinedMapLegendSetPanel = new Ext.form.FormPanel({
        id: 'deletepredefinedmaplegendset_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [   
            { html: '<div class="window-field-label-first">' + i18n_legend_set + '</p>' },
            new Ext.form.ComboBox({id:'predefinedmaplegendsetindicator_cb',hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:GLOBALS.conf.emptytext,selectOnFocus:true,width:GLOBALS.conf.combo_width,minListWidth:GLOBALS.conf.combo_width,store:GLOBALS.stores.predefinedMapLegendSet}),
            {
                xtype: 'button',
                id: 'deletepredefinedmaplegendset_b',
                text: i18n_delete,
				cls: 'window-button',
                handler: function() {
                    var mlsv = Ext.getCmp('predefinedmaplegendsetindicator_cb').getValue();
                    var mlsrv = Ext.getCmp('predefinedmaplegendsetindicator_cb').getRawValue();
                    
                    if (!mlsv) {
                        Ext.message.msg(false, i18n_please_select_a_legend_set);
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + 'deleteMapLegendSet' + GLOBALS.conf.type,
                        method: 'POST',
                        params: {id: mlsv},
                        success: function(r) {
                            Ext.message.msg(true, i18n_legend_set + ' <span class="x-msg-hl">' + mlsrv + '</span> ' + i18n_was_deleted);
                            GLOBALS.stores.predefinedMapLegendSet.load();
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
    });
    
    var assignPredefinedMapLegendSetIndicatorPanel = new Ext.form.FormPanel({
        id: 'assignpredefinedmaplegendsetindicator_p',
		bodyStyle: 'border:0px',
        items:
        [
            { html: '<div class="window-field-label-first">' + i18n_legend_set + '</div>' },
            new Ext.form.ComboBox({
                id: 'predefinedmaplegendsetindicator2_cb',
                hideLabel: true,
                typeAhead: true,
                editable: false,
                valueField: 'id',
                displayField: 'name',
                mode: 'remote',
                forceSelection: true,
                triggerAction: 'all',
                emptyText: GLOBALS.conf.emptytext,
                selectOnFocus: true,
                width: GLOBALS.conf.combo_width,
                minListWidth: GLOBALS.conf.combo_width,
                store: GLOBALS.stores.predefinedMapLegendSet,
                listeners: {
                    'select': {
                        fn: function(cb, record, i) {
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
            }),
            { html: '<div class="window-field-label">' + i18n_indicator + '</div>' },
			new Ext.ux.Multiselect({id:'predefinedmaplegendsetindicator_ms',hideLabel:true,dataFields:['id','name','shortName'],valueField:'id',displayField:'shortName',width:GLOBALS.conf.multiselect_width,height:GLOBALS.util.getMultiSelectHeight(),store:GLOBALS.stores.indicator}),
            {
                xtype: 'button',
                id: 'assignpredefinedmaplegendsetindicator_b',
                text: i18n_assign_to_indicator,
				cls: 'window-button',
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
                    
                    var array = new Array();
                    array = lims.split(',');
                    var params = '?indicators=' + array[0];
                    
                    if (array.length > 1) {
                        for (var i = 1; i < array.length; i++) {
                            array[i] = '&indicators=' + array[i];
                            params += array[i];
                        }
                    }
                    
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + 'assignIndicatorsToMapLegendSet.action' + params,
                        method: 'POST',
                        params: {id: ls},
                        success: function(r) {
                            Ext.message.msg(true, i18n_legend_set+' <span class="x-msg-hl">' + lsrw + '</span> ' + i18n_was_updated);
                            GLOBALS.stores.predefinedMapLegendSet.load();
                        }
                    });
                }
            }
        ]
    });
    
    var assignPredefinedMapLegendSetDataElementPanel = new Ext.form.FormPanel({
        id: 'assignpredefinedmaplegendsetdataelement_p',
		bodyStyle: 'border:0px',
        items:
        [
            { html: '<div class="window-field-label-first">'+i18n_legend_set+'</div>' },
            new Ext.form.ComboBox({
                id: 'predefinedmaplegendsetdataelement_cb',
                isFormField: true,
                hideLabel: true,
                typeAhead: true,
                editable: false,
                valueField: 'id',
                displayField: 'name',
                mode: 'remote',
                forceSelection: true,
                triggerAction: 'all',
                emptyText: GLOBALS.conf.emptytext,
                selectOnFocus: true,
                width: GLOBALS.conf.combo_width,
                minListWidth: GLOBALS.conf.combo_width,
                store: GLOBALS.stores.predefinedMapLegendSet,
                listeners:{
                    'select': {
                        fn: function(cb, record, i) {
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
            }),
            { html: '<div class="window-field-label">' + i18n_dataelement + '</div>' },
			new Ext.ux.Multiselect({id:'predefinedmaplegendsetdataelement_ms',hideLabel:true,dataFields:['id','name','shortName'],valueField:'id',displayField:'shortName',width:GLOBALS.conf.multiselect_width,height:GLOBALS.util.getMultiSelectHeight(),store:GLOBALS.stores.dataElement}),
            {
                xtype: 'button',
                id: 'assignpredefinedmaplegendsetdataelement_b',
                text: i18n_assign_to_dataelement,
				cls: 'window-button',
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
                    
                    var array = new Array();
                    array = lims.split(',');
                    var params = '?dataElements=' + array[0];
                    
                    if (array.length > 1) {
                        for (var i = 1; i < array.length; i++) {
                            array[i] = '&dataElements=' + array[i];
                            params += array[i];
                        }
                    }
                    
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + 'assignDataElementsToMapLegendSet.action' + params,
                        method: 'POST',
                        params: {id: ls},
                        success: function(r) {
                            Ext.message.msg(true, i18n_legend_set+' <span class="x-msg-hl">' + lsrw + '</span> ' + i18n_was_updated);
                            GLOBALS.stores.predefinedMapLegendSet.load();
                        }
                    });
                }
            }
        ]
    });
	
	var predefinedMapLegendSetWindow = new Ext.Window({
        id: 'predefinedmaplegendset_w',
        title: '<span id="window-predefinedlegendset-title">'+i18n_predefined_legend_sets+'</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: 592,
        items:
        [
			{
				xtype: 'tabpanel',
				activeTab: 0,
				layoutOnTabChange: true,
				deferredRender: false,
				plain: true,
				defaults: {layout: 'fit', bodyStyle: 'padding:8px; border:0px'},
				listeners: {
					tabchange: function(panel, tab)
					{
						var w = Ext.getCmp('predefinedmaplegendset_w');
						
						if (tab.id == 'predefinedmaplegendset0') { 
							w.setHeight(298);
						}
						else if (tab.id == 'predefinedmaplegendset1') {
							w.setHeight(151);
						}
						else if (tab.id == 'predefinedmaplegendset2') {
							w.setHeight(GLOBALS.util.getMultiSelectHeight() + 180);
						}
						else if (tab.id == 'predefinedmaplegendset3') {
							w.setHeight(151);
						}
                        else if (tab.id == 'predefinedmaplegendset4') {
                            w.setHeight(GLOBALS.util.getMultiSelectHeight() + 180);
                        }
                        else if (tab.id == 'predefinedmaplegendset5') {
                            w.setHeight(GLOBALS.util.getMultiSelectHeight() + 180);
                        }
					}
				},
				items:
				[
					{
						title: '<span class="panel-tab-title">'+i18n_new_legend+'</span>',
						id: 'predefinedmaplegendset0',
						items: [newPredefinedMapLegendPanel]
					},
					{
						title: '<span class="panel-tab-title">'+i18n_delete+'</span>',
						id: 'predefinedmaplegendset1',
						items: [deletePredefinedMapLegendPanel]
					},
					{
						title: '<span class="panel-tab-title">'+i18n_new_legend_set+'</span>',
						id: 'predefinedmaplegendset2',
						items: [newPredefinedMapLegendSetPanel]
					},
					{
						title: '<span class="panel-tab-title">'+i18n_delete+'</span>',
						id: 'predefinedmaplegendset3',
						items: [deletePredefinedMapLegendSetPanel]
					},
					{
                        title: '<span class="panel-tab-title">'+i18n_assign_to_indicator+'</span>',
						id: 'predefinedmaplegendset4',
						items: [assignPredefinedMapLegendSetIndicatorPanel]
					},
					{
                        title: '<span class="panel-tab-title">'+i18n_assign_to_dataelement+'</span>',
						id: 'predefinedmaplegendset5',
						items: [assignPredefinedMapLegendSetDataElementPanel]
					}
				]
			}
        ]
    });
	
    /* Section: help */
	function getHelpText(topic, tab) {
		Ext.Ajax.request({
			url: '../../dhis-web-commons-about/getHelpContent.action',
			method: 'POST',
			params: {id: topic},
			success: function(r) {
				Ext.getCmp(tab).body.update('<div id="help">' + r.responseText + '</div>');
			}
		});
	}
    
	var helpWindow = new Ext.Window({
        id: 'help_w',
        title: '<span id="window-help-title">'+i18n_help+'</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: 629,
		height: 430, 
        items:
        [
            {
                xtype: 'tabpanel',
                activeTab: 0,
				layoutOnTabChange: true,
                deferredRender: false,
                plain: true,
                defaults: {layout: 'fit'},
                listeners: {
                    tabchange: function(panel, tab)
                    {
                        if (tab.id == 'help0') {
							getHelpText(GLOBALS.conf.thematicMap, tab.id);
                        }
                        else if (tab.id == 'help1') {
							getHelpText(GLOBALS.conf.mapRegistration, tab.id);
                        }
                        else if (tab.id == 'help2') {
                            getHelpText(GLOBALS.conf.organisationUnitAssignment, tab.id);
                        }
						if (tab.id == 'help3') { 
                            getHelpText(GLOBALS.conf.overlayRegistration, tab.id);
                        }
                        else if (tab.id == 'help4') {
                            getHelpText(GLOBALS.conf.administration, tab.id);
                        }
                        else if (tab.id == 'help5') {
                            getHelpText(GLOBALS.conf.favorites, tab.id);
                        }
						else if (tab.id == 'help6') {
                            getHelpText(GLOBALS.conf.legendSets, tab.id);
                        }
						else if (tab.id == 'help7') {
                            getHelpText(GLOBALS.conf.pdfprint, tab.id);
                        }
                    }
                },
                items:
                [
                    {
                        title: '<span class="panel-tab-title">' + i18n_thematic_map + '</span>',
                        id: 'help0'
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_map + '</span>',
                        id: 'help1'
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_assignment + '</span>',
                        id: 'help2'
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_overlays + '</span>',
                        id: 'help3'
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_administrator + '</span>',
                        id: 'help4'
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_favorite + '</span>',
                        id: 'help5'
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_legend_set + '</span>',
                        id: 'help6'
                    },
                    {
                        title: '<span class="panel-tab-title">PDF print</span>',
                        id: 'help7'
                    }
                ]
            }
        ],
		listeners: {
			'hide': {
				fn: function() {
					mapping.relation = false;
				}
			}
		}
    });

    /* Section: register maps */
	var newNameTextField=new Ext.form.TextField({id:'newname_tf',emptyText:GLOBALS.conf.emptytext,hideLabel:true,width:GLOBALS.conf.combo_width});
    var editNameTextField=new Ext.form.TextField({id:'editname_tf',emptyText:GLOBALS.conf.emptytext,hideLabel:true,width:GLOBALS.conf.combo_width});
	var organisationUnitLevelComboBox=new Ext.form.ComboBox({id:'organisationunitlevel_cb',typeAhead:true,editable:false,valueField:'id',displayField:'name',emptyText:GLOBALS.conf.emptytext,hideLabel:true,mode:'remote',forceSelection:true,triggerAction:'all',selectOnFocus:true,width:GLOBALS.conf.combo_width,minListWidth:GLOBALS.conf.combo_width,store:GLOBALS.stores.organisationUnitLevel});
		
	var mapLayerPathComboBox = new Ext.form.ComboBox({
        id: 'maplayerpath_cb',
		typeAhead: true,
        editable: false,
        valueField: 'name',
        displayField: 'name',
		emptyText: GLOBALS.conf.emptytext,
		hideLabel: true,
        width: GLOBALS.conf.combo_width,
        minListWidth: GLOBALS.conf.combo_width,
        triggerAction: 'all',
        mode: 'remote',
        store: GLOBALS.stores.geojsonFiles,
		listeners: {
			'select': function(cb) {
                Ext.Ajax.request({
                    url: GLOBALS.conf.path_mapping + 'getGeoJsonFromFile' + GLOBALS.conf.type,
                    method: 'POST',
                    params: {name: cb.getValue()},
                    success: function(r) {
                        var file = Ext.util.JSON.decode(r.responseText);
                        var keys = [];
                        var data = [];

                        var nameList = GLOBALS.util.getKeys(file.features[0].properties);
                        for (var i = 0; i < nameList.length; i++) {
                            data.push(new Array(nameList[i]));
                        }
                        
                        GLOBALS.stores.nameColumn.loadData(data, false);
                    }
                });
			}
		}
    });
    
	var wmsGrid = new Ext.grid.GridPanel({
		id: 'wms_g',
		sm: new Ext.grid.RowSelectionModel({
			singleSelect: true
		}),
        columns: [
            {header: 'Title', dataIndex: 'title', sortable: true, width: 180},
            {header: 'Name', dataIndex: 'name', sortable: true, width: 180},
            {header: 'Queryable', dataIndex: 'queryable', sortable: true, width: 100},
            {header: 'Description', id: 'description_c', dataIndex: 'abstract'}
        ],
        autoExpandColumn: 'description_c',
        width: 700,
        height: screen.height * 0.6,
		store: GLOBALS.stores.wmsCapabilities,
        listeners: {
            'rowdblclick': mapPreview
        }
    });
    
    function mapPreview(grid, i) {
        var record = grid.getStore().getAt(i);
        var layer = record.get('layer').clone();
        
        var wmsPreviewWindow = new Ext.Window({
            title: '<span class="panel-title">Preview: ' + record.get("title") + '</span>',
            width: screen.width * 0.5,
            height: screen.height * 0.3,
            layout: 'fit',
            items: [{
                xtype: 'gx_mappanel',
                layers: [layer],
                extent: record.get('llbbox')
            }]
        });
        wmsPreviewWindow.show();
    }
	
	var wmsWindow = new Ext.Window({
		id: 'wms_w',
		title: '<span class="panel-title">'+i18n_geoserver_shapefiles+'</span>',
		closeAction: 'hide',
		width: wmsGrid.width,
		height: screen.height * 0.4,
		items: [wmsGrid],
		bbar: new Ext.Toolbar({
			id: 'wmswindow_sb',
			items:
			[
				{
					xtype: 'button',
					id: 'selectwms_b',
					text: 'Select',
					cls: 'aa_med',
					handler: function() {
						var selected = Ext.getCmp('wms_g').getSelectionModel().getSelected() || false;
                        if (selected) {
                            mapLayerPathWMSTextField.setValue(selected.get('name'));
                            wmsWindow.hide();
                            newNameColumnComboBox.focus();
                        }
					}
				}
			]
		})		
	});
	
	var mapLayerPathWMSTextField = new Ext.form.TextField({
		id: 'maplayerpathwms_tf',
		emptyText: GLOBALS.conf.emptytext,
		hideLabel: true,
        width: GLOBALS.conf.combo_width,
		listeners: {
			'focus': {
				fn: function() {
                    function show() {
                        var x = Ext.getCmp('center').x + 15;
                        var y = Ext.getCmp('center').y + 41;    
                        wmsWindow.show();
                        wmsWindow.setPosition(x,y);
                    }
                    
                    if (!GLOBALS.stores.wmsCapabilities.isLoaded) {
                        GLOBALS.stores.wmsCapabilities.load({callback: function() {
                            show();
                        }});
                    }
                    else {
                        show();
                    }
				}
			}
		}
	});

	var newNameColumnComboBox = new Ext.form.ComboBox({
        id: 'newnamecolumn_cb',
        editable: false,
        displayField: 'name',
        valueField: 'name',
		emptyText: GLOBALS.conf.emptytext,
		hideLabel: true,
        width: GLOBALS.conf.combo_width,
        minListWidth: GLOBALS.conf.combo_width,
        triggerAction: 'all',
        mode: 'local',
        store: GLOBALS.stores.nameColumn,
		listeners: {
			'focus': {
				fn: function() {
					var mlp = Ext.getCmp('maplayerpathwms_tf').getValue();
					
					if (GLOBALS.vars.mapSourceType.isShapefile() && mlp) {
						Ext.Ajax.request({
							url: GLOBALS.conf.path_geoserver + GLOBALS.conf.wfs + mlp + GLOBALS.conf.output,
							method: 'POST',
							success: function(r) {
								var file = Ext.util.JSON.decode(r.responseText);
								var keys = [];

								var nameList = GLOBALS.util.getKeys(file.features[0].properties);
								for (var i = 0; i < nameList.length; i++) {
									data.push(new Array(nameList[i]));
								}
								
								GLOBALS.stores.nameColumn.loadData(data, false);
							}
						});
					}
				}
			}
		}				
	});
	
	var editNameColumnComboBox=new Ext.form.ComboBox({id:'editnamecolumn_cb',editable:false,displayField:'name',valueField:'name',emptyText:GLOBALS.conf.emptytext,hideLabel:true,width:GLOBALS.conf.combo_width,minListWidth:GLOBALS.conf.combo_width,triggerAction:'all',mode:'local',store:GLOBALS.stores.nameColumn});
    
    var newMapButton = new Ext.Button({
        id: 'newmap_b',
        text: i18n_register_map,
		cls: 'aa_med',
        handler: function()
        {
            var ouli = Ext.getCmp('organisationunitlevel_cb').getValue();
            var nn = Ext.getCmp('newname_tf').getValue();
            var mlp = Ext.getCmp('maplayerpath_cb').getValue();
            var mlpwms = Ext.getCmp('maplayerpathwms_tf').getValue();					
            var nc = Ext.getCmp('newnamecolumn_cb').getValue();
             
            if (!nn || !ouli || !nc) {
                Ext.message.msg(false, i18n_form_is_not_complete);
                return;
            }
            else if (!mlp && !mlpwms) {
                Ext.message.msg(false, i18n_form_is_not_complete);
                return;
            }
            
            if (GLOBALS.util.validateInputNameLength(nn) == false) {
                Ext.message.msg(false, '<span class="x-msg-hl">' + i18n_map + ' ' + i18n_name_can_not_longer_than_25 + '</span>');
                return;
            }
            
            if (GLOBALS.stores.map.find('name', nn) !== -1) {
                Ext.message.msg(false, i18n_map + ' <span class="x-msg-hl">' + nn + ' </span>' + i18n_already_exists);
                return;
            }
            
            if (GLOBALS.stores.map.find('mapLayerPath', mlp) !== -1) {
                Ext.message.msg(false, i18n_the_source_file + ' <span class="x-msg-hl">' + mlp + '</span> ' + i18n_already_exists);
                return;
            }
                            
            var source = mlp || mlpwms;
            
            Ext.Ajax.request({
                url: GLOBALS.conf.path_mapping + 'addOrUpdateMap' + GLOBALS.conf.type,
                method: 'POST',
                params: {
                    name: nn,
                    mapLayerPath: source,
                    organisationUnitLevelId: ouli,
                    nameColumn: nc
                },
                success: function(r) {
                    Ext.message.msg(true, i18n_map + ' <span class="x-msg-hl">' + nn + '</span> (<span class="x-msg-hl">' + source + '</span>) ' + i18n_was_registered);
                    
                    GLOBALS.stores.map.load();
                    Ext.getCmp('organisationunitlevel_cb').clearValue();
                    Ext.getCmp('newname_tf').reset();
                    Ext.getCmp('maplayerpath_cb').clearValue();
                    Ext.getCmp('newnamecolumn_cb').clearValue();                            
                }
            });
        }
    });
    
    var editMapButton = new Ext.Button({
        id: 'editmap_b',
        text: i18n_save,
		cls: 'aa_med',
        handler: function() {
            var en = Ext.getCmp('editname_tf').getValue();
            var em = Ext.getCmp('editmap_cb').getValue();
            var nc = Ext.getCmp('editnamecolumn_cb').getValue();

            if (!en || !em || !nc) {
                Ext.message.msg(false, i18n_form_is_not_complete );
                return;
            }
            
            if (GLOBALS.util.validateInputNameLength(en) == false) {
                Ext.message.msg(false, i18n_name_can_not_longer_than_25);
                return;
            }
           
            Ext.Ajax.request({
                url: GLOBALS.conf.path_mapping + 'addOrUpdateMap' + GLOBALS.conf.type,
                method: 'POST',
                params: {name: en, mapLayerPath: em, nameColumn: nc},
                success: function(r) {
                    Ext.message.msg(true, i18n_map + ' <span class="x-msg-hl">' + en + '</span> (<span class="x-msg-hl">' + em + '</span>)' + i18n_was_updated);
                    
                    GLOBALS.stores.map.load();
                    Ext.getCmp('editmap_cb').clearValue();
                    Ext.getCmp('deletemap_cb').clearValue();
                    Ext.getCmp('editmap_cb').clearValue();
                    Ext.getCmp('editname_tf').reset();
                    Ext.getCmp('editnamecolumn_cb').clearValue();
                }
            });
        }
    });
    
    var deleteMapButton = new Ext.Button({
        id: 'deletemap_b',
        text: i18n_delete_map,
		cls: 'aa_med',
        handler: function() {
            var mlp = Ext.getCmp('deletemap_cb').getValue();
            var mn = Ext.getCmp('deletemap_cb').getRawValue();
            
            if (!mlp) {
                Ext.message.msg(false, i18n_please_select_a_map);
                return;
            }
            
            Ext.Ajax.request({
                url: GLOBALS.conf.path_mapping + 'deleteMap' + GLOBALS.conf.type,
                method: 'POST',
                params: {mapLayerPath: mlp},
                success: function(r) {
                    Ext.message.msg(true, i18n_map + ' <span class="x-msg-hl">' + mn + '</span> (<span class="x-msg-hl">' + mlp + '</span>) ' + i18n_was_deleted);
                    
					if (mlp == Ext.getCmp('map_cb').getValue()) {
						Ext.getCmp('map_cb').clearValue();
					}
                    if (mlp == Ext.getCmp('map_cb2').getValue()) {
						Ext.getCmp('map_cb2').clearValue();
					}
                    if (mlp == Ext.getCmp('editmap_cb').getValue()) {
                        Ext.getCmp('editmap_cb').clearValue();
                        Ext.getCmp('editname_tf').reset();
                        Ext.getCmp('editnamecolumn_cb').clearValue();
                    }
                    
                    GLOBALS.stores.map.load();
                    GLOBALS.stores.mapView.load();
                    Ext.getCmp('editmap_cb').clearValue();
                    Ext.getCmp('deletemap_cb').clearValue();
                    Ext.getCmp('mapview_cb').clearValue();
                }
            });
        }
    });
    
    var editMapComboBox = new Ext.form.ComboBox({
        id: 'editmap_cb',
        typeAhead: true,
        editable: false,
        valueField: 'mapLayerPath',
        displayField: 'name',
        emptyText: GLOBALS.conf.emptytext,
		hideLabel: true,
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: GLOBALS.conf.combo_width,
        minListWidth: GLOBALS.conf.combo_width,
        store: GLOBALS.stores.map,
        listeners: {
            'select': {
                fn: function(cb, record, i) {
                    Ext.getCmp('editname_tf').setValue(record.data.name);
                    Ext.getCmp('editnamecolumn_cb').setValue(record.data.nameColumn);
					
                    var url;
                    var params = {};
                    
					if (GLOBALS.vars.mapSourceType.isGeojson()) {
                        url = GLOBALS.conf.path_mapping + 'getGeoJsonFromFile' + GLOBALS.conf.type;
                        params.name = cb.getValue();
                    }
                    else if (GLOBALS.vars.mapSourceType.isShapefile()) {
                        url = GLOBALS.conf.path_geoserver + GLOBALS.conf.wfs + mlp + GLOBALS.conf.output;
                    }
                    
                    Ext.Ajax.request({
                        url: url,
                        method: 'POST',
                        params: params,
                        success: function(r) {
                            var file = Ext.util.JSON.decode(r.responseText);
                            var keys = [];
                            var data = [];

                            var nameList = GLOBALS.util.getKeys(file.features[0].properties);
                            for (var i = 0; i < nameList.length; i++) {
                                data.push(new Array(nameList[i]));
                            }
                            
                            GLOBALS.stores.nameColumn.loadData(data, false);
                        }
                    });
                }
            }
        }
    });
    
    var deleteMapComboBox = new Ext.form.ComboBox({
        xtype: 'combo',
        id: 'deletemap_cb',
        typeAhead: true,
        editable: false,
        valueField: 'mapLayerPath',
        displayField: 'name',
        emptyText: GLOBALS.conf.emptytext,
		hideLabel: true,
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: GLOBALS.conf.combo_width,
        minListWidth: GLOBALS.conf.combo_width,
        store: GLOBALS.stores.map
    });
    
    var newMapPanel = new Ext.form.FormPanel({
        id: 'newmap_p',
        items:
        [   
            { html: '<div class="panel-fieldlabel-first">' + i18n_display_name + '</div>' }, newNameTextField,
            { html: '<div class="panel-fieldlabel">' + i18n_organisation_unit_level + '</div>' }, organisationUnitLevelComboBox,
			{ html: '<div class="panel-fieldlabel">' + i18n_map_source_file + '</div>' }, mapLayerPathComboBox, mapLayerPathWMSTextField,
            { html: '<div class="panel-fieldlabel">' + i18n_name_column + '</div>' }, newNameColumnComboBox
        ]
    });
    
    var editMapPanel = new Ext.form.FormPanel({
        id: 'editmap_p',
        items: [
            { html: '<div class="panel-fieldlabel-first">' + i18n_map + '</div>' }, editMapComboBox,
            { html: '<div class="panel-fieldlabel">' + i18n_display_name + '</div>' }, editNameTextField,
            { html: '<div class="panel-fieldlabel">' + i18n_name_column + '</div>' }, editNameColumnComboBox
        ]
    });
    
    var deleteMapPanel = new Ext.form.FormPanel({
        id: 'deletemap_p',
        items: [
            { html: '<div class="panel-fieldlabel-first">' + i18n_map + '</div>' }, deleteMapComboBox
        ]
    });

    var shapefilePanel = new Ext.Panel({
        id: 'shapefile_p',
        title: '<span class="panel-title">' + i18n_register_maps + '</span>',
        items:
        [
            {
                xtype: 'tabpanel',
                activeTab: 0,
                deferredRender: false,
                plain: true,
                defaults: {layout: 'fit', bodyStyle: 'padding:8px'},
                listeners: {
                    tabchange: function(panel, tab) {
                        var nm_b = Ext.getCmp('newmap_b');
                        var em_b = Ext.getCmp('editmap_b');
                        var dm_b = Ext.getCmp('deletemap_b');
                        
                        if (tab.id == 'map0')
                        { 
                            nm_b.setVisible(true);
                            em_b.setVisible(false);
                            dm_b.setVisible(false);
                        }
                        
                        else if (tab.id == 'map1')
                        {
                            nm_b.setVisible(false);
                            em_b.setVisible(true);
                            dm_b.setVisible(false);
                        }
                        
                        else if (tab.id == 'map2')
                        {
                            nm_b.setVisible(false);
                            em_b.setVisible(false);
                            dm_b.setVisible(true);
                        }
                    }
                },
                items:
                [
                    {
                        title: '<span class="panel-tab-title">' + i18n_new + '</span>',
                        id: 'map0',
                        items: [newMapPanel]
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_edit + '</span>',
                        id: 'map1',
                        items: [editMapPanel]
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_delete + '</span>',
                        id: 'map2',
                        items: [deleteMapPanel]
                    }
                ]
            },
            { html: '<br>' },
            
            newMapButton,
            
            editMapButton,
            
            deleteMapButton
        ],
		listeners: {
			expand: {
				fn: function() {
					if (GLOBALS.vars.mapSourceType.isShapefile()) {
						mapLayerPathComboBox.hide();
						mapLayerPathWMSTextField.show();						
					}
					else {
						mapLayerPathComboBox.show();
						mapLayerPathWMSTextField.hide();						
					}
					
					GLOBALS.vars.activePanel.value = GLOBALS.conf.mapRegistration;
				}
			},
			collapse: {
				fn: function() {
					GLOBALS.vars.activePanel.value = null;
				}
			}
		}
    });
    
    /* Section: map layers */
	var mapLayerNameTextField=new Ext.form.TextField({id:'maplayername_tf',emptyText:GLOBALS.conf.emptytext,hideLabel:true,width:GLOBALS.conf.combo_width});
	var mapLayerMapSourceFileComboBox=new Ext.form.ComboBox({id:'maplayermapsourcefile_cb',editable:false,displayField:'name',valueField:'name',emptyText:GLOBALS.conf.emptytext,hideLabel:true,width:GLOBALS.conf.combo_width,minListWidth:GLOBALS.conf.combo_width,triggerAction:'all',mode:'remote',store:GLOBALS.stores.geojsonFiles});
	
	var wmsOverlayGrid = new Ext.grid.GridPanel({
		id: 'wmsoverlay_g',
		sm: new Ext.grid.RowSelectionModel({
			singleSelect:true
		}),
        columns: [
            {header: i18n_title, dataIndex: 'title', sortable: true, width: 180},
            {header: name, dataIndex: 'name', sortable: true, width: 180},
            {header: i18n_queryable, dataIndex: 'queryable', sortable: true, width: 100},
            {header: i18n_description, id: 'description', dataIndex: 'abstract'}
        ],
        autoExpandColumn: 'description',
        width: 700,
        height: screen.height * 0.6,
        store: GLOBALS.stores.wmsCapabilities,
        listeners: {
            'rowdblclick': mapOverlayPreview
        }
    });
    
    function mapOverlayPreview(grid, index) {
        var record = grid.getStore().getAt(index);
        var layer = record.get('layer').clone();
        
        var wmsOverlayPreviewWindow = new Ext.Window({
            title: '<span class="panel-title">'+i18n_preview+': ' + record.get("title") + '</span>',
            width: screen.width * 0.4,
            height: screen.height * 0.4,
            layout: 'fit',
            items: [{
                xtype: 'gx_mappanel',
                layers: [layer],
                extent: record.get('llbbox')
            }]
        });
        wmsOverlayPreviewWindow.show();
    }
	
	var wmsOverlayWindow = new Ext.Window({
		id: 'wmsoverlay_w',
		title: '<span class="panel-title">Geoserver shapefiles</span>',
		closeAction: 'hide',
		width: wmsOverlayGrid.width,
		height: screen.height * 0.4,
		items: [wmsOverlayGrid],
		bbar: new Ext.StatusBar({
			id: 'wmsoverlaywindow_sb',
			items:
			[
				{
					xtype: 'button',
					id: 'selectwmsoverlay_b',
					text: i18n_select,
					cls: 'aa_med',
					handler: function() {
						var selected = Ext.getCmp('wmsoverlay_g').getSelectionModel().getSelected();
                        if (selected) {
                            mapLayerPathWMSOverlayTextField.setValue(selected.get('name'));
                            wmsOverlayWindow.hide();
                            newMapLayerButton.focus();
                        }
					}
				}
			]
		})
	});
	
	var mapLayerPathWMSOverlayTextField = new Ext.form.TextField({
		id: 'maplayerpathwmsoverlay_tf',
		emptyText: GLOBALS.conf.emptytext,
		hideLabel: true,
        width: GLOBALS.conf.combo_width,
		listeners: {
			'focus': {
				fn: function() {
                    function show() {
                        var x = Ext.getCmp('center').x + 15;
                        var y = Ext.getCmp('center').y + 41;    
                        wmsOverlayWindow.show();
                        wmsOverlayWindow.setPosition(x,y);
                    }
                    
                    if (!GLOBALS.stores.wmsCapabilities.isLoaded) {
                        GLOBALS.stores.wmsCapabilities.load({callback: function() {
                            show();
                        }});
                    }
                    else {
                        show();
                    }
				}
			}
		}
	});
	
	var mapLayerFillColorColorField=new Ext.ux.ColorField({id:'maplayerfillcolor_cf',hideLabel:true,allowBlank:false,width:GLOBALS.conf.combo_width,value:'#FF0000'});
	var mapLayerFillOpacityComboBox=new Ext.form.ComboBox({id:'maplayerfillopacity_cb',hideLabel:true,editable:true,valueField:'value',displayField:'value',mode:'local',triggerAction:'all',width:GLOBALS.conf.combo_number_width,minListWidth:GLOBALS.conf.combo_number_width,value:0.5,store:new Ext.data.ArrayStore({fields:['value'],data:[[0.0],[0.1],[0.2],[0.3],[0.4],[0.5],[0.6],[0.7],[0.8],[0.9],[1.0]]})});
	var mapLayerStrokeColorColorField=new Ext.ux.ColorField({id:'maplayerstrokecolor_cf',hideLabel:true,allowBlank:false,width:GLOBALS.conf.combo_width,value:'#222222'});
	var mapLayerStrokeWidthComboBox=new Ext.form.ComboBox({id:'maplayerstrokewidth_cb',hideLabel:true,editable:true,valueField:'value',displayField:'value',mode:'local',triggerAction:'all',width:GLOBALS.conf.combo_number_width,minListWidth:GLOBALS.conf.combo_number_width,value:2,store:new Ext.data.ArrayStore({fields:['value'],data:[[0],[1],[2],[3],[4]]})});
	var mapLayerComboBox=new Ext.form.ComboBox({id:'maplayer_cb',typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:GLOBALS.conf.emptytext,hideLabel:true,selectOnFocus:true,width:GLOBALS.conf.combo_width,minListWidth:GLOBALS.conf.combo_width,store:GLOBALS.stores.overlay});
    
    var deleteMapLayerButton = new Ext.Button({
        id: 'deletemaplayer_b',
        text: i18n_delete_overlay,
 		cls: 'window-button',
        handler: function() {
            var ml = Ext.getCmp('maplayer_cb').getValue();
            var mln = Ext.getCmp('maplayer_cb').getRawValue();
            
            if (!ml) {
                Ext.message.msg(false, i18n_please_select_an_overlay);
                return;
            }
            
            Ext.Ajax.request({
                url: GLOBALS.conf.path_mapping + 'deleteMapLayer' + GLOBALS.conf.type,
                method: 'POST',
                params: {id:ml},
                success: function(r) {
                    Ext.message.msg(true, i18n_overlay + ' <span class="x-msg-hl">' + mln + '</span> '+i18n_was_deleted);
                    GLOBALS.stores.overlay.load();
                    Ext.getCmp('maplayer_cb').clearValue();
                }
            });
            
            GLOBALS.vars.map.getLayersByName(mln)[0].destroy();
        }
    });
	
    var newMapLayerPanel = new Ext.form.FormPanel({
        id: 'newmaplayer_p',
        items:
        [
            { html: '<div class="panel-fieldlabel-first">' + i18n_display_name + '</div>' }, mapLayerNameTextField,
            { html: '<div class="panel-fieldlabel">' + i18n_map_source_file + '</div>' }, mapLayerMapSourceFileComboBox, mapLayerPathWMSOverlayTextField,
            { html: '<div class="panel-fieldlabel">' + i18n_fill_color + '</div>' }, mapLayerFillColorColorField,
            { html: '<div class="panel-fieldlabel">' + i18n_fill_opacity + '</div>' }, mapLayerFillOpacityComboBox,
            { html: '<div class="panel-fieldlabel">' + i18n_stroke_color + '</div>' }, mapLayerStrokeColorColorField,
            { html: '<div class="panel-fieldlabel">' + i18n_stroke_width + '</div>' }, mapLayerStrokeWidthComboBox,
            {
				xtype: 'button',
				id: 'newmaplayer_b',
				text: 'Register',
				cls: 'window-button',
				handler: function() {
					var mln = Ext.getCmp('maplayername_tf').getRawValue();
					var mlfc = Ext.getCmp('maplayerfillcolor_cf').getValue();
					var mlfo = Ext.getCmp('maplayerfillopacity_cb').getRawValue();
					var mlsc = Ext.getCmp('maplayerstrokecolor_cf').getValue();
					var mlsw = Ext.getCmp('maplayerstrokewidth_cb').getRawValue();
					var mlmsf = Ext.getCmp('maplayermapsourcefile_cb').getValue();
					var mlwmso = Ext.getCmp('maplayerpathwmsoverlay_tf').getValue();
					
					if (!mln) {
						Ext.message.msg(false, i18n_overlay_form_is_not_complete);
						return;
					}
					else if (!mlmsf && !mlwmso) {
						Ext.message.msg(false, i18n_overlay_form_is_not_complete);
						return;
					}
					
					if (GLOBALS.util.validateInputNameLength(mln) == false) {
						Ext.message.msg(false, i18n_overlay_name_cannot_be_longer_than_25_characters);
						return;
					}
                    
                    if (GLOBALS.stores.overlay.find('name', mln) !== -1) {
                        Ext.message.msg(false, i18n_name + ' <span class="x-msg-hl">' + mln + '</span> ' + i18n_is_already_in_use);
                        return;
                    }
                        
                    var ms = GLOBALS.vars.mapSourceType.isShapefile() ? mlwmso : mlmsf;
							
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + 'addOrUpdateMapLayer' + GLOBALS.conf.type,
                        method: 'POST',
                        params: {name: mln, type: 'overlay', mapSource: ms, fillColor: mlfc, fillOpacity: mlfo, strokeColor: mlsc, strokeWidth: mlsw},
                        success: function(r) {
                            Ext.message.msg(true, 'The overlay <span class="x-msg-hl">' + mln + '</span> '+i18n_was_registered);
                            GLOBALS.stores.overlay.load();
                    
                            var mapurl = GLOBALS.vars.mapSourceType.isShapefile() ?
                                GLOBALS.conf.path_geoserver + GLOBALS.conf.wfs + mlwmso + GLOBALS.conf.output : GLOBALS.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + mlmsf;
                            
                            GLOBALS.vars.map.addLayer(
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
                                        'url': mapurl,
                                        'format': new OpenLayers.Format.GeoJSON()
                                    })
                                })
                            );
                            
                            Ext.getCmp('maplayername_tf').reset();
                            Ext.getCmp('maplayermapsourcefile_cb').clearValue();
                            Ext.getCmp('maplayerpathwmsoverlay_tf').reset();
                        }
                    });
				}
			}
        ]
    });
    
    var deleteMapLayerPanel = new Ext.form.FormPanel({
        id: 'deletemaplayer_p',
        items:
        [
            { html: '<div class="panel-fieldlabel-first">' + i18n_overlays + '</div>' }, mapLayerComboBox,
            deleteMapLayerButton
        ]
    });

	var overlaysWindow = new Ext.Window({
        id: 'overlays_w',
        title: '<span id="window-maplayer-title">' + i18n_overlays + '</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: 234,
        items:
        [
			{
                xtype: 'tabpanel',
                activeTab: 0,
                deferredRender: false,
                plain: true,
                defaults: {layout: 'fit', bodyStyle: 'padding:8px'},
                listeners: {
                    tabchange: function(panel, tab)
                    {
                        if (tab.id == 'overlay0') {
							Ext.getCmp('overlays_w').setHeight(395);                        
                        }
                        else if (tab.id == 'overlay1') {
							Ext.getCmp('overlays_w').setHeight(151);
                        }
                    }
                },
                items:
                [
                    {
                        title: '<span class="panel-tab-title">' + i18n_new + '</span>',
                        id: 'overlay0',
                        items: [newMapLayerPanel]
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_delete + '</span>',
                        id: 'overlay1',
                        items: [deleteMapLayerPanel]
                    }
                ]
            }
        ],
		listeners: {
			show: {
				fn: function() {
					if (GLOBALS.vars.mapSourceType.isGeojson() || GLOBALS.vars.mapSourceType.isDatabase()) {
						mapLayerMapSourceFileComboBox.show();
						mapLayerPathWMSOverlayTextField.hide();
					}
					else if (GLOBALS.vars.mapSourceType.isShapefile()) {
						mapLayerMapSourceFileComboBox.hide();
						mapLayerPathWMSOverlayTextField.show();
					}
				}
			}
		}
    });
    
    var mapLayerBaseLayersNameTextField=new Ext.form.TextField({id:'maplayerbaselayersname_tf',emptyText:GLOBALS.conf.emptytext,hideLabel:true,width:GLOBALS.conf.combo_width});
    var mapLayerBaseLayersUrlTextField=new Ext.form.TextField({id:'maplayerbaselayersurl_tf',emptyText:GLOBALS.conf.emptytext,hideLabel:true,width:GLOBALS.conf.combo_width});
    var mapLayerBaseLayersLayerTextField=new Ext.form.TextField({id:'maplayerbaselayerslayer_tf',emptyText:GLOBALS.conf.emptytext,hideLabel:true,width:GLOBALS.conf.combo_width});
    
    
	var mapLayerBaseLayerComboBox=new Ext.form.ComboBox({id:'maplayerbaselayers_cb',typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:GLOBALS.conf.emptytext,hideLabel:true,selectOnFocus:true,width:GLOBALS.conf.combo_width,minListWidth:GLOBALS.conf.combo_width,store:GLOBALS.stores.baseLayer});
    
    var deleteMapLayerBaseLayersButton = new Ext.Button({
        id: 'deletemaplayerbaselayers_b',
        text: i18n_delete_baselayer,
 		cls: 'window-button',
        handler: function() {
            var ml = Ext.getCmp('maplayerbaselayers_cb').getValue();
            var mln = Ext.getCmp('maplayerbaselayers_cb').getRawValue();
            
            if (!ml) {
                Ext.message.msg(false, i18n_please_select_a_baselayer);
                return;
            }
            
            Ext.Ajax.request({
                url: GLOBALS.conf.path_mapping + 'deleteMapLayer' + GLOBALS.conf.type,
                method: 'POST',
                params: {id: ml},
                success: function(r) {
                    Ext.message.msg(true, i18n_baselayer + ' <span class="x-msg-hl">' + mln + '</span> '+i18n_was_deleted);
                    GLOBALS.stores.baseLayer.load({callback: function() {
                        Ext.getCmp('maplayerbaselayers_cb').clearValue();
                        var names = GLOBALS.stores.baseLayer.collect('name');
                        
                        for (var i = 0; i < names.length; i++) {
                            GLOBALS.vars.map.getLayersByName(names[i])[0].setVisibility(false);
                        }
                        
                        GLOBALS.vars.map.getLayersByName(mln)[0].destroy(false);
                    }});
                }
            });
            
        }
    });
    
    var newMapLayerBaseLayersPanel = new Ext.form.FormPanel({
        id: 'newmaplayerbaselayers_p',
        items:
        [
            { html: '<div class="panel-fieldlabel-first">'+i18n_display_name+'</div>' }, mapLayerBaseLayersNameTextField,
            { html: '<div class="panel-fieldlabel">'+i18n_url+'</div>' }, mapLayerBaseLayersUrlTextField,
            { html: '<div class="panel-fieldlabel">'+i18n_layer+'</div>' }, mapLayerBaseLayersLayerTextField,
            {
				xtype: 'button',
				id: 'newmaplayerbaselayers_b',
				text: 'Register new base layer',
				cls: 'window-button',
				handler: function() {
					var mlbn = Ext.getCmp('maplayerbaselayersname_tf').getValue();
					var mlbu = Ext.getCmp('maplayerbaselayersurl_tf').getValue();
					var mlbl = Ext.getCmp('maplayerbaselayerslayer_tf').getValue();
					
					if (!mlbn || !mlbu || !mlbl) {
						Ext.message.msg(false, i18n_baselayer_form_is_not_complete );
						return;
					}
					
					if (GLOBALS.util.validateInputNameLength(mlbn) == false) {
						Ext.message.msg(false, i18n_baselayer_name_cannot_be_longer_than_25_characters);
						return;
					}
                    
                    if (GLOBALS.stores.baseLayer.find('name', mlbn) !== -1) {
                        Ext.message.msg(false, i18n_name + ' <span class="x-msg-hl">' + mlbn + '</span> ' + i18n_is_already_in_use);
                        return;
                    }
					
                    Ext.Ajax.request({
                        url: GLOBALS.conf.path_mapping + 'addOrUpdateMapLayer' + GLOBALS.conf.type,
                        method: 'POST',
                        params: {name: mlbn, type: GLOBALS.conf.map_layer_type_baselayer, mapSource: mlbu, layer: mlbl, fillColor: '', fillOpacity: 0, strokeColor: '', strokeWidth: 0},
                        success: function(r) {
                            Ext.message.msg(true, 'The base layer <span class="x-msg-hl">' + mlbn + '</span> ' + i18n_was_registered);
                            GLOBALS.stores.baseLayer.load();
                            GLOBALS.vars.map.addLayers([
                                new OpenLayers.Layer.WMS(
                                    mlbn,
                                    mlbu,
                                    {layers: mlbl}
                                )
                            ]);
                            
                            Ext.getCmp('maplayerbaselayersname_tf').reset();
                            Ext.getCmp('maplayerbaselayersurl_tf').reset();
                            Ext.getCmp('maplayerbaselayerslayer_tf').reset();
                        }
                    });
				}
			}
        ]
    });

    var deleteMapLayerBaseLayerPanel = new Ext.form.FormPanel({
        id: 'deletemaplayerbaselayer_p',
        items:
        [
            { html: '<div class="panel-fieldlabel-first">'+i18n_baselayers+'</div>' }, mapLayerBaseLayerComboBox,
            deleteMapLayerBaseLayersButton
        ]
    });
    
    var baselayersWindow = new Ext.Window({
        id: 'baselayers_w',
        title: '<span id="window-maplayer-title">'+i18n_baselayers+'</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: 234,
        items:
        [
			{
                xtype: 'tabpanel',
                activeTab: 0,
                deferredRender: false,
                plain: true,
                defaults: {layout: 'fit', bodyStyle: 'padding:8px'},
                listeners: {
                    tabchange: function(panel, tab)
                    {
                        if (tab.id == 'baselayer0') {
							Ext.getCmp('baselayers_w').setHeight(247);
                        }
                        else if (tab.id == 'baselayer1') {
							Ext.getCmp('baselayers_w').setHeight(151);
                        }
                    }
                },
                items:
                [
                    {
                        title: '<span class="panel-tab-title">' + i18n_new + '</span>',
                        id: 'baselayer0',
                        items: [newMapLayerBaseLayersPanel]
                    },
                    {
                        title: '<span class="panel-tab-title">' + i18n_delete + '</span>',
                        id: 'baselayer1',
                        items: [deleteMapLayerBaseLayerPanel]
                    }
                ]
            }
        ]
    });
	
    /* Section: administrator */
    var adminPanel = new Ext.form.FormPanel({
        id: 'admin_p',
        title: '<span class="panel-title">' + i18n_administrator + '</span>',
        items:
        [
			{ html: '<p style="height:5px;">' },
			{
				xtype:'fieldset',
				columnWidth: 0.5,
				title: '&nbsp;<span class="panel-tab-title">' + i18n_map_source + '</span>&nbsp;',
				collapsible: true,
				animCollapse: true,
				autoHeight:true,
				items:
				[
					{
						xtype: 'combo',
						id: 'mapsource_cb',
						fieldLabel: i18n_map_source,
						labelSeparator: GLOBALS.conf.labelseparator,
						editable: false,
						valueField: 'id',
						displayField: 'text',
						isFormField: true,
						width: GLOBALS.conf.combo_width_fieldset,
						minListWidth: GLOBALS.conf.combo_width_fieldset,
						mode: 'local',
						triggerAction: 'all',
						value: GLOBALS.vars.mapSourceType.value,
						store: new Ext.data.ArrayStore({
							fields: ['id', 'text'],
							data: [
                                [GLOBALS.conf.map_source_type_database, 'DHIS database'],
                                [GLOBALS.conf.map_source_type_geojson, 'GeoJSON files'],
                                [GLOBALS.conf.map_source_type_shapefile, 'Shapefiles']
                            ]
						}),
						listeners: {
							'select': {
								fn: function(cb) {
                                    if (GLOBALS.vars.mapSourceType.value != cb.getValue()) {
                                        GLOBALS.vars.mapSourceType.value = cb.getValue();
                                        
                                        Ext.Ajax.request({
                                            url: GLOBALS.conf.path_mapping + 'setMapUserSettings' + GLOBALS.conf.type,
											method: 'POST',
											params: {mapSourceType: GLOBALS.vars.mapSourceType.value, mapDateType: GLOBALS.vars.mapDateType.value},
											success: function(r) {
                                                GLOBALS.stores.map.load();
                                                GLOBALS.stores.mapView.load();
                                                GLOBALS.stores.overlay.load();

												Ext.getCmp('map_cb').clearValue();
                                                Ext.getCmp('map_cb2').clearValue();
												Ext.getCmp('mapview_cb').clearValue();
                                                
                                                if (GLOBALS.vars.mapSourceType.isDatabase()) {
													Ext.getCmp('register_chb').disable();													
													mapping.hide();
													shapefilePanel.hide();
												}
                                                else {
                                                    Ext.getCmp('register_chb').enable();
													
													if (Ext.getCmp('register_chb').checked) {
														mapping.show();
														shapefilePanel.show();
													}
                                                    
                                                    if (GLOBALS.vars.mapDateType.isStartEnd()) {
                                                        GLOBALS.vars.mapDateType.setFixed();
                                                        Ext.getCmp('mapdatetype_cb').setValue(GLOBALS.vars.mapDateType.value);
                                                        choropleth.prepareMapViewDateType();
                                                        proportionalSymbol.prepareMapViewDateType();
                                                    }
										
                                                    if (GLOBALS.vars.mapSourceType.isGeojson()) {
                                                        mapLayerMapSourceFileComboBox.show();
                                                        mapLayerPathWMSOverlayTextField.hide();
                                                    }
                                                    else if (GLOBALS.vars.mapSourceType.isShapefile()) {
                                                        mapLayerMapSourceFileComboBox.hide();
                                                        mapLayerPathWMSOverlayTextField.show();
                                                    }
												}
                                                
                                                choropleth.prepareMapViewMap();
                                                proportionalSymbol.prepareMapViewMap();
                                                
												if (GLOBALS.vars.map.layers.length > 2) {
													for (var i = 0; i < GLOBALS.vars.map.layers.length; i++) {
                                                        if (GLOBALS.vars.map.layers[i].isOverlay) {
                                                            GLOBALS.vars.map.removeLayer(GLOBALS.vars.map.layers[i]);
                                                        }
													}
												}
												addOverlaysToMap();
												Ext.message.msg(true, '<span class="x-msg-hl">' + cb.getRawValue() + '</span> '+i18n_is_saved_as_map_source);
											}
										});
									}
								}
							}
						}
					},
                    
					{
						xtype: 'checkbox',
						id: 'register_chb',
						fieldLabel: i18n_admin_panels,
						labelSeparator: GLOBALS.conf.labelseparator,
						isFormField: true,
						listeners: {
							'check': {
								fn: function(checkbox,checked) {
									if (checked) {
										mapping.show();
										shapefilePanel.show();
										Ext.getCmp('west').doLayout();
									}
									else {
										mapping.hide();
										shapefilePanel.hide();
										Ext.getCmp('west').doLayout();
									}
								}
							}
						}
					}
				]
			},
            
			{
				xtype:'fieldset',
				columnWidth: 0.5,
				title: '&nbsp;<span class="panel-tab-title">' + i18n_date_type + '</span>&nbsp;',
				collapsible: true,
				animCollapse: true,
				autoHeight:true,
				items: [
                    {
                        xtype: 'combo',
                        id: 'mapdatetype_cb',
                        fieldLabel: i18n_date_type,
                        labelSeparator: GLOBALS.conf.labelseparator,
                        editable: false,
                        valueField: 'value',
                        displayField: 'text',
                        mode: 'local',
                        value: GLOBALS.conf.map_date_type_fixed,
                        triggerAction: 'all',
						width: GLOBALS.conf.combo_width_fieldset,
						minListWidth: GLOBALS.conf.combo_width_fieldset,
                        store: new Ext.data.SimpleStore({
                            fields: ['value', 'text'],
                            data: [
                                [GLOBALS.conf.map_date_type_fixed, i18n_fixed_periods],
                                [GLOBALS.conf.map_date_type_start_end, i18n_start_end_dates]
                            ]
                        }),
                        listeners: {
                            'select': {
                                fn: function(cb) {
                                    if (cb.getValue() != GLOBALS.vars.mapDateType.value) {
                                        if (cb.getValue() == GLOBALS.conf.map_date_type_start_end && GLOBALS.vars.mapSourceType.value != GLOBALS.conf.map_source_type_database) {
                                            cb.setValue(GLOBALS.conf.map_date_type_fixed);
                                            Ext.message.msg(false, 'Start-end dates require map source <span class="x-msg-hl">' + GLOBALS.conf.map_source_type_database + '</span>');
                                            return;
                                        }

                                        GLOBALS.vars.mapDateType.value = cb.getValue();
                                        Ext.Ajax.request({
                                            url: GLOBALS.conf.path_mapping + 'setMapUserSettings' + GLOBALS.conf.type,
                                            method: 'POST',
                                            params: {mapSourceType: GLOBALS.vars.mapSourceType.value, mapDateType: GLOBALS.vars.mapDateType.value},
                                            success: function() {
                                                Ext.message.msg(true, '<span class="x-msg-hl">' + cb.getRawValue() + '</span> '+i18n_saved_as_date_type);
                                                choropleth.prepareMapViewDateType();
                                                proportionalSymbol.prepareMapViewDateType();
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                ]
            }
        ],
        listeners: {
            expand: {
                fn: function() {
                    if (GLOBALS.vars.mapSourceType.isGeojson()) {
                        Ext.getCmp('register_chb').enable();
                    }
                    else if (GLOBALS.vars.mapSourceType.isDatabase()) {
                        Ext.getCmp('register_chb').disable();
                    }
					
					GLOBALS.vars.activePanel.value = GLOBALS.conf.administration;
                }
            },
			collapse: {
				fn: function() {
					GLOBALS.vars.activePanel.value = null;
				}
			}
        }
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
    
    var proportionalSymbolLayer = new OpenLayers.Layer.Vector('Point layer', {
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
    
    GLOBALS.vars.map.addLayers([choroplethLayer, proportionalSymbolLayer]);
        
    /* Section: layer options */
    function showWMSLayerOptions(layer) {
        if (Ext.getCmp('baselayeroptions_w')) {
            Ext.getCmp('baselayeroptions_w').destroy();
        }
        
        var baseLayerOptionsWindow = new Ext.Window({
            id: 'baselayeroptions_w',
            title: 'Options: <span style="font-weight:normal;">' + layer.name + '</span>',
            width: 180,
            items: [
                {
                    xtype: 'menu',
                    id: 'baselayeroptions_m',
                    floating: false,
                    items: [
                        {
                            text: 'Show WMS legend',
                            iconCls: 'menu-layeroptions-wmslegend',
                            listeners: {
                                'click': {
                                    fn: function() {
                                        baseLayerOptionsWindow.destroy();
                                        
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
                                }
                            }
                        },
                        {
                            text: 'Opacity',
                            iconCls: 'menu-layeroptions-opacity',
                            menu: { 
                                items: [
                                    {
                                        text: '0.1',
                                        iconCls: 'menu-layeroptions-opacity-10',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.1); } } }
                                    },
                                    {
                                        text: '0.2',
                                        iconCls: 'menu-layeroptions-opacity-20',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.2); } } }
                                    },
                                    {
                                        text: '0.3',
                                        iconCls: 'menu-layeroptions-opacity-30',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.3); } } }
                                    },
                                    {
                                        text: '0.4',
                                        iconCls: 'menu-layeroptions-opacity-40',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.4); } } }
                                    },
                                    {
                                        text: '0.5',
                                        iconCls: 'menu-layeroptions-opacity-50',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.5); } } }
                                    },
                                    {
                                        text: '0.6',
                                        iconCls: 'menu-layeroptions-opacity-60',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.6); } } }
                                    },
                                    {
                                        text: '0.7',
                                        iconCls: 'menu-layeroptions-opacity-70',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.7); } } }
                                    },
                                    {
                                        text: '0.8',
                                        iconCls: 'menu-layeroptions-opacity-80',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.8); } } }
                                    },
                                    {
                                        text: '0.9',
                                        iconCls: 'menu-layeroptions-opacity-90',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.9); } } }
                                    },
                                    {
                                        text: '1.0',
                                        iconCls: 'menu-layeroptions-opacity-100',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(1.0); } } }
                                    }
                                ]
                            }
                        }
                    ]
                }
            ]
        });
        baseLayerOptionsWindow.setPagePosition(Ext.getCmp('east').x - 206, Ext.getCmp('center').y + 50);
        baseLayerOptionsWindow.show();
    }
    
    
    function showVectorLayerOptions(layer) {
        if (Ext.getCmp('vectorlayeroptions_w')) {
            Ext.getCmp('vectorlayeroptions_w').destroy();
        }
        
        var data = [];        
        for (var i = 0; i < layer.features.length; i++) {
            data.push([layer.features[i].data.id || i, layer.features[i].data.name]);
        }
        
        var featureStore = new Ext.data.ArrayStore({
            mode: 'local',
            autoDestroy: true,
            idProperty: 'id',
            fields: ['id','name'],
            sortInfo: {field: 'name', direction: 'ASC'},
            data: data
        });
        
        var locateFeatureWindow = new Ext.Window({
            id: 'locatefeature_w',
            title: 'Locate features',
            layout: 'fit',
            defaults: {layout: 'fit', bodyStyle:'padding:8px; border:0px'},
            width: 250,
            height: GLOBALS.util.getMultiSelectHeight() + 145,
            items: [
                {
                    xtype: 'panel',
                    items: [
                        {
                            xtype: 'panel',
                            items: [
                                { html: '<div class="window-field-label-first">' + i18n_highlight_color + '</div>' },
                                {
                                    xtype: 'colorfield',
                                    labelSeparator: GLOBALS.conf.labelseparator,
                                    id: 'highlightcolor_cf',
                                    allowBlank: false,
                                    isFormField: true,
                                    width: GLOBALS.conf.combo_width,
                                    value: "#0000FF"
                                },
                                { html: '<div class="window-field-label">' + i18n_feature_filter + '</div>' },
                                {
                                    xtype: 'textfield',
                                    id: 'locatefeature_tf',
                                    enableKeyEvents: true,
                                    listeners: {
                                        'keyup': {
                                            fn: function() {
                                                var p = Ext.getCmp('locatefeature_tf').getValue();
                                                featureStore.filter('name', p, true, false);
                                            }
                                        }
                                    }
                                },
                                { html: '<div class="window-field-nolabel"></div>' },
                                {
                                    xtype: 'grid',
                                    id: 'featuregrid_gp',
                                    height: GLOBALS.util.getMultiSelectHeight(),
                                    store: featureStore,
                                    cm: new Ext.grid.ColumnModel({
                                        columns: [{id: 'name', header: 'Features', dataIndex: 'name', width: 250}]
                                    }),
                                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                                    viewConfig: {forceFit: true},
                                    sortable: true,
                                    autoExpandColumn: 'name',
                                    listeners: {
                                        'cellclick': {
                                            fn: function(g, ri, ci) {
                                                layer.redraw();
                                                
                                                var id, feature, backupF, backupS;
                                                id = g.getStore().getAt(ri).data.id;
                                                
                                                for (var i = 0; i < layer.features.length; i++) {
                                                    if (layer.features[i].data.id == id) {
                                                        feature = layer.features[i];
                                                        break;
                                                    }
                                                }
                                                
                                                if (feature) {
                                                    var color = Ext.getCmp('highlightcolor_cf').getValue();
                                                    layer.drawFeature(feature,{'fillColor':color, 'strokeColor':color});
                                                }
                                            }
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                }
            ],
            listeners: {
                'close': {
                    fn: function() {
                        GLOBALS.vars.locateFeatureWindow = false;
                        layer.redraw();
                    }
                }
            }
        });
        
        GLOBALS.vars.locateFeatureWindow = locateFeatureWindow;
        
        var vectorLayerOptionsWindow = new Ext.Window({
            id: 'vectorlayeroptions_w',
            title: 'Options: <span style="font-weight:normal;">' + layer.name + '</span>',
            closeAction: 'hide',
            width: 180,
            items: [
                {
                    xtype: 'menu',
                    id: 'vectorlayeroptions_m',
                    floating: false,
                    items: [
                        {
                            text: 'Locate feature',
                            iconCls: 'menu-layeroptions-locate',
                            listeners: {
                                'click': {
                                    fn: function() {
                                        if (layer.features.length > 0) {
                                            locateFeatureWindow.setPagePosition(Ext.getCmp('east').x - 272, Ext.getCmp('center').y + 50);
                                            locateFeatureWindow.show();
                                            vectorLayerOptionsWindow.hide();
                                        }
                                        else {
                                            Ext.message.msg(false, '<span class="x-msg-hl">' + layer.name + ' </span>' + i18n_has_no_orgunits);
                                        }
                                    }
                                }
                            }
                        },
                        
                        {
                            text: 'Show/hide labels',
                            iconCls: 'menu-layeroptions-labels',
                            listeners: {
                                'click': {
                                    fn: function() {
                                        if (layer.features.length > 0) {
                                            if (layer.name == 'Polygon layer') {
                                                if (GLOBALS.vars.activePanel.isPolygon()) {
                                                    GLOBALS.util.toggleFeatureLabels(choropleth);
                                                }
                                                else if (GLOBALS.vars.activePanel.isAssignment()) {
                                                    GLOBALS.util.toggleFeatureLabelsAssignment();
                                                }
                                                else {
                                                    Ext.message.msg(false, 'Please use <span class="x-msg-hl">Point layer</span> options');
                                                }
                                            }
                                            else if (layer.name == 'Point layer') {
                                                if (GLOBALS.vars.activePanel.isPoint()) {
                                                    GLOBALS.util.toggleFeatureLabels(proportionalSymbol);
                                                }
                                                else {
                                                    Ext.message.msg(false, 'Please use <span class="x-msg-hl">Polygon layer</span> options');
                                                }
                                            }
                                        }
                                        else {
                                            Ext.message.msg(false, '<span class="x-msg-hl">' + layer.name + ' </span>' + i18n_has_no_orgunits);
                                        }
                                    }
                                }
                            }
                        },
                        
                        {
                            text: 'Opacity',
                            iconCls: 'menu-layeroptions-opacity',
                            menu: { 
                                items: [
                                    {
                                        text: '0.1',
                                        iconCls: 'menu-layeroptions-opacity-10',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.1); } } }
                                    },
                                    {
                                        text: '0.2',
                                        iconCls: 'menu-layeroptions-opacity-20',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.2); } } }
                                    },
                                    {
                                        text: '0.3',
                                        iconCls: 'menu-layeroptions-opacity-30',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.3); } } }
                                    },
                                    {
                                        text: '0.4',
                                        iconCls: 'menu-layeroptions-opacity-40',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.4); } } }
                                    },
                                    {
                                        text: '0.5',
                                        iconCls: 'menu-layeroptions-opacity-50',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.5); } } }
                                    },
                                    {
                                        text: '0.6',
                                        iconCls: 'menu-layeroptions-opacity-60',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.6); } } }
                                    },
                                    {
                                        text: '0.7',
                                        iconCls: 'menu-layeroptions-opacity-70',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.7); } } }
                                    },
                                    {
                                        text: '0.8',
                                        iconCls: 'menu-layeroptions-opacity-80',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.8); } } }
                                    },
                                    {
                                        text: '0.9',
                                        iconCls: 'menu-layeroptions-opacity-90',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(0.9); } } }
                                    },
                                    {
                                        text: '1.0',
                                        iconCls: 'menu-layeroptions-opacity-100',
                                        listeners: { 'click': { fn: function() { layer.setOpacity(1.0); } } }
                                    }
                                ]
                            }
                        }
                    ]
                }
            ]
        });
        vectorLayerOptionsWindow.setPagePosition(Ext.getCmp('east').x - 202, Ext.getCmp('center').y + 50);
        vectorLayerOptionsWindow.show();
    }
	
	var layerTreeConfig = [{
        nodeType: 'gx_baselayercontainer',
        singleClickExpand: true,
        expanded: true,
        text: 'Base layers',
		iconCls: 'icon-background'
    }, {
        nodeType: 'gx_overlaylayercontainer',
        singleClickExpand: true
    }, {
        nodeType: 'gx_layer',
        layer: 'Polygon layer'
    }, {
        nodeType: 'gx_layer',
        layer: 'Point layer'
    }];       
    
    var layerTree = new Ext.tree.TreePanel({
        title: '<span class="panel-title">' + i18n_map_layers + '</span>',
        enableDD: true,
        bodyStyle: 'padding-bottom:5px;',
        rootVisible: false,
        root: {
            nodeType: 'async',
            children: layerTreeConfig
        },
		listeners: {
			'click': {
				fn: function(n) {
					if (n.parentNode.attributes.text == 'Base layers') {
						showWMSLayerOptions(GLOBALS.vars.map.getLayersByName(n.attributes.layer.name)[0]);
					}
                    else if (n.parentNode.attributes.text == 'Overlays') {
                        showVectorLayerOptions(GLOBALS.vars.map.getLayersByName(n.attributes.layer.name)[0]);
                    }
					else if (n.isLeaf()) {
                        showVectorLayerOptions(GLOBALS.vars.map.getLayersByName(n.attributes.layer)[0]);
					}
				}
			}
		},					
        bbar: new Ext.StatusBar({
			id: 'maplayers_sb',
			items:
			[
				{
					xtype: 'button',
					id: 'baselayers_b',
					text: 'Base layers',
					cls: 'x-btn-text-icon',
					ctCls: 'aa_med',
					icon: '../../images/add_small.png',
					handler: function() {
                        Ext.getCmp('baselayers_w').setPagePosition(Ext.getCmp('east').x - 262, Ext.getCmp('center').y + 50);
						Ext.getCmp('baselayers_w').show();
					}
				},
                {
					xtype: 'button',
					id: 'overlays_b',
					text: 'Overlays',
					cls: 'x-btn-text-icon',
					ctCls: 'aa_med',
					icon: '../../images/add_small.png',
					handler: function() {
                        Ext.getCmp('overlays_w').setPagePosition(Ext.getCmp('east').x - 262, Ext.getCmp('center').y + 50);
						Ext.getCmp('overlays_w').show();
					}
				}
			]
		})
	});
	
    /* Section: widgets */
    choropleth = new mapfish.widgets.geostat.Choropleth({
        id: 'choropleth',
        map: GLOBALS.vars.map,
        layer: choroplethLayer,
		title: '<span class="panel-title">' + i18n_polygon_layer + '</span>',
        url: 'init',
        featureSelection: false,
        legendDiv: 'polygonlegend',
        defaults: {width: 130},
        listeners: {
            'expand': {
                fn: function() {
                    if (GLOBALS.vars.activePanel.value != GLOBALS.conf.thematicMap) {
                        GLOBALS.vars.activePanel.setPolygon();
                        this.layer.setVisibility(true);
                        
                        if (this.legend.type == GLOBALS.conf.map_legend_type_predefined) {
                            this.applyPredefinedLegend();
                        }
                        else {
                            this.classify(false, true);
                        }
                    }
                }
            }
        }
    });

    proportionalSymbol = new mapfish.widgets.geostat.Symbol({
        id: 'proportionalsymbol',
        map: GLOBALS.vars.map,
        layer: proportionalSymbolLayer,
		title: '<span class="panel-title">' + i18n_point_layer + '</span>',
        url: 'init',
        featureSelection: false,
        legendDiv: 'pointlegend',
        defaults: {width: 130},
        listeners: {
            'expand': {
                fn: function() {
                    if (GLOBALS.vars.activePanel.value != GLOBALS.conf.thematicMap2) {
                        GLOBALS.vars.activePanel.setPoint();
                        this.layer.setVisibility(false);
                        
                        if (this.legend.type == GLOBALS.conf.map_legend_type_predefined) {
                            this.applyPredefinedLegend();
                        }
                        else {
                            this.classify(false, true);
                        }
                    }
                }
            }
        }
    });
    
    mapping = new mapfish.widgets.geostat.Mapping({
        id: 'mapping',
        map: GLOBALS.vars.map,
        layer: choroplethLayer,
        title: '<span class="panel-title">' + i18n_assign_organisation_units_to_map + '</span>',
        url: 'init',
        featureSelection: false,
        legendDiv: 'polygonlegend',
        defaults: {width: 130},
        listeners: {
            'expand': {
                fn: function() {
                    GLOBALS.vars.activePanel.setAssignment();
                    this.layer.setVisibility(false);
                    proportionalSymbol.layer.setVisibility(false);
                    this.classify(false, true);
                }
            }
        }
    });
	
	/* Section: map toolbar */  
	var mapLabel = new Ext.form.Label({
		text: i18n_map,
		style: 'font:bold 11px arial; color:#333;'
	});
	
	var zoomInButton = new Ext.Button({
		iconCls: 'icon-zoomin',
		tooltip: i18n_zoom_in,
		handler:function() {
			GLOBALS.vars.map.zoomIn();
		}
	});
	
	var zoomOutButton = new Ext.Button({
		iconCls: 'icon-zoomout',
		tooltip: i18n_zoom_out,
		handler:function() {
			GLOBALS.vars.map.zoomOut();
		}
	});
	
	var zoomToVisibleExtentButton = new Ext.Button({
		iconCls: 'icon-zoommin',
		tooltip: i18n_zoom_to_visible_extent,
		handler: function() {
            if (GLOBALS.vars.activePanel.isPolygon()) {
                if (choropleth.layer.getDataExtent()) {
                    GLOBALS.vars.map.zoomToExtent(choropleth.layer.getDataExtent());
                }
                else {
                    Ext.message.msg(false, 'Vector layer is empty');
                }
            }
            else if (GLOBALS.vars.activePanel.isPoint()) {
                if (proportionalSymbol.layer.getDataExtent()) {
                    GLOBALS.vars.map.zoomToExtent(proportionalSymbol.layer.getDataExtent());
                }
                else {
                    Ext.message.msg(false, 'Vector layer is empty');
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
			viewWindow.setPosition(x,y);

			if (viewWindow.visible) {
				viewWindow.hide();
			}
			else {
				viewWindow.show();
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
                if (!GLOBALS.stores.predefinedMapLegend.isLoaded) {
                    GLOBALS.stores.predefinedMapLegend.load();
                }
                if (!GLOBALS.stores.indicator.isLoaded) {
                    GLOBALS.stores.indicator.load();
                }
                if (!GLOBALS.stores.dataElement.isLoaded) {
                    GLOBALS.stores.dataElement.load();
                }                
			}
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
		cls: 'x-btn-text-icon',
		ctCls: 'aa_med',
		icon: '../../images/exit.png',
		tooltip: i18n_return_to_DHIS_2_dashboard,
		handler: function() {
			window.location.href = '../../dhis-web-portal/redirect.action'
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
			exportImageButton,
			// exportExcelButton,
			// '-',
            ' ',
			favoritesButton,
			// '-',
            ' ',
            predefinedMapLegendSetButton,
			'-',
			helpButton,
			'->',
			exitButton,
            ' ',' '
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
                height: GLOBALS.conf.north_height
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
                width: GLOBALS.conf.west_width,
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
                    proportionalSymbol,
                    shapefilePanel,
                    mapping,
					adminPanel
                ]
            },
            {
                xtype: 'gx_mappanel',
                region: 'center',
                id: 'center',
                height: 1000,
                width: 800,
                map: GLOBALS.vars.map,
                title: '',
                zoom: 3,
				tbar: mapToolbar
            }
        ]
    });
	
    shapefilePanel.hide();
	mapping.hide();
	// Ext.getCmp('printMultiPage_p').hide();
	GLOBALS.vars.activePanel.setPolygon();

	GLOBALS.vars.map.addControl(new OpenLayers.Control.MousePosition({
        displayClass: 'void', 
        div: $('mouseposition'), 
        prefix: '<span style="color:#666;">x: &nbsp;</span>',
        separator: '<br/><span style="color:#666;">y: &nbsp;</span>'
    }));
    
    GLOBALS.vars.map.addControl(new OpenLayers.Control.OverviewMap({
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
    
    GLOBALS.vars.map.addControl(new OpenLayers.Control.ZoomBox());
    
    function toggleSelectFeatures(e) {
        if (GLOBALS.stores.overlay.find('name', e.layer.name) !== -1) {
            var names = GLOBALS.stores.overlay.collect('name');
            var visibleOverlays = false;
            
            for (var i = 0; i < names.length; i++) {
                if (GLOBALS.vars.map.getLayersByName(names[i])[0].visibility) {
                    visibleOverlays = true;
                }
            }
            
            var widget = GLOBALS.vars.activePanel.isPolygon() ? choropleth : proportionalSymbol;
            
            if (visibleOverlays) {
                widget.selectFeatures.deactivate();
            }
            else {
                widget.selectFeatures.activate();
            }
        }
    }
	
	GLOBALS.vars.map.events.on({
        changelayer: function(e) {
            if (e.layer.name != 'Polygon layer' && e.layer.name != 'Point layer') {
                if (e.property == 'visibility') {
                    if (!GLOBALS.stores.overlay.isLoaded) {
                        GLOBALS.stores.overlay.load({callback: function() {
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
            
    Ext.getCmp('mapsource_cb').setValue(GLOBALS.vars.mapSourceType.value);
    Ext.getCmp('mapdatetype_cb').setValue(GLOBALS.vars.mapDateType.value);
    
    choropleth.prepareMapViewValueType();
    proportionalSymbol.prepareMapViewValueType();
    
    choropleth.prepareMapViewDateType();
    proportionalSymbol.prepareMapViewDateType();
    
    choropleth.prepareMapViewLegend();
    proportionalSymbol.prepareMapViewLegend();
    
    choropleth.prepareMapViewMap();
    proportionalSymbol.prepareMapViewMap();
    
	}});
	}});
	}});
});