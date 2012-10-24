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
 * @requires core/GeoStat/Thematic2.js
 * @requires core/Color.js
 */

Ext.define('mapfish.widgets.geostat.Thematic2', {
	extend: 'Ext.panel.Panel',
	alias: 'widget.thematic2',
	
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
    
    toggler: {},
    
    features: [],
    
    selectHandlers: {},
    
    store: {
		indicatorsByGroup: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: '',
				reader: {
					type: 'json',
					root: 'indicators'
				}
			},
			isLoaded: false,
			loadFn: function(fn) {
				if (this.isLoaded) {
					fn.call();
				}
				else {
					this.load(fn);
				}
			},
			listeners: {
				load: function() {
					if (!this.isLoaded) {
						this.isLoaded = true;
					}
					this.sort('name', 'ASC');
				}
			}
		}),
		
		dataElementsByGroup: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: '',
				reader: {
					type: 'json',
					root: 'dataElements'
				}
			},
			isLoaded: false,
			loadFn: function(fn) {
				if (this.isLoaded) {
					fn.call();
				}
				else {
					this.load(fn);
				}
			},
			listeners: {
				load: function() {
					if (!this.isLoaded) {
						this.isLoaded = true;
					}
					this.sort('name', 'ASC');
				}
			}
		}),
		
		periodsByType: Ext.create('Ext.data.Store', {
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
		}),
		
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
		}),

		legendsByLegendSet: Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'startValue', 'endValue', 'color'],
			proxy: {
				type: 'ajax',
				url: '',
				reader: {
					type: 'json',
					root: 'mapLegends'
				}
			},
			isLoaded: false,
			loadFn: function(fn) {
				if (this.isLoaded) {
					fn.call();
				}
				else {
					this.load(fn);
				}
			},
			listeners: {
				load: function() {
					if (!this.isLoaded) {
						this.isLoaded = true;
					}
					this.sort('name', 'ASC');
				}
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
		this.createUtils();
		
		this.createItems();
		
		this.addItems();
		
		this.createSelectHandlers();
		
		this.coreComp = new mapfish.GeoStat.Thematic2(this.map, {
            layer: this.layer,
            format: this.format,
            url: this.url,
            requestSuccess: Ext.bind(this.requestSuccess, this),
            requestFailure: Ext.bind(this.requestFailure, this),
            legendDiv: this.legendDiv,
            labelGenerator: this.labelGenerator,
            widget: this
        });
		
		mapfish.widgets.geostat.Thematic2.superclass.initComponent.apply(this);
    },
    
    createUtils: function() {
		var that = this;
		
		this.toggler.valueType = function(valueType) {
			if (valueType === GIS.conf.finals.dimension.indicator.id) {
				that.cmp.indicatorGroup.show();
				that.cmp.indicator.show();
				that.cmp.dataElementGroup.hide();
				that.cmp.dataElement.hide();
			}
			else if (valueType === GIS.conf.finals.dimension.dataElement.id) {
				that.cmp.indicatorGroup.hide();
				that.cmp.indicator.hide();
				that.cmp.dataElementGroup.show();
				that.cmp.dataElement.show();
			}
		};
		
		this.toggler.legendType = function(legendType) {
			if (legendType === GIS.conf.finals.widget.legendtype_automatic) {
				that.cmp.methodPanel.show();
				that.cmp.lowPanel.show();
				that.cmp.highPanel.show();
				that.cmp.legendSet.hide();
			}
			else if (legendType === GIS.conf.finals.widget.legendtype_predefined) {
				that.cmp.methodPanel.hide();
				that.cmp.lowPanel.hide();
				that.cmp.highPanel.hide();
				that.cmp.legendSet.show();
			}
		};
	},
    
    createItems: function() {
		
		// Data options
		
        this.cmp.valueType = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: 'Value type', //i18n
            editable: false,
            valueField: 'id',
            displayField: 'name',
            queryMode: 'local',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            value: GIS.conf.finals.dimension.indicator.id,
            store: Ext.create('Ext.data.ArrayStore', {
                fields: ['id', 'name'],
                data: [
                    [GIS.conf.finals.dimension.indicator.id, 'Indicator'], //i18n
                    [GIS.conf.finals.dimension.dataElement.id, 'Data element'] //i18n
                ]
            }),
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
						this.config.updateData = true;
						this.toggler.valueType(cb.getValue());
                    }
                }
            }
        });
        
        this.cmp.indicatorGroup = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.indicator_group,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            store: GIS.store.indicatorGroups,
            listeners: {
				added: function() {
					this.store.cmp.push(this);
				},
                select: {
                    scope: this,
                    fn: function(cb) {
						this.config.updateData = true;
                        this.cmp.indicator.clearValue();
                        
                        var store = this.cmp.indicator.store;
                        store.proxy.url = GIS.conf.url.path_api +  'indicatorGroups/' + cb.getValue() + '.json?links=false&paging=false';
                        store.load({
							scope: this,
							callback: function() {
								this.cmp.indicator.selectFirst();
							}
						});
                    }
                }
            }
        });
        
        this.cmp.indicator = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.indicator,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            queryMode: 'local',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            listConfig: {loadMask: false},
            scope: this,
            selectFirst: function() {
				if (this.store.getCount() > 0) {
					this.setValue(this.store.getAt(0).data.id);
				}
				this.scope.config.updateData = true;
			},
            store: this.store.indicatorsByGroup,
            listeners: {
                select: function() {
					this.scope.config.updateData = true;
                }
            }
        });
        
        this.cmp.dataElementGroup = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.dataelement_group,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            hidden: true,
            store: GIS.store.dataElementGroups,
            listeners: {
				added: function() {
					this.store.cmp.push(this);
				},
                select: {
                    scope: this,
                    fn: function(cb) {
                        this.cmp.indicator.clearValue();
                        
                        var store = this.cmp.dataElement.store;
                        store.proxy.url = GIS.conf.url.path_api +  'dataElementGroups/' + cb.getValue() + '.json?links=false&paging=false';
                        store.load({
							scope: this,
							callback: function() {
								this.cmp.dataElement.selectFirst();
							}
						});
                    }
                }
            }
        });
        
        this.cmp.dataElement = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.dataelement,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            queryMode: 'local',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            listConfig: {loadMask: false},
            hidden: true,
            scope: this,
            selectFirst: function() {
				if (this.store.getCount() > 0) {
					this.setValue(this.store.getAt(0).data.id);
				}
				this.scope.config.updateData = true;
			},
            store: this.store.dataElementsByGroup,
            listeners: {
                select: function() {
					this.scope.config.updateData = true;
                }
			}
        });
        
        this.cmp.periodType = Ext.create('Ext.form.field.ComboBox', {
            editable: false,
            valueField: 'id',
            displayField: 'name',
            forceSelection: true,
            queryMode: 'local',
            width: 116,
            store: GIS.store.periodTypes,
			periodOffset: 0,
            listeners: {
                select: {
                    scope: this,
                    fn: function() {
						var pt = new PeriodType(),
							periodType = this.cmp.periodType.getValue();
						
						var periods = pt.get(periodType).generatePeriods({
							offset: this.cmp.periodType.periodOffset,
							filterFuturePeriods: true,
							reversePeriods: true
						});
						
						this.store.periodsByType.setIndex(periods);
						this.store.periodsByType.loadData(periods);
						
                        this.cmp.period.selectFirst();
                    }
                }
            }
        });
        
        this.cmp.period = Ext.create('Ext.form.field.ComboBox', {
			fieldLabel: GIS.i18n.period,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            queryMode: 'local',
            forceSelection: true,
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            store: this.store.periodsByType,
            scope: this,
            selectFirst: function() {
				this.setValue(this.store.getAt(0).data.id);
				this.scope.config.updateData = true;
			},
			listeners: {
				select: {
					scope: this,
					fn: function() {
						this.config.updateData = true;
					}
				}
			}	
        });
        
        this.cmp.periodPrev = Ext.create('Ext.button.Button', {
			xtype: 'button',
			text: '<',
			width: 20,
			style: 'margin-left: 3px',
			scope: this,
			handler: function() {
				if (this.cmp.periodType.getValue()) {
					this.cmp.periodType.periodOffset--;
					this.cmp.periodType.fireEvent('select');
				}
			}
		});
        
        this.cmp.periodNext = Ext.create('Ext.button.Button', {
			xtype: 'button',
			text: '>',
			width: 20,
			style: 'margin-left: 3px',
			scope: this,
			handler: function() {
				if (this.cmp.periodType.getValue() && this.cmp.periodType.periodOffset < 0) {
					this.cmp.periodType.periodOffset++;
					this.cmp.periodType.fireEvent('select');
				}
			}
		});
		
		// Legend options
        
        this.cmp.legendType = Ext.create('Ext.form.field.ComboBox', {
            editable: false,
            valueField: 'id',
            displayField: 'name',
            fieldLabel: GIS.i18n.legend_type,
            value: GIS.conf.finals.widget.legendtype_automatic,
            queryMode: 'local',
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            store: Ext.create('Ext.data.ArrayStore', {
                fields: ['id', 'name'],
                data: [
                    [GIS.conf.finals.widget.legendtype_automatic, GIS.i18n.automatic],
                    [GIS.conf.finals.widget.legendtype_predefined, GIS.i18n.predefined]
                ]
            }),
            listeners: {
                select: {
                    scope: this,
                    fn: function(cb) {
						this.toggler.legendType(cb.getValue());
						
						this.config.updateLegend = true;
                    }
                }
            }
        });
        
        this.cmp.legendSet = Ext.create('Ext.form.field.ComboBox', {
            fieldLabel: GIS.i18n.legendset,
            editable: false,
            valueField: 'id',
            displayField: 'name',
            width: GIS.conf.layout.widget.item_width,
            labelWidth: GIS.conf.layout.widget.itemlabel_width,
            hidden: true,
            store: GIS.store.legendSets,
            listeners: {
				select: {
					scope: this,
					fn: function() {
						this.config.updateLegend = true;
					}
				}
			}
        });
        
        this.cmp.classes = Ext.create('Ext.form.field.Number', {
            editable: false,
            valueField: 'id',
            displayField: 'id',
            queryMode: 'local',
            value: 5,
            minValue: 1,
            maxValue: 7,
            width: 50,
            style: 'margin-right: 3px',
            store: Ext.create('Ext.data.ArrayStore', {
                fields: ['id'],
                data: [[1], [2], [3], [4], [5], [6], [7]]
            }),
            listeners: {
				change: {
					scope: this,
					fn: function() {
						this.config.updateLegend = true;
					}
				}
			}
        });
        
        this.cmp.method = Ext.create('Ext.form.field.ComboBox', {
            editable: false,
            valueField: 'id',
            displayField: 'name',
            queryMode: 'local',
            value: 2,
            width: 109,
            store: Ext.create('Ext.data.ArrayStore', {
                fields: ['id', 'name'],
                data: [
                    [2, 'By class range'],
                    [3, 'By class count'] //i18n
                ]
            }),
            listeners: {
				select: {
					scope: this,
					fn: function() {
						this.config.updateLegend = true;
					}
				}
			}
        });
		
		this.cmp.colorLow = Ext.create('Ext.ux.button.ColorButton', {
			style: 'margin-right: 3px',
			value: 'ff0000',
			scope: this,
			menuHandler: function() {
				this.scope.config.updateLegend = true;
			}
		});
        
        this.cmp.colorHigh = Ext.create('Ext.ux.button.ColorButton', {
			style: 'margin-right: 3px',
			value: '00ff00',
			scope: this,
			menuHandler: function() {
				this.scope.config.updateLegend = true;
			}
		});
        
        this.cmp.radiusLow = Ext.create('Ext.form.field.Number', {
            width: 50,
            allowDecimals: false,
            minValue: 1,
            value: 5,
            listeners: {
				change: {
					scope: this,
					fn: function() {
						this.config.updateLegend = true;
					}
				}
			}
        });
        
        this.cmp.radiusHigh = Ext.create('Ext.form.field.Number', {
            width: 50,
            allowDecimals: false,
            minValue: 1,
            value: 15,
            listeners: {
				change: {
					scope: this,
					fn: function() {
						this.config.updateLegend = true;
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
        
        // Custom panels
        
        this.cmp.periodTypePanel = Ext.create('Ext.panel.Panel', {
			layout: 'hbox',
			items: [
				{
					html: 'Period type:', //i18n
					width: 100,
					bodyStyle: 'color: #444',
					style: 'padding: 3px 0 0 4px'
				},
				this.cmp.periodType,
				this.cmp.periodPrev,
				this.cmp.periodNext
			]
		});			
        
        this.cmp.methodPanel = Ext.create('Ext.panel.Panel', {
			layout: 'hbox',
			items: [
				{
					html: 'Classes / method:', //i18n
					width: 100,
					bodyStyle: 'color: #444',
					style: 'padding: 3px 0 0 4px'
				},
				this.cmp.classes,
				this.cmp.method
			]
		});
        
        this.cmp.lowPanel = Ext.create('Ext.panel.Panel', {
			layout: 'hbox',
			items: [
				{
					html: 'Low color / size:', //i18n
					width: 100,
					bodyStyle: 'color: #444',
					style: 'padding: 3px 0 0 4px'
				},
				this.cmp.colorLow,
				this.cmp.radiusLow
			]
		});
        
        this.cmp.highPanel = Ext.create('Ext.panel.Panel', {
			layout: 'hbox',
			items: [
				{
					html: 'High color / size:', //i18n
					width: 100,
					bodyStyle: 'color: #444',
					style: 'padding: 3px 0 0 4px'
				},
				this.cmp.colorHigh,
				this.cmp.radiusHigh
			]
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
						html: GIS.i18n.data_options,
						cls: 'gis-form-subtitle-first'
					},
					this.cmp.valueType,
					this.cmp.indicatorGroup,
					this.cmp.indicator,
					this.cmp.dataElementGroup,
					this.cmp.dataElement,
					this.cmp.periodTypePanel,
					this.cmp.period,
					{
						html: GIS.i18n.legend_options,
						cls: 'gis-form-subtitle'
					},
					this.cmp.legendType,
					this.cmp.legendSet,
					this.cmp.methodPanel,
					this.cmp.lowPanel,
					this.cmp.highPanel,
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
	
	setPredefinedLegend: function(fn) {
		var store = this.store.legendsByLegendSet,
			colors = [],
			bounds = [],
			names = [],
			legends;
		
		Ext.Ajax.request({
			url: GIS.conf.url.path_api + 'mapLegendSets/' + this.tmpModel.legendSet + '.json?links=false&paging=false',
			scope: this,
			success: function(r) {
				legends = Ext.decode(r.responseText).mapLegends;
				
				for (var i = 0; i < legends.length; i++) {
					if (bounds[bounds.length-1] !== legends[i].startValue) {
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

				this.tmpModel.colorInterpolation = colors;
				this.tmpModel.bounds = bounds;
                this.tmpModel.legendNames = names;

				if (fn) {
					fn.call(this);
				}
			}
		});
	},
	
	getLegendConfig: function() {
		return {
			what: this.tmpModel.valueType === 'indicator' ? this.cmp.indicator.getRawValue() : this.cmp.dataElement.getRawValue(),
			when: this.cmp.period.getRawValue(),
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
		
		// Components
		this.cmp.valueType.reset();
		this.toggler.valueType(GIS.conf.finals.dimension.indicator.id);
		
		this.cmp.indicatorGroup.clearValue();
		this.cmp.indicator.clearValue();
		this.cmp.dataElementGroup.clearValue();
		this.cmp.dataElement.clearValue();
		
		this.cmp.periodType.clearValue();
		this.cmp.period.clearValue();
		
		this.cmp.legendType.reset();
		this.toggler.legendType(GIS.conf.finals.widget.legendtype_automatic);
		this.cmp.legendSet.clearValue();
		this.cmp.classes.reset();
		this.cmp.method.reset();
		this.cmp.colorLow.reset();
		this.cmp.colorHigh.reset();
		this.cmp.radiusLow.reset();
		this.cmp.radiusHigh.reset();
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
		this.config.valueType = config.valueType;
		this.config.indicatorGroup = config.indicatorGroup;
		this.config.indicator = config.indicator;
		this.config.dataElementGroup = config.dataElementGroup;
		this.config.dataElement = config.dataElement;
		this.config.periodType = config.periodType;
		this.config.period = config.period;
		this.config.legendType = config.legendType;
		this.config.legendSet = config.legendSet;
		this.config.classes = config.classes;
		this.config.method = config.method;
		this.config.colorLow = config.colorLow;
		this.config.colorHigh = config.colorHigh;
		this.config.radiusLow = config.radiusLow;
		this.config.radiusHigh = config.radiusHigh;
		this.config.level = config.level;
		this.config.levelName = config.levelName;
		this.config.parentId = config.parentId;
		this.config.parentName = config.parentName;
		this.config.parentLevel = config.parentLevel;
		this.config.parentPath = config.parentPath;
		this.config.updateOrganisationUnit = true;
		this.config.updateData = false;
		this.config.updateLegend = false;
		this.config.updateGui = true;
	},
	
	setGui: function() {
		var model = this.tmpModel,
			that = this;
		
		// Value type
		this.cmp.valueType.setValue(model.valueType);
		
		// Indicator and data element
		this.toggler.valueType(model.valueType);
		
		var	indeGroupView = model.valueType === GIS.conf.finals.dimension.indicator.id ? this.cmp.indicatorGroup : this.cmp.dataElementGroup,
			indeGroupStore = indeGroupView.store,
			indeGroupValue = model.valueType === GIS.conf.finals.dimension.indicator.id ? model.indicatorGroup : model.dataElementGroup,
			
			indeStore = model.valueType === GIS.conf.finals.dimension.indicator.id ? this.store.indicatorsByGroup : this.store.dataElementsByGroup,
			indeView = model.valueType === GIS.conf.finals.dimension.indicator.id ? this.cmp.indicator : this.cmp.dataElement,
			indeValue = model.valueType === GIS.conf.finals.dimension.indicator.id ? model.indicator : model.dataElement;
			
		indeGroupStore.loadFn( function() {
			indeGroupView.setValue(indeGroupValue);
		});
		
		indeStore.proxy.url = GIS.conf.url.path_api + model.valueType + 'Groups/' + indeGroupValue + '.json?links=false&paging=false';
		indeStore.loadFn( function() {
			indeView.setValue(indeValue);
		});
		
		// Period
		this.cmp.periodType.setValue(model.periodType);
		this.cmp.periodType.fireEvent('select');
		this.cmp.period.setValue(model.period);
		
		// Legend
		this.cmp.legendType.setValue(model.legendType);
		this.toggler.legendType(model.legendType);
		
		if (model.legendType === GIS.conf.finals.widget.legendtype_automatic) {
			this.cmp.classes.setValue(model.classes);
			this.cmp.method.setValue(model.method);
			this.cmp.colorLow.setValue(model.colorLow);
			this.cmp.colorHigh.setValue(model.colorHigh);
			this.cmp.radiusLow.setValue(model.radiusLow);
			this.cmp.radiusHigh.setValue(model.radiusHigh);
		}
		else if (model.legendType === GIS.conf.finals.widget.legendtype_predefined) {
			GIS.store.legendSets.loadFn( function() {
				that.cmp.legendSet.setValue(model.legendSet);
			});
		}
		
		// Level and parent
		GIS.store.organisationUnitLevels.loadFn( function() {
			that.cmp.level.setValue(model.level);
		});
		
		this.cmp.parent.selectTreePath('/root' + model.parentPath);
	},
    	
	getModel: function() {
		var level = this.cmp.level,
			parent = this.cmp.parent.getSelectionModel().getSelection();
		parent = parent.length ? parent : [{raw: GIS.init.rootNodes[0]}];
		
		var model = {
			valueType: this.cmp.valueType.getValue(),
			indicatorGroup: this.cmp.indicatorGroup.getValue(),
			indicator: this.cmp.indicator.getValue(),
			dataElementGroup: this.cmp.dataElementGroup.getValue(),
			dataElement: this.cmp.dataElement.getValue(),
			periodType: this.cmp.periodType.getValue(),
			period: this.cmp.period.getValue(),
			legendType: this.cmp.legendType.getValue(),
			legendSet: this.cmp.legendSet.getValue(),
			classes: this.cmp.classes.getValue(),
			method: this.cmp.method.getValue(),
			colorLow: this.cmp.colorLow.getValue(),
			colorHigh: this.cmp.colorHigh.getValue(),
			colors: this.getColors(),
			radiusLow: parseInt(this.cmp.radiusLow.getValue()),
			radiusHigh: parseInt(this.cmp.radiusHigh.getValue()),
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
		
		model.valueType = this.config.valueType || model.valueType;
		model.indicatorGroup = this.config.indicatorGroup || model.indicatorGroup;
		model.indicator = this.config.indicator || model.indicator;
		model.dataElementGroup = this.config.dataElementGroup || model.dataElementGroup;
		model.dataElement = this.config.dataElement || model.dataElement;
		model.periodType = this.config.periodType || model.periodType;
		model.period = this.config.period || model.period;
		model.legendType = this.config.legendType || model.legendType;
		model.legendSet = this.config.legendSet || model.legendSet;
		model.classes = this.config.classes || model.classes;
		model.method = this.config.method || model.method;
		model.colorLow = this.config.colorLow || model.colorLow;
		model.colorHigh = this.config.colorHigh || model.colorHigh;
		model.colors = this.getColors(model.colorLow, model.colorHigh);
		model.radiusLow = this.config.radiusLow || model.radiusLow;
		model.radiusHigh = this.config.radiusHigh || model.radiusHigh;
		model.level = this.config.level || model.level;
		model.levelName = this.config.levelName || model.levelName;
		model.parentId = this.config.parentId || model.parentId;
		model.parentName = this.config.parentName || model.parentName;
		model.parentLevel = this.config.parentLevel || model.parentLevel;
		model.parentPath = this.config.parentPath || null;
		model.updateOrganisationUnit = Ext.isDefined(this.config.updateOrganisationUnit) ? this.config.updateOrganisationUnit : false;
		model.updateData = Ext.isDefined(this.config.updateData) ? this.config.updateData : false;
		model.updateLegend = Ext.isDefined(this.config.updateLegend) ? this.config.updateLegend : false;
		model.updateGui = Ext.isDefined(this.config.updateGui) ? this.config.updateGui : false;
		
		return model;
	},
	
	validateModel: function(model) {
		if (model.valueType === GIS.conf.finals.dimension.indicator.id) {
			if (!model.indicatorGroup || !Ext.isString(model.indicatorGroup)) {
				GIS.logg.push([model.indicatorGroup, this.xtype + '.indicatorGroup: string']);
				//alert("validation failed"); //todo
				return false;
			}
			if (!model.indicator || !Ext.isString(model.indicator)) {
				GIS.logg.push([model.indicator, this.xtype + '.indicator: string']);
				//alert("validation failed"); //todo
				return false;
			}
		}
		else if (model.valueType === GIS.conf.finals.dimension.dataElement.id) {
			if (!model.dataElementGroup || !Ext.isString(model.dataElementGroup)) {
				GIS.logg.push([model.dataElementGroup, this.xtype + '.dataElementGroup: string']);
				//alert("validation failed"); //todo
				return false;
			}
			if (!model.dataElement || !Ext.isString(model.dataElement)) {
				GIS.logg.push([model.dataElement, this.xtype + '.dataElement: string']);
				//alert("validation failed"); //todo
				return false;
			}
		}
		
		if (!model.periodType || !Ext.isString(model.periodType)) {
			GIS.logg.push([model.periodType, this.xtype + '.periodType: string']);
				//alert("validation failed"); //todo
			return false;
		}
		if (!model.period || !Ext.isString(model.period)) {
			GIS.logg.push([model.period, this.xtype + '.period: string']);
				//alert("validation failed"); //todo
			return false;
		}
		
		if (model.legendType === GIS.conf.finals.widget.legendtype_automatic) {
			if (!model.classes || !Ext.isNumber(model.classes)) {
				GIS.logg.push([model.classes, this.xtype + '.classes: number']);
				//alert("validation failed"); //todo
				return false;
			}
			if (!model.method || !Ext.isNumber(model.method)) {
				GIS.logg.push([model.method, this.xtype + '.method: number']);
				//alert("validation failed"); //todo
				return false;
			}
			if (!model.colorLow || !Ext.isString(model.colorLow)) {
				GIS.logg.push([model.colorLow, this.xtype + '.colorLow: string']);
				//alert("validation failed"); //todo
				return false;
			}
			if (!model.radiusLow || !Ext.isNumber(model.radiusLow)) {
				GIS.logg.push([model.radiusLow, this.xtype + '.radiusLow: number']);
				//alert("validation failed"); //todo
				return false;
			}
			if (!model.colorHigh || !Ext.isString(model.colorHigh)) {
				GIS.logg.push([model.colorHigh, this.xtype + '.colorHigh: string']);
				//alert("validation failed"); //todo
				return false;
			}
			if (!model.radiusHigh || !Ext.isNumber(model.radiusHigh)) {
				GIS.logg.push([model.radiusHigh, this.xtype + '.radiusHigh: number']);
				//alert("validation failed"); //todo
				return false;
			}
		}
		else if (model.legendType === GIS.conf.finals.widget.legendtype_predefined) {			
			if (!model.legendSet || !Ext.isString(model.legendSet)) {
				GIS.logg.push([model.legendSet, this.xtype + '.legendSet: string']);
				//alert("validation failed"); //todo
				return false;
			}
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
		
		if (!model.updateOrganisationUnit && !model.updateData && !model.updateLegend) {
			GIS.logg.push([model.updateOrganisationUnit, model.updateData, model.updateLegend, this.xtype + '.update ou/data/legend: true||true||true']);
			return false;
		}
		
		return true;
	},
	
    loadOrganisationUnits: function() {
        var url = GIS.conf.url.path_gis + 'getGeoJson.action?' +
            'parentId=' + this.tmpModel.parentId +
            '&level=' + this.tmpModel.level;
        this.setUrl(url);
    },
    
    loadData: function() {
		var type = this.tmpModel.valueType,
			dataUrl = '../api/mapValues/' + GIS.conf.finals.dimension[type].param + '.json',
			indicator = GIS.conf.finals.dimension.indicator,
			dataElement = GIS.conf.finals.dimension.dataElement,
			period = GIS.conf.finals.dimension.period,
			organisationUnit = GIS.conf.finals.dimension.organisationUnit,
			params = {};
		
		params[type === indicator.id ? indicator.param : dataElement.param] = this.tmpModel[type];
		params[period.param] = this.tmpModel.period;
		params[organisationUnit.param] = this.tmpModel.parentId;
		params.level = this.tmpModel.level;
		
		Ext.Ajax.request({
			url: GIS.conf.url.path_gis + dataUrl,
			params: params,
			disableCaching: false,
			scope: this,
			success: function(r) {
				var values = Ext.decode(r.responseText),
					featureMap = {},
					valueMap = {},
					features = [];
					
				if (values.length === 0) {
					alert("no data"); //todo Ext.message.msg(false, GIS.i18n.current_selection_no_data);
					GIS.mask.hide();
					return;
				}
				
				for (var i = 0; i < this.layer.features.length; i++) {
					var iid = this.layer.features[i].attributes.internalId;
					featureMap[iid] = true;
				}
				for (var i = 0; i < values.length; i++) {
					var iid = values[i].organisationUnitId,
						value = values[i].value;						
					valueMap[iid] = value;
				}
				
				for (var i = 0; i < this.layer.features.length; i++) {
					var feature = this.layer.features[i],
						iid = feature.attributes.internalId;						
					if (featureMap.hasOwnProperty(iid) && valueMap.hasOwnProperty(iid)) {
						feature.attributes.value = valueMap[iid];
						feature.attributes.label = feature.attributes.name + ' (' + feature.attributes.value + ')';
						features.push(feature);
					}
				}
				
				this.layer.features = features;
				
				this.loadLegend();
			}
		});
	},
	
	loadLegend: function() {
		var options,
			that = this,
			
			fn = function() {
				options = {
					indicator: GIS.conf.finals.widget.value,
					method: that.tmpModel.method,
					numClasses: that.tmpModel.classes,
					colors: that.tmpModel.colors,
					minSize: that.tmpModel.radiusLow,
					maxSize: that.tmpModel.radiusHigh
				};

				that.coreComp.applyClassification(options);
				that.classificationApplied = true;
				
				that.afterLoad();
			};
		
		if (this.tmpModel.legendType === GIS.conf.finals.widget.legendtype_predefined) {
			this.setPredefinedLegend(fn);
		}
		else {
			fn();
		}
	},
	
    execute: function() {
		this.tmpModel = this.getModel();
		
		if (!this.validateModel(this.tmpModel)) {
			return;
		}
				
		GIS.mask.msg = GIS.i18n.loading;
		GIS.mask.show();
		
		if (this.tmpModel.updateGui) {
			this.setGui();
		}
		
		if (this.tmpModel.updateOrganisationUnit) {
			this.loadOrganisationUnits();
		}
		else if (this.tmpModel.updateData) {
			this.loadData();
		}
		else {
			this.loadLegend();
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
        mapfish.widgets.geostat.Thematic2.superclass.onRender.apply(this, arguments);
    }
});
