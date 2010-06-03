Ext.BLANK_IMAGE_URL = '../resources/ext/resources/images/default/s.gif';

/* OpenLayers map */
var MAP;
/* Center point of the country */
var BASECOORDINATE;
/* Geojson, shapefile or database */
var MAPSOURCE;
/* A map object */
var MAPDATA;
/* Filename or level */
var URL;
/* Active mapview object  */
var MAPVIEW;
/* Active mapview id parameter from URL */
var PARAMETER;
/* Current expanded accordion panel */
var ACTIVEPANEL;
/* Mask */
var MASK;
/* Boolean */
var LABELS;
/* Legend colors for export */
var COLORINTERPOLATION;
/* Export values */
var EXPORTVALUES;
/* Currently selected vector feature */
var FEATURE;
/* Global chart for show/hide */
var CHART;
/* Current legend type and method */
var LEGEND = new Object();
LEGEND.type = map_legend_type_automatic;
LEGEND.method = 1;
LEGEND.classes = 5;

/* Detect mapview parameter in URL */
function getUrlParam(strParamName){var output='';var strHref=window.location.href;if(strHref.indexOf('?')>-1){var strQueryString=strHref.substr(strHref.indexOf('?')).toLowerCase();var aQueryString=strQueryString.split('&');for(var iParam=0;iParam<aQueryString.length;iParam++){if(aQueryString[iParam].indexOf(strParamName.toLowerCase()+'=')>-1){var aParam=aQueryString[iParam].split('=');output=aParam[1];break;}}}return unescape(output);}
/* Input validation */
function validateInput(name){return (name.length<=25);}
/* Decide multiselect height based on screen resolution */
function getMultiSelectHeight(){var h=screen.height;if(h<=800){return 220;}else if(h<=1050){return 310;}else if(h<=1200){return 470;}else{return 900;}}
/* Toggle feature labels */
function toggleFeatureLabels(classify){var layer=MAP.getLayersByName('Thematic map')[0];function activateLabels(){layer.styleMap=new OpenLayers.StyleMap({'default':new OpenLayers.Style(OpenLayers.Util.applyDefaults({'fillOpacity':1,'strokeColor':'#222222','strokeWidth':1,'label':'${'+MAPDATA.nameColumn+'}','fontFamily':'arial,lucida sans unicode','fontWeight':'bold','fontSize':14},OpenLayers.Feature.Vector.style['default'])),'select':new OpenLayers.Style({'strokeColor':'#000000','strokeWidth':2,'cursor':'pointer'})});layer.refresh();LABELS=true;}function deactivateLabels(){layer.styleMap=new OpenLayers.StyleMap({'default':new OpenLayers.Style(OpenLayers.Util.applyDefaults({'fillOpacity':1,'strokeColor':'#222222','strokeWidth':1},OpenLayers.Feature.Vector.style['default'])),'select':new OpenLayers.Style({'strokeColor':'#000000','strokeWidth':2,'cursor':'pointer'})});layer.refresh();LABELS=false;}if(classify){if(LABELS){deactivateLabels();}else{activateLabels();}if(ACTIVEPANEL==thematicMap){choropleth.classify(false,true);}else if(ACTIVEPANEL==organisationUnitAssignment){mapping.classify(false,true);}}else{if(LABELS){activateLabels();}}}
/* Sort method */
function sortByValue(a,b){return b.value-a.value;}
/* Create JSON for map export */
function getExportDataValueJSON(mapvalues){var json='{';json+='"datavalues":';json+='[';mapvalues.sort(sortByValue);for(var i=0;i<mapvalues.length;i++){json+='{';json+='"organisation": "'+mapvalues[i].orgUnitId+'",';json+='"value": "'+mapvalues[i].value+'" ';json+=i<mapvalues.length-1?'},':'}'}json+=']';json+='}';return json}
function getLegendsJSON(){var legends=choropleth.imageLegend;var json='{';json+='"legends":';json+='[';for(var i=0;i<choropleth.imageLegend.length;i++){json+='{';json+='"label": "'+choropleth.imageLegend[i].label+'",';json+='"color": "'+choropleth.imageLegend[i].color+'" ';json+=i<choropleth.imageLegend.length-1?'},':'}'}json+=']';json+='}';return json}

Ext.onReady( function() {
	/* Cookie provider */
	Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
	/* Ext 3.2.0 override */
	Ext.override(Ext.form.Field,{showField:function(){this.show();this.container.up('div.x-form-item').setDisplayed( true );},hideField:function(){this.hide();this.container.up('div.x-form-item').setDisplayed( false );}});
    /* Disallow right clicks */
	document.body.oncontextmenu = function(){return false;};
	/* Activate tooltip */
	Ext.QuickTips.init();

    MAP = new OpenLayers.Map({controls:[new OpenLayers.Control.Navigation(),new OpenLayers.Control.ArgParser(),new OpenLayers.Control.Attribution()]});
	MASK = new Ext.LoadMask(Ext.getBody(),{msg:i18n_loading,msgCls:'x-mask-loading2'});
    
    /* Base layers */
    function addBaseLayersToMap() {
        Ext.Ajax.request({
            url: path + 'getMapLayersByType' + type,
            params: { type: map_layer_type_baselayer },
            method: 'POST',
            success: function(r) {
                var mapLayers = Ext.util.JSON.decode(r.responseText).mapLayers;
				
				if (mapLayers.length > 0) {
					for (var i = 0; i < mapLayers.length; i++) {
						MAP.addLayers([
							new OpenLayers.Layer.WMS(
								mapLayers[i].name,
								mapLayers[i].mapSource,
								{layers: mapLayers[i].layer}
							)
						]);
						MAP.layers[MAP.layers.length-1].setVisibility(false);
					}
				}
				else {
					MAP.addLayers([
						new OpenLayers.Layer.WMS(
							'World',
							'http://labs.metacarta.com/wms/vmap0',
							{layers: 'basic'}
						)
					]);
					
					MAP.getLayersByName('World')[0].setVisibility(false);
				}
            }
        });
    }
    
    addBaseLayersToMap();
    
    /* Get map view parameter and apply to global variable */
    if (getUrlParam('view')){PARAMETER=getUrlParam('view');}
	var mapViewParam = PARAMETER || 0;
	
	Ext.Ajax.request({
		url: path + 'getBaseCoordinate' + type,
		method: 'GET',
		success: function(r) {
			var bc = Ext.util.JSON.decode( r.responseText ).baseCoordinate;
			BASECOORDINATE = {longitude:bc[0].longitude, latitude:bc[0].latitude};
			
			Ext.Ajax.request({
				url: path + 'getMapView' + type,
				method: 'GET',
				params: { id: mapViewParam },
				success: function(r) {
					var mst = Ext.util.JSON.decode(r.responseText).mapView[0].mapSourceType;
					
					Ext.Ajax.request({
						url: path + 'getMapSourceTypeUserSetting' + type,
						method: 'GET',
						success: function(r) {
							var ms = Ext.util.JSON.decode(r.responseText).mapSource;
							MAPSOURCE = PARAMETER ? mst : ms;
							
							Ext.Ajax.request({
								url: path + 'setMapSourceTypeUserSetting' + type,
								method: 'POST',
								params: { mapSourceType: MAPSOURCE },
								success: function() {
			
	/* Section: mapview */
	var viewStore=new Ext.data.JsonStore({url:path+'getAllMapViews'+type,root:'mapViews',fields:['id','name'],id:'id',sortInfo:{field:'name',direction:'ASC'},autoLoad:true});
	var viewNameTextField=new Ext.form.TextField({id:'viewname_tf',emptyText:'',width:combo_width,hideLabel:true});
	var viewComboBox=new Ext.form.ComboBox({id:'view_cb',isFormField:true,hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:emptytext,selectOnFocus:true,width:combo_width,minListWidth:combo_width,store:viewStore});
	var view2ComboBox=new Ext.form.ComboBox({id:'view2_cb',isFormField:true,hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:emptytext,selectOnFocus:true,width:combo_width,minListWidth:combo_width,store:viewStore});
    
    var newViewPanel = new Ext.form.FormPanel({
        id: 'newview_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [
            { html: '<div class="window-info">'+i18n_saving_current_thematic_map_selection+'</div>' },
            { html: '<div class="window-field-label-first">'+i18n_display_name+'</div>' },
			viewNameTextField,
			{
				xtype: 'button',
                id: 'newview_b',
				isFormField: true,
				hideLabel: true,
				cls: 'window-button',
				text: i18n_save ,
				handler: function() {
					var vn = Ext.getCmp('viewname_tf').getValue();
					var ig = Ext.getCmp('indicatorgroup_cb').getValue();
					var ii = Ext.getCmp('indicator_cb').getValue();
					var pt = Ext.getCmp('periodtype_cb').getValue();
					var p = Ext.getCmp('period_cb').getValue();
					var ms = Ext.getCmp('map_cb').getValue();
					var mlt = Ext.getCmp('maplegendtype_cb').getValue();
					var c = Ext.getCmp('numClasses').getValue();
					var ca = Ext.getCmp('colorA_cf').getValue();
					var cb = Ext.getCmp('colorB_cf').getValue();
					var mlsid = Ext.getCmp('maplegendset_cb').getValue() || 0;
					var lon = MAP.getCenter().lon;
					var lat = MAP.getCenter().lat;
					var zoom = parseInt(MAP.getZoom());
					
					if (!vn) {
						Ext.messageRed.msg( i18n_new_map_view , i18n_map_view_form_is_not_complete );
						return;
					}
					
					if (!ig || !ii || !pt || !p || !ms || !c ) {
						Ext.messageRed.msg( i18n_new_map_view, i18n_thematic_map_form_is_not_complete );
						return;
					}
					
					if (validateInput(vn) == false) {
						Ext.messageRed.msg( i18n_new_map_view , i18n_map_view_name_cannot_be_longer_than_25_characters );
						return;
					}
					
					Ext.Ajax.request({
						url: path + 'getAllMapViews' + type,
						method: 'GET',
						success: function(r) {
							var mapViews = Ext.util.JSON.decode(r.responseText).mapViews;
							
							for (var i = 0; i < mapViews.length; i++) {
								if (mapViews[i].name == vn) {
									Ext.messageRed.msg(	i18n_new_map_view , i18n_there_is_already_a_map_view_called + ' <span class="x-msg-hl">' + vn + '</span>.');
									return;
								}
							}
					
							Ext.Ajax.request({
								url: path + 'addOrUpdateMapView' + type,
								method: 'POST',
								params: { name: vn, indicatorGroupId: ig, indicatorId: ii, periodTypeId: pt, periodId: p, mapSource: ms, mapLegendType: mlt, method: 2, classes: c, colorLow: ca, colorHigh: cb, mapLegendSetId: mlsid, longitude: lon, latitude: lat, zoom: zoom },

								success: function(r) {
									Ext.messageBlack.msg( i18n_new_map_view, 'The view <span class="x-msg-hl">' + vn + '</span> ' + i18n_was_registered);
									Ext.getCmp('view_cb').getStore().reload();
									Ext.getCmp('mapview_cb').getStore().reload();
									Ext.getCmp('viewname_tf').reset();
								},
								failure: function() {
									alert( 'Error: addOrUpdateMapView' );
								}
							});
						},
						failure: function() {
									alert( 'Error: getAllMapViews' );
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
            { html: '<div class="window-field-label-first">'+i18n_view+'</div>' },
			viewComboBox,
			{
				xtype: 'button',
                id: 'deleteview_b',
				isFormField: true,
				hideLabel: true,
				text: i18n_delete,
				cls: 'window-button',
				handler: function() {
					var v = Ext.getCmp('view_cb').getValue();
					var name = Ext.getCmp('view_cb').getStore().getById(v).get('name');
					
					if (!v) {
						Ext.messageRed.msg( i18n_delete_map_view , i18n_please_select_a_map_view );
						return;
					}
					
					Ext.Ajax.request({
						url: path + 'deleteMapView' + type,
						method: 'POST',
						params: { id: v },

						success: function(r) {
							Ext.messageBlack.msg( i18n_delete_map_view , 'The map view <span class="x-msg-hl">' + name + '</span> '+ i18n_was_deleted );
							Ext.getCmp('view_cb').getStore().reload();
							Ext.getCmp('view_cb').reset();
							Ext.getCmp('mapview_cb').getStore().reload();
						},
						failure: function() {
							alert( i18n_status , i18n_error_while_saving_data );
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
			view2ComboBox,
			{
				xtype: 'button',
                id: 'dashboardview_b',
				isFormField: true,
				hideLabel: true,
				text: 'Add to DHIS dashboard',
				cls: 'window-button',
				handler: function() {
					var v2 = Ext.getCmp('view2_cb').getValue();
					var nv = Ext.getCmp('view2_cb').getRawValue();
					
					if (!v2) {
						Ext.messageRed.msg( i18n_dashboard_map_view , i18n_please_select_a_map_view );
						return;
					}
					
					Ext.Ajax.request({
						url: path + 'addMapViewToDashboard' + type,
						method: 'POST',
						params: { id: v2 },

						success: function(r) {
							Ext.messageBlack.msg( i18n_dashboard_map_view, the_view + ' <span class="x-msg-hl">' + nv + '</span> ' + i18n_was_added_to_dashboard );
							
							Ext.getCmp('view_cb').getStore().reload();
							Ext.getCmp('view_cb').reset();
							Ext.getCmp('mapview_cb').getStore().reload();
						},
						failure: function() {
							alert( i18n_status , i18n_error_while_saving_data );
						}
					});
				}
			}
        ]
    });
    
	var viewWindow = new Ext.Window({
        id: 'view_w',
        title: '<span id="window-favorites-title">'+i18n_favorite+'</span>',
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
                defaults: {layout: 'fit', bodyStyle: 'padding:8px; border:0px'},
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
                items:
                [
                    {
                        title: '<span class="panel-tab-title">'+i18n_new+'</span>',
                        id: 'view0',
                        items:
                        [
							newViewPanel
                        ]
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_delete+'</span>',
                        id: 'view1',
                        items:
                        [
                            deleteViewPanel
                        ]
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_add_to_dashboard+'</span>',
                        id: 'view2',
                        items:
                        [
                            dashboardViewPanel
                        ]
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
				labelSeparator: labelseparator,
				editable: true,
				valueField: 'id',
				displayField: 'text',
				isFormField: true,
				width: combo_width_fieldset,
				minListWidth: combo_list_width_fieldset,
				mode: 'local',
				triggerAction: 'all'						
			},
			{
				xtype: 'combo',
				id: 'exportimageformat_cb',
				fieldLabel: i18n_image_format,
				labelSeparator: labelseparator,
				editable: false,
				valueField: 'id',
				displayField: 'text',
				isFormField: true,
				width: combo_width_fieldset,
				minListWidth: combo_list_width_fieldset,
				mode: 'local',
				triggerAction: 'all',
				value: 'image/jpeg',
				store: new Ext.data.SimpleStore({
					fields: ['id', 'text'],
					data: [['image/png', 'PNG'], ['image/jpeg', 'JPEG']]
				})					
			},
			{
				xtype: 'combo',
				id: 'exportimagequality_cb',
				fieldLabel: i18n_image_quality,
				labelSeparator: labelseparator,
				editable: false,
				valueField: 'id',
				displayField: 'text',
				isFormField: true,
				width: combo_width_fieldset,
				minListWidth: combo_list_width_fieldset,
				mode: 'local',
				triggerAction: 'all',
				value: 1,
				store: new Ext.data.SimpleStore({
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
				labelSeparator: labelseparator,
				hideLabel: false,
				cls: 'window-button',
				text: i18n_export_image,
				handler: function() {
					if (ACTIVEPANEL == thematicMap
						&& Ext.getCmp('period_cb').getValue()!='' 
						&& Ext.getCmp('indicator_cb').getValue()!=''
						&& Ext.getCmp('map_cb').getValue()!='') {
						
						var svg = document.getElementById('OpenLayers.Layer.Vector_17').innerHTML;
						var objectSVGDocument = document.getElementById('OpenLayers.Layer.Vector_17').childNodes[0];
						var viewBox = objectSVGDocument.getAttribute('viewBox');
						var title = Ext.getCmp('exportimagetitle_tf').getValue();
						
						if(title==''){
							Ext.messageRed.msg( i18n_export_map_as_image , i18n_please_enter_map_title );
						}else{						
							var q = Ext.getCmp('exportimagequality_cb').getValue();
							var w = objectSVGDocument.getAttribute('width') * q;
							var h = objectSVGDocument.getAttribute('height') * q;
							var includeLegend = Ext.getCmp('exportimageincludelegend_chb').getValue();
							var period = Ext.getCmp('period_cb').getValue();
							var indicator = Ext.getCmp('indicator_cb').getValue();
							var imageFormat = Ext.getCmp('exportimageformat_cb').getValue();
							
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
							document.getElementById('indicatorField').value = indicator;   
							document.getElementById('legendsField').value = getLegendsJSON();
							document.getElementById('imageFormat').value = imageFormat;

							exportForm.submit();
						}
					}
					else {
						Ext.messageRed.msg( i18n_export_map_as_image , i18n_please_render_map_fist );
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
				labelSeparator: labelseparator,
				editable: true,
				valueField: 'id',
				displayField: 'text',
				isFormField: true,
				width: combo_width_fieldset,
				minListWidth: combo_list_width_fieldset,
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
				labelSeparator: labelseparator,
				hideLabel: false,
				cls: 'window-button',
				text: i18n_export_excel,
				handler: function() {
					if (ACTIVEPANEL == thematicMap
						&& Ext.getCmp('period_cb').getValue()!='' 
						&& Ext.getCmp('indicator_cb').getValue()!=''
						&& Ext.getCmp('map_cb').getValue()!='') {
												
						var title = Ext.getCmp('exportexceltitle_ft').getValue();
						var svg = document.getElementById('OpenLayers.Layer.Vector_17').innerHTML;	
						var includeLegend = Ext.getCmp('exportexcelincludelegend_chb').getValue();
						var includeValues = Ext.getCmp('exportexcelincludevalue_chb').getValue();
						var period = Ext.getCmp('period_cb').getValue();
						var indicator = Ext.getCmp('indicator_cb').getValue();
						
						Ext.getCmp('exportexceltitle_ft').reset();
											
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
                        document.getElementById('legendsField').value = getLegendsJSON();
                        document.getElementById('dataValuesField').value = EXPORTVALUES;

                        exportForm.submit();
					}
					else {
						Ext.messageRed.msg( i18n_export_excel, i18n_please_render_map_fist );
					}
				}
			}	
		]
	});
	
	var exportImageWindow=new Ext.Window({id:'exportimage_w',title:'<span id="window-image-title">'+ i18n_export_map_as_image +'</span>',layout:'fit',closeAction:'hide',defaults:{layout:'fit',bodyStyle:'padding:8px; border:0px'},width:250,height:190,items:[{xtype:'panel',items:[exportImagePanel]}]});
	var exportExcelWindow=new Ext.Window({id:'exportexcel_w',title:'<span id="window-excel-title">'+i18n_export_excel+'</span>',layout:'fit',closeAction:'hide',defaults:{layout:'fit',bodyStyle:'padding:8px; border:0px'},width:260,height:157,items:[{xtype:'panel',items:[exportExcelPanel]}]});
	
    /* Section: automatic legend set */
	var automaticMapLegendSetNameTextField=new Ext.form.TextField({id:'automaticmaplegendsetname_tf',isFormField:true,hideLabel:true,emptyText:emptytext,width:combo_width});
	var automaticMapLegendSetMethodComboBox=new Ext.form.ComboBox({id:'automaticmaplegendsetmethod_cb',isFormField:true,hideLabel:true,editable:false,valueField:'value',displayField:'text',mode:'local',emptyText:emptytext,triggerAction:'all',width:combo_width,minListWidth:combo_width,store:new Ext.data.SimpleStore({fields:['value','text'],data:[[2,'Distributed values'],[1,'Equal intervals']]})});
	var automaticMapLegendSetClassesComboBox=new Ext.form.ComboBox({id:'automaticmaplegendsetclasses_cb',isFormField:true,hideLabel:true,editable:false,valueField:'value',displayField:'value',mode:'local',emptyText:emptytext,triggerAction:'all',value:5,width:combo_number_width,minListWidth:combo_number_width,store:new Ext.data.SimpleStore({fields:['value'],data:[[1],[2],[3],[4],[5],[6],[7],[8]]})});
	var automaticMapLegendSetLowColorColorPalette=new Ext.ux.ColorField({id:'automaticmaplegendsetlowcolor_cp',isFormField:true,hideLabel:true,allowBlank:false,width:combo_width,minListWidth:combo_width,value:"#FFFF00"});
	var automaticMapLegendSetHighColorColorPalette=new Ext.ux.ColorField({id:'automaticmaplegendsethighcolor_cp',isFormField:true,hideLabel:true,allowBlank:false,width:combo_width,minListWidth:combo_width,value:"#FF0000"});
	var automaticMapLegendSetStore=new Ext.data.JsonStore({url:path+'getMapLegendSetsByType'+type,baseParams:{type:map_legend_type_automatic},root:'mapLegendSets',id:'id',fields:['id','name'],sortInfo:{field:'name',direction:'ASC'},autoLoad:true});
	
	var automaticMapLegendSetComboBox = new Ext.form.ComboBox({
        id: 'automaticmaplegendset_cb',
		isFormField: true,
		hideLabel: true,
        typeAhead: true,
        editable: false,
        valueField: 'id',
        displayField: 'name',
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        emptyText: emptytext,
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width,
        store: automaticMapLegendSetStore,
		listeners:{
			'select': {
				fn: function() {
					var lsid = Ext.getCmp('automaticmaplegendset_cb').getValue();
					
					Ext.Ajax.request({
						url: path + 'getMapLegendSetIndicators' + type,
						method: 'POST',
						params: { id:lsid },
						success: function(r) {
							var indicators = Ext.util.JSON.decode(r.responseText).mapLegendSet[0].indicators;
							var indicatorString = '';
							
							for (var i = 0; i < indicators.length; i++) {
								indicatorString += indicators[i];
								if (i < indicators.length-1) {
									indicatorString += ',';
								}
							}
							
							Ext.getCmp('automaticmaplegendsetindicator_ms').setValue(indicatorString);							
						},
						failure: function() {
							alert( i18n_status , i18n_error_while_saving_data );
						}
					});
				}
			}
		}					
    });
	
	var automaticMapLegendSetIndicatorStore=new Ext.data.JsonStore({url:path+'getAllIndicators'+type,root:'indicators',fields:['id','name','shortName'],sortInfo:{field:'name',direction:'ASC'},autoLoad:true});
	var automaticMapLegendSetIndicatorMultiSelect=new Ext.ux.Multiselect({id:'automaticmaplegendsetindicator_ms',isFormField:true,hideLabel:true,dataFields:['id','name','shortName'],valueField:'id',displayField:'shortName',width:multiselect_width,height:getMultiSelectHeight(),store:automaticMapLegendSetIndicatorStore});
	var automaticMapLegendSet2ComboBox=new Ext.form.ComboBox({id:'automaticmaplegendset2_cb',isFormField:true,hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:emptytext,selectOnFocus:true,width:combo_width,minListWidth:combo_width,store:automaticMapLegendSetStore});

	var newAutomaticMapLegendSetPanel = new Ext.form.FormPanel({   
        id: 'newautomaticmaplegendset_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [   
            { html: '<div class="window-field-label-first">'+i18n_display_name+'</div>' },
            automaticMapLegendSetNameTextField,
/*            { html: '<p style="padding-bottom:4px; color:' + MENU_TEXTCOLOR + ';">&nbsp;Method</p>' }, legendSetMethodComboBox, { html: '<br>' },*/
            { html: '<div class="window-field-label">'+i18n_classes+'</div>' },
            automaticMapLegendSetClassesComboBox,
            { html: '<div class="window-field-label">'+i18n_low_color+'</div>' },
            automaticMapLegendSetLowColorColorPalette,
            { html: '<div class="window-field-label">'+i18n_high_color+'</div>' },
            automaticMapLegendSetHighColorColorPalette,
            {
                xtype: 'button',
                id: 'newautomaticmaplegendset_b',
				isFormField: true,
				hideLabel: true,
                text: i18n_save,
				cls: 'window-button',
                handler: function() {
                    var ln = Ext.getCmp('automaticmaplegendsetname_tf').getValue();
        /*            var lm = Ext.getCmp('automaticmaplegendsetmethod_cb').getValue();*/
                    var lc = Ext.getCmp('automaticmaplegendsetclasses_cb').getValue();            
                    var llc = Ext.getCmp('automaticmaplegendsetlowcolor_cp').getValue();
                    var lhc = Ext.getCmp('automaticmaplegendsethighcolor_cp').getValue();
                    
                    if (!ln || !lc) {
                        Ext.messageRed.msg( i18n_new_legend_set, i18n_form_is_not_complete );
                        return;
                    }
                    
                    if (validateInput(ln) == false) {
                        Ext.messageRed.msg( i18n_new_legend_set,  i18n_name_can_not_longer_than_25 );
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: path + 'getAllMapLegendSets' + type,
                        method: 'GET',
						success: function(r) {
                            var mapLegendSets = Ext.util.JSON.decode(r.responseText).mapLegendSets;
                            for (var i = 0; i < mapLegendSets.length; i++) {
                                if (ln == mapLegendSets[i].name) {
                                    Ext.messageRed.msg( i18n_new_legend_set, i18n_a_legend_set_called+' <span class="x-msg-hl">' + ln + '</span> ' + i18n_already_exists );
                                    return;
                                }
                            }
                            
                            Ext.Ajax.request({
                                url: path + 'addOrUpdateMapLegendSet' + type,
                                method: 'POST',
                                params: { name: ln, type: map_legend_type_automatic, method: 2, classes: lc, colorLow: llc, colorHigh: lhc },
                                success: function(r) {
                                    Ext.messageBlack.msg( i18n_new_legend_set , i18n_legend_set + ' <span class="x-msg-hl">' + ln + '</span> ' + i18n_was_registered);
                                    Ext.getCmp('automaticmaplegendset_cb').getStore().reload();
                                    Ext.getCmp('automaticmaplegendsetname_tf').reset();
                                    Ext.getCmp('automaticmaplegendsetclasses_cb').reset();
                                    Ext.getCmp('automaticmaplegendsetlowcolor_cp').reset();
                                    Ext.getCmp('automaticmaplegendsethighcolor_cp').reset();
                                },
                                failure: function() {
                                    alert( i18n_status , i18n_error_while_saving_data );
                                }
                            });
                        },
                        failure: function() {
                            alert( 'Error: getAllMapLegendSets' );
                        }
                    });
                }
            }
        ]	
    });
	
	var assignAutomaticMapLegendSetPanel = new Ext.form.FormPanel({   
        id: 'assignautomaticmaplegendset_p',
		bodyStyle: 'border:0px',
        items:
        [   
            { html: '<div class="window-field-label-first">'+i18n_legend_set+'</div>' },
            automaticMapLegendSetComboBox,
            { html: '<div class="window-field-label">'+i18n_indicator+'</div>' },
			automaticMapLegendSetIndicatorMultiSelect,
            {
                xtype: 'button',
                id: 'assignautomaticmaplegendset_b',
                text: i18n_assign_to_indicators,
				cls: 'window-button',
                handler: function() {
                    var ls = Ext.getCmp('automaticmaplegendset_cb').getValue();
                    var lsrw = Ext.getCmp('automaticmaplegendset_cb').getRawValue();
                    var lims = Ext.getCmp('automaticmaplegendsetindicator_ms').getValue();
                    
                    if (!ls) {
                        Ext.messageRed.msg( i18n_assign_to_indocator , i18n_please_select_a_legend_set );
                        return;
                    }
                    
                    if (!lims) {
                        Ext.messageRed.msg( i18n_link_legend_set_to_indicator , i18n_please_select_at_least_one_indicator );
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
                        url: path + 'assignIndicatorsToMapLegendSet.action' + params,
                        method: 'POST',
                        params: { id: ls },

                        success: function(r) {
                            Ext.messageBlack.msg( i18n_assign_to_indocator , i18n_legend_set+'<span class="x-msg-hl">' + lsrw + '</span> ' + i18n_was_updated);
                            Ext.getCmp('automaticmaplegendset_cb').getStore().reload();
                        },
                        failure: function() {
                            alert( 'Error: assignIndicatorsToMapLegendSet' );
                        }
                    });
                }
            }
        ]
    });
    
    var deleteAutomaticMapLegendSetPanel = new Ext.form.FormPanel({
        id: 'deleteautomaticmaplegendset_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [   
            { html: '<div class="window-field-label-first">'+i18n_legend_set+'</p>' },
            automaticMapLegendSet2ComboBox,
            {
                xtype: 'button',
                id: 'deleteautomaticmaplegendset_b',
                text: i18n_delete,
				cls: 'window-button',
                handler: function() {
                    var ls = Ext.getCmp('automaticmaplegendset2_cb').getValue();
                    var lsrw = Ext.getCmp('automaticmaplegendset2_cb').getRawValue();
                    
                    if (!ls) {
                        Ext.messageRed.msg( i18n_delete + ' ' + i18n_legend_set, i18n_please_select_a_legend_set );
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: path + 'deleteMapLegendSet' + type,
                        method: 'GET',
                        params: { id: ls },
                        success: function(r) {
                            Ext.messageBlack.msg( i18n_delete + ' ' + i18n_legend_set , i18n_legend_set + ' <span class="x-msg-hl">' + lsrw + '</span> ' + i18n_was_deleted );
                            Ext.getCmp('automaticmaplegendset_cb').getStore().reload();
                            Ext.getCmp('automaticmaplegendset_cb').reset();
							Ext.getCmp('automaticmaplegendset2_cb').reset();
                            Ext.getCmp('automaticmaplegendsetindicator_ms').reset();
                        },
                        failure: function() {
                            alert( i18n_status , i18n_error_while_saving_data );
                        }
                    });
                }
            }
        ]
    });
    
    var automaticMapLegendSetWindow = new Ext.Window({
        id: 'automaticmaplegendset_w',
        title: '<span id="window-automaticlegendset-title">'+i18n_automatic_legend_sets+'</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: 245,
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
						var w = Ext.getCmp('automaticmaplegendset_w');
						
						if (tab.id == 'automaticmaplegendset0') { 
							w.setHeight(298);
						}
						else if (tab.id == 'automaticmaplegendset1') {
							w.setHeight(getMultiSelectHeight() + 180);
						}
						else if (tab.id == 'automaticmaplegendset2') {
							w.setHeight(151);
						}
					}
				},
				items:
				[
					{
						title: '<span class="panel-tab-title">'+i18n_new+'</span>',
						id: 'automaticmaplegendset0',
						items:
						[
							newAutomaticMapLegendSetPanel
						]
					},
					{
						title: '<span class="panel-tab-title">'+i18n_assign_to_indocator+'</span>',
						id: 'automaticmaplegendset1',
						items:
						[
							assignAutomaticMapLegendSetPanel
						]
					},
					{
						title: '<span class="panel-tab-title">'+i18n_delete+'</span>',
						id: 'automaticmaplegendset2',
						items:
						[
							deleteAutomaticMapLegendSetPanel
						]
					}
				]
			}
        ]
    });
	
	/* Section: predefined legend set */
	var predefinedMapLegendStore=new Ext.data.JsonStore({url:path+'getAllMapLegends'+type,root:'mapLegends',id:'id',fields:['id','name','startValue','endValue','color','displayString'],autoLoad:true});
	var predefinedMapLegendSetStore=new Ext.data.JsonStore({url:path+'getMapLegendSetsByType'+type,baseParams:{type:map_legend_type_predefined},root:'mapLegendSets',id:'id',fields:['id','name'],sortInfo:{field:'name',direction:'ASC'},autoLoad:true});
	var predefinedMapLegendNameTextField=new Ext.form.TextField({id:'predefinedmaplegendname_tf',isFormField:true,hideLabel:true,emptyText:emptytext,width:combo_width});
	var predefinedMapLegendStartValueTextField=new Ext.form.TextField({id:'predefinedmaplegendstartvalue_tf',isFormField:true,hideLabel:true,emptyText:emptytext,width:combo_number_width,minListWidth:combo_number_width});
	var predefinedMapLegendEndValueTextField=new Ext.form.TextField({id:'predefinedmaplegendendvalue_tf',isFormField:true,hideLabel:true,emptyText:emptytext,width:combo_number_width,minListWidth:combo_number_width});
	var predefinedMapLegendColorColorPalette=new Ext.ux.ColorField({id:'predefinedmaplegendcolor_cp',isFormField:true,hideLabel:true,allowBlank:false,width:combo_width,minListWidth:combo_width,value:"#FFFF00"});
	var predefinedMapLegendComboBox=new Ext.form.ComboBox({id:'predefinedmaplegend_cb',isFormField:true,hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:emptytext,selectOnFocus:true,width:combo_width,minListWidth:combo_width,store:predefinedMapLegendStore});
	var predefinedMapLegendSetNameTextField=new Ext.form.TextField({id:'predefinedmaplegendsetname_tf',isFormField:true,hideLabel:true,emptyText:emptytext,width:combo_width});
	var predefinedNewMapLegendMultiSelect=new Ext.ux.Multiselect({id:'predefinednewmaplegend_ms',isFormField:true,hideLabel:true,dataFields:['id','name','startValue','endValue','color','displayString'],valueField:'id',displayField:'displayString',width:multiselect_width,height:getMultiSelectHeight(),store:predefinedMapLegendStore});
	var predefinedMapLegendSetComboBox=new Ext.form.ComboBox({id:'predefinedmaplegendset_cb',isFormField:true,hideLabel:true,typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:emptytext,selectOnFocus:true,width:combo_width,minListWidth:combo_width,store:predefinedMapLegendSetStore});
	
	var newPredefinedMapLegendPanel = new Ext.form.FormPanel({   
        id: 'newpredefinedmaplegend_p',
		bodyStyle: 'border:0px solid #fff',
        items:
        [   
            { html: '<div class="window-field-label-first">'+i18n_display_name+'</div>' },
            predefinedMapLegendNameTextField,
            { html: '<div class="window-field-label">'+i18n_start_value+'</div>' },
            predefinedMapLegendStartValueTextField,
            { html: '<div class="window-field-label">'+i18n_end_value+'</div>' },
            predefinedMapLegendEndValueTextField,
            { html: '<div class="window-field-label">'+i18n_color+'</div>' },
            predefinedMapLegendColorColorPalette,
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
                        Ext.messageRed.msg(	i18n_new_legend , i18n_form_is_not_complete );
                        return;
                    }
                    
                    if (!validateInput(mln)) {
                        Ext.messageRed.msg( i18n_new_legend, i18n_name_can_not_longer_than_25 );
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: path + 'getAllMapLegends' + type,
                        method: 'GET',
						success: function(r) {
                            var mapLegends = Ext.util.JSON.decode(r.responseText).mapLegends;
                            for (var i = 0; i < mapLegends.length; i++) {
                                if (mln == mapLegends[i].name) {
                                    Ext.messageRed.msg( i18n_new_legend , i18n_legend + '<span class="x-msg-hl">' + ln + '</span> ' + i18n_already_exists);
                                    return;
                                }
                            }

                            Ext.Ajax.request({
                                url: path + 'addOrUpdateMapLegend' + type,
                                method: 'POST',
                                params: { name: mln, startValue: mlsv, endValue: mlev, color: mlc },
                                success: function(r) {
                                    Ext.messageBlack.msg(i18n_new_legend, i18n_legend + ' <span class="x-msg-hl">' + mln + '</span> ' + i18n_was_registered);
                                    Ext.getCmp('predefinedmaplegend_cb').getStore().reload();
                                    Ext.getCmp('predefinedmaplegendname_tf').reset();
                                    Ext.getCmp('predefinedmaplegendstartvalue_tf').reset();
                                    Ext.getCmp('predefinedmaplegendendvalue_tf').reset();
                                    Ext.getCmp('predefinedmaplegendcolor_cp').reset();
                                },
                                failure: function() {
                                    alert( 'Error: addOrUpdateMapLegend' );
                                }
                            });
                        },
                        failure: function() {
                            alert( 'Error: getAllMapLegends' );
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
            { html: '<div class="window-field-label-first">'+i18n_legend+'</p>' },
            predefinedMapLegendComboBox,
            {
                xtype: 'button',
                id: 'deletepredefinedmaplegend_b',
                text: i18n_delete,
				cls: 'window-button',
                handler: function() {
                    var mlv = Ext.getCmp('predefinedmaplegend_cb').getValue();
                    var mlrv = Ext.getCmp('predefinedmaplegend_cb').getRawValue();
                    
                    if (!mlv) {
                        Ext.messageRed.msg( i18n_delete + ' ' + i18n_legend, i18n_please_select_a_legend );
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: path + 'deleteMapLegend' + type,
                        method: 'POST',
                        params: { id: mlv },
                        success: function(r) {
                            Ext.messageBlack.msg(i18n_delete + ' ' + i18n_legend , i18n_legend+ ' <span class="x-msg-hl">' + mlrv + '</span> ' + i18n_was_deleted);
                            Ext.getCmp('predefinedmaplegend_cb').getStore().reload();
                            Ext.getCmp('predefinedmaplegend_cb').reset();
                        },
                        failure: function() {
                            alert( 'Error: deleteMapLegend' );
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
            predefinedMapLegendSetNameTextField,
            { html: '<div class="window-field-label">'+i18n_legends+'</div>' },
			predefinedNewMapLegendMultiSelect,
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
								var sv = predefinedMapLegendStore.getById(array[i]).get('startValue');
								var ev = predefinedMapLegendStore.getById(array[i]).get('endValue');
								for (var j = 0; j < array.length; j++) {
									if (j != i) {
										var temp_sv = predefinedMapLegendStore.getById(array[j]).get('startValue');
										var temp_ev = predefinedMapLegendStore.getById(array[j]).get('endValue');
										for (var k = sv+1; k < ev; k++) {
											if (k > temp_sv && k < temp_ev) {
												Ext.messageRed.msg( i18n_new_legend_set , i18n_overlapping_legends_are_not_allowed );
												return;
											}
										}
									}
								}
							}
						}
					}
					else {
						Ext.messageRed.msg( i18n_new_legend_set , i18n_please_select_at_least_one_legend );
                        return;
					}
					
                    if (!mlsv) {
                        Ext.messageRed.msg( i18n_new_legend_set, i18n_form_is_not_complete );
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
                        url: path + 'addOrUpdateMapLegendSet.action' + params,
                        method: 'POST',
                        params: { name: mlsv, type: map_legend_type_predefined },
                        success: function(r) {
                            Ext.messageBlack.msg( i18n_new_legend_set , i18n_new_legend_set+' <span class="x-msg-hl">' + mlsv + '</span> ' + i18n_was_registered );
                            Ext.getCmp('predefinedmaplegendset_cb').getStore().reload();
							Ext.getCmp('maplegendset_cb').getStore().reload();
							Ext.getCmp('predefinedmaplegendsetname_tf').reset();
							Ext.getCmp('predefinednewmaplegend_ms').reset();							
                        },
                        failure: function() {
                            alert( 'Error: addOrUpdateMapLegendSet' );
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
            { html: '<div class="window-field-label-first">'+i18n_legend_set+'</p>' },
            predefinedMapLegendSetComboBox,
            {
                xtype: 'button',
                id: 'deletepredefinedmaplegendset_b',
                text: i18n_delete,
				cls: 'window-button',
                handler: function() {
                    var mlsv = Ext.getCmp('predefinedmaplegendset_cb').getValue();
                    var mlsrv = Ext.getCmp('predefinedmaplegendset_cb').getRawValue();
                    
                    if (!mlsv) {
                        Ext.messageRed.msg( i18n_delete + ' ' + i18n_legend_set, i18n_please_select_a_legend_set );
                        return;
                    }
                    
                    Ext.Ajax.request({
                        url: path + 'deleteMapLegendSet' + type,
                        method: 'POST',
                        params: { id: mlsv },
                        success: function(r) {
                            Ext.messageBlack.msg(i18n_delete + ' ' + i18n_legend_set, i18n_legend_set + ' <span class="x-msg-hl">' + mlsrv + '</span> ' + i18n_was_deleted);
                            Ext.getCmp('predefinedmaplegendset_cb').getStore().reload();
                            Ext.getCmp('predefinedmaplegendset_cb').reset();
							Ext.getCmp('maplegendset_cb').getStore().reload();
                        },
                        failure: function() {
                            alert( 'Error: deleteMapLegendSet' );
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
		width: 311,
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
							w.setHeight(getMultiSelectHeight() + 180);
						}
						else if (tab.id == 'predefinedmaplegendset3') {
							w.setHeight(151);
						}
					}
				},
				items:
				[
					{
						title: '<span class="panel-tab-title">'+i18n_new_legend+'</span>',
						id: 'predefinedmaplegendset0',
						items: [
							newPredefinedMapLegendPanel
						]
					},
					{
						title: '<span class="panel-tab-title">'+i18n_delete+'</span>',
						id: 'predefinedmaplegendset1',
						items: [
							deletePredefinedMapLegendPanel
						]
					},
					{
						title: '<span class="panel-tab-title">'+i18n_new_legend_set+'</span>',
						id: 'predefinedmaplegendset2',
						items: [
							newPredefinedMapLegendSetPanel
						]
					},
					{
						title: '<span class="panel-tab-title">'+i18n_delete+'</span>',
						id: 'predefinedmaplegendset3',
						items: [
							deletePredefinedMapLegendSetPanel
						]
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
			params: { id: topic },
			success: function(r) {
				Ext.getCmp(tab).body.update('<div id="help">' + r.responseText + '</div>');
			},
			failure: function() {
				alert('Error: getHelpText');
				return;
			}
		});
	}
    
	var helpWindow = new Ext.Window({
        id: 'help_w',
        title: '<span id="window-help-title">'+i18n_help+'</span>',
		layout: 'fit',
        closeAction: 'hide',
		width: 607,
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
							getHelpText(thematicMap, tab.id);
                        }
                        else if (tab.id == 'help1') {
							getHelpText(mapRegistration, tab.id);
                        }
                        else if (tab.id == 'help2') {
                            getHelpText(organisationUnitAssignment, tab.id);
                        }
						if (tab.id == 'help3') { 
                            getHelpText(overlayRegistration, tab.id);
                        }
                        else if (tab.id == 'help4') {
                            getHelpText(administration, tab.id);
                        }
                        else if (tab.id == 'help5') {
                            getHelpText(favorites, tab.id);
                        }
						else if (tab.id == 'help6') {
                            getHelpText(legendSets, tab.id);
                        }
						else if (tab.id == 'help7') {
                            getHelpText(pdfprint, tab.id);
                        }
                    }
                },
                items:
                [
                    {
                        title: '<span class="panel-tab-title">'+i18n_thematic_map+'</span>',
                        id: 'help0'
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_map+'</span>',
                        id: 'help1'
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_assignment+'</span>',
                        id: 'help2'
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_overlays+'</span>',
                        id: 'help3'
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_administrator+'</span>',
                        id: 'help4'
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_favorite+'</span>',
                        id: 'help5'
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_legend_set+'</span>',
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
	var organisationUnitLevelStore=new Ext.data.JsonStore({url:path+'getOrganisationUnitLevels'+type,id:'id',baseParams:{format:'json'},root:'organisationUnitLevels',fields:['id','level','name'],autoLoad:true});
	var organisationUnitStore=new Ext.data.JsonStore({url:path+'getOrganisationUnitsAtLevel'+type,baseParams:{level:1,format:'json'},root:'organisationUnits',fields:['id','name'],sortInfo:{field:'name',direction:'ASC'},autoLoad:false});
	var existingMapsStore=new Ext.data.JsonStore({url:path+'getAllMaps'+type,baseParams:{format:'jsonmin'},root:'maps',fields:['id','name','mapLayerPath','organisationUnitLevel'],autoLoad:true});
	var wmsMapStore=new GeoExt.data.WMSCapabilitiesStore({url:path_geoserver+ows});
	var geojsonStore=new Ext.data.JsonStore({url:path+'getGeoJsonFiles'+type,root:'files',fields:['name'],autoLoad:true});
	var nameColumnStore=new Ext.data.SimpleStore({fields:['name'],data:[]});
	var baseCoordinateStore=new Ext.data.JsonStore({url:path+'getBaseCoordinate'+type,root:'baseCoordinate',fields:['longitude','latitude'],autoLoad:true});
	var organisationUnitComboBox=new Ext.form.ComboBox({id:'organisationunit_cb',fieldLabel:'Organisation unit',typeAhead:true,editable:false,valueField:'id',displayField:'name',emptyText:emptytext,hideLabel:true,mode:'remote',forceSelection:true,triggerAction:'all',selectOnFocus:true,width:combo_width,minListWidth:combo_width,store:organisationUnitStore});
	var organisationUnitLevelComboBox=new Ext.form.ComboBox({id:'organisationunitlevel_cb',typeAhead:true,editable:false,valueField:'id',displayField:'name',emptyText:emptytext,hideLabel:true,mode:'remote',forceSelection:true,triggerAction:'all',selectOnFocus:true,width:combo_width,minListWidth:combo_width,store:organisationUnitLevelStore});
	var newNameTextField=new Ext.form.TextField({id:'newname_tf',emptyText:emptytext,hideLabel:true,width:combo_width});
	var editNameTextField=new Ext.form.TextField({id:'editname_tf',emptyText:emptytext,hideLabel:true,width:combo_width});
	
	if (MAPSOURCE == map_source_type_shapefile) {
		wmsMapStore.load();
	}
	
	var mapLayerPathComboBox = new Ext.form.ComboBox({
        id: 'maplayerpath_cb',
		typeAhead: true,
        editable: false,
        valueField: 'name',
        displayField: 'name',
		emptyText: emptytext,
		hideLabel: true,
        width: combo_width,
        minListWidth: combo_width,
        triggerAction: 'all',
        mode: 'remote',
        store: geojsonStore,
		listeners: {
			'select': {
				fn: function() {
					var n = Ext.getCmp('maplayerpath_cb').getValue();
					
					Ext.Ajax.request({
						url: path + 'getGeoJson' + type,
						method: 'POST',
						params: {name: n},
						success: function(r) {
							var file = Ext.util.JSON.decode(r.responseText);
							var keys = [];
							var data = [];
							
							function getKeys(object) {
								for (var key in object) {
									if (object.hasOwnProperty(key)) {
										keys.push(key);
									}
								}
								return keys;
							}

							var nameList = getKeys(file.features[0].properties);
							for (var i = 0; i < nameList.length; i++) {
								data.push(new Array(nameList[i]));
							}
							
							Ext.getCmp('newnamecolumn_cb').getStore().loadData(data, false);
						},
						failure: function() {}
					});
				},
				scope: this
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
		store: wmsMapStore,
        listeners: {
            'rowdblclick': mapPreview
        }
    });
    
    function mapPreview(grid, index) {
        var record = grid.getStore().getAt(index);
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
				/* {
					 xtype: 'button',
					 id: 'previewwms_b',
					 text: 'Preview',
					 handler: function() {
						
					 }
				 },*/
				{
					xtype: 'button',
					id: 'selectwms_b',
					text: 'Select',
					cls: 'aa_med',
					handler: function() {
						var name = Ext.getCmp('wms_g').getSelectionModel().getSelected().get('name');
						mapLayerPathWMSTextField.setValue(name);
						wmsWindow.hide();
						newNameColumnComboBox.focus();						
					}
				}
			]
		})		
	});
	
	var mapLayerPathWMSTextField = new Ext.form.TextField({
		id: 'maplayerpathwms_tf',
		emptyText: emptytext,
		hideLabel: true,
        width: combo_width,
		listeners: {
			'focus': {
				fn: function() {
					var x = Ext.getCmp('center').x + 15;
					var y = Ext.getCmp('center').y + 41;    
					wmsWindow.show();
					wmsWindow.setPosition(x,y);
				}
			}
		}
	});
	
    var typeComboBox = new Ext.form.ComboBox({
        id: 'type_cb',
        editable: false,
        displayField: 'name',
        valueField: 'name',
		emptyText: emptytext,
		hideLabel: true,
        width: combo_width,
        minListWidth: combo_width,
        triggerAction: 'all',
        mode: 'local',
        value: 'Polygon',
        store: new Ext.data.SimpleStore({
            fields: ['name'],
            data: [['Polygon']]
        })
    });

	var newNameColumnComboBox = new Ext.form.ComboBox({
        id: 'newnamecolumn_cb',
        editable: false,
        displayField: 'name',
        valueField: 'name',
		emptyText: emptytext,
		hideLabel: true,
        width: combo_width,
        minListWidth: combo_width,
        triggerAction: 'all',
        mode: 'local',
        store: nameColumnStore,
		listeners: {
			'focus': {
				fn: function() {
					var mlp = Ext.getCmp('maplayerpathwms_tf').getValue();
					
					if (mlp) {					
						Ext.Ajax.request({
							url: path_geoserver + wfs + mlp + output,
							method: 'POST',
							success: function(r) {
								var file = Ext.util.JSON.decode(r.responseText);
								var keys = [];
								var data = [];
								
								function getKeys(object) {
									for (var key in object) {
										if (object.hasOwnProperty(key)) {
											keys.push(key);
										}
									}
									return keys;
								}

								var nameList = getKeys(file.features[0].properties);
								for (var i = 0; i < nameList.length; i++) {
									data.push(new Array(nameList[i]));
								}
								
								Ext.getCmp('newnamecolumn_cb').getStore().loadData(data, false);
							},
							failure: function() {}
						});
					}
				}
			}
		}				
	});
	
	var editNameColumnComboBox=new Ext.form.ComboBox({id:'editnamecolumn_cb',editable:false,displayField:'name',valueField:'name',emptyText:emptytext,hideLabel:true,width:combo_width,minListWidth:combo_width,triggerAction:'all',mode:'local',store:nameColumnStore});
	var newLongitudeComboBox=new Ext.form.ComboBox({id:'newlongitude_cb',valueField:'longitude',displayField:'longitude',editable:true,emptyText:emptytext,hideLabel:true,width:combo_number_width,minListWidth:combo_number_width,triggerAction:'all',value:BASECOORDINATE.longitude,mode:'remote',store:baseCoordinateStore});
	var editLongitudeComboBox=new Ext.form.ComboBox({id:'editlongitude_cb',valueField:'longitude',displayField:'longitude',editable:true,emptyText:emptytext,hideLabel:true,width:combo_number_width,minListWidth:combo_number_width,triggerAction:'all',mode:'remote',store:baseCoordinateStore});
	var newLatitudeComboBox=new Ext.form.ComboBox({id:'newlatitude_cb',valueField:'latitude',displayField:'latitude',editable:true,emptyText:emptytext,hideLabel:true,width:combo_number_width,minListWidth:combo_number_width,triggerAction:'all',value:BASECOORDINATE.latitude,mode:'remote',store:baseCoordinateStore});
	var editLatitudeComboBox=new Ext.form.ComboBox({id:'editlatitude_cb',valueField:'latitude',displayField:'latitude',editable:true,emptyText:emptytext,hideLabel:true,width:combo_number_width,minListWidth:combo_number_width,triggerAction:'all',mode:'remote',store:baseCoordinateStore});
	var newZoomComboBox=new Ext.form.ComboBox({id:'newzoom_cb',editable:true,displayField:'text',valueField:'value',hideLabel:true,width:combo_number_width,minListWidth:combo_number_width,triggerAction:'all',mode:'local',value:7,store:new Ext.data.SimpleStore({fields:['value','text'],data:[[3,'3 (out)'],[4,'4'],[5,'5'],[6,'6'],[7,'7'],[8,'8'],[9,'9'],[10,'10 (in)']]})});
	var editZoomComboBox=new Ext.form.ComboBox({id:'editzoom_cb',editable:false,emptyText:'',displayField:'value',valueField:'value',hideLabel:true,width:combo_number_width,minListWidth:combo_number_width+17,triggerAction:'all',mode:'local',store:new Ext.data.SimpleStore({fields:['value','text'],data:[[5,'5 (out)'],[6,'6'],[7,'7'],[8,'8'],[9,'9 (in)']]})});
    
    var newMapButton = new Ext.Button({
        id: 'newmap_b',
        text: i18n_register_maps,
		cls: 'aa_med',
        handler: function()
        {
            /*var nm = Ext.getCmp('newmap_cb').getValue();
            var oui = Ext.getCmp('organisationunit_cb').getValue();*/
    
            Ext.Ajax.request({
                url: path + 'getOrganisationUnitsAtLevel' + type,
                method: 'POST',
                params: { level: 1, format: 'json' },

                success: function(r) {
                    var oui = Ext.util.JSON.decode( r.responseText ).organisationUnits[0].id;
                    var ouli = Ext.getCmp('organisationunitlevel_cb').getValue();
                    var nn = Ext.getCmp('newname_tf').getValue();
                    var t = Ext.getCmp('type_cb').getValue();
					var mlp = Ext.getCmp('maplayerpath_cb').getValue();
					var mlpwms = Ext.getCmp('maplayerpathwms_tf').getValue();					
                    var nc = Ext.getCmp('newnamecolumn_cb').getValue();
                    var lon = Ext.getCmp('newlongitude_cb').getRawValue();
                    var lat = Ext.getCmp('newlatitude_cb').getRawValue();
                    var zoom = Ext.getCmp('newzoom_cb').getValue();
                     
                    if (!nn || !oui || !ouli || !nc || !lon || !lat) {
						Ext.messageRed.msg( i18n_new + ' ' + i18n_map, i18n_form_is_not_complete );
						return;
					}
					else if (!mlp && !mlpwms) {
						Ext.messageRed.msg( i18n_new + ' ' + i18n_map, i18n_form_is_not_complete );
						return;
                    }
                    
                    if (validateInput(nn) == false) {
                        Ext.messageRed.msg( i18n_new + ' ' + i18n_map, '<span class="x-msg-hl">' + i18n_map + ' ' + i18n_name_can_not_longer_than_25 + '</span>');
                        return;
                    }
                    
                    if (!Ext.num(parseFloat(lon), false)) {
                        Ext.messageRed.msg( i18n_new + ' ' + i18n_map, '<span class="x-msg-hl">' + i18n_longitude_x + '</span>' + i18n_must_be_a_number);
                        return;
                    }
                    else {
                        if (lon < -180 || lon > 180) {
                            Ext.messageRed.msg( i18n_new + ' ' + i18n_map , '<span class="x-msg-hl">' + i18n_longitude_x + '</span> ' + i18n_must_be_between_180_and_180);
                            return;
                        }
                    }
                    
                    if (!Ext.num(parseFloat(lat), false)) {
                        Ext.messageRed.msg( i18n_new + ' ' + i18n_map, '<span class="x-msg-hl">' + i18n_latitude_y + '</span> ' + i18n_must_be_a_number);
                        return;
                    }
                    else {
                        if (lat < -90 || lat > 90) {
                            Ext.messageRed.msg( i18n_new + ' ' + i18n_map, '<span class="x-msg-hl">' + i18n_latitude_y + '</span> ' + i18n_must_be_between_90_and_90);
                            return;
                        }
                    }

                    Ext.Ajax.request({
                        url: path + 'getAllMaps' + type,
                        method: 'GET',
                        success: function(r) {
                            var maps = Ext.util.JSON.decode(r.responseText).maps;
                            for (var i = 0; i < maps.length; i++) {
                                if (maps[i].name == nn) {
                                    Ext.messageRed.msg( i18n_new + ' ' + i18n_map, ' <span class="x-msg-hl">' + nn + '</span>.');
                                    return;
                                }
                                else if (maps[i].mapLayerPath == mlp) {
                                    Ext.messageRed.msg( i18n_new + ' ' + i18n_map, i18n_the_source_file+' <span class="x-msg-hl">' + mlp + '</span> ' + i18n_already_exists );
                                    return;
                                }
                            }
							
							var source = mlp ? mlp : mlpwms;
							
                            Ext.Ajax.request({
                                url: path + 'addOrUpdateMap' + type,
                                method: 'POST',
                                params: { name: nn, mapLayerPath: source, type: t, sourceType: MAPSOURCE, organisationUnitId: oui, organisationUnitLevelId: ouli, nameColumn: nc, longitude: lon, latitude: lat, zoom: zoom},
                                success: function(r) {
                                    Ext.messageBlack.msg( i18n_new + ' ' + i18n_map, i18n_map+' <span class="x-msg-hl">' + nn + '</span> (<span class="x-msg-hl">' + source + '</span>) ' + i18n_was_registered );
                                    
                                    Ext.getCmp('map_cb').getStore().reload();
                                    Ext.getCmp('maps_cb').getStore().reload();
                                    Ext.getCmp('editmap_cb').getStore().reload();
                                    Ext.getCmp('deletemap_cb').getStore().reload();
                                    
                                    Ext.getCmp('organisationunitlevel_cb').reset();
                                    Ext.getCmp('newname_tf').reset();
                                    Ext.getCmp('maplayerpath_cb').reset();
                                    Ext.getCmp('newnamecolumn_cb').reset();
                                    Ext.getCmp('newlongitude_cb').reset();
                                    Ext.getCmp('newlatitude_cb').reset();
                                    Ext.getCmp('newzoom_cb').reset();                                    
                                },
                                failure: function() {
                                    alert( 'Error: addOrUpdateMap' );
                                }
                            });
                        },
                        failure: function() {
                            alert( 'Error: getAllMaps' );
                        }
                    });
                },
                failure: function() {
                    alert( 'Error: getOrganisationUnitsAtLevel' );
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
            var lon = Ext.getCmp('editlongitude_cb').getRawValue();
            var lat = Ext.getCmp('editlatitude_cb').getRawValue();
            var zoom = Ext.getCmp('editzoom_cb').getValue();
			var t = Ext.getCmp('type_cb').getValue();
			
            if (!en || !em || !nc || !lon || !lat) {
                Ext.messageRed.msg( i18n_new + ' ' + i18n_map, i18n_form_is_not_complete );
                return;
            }
            
            if (validateInput(en) == false) {
                Ext.messageRed.msg( i18n_new + ' ' + i18n_map , i18n_name_can_not_longer_than_25 );
                return;
            }
           
            Ext.Ajax.request({
                url: path + 'addOrUpdateMap' + type,
                method: 'GET',
                params: { name: en, mapLayerPath: em, nameColumn: nc, longitude: lon, latitude: lat, zoom: zoom },

                success: function(r) {
                    Ext.messageBlack.msg( i18n_new + ' ' + i18n_map,  i18n_map + ' <span class="x-msg-hl">' + en + '</span> (<span class="x-msg-hl">' + em + '</span>)' + i18n_was_updated );
                    
                    Ext.getCmp('map_cb').getStore().reload();
                    Ext.getCmp('maps_cb').getStore().reload();
                    Ext.getCmp('editmap_cb').getStore().reload();
                    Ext.getCmp('editmap_cb').reset();
                    Ext.getCmp('deletemap_cb').getStore().reload();
                    Ext.getCmp('deletemap_cb').reset();
                    
                    Ext.getCmp('editmap_cb').reset();
                    Ext.getCmp('editname_tf').reset();
                    Ext.getCmp('editnamecolumn_cb').reset();
                    Ext.getCmp('editlongitude_cb').reset();
                    Ext.getCmp('editlatitude_cb').reset();
                    Ext.getCmp('editzoom_cb').reset();
                },
                failure: function() {
                    alert( i18n_status, i18n_error_while_saving_data );
                }
            });
        }
    });
    
    var deleteMapButton = new Ext.Button({
        id: 'deletemap_b',
        text: i18n_delete + ' ' + i18n_map,
		cls: 'aa_med',
        handler: function() {
            var mlp = Ext.getCmp('deletemap_cb').getValue();
            var mn = Ext.getCmp('deletemap_cb').getRawValue();
            
            if (!mlp) {
                Ext.messageRed.msg( i18n_delete + ' ' + i18n_map, i18n_please_select_a_map );
                return;
            }
            
            Ext.Ajax.request({
                url: path + 'deleteMap' + type,
                method: 'GET',
                params: { mapLayerPath: mlp },
                success: function(r) {
                    Ext.messageBlack.msg( i18n_edit + ' ' + i18n_map, i18n_map + ' <span class="x-msg-hl">' + mn + '</span> (<span class="x-msg-hl">' + mlp + '</span>) ' + i18n_was_deleted );
                    
                    Ext.getCmp('map_cb').getStore().reload();
					
					if (Ext.getCmp('map_cb').getValue() == mlp) {
						Ext.getCmp('map_cb').reset();
					}
					
                    Ext.getCmp('maps_cb').getStore().reload();
                    Ext.getCmp('editmap_cb').getStore().reload();
                    Ext.getCmp('editmap_cb').reset();
                    Ext.getCmp('deletemap_cb').getStore().reload();
                    Ext.getCmp('deletemap_cb').reset();
                    Ext.getCmp('mapview_cb').getStore().reload();
                    Ext.getCmp('mapview_cb').reset();
                },
                failure: function() {
                     alert( i18n_status, i18n_error_while_saving_data );
                }
            });
        }
    });
    
    // var newMapComboBox = new Ext.form.ComboBox({
        // id: 'newmap_cb',
        // typeAhead: true,
        // editable: false,
        // valueField: 'level',
        // displayField: 'name',
        // emptyText: emptytext,
		// hideLabel: true,
        // mode: 'remote',
        // forceSelection: true,
        // triggerAction: 'all',
        // selectOnFocus: true,
        // width: combo_width,
        // minListWidth: combo_width,
        // store: organisationUnitLevelStore,
        // listeners: {
            // 'select': {
                // fn: function() {
                    // var level = Ext.getCmp('newmap_cb').getValue();
                    // organisationUnitStore.baseParams = { level: level, format: 'json' };
                    // organisationUnit();
                // },
                // scope: this
            // }
        // }
    // });
    
    var editMapComboBox = new Ext.form.ComboBox({
        id: 'editmap_cb',
        typeAhead: true,
        editable: false,
        valueField: 'mapLayerPath',
        displayField: 'name',
        emptyText: emptytext,
		hideLabel: true,
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width,
        store: existingMapsStore,
        listeners: {
            'select': {
                fn: function() {
                    var mlp = Ext.getCmp('editmap_cb').getValue();
                    
                    Ext.Ajax.request({
                        url: path + 'getMapByMapLayerPath' + type,
                        method: 'GET',
                        params: { mapLayerPath: mlp, format: 'json' },

                        success: function(r) {
                            var map = Ext.util.JSON.decode( r.responseText ).map[0];
                            
                            Ext.getCmp('editname_tf').setValue(map.name);
                            Ext.getCmp('editnamecolumn_cb').setValue(map.nameColumn);
                            Ext.getCmp('editlongitude_cb').setValue(map.longitude);
                            Ext.getCmp('editlatitude_cb').setValue(map.latitude);
                            Ext.getCmp('editzoom_cb').setValue(map.zoom);
                        },
                        failure: function() {
                            alert( 'Error while retrieving data: getAssignOrganisationUnitData' );
                        } 
                    });
					
					if (MAPSOURCE == map_source_type_geojson) {
						Ext.Ajax.request({
							url: path + 'getGeoJson' + type,
							method: 'POST',
							params: {name: mlp},
							success: function(r) {
								var file = Ext.util.JSON.decode(r.responseText);
								var keys = [];
								var data = [];
								
								function getKeys(object) {
									for (var key in object) {
										if (object.hasOwnProperty(key)) {
											keys.push(key);
										}
									}
									return keys;
								}

								var nameList = getKeys(file.features[0].properties);
								for (var i = 0; i < nameList.length; i++) {
									data.push(new Array(nameList[i]));
								}
								
								Ext.getCmp('editnamecolumn_cb').getStore().loadData(data, false);
							},
							failure: function() {}
						});
					}
					else if (MAPSOURCE == map_source_type_shapefile) {
						Ext.Ajax.request({
							url: path_geoserver + wfs + mlp + output,
							method: 'POST',
							success: function(r) {
								var file = Ext.util.JSON.decode(r.responseText);
								var keys = [];
								var data = [];
								
								function getKeys(object) {
									for (var key in object) {
										if (object.hasOwnProperty(key)) {
											keys.push(key);
										}
									}
									return keys;
								}

								var nameList = getKeys(file.features[0].properties);
								for (var i = 0; i < nameList.length; i++) {
									data.push(new Array(nameList[i]));
								}
								
								Ext.getCmp('editnamecolumn_cb').getStore().loadData(data, false);
							},
							failure: function() {}
						});
					}
                },
                scope: this
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
        emptyText: emptytext,
		hideLabel: true,
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width,
        store: existingMapsStore
    });
    
    var newMapPanel = new Ext.form.FormPanel({   
        id: 'newmap_p',
        items:
        [   
            /*{ html: '<div class="panel-fieldlabel">Map type</div>' }, typeComboBox,
            { html: '<div class="panel-fieldlabel">Organisation unit level</div>' }, newMapComboBox,
            { html: '<div class="panel-fieldlabel">Organisation unit</div>' }, multi,*/
            { html: '<div class="panel-fieldlabel-first">'+i18n_display_name+'</div>' }, newNameTextField,
            { html: '<div class="panel-fieldlabel">'+i18n_organisation_unit_level+'</div>' }, organisationUnitLevelComboBox,
			{ html: '<div class="panel-fieldlabel">'+i18n_map_source_file+'</div>' }, mapLayerPathComboBox, mapLayerPathWMSTextField,
            { html: '<div class="panel-fieldlabel">'+i18n_name_column+'</div>' }, newNameColumnComboBox,
            { html: '<div class="panel-fieldlabel">'+i18n_longitude_x+'</div>' }, newLongitudeComboBox,
            { html: '<div class="panel-fieldlabel">'+i18n_latitude_y+'</div>' }, newLatitudeComboBox,
            { html: '<div class="panel-fieldlabel">'+i18n_zoom+'</div>' }, newZoomComboBox
        ]
    });
    
    var editMapPanel = new Ext.form.FormPanel({
        id: 'editmap_p',
        items: [
            { html: '<div class="panel-fieldlabel-first">'+i18n_map+'</div>' }, editMapComboBox,
            { html: '<div class="panel-fieldlabel">'+i18n_display_name+'</div>' }, editNameTextField,
            { html: '<div class="panel-fieldlabel">'+i18n_name_column+'</div>' }, editNameColumnComboBox,
            { html: '<div class="panel-fieldlabel">'+i18n_longitude_x+'</div>' }, editLongitudeComboBox,
            { html: '<div class="panel-fieldlabel">'+i18n_latitude_y+'</div>' }, editLatitudeComboBox,
            { html: '<div class="panel-fieldlabel">'+i18n_zoom+'</div>' }, editZoomComboBox
        ]
    });
    
    var deleteMapPanel = new Ext.form.FormPanel({
        id: 'deletemap_p',
        items: [
            { html: '<div class="panel-fieldlabel-first">'+i18n_map+'</div>' }, deleteMapComboBox
        ]
    });

    shapefilePanel = new Ext.Panel({
        id: 'shapefile_p',
        title: '<span class="panel-title">'+i18n_register_maps+'</span>',
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
                        title: '<span class="panel-tab-title">'+i18n_new+'</span>',
                        id: 'map0',
                        items:
                        [
                            newMapPanel
                        ]
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_edit+'</span>',
                        id: 'map1',
                        items:
                        [
                            editMapPanel
                        ]
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_delete+'</span>',
                        id: 'map2',
                        items:
                        [
                            deleteMapPanel
                        ]
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
					if (MAPSOURCE == map_source_type_shapefile) {
						mapLayerPathComboBox.hide();
						mapLayerPathWMSTextField.show();						
					}
					else {
						mapLayerPathComboBox.show();
						mapLayerPathWMSTextField.hide();						
					}
					
					ACTIVEPANEL = mapRegistration;
				}
			},
			collapse: {
				fn: function() {
					ACTIVEPANEL = false;
				}
			}
		}
    });
    
    /* Section: map layers */
	var wmsOverlayStore=new GeoExt.data.WMSCapabilitiesStore({url:path_geoserver+ows});
	var mapLayerNameTextField=new Ext.form.TextField({id:'maplayername_tf',emptyText:emptytext,hideLabel:true,width:combo_width});
	var mapLayerMapSourceFileComboBox=new Ext.form.ComboBox({id:'maplayermapsourcefile_cb',editable:false,displayField:'name',valueField:'name',emptyText:emptytext,hideLabel:true,width:combo_width,minListWidth:combo_width,triggerAction:'all',mode:'remote',store:geojsonStore});
	
	if (MAPSOURCE == map_source_type_shapefile) {
		wmsOverlayStore.load();
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
        store: wmsOverlayStore,
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
/*			
				{
					xtype: 'button',
					id: 'previewwmsoverlay_b',
					text: 'Preview',
					handler: function() {}
				},
*/				
				{
					xtype: 'button',
					id: 'selectwmsoverlay_b',
					text: i18n_select,
					cls: 'aa_med',
					handler: function() {
						var name = Ext.getCmp('wmsoverlay_g').getSelectionModel().getSelected().get('name');
						mapLayerPathWMSOverlayTextField.setValue(name);
						wmsOverlayWindow.hide();
						newMapLayerButton.focus();						
					}
				}
			]
		})
	});
	
	var mapLayerPathWMSOverlayTextField = new Ext.form.TextField({
		id: 'maplayerpathwmsoverlay_tf',
		emptyText: emptytext,
		hideLabel: true,
        width: combo_width,
		listeners: {
			'focus': {
				fn: function() {
					var x = Ext.getCmp('center').x + 15;
					var y = Ext.getCmp('center').y + 41;    
					wmsOverlayWindow.show();
					wmsOverlayWindow.setPosition(x,y);
				}
			}
		}
	});
	
	var mapLayerFillColorColorField=new Ext.ux.ColorField({id:'maplayerfillcolor_cf',hideLabel:true,allowBlank:false,width:combo_width,value:'#FF0000'});
	var mapLayerFillOpacityComboBox=new Ext.form.ComboBox({id:'maplayerfillopacity_cb',hideLabel:true,editable:true,valueField:'value',displayField:'value',mode:'local',triggerAction:'all',width:combo_number_width,minListWidth:combo_number_width,value:0.5,store:new Ext.data.SimpleStore({fields:['value'],data:[[0.0],[0.1],[0.2],[0.3],[0.4],[0.5],[0.6],[0.7],[0.8],[0.9],[1.0]]})});
	var mapLayerStrokeColorColorField=new Ext.ux.ColorField({id:'maplayerstrokecolor_cf',hideLabel:true,allowBlank:false,width:combo_width,value:'#222222'});
	var mapLayerStrokeWidthComboBox=new Ext.form.ComboBox({id:'maplayerstrokewidth_cb',hideLabel:true,editable:true,valueField:'value',displayField:'value',mode:'local',triggerAction:'all',width:combo_number_width,minListWidth:combo_number_width,value:2,store:new Ext.data.SimpleStore({fields:['value'],data:[[0],[1],[2],[3],[4]]})});
	var mapLayerStore=new Ext.data.JsonStore({url:path+'getMapLayersByType'+type,baseParams:{type:map_layer_type_overlay},root:'mapLayers',fields:['id','name'],sortInfo:{field:'name',direction:'ASC'},autoLoad:true});
	var mapLayerComboBox=new Ext.form.ComboBox({id:'maplayer_cb',typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:emptytext,hideLabel:true,selectOnFocus:true,width:combo_width,minListWidth:combo_width,store:mapLayerStore});
    
    var deleteMapLayerButton = new Ext.Button({
        id: 'deletemaplayer_b',
        text: i18n_delete_overlay,
 		cls: 'window-button',
        handler: function() {
            var ml = Ext.getCmp('maplayer_cb').getValue();
            var mln = Ext.getCmp('maplayer_cb').getRawValue();
            
            if (!ml) {
                Ext.messageRed.msg( i18n_delete_overlay, i18n_please_select_an_overlay );
                return;
            }
            
            Ext.Ajax.request({
                url: path + 'deleteMapLayer' + type,
                method: 'POST',
                params: { id: ml },

                success: function(r) {
                    Ext.messageBlack.msg( i18n_delete_overlay , i18n_overlay + ' <span class="x-msg-hl">' + mln + '</span> '+i18n_was_deleted+'.');
                    Ext.getCmp('maplayer_cb').getStore().reload();
                    Ext.getCmp('maplayer_cb').reset();
                },
                failure: function() {
                    alert( i18n_status , i18n_error_while_saving_data );
                }
            });
            
            MAP.getLayersByName(mln)[0].destroy();
        }
    });
	
    var newMapLayerPanel = new Ext.form.FormPanel({
        id: 'newmaplayer_p',
        items:
        [
            { html: '<div class="panel-fieldlabel-first">'+i18n_display_name+'</div>' }, mapLayerNameTextField,
            { html: '<div class="panel-fieldlabel">'+i18n_map_source_file+'</div>' }, mapLayerMapSourceFileComboBox, mapLayerPathWMSOverlayTextField,
            { html: '<div class="panel-fieldlabel">'+i18n_fill_color+'</div>' }, mapLayerFillColorColorField,
            { html: '<div class="panel-fieldlabel">'+i18n_fill_opacity+'</div>' }, mapLayerFillOpacityComboBox,
            { html: '<div class="panel-fieldlabel">'+i18n_stroke_color+'</div>' }, mapLayerStrokeColorColorField,
            { html: '<div class="panel-fieldlabel">'+i18n_stroke_width+'</div>' }, mapLayerStrokeWidthComboBox,
            {
				xtype: 'button',
				id: 'newmaplayer_b',
				text: 'Register new overlay',
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
						Ext.messageRed.msg( i18n_new_overlay, i18n_overlay_form_is_not_complete );
						return;
					}
					else if (!mlmsf && !mlwmso) {
						Ext.messageRed.msg( i18n_new_overlay, i18n_overlay_form_is_not_complete );
						return;
					}
					
					if (validateInput(mln) == false) {
						Ext.messageRed.msg( i18n_new_overlay , i18n_overlay_name_cannot_be_longer_than_25_characters );
						return;
					}
					
					Ext.Ajax.request({
						url: path + 'getAllMapLayers' + type,
						method: 'GET',
						success: function(r) {
							var mapLayers = Ext.util.JSON.decode(r.responseText).mapLayers;
							
							for (i in mapLayers) {
								if (mapLayers[i].name == mln) {
									Ext.messageRed.msg( i18n_new_overlay , i18n_name + ' <span class="x-msg-hl">' + mln + '</span> '+i18n_is_already_in_use+'.');
									return;
								}
							}
					
							var ms = MAPSOURCE == map_source_type_geojson ? mlmsf : mlwmso;
							
							Ext.Ajax.request({
								url: path + 'addOrUpdateMapLayer' + type,
								method: 'POST',
								params: { name: mln, type: 'overlay', mapSource: ms, fillColor: mlfc, fillOpacity: mlfo, strokeColor: mlsc, strokeWidth: mlsw },
								success: function(r) {
									Ext.messageBlack.msg( i18n_new_overlay , 'The overlay <span class="x-msg-hl">' + mln + '</span> '+i18n_was_registered+'.');
									Ext.getCmp('maplayer_cb').getStore().reload();
							
									var mapurl = MAPSOURCE == map_source_type_geojson ? path + 'getGeoJson.action?name=' + mlmsf : path_geoserver + wfs + mlwmso + output;
									
									MAP.addLayer(
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
									Ext.getCmp('maplayermapsourcefile_cb').reset();
									Ext.getCmp('maplayerpathwmsoverlay_tf').reset();
								},
								failure: function() {}
							});
						},
						failure: function() {}
					});
				}
			}
        ]
    });
    
    var deleteMapLayerPanel = new Ext.form.FormPanel({
        id: 'deletemaplayer_p',
        items:
        [
            { html: '<div class="panel-fieldlabel-first">'+i18n_overlays+'</div>' }, mapLayerComboBox,
            deleteMapLayerButton
        ]
    });

	var overlaysWindow = new Ext.Window({
        id: 'overlays_w',
        title: '<span id="window-maplayer-title">'+i18n_overlays+'</span>',
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
                        title: '<span class="panel-tab-title">'+i18n_new+'</span>',
                        id: 'overlay0',
                        items:
                        [
                            newMapLayerPanel
                        ]
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_delete+'</span>',
                        id: 'overlay1',
                        items:
                        [
                            deleteMapLayerPanel
                        ]
                    }
                ]
            }
        ],
		listeners: {
			show: {
				fn: function() {
					if (MAPSOURCE == map_source_type_geojson) {
						mapLayerMapSourceFileComboBox.show();
						mapLayerPathWMSOverlayTextField.hide();
					}
					else if (MAPSOURCE == map_source_type_shapefile) {
						mapLayerMapSourceFileComboBox.hide();
						mapLayerPathWMSOverlayTextField.show();
					}
				}
			}
		}
    });
    
    var mapLayerBaseLayersNameTextField=new Ext.form.TextField({id:'maplayerbaselayersname_tf',emptyText:emptytext,hideLabel:true,width:combo_width});
    var mapLayerBaseLayersUrlTextField=new Ext.form.TextField({id:'maplayerbaselayersurl_tf',emptyText:emptytext,hideLabel:true,width:combo_width});
    var mapLayerBaseLayersLayerTextField=new Ext.form.TextField({id:'maplayerbaselayerslayer_tf',emptyText:emptytext,hideLabel:true,width:combo_width});
    
    var mapLayerBaseLayerStore=new Ext.data.JsonStore({url:path+'getMapLayersByType'+type,baseParams:{ type:map_layer_type_baselayer },root:'mapLayers',fields:['id','name'],sortInfo:{field:'name',direction:'ASC'},autoLoad:true});
	var mapLayerBaseLayerComboBox=new Ext.form.ComboBox({id:'maplayerbaselayers_cb',typeAhead:true,editable:false,valueField:'id',displayField:'name',mode:'remote',forceSelection:true,triggerAction:'all',emptyText:emptytext,hideLabel:true,selectOnFocus:true,width:combo_width,minListWidth:combo_width,store:mapLayerBaseLayerStore});
    
    var deleteMapLayerBaseLayersButton = new Ext.Button({
        id: 'deletemaplayerbaselayers_b',
        text: i18n_delete_baselayer,
 		cls: 'window-button',
        handler: function() {
            var ml = Ext.getCmp('maplayerbaselayers_cb').getValue();
            var mln = Ext.getCmp('maplayerbaselayers_cb').getRawValue();
            
            if (!ml) {
                Ext.messageRed.msg( i18n_delete_baselayer, i18n_please_select_a_baselayer );
                return;
            }
            
            Ext.Ajax.request({
                url: path + 'deleteMapLayer' + type,
                method: 'POST',
                params: { id: ml },
                success: function(r) {
                    Ext.messageBlack.msg( i18n_delete_baselayer , i18n_baselayer + ' <span class="x-msg-hl">' + mln + '</span> '+i18n_was_deleted+'.');
                    Ext.getCmp('maplayerbaselayers_cb').getStore().reload();
                    Ext.getCmp('maplayerbaselayers_cb').reset();
                    
                    if (MAP.baseLayer && mln == MAP.baseLayer.name) {                    
                        Ext.Ajax.request({
                            url: path + 'getMapLayersByType' + type,
                            params: { type: map_layer_type_baselayer },
                            method: 'POST',
                            success: function(r) {
                                var mapLayers = Ext.util.JSON.decode(r.responseText).mapLayers;
                                for (var i = 0; i < mapLayers.length; i++) {
                                    MAP.getLayersByName(mapLayers[i].name)[0].setVisibility(false);
                                }
                            },
                            failure: function() {
                                alert( 'Error: getMapLayersByType' );
                            }
                        });
                    }
                },
                failure: function() {
                    alert( 'Error: deleteMapLayer' );
                }
            });
            
            MAP.getLayersByName(mln)[0].destroy(false);
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
						Ext.messageRed.msg( i18n_new_baselayer, i18n_baselayer_form_is_not_complete );
						return;
					}
					
					if (validateInput(mlbn) == false) {
						Ext.messageRed.msg( i18n_new_baselayer , i18n_baselayer_name_cannot_be_longer_than_25_characters );
						return;
					}
					
					Ext.Ajax.request({
						url: path + 'getMapLayersByType' + type,
                        params: { type: map_layer_type_baselayer },
						method: 'POST',
						success: function(r) {
							var mapLayers = Ext.util.JSON.decode(r.responseText).mapLayers;
							
							for (i in mapLayers) {
								if (mapLayers[i].name == mlbn) {
									Ext.messageRed.msg( i18n_new_baselayer , i18n_name + ' <span class="x-msg-hl">' + mlbn + '</span> '+i18n_is_already_in_use+'.');
									return;
								}
							}
					
							Ext.Ajax.request({
								url: path + 'addOrUpdateMapLayer' + type,
								method: 'POST',
								params: { name: mlbn, type: map_layer_type_baselayer, mapSource: mlbu, layer: mlbl, fillColor: '', fillOpacity: 0, strokeColor: '', strokeWidth: 0 },
								success: function(r) {
									Ext.messageBlack.msg( i18n_new_baselayer , 'The base layer <span class="x-msg-hl">' + mlbn + '</span> '+i18n_was_registered+'.');
									Ext.getCmp('maplayerbaselayers_cb').getStore().reload();
									MAP.addLayers([
                                        new OpenLayers.Layer.WMS(
                                            mlbn,
                                            mlbu,
                                            {layers: mlbl}
                                        )
                                    ]);
									
									Ext.getCmp('maplayerbaselayersname_tf').reset();
									Ext.getCmp('maplayerbaselayersurl_tf').reset();
									Ext.getCmp('maplayerbaselayerslayer_tf').reset();
								},
								failure: function() {}
							});
						},
						failure: function() {}
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
                        title: '<span class="panel-tab-title">'+i18n_new+'</span>',
                        id: 'baselayer0',
                        items:
                        [
                            newMapLayerBaseLayersPanel
                        ]
                    },
                    {
                        title: '<span class="panel-tab-title">'+i18n_delete+'</span>',
                        id: 'baselayer1',
                        items:
                        [
                            deleteMapLayerBaseLayerPanel
                        ]
                    }
                ]
            }
        ]
    });
	
    /* Section: administrator */
    var adminPanel = new Ext.form.FormPanel({
        id: 'admin_p',
        title: '<span class="panel-title">'+i18n_administrator+'</span>',
        items:
        [
			{ html: '<p style="height:5px;">' },
			{
				xtype:'fieldset',
				columnWidth: 0.5,
				title: '&nbsp;<span class="panel-tab-title">'+i18n_map_source+'</span>&nbsp;',
				collapsible: true,
				animCollapse: true,
				autoHeight:true,
				items:
				[
					{
						xtype: 'combo',
						id: 'mapsource_cb',
						fieldLabel: i18n_map_source,
						labelSeparator: labelseparator,
						editable: false,
						valueField: 'id',
						displayField: 'text',
						isFormField: true,
						width: combo_width_fieldset,
						minListWidth: combo_list_width_fieldset,
						mode: 'local',
						triggerAction: 'all',
						value: MAPSOURCE,
						store: new Ext.data.SimpleStore({
							fields: ['id', 'text'],
							data: [[map_source_type_geojson, 'GeoJSON files'], [map_source_type_shapefile, 'Shapefiles'], [map_source_type_database, 'DHIS database']]
						}),
						listeners: {
							'select': {
								fn: function() {
									var msv = Ext.getCmp('mapsource_cb').getValue();
									var msrw = Ext.getCmp('mapsource_cb').getRawValue();

									if (MAPSOURCE == msv) {
										Ext.messageRed.msg( i18n_map_source , '<span class="x-msg-hl">' + msrw + '</span> '+i18n_is_already_selected+'.');
									}
									else {
										Ext.Ajax.request({
											url: path + 'setMapSourceTypeUserSetting' + type,
											method: 'POST',
											params: { mapSourceType: msv },
											success: function(r) {
												MAPSOURCE = msv;
												
												Ext.getCmp('map_cb').getStore().reload();
												Ext.getCmp('maps_cb').getStore().reload();
												Ext.getCmp('mapview_cb').getStore().reload();
												Ext.getCmp('view_cb').getStore().reload();
												Ext.getCmp('editmap_cb').getStore().reload();
												Ext.getCmp('maplayer_cb').getStore().reload();

												Ext.getCmp('map_cb').reset();
												Ext.getCmp('mapview_cb').reset();
												
												if (MAPSOURCE == map_source_type_geojson) {
													Ext.getCmp('register_chb').enable();
													
													if (Ext.getCmp('register_chb').checked) {
														mapping.show();
														shapefilePanel.show();
													}
												}
												else if (MAPSOURCE == map_source_type_shapefile) {
													Ext.getCmp('register_chb').enable();
													
													if (Ext.getCmp('register_chb').checked) {
														mapping.show();
														shapefilePanel.show();
													}
												}
												else if (MAPSOURCE == map_source_type_database) {
													Ext.getCmp('register_chb').disable();
													
													mapping.hide();
													shapefilePanel.hide();
												}
												
												if (MAP.layers.length > 2) {
													for (var i = MAP.layers.length - 1; i >= 2; i--) {
														MAP.removeLayer(MAP.layers[i]);
													}
												}
												addOverlaysToMap();
												
												Ext.messageBlack.msg( i18n_map_source, '<span class="x-msg-hl">' + msrw + '</span> '+i18n_is_saved_as_map_source+'.');
											},
											failure: function() {
												alert( i18n_status, i18n_error_while_saving_data );
											}
										});
										
										if (MAPSOURCE == map_source_type_geojson) {
											mapLayerMapSourceFileComboBox.showField();
											mapLayerPathWMSOverlayTextField.hideField();
										}
										else if (MAPSOURCE == map_source_type_shapefile) {
											mapLayerMapSourceFileComboBox.hideField();
											mapLayerPathWMSOverlayTextField.showField();
										}
									}
								}
							}
						}
					},
					{
						xtype: 'checkbox',
						id: 'register_chb',
						fieldLabel: i18n_admin_panels,
						labelSeparator: labelseparator,
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
								},
								scope: this
							}
						}
					}
				]
			},
			{
				xtype:'fieldset',
				columnWidth: 0.5,
				title: '&nbsp;<span class="panel-tab-title">'+i18n_base_coordinate+'</span>&nbsp;',
				collapsible: true,
				animCollapse: true,
				autoHeight:true,
				items:
				[
					{
						xtype: 'combo',
						id: 'baselongitude_cb',
						fieldLabel: i18n_longitude_x,
						valueField: 'longitude',
						displayField: 'longitude',
						editable: true,
						isFormField: true,
						emptyText: emptytext,
						width: combo_number_width,
						minListWidth: combo_number_width,
						triggerAction: 'all',
						value: BASECOORDINATE.longitude,
						mode: 'remote',
						store: baseCoordinateStore
					},	
					{
						xtype: 'combo',
						id: 'baselatitude_cb',
						fieldLabel: i18n_latitude_y,
						valueField: 'latitude',
						displayField: 'latitude',
						editable: true,
						isFormField: true,
						emptyText: emptytext,
						width: combo_number_width,
						minListWidth: combo_number_width,
						triggerAction: 'all',
						value: BASECOORDINATE.latitude,
						mode: 'remote',
						store: baseCoordinateStore
					},
					{ html: '<p style="height:5px;">' },
					{
						xtype: 'button',
						isFormField: true,
						fieldLabel: '',
						labelSeparator: '',
						text: i18n_save_coordinate,
						cls: 'aa_med',
						handler: function() {
							var blo = Ext.getCmp('baselongitude_cb').getRawValue();
							var bla = Ext.getCmp('baselatitude_cb').getRawValue();
							
							Ext.Ajax.request({
								url: path + 'setBaseCoordinate' + type,
								method: 'POST',
								params: {longitude:blo, latitude:bla},
								
								success: function() {
									BASECOORDINATE = {longitude:blo, latitude:bla};
									Ext.messageBlack.msg( i18n_base_coordinate , i18n_longitude_x + ' <span class="x-msg-hl">' + blo + '</span> '+i18n_and+' '+i18n_latitude_y+' <span class="x-msg-hl">' + bla + '</span> ' + i18n_was_saved_as_base_coordinate);
									Ext.getCmp('newlongitude_cb').getStore().reload();
									Ext.getCmp('newlongitude_cb').setValue(blo);
									Ext.getCmp('newlatitude_cb').setValue(bla);
									Ext.getCmp('baselongitude_cb').getStore().reload();
									Ext.getCmp('baselongitude_cb').setValue(blo);
									Ext.getCmp('baselatitude_cb').setValue(bla);
								},
								failure: function() {
									alert('Error: setBaseCoordinate');
								}
							});
						}
					}
				]
			}
        ],
        listeners: {
            expand: {
                fn: function() {
                    if (MAPSOURCE == map_source_type_geojson) {
                        Ext.getCmp('register_chb').enable();
                    }
                    else if (MAPSOURCE == map_source_type_database) {
                        Ext.getCmp('register_chb').disable();
                    }
					
					ACTIVEPANEL = administration;
                }
            },
			collapse: {
				fn: function() {
					ACTIVEPANEL = false;
				}
			}
        }
    });
	
	/* Section: layers */
    var choroplethLayer = new OpenLayers.Layer.Vector('Thematic map', {
        'visibility': false,
        'displayInLayerSwitcher': false,
        'styleMap': new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
                    {'fillOpacity': 1, 'strokeColor': '#222222', 'strokeWidth': 1 },
                    OpenLayers.Feature.Vector.style['default']
                )
            ),
            'select': new OpenLayers.Style(
                {'strokeColor': '#000000', 'strokeWidth': 2, 'cursor': 'pointer'}
            )
        })
    });
    
    MAP.addLayers([ choroplethLayer ]);
    
	function addOverlaysToMap() {
		Ext.Ajax.request({
			url: path + 'getMapLayersByType' + type,
            params: { type: map_layer_type_overlay },
			method: 'POST',
			success: function(r) {
				var mapLayers = Ext.util.JSON.decode(r.responseText).mapLayers;
				
				for (var i = 0; i < mapLayers.length; i++) {
					var mapurl = MAPSOURCE == map_source_type_geojson ? path + 'getGeoJson.action?name=' + mapLayers[i].mapSource : path_geoserver + wfs + mapLayers[i].mapSource + output;
					var fillColor = mapLayers[i].fillColor;
					var fillOpacity = parseFloat(mapLayers[i].fillOpacity);
					var strokeColor = mapLayers[i].strokeColor;
					var strokeWidth = parseFloat(mapLayers[i].strokeWidth);
					
					var treeLayer = new OpenLayers.Layer.Vector(mapLayers[i].name, {
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
							'url': mapurl,
							'format': new OpenLayers.Format.GeoJSON()
						})
					});
					
					treeLayer.events.register('loadstart', null, function() {
						MASK.msg = i18n_loading;
						MASK.show();
					});
					
					treeLayer.events.register('loadend', null, function() {
						MASK.hide();
					});
						
					MAP.addLayer(treeLayer);
				}
			},
			failure: function() {
				alert('Error: getAllMapLayers');
			}
		});
	}
	
	addOverlaysToMap();
	
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
        layer: 'Thematic map'
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
        map: MAP,
        layer: choroplethLayer,
		title: '<span class="panel-title">' + i18n_thematic_map + '</span>',
        url: 'init',
        featureSelection: false,
        legendDiv: 'choroplethLegend',
        defaults: {width: 130},
        listeners: {
            expand: {
                fn: function() {
                    choroplethLayer.setVisibility(false);
                    choropleth.classify(false, true);
                    ACTIVEPANEL = thematicMap;
                }
            }
        }
    });
	
    mapping = new mapfish.widgets.geostat.Mapping({
        id: 'mapping',
        map: MAP,
        layer: choroplethLayer,
        title: '<span class="panel-title">'+i18n_assign_organisation_units_to_map+'</span>',
        url: 'init',
        featureSelection: false,
        legendDiv: 'choroplethLegend',
        defaults: {width: 130},
        listeners: {
            expand: {
                fn: function() {
                    choroplethLayer.setVisibility(false);
                    mapping.classify(false, true);
                    ACTIVEPANEL = organisationUnitAssignment;
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
			MAP.zoomIn();
		},
		scope: this
	});
	
	var zoomOutButton = new Ext.Button({
		iconCls: 'icon-zoomout',
		tooltip: i18n_zoom_out,
		handler:function() {
			MAP.zoomOut();
		},
		scope: this
	});
	
	var zoomMaxExtentButton = new Ext.Button({
		iconCls: 'icon-zoommin',
		tooltip: i18n_zoom_to_visible_extent,
		handler: function() {
			MAP.zoomToMaxExtent();
		},
		scope: this
	});
		
	var labelsButton = new Ext.Button({
		iconCls: 'icon-labels',
		tooltip: i18n_show_hide_labels,
		handler: function() {
			toggleFeatureLabels(true);				
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
	
	var pdfButton = new Ext.Button({
		iconCls: 'icon-pdf',
		tooltip: 'Export map as PDF',
		handler: function() {
			var active = ACTIVEPANEL;
			var printMultiPagePanel = Ext.getCmp('printMultiPage_p');
			if (printMultiPagePanel.hidden) {
				printMultiPagePanel.show();
				printMultiPagePanel.expand();
			}
			else {
				printMultiPagePanel.collapse();
				printMultiPagePanel.hide();
				if (active == thematicMap) {
					choropleth.expand();
				}
				else if (active == organisationUnitAssignment) {
					mapping.expand();
				}
			}			
		}
	});

    var automaticMapLegendSetButton = new Ext.Button({
		iconCls: 'icon-automaticlegendset',
		tooltip: i18n_create_legend_sets_for_legend_type + '"' + i18n_automatic + '"',
		handler: function() {
			var x = Ext.getCmp('center').x + 15;
			var y = Ext.getCmp('center').y + 41;    
			automaticMapLegendSetWindow.setPosition(x,y);
		
			if (automaticMapLegendSetWindow.visible) {
				automaticMapLegendSetWindow.hide();
			}
			else {
				automaticMapLegendSetWindow.show();
			}
		}
	});
	
	var predefinedMapLegendSetButton = new Ext.Button({
		iconCls: 'icon-predefinedlegendset',
		tooltip: i18n_create_legend_sets_for_legend_type + '"' + i18n_predefined + '"',
		handler: function() {
			var x = Ext.getCmp('center').x + 15;
			var y = Ext.getCmp('center').y + 41;    
			predefinedMapLegendSetWindow.setPosition(x,y);
		
			if (predefinedMapLegendSetWindow.visible) {
				predefinedMapLegendSetWindow.hide();
			}
			else {
				predefinedMapLegendSetWindow.show();
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
			' ',' ',' ',
			mapLabel,
			' ',' ',' ',' ',' ',
			zoomInButton,
			zoomOutButton,
			zoomMaxExtentButton,
			labelsButton,
			'-',
			exportImageButton,
			exportExcelButton,
			'-',
			favoritesButton,
			'-',
            automaticMapLegendSetButton,
			predefinedMapLegendSetButton,
			'-',
			helpButton,
			'->',
			exitButton
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
                height: north_height
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
                        title: '<span class="panel-title">'+i18n_overview_map+'</span>',
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
                        title: '<span class="panel-title">'+ i18n_map_legend +'</span>',
                        minHeight: 65,
                        autoHeight: true,
                        contentEl: 'legend',
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
                width: west_width,
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
                    shapefilePanel,
                    mapping,
					adminPanel,
					{
						xtype: 'print-multi',
						id: 'printMultiPage_p',
						title: '<span class="panel-title">Print multi page PDF</span>',
						formConfig: {
							labelWidth: 65,
							bodyStyle: 'padding: 7px;',
							defaults: {
								width: 140,
								listWidth: 140
							}
						},
						columns: [
							{
								header: 'Map title',
								width: 80,
								dataIndex: 'mapTitle',
								editor: new Ext.form.TextField()
							},
							{
								header: 'Comment',
								dataIndex: 'comment',
								editor: new Ext.form.TextField()
							}
						],
						border: false,
						map: MAP,
						configUrl: printConfigUrl,
						overrides: layerOverrides
					}
                ]
            },
            {
                xtype: 'gx_mappanel',
                region: 'center',
                id: 'center',
                height: 1000,
                width: 800,
                map: MAP,
                title: '',
                zoom: 3,
				tbar: mapToolbar
            }
        ]
    });
	
    shapefilePanel.hide();
	mapping.hide();
	Ext.getCmp('printMultiPage_p').hide();
	ACTIVEPANEL = thematicMap;
    
	/* Section: map controls */
	var selectFeatureChoropleth = new OpenLayers.Control.newSelectFeature(
        choroplethLayer, {
            onClickSelect: onClickSelectChoropleth,
            onClickUnselect: onClickUnselectChoropleth,
            onHoverSelect: onHoverSelectChoropleth,
            onHoverUnselect: onHoverUnselectChoropleth
        }
    );
    
    MAP.addControl(selectFeatureChoropleth);
    selectFeatureChoropleth.activate();

	MAP.addControl(new OpenLayers.Control.MousePosition({
        displayClass: 'void', 
        div: $('mouseposition'), 
        prefix: '<span style="color:#666;">x: &nbsp;</span>',
        separator: '<br/><span style="color:#666;">y: &nbsp;</span>'
    }));
    
    var vmap0 = new OpenLayers.Layer.WMS(
        "World",
        "http://labs.metacarta.com/wms/vmap0", 
        {layers: "basic"}
    );
    
    MAP.addControl(new OpenLayers.Control.OverviewMap({
        div: $('overviewmap'),
        size: new OpenLayers.Size(188, 97),
        minRectSize: 0,
        layers: [vmap0]
    }));
    
    MAP.addControl(new OpenLayers.Control.ZoomBox());
	
	MAP.setCenter(new OpenLayers.LonLat(BASECOORDINATE.longitude, BASECOORDINATE.latitude), 6);
    
	MAP.events.on({
        changelayer: function(e) {
            var isOverlay = false;
            for (var i = 0; i < mapLayerStore.getTotalCount(); i++) {
                if (mapLayerStore.getAt(i).data.name == e.layer.name) {
                    isOverlay = true;
                }
            }
            
            var activeOverlays = false;
            if (e.property == 'visibility' && isOverlay ) {
                if (e.layer.visibility) {
                    selectFeatureChoropleth.deactivate();
                }
                else {
                    for (var i = 0; i < mapLayerStore.getTotalCount(); i++) {
                        if (MAP.getLayersByName(mapLayerStore.getAt(i).data.name)[0].visibility) {
                            activeOverlays = true;
                        }
                    }
                    if (!activeOverlays) {
                        selectFeatureChoropleth.activate();
                    }
                }
            }
        }
    });
	
	Ext.getCmp('maplegendset_cb').hideField();
	Ext.getCmp('bounds').hideField();
	
    Ext.get('loading').fadeOut({remove: true});
	
	}});
	}});
	}});
	}});
});

/* Section: select features */
var popup;

var featureWindow = new Ext.Window({
    closeAction: 'hide',
    items: [
        {
            xtype: 'menu',
            id: 'feature_m',
            floating: false,
            items: [
                {
                    html: 'Centre orgunit in the map',
                    iconCls: 'no-icon',
                    listeners: {
                        'click': {
                            fn: function() {
                                MAP.setCenter(FEATURE.geometry.getBounds().getCenterLonLat());
                            }
                        }
                    }
                },
                {
                    html: 'Indicator value timeseries',
                    iconCls: 'no-icon',
                    listeners: {
                        'click': {
                            fn: function() {
                                periodWindow.setPagePosition(Ext.getCmp('east').x - 262, Ext.getCmp('center').y + 135);
                                periodWindow.show();
                            }
                        }
                    }
                }
            ]
        }
    ]
});

var periodTypeTimeseriesStore = new Ext.data.JsonStore({
    url: path + 'getAllPeriodTypes' + type,
    root: 'periodTypes',
    fields: ['name'],
    autoLoad: true
});

var periodTimeseriesStore = new Ext.data.JsonStore({
    url: path + 'getPeriodsByPeriodType' + type,
    baseParams: { name: 0 },
    root: 'periods',
    fields: ['id', 'name', 'value'],
    autoLoad: false                
});

var periodWindow = new Ext.Window({
    title: 'Select periods',
    closeAction: 'hide',
    defaults: { bodyStyle: 'padding:8px; border:0px' },
    width: 250,
    items: [
        {
            xtype: 'panel',
            items: [
                { html: '<div class="window-field-label-first">Period type</div>' },
                {
                    xtype: 'combo',
                    id: 'periodtypetimeseries_cb',
                    fieldLabel: 'Period type',
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
                    store: periodTypeTimeseriesStore,
                    listeners: {
                        'select': {
                            fn: function() {
                                var pt = Ext.getCmp('periodtypetimeseries_cb').getValue();
                                periodTimeseriesStore.baseParams = { name: pt };
                                periodTimeseriesStore.reload();
                            },
                            scope: this
                        }
                    }
                },
                { html: '<div class="window-field-label">Periods</div>' },
                {
                    xtype: 'multiselect',
                    id: 'periodstimeseries_ms',
                    dataFields: ['id', 'name'],
                    valueField: 'id',
                    displayField: 'name',
                    width: multiselect_width,
                    height: getMultiSelectHeight(),
                    store: periodTimeseriesStore
                },
                { html: '<div class="window-field-label">Window width</div>' },
                {
                    xtype: 'textfield',
                    id: 'timeserieswindowwidth_tf',
                    value: 800,
                    width: combo_number_width
                },
                { html: '<div class="window-field-label">Window height</div>' },
                {
                    xtype: 'textfield',
                    id: 'timeserieswindowheight_tf',
                    value: 400,
                    width: combo_number_width
                },
                {
                    xtype: 'button',
                    id: 'timeseries_b',
                    isFormField: true,
                    hideLabel: true,
                    cls: 'window-button',
                    text: 'Create graph',
                    handler: function() {
                        var iid = Ext.getCmp('indicator_cb').getValue();
                        var pids = Ext.getCmp('periodstimeseries_ms').getValue();
                        
                        var pidArray = new Array();
                        pidArray = pids.split(',');
                        
                        var pnameArray = new Array();
                        for (var i = 0; i < pidArray.length; i++) {
                            pnameArray[i] = [i, periodTimeseriesStore.getById(pidArray[i]).data.name];
                        }
                        
                        setMapValueTimeseriesStore(iid, pidArray, pnameArray, URL);
                        mapValueTimeseriesStore.reload();
                    }
                }
            ]
        }
    ]
});

var mapValueTimeseriesStore;

function setMapValueTimeseriesStore(iid, pidArray, pnameArray, URL) {
    var params = pidArray[0];
    if (pidArray.length > 1) {
        for (var i = 1; i < pidArray.length; i++) {
            params += '&periodIds=' + pidArray[i];
        }
    }
    
    mapValueTimeseriesStore = new Ext.data.JsonStore({
        url: path + 'getIndicatorMapValuesByMapAndFeatureId' + type + '?indicatorId=' + iid + '&mapLayerPath=' + URL + '&featureId=' + FEATURE.attributes[MAPDATA.nameColumn] + '&periodIds=' + params,
        root: 'mapvalues',
        fields:['orgUnitId', 'orgUnitName', 'featureId', 'periodId', 'value'],
        autoLoad: false,
        listeners: {
            'load': {
                fn: function() {
                    var title = FEATURE.attributes[MAPDATA.nameColumn];
                    var indicator = Ext.getCmp('indicator_cb').getRawValue();
                    
                    var valueArray = new Array();
                    for (var i = 0; i < pidArray.length; i++) {
                        for (var j = 0; j < mapValueTimeseriesStore.getCount(); j++) {
                            if (mapValueTimeseriesStore.getAt(j).data.periodId == pidArray[i]) {
                                valueArray[i] = [i, parseFloat(mapValueTimeseriesStore.getAt(j).data.value)];
                            }
                        }
                    }

                    CHART = getChart(title + ', ' + indicator, pnameArray, FEATURE.attributes[MAPDATA.nameColumn], valueArray);
                    CHART.show();
                }
            }
        }
    });
}

function getChart(title, pnameArray, name, valueArray) {
    var width = Ext.getCmp('timeserieswindowwidth_tf').getValue() || 800;
    var height = Ext.getCmp('timeserieswindowheight_tf').getValue() || 400;
    
    return new Ext.Window({
        title: title,
        defaults: { bodyStyle: 'padding:10px 32px 12px 22px; border:0px' },
        items: [{
            xtype: 'panel',
            items: [{
                xtype: 'flot',
                width: 1000,
                height: 300,
                series: [valueArray],
                xaxis: {
                    ticks: pnameArray
                }
            }]
        }]
    });
}

var chartWindow = new Ext.Window({
    closeAction: 'hide',
    defaults: { bodyStyle: 'padding:8px; border:0px' },
    items: CHART
});

function onHoverSelectChoropleth(feature) {
    if (MAPDATA != null) {
        if (ACTIVEPANEL == thematicMap) {
			Ext.getCmp('featureinfo_l').setText('<div style="color:black">' + feature.attributes[MAPDATA.nameColumn] + '</div><div style="color:#555">' + feature.attributes.value + '</div>', false);
        }
        else if (ACTIVEPANEL == organisationUnitAssignment) {
			Ext.getCmp('featureinfo_l').setText('<span style="color:black">' + feature.attributes[MAPDATA.nameColumn] + '</span>', false);
        }
    }
}

function onHoverUnselectChoropleth(feature) {
    Ext.getCmp('featureinfo_l').setText('<span style="color:#666">'+ i18n_no_feature_selected +'.</span>', false);
}

function onClickSelectChoropleth(feature) {
	FEATURE = feature;

	var east_panel = Ext.getCmp('east');
	var x = east_panel.x - 210;
	var y = east_panel.y + 41;
	
    if (ACTIVEPANEL == organisationUnitAssignment) {
		if (popup) {
			popup.destroy();
		}
		
		var feature_popup = new Ext.Window({
			title: '<span class="panel-title">Assign organisation unit</span>',
			width: 180,
			height: 65,
			layout: 'fit',
			plain: true,
			html: '<div class="window-orgunit-text">' + feature.attributes[MAPDATA.nameColumn] + '</div>',
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
		
		popup = feature_popup;		
		feature_popup.show();
		mapping.relation = feature.attributes[MAPDATA.nameColumn];
    }
	else {
        // featureWindow.setPagePosition(Ext.getCmp('east').x - 202, Ext.getCmp('center').y + 41);
        // featureWindow.setTitle(FEATURE.attributes[MAPDATA.nameColumn]);
        // featureWindow.show();
        // periodWindow.hide();
	}
}

function onClickUnselectChoropleth(feature) {}



/* Section: map data */
function loadMapData(redirect, position) {
    Ext.Ajax.request({
        url: path + 'getMapByMapLayerPath' + type,
        method: 'POST',
        params: { mapLayerPath: URL },
        success: function(r) {
			MAPDATA = Ext.util.JSON.decode(r.responseText).map[0];
            
            if (MAPSOURCE == map_source_type_database) {
                MAPDATA.name = Ext.getCmp('map_cb').getRawValue();
                MAPDATA.organisationUnit = 'Country';
                MAPDATA.organisationUnitLevel = Ext.getCmp('map_cb').getValue();
                MAPDATA.nameColumn = 'name';
                MAPDATA.longitude = BASECOORDINATE.longitude;
                MAPDATA.latitude = BASECOORDINATE.latitude;
                MAPDATA.zoom = 7;
            }
            else if (MAPSOURCE == map_source_type_geojson || MAPSOURCE == map_source_type_shapefile) {
                MAPDATA.organisationUnitLevel = parseFloat(MAPDATA.organisationUnitLevel);
                MAPDATA.longitude = parseFloat(MAPDATA.longitude);
                MAPDATA.latitude = parseFloat(MAPDATA.latitude);
                MAPDATA.zoom = parseFloat(MAPDATA.zoom);
            }
			
			if (!position) {
				if (MAPDATA.zoom != MAP.getZoom()) {
					MAP.zoomTo(MAPDATA.zoom);
				}
				MAP.setCenter(new OpenLayers.LonLat(MAPDATA.longitude, MAPDATA.latitude));
			}
			
			if (MAPVIEW) {
				if (MAPVIEW.longitude && MAPVIEW.latitude && MAPVIEW.zoom) {
					MAP.setCenter(new OpenLayers.LonLat(MAPVIEW.longitude, MAPVIEW.latitude), MAPVIEW.zoom);
				}
				else {
					MAP.setCenter(new OpenLayers.LonLat(MAPDATA.longitude, MAPDATA.latitude), MAPDATA.zoom);
				}
				MAPVIEW = false;
			}
			
			toggleFeatureLabels(false);

            if (redirect == thematicMap) {
                getChoroplethData(); }
            else if (redirect == organisationUnitAssignment) {
                getAssignOrganisationUnitData(); }
            else if (redirect == 'auto-assignment') {
                getAutoAssignOrganisationUnitData(position); }
        },
        failure: function() {
            alert( i18n_error_while_retrieving_data + ': loadMapData' );
        } 
    });
}


/* Section: choropleth */
function getChoroplethData() {
	MASK.msg = i18n_creating_choropleth ;
	MASK.show();
	
    var indicatorId = Ext.getCmp('indicator_cb').getValue();
    var periodId = Ext.getCmp('period_cb').getValue();
    var mapLayerPath = MAPDATA.mapLayerPath;
	var url = MAPSOURCE == map_source_type_geojson || MAPSOURCE == map_source_type_shapefile ? 'getIndicatorMapValuesByMap' : 'getIndicatorMapValuesByLevel';
	var params = MAPSOURCE == map_source_type_geojson || MAPSOURCE == map_source_type_shapefile ? { indicatorId: indicatorId, periodId: periodId, mapLayerPath: mapLayerPath } : { indicatorId: indicatorId, periodId: periodId, level: mapLayerPath };

    Ext.Ajax.request({
        url: path + url + type,
        method: 'POST',
        params: params,
        success: function(r) {
			var features = MAP.getLayersByName('Thematic map')[0].features;
			var mapvalues = Ext.util.JSON.decode(r.responseText).mapvalues;
			EXPORTVALUES = getExportDataValueJSON( mapvalues );
			var mv = new Array();
			var nameColumn = MAPDATA.nameColumn;
			var options = {};
			
			if (mapvalues.length == 0) {
				Ext.messageRed.msg( i18n_thematic_map ,  i18n_selected_period_level_no_data );
				MASK.hide();
				return;
			}

			for (var i = 0; i < mapvalues.length; i++) {
				mv[mapvalues[i].orgUnitName] = mapvalues[i].orgUnitName ? mapvalues[i].value : '';
			}

			if (MAPSOURCE == map_source_type_geojson || MAPSOURCE == map_source_type_shapefile) {
				for (var j = 0; j < features.length; j++) {
					features[j].attributes.value = mv[features[j].attributes[nameColumn]] ? mv[features[j].attributes[nameColumn]] : 0;
				}
			}
			else if (MAPSOURCE == map_source_type_database) {
				for (var i = 0; i < mapvalues.length; i++) {
					for (var j = 0; j < features.length; j++) {
						if (mapvalues[i].orgUnitName == features[j].attributes.name) {
							features[j].attributes.value = parseFloat(mapvalues[i].value);
							break;
						}
					}
				}
			}

			choropleth.indicator = options.indicator = 'value';
			options.method = Ext.getCmp('method').getValue();
			options.numClasses = Ext.getCmp('numClasses').getValue();
			options.colors = choropleth.getColors();
			
			choropleth.coreComp.updateOptions(options);
			choropleth.coreComp.applyClassification();
			choropleth.classificationApplied = true;
			
			MASK.hide();		
        },
        failure: function() {
            alert( 'Error: getIndicatorMapValues' );
        } 
    });
}

/* Section: mapping */
function getAssignOrganisationUnitData() {
	MASK.msg = i18n_creating_map;
	MASK.show();
	
    var mlp = MAPDATA.mapLayerPath;
	var relations =	 Ext.getCmp('grid_gp').getStore();
	var features = MAP.getLayersByName('Thematic map')[0].features;
	var nameColumn = MAPDATA.nameColumn;
	var noCls = 1;
	var noAssigned = 0;
	var options = {};
	
	for (var i = 0; i < features.length; i++) {
		features[i].attributes['value'] = 0;
	
		for (var j = 0; j < relations.getTotalCount(); j++) {
			if (relations.getAt(j).data.featureId == features[i].attributes[nameColumn]) {
				features[i].attributes['value'] = 1;
				noAssigned++;
				noCls = noCls < 2 ? 2 : noCls;
				break;
			}
		}
	}

	var color = noCls > 1 && noAssigned == features.length ? assigned_row_color : unassigned_row_color;
	noCls = noCls > 1 && noAssigned == features.length ? 1 : noCls;
	
	mapping.indicator = options.indicator = 'value';
	options.method = 1;
	options.numClasses = noCls;
	
	var colorA = new mapfish.ColorRgb();
	colorA.setFromHex(color);
	var colorB = new mapfish.ColorRgb();
	colorB.setFromHex(assigned_row_color);
	options.colors = [colorA, colorB];
	
	mapping.coreComp.updateOptions(options);
	mapping.coreComp.applyClassification();
	mapping.classificationApplied = true;
	
	MASK.hide();
}

/* Section: auto-mapping */
function getAutoAssignOrganisationUnitData(position) {
	MASK.msg = i18n_loading ;
	MASK.show();

    var level = MAPDATA.organisationUnitLevel;

    Ext.Ajax.request({
        url: path + 'getOrganisationUnitsAtLevel' + type,
        method: 'POST',
        params: { level: level },
        success: function(r) {
		    var layers = MAP.getLayersByName('Thematic map');
			var features = layers[0]['features'];
			var organisationUnits = Ext.util.JSON.decode(r.responseText).organisationUnits;
			var nameColumn = MAPDATA.nameColumn;
			var mlp = MAPDATA.mapLayerPath;
			var count_match = 0;
			var relations = '';
			
			for ( var i = 0; i < features.length; i++ ) {
				features[i].attributes.compareName = features[i].attributes[nameColumn].split(' ').join('').toLowerCase();
			}
	
			for ( var i = 0; i < organisationUnits.length; i++ ) {
				organisationUnits[i].compareName = organisationUnits[i].name.split(' ').join('').toLowerCase();
			}
			
			for ( var i = 0; i < organisationUnits.length; i++ ) {
				for ( var j = 0; j < features.length; j++ ) {
					if (features[j].attributes.compareName == organisationUnits[i].compareName) {
						count_match++;
						relations += organisationUnits[i].id + '::' + features[j].attributes[nameColumn] + ';;';
						break;
					}
				}
			}
			
			MASK.msg = count_match == 0 ? i18n_no + ' ' + i18n_organisation_units + ' ' +  i18n_assigned +'...' : + i18n_assigning +' ' + count_match + ' '+ i18n_organisation_units + '...';
			MASK.show();

			Ext.Ajax.request({
				url: path + 'addOrUpdateMapOrganisationUnitRelations' + type,
				method: 'POST',
				params: { mapLayerPath: mlp, relations: relations },

				success: function(r) {
					MASK.msg = i18n_applying_organisation_units_relations ;
					MASK.show();
					
					Ext.messageBlack.msg( i18n_assign + ' ' + i18n_organisation_units, '<span class="x-msg-hl">' + count_match + '</span> '+ i18n_organisation_units_assigned +'.<br><br>Database: <span class="x-msg-hl">' + organisationUnits.length + '</span><br>Shapefile: <span class="x-msg-hl">' + features.length + '</span>');
					
					Ext.getCmp('grid_gp').getStore().reload();
					loadMapData(organisationUnitAssignment, position);
				},
				failure: function() {
					alert( 'Error: addOrUpdateMapOrganisationUnitRelations' );
				} 
			});
        },
        failure: function() {
            alert( i18n_status , i18n_error_while_retrieving_data );
        } 
    });
}