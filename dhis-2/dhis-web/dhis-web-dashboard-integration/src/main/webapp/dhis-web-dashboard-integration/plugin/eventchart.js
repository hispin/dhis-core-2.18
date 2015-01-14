Ext.onReady( function() {

	// SIMPLE REGRESSION
	function SimpleRegression()
	{
		var sumX = 0; // Sum of x values
		var sumY = 0; // Sum of y values
		var sumXX = 0; // Total variation in x
		var sumXY = 0; // Sum of products
		var n = 0; // Number of observations
		var xbar = 0; // Mean of accumulated x values, used in updating formulas
		var ybar = 0; // Mean of accumulated y values, used in updating formulas

		this.addData = function( x, y )
		{
			if ( n == 0 )
			{
				xbar = x;
				ybar = y;
			}
			else
			{
				var dx = x - xbar;
				var dy = y - ybar;
				sumXX += dx * dx * n / ( n + 1 );
				sumXY += dx * dy * n / ( n + 1 );
				xbar += dx / ( n + 1 );
				ybar += dy / ( n + 1 );
			}

			sumX += x;
			sumY += y;
			n++;
		};

		this.predict = function( x )
		{
			var b1 = this.getSlope();

			return this.getIntercept( b1 ) + b1 * x;
		};

		this.getSlope = function()
		{
			if ( n < 2 )
			{
				return Number.NaN;
			}

			return sumXY / sumXX;
		};

		this.getIntercept = function( slope )
		{
			return ( sumY - slope * sumX ) / n;
		};
	}

	// CORE

	// ext config
	Ext.Ajax.method = 'GET';

    Ext.isIE = function() {
        return /trident/.test(Ext.userAgent);
    }();

    // override
    Ext.override(Ext.chart.Chart, {
        insetPaddingObject: {},

        alignAxes: function() {
            var me = this,
                axes = me.axes,
                legend = me.legend,
                edges = ['top', 'right', 'bottom', 'left'],
                chartBBox,
                insetPadding = me.insetPadding,
                insetPaddingObject = me.insetPaddingObject,
                insets = {
                    top: insetPaddingObject.top || insetPadding,
                    right: insetPaddingObject.right || insetPadding,
                    bottom: insetPaddingObject.bottom || insetPadding,
                    left: insetPaddingObject.left || insetPadding
                };

            function getAxis(edge) {
                var i = axes.findIndex('position', edge);
                return (i < 0) ? null : axes.getAt(i);
            }


            Ext.each(edges, function(edge) {
                var isVertical = (edge === 'left' || edge === 'right'),
                    axis = getAxis(edge),
                    bbox;


                if (legend !== false) {
                    if (legend.position === edge) {
                        bbox = legend.getBBox();
                        insets[edge] += (isVertical ? bbox.width : bbox.height) + insets[edge];
                    }
                }



                if (axis && axis.bbox) {
                    bbox = axis.bbox;
                    insets[edge] += (isVertical ? bbox.width : bbox.height);
                }
            });

            chartBBox = {
                x: insets.left,
                y: insets.top,
                width: me.curWidth - insets.left - insets.right,
                height: me.curHeight - insets.top - insets.bottom
            };
            me.chartBBox = chartBBox;



            axes.each(function(axis) {
                var pos = axis.position,
                    isVertical = (pos === 'left' || pos === 'right');

                axis.x = (pos === 'right' ? chartBBox.x + chartBBox.width : chartBBox.x);
                axis.y = (pos === 'top' ? chartBBox.y : chartBBox.y + chartBBox.height);
                axis.width = (isVertical ? chartBBox.width : chartBBox.height);
                axis.length = (isVertical ? chartBBox.height : chartBBox.width);
            });
        }
    });

    Ext.override(Ext.chart.Legend, {
        updatePosition: function() {
            var me = this,
                x, y,
                legendWidth = me.width,
                legendHeight = me.height,
                padding = me.padding,
                chart = me.chart,
                chartBBox = chart.chartBBox,
                insets = chart.insetPadding,
                chartWidth = chartBBox.width - (insets * 2),
                chartHeight = chartBBox.height - (insets * 2),
                chartX = chartBBox.x + insets,
                chartY = chartBBox.y + insets,
                surface = chart.surface,
                mfloor = Math.floor;

            if (me.isDisplayed()) {
                // Find the position based on the dimensions
                switch(me.position) {
                    case "left":
                        x = insets;
                        y = mfloor(chartY + chartHeight / 2 - legendHeight / 2);
                        break;
                    case "right":
                        x = mfloor(surface.width - legendWidth) - insets;
                        y = mfloor(chartY + chartHeight / 2 - legendHeight / 2);
                        break;
                    case "top":
                        x = mfloor((chartX + chartBBox.width) / 2 - legendWidth / 2) - 7;
                        y = insets;
                        break;
                    case "bottom":
                        x = mfloor(chartX + chartWidth / 2 - legendWidth / 2);
                        y = mfloor(surface.height - legendHeight) - insets;
                        break;
                    default:
                        x = mfloor(me.origX) + insets;
                        y = mfloor(me.origY) + insets;
                }
                me.x = x;
                me.y = y;

                // Update the position of each item
                Ext.each(me.items, function(item) {
                    item.updatePosition();
                });
                // Update the position of the outer box
                me.boxSprite.setAttributes(me.getBBox(), true);
            }
        }
    });

    Ext.override(Ext.chart.LegendItem, {
        createLegend: function(config) {
            var me = this,
                index = config.yFieldIndex,
                series = me.series,
                seriesType = series.type,
                idx = me.yFieldIndex,
                legend = me.legend,
                surface = me.surface,
                refX = legend.x + me.x,
                refY = legend.y + me.y,
                bbox, z = me.zIndex,
                markerConfig, label, mask,
                radius, toggle = false,
                seriesStyle = Ext.apply(series.seriesStyle, series.style),
                labelMarkerSize = legend.labelMarkerSize || 12;

            function getSeriesProp(name) {
                var val = series[name];
                return (Ext.isArray(val) ? val[idx] : val);
            }

            label = me.add('label', surface.add({
                type: 'text',
                x: 0,
                y: 0,
                zIndex: z || 0,
                font: legend.labelFont,
                fill: legend.labelColor || '#000',
                text: getSeriesProp('title') || getSeriesProp('yField')
            }));

            if (seriesType === 'line' || seriesType === 'scatter') {
                if (seriesType === 'line') {
                    me.add('line', surface.add({
                        type: 'path',
                        path: 'M0.5,0.5L16.5,0.5',
                        zIndex: z,
                        "stroke-width": series.lineWidth,
                        "stroke-linejoin": "round",
                        "stroke-dasharray": series.dash,
                        stroke: seriesStyle.stroke || '#000',
                        style: {
                            cursor: 'pointer'
                        }
                    }));
                }
                if (series.showMarkers || seriesType === 'scatter') {
                    markerConfig = Ext.apply(series.markerStyle, series.markerConfig || {});
                    me.add('marker', Ext.chart.Shape[markerConfig.type](surface, {
                        fill: markerConfig.fill,
                        x: 8.5,
                        y: 0.5,
                        zIndex: z,
                        radius: markerConfig.radius || markerConfig.size,
                        style: {
                            cursor: 'pointer'
                        }
                    }));
                }
            }
            else {
                me.add('box', surface.add({
                    type: 'rect',
                    zIndex: z,
                    x: 6,
                    y: 0,
                    width: labelMarkerSize,
                    height: labelMarkerSize,
                    fill: series.getLegendColor(index),
                    style: {
                        cursor: 'pointer'
                    }
                }));
            }

            me.setAttributes({
                hidden: false
            }, true);

            bbox = me.getBBox();

            mask = me.add('mask', surface.add({
                type: 'rect',
                x: bbox.x,
                y: bbox.y,
                width: bbox.width || 20,
                height: bbox.height || 20,
                zIndex: (z || 0) + 1000,
                fill: '#f00',
                opacity: 0,
                style: {
                    'cursor': 'pointer'
                }
            }));


            me.on('mouseover', function() {
                label.setStyle({
                    'font-weight': 'bold'
                });
                mask.setStyle({
                    'cursor': 'pointer'
                });
                series._index = index;
                series.highlightItem();
            }, me);

            me.on('mouseout', function() {
                label.setStyle({
                    'font-weight': 'normal'
                });
                series._index = index;
                series.unHighlightItem();
            }, me);

            if (!series.visibleInLegend(index)) {
                toggle = true;
                label.setAttributes({
                   opacity: 0.5
                }, true);
            }

            me.on('mousedown', function() {
                if (!toggle) {
                    series.hideAll();
                    label.setAttributes({
                        opacity: 0.5
                    }, true);
                } else {
                    series.showAll();
                    label.setAttributes({
                        opacity: 1
                    }, true);
                }
                toggle = !toggle;
            }, me);
            me.updatePosition({x:0, y:0});
        }
    });

    Ext.override(Ext.chart.axis.Axis, {
        drawHorizontalLabels: function() {
            var me = this,
                labelConf = me.label,
                floor = Math.floor,
                max = Math.max,
                axes = me.chart.axes,
                position = me.position,
                inflections = me.inflections,
                ln = inflections.length,
                labels = me.labels,
                labelGroup = me.labelGroup,
                maxHeight = 0,
                ratio,
                gutterY = me.chart.maxGutter[1],
                ubbox, bbox, point, prevX, prevLabel,
                projectedWidth = 0,
                textLabel, attr, textRight, text,
                label, last, x, y, i, firstLabel;

            last = ln - 1;
            // get a reference to the first text label dimensions
            point = inflections[0];
            firstLabel = me.getOrCreateLabel(0, me.label.renderer(labels[0]));
            ratio = Math.floor(Math.abs(Math.sin(labelConf.rotate && (labelConf.rotate.degrees * Math.PI / 180) || 0)));

            for (i = 0; i < ln; i++) {
                point = inflections[i];
                text = me.label.renderer(labels[i]) || '';
                textLabel = me.getOrCreateLabel(i, text);
                bbox = textLabel._bbox;
                maxHeight = max(maxHeight, bbox.height + me.dashSize + me.label.padding);
                x = floor(point[0] - (ratio? bbox.height : bbox.width) / 2);
                if (me.chart.maxGutter[0] == 0) {
                    if (i == 0 && axes.findIndex('position', 'left') == -1) {
                        x = point[0];
                    }
                    else if (i == last && axes.findIndex('position', 'right') == -1) {
                        x = point[0] - bbox.width;
                    }
                }
                if (position == 'top') {
                    y = point[1] - (me.dashSize * 2) - me.label.padding - (bbox.height / 2);
                }
                else {
                    y = point[1] + (me.dashSize * 2) + me.label.padding + (bbox.height / 2);
                }

                var moveLabels = labelConf.rotate && labelConf.rotate.degrees && !Ext.Array.contains([0,90,180,270,360], labelConf.rotate.degrees),
                    adjust = Math.floor((textLabel.text.length - 12) * -1 * 0.75),
                    newX = moveLabels ? point[0] - textLabel._bbox.width + adjust: x;

                textLabel.setAttributes({
                    hidden: false,
                    x: newX,
                    y: y
                }, true);

                // skip label if there isn't available minimum space
                if (i != 0 && (me.intersect(textLabel, prevLabel)
                    || me.intersect(textLabel, firstLabel))) {
                    textLabel.hide(true);
                    continue;
                }

                prevLabel = textLabel;
            }

            return maxHeight;
        }
    });

	// namespace
	EV = {};

	EV.instances = [];
	EV.i18n = {};
	EV.isDebug = false;
	EV.isSessionStorage = ('sessionStorage' in window && window['sessionStorage'] !== null);

    // core
	EV.getCore = function(init) {
        var conf = {},
            api = {},
            support = {},
            service = {},
            web = {},
            dimConf;

		// conf
		(function() {
			conf.finals = {
				dimension: {
					data: {
						value: 'data',
						name: EV.i18n.data,
						dimensionName: 'dx',
						objectName: 'dx',
						warning: {
							filter: '...'//EV.i18n.wm_multiple_filter_ind_de
						}
					},
					category: {
						name: EV.i18n.categories,
						dimensionName: 'co',
						objectName: 'co',
					},
					indicator: {
						value: 'indicators',
						name: EV.i18n.indicators,
						dimensionName: 'dx',
						objectName: 'in'
					},
					dataElement: {
						value: 'dataElements',
						name: EV.i18n.data_elements,
						dimensionName: 'dx',
						objectName: 'de'
					},
					operand: {
						value: 'operand',
						name: 'Operand',
						dimensionName: 'dx',
						objectName: 'dc'
					},
					dataSet: {
						value: 'dataSets',
						name: EV.i18n.data_sets,
						dimensionName: 'dx',
						objectName: 'ds'
					},
					period: {
						value: 'period',
						name: EV.i18n.periods,
						dimensionName: 'pe',
						objectName: 'pe'
					},
					fixedPeriod: {
						value: 'periods'
					},
					relativePeriod: {
						value: 'relativePeriods',
						name: EV.i18n.relative_periods
					},
                    startEndDate: {
                        value: 'dates',
                        name: EV.i18n.start_end_dates
                    },
					organisationUnit: {
						value: 'organisationUnits',
						name: EV.i18n.organisation_units,
						dimensionName: 'ou',
						objectName: 'ou'
					},
					dimension: {
						value: 'dimension'
						//objectName: 'di'
					},
					value: {
						value: 'value'
					}
				},
                chart: {
                    series: 'series',
                    category: 'category',
                    filter: 'filter',
                    column: 'column',
                    stackedcolumn: 'stackedcolumn',
                    bar: 'bar',
                    stackedbar: 'stackedbar',
                    line: 'line',
                    area: 'area',
                    pie: 'pie',
                    radar: 'radar'
                },
                data: {
                    domain: 'domain_',
                    targetLine: 'targetline_',
                    baseLine: 'baseline_',
                    trendLine: 'trendline_'
                },
                image: {
                    png: 'png',
                    pdf: 'pdf'
                },
                cmd: {
                    init: 'init_',
                    none: 'none_',
                    urlparam: 'id'
                },
                root: {
                    id: 'root'
                }
			};

			dimConf = conf.finals.dimension;

			dimConf.objectNameMap = {};
			dimConf.objectNameMap[dimConf.data.objectName] = dimConf.data;
			dimConf.objectNameMap[dimConf.indicator.objectName] = dimConf.indicator;
			dimConf.objectNameMap[dimConf.dataElement.objectName] = dimConf.dataElement;
			dimConf.objectNameMap[dimConf.operand.objectName] = dimConf.operand;
			dimConf.objectNameMap[dimConf.dataSet.objectName] = dimConf.dataSet;
			dimConf.objectNameMap[dimConf.category.objectName] = dimConf.category;
			dimConf.objectNameMap[dimConf.period.objectName] = dimConf.period;
			dimConf.objectNameMap[dimConf.organisationUnit.objectName] = dimConf.organisationUnit;
			dimConf.objectNameMap[dimConf.dimension.objectName] = dimConf.dimension;

			conf.period = {
				periodTypes: [
					{id: 'Daily', name: EV.i18n.daily},
					{id: 'Weekly', name: EV.i18n.weekly},
					{id: 'Monthly', name: EV.i18n.monthly},
					{id: 'BiMonthly', name: EV.i18n.bimonthly},
					{id: 'Quarterly', name: EV.i18n.quarterly},
					{id: 'SixMonthly', name: EV.i18n.sixmonthly},
					{id: 'Yearly', name: EV.i18n.yearly},
					{id: 'FinancialOct', name: EV.i18n.financial_oct},
					{id: 'FinancialJuly', name: EV.i18n.financial_july},
					{id: 'FinancialApril', name: EV.i18n.financial_april}
				]
			};

			conf.layout = {
				west_width: 452,
				west_fill: 2,
                west_fill_accordion_indicator: 56,
                west_fill_accordion_dataelement: 59,
                west_fill_accordion_dataset: 31,
                west_fill_accordion_period: 307,
                west_fill_accordion_organisationunit: 58,
                west_maxheight_accordion_indicator: 450,
                west_maxheight_accordion_dataset: 350,
                west_maxheight_accordion_period: 405,
                west_maxheight_accordion_organisationunit: 500,
                west_scrollbarheight_accordion_indicator: 300,
                west_scrollbarheight_accordion_dataset: 250,
                west_scrollbarheight_accordion_period: 405,
                west_scrollbarheight_accordion_organisationunit: 350,
				east_tbar_height: 31,
				east_gridcolumn_height: 30,
				form_label_width: 55,
				window_favorite_ypos: 100,
				window_confirm_width: 250,
				window_share_width: 500,
				grid_favorite_width: 420,
				grid_row_height: 27,
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

			conf.chart = {
                style: {
                    inset: 30,
                    fontFamily: 'Arial,Sans-serif,Lucida Grande,Ubuntu'
                },
                theme: {
                    dv1: ['#94ae0a', '#1d5991', '#a61120', '#ff8809', '#7c7474', '#a61187', '#ffd13e', '#24ad9a', '#a66111', '#414141', '#4500c4', '#1d5700']
                }
            };

            conf.url = {
                analysisFields: [
                    '*',
                    'program[id,name]',
                    'programStage[id,name]',
                    'columns[dimension,filter,items[id,' + init.namePropertyUrl + ']]',
                    'rows[dimension,filter,items[id,' + init.namePropertyUrl + ']]',
                    'filters[dimension,filter,items[id,' + init.namePropertyUrl + ']]',
                    '!lastUpdated',
                    '!href',
                    '!created',
                    '!publicAccess',
                    '!rewindRelativePeriods',
                    '!userOrganisationUnit',
                    '!userOrganisationUnitChildren',
                    '!userOrganisationUnitGrandChildren',
                    '!externalAccess',
                    '!access',
                    '!relativePeriods',
                    '!columnDimensions',
                    '!rowDimensions',
                    '!filterDimensions',
                    '!user',
                    '!organisationUnitGroups',
                    '!itemOrganisationUnitGroups',
                    '!userGroupAccesses',
                    '!indicators',
                    '!dataElements',
                    '!dataElementOperands',
                    '!dataElementGroups',
                    '!dataSets',
                    '!periods',
                    '!organisationUnitLevels',
                    '!organisationUnits'
                ]
            };
		}());

		// api
		(function() {
			api.layout = {};

			api.layout.Record = function(config) {
				var config = Ext.clone(config);

				// id: string

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Record: config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.id)) {
						alert('Record: id is not text: ' + config);
						return;
					}

					return config;
				}();
			};

			api.layout.Dimension = function(config) {
				var config = Ext.clone(config);

				// dimension: string

				// items: [Record]

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Dimension: config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.dimension)) {
						console.log('Dimension: name is not a string: ' + config);
						return;
					}

					if (config.dimension !== conf.finals.dimension.category.objectName) {
						var records = [];

						//if (!Ext.isArray(config.items)) {
							//console.log('Dimension: items is not an array: ' + config);
							//return;
						//}

						//for (var i = 0; i < config.items.length; i++) {
							//records.push(api.layout.Record(config.items[i]));
						//}

						//config.items = Ext.Array.clean(records);

						//if (!config.items.length) {
							//console.log('Dimension: has no valid items: ' + config);
							//return;
						//}
					}

					return config;
				}();
			};

			api.layout.Layout = function(config, applyConfig) {
				var config = Ext.clone(config),
					layout = {},
					getValidatedDimensionArray,
					validateSpecialCases;

                // type: string ('column') - 'column', 'stackedcolumn', 'bar', 'stackedbar', 'line', 'area', 'pie'

                // program: object

                // programStage: object

				// columns: [Dimension]

				// rows: [Dimension]

				// filters: [Dimension]

                // showTrendLine: boolean (false)

                // targetLineValue: number

                // targetLineTitle: string

                // baseLineValue: number

                // baseLineTitle: string

                // sortOrder: number

                // rangeAxisMaxValue: number

                // rangeAxisMinValue: number

                // rangeAxisSteps: number

                // rangeAxisDecimals: number

                // showValues: boolean (true)

                    // showTotals: boolean (true)

                    // showSubTotals: boolean (true)

				// hideEmptyRows: boolean (false)

                    // aggregationType: string ('default') - 'default', 'count', 'sum'

                    // showHierarchy: boolean (false)

                    // displayDensity: string ('normal') - 'compact', 'normal', 'comfortable'

                    // fontSize: string ('normal') - 'small', 'normal', 'large'

                    // digitGroupSeparator: string ('space') - 'none', 'comma', 'space'

                    // legendSet: object

                // hideLegend: boolean (false)

                // hideTitle: boolean (false)

                // domainAxisTitle: string

                // rangeAxisTitle: string

                // userOrganisationUnit: boolean (false)

                // userOrganisationUnitChildren: boolean (false)

				// parentGraphMap: object

				// sorting: transient object

                    // reportingPeriod: boolean (false) //report tables only

                    // organisationUnit: boolean (false) //report tables only

                    // parentOrganisationUnit: boolean (false) //report tables only

				// regression: boolean (false)

				// cumulative: boolean (false)

				// sortOrder: integer (0) //-1, 0, 1

				// topLimit: integer (100) //5, 10, 20, 50, 100

				getValidatedDimensionArray = function(dimensionArray) {
					var dimensionArray = Ext.clone(dimensionArray);

					if (!(dimensionArray && Ext.isArray(dimensionArray) && dimensionArray.length)) {
						return;
					}

					for (var i = 0; i < dimensionArray.length; i++) {
						dimensionArray[i] = api.layout.Dimension(dimensionArray[i]);
					}

					dimensionArray = Ext.Array.clean(dimensionArray);

					return dimensionArray.length ? dimensionArray : null;
				};

				validateSpecialCases = function() {
					var dimConf = conf.finals.dimension,
						dimensions,
						objectNameDimensionMap = {};

					if (!layout) {
						return;
					}

					dimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [], layout.filters || []));

					for (var i = 0; i < dimensions.length; i++) {
						objectNameDimensionMap[dimensions[i].dimension] = dimensions[i];
					}

					if (layout.filters && layout.filters.length) {
						for (var i = 0; i < layout.filters.length; i++) {

							// Indicators as filter
							if (layout.filters[i].dimension === dimConf.indicator.objectName) {
								web.message.alert(EV.i18n.indicators_cannot_be_specified_as_filter || 'Indicators cannot be specified as filter');
								return;
							}

							// Categories as filter
							if (layout.filters[i].dimension === dimConf.category.objectName) {
								web.message.alert(EV.i18n.categories_cannot_be_specified_as_filter || 'Categories cannot be specified as filter');
								return;
							}

							// Data sets as filter
							if (layout.filters[i].dimension === dimConf.dataSet.objectName) {
								web.message.alert(EV.i18n.data_sets_cannot_be_specified_as_filter || 'Data sets cannot be specified as filter');
								return;
							}
						}
					}

					// dc and in
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.indicator.objectName]) {
						web.message.alert('Indicators and detailed data elements cannot be specified together');
						return;
					}

					// dc and de
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.dataElement.objectName]) {
						web.message.alert('Detailed data elements and totals cannot be specified together');
						return;
					}

					// dc and ds
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.dataSet.objectName]) {
						web.message.alert('Data sets and detailed data elements cannot be specified together');
						return;
					}

					// dc and co
					if (objectNameDimensionMap[dimConf.operand.objectName] && objectNameDimensionMap[dimConf.category.objectName]) {
						web.message.alert('Categories and detailed data elements cannot be specified together');
						return;
					}

					return true;
				};

				return function() {
					var objectNames = [],
						dimConf = conf.finals.dimension;

					// config must be an object
					if (!(config && Ext.isObject(config))) {
						console.log('Layout: config is not an object (' + init.el + ')');
						return;
					}

					// get object names
					for (var i = 0, dims = Ext.Array.clean([].concat(config.columns || [], config.rows || [], config.filters || [])); i < dims.length; i++) {

						// Object names
						if (api.layout.Dimension(dims[i])) {
							objectNames.push(dims[i].dimension);
						}
					}

                    // period
                    if (!Ext.Array.contains(objectNames, 'pe') && !(config.startDate && config.endDate)) {
                        alert('At least one fixed period, one relative period or start/end dates must be specified');
                        return;
                    }

					config.columns = getValidatedDimensionArray(config.columns);
					config.rows = getValidatedDimensionArray(config.rows);
					config.filters = getValidatedDimensionArray(config.filters);

					// column
					if (!config.columns) {
						alert('No series items selected');
						return;
					}

                    if (config.columns.length > 1) {
                        config.filters = config.filters || [];

                        config.filters = config.filters.concat(config.columns.splice(1));
                    }

					// row
					if (!config.rows) {
						alert('No category items selected');
						return;
					}

                    if (config.rows.length > 1) {
                        config.filters = config.filters || [];

                        config.filters = config.filters.concat(config.rows.splice(1));
                    }

					// favorite
					if (config.id) {
						layout.id = config.id;
					}

					if (config.name) {
						layout.name = config.name;
					}

					// layout
					layout.columns = config.columns;
					layout.rows = config.rows;
					layout.filters = config.filters;

                    layout.type = Ext.isString(config.type) ? config.type : 'column';
                    layout.program = config.program;
                    layout.programStage = config.programStage;

                    // dates
                    if (config.startDate && config.endDate) {
                        layout.startDate = config.startDate.substr(0,10);
                        layout.endDate = config.endDate.substr(0,10);
                    }

					// properties
                    layout.showValues = Ext.isBoolean(config.showData) ? config.showData : (Ext.isBoolean(config.showValues) ? config.showValues : true);
                    layout.hideEmptyRows = Ext.isBoolean(config.hideEmptyRows) ? config.hideEmptyRows : (Ext.isBoolean(config.hideEmptyRows) ? config.hideEmptyRows : true);
                    layout.showTrendLine = Ext.isBoolean(config.regression) ? config.regression : (Ext.isBoolean(config.showTrendLine) ? config.showTrendLine : false);
                    layout.targetLineValue = Ext.isNumber(config.targetLineValue) ? config.targetLineValue : null;
                    layout.targetLineTitle = Ext.isString(config.targetLineLabel) && !Ext.isEmpty(config.targetLineLabel) ? config.targetLineLabel :
                        (Ext.isString(config.targetLineTitle) && !Ext.isEmpty(config.targetLineTitle) ? config.targetLineTitle : null);
                    layout.baseLineValue = Ext.isNumber(config.baseLineValue) ? config.baseLineValue : null;
                    layout.baseLineTitle = Ext.isString(config.baseLineLabel) && !Ext.isEmpty(config.baseLineLabel) ? config.baseLineLabel :
                        (Ext.isString(config.baseLineTitle) && !Ext.isEmpty(config.baseLineTitle) ? config.baseLineTitle : null);
                    layout.sortOrder = Ext.isNumber(config.sortOrder) ? config.sortOrder : 0;

					layout.rangeAxisMaxValue = Ext.isNumber(config.rangeAxisMaxValue) ? config.rangeAxisMaxValue : null;
					layout.rangeAxisMinValue = Ext.isNumber(config.rangeAxisMinValue) ? config.rangeAxisMinValue : null;
					layout.rangeAxisSteps = Ext.isNumber(config.rangeAxisSteps) ? config.rangeAxisSteps : null;
					layout.rangeAxisDecimals = Ext.isNumber(config.rangeAxisDecimals) ? config.rangeAxisDecimals : null;
					layout.rangeAxisTitle = Ext.isString(config.rangeAxisLabel) && !Ext.isEmpty(config.rangeAxisLabel) ? config.rangeAxisLabel :
                        (Ext.isString(config.rangeAxisTitle) && !Ext.isEmpty(config.rangeAxisTitle) ? config.rangeAxisTitle : null);
					layout.domainAxisTitle = Ext.isString(config.domainAxisLabel) && !Ext.isEmpty(config.domainAxisLabel) ? config.domainAxisLabel :
                        (Ext.isString(config.domainAxisTitle) && !Ext.isEmpty(config.domainAxisTitle) ? config.domainAxisTitle : null);

                    layout.hideLegend = Ext.isBoolean(config.hideLegend) ? config.hideLegend : false;
                    layout.hideTitle = Ext.isBoolean(config.hideTitle) ? config.hideTitle : false;
                    layout.title = Ext.isString(config.title) &&  !Ext.isEmpty(config.title) ? config.title : null;

                    layout.parentGraphMap = Ext.isObject(config.parentGraphMap) ? config.parentGraphMap : null;

					//layout.sorting = Ext.isObject(config.sorting) && Ext.isDefined(config.sorting.id) && Ext.isString(config.sorting.direction) ? config.sorting : null;
					//layout.sortOrder = Ext.isNumber(config.sortOrder) ? config.sortOrder : 0;
					//layout.topLimit = Ext.isNumber(config.topLimit) ? config.topLimit : 0;

                    // style
                    if (Ext.isObject(config.domainAxisStyle)) {
                        layout.domainAxisStyle = config.domainAxisStyle;
                    }

                    if (Ext.isObject(config.rangeAxisStyle)) {
                        layout.rangeAxisStyle = config.rangeAxisStyle;
                    }

                    if (Ext.isObject(config.legendStyle)) {
                        layout.legendStyle = config.legendStyle;
                    }

                    if (Ext.isObject(config.seriesStyle)) {
                        layout.seriesStyle = config.seriesStyle;
                    }

					if (!validateSpecialCases()) {
						return;
					}

					return Ext.apply(layout, applyConfig);
				}();
			};

			api.response = {};

			api.response.Header = function(config) {
				var config = Ext.clone(config);

				// name: string

				// meta: boolean

				return function() {
					if (!Ext.isObject(config)) {
						console.log('Header: config is not an object: ' + config);
						return;
					}

					if (!Ext.isString(config.name)) {
						console.log('Header: name is not a string: ' + config);
						return;
					}

					if (!Ext.isBoolean(config.meta)) {
						console.log('Header: meta is not boolean: ' + config);
						return;
					}

					return config;
				}();
			};

			api.response.Response = function(config) {
				var config = Ext.clone(config);

				// headers: [Header]

				return function() {
					if (!(config && Ext.isObject(config))) {
						console.log('Response: config is not an object');
						return;
					}

					if (!(config.headers && Ext.isArray(config.headers))) {
						console.log('Response: headers is not an array');
						return;
					}

					for (var i = 0, header; i < config.headers.length; i++) {
						config.headers[i] = api.response.Header(config.headers[i]);
					}

					config.headers = Ext.Array.clean(config.headers);

					if (!config.headers.length) {
						console.log('Response: no valid headers');
						return;
					}

					if (!(Ext.isArray(config.rows) && config.rows.length > 0)) {
						alert('No values found');
						return;
					}

					if (config.headers.length !== config.rows[0].length) {
						console.log('Response: headers.length !== rows[0].length');
					}

					return config;
				}();
			};
		}());

		// support
		(function() {

			// prototype
			support.prototype = {};

				// array
			support.prototype.array = {};

			support.prototype.array.getLength = function(array, suppressWarning) {
				if (!Ext.isArray(array)) {
					if (!suppressWarning) {
						console.log('support.prototype.array.getLength: not an array');
					}

					return null;
				}

				return array.length;
			};

			support.prototype.array.sort = function(array, direction, key) {
				// supports [number], [string], [{key: number}], [{key: string}], [[string]], [[number]]

				if (!support.prototype.array.getLength(array)) {
					return;
				}

				key = !!key || Ext.isNumber(key) ? key : 'name';

				array.sort( function(a, b) {

					// if object, get the property values
					if (Ext.isObject(a) && Ext.isObject(b)) {
						a = a[key];
						b = b[key];
					}

					// if array, get from the right index
					if (Ext.isArray(a) && Ext.isArray(b)) {
						a = a[key];
						b = b[key];
					}

					// string
					if (Ext.isString(a) && Ext.isString(b)) {
						a = a.toLowerCase();
						b = b.toLowerCase();

						if (direction === 'DESC') {
							return a < b ? 1 : (a > b ? -1 : 0);
						}
						else {
							return a < b ? -1 : (a > b ? 1 : 0);
						}
					}

					// number
					else if (Ext.isNumber(a) && Ext.isNumber(b)) {
						return direction === 'DESC' ? b - a : a - b;
					}

					return -1;
				});

				return array;
			};

            support.prototype.array.uniqueByProperty = function(array, property) {
                var names = [],
                    uniqueItems = [];

                for (var i = 0, item; i < array.length; i++) {
                    item = array[i];

                    if (!Ext.Array.contains(names, item[property])) {
                        uniqueItems.push(item);
                        names.push(item[property]);
                    }
                }

                return uniqueItems;
            };

            support.prototype.array.getObjectMap = function(array, idProperty, nameProperty, namePrefix) {
                if (!(Ext.isArray(array) && array.length)) {
                    return {};
                }

                var o = {};
                idProperty = idProperty || 'id';
                nameProperty = nameProperty || 'name';
                namePrefix = namePrefix || '';

                for (var i = 0, obj; i < array.length; i++) {
                    obj = array[i];

                    o[namePrefix + obj[idProperty]] = obj[nameProperty];
                }

                return o;
            };

				// object
			support.prototype.object = {};

			support.prototype.object.getLength = function(object, suppressWarning) {
				if (!Ext.isObject(object)) {
					if (!suppressWarning) {
						console.log('support.prototype.object.getLength: not an object');
					}

					return null;
				}

				var size = 0;

				for (var key in object) {
					if (object.hasOwnProperty(key)) {
						size++;
					}
				}

				return size;
			};

			support.prototype.object.hasObject = function(object, property, value) {
				if (!support.prototype.object.getLength(object)) {
					return null;
				}

				for (var key in object) {
					var record = object[key];

					if (object.hasOwnProperty(key) && record[property] === value) {
						return true;
					}
				}

				return null;
			};

				// str
			support.prototype.str = {};

			support.prototype.str.replaceAll = function(variable, find, replace) {
                if (Ext.isString(variable)) {
                    variable = variable.split(find).join(replace);
                }
                else if (Ext.isArray(variable)) {
                    for (var i = 0; i < variable.length; i++) {
                        variable[i] = variable[i].split(find).join(replace);
                    }
                }

                return variable;
			};

			support.prototype.str.toggleDirection = function(direction) {
				return direction === 'DESC' ? 'ASC' : 'DESC';
			};

				// number
			support.prototype.number = {};

			support.prototype.number.getNumberOfDecimals = function(number) {
				var str = new String(number);
				return (str.indexOf('.') > -1) ? (str.length - str.indexOf('.') - 1) : 0;
			};

			support.prototype.number.roundIf = function(number, precision) {
				number = parseFloat(number);
				precision = parseFloat(precision);

				if (Ext.isNumber(number) && Ext.isNumber(precision)) {
					var numberOfDecimals = support.prototype.number.getNumberOfDecimals(number);
					return numberOfDecimals > precision ? Ext.Number.toFixed(number, precision) : number;
				}

				return number;
			};

			support.prototype.number.prettyPrint = function(number, separator) {
				separator = separator || 'space';

				if (separator === 'none') {
					return number;
				}

				return number.toString().replace(/\B(?=(\d{3})+(?!\d))/g, conf.report.digitGroupSeparator[separator]);
			};

			// color
			support.color = {};

			support.color.hexToRgb = function(hex) {
				var shorthandRegex = /^#?([a-f\d])([a-f\d])([a-f\d])$/i,
					result;

				hex = hex.replace(shorthandRegex, function(m, r, g, b) {
					return r + r + g + g + b + b;
				});

				result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);

				return result ? {
					r: parseInt(result[1], 16),
					g: parseInt(result[2], 16),
					b: parseInt(result[3], 16)
				} : null;
			};

		}());

		// service
		(function() {

			// layout
			service.layout = {};

			service.layout.cleanDimensionArray = function(dimensionArray) {
				if (!support.prototype.array.getLength(dimensionArray)) {
					return null;
				}

				var array = [];

				for (var i = 0; i < dimensionArray.length; i++) {
					array.push(api.layout.Dimension(dimensionArray[i]));
				}

				array = Ext.Array.clean(array);

				return array.length ? array : null;
			};

			service.layout.sortDimensionArray = function(dimensionArray, key) {
				if (!support.prototype.array.getLength(dimensionArray, true)) {
					return null;
				}

				// Clean dimension array
				dimensionArray = service.layout.cleanDimensionArray(dimensionArray);

				if (!dimensionArray) {
					console.log('service.layout.sortDimensionArray: no valid dimensions');
					return null;
				}

				key = key || 'dimensionName';

				// Dimension order
				Ext.Array.sort(dimensionArray, function(a,b) {
					if (a[key] < b[key]) {
						return -1;
					}
					if (a[key] > b[key]) {
						return 1;
					}
					return 0;
				});

				// Sort object items, ids
				for (var i = 0, items; i < dimensionArray.length; i++) {
					support.prototype.array.sort(dimensionArray[i].items, 'ASC', 'id');

					if (support.prototype.array.getLength(dimensionArray[i].ids)) {
						support.prototype.array.sort(dimensionArray[i].ids);
					}
				}

				return dimensionArray;
			};

			service.layout.getObjectNameDimensionMapFromDimensionArray = function(dimensionArray) {
				var map = {};

				if (!support.prototype.array.getLength(dimensionArray)) {
					return null;
				}

				for (var i = 0, dimension; i < dimensionArray.length; i++) {
					dimension = api.layout.Dimension(dimensionArray[i]);

					if (dimension) {
						map[dimension.dimension] = dimension;
					}
				}

				return support.prototype.object.getLength(map) ? map : null;
			};

			service.layout.getObjectNameDimensionItemsMapFromDimensionArray = function(dimensionArray) {
				var map = {};

				if (!support.prototype.array.getLength(dimensionArray)) {
					return null;
				}

				for (var i = 0, dimension; i < dimensionArray.length; i++) {
					dimension = api.layout.Dimension(dimensionArray[i]);

					if (dimension) {
						map[dimension.dimension] = dimension.items;
					}
				}

				return support.prototype.object.getLength(map) ? map : null;
			};

			service.layout.getItemName = function(layout, response, id, isHtml) {
				var metaData = response.metaData,
					name = '';

				if (service.layout.isHierarchy(layout, response, id)) {
					var a = metaData.names[id].split('/');
					a.shift();

					for (var i = 0, isLast; i < a.length; i++) {
						isLast = !!(i === a.length - 1);

						name += (isHtml && !isLast ? '<span class="text-weak">' : '') + a[i] + (isHtml && !isLast ? '</span>' : '') + (!isLast ? ' / ' : '');
					}

					return name;
				}

				name += metaData.names[id];

				return name;
			};

			service.layout.getExtendedLayout = function(layout) {
				var layout = Ext.clone(layout),
					xLayout;

				xLayout = {
					columns: [],
					rows: [],
					filters: [],

					columnObjectNames: [],
					columnDimensionNames: [],
					rowObjectNames: [],
					rowDimensionNames: [],

					// axis
					axisDimensions: [],
					axisObjectNames: [],
					axisDimensionNames: [],

						// for param string
					sortedAxisDimensionNames: [],

					// Filter
					filterDimensions: [],
					filterObjectNames: [],
					filterDimensionNames: [],

						// for param string
					sortedFilterDimensions: [],

					// all
					dimensions: [],
					objectNames: [],
					dimensionNames: [],

					// oject name maps
					objectNameDimensionsMap: {},
					objectNameItemsMap: {},
					objectNameIdsMap: {},

					// dimension name maps
					dimensionNameDimensionsMap: {},
					dimensionNameItemsMap: {},
					dimensionNameIdsMap: {},

						// for param string
					dimensionNameSortedIdsMap: {}

					// sort table by column
					//sortableIdObjects: []
				};

				Ext.applyIf(xLayout, layout);

				// columns, rows, filters
				if (layout.columns) {
                    //layout.columns = support.prototype.array.uniqueByProperty(layout.columns, 'dimension');

					for (var i = 0, dim, items, xDim; i < layout.columns.length; i++) {
						dim = layout.columns[i];
						items = dim.items;
						xDim = {};

						xDim.dimension = dim.dimension;
						xDim.objectName = dim.dimension;
						xDim.dimensionName = dimConf.objectNameMap.hasOwnProperty(dim.dimension) ? dimConf.objectNameMap[dim.dimension].dimensionName || dim.dimension : dim.dimension;

						xDim.items = [];
						xDim.ids = [];

						if (items) {
							xDim.items = items;

							for (var j = 0; j < items.length; j++) {
								xDim.ids.push(items[j].id);
							}
						}

						xLayout.columns.push(xDim);

						xLayout.columnObjectNames.push(xDim.objectName);
						xLayout.columnDimensionNames.push(xDim.dimensionName);

						xLayout.axisDimensions.push(xDim);
						xLayout.axisObjectNames.push(xDim.objectName);
						xLayout.axisDimensionNames.push(dimConf.objectNameMap.hasOwnProperty(xDim.objectName) ? dimConf.objectNameMap[xDim.objectName].dimensionName || xDim.objectName : xDim.objectName);

						xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
						xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
						xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
					}
				}

				if (layout.rows) {
                    //layout.rows = support.prototype.array.uniqueByProperty(layout.rows, 'dimension');

					for (var i = 0, dim, items, xDim; i < layout.rows.length; i++) {
						dim = Ext.clone(layout.rows[i]);
						items = dim.items;
						xDim = {};

						xDim.dimension = dim.dimension;
						xDim.objectName = dim.dimension;
						xDim.dimensionName = dimConf.objectNameMap.hasOwnProperty(dim.dimension) ? dimConf.objectNameMap[dim.dimension].dimensionName || dim.dimension : dim.dimension;

						xDim.items = [];
						xDim.ids = [];

						if (items) {
							xDim.items = items;

							for (var j = 0; j < items.length; j++) {
								xDim.ids.push(items[j].id);
							}
						}

						xLayout.rows.push(xDim);

						xLayout.rowObjectNames.push(xDim.objectName);
						xLayout.rowDimensionNames.push(xDim.dimensionName);

						xLayout.axisDimensions.push(xDim);
						xLayout.axisObjectNames.push(xDim.objectName);
						xLayout.axisDimensionNames.push(dimConf.objectNameMap.hasOwnProperty(xDim.objectName) ? dimConf.objectNameMap[xDim.objectName].dimensionName || xDim.objectName : xDim.objectName);

						xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
						xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
						xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
					}
				}

				if (layout.filters) {
                    //layout.filters = support.prototype.array.uniqueByProperty(layout.filters, 'dimension');

					for (var i = 0, dim, items, xDim; i < layout.filters.length; i++) {
						dim = layout.filters[i];
						items = dim.items;
						xDim = {};

						xDim.dimension = dim.dimension;
						xDim.objectName = dim.dimension;
						xDim.dimensionName = dimConf.objectNameMap.hasOwnProperty(dim.dimension) ? dimConf.objectNameMap[dim.dimension].dimensionName || dim.dimension : dim.dimension;

						xDim.items = [];
						xDim.ids = [];

						if (items) {
							xDim.items = items;

							for (var j = 0; j < items.length; j++) {
								xDim.ids.push(items[j].id);
							}
						}

						xLayout.filters.push(xDim);

						xLayout.filterDimensions.push(xDim);
						xLayout.filterObjectNames.push(xDim.objectName);
						xLayout.filterDimensionNames.push(dimConf.objectNameMap.hasOwnProperty(xDim.objectName) ? dimConf.objectNameMap[xDim.objectName].dimensionName || xDim.objectName : xDim.objectName);

						xLayout.objectNameDimensionsMap[xDim.objectName] = xDim;
						xLayout.objectNameItemsMap[xDim.objectName] = xDim.items;
						xLayout.objectNameIdsMap[xDim.objectName] = xDim.ids;
					}
				}

				// legend set
				xLayout.legendSet = layout.legendSet ? init.idLegendSetMap[layout.legendSet.id] : null;

				if (layout.legendSet && layout.legendSet.mapLegends) {
					xLayout.legendSet = init.idLegendSetMap[layout.legendSet.id];
					support.prototype.array.sort(xLayout.legendSet.mapLegends, 'ASC', 'startValue');
				}

				// unique dimension names
				xLayout.axisDimensionNames = Ext.Array.unique(xLayout.axisDimensionNames);
				xLayout.filterDimensionNames = Ext.Array.unique(xLayout.filterDimensionNames);

				xLayout.columnDimensionNames = Ext.Array.unique(xLayout.columnDimensionNames);
				xLayout.rowDimensionNames = Ext.Array.unique(xLayout.rowDimensionNames);
				xLayout.filterDimensionNames = Ext.Array.unique(xLayout.filterDimensionNames);

					// for param string
				xLayout.sortedAxisDimensionNames = Ext.clone(xLayout.axisDimensionNames).sort();
				xLayout.sortedFilterDimensions = service.layout.sortDimensionArray(Ext.clone(xLayout.filterDimensions));

				// all
				xLayout.dimensions = [].concat(xLayout.axisDimensions, xLayout.filterDimensions);
				xLayout.objectNames = [].concat(xLayout.axisObjectNames, xLayout.filterObjectNames);
				xLayout.dimensionNames = [].concat(xLayout.axisDimensionNames, xLayout.filterDimensionNames);

				// dimension name maps
				for (var i = 0, dimName; i < xLayout.dimensionNames.length; i++) {
					dimName = xLayout.dimensionNames[i];

					xLayout.dimensionNameDimensionsMap[dimName] = [];
					xLayout.dimensionNameItemsMap[dimName] = [];
					xLayout.dimensionNameIdsMap[dimName] = [];
				}

				for (var i = 0, xDim; i < xLayout.dimensions.length; i++) {
					xDim = xLayout.dimensions[i];

					xLayout.dimensionNameDimensionsMap[xDim.dimensionName].push(xDim);
					xLayout.dimensionNameItemsMap[xDim.dimensionName] = xLayout.dimensionNameItemsMap[xDim.dimensionName].concat(xDim.items);
					xLayout.dimensionNameIdsMap[xDim.dimensionName] = xLayout.dimensionNameIdsMap[xDim.dimensionName].concat(xDim.ids);
				}

					// for param string
				for (var key in xLayout.dimensionNameIdsMap) {
					if (xLayout.dimensionNameIdsMap.hasOwnProperty(key)) {
						xLayout.dimensionNameSortedIdsMap[key] = Ext.clone(xLayout.dimensionNameIdsMap[key]).sort();
					}
				}

				// Uuid
				xLayout.tableUuid = init.el + '_' + Ext.data.IdGenerator.get('uuid').generate();

				return xLayout;
			};

			service.layout.getSyncronizedXLayout = function(layout, xLayout, xResponse) {
				var removeDimensionFromXLayout,
					getHeaderNames,
					dimensions = Ext.Array.clean([].concat(xLayout.columns || [], xLayout.rows || [], xLayout.filters || [])),
                    originalDimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [], layout.filters || [])),
                    getSeriesValidatedLayout,
                    layout;

				removeDimensionFromXLayout = function(objectName) {
					var getUpdatedAxis;

					getUpdatedAxis = function(axis) {
						var dimension;
						axis = Ext.clone(axis);

						for (var i = 0; i < axis.length; i++) {
							if (axis[i].dimension === objectName) {
								dimension = axis[i];
							}
						}

						if (dimension) {
							Ext.Array.remove(axis, dimension);
						}

						return axis;
					};

					if (xLayout.columns) {
						xLayout.columns = getUpdatedAxis(xLayout.columns);
					}
					if (xLayout.rows) {
						xLayout.rows = getUpdatedAxis(xLayout.rows);
					}
					if (xLayout.filters) {
						xLayout.filters = getUpdatedAxis(xLayout.filters);
					}
				};

				getHeaderNames = function() {
					var headerNames = [];

					for (var i = 0; i < xResponse.headers.length; i++) {
						headerNames.push(xResponse.headers[i].name);
					}

					return headerNames;
				};

                getSeriesValidatedLayout = function(xLayout) {
                    var nSeries = xLayout.columns[0].ids.length * xLayout.rows[0].ids.length,
                        message = 'This chart is potentially very large due to the high number of series and category items. Create the chart anyway?';

                    if (nSeries > 200) {
                        if (!confirm(message))  {
                            return null;
                        }
                    }

                    return xLayout;
                };

				return function() {

					// items
					for (var i = 0, dim, header; i < dimensions.length; i++) {
						dim = dimensions[i];
						dim.items = [];
						header = xResponse.nameHeaderMap[dim.dimension];

						if (header) {
							for (var j = 0, id; j < header.ids.length; j++) {
								id = header.ids[j];

								dim.items.push({
									id: id,
									name: xResponse.metaData.names[id] || id
								});
							}
						}
					}

                    // restore order for options
                    for (var i = 0, orgDim; i < originalDimensions.length; i++) {
                        orgDim = originalDimensions[i];

                        if (Ext.isString(orgDim.filter)) {
                            var a = orgDim.filter.split(':');

                            if (a[0] === 'IN' && a.length > 1 && Ext.isString(a[1])) {
                                var options = a[1].split(';'),
                                    items = [];

                                for (var j = 0, dim; j < dimensions.length; j++) {
                                    dim = dimensions[j];

                                    if (dim.dimension === orgDim.dimension && dim.items && dim.items.length) {
                                        var items = [];

                                        for (var k = 0, option; k < options.length; k++) {
                                            option = options[k];

                                            for (var l = 0, item; l < dim.items.length; l++) {
                                                item = dim.items[l];

                                                if (item.name === option) {
                                                    items.push(item);
                                                }
                                            }
                                        }

                                        dim.items = items;
                                    }
                                }
                            }
                        }
                    }

					// Re-layout
					layout = api.layout.Layout(xLayout);

                    if (!layout) {
                        return null;
                    }

                    xLayout = service.layout.getExtendedLayout(layout);

                    // validate number of series
                    xLayout = getSeriesValidatedLayout(xLayout);

                    if (!xLayout) {
                        return null;
                    }

                    return xLayout;
				}();
			};

			service.layout.getExtendedAxis = function(xLayout, type) {
				var dimensionNames,
					spanType,
					aDimensions = [],
					nAxisWidth = 1,
					nAxisHeight,
					aaUniqueFloorIds,
					aUniqueFloorWidth = [],
					aAccFloorWidth = [],
					aFloorSpan = [],
					aaGuiFloorIds = [],
					aaAllFloorIds = [],
					aCondoId = [],
					aaAllFloorObjects = [],
					uuidObjectMap = {};

				if (type === 'col') {
					dimensionNames = Ext.clone(xLayout.columnDimensionNames);
					spanType = 'colSpan';
				}
				else if (type === 'row') {
					dimensionNames = Ext.clone(xLayout.rowDimensionNames);
					spanType = 'rowSpan';
				}

				if (!(Ext.isArray(dimensionNames) && dimensionNames.length)) {
					return;
				}
	//dimensionNames = ['pe', 'ou'];

				// aDimensions: array of dimension objects with dimensionName property
				for (var i = 0; i < dimensionNames.length; i++) {
					aDimensions.push({
						dimensionName: dimensionNames[i]
					});
				}
	//aDimensions = [{
		//dimensionName: 'pe'
	//}]

				// aaUniqueFloorIds: array of arrays with unique ids for each dimension floor
				aaUniqueFloorIds = function() {
					var a = [];

					for (var i = 0; i < aDimensions.length; i++) {
						a.push(xLayout.dimensionNameIdsMap[aDimensions[i].dimensionName]);
					}

					return a;
				}();
	//aaUniqueFloorIds	= [ [de-id1, de-id2, de-id3],
	//					    [pe-id1],
	//					    [ou-id1, ou-id2, ou-id3, ou-id4] ]


				// nAxisHeight
				nAxisHeight = aaUniqueFloorIds.length;
	//nAxisHeight = 3


				// aUniqueFloorWidth, nAxisWidth, aAccFloorWidth
				for (var i = 0, nUniqueFloorWidth; i < nAxisHeight; i++) {
					nUniqueFloorWidth = aaUniqueFloorIds[i].length;

					aUniqueFloorWidth.push(nUniqueFloorWidth);
					nAxisWidth = nAxisWidth * nUniqueFloorWidth;
					aAccFloorWidth.push(nAxisWidth);
				}
	//aUniqueFloorWidth	= [3, 1, 4]
	//nAxisWidth		= 12 (3 * 1 * 4)
	//aAccFloorWidth	= [3, 3, 12]

				// aFloorSpan
				for (var i = 0; i < nAxisHeight; i++) {
					if (aUniqueFloorWidth[i] === 1) {
						if (i === 0) { // if top floor
							aFloorSpan.push(nAxisWidth); // span max
						}
						else {
							if (xLayout.hideEmptyRows && type === 'row') {
								aFloorSpan.push(nAxisWidth / aAccFloorWidth[i]);
							}
							else {
								aFloorSpan.push(aFloorSpan[0]); //if just one item and not top level, span same as top level
							}
						}
					}
					else {
						aFloorSpan.push(nAxisWidth / aAccFloorWidth[i]);
					}
				}
	//aFloorSpan			= [4, 12, 1]


				// aaGuiFloorIds
				aaGuiFloorIds.push(aaUniqueFloorIds[0]);

				if (nAxisHeight.length > 1) {
					for (var i = 1, a, n; i < nAxisHeight; i++) {
						a = [];
						n = aUniqueFloorWidth[i] === 1 ? aUniqueFloorWidth[0] : aAccFloorWidth[i-1];

						for (var j = 0; j < n; j++) {
							a = a.concat(aaUniqueFloorIds[i]);
						}

						aaGuiFloorIds.push(a);
					}
				}
	//aaGuiFloorIds	= [ [d1, d2, d3], (3)
	//					[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (15)
	//					[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2...] (30)
	//		  	  	  ]


				// aaAllFloorIds
				for (var i = 0, aAllFloorIds, aUniqueFloorIds, span, factor; i < nAxisHeight; i++) {
					aAllFloorIds = [];
					aUniqueFloorIds = aaUniqueFloorIds[i];
					span = aFloorSpan[i];
					factor = nAxisWidth / (span * aUniqueFloorIds.length);

					for (var j = 0; j < factor; j++) {
						for (var k = 0; k < aUniqueFloorIds.length; k++) {
							for (var l = 0; l < span; l++) {
								aAllFloorIds.push(aUniqueFloorIds[k]);
							}
						}
					}

					aaAllFloorIds.push(aAllFloorIds);
				}
	//aaAllFloorIds	= [ [d1, d1, d1, d1, d1, d1, d1, d1, d1, d1, d2, d2, d2, d2, d2, d2, d2, d2, d2, d2, d3, d3, d3, d3, d3, d3, d3, d3, d3, d3], (30)
	//					[p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5, p1, p2, p3, p4, p5], (30)
	//					[o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2, o1, o2] (30)
	//		  	  	  ]


				// aCondoId
				for (var i = 0, id; i < nAxisWidth; i++) {
					id = '';

					for (var j = 0; j < nAxisHeight; j++) {
						id += aaAllFloorIds[j][i];
					}

					if (id) {
						aCondoId.push(id);
					}
				}
	//aCondoId	= [ id11+id21+id31, id12+id22+id32, ... ]


				// allObjects
				for (var i = 0, allFloor; i < aaAllFloorIds.length; i++) {
					allFloor = [];

					for (var j = 0, obj; j < aaAllFloorIds[i].length; j++) {
						obj = {
							id: aaAllFloorIds[i][j],
							uuid: Ext.data.IdGenerator.get('uuid').generate(),
							dim: i,
							axis: type
						};

						// leaf?
						if (i === aaAllFloorIds.length - 1) {
							obj.leaf = true;
						}

						allFloor.push(obj);
					}

					aaAllFloorObjects.push(allFloor);
				}

				// add span and children
				for (var i = 0; i < aaAllFloorObjects.length; i++) {
					for (var j = 0, obj, doorCount = 0, oldestObj; j < aaAllFloorObjects[i].length; j++) {

						obj = aaAllFloorObjects[i][j];

						if (doorCount === 0) {

							// span
							obj[spanType] = aFloorSpan[i];

							// children
							//obj.children = Ext.isDefined(aFloorSpan[i + 1]) ? aFloorSpan[i] / aFloorSpan[i + 1] : 0;
							obj.children = obj.leaf ? 0 : aFloorSpan[i];

							// first sibling
							obj.oldest = true;

							// root?
							if (i === 0) {
								obj.root = true;
							}

							// tmp oldest uuid
							oldestObj = obj;
						}

						obj.oldestSibling = oldestObj;

						if (++doorCount === aFloorSpan[i]) {
							doorCount = 0;
						}
					}
				}

				// add parents if more than 1 floor
				if (nAxisHeight > 1) {
					for (var i = 1, allFloor; i < nAxisHeight; i++) {
						allFloor = aaAllFloorObjects[i];

						//for (var j = 0, obj, doorCount = 0, span = aFloorSpan[i - 1], parentObj = aaAllFloorObjects[i - 1][0]; j < allFloor.length; j++) {
						for (var j = 0, doorCount = 0, span = aFloorSpan[i - 1]; j < allFloor.length; j++) {
							allFloor[j].parent = aaAllFloorObjects[i - 1][j];

							//doorCount++;

							//if (doorCount === span) {
								//parentObj = aaAllFloorObjects[i - 1][j + 1];
								//doorCount = 0;
							//}
						}
					}
				}

				// add uuids array to leaves
				if (aaAllFloorObjects.length) {

					// set span to second lowest span number: if aFloorSpan == [15,3,15,1], set span to 3
					var span = nAxisHeight > 1 ? support.prototype.array.sort(Ext.clone(aFloorSpan))[1] : nAxisWidth,
						allFloorObjectsLast = aaAllFloorObjects[aaAllFloorObjects.length - 1];

					for (var i = 0, leaf, parentUuids, obj, leafUuids = []; i < allFloorObjectsLast.length; i++) {
						leaf = allFloorObjectsLast[i];
						leafUuids.push(leaf.uuid);
						parentUuids = [];
						obj = leaf;

						// get the uuid of the oldest sibling
						while (obj.parent) {
							obj = obj.parent;
							parentUuids.push(obj.oldestSibling.uuid);
						}

						// add parent uuids to leaf
						leaf.uuids = Ext.clone(parentUuids);

						// add uuid for all leaves
						if (leafUuids.length === span) {
							for (var j = (i - span) + 1, leaf; j <= i; j++) {
								leaf = allFloorObjectsLast[j];
								leaf.uuids = leaf.uuids.concat(Ext.clone(leafUuids));
							}

							leafUuids = [];
						}
					}
				}

				// populate uuidObject map
				for (var i = 0; i < aaAllFloorObjects.length; i++) {
					for (var j = 0, object; j < aaAllFloorObjects[i].length; j++) {
						object = aaAllFloorObjects[i][j];
//console.log(object.uuid, object);
						uuidObjectMap[object.uuid] = object;
					}
				}

//console.log("aaAllFloorObjects", aaAllFloorObjects);

				return {
					type: type,
					items: aDimensions,
					xItems: {
						unique: aaUniqueFloorIds,
						gui: aaGuiFloorIds,
						all: aaAllFloorIds
					},
					objects: {
						all: aaAllFloorObjects
					},
					ids: aCondoId,
					span: aFloorSpan,
					dims: nAxisHeight,
					size: nAxisWidth,
					uuidObjectMap: uuidObjectMap
				};
			};

			service.layout.isHierarchy = function(layout, response, id) {
				return layout.showHierarchy && Ext.isObject(response.metaData.ouHierarchy) && response.metaData.ouHierarchy.hasOwnProperty(id);
			};

            service.layout.getHierarchyName = function(ouHierarchy, names, id) {
                var graph = ouHierarchy[id],
                    ids = Ext.Array.clean(graph.split('/')),
                    hierarchyName = '';

                if (ids.length < 2) {
                    return names[id];
                }

                for (var i = 0; i < ids.length; i++) {
                    hierarchyName += names[ids[i]] + ' / ';
                }

                hierarchyName += names[id];

                return hierarchyName;
            };

			service.layout.layout2plugin = function(layout, el) {
				var layout = Ext.clone(layout),
					dimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [], layout.filters || []));

				layout.url = init.contextPath;

				if (el) {
					layout.el = el;
				}

				if (Ext.isString(layout.id)) {
					return {id: layout.id};
				}

				for (var i = 0, dimension, item; i < dimensions.length; i++) {
					dimension = dimensions[i];

					delete dimension.id;
					delete dimension.ids;
					delete dimension.type;
					delete dimension.dimensionName;
					delete dimension.objectName;

					for (var j = 0, item; j < dimension.items.length; j++) {
						item = dimension.items[j];

						delete item.name;
						delete item.code;
						delete item.created;
						delete item.lastUpdated;
						delete item.value;
					}
				}

				if (layout.showTotals) {
					delete layout.showTotals;
				}

				if (layout.showSubTotals) {
					delete layout.showSubTotals;
				}

				if (!layout.hideEmptyRows) {
					delete layout.hideEmptyRows;
				}

				if (!layout.showHierarchy) {
					delete layout.showHierarchy;
				}

				if (layout.displayDensity === 'normal') {
					delete layout.displayDensity;
				}

				if (layout.fontSize === 'normal') {
					delete layout.fontSize;
				}

				if (layout.digitGroupSeparator === 'space') {
					delete layout.digitGroupSeparator;
				}

				if (!layout.legendSet) {
					delete layout.legendSet;
				}

				if (!layout.sorting) {
					delete layout.sorting;
				}

				delete layout.parentGraphMap;
				delete layout.reportingPeriod;
				delete layout.organisationUnit;
				delete layout.parentOrganisationUnit;
				delete layout.regression;
				delete layout.cumulative;
				delete layout.topLimit;

				return layout;
			};

            service.layout.getDataDimensionsFromLayout = function(layout) {
                var dimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [], layout.filters || [])),
                    ignoreKeys = ['pe', 'ou'],
                    dataDimensions = [];

                for (var i = 0; i < dimensions.length; i++) {
                    if (!Ext.Array.contains(ignoreKeys, dimensions[i].dimension)) {
                        dataDimensions.push(dimensions[i]);
                    }
                }

                return dataDimensions;
            };

			// response
			service.response = {};

				// aggregate
			service.response.aggregate = {};

			service.response.aggregate.getExtendedResponse = function(xLayout, response) {
				var emptyId = '[N/A]',
                    meta = ['ou', 'pe'],
                    ouHierarchy,
                    names,
					headers;

				response = Ext.clone(response);
				headers = response.headers;
                ouHierarchy = response.metaData.ouHierarchy,
                names = response.metaData.names;
                names[emptyId] = emptyId;

                response.metaData.optionNames = {};
				response.nameHeaderMap = {};
				response.idValueMap = {};

				// add to headers: size, index, response ids
				for (var i = 0, header, isMeta; i < headers.length; i++) {
					header = headers[i];
                    header.ids = [];
                    isMeta = Ext.Array.contains(meta, header.name);

                    // overwrite row ids, update metadata, set unique header ids
                    if (header.meta) {
                        if (header.type === 'java.lang.Double') {
                            var objects = [];

                            for (var j = 0, id, fullId, parsedId, displayId; j < response.rows.length; j++) {
                                id = response.rows[j][i] || emptyId;
                                fullId = header.name + id;
                                parsedId = parseFloat(id);
                                displayId = Ext.isNumber(parsedId) ? parsedId : (names[id] || id);

								// update names
                                names[fullId] = (isMeta ? '' : header.column + ' ') + displayId;

								// update rows
                                response.rows[j][i] = fullId;

								// number sorting
                                objects.push({
                                    id: fullId,
                                    sortingId: Ext.isNumber(parsedId) ? parsedId : Number.MAX_VALUE
                                });
                            }

                            support.prototype.array.sort(objects, 'ASC', 'sortingId');
                            header.ids = Ext.Array.pluck(objects, 'id');
                        }
                        else {
							var objects = [];

                            for (var j = 0, id, fullId, name, isHierarchy; j < response.rows.length; j++) {
                                id = response.rows[j][i] || emptyId;
                                fullId = header.name + id;
                                isHierarchy = service.layout.isHierarchy(xLayout, response, id);

                                // add dimension name prefix if not pe/ou
                                name = isMeta ? '' : header.column + ' ';

                                // add hierarchy if ou and showHierarchy
                                name = isHierarchy ? service.layout.getHierarchyName(ouHierarchy, names, id) : (names[id] || id);

                                names[fullId] = name;

                                // update rows
                                response.rows[j][i] = fullId;

                                // update ou hierarchy
                                if (isHierarchy) {
									ouHierarchy[fullId] = ouHierarchy[id];
								}

								objects.push({
									id: fullId,
									sortingId: header.name === 'pe' ? fullId : name
								});
                            }

                            support.prototype.array.sort(objects, 'ASC', 'sortingId');
                            header.ids = Ext.Array.pluck(objects, 'id');
                        }
                    }

					header.ids = Ext.Array.unique(header.ids);

					header.size = header.ids.length;
					header.index = i;

					response.nameHeaderMap[header.name] = header;
				}

				// idValueMap: vars
				var valueHeaderIndex = response.nameHeaderMap[conf.finals.dimension.value.value].index,
					dx = dimConf.data.dimensionName,
					axisDimensionNames = xLayout.axisDimensionNames,
					idIndexOrder = [];

				// idValueMap: idIndexOrder
				for (var i = 0; i < axisDimensionNames.length; i++) {
					idIndexOrder.push(response.nameHeaderMap[axisDimensionNames[i]].index);
				}

				// idValueMap
				for (var i = 0, row, id; i < response.rows.length; i++) {
					row = response.rows[i];
					id = '';

					for (var j = 0; j < idIndexOrder.length; j++) {
						id += row[idIndexOrder[j]];
					}

					response.idValueMap[id] = row[valueHeaderIndex];
				}

				return response;
			};
        }());

		// web
		(function() {

			// mask
			web.mask = {};

			web.mask.show = function(component, message) {
				if (!Ext.isObject(component)) {
					console.log('web.mask.show: component not an object');
					return null;
				}

				message = message || 'Loading..';

				if (component.mask && component.mask.destroy) {
					component.mask.destroy();
					component.mask = null;
				}

				component.mask = new Ext.create('Ext.LoadMask', component, {
					shadow: false,
					msg: message,
					style: 'box-shadow:0',
					bodyStyle: 'box-shadow:0'
				});

				component.mask.show();
			};

			web.mask.hide = function(component) {
				if (!Ext.isObject(component)) {
					console.log('support.gui.mask.hide: component not an object');
					return null;
				}

				if (component.mask && component.mask.destroy) {
					component.mask.destroy();
					component.mask = null;
				}
			};

			// message
			web.message = {};

			web.message.alert = function(message) {
				console.log(message);
			};

			// analytics
			web.analytics = {};

			web.analytics.getParamString = function(layout, format) {
                var paramString,
                    dimensions = Ext.Array.clean([].concat(layout.columns || [], layout.rows || [])),
                    ignoreKeys = ['longitude', 'latitude'],
                    nameItemsMap;

                paramString = '/api/analytics/events/aggregate/' + layout.program.id + '.' + (format || 'json') + '?';

				// stage
				paramString += 'stage=' + layout.programStage.id;

                // dimensions
                if (dimensions) {
					for (var i = 0, dim; i < dimensions.length; i++) {
						dim = dimensions[i];

						if (Ext.Array.contains(ignoreKeys, dim.dimension) || (dim.dimension === 'pe' && !dim.items && !dim.filter)) {
							continue;
						}

						paramString += '&dimension=' + dim.dimension;

						if (dim.items && dim.items.length) {
							paramString += ':';

							for (var j = 0, item; j < dim.items.length; j++) {
								item = dim.items[j];

								paramString += encodeURIComponent(item.id) + ((j < (dim.items.length - 1)) ? ';' : '');
							}
						}
						else {
							paramString += dim.filter ? ':' + encodeURIComponent(dim.filter) : '';
						}
					}
				}

                // filters
                if (layout.filters) {
					for (var i = 0, dim; i < layout.filters.length; i++) {
						dim = layout.filters[i];

                        paramString += '&filter=' + dim.dimension;

                        if (Ext.isArray(dim.items) && dim.items.length) {
                            paramString += ':';

                            for (var j = 0; j < dim.items.length; j++) {
                                paramString += encodeURIComponent(dim.items[j].id);
                                paramString += j < dim.items.length - 1 ? ';' : '';
                            }
                        }
                        else {
                            paramString += dim.filter ? ':' + encodeURIComponent(dim.filter) : '';
                        }
					}
				}

                // dates
                if (layout.startDate && layout.endDate) {
                    paramString += '&startDate=' + layout.startDate + '&endDate=' + layout.endDate;
                }

                // display property
                paramString += '&displayProperty=' + init.userAccount.settings.keyAnalysisDisplayProperty.toUpperCase();

                return paramString;
            };

			web.analytics.validateUrl = function(url) {
				var msg;

                if (Ext.isIE) {
                    msg = 'Too many items selected (url has ' + url.length + ' characters). Internet Explorer accepts maximum 2048 characters.';
                }
                else {
					var len = url.length > 8000 ? '8000' : (url.length > 4000 ? '4000' : '2000');
					msg = 'Too many items selected (url has ' + url.length + ' characters). Please reduce to less than ' + len + ' characters.';
                }

                msg += '\n\n' + 'Hint: A good way to reduce the number of items is to use relative periods and level/group organisation unit selection modes.';

                alert(msg);
			};

			// report
			web.report = {};

				// aggregate
			web.report.aggregate = {};

			web.report.aggregate.sort = function(xLayout, xResponse, xColAxis) {
				var condoId = xLayout.sorting.id,
					name = xLayout.rows[0].dimension,
					ids = xResponse.nameHeaderMap[name].ids,
					valueMap = xResponse.idValueMap,
					direction = xLayout.sorting ? xLayout.sorting.direction : 'DESC',
					objects = [],
					layout;

				// relative id?
				if (Ext.isString(condoId)) {
					condoId = condoId.toLowerCase() === 'total' ? 'total_' : condoId;
				}
				else if (Ext.isNumber(condoId)) {
					if (condoId === 0) {
						condoId = 'total_';
					}
					else {
						condoId = xColAxis.ids[parseInt(condoId) - 1];
					}
				}
				else {
					return xResponse;
				}

				// collect values
				for (var i = 0, key, value; i < ids.length; i++) {
					key = condoId + ids[i];
					value = parseFloat(valueMap[key]);

					objects.push({
						id: ids[i],
						value: Ext.isNumber(value) ? value : (Number.MAX_VALUE * -1)
					});
				}

				support.prototype.array.sort(objects, direction, 'value');

				// new id order
				xResponse.nameHeaderMap[name].ids = Ext.Array.pluck(objects, 'id');

				return xResponse;
			};

			web.report.aggregate.createChart = function(ns, legendSet) {
                var xLayout = ns.app.xLayout,
                    xResponse = ns.app.xResponse,
                    columnIds = xLayout.columnDimensionNames[0] ? xLayout.dimensionNameIdsMap[xLayout.columnDimensionNames[0]] : [],
                    failSafeColumnIds = [],
                    failSafeColumnIdMap = {},
                    createFailSafeIds = function() {
                        for (var i = 0, uuid; i < columnIds.length; i++) {
                            uuid = Ext.data.IdGenerator.get('uuid').generate();

                            failSafeColumnIds.push(uuid);
                            failSafeColumnIdMap[uuid] = columnIds[i];

                            xResponse.metaData.names[uuid] = xResponse.metaData.names[columnIds[i]];
                        }
                    }(),

                    // row ids
                    rowIds = xLayout.rowDimensionNames[0] ? xLayout.dimensionNameIdsMap[xLayout.rowDimensionNames[0]] : [],

                    // filter ids
                    filterIds = function() {
                        var ids = [];

                        if (xLayout.filters) {
                            for (var i = 0; i < xLayout.filters.length; i++) {
                                ids = ids.concat(xLayout.filters[i].ids || []);
                            }
                        }

                        return ids;
                    }(),

                    // totals
                    dataTotalKey = Ext.data.IdGenerator.get('uuid').generate(),
                    addDataTotals = function(data, ids) {
                        for (var i = 0, obj, total; i < data.length; i++) {
                            obj = data[i];
                            total = 0;

                            for (var j = 0; j < ids.length; j++) {
                                total += parseFloat(obj[ids[j]]);
                                obj[dataTotalKey] = total;
                            }
                        }
                    },

					getSyncronizedXLayout,
                    getExtendedResponse,
                    validateUrl,

                    getDefaultStore,
                    getDefaultNumericAxis,
                    getDefaultCategoryAxis,
                    getDefaultSeriesTitle,
                    getPieSeriesTitle,
                    getDefaultSeries,
                    getDefaultTrendLines,
                    getDefaultTargetLine,
                    getDefaultBaseLine,
                    getDefaultTips,
                    setDefaultTheme,
                    getDefaultLegend,
                    getDefaultChartTitle,
                    getDefaultChartSizeHandler,
                    getDefaultChartTitlePositionHandler,
                    getDefaultChart,

                    generator = {};

                getDefaultStore = function(isStacked) {
                    var data = [],
                        trendLineFields = [],
                        targetLineFields = [],
                        baseLineFields = [],
                        store;

                    // data
                    for (var i = 0, obj, category, rowValues, isEmpty; i < rowIds.length; i++) {
                        obj = {};
                        category = rowIds[i];
                        rowValues = [];
                        isEmpty = false;

                        obj[conf.finals.data.domain] = xResponse.metaData.names[category];

                        for (var j = 0, id, value; j < columnIds.length; j++) {
                            id = support.prototype.str.replaceAll(columnIds[j], '#', '') + support.prototype.str.replaceAll(rowIds[i], '#', '');
                            value = xResponse.idValueMap[id];
                            rowValues.push(value);

                            obj[failSafeColumnIds[j]] = value ? parseFloat(value) : '0.0';
                        }

                        isEmpty = !(Ext.Array.clean(rowValues).length);

                        if (!(isEmpty && xLayout.hideEmptyRows)) {
                            data.push(obj);
                        }
                    }

                    // stacked
                    if (isStacked) {
                        addDataTotals(data, failSafeColumnIds);
                    }

                    // sort order
                    if (xLayout.sortOrder) {
                        var sortingKey = isStacked ? dataTotalKey : failSafeColumnIds[0];

                        support.prototype.array.sort(data, xLayout.sortOrder === -1 ? 'ASC' : 'DESC', sortingKey);
                    }

                    // trend lines
                    if (xLayout.showTrendLine) {
                        var regression,
                            regressionKey;

                        if (isStacked) {
                            regression = new SimpleRegression();
                            regressionKey = conf.finals.data.trendLine + dataTotalKey;

                            for (var i = 0, value; i < data.length; i++) {
                                value = data[i][dataTotalKey];
                                regression.addData(i, parseFloat(value));
                            }

                            for (var i = 0; i < data.length; i++) {
                                data[i][regressionKey] = parseFloat(regression.predict(i).toFixed(1));
                            }

                            trendLineFields.push(regressionKey);
                            xResponse.metaData.names[regressionKey] = EV.i18n.trend + ' (Total)';
                        }
                        else {
                            for (var i = 0; i < failSafeColumnIds.length; i++) {
                                regression = new SimpleRegression();
                                regressionKey = conf.finals.data.trendLine + failSafeColumnIds[i];

                                for (var j = 0, value; j < data.length; j++) {
                                    value = data[j][failSafeColumnIds[i]];
                                    regression.addData(j, parseFloat(value));
                                }

                                for (var j = 0; j < data.length; j++) {
                                    data[j][regressionKey] = parseFloat(regression.predict(j).toFixed(1));
                                }

                                trendLineFields.push(regressionKey);
                                xResponse.metaData.names[regressionKey] = EV.i18n.trend + ' (' + xResponse.metaData.names[failSafeColumnIds[i]] + ')';
                            }
                        }
                    }

                    // target line
                    if (Ext.isNumber(xLayout.targetLineValue) || Ext.isNumber(parseFloat(xLayout.targetLineValue))) {
                        for (var i = 0; i < data.length; i++) {
                            data[i][conf.finals.data.targetLine] = parseFloat(xLayout.targetLineValue);
                        }

                        targetLineFields.push(conf.finals.data.targetLine);
                    }

                    // base line
                    if (Ext.isNumber(xLayout.baseLineValue) || Ext.isNumber(parseFloat(xLayout.baseLineValue))) {
                        for (var i = 0; i < data.length; i++) {
                            data[i][conf.finals.data.baseLine] = parseFloat(xLayout.baseLineValue);
                        }

                        baseLineFields.push(conf.finals.data.baseLine);
                    }

                    store = Ext.create('Ext.data.Store', {
                        fields: function() {
                            var fields = Ext.clone(failSafeColumnIds);
                            fields.push(conf.finals.data.domain);
                            fields = fields.concat(trendLineFields, targetLineFields, baseLineFields);

                            return fields;
                        }(),
                        data: data
                    });

                    store.rangeFields = failSafeColumnIds;
                    store.domainFields = [conf.finals.data.domain];
                    store.trendLineFields = trendLineFields;
                    store.targetLineFields = targetLineFields;
                    store.baseLineFields = baseLineFields;
                    store.numericFields = [].concat(store.rangeFields, store.trendLineFields, store.targetLineFields, store.baseLineFields);

                    store.getMaximum = function() {
                        var maximums = [];

                        for (var i = 0; i < store.numericFields.length; i++) {
                            maximums.push(store.max(store.numericFields[i]));
                        }

                        return Ext.Array.max(maximums);
                    };

                    store.getMinimum = function() {
                        var minimums = [];

                        for (var i = 0; i < store.numericFields.length; i++) {
                            minimums.push(store.min(store.numericFields[i]));
                        }

                        return Ext.Array.min(minimums);
                    };

                    store.getMaximumSum = function() {
                        var sums = [],
                            recordSum = 0;

                        store.each(function(record) {
                            recordSum = 0;

                            for (var i = 0; i < store.rangeFields.length; i++) {
                                recordSum += record.data[store.rangeFields[i]];
                            }

                            sums.push(recordSum);
                        });

                        return Ext.Array.max(sums);
                    };

                    store.hasDecimals = function() {
                        var records = store.getRange();

                        for (var i = 0; i < records.length; i++) {
                            for (var j = 0, value; j < store.rangeFields.length; j++) {
                                value = records[i].data[store.rangeFields[j]];

                                if (Ext.isNumber(value) && (value % 1)) {
                                    return true;
                                }
                            }
                        }

                        return false;
                    };

                    store.getNumberOfDecimals = function() {
                        var records = store.getRange(),
                            values = [];

                        for (var i = 0; i < records.length; i++) {
                            for (var j = 0, value; j < store.rangeFields.length; j++) {
                                value = records[i].data[store.rangeFields[j]];

                                if (Ext.isNumber(value) && (value % 1)) {
                                    value = value.toString();

                                    values.push(value.length - value.indexOf('.') - 1);
                                }
                            }
                        }

                        return Ext.Array.max(values);
                    };

                    if (EV.isDebug) {
                        console.log("data", data);
                        console.log("rangeFields", store.rangeFields);
                        console.log("domainFields", store.domainFields);
                        console.log("trendLineFields", store.trendLineFields);
                        console.log("targetLineFields", store.targetLineFields);
                        console.log("baseLineFields", store.baseLineFields);
                    }

                    return store;
                };

                getDefaultNumericAxis = function(store) {
                    var labelFont = 'normal 11px ' + conf.chart.style.fontFamily,
                        labelColor = 'black',
                        labelRotation = 0,
                        titleFont = 'bold 12px ' + conf.chart.style.fontFamily,
                        titleColor = 'black',

                        typeConf = conf.finals.chart,
                        minimum = store.getMinimum(),
                        maximum,
                        numberOfDecimals,
                        axis;

                    getRenderer = function(numberOfDecimals) {
                        var renderer = '0.';

                        for (var i = 0; i < numberOfDecimals; i++) {
                            renderer += '0';
                        }

                        return renderer;
                    };

                    // set maximum if stacked + extra line
                    if ((xLayout.type === typeConf.stackedcolumn || xLayout.type === typeConf.stackedbar) &&
                        (xLayout.showTrendLine || xLayout.targetLineValue || xLayout.baseLineValue)) {
                        var a = [store.getMaximum(), store.getMaximumSum()];
                        maximum = Math.ceil(Ext.Array.max(a) * 1.1);
                        maximum = Math.floor(maximum / 10) * 10;
                    }

                    // renderer
                    numberOfDecimals = store.getNumberOfDecimals();
                    renderer = !!numberOfDecimals && (store.getMaximum() < 20) ? getRenderer(numberOfDecimals) : '0,0';

                    axis = {
                        type: 'Numeric',
                        position: 'left',
                        fields: store.numericFields,
                        minimum: minimum < 0 ? minimum : 0,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(renderer),
                            style: {},
                            rotate: {}
                        },
                        labelTitle: {},
                        grid: {
                            odd: {
                                opacity: 1,
                                stroke: '#aaa',
                                'stroke-width': 0.1
                            },
                            even: {
                                opacity: 1,
                                stroke: '#aaa',
                                'stroke-width': 0.1
                            }
                        }
                    };

                    if (maximum) {
                        axis.maximum = maximum;
                    }

                    if (xLayout.rangeAxisMaxValue) {
						axis.maximum = xLayout.rangeAxisMaxValue;
					}

                    if (xLayout.rangeAxisMinValue) {
						axis.minimum = xLayout.rangeAxisMinValue;
					}

					if (xLayout.rangeAxisSteps) {
						axis.majorTickSteps = xLayout.rangeAxisSteps - 1;
					}

					if (xLayout.rangeAxisDecimals) {
						axis.label.renderer = Ext.util.Format.numberRenderer(getRenderer(xLayout.rangeAxisDecimals));
					}

                    if (xLayout.rangeAxisTitle) {
                        axis.title = xLayout.rangeAxisTitle;
                    }

                    // style
                    if (Ext.isObject(xLayout.rangeAxisStyle)) {
                        var style = xLayout.rangeAxisStyle;

                        // label
                        labelColor = style.labelColor || labelColor;

                        if (style.labelFont) {
                            labelFont = style.labelFont;
                        }
                        else {
                            labelFont = style.labelFontWeight ? style.labelFontWeight + ' ' : 'normal ';
                            labelFont += style.labelFontSize ? parseFloat(style.labelFontSize) + 'px ' : '11px ';
                            labelFont +=  style.labelFontFamily ? style.labelFontFamily : conf.chart.style.fontFamily;
                        }

                        // rotation
                        if (Ext.isNumber(parseFloat(style.labelRotation))) {
                            labelRotation = 360 - parseFloat(style.labelRotation);
                        }

                        // title
                        titleColor = style.titleColor || titleColor;

                        if (style.titleFont) {
                            titleFont = style.titleFont;
                        }
                        else {
                            titleFont = style.titleFontWeight ? style.titleFontWeight + ' ' : 'bold ';
                            titleFont += style.titleFontSize ? parseFloat(style.titleFontSize) + 'px ' : '12px ';
                            titleFont +=  style.titleFontFamily ? style.titleFontFamily : conf.chart.style.fontFamily;
                        }
                    }

                    axis.label.style.fill = labelColor;
                    axis.label.style.font = labelFont;
                    axis.label.rotate.degrees = labelRotation;

                    axis.labelTitle.fill = titleColor;
                    axis.labelTitle.font = titleFont;

                    return axis;
                };

                getDefaultCategoryAxis = function(store) {
                    var labelFont = 'normal 11px ' + conf.chart.style.fontFamily,
                        labelColor = 'black',
                        labelRotation = 315,
                        titleFont = 'bold 12px ' + conf.chart.style.fontFamily,
                        titleColor = 'black',

                        axis = {
                            type: 'Category',
                            position: 'bottom',
                            fields: store.domainFields,
                            label: {
                                rotate: {},
                                style: {}
                            },
                            labelTitle: {}
                        };

                    if (xLayout.domainAxisTitle) {
                        axis.title = xLayout.domainAxisTitle;
                    }

                    // style
                    if (Ext.isObject(xLayout.domainAxisStyle)) {
                        var style = xLayout.domainAxisStyle;

                        // label
                        labelColor = style.labelColor || labelColor;

                        if (style.labelFont) {
                            labelFont = style.labelFont;
                        }
                        else {
                            labelFont = style.labelFontWeight ? style.labelFontWeight + ' ' : 'normal ';
                            labelFont += style.labelFontSize ? parseFloat(style.labelFontSize) + 'px ' : '11px ';
                            labelFont +=  style.labelFontFamily ? style.labelFontFamily : conf.chart.style.fontFamily;
                        }

                        // rotation
                        if (Ext.isNumber(parseFloat(style.labelRotation))) {
                            labelRotation = 360 - parseFloat(style.labelRotation);
                        }

                        // title
                        titleColor = style.titleColor || titleColor;

                        if (style.titleFont) {
                            titleFont = style.titleFont;
                        }
                        else {
                            titleFont = style.titleFontWeight ? style.titleFontWeight + ' ' : 'bold ';
                            titleFont += style.titleFontSize ? parseFloat(style.titleFontSize) + 'px ' : '12px ';
                            titleFont +=  style.titleFontFamily ? style.titleFontFamily : conf.chart.style.fontFamily;
                        }
                    }

                    axis.label.style.fill = labelColor;
                    axis.label.style.font = labelFont;
                    axis.label.rotate.degrees = labelRotation;

                    axis.labelTitle.fill = titleColor;
                    axis.labelTitle.font = titleFont;

                    return axis;
                };

                getDefaultSeriesTitle = function(store) {
                    var a = [];

                    if (Ext.isObject(xLayout.legendStyle) && Ext.isArray(xLayout.legendStyle.labelNames)) {
                        return xLayout.legendStyle.labelNames;
                    }
                    else {
                        for (var i = 0, id, name, mxl, ids; i < store.rangeFields.length; i++) {
                            id = failSafeColumnIdMap[store.rangeFields[i]];
                            name = xResponse.metaData.names[id];

                            if (Ext.isString(name) && Ext.isObject(xLayout.legendStyle) && Ext.isNumber(xLayout.legendStyle.labelMaxLength)) {
                                var mxl = parseInt(xLayout.legendStyle.labelMaxLength);

                                name = name.length > mxl ? name.substr(0, mxl) + '..' : name;
                            }

                            a.push(name);
                        }
                    }

                    return a;
				};

                getPieSeriesTitle = function(store) {
                    var a = [];

                    if (Ext.isObject(xLayout.legendStyle) && Ext.isArray(xLayout.legendStyle.labelNames)) {
                        return xLayout.legendStyle.labelNames;
                    }
                    else {
                        var id = store.domainFields[0],
                            name;

                        store.each( function(r) {
                            name = r.data[id];

                            if (Ext.isString(name) && Ext.isObject(xLayout.legendStyle) && Ext.isNumber(xLayout.legendStyle.labelMaxLength)) {
                                var mxl = parseInt(xLayout.legendStyle.labelMaxLength);

                                name = name.length > mxl ? name.substr(0, mxl) + '..' : name;
                            }

                            a.push(name);
                        });
                    }

                    return a;
				};

                getDefaultSeries = function(store) {
                    var main = {
                        type: 'column',
                        axis: 'left',
                        xField: store.domainFields,
                        yField: store.rangeFields,
                        style: {
                            opacity: 0.8,
                            lineWidth: 3
                        },
                        markerConfig: {
                            type: 'circle',
                            radius: 4
                        },
                        tips: getDefaultTips(),
                        title: getDefaultSeriesTitle(store)
                    };

                    if (xLayout.showValues) {
                        var labelFont = conf.chart.style.fontFamily,
                            labelColor = 'black';

                        if (Ext.isObject(xLayout.seriesStyle)) {
                            var style = xLayout.seriesStyle;

                            // label
                            labelColor = style.labelColor || labelColor;

                            if (style.labelFont) {
                                labelFont = style.labelFont;
                            }
                            else {
                                labelFont = style.labelFontWeight ? style.labelFontWeight + ' ' : 'normal ';
                                labelFont += style.labelFontSize ? parseFloat(style.labelFontSize) + 'px ' : '11px ';
                                labelFont +=  style.labelFontFamily ? style.labelFontFamily : conf.chart.style.fontFamily;
                            }
                        }

                        main.label = {
                            display: 'outside',
                            'text-anchor': 'middle',
                            field: store.rangeFields,
                            font: labelFont,
                            fill: labelColor,
                            renderer: function(n) {
                                return n === '0.0' ? '' : n;
                            }
                        };
                    }

                    return main;
                };

                getDefaultTrendLines = function(store, isStacked) {
                    var a = [];

                    for (var i = 0, strokeColor; i < store.trendLineFields.length; i++) {
                        strokeColor = isStacked ? '#000' : conf.chart.theme.dv1[i];

                        a.push({
                            type: 'line',
                            axis: 'left',
                            xField: store.domainFields,
                            yField: store.trendLineFields[i],
                            style: {
                                opacity: 0.8,
                                lineWidth: 2,
                                'stroke-dasharray': 14,
                                stroke: strokeColor
                            },
                            markerConfig: {
                                type: 'circle',
                                radius: 0,
                                fill: strokeColor
                            },
                            title: function() {
                                var title = xResponse.metaData.names[store.trendLineFields[i]],
                                    ls = xLayout.legendStyle;
                                return ls && Ext.isNumber(ls.labelMaxLength) ? title.substr(0, ls.labelMaxLength) + '..' : title;
                            }()
                        });
                    }

                    return a;
                };

                getDefaultTargetLine = function(store) {
                    return {
                        type: 'line',
                        axis: 'left',
                        xField: store.domainFields,
                        yField: store.targetLineFields,
                        style: {
                            opacity: 1,
                            lineWidth: 1,
                            'stroke-width': 1,
                            stroke: '#000'
                        },
                        showMarkers: false,
                        title: function() {
                            var title = (Ext.isString(xLayout.targetLineTitle) ? xLayout.targetLineTitle : EV.i18n.target) + ' (' + xLayout.targetLineValue + ')',
                                ls = xLayout.legendStyle;
                            return ls && Ext.isNumber(ls.labelMaxLength) ? title.substr(0, ls.labelMaxLength) + '..' : title;
                        }()
                    };
                };

                getDefaultBaseLine = function(store) {
                    return {
                        type: 'line',
                        axis: 'left',
                        xField: store.domainFields,
                        yField: store.baseLineFields,
                        style: {
                            opacity: 1,
                            lineWidth: 1,
                            'stroke-width': 1,
                            stroke: '#000'
                        },
                        showMarkers: false,
                        title: function() {
                            var title = (Ext.isString(xLayout.baseLineTitle) ? xLayout.baseLineTitle : EV.i18n.base) + ' (' + xLayout.baseLineValue + ')',
                                ls = xLayout.legendStyle;
                            return ls && Ext.isNumber(ls.labelMaxLength) ? title.substr(0, ls.labelMaxLength) + '..' : title;
                        }()
                    };
                };

                getDefaultTips = function() {
                    return {
                        trackMouse: true,
                        cls: 'dv-chart-tips',
                        renderer: function(si, item) {
                            if (item.value) {
                                var value = item.value[1] === '0.0' ? '-' : item.value[1];
                                this.update('<div style="text-align:center"><div style="font-size:17px; font-weight:bold">' + value + '</div><div style="font-size:10px">' + si.data[conf.finals.data.domain] + '</div></div>');
                            }
                        }
                    };
                };

                setDefaultTheme = function(store) {
                    var colors = conf.chart.theme.dv1.slice(0, store.rangeFields.length);

                    Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
                        constructor: function(config) {
                            Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
                                seriesThemes: colors,
                                colors: colors
                            }, config));
                        }
                    });
                };

                getDefaultLegend = function(store, chartConfig) {
                    var itemLength = ns.dashboard ? 24 : 30,
                        charLength = ns.dashboard ? 4 : 6,
                        numberOfItems = 0,
                        numberOfChars = 0,
                        width,
                        isVertical = false,
                        labelFont = '11px ' + conf.chart.style.fontFamily,
                        labelColor = 'black';
                        position = 'top',
                        padding = 0,
                        positions = ['top', 'right', 'bottom', 'left'],
                        series = chartConfig.series;

                    for (var i = 0, title; i < series.length; i++) {
                        title = series[i].title;

                        if (Ext.isString(title)) {
                            numberOfItems += 1;
                            numberOfChars += title.length;
                        }
                        else if (Ext.isArray(title)) {
                            numberOfItems += title.length;
                            numberOfChars += title.toString().split(',').join('').length;
                        }
                    }

                    width = (numberOfItems * itemLength) + (numberOfChars * charLength);

                    if (width > ns.app.centerRegion.getWidth() - 6) {
                        position = 'right';
                    }

                    // style
                    if (Ext.isObject(xLayout.legendStyle)) {
                        var style = xLayout.legendStyle;

                        labelColor = style.labelColor || labelColor;

                        if (Ext.Array.contains(positions, style.position)) {
                            position = style.position;
                        }

                        if (style.labelFont) {
                            labelFont = style.labelFont;
                        }
                        else {
                            labelFont = style.labelFontWeight ? style.labelFontWeight + ' ' : 'normal ';
                            labelFont += style.labelFontSize ? parseFloat(style.labelFontSize) + 'px ' : '11px ';
                            labelFont +=  style.labelFontFamily ? style.labelFontFamily : conf.chart.style.fontFamily;
                        }
                    }

                    // padding
                    if (position === 'right') {
                        padding = 3;
                    }

                    return Ext.create('Ext.chart.Legend', {
                        position: position,
                        isVertical: isVertical,
                        boxStroke: '#ffffff',
                        boxStrokeWidth: 0,
                        padding: padding,
                        itemSpacing: 3,
                        labelFont: labelFont,
                        labelColor: labelColor,
                        labelMarkerSize: xLayout.legendStyle.labelMarkerSize
                    });
                };

                getDefaultChartTitle = function(store) {
                    var ids = [],
                        text = '',
                        titleFont,
                        titleColor,
                        isPie = xLayout.type === conf.finals.chart.pie,
                        isGauge = xLayout.type === conf.finals.chart.gauge;

                    if (isPie) {
                        ids.push(columnIds[0]);
                    }
                    else if (isGauge) {
                        ids.push(columnIds[0], rowIds[0]);
                    }

                    ids = Ext.Array.clean(ids.concat(filterIds || []));

                    if (Ext.isArray(ids) && ids.length) {
                        for (var i = 0; i < ids.length; i++) {
                            text += xResponse.metaData.names[ids[i]];
                            text += i < ids.length - 1 ? ', ' : '';
                        }
                    }

                    if (xLayout.title) {
                        text = xLayout.title;
                    }

                    fontSize = (ns.app.centerRegion.getWidth() / text.length) < 11.6 ? 12 : 17;
                    titleFont = 'normal ' + fontSize + 'px ' + conf.chart.style.fontFamily;
                    titleColor = 'black';

                    // legend
                    if (Ext.isObject(xLayout.legendStyle)) {
                        var style = xLayout.legendStyle;

                        titleColor = style.titleColor || titleColor;

                        if (style.titleFont) {
                            titleFont = style.titleFont;
                        }
                        else {
                            titleFont = style.titleFontWeight ? style.titleFontWeight + ' ' : 'normal ';
                            titleFont += style.titleFontSize ? parseFloat(style.titleFontSize) + 'px ' : (fontSize + 'px ');
                            titleFont +=  style.titleFontFamily ? style.titleFontFamily : conf.chart.style.fontFamily;
                        }
                    }

                    return Ext.create('Ext.draw.Sprite', {
                        type: 'text',
                        text: text,
                        font: titleFont,
                        fill: titleColor,
                        height: 20,
                        y: ns.dashboard ? 7 : 20
                    });
                };

                getDefaultChartSizeHandler = function() {
                    var width = ns.app.centerRegion.getWidth(),
                        height = ns.app.centerRegion.getHeight();

                    return function() {
						this.animate = false;
                        this.setWidth(ns.dashboard ? width : width - 15);
                        this.setHeight(ns.dashboard ? height : height - 40);
                        this.animate = !ns.dashboard;
                    };
                };

                getDefaultChartTitlePositionHandler = function() {
                    return function() {
                        if (this.items) {
                            var title = this.items[0],
                                titleWidth = Ext.isIE ? title.el.dom.scrollWidth : title.el.getWidth(),
                                titleXFallback = 10,
                                legend = this.legend,
                                legendCenterX,
                                titleX;

                            if (this.legend.position === 'top') {
                                legendCenterX = legend.x + (legend.width / 2);
                                titleX = titleWidth ? legendCenterX - (titleWidth / 2) : titleXFallback;
                            }
                            else {
                                var legendWidth = legend ? legend.width : 0;
                                titleX = titleWidth ? (this.width / 2) - (titleWidth / 2) : titleXFallback;
                            }

                            title.setAttributes({
                                x: titleX
                            }, true);
                        }
                    };
                };

                getDefaultChart = function(config) {
                    var chart,
                        store = config.store || {},
                        width = ns.app.centerRegion.getWidth(),
                        height = ns.app.centerRegion.getHeight(),
                        isLineBased = Ext.Array.contains(['line', 'area'], xLayout.type),
                        defaultConfig = {
                            //animate: true,
                            animate: false,
                            shadow: false,
                            insetPadding: ns.dashboard ? 17 : 35,
                            insetPaddingObject: {
                                top: 10,
                                right: isLineBased ? 5 : 3,
                                bottom: 2,
                                left: isLineBased ? 15 : 7
                            },
                            width: ns.dashboard ? width : width - 15,
                            height: ns.dashboard ? height : height - 40,
                            theme: 'dv1'
                        };

                    // legend
                    if (!xLayout.hideLegend) {
                        defaultConfig.legend = getDefaultLegend(store, config);

                        if (defaultConfig.legend.position === 'right') {
                            defaultConfig.insetPaddingObject.top = ns.dashboard ? 20 : 40;
                            defaultConfig.insetPaddingObject.right = ns.dashboard ? 5 : 40;
                        }
                    }

                    // title
                    if (xLayout.hideTitle) {
                        defaultConfig.insetPadding = ns.dashboard ? 1 : 10;
                        defaultConfig.insetPaddingObject.top = ns.dashboard ? 3 : 10;
                    }
                    else {
                        defaultConfig.items = [getDefaultChartTitle(store)];
                    }

                    Ext.apply(defaultConfig, config);

                    // chart
                    chart = Ext.create('Ext.chart.Chart', defaultConfig);

                    chart.setChartSize = getDefaultChartSizeHandler();
                    chart.setTitlePosition = getDefaultChartTitlePositionHandler();

                    chart.onViewportResize = function() {
                        chart.setChartSize();
                        chart.redraw();
                        chart.setTitlePosition();
                    };

                    chart.on('resize', function() {
                        chart.setTitlePosition();
                    });

                    return chart;
                };

                generator.column = function(isStacked) {
                    var store = getDefaultStore(isStacked),
                        numericAxis = getDefaultNumericAxis(store),
                        categoryAxis = getDefaultCategoryAxis(store),
                        axes = [numericAxis, categoryAxis],
                        series = [getDefaultSeries(store)];

                    // options
                    if (xLayout.showTrendLine) {
                        series = series.concat(getDefaultTrendLines(store, isStacked));
                    }

                    if (xLayout.targetLineValue) {
                        series.push(getDefaultTargetLine(store));
                    }

                    if (xLayout.baseLineValue) {
                        series.push(getDefaultBaseLine(store));
                    }

                    // theme
                    setDefaultTheme(store, isStacked);

                    return getDefaultChart({
                        store: store,
                        axes: axes,
                        series: series
                    });
                };

                generator.stackedcolumn = function() {
                    var chart = this.column(true);

                    for (var i = 0, item; i < chart.series.items.length; i++) {
                        item = chart.series.items[i];

                        if (item.type === conf.finals.chart.column) {
                            item.stacked = true;
                        }
                    }

                    return chart;
                };

                generator.bar = function(isStacked) {
                    var store = getDefaultStore(isStacked),
                        numericAxis = getDefaultNumericAxis(store),
                        categoryAxis = getDefaultCategoryAxis(store),
                        axes,
                        series = getDefaultSeries(store),
                        trendLines,
                        targetLine,
                        baseLine,
                        chart;

                    // Axes
                    numericAxis.position = 'bottom';
                    categoryAxis.position = 'left';
                    categoryAxis.label.rotate.degrees = 360;
                    axes = [numericAxis, categoryAxis];

                    // Series
                    series.type = 'bar';
                    series.axis = 'bottom';

                    // Options
                    if (xLayout.showValues) {
                        series.label = {
                            display: 'outside',
                            'text-anchor': 'middle',
                            field: store.rangeFields
                        };
                    }

                    series = [series];

                    if (xLayout.showTrendLine) {
                        trendLines = getDefaultTrendLines(store, isStacked);

                        for (var i = 0; i < trendLines.length; i++) {
                            trendLines[i].axis = 'bottom';
                            trendLines[i].xField = store.trendLineFields[i];
                            trendLines[i].yField = store.domainFields;
                        }

                        series = series.concat(trendLines);
                    }

                    if (xLayout.targetLineValue) {
                        targetLine = getDefaultTargetLine(store);
                        targetLine.axis = 'bottom';
                        targetLine.xField = store.targetLineFields;
                        targetLine.yField = store.domainFields;

                        series.push(targetLine);
                    }

                    if (xLayout.baseLineValue) {
                        baseLine = getDefaultBaseLine(store);
                        baseLine.axis = 'bottom';
                        baseLine.xField = store.baseLineFields;
                        baseLine.yField = store.domainFields;

                        series.push(baseLine);
                    }

                    // Theme
                    setDefaultTheme(store);

                    return getDefaultChart({
                        store: store,
                        axes: axes,
                        series: series
                    });
                };

                generator.stackedbar = function() {
                    var chart = this.bar(true);

                    for (var i = 0, item; i < chart.series.items.length; i++) {
                        item = chart.series.items[i];

                        if (item.type === conf.finals.chart.bar) {
                            item.stacked = true;
                        }
                    }

                    return chart;
                };

                generator.line = function() {
                    var store = getDefaultStore(),
                        numericAxis = getDefaultNumericAxis(store),
                        categoryAxis = getDefaultCategoryAxis(store),
                        axes = [numericAxis, categoryAxis],
                        series = [],
                        colors = conf.chart.theme.dv1.slice(0, store.rangeFields.length),
                        seriesTitles = getDefaultSeriesTitle(store);

                    // Series
                    for (var i = 0, line; i < store.rangeFields.length; i++) {
                        line = {
                            type: 'line',
                            axis: 'left',
                            xField: store.domainFields,
                            yField: store.rangeFields[i],
                            style: {
                                opacity: 0.8,
                                lineWidth: 3
                            },
                            markerConfig: {
                                type: 'circle',
                                radius: 4
                            },
                            tips: getDefaultTips(),
                            title: seriesTitles[i]
                        };

                        //if (xLayout.showValues) {
                            //line.label = {
                                //display: 'over',
                                //field: store.rangeFields[i]
                            //};
                        //}

                        series.push(line);
                    }

                    // Options, theme colors
                    if (xLayout.showTrendLine) {
                        series = getDefaultTrendLines(store).concat(series);

                        colors = colors.concat(colors);
                    }

                    if (xLayout.targetLineValue) {
                        series.push(getDefaultTargetLine(store));

                        colors.push('#051a2e');
                    }

                    if (xLayout.baseLineValue) {
                        series.push(getDefaultBaseLine(store));

                        colors.push('#051a2e');
                    }

                    // Theme
                    Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
                        constructor: function(config) {
                            Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
                                seriesThemes: colors,
                                colors: colors
                            }, config));
                        }
                    });

                    return getDefaultChart({
                        store: store,
                        axes: axes,
                        series: series
                    });
                };

                generator.area = function() {

                    // NB, always true for area charts as extjs area charts cannot handle nulls
                    xLayout.hideEmptyRows = true;

                    var store = getDefaultStore(true),
                        numericAxis = getDefaultNumericAxis(store),
                        categoryAxis = getDefaultCategoryAxis(store),
                        axes = [numericAxis, categoryAxis],
                        series = getDefaultSeries(store);

                    series.type = 'area';
                    series.style.opacity = 0.7;
                    series.style.lineWidth = 0;
                    delete series.label;
                    delete series.tips;
                    series = [series];

                    // Options
                    if (xLayout.showTrendLine) {
                        series = series.concat(getDefaultTrendLines(store, true));
                    }

                    if (xLayout.targetLineValue) {
                        series.push(getDefaultTargetLine(store));
                    }

                    if (xLayout.baseLineValue) {
                        series.push(getDefaultBaseLine(store));
                    }

                    // Theme
                    setDefaultTheme(store);

                    return getDefaultChart({
                        store: store,
                        axes: axes,
                        series: series
                    });
                };

                generator.pie = function() {
                    var store = getDefaultStore(),
                        series,
                        colors,
                        chart,
                        label = {
                            field: conf.finals.data.domain
                        };

                    // label
                    if (xLayout.showValues) {
                        var labelFont = conf.chart.style.fontFamily,
                            labelColor;

                        if (Ext.isObject(xLayout.seriesStyle)) {
                            var style = xLayout.seriesStyle;

                            // color
                            labelColor = style.labelColor || labelColor;

                            if (style.labelFont) {
                                labelFont = style.labelFont;
                            }
                            else {
                                labelFont = style.labelFontWeight ? style.labelFontWeight + ' ' : 'normal ';
                                labelFont += style.labelFontSize ? parseFloat(style.labelFontSize) + 'px ' : '11px ';
                                labelFont +=  style.labelFontFamily ? style.labelFontFamily : conf.chart.style.fontFamily;
                            }
                        }

                        label.display = 'middle';
                        label.contrast = !labelColor;
                        label.font = labelFont;
                        label.fill = labelColor;
                        label.renderer = function(value) {
                            var record = store.getAt(store.findExact(conf.finals.data.domain, value));
                            return record.data[store.rangeFields[0]];
                        };
                    }

                    // series
                    series = [{
                        type: 'pie',
                        field: store.rangeFields[0],
                        donut: 5,
                        showInLegend: true,
                        highlight: {
                            segment: {
                                margin: 5
                            }
                        },
                        label: label,
                        style: {
                            opacity: 0.8,
                            stroke: '#555'
                        },
                        tips: {
                            trackMouse: true,
                            cls: 'dv-chart-tips',
                            renderer: function(item) {
                                this.update('<div style="text-align:center"><div style="font-size:17px; font-weight:bold">' + item.data[store.rangeFields[0]] + '</div><div style="font-size:10px">' + item.data[conf.finals.data.domain] + '</div></div>');
                            }
                        },
                        shadowAttributes: false,
                        title: getPieSeriesTitle(store)
                    }];

                    // theme
                    colors = conf.chart.theme.dv1.slice(0, xResponse.nameHeaderMap[xLayout.rowDimensionNames[0]].ids.length);

                    Ext.chart.theme.dv1 = Ext.extend(Ext.chart.theme.Base, {
                        constructor: function(config) {
                            Ext.chart.theme.Base.prototype.constructor.call(this, Ext.apply({
                                seriesThemes: colors,
                                colors: colors
                            }, config));
                        }
                    });

                    // chart
                    chart = getDefaultChart({
                        store: store,
                        series: series,
                        insetPaddingObject: {
                            top: 15,
                            right: 2,
                            bottom: 13,
                            left: 7
                        }
                    });

                    return chart;
                };

                generator.radar = function() {
                    var store = getDefaultStore(),
                        axes = [],
                        series = [],
                        seriesTitles = getDefaultSeriesTitle(store),
                        chart;

                    // axes
                    axes.push({
                        type: 'Radial',
                        position: 'radial',
                        label: {
                            display: true
                        }
                    });

                    // series
                    for (var i = 0, obj; i < store.rangeFields.length; i++) {
                        obj = {
                            showInLegend: true,
                            type: 'radar',
                            xField: store.domainFields,
                            yField: store.rangeFields[i],
                            style: {
                                opacity: 0.5
                            },
                            tips: getDefaultTips(),
                            title: seriesTitles[i]
                        };

                        if (xLayout.showValues) {
                            obj.label = {
                                display: 'over',
                                field: store.rangeFields[i]
                            };
                        }

                        series.push(obj);
                    }

                    chart = getDefaultChart({
                        store: store,
                        axes: axes,
                        series: series,
                        theme: 'Category2'
                    });

                    chart.insetPadding = 40;
                    chart.height = ns.app.centerRegion.getHeight() - 80;

                    chart.setChartSize = function() {
                        //this.animate = false;
                        this.setWidth(ns.app.centerRegion.getWidth());
                        this.setHeight(ns.app.centerRegion.getHeight() - 80);
                        //this.animate = true;
                    };

                    return chart;
                };

                generator.gauge = function() {
                    var valueColor = '#aaa',
                        store,
                        axis,
                        series,
                        legend,
                        config,
                        chart;

                    // overwrite items
                    columnIds = [columnIds[0]];
                    failSafeColumnIds = [failSafeColumnIds[0]];
                    rowIds = [rowIds[0]];

                    // store
                    store = getDefaultStore();

                    // axis
                    axis = {
                        type: 'gauge',
                        position: 'gauge',
                        minimum: 0,
                        maximum: 100,
                        steps: 10,
                        margin: -7
                    };

                    // series, legendset
                    if (legendSet) {
                        valueColor = service.mapLegend.getColorByValue(legendSet, store.getRange()[0].data[failSafeColumnIds[0]]) || valueColor;
                    }

                    series = {
                        type: 'gauge',
                        field: store.rangeFields[0],
                        //donut: 5,
                        colorSet: [valueColor, '#ddd']
                    };

                    chart = getDefaultChart({
                        axes: [axis],
                        series: [series],
                        width: ns.app.centerRegion.getWidth(),
                        height: ns.app.centerRegion.getHeight() * 0.6,
                        store: store,
                        insetPadding: 100,
                        theme: null,
                        //animate: {
                            //easing: 'elasticIn',
                            //duration: 1000
                        //}
                        animate: false
                    });

                    if (xLayout.showValues) {
                        chart.items.push(Ext.create('Ext.draw.Sprite', {
                            type: 'text',
                            text: store.getRange()[0].data[failSafeColumnIds[0]],
                            font: 'normal 26px ' + conf.chart.style.fontFamily,
                            fill: '#111',
                            height: 40,
                            y: 	60
                        }));
                    }

                    chart.setChartSize = function() {
						//this.animate = false;
                        this.setWidth(ns.app.centerRegion.getWidth());
                        this.setHeight(ns.app.centerRegion.getHeight() * 0.6);
                        //this.animate = true;
                    };

                    chart.setTitlePosition = function() {
                        if (this.items) {
                            var title = this.items[0],
                                subTitle = this.items[1],
                                titleXFallback = 10;

                            if (title) {
                                var titleWidth = Ext.isIE ? title.el.dom.scrollWidth : title.el.getWidth(),
                                    titleX = titleWidth ? (ns.app.centerRegion.getWidth() / 2) - (titleWidth / 2) : titleXFallback;
                                title.setAttributes({
                                    x: titleX
                                }, true);
                            }

                            if (subTitle) {
                                var subTitleWidth = Ext.isIE ? subTitle.el.dom.scrollWidth : subTitle.el.getWidth(),
                                    subTitleX = subTitleWidth ? (ns.app.centerRegion.getWidth() / 2) - (subTitleWidth / 2) : titleXFallback;
                                subTitle.setAttributes({
                                    x: subTitleX
                                }, true);
                            }
                        }
                    };

                    return chart;
                };

                // initialize
                return generator[xLayout.type]();
            };

		}());

		// extend init
		(function() {

			// sort and extend dynamic dimensions
			if (Ext.isArray(init.dimensions)) {
				support.prototype.array.sort(init.dimensions);

				for (var i = 0, dim; i < init.dimensions.length; i++) {
					dim = init.dimensions[i];
					dim.dimensionName = dim.id;
					dim.objectName = conf.finals.dimension.dimension.objectName;
					conf.finals.dimension.objectNameMap[dim.id] = dim;
				}
			}

			// sort ouc
			if (init.user && init.user.ouc) {
				support.prototype.array.sort(init.user.ouc);
			}

			// legend set map
			//init.idLegendSetMap = {};

			//for (var i = 0, set; i < init.legendSets.length; i++) {
				//set = init.legendSets[i];
				//init.idLegendSetMap[set.id] = set;
			//}
		}());

		// instance
		return {
			conf: conf,
			api: api,
			support: support,
			service: service,
			web: web,
			init: init
		};
	};

	// PLUGIN

	// chart tips css

	EV.plugin = {};

	var init = {
			user: {},
            systemInfo: {}
		},
		configs = [],
		isInitStarted = false,
		isInitComplete = false,
		getInit,
        applyCss,
		execute;

	getInit = function(config) {
		var isInit = false,
			requests = [],
			callbacks = 0,
            type = config.plugin && config.crossDomain ? 'jsonp' : 'json',
			fn;

        init.contextPath = config.url;

		fn = function() {
			if (++callbacks === requests.length) {
				isInitComplete = true;

				for (var i = 0; i < configs.length; i++) {
					execute(configs[i]);
				}

				configs = [];
			}
		};

        // dhis2
        requests.push({
            url: init.contextPath + '/api/systemSettings.' + type + '?key=keyCalendar&key=keyDateFormat',
            disableCaching: false,
            success: function(r) {
                var systemSettings = r.responseText ? Ext.decode(r.responseText) : r,
                    userAccountConfig;

                init.systemInfo.dateFormat = Ext.isString(systemSettings.keyDateFormat) ? systemSettings.keyDateFormat.toLowerCase() : 'yyyy-mm-dd';
                init.systemInfo.calendar = systemSettings.keyCalendar;

                // user-account
                userAccountConfig = {
                    url: init.contextPath + '/api/me/user-account.' + type,
                    disableCaching: false,
                    success: function(r) {
                        init.userAccount = r.responseText ? Ext.decode(r.responseText) : r;

                        var onScriptReady = function() {
                            var defaultKeyUiLocale = 'en',
                                defaultKeyAnalysisDisplayProperty = 'name',
                                namePropertyUrl,
                                contextPath,
                                keyUiLocale,
                                dateFormat,
                                optionSetVersionConfig;

                            init.userAccount.settings.keyUiLocale = init.userAccount.settings.keyUiLocale || defaultKeyUiLocale;
                            init.userAccount.settings.keyAnalysisDisplayProperty = init.userAccount.settings.keyAnalysisDisplayProperty || defaultKeyAnalysisDisplayProperty;

                            // local vars
                            contextPath = init.contextPath;
                            keyUiLocale = init.userAccount.settings.keyUiLocale;
                            keyAnalysisDisplayProperty = init.userAccount.settings.keyAnalysisDisplayProperty;
                            namePropertyUrl = keyAnalysisDisplayProperty === defaultKeyAnalysisDisplayProperty ? keyAnalysisDisplayProperty : keyAnalysisDisplayProperty + '|rename(' + defaultKeyAnalysisDisplayProperty + ')';
                            dateFormat = init.systemInfo.dateFormat;

                            init.namePropertyUrl = namePropertyUrl;

                            // dhis2
                            dhis2.util.namespace('dhis2.ev');

                            dhis2.ev.store = dhis2.ev.store || new dhis2.storage.Store({
                                name: 'dhis2',
                                adapters: [dhis2.storage.IndexedDBAdapter, dhis2.storage.DomSessionStorageAdapter, dhis2.storage.InMemoryAdapter],
                                objectStores: ['optionSets']
                            });

                            optionSetVersionConfig = {
                                url: contextPath + '/api/optionSets.' + type + '?fields=id,version&paging=false',
                                disableCashing: false,
                                success: function(r) {
                                    var optionSets = (r.responseText ? Ext.decode(r.responseText).optionSets : r.optionSets) || [],
                                        store = dhis2.ev.store,
                                        ids = [],
                                        url = '',
                                        callbacks = 0,
                                        checkOptionSet,
                                        updateStore,
                                        optionSetConfig;

                                    optionSetConfig = {
                                        url: contextPath + '/api/optionSets.' + type + '?fields=id,name,version,options[code,name]&paging=false' + url,
                                        disableCashing: false,
                                        success: function(r) {
                                            var sets = r.responseText ? Ext.decode(r.responseText).optionSets : r.optionSets;
                                            store.setAll('optionSets', sets).done(fn);
                                        }
                                    };

                                    updateStore = function() {
                                        if (++callbacks === optionSets.length) {
                                            if (!ids.length) {
                                                fn();
                                                return;
                                            }

                                            for (var i = 0; i < ids.length; i++) {
                                                url += '&filter=id:eq:' + ids[i];
                                            }

                                            if (type === 'jsonp') {
                                                Ext.data.JsonP.request(optionSetConfig);
                                            }
                                            else {
                                                Ext.Ajax.request(optionSetConfig);
                                            }
                                        }
                                    };

                                    registerOptionSet = function(optionSet) {
                                        store.get('optionSets', optionSet.id).done( function(obj) {
                                            if (!Ext.isObject(obj) || obj.version !== optionSet.version) {
                                                ids.push(optionSet.id);
                                            }

                                            updateStore();
                                        });
                                    };

                                    store.open().done( function() {
                                        for (var i = 0; i < optionSets.length; i++) {
                                            registerOptionSet(optionSets[i]);
                                        }
                                    });
                                }
                            };

                            // option sets
                            if (type === 'jsonp') {
                                Ext.data.JsonP.request(optionSetVersionConfig);
                            }
                            else {
                                Ext.Ajax.request(optionSetVersionConfig);
                            }
                        };

                        // init
                        if (config.dashboard) {
                            onScriptReady();
                        }
                        else {
                            Ext.Loader.injectScriptElement(init.contextPath + '/dhis-web-commons/javascripts/jQuery/jquery.min.js', function () {
                                Ext.Loader.injectScriptElement(init.contextPath + '/dhis-web-commons/javascripts/dhis2/dhis2.util.js', function () {
                                    Ext.Loader.injectScriptElement(init.contextPath + '/dhis-web-commons/javascripts/dhis2/dhis2.storage.js', function () {
                                        Ext.Loader.injectScriptElement(init.contextPath + '/dhis-web-commons/javascripts/dhis2/dhis2.storage.idb.js', function () {
                                            Ext.Loader.injectScriptElement(init.contextPath + '/dhis-web-commons/javascripts/dhis2/dhis2.storage.ss.js', function () {
                                                Ext.Loader.injectScriptElement(init.contextPath + '/dhis-web-commons/javascripts/dhis2/dhis2.storage.memory.js', function () {
                                                    onScriptReady();
                                                });
                                            });
                                        });
                                    });
                                });
                            });
                        }
                    }
                };

                if (type === 'jsonp') {
                    Ext.data.JsonP.request(userAccountConfig);
                }
                else {
                    Ext.Ajax.request(userAccountConfig);
                }
            }
        });

		// user orgunit
		requests.push({
			url: init.contextPath + '/api/organisationUnits.' + type + '?userOnly=true&fields=id,name,children[id,name]&paging=false',
            disableCaching: false,
			success: function(r) {
				var organisationUnits = (r.responseText ? Ext.decode(r.responseText).organisationUnits : r) || [],
                    ou = [],
                    ouc = [];

                if (organisationUnits.length) {
                    for (var i = 0, org; i < organisationUnits.length; i++) {
                        org = organisationUnits[i];

                        ou.push(org.id);

                        if (org.children) {
                            ouc = Ext.Array.clean(ouc.concat(Ext.Array.pluck(org.children, 'id') || []));
                        }
                    }

                    init.user = init.user || {};
                    init.user.ou = ou;
                    init.user.ouc = ouc;
                }
                else {
                    alert('User is not assigned to any organisation units');
                }

                fn();
			}
		});

        // dimensions
		requests.push({
			url: init.contextPath + '/api/dimensions.' + type + '?fields=id,name&paging=false',
            disableCaching: false,
			success: function(r) {
				init.dimensions = r.responseText ? Ext.decode(r.responseText).dimensions : r.dimensions;
				fn();
			}
		});

        init.legendSets = [];

		for (var i = 0; i < requests.length; i++) {
            if (type === 'jsonp') {
                Ext.data.JsonP.request(requests[i]);
            }
            else {
                Ext.Ajax.request(requests[i]);
            }
		}
	};

    applyCss = function() {
        var css = '.dv-chart-tips { border-radius: 2px; padding: 0px 3px 1px; border: 2px solid #000; background-color: #000; } \n';
        css += '.dv-chart-tips .x-tip-body { background-color: #000; font-size: 13px; font-weight: normal; color: #fff; -webkit-text-stroke: 0; } \n';
        css += '.dv-chart-tips .x-tip-body div { font-family: arial,sans-serif,ubuntu,consolas !important; } \n';

        // load mask css
        css += '.x-mask-msg { padding: 0; border: 0 none; background-image: none; background-color: transparent; } \n';
        css += '.x-mask-msg div { background-position: 11px center; } \n';
        css += '.x-mask-msg .x-mask-loading { border: 0 none; \n background-color: #000; color: #fff; border-radius: 2px; padding: 12px 14px 12px 30px; opacity: 0.65; } \n';
        css += '.x-mask { opacity: 0; } \n';

        Ext.util.CSS.createStyleSheet(css);
    };

	execute = function(config) {
		var validateConfig,
            extendInstance,
			createViewport,
			initialize,
			ns = {
				core: {},
				app: {}
			};

		validateConfig = function(config) {
			if (!Ext.isObject(config)) {
				console.log('Event report configuration is not an object');
				return;
			}

			if (!Ext.isString(config.el)) {
				console.log('No valid element id provided');
				return;
			}

			config.id = config.id || config.uid;

			return true;
		};

        extendInstance = function(ns) {
            var init = ns.core.init,
                conf = ns.core.conf,
				api = ns.core.api,
				support = ns.core.support,
				service = ns.core.service,
				web = ns.core.web,
                dimConf = conf.finals.dimension,
                type = ns.plugin && ns.crossDomain ? 'jsonp' : 'json',
                headerMap = {
                    json: 'application/json',
                    jsonp: 'application/javascript'
                },
                headers = {
                    'Content-Type': headerMap[type],
                    'Accepts': headerMap[type]
                };

            ns.plugin = init.plugin;
            ns.dashboard = init.dashboard;
            ns.crossDomain = init.crossDomain;
            ns.skipMask = init.skipMask;
            ns.skipFade = init.skipFade;

			init.el = config.el;

            if (!ns.skipFade) {
                Ext.get(init.el).setStyle('opacity', 0);
            }

			// report
			web.report = web.report || {};

			web.report.loadReport = function(obj) {
                var success,
                    failure,
                    config = {};

                if (!(obj && obj.id)) {
                    console.log('Error, no chart id');
                    return;
                }

                success = function(r) {
                    var layout = api.layout.Layout((r.responseText ? Ext.decode(r.responseText) : r), obj);

                    if (layout) {
                        web.report.getData(layout, true);
                    }
                };

                failure = function(r) {
                    console.log(obj.id, (r.responseText ? Ext.decode(r.responseText) : r));
                };

                config.url = init.contextPath + '/api/eventCharts/' + obj.id + '.' + type + '?fields=' + conf.url.analysisFields.join(',');
                config.disableCaching = false;
                config.headers = headers;
                config.success = success;
                config.failure = failure;

                if (type === 'jsonp') {
                    Ext.data.JsonP.request(config);
                }
                else {
                    Ext.Ajax.request(config);
                }
			};

			web.report.getData = function(layout, isUpdateGui) {
				var xLayout,
					paramString,
                    success,
                    failure,
                    config = {};

				if (!layout) {
					return;
				}

                //xLayout = service.layout.getExtendedLayout(layout);
				paramString = web.analytics.getParamString(layout, type);

				// mask
                if (!ns.skipMask) {
                    web.mask.show(ns.app.centerRegion);
                }

                success = function(r) {
                    var response = api.response.Response((r.responseText ? Ext.decode(r.responseText) : r));

                    if (!response && !ns.skipMask) {
                        web.mask.hide(ns.app.centerRegion);
                        return;
                    }

                    // add to dimConf, TODO
                    for (var i = 0, map = dimConf.objectNameMap, header; i < response.headers.length; i++) {
                        header = response.headers[i];
                        map[header.name] = map[header.name] || {
                            id: header.name,
                            dimensionName: header.name,
                            name: header.column
                        };
                    }

                    ns.app.paramString = paramString;

                    web.report.createReport(layout, response, isUpdateGui);
                };

                failure = function(r) {
                    if (!ns.skipMask) {
                        web.mask.hide(ns.app.centerRegion);
                    }

                    console.log(r);
                };

                config.url = init.contextPath + paramString;
                config.disableCaching = false;
                config.scope = this;
                config.timeout = 60000;
                config.headers = headers;
                config.success = success;
                config.failure = failure;

                if (type === 'jsonp') {
                    Ext.data.JsonP.request(config);
                }
                else {
                    Ext.Ajax.request(config);
                }
			};

			web.report.createReport = function(layout, response, isUpdateGui) {
                var xLayout,
                    xResponse,
                    xColAxis,
                    xRowAxis,
                    chart,
                    getOptionSets,
                    getReport,
                    getSXLayout,
                    getXResponse;

                getOptionSets = function(xResponse, callbackFn) {
                    var optionSetHeaders = [];

                    for (var i = 0; i < xResponse.headers.length; i++) {
                        if (Ext.isString(xResponse.headers[i].optionSet)) {
                            optionSetHeaders.push(xResponse.headers[i]);
                        }
                    }

                    if (optionSetHeaders.length) {
                        var callbacks = 0,
                            optionMap = {},
                            getOptions,
                            fn;

                        fn = function() {
                            if (++callbacks === optionSetHeaders.length) {
                                xResponse.metaData.optionNames = optionMap;
                                callbackFn();
                            }
                        };

                        getOptions = function(optionSetId, dataElementId) {
                            dhis2.ev.store.get('optionSets', optionSetId).done( function(obj) {
                                Ext.apply(optionMap, support.prototype.array.getObjectMap(obj.options, 'code', 'name', dataElementId));
                                fn();
                            });
                        };

                        // execute
                        for (var i = 0, header, optionSetId, dataElementId; i < optionSetHeaders.length; i++) {
                            header = optionSetHeaders[i];
                            optionSetId = header.optionSet;
                            dataElementId = header.name;

                            getOptions(optionSetId, dataElementId);
                        }
                    }
                    else {
                        callbackFn();
                    }
                };

                getReport = function() {
                    if (!xLayout && !ns.skipMask) {
                        web.mask.hide(ns.app.centerRegion);
                        return;
                    }

                    ns.app.layout = layout;
                    ns.app.xLayout = xLayout;
                    ns.app.response = response;
                    ns.app.xResponse = xResponse;

                    chart = web.report.aggregate.createChart(ns);

                    // fade
                    if (!ns.skipFade) {
                        chart.on('afterrender', function() {
                            Ext.defer( function() {
                                Ext.get(init.el).fadeIn({
                                    duration: 400
                                });
                            }, 300 );
                        });
                    }

                    ns.app.centerRegion.removeAll();
                    ns.app.centerRegion.add(chart);

                    // after render
                    ns.app.chart = chart;

                    if (!ns.skipMask) {
                        web.mask.hide(ns.app.centerRegion);
                    }

                    if (EV.isDebug) {
                        console.log("layout", layout);
                        console.log("response", response);
                        console.log("xResponse", xResponse);
                        console.log("xLayout", xLayout);
                        console.log("core", ns.core);
                        console.log("app", ns.app);
                    }
                };

                getSXLayout = function() {
                    xLayout = service.layout.getSyncronizedXLayout(layout, xLayout, xResponse);

                    getReport();
                };

                getXResponse = function() {
                    xLayout = service.layout.getExtendedLayout(layout);
                    xResponse = service.response.aggregate.getExtendedResponse(xLayout, response);

                    getOptionSets(xResponse, getSXLayout);
                };

                // execute
                response = response || ns.app.response;

                getXResponse();
			};
        };

		createViewport = function() {
			var el = Ext.get(ns.core.init.el),
				centerRegion,
				elBorderW = parseInt(el.getStyle('border-left-width')) + parseInt(el.getStyle('border-right-width')),
				elBorderH = parseInt(el.getStyle('border-top-width')) + parseInt(el.getStyle('border-bottom-width')),
				elPaddingW = parseInt(el.getStyle('padding-left')) + parseInt(el.getStyle('padding-right')),
				elPaddingH = parseInt(el.getStyle('padding-top')) + parseInt(el.getStyle('padding-bottom')),
				width = el.getWidth() - elBorderW - elPaddingW,
				height = el.getHeight() - elBorderH - elPaddingH;

			centerRegion = Ext.create('Ext.panel.Panel', {
				renderTo: el,
				bodyStyle: 'border: 0 none',
				width: config.width || width,
				height: config.height || height,
				layout: 'fit'
			});

			return {
				centerRegion: centerRegion
			};
        };

		initialize = function() {
			if (!validateConfig(config)) {
				return;
			}

            applyCss();

            init.plugin = true;
            init.dashboard = Ext.isBoolean(config.dashboard) ? config.dashboard : false;
            init.crossDomain = Ext.isBoolean(config.crossDomain) ? config.crossDomain : true;
            init.skipMask = Ext.isBoolean(config.skipMask) ? config.skipMask : false;
            init.skipFade = Ext.isBoolean(config.skipFade) ? config.skipFade : false;

			ns.core = EV.getCore(Ext.clone(init));
			extendInstance(ns);

			ns.app.viewport = createViewport();
			ns.app.centerRegion = ns.app.viewport.centerRegion;

            Ext.get(config.el).setViewportWidth = function(width) {
                ns.app.centerRegion.setWidth(width);
            };

			if (config && config.id) {
				ns.core.web.report.loadReport(config);
			}
			else {
				layout = ns.core.api.layout.Layout(config);

				if (!layout) {
					return;
				}

				ns.core.web.report.getData(layout);
			}
		}();
	};

	EV.plugin.getEventChart = function(config) {
		if (Ext.isString(config.url) && config.url.split('').pop() === '/') {
			config.url = config.url.substr(0, config.url.length - 1);
		}

		if (isInitComplete) {
			execute(config);
		}
		else {
			configs.push(config);

			if (!isInitStarted) {
				isInitStarted = true;
				getInit(config);
			}
		}
	};

	DHIS = Ext.isObject(window['DHIS']) ? DHIS : {};
	DHIS.getEventChart = EV.plugin.getEventChart;
});
