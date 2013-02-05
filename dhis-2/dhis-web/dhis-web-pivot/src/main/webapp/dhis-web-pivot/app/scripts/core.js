PT = {};

PT.core = {};

Ext.onReady( function() {

PT.core.getConfigs = function() {
	var conf = {};

	conf.finals = {
        ajax: {
            path_pivot: '../',
            path_pivot_static: 'dhis-web-pivot/',
            path_api: '../../api/',
            path_commons: '../../dhis-web-commons-ajax-json/',
            path_lib: '../../dhis-web-commons/javascripts/',
            path_images: 'images/',
            initialize: 'initialize.action',
            redirect: 'dhis-web-commons-about/redirect.action',
            data_get: 'chartValues.json',
            indicator_get: 'indicatorGroups/',
            indicator_getall: 'indicators.json?paging=false&links=false',
            indicatorgroup_get: 'indicatorGroups.json?paging=false&links=false',
            dataelement_get: 'dataElementGroups/',
            dataelement_getall: 'dataElements.json?paging=false&links=false',
            dataelementgroup_get: 'dataElementGroups.json?paging=false&links=false',
            dataset_get: 'dataSets.json?paging=false&links=false',
            organisationunit_getbygroup: 'getOrganisationUnitPathsByGroup.action',
            organisationunit_getbylevel: 'getOrganisationUnitPathsByLevel.action',
            organisationunit_getbyids: 'getOrganisationUnitPaths.action',
            organisationunitgroup_getall: 'organisationUnitGroups.json?paging=false&links=false',
            organisationunitgroupset_get: 'getOrganisationUnitGroupSetsMinified.action',
            organisationunitlevel_getall: 'organisationUnitLevels.json?paging=false&links=false&viewClass=detailed',
            organisationunitchildren_get: 'getOrganisationUnitChildren.action',
            favorite_addorupdate: 'addOrUpdateChart.action',
            favorite_addorupdatesystem: 'addOrUpdateSystemChart.action',
            favorite_updatename: 'updateChartName.action',
            favorite_get: 'charts/',
            favorite_getall: 'getSystemAndCurrentUserCharts.action',
            favorite_delete: 'deleteCharts.action'
        },
        dimension: {
            data: {
                value: 'data',
                rawvalue: 'Data', //i18n PT.i18n.data,
                paramname: 'dx',
                warning: {
					filter: '...'//PT.i18n.wm_multiple_filter_ind_de
				}
            },
            category: {
				paramname: 'coc'
			},
            indicator: {
                value: 'indicator',
                rawvalue: 'Indicator', //i18n PT.i18n.indicator,
                paramname: 'dx'
            },
            dataelement: {
                value: 'dataelement',
                rawvalue: 'Data element', //i18n PT.i18n.data_element,
                paramname: 'dx'
            },
            dataset: {
				value: 'dataset',
                rawvalue: 'Data set', //i18n PT.i18n.dataset,
                paramname: 'dx'
			},
            period: {
                value: 'period',
                rawvalue: 'Period', //i18n PT.i18n.period,
                paramname: 'pe',
                warning: {
					filter: '...'//PT.i18n.wm_multiple_filter_period
				}
            },
            organisationunit: {
                value: 'organisationunit',
                rawvalue: 'Organisation unit', //i18n PT.i18n.organisation_unit,
                paramname: 'ou',
                warning: {
					filter: '...'//PT.i18n.wm_multiple_filter_orgunit
				}
            },
            organisationunitgroup: {
				value: 'organisationunitgroup'
			},
			value: {
				value: 'value'
			}
        },
        root: {
			id: 'root'
		}
	};

	conf.period = {
		relativeperiodunits: {
			LAST_MONTH: 1,
			LAST_3_MONTHS: 3,
			LAST_12_MONTHS: 12,
			LAST_QUARTER: 1,
			LAST_4_QUARTERS: 4,
			LAST_SIX_MONTH: 1,
			LAST_2_SIXMONTHS: 2,
			THIS_YEAR: 1,
			LAST_YEAR: 1,
			LAST_5_YEARS: 5
		},
		periodtypes: [
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
	};

	conf.layout = {
        west_width: 424,
        west_fieldset_width: 416,
        west_width_padding: 18,
        west_fill: 117,
        west_fill_accordion_indicator: 77,
        west_fill_accordion_dataelement: 77,
        west_fill_accordion_dataset: 45,
        west_fill_accordion_period: 270,
        //west_fill_accordion_fixedperiod: 77,        
        west_fill_accordion_organisationunit: 103,
        west_maxheight_accordion_indicator: 478,
        west_maxheight_accordion_dataelement: 478,
        west_maxheight_accordion_dataset: 478,
        west_maxheight_accordion_period: 700,
        //west_maxheight_accordion_relativeperiod: 423,
        //west_maxheight_accordion_fixedperiod: 478,
        west_maxheight_accordion_organisationunit: 756,
        west_maxheight_accordion_organisationunitgroup: 298,
        west_maxheight_accordion_options: 449,
        east_tbar_height: 31,
        east_gridcolumn_height: 30,
        form_label_width: 55,
        window_favorite_ypos: 100,
        window_confirm_width: 250,
        window_share_width: 500,
        grid_favorite_width: 420,
        treepanel_minheight: 135,
        treepanel_maxheight: 400,
        treepanel_fill_default: 310,
        treepanel_toolbar_menu_width_group: 140,
        treepanel_toolbar_menu_width_level: 120,
        multiselect_minheight: 100,
        multiselect_maxheight: 250,
        multiselect_fill_default: 345,
        multiselect_fill_reportingrates: 315
    };

    conf.util = {
		jsonEncodeString: function(str) {
			return typeof str === 'string' ? str.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'') : str;
		},
		jsonEncodeArray: function(a) {
			for (var i = 0; i < a.length; i++) {
				a[i] = pt.conf.util.jsonEncodeString(a[i]);
			}
			return a;
		}
	};

	return conf;
};

PT.core.getUtils = function(pt) {
	var util = {};

	util.object = {
		getLength: function(object) {
			var size = 0;

			for (var key in object) {
				if (object.hasOwnProperty(key)) {
					size++;
				}
			}

			return size;
		}
	};

    util.url = {
		getParam: function(s) {
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
		}
	};

	util.viewport = {
		getSize: function() {
			return {x: PT.cmp.region.center.getWidth(), y: PT.cmp.region.center.getHeight()};
		},
		getXY: function() {
			return {x: PT.cmp.region.center.x + 15, y: PT.cmp.region.center.y + 43};
		},
		getPageCenterX: function(cmp) {
			return ((screen.width/2)-(cmp.width/2));
		},
		getPageCenterY: function(cmp) {
			return ((screen.height/2)-((cmp.height/2)-100));
		},
		resizeDimensions: function() {
			var a = [PT.cmp.dimension.indicator.panel, PT.cmp.dimension.dataelement.panel, PT.cmp.dimension.dataset.panel,
					PT.cmp.dimension.relativePeriod.panel, PT.cmp.dimension.fixedperiod.panel, PT.cmp.dimension.organisationUnit.panel,
					PT.cmp.dimension.organisationUnitGroup.panel, PT.cmp.options.panel];
			for (var i = 0; i < a.length; i++) {
				if (!a[i].collapsed) {
					a[i].fireEvent('expand');
				}
			}
		}
	};

	util.multiselect = {
		select: function(a, s) {
			var selected = a.getValue();
			if (selected.length) {
				var array = [];
				Ext.Array.each(selected, function(item) {
					array.push({id: item, name: a.store.getAt(a.store.findExact('id', item)).data.name});
				});
				s.store.add(array);
			}
			this.filterAvailable(a, s);
		},
		selectAll: function(a, s) {
			var array = [];
			a.store.each( function(r) {
				array.push({id: r.data.id, name: r.data.name});
			});
			s.store.add(array);
			this.filterAvailable(a, s);
		},
		unselect: function(a, s) {
			var selected = s.getValue();
			if (selected.length) {
				Ext.Array.each(selected, function(item) {
					s.store.remove(s.store.getAt(s.store.findExact('id', item)));
				});
				this.filterAvailable(a, s);
			}
		},
		unselectAll: function(a, s) {
			s.store.removeAll();
			a.store.clearFilter();
			this.filterAvailable(a, s);
		},
		filterAvailable: function(a, s) {
			a.store.filterBy( function(r) {
				var keep = true;
				s.store.each( function(r2) {
					if (r.data.id == r2.data.id) {
						keep = false;
					}

				});
				return keep;
			});
			a.store.sortStore();
		},
		setHeight: function(ms, panel, fill) {
			for (var i = 0; i < ms.length; i++) {
				ms[i].setHeight(panel.getHeight() - fill);
			}
		}
	};

	util.treepanel = {
		getHeight: function() {
			var h1 = PT.cmp.region.west.getHeight();
			var h2 = PT.cmp.options.panel.getHeight();
			var h = h1 - h2 - PT.conf.layout.treepanel_fill_default;
			var mx = PT.conf.layout.treepanel_maxheight;
			var mn = PT.conf.layout.treepanel_minheight;
			return h > mx ? mx : h < mn ? mn : h;
		}
	};

	util.store = {
		addToStorage: function(s, records) {
			s.each( function(r) {
				if (!s.storage[r.data.id]) {
					s.storage[r.data.id] = {id: r.data.id, name: r.data.name, parent: s.parent};
				}
			});
			if (records) {
				Ext.Array.each(records, function(r) {
					if (!s.storage[r.data.id]) {
						s.storage[r.data.id] = {id: r.data.id, name: r.data.name, parent: s.parent};
					}
				});
			}
		},
		loadFromStorage: function(s) {
			var items = [];
			s.removeAll();
			for (var obj in s.storage) {
				if (s.storage[obj].parent === s.parent) {
					items.push(s.storage[obj]);
				}
			}
			s.add(items);
			s.sort('name', 'ASC');
		},
		containsParent: function(s) {
			for (var obj in s.storage) {
				if (s.storage[obj].parent === s.parent) {
					return true;
				}
			}
			return false;
		}
	};

	util.mask = {
		showMask: function(cmp, msg) {
			cmp = cmp || pt.viewport;
			msg = msg || 'Loading..';
			
			if (pt.viewport.mask) {
				pt.viewport.mask.destroy();
			}
			pt.viewport.mask = new Ext.create('Ext.LoadMask', cmp, {
				id: 'pt-loadmask',
				shadow: false,
				msg: msg,
				style: 'box-shadow:0',
				bodyStyle: 'box-shadow:0'
			});
			pt.viewport.mask.show();
		},
		hideMask: function() {
			if (pt.viewport.mask) {
				pt.viewport.mask.hide();
			}
		}
	};

	util.checkbox = {
		setRelativePeriods: function(rp) {
			if (rp) {
				for (var r in rp) {
					var cmp = pt.util.getCmp('checkbox[paramName="' + r + '"]');
					if (cmp) {
						cmp.setValue(rp[r]);
					}
				}
			}
			else {
				PT.util.checkbox.setAllFalse();
			}
		},
		setAllFalse: function() {
			var a = pt.cmp.dimension.relativePeriod.checkbox;
			for (var i = 0; i < a.length; i++) {
				a[i].setValue(false);
			}
		},
		isAllFalse: function() {
			var a = pt.cmp.dimension.relativePeriod.checkbox;
			for (var i = 0; i < a.length; i++) {
				if (a[i].getValue()) {
					return false;
				}
			}
			return true;
		}
	};

	util.pivot = {
		getTable: function(settings, pt, container) {
			var getDimensionItemsFromSettings,
				getParamStringFromDimensionItems,

				validateResponse,
				extendResponse,
				extendDims,
				getDims,
				extendRowDims,
				getEmptyItem,
				getColItems,
				getRowItems,
				createTableArray,
				initialize;

			getDimensionItemsFromSettings = function() {
				var col = settings.col,
					row = settings.row,
					dimensionItems;

				dimensionItems = Ext.clone(col);
				Ext.apply(dimensionItems, row);

				return dimensionItems;
			};

			getParamStringFromDimensionItems = function(dimensionItems) {
				var paramString = '?',
					dim;

				for (var key in dimensionItems) {
					if (dimensionItems.hasOwnProperty(key)) {
						if (key === pt.conf.finals.dimension.category.paramname) {
							paramString += 'dimension=' + key + '&';
						}
						else {
							dim = dimensionItems[key];

							if (!(dim && Ext.isArray(dim) && dim.length)) {
								pt.util.mask.hideMask();
								alert('Empty dimension');
								return;
							}
							
							paramString += 'dimension=' + key + ':' + dim.join(';') + '&';
						}
					}
				}

				paramString = paramString.substring(0, paramString.length-1);

				for (var key in settings.filter) {
					if (settings.filter.hasOwnProperty(key)) {
						dim = settings.filter[key];
						paramString += '&filter=' + key + ':' + dim.join(';');
					}
				}

				return paramString;
			};

			validateResponse = function(response) {
				if (response.width < 1 || response.height < 1 || response.rows.length < 1) {
					alert('No values found');
					return false;
				}

				return true;
			};
		
			extendResponse = function(dimensionItems) {
				var response = pt.response,
					headers = response.headers,
					metaData = response.metaData,
					rows = response.rows;

				response.metaDataHeaderMap = {};
				response.nameHeaderMap = {};
				response.idValueMap = {};

				var extendHeaders = function() {

					// Extend headers: index, items (unique), size
					for (var i = 0, header, items; i < headers.length; i++) {
						header = headers[i];
						items = [];

						header.index = i;

						for (var j = 0; j < rows.length; j++) {
							items.push(rows[j][header.index]);
						}

						header.items = Ext.Array.unique(items);
						header.size = header.items.length;
					}

					// metaDataHeaderMap (metaDataId: header)
					for (var i = 0, header; i < headers.length; i++) {
						header = headers[i];

						if (header.meta) {
							for (var j = 0, item; j < header.items.length; j++) {
								item = header.items[j];

								response.metaDataHeaderMap[item] = header.name;
							}
						}
					}

					// nameHeaderMap (headerName: header)
					for (var i = 0, header; i < headers.length; i++) {
						header = headers[i];

						response.nameHeaderMap[header.name] = header;
					}

					// Remove all header items
					for (var i = 0, header; i < headers.length; i++) {
						header = headers[i];

						header.items = [];
					}

					// Add sorted header items based on metaData
					for (var key in metaData) {
						if (metaData.hasOwnProperty(key)) {
							var headerName = response.metaDataHeaderMap[key],
								header = response.nameHeaderMap[headerName];

							if (header) {
								header.items.push(key);
							}
						}
					}
				}();

				var createValueIds = function() {
					var valueHeaderIndex = response.nameHeaderMap[pt.conf.finals.dimension.value.value].index,
						dimensionNames = [];

					// Dimension names
					for (var key in dimensionItems) {
						if (dimensionItems.hasOwnProperty(key)) {
							dimensionNames.push(key);
						}
					}

					// idValueMap
					for (var i = 0, id; i < rows.length; i++) {
						id = '';

						for (var j = 0, header; j < dimensionNames.length; j++) {
							header = response.nameHeaderMap[dimensionNames[j]];

							id += rows[i][header.index];
						}

						response.idValueMap[id] = rows[i][valueHeaderIndex];
					}
				}();
			};

			extendDims = function(aUniqueItems) {
				//aUniqueItems	= [ [de1, de2, de3],
				//					[p1],
				//					[ou1, ou2, ou3, ou4] ]

				var nCols = 1,
					aNumCols = [],
					aAccNumCols = [],
					aSpan = [],
					aGuiItems = [],
					aAllItems = [],
					aColIds = [];

				for (var i = 0, dim; i < aUniqueItems.length; i++) {
					nNumCols = aUniqueItems[i].length;

					aNumCols.push(nNumCols);
					nCols = nCols * nNumCols;
					aAccNumCols.push(nCols);
				}

			console.log("");
			console.log("aNumCols", aNumCols);
			console.log("nCols", nCols);
			console.log("aAccNumCols", aAccNumCols);

				//aNumCols		= [3, 1, 4]
				//nCols			= 12 (3 * 1 * 4)
				//aAccNumCols	= [3, 3, 12]

				for (var i = 0; i < aUniqueItems.length; i++) {
					aSpan.push(aNumCols[i] === 1 ? nCols : nCols / aAccNumCols[i]); //if one, span all
				}

			console.log("aSpan", aSpan);

				//aSpan			= [10, 2, 1]

				aGuiItems.push(aUniqueItems[0]);

				if (aUniqueItems.length > 1) {
					for (var i = 1, a, n; i < aUniqueItems.length; i++) {
						a = [];
						n = aNumCols[i] === 1 ? 1 : aAccNumCols[i-1];

						for (var j = 0; j < n; j++) {
							a = a.concat(aUniqueItems[i]);
						}

						aGuiItems.push(a);
					}
				}

			console.log("aGuiItems", aGuiItems);
				//aGuiItems	= [ [d1, d2, d3], (3)
				//				[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (15)
				//				[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2...] (30)
				//		  	  ]


				for (var i = 0, dimItems, span; i < aUniqueItems.length; i++) {
					dimItems = [];
					span = aSpan[i];

					if (i === 0) {
						for (var j = 0; j < aUniqueItems[i].length; j++) {
							for (var k = 0; k < span; k++) {
								dimItems.push(aUniqueItems[i][j]);
							}
						}
					}
					else {
						var factor = nCols / aUniqueItems[i].length;

						for (var k = 0; k < factor; k++) {
							dimItems = dimItems.concat(aUniqueItems[i]);
						}
					}

					aAllItems.push(dimItems);
				}

			console.log("aAllItems", aAllItems);
				//aAllItems	= [ [d1, d1, d1, d1, d1, d1, d1, d1, d1, d1, d2, d2, d2, d2, d2, d2, d2, d2, d2, d2, d3, d3, d3, d3, d3, d3, d3, d3, d3, d3], (30)
				//				[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (30)
				//				[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2] (30)
				//		  	  ]

				for (var i = 0, id; i < nCols; i++) {
					id = '';

					for (var j = 0; j < aAllItems.length; j++) {
						id += aAllItems[j][i];
					}

					aColIds.push(id);
				}

			console.log("aColIds", aColIds);
				//aColIds	= [ aaaaaaaaBBBBBBBBccccccc, aaaaaaaaaccccccccccbbbbbbbbbb, ... ]


			console.log("");
				return {
					items: {
						unique: aUniqueItems,
						gui: aGuiItems,
						all: aAllItems
					},
					ids: aColIds,
					span: aSpan,
					dims: aUniqueItems.length,
					size: nCols
				};
			};

			getDims = function() {
				var response = pt.response,
					col = settings.col,
					row = settings.row,
					getUniqueColsArray,
					getUniqueRowsArray;

				getUniqueColsArray = function() {
					var a = [];

					for (var dim in col) {
						if (col.hasOwnProperty(dim)) {
							a.push(response.nameHeaderMap[dim].items);
						}
					}

					return a;
				};

				getUniqueRowsArray = function() {
					var a = [];

					for (var dim in row) {
						if (row.hasOwnProperty(dim)) {
							a.push(response.nameHeaderMap[dim].items);
						}
					}

					return a;
				};

				// aUniqueCols ->  [[p1, p2, p3], [ou1, ou2, ou3, ou4]]

				return {
					cols: extendDims(getUniqueColsArray()),
					rows: extendDims(getUniqueRowsArray())
				};
			};

			extendRowDims = function(rows) {
				var all = rows.items.all,
					allObjects = [];

				for (var i = 0, allRow; i < all.length; i++) {
					allRow = [];

					for (var j = 0; j < all[i].length; j++) {
						allRow.push({
							id: all[i][j]
						});
					}

					allObjects.push(allRow);
				}

				for (var i = 0; i < allObjects.length; i++) {
					for (var j = 0, object; j < allObjects[i].length; j += rows.span[i]) {
						object = allObjects[i][j];
						object.rowSpan = rows.span[i];
					}
				}

				rows.items.allObjects = allObjects;
			};

			getEmptyItem = function() {
				//return {
					//colspan: pt.config.rows.dims,
					//rowspan: pt.config.cols.dims,
					//baseCls: 'pivot-empty'
				//};

				return '<td class="pivot-empty" colspan="' + pt.config.rows.dims + '" rowspan="' + pt.config.cols.dims + '"></td>';
			};

			getColItems = function() {
				var response = pt.response,
					rows = pt.config.rows,
					cols = pt.config.cols,
					colItems = [];

				for (var i = 0, dimItems, colSpan, rowArray; i < cols.dims; i++) {
					dimItems = cols.items.gui[i];
					colSpan = cols.span[i];
					rowArray = [];

					if (i === 0) {
						rowArray.push(getEmptyItem());
					}

					for (var j = 0, id; j < dimItems.length; j++) {
						id = dimItems[j];						
						rowArray.push('<td class="pivot-dim" colspan="' + colSpan + '">' + response.metaData[id] + '</td>');

						if (i === 0 && j === (dimItems.length - 1)) {
							rowArray.push('<td class="pivot-dimtotal" rowspan="' + cols.dims + '">Total</td>');
						}
					}

					colItems.push(rowArray);
				}

				return colItems;
			};

			getRowItems = function() {
				var response = pt.response,
					rows = pt.config.rows,
					cols = pt.config.cols,
					size = rows.size,
					dims = rows.dims,
					allObjects = rows.items.allObjects,
					dimHtmlItems = [],
					valueItems = [],
					valueHtmlItems = [],
					totalRowItems = [],
					totalRowHtmlItems = [],
					totalColItems = [],
					totalColHtmlItems = [],
					grandTotalItem = 0,
					grandTotalHtmlItem;

				// Value items
				for (var i = 0, row; i < size; i++) {
					row = [];

					for (var j = 0, id, value, row; j < pt.config.cols.size; j++) {
						id = cols.ids[j] + rows.ids[i];
						value = response.idValueMap[id] ? parseFloat(response.idValueMap[id]) : 0;
						row.push(value);
					}

					valueItems.push(row);
				}

				// Value html items
				for (var i = 0, row; i < valueItems.length; i++) {
					row = [];

					for (var j = 0, id, value, cls; j < valueItems[i].length; j++) {
						id = cols.ids[j] + rows.ids[i];
						value = valueItems[i][j];

						//if (Ext.isNumber(value)) {
							//cls = value < 5000 ? 'bad' : (value < 20000 ? 'medium' : 'good'); //basic legendset
						//}

						row.push('<td id="' + id + '" class="pivot-value">' + value + '</td>');
					}

					valueHtmlItems.push(row);
				}

				// Total row items
				for (var i = 0, rowSum; i < valueItems.length; i++) {
					rowSum = Ext.Array.sum(valueItems[i]);
					totalRowItems.push(rowSum);
				}

				// Total row html items
				for (var i = 0, rowSum; i < totalRowItems.length; i++) {
					rowSum = totalRowItems[i];

					totalRowHtmlItems.push('<td class="pivot-valuetotal">' + rowSum.toString() + '</td>');
				}

				// Total col items
				for (var i = 0, colSum; i < valueItems[0].length; i++) {
					colSum = 0;

					for (var j = 0; j < valueItems.length; j++) {
						colSum += valueItems[j][i];
					}

					totalColItems.push(colSum);
				}

				// Total col html items
				for (var i = 0, colSum; i < totalColItems.length; i++) {
					colSum = totalColItems[i];

					totalColHtmlItems.push('<td class="pivot-valuetotal">' + colSum.toString() + '</td>');
				}

				// Grand total item
				grandTotalItem = Ext.Array.sum(totalColItems);

				// Grand total html item
				grandTotalHtmlItem = '<td class="pivot-valuegrandtotal">' + grandTotalItem.toString() + '</td>';

				// GUI

				// Dim html items
				for (var i = 0, row; i < size; i++) {
					row = [];
					
					for (var j = 0, object; j < dims; j++) {
						object = allObjects[j][i];

						if (object.rowSpan) {
							row.push('<td class="pivot-dim" rowspan="' + object.rowSpan + '">' + response.metaData[object.id] + '</td>');
						}
					}

					row = row.concat(valueHtmlItems[i]);
					row = row.concat(totalRowHtmlItems[i]);

					dimHtmlItems.push(row);
				}

				// Final row
				var finalRow = [];

				finalRow.push('<td class="pivot-dimtotal" colspan="' + rows.dims + '">Total</td>');

				finalRow = finalRow.concat(totalColHtmlItems);
				finalRow = finalRow.concat(grandTotalHtmlItem);

				dimHtmlItems.push(finalRow);

				return dimHtmlItems;
			};

			createTablePanel = function(items) {
				var html = '<table>';

				for (var i = 0; i < items.length; i++) {
					html += '<tr>' + items[i].join('') + '</tr>';
				}

				html += '</table>';

				return Ext.create('Ext.panel.Panel', {
					bodyStyle: 'border:0 none',
					autoScroll: true,
					html: html
				});
				
				//var config = {
					//layout: {
						//type: 'table',
						//columns: pt.config.cols.size + pt.config.rows.dims + 1
					//},
					//autoScroll: true,
					//bodyStyle: 'border:0 none',
					//defaults: {
						//baseCls: 'td'
					//},
					//listeners: {
						//resize: function(p) {
							//addTdClasses(p);
						//}
					//}
				//};

				//if (pt.el) {
					//config.renderTo = pt.el;
				//};

				//return Ext.create('Ext.panel.Panel', config);
			};
			
			initialize = function() {
				var dimensionItems,
					paramString;

				pt.util.mask.showMask();

				dimensionItems = getDimensionItemsFromSettings(settings);

				paramString = getParamStringFromDimensionItems(dimensionItems);

				Ext.data.JsonP.request({
					method: 'GET',
					url: pt.init.contextPath + '/api/analytics.jsonp' + paramString,
					callbackName: 'analytics',
					headers: {
						'Content-Type': 'application/json',
						'Accept': 'application/json'
					},
					disableCaching: false,
					failure: function() {
						pt.util.mask.hideMask();
						console.log(arguments);
						alert('Data request failed');
					},						
					success: function(r) {
						var panel,
							items = [];

						if (!validateResponse(r)) {
							pt.util.mask.hideMask();
							console.log(r);
							alert('Data response invalid');
							return;
						}
						
						pt.response = r;
//todo
pt.response.metaData['PT59n8BQbqM'] = '(Outreach)';
pt.response.metaData['pq2XI5kz2BY'] = '(Fixed)';

						extendResponse(dimensionItems);
						pt.config = getDims();
						extendRowDims(pt.config.rows);

						items = getColItems();						
						items = items.concat(getRowItems());
						
						panel = createTablePanel(items);

						if (!pt.el) {
							container.removeAll(true);
							container.add(panel);
						}
						
						pt.util.mask.hideMask();
					}
				});

			}();
		}
	};

	return util;
};

PT.core.getAPI = function(pt) {
	var api = {};

	api.Settings = function(config) {
		var col,
			row,
			filter,
			settings = {};

		if (!(config && Ext.isObject(config))) {
			alert('Settings config is not an object'); //i18n
			return;
		}

		col = (config.col && Ext.isObject(config.col) && pt.util.object.getLength(config.col)) ? config.col : null;

		row = (config.row && Ext.isObject(config.row) && pt.util.object.getLength(config.row)) ? config.row : null;

		if (!(col || row)) {
			alert('No col or row items selected'); //i18n
			return;
		}

		filter = config.filter;

		if (!(filter === undefined || Ext.isObject(filter))) {
			alert('Illegal filter type'); //i18n
			return;
		}

		if (col) {
			settings.col = col;
		}

		if (row) {
			settings.row = row;
		}

		settings.filter = filter;

		return settings;
	};

	return api;
};

PT.core.getInstance = function(config) {
	var pt = {};

	pt.baseUrl = config && config.baseUrl ? config.baseUrl : '../../';
	pt.el = config && config.el ? config.el : null;

	pt.conf = PT.core.getConfigs();
	pt.util = PT.core.getUtils(pt);
	pt.api = PT.core.getAPI(pt);

	return pt;
};

});
