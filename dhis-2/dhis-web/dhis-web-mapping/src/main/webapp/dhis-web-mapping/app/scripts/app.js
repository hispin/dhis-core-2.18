GIS.conf = {
	finals: {
		layer: {
			type_base: 'base',
			type_vector: 'vector'
		},
		dimension: {
			indicator: {
				id: 'indicator',
				param: 'in'
			},
			dataElement: {
				id: 'dataElement',
				param: 'de'
			},
			period: {
				id: 'period',
				param: 'pe'
			},
			organisationUnit: {
				id: 'organisationUnit',
				param: 'ou'
			}
		},
		widget: {
			value: 'value',
			legendtype_automatic: 'automatic',
			legendtype_predefined: 'predefined',
			symbolizer_color: 'color',
			symbolizer_image: 'image'
		},
		openLayers: {
			point_classname: 'OpenLayers.Geometry.Point'
		},
		mapfish: {
			classify_with_bounds: 1,
			classify_by_equal_intervals: 2,
			classify_by_quantils: 3
		}
	},
	url: {
		path_api: '../../api/',
		path_gis: '../',
		path_scripts: 'scripts/'
	},
	layout: {
		widget: {
			item_width: 262,
			itemlabel_width: 95,
			window_width: 290
		},
		tool: {
			item_width: 222,
			itemlabel_width: 95,
			window_width: 250
		},
		grid: {
			row_height: 27
		}
	},
	period: {
		periodTypes: [
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
		]
	},
	opacity: {
		items: [
			{text: '0.1', iconCls: 'gis-menu-item-icon-opacity10'},
			{text: '0.2', iconCls: 'gis-menu-item-icon-opacity20'},
			{text: '0.3', iconCls: 'gis-menu-item-icon-opacity30'},
			{text: '0.4', iconCls: 'gis-menu-item-icon-opacity40'},
			{text: '0.5', iconCls: 'gis-menu-item-icon-opacity50'},
			{text: '0.6', iconCls: 'gis-menu-item-icon-opacity60'},
			{text: '0.7', iconCls: 'gis-menu-item-icon-opacity70'},
			{text: '0.8', iconCls: 'gis-menu-item-icon-opacity80'},
			{text: '0.9', iconCls: 'gis-menu-item-icon-opacity90'},
			{text: '1.0', iconCls: 'gis-menu-item-icon-opacity100'}
		]
	}
};

GIS.init = {};

GIS.mask;

GIS.util = {
	url: {},
	google: {},
	map: {},
	svg: {},
	store: {},
	geojson: {},
	vector: {},
	json: {},
	jsonEncodeString: function(str) {
		return typeof str === 'string' ? str.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'') : str;
	},
	gui: {
		window: {},
		combo: {}
	},
	measure: {}
};

GIS.map;

GIS.base = {
	boundary: {
		id: 'boundary',
		name: 'Boundary layer', //i18n
		legendDiv: 'boundaryLegend'
	},
	thematic1: {
		id: 'thematic1',
		name: 'Thematic 1 layer', //i18n
		legendDiv: 'thematic1Legend'
	},
	thematic2: {
		id: 'thematic2',
		name: 'Thematic 2 layer', //i18n
		legendDiv: 'thematic2Legend'
	},
	facility: {
		id: 'facility',
		name: 'Facility layer', //i18n
		legendDiv: 'facilityLegend'
	},
	symbol: {
		id: 'symbol',
		name: 'Symbol layer', //i18n
		legendDiv: 'symbolLegend'
	},
	googleStreets: {
		id: 'googleStreets',
		name: 'Google Streets'
	},
	googleHybrid: {
		id: 'googleHybrid',
		name: 'Google Hybrid'
	},
	openStreetMap: {
		id: 'openStreetMap',
		name: 'OpenStreetMap'
	}
};

GIS.store = {};

GIS.obj = {};

GIS.cmp = {
	region: {}
};

GIS.gui = {};

GIS.logg = [];

Ext.onReady( function() {
	Ext.Ajax.method = 'GET';
    Ext.QuickTips.init();
    
    Ext.override(Ext.LoadMask, {
		onHide: function() {
			this.callParent();
		}
	});
	
	// Init
	
	GIS.init.onInitialize = function(r) {
		var init = Ext.decode(r.responseText);
			nodes = init.rootNodes;
		
		for (var i = 0; i < nodes.length; i++) {
			var node = init.rootNodes[i];
			node.path = '/root/' + node.id;
		}
		GIS.init.rootNodes = init.rootNodes;
		
		GIS.init.systemSettings = {
			infrastructuralDataElementGroup: init.systemSettings.infrastructuralDataElementGroup,
			infrastructuralPeriodType: init.systemSettings.infrastructuralPeriodType
		};
		
		GIS.init.security = {
			isAdmin: init.security.isAdmin
		};
		
		GIS.init.contextPath = init.contextPath;
	};
	
	Ext.Ajax.request({
		url: GIS.conf.url.path_gis + 'initialize.action',
		success: function(r) {
			GIS.init.onInitialize(r);	
	
	GIS.init.onRender = function() {
		if (!window.google) {
			GIS.base.openStreetMap.layer.item.setValue(true);
		}
	};
	
	GIS.init.afterRender = function() {
		
		// Map tools
		document.getElementsByClassName('zoomInButton')[0].innerHTML = '<img src="images/zoomin_24.png" />';
		document.getElementsByClassName('zoomOutButton')[0].innerHTML = '<img src="images/zoomout_24.png" />';
		document.getElementsByClassName('zoomVisibleButton')[0].innerHTML = '<img src="images/zoomvisible_24.png" />';
		document.getElementsByClassName('measureButton')[0].innerHTML = '<img src="images/measure_24.png" />';
		
		// Map events
		GIS.map.events.register('mousemove', null, function(e) {
			GIS.map.mouseMove.x = e.clientX;
			GIS.map.mouseMove.y = e.clientY;
		});
		                
		GIS.map.events.register('click', null, function(e) {
			if (GIS.map.relocate.active) {
				var el = document.getElementById('mouseposition').childNodes[0],
					coordinates = '[' + el.childNodes[1].data + ',' + el.childNodes[3].data + ']',
					center = GIS.cmp.region.center;

				Ext.Ajax.request({
					url: GIS.conf.url.path_gis + 'updateOrganisationUnitCoordinates.action',
					method: 'POST',
					params: {id: GIS.map.relocate.feature.attributes.id, coordinates: coordinates},
					success: function(r) {
						GIS.map.relocate.active = false;
						GIS.map.relocate.widget.cmp.relocateWindow.destroy();
														
						GIS.map.relocate.feature.move({x: parseFloat(e.clientX - center.x), y: parseFloat(e.clientY - 28)});
						GIS.map.getViewport().style.cursor = 'auto';
						
						console.log(GIS.map.relocate.feature.attributes.name + ' relocated to ' + coordinates);
					}
				});
			}
		});
		
		// Favorite
		
		var id = GIS.util.url.getUrlParam('id');
		if (id) {
			GIS.util.map.getMap(id, true);
		}
	};
	
	// Mask
	
	GIS.mask = new Ext.LoadMask(Ext.getBody(), {
		msg: GIS.i18n.loading
	});
	
	// Util
	
	GIS.util.google.openTerms = function() {
		window.open('http://www.google.com/intl/en-US_US/help/terms_maps.html', '_blank');
	};
	
	GIS.util.url.getUrlParam = function(s) {
		var output = '';
		var href = window.location.href;
		if (href.indexOf('?') > -1 ) {
			var query = href.substr(href.indexOf('?') + 1);
			var query = query.split('&');
			for (var i = 0; i < query.length; i++) {
				if (query[i].indexOf('=') > -1) {
					var a = query[i].split('=');
					if (a[0].toLowerCase() === s) {
						output = a[1];
						break;
					}
				}
			}
		}
		return unescape(output);
	};
	
	GIS.util.map.getVisibleVectorLayers = function() {
		var layers = [],
			layer;
			
		for (var i = 0; i < GIS.map.layers.length; i++) {
			layer = GIS.map.layers[i];
			if (layer.layerType === GIS.conf.finals.layer.type_vector &&
				layer.visibility &&
				layer.features.length) {
				layers.push(layer);
			}
		}
		return layers;
	};
	
	GIS.util.map.hasVisibleFeatures = function() {
		var layers = GIS.util.map.getVisibleVectorLayers(),
			layer;
		
		if (layers.length) {
			for (var i = 0; i < layers.length; i++) {
				layer = layers[i];
				if (layer.features.length) {
					return true;
				}
			}
		}
		
		return false;
	};
	
    GIS.util.map.getLayersByType = function(layerType) {
		var layers = [];
		for (var i = 0; i < GIS.map.layers.length; i++) {
			var layer = GIS.map.layers[i];
			if (layer.layerType === layerType) {
				layers.push(layer);
			}
		}
		return layers;
	};
	
	GIS.util.map.getExtendedBounds = function(layers) {
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
	
	GIS.util.map.zoomToVisibleExtent = function() {
		var bounds = GIS.util.map.getExtendedBounds(GIS.util.map.getVisibleVectorLayers());
		if (bounds) {
			GIS.map.zoomToExtent(bounds);
		}
	};
	
	GIS.util.map.addMapControl = function(name, fn) {
		var panel = new GIS.obj.MapControlPanel(name, fn);		
		GIS.map.addControl(panel);
		panel.div.className += ' ' + name;
		panel.div.childNodes[0].className += ' ' + name + 'Button';
	};
	
	GIS.util.map.getTransformedPointByXY = function(x, y) {
		var p = new OpenLayers.Geometry.Point(parseFloat(x), parseFloat(y));
        return p.transform(new OpenLayers.Projection("EPSG:4326"), new OpenLayers.Projection("EPSG:900913"));
    };
    
    GIS.util.map.getLonLatByXY = function(x, y) {
		var point = GIS.util.map.getTransformedPointByXY(x, y);		
		return new OpenLayers.LonLat(point.x, point.y);
	};
	
	GIS.util.map.getMap = function(id, setMap) {
		if (!id) {
			alert('No favorite id provided');
			return;
		}
		if (!Ext.isString(id)) {
			alert('Favorite id must be a string');
			return;
		}
		
		Ext.Ajax.request({
			url: GIS.conf.url.path_api + 'maps/' + id + '.json?links=false',
			success: function(r) {
				var map = Ext.decode(r.responseText);
				
				if (setMap) {
					GIS.util.map.setMap(map);
				}
			}
		});
	};
	
	GIS.util.map.setMap = function(map) {
		var views = map.mapViews,
			view,
			center,
			lonLat;
			
		GIS.map.map = map;
			
		GIS.util.map.closeAllLayers();
		
		for (var i = 0; i < views.length; i++) {
			view = views[i];
			
			GIS.base[view.layer].widget.execute(view);
		}
		
		lonLat = new OpenLayers.LonLat(map.longitude, map.latitude);
        GIS.map.setCenter(lonLat, map.zoom);
	};
	
	GIS.util.map.closeAllLayers = function() {
		GIS.base.boundary.widget.reset();
		GIS.base.thematic1.widget.reset();
		GIS.base.thematic2.widget.reset();
		GIS.base.facility.widget.reset();
	};
	
	GIS.util.svg.merge = function(str, strArray) {
        if (strArray.length) {
            str = str || '<svg></svg>';
            for (var i = 0; i < strArray.length; i++) {
                str = str.replace('</svg>', '');
                strArray[i] = strArray[i].substring(strArray[i].indexOf('>') + 1);
                str += strArray[i];
            }
        }
        return str;
    };
    
	GIS.util.svg.getString = function(title, layers) {
		var svgArray = [],
			svg = '',
			namespace,
			titleSVG,
			legendSVG = '',
			x = 20,
			y = 35,
			center = GIS.cmp.region.center;
		
		if (!layers.length) {
			return false;
		}
					
		namespace = 'xmlns="http://www.w3.org/2000/svg"';
					
		svg = '<svg ' + namespace + ' width="' + center.getWidth() + '" height="' + center.getHeight() + '"></svg>';
		
		titleSVG = '<g id="title" style="display: block; visibility: visible;">' +
				   '<text id="title" x="' + x + '" y="' + y + '" font-size="18" font-weight="bold">' +
				   '<tspan>' + title + '</tspan></text></g>';
		
		y += 35;
		
		for (var i = layers.length - 1; i > 0; i--) {
			if (layers[i].base.id === GIS.base.facility.id) {
				layers.splice(i, 1);
				console.log('Facility layer export currently not supported');
			}
		}
		
		for (var i = 0; i < layers.length; i++) {
			var layer = layers[i],
				id = layer.base.id,
				legendConfig = layer.base.widget.getLegendConfig(),
				imageLegendConfig = layer.base.widget.view.extended.imageLegendConfig,
				what,
				when,
				where,
				legend;
				
			// SVG
			svgArray.push(layer.div.innerHTML);
			
			// Legend
			if (id !== GIS.base.boundary.id && id !== GIS.base.facility.id) {
				what = '<g id="indicator" style="display: block; visibility: visible;">' +
					   '<text id="indicator" x="' + x + '" y="' + y + '" font-size="12">' +
					   '<tspan>' + legendConfig.what + '</tspan></text></g>';
				
				y += 15;
				
				when = '<g id="period" style="display: block; visibility: visible;">' +
					   '<text id="period" x="' + x + '" y="' + y + '" font-size="12">' +
					   '<tspan>' + legendConfig.when + '</tspan></text></g>';
				
				y += 15;
				
				where = '<g id="period" style="display: block; visibility: visible;">' +
					   '<text id="period" x="' + x + '" y="' + y + '" font-size="12">' +
					   '<tspan>' + legendConfig.where + '</tspan></text></g>';
				
				y += 8;
				
				legend = '<g>';
				
				for (var j = 0; j < imageLegendConfig.length; j++) {
					if (j !== 0) {
						y += 15;
					}
					
					legend += '<rect x="' + x + '" y="' + y + '" height="15" width="30" ' +
							  'fill="' + imageLegendConfig[j].color + '" stroke="#000000" stroke-width="1"/>';
							  
					legend += '<text id="label" x="' + (x + 40) + '" y="' + (y + 12) + '" font-size="12">' +
							  '<tspan>' + imageLegendConfig[j].label + '</tspan></text>';
				}
				
				legend += '</g>';
				
				legendSVG += (what + when + where + legend);
				
				y += 50;
			}
		}
		
		if (svgArray.length) {
			svg = GIS.util.svg.merge(svg, svgArray);
		}
		
		svg = svg.replace('</svg>', (titleSVG + legendSVG) + '</svg>');
		
		return svg;
	};
	
	GIS.util.geojson.decode = function(doc, widget) {
		var geojson = {};
        doc = Ext.decode(doc);
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
                    hcwc: doc.geojson[i].hc,
                    path: doc.geojson[i].path,
                    parentId: doc.geojson[i].pi,
                    parentName: doc.geojson[i].pn
                }
            });
        }
        
        if (widget) {
			widget.tmpView.extended.hasCoordinatesUp = doc.properties.hasCoordinatesUp;
		}
			
        return geojson;
    };
    
    GIS.util.json.decodeAggregatedValues = function(responseText) {
		responseText = Ext.decode(responseText);
		var values = [];
		
        for (var i = 0; i < responseText.length; i++) {
            values.push({
                oi: responseText[i][0],
                v: responseText[i][1]
            });
        }
        return values;        
    };
    
    GIS.util.vector.getTransformedFeatureArray = function(features) {
        var sourceProjection = new OpenLayers.Projection("EPSG:4326"),
			destinationProjection = new OpenLayers.Projection("EPSG:900913");
        for (var i = 0; i < features.length; i++) {
            features[i].geometry.transform(sourceProjection, destinationProjection);
        }
        return features;
    };
    
    GIS.util.gui.window.setPositionTopRight = function(window) {		
		var center = GIS.cmp.region.center;				
		window.setPosition(GIS.gui.viewport.width - (window.width + 7), center.y + 8);
	};
	
	GIS.util.gui.window.setPositionTopLeft = function(window) {
		window.setPosition(4,35);
	};
	
	GIS.util.gui.combo.setQueryMode = function(cmpArray, mode) {
		for (var i = 0; i < cmpArray.length; i++) {
			cmpArray[i].queryMode = mode;
		}
	};
    
    // Stores
    
    GIS.store.indicatorGroups = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_api + 'indicatorGroups.json?links=false&paging=false',
			reader: {
				type: 'json',
				root: 'indicatorGroups'
			}
		},
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
		listeners: {
			load: function() {
				if (!this.isLoaded) {
					this.isLoaded = true;
					GIS.util.gui.combo.setQueryMode(this.cmp, 'local');
				}
				this.sort('name', 'ASC');
			}
		}
	});
    
    GIS.store.dataElementGroups = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_api + 'dataElementGroups.json?links=false&paging=false',
			reader: {
				type: 'json',
				root: 'dataElementGroups'
			}
		},
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
		listeners: {
			load: function() {
				if (!this.isLoaded) {
					this.isLoaded = true;
					GIS.util.gui.combo.setQueryMode(this.cmp, 'local');
				}
				this.sort('name', 'ASC');
			}
		}
	});
	
	GIS.store.periodTypes = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		data: GIS.conf.period.periodTypes
	});
    
    GIS.store.organisationUnitLevels = Ext.create('Ext.data.Store', {
		fields: ['id', 'name', 'level'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_api + 'organisationUnitLevels.json?viewClass=detailed&links=false&paging=false',
			reader: {
				type: 'json',
				root: 'organisationUnitLevels'
			}
		},
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
					GIS.util.gui.combo.setQueryMode(this.cmp, 'local');
				}
				this.sort('level', 'ASC');
			}
		}
	});
        
    GIS.store.infrastructuralPeriodsByType = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_gis + 'getPeriodsByPeriodType.action',
			reader: {
				type: 'json',
				root: 'periods'
			},
			extraParams: {
				name: GIS.init.systemSettings.infrastructuralPeriodType
			}
		},
        autoLoad: false,
        isLoaded: false,
        listeners: {
			load: function() {
				if (!this.isLoaded) {
					this.isLoaded = true;
				}
			}
        }
    });    
    
    GIS.store.groupSets = Ext.create('Ext.data.Store', {
        fields: ['id', 'name'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_api + 'organisationUnitGroupSets.json?paging=false&links=false',
			reader: {
				type: 'json',
				root: 'organisationUnitGroupSets'
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
    });
    
	GIS.store.groupsByGroupSet = Ext.create('Ext.data.Store', {
		fields: ['id', 'name', 'symbol'],
		proxy: {
			type: 'ajax',
			url: '',
			reader: {
				type: 'json',
				root: 'organisationUnitGroups'
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
	});
	
	GIS.store.legendSets = Ext.create('Ext.data.Store', {
		fields: ['id', 'name'],
		proxy: {
			type: 'ajax',
			url: GIS.conf.url.path_api + 'mapLegendSets.json?links=false&paging=false',
			reader: {
				type: 'json',
				root: 'mapLegendSets'
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
	});
	
	GIS.store.maps = Ext.create('Ext.data.Store', {
		fields: ['id', 'name', 'lastUpdated', 'user'],
		proxy: {
			type: 'ajax',
			reader: {
				type: 'json',
				root: 'maps'
			}
		},
		isLoaded: false,
		pageSize: 10,
		page: 1,
		defaultUrl: GIS.conf.url.path_api + 'maps.json?viewClass=detailed&links=false',
		loadStore: function(url) {
			this.proxy.url = url || this.defaultUrl;
			
			this.load({
				params: {
					pageSize: this.pageSize,
					page: this.page
				}
			});
		},
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
	});
	
    // Objects
    
    GIS.obj.StyleMap = function(base, labelConfig) {
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
			
		if (base.id === GIS.base.boundary.id) {
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
			'default': new OpenLayers.Style(
				OpenLayers.Util.applyDefaults(defaults),
				OpenLayers.Feature.Vector.style['default']),
			select: new OpenLayers.Style(select)
		});
	};
    
    GIS.obj.VectorLayer = function(base, config) {
		var layer = new OpenLayers.Layer.Vector(base.name, {
			strategies: [
				new OpenLayers.Strategy.Refresh({
					force:true
				})
			],
			styleMap: GIS.obj.StyleMap(base),
			visibility: false,
			displayInLayerSwitcher: false,
			layerType: GIS.conf.finals.layer.type_vector,
			layerOpacity: config ? config.opacity || 1 : 1,
			setLayerOpacity: function(number) {
				if (number) {
					this.layerOpacity = parseFloat(number);
				}
				this.setOpacity(this.layerOpacity);
			},
			hasLabels: false
			
		});
		layer.base = base;
		
		return layer;
	};
    
    GIS.obj.LayerMenu = function(base, cls) {
		var layer = base.layer,
			items = [],
			item;
		
		item = {
			text: 'Edit layer..', //i18n
			iconCls: 'gis-menu-item-icon-edit',
			cls: 'gis-menu-item-first',
			alwaysEnabled: true,
			handler: function() {
				GIS.base[base.id].window.show();
			}
		};
		items.push(item);
		
		items.push({
			xtype: 'menuseparator',
			alwaysEnabled: true
		});
		
		item = {
			text: 'Labels..', //i18n
			iconCls: 'gis-menu-item-icon-labels',
			handler: function() {
				if (base.widget.cmp.labelWindow) {
					base.widget.cmp.labelWindow.show();
				}
				else {
					base.widget.cmp.labelWindow = new GIS.obj.LabelWindow(base);
					base.widget.cmp.labelWindow.show();
				}
			}
		};
		items.push(item);
		
		if (base.id !== GIS.base.boundary.id) {
			item = {
				text: 'Filter..', //i18n
				iconCls: 'gis-menu-item-icon-filter',
				handler: function() {
					if (base.widget.cmp.filterWindow) {
						if (base.widget.cmp.filterWindow.isVisible()) {
							return;
						}
						else {
							base.widget.cmp.filterWindow.destroy();
						}
					}
				
					base.widget.cmp.filterWindow = new GIS.obj.FilterWindow(base);
					base.widget.cmp.filterWindow.show();
				}
			};
			items.push(item);
		}
		
		item = {
			text: 'Search..', //i18n
			iconCls: 'gis-menu-item-icon-search',
			handler: function() {
				if (base.widget.cmp.searchWindow) {
					if (base.widget.cmp.searchWindow.isVisible()) {
						return;
					}
					else {
						base.widget.cmp.searchWindow.destroy();
					}
				}
			
				base.widget.cmp.searchWindow = new GIS.obj.SearchWindow(base);
				base.widget.cmp.searchWindow.show();
			}
		};
		items.push(item);
		
		items.push({
			xtype: 'menuseparator',
			alwaysEnabled: true
		});
		
		item = {
			text: 'Close', //i18n
			iconCls: 'gis-menu-item-icon-clear',
			handler: function() {
				GIS.cmp.interpretationButton.disable();
				
				base.widget.reset();
			}
		};
		items.push(item);		
			
		return Ext.create('Ext.menu.Menu', {
			shadow: false,
			showSeparator: false,
			enableItems: function() {
				Ext.each(this.items.items, function(item) {
					item.enable();
				});
			},
			disableItems: function() {
				Ext.Array.each(this.items.items, function(item) {
					if (!item.alwaysEnabled) {
						item.disable();
					}
				});
			},
			items: items,
			listeners: {
				afterrender: function() {
					this.getEl().addCls('gis-toolbar-btn-menu');
					if (cls) {
						this.getEl().addCls(cls);
					}
				},
				show: function() {
					if (base.layer.features.length) {
						this.enableItems();
					}
					else {
						this.disableItems();
					}
					
					this.doLayout(); // show menu bug workaround
				}
			}
		});
	};
	
	GIS.obj.LayersPanel = function() {
		var layers = GIS.map.layers,
			layer,
			items = [],
			item,
			panel,
			visibleLayer;
			
		visibleLayerId = window.google ? GIS.base.googleStreets.id : GIS.base.openStreetMap.id;
		
		for (var i = 0; i < layers.length; i++) {
			layer = layers[i];
			
			item = Ext.create('Ext.ux.panel.LayerItemPanel', {
				cls: 'gis-container-inner',
				height: 23,
				layer: layer,
				text: layer.base.name,
				imageUrl: 'images/' + layer.base.id + '_14.png',
				value: layer.base.id === visibleLayerId ? true : false,
				opacity: layer.layerOpacity,
				numberFieldDisabled: layer.base.id !== visibleLayerId
			});
			layer.item = item;
			items.push(layer.item);
		}
        
        panel = Ext.create('Ext.panel.Panel', {
			renderTo: 'layerItems',
			layout: 'fit',
			cls: 'gis-container-inner',
			items: {
				cls: 'gis-container-inner',
				items: items
			}
		});
		
		return panel;
	};
	
	GIS.obj.WidgetWindow = function(base) {
		return Ext.create('Ext.window.Window', {
			autoShow: true,
			title: base.name,
			layout: 'fit',
			iconCls: 'gis-window-title-icon-' + base.id,
			cls: 'gis-container-default',
			closeAction: 'hide',
			width: GIS.conf.layout.widget.window_width,
			resizable: false,
			isRendered: false,
			items: base.widget,
			bbar: [
				'->',
				{
					text: 'Update', //i18n
					handler: function() {
						GIS.map.mapLoader = null;
						GIS.cmp.interpretationButton.disable();
						
						base.widget.execute();						
					}
				}
			],
			listeners: {
				show: function() {
					if (!this.isRendered) {
						this.isRendered = true;
						this.hide();
					}
					else {
						GIS.util.gui.window.setPositionTopLeft(this);
					}
				}
			}
		});
	};
	
	GIS.obj.SearchWindow = function(base) {
		var layer = base.layer,
			data = [],
			store = base.widget.store.features,
			button,
			window;
			
		for (var i = 0; i < layer.features.length; i++) {
			data.push([layer.features[i].data.id, layer.features[i].data.name]);
		}
		
		if (!data.length) {
			GIS.logg.push([data, base.widget.xtype + '.search.data: feature ids/names']);
			alert('Layer has no organisation units'); //todo
			return;
		}
		
		button = Ext.create('Ext.ux.button.ColorButton', {
			width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width,
			value: '0000ff'
		});
		
		window = Ext.create('Ext.window.Window', {
			title: GIS.i18n.organisationunit_search,
			layout: 'fit',
			iconCls: 'gis-window-title-icon-search',
			cls: 'gis-container-default',
			width: GIS.conf.layout.tool.window_width,
			resizable: false,
			height: 400,
			items: [
				{
					cls: 'gis-container-inner',
					items: [
						{
							layout: 'column',
							cls: 'gis-container-inner',
							items: [
								{
									cls: 'gis-panel-html-label',
									html: GIS.i18n.highlight_color + ':',
									width: GIS.conf.layout.tool.itemlabel_width
								},
								button
							]
						},
						{
							cls: 'gis-panel-html-separator'
						},
						{
							layout: 'column',
							cls: 'gis-container-inner',
							items: [
								{
									cls: 'gis-panel-html-label',
									html: GIS.i18n.text_filter + ':',
									width: GIS.conf.layout.tool.itemlabel_width
								},								
								{
									xtype: 'textfield',
									cls: 'gis-textfield',
									width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width,
									enableKeyEvents: true,
									listeners: {
										keyup: function() {
											store.clearFilter();
											if (this.getValue()) {
												store.filter('name', this.getValue());
											}
											store.sortStore();
										}
									}
								}
							]
						},
						{
							xtype: 'grid',
							cls: 'gis-grid',
							height: 290,
							width: GIS.conf.layout.tool.item_width,
							scroll: 'vertical',
							hideHeaders: true,
							columns: [{
								id: 'name',
								text: 'Organisation units',
								dataIndex: 'name',
								sortable: false,
								width: GIS.conf.layout.tool.item_width
							}],
							store: base.widget.store.features,
							listeners: {
								select: function(grid, record) {
									var feature = layer.getFeaturesByAttribute('id', record.data.id)[0],
										color = button.getValue(),
										symbolizer;
									
									layer.redraw();
									
									if (feature.geometry.CLASS_NAME === GIS.conf.finals.openLayers.point_classname) {
										symbolizer = new OpenLayers.Symbolizer.Point({
											pointRadius: 6,
											fillColor: '#' + color,
											strokeWidth: 1
										});
									}
									else {
										symbolizer = new OpenLayers.Symbolizer.Polygon({
											strokeColor: '#' + color,
											fillColor: '#' + color
										});
									}

									layer.drawFeature(feature, symbolizer);
								}
							}
						}
					]
				}
			],
			listeners: {
				render: function() {
					GIS.util.gui.window.setPositionTopLeft(this);
					store.sortStore();
				},
				destroy: function() {
					layer.redraw();
				}
			}
		});
		
		return window;
	};
	
	GIS.obj.FilterWindow = function(base) {
		var layer = base.layer,
			lowerNumberField,
			greaterNumberField,
			lt,
			gt,
			filter,
			window;
		
		greaterNumberField = Ext.create('Ext.form.field.Number', {
			width: GIS.conf.layout.tool.itemlabel_width,
			value: parseInt(base.widget.coreComp.minVal),
			listeners: {
				change: function() {
					gt = this.getValue();
				}
			}
		});
		
		lowerNumberField = Ext.create('Ext.form.field.Number', {
			width: GIS.conf.layout.tool.itemlabel_width,
			value: parseInt(base.widget.coreComp.maxVal) + 1,
			listeners: {
				change: function() {
					lt = this.getValue();
				}
			}
		});
		
        filter = function() {
			var cache = base.widget.features.slice(0),
				features = [];
				
            if (!gt && !lt) {
                features = cache;
            }
            else if (gt && lt) {
                for (var i = 0; i < cache.length; i++) {
                    if (gt < lt && (cache[i].attributes.value > gt && cache[i].attributes.value < lt)) {
                        features.push(cache[i]);
                    }
                    else if (gt > lt && (cache[i].attributes.value > gt || cache[i].attributes.value < lt)) {
                        features.push(cache[i]);
                    }
                    else if (gt === lt && cache[i].attributes.value === gt) {
                        features.push(cache[i]);
                    }
                }
            }
            else if (gt && !lt) {
                for (var i = 0; i < cache.length; i++) {
                    if (cache[i].attributes.value > gt) {
                        features.push(cache[i]);
                    }
                }
            }
            else if (!gt && lt) {
                for (var i = 0; i < cache.length; i++) {
                    if (cache[i].attributes.value < lt) {
                        features.push(cache[i]);
                    }
                }
            }
            
            layer.removeAllFeatures();
            layer.addFeatures(features);
            
            base.widget.store.features.loadFeatures(layer.features);
        };
		
		window = Ext.create('Ext.window.Window', {
			title: 'Filter by value',
			iconCls: 'gis-window-title-icon-filter',
			cls: 'gis-container-default',
			width: GIS.conf.layout.tool.window_width,
			resizable: false,
			filter: filter,
			items: {
				layout: 'fit',
				cls: 'gis-container-inner',
				items: [
					{
						cls: 'gis-container-inner',
						html: 'Show organisation units with values..'
					},
					{
						cls: 'gis-panel-html-separator'
					},
					{
						cls: 'gis-panel-html-separator'
					},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: 'Greater than:',
								width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width
							},
							greaterNumberField
						]
					},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: 'And/or lower than:',
								width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width
							},
							lowerNumberField
						]
					}
				]
			},
			bbar: [
				'->',
				{
					xtype: 'button',
					text: GIS.i18n.update,
					handler: function() {
						filter();
					}
				}
			],
			listeners: {
				render: function() {
					GIS.util.gui.window.setPositionTopLeft(this);
				},				
				destroy: function() {
					layer.removeAllFeatures();
					layer.addFeatures(base.widget.features);
					base.widget.store.features.loadFeatures(layer.features);
				}
			}
		});
		
		return window;
	};
	
	GIS.obj.LabelWindow = function(base) {
		var layer = base.layer,
			fontSize,
			strong,
			italic,
			color,
			getValues,
			updateLabels,
			window;
			
		fontSize = Ext.create('Ext.form.field.Number', {
			width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width,
			allowDecimals: false,
			minValue: 8,
			value: 13,
			emptyText: 13,
			listeners: {
				change: function() {
					updateLabels();
				}
			}
		});
		
		strong = Ext.create('Ext.form.field.Checkbox', {
			listeners: {
				change: function() {
					updateLabels();
				}
			}
		});
		
		italic = Ext.create('Ext.form.field.Checkbox', {
			listeners: {
				change: function() {
					updateLabels();
				}
			}
		});
		
		button = Ext.create('Ext.ux.button.ColorButton', {
			width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width,
			value: '0000ff'
		});
		
		color = Ext.create('Ext.ux.button.ColorButton', {
			width: GIS.conf.layout.tool.item_width - GIS.conf.layout.tool.itemlabel_width,
			value: '000000',
			menuHandler: function() {
				updateLabels();
			}
		});
		
		getLabelConfig = function() {
			return {
				fontSize: fontSize.getValue(),
				strong: strong.getValue(),
				italic: italic.getValue(),
				color: color.getValue()
			};
		};		
		
		updateLabels = function() {
			if (layer.hasLabels) {
				layer.styleMap = GIS.obj.StyleMap(base, getLabelConfig());				
				base.widget.config.extended.updateLegend = true;
				base.widget.execute();
			}
		};
		
		window = Ext.create('Ext.window.Window', {
			title: GIS.i18n.labels,
			iconCls: 'gis-window-title-icon-labels',
			cls: 'gis-container-default',
			width: GIS.conf.layout.tool.window_width,
			resizable: false,
			closeAction: 'hide',
			items: {
				layout: 'fit',
				cls: 'gis-container-inner',
				items: [
					//{
						//layout: 'column',
						//cls: 'gis-container-inner',
						//items: [
							//{
								//cls: 'gis-panel-html-label',
								//html: GIS.i18n.font_size,
								//width: GIS.conf.layout.tool.itemlabel_width
							//},
							//fontSize
						//]
					//},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: '<b>' + GIS.i18n.bold_ + '</b>:',
								width: GIS.conf.layout.tool.itemlabel_width
							},
							strong
						]
					},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: '<i>' + GIS.i18n.italic + '</i>:',
								width: GIS.conf.layout.tool.itemlabel_width
							},
							italic
						]
					},
					{
						layout: 'column',
						cls: 'gis-container-inner',
						items: [
							{
								cls: 'gis-panel-html-label',
								html: 'Color:', //i18n
								width: GIS.conf.layout.tool.itemlabel_width
							},
							color
						]
					}
				]
			},
			bbar: [
				'->',
				{
					xtype: 'button',
					text: 'Show / hide', //i18n
					handler: function() {
						if (layer.hasLabels) {
							layer.hasLabels = false;
							layer.styleMap = GIS.obj.StyleMap(base);
						}
						else {
							layer.hasLabels = true;
							layer.styleMap = GIS.obj.StyleMap(base, getLabelConfig());
						}
						
						base.widget.config.extended.updateLegend = true;
						base.widget.execute();
					}
				}
			],
			listeners: {
				render: function() {
					GIS.util.gui.window.setPositionTopLeft(this);
				}
			}
		});
		
		return window;
	};
    
    GIS.obj.MapControlPanel = function(name, fn) {
		var button,
			panel;
			
		button = new OpenLayers.Control.Button({
			displayClass: 'olControlButton',
			trigger: function() {
				fn.call(GIS.map);
			}
		});
		
		panel = new OpenLayers.Control.Panel({
			defaultControl: button
		});
		
		panel.addControls([button]);
		
		return panel;
	};
	
	GIS.obj.MapWindow = function() {
		
		// Objects
		var NameWindow,
		
		// Instances
			nameWindow,
			
		// Components
			addButton,
			searchTextfield,
			grid,
			prevButton,
			nextButton,
			tbar,
			bbar,
			info,
			
			nameTextfield,
			systemCheckbox,
			createButton,
			updateButton,
			cancelButton,
			
			mapWindow;
		
		GIS.store.maps.on('load', function(store, records) {
			info.setText(records.length + ' favorite' + (records.length !== 1 ? 's' : '') + ' available');
		});
			
		NameWindow = function(id) {
			var window;
			
			nameTextfield = Ext.create('Ext.form.field.Text', {
				height: 26,
				width: 300,
				labelWidth: 70,
				fieldStyle: 'padding-left: 6px; border-radius: 1px; border-color: #bbb',
				fieldLabel: 'Name', //i18n
				value: id ? GIS.store.maps.getById(id).data.name : '',
				listeners: {
					afterrender: function() {
						this.focus();
					}
				}
			});
			
			systemCheckbox = Ext.create('Ext.form.field.Checkbox', {
				labelWidth: 70,
				fieldLabel: 'System', //i18n
				style: 'margin-bottom: 0',
				disabled: !GIS.init.security.isAdmin,
				value: id ? GIS.store.maps.getById(id).data.system : false
			});
			
			createButton = Ext.create('Ext.button.Button', {
				text: 'Create', //i18n
				handler: function() {
					var name = nameTextfield.getValue(),
						system = systemCheckbox.getValue(),
						layers = GIS.util.map.getVisibleVectorLayers(),
						layer,
						lonlat = GIS.map.getCenter(),
						views = [],
						view,
						map;
						
					if (layers.length) {
						if (name) {
							for (var i = 0; i < layers.length; i++) {
								layer = layers[i];
								view = layer.base.widget.getView();
								
								// add
								view.layer = layer.base.id;
								
								// remove
								delete view.periodType;
								views.push(view);
							}
							
							map = {
								name: name,
								longitude: lonlat.lon,
								latitude: lonlat.lat,
								zoom: GIS.map.getZoom(),
								mapViews: views
							};
							
							if (!system) {
								map.user = {
									id: 'currentUser'
								};
							}
						
							Ext.Ajax.request({
								url: GIS.conf.url.path_api + 'maps/',
								method: 'POST',
								headers: {'Content-Type': 'application/json'},
								params: Ext.encode(map),
								success: function() {								
									GIS.store.maps.loadStore();
									
									GIS.cmp.interpretationButton.enable();
									
									window.destroy();
								}
							});
						}
						else {
							alert('Please enter a name');
						}
					}
					else {
						alert('No layers to save');
					}
				}
			});
			
			updateButton = Ext.create('Ext.button.Button', {
				text: 'Update', //i18n
				handler: function() {
					var name = nameTextfield.getValue(),
						system = systemCheckbox.getValue();
					
					Ext.Ajax.request({
						url: GIS.conf.url.path_gis + 'renameMap.action?id=' + id + '&name=' + name + '&user=' + !system,
						success: function() {								
							GIS.store.maps.loadStore();
							
							window.destroy();
						}
					});
				}
			});
			
			cancelButton = Ext.create('Ext.button.Button', {
				text: 'Cancel', //i18n
				handler: function() {
					window.destroy();
				}
			});
			
			window = Ext.create('Ext.window.Window', {
				title: id ? 'Edit favorite' : 'Create new favorite',
				iconCls: 'gis-window-title-icon-favorite',
				cls: 'gis-container-default',
				resizable: false,
				modal: true,
				items: [
					nameTextfield,
					systemCheckbox
				],
				bbar: [
					cancelButton,
					'->',
					id ? updateButton : createButton
				],
				listeners: {
					show: function() {
						this.setPosition(this.getPosition()[0], 100);
					}
				}
			});
			
			return window;
		};
			
		searchTextfield = Ext.create('Ext.form.field.Text', {
			width: 353,
			height: 26,
			fieldStyle: 'padding-left: 6px; border-radius: 1px; border-color: #bbb',
			emptyText: 'Search for favorites..', //i18n
			enableKeyEvents: true,
			currentValue: '',
			listeners: {
				keyup: function() {
					if (this.getValue() !== this.currentValue) {
						this.currentValue = this.getValue();
						
						var value = this.getValue(),
							url = value ? GIS.conf.url.path_api +  'maps/query/' + value + '.json?links=false' : null,
							store = GIS.store.maps;
							
						store.page = 1;
						store.loadStore(url);
					}
				}
			}
		});
		
		addButton = Ext.create('Ext.button.Button', {
			text: 'Add new', //i18n
			height: 26,
			style: 'border-radius: 1px; margin-right: 5px',
			menu: {},
			handler: function() {
				nameWindow = new NameWindow();
				nameWindow.show();
			}
		});
		
		prevButton = Ext.create('Ext.button.Button', {
			text: 'Prev', //i18n
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? GIS.conf.url.path_api +  'maps/query/' + value + '.json?links=false' : null,
					store = GIS.store.maps;
					
				store.page = store.page <= 1 ? 1 : store.page - 1;
				store.loadStore(url);
			}
		});
		
		nextButton = Ext.create('Ext.button.Button', {
			text: 'Next', //i18n
			handler: function() {
				var value = searchTextfield.getValue(),
					url = value ? GIS.conf.url.path_api +  'maps/query/' + value + '.json?links=false' : null,
					store = GIS.store.maps;
					
				store.page = store.page + 1;
				store.loadStore(url);
			}
		});
		
		info = Ext.create('Ext.form.Label', {
			cls: 'gis-label-info',
			width: 300,
			height: 22
		});
		
		grid = Ext.create('Ext.grid.Panel', {
			cls: 'gis-grid',
			scroll: false,
			hideHeaders: true,
			columns: [
				{
					dataIndex: 'name',
					sortable: false,
					width: 355,
					renderer: function(value, metaData, record) {
						var fn = function() {
							var el = Ext.get(record.data.id).parent('td');
							el.addClsOnOver('link');
							el.dom.setAttribute('onclick', 'GIS.cmp.mapWindow.destroy(); GIS.map.mapLoader = new GIS.obj.MapLoader("' + record.data.id + '"); GIS.map.mapLoader.load();');
						};
						
						Ext.defer(fn, 100);
						
						return '<div id="' + record.data.id + '">' + value + '</div>';
					}
				},
				{
					xtype: 'actioncolumn',
					sortable: false,
					width: 65,
					items: [
						{
							iconCls: 'gis-grid-row-icon-edit',
							getClass: function() {
								return 'tooltip-map-edit'; // tooltips removed if deleteArray is empty
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									id = record.data.id,
									system = !record.data.user,
									isAdmin = GIS.init.security.isAdmin;
									
								if (isAdmin || (!isAdmin && !system)) {
									var id = this.up('grid').store.getAt(rowIndex).data.id;
									nameWindow = new NameWindow(id);
									nameWindow.show();
								}
							}
						},
						{
							iconCls: 'gis-grid-row-icon-overwrite',
							getClass: function() {
								return 'tooltip-map-overwrite'; // tooltips removed if deleteArray is empty
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									id = record.data.id,
									name = record.data.name,
									layers = GIS.util.map.getVisibleVectorLayers(),
									layer,
									lonlat = GIS.map.getCenter(),
									views = [],
									view,
									map,
									message = 'Overwrite favorite?\n\n' + name;
								
								if (layers.length) {
									if (confirm(message)) {
										for (var i = 0; i < layers.length; i++) {
											layer = layers[i];
											view = layer.base.widget.getView();
											
											// add
											view.layer = layer.base.id;
											
											// remove
											delete view.periodType;
											views.push(view);
										}
										
										map = {
											longitude: lonlat.lon,
											latitude: lonlat.lat,
											zoom: GIS.map.getZoom(),
											mapViews: views
										};
									
										Ext.Ajax.request({
											url: GIS.conf.url.path_api + 'maps/' + id,
											method: 'PUT',
											headers: {'Content-Type': 'application/json'},
											params: Ext.encode(map),
											success: function() {								
												GIS.store.maps.loadStore();
											}
										});
									}
								}
								else {
									alert('No layers to save'); //i18n
								}
							}
						},
						{
							iconCls: 'gis-grid-row-icon-delete',
							getClass: function(value, metaData, record) { // all tooltips removed if deleteArray is empty
								var system = !record.data.user,
									isAdmin = GIS.init.security.isAdmin;
								
								if (isAdmin || (!isAdmin && !system)) {
									return 'tooltip-map-delete';
								}
							},
							handler: function(grid, rowIndex, colIndex, col, event) {
								var record = this.up('grid').store.getAt(rowIndex),
									id = record.data.id,
									name = record.data.name,
									message = 'Delete favorite?\n\n' + name;
									
								if (confirm(message)) {								
									Ext.Ajax.request({
										url: GIS.conf.url.path_api + 'maps/' + id,
										method: 'DELETE',
										success: function() {
											GIS.store.maps.loadStore();
										}
									});
								}
							}
						}
					],
					renderer: function(value, metaData, record) {
						if (!GIS.init.security.isAdmin && !record.data.user) {
							metaData.tdCls += ' gis-grid-row-icon-disabled';
						}
					}
				}
			],
			store: GIS.store.maps,
			bbar: [
				info,
				'->',
				prevButton,
				nextButton
			],
			listeners: {
				added: function() {
					GIS.cmp.mapGrid = this;
				},
				render: function() {
					var size = Math.floor((GIS.cmp.region.center.getHeight() - 155) / GIS.conf.layout.grid.row_height);
					this.store.pageSize = size;
					this.store.page = 1;
					this.store.loadStore();
				},
				afterrender: function() {
					var fn = function() {
						var editArray = document.getElementsByClassName('tooltip-map-edit'),
							overwriteArray = document.getElementsByClassName('tooltip-map-overwrite'),
							deleteArray = document.getElementsByClassName('tooltip-map-delete'),
							len = deleteArray.length,
							el;
						
						for (var i = 0; i < len; i++) {
							el = editArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Rename',
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
							
							el = overwriteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Overwrite',
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
							
							el = deleteArray[i];
							Ext.create('Ext.tip.ToolTip', {
								target: el,
								html: 'Delete',
								'anchor': 'bottom',
								anchorOffset: -14,
								showDelay: 1000
							});
						}
					};
					
					Ext.defer(fn, 100);
				},
				itemmouseenter: function(grid, record, item) {
					this.currentItem = Ext.get(item);
					this.currentItem.removeCls('x-grid-row-over');
				},
				select: function() {
					this.currentItem.removeCls('x-grid-row-selected');
				},
				selectionchange: function() {
					this.currentItem.removeCls('x-grid-row-focused');
				}
			}
		});
		
		mapWindow = Ext.create('Ext.window.Window', {
			title: 'Manage favorites',
			iconCls: 'gis-window-title-icon-favorite',
			cls: 'gis-container-default',
			resizable: false,
			modal: true,
			width: 450,
			items: [
				{
					xtype: 'panel',
					layout: 'hbox',
					cls: 'gis-container-inner',
					items: [
						addButton,
						searchTextfield
					]
				},
				grid
			],
			listeners: {
				show: function() {
					this.setPosition(115, 37);
				}
			}
		});
		
		return mapWindow;
	};
	
	GIS.obj.MapLoader = function(id) {
		var getMap,
			setMap,
			map,
			callbackRegister = [],
			loader;
		
		getMap = function(id) {
			if (!id) {
				alert('No favorite id provided');
				return;
			}
			if (!Ext.isString(id)) {
				alert('Favorite id must be a string');
				return;
			}
			
			Ext.Ajax.request({
				url: GIS.conf.url.path_api + 'maps/' + id + '.json?links=false',
				success: function(r) {
					map = Ext.decode(r.responseText);
					setMap(map);
				}
			});
		};
		
		setMap = function(map) {
			var views = map.mapViews,
				view,
				center,
				lonLat;
				
			if (!views.length) {
				alert('Favorite has no layers'); //i18n
				return;
			}
			GIS.util.map.closeAllLayers();
			
			for (var i = 0; i < views.length; i++) {
				view = views[i];
				GIS.base[view.layer].widget.execute(view);
			}
			
			lonLat = new OpenLayers.LonLat(map.longitude, map.latitude);
			GIS.map.setCenter(lonLat, map.zoom);
		};
		
		loader = {
			id: id,
			load: function(id) {
				this.id = id || this.id;
				getMap(this.id);
			},
			callBack: function(widget) {
				callbackRegister.push(widget);
				
				if (callbackRegister.length === map.mapViews.length) {
					GIS.cmp.interpretationButton.enable();
				}
			}
		};
		
		return loader;
	};
	
	GIS.obj.LegendSetWindow = function() {
		
		// Stores
		var legendSetStore,
			legendStore,
			tmpLegendStore,
			
		// Objects
			LegendSetPanel,
			LegendPanel,
			
		// Instances
			legendSetPanel,
			legendPanel,
			
		// Components
			window,	
			legendSetName,
			legendName,
			startValue,
			endValue,
			color,
			legendGrid,
			create,
			update,
			cancel,
			info,
			error1Window,
			error2Window,
			
		// Functions
			showUpdateLegendSet,
			deleteLegendSet,
			deleteLegend,
			getRequestBody,
			reset,
			validateLegends;
				
		legendSetStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: GIS.conf.url.path_api + 'mapLegendSets.json?links=false&paging=false',
				reader: {
					type: 'json',
					root: 'mapLegendSets'
				}
			},
			listeners: {
				load: function(store, records) {
					this.sort('name', 'ASC');
					
					info.setText(records.length + ' legend set' + (records.length !== 1 ? 's' : '') + ' available');
				}
			}
		});
		
		legendStore = Ext.create('Ext.data.Store', {
			fields: ['id', 'name', 'startValue', 'endValue', 'color'],
			proxy: {
				type: 'ajax',
				url: '',
				reader: {
					type: 'json',
					root: 'mapLegends'
				}
			},
			deleteLegend: deleteLegend,
			listeners: {
				load: function(store, records) {						
					var data = [],
						record;
					
					for (var i = 0; i < records.length; i++) {
						data.push(records[i].data);
					}
					
					Ext.Array.sort(data, function (a, b) {  
						return a.startValue - b.startValue;  
					});
					
					tmpLegendStore.add(data);
					
					info.setText(records.length + ' legend sets available');
				}
			}
		});
		
		LegendSetPanel = function() {
			var items,
				addButton;
				
			addButton = Ext.create('Ext.button.Button', {
				text: 'Add new', //i18n
				height: 26,
				style: 'border-radius: 1px',
				menu: {},
				handler: function() {
					showUpdateLegendSet();
				}
			});
			
			legendSetGrid = Ext.create('Ext.grid.Panel', {
				cls: 'gis-grid',
				scroll: 'vertical',
				height: true,
				hideHeaders: true,
				currentItem: null,
				columns: [						
					{
						dataIndex: 'name',
						sortable: false,
						width: 355
					},
					{
						xtype: 'actioncolumn',
						sortable: false,
						width: 45,
						items: [
							{
								iconCls: 'gis-grid-row-icon-edit',
								getClass: function() {
									return 'tooltip-legendset-edit';
								},
								handler: function(grid, rowIndex, colIndex, col, event) {
									var id = this.up('grid').store.getAt(rowIndex).data.id;
									showUpdateLegendSet(id);
								}
							},
							{
								iconCls: 'gis-grid-row-icon-delete',
								getClass: function() {
									return 'tooltip-legendset-delete';
								},
								handler: function(grid, rowIndex, colIndex, col, event) {
									var record = this.up('grid').store.getAt(rowIndex),
										id = record.data.id,
										name = record.data.name,
										message = 'Delete legend set?\n\n' + name;
								
									if (confirm(message)) {
										deleteLegendSet(id);
									}
								}
							}
						]
					},
					{
						sortable: false,
						width: 20
					}
				],
				store: legendSetStore,
				listeners: {
					render: function() {
						var that = this,
							maxHeight = GIS.cmp.region.center.getHeight() - 155,
							height;
							
						this.store.on('load', function() {
							if (Ext.isDefined(that.setHeight)) {
								height = 1 + that.store.getCount() * GIS.conf.layout.grid.row_height;
								that.setHeight(height > maxHeight ? maxHeight : height);
								window.doLayout();
							}
						});
								
						this.store.load();
					},
					afterrender: function() {
						var fn = function() {
							var editArray = document.getElementsByClassName('tooltip-legendset-edit'),
								deleteArray = document.getElementsByClassName('tooltip-legendset-delete'),
								len = editArray.length,
								el;
							
							for (var i = 0; i < len; i++) {
								el = editArray[i];
								Ext.create('Ext.tip.ToolTip', {
									target: el,
									html: 'Rename',
									'anchor': 'bottom',
									anchorOffset: -14,
									showDelay: 1000
								});
								
								el = deleteArray[i];
								Ext.create('Ext.tip.ToolTip', {
									target: el,
									html: 'Delete',
									'anchor': 'bottom',
									anchorOffset: -14,
									showDelay: 1000
								});
							}
						};
						
						Ext.defer(fn, 100);
					},
					itemmouseenter: function(grid, record, item) {
						this.currentItem = Ext.get(item);
						this.currentItem.removeCls('x-grid-row-over');
					},
					select: function() {
						this.currentItem.removeCls('x-grid-row-selected');
					},
					selectionchange: function() {
						this.currentItem.removeCls('x-grid-row-focused');
					}
				}
			});
			
			items = [
				{
					xtype: 'panel',
					layout: 'hbox',
					cls: 'gis-container-inner',
					style: 'margin-bottom: 5px',
					items: [
						addButton
					]
				},
				legendSetGrid
			];
			
			return items;
		};
		
		LegendPanel = function(id) {
			var panel,
				addLegend,
				reset,
				data = [];
				
			tmpLegendStore = Ext.create('Ext.data.ArrayStore', {
				fields: ['id', 'name', 'startValue', 'endValue', 'color']
			});
			
			legendSetName = Ext.create('Ext.form.field.Text', {
				cls: 'gis-textfield',
				width: 422,
				height: 25,
				fieldStyle: 'padding-left: 6px; border-color: #bbb',
				fieldLabel: 'Legend set name' //i18n
			});
			
			legendName = Ext.create('Ext.form.field.Text', {
				cls: 'gis-textfield',
				fieldStyle: 'padding-left: 6px',
				width: 404,
				height: 23,
				fieldLabel: 'Legend name' //i18n
			});
			
			startValue = Ext.create('Ext.form.field.Number', {
				width: 148,
				height: 23,
				allowDecimals: false,
				fieldStyle: 'padding-left: 6px; border-radius: 1px',
				value: 0
			});
			
			endValue = Ext.create('Ext.form.field.Number', {
				width: 148,
				height: 23,
				allowDecimals: false,
				fieldStyle: 'padding-left: 6px; border-radius: 1px',
				value: 0,
				style: 'padding-left: 3px'
			});
			
			color = Ext.create('Ext.ux.button.ColorButton', {
				width: 299,
				height: 23,
				fieldLabel: 'Symbolizer', //i18n
				style: 'border-radius: 1px',
				value: 'e1e1e1'
			});
			
			addLegend = Ext.create('Ext.button.Button', {
				text: 'Add legend', //i18n
				height: 26,
				style: 'border-radius: 1px',
				handler: function() {
					var date = new Date(),
						id = date.toISOString(),
						ln = legendName.getValue(),
						sv = startValue.getValue(),
						ev = endValue.getValue(),
						co = color.getValue().toUpperCase(),
						items = tmpLegendStore.data.items,
						data = [];
					
					if (ln && (ev > sv)) {
						for (var i = 0; i < items.length; i++) {
							data.push(items[i].data);
						}
						
						data.push({
							id: id,
							name: ln,
							startValue: sv,
							endValue: ev,
							color: '#' + co
						});
					
						Ext.Array.sort(data, function (a, b) {  
							return a.startValue - b.startValue;  
						});
						
						tmpLegendStore.removeAll();
						tmpLegendStore.add(data);
						
						legendName.reset();
						startValue.reset();
						endValue.reset();
						color.reset();						
					}
				}
			});
			
			legendGrid = Ext.create('Ext.grid.Panel', {
				cls: 'gis-grid',
				bodyStyle: 'border-top: 0 none',
				width: 422,
				height: 235,
				scroll: 'vertical',
				hideHeaders: true,
				currentItem: null,
				columns: [
					{
						dataIndex: 'name',
						sortable: false,
						width: 247
					},
					{
						sortable: false,
						width: 45,
						renderer: function(value, metaData, record) {
							return '<span style="color:' + record.data.color + '">Color</span>';
						}
					},
					{
						dataIndex: 'startValue',
						sortable: false,
						width: 45
					},
					{
						dataIndex: 'endValue',
						sortable: false,
						width: 45
					},
					{
						xtype: 'actioncolumn',
						sortable: false,
						width: 20,
						items: [
							{
								iconCls: 'gis-grid-row-icon-delete',
								getClass: function() {
									return 'tooltip-legend-delete';
								},
								handler: function(grid, rowIndex, colIndex, col, event) {
									var id = this.up('grid').store.getAt(rowIndex).data.id;
									deleteLegend(id);
								}
							}
						]
					},
					{
						sortable: false,
						width: 20
					}
				],
				store: tmpLegendStore,
				listeners: {
					itemmouseenter: function(grid, record, item) {
						this.currentItem = Ext.get(item);
						this.currentItem.removeCls('x-grid-row-over');
					},
					select: function() {
						this.currentItem.removeCls('x-grid-row-selected');
					},
					selectionchange: function() {
						this.currentItem.removeCls('x-grid-row-focused');
					},
					afterrender: function() {
						var fn = function() {
							var deleteArray = document.getElementsByClassName('tooltip-legend-delete'),
								len = deleteArray.length,
								el;
							
							for (var i = 0; i < len; i++) {
								el = deleteArray[i];
								Ext.create('Ext.tip.ToolTip', {
									target: el,
									html: 'Delete',
									'anchor': 'bottom',
									anchorOffset: -14,
									showDelay: 1000
								});
							}
						};
						
						Ext.defer(fn, 100);
					}
				}
			});
			
			panel = Ext.create('Ext.panel.Panel', {
				cls: 'gis-container-inner',
				legendSetId: id,
				items: [
					legendSetName,
					{
						cls: 'gis-panel-html-separator'
					},
					{
						html: 'Add legend', //i18n
						cls: 'gis-panel-html-title'
					},
					{
						cls: 'gis-panel-html-separator'
					},
					{
						bodyStyle: 'background-color: #f1f1f1; border: 1px solid #ccc; border-radius: 1px; padding: 8px',
						items: [
							legendName,
							{
								layout: 'hbox',
								bodyStyle: 'background: transparent',
								items: [
									{
										html: 'Start / end value:', //i18n
										width: 105,
										bodyStyle: 'background: transparent; padding-top: 3px'
									},
									startValue,
									endValue
								]
							},
							{
								layout: 'column',
								cls: 'gis-container-inner',
								bodyStyle: 'background: transparent',
								items: [
									{
										cls: 'gis-panel-html-label',
										html: 'Symbolizer:', //i18n
										bodyStyle: 'background: transparent',
										width: GIS.conf.layout.widget.itemlabel_width + 10
									},
									color
								]
							},
						]
					},
					{
						cls: 'gis-panel-html-separator'
					},
					{
						cls: 'gis-container-inner',
						bodyStyle: 'text-align: right',
						width: 422,
						items: addLegend
					},
					{
						html: 'Current legends', //i18n
						cls: 'gis-panel-html-title'
					},
					{
						cls: 'gis-panel-html-separator'
					},
					legendGrid
				]
			});
			
			if (id) {
				legendStore.proxy.url = GIS.conf.url.path_api +  'mapLegendSets/' + id + '.json?links=false&paging=false';
				legendStore.load();
				
				legendSetName.setValue(legendSetStore.getById(id).data.name);
			}
			
			return panel;
		};
		
		showUpdateLegendSet = function(id) {
			legendPanel = new LegendPanel(id);
			window.removeAll();
			window.add(legendPanel);
			info.hide();
			cancel.show();
			
			if (id) {
				update.show();
			}
			else {
				create.show();
			}
		};
			
		deleteLegendSet = function(id) {
			if (id) {
				Ext.Ajax.request({
					url: GIS.conf.url.path_api + 'mapLegendSets/' + id,
					method: 'DELETE',
					success: function() {
						legendSetStore.load();
						GIS.store.legendSets.load();
					}
				});
			}
		};
		
		deleteLegend = function(id) {
			tmpLegendStore.remove(tmpLegendStore.getById(id));
		};
		
		getRequestBody = function() {
			var items = tmpLegendStore.data.items,
				body;
				
			body = {
				name: legendSetName.getValue(),
				symbolizer: GIS.conf.finals.widget.symbolizer_color,
				mapLegends: []
			};
			
			for (var i = 0; i < items.length; i++) {
				var item = items[i];
				body.mapLegends.push({
					name: item.data.name,
					startValue: item.data.startValue,
					endValue: item.data.endValue,
					color: item.data.color
				});
			}
			
			return body;
		};			
		
		reset = function() {
			legendPanel.destroy();
			legendSetPanel = new LegendSetPanel();
			window.removeAll();
			window.add(legendSetPanel);
			
			info.show();
			cancel.hide();
			create.hide();
			update.hide();
		};
		
		validateLegends = function() {
			var items = tmpLegendStore.data.items,
				item,
				prevItem;
				
			if (items.length === 0) {
				alert('No legend set name');
				return false;
			}
				
			for (var i = 1; i < items.length; i++) {
				item = items[i].data;
				prevItem = items[i - 1].data;
				
				if (item.startValue < prevItem.endValue) {
					var msg = 'Overlapping legends not allowed!\n\n' +
							  prevItem.name + ' (' + prevItem.startValue + ' - ' + prevItem.endValue + ')\n' +
							  item.name + ' (' + item.startValue + ' - ' + item.endValue + ')';
					alert(msg);
					return false;
				}
				
				if (prevItem.endValue < item.startValue) {
					var msg = 'Legend gaps detected!\n\n' +
							  prevItem.name + ' (' + prevItem.startValue + ' - ' + prevItem.endValue + ')\n' +
							  item.name + ' (' + item.startValue + ' - ' + item.endValue + ')\n\n' +
							  'Proceed anyway?';
					
					if (!confirm(msg)) {
						return false;
					}
				}
			}
			
			return true;
		};
		
		create = Ext.create('Ext.button.Button', {
			text: 'Create', //i18n
			hidden: true,
			handler: function() {
				if (legendSetName.getValue() && validateLegends()) {
					var body = Ext.encode(getRequestBody());
					
					Ext.Ajax.request({
						url: GIS.conf.url.path_api + 'mapLegendSets/',
						method: 'POST',
						headers: {'Content-Type': 'application/json'},
						params: body,
						success: function() {
							GIS.store.legendSets.load();
							reset();
						}
					});
				}
			}
		});
		
		update = Ext.create('Ext.button.Button', {
			text: 'Update', //i18n
			hidden: true,
			handler: function() {
				if (legendSetName.getValue() && validateLegends()) {
					var body = getRequestBody(),
						id = legendPanel.legendSetId;
					body.id = id;
					body = Ext.encode(getRequestBody());
					
					Ext.Ajax.request({
						url: GIS.conf.url.path_api + 'mapLegendSets/' + id,
						method: 'PUT',
						headers: {'Content-Type': 'application/json'},
						params: body,
						success: function() {
							reset();
						}
					});
				}
			}
		});
		
		cancel = Ext.create('Ext.button.Button', {
			text: 'Cancel', //i18n
			hidden: true,
			handler: function() {
				reset();
			}
		});
		
		info = Ext.create('Ext.form.Label', {
			cls: 'gis-label-info',
			width: 400,
			height: 22
		});
		
		window = Ext.create('Ext.window.Window', {
			title: 'Legend sets', //i18n
			iconCls: 'gis-window-title-icon-legendset', //todo
			cls: 'gis-container-default',
			resizable: false,
			width: 450,
			modal: true,
			items: new LegendSetPanel(),
			bbar: {
				height: 27,
				items: [
					info,
					cancel,
					'->',
					create,
					update
				]
			},
			listeners: {					
				show: function() {
					this.setPosition(185, 37);
				}
			}
		});
		
		return window;
	};
	
	GIS.obj.DownloadWindow = function() {			
		var window,
			textfield,
			button;
			
		textfield = Ext.create('Ext.form.field.Text', {
			cls: 'gis-textfield',
			height: 26,
			width: 230,
			fieldStyle: 'padding-left: 5px',
			emptyText: 'Enter map title...' //i18n
		});
		
		button = Ext.create('Ext.button.Button', {
			text: 'Download', //i18n
			handler: function() {
				var title = textfield.getValue(),
					svg = GIS.util.svg.getString(title, GIS.util.map.getVisibleVectorLayers()),
					exportForm = document.getElementById('exportForm');
					
				if (!svg) {
					alert('Please create a map first'); //todo //i18n
					return;
				}
				
				document.getElementById('svgField').value = svg;
				document.getElementById('titleField').value = title;
				exportForm.action = '../exportImage.action';
				exportForm.method = 'post';
				exportForm.submit();
				
				window.destroy();
			}
		});
		
		window = Ext.create('Ext.window.Window', {
			title: 'Download map as PNG', //i18n
			layout: 'fit',
			iconCls: 'gis-window-title-icon-download',
			cls: 'gis-container-default',
			resizable: true,
			modal: true,
			items: textfield,
			bbar: [
				'->',
				button
			],
			listeners: {
				show: function() {
					this.setPosition(253, 37);
				}
			}
		});
		
		return window;
	};
	
	GIS.obj.InterpretationWindow = function() {
		var window,
			textarea,
			panel,
			button;
			
		textarea = Ext.create('Ext.form.field.TextArea', {
			cls: 'gis-textarea',
			height: 130,
			fieldStyle: 'padding-left: 4px; padding-top: 3px',
			emptyText: 'Write your interpretation...' //i18n
		});
		
		panel = Ext.create('Ext.panel.Panel', {
			cls: 'gis-container-inner',
			html: '<b>Direct link: </b>' + GIS.init.contextPath + '/dhis-web-mapping/app/index.html?id=' + GIS.map.mapLoader.id,
			style: 'padding-top: 9px; padding-bottom: 2px'
		});
		
		button = Ext.create('Ext.button.Button', {
			text: 'Share', //i18n
			handler: function() {
				if (textarea.getValue() && GIS.map.mapLoader) {
					Ext.Ajax.request({
						url: GIS.conf.url.path_api + 'interpretations/map/' + GIS.map.mapLoader.id,
						method: 'POST',
						params: textarea.getValue(),
						headers: {'Content-Type': 'text/html'},
						success: function() {
							window.destroy();
							
							alert('Interpretation was shared!');
						}
					});
				}
			}
		});
		
		window = Ext.create('Ext.window.Window', {
			title: 'Share interpretation', //i18n
			layout: 'fit',
			iconCls: 'gis-window-title-icon-interpretation',
			cls: 'gis-container-default',
			width: 500,
			resizable: true,
			modal: true,
			items: [
				textarea,
				panel
			],
			bbar: [
				'->',
				button
			],
			listeners: {
				show: function() {
					this.setPosition(325, 37);
				},
				destroy: function() {
					document.body.oncontextmenu = function(){
						return false;
					};
				}
					
			}
		});
		
		document.body.oncontextmenu = true; // right click to copy url
		
		return window;
	};
	
	GIS.obj.MeasureWindow = function() {
		var window,
			label,
			handleMeasurements,
			control,
			styleMap;
			
		styleMap = new OpenLayers.StyleMap({
			'default': new OpenLayers.Style()
		});
			
		control = new OpenLayers.Control.Measure( OpenLayers.Handler.Path, {
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
		
		GIS.map.addControl(control);
		
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
			title: 'Measure distance', //i18n
			layout: 'fit',
			cls: 'gis-container-default',
			bodyStyle: 'text-align: center',
			width: 130,
			minWidth: 130,
			resizable: false,
			items: label,
			listeners: {
				show: function() {
					var x = GIS.cmp.region.east.x - this.getWidth() - 5,
						y = 60;
					this.setPosition(x, y);
				},
				destroy: function() {
					control.deactivate();
					GIS.map.removeControl(control);
				}
			}
		});
		
		return window;
	};
	
	// OpenLayers map
	
	GIS.map = new OpenLayers.Map({
        controls: [
			new OpenLayers.Control.Navigation({
				documentDrag: true
			}),
			new OpenLayers.Control.MousePosition({
				id: 'mouseposition',
				prefix: '<span class="el-fontsize-10"><span class="text-mouseposition-lonlat">LON </span>',
				separator: '<span class="text-mouseposition-lonlat">&nbsp;&nbsp;LAT </span>',
				suffix: '<div id="google-logo" onclick="javascript:GIS.util.google.openTerms();"></div></span>'
			}),
			new OpenLayers.Control.Permalink()
		],
        displayProjection: new OpenLayers.Projection('EPSG:4326'),
        maxExtent: new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508),
        mouseMove: {}, // Track all mouse moves
        relocate: {} // Relocate organisation units
    });
	
	// Map controls
	GIS.util.map.addMapControl('zoomIn', GIS.map.zoomIn);
	GIS.util.map.addMapControl('zoomOut', GIS.map.zoomOut);
	GIS.util.map.addMapControl('zoomVisible', GIS.util.map.zoomToVisibleExtent);
	GIS.util.map.addMapControl('measure', function() {
		if (GIS.cmp.measureWindow && GIS.cmp.measureWindow.destroy) {
			GIS.cmp.measureWindow.destroy();
		}		
		GIS.cmp.measureWindow = new GIS.obj.MeasureWindow();
		GIS.cmp.measureWindow.show();
	});
    
    // Base layers
    
    if (window.google) {
		
		// Google Streets
        GIS.base.googleStreets.layer = new OpenLayers.Layer.Google(GIS.base.googleStreets.name, {
			numZoomLevels: 20,
			animationEnabled: true,
			layerType: GIS.conf.finals.layer.type_base,
			base: GIS.base.googleStreets,
			layerOpacity: 1,
			setLayerOpacity: function(number) {
				if (number) {
					this.layerOpacity = parseFloat(number);
				}
				this.setOpacity(this.layerOpacity);
			}
		});
        GIS.map.addLayer(GIS.base.googleStreets.layer);
        
		// Google Hybrid
        GIS.base.googleHybrid.layer = new OpenLayers.Layer.Google(GIS.base.googleHybrid.name, {
			type: google.maps.MapTypeId.HYBRID,
			numZoomLevels: 20,
			animationEnabled: true,
			layerType: GIS.conf.finals.layer.type_base,
			base: GIS.base.googleHybrid,
			layerOpacity: 1,
			setLayerOpacity: function(number) {
				if (number) {
					this.layerOpacity = parseFloat(number);
				}
				this.setOpacity(this.layerOpacity);
			}
		});
        GIS.map.addLayer(GIS.base.googleHybrid.layer);
    }
    
    // OpenStreetMap
    GIS.base.openStreetMap.layer = new OpenLayers.Layer.OSM(GIS.base.openStreetMap.name);
	GIS.base.openStreetMap.layer.layerType = GIS.conf.finals.layer.type_base;
	GIS.base.openStreetMap.layer.base = GIS.base.openStreetMap;
	GIS.base.openStreetMap.layer.layerOpacity = 1;
	GIS.base.openStreetMap.layer.setLayerOpacity = function(number) {
		if (number) {
			this.layerOpacity = parseFloat(number);
		}
		this.setOpacity(this.layerOpacity);
	};
    GIS.map.addLayer(GIS.base.openStreetMap.layer);
    
    // Base objects
    
    GIS.base.boundary.layer = new GIS.obj.VectorLayer(GIS.base.boundary);
    GIS.map.addLayer(GIS.base.boundary.layer);
	GIS.base.boundary.menu = new GIS.obj.LayerMenu(GIS.base.boundary, 'gis-toolbar-btn-menu-first');	
	GIS.base.boundary.widget = Ext.create('mapfish.widgets.geostat.Boundary', {
        map: GIS.map,
        layer: GIS.base.boundary.layer,
        menu: GIS.base.boundary.menu
    });    
    GIS.base.boundary.window = new GIS.obj.WidgetWindow(GIS.base.boundary);
    
    GIS.base.thematic1.layer = new GIS.obj.VectorLayer(GIS.base.thematic1, {opacity: 0.8});
    GIS.map.addLayer(GIS.base.thematic1.layer);
	GIS.base.thematic1.menu = new GIS.obj.LayerMenu(GIS.base.thematic1);	
	GIS.base.thematic1.widget = Ext.create('mapfish.widgets.geostat.Thematic1', {
        map: GIS.map,
        layer: GIS.base.thematic1.layer,
        menu: GIS.base.thematic1.menu,
        legendDiv: GIS.base.thematic1.legendDiv
    });
    GIS.base.thematic1.window = new GIS.obj.WidgetWindow(GIS.base.thematic1);
    
    GIS.base.thematic2.layer = new GIS.obj.VectorLayer(GIS.base.thematic2, {opacity: 0.8});
    GIS.map.addLayer(GIS.base.thematic2.layer);
	GIS.base.thematic2.menu = new GIS.obj.LayerMenu(GIS.base.thematic2);	
	GIS.base.thematic2.widget = Ext.create('mapfish.widgets.geostat.Thematic2', {
        map: GIS.map,
        layer: GIS.base.thematic2.layer,
        menu: GIS.base.thematic2.menu,
        legendDiv: GIS.base.thematic2.legendDiv
    });    
    GIS.base.thematic2.window = new GIS.obj.WidgetWindow(GIS.base.thematic2);
    
    GIS.base.facility.layer = new GIS.obj.VectorLayer(GIS.base.facility);
    GIS.map.addLayer(GIS.base.facility.layer);
	GIS.base.facility.menu = new GIS.obj.LayerMenu(GIS.base.facility);
	GIS.base.facility.widget = Ext.create('mapfish.widgets.geostat.Facility', {
        map: GIS.map,
        layer: GIS.base.facility.layer,
        menu: GIS.base.facility.menu,
        legendDiv: GIS.base.facility.legendDiv
    });
    GIS.base.facility.window = new GIS.obj.WidgetWindow(GIS.base.facility);
    
	// User interface
	
	GIS.gui.viewport = Ext.create('Ext.container.Viewport', {
		layout: 'border',
		items: [
			{
				region: 'east',
				layout: 'anchor',
				width: 200,
                preventHeader: true,
                collapsible: true,
                collapseMode: 'mini',
				items: [
                    {
                        title: 'Layer overview and visibility %', //i18n
                        bodyStyle: 'padding: 6px',
                        items: new GIS.obj.LayersPanel(),
                        collapsible: true,
                        animCollapse: false
                    },
                    {
                        title: 'Thematic layer 1 legend', //i18n
                        contentEl: 'thematic1Legend',
                        bodyStyle: 'padding: 6px; border: 0 none',
                        collapsible: true,
                        collapsed: true,
                        animCollapse: false,
                        listeners: {
							added: function() {
								GIS.base.thematic1.layer.legend = this;
							}
						}
                    },
                    {
                        title: 'Thematic layer 2 legend', //i18n
                        contentEl: 'thematic2Legend',
                        bodyStyle: 'padding: 6px; border: 0 none',
                        collapsible: true,
                        collapsed: true,
                        animCollapse: false,
                        listeners: {
							added: function() {
								GIS.base.thematic2.layer.legend = this;
							}
						}
                    },
                    {
                        title: 'Facility layer legend', //i18n
                        contentEl: 'facilityLegend',
                        bodyStyle: 'padding: 6px; border: 0 none',
                        collapsible: true,
                        collapsed: true,
                        animCollapse: false,
                        listeners: {
							added: function() {
								GIS.base.facility.layer.legend = this;
							}
						}
                    }
				],
				listeners: {
					added: function() {
						GIS.cmp.region.east = this;
					},
                    collapse: function() {
                        GIS.cmp.region.center.cmp.tbar.resize.setText('<<<');
                    },
                    expand: function() {
                        GIS.cmp.region.center.cmp.tbar.resize.setText('>>>');
                    }
				}
			},
            {
                xtype: 'gx_mappanel',
                region: 'center',
                map: GIS.map,
                height: 31,
                cmp: {
					tbar: {}
				},
                tbar: {
					defaults: {
						height: 26
					},
					items: [
						{
							iconCls: 'gis-btn-icon-' + GIS.base.boundary.id,
							menu: GIS.base.boundary.menu,
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.base.thematic1.id,
							menu: GIS.base.thematic1.menu,
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.base.thematic2.id,
							menu: new GIS.obj.LayerMenu(GIS.base.thematic2),
							width: 26
						},
						{
							iconCls: 'gis-btn-icon-' + GIS.base.facility.id,
							menu: new GIS.obj.LayerMenu(GIS.base.facility),
							width: 26
						},
						{
							text: 'Favorites', //i18n
							menu: {},
							handler: function() {
								if (GIS.cmp.mapWindow && GIS.cmp.mapWindow.destroy) {
									GIS.cmp.mapWindow.destroy();
								}
								
								GIS.cmp.mapWindow = new GIS.obj.MapWindow();
								GIS.cmp.mapWindow.show();
							}
						},
						{
							text: 'Legend', //i18n
							menu: {},
							handler: function() {
								if (GIS.cmp.legendSetWindow && GIS.cmp.legendSetWindow.destroy) {
									GIS.cmp.legendSetWindow.destroy();
								}
								
								GIS.cmp.legendSetWindow = new GIS.obj.LegendSetWindow();
								GIS.cmp.legendSetWindow.show();
							}
						},
						{
							xtype: 'tbseparator',
							height: 18,
							style: 'border-color: transparent #d1d1d1 transparent transparent; margin-right: 4px',
						},
						{
							text: 'Download', //i18n
							menu: {},
							disabled: true,
							handler: function() {
								if (GIS.cmp.downloadWindow && GIS.cmp.downloadWindow.destroy) {
									GIS.cmp.downloadWindow.destroy();
								}
								
								GIS.cmp.downloadWindow = new GIS.obj.DownloadWindow();
								GIS.cmp.downloadWindow.show();
							},								
							xable: function() {
								if (GIS.util.map.hasVisibleFeatures()) {
									this.enable();
								}
								else {
									this.disable();
								}
							},
							listeners: {
								added: function() {
									GIS.cmp.downloadButton = this;
								}
							}
						},
						{
							text: 'Share', //i18n
							menu: {},
							disabled: true,
							handler: function() {
								if (GIS.cmp.interpretationWindow && GIS.cmp.interpretationWindow.destroy) {
									GIS.cmp.interpretationWindow.destroy();
								}
								
								GIS.cmp.interpretationWindow = new GIS.obj.InterpretationWindow();
								GIS.cmp.interpretationWindow.show();
							},
							listeners: {
								added: function() {
									GIS.cmp.interpretationButton = this;
								}
							}
						},
						'->',
						{
							text: 'Exit', //i18n
							handler: function() {								
                                window.location.href = '../../dhis-web-commons-about/redirect.action';
							}
						},
						{
							text: '>>>', //i18n
							handler: function() {
								GIS.cmp.region.east.toggleCollapse();
							},
							listeners: {
								render: function() {
									GIS.cmp.region.center.cmp.tbar.resize = this;
								}
							}
						}
						
					]
				},
				listeners: {
					added: function() {
						GIS.cmp.region.center = this;
					}
				}
            }
		],
		listeners: {
			render: GIS.init.onRender,
			afterrender: GIS.init.afterRender
		}
	});
	
	}});
});
