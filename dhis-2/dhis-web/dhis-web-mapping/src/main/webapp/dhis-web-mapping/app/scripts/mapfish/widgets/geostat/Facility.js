/*
 * Copyright (C) 2007-2008  Camptocamp|
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @requires core/GeoStat/Facility.js
 * @requires core/Color.js
 */

Ext.define('mapfish.widgets.geostat.Facility', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.facility',
	
	// Ext panel
	cls: 'gis-form-widget el-border-0',
    border: false,

	// Mapfish
    layer: null,
    format: null,
    url: null,
    indicator: null,
    coreComp: null,
    classificationApplied: false,
    loadMask: false,
    labelGenerator: null,
    
    // Properties
    
    config: {},
    
    tmpModel: {},
    
    model: {},
    
    cmp: {},
    
    features: [],
    
    selectHandlers: {},
    
    store: {		
		infrastructuralDataElementValues: Ext.create('Ext.data.Store', {
			fields: ['dataElementName', 'value'],
			proxy: {
				type: 'ajax',
				url: '../getInfrastructuralDataElementMapValues.action',
				reader: {
					type: 'json',
					root: 'mapValues'
				}
			},
			sortInfo: {field: 'dataElementName', direction: 'ASC'},
			autoLoad: false,
			isLoaded: false,
			listeners: {
				load: function() {
					if (!this.isLoaded) {
						this.isLoaded = true;
					}
				}
			}
		}),
		
		features: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			loadFeatures: function(features) {
				if (features && features.length) {
					var data = [];
					for (var i = 0; i < features.length; i++) {
						data.push([features[i].attributes.id, features[i].attributes.name]);
					}
					this.loadData(data);
					this.sortStore();
				}
				else {
					this.removeAll();
				}
			},
			sortStore: function() {
				this.sort('name', 'ASC');
			}
		})
	},
    
    setUrl: function(url) {
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    requestSuccess: function(request) {
        var doc = request.responseXML,
			format = new OpenLayers.Format.GeoJSON();
			
        if (!doc || !doc.documentElement) {
            doc = request.responseText;
        }
        
        if (doc.length) {
            doc = GIS.util.geojson.decode(doc, this);
        }
        else {
			alert("no coordinates"); //todo //i18n
		}
        
        this.layer.removeFeatures(this.layer.features);
        this.layer.addFeatures(format.read(doc));
		this.layer.features = GIS.util.vector.getTransformedFeatureArray(this.layer.features);
        this.features = this.layer.features.slice(0);
        
        this.loadData();
    },

    requestFailure: function(request) {
        GIS.logg.push(request.status, request.statusText);        
        console.log(request.status, request.statusText);
    },
    
    getColors: function(low, high) {
        var startColor = new mapfish.ColorRgb();
        startColor.setFromHex(low || this.cmp.colorLow.getValue());
        var endColor = new mapfish.ColorRgb();
        endColor.setFromHex(high || this.cmp.colorHigh.getValue());
        return [startColor, endColor];
    },
    
    initComponent: function() {		
		this.createItems();
		
		this.addItems();
		
		this.createSelectHandlers();
		
		this.coreComp = new mapfish.GeoStat.Facility(this.map, {
            layer: this.layer,
            format: this.format,
            url: this.url,
            requestSuccess: Ext.bind(this.requestSuccess, this),
            requestFailure: Ext.bind(this.requestFailure, this),
            legendDiv: this.legendDiv,
            labelGenerator: this.labelGenerator,
            widget: this
        });
		
		mapfish.widgets.geostat.Facility.superclass.initComponent.apply(this);
    },
    
    createItems: function() {
		
		// Group set
        
        this.cmp.groupSet = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.groupset,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            currentValue: false,
            store: GIS.store.groupSets, //todo
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
                        var store = GIS.store.groupsByGroupSet;
                        store.proxy.url = GIS.conf.url.path_api +  'organisationUnitGroupSets/' + cb.getValue() + '.json?links=false&paging=false';
                        store.load({
							scope: this,
							callback: function() {
								if (this.tmpModel.updateGui) { // When favorite, load store and continue execution
									if (this.tmpModel.updateOrganisationUnit) {
										this.loadOrganisationUnits();
									}
									else {
										this.loadLegend();
									}
								}	
							}
						});
                    }
                }
            }
        });
        
        // Organisation unit options
        
        this.cmp.level = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.level,
            editable: false,
            valueField: 'level',
            displayField: 'name',
            mode: 'remote',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            style: 'margin-bottom: 4px',
            store: GIS.store.organisationUnitLevels,
			listeners: {
				added: function() {
					this.store.cmp.push(this);
				},
				select: {
					scope: this,
					fn: function() {
						this.config.updateOrganisationUnit = true;
					}
				}
			}
        });
        
        this.cmp.parent = Ext.create('Ext.tree.Panel', {
            autoScroll: true,
            lines: false,
			rootVisible: false,
			multiSelect: false,
			width: GIS.conf.layout.widget.item_width,
			height: 220,
			pathToSelect: null,
			pathToExpand: null,
			reset: function() {
				this.collapseAll();
				this.expandTreePath(GIS.init.rootNodes[0].path);
				this.selectTreePath(GIS.init.rootNodes[0].path);
			},
			selectTreePath: function(path) {
				if (this.rendered) {
					this.selectPath(path);
				}
				else {
					this.pathToSelect = path;
				}
			},
			expandTreePath: function(path) {
				if (this.rendered) {
					this.expandPath(path);
				}
				else {
					this.pathToExpand = path;
				}
			},
			store: GIS.store.organisationUnitHierarchy,
			listeners: {
				select: {
					scope: this,
					fn: function() {
						this.config.updateOrganisationUnit = true;
					}
				},
				afterrender: function() {					
					if (this.pathToSelect) {
						this.selectPath(this.pathToSelect);
						this.pathToSelect = null;
					}
					else {
						this.getSelectionModel().select(0);
					}
					
					if (this.pathToExpand) {
						this.expandPath(this.pathToExpand);
						this.pathToExpand = null;
					}
				}
			}
        });
    },
    
    addItems: function() {
        
        this.items = [
            {
                xtype: 'form',
				cls: 'el-border-0',
                width: 270,
                items: [
					{
						html: 'Organisation unit group set', //i18n
						cls: 'gis-form-subtitle-first'
					},
					this.cmp.groupSet,
					{
						html: 'Organisation unit level / parent', //i18n
						cls: 'gis-form-subtitle'
					},
					this.cmp.level,
					this.cmp.parent
				]
            }
        ];
    },
    
    createSelectHandlers: function() {
        var that = this,
			window,
			menu,
			infrastructuralPeriod,
			onHoverSelect,
			onHoverUnselect,
			onClickSelect;
        
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
			
			var x = window.getPosition()[0];
			window.setPosition(x, 32);
        };
        
        onHoverUnselect = function fn(feature) {
			window.destroy();
        };
        
        onClickSelect = function fn(feature) {
			var showInfo,				
				showRelocate,
				drill,
				menu,
				isPoint = feature.geometry.CLASS_NAME === GIS.conf.finals.openLayers.point_classname;
			
			// Relocate
			showRelocate = function() {
				if (that.cmp.relocateWindow) {
					that.cmp.relocateWindow.destroy();
				}
				
				that.cmp.relocateWindow = Ext.create('Ext.window.Window', {
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
								GIS.map.relocate.active = false;
								that.cmp.relocateWindow.destroy();
								GIS.map.getViewport().style.cursor = 'auto';
							}
						}
					],
					listeners: {
						close: function() {
							GIS.map.relocate.active = false;
							GIS.map.getViewport().style.cursor = 'auto';
						}
					}
				});
					
				that.cmp.relocateWindow.show();					
				that.cmp.relocateWindow.setMinWidth(220);
				
				GIS.util.gui.window.setPositionTopRight(that.cmp.relocateWindow);
			};
			
			// Infrastructural data
			showInfo = function() {
				Ext.Ajax.request({
					url: GIS.conf.url.path_gis + 'getFacilityInfo.action',
					params: {
						id: feature.attributes.id
					},
					success: function(r) {
						var ou = Ext.decode(r.responseText);
						
						if (that.cmp.infrastructuralWindow) {
							that.cmp.infrastructuralWindow.destroy();
						}
						
						that.cmp.infrastructuralWindow = Ext.create('Ext.window.Window', {
							title: 'Facility information', //i18n
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
									items: [
										{
											html: GIS.i18n.name,
											cls: 'gis-panel-html-title'
										},
										{
											html: feature.attributes.name,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.type,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.ty,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.code,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.co,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.address,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.ad,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.contact_person,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.cp,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.email,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.em,
											cls: 'gis-panel-html'
										},
										{
											cls: 'gis-panel-html-separator'
										},
										{
											html: GIS.i18n.phone_number,
											cls: 'gis-panel-html-title'
										},
										{
											html: ou.pn,
											cls: 'gis-panel-html'
										}
									]
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
											store: GIS.store.infrastructuralPeriodsByType,
											lockPosition: false,
											listeners: {
												select: function() {
													infrastructuralPeriod = this.getValue();
													
													that.store.infrastructuralDataElementValues.load({
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
											store: that.store.infrastructuralDataElementValues
										}
									]
								}
							],
							listeners: {
								show: function() {									
									if (infrastructuralPeriod) {
										this.down('combo').setValue(infrastructuralPeriod);
										that.store.infrastructuralDataElementValues.load({
											params: {
												periodId: infrastructuralPeriod,
												organisationUnitId: feature.attributes.internalId
											}
										});
									}
								}
							}
						});
						
						that.cmp.infrastructuralWindow.show();
						GIS.util.gui.window.setPositionTopRight(that.cmp.infrastructuralWindow);
					}
				});
			};
			
			// Drill or float
			drill = function(direction) {
				var store = GIS.store.organisationUnitLevels;
				
				store.loadFn( function() {
					var store = GIS.store.organisationUnitLevels;
					
					if (direction === 'up') {
						var rootNode = GIS.init.rootNodes[0];
						
						that.config.level = that.model.level - 1;
						that.config.levelName = store.getAt(store.find('level', that.config.level)).data.name;
						that.config.parentId = rootNode.id;
						that.config.parentName = rootNode.text;
						that.config.parentLevel = rootNode.level;
						that.config.parentPath = '/' + GIS.init.rootNodes[0].id;
					}
					else if (direction === 'down') {
						that.config.level = that.model.level + 1;
						that.config.levelName = store.getAt(store.find('level', that.config.level)).data.name;
						that.config.parentId = feature.attributes.id;
						that.config.parentName = feature.attributes.name;
						that.config.parentLevel = that.model.level;
						that.config.parentPath = feature.attributes.path;
					}
					
					that.config.updateOrganisationUnit = true;
					that.config.updateGui = true;
					
					that.execute();
				});
			};
			
			// Menu
			var menuItems = [
				Ext.create('Ext.menu.Item', {
					text: 'Float up',
					iconCls: 'gis-menu-item-icon-float',
					disabled: !that.model.hasCoordinatesUp,
					scope: this,
					handler: function() {
						drill('up');
					}
				}),
				Ext.create('Ext.menu.Item', {
					text: 'Drill down',
					iconCls: 'gis-menu-item-icon-drill',
					cls: 'gis-menu-item-first',
					disabled: !feature.attributes.hcwc,
					scope: this,
					handler: function() {
						drill('down');
					}
				})
			];
			
			if (isPoint) {
				menuItems.push({
					xtype: 'menuseparator'
				});
				
				menuItems.push( Ext.create('Ext.menu.Item', {
					text: GIS.i18n.relocate,
					iconCls: 'gis-menu-item-icon-relocate',
					disabled: !GIS.init.security.isAdmin,
					handler: function(item) {
						GIS.map.relocate.active = true;
						GIS.map.relocate.widget = that;
						GIS.map.relocate.feature = feature;
						GIS.map.getViewport().style.cursor = 'crosshair';
						showRelocate();
					}
				}));
				
				menuItems.push( Ext.create('Ext.menu.Item', {
					text: 'Show information', //i18n
					iconCls: 'gis-menu-item-icon-information',
					handler: function(item) {
						if (GIS.store.infrastructuralPeriodsByType.isLoaded) {
							showInfo();
						}
						else {
							GIS.store.infrastructuralPeriodsByType.load({
								params: {
									name: GIS.init.systemSettings.infrastructuralPeriodType
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
            
            menu.showAt([GIS.map.mouseMove.x, GIS.map.mouseMove.y]);
        };
        
        this.selectHandlers = new OpenLayers.Control.newSelectFeature(this.layer, {
			onHoverSelect: onHoverSelect,
			onHoverUnselect: onHoverUnselect,
			onClickSelect: onClickSelect
		});
        
        GIS.map.addControl(this.selectHandlers);
        this.selectHandlers.activate();
    },
	
	getLegendConfig: function() {
		return {
			where: this.tmpModel.levelName + ' / ' + this.tmpModel.parentName
		};
	},
        //,
        
        //getImageExportValues: function() {
			//return {
				//mapValueTypeValue: this.cmp.valueType.getValue() == GIS.conf.map_value_type_indicator ?
					//this.cmp.indicator.getRawValue() : this.cmp.dataElement.getRawValue(),
				//dateValue: this.cmp.period.getRawValue()
			//};
		//},
		
	reset: function() {
		this.cmp.groupSet.clearValue();
		
		this.cmp.level.clearValue();
		this.cmp.parent.reset();
		
		// Layer options
		if (this.cmp.searchWindow) {
			this.cmp.searchWindow.destroy();
		}
		if (this.cmp.filterWindow) {
			this.cmp.filterWindow.destroy();
		}
		if (this.cmp.labelWindow) {
			this.cmp.labelWindow.destroy();
		}
		
		// Model
		this.config = {};
		this.tmpModel = {};
		this.model = {};
		
		// Layer
		this.layer.destroyFeatures();
		this.features = this.layer.features.slice(0);
		this.store.features.loadFeatures();
		this.layer.item.setValue(false);
		
		// Legend
		document.getElementById(this.legendDiv).innerHTML = '';
	},
	
	setConfig: function(config) {
		this.config.groupSet = config.groupSet;
		this.config.level = config.level;
		this.config.levelName = config.levelName;
		this.config.parentId = config.parentId;
		this.config.parentName = config.parentName;
		this.config.parentLevel = config.parentLevel;
		this.config.parentPath = config.parentPath;
		this.config.updateOrganisationUnit = true;
		this.config.updateLegend = false;
		this.config.updateGui = true;
	},
	
	setGui: function() {
		var model = this.tmpModel;
		
		// Group set		
		this.cmp.groupSet.setValue(model.groupSet);
		this.cmp.groupSet.fireEvent('select');
		
		// Level and parent
		var levelView = this.cmp.level;
		GIS.store.organisationUnitLevels.loadFn( function() {
			levelView.setValue(model.level);
		});
		
		this.cmp.parent.selectTreePath('/root' + model.parentPath);
	},
    	
	getModel: function() {
		var level = this.cmp.level,
			parent = this.cmp.parent.getSelectionModel().getSelection();
		parent = parent.length ? parent : [{raw: GIS.init.rootNodes[0]}];
		
		var model = {
			groupSet: this.cmp.groupSet.getValue(),
			level: level.getValue(),
			levelName: level.getRawValue(),
			parentId: parent[0].raw.id,
			parentName: parent[0].raw.text,
			parentLevel: parent[0].raw.level,
			parentPath: parent[0].raw.path,
			updateOrganisationUnit: false,
			updateData: false,
			updateLegend: false,
			updateGui: false
		};
		
		model.groupSet = this.config.groupSet || model.groupSet;
		model.level = this.config.level || model.level;
		model.levelName = this.config.levelName || model.levelName;
		model.parentId = this.config.parentId || model.parentId;
		model.parentName = this.config.parentName || model.parentName;
		model.parentLevel = this.config.parentLevel || model.parentLevel;
		model.parentPath = this.config.parentPath || null;
		model.updateOrganisationUnit = Ext.isDefined(this.config.updateOrganisationUnit) ? this.config.updateOrganisationUnit : false;
		model.updateLegend = Ext.isDefined(this.config.updateLegend) ? this.config.updateLegend : false;
		model.updateGui = Ext.isDefined(this.config.updateGui) ? this.config.updateGui : false;
		
		return model;
	},
	
	validateModel: function(model) {
		if (!model.groupSet || !Ext.isString(model.groupSet)) {
			GIS.logg.push([model.groupSet, this.xtype + '.parentId: string']);
				//alert("validation failed"); //todo
			return false;
		}
		
		if (!model.level || !Ext.isNumber(model.level)) {
			GIS.logg.push([model.level, this.xtype + '.level: number']);
				//alert("validation failed"); //todo
			return false;
		}
		if (!model.levelName || !Ext.isString(model.levelName)) {
			GIS.logg.push([model.levelName, this.xtype + '.levelName: string']);
				//alert("validation failed"); //todo
			return false;
		}
		if (!model.parentId || !Ext.isString(model.parentId)) {
			GIS.logg.push([model.parentId, this.xtype + '.parentId: string']);
				//alert("validation failed"); //todo
			return false;
		}
		if (!model.parentName || !Ext.isString(model.parentName)) {
			GIS.logg.push([model.parentName, this.xtype + '.parentName: string']);
				//alert("validation failed"); //todo
			return false;
		}
		if (!model.parentLevel || !Ext.isNumber(model.parentLevel)) {
			GIS.logg.push([model.parentLevel, this.xtype + '.parentLevel: number']);
				//alert("validation failed"); //todo
			return false;
		}
		if (model.parentLevel > model.level) {
			GIS.logg.push([model.parentLevel, model.level, this.xtype + '.parentLevel: number <= ' + this.xtype + '.level']);
				//alert("validation failed"); //todo
			return false;
		}
		
		if (!model.parentPath && model.updateGui) {
			GIS.logg.push([model.parentpath, this.xtype + '.parentpath: string']);
				//alert("validation failed"); //todo
			return false;
		}
		
		if (!model.updateOrganisationUnit && !model.updateLegend) {
			GIS.logg.push([model.updateOrganisationUnit, model.updateLegend, this.xtype + '.update ou/legend: true||true']);
			return false;
		}
		
		return true;
	},
	
    loadOrganisationUnits: function() {
        var url = GIS.conf.url.path_gis + 'getGeoJsonFacilities.action?' +
            'parentId=' + this.tmpModel.parentId +
            '&level=' + this.tmpModel.level;
        this.setUrl(url);
    },
    
    loadData: function() {
		for (var i = 0; i < this.layer.features.length; i++) {
			var feature = this.layer.features[i];
			feature.attributes.label = feature.attributes.name;
			feature.attributes.value = 0;
		}
		
		this.loadLegend();
	},
	
	loadLegend: function() {
		var options = {
            indicator: this.cmp.groupSet.getRawValue()
		};

        this.coreComp.applyClassification(options);
        this.classificationApplied = true;
        
        this.afterLoad();
	},
	
    execute: function() {
		this.tmpModel = this.getModel();
		
		if (!this.validateModel(this.tmpModel)) {
			return;
		}
				
		GIS.mask.msg = GIS.i18n.loading;
		GIS.mask.show();
		
		if (this.tmpModel.updateGui) { // If favorite, wait for groups store callback
			this.setGui();
		}
		else {
			if (this.tmpModel.updateOrganisationUnit) {
				this.loadOrganisationUnits();
			}
			else {
				this.loadLegend();
			}
		}
	},
	
	afterLoad: function() {
		this.model = this.tmpModel;
		this.config = {};
		
		// Layer item
		this.layer.item.setValue(true);
		
		// Layer menu
		this.menu.enableItems();
		
		// Update search window
		this.store.features.loadFeatures(this.layer.features);
		
		// Update filter window
		if (this.cmp.filterWindow && this.cmp.filterWindow.isVisible()) { 
			this.cmp.filterWindow.filter();
		}
        
        // Set favorite position, else zoom to visible extent
        if (this.model.longitude && this.model.latitude && this.model.zoom) {
			var lonLat = GIS.util.map.getLonLatByXY(this.model.longitude, this.model.latitude);
			GIS.map.setCenter(lonLat, this.model.zoom);
		}
		else if (this.model.updateOrganisationUnit) {
			GIS.util.map.zoomToVisibleExtent();
		}
		
		// Legend
		GIS.cmp.region.east.doLayout();
		
        GIS.mask.hide();
	},
    
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Facility.superclass.onRender.apply(this, arguments);
    }
});
