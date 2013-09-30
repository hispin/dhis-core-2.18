Ext.onReady( function() {
	/*
	CONFIG              			TYPE            DEFAULT             DESCRIPTION

	url                 			string                              (Required) The base url of the DHIS instance.
	el                  			string                              (Required) The element id to render the map.
	id								string                              (Optional) A map uid. If provided, only 'el' and 'url' are required.
	longitude						number			<zoom visible>		(Optional) Initial map center longitude.
	latitude						number			<zoom visible>		(Optional) Initial map center latitude.
	zoom							number			<zoom visible>		(Optional) Initial zoom level.
	mapViews						[object]							(Required*) Array of mapViews. *Required if no map id is provided.

	layer							string			'thematic1'			(Optional) 'boundary', 'thematic1', 'thematic2' or 'facility'.
	indicator          				string								(Required*) Indicator uid. *Required if no data element is provided.
	dataelement	        			string								(Required*) Data element uid. *Required if no indicator is provided.
	period							string								(Required) Fixed period ISO.
	level							number			2					(Optional) Organisation unit level.
	parent							string								(Required) Parent organisation unit uid.
	legendSet						string								(Optional) Legend set uid.
	classes							number			5					(Optional) Automatic legend set, number of classes.
	method							number			2					(Optional) Automatic legend set, method. 2 = by class range. 3 = by class count.
	colorLow						string			'ff0000' (red)		(Optional) Automatic legend set, low color.
	colorHigh						string			'00ff00' (green)	(Optional) Automatic legend set, high color.
	radiusLow						number			5					(Optional) Automatic legend set, low radius for points.
	radiusHigh						number			15					(Optional) Automatic legend set, high radius for points.
	*/
	
	// css	
	var css = 'body { \n font-family: arial, sans-serif, liberation sans, consolas !important; \n font-size: 11px; \n } \n';
	css += '.x-panel-body { \n font-size: 11px; \n } \n';
	css += '.x-panel-header { \n height: 30px; \n padding: 7px 4px 4px 7px; \n border: 0 none; \n } \n';
	css += '.olControlPanel { \n position: absolute; \n top: 0; \n right: 0; \n border: 0 none; \n } \n';
	css += '.olControlButtonItemActive { \n background: #556; \n color: #fff; \n width: 24px; \n height: 24px; \n opacity: 0.75; \n filter: alpha(opacity=75); \n -ms-filter: "alpha(opacity=75)"; \n cursor: pointer; \n cursor: hand; \n text-align: center; \n font-size: 21px !important; \n text-shadow: 0 0 1px #ddd; \n } \n';
	css += '.olControlPanel.zoomIn { \n right: 72px; \n } \n';
	css += '.olControlPanel.zoomIn .olControlButtonItemActive { \n border-bottom-left-radius: 2px; \n } \n';
	css += '.olControlPanel.zoomOut { \n right: 48px; \n } \n';
	css += '.olControlPanel.zoomVisible { \n right: 24px; \n } \n';
	css += '.olControlPermalink { \n display: none !important; \n } \n';
	css += '.olControlMousePosition { \n background: #fff !important; \n opacity: 0.8 !important; \n filter: alpha(opacity=80) !important; \n -ms-filter: "alpha(opacity=80)" !important; \n right: 0 !important; \n bottom: 0 !important; \n border-top-left-radius: 2px !important; \n padding: 2px 2px 2px 5px !important; \n color: #000 !important; \n -webkit-text-stroke-width: 0.2px; \n -webkit-text-stroke-color: #555; \n } \n';
	css += '.olControlMousePosition * { \n font-size: 10px !important; \n } \n';
	css += '.text-mouseposition-lonlat { \n color: #555; \n } \n';
	css += '.olLayerGoogleCopyright, .olLayerGoogleV3.olLayerGooglePoweredBy { \n display: none; \n } \n';
	css += '#google-logo { \n background: url("../images/google-logo.png") no-repeat; \n width: 40px; \n height: 13px; \n margin-left: 6px; \n display: inline-block; \n vertical-align: bottom; \n cursor: pointer; \n cursor: hand; \n } \n';
	css += '.olControlScaleLine { \n left: 5px !important; \n bottom: 5px !important; \n } \n';
	css += '.olControlScaleLineBottom { \n display: none; \n } \n';
	css += '.olControlScaleLineTop { \n font-weight: bold; \n } \n';
	css += '.x-mask-msg { \n padding: 0; \n border: 0 none; \n background-image: none; \n background-color: transparent; \n } \n';
	css += '.x-mask-msg div { \n background-position: 11px center; \n } \n';
	css += '.x-mask-msg .x-mask-loading { \n border: 0 none; \n background-color: #000; \n color: #fff; \n border-radius: 2px; \n padding: 12px 14px 12px 30px; \n opacity: 0.65; \n } \n';

	Ext.util.CSS.createStyleSheet(css);

	GIS.plugin = {};

	GIS.plugin.getMap = function(config) {
		var validateConfig,
			onRender,
			afterRender,
			getViews,
			createViewport,
			//gis,
			initialize;

		validateConfig = function() {
			if (!config.url || !Ext.isString(config.url)) {
				alert('Invalid url (' + config.el + ')');
				return false;
			}

			if (!config.el || !Ext.isString(config.el)) {
				alert('Invalid html element id (' + config.el + ')');
				return false;
			}

			if (config.id) {
				if (Ext.isString(config.id)) {
					return true;
				}
				else {
					alert('Invalid map id (' + config.el + ')');
					return false;
				}
			}
			else {
				if (!config.indicator && !config.dataelement) {
					alert('No indicator or data element (' + config.el + ')');
					return false;
				}
				else {
					if (config.indicator && !Ext.isString(config.indicator)) {
						alert('Invalid indicator id (' + config.el + ')');
						return false;
					}
					if (config.dataelement && !Ext.isString(config.dataelement)) {
						alert('Invalid dataelement id (' + config.el + ')');
						return false;
					}
				}

				if (!config.period) {
					alert('No period (' + config.el + ')');
					return false;
				}

				if (!config.level || !Ext.isNumber(config.level)) {
					alert('Invalid organisation unit level (' + config.el + ')');
					return false;
				}

				if (!config.parent || !Ext.isNumber(config.parent)) {
					alert('Invalid parent organisation unit (' + config.el + ')');
					return false;
				}
			}
		};

		afterRender = function(vp) {
			var len = Ext.query('.zoomInButton').length;

			for (var i = 0; i < len; i++) {
				Ext.query('.zoomInButton')[i].innerHTML = '<img src="images/zoomin_24.png" />';
				Ext.query('.zoomOutButton')[i].innerHTML = '<img src="images/zoomout_24.png" />';
				Ext.query('.zoomVisibleButton')[i].innerHTML = '<img src="images/zoomvisible_24.png" />';
				Ext.query('.measureButton')[i].innerHTML = '<img src="images/measure_24.png" />';
			}
		};

		getViews = function() {
			var view,
				views = [],
				indicator = gis.conf.finals.dimension.indicator.id,
				dataElement = gis.conf.finals.dimension.dataElement.id,
				automatic = gis.conf.finals.widget.legendtype_automatic,
				predefined = gis.conf.finals.widget.legendtype_predefined;

			config.mapViews = config.mapViews || [];

			for (var i = 0; i < config.mapViews.length; i++) {
				view = config.mapViews[i];

				view = {
					layer: view.layer || 'thematic1',
					valueType: view.indicator ? indicator : dataElement,
					indicator: {
						id: view.indicator
					},
					dataElement: {
						id: view.dataelement
					},
					period: {
						id: view.period
					},
					legendType: view.legendSet ? predefined : automatic,
					legendSet: view.legendSet,
					classes: parseInt(view.classes) || 5,
					method: parseInt(view.method) || 2,
					colorLow: view.colorLow || 'ff0000',
					colorHigh: view.colorHigh || '00ff00',
					radiusLow: parseInt(view.radiusLow) || 5,
					radiusHigh: parseInt(view.radiusHigh) || 15,
					organisationUnitLevel: {
						level: parseInt(view.level) || 2
					},
					parentOrganisationUnit: {
						id: view.parent
					},
					opacity: parseFloat(view.opacity) || 0.8
				};

				views.push(view);
			}

			return views;
		};

		createViewport = function() {
			var viewport,
				eastRegion,
				centerRegion,
				el = Ext.get(gis.el);

			viewport = Ext.create('Ext.panel.Panel', {
				renderTo: el,
				width: el.getWidth(),
				height: el.getHeight(),
				layout: {
					type: 'hbox',
					align: 'stretch'
				},
				items: [
					{
						xtype: 'gx_mappanel',
						map: gis.olmap,
						bodyStyle: 'border:0 none',
						width: el.getWidth() - 200,
						height: el.getHeight(),
						listeners: {
							added: function() {
								centerRegion = this;
							}
						}
					},
					{
						xtype: 'panel',
						layout: 'anchor',
						bodyStyle: 'border-top:0 none; border-bottom:0 none',
						width: 200,
						preventHeader: true,
						defaults: {
							bodyStyle: 'padding: 6px; border: 0 none',
							collapsible: true,
							collapsed: true,
							animCollapse: false
						},
						items: [
							{
								title: GIS.i18n.thematic_layer_1_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic1.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.thematic_layer_2_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic2.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.thematic_layer_3_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic3.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.thematic_layer_4_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.thematic4.legendPanel = this;
									}
								}
							},
							{
								title: GIS.i18n.facility_layer_legend,
								bodyStyle: 'padding:3px 0 4px 5px; border-width:1px 0 1px 0; border-color:#d0d0d0;',
								listeners: {
									added: function() {
										gis.layer.facility.legendPanel = this;
									}
								}
							}
						],
						listeners: {
							added: function() {
								eastRegion = this;
							}
						}
					}
				],
				listeners: {
					afterrender: function() {
						afterRender();
					}
				}
			});

			viewport.centerRegion = centerRegion;
			viewport.eastRegion = eastRegion;

			return viewport;
		};

		initialize = function() {
			if (!validateConfig()) {
				return;
			}

			Ext.data.JsonP.request({
				url: '../initialize.action',
				success: function(r) {
					gis = GIS.core.getInstance(r);
					
					gis.el = config.el;

					GIS.core.createSelectHandlers(gis, gis.layer.boundary);
					GIS.core.createSelectHandlers(gis, gis.layer.thematic1);
					GIS.core.createSelectHandlers(gis, gis.layer.thematic2);
					GIS.core.createSelectHandlers(gis, gis.layer.thematic3);
					GIS.core.createSelectHandlers(gis, gis.layer.thematic4);
					GIS.core.createSelectHandlers(gis, gis.layer.facility);

					gis.map = {
						id: config.id,
						longitude: config.longitude,
						latitude: config.latitude,
						zoom: config.zoom,
						mapViews: getViews()
					};

					gis.viewport = createViewport();

					gis.olmap.mask = Ext.create('Ext.LoadMask', gis.viewport.centerRegion.getEl(), {
						msg: 'Loading'
					});

					GIS.core.MapLoader(gis).load();
				}
			});
		}();
	};

	GIS.getMap = GIS.plugin.getMap;
});
