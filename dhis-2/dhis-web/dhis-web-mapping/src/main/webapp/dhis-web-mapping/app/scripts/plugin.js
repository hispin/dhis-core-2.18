Ext.onReady( function() {
	
	// css	
	var css = 'body { font-family: arial, sans-serif, liberation sans, consolas !important; font-size: 11px; } \n';
	css += '.x-panel-body { font-size: 11px; } \n';
	css += '.x-panel-header { height: 30px; padding: 7px 4px 4px 7px; border: 0 none; } \n';
	css += '.olControlPanel { position: absolute; top: 0; right: 0; border: 0 none; } \n';
	css += '.olControlButtonItemActive { background: #556; color: #fff; width: 24px; height: 24px; opacity: 0.75; filter: alpha(opacity=75); -ms-filter: "alpha(opacity=75)"; cursor: pointer; cursor: hand; text-align: center; font-size: 21px !important; text-shadow: 0 0 1px #ddd; } \n';
	css += '.olControlPanel.zoomIn { right: 72px; } \n';
	css += '.olControlPanel.zoomIn .olControlButtonItemActive { border-bottom-left-radius: 2px; } \n';
	css += '.olControlPanel.zoomOut { right: 48px; } \n';
	css += '.olControlPanel.zoomVisible { right: 24px; } \n';
	css += '.olControlPermalink { display: none !important; } \n';
	css += '.olControlMousePosition { background: #fff !important; opacity: 0.8 !important; filter: alpha(opacity=80) !important; -ms-filter: "alpha(opacity=80)" !important; right: 0 !important; bottom: 0 !important; border-top-left-radius: 2px !important; padding: 2px 2px 2px 5px !important; color: #000 !important; -webkit-text-stroke-width: 0.2px; -webkit-text-stroke-color: #555; } \n';
	css += '.olControlMousePosition * { font-size: 10px !important; } \n';
	css += '.text-mouseposition-lonlat { color: #555; } \n';
	css += '.olLayerGoogleCopyright, .olLayerGoogleV3.olLayerGooglePoweredBy { display: none; } \n';
	css += '#google-logo { background: url("images/google-logo.png") no-repeat; width: 40px; height: 13px; margin-left: 6px; display: inline-block; vertical-align: bottom; cursor: pointer; cursor: hand; } \n';
	css += '.olControlScaleLine { left: 5px !important; bottom: 5px !important; } \n';
	css += '.olControlScaleLineBottom { display: none; } \n';
	css += '.olControlScaleLineTop { font-weight: bold; } \n';
	css += '.x-mask-msg { padding: 0; border: 0 none; background-image: none; background-color: transparent; } \n';
	css += '.x-mask-msg div { background-position: 11px center; } \n';
	css += '.x-mask-msg .x-mask-loading { border: 0 none; background-color: #000; color: #fff; border-radius: 2px; padding: 12px 14px 12px 30px; opacity: 0.65; } \n';
	css += '.gis-window-widget-feature { padding: 0; border: 0 none; border-radius: 0; background: transparent; box-shadow: none; } \n';
	css += '.gis-window-widget-feature .x-window-body-default { border: 0 none; background: transparent; } \n';
	css += '.gis-window-widget-feature .x-window-body-default .x-panel-body-default { border: 0 none; background: #556; opacity: 0.92; filter: alpha(opacity=92); -ms-filter: "alpha(opacity=92)"; padding: 5px 8px 5px 8px; border-bottom-left-radius: 2px; border-bottom-right-radius: 2px; color: #fff; font-weight: bold; letter-spacing: 1px; } \n';
	css += '.x-menu-body { border-color: #bbb; border-radius: 2px; padding: 0; background-color: #fff !important; } \n';
	css += '.x-menu-item-active .x-menu-item-link {	border-radius: 0; border-color: #e1e1e1; background-color: #e1e1e1; background-image: none; } \n';
	css += '.x-menu-item-link { padding: 4px 5px 4px 26px; } \n';
	css += '.x-menu-item-text { color: #111; } \n';
	css += '.disabled { opacity: 0.4; cursor: default !important; } \n';
	css += '.el-opacity-1 { opacity: 1 !important; } \n';
	css += '.el-border-0, .el-border-0 .x-panel-body { border: 0 none !important; } \n';
	css += '.el-fontsize-10 { font-size: 10px !important; } \n';
	css += '.gis-grid-row-icon-disabled * { cursor: default !important; } \n';
	css += '.gis-toolbar-btn-menu { margin-top: 4px; } \n';
	css += '.gis-toolbar-btn-menu .x-panel-body-default { border-radius: 2px; border-color: #999; } \n';
	css += '.gis-grid .link, .gis-grid .link * { cursor: pointer; cursor: hand; color: blue; text-decoration: underline; } \n';
	css += '.gis-menu-item-icon-drill, .gis-menu-item-icon-float { left: 6px; } \n';
	css += '.gis-menu-item-first.x-menu-item-active .x-menu-item-link {	border-radius: 0; border-top-left-radius: 2px; border-top-right-radius: 2px; } \n';
	css += '.gis-menu-item-last.x-menu-item-active .x-menu-item-link { border-radius: 0; border-bottom-left-radius: 2px; border-bottom-right-radius: 2px; } \n';
	css += '.gis-menu-item-icon-drill { \n background: url("images/drill_16.png") no-repeat; } \n';
	css += '.gis-menu-item-icon-float { background: url("images/float_16.png") no-repeat; } \n';
	css += '.x-color-picker a { padding: 0; } \n';
	css += '.x-color-picker em span { width: 14px; height: 14px; } \n';
	
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
			if (!Ext.isString(config.url)) {
				alert('Invalid url (' + config.el + ')');
				return;
			}

			if (!Ext.isString(config.el)) {
				alert('Invalid html element id (' + config.el + ')');
				return;
			}
			
			if (config.id && !Ext.isString(config.id)) {
				alert('Invalid map id (' + config.el + ')');
				return;
			}
			
			return true;
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

					gis.map = config;

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
