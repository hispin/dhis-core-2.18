Ext.onReady( function() {

	// ext config
	Ext.Ajax.method = 'GET';

	// namespace
	if (!('GIS' in window)) {
		GIS = {
			i18n: {
				thematic_layer_1_legend: 'Thematic layer 1 legend',
				thematic_layer_2_legend: 'Thematic layer 2 legend',
				thematic_layer_3_legend: 'Thematic layer 3 legend',
				thematic_layer_4_legend: 'Thematic layer 4 legend',
				facility_layer_legend: 'Facility layer legend'				
			}
		};
	}

	// mode
	GIS.isDebug = false;

	// html5
	GIS.isSessionStorage = 'sessionStorage' in window && window['sessionStorage'] !== null;

	// log
	GIS.logg = [];

	// core

	GIS.core = {};
	GIS.core.instances = [];

	GIS.core.getOLMap = function(gis) {
		var olmap,
			addControl;

		addControl = function(name, fn) {
			var button,
				panel;

			button = new OpenLayers.Control.Button({
				displayClass: 'olControlButton',
				trigger: function() {
					fn.call(gis.olmap);
				}
			});

			panel = new OpenLayers.Control.Panel({
				defaultControl: button
			});

			panel.addControls([button]);

			olmap.addControl(panel);

			panel.div.className += ' ' + name;
			panel.div.childNodes[0].className += ' ' + name + 'Button';
		};

		olmap = new OpenLayers.Map({
			controls: [
				new OpenLayers.Control.Navigation({
					zoomWheelEnabled: true,
					documentDrag: true
				}),
				new OpenLayers.Control.MousePosition({
					prefix: '<span id="mouseposition" class="el-fontsize-10"><span class="text-mouseposition-lonlat">LON </span>',
					separator: '<span class="text-mouseposition-lonlat">,&nbsp;LAT </span>',
					suffix: '<div id="google-logo" name="http://www.google.com/intl/en-US_US/help/terms_maps.html" onclick="window.open(Ext.get(this).dom.attributes.name.nodeValue);"></div></span>'
				}),
				new OpenLayers.Control.Permalink(),
				new OpenLayers.Control.ScaleLine({
					geodesic: true,
					maxWidth: 170,
					minWidth: 100
				})
			],
			displayProjection: new OpenLayers.Projection('EPSG:4326'),
			maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508),
			mouseMove: {}, // Track all mouse moves
			relocate: {} // Relocate organisation units
		});

		// Map events
		olmap.events.register('mousemove', null, function(e) {
			gis.olmap.mouseMove.x = e.clientX;
			gis.olmap.mouseMove.y = e.clientY;
		});

		olmap.zoomToVisibleExtent = function() {
			gis.util.map.zoomToVisibleExtent(this);
		};

		olmap.closeAllLayers = function() {
			gis.layer.boundary.core.reset();
			gis.layer.thematic1.core.reset();
			gis.layer.thematic2.core.reset();
			gis.layer.thematic3.core.reset();
			gis.layer.thematic4.core.reset();
			gis.layer.facility.core.reset();
		};

		addControl('zoomIn', olmap.zoomIn);
		addControl('zoomOut', olmap.zoomOut);
		addControl('zoomVisible', olmap.zoomToVisibleExtent);
		addControl('measure', function() {
			GIS.core.MeasureWindow(gis).show();
		});

		return olmap;
	};

	GIS.core.getLayers = function(gis) {
		var layers = {},
			createSelectionHandlers,
			layerNumbers = ['1', '2', '3', '4'];

		if (window.google) {
			layers.googleStreets = new OpenLayers.Layer.Google('Google Streets', {
				numZoomLevels: 20,
				animationEnabled: true,
				layerType: gis.conf.finals.layer.type_base,
				layerOpacity: 1,
				setLayerOpacity: function(number) {
					if (number) {
						this.layerOpacity = parseFloat(number);
					}
					this.setOpacity(this.layerOpacity);
				}
			});
			layers.googleStreets.id = 'googleStreets';

			layers.googleHybrid = new OpenLayers.Layer.Google('Google Hybrid', {
				type: google.maps.MapTypeId.HYBRID,
				numZoomLevels: 20,
				animationEnabled: true,
				layerType: gis.conf.finals.layer.type_base,
				layerOpacity: 1,
				setLayerOpacity: function(number) {
					if (number) {
						this.layerOpacity = parseFloat(number);
					}
					this.setOpacity(this.layerOpacity);
				}
			});
			layers.googleHybrid.id = 'googleHybrid';
		}

		layers.openStreetMap = new OpenLayers.Layer.OSM.Mapnik('OpenStreetMap', {
			layerType: gis.conf.finals.layer.type_base,
			layerOpacity: 1,
			setLayerOpacity: function(number) {
				if (number) {
					this.layerOpacity = parseFloat(number);
				}
				this.setOpacity(this.layerOpacity);
			}
		});
		layers.openStreetMap.id = 'openStreetMap';

		layers.facility = GIS.core.VectorLayer(gis, 'facility', GIS.i18n.facility_layer, {opacity: 1});
		layers.facility.core = new mapfish.GeoStat.Facility(gis.olmap, {
			layer: layers.facility,
			gis: gis
		});

		layers.boundary = GIS.core.VectorLayer(gis, 'boundary', GIS.i18n.boundary_layer, {opacity: 0.8});
		layers.boundary.core = new mapfish.GeoStat.Boundary(gis.olmap, {
			layer: layers.boundary,
			gis: gis
		});
		
		for (var i = 0, number; i < layerNumbers.length; i++) {
			number = layerNumbers[i];			
		
			layers['thematic' + number] = GIS.core.VectorLayer(gis, 'thematic' + number, GIS.i18n.thematic_layer + ' ' + number, {opacity: 0.8});
			layers['thematic' + number].layerCategory = gis.conf.finals.layer.category_thematic,
			layers['thematic' + number].core = new mapfish.GeoStat['Thematic' + number](gis.olmap, {
				layer: layers['thematic' + number],
				gis: gis
			});
		}

		return layers;
	};

	GIS.core.createSelectHandlers = function(gis, layer) {
		var isRelocate = !!GIS.app ? (gis.init.user.isAdmin ? true : false) : false,

			window,
			infrastructuralPeriod,
			onHoverSelect,
			onHoverUnselect,
			onClickSelect,
			dimConf = gis.conf.finals.dimension;

		onHoverSelect = function fn(feature) {
			if (window) {
				window.destroy();
			}
			window = Ext.create('Ext.window.Window', {
				cls: 'gis-window-widget-feature',
				preventHeader: true,
				shadow: false,
				resizable: false,
				items: {
					html: feature.attributes.label
				}
			});

			window.show();

			var eastX = gis.viewport.eastRegion.getPosition()[0],
				centerX = gis.viewport.centerRegion.getPosition()[0],
				centerRegionCenterX = centerX + ((eastX - centerX) / 2),
				centerRegionY = gis.viewport.centerRegion.getPosition()[1] + (GIS.app ? 32 : 0);

			window.setPosition(centerRegionCenterX - (window.getWidth() / 2), centerRegionY);
		};

		onHoverUnselect = function fn(feature) {
			window.destroy();
		};

		onClickSelect = function fn(feature) {
			var showInfo,
				showRelocate,
				drill,
				menu,
				selectHandlers,
				isPoint = feature.geometry.CLASS_NAME === gis.conf.finals.openLayers.point_classname;

			// Relocate
			showRelocate = function() {
				if (gis.olmap.relocate.window) {
					gis.olmap.relocate.window.destroy();
				}

				gis.olmap.relocate.window = Ext.create('Ext.window.Window', {
					title: 'Relocate facility',
					layout: 'fit',
					iconCls: 'gis-window-title-icon-relocate',
					cls: 'gis-container-default',
					setMinWidth: function(minWidth) {
						this.setWidth(this.getWidth() < minWidth ? minWidth : this.getWidth());
					},
					items: {
						html: feature.attributes.name,
						cls: 'gis-container-inner'
					},
					bbar: [
						'->',
						{
							xtype: 'button',
							hideLabel: true,
							text: GIS.i18n.cancel,
							handler: function() {
								gis.olmap.relocate.active = false;
								gis.olmap.relocate.window.destroy();
								gis.olmap.getViewport().style.cursor = 'auto';
							}
						}
					],
					listeners: {
						close: function() {
							gis.olmap.relocate.active = false;
							gis.olmap.getViewport().style.cursor = 'auto';
						}
					}
				});

				gis.olmap.relocate.window.show();
				gis.olmap.relocate.window.setMinWidth(220);

				gis.util.gui.window.setPositionTopRight(gis.olmap.relocate.window);
			};

			// Infrastructural data
			showInfo = function() {
				Ext.Ajax.request({
					url: gis.init.contextPath + gis.conf.finals.url.path_module + 'getFacilityInfo.action',
					params: {
						id: feature.attributes.id
					},
					success: function(r) {
						var ou = Ext.decode(r.responseText);

						if (layer.infrastructuralWindow) {
							layer.infrastructuralWindow.destroy();
						}

						layer.infrastructuralWindow = Ext.create('Ext.window.Window', {
							title: GIS.i18n.information,
							layout: 'column',
							iconCls: 'gis-window-title-icon-information',
							cls: 'gis-container-default',
							width: 460,
							height: 400, //todo
							period: null,
							items: [
								{
									cls: 'gis-container-inner',
									columnWidth: 0.4,
									bodyStyle: 'padding-right:4px',
									items: function() {
										var a = [];

										if (feature.attributes.name) {
											a.push({html: GIS.i18n.name, cls: 'gis-panel-html-title'}, {html: feature.attributes.name, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.pa) {
											a.push({html: GIS.i18n.parent_unit, cls: 'gis-panel-html-title'}, {html: ou.pa, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.ty) {
											a.push({html: GIS.i18n.type, cls: 'gis-panel-html-title'}, {html: ou.ty, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.co) {
											a.push({html: GIS.i18n.code, cls: 'gis-panel-html-title'}, {html: ou.co, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.ad) {
											a.push({html: GIS.i18n.address, cls: 'gis-panel-html-title'}, {html: ou.ad, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.em) {
											a.push({html: GIS.i18n.email, cls: 'gis-panel-html-title'}, {html: ou.em, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										if (ou.pn) {
											a.push({html: GIS.i18n.phone_number, cls: 'gis-panel-html-title'}, {html: ou.pn, cls: 'gis-panel-html'}, {cls: 'gis-panel-html-separator'});
										}

										return a;
									}()
								},
								{
									xtype: 'form',
									cls: 'gis-container-inner gis-form-widget',
									columnWidth: 0.6,
									bodyStyle: 'padding-left:4px',
									items: [
										{
											html: GIS.i18n.infrastructural_data,
											cls: 'gis-panel-html-title'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											xtype: 'combo',
											fieldLabel: GIS.i18n.period,
											editable: false,
											valueField: 'id',
											displayField: 'name',
											forceSelection: true,
											width: 255, //todo
											labelWidth: 70,
											store: gis.store.infrastructuralPeriodsByType,
											lockPosition: false,
											listeners: {
												select: function() {
													infrastructuralPeriod = this.getValue();

													layer.widget.infrastructuralDataElementValuesStore.load({
														params: {
															periodId: infrastructuralPeriod,
															organisationUnitId: feature.attributes.internalId
														}
													});
												}
											}
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											xtype: 'grid',
											cls: 'gis-grid',
											height: 300, //todo
											width: 255,
											scroll: 'vertical',
											columns: [
												{
													id: 'dataElementName',
													text: 'Data element',
													dataIndex: 'dataElementName',
													sortable: true,
													width: 195
												},
												{
													id: 'value',
													header: 'Value',
													dataIndex: 'value',
													sortable: true,
													width: 60
												}
											],
											disableSelection: true,
											store: layer.widget.infrastructuralDataElementValuesStore
										}
									]
								}
							],
							listeners: {
								show: function() {
									if (infrastructuralPeriod) {
										this.down('combo').setValue(infrastructuralPeriod);
										infrastructuralDataElementValuesStore.load({
											params: {
												periodId: infrastructuralPeriod,
												organisationUnitId: feature.attributes.internalId
											}
										});
									}
								}
							}
						});

						layer.infrastructuralWindow.show();
						gis.util.gui.window.setPositionTopRight(layer.infrastructuralWindow);
					}
				});
			};

			// Drill or float
			drill = function(parent, level) {
				var view = Ext.clone(layer.core.view),
					items,
					loader;
					
				items = [
					{id: parent},
					{id: 'LEVEL-' + level}
				];
				
				view.rows = [{
					dimension: dimConf.organisationUnit.objectName,
					items: items
				}];

				if (view) {
					loader = layer.core.getLoader();
					loader.updateGui = true;
					loader.zoomToVisibleExtent = true;
					loader.hideMask = true;
					loader.load(view);
				}
			};

			// Menu
			var menuItems = [
				Ext.create('Ext.menu.Item', {
					text: 'Float up',
					iconCls: 'gis-menu-item-icon-float',
					disabled: !feature.attributes.hasCoordinatesUp,
					handler: function() {
						drill(feature.attributes.grandParentId, parseInt(feature.attributes.level) - 1);
					}
				}),
				Ext.create('Ext.menu.Item', {
					text: 'Drill down',
					iconCls: 'gis-menu-item-icon-drill',
					cls: 'gis-menu-item-first',
					disabled: !feature.attributes.hasCoordinatesDown,
					handler: function() {
						drill(feature.attributes.id, parseInt(feature.attributes.level) + 1);
					}
				})
			];

			if (isRelocate && isPoint) {
				menuItems.push({
					xtype: 'menuseparator'
				});

				menuItems.push( Ext.create('Ext.menu.Item', {
					text: GIS.i18n.relocate,
					iconCls: 'gis-menu-item-icon-relocate',
					disabled: !gis.init.user.isAdmin,
					handler: function(item) {
						gis.olmap.relocate.active = true;
						gis.olmap.relocate.feature = feature;
						gis.olmap.getViewport().style.cursor = 'crosshair';
						showRelocate();
					}
				}));

				menuItems.push( Ext.create('Ext.menu.Item', {
					text: GIS.i18n.show_information_sheet,
					iconCls: 'gis-menu-item-icon-information',
					handler: function(item) {
						if (gis.store.infrastructuralPeriodsByType.isLoaded) {
							showInfo();
						}
						else {
							gis.store.infrastructuralPeriodsByType.load({
								params: {
									name: gis.init.systemSettings.infrastructuralPeriodType
								},
								callback: function() {
									showInfo();
								}
							});
						}
					}
				}));
			}

			menuItems[menuItems.length - 1].addCls('gis-menu-item-last');

			menu = new Ext.menu.Menu({
				shadow: false,
				showSeparator: false,
				defaults: {
					bodyStyle: 'padding-right:6px'
				},
				items: menuItems,
				listeners: {
					afterrender: function() {
						this.getEl().addCls('gis-toolbar-btn-menu');
					}
				}
			});

			menu.showAt([gis.olmap.mouseMove.x, gis.olmap.mouseMove.y]);
		};

		selectHandlers = new OpenLayers.Control.newSelectFeature(layer, {
			onHoverSelect: onHoverSelect,
			onHoverUnselect: onHoverUnselect,
			onClickSelect: onClickSelect
		});

		gis.olmap.addControl(selectHandlers);
		selectHandlers.activate();
	};

	GIS.core.OrganisationUnitLevelStore = function(gis) {
		return Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'level'],
			proxy: {
				type: 'jsonp',
				url: gis.init.contextPath + gis.conf.finals.url.path_api + 'organisationUnitLevels.jsonp?viewClass=detailed&links=false&paging=false',
				reader: {
					type: 'json',
					root: 'organisationUnitLevels'
				}
			},
			autoLoad: true,
			cmp: [],
			isLoaded: false,
			loadFn: function(fn) {
				if (this.isLoaded) {
					fn.call();
				}
				else {
					this.load(fn);
				}
			},
			getRecordByLevel: function(level) {
				return this.getAt(this.findExact('level', level));
			},
			listeners: {
				load: function() {
					if (!this.isLoaded) {
						this.isLoaded = true;
						gis.util.gui.combo.setQueryMode(this.cmp, 'local');
					}
					this.sort('level', 'ASC');
				}
			}
		});
	};

	GIS.core.StyleMap = function(id, labelConfig) {
		var defaults = {
				fillOpacity: 1,
				strokeColor: '#fff',
				strokeWidth: 1
			},
			select = {
				strokeColor: '#000000',
				strokeWidth: 2,
				cursor: 'pointer'
			};

		if (id === 'boundary') {
			defaults.fillOpacity = 0;
			defaults.strokeColor = '#000';

			select.fillColor = '#000';
			select.fillOpacity = 0.2;
			select.strokeWidth = 1;
		}

		if (labelConfig) {
			defaults.label = '\${label}';
			defaults.fontFamily = 'arial,sans-serif,ubuntu,consolas';
			defaults.fontSize = labelConfig.fontSize ? labelConfig.fontSize + 'px' : '13px';
			defaults.fontWeight = labelConfig.strong ? 'bold' : 'normal';
			defaults.fontStyle = labelConfig.italic ? 'italic' : 'normal';
			defaults.fontColor = labelConfig.color ? '#' + labelConfig.color : '#000000';
		}

		return new OpenLayers.StyleMap({
			'default': defaults,
			select: select
		});
	};

	GIS.core.VectorLayer = function(gis, id, name, config) {
		var layer = new OpenLayers.Layer.Vector(name, {
			strategies: [
				new OpenLayers.Strategy.Refresh({
					force:true
				})
			],
			styleMap: GIS.core.StyleMap(id),
			visibility: false,
			displayInLayerSwitcher: false,
			layerType: gis.conf.finals.layer.type_vector,
			layerOpacity: config ? config.opacity || 1 : 1,
			setLayerOpacity: function(number) {
				if (number) {
					this.layerOpacity = parseFloat(number);
				}
				this.setOpacity(this.layerOpacity);
			},
			hasLabels: false
		});

		layer.id = id;

		return layer;
	};

	GIS.core.MeasureWindow = function(gis) {
		var window,
			label,
			handleMeasurements,
			control,
			styleMap;

		styleMap = new OpenLayers.StyleMap({
			'default': new OpenLayers.Style()
		});

		control = new OpenLayers.Control.Measure(OpenLayers.Handler.Path, {
			persist: true,
			immediate: true,
			handlerOption: {
				layerOptions: {
					styleMap: styleMap
				}
			}
		});

		handleMeasurements = function(e) {
			if (e.measure) {
				label.setText(e.measure.toFixed(2) + ' ' + e.units);
			}
		};

		gis.olmap.addControl(control);

		control.events.on({
			measurepartial: handleMeasurements,
			measure: handleMeasurements
		});

		control.geodesic = true;
		control.activate();

		label = Ext.create('Ext.form.Label', {
			style: 'height: 20px',
			text: '0 km'
		});

		window = Ext.create('Ext.window.Window', {
			title: GIS.i18n.measure_distance,
			layout: 'fit',
			cls: 'gis-container-default',
			bodyStyle: 'text-align: center',
			width: 130,
			minWidth: 130,
			resizable: false,
			items: label,
			listeners: {
				show: function() {
					var x = gis.viewport.eastRegion.x - this.getWidth() - 5,
						y = 60;
					this.setPosition(x, y);
				},
				destroy: function() {
					control.deactivate();
					gis.olmap.removeControl(control);
				}
			}
		});

		return window;
	};

	GIS.core.MapLoader = function(gis) {
		var getMap,
			setMap,
			afterLoad,
			callBack,
			register = [],
			loader;

		getMap = function() {
			Ext.data.JsonP.request({
				url: gis.init.contextPath + gis.conf.finals.url.path_api + 'maps/' + gis.map.id + '.jsonp?viewClass=dimensional&links=false',
				success: function(r) {

					// Operand
					if (Ext.isArray(r.mapViews)) {
						for (var i = 0, view; i < r.mapViews.length; i++) {
							view = r.mapViews[i];

							if (view) {
								if (Ext.isArray(view.columns) && view.columns.length) {
									for (var j = 0, dim; j < view.columns.length; j++) {
										dim = view.columns[j];
										
										if (Ext.isArray(dim.items) && dim.items.length) {
											for (var k = 0, item; k < dim.items.length; k++) {
												item = dim.items[k];
												
												item.id = item.id.replace('.', '-');
											}
										}
									}
								}
							}
						}
					}

					gis.map = r;
					setMap();
				},
				failure: function() {
					gis.olmap.mask.hide();
					alert('Map id not recognized' + (gis.el ? ' (' + gis.el + ')' : ''));
					return;
				}
			});
		};

		setMap = function() {
			var views = gis.map.mapViews,
				loader;

			if (!(Ext.isArray(views) && views.length)) {
				gis.olmap.mask.hide();
				alert(GIS.i18n.favorite_outdated_create_new);
				return;
			}
			
			for (var i = 0; i < views.length; i++) {
				views[i] = gis.api.layout.Layout(views[i]);
			}
			
			views = Ext.Array.clean(views);
			
			if (!views.length) {
				return;
			}

			if (gis.viewport && gis.viewport.favoriteWindow && gis.viewport.favoriteWindow.isVisible()) {
				gis.viewport.favoriteWindow.destroy();
			}

			gis.olmap.closeAllLayers();

			for (var i = 0, layout; i < views.length; i++) {
				layout = views[i];
				
				loader = gis.layer[layout.layer].core.getLoader();
				loader.updateGui = !gis.el;
				loader.callBack = callBack;
				loader.load(layout);
			}
		};

		callBack = function(layer) {
			register.push(layer);

			if (register.length === gis.map.mapViews.length) {
				afterLoad();
			}
		};

		afterLoad = function() {
			register = [];

			if (gis.el) {
				gis.olmap.zoomToVisibleExtent();
			}
			else {
				if (gis.map.longitude && gis.map.latitude && gis.map.zoom) {
					gis.olmap.setCenter(new OpenLayers.LonLat(gis.map.longitude, gis.map.latitude), gis.map.zoom);
				}
				else {
					gis.olmap.zoomToVisibleExtent();
				}
			}

			if (gis.viewport.interpretationButton) {
				gis.viewport.interpretationButton.enable();
			}

			gis.olmap.mask.hide();
		};

		loader = {
			load: function(views) {
				gis.olmap.mask.show();
				
				if (gis.map && gis.map.id) {
					getMap();
				}
				else {
					if (views) {
						gis.map = {
							mapViews: views
						};
					}
						
					setMap();
				}
			}
		};

		return loader;
	};

	GIS.core.LayerLoaderBoundary = function(gis, layer) {
		var olmap = layer.map,
			compareView,
			loadOrganisationUnits,
			loadData,
			loadLegend,
			afterLoad,
			loader;

		compareView = function(view, doExecute) {
			var src = layer.core.view,
				viewIds,
				viewDim,
				srcIds,
				srcDim;

			if (!src) {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}
			
			viewIds = [];
			viewDim = view.rows[0];
			srcIds = [];
			srcDim = src.rows[0];
			
			// organisation units
			if (viewDim.items.length === srcDim.items.length) {					
				for (var i = 0; i < viewDim.items.length; i++) {
					viewIds.push(viewDim.items[i].id);
				}
				
				for (var i = 0; i < srcDim.items.length; i++) {
					srcIds.push(srcDim.items[i].id);
				}
				
				if (Ext.Array.difference(viewIds, srcIds).length !== 0) {
					if (doExecute) {
						loadOrganisationUnits(view);
					}
					return gis.conf.finals.widget.loadtype_organisationunit;
				}
			}
			else {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			gis.olmap.mask.hide();
		};

		loadOrganisationUnits = function(view) {
			var items = view.rows[0].items,
				idParamString = '';
			
			for (var i = 0; i < items.length; i++) {
				idParamString += 'ids=' + items[i].id;
				idParamString += i !== items.length - 1 ? '&' : '';
			}
			
			Ext.data.JsonP.request({
				url: gis.init.contextPath + gis.conf.finals.url.path_module + 'getGeoJson.action?' + idParamString,
				scope: this,
				disableCaching: false,
				success: function(r) {
					var geojson = gis.util.geojson.decode(r),
						format = new OpenLayers.Format.GeoJSON(),						
						features = gis.util.map.getTransformedFeatureArray(format.read(geojson));

					if (!Ext.isArray(features)) {
						olmap.mask.hide();
						alert(GIS.i18n.invalid_coordinates);
						return;
					}

					if (!features.length) {
						olmap.mask.hide();
						alert(GIS.i18n.no_valid_coordinates_found);
						return;
					}

					loadData(view, features);
				},
				failure: function(r) {
					olmap.mask.hide();
					alert(GIS.i18n.coordinates_could_not_be_loaded);
				}
			});
		};

		loadData = function(view, features) {
			view = view || layer.core.view;
			features = features || layer.features.slice(0);

			for (var i = 0; i < features.length; i++) {
				features[i].attributes.label = features[i].attributes.name;
				features[i].attributes.value = 0;
			}

			layer.removeFeatures(layer.features);
			layer.addFeatures(features);

			layer.core.featureStore.loadFeatures(layer.features.slice(0));

			loadLegend(view);
		};

		loadLegend = function(view) {
			view = view || layer.core.view;

			var options = {
				indicator: gis.conf.finals.widget.value,
				method: 2,
				numClasses: 5,
				colors: layer.core.getColors('000000', '000000'),
				minSize: 6,
				maxSize: 6
			};

			layer.core.view = view;

			layer.core.applyClassification(options);

			afterLoad(view);
		};

		afterLoad = function(view) {

			// Layer
			if (layer.item) {
				layer.item.setValue(true, view.opacity);
			}
			else {
				layer.setLayerOpacity(view.opacity);
			}

			// Gui
			if (loader.updateGui && Ext.isObject(layer.widget)) {
				layer.widget.setGui(view);
			}

			// Zoom
			if (loader.zoomToVisibleExtent) {
				olmap.zoomToVisibleExtent();
			}

			// Mask
			if (loader.hideMask) {
				olmap.mask.hide();
			}

			// Map callback
			if (loader.callBack) {
				loader.callBack(layer);
			}
			else {
				gis.map = null;
				gis.viewport.interpretationButton.disable();
			}
		};

		loader = {
			compare: false,
			updateGui: false,
			zoomToVisibleExtent: false,
			hideMask: false,
			callBack: null,
			load: function(view) {
				gis.olmap.mask.show();

				if (this.compare) {
					compareView(view, true);
				}
				else {
					loadOrganisationUnits(view);
				}
			},
			loadData: loadData,
			loadLegend: loadLegend
		};

		return loader;
	};

	GIS.core.LayerLoaderThematic = function(gis, layer) {
		var olmap = layer.map,
			compareView,
			loadOrganisationUnits,
			loadData,
			loadLegend,
			afterLoad,
			loader,
			dimConf = gis.conf.finals.dimension;

		compareView = function(view, doExecute) {
			var src = layer.core.view,
				viewIds,
				viewDim,
				srcIds,
				srcDim;

			if (!src) {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}
			
			// organisation units
			viewIds = [];
			viewDim = view.rows[0];
			srcIds = [];
			srcDim = src.rows[0];
				
			if (viewDim.items.length === srcDim.items.length) {					
				for (var i = 0; i < viewDim.items.length; i++) {
					viewIds.push(viewDim.items[i].id);
				}
				
				for (var i = 0; i < srcDim.items.length; i++) {
					srcIds.push(srcDim.items[i].id);
				}
				
				if (Ext.Array.difference(viewIds, srcIds).length !== 0) {
					if (doExecute) {
						loadOrganisationUnits(view);
					}
					return gis.conf.finals.widget.loadtype_organisationunit;
				}
			}
			else {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}
			
			// data
			viewIds = [];
			viewDim = view.columns[0];
			srcIds = [];
			srcDim = src.columns[0];
					
			if (viewDim.items.length === srcDim.items.length) {
				for (var i = 0; i < viewDim.items.length; i++) {
					viewIds.push(viewDim.items[i].id);
				}
				
				for (var i = 0; i < srcDim.items.length; i++) {
					srcIds.push(srcDim.items[i].id);
				}
				
				if (Ext.Array.difference(viewIds, srcIds).length !== 0) {
					if (doExecute) {
						loadData(view);
					}
					return gis.conf.finals.widget.loadtype_organisationunit;
				}
			}
			else {
				if (doExecute) {
					loadData(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}
			
			// period
			viewIds = [];
			viewDim = view.filters[0];
			srcIds = [];
			srcDim = src.filters[0];
					
			if (viewDim.items.length === srcDim.items.length) {					
				for (var i = 0; i < viewDim.items.length; i++) {
					viewIds.push(viewDim.items[i].id);
				}
				
				for (var i = 0; i < srcDim.items.length; i++) {
					srcIds.push(srcDim.items[i].id);
				}
				
				if (Ext.Array.difference(viewIds, srcIds).length !== 0) {
					if (doExecute) {
						loadData(view);
					}
					return gis.conf.finals.widget.loadtype_organisationunit;
				}
			}
			else {
				if (doExecute) {
					loadData(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}
			
			// legend			
			if (typeof view.legendSet !== typeof src.legendSet) {
				if (doExecute) {
					loadLegend(view);
				}
				return gis.conf.finals.widget.loadtype_legend;
			}
			else if (view.classes !== src.classes ||
				view.method !== src.method ||
				view.colorLow !== src.colorLow ||
				view.radiusLow !== src.radiusLow ||
				view.colorHigh !== src.colorHigh ||
				view.radiusHigh !== src.radiusHigh) {
					if (doExecute) {
						loadLegend(view);
					}
					return gis.conf.finals.widget.loadtype_legend;
			}

			gis.olmap.mask.hide();
		};

		loadOrganisationUnits = function(view) {
			var items = view.rows[0].items,
				idParamString = '';
			
			for (var i = 0; i < items.length; i++) {
				idParamString += 'ids=' + items[i].id;
				idParamString += i !== items.length - 1 ? '&' : '';
			}
			
			Ext.data.JsonP.request({
				url: gis.init.contextPath + gis.conf.finals.url.path_module + 'getGeoJson.action?' + idParamString,
				scope: this,
				disableCaching: false,
				success: function(r) {
					var geojson = gis.util.geojson.decode(r),
						format = new OpenLayers.Format.GeoJSON(),						
						features = gis.util.map.getTransformedFeatureArray(format.read(geojson));

					if (!Ext.isArray(features)) {
						olmap.mask.hide();
						alert(GIS.i18n.invalid_coordinates);
						return;
					}

					if (!features.length) {
						olmap.mask.hide();
						alert(GIS.i18n.no_valid_coordinates_found);
						return;
					}

					loadData(view, features);
				},
				failure: function(r) {
					olmap.mask.hide();
					alert(GIS.i18n.coordinates_could_not_be_loaded);
				}
			});
		};

		loadData = function(view, features) {
			view = view || layer.core.view;
			features = features || layer.features.slice(0);

			var dimConf = gis.conf.finals.dimension,
				paramString = '?',
				dxItems = view.columns[0].items,
				isOperand = view.columns[0].dimension === dimConf.operand.objectName,
				peItems = view.filters[0].items,
				ouItems = view.rows[0].items;

			// ou
			paramString += 'dimension=ou:';
			
			for (var i = 0; i < ouItems.length; i++) {
				paramString += ouItems[i].id;
				paramString += i < ouItems.length - 1 ? ';' : '';
			}

			// dx
			paramString += '&dimension=dx:';
			
			for (var i = 0; i < dxItems.length; i++) {
				paramString += isOperand ? dxItems[i].id.split('-')[0] : dxItems[i].id;
				paramString += i < dxItems.length - 1 ? ';' : '';
			}
			
			paramString += isOperand ? '&dimension=co' : '';
			
			// pe
			paramString += '&filter=pe:';
			
			for (var i = 0; i < peItems.length; i++) {
				paramString += peItems[i].id;
				paramString += i < peItems.length - 1 ? ';' : '';
			}
			
			Ext.data.JsonP.request({
				url: gis.init.contextPath + '/api/analytics.jsonp' + paramString,
				disableCaching: false,
				scope: this,
				success: function(r) {
					var response = gis.api.response.Response(r),
						featureMap = {},
						valueMap = {},
						ouIndex,
						dxIndex,
						valueIndex,
						newFeatures = [],
						dimensions,
						items = [];
						
					if (!response) {
						alert(GIS.i18n.current_selection_no_data);
						olmap.mask.hide();
						return;
					}

					// ou index, value index
					for (var i = 0; i < response.headers.length; i++) {
						if (response.headers[i].name === dimConf.organisationUnit.dimensionName) {
							ouIndex = i;
						}
						else if (response.headers[i].name === dimConf.value.dimensionName) {
							valueIndex = i;
						}
					}

					// Feature map
					for (var i = 0, id; i < features.length; i++) {
						var id = features[i].attributes.id;
						
						featureMap[id] = true;
					}

					// Value map
					for (var i = 0; i < response.rows.length; i++) {
						var id = response.rows[i][ouIndex],
							value = parseFloat(response.rows[i][valueIndex]);
							
						valueMap[id] = value;
					}

					for (var i = 0; i < features.length; i++) {
						var feature = features[i],
							id = feature.attributes.id;
							
						if (featureMap.hasOwnProperty(id) && valueMap.hasOwnProperty(id)) {
							feature.attributes.value = valueMap[id];
							feature.attributes.label = feature.attributes.name + ' (' + feature.attributes.value + ')';
							newFeatures.push(feature);
						}
					}

					layer.removeFeatures(layer.features);
					layer.addFeatures(newFeatures);

					layer.core.featureStore.loadFeatures(layer.features.slice(0));
					
					gis.response = response;

					loadLegend(view);
				}
			});
		};

		loadLegend = function(view) {
			var bounds,
				addNames,
				fn;
			
			view = view || layer.core.view;
			
			addNames = function(response) {
					
				// All dimensions
				var dimensions = [].concat(view.columns || [], view.rows || [], view.filters || []),
					metaData = response.metaData;
					
				for (var i = 0, dimension; i < dimensions.length; i++) {
					dimension = dimensions[i];
					
					for (var j = 0, item; j < dimension.items.length; j++) {
						item = dimension.items[j];
						
						if (item.id.indexOf('-') !== -1) {
							var ids = item.id.split('-');							
							item.name = metaData.names[ids[0]] + ' ' + metaData.names[ids[1]];
						}
						else {
							item.name = metaData.names[item.id];
						}
					}
				}
								
				// Period name without changing the id
				view.filters[0].items[0].name = metaData.names[gis.response.metaData['pe'][0]];
			};
			
			fn = function() {
				
				addNames(gis.response);
				
				// Classification options
				var options = {
					indicator: gis.conf.finals.widget.value,
					method: view.legendSet ? mapfish.GeoStat.Distribution.CLASSIFY_WITH_BOUNDS : view.method,
					numClasses: view.classes,
					bounds: bounds,
					colors: layer.core.getColors(view.colorLow, view.colorHigh),
					minSize: view.radiusLow,
					maxSize: view.radiusHigh
				};
				
				layer.core.view = view;
				layer.core.colorInterpolation = colors;
				layer.core.applyClassification(options);

				afterLoad(view);
			};
			
			if (view.legendSet) {
				var bounds = [],
					colors = [],
					names = [],
					legends = [];

				Ext.Ajax.request({
					url: gis.init.contextPath + gis.conf.finals.url.path_api + 'mapLegendSets/' + view.legendSet.id + '.json?links=false&paging=false',
					scope: this,
					success: function(r) {
						legends = Ext.decode(r.responseText).mapLegends;

						Ext.Array.sort(legends, function (a, b) {
							return a.startValue - b.startValue;
						});

						for (var i = 0; i < legends.length; i++) {
							if (bounds[bounds.length - 1] !== legends[i].startValue) {
								if (bounds.length !== 0) {
									colors.push(new mapfish.ColorRgb(240,240,240));
									names.push('');
								}
								bounds.push(legends[i].startValue);
							}
							colors.push(new mapfish.ColorRgb());
							colors[colors.length - 1].setFromHex(legends[i].color);
							names.push(legends[i].name);
							bounds.push(legends[i].endValue);
						}
						
						view.legendSet.names = names;
						view.legendSet.bounds = bounds;
						view.legendSet.colors = colors;

						fn();
					}
				});
			}
			else {
				fn();
			}
		};

		afterLoad = function(view) {

			// Legend
			gis.viewport.eastRegion.doLayout();
			layer.legendPanel.expand();

			// Layer
			layer.setLayerOpacity(view.opacity);

			if (layer.item) {
				layer.item.setValue(true);
			}

			// Filter
			if (layer.filterWindow && layer.filterWindow.isVisible()) {
				layer.filterWindow.filter();
			}

			// Gui
			if (loader.updateGui && Ext.isObject(layer.widget)) {
				layer.widget.setGui(view);
			}

			// Zoom
			if (loader.zoomToVisibleExtent) {
				olmap.zoomToVisibleExtent();
			}

			// Mask
			if (loader.hideMask) {
				olmap.mask.hide();
			}

			// Map callback
			if (loader.callBack) {
				loader.callBack(layer);
			}
			else {
				gis.map = null;
				if (gis.viewport.interpretationButton) {
					gis.viewport.interpretationButton.disable();
				}
			}
		};

		loader = {
			compare: false,
			updateGui: false,
			zoomToVisibleExtent: false,
			hideMask: false,
			callBack: null,
			load: function(view) {
				gis.olmap.mask.show();

				if (this.compare) {
					compareView(view, true);
				}
				else {
					loadOrganisationUnits(view);
				}
			},
			loadData: loadData,
			loadLegend: loadLegend
		};

		return loader;
	};

	GIS.core.LayerLoaderFacility = function(gis, layer) {
		var olmap = layer.map,
			compareView,
			loadOrganisationUnits,
			loadData,
			loadLegend,
			addCircles,
			afterLoad,
			loader;

		compareView = function(view, doExecute) {
			var src = layer.core.view;

			if (!src) {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			if (view.organisationUnitGroupSet.id !== src.organisationUnitGroupSet.id) {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			if (view.organisationUnitLevel.id !== src.organisationUnitLevel.id) {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			if (view.parentOrganisationUnit.id !== src.parentOrganisationUnit.id) {
				if (doExecute) {
					loadOrganisationUnits(view);
				}
				return gis.conf.finals.widget.loadtype_organisationunit;
			}

			if (view.areaRadius !== src.areaRadius) {
				if (doExecute) {
					loadLegend(view);
				}
				return gis.conf.finals.widget.loadtype_legend;
			}

			gis.olmap.mask.hide();
		};

		loadOrganisationUnits = function(view) {
			var items = view.rows[0].items,
				idParamString = '';
			
			for (var i = 0; i < items.length; i++) {
				idParamString += 'ids=' + items[i].id;
				idParamString += i !== items.length - 1 ? '&' : '';
			}
			
			Ext.data.JsonP.request({
				url: gis.init.contextPath + gis.conf.finals.url.path_module + 'getGeoJsonFacilities.action?' + idParamString,
				scope: this,
				disableCaching: false,
				success: function(r) {
					var geojson = layer.core.decode(r),
						format = new OpenLayers.Format.GeoJSON(),
						features = gis.util.map.getTransformedFeatureArray(format.read(geojson));

					if (!Ext.isArray(features)) {
						olmap.mask.hide();
						alert(GIS.i18n.invalid_coordinates);
						return;
					}

					if (!features.length) {
						olmap.mask.hide();
						alert(GIS.i18n.no_valid_coordinates_found);
						return;
					}

					loadData(view, features);
				},
				failure: function(r) {
					olmap.mask.hide();
					alert(GIS.i18n.coordinates_could_not_be_loaded);
				}
			});
		};

		loadData = function(view, features) {
			view = view || layer.core.view;
			features = features || layer.features.slice(0);

			for (var i = 0; i < features.length; i++) {
				features[i].attributes.label = features[i].attributes.name;
			}

			layer.removeFeatures(layer.features);
			layer.addFeatures(features);

			layer.core.featureStore.loadFeatures(layer.features.slice(0));

			loadLegend(view);
		};

		loadLegend = function(view) {
			view = view || layer.core.view;

			var store = gis.store.groupsByGroupSet;

			store.proxy.url = gis.init.contextPath + gis.conf.finals.url.path_module + 'getOrganisationUnitGroupsByGroupSet.action?id=' + view.organisationUnitGroupSet.id;
			store.load({
				scope: this,
				callback: function() {
					var options = {
						indicator: view.organisationUnitGroupSet.id
					};

					layer.core.view = view;

					layer.core.applyClassification(options);

					addCircles(view);

					afterLoad(view);
				}
			});
		};

		addCircles = function(view) {
			var radius = view.areaRadius;

			if (layer.circleLayer) {
				layer.circleLayer.deactivateControls();
				layer.circleLayer = null;
			}
			if (Ext.isDefined(radius) && radius) {
				layer.circleLayer = GIS.app.CircleLayer(layer.features, radius);
				nissa = layer.circleLayer;
			}
		};

		afterLoad = function(view) {

			// Legend
			gis.viewport.eastRegion.doLayout();
			layer.legendPanel.expand();

			// Layer
			if (layer.item) {
				layer.item.setValue(true, view.opacity);
			}
			else {
				layer.setLayerOpacity(view.opacity);
			}

			// Gui
			if (loader.updateGui && Ext.isObject(layer.widget)) {
				layer.widget.setGui(view);
			}

			// Zoom
			if (loader.zoomToVisibleExtent) {
				olmap.zoomToVisibleExtent();
			}

			// Mask
			if (loader.hideMask) {
				olmap.mask.hide();
			}

			// Map callback
			if (loader.callBack) {
				loader.callBack(layer);
			}
			else {
				gis.map = null;
				gis.viewport.interpretationButton.disable();
			}
		};

		loader = {
			compare: false,
			updateGui: false,
			zoomToVisibleExtent: false,
			hideMask: false,
			callBack: null,
			load: function(view) {
				gis.olmap.mask.show();

				if (this.compare) {
					compareView(view, true);
				}
				else {
					loadOrganisationUnits(view);
				}
			},
			loadData: loadData,
			loadLegend: loadLegend
		};

		return loader;
	};

	GIS.core.getInstance = function(init) {
		var conf = {},
			util = {},
			api = {},
			store = {},
			layers = [],
			gis = {};

		// conf
		(function() {
			conf.finals = {
				url: {
					path_api: '/api/',
					path_module: '/dhis-web-mapping/',
					path_commons: '/dhis-web-commons-ajax-json/',
					organisationunitchildren_get: 'getOrganisationUnitChildren.action',
					organisationunitgroup_getall: 'organisationUnitGroups.json?paging=false&links=false',
					dataset_get: 'dataSets.json?paging=false&links=false'
				},
				layer: {
					type_base: 'base',
					type_vector: 'vector',
					category_thematic: 'thematic'
				},
				dimension: {
					data: {
						id: 'data',
						value: 'data',
						param: 'dx',
						dimensionName: 'dx',
						objectName: 'dx'
					},
					category: {
						name: GIS.i18n.categories,
						dimensionName: 'co',
						objectName: 'co',
					},
					indicator: {
						id: 'indicator',
						value: 'indicators',
						param: 'in',
						dimensionName: 'dx',
						objectName: 'in'
					},
					dataElement: {
						id: 'dataElement',
						value: 'dataElement',
						param: 'de',
						dimensionName: 'dx',
						objectName: 'de'
					},
					operand: {
						id: 'operand',
						value: 'operand',
						param: 'dc',
						dimensionName: 'dx',
						objectName: 'dc'
					},
					dataSet: {
						value: 'dataSets',
						dimensionName: 'dx',
						objectName: 'ds'
					},
					period: {
						id: 'period',
						value: 'period',
						param: 'pe',
						dimensionName: 'pe',
						objectName: 'pe'
					},
					organisationUnit: {
						id: 'organisationUnit',
						value: 'organisationUnit',
						param: 'ou',
						dimensionName: 'ou',
						objectName: 'ou'
					},
					value: {
						id: 'value',
						value: 'value',
						param: 'value',
						dimensionName: 'value',
						objectName: 'value'
					}
				},
				widget: {
					value: 'value',
					legendtype_automatic: 'automatic',
					legendtype_predefined: 'predefined',
					symbolizer_color: 'color',
					symbolizer_image: 'image',
					loadtype_organisationunit: 'organisationUnit',
					loadtype_data: 'data',
					loadtype_legend: 'legend'
				},
				openLayers: {
					point_classname: 'OpenLayers.Geometry.Point'
				},
				mapfish: {
					classify_with_bounds: 1,
					classify_by_equal_intervals: 2,
					classify_by_quantils: 3
				},
				root: {
					id: 'root'
				}
			};

			conf.layout = {
				widget: {
					item_width: 288,
					itemlabel_width: 95,
					window_width: 310
				},
				tool: {
					item_width: 228,
					itemlabel_width: 95,
					window_width: 250
				},
				grid: {
					row_height: 27
				}
			};

			conf.period = {
				periodTypes: [
					{id: 'relativePeriods', name: 'Relative'},
					{id: 'Daily', name: 'Daily'},
					{id: 'Weekly', name: 'Weekly'},
					{id: 'Monthly', name: 'Monthly'},
					{id: 'BiMonthly', name: 'BiMonthly'},
					{id: 'Quarterly', name: 'Quarterly'},
					{id: 'SixMonthly', name: 'SixMonthly'},
					{id: 'Yearly', name: 'Yearly'},
					{id: 'FinancialOct', name: 'FinancialOct'},
					{id: 'FinancialJuly', name: 'FinancialJuly'},
					{id: 'FinancialApril', name: 'FinancialApril'}
				],
				relativePeriods: [
					{id: 'LAST_WEEK', name: GIS.i18n.last_week},
					{id: 'LAST_MONTH', name: GIS.i18n.last_month},
					{id: 'LAST_BIMONTH', name: GIS.i18n.last_bimonth},
					{id: 'LAST_QUARTER', name: GIS.i18n.last_quarter},
					{id: 'LAST_SIX_MONTH', name: GIS.i18n.last_sixmonth},
					{id: 'LAST_FINANCIAL_YEAR', name: GIS.i18n.last_financial_year},
					{id: 'THIS_YEAR', name: GIS.i18n.this_year},
					{id: 'LAST_YEAR', name: GIS.i18n.last_year}
				]
			};
		}());

		// util
		(function() {
			util.map = {};

			util.map.getVisibleVectorLayers = function() {
				var layers = [];

				for (var i = 0, layer; i < gis.olmap.layers.length; i++) {
					layer = gis.olmap.layers[i];
					if (layer.layerType === conf.finals.layer.type_vector && layer.visibility && layer.features.length) {
						layers.push(layer);
					}
				}
				return layers;
			};

			util.map.getExtendedBounds = function(layers) {
				var bounds = null;
				if (layers.length) {
					bounds = layers[0].getDataExtent();
					if (layers.length > 1) {
						for (var i = 1; i < layers.length; i++) {
							bounds.extend(layers[i].getDataExtent());
						}
					}
				}
				return bounds;
			};

			util.map.zoomToVisibleExtent = function(olmap) {
				var bounds = util.map.getExtendedBounds(util.map.getVisibleVectorLayers(olmap));
				if (bounds) {
					olmap.zoomToExtent(bounds);
				}
			};

			util.map.getTransformedFeatureArray = function(features) {
				var sourceProjection = new OpenLayers.Projection("EPSG:4326"),
					destinationProjection = new OpenLayers.Projection("EPSG:900913");
				for (var i = 0; i < features.length; i++) {
					features[i].geometry.transform(sourceProjection, destinationProjection);
				}
				return features;
			};

			util.geojson = {};

			util.geojson.decode = function(doc) {
				var geojson = {};
				geojson.type = 'FeatureCollection';
				geojson.crs = {
					type: 'EPSG',
					properties: {
						code: '4326'
					}
				};
				geojson.features = [];

				for (var i = 0; i < doc.geojson.length; i++) {
					geojson.features.push({
						geometry: {
							type: parseInt(doc.geojson[i].ty) === 1 ? 'MultiPolygon' : 'Point',
							coordinates: doc.geojson[i].co
						},
						properties: {
							id: doc.geojson[i].uid,
							internalId: doc.geojson[i].iid,
							name: doc.geojson[i].na,
							hasCoordinatesDown: doc.geojson[i].hcd,
							hasCoordinatesUp: doc.geojson[i].hcu,
							level: doc.geojson[i].le,
							grandParentParentGraph: doc.geojson[i].gppg,
							grandParentId: doc.geojson[i].gpuid,
							path: doc.geojson[i].path,
							parentId: doc.geojson[i].pi,
							parentName: doc.geojson[i].pn
						}
					});
				}

				return geojson;
			};

			util.gui = {};
			util.gui.combo = {};

			util.gui.combo.setQueryMode = function(cmpArray, mode) {
				for (var i = 0; i < cmpArray.length; i++) {
					cmpArray[i].queryMode = mode;
				}
			};
			
			util.object = {};
			
			util.object.sortObjectsByString = function(array, key) {
				key = key || 'name';
				array.sort( function(a, b) {
					var nameA = a[key].toLowerCase(),
						nameB = b[key].toLowerCase();

					if (nameA < nameB) {
						return -1;
					}
					if (nameA > nameB) {
						return 1;
					}
					return 0;
				});
				return array;
			};
			
			util.object.getLength = function(object) {
				var size = 0;

				for (var key in object) {
					if (object.hasOwnProperty(key)) {
						size++;
					}
				}

				return size;
			};
		}());

		gis.init = init;
		gis.conf = conf;
		gis.util = util;

		// api
		(function() {
			var dimConf = gis.conf.finals.dimension;
			
			api.layout = {};
			api.response = {};
			
			api.layout.Record = function(config) {
				var record = {};

				// id: string

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Record config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.id)) {
						alert('Record id is not text: ' + config);
						return;
					}

					record.id = config.id.replace('.', '-');

					if (Ext.isString(config.name)) {
						record.name = config.name;
					}

					return Ext.clone(record);
				}();
			};

			api.layout.Dimension = function(config) {
				var dimension = {};

				// dimension: string

				// items: [Record]

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Dimension config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.dimension)) {
						console.log('Dimension name is not text: ' + config);
						return;
					}

					if (config.dimension !== conf.finals.dimension.category.objectName) {
						var records = [];

						if (!Ext.isArray(config.items)) {
							console.log('Dimension items is not an array: ' + config);
							return;
						}

						for (var i = 0; i < config.items.length; i++) {
							record = api.layout.Record(config.items[i]);

							if (record) {
								records.push(record);
							}
						}

						config.items = records;

						if (!config.items.length) {
							console.log('Dimension has no valid items: ' + config);
							return;
						}
					}

					dimension.dimension = config.dimension;
					dimension.items = config.items;

					return Ext.clone(dimension);
				}();
			};

			api.layout.Layout = function(config) {
				var layout = {},
					getValidatedDimensionArray,
					validateSpecialCases;
					
				// layer: string

				// columns: [Dimension]

				// rows: [Dimension]

				// filters: [Dimension]

				// classes: integer (5) - 1-7

				// method: integer (2) - 2, 3 // 2=equal intervals, 3=equal counts

				// colorLow: string ('ff0000')

				// colorHigh: string ('00ff00')

				// radiusLow: integer (5)

				// radiusHigh: integer (15)

				// opacity: integer (0.8) - 0-1
				
				// legendSet: object

				getValidatedDimensionArray = function(dimensionArray) {
					var dimensions = [];

					if (!(dimensionArray && Ext.isArray(dimensionArray) && dimensionArray.length)) {
						return;
					}

					for (var i = 0, dimension; i < dimensionArray.length; i++) {
						dimension = api.layout.Dimension(dimensionArray[i]);

						if (dimension) {
							dimensions.push(dimension);
						}
					}

					dimensionArray = dimensions;

					return dimensionArray.length ? dimensionArray : null;
				};

				validateSpecialCases = function(config) {
					var dimensions = [].concat(config.columns || [], config.rows || [], config.filters || []),
						dxDim,
						peDim,
						ouDim;
					
					for (var i = 0, dim; i < dimensions.length; i++) {
						dim = dimensions[i];
						
						if (dim.dimension === dimConf.indicator.objectName ||
							dim.dimension === dimConf.dataElement.objectName ||
							dim.dimension === dimConf.operand.objectName ||
							dim.dimension === dimConf.dataSet.objectName) {
							dxDim = dim;
						}
						else if (dim.dimension === dimConf.period.objectName) {
							peDim = dim;
						}
						else if (dim.dimension === dimConf.organisationUnit.objectName) {
							ouDim = dim;
						}
					}
					
					if (!dxDim) {
						return;
					}
					
					if (!peDim) {
						peDim = {
							dimension: 'pe',
							items: [{id: 'LAST_MONTH'}]
						};
					}
					
					if (!ouDim) {
						ouDim = {
							dimension: 'ou',
							items: [{id: 'LEVEL-2'}]
						};
					}
					
					config.columns = [dxDim];
					config.rows = [ouDim];
					config.filters = [peDim];
					
					return config;
				};						

				return function() {
					var a = [],
						objectNames = [],
						dimConf = conf.finals.dimension,
						isOu = false,
						isOuc = false,
						isOugc = false;

					config.columns = getValidatedDimensionArray(config.columns);
					config.rows = getValidatedDimensionArray(config.rows);
					config.filters = getValidatedDimensionArray(config.filters);
					
					config = validateSpecialCases(config);

					// Config must be an object
					if (!(config && Ext.isObject(config))) {
						alert(gis.el + ': Data required');
						return;
					}

					// Collect object names and user orgunits
					for (var i = 0, dim, dims = [].concat(config.columns, config.rows, config.filters); i < dims.length; i++) {
						dim = dims[i];

						if (dim) {

							// Object names
							if (Ext.isString(dim.dimension)) {
								objectNames.push(dim.dimension);
							}

							// user orgunits
							if (dim.dimension === dimConf.organisationUnit.objectName && Ext.isArray(dim.items)) {
								for (var j = 0; j < dim.items.length; j++) {
									if (dim.items[j].id === 'USER_ORGUNIT') {
										isOu = true;
									}
									else if (dim.items[j].id === 'USER_ORGUNIT_CHILDREN') {
										isOuc = true;
									}
									else if (dim.items[j].id === 'USER_ORGUNIT_GRANDCHILDREN') {
										isOugc = true;
									}
								}
							}
						}
					}

					// At least one period
					if (!Ext.Array.contains(objectNames, dimConf.period.objectName)) {
						alert(GIS.i18n.at_least_one_period_must_be_specified_as_column_row_or_filter);
						return;
					}

					// Layout
					layout.columns = config.columns;
					layout.rows = config.rows;
					layout.filters = config.filters;

					// Properties
					layout.layer = Ext.isString(config.layer) && !Ext.isEmpty(config.layer) ? config.layer : 'thematic1';
					layout.classes = Ext.isNumber(config.classes) && !Ext.isEmpty(config.classes) ? config.classes : 5;
					layout.method = Ext.isNumber(config.method) && !Ext.isEmpty(config.method) ? config.method : 2;
					layout.colorLow = Ext.isString(config.colorLow) && !Ext.isEmpty(config.colorLow) ? config.colorLow : 'ff0000';
					layout.colorHigh = Ext.isString(config.colorHigh) && !Ext.isEmpty(config.colorHigh) ? config.colorHigh : '00ff00';
					layout.radiusLow = Ext.isNumber(config.radiusLow) && !Ext.isEmpty(config.radiusLow) ? config.radiusLow : 5;
					layout.radiusHigh = Ext.isNumber(config.radiusHigh) && !Ext.isEmpty(config.radiusHigh) ? config.radiusHigh : 15;
					layout.opacity = Ext.isNumber(config.opacity) && !Ext.isEmpty(config.opacity) ? config.opacity : 0.8;

					layout.userOrganisationUnit = isOu;
					layout.userOrganisationUnitChildren = isOuc;
					layout.userOrganisationUnitGrandChildren = isOugc;

					layout.parentGraphMap = Ext.isObject(config.parentGraphMap) ? config.parentGraphMap : null;
					
					layout.legendSet = config.legendSet;

					return Ext.clone(layout);
				}();
			};

			api.response.Header = function(config) {
				var header = {};

				// name: string

				// meta: boolean

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Header is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.name)) {
						console.log('Header name is not text: ' + config);
						return;
					}

					if (!Ext.isBoolean(config.meta)) {
						console.log('Header meta is not boolean: ' + config);
						return;
					}

					header.name = config.name;
					header.meta = config.meta;

					return Ext.clone(header);
				}();
			};

			api.response.Response = function(config) {
				var response = {};

				// headers: [Header]

				return function() {
					var headers = [];

					if (!(config && Ext.isObject(config))) {
						alert('Data response invalid');
						return false;
					}

					if (!(config.headers && Ext.isArray(config.headers))) {
						alert('Data response invalid');
						return false;
					}

					for (var i = 0, header; i < config.headers.length; i++) {
						header = api.response.Header(config.headers[i]);

						if (header) {
							headers.push(header);
						}
					}

					config.headers = headers;

					if (!config.headers.length) {
						alert('No valid response headers');
						return;
					}

					if (!(Ext.isArray(config.rows) && config.rows.length > 0)) {
						alert('No values found');
						return false;
					}

					if (config.headers.length !== config.rows[0].length) {
						alert('Data invalid');
						return false;
					}

					response.headers = config.headers;
					response.metaData = config.metaData;
					response.width = config.width;
					response.height = config.height;
					response.rows = config.rows;

					return response;
				}();
			};
		}());

		// store
		(function() {
			store.organisationUnitLevels = GIS.core.OrganisationUnitLevelStore(gis);
		}());

		gis.api = api;
		gis.store = store;

		gis.olmap = GIS.core.getOLMap(gis);
		gis.layer = GIS.core.getLayers(gis);
		gis.thematicLayers = [gis.layer.thematic1, gis.layer.thematic2, gis.layer.thematic3, gis.layer.thematic4];

		if (window.google) {
			layers.push(gis.layer.googleStreets, gis.layer.googleHybrid);
		}

		layers.push(
			gis.layer.openStreetMap,
			gis.layer.thematic4,
			gis.layer.thematic3,
			gis.layer.thematic2,
			gis.layer.thematic1,
			gis.layer.boundary,
			gis.layer.facility
		);

		gis.olmap.addLayers(layers);

		return gis;
	};

});
