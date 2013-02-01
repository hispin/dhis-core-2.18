PT.app = {};
PT.app.init = {};

Ext.onReady( function() {
	Ext.Ajax.method = 'GET';
	Ext.QuickTips.init();

	document.body.oncontextmenu = function() {
		return false;
	};

    Ext.override(Ext.LoadMask, {
		onHide: function() {
			this.callParent();
		}
	});

	// Init

	var pt = PT.core.getInstance();

	PT.instance = pt;

	PT.app.getInits = function(r) {
		var init = Ext.decode(r.responseText);

		for (var i = 0; i < init.rootNodes.length; i++) {
			init.rootNodes[i].path = '/' + pt.conf.finals.root.id + '/' + init.rootNodes[i].id;
		}

		init.afterRender = function() {
			pt.cmp.dimension.panels[0].expand();
		};

		return init;
	};

	PT.app.getUtils = function() {
		var util = pt.util || {};

		util.dimension = {
			panel: {
				setHeight: function(mx) {
					var h = pt.viewport.westRegion.getHeight() - pt.conf.layout.west_fill;
					pt.cmp.dimension.panel.setHeight(h > mx ? mx : h);
				}
			}
		};

		util.pivot.getSettingsConfig = function() {
			var data = {},
				setup = pt.viewport.settingsWindow.getSetup(),
				config = {
					col: {},
					row: {},
					filter: {}
				},
				getData,
				extendSettings;

			getData = function() {
				var panels = pt.cmp.dimension.panels,
					dxItems = [];

				for (var i = 0, dimData; i < panels.length; i++) {
					dimData = panels[i].getData();

					if (dimData) {
						if (dimData.param === pt.conf.finals.dimension.data.paramname) {
							dxItems = dxItems.concat(dimData.items);
						}
						else {
							data[dimData.param] = dimData.items;
						}
					}
				}

				if (dxItems.length) {
					data[pt.conf.finals.dimension.data.paramname] = dxItems;
				}
			}();

			extendSettings = function() {
				for (var i = 0, dim; i < setup.col.length; i++) {
					dim = setup.col[i];
					config.col[dim] = data[dim];
				}

				for (var i = 0, dim; i < setup.row.length; i++) {
					dim = setup.row[i];
					config.row[dim] = data[dim];
				}

				for (var i = 0, dim; i < setup.filter.length; i++) {
					dim = setup.filter[i];
					config.filter[dim] = data[dim];
				}
			}();

			return config;
		};

		return util;
	};

	PT.app.getStores = function() {
		var store = pt.store || {};

		store.indicatorAvailable = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				reader: {
					type: 'json',
					root: 'indicators'
				}
			},
			storage: {},
			sortStore: function() {
				this.sort('name', 'ASC');
			},
			listeners: {
				load: function(s) {
					s.each( function(r) {
						r.data.name = pt.conf.util.jsonEncodeString(r.data.name);
					});
					pt.util.store.addToStorage(s);
				}
			}
		});

		store.indicatorSelected = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});

		store.dataElementAvailable = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: pt.conf.finals.ajax.path_visualizer + pt.conf.finals.ajax.dataelement_get,
				reader: {
					type: 'json',
					root: 'dataElements'
				}
			},
			storage: {},
			sortStore: function() {
				this.sort('name', 'ASC');
			},
			listeners: {
				load: function(s) {
					s.each( function(r) {
						r.data.name = pt.conf.util.jsonEncodeString(r.data.name);
					});
					pt.util.store.addToStorage(s);
				}
			}
		});

		store.dataElementSelected = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});

		store.dataSetAvailable = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.dataset_get,
				reader: {
					type: 'json',
					root: 'dataSets'
				}
			},
			storage: {},
			sortStore: function() {
				this.sort('name', 'ASC');
			},
			isloaded: false,
			listeners: {
				load: function(s) {
					this.isloaded = true;
					s.each( function(r) {
						r.data.name = pt.conf.util.jsonEncodeString(r.data.name);
					});
					pt.util.store.addToStorage(s);
				}
			}
		});

		store.dataSetSelected = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});

		store.periodType = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: pt.conf.period.periodtypes
		});

		store.fixedPeriodAvailable = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'index'],
			data: [],
			setIndex: function(periods) {
				for (var i = 0; i < periods.length; i++) {
					periods[i].index = i;
				}
			},
			sortStore: function() {
				this.sort('index', 'ASC');
			}
		});

		store.fixedPeriodSelected = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			data: []
		});

		return store;
	};

	PT.app.getCmp = function() {
		var cmp = {};

		cmp.dimension = {
			panels: [],

			indicator: {},
			dataElement: {},
			dataSet: {},
			relativePeriod: {},
			fixedPeriod: {},
			organisationUnit: {}
		};

		cmp.dimension.relativePeriod.checkbox = [];

		cmp.favorite = {};

		return cmp;
	};

	PT.app.SettingsWindow = function(pt) {
		var dimension,
			dimensionStore,
			row,
			rowStore,
			col,
			colStore,
			filter,
			filterStore,
			value,

			getData,
			getStore,
			getCmpHeight,
			getSetup,

			dimensionPanel,
			selectPanel,
			window,

			margin = 5,
			defaultWidth = 160,
			defaultHeight = 158,
			maxHeight = (pt.viewport.getHeight() - 100) / 2;

		getData = function() {
			var groupSets = [],
				data = [
					{ id: 'dx', name: 'Data' },
					{ id: 'coc', name: 'Categories' },
					{ id: 'pe', name: 'Periods' },
					{ id: 'ou', name: 'Organisation units' }
				];

			for (var i = 0; i < pt.init.ougs.length; i++) {
				var gs = pt.init.ougs[i];

				groupSets.push({
					id: gs.id,
					name: 'Group set: ' + gs.name //i18n
				});
			}

			return data.concat(groupSets);
		};

		getStore = function(data) {
			return Ext.create('Ext.data.Store', {
				fields: ['id', 'name'],
				data: data || []
			});
		};

		dimensionStore = getStore(getData());
		rowStore = getStore();
		colStore = getStore();
		filterStore = getStore();

		getCmpHeight = function() {
			var size = dimensionStore.totalCount,
				expansion = 10,
				height = defaultHeight,
				diff;

			if (size > 10) {
				diff = size - 10;
				height += (diff * expansion);
			}

			height = height > maxHeight ? maxHeight : height;

			return height;
		};

		dimension = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: (getCmpHeight() * 2) + margin,
			style: 'margin-right:' + margin + 'px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'settingsDD',
			dropGroup: 'settingsDD',
			store: dimensionStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: 'Dimensions', //i18n
					cls: 'pt-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		row = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-right:' + margin + 'px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'settingsDD',
			dropGroup: 'settingsDD',
			store: rowStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: 'Row', //i18n
					cls: 'pt-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		col = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: getCmpHeight(),
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'settingsDD',
			dropGroup: 'settingsDD',
			store: colStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: 'Column', //i18n
					cls: 'pt-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		filter = Ext.create('Ext.ux.form.MultiSelect', {
			cls: 'pt-toolbar-multiselect-leftright',
			width: defaultWidth,
			height: getCmpHeight(),
			style: 'margin-right:' + margin + 'px',
			valueField: 'id',
			displayField: 'name',
			dragGroup: 'settingsDD',
			dropGroup: 'settingsDD',
			store: filterStore,
			tbar: {
				height: 25,
				items: {
					xtype: 'label',
					text: 'Filter', //i18n
					cls: 'pt-toolbar-multiselect-leftright-label'
				}
			},
			listeners: {
				afterrender: function(ms) {
					ms.boundList.on('itemdblclick', function(view, record) {
						ms.store.remove(record);
						dimensionStore.add(record);
					});

					ms.store.on('add', function() {
						Ext.defer( function() {
							ms.boundList.getSelectionModel().deselectAll();
						}, 10);
					});
				}
			}
		});

		value = Ext.create('Ext.form.Label', {
			text: 'Value = DHIS 2 data value'
		});

		selectPanel = Ext.create('Ext.panel.Panel', {
			bodyStyle: 'border:0 none',
			items: [
				{
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						filter,
						col
					]
				},
				{
					layout: 'column',
					bodyStyle: 'border:0 none',
					items: [
						row,
						value
					]
				}
			]
		});

		getSetup = function() {
			return {
				col: colStore.data.keys,
				row: rowStore.data.keys,
				filter: filterStore.data.keys
			};
		};

		window = Ext.create('Ext.window.Window', {
			title: 'Pivot settings', //i18n
			layout: 'fit',
			bodyStyle: 'background-color:#fff; padding:8px 8px 3px',
			closeAction: 'hide',
			modal: true,
			resizable: false,
			getSetup: getSetup,
			items: {
				layout: 'column',
				bodyStyle: 'border:0 none',
				items: [
					dimension,
					selectPanel
				]
			},
			bbar: [
				'->',
				{
					text: 'Hide',
					handler: function() {
						window.hide();
					}
				}
			],
			listeners: {
				afterrender: function(w) {
					w.setPosition(w.getPosition()[0], 100);
				}
			}
		});

		return window;
	};

	PT.app.init.onInitialize = function(r) {
		var createViewport;

		createViewport = function() {
			var viewport,
				westRegion,
				centerRegion,

				indicatorAvailable,
				indicatorSelected,
				indicator,
				dataElementAvailable,
				dataElementSelected,
				dataElement,
				dataSetAvailable,
				dataSetSelected,
				dataSet,
				rewind,
				relativePeriod,
				fixedPeriodAvailable,
				fixedPeriodSelected,
				fixedPeriod,
				organisationUnit,
				getOrganisationUnitGroupSetPanels,
				accordion,

				addListeners;

			indicatorAvailable = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-left',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				store: pt.store.indicatorAvailable,
				tbar: [
					{
						xtype: 'label',
						text: 'Available', //i18n pt.i18n.available
						cls: 'pt-toolbar-multiselect-left-label'
					},
					'->',
					{
						xtype: 'button',
						icon: 'images/arrowright.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.select(indicatorAvailable, indicatorSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowrightdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.selectAll(indicatorAvailable, indicatorSelected);
						}
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.select(this, indicatorSelected);
						}, this);
					}
				}
			});

			indicatorSelected = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-right',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				store: pt.store.indicatorSelected,
				tbar: [
					{
						xtype: 'button',
						icon: 'images/arrowleftdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselectAll(indicatorAvailable, indicatorSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowleft.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselect(indicatorAvailable, indicatorSelected);
						}
					},
					'->',
					{
						xtype: 'label',
						text: 'Selected', //i18n pt.i18n.selected,
						cls: 'pt-toolbar-multiselect-right-label'
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.unselect(indicatorAvailable, this);
						}, this);
					}
				}
			});

			indicator = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-data">Indicators</div>', //i18n
				hideCollapseTool: true,
				getData: function() {
					var data = {
						param: pt.conf.finals.dimension.indicator.paramname,
						items: []
					};

					pt.store.indicatorSelected.each( function(r) {
						data.items.push(r.data.id);
					});

					return data.items.length ? data : null;
				},
				items: [
					{
						xtype: 'combobox',
						cls: 'pt-combo',
						style: 'margin-bottom:8px',
						width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding,
						valueField: 'id',
						displayField: 'name',
						fieldLabel: 'Select group', //i18n pt.i18n.select_group
						labelStyle: 'padding-left:7px',
						editable: false,
						store: {
							xtype: 'store',
							fields: ['id', 'name', 'index'],
							proxy: {
								type: 'ajax',
								url: pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.indicatorgroup_get,
								reader: {
									type: 'json',
									root: 'indicatorGroups'
								}
							},
							listeners: {
								load: function(s) {
									s.add({
										id: 0,
										name: 'All indicator groups', //i18n pt.i18n.all_indicator_groups
										index: -1
									});
									s.sort([
										{
											property: 'index',
											direction: 'ASC'
										},
										{
											property: 'name',
											direction: 'ASC'
										}
									]);
								}
							}
						},
						listeners: {
							select: function(cb) {
								var store = pt.store.indicatorAvailable;
								store.parent = cb.getValue();

								if (pt.util.store.containsParent(store)) {
									pt.util.store.loadFromStorage(store);
									pt.util.multiselect.filterAvailable(indicatorAvailable, indicatorSelected);
								}
								else {
									if (cb.getValue() === 0) {
										store.proxy.url = pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.indicator_getall;
										store.load();
									}
									else {
										store.proxy.url = pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.indicator_get + cb.getValue() + '.json';
										store.load();
									}
								}
							}
						}
					},
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							indicatorAvailable,
							indicatorSelected
						]
					}
				],
				listeners: {
					added: function() {
						pt.cmp.dimension.panels.push(this);
					},
					expand: function() {
						pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_indicator);
						pt.util.multiselect.setHeight(
							[indicatorAvailable, indicatorSelected],
							this,
							pt.conf.layout.west_fill_accordion_indicator
						);
					}
				}
			});

			dataElementAvailable = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-left',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				store: pt.store.dataElementAvailable,
				tbar: [
					{
						xtype: 'label',
						text: 'Available', //i18n pt.i18n.available
						cls: 'pt-toolbar-multiselect-left-label'
					},
					'->',
					{
						xtype: 'button',
						icon: 'images/arrowright.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.select(dataElementAvailable, dataElementSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowrightdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.selectAll(dataElementAvailable, dataElementSelected);
						}
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.select(this, dataElementSelected);
						}, this);
					}
				}
			});

			dataElementSelected = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-right',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				ddReorder: true,
				store: pt.store.dataElementSelected,
				tbar: [
					{
						xtype: 'button',
						icon: 'images/arrowleftdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselectAll(dataElementAvailable, dataElementSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowleft.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselect(dataElementAvailable, dataElementSelected);
						}
					},
					'->',
					{
						xtype: 'label',
						text: 'Selected', //i18n pt.i18n.selected,
						cls: 'pt-toolbar-multiselect-right-label'
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.unselect(dataElementAvailable, this);
						}, this);
					}
				}
			});

			dataElement = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-data">Data elements</div>', //i18n
				hideCollapseTool: true,
				getData: function() {
					var data = {
						param: pt.conf.finals.dimension.indicator.paramname,
						items: []
					};

					pt.store.dataElementSelected.each( function(r) {
						data.items.push(r.data.id);
					});

					return data.items.length ? data : null;
				},
				items: [
					{
						xtype: 'combobox',
						cls: 'pt-combo',
						style: 'margin-bottom:8px',
						width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding,
						valueField: 'id',
						displayField: 'name',
						fieldLabel: 'Select group', //i18n pt.i18n.select_group
						labelStyle: 'padding-left:7px',
						editable: false,
						store: {
							xtype: 'store',
							fields: ['id', 'name', 'index'],
							proxy: {
								type: 'ajax',
								url: pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.dataelementgroup_get,
								reader: {
									type: 'json',
									root: 'dataElementGroups'
								}
							},
							listeners: {
								load: function(s) {
									s.add({
										id: 0,
										name: 'All data element groups', //i18n pt.i18n.all_indicator_groups
										index: -1
									});
									s.sort([
										{
											property: 'index',
											direction: 'ASC'
										},
										{
											property: 'name',
											direction: 'ASC'
										}
									]);
								}
							}
						},
						listeners: {
							select: function(cb) {
								var store = pt.store.dataElementAvailable;
								store.parent = cb.getValue();

								if (pt.util.store.containsParent(store)) {
									pt.util.store.loadFromStorage(store);
									pt.util.multiselect.filterAvailable(dataElementAvailable, dataElementSelected);
								}
								else {
									if (cb.getValue() === 0) {
										store.proxy.url = pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.dataelement_getall;
										store.load();
									}
									else {
										store.proxy.url = pt.conf.finals.ajax.path_api + pt.conf.finals.ajax.dataelement_get + cb.getValue() + '.json';
										store.load();
									}
								}
							}
						}
					},
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							dataElementAvailable,
							dataElementSelected
						]
					}
				],
				listeners: {
					added: function() {
						pt.cmp.dimension.panels.push(this);
					},
					expand: function() {
						pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_indicator);
						pt.util.multiselect.setHeight(
							[dataElementAvailable, dataElementSelected],
							this,
							pt.conf.layout.west_fill_accordion_indicator
						);
					}
				}
			});

			dataSetAvailable = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-left',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				store: pt.store.dataSetAvailable,
				tbar: [
					{
						xtype: 'label',
						text: 'Available', //i18n pt.i18n.available
						cls: 'pt-toolbar-multiselect-left-label'
					},
					'->',
					{
						xtype: 'button',
						icon: 'images/arrowright.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.select(dataSetAvailable, dataSetSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowrightdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.selectAll(dataSetAvailable, dataSetSelected);
						}
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.select(this, dataSetSelected);
						}, this);
					}
				}
			});

			dataSetSelected = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-right',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				valueField: 'id',
				displayField: 'name',
				ddReorder: true,
				store: pt.store.dataSetSelected,
				tbar: [
					{
						xtype: 'button',
						icon: 'images/arrowleftdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselectAll(dataSetAvailable, dataSetSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowleft.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselect(dataSetAvailable, dataSetSelected);
						}
					},
					'->',
					{
						xtype: 'label',
						text: 'Selected', //i18n pt.i18n.selected,
						cls: 'pt-toolbar-multiselect-right-label'
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.unselect(dataSetAvailable, this);
						}, this);
					}
				}
			});

			dataSet = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-data">Reporting rates</div>', //i18n
				hideCollapseTool: true,
				getData: function() {
					var data = {
						param: pt.conf.finals.dimension.indicator.paramname,
						items: []
					};

					pt.store.dataSetSelected.each( function(r) {
						data.items.push(r.data.id);
					});

					return data.items.length ? data : null;
				},
				items: [
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							dataSetAvailable,
							dataSetSelected
						]
					}
				],
				listeners: {
					added: function() {
						pt.cmp.dimension.panels.push(this);
					},
					expand: function() {
						pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_indicator);
						pt.util.multiselect.setHeight(
							[dataElementAvailable, dataElementSelected],
							this,
							pt.conf.layout.west_fill_accordion_indicator
						);
					}
				}
			});

			rewind = Ext.create('Ext.form.field.Checkbox', {
				paramName: 'rewind',
				boxLabel: 'Rewind one period',
				xable: function() {
					this.setDisabled(pt.util.checkbox.isAllFalse());
				}
			});

			relativePeriod = Ext.create('Ext.panel.Panel', {
				hideCollapseTool: true,
				autoScroll: true,
				bodyStyle: 'border:0 none',
				items: [
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							{
								xtype: 'panel',
								layout: 'anchor',
								bodyStyle: 'border-style:none; padding:0 0 0 10px',
								defaults: {
									labelSeparator: '',
									listeners: {
										added: function(chb) {
											if (chb.xtype === 'checkbox') {
												pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											}
										},
										change: function() {
											rewind.xable();
										}
									}
								},
								items: [
									{
										xtype: 'label',
										text: 'Months', //i18n pt.i18n.months,
										cls: 'pt-label-period-heading'
									},
									{
										xtype: 'checkbox',
										paramName: 'LAST_MONTH',
										boxLabel: 'Last month', //i18n pt.i18n.last_month
									},
									{
										xtype: 'checkbox',
										paramName: 'LAST_3_MONTHS',
										boxLabel: 'Last 3 months', //i18n pt.i18n.last_3_months
									},
									{
										xtype: 'checkbox',
										paramName: 'LAST_12_MONTHS',
										boxLabel: 'Last 12 months', //i18n pt.i18n.last_12_months,
										checked: true
									}
								]
							},
							{
								xtype: 'panel',
								layout: 'anchor',
								bodyStyle: 'border-style:none; padding:0 0 0 32px',
								defaults: {
									labelSeparator: '',
									listeners: {
										added: function(chb) {
											if (chb.xtype === 'checkbox') {
												pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											}
										},
										change: function() {
											rewind.xable();
										}
									}
								},
								items: [
									{
										xtype: 'label',
										text: 'Quarters', //i18n pt.i18n.quarters,
										cls: 'pt-label-period-heading'
									},
									{
										xtype: 'checkbox',
										paramName: 'LAST_QUARTER',
										boxLabel: 'Last quarter', //i18n pt.i18n.last_quarter
									},
									{
										xtype: 'checkbox',
										paramName: 'LAST_4_QUARTERS',
										boxLabel: 'Last 4 quarters', //i18n pt.i18n.last_4_quarters
									}
								]
							},
							{
								xtype: 'panel',
								layout: 'anchor',
								bodyStyle: 'border-style:none; padding:0 0 0 32px',
								defaults: {
									labelSeparator: '',
									listeners: {
										added: function(chb) {
											if (chb.xtype === 'checkbox') {
												pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											}
										},
										change: function() {
											rewind.xable();
										}
									}
								},
								items: [
									{
										xtype: 'label',
										text: 'Six-months', //i18n pt.i18n.six_months,
										cls: 'pt-label-period-heading'
									},
									{
										xtype: 'checkbox',
										paramName: 'LAST_SIX_MONTH',
										boxLabel: 'Last six-month', //i18n pt.i18n.last_six_month
									},
									{
										xtype: 'checkbox',
										paramName: 'LAST_2_SIXMONTHS',
										boxLabel: 'Last two six-months', //i18n pt.i18n.last_two_six_month
									}
								]
							}
						]
					},
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							{
								xtype: 'panel',
								layout: 'anchor',
								bodyStyle: 'border-style:none; padding:5px 0 0 10px',
								defaults: {
									labelSeparator: '',
									listeners: {
										added: function(chb) {
											if (chb.xtype === 'checkbox') {
												pt.cmp.dimension.relativePeriod.checkbox.push(chb);
											}
										},
										change: function() {
											rewind.xable();
										}
									}
								},
								items: [
									{
										xtype: 'label',
										text: 'Years', //i18n pt.i18n.years,
										cls: 'pt-label-period-heading'
									},
									{
										xtype: 'checkbox',
										paramName: 'THIS_YEAR',
										boxLabel: 'This year', //i18n pt.i18n.this_year
									},
									{
										xtype: 'checkbox',
										paramName: 'LAST_YEAR',
										boxLabel: 'Last year', //i18n pt.i18n.last_year
									},
									{
										xtype: 'checkbox',
										paramName: 'LAST_5_YEARS',
										boxLabel: 'Last 5 years', //i18n pt.i18n.last_5_years
									}
								]
							},
							{
								xtype: 'panel',
								layout: 'anchor',
								bodyStyle: 'border-style:none; padding:5px 0 0 46px',
								defaults: {
									labelSeparator: ''
								},
								items: [
									{
										xtype: 'label',
										text: 'Options',
										cls: 'pt-label-period-heading-options'
									},
									rewind
								]
							}
						]
					}
				]
			});

			fixedPeriodAvailable = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-left',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				height: 180,
				valueField: 'id',
				displayField: 'name',
				store: pt.store.fixedPeriodAvailable,
				tbar: [
					{
						xtype: 'label',
						text: 'Available', //i18n pt.i18n.available,
						cls: 'pt-toolbar-multiselect-left-label'
					},
					'->',
					{
						xtype: 'button',
						icon: 'images/arrowright.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.select(fixedPeriodAvailable, fixedPeriodSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowrightdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.selectAll(fixedPeriodAvailable, fixedPeriodSelected);
						}
					},
					' '
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.select(fixedPeriodAvailable, fixedPeriodSelected);
						}, this);
					}
				}
			});

			fixedPeriodSelected = Ext.create('Ext.ux.form.MultiSelect', {
				cls: 'pt-toolbar-multiselect-right',
				width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
				height: 180,
				valueField: 'id',
				displayField: 'name',
				ddReorder: false,
				store: pt.store.fixedPeriodSelected,
				tbar: [
					' ',
					{
						xtype: 'button',
						icon: 'images/arrowleftdouble.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselectAll(fixedPeriodAvailable, fixedPeriodSelected);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowleft.png',
						width: 22,
						handler: function() {
							pt.util.multiselect.unselect(fixedPeriodAvailable, fixedPeriodSelected);
						}
					},
					'->',
					{
						xtype: 'label',
						text: 'Selected', //i18n pt.i18n.selected,
						cls: 'pt-toolbar-multiselect-right-label'
					}
				],
				listeners: {
					afterrender: function() {
						this.boundList.on('itemdblclick', function() {
							pt.util.multiselect.unselect(fixedPeriodAvailable, fixedPeriodSelected);
						}, this);
					}
				}
			});

			period = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-period">Periods</div>',
				hideCollapseTool: true,
				getData: function() {
					var data = {
						param: pt.conf.finals.dimension.period.paramname,
							items: []
						},
						chb = pt.cmp.dimension.relativePeriod.checkbox;

					pt.store.fixedPeriodSelected.each( function(r) {
						data.items.push(r.data.id);
					});

					for (var i = 0; i < chb.length; i++) {
						if (chb[i].getValue()) {
							data.items.push(chb[i].paramName);
						}
					}

					return data.items.length ? data : null;
				},
				items: [
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none',
						items: [
							{
								xtype: 'combobox',
								cls: 'pt-combo',
								style: 'margin-bottom:8px',
								width: 261,
								valueField: 'id',
								displayField: 'name',
								fieldLabel: 'Select type', //i18n pt.i18n.select_type,
								labelStyle: 'padding-left:7px',
								editable: false,
								queryMode: 'remote',
								store: pt.store.periodType,
								periodOffset: 0,
								listeners: {
									select: function() {
										var ptype = new PeriodType(),
											periodType = this.getValue();

										var periods = ptype.get(periodType).generatePeriods({
											offset: this.periodOffset,
											filterFuturePeriods: true,
											reversePeriods: true
										});

										pt.store.fixedPeriodAvailable.setIndex(periods);
										pt.store.fixedPeriodAvailable.loadData(periods);
										pt.util.multiselect.filterAvailable(fixedPeriodAvailable, fixedPeriodSelected);
									}
								}
							},
							{
								xtype: 'button',
								text: 'Prev year', //i18n
								style: 'margin-left:4px',
								height: 24,
								handler: function() {
									var cb = this.up('panel').down('combobox');
									if (cb.getValue()) {
										cb.periodOffset--;
										cb.fireEvent('select');
									}
								}
							},
							{
								xtype: 'button',
								text: 'Next year', //i18n
								style: 'margin-left:3px',
								height: 24,
								handler: function() {
									var cb = this.up('panel').down('combobox');
									if (cb.getValue() && cb.periodOffset < 0) {
										cb.periodOffset++;
										cb.fireEvent('select');
									}
								}
							}
						]
					},
					{
						xtype: 'panel',
						layout: 'column',
						bodyStyle: 'border-style:none; padding-bottom:10px',
						items: [
							fixedPeriodAvailable,
							fixedPeriodSelected
						]
					},
					relativePeriod
				],
				listeners: {
					added: function() {
						pt.cmp.dimension.panels.push(this);
					},
					expand: function() {
						pt.util.dimension.panel.setHeight(600);
						//pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_fixedperiod + pt.conf.layout.west_maxheight_accordion_relativeperiod);
						//pt.util.multiselect.setHeight(
							//[fixedPeriodAvailable, fixedPeriodSelected],
							//this,
							//pt.conf.layout.west_fill_accordion_fixedperiod
						//);
					}
				}
			});

			organisationUnit = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-organisationunit">Organisation units</div>', //i18n pt.i18n.organisation_units
				hideCollapseTool: true,
				collapsed: false,
				getData: function() {
					var records = pt.cmp.dimension.organisationUnit.treepanel.getSelectionModel().getSelection(),
						data = {
							param: 'ou',
							items: []
						};

					for (var i = 0; i < records.length; i++) {
						data.items.push(records[i].data.id);
					}

					return data.items.length ? data : null;
				},
				items: [
					{
						layout: 'column',
						bodyStyle: 'border:0 none; padding-bottom:4px',
						items: [
							{
								xtype: 'checkbox',
								columnWidth: 0.5,
								boxLabel: 'User organisation unit', //i18n pt.i18n.user_orgunit,
								labelWidth: pt.conf.layout.form_label_width,
								handler: function(chb, checked) {
									pt.cmp.dimension.organisationUnit.toolbar.xable(checked, pt.cmp.favorite.userOrganisationUnitChildren.getValue());
									pt.cmp.dimension.organisationUnit.treepanel.xable(checked, pt.cmp.favorite.userOrganisationUnitChildren.getValue());
								},
								listeners: {
									added: function() {
										pt.cmp.favorite.userOrganisationUnit = this;
									}
								}
							},
							{
								xtype: 'checkbox',
								columnWidth: 0.5,
								boxLabel: 'User organisation unit children', //i18n pt.i18n.user_orgunit_children,
								labelWidth: pt.conf.layout.form_label_width,
								handler: function(chb, checked) {
									pt.cmp.dimension.organisationUnit.toolbar.xable(checked, pt.cmp.favorite.userOrganisationUnit.getValue());
									pt.cmp.dimension.organisationUnit.treepanel.xable(checked, pt.cmp.favorite.userOrganisationUnit.getValue());
								},
								listeners: {
									added: function() {
										pt.cmp.favorite.userOrganisationUnitChildren = this;
									}
								}
							}
						]
					},
					{
						id: 'organisationunit_t',
						xtype: 'toolbar',
						style: 'margin-bottom: 5px',
						width: pt.conf.layout.west_fieldset_width - 18,
						xable: function(checked, value) {
							if (checked || value) {
								this.disable();
							}
							else {
								this.enable();
							}
						},
						defaults: {
							height: 24
						},
						items: [
							{
								xtype: 'label',
								text: 'Auto-select organisation units by', //i18n
								style: 'padding-left:8px; color:#666; line-height:24px'
							},
							'->',
							{
								text: 'Group..',
								handler: function() {},
								listeners: {
									added: function() {
										this.menu = Ext.create('Ext.menu.Menu', {
											shadow: false,
											showSeparator: false,
											width: pt.conf.layout.treepanel_toolbar_menu_width_group,
											items: [
												{
													xtype: 'grid',
													cls: 'pt-menugrid',
													width: pt.conf.layout.treepanel_toolbar_menu_width_group,
													scroll: 'vertical',
													columns: [
														{
															dataIndex: 'name',
															width: pt.conf.layout.treepanel_toolbar_menu_width_group,
															style: 'display:none'
														}
													],
													setHeightInMenu: function(store) {
														var h = store.getCount() * 24,
															sh = pt.util.viewport.getSize().y * 0.6;
														this.setHeight(h > sh ? sh : h);
														this.doLayout();
														this.up('menu').doLayout();
													},
													store: pt.store.group,
													listeners: {
														itemclick: function(g, r) {
															g.getSelectionModel().select([], false);
															this.up('menu').hide();
															pt.cmp.dimension.organisationUnit.treepanel.selectByGroup(r.data.id);
														}
													}
												}
											],
											listeners: {
												show: function() {
													var store = pt.store.group;

													if (!store.isLoaded) {
														store.load({scope: this, callback: function() {
															this.down('grid').setHeightInMenu(store);
														}});
													}
													else {
														this.down('grid').setHeightInMenu(store);
													}
												}
											}
										});
									}
								}
							}
						],
						listeners: {
							added: function() {
								pt.cmp.dimension.organisationUnit.toolbar = this;
							}
						}
					},
					{
						xtype: 'treepanel',
						cls: 'pt-tree',
						width: pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding,
						rootVisible: false,
						autoScroll: true,
						multiSelect: true,
						rendered: false,
						selectRootIf: function() {
							if (this.getSelectionModel().getSelection().length < 1) {
								var node = this.getRootNode().findChild('id', pt.init.rootnodes[0].id, true);
								if (this.rendered) {
									this.getSelectionModel().select(node);
								}
								return node;
							}
						},
						numberOfRecords: 0,
						recordsToSelect: [],
						multipleSelectIf: function() {
							if (this.recordsToSelect.length === this.numberOfRecords) {
								this.getSelectionModel().select(this.recordsToSelect);
								this.recordsToSelect = [];
								this.numberOfRecords = 0;
							}
						},
						multipleExpand: function(id, path) {
							this.expandPath('/' + pt.conf.finals.root.id + path, 'id', '/', function() {
								var record = this.getRootNode().findChild('id', id, true);
								this.recordsToSelect.push(record);
								this.multipleSelectIf();
							}, this);
						},
						select: function(url, params) {
							if (!params) {
								params = {};
							}
							Ext.Ajax.request({
								url: url,
								method: 'GET',
								params: params,
								scope: this,
								success: function(r) {
									var a = Ext.decode(r.responseText).organisationUnits;
									this.numberOfRecords = a.length;
									for (var i = 0; i < a.length; i++) {
										this.multipleExpand(a[i].id, a[i].path);
									}
								}
							});
						},
						selectByGroup: function(id) {
							if (id) {
								var url = pt.conf.finals.ajax.path_pivot + pt.conf.finals.ajax.organisationunit_getbygroup,
									params = {id: id};
								this.select(url, params);
							}
						},
						selectByLevel: function(level) {
							if (level) {
								var url = pt.conf.finals.ajax.path_pivot + pt.conf.finals.ajax.organisationunit_getbylevel,
									params = {level: level};
								this.select(url, params);
							}
						},
						selectByIds: function(ids) {
							if (ids) {
								var url = pt.conf.finals.ajax.path_pivot + pt.conf.finals.ajax.organisationunit_getbyids;
								Ext.Array.each(ids, function(item) {
									url = Ext.String.urlAppend(url, 'ids=' + item);
								});
								if (!this.rendered) {
									pt.cmp.dimension.organisationUnit.panel.expand();
								}
								this.select(url);
							}
						},
						store: Ext.create('Ext.data.TreeStore', {
							proxy: {
								type: 'ajax',
								url: pt.conf.finals.ajax.path_pivot + pt.conf.finals.ajax.organisationunitchildren_get
							},
							root: {
								id: pt.conf.finals.root.id,
								expanded: true,
								children: pt.init.rootNodes
							},
							listeners: {
								load: function(s, node, r) {
									for (var i = 0; i < r.length; i++) {
										r[i].data.text = pt.conf.util.jsonEncodeString(r[i].data.text);
									}
								}
							}
						}),
						xable: function(checked, value) {
							if (checked || value) {
								this.disable();
							}
							else {
								this.enable();
							}
						},
						listeners: {
							added: function() {
								pt.cmp.dimension.organisationUnit.treepanel = this;
							},
							render: function() {
								this.rendered = true;
							},
							afterrender: function() {
								this.getSelectionModel().select(0);
							},
							itemcontextmenu: function(v, r, h, i, e) {
								v.getSelectionModel().select(r, false);

								if (v.menu) {
									v.menu.destroy();
								}
								v.menu = Ext.create('Ext.menu.Menu', {
									id: 'treepanel-contextmenu',
									showSeparator: false
								});
								if (!r.data.leaf) {
									v.menu.add({
										id: 'treepanel-contextmenu-item',
										text: 'Select all children', //i18n pt.i18n.select_all_children,
										icon: 'images/node-select-child.png',
										handler: function() {
											r.expand(false, function() {
												v.getSelectionModel().select(r.childNodes, true);
												v.getSelectionModel().deselect(r);
											});
										}
									});
								}
								else {
									return;
								}

								v.menu.showAt(e.xy);
							}
						}
					}
				],
				listeners: {
					added: function() {
						pt.cmp.dimension.panels.push(this);
					},
					expand: function(p) {
						pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_organisationunit);
						pt.cmp.dimension.organisationUnit.treepanel.setHeight(p.getHeight() - pt.conf.layout.west_fill_accordion_organisationunit);
						//pt.cmp.dimension.organisationUnit.treepanel.selectRootIf();
					}
				}
			});

			options = Ext.create('Ext.panel.Panel', {
				title: '<div class="pt-panel-title-options">Options</div>', //i18n pt.i18n.chart_options
				hideCollapseTool: true,
				cls: 'pt-accordion-options',
				items: []
			});

			getOrganisationUnitGroupSetPanels = function() {
				var	getAvailableStore,
					getSelectedStore,

					createPanel,
					getPanels;

				getAvailableStore = function(groupSetId) {
					return Ext.create('Ext.data.Store', {
						fields: ['id', 'name'],
						proxy: {
							type: 'ajax',
							url: pt.conf.finals.ajax.path_api + 'organisationUnitGroupSets/' + groupSetId + '.json?links=false&paging=false',
							reader: {
								type: 'json',
								root: 'organisationUnitGroups'
							}
						},
						isLoaded: false,
						storage: {},
						sortStore: function() {
							this.sort('name', 'ASC');
						},
						listeners: {
							load: function(s) {
								s.isLoaded = true;
								s.each( function(r) {
									r.data.name = pt.conf.util.jsonEncodeString(r.data.name);
								});
								pt.util.store.addToStorage(s);
							}
						}
					});
				};

				getSelectedStore = function() {
					return Ext.create('Ext.data.Store', {
						fields: ['id', 'name'],
						data: []
					});
				};

				createPanel = function(groupSet) {
					var getAvailable,
						getSelected,

						availableStore,
						selectedStore,
						available,
						selected,

						panel;

					getAvailable = function(availableStore) {
						return Ext.create('Ext.ux.form.MultiSelect', {
							cls: 'pt-toolbar-multiselect-left',
							width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
							valueField: 'id',
							displayField: 'name',
							store: availableStore,
							tbar: [
								{
									xtype: 'label',
									text: 'Available', //i18n pt.i18n.available
									cls: 'pt-toolbar-multiselect-left-label'
								},
								'->',
								{
									xtype: 'button',
									icon: 'images/arrowright.png',
									width: 22,
									handler: function() {
										pt.util.multiselect.select(available, selected);
									}
								},
								{
									xtype: 'button',
									icon: 'images/arrowrightdouble.png',
									width: 22,
									handler: function() {
										pt.util.multiselect.selectAll(available, selected);
									}
								}
							],
							listeners: {
								afterrender: function() {
									this.boundList.on('itemdblclick', function() {
										pt.util.multiselect.select(available, selected);
									}, this);
								}
							}
						});
					};

					getSelected = function(selectedStore) {
						return Ext.create('Ext.ux.form.MultiSelect', {
							cls: 'pt-toolbar-multiselect-right',
							width: (pt.conf.layout.west_fieldset_width - pt.conf.layout.west_width_padding) / 2,
							valueField: 'id',
							displayField: 'name',
							ddReorder: true,
							store: selectedStore,
							tbar: [
								{
									xtype: 'button',
									icon: 'images/arrowleftdouble.png',
									width: 22,
									handler: function() {
										pt.util.multiselect.unselectAll(available, selected);
									}
								},
								{
									xtype: 'button',
									icon: 'images/arrowleft.png',
									width: 22,
									handler: function() {
										pt.util.multiselect.unselect(available, selected);
									}
								},
								'->',
								{
									xtype: 'label',
									text: 'Selected', //i18n pt.i18n.selected,
									cls: 'pt-toolbar-multiselect-right-label'
								}
							],
							listeners: {
								afterrender: function() {
									this.boundList.on('itemdblclick', function() {
										pt.util.multiselect.unselect(available, selected);
									}, this);
								}
							}
						});
					};

					availableStore = getAvailableStore(groupSet.id);
					selectedStore = getSelectedStore();

					available = getAvailable(availableStore);
					selected = getSelected(selectedStore);

					availableStore.on('load', function() {
						pt.util.multiselect.filterAvailable(available, selected);
					});

					panel = Ext.create('Ext.panel.Panel', {
						title: '<div class="pt-panel-title-organisationunit">Group set: ' + groupSet.name + '</div>', //i18n
						hideCollapseTool: true,
						getData: function() {
							var data = {
								param: groupSet.id,
								items: []
							};

							selectedStore.each( function(r) {
								data.items.push(r.data.id);
							});

							return data.items.length ? data : null;
						},
						items: [
							{
								xtype: 'panel',
								layout: 'column',
								bodyStyle: 'border-style:none',
								items: [
									available,
									selected
								]
							}
						],
						listeners: {
							added: function() {
								pt.cmp.dimension.panels.push(this);
							},
							expand: function() {
								if (!availableStore.isLoaded) {
									availableStore.load();
								}

								pt.util.dimension.panel.setHeight(pt.conf.layout.west_maxheight_accordion_indicator);

								pt.util.multiselect.setHeight(
									[available, selected],
									this,
									pt.conf.layout.west_fill_accordion_indicator
								);
							}
						}
					});

					return panel;
				};

				getPanels = function() {
					var ougs = pt.init.ougs,
						panels = [],
						groupSet;

					for (var i = 0, panel; i < ougs.length; i++) {
						groupSet = ougs[i];

						panel = createPanel(groupSet);

						panels.push(panel);
					}

					return panels;
				};

				return getPanels();
			};

			accordion = Ext.create('Ext.panel.Panel', {
				bodyStyle: 'border-style:none; padding:6px;',
				layout: 'fit',
				items: [
					{
						xtype: 'panel',
						layout: 'accordion',
						activeOnTop: true,
						cls: 'pt-accordion',
						bodyStyle: 'border:0 none',
						height: 430,
						items: function() {
							var panels = [
								indicator,
								dataElement,
								dataSet,
								period,
								organisationUnit
							];

							panels = panels.concat(getOrganisationUnitGroupSetPanels());
							panels.push(options);

							return panels;
						}(),
					}
				],
				listeners: {
					added: function() {
						pt.cmp.dimension.panel = this;
					}
				}
			});

			westRegion = Ext.create('Ext.panel.Panel', {
				region: 'west',
				preventHeader: true,
				collapsible: true,
				collapseMode: 'mini',
				width: pt.conf.layout.west_width,
				items: accordion,
                listeners: {
                    collapse: function() {
                        this.collapsed = true;
                        pt.cmp.toolbar.resizewest.setText('>>>');
                    },
                    expand: function() {
                        this.collapsed = false;
                        pt.cmp.toolbar.resizewest.setText('<<<');
                    }
                }
			});

			centerRegion = Ext.create('Ext.panel.Panel', {
				region: 'center',
				bodyStyle: 'padding:20px',
				tbar: {
                    defaults: {
                        height: 26
                    },
					items: [
						{
							text: 'Settings',
							handler: function() {
								if (!pt.viewport.settingsWindow) {
									pt.viewport.settingsWindow = PT.app.SettingsWindow(pt);
								}

								pt.viewport.settingsWindow.show();
							}
						},
						{
							text: '<b>Update</b>',
							handler: function() {
								var settings = pt.api.Settings(pt.util.pivot.getSettingsConfig());

								if (settings && Ext.isObject(settings)) {
									pt.util.pivot.getTable(settings, pt, centerRegion);
								}
							}
						}
					]
				}
			});

			viewport = Ext.create('Ext.container.Viewport', {
				layout: 'border',
				items: [
					westRegion,
					centerRegion
				],
				listeners: {
					afterrender: function() {
						pt.init.afterRender();
					}
				}
			});

			viewport.westRegion = westRegion;
			viewport.centerRegion = centerRegion;

			addListeners = function() {
				pt.store.indicatorAvailable.on('load', function() {
					pt.util.multiselect.filterAvailable(indicatorAvailable, indicatorSelected);
				});

				pt.store.dataElementAvailable.on('load', function() {
					pt.util.multiselect.filterAvailable(dataElementAvailable, dataElementSelected);
				});

				pt.store.dataSetAvailable.on('load', function(s) {
					pt.util.multiselect.filterAvailable(dataSetAvailable, dataSetSelected);
					s.sort('name', 'ASC');
				});
			}();

			return viewport;
		};

		pt.init = PT.app.getInits(r);

		pt.util = PT.app.getUtils();

		pt.store = PT.app.getStores();

		pt.cmp = PT.app.getCmp();

		pt.viewport = createViewport();
	};

	Ext.Ajax.request({
		url: pt.conf.finals.ajax.path_pivot + 'initialize.action',
		success: function(r) {
			PT.app.init.onInitialize(r);
	}});
});

