Ext.onReady( function() {
    Ext.BLANK_IMAGE_URL = '../resources/ext-ux/theme/gray-extend/gray-extend/s.gif';
	Ext.override(Ext.form.Field,{showField:function(){this.show();this.container.up('div.x-form-item').setDisplayed(true);},hideField:function(){this.hide();this.container.up('div.x-form-item').setDisplayed(false);}});
	Ext.QuickTips.init();
	document.body.oncontextmenu = function(){return false;};
	
	GLOBAL.vars.map = new OpenLayers.Map({controls:[new OpenLayers.Control.Navigation(),new OpenLayers.Control.ArgParser(),new OpenLayers.Control.Attribution()]});
	GLOBAL.vars.mask = new Ext.LoadMask(Ext.getBody(),{msg:i18n_loading,msgCls:'x-mask-loading2'});
    GLOBAL.vars.parameter = GLOBAL.util.getUrlParam('view') ? {id: GLOBAL.util.getUrlParam('view')} : false;

    Ext.Ajax.request({
        url: GLOBAL.conf.path_mapping + 'initialize' + GLOBAL.conf.type,
        method: 'POST',
        params: {id: GLOBAL.vars.parameter.id || null},
        success: function(r) {
            var init = Ext.util.JSON.decode(r.responseText);
            if (GLOBAL.vars.parameter) {
                GLOBAL.vars.parameter.mapView = init.mapView;
            }
            GLOBAL.vars.mapDateType.value = init.userSettings.mapDateType;
                        
    /* Section: stores */
    var mapViewStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getAllMapViews' + GLOBAL.conf.type,
        root: 'mapViews',
        fields: [ 'id', 'name', 'mapValueType', 'indicatorGroupId', 'indicatorId', 'dataElementGroupId', 'dataElementId',
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
        
    var mapStore = new Ext.data.JsonStore({
        url: GLOBAL.conf.path_mapping + 'getAllMaps' + GLOBAL.conf.type,
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
        url: GLOBAL.conf.path_mapping + 'getOrganisationUnitLevels' + GLOBAL.conf.type,
        root: 'organisationUnitLevels',
        fields: ['id', 'level', 'name'],
        autoLoad: false,
        isLoaded: false,
        listeners: {
            'load': function(store) {
                store.isLoaded = true;
                // Ext.getCmp('level_cb').mode = 'local';
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
        wmsCapabilities: wmsCapabilitiesStore,
        baseLayer: baseLayerStore,
        overlay: overlayStore
    };
	
	/* Add base layers */	
	function addBaseLayersToMap() {
		GLOBAL.vars.map.addLayers([new OpenLayers.Layer.WMS('World', 'http://labs.metacarta.com/wms/vmap0', {layers: 'basic'})]);
		GLOBAL.vars.map.layers[0].setVisibility(false);
		
		GLOBAL.stores.baseLayer.load({callback: function(r) {
			if (r.length) {
				for (var i = 0; i < r.length; i++) {
					GLOBAL.vars.map.addLayers([new OpenLayers.Layer.WMS(r[i].data.name, r[i].data.mapSource, {layers: r[i].data.layer})]);
					GLOBAL.vars.map.layers[GLOBAL.vars.map.layers.length-1].setVisibility(false);
				}
			}
		}});
	}	
	addBaseLayersToMap();	
    
	function addOverlaysToMap() {
		GLOBAL.stores.overlay.load({callback: function(r) {
			if (r.length) {
                var loadStart = function() {
                    GLOBAL.vars.mask.msg = i18n_loading;
                    GLOBAL.vars.mask.show();
                };
                var loadEnd = function() {
                    GLOBAL.vars.mask.hide();
                };
                
				for (var i = 0; i < r.length; i++) {
					var url = GLOBAL.vars.mapSourceType.isShapefile() ? GLOBAL.conf.path_geoserver + GLOBAL.conf.wfs + r[i].data.mapSource + GLOBAL.conf.output : GLOBAL.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + r[i].data.mapSource;
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
					
					overlay.events.register('loadstart', null, loadStart);					
					overlay.events.register('loadend', null, loadEnd);
                    overlay.isOverlay = true;
					GLOBAL.vars.map.addLayer(overlay);
				}
			}
		}});
	}
	addOverlaysToMap();
			
	/* Section: mapview */
	var viewNameTextField=new Ext.form.TextField({id:'viewname_tf',emptytext:'',width:GLOBAL.conf.combo_width,hideLabel:true,autoCreate:{tag:'input',type:'text',size:'20',autocomplete:'off', maxlength:'35'}});
	var deleteMapViewComboBox=new Ext.form.ComboBox({id:'view_cb',isFormField:true,hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:GLOBAL.conf.emptytext,selectOnFocus:true,width:GLOBAL.conf.combo_width,minListWidth:GLOBAL.conf.combo_width,store:GLOBAL.stores.mapView});
	var dashboardMapViewComboBox=new Ext.form.ComboBox({id:'view2_cb',isFormField:true,hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:GLOBAL.conf.emptytext,selectOnFocus:true,width:GLOBAL.conf.combo_width,minListWidth:GLOBAL.conf.combo_width,store:GLOBAL.stores.mapView});
    
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
				text: i18n_register,
				handler: function() {
					var vn = Ext.getCmp('viewname_tf').getValue();
                    
                    if (!vn) {
						Ext.message.msg(false, i18n_form_is_not_complete);
						return;
					}
                    
                    var formValues;
                    
                    if (GLOBAL.vars.activePanel.isPolygon()) {
                        if (!choropleth.formValidation.validateForm(true)) {
                            return;
                        }
                        formValues = choropleth.getFormValues();
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
                            longitude: formValues.longitude,
                            latitude: formValues.latitude,
                            zoom: formValues.zoom
                        },
                        success: function(r) {
                            Ext.message.msg(true, i18n_favorite + ' <span class="x-msg-hl">' + vn + '</span> ' + i18n_registered);
                            GLOBAL.stores.mapView.load();
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
                    
					var name = GLOBAL.stores.mapView.getById(v).get('name');				
					
					Ext.Ajax.request({
						url: GLOBAL.conf.path_mapping + 'deleteMapView' + GLOBAL.conf.type,
						method: 'POST',
						params: {id:v},
						success: function(r) {
							Ext.message.msg(true, i18n_favorite + ' <span class="x-msg-hl">' + name + '</span> ' + i18n_deleted);
                            GLOBAL.stores.mapView.load();
                            if (v == choropleth.form.findField('mapview').getValue()) {
                                choropleth.form.findField('mapview').clearValue();
                            }
                            if (v == symbol.form.findField('mapview').getValue()) {
                                symbol.form.findField('mapview').clearValue();
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
				text: i18n_add,
				cls: 'window-button',
				handler: function() {
					var v = Ext.getCmp('view2_cb').getValue();
					var rv = Ext.getCmp('view2_cb').getRawValue();
					
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
		width: 223,
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
                        title: '<span class="panel-tab-title">' + i18n_dhis_dashboard + '</span>',
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
				labelSeparator: GLOBAL.conf.labelseparator,
				editable: true,
				valueField: 'id',
				displayField: 'text',
				isFormField: true,
				width: GLOBAL.conf.combo_width_fieldset,
				mode: 'local',
				triggerAction: 'all'
			},
			{
				xtype: 'combo',
				id: 'exportimagequality_cb',
				fieldLabel: i18n_image_resolution,
				labelSeparator: GLOBAL.conf.labelseparator,
				editable: false,
				valueField: 'id',
				displayField: 'text',
				isFormField: true,
				width: GLOBAL.conf.combo_width_fieldset,
				minListWidth: GLOBAL.conf.combo_width_fieldset,
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
				labelSeparator: GLOBAL.conf.labelseparator,
				hideLabel: false,
				cls: 'window-button',
				text: i18n_export,
				handler: function() {
                    var vcb, period;
                    if (GLOBAL.vars.activePanel.isPolygon()) {
                        if (choropleth.formValidation.validateForm()) {
                            vcb = choropleth.form.findField('mapvaluetype').getValue() == GLOBAL.conf.map_value_type_indicator ? choropleth.form.findField('indicator').getValue() : choropleth.form.findField('dataelement').getValue();
                            period = GLOBAL.vars.mapDateType.isFixed() ? choropleth.form.findField('period').getRawValue() : new Date(choropleth.form.findField('startdate').getRawValue()).format('Y M j') + ' - ' + new Date(choropleth.form.findField('enddate').getRawValue()).format('Y M j');
                    
                            var svgElement = document.getElementsByTagName('svg')[0];
                            var parentSvgElement = svgElement.parentNode;                            
                            var svg = parentSvgElement.innerHTML;                            
                            var viewBox = svgElement.getAttribute('viewBox');
                            var title = Ext.getCmp('exportimagetitle_tf').getValue();
                            
                            if (!title) {
                                Ext.message.msg(false, i18n_form_is_not_complete);
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
                                document.getElementById('legendsField').value = GLOBAL.util.getLegendsJSON();

                                exportForm.submit();
                            }
                        }
                        else {
                            Ext.message.msg(false, i18n_please_render_map_first);
                        }
                    }
                    else if (GLOBAL.vars.activePanel.isPoint()) {
                        Ext.message.msg(false, 'Please use <span class="x-msg-hl">polygon layer</span> for printing');
                        return;
                    }
                    else {
                        Ext.message.msg(false, i18n_please_expand_layer_panel);
                        return;
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
				labelSeparator: GLOBAL.conf.labelseparator,
				editable: true,
				valueField: 'id',
				displayField: 'text',
				isFormField: true,
				width: GLOBAL.conf.combo_width_fieldset,
				minListWidth: GLOBAL.conf.combo_list_width_fieldset,
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
				labelSeparator: GLOBAL.conf.labelseparator,
				hideLabel: false,
				cls: 'window-button',
				text: i18n_export_excel,
				handler: function() {
                    var indicatorOrDataElement, period, mapOrOrganisationUnit;
					if (GLOBAL.vars.activePanel.isPolygon()) {
                        indicatorOrDataElement = Ext.getCmp('mapvaluetype_cb').getValue() == GLOBAL.conf.map_value_type_indicator ?
                            Ext.getCmp('indicator_cb').getValue() : Ext.getCmp('dataelement_cb').getValue();
                        period = Ext.getCmp('period_cb').getValue();
                        mapOrOrganisationUnit = GLOBAL.vars.mapSourceType.isDatabase() ?
                            Ext.getCmp('boundary_tf').getValue() : Ext.getCmp('map_cb').getValue();
                    }
                    else if (GLOBAL.vars.activePanel.isPoint()) {
                        indicatorOrDataElement = Ext.getCmp('mapvaluetype_cb2').getValue() == GLOBAL.conf.map_value_type_indicator ?
                            Ext.getCmp('indicator_cb2').getValue() : Ext.getCmp('dataelement_cb2').getValue();
                        period = Ext.getCmp('period_cb2').getValue();
                        mapOrOrganisationUnit = GLOBAL.vars.mapSourceType.isDatabase() ?
                            Ext.getCmp('map_tf2').getValue() : Ext.getCmp('map_cb2').getValue();
                    }
                    
                    if (indicatorOrDataElement && period && mapOrOrganisationUnit) {
                        var title = Ext.getCmp('exportexceltitle_ft').getValue();
                        var svg = document.getElementById('OpenLayers.Layer.Vector_17').innerHTML;	
                        var includeLegend = Ext.getCmp('exportexcelincludelegend_chb').getValue();
                        var includeValues = Ext.getCmp('exportexcelincludevalue_chb').getValue();
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
                        document.getElementById('legendsField').value = GLOBAL.util.getLegendsJSON();
                        document.getElementById('dataValuesField').value = GLOBAL.vars.exportValues;

                        exportForm.submit();
                    }
                    else {
                        Ext.message.msg(false, i18n_please_render_map_first);
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
            new Ext.form.TextField({id:'predefinedmaplegendname_tf',isFormField:true,hideLabel:true,emptyText:GLOBAL.conf.emptytext,width:GLOBAL.conf.combo_width}),
            { html: '<div class="window-field-label">'+i18n_start_value+'</div>' },
            new Ext.form.TextField({id:'predefinedmaplegendstartvalue_tf',isFormField:true,hideLabel:true,emptyText:GLOBAL.conf.emptytext,width:GLOBAL.conf.combo_number_width,minListWidth:GLOBAL.conf.combo_number_width}),
            { html: '<div class="window-field-label">'+i18n_end_value+'</div>' },
            new Ext.form.TextField({id:'predefinedmaplegendendvalue_tf',isFormField:true,hideLabel:true,emptyText:GLOBAL.conf.emptytext,width:GLOBAL.conf.combo_number_width,minListWidth:GLOBAL.conf.combo_number_width}),
            { html: '<div class="window-field-label">'+i18n_color+'</div>' },
            new Ext.ux.ColorField({id:'predefinedmaplegendcolor_cp',isFormField:true,hideLabel:true,allowBlank:false,width:GLOBAL.conf.combo_width,minListWidth:GLOBAL.conf.combo_width,value:"#FFFF00"}),
            {
                xtype: 'button',
                id: 'newpredefinedmaplegend_b',
				hideLabel: true,
                text: i18n_register,
				cls: 'window-button',
                handler: function() {
                    var mln = Ext.getCmp('predefinedmaplegendname_tf').getValue();
					var mlsv = parseFloat(Ext.getCmp('predefinedmaplegendstartvalue_tf').getValue());
					var mlev = parseFloat(Ext.getCmp('predefinedmaplegendendvalue_tf').getValue());
                    var mlc = Ext.getCmp('predefinedmaplegendcolor_cp').getValue();
                    
                    if (!Ext.isNumber(parseFloat(mlsv)) || !Ext.isNumber(mlev)) {
                        Ext.message.msg(false, 'Input invalid');
                        return;
                    }
					
					if (!mln || !mlsv || !mlev || !mlc) {
                        Ext.message.msg(false, i18n_form_is_not_complete);
                        return;
                    }
                    
                    if (!GLOBAL.util.validateInputNameLength(mln)) {
                        Ext.message.msg(false, i18n_name_can_not_longer_than_25);
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
                emptyText: GLOBAL.conf.emptytext,
                selectOnFocus: true,
                width: GLOBAL.conf.combo_width,
                minListWidth: GLOBAL.conf.combo_width,
                store: GLOBAL.stores.predefinedMapLegend
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
    });
	
	var newPredefinedMapLegendSetPanel = new Ext.form.FormPanel({   
        id: 'newpredefinedmaplegendset_p',
		bodyStyle: 'border:0px',
        items:
        [   
            { html: '<div class="window-field-label-first">'+i18n_display_name+'</div>' },
            new Ext.form.TextField({id:'predefinedmaplegendsetname_tf',hideLabel:true,emptyText:GLOBAL.conf.emptytext,width:GLOBAL.conf.combo_width}),
            { html: '<div class="window-field-label">'+i18n_legends+'</div>' },
			new Ext.ux.Multiselect({id:'predefinednewmaplegend_ms',hideLabel:true,dataFields:['id','name','startValue','endValue','color','displayString'],valueField:'id',displayField:'displayString',width:GLOBAL.conf.multiselect_width,height:GLOBAL.util.getMultiSelectHeight(),store:GLOBAL.stores.predefinedMapLegend}),
            {
                xtype: 'button',
                id: 'newpredefinedmaplegendset_b',
                text: i18n_register,
				cls: 'window-button',
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
            }
        ]
    });
	
	var deletePredefinedMapLegendSetPanel = new Ext.form.FormPanel({
        id: 'deletepredefinedmaplegendset_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [   
            { html: '<div class="window-field-label-first">' + i18n_legendset + '</p>' },
            new Ext.form.ComboBox({id:'predefinedmaplegendsetindicator_cb',hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:GLOBAL.conf.emptytext,selectOnFocus:true,width:GLOBAL.conf.combo_width,minListWidth:GLOBAL.conf.combo_width,store:GLOBAL.stores.predefinedMapLegendSet}),
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
    });
    
    var assignPredefinedMapLegendSetIndicatorPanel = new Ext.form.FormPanel({
        id: 'assignpredefinedmaplegendsetindicator_p',
		bodyStyle: 'border:0px',
        items:
        [
            { html: '<div class="window-field-label-first">' + i18n_legendset + '</div>' },
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
                emptyText: GLOBAL.conf.emptytext,
                selectOnFocus: true,
                width: GLOBAL.conf.combo_width,
                minListWidth: GLOBAL.conf.combo_width,
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
            }),
            { html: '<div class="window-field-label">' + i18n_indicators + '</div>' },
			new Ext.ux.Multiselect({id:'predefinedmaplegendsetindicator_ms',hideLabel:true,dataFields:['id','name','shortName'],valueField:'id',displayField:'shortName',width:GLOBAL.conf.multiselect_width,height:GLOBAL.util.getMultiSelectHeight(),store:GLOBAL.stores.indicator}),
            {
                xtype: 'button',
                id: 'assignpredefinedmaplegendsetindicator_b',
                text: i18n_assign,
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
    });
    
    var assignPredefinedMapLegendSetDataElementPanel = new Ext.form.FormPanel({
        id: 'assignpredefinedmaplegendsetdataelement_p',
		bodyStyle: 'border:0px',
        items:
        [
            { html: '<div class="window-field-label-first">'+i18n_legendset+'</div>' },
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
                emptyText: GLOBAL.conf.emptytext,
                selectOnFocus: true,
                width: GLOBAL.conf.combo_width,
                minListWidth: GLOBAL.conf.combo_width,
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
            }),
            { html: '<div class="window-field-label">' + i18n_dataelements + '</div>' },
			new Ext.ux.Multiselect({id:'predefinedmaplegendsetdataelement_ms',hideLabel:true,dataFields:['id','name','shortName'],valueField:'id',displayField:'shortName',width:GLOBAL.conf.multiselect_width,height:GLOBAL.util.getMultiSelectHeight(),store:GLOBAL.stores.dataElement}),
            {
                xtype: 'button',
                id: 'assignpredefinedmaplegendsetdataelement_b',
                text: i18n_assign,
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
    });
	
	var predefinedMapLegendSetWindow = new Ext.Window({
        id: 'predefinedmaplegendset_w',
        title: '<span id="window-predefinedlegendset-title">'+i18n_predefined_legend_sets+'</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: 485,
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
							w.setHeight(GLOBAL.util.getMultiSelectHeight() + 178);
						}
						else if (tab.id == 'predefinedmaplegendset3') {
							w.setHeight(151);
						}
                        else if (tab.id == 'predefinedmaplegendset4') {
                            w.setHeight(GLOBAL.util.getMultiSelectHeight() + 178);
                        }
                        else if (tab.id == 'predefinedmaplegendset5') {
                            w.setHeight(GLOBAL.util.getMultiSelectHeight() + 178);
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
                        title: '<span class="panel-tab-title">'+i18n_indicators+'</span>',
						id: 'predefinedmaplegendset4',
						items: [assignPredefinedMapLegendSetIndicatorPanel]
					},
					{
                        title: '<span class="panel-tab-title">'+i18n_dataelements+'</span>',
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
							getHelpText(GLOBAL.conf.thematicMap, tab.id);
                        }
                        else if (tab.id == 'help1') {
							getHelpText(GLOBAL.conf.mapRegistration, tab.id);
                        }
                        else if (tab.id == 'help2') {
                            getHelpText(GLOBAL.conf.organisationUnitAssignment, tab.id);
                        }
						if (tab.id == 'help3') { 
                            getHelpText(GLOBAL.conf.overlayRegistration, tab.id);
                        }
                        else if (tab.id == 'help4') {
                            getHelpText(GLOBAL.conf.administration, tab.id);
                        }
                        else if (tab.id == 'help5') {
                            getHelpText(GLOBAL.conf.favorites, tab.id);
                        }
						else if (tab.id == 'help6') {
                            getHelpText(GLOBAL.conf.legendSets, tab.id);
                        }
						else if (tab.id == 'help7') {
                            getHelpText(GLOBAL.conf.pdfprint, tab.id);
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
                        title: '<span class="panel-tab-title">' + i18n_legendset + '</span>',
                        id: 'help6'
                    },
                    {
                        title: '<span class="panel-tab-title">PDF print</span>',
                        id: 'help7'
                    }
                ]
            }
        ]
    });

    /* Section: map layers */
	var mapLayerNameTextField=new Ext.form.TextField({id:'maplayername_tf',emptyText:GLOBAL.conf.emptytext,hideLabel:true,width:GLOBAL.conf.combo_width});
	var mapLayerMapSourceFileComboBox=new Ext.form.ComboBox({id:'maplayermapsourcefile_cb',editable:false,displayField:'name',valueField:'name',emptyText:GLOBAL.conf.emptytext,hideLabel:true,width:GLOBAL.conf.combo_width,minListWidth:GLOBAL.conf.combo_width,triggerAction:'all',mode:'remote',store:GLOBAL.stores.geojsonFiles});
    
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
        store: GLOBAL.stores.wmsCapabilities,
        listeners: {
            'rowdblclick': mapOverlayPreview
        }
    });
	
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
		emptyText: GLOBAL.conf.emptytext,
		hideLabel: true,
        width: GLOBAL.conf.combo_width,
		listeners: {
			'focus': {
				fn: function() {
                    function show() {
                        var x = Ext.getCmp('center').x + 15;
                        var y = Ext.getCmp('center').y + 41;    
                        wmsOverlayWindow.show();
                        wmsOverlayWindow.setPosition(x,y);
                    }
                    
                    if (!GLOBAL.stores.wmsCapabilities.isLoaded) {
                        GLOBAL.stores.wmsCapabilities.load({callback: function() {
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
	
	var mapLayerFillColorColorField=new Ext.ux.ColorField({id:'maplayerfillcolor_cf',hideLabel:true,allowBlank:false,width:GLOBAL.conf.combo_width,value:'#FF0000'});
	var mapLayerFillOpacityComboBox=new Ext.form.ComboBox({id:'maplayerfillopacity_cb',hideLabel:true,editable:true,valueField:'value',displayField:'value',mode:'local',triggerAction:'all',width:GLOBAL.conf.combo_number_width,minListWidth:GLOBAL.conf.combo_number_width,value:0.5,store:new Ext.data.ArrayStore({fields:['value'],data:[[0.0],[0.1],[0.2],[0.3],[0.4],[0.5],[0.6],[0.7],[0.8],[0.9],[1.0]]})});
	var mapLayerStrokeColorColorField=new Ext.ux.ColorField({id:'maplayerstrokecolor_cf',hideLabel:true,allowBlank:false,width:GLOBAL.conf.combo_width,value:'#222222'});
	var mapLayerStrokeWidthComboBox=new Ext.form.ComboBox({id:'maplayerstrokewidth_cb',hideLabel:true,editable:true,valueField:'value',displayField:'value',mode:'local',triggerAction:'all',width:GLOBAL.conf.combo_number_width,minListWidth:GLOBAL.conf.combo_number_width,value:2,store:new Ext.data.ArrayStore({fields:['value'],data:[[0],[1],[2],[3],[4]]})});
	var mapLayerComboBox=new Ext.form.ComboBox({id:'maplayer_cb',typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:GLOBAL.conf.emptytext,hideLabel:true,selectOnFocus:true,width:GLOBAL.conf.combo_width,minListWidth:GLOBAL.conf.combo_width,store:GLOBAL.stores.overlay});
    
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
                url: GLOBAL.conf.path_mapping + 'deleteMapLayer' + GLOBAL.conf.type,
                method: 'POST',
                params: {id:ml},
                success: function(r) {
                    Ext.message.msg(true, i18n_overlay + ' <span class="x-msg-hl">' + mln + '</span> '+i18n_was_deleted);
                    GLOBAL.stores.overlay.load();
                    Ext.getCmp('maplayer_cb').clearValue();
                }
            });
            
            GLOBAL.vars.map.getLayersByName(mln)[0].destroy();
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
					
					if (GLOBAL.util.validateInputNameLength(mln)) {
						Ext.message.msg(false, i18n_overlay_name_cannot_be_longer_than_25_characters);
						return;
					}
                    
                    if (GLOBAL.stores.overlay.find('name', mln) !== -1) {
                        Ext.message.msg(false, i18n_name + ' <span class="x-msg-hl">' + mln + '</span> ' + i18n_is_already_in_use);
                        return;
                    }
                        
                    var ms = GLOBAL.vars.mapSourceType.isShapefile() ? mlwmso : mlmsf;
							
                    Ext.Ajax.request({
                        url: GLOBAL.conf.path_mapping + 'addOrUpdateMapLayer' + GLOBAL.conf.type,
                        method: 'POST',
                        params: {name: mln, type: 'overlay', mapSource: ms, fillColor: mlfc, fillOpacity: mlfo, strokeColor: mlsc, strokeWidth: mlsw},
                        success: function(r) {
                            Ext.message.msg(true, 'The overlay <span class="x-msg-hl">' + mln + '</span> '+i18n_was_registered);
                            GLOBAL.stores.overlay.load();
                    
                            var mapurl = GLOBAL.vars.mapSourceType.isShapefile() ?
                                GLOBAL.conf.path_geoserver + GLOBAL.conf.wfs + mlwmso + GLOBAL.conf.output : GLOBAL.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + mlmsf;
                            
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
							Ext.getCmp('overlays_w').setHeight(390);                        
                        }
                        else if (tab.id == 'overlay1') {
							Ext.getCmp('overlays_w').setHeight(150);
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
                    mapLayerMapSourceFileComboBox.show();
                    mapLayerPathWMSOverlayTextField.hide();
				}
			}
		}
    });
    
    var mapLayerBaseLayersNameTextField=new Ext.form.TextField({id:'maplayerbaselayersname_tf',emptyText:GLOBAL.conf.emptytext,hideLabel:true,width:GLOBAL.conf.combo_width});
    var mapLayerBaseLayersUrlTextField=new Ext.form.TextField({id:'maplayerbaselayersurl_tf',emptyText:GLOBAL.conf.emptytext,hideLabel:true,width:GLOBAL.conf.combo_width});
    var mapLayerBaseLayersLayerTextField=new Ext.form.TextField({id:'maplayerbaselayerslayer_tf',emptyText:GLOBAL.conf.emptytext,hideLabel:true,width:GLOBAL.conf.combo_width});
    
    
	var mapLayerBaseLayerComboBox=new Ext.form.ComboBox({id:'maplayerbaselayers_cb',typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:GLOBAL.conf.emptytext,hideLabel:true,selectOnFocus:true,width:GLOBAL.conf.combo_width,minListWidth:GLOBAL.conf.combo_width,store:GLOBAL.stores.baseLayer});
    
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
                url: GLOBAL.conf.path_mapping + 'deleteMapLayer' + GLOBAL.conf.type,
                method: 'POST',
                params: {id: ml},
                success: function(r) {
                    Ext.message.msg(true, i18n_baselayer + ' <span class="x-msg-hl">' + mln + '</span> '+i18n_was_deleted);
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
					
					if (GLOBAL.util.validateInputNameLength(mlbn)) {
						Ext.message.msg(false, i18n_baselayer_name_cannot_be_longer_than_25_characters);
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
                            Ext.message.msg(true, 'The base layer <span class="x-msg-hl">' + mlbn + '</span> ' + i18n_was_registered);
                            GLOBAL.stores.baseLayer.load();
                            GLOBAL.vars.map.addLayers([
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
                        labelSeparator: GLOBAL.conf.labelseparator,
                        editable: false,
                        valueField: 'value',
                        displayField: 'text',
                        mode: 'local',
                        value: GLOBAL.conf.map_date_type_fixed,
                        triggerAction: 'all',
						width: GLOBAL.conf.combo_width_fieldset,
						minListWidth: GLOBAL.conf.combo_width_fieldset,
                        store: new Ext.data.ArrayStore({
                            fields: ['value', 'text'],
                            data: [
                                [GLOBAL.conf.map_date_type_fixed, i18n_fixed_periods],
                                [GLOBAL.conf.map_date_type_start_end, i18n_start_end_dates]
                            ]
                        }),
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
        ],
        listeners: {
            expand: function() {					
                GLOBAL.vars.activePanel.value = GLOBAL.conf.administration;
            },
			collapse: function() {
                GLOBAL.vars.activePanel.value = null;
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
            height: GLOBAL.util.getMultiSelectHeight() + 145,
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
                                    labelSeparator: GLOBAL.conf.labelseparator,
                                    id: 'highlightcolor_cf',
                                    allowBlank: false,
                                    isFormField: true,
                                    width: GLOBAL.conf.combo_width,
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
                                    height: GLOBAL.util.getMultiSelectHeight(),
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
                        GLOBAL.vars.locateFeatureWindow = false;
                        layer.redraw();
                    }
                }
            }
        });
        
        GLOBAL.vars.locateFeatureWindow = locateFeatureWindow;
        
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
                                                if (GLOBAL.vars.activePanel.isPolygon()) {
                                                    GLOBAL.util.toggleFeatureLabels(choropleth);
                                                }
                                                else {
                                                    Ext.message.msg(false, 'Please use <span class="x-msg-hl">Point layer</span> options');
                                                }
                                            }
                                            else if (layer.name == 'Point layer') {
                                                if (GLOBAL.vars.activePanel.isPoint()) {
                                                    GLOBAL.util.toggleFeatureLabels(symbol);
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
						showWMSLayerOptions(GLOBAL.vars.map.getLayersByName(n.attributes.layer.name)[0]);
					}
                    else if (n.parentNode.attributes.text == 'Overlays') {
                        showVectorLayerOptions(GLOBAL.vars.map.getLayersByName(n.attributes.layer.name)[0]);
                    }
					else if (n.isLeaf()) {
                        showVectorLayerOptions(GLOBAL.vars.map.getLayersByName(n.attributes.layer)[0]);
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
            }
        }
    });
    
    mapping = new mapfish.widgets.geostat.Mapping({});
	
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
                if (!GLOBAL.stores.predefinedMapLegend.isLoaded) {
                    GLOBAL.stores.predefinedMapLegend.load();
                }
                if (!GLOBAL.stores.indicator.isLoaded) {
                    GLOBAL.stores.indicator.load();
                }
                if (!GLOBAL.stores.dataElement.isLoaded) {
                    GLOBAL.stores.dataElement.load();
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
			favoritesButton, ' ',
            predefinedMapLegendSetButton, ' ',
			exportImageButton,
			'-',
			helpButton,
			'->',
			exitButton, ' ',' '
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
                height: GLOBAL.conf.north_height
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
                    symbol,
					adminPanel
                ]
            },
            {
                xtype: 'gx_mappanel',
                region: 'center',
                id: 'center',
                height: 1000,
                width: 800,
                map: GLOBAL.vars.map,
                title: '',
                zoom: 3,
				tbar: mapToolbar
            }
        ]
    });
	
	GLOBAL.vars.activePanel.setPolygon();

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