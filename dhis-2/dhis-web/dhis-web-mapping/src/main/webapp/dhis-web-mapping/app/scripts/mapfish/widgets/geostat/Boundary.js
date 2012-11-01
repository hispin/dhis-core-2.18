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
 * @requires core/GeoStat/Boundary.js
 * @requires core/Color.js
 */

Ext.define('mapfish.widgets.geostat.Boundary', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.boundary',
	
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
    
    config: {
		extended: {}
	},
    
    tmpView: {},
    
    view: {},
    
    cmp: {},
    
    features: [],
    
    selectHandlers: {},
    
    store: {		
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
			alert('No valid coordinates found'); //todo //i18n
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
        startColor.setFromHex(low);
        var endColor = new mapfish.ColorRgb();
        endColor.setFromHex(high);
        return [startColor, endColor];
    },
    
    initComponent: function() {		
		this.createItems();
		
		this.addItems();
		
		this.createSelectHandlers();
		
		this.coreComp = new mapfish.GeoStat.Boundary(this.map, {
            layer: this.layer,
            format: this.format,
            url: this.url,
            requestSuccess: Ext.bind(this.requestSuccess, this),
            requestFailure: Ext.bind(this.requestFailure, this),
            legendDiv: this.legendDiv,
            labelGenerator: this.labelGenerator,
            widget: this
        });
		
		mapfish.widgets.geostat.Boundary.superclass.initComponent.apply(this);
    },
    
    createItems: function() {
		        
        // Organisation unit options
        
        this.cmp.level = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.level,
            editable: false,
            valueField: 'id',
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
						this.config.extended.updateOrganisationUnit = true;
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
						html: 'Organisation unit level / parent', //i18n
						cls: 'gis-form-subtitle-first'
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
			
			// Drill or float
			drill = function(direction) {
				var store = GIS.store.organisationUnitLevels;
				
				store.loadFn( function() {
					var store = GIS.store.organisationUnitLevels;
					
					if (direction === 'up') {
						var rootNode = GIS.init.rootNodes[0],
							level = store.getAt(store.find('level', that.view.organisationUnitLevel.level - 1));
						
						that.config.organisationUnitLevel = {
							id: level.data.id,
							name: level.data.name,
							level: level.data.level
						};
						that.config.parentOrganisationUnit = {
							id: rootNode.id,
							name: rootNode.text,
							level: rootNode.level
						};
						that.config.parentGraph = '/' + GIS.init.rootNodes[0].id;
					}
					else if (direction === 'down') {
						var level = store.getAt(store.find('level', that.view.organisationUnitLevel.level + 1));
						
						that.config.organisationUnitLevel = {
							id: level.data.id,
							name: level.data.name,
							level: level.data.level
						};
						that.config.parentOrganisationUnit = {
							id: feature.attributes.id,
							name: feature.attributes.name,
							level: that.view.organisationUnitLevel.level
						};
						that.config.parentGraph = feature.attributes.path;
					}
					
					that.config.extended.updateOrganisationUnit = true;
					that.config.extended.updateGui = true;
					
					that.execute();
				});
			};
			
			// Menu
			var menuItems = [
				Ext.create('Ext.menu.Item', {
					text: 'Float up',
					iconCls: 'gis-menu-item-icon-float',
					disabled: !that.view.extended.hasCoordinatesUp,
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
			where: this.tmpView.levelName + ' / ' + this.tmpView.parentName
		};
	},
	
	reset: function() {
		
		// Components
		this.cmp.level.clearValue();
		this.cmp.parent.reset();
		
		// Layer options
		if (this.cmp.searchWindow) {
			this.cmp.searchWindow.destroy();
		}
		if (this.cmp.labelWindow) {
			this.cmp.labelWindow.destroy();
		}
		
		// View
		this.config = {
			extended: {}
		};
		this.tmpView = {};
		this.view = {};
		
		// Layer
		this.layer.destroyFeatures();
		this.features = this.layer.features.slice(0);
		this.store.features.loadFeatures();		
		this.layer.item.setValue(false);
		
		// Legend
		//document.getElementById(this.legendDiv).innerHTML = '';
	},
	
	setGui: function() {
		var view = this.tmpView,
			that = this;
		
		// Level and parent
		GIS.store.organisationUnitLevels.loadFn( function() {
			that.cmp.level.setValue(view.organisationUnitLevel.id);
		});
		
		this.cmp.parent.selectTreePath('/root' + view.parentGraph);
	},
    	
	getView: function() {
		var level = this.cmp.level,
			parent = this.cmp.parent.getSelectionModel().getSelection(),
			view;
				
		parent = parent.length ? parent : [{raw: GIS.init.rootNodes[0]}];
		
		view = {
			organisationUnitLevel: {
				id: level.getValue(),
				name: level.getRawValue(),
				level: GIS.store.organisationUnitLevels.getById(level.getValue()).data.level
			},
			parentOrganisationUnit: {
				id: parent[0].raw.id,
				name: parent[0].raw.text
			},
			parentLevel: parent[0].raw.level,
			parentGraph: parent[0].raw.path,
			opacity: this.layer.item.getOpacity()
		};
		
		return view;
	},
	
	extendView: function(view) {
		var conf = this.config;
		view = view || {};

		view.organisationUnitLevel = conf.organisationUnitLevel || view.organisationUnitLevel;
		view.parentOrganisationUnit = conf.parentOrganisationUnit || view.parentOrganisationUnit;
		view.parentLevel = conf.parentLevel || view.parentLevel;
		view.parentGraph = conf.parentGraph || view.parentGraph;
		view.opacity = conf.opacity || view.opacity;
		
		view.extended = {
			updateOrganisationUnit: Ext.isDefined(conf.extended.updateOrganisationUnit) ? conf.extended.updateOrganisationUnit : false,
			updateData: Ext.isDefined(conf.extended.updateData) ? conf.extended.updateData : false,
			updateLegend: Ext.isDefined(conf.extended.updateLegend) ? conf.extended.updateLegend : false,
			updateGui: Ext.isDefined(conf.extended.updateGui) ? conf.extended.updateGui : false
		};
		
		return view;
	},
	
	validateView: function(view) {
		if (!view.organisationUnitLevel.id || !Ext.isString(view.organisationUnitLevel.id)) {
			GIS.logg.push([view.organisationUnitLevel.id, this.xtype + '.organisationUnitLevel.id: string']);
				alert('No level selected'); //todo
			return false;
		}
		if (!view.organisationUnitLevel.name || !Ext.isString(view.organisationUnitLevel.name)) {
			GIS.logg.push([view.organisationUnitLevel.name, this.xtype + '.organisationUnitLevel.name: string']);
				//alert("validation failed"); //todo
			return false;
		}
		if (!view.organisationUnitLevel.level || !Ext.isNumber(view.organisationUnitLevel.level)) {
			GIS.logg.push([view.organisationUnitLevel.level, this.xtype + '.organisationUnitLevel.level: number']);
				//alert("validation failed"); //todo
			return false;
		}
		if (!view.parentOrganisationUnit.id || !Ext.isString(view.parentOrganisationUnit.id)) {
			GIS.logg.push([view.parentOrganisationUnit.id, this.xtype + '.parentOrganisationUnit.id: string']);
				alert('No parent organisation unit selected'); //todo
			return false;
		}
		if (!view.parentOrganisationUnit.name || !Ext.isString(view.parentOrganisationUnit.name)) {
			GIS.logg.push([view.parentOrganisationUnit.name, this.xtype + '.parentOrganisationUnit.name: string']);
				//alert("validation failed"); //todo
			return false;
		}
		if (!view.parentLevel || !Ext.isNumber(view.parentLevel)) {
			GIS.logg.push([view.parentLevel, this.xtype + '.parentLevel: number']);
				//alert("validation failed"); //todo
			return false;
		}
		if (!view.parentGraph || !Ext.isString(view.parentGraph)) {
			GIS.logg.push([view.parentGraph, this.xtype + '.parentGraph: string']);
				//alert("validation failed"); //todo
			return false;
		}
		
		if (view.parentOrganisationUnit.level > view.organisationUnitLevel.level) {
			GIS.logg.push([view.parentOrganisationUnit.level, view.organisationUnitLevel.level, this.xtype + '.parentOrganisationUnit.level: number <= ' + this.xtype + '.organisationUnitLevel.level']);
				alert('Orgunit level cannot be higher than parent level'); //todo
			return false;
		}
				
		if (!view.extended.updateOrganisationUnit && !view.extended.updateData && !view.extended.updateLegend) {
			GIS.logg.push([view.extended.updateOrganisationUnit, view.extended.updateData, view.extended.updateLegend, this.xtype + '.extended.update ou/data/legend: true||true||true']);
			return false;
		}
		
		return true;
	},
	
    loadOrganisationUnits: function() {
        var url = GIS.conf.url.path_gis + 'getGeoJson.action?' +
            'parentId=' + this.tmpView.parentOrganisationUnit.id +
            '&level=' + this.tmpView.organisationUnitLevel.id;
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
		var options;
		
		this.tmpView.extended.legendConfig = {
			where: this.tmpView.organisationUnitLevel.name + ' / ' + this.tmpView.parentOrganisationUnit.name
		};
		
		options = {
            indicator: GIS.conf.finals.widget.value,
            method: 2,
            numClasses: 5,
            colors: this.getColors('000000', '000000'),
            minSize: 6,
            maxSize: 6
        };

        this.coreComp.applyClassification(options);
        this.classificationApplied = true;
        
        this.afterLoad();		
	},
	
    execute: function(view) {
		if (view) {
			this.config.extended.updateOrganisationUnit = true;
			this.config.extended.updateGui = true;
		}
		else {
			view = this.getView();
		}
		
		this.tmpView = this.extendView(view);
		
		if (!this.validateView(this.tmpView)) {
			return;
		}
		
		GIS.mask.msg = GIS.i18n.loading;
		GIS.mask.show();
		
		if (this.tmpView.extended.updateGui) {
			this.setGui();
		}
		
		if (this.tmpView.extended.updateOrganisationUnit) {
			this.loadOrganisationUnits();
		}
		else if (this.tmpView.extended.updateData) {
			this.loadData();
		}
		else {
			this.loadLegend();
		}
	},
	
	afterLoad: function() {
		this.view = this.tmpView;
		this.config = {
			extended: {}
		};
		
		// Layer item
		this.layer.item.setValue(true, this.view.opacity);
		
		// Layer menu
		this.menu.enableItems();
		
		// Update search window
		this.store.features.loadFeatures(this.layer.features); 
		
		// Update filter window
		if (this.cmp.filterWindow && this.cmp.filterWindow.isVisible()) { 
			this.cmp.filterWindow.filter();
		}
		
		// Legend
		GIS.cmp.region.east.doLayout();
        
        // Zoom to visible extent if not loading a favorite
        if (!GIS.map.map) {
			GIS.util.map.zoomToVisibleExtent();
		}
		
        GIS.mask.hide();
	},
    
    onRender: function(ct, position) {
        mapfish.widgets.geostat.Boundary.superclass.onRender.apply(this, arguments);
    }
});
