DHIS = {};
DHIS.conf = {
    finals: {
        ajax: {
            url_visualizer: '../',
            url_commons: '../../dhis-web-commons-ajax-json/',
            url_portal: '../../dhis-web-portal/',
            url_indicator: 'getAggregatedIndicatorValuesPlugin',
            url_dataelement: 'getAggregatedDataValuesPlugin'
        },        
        dimension: {
            indicator: {
                value: 'indicator',
                rawvalue: 'Indicator'
            },
            dataelement: {
                value: 'dataelement',
                rawvalue: 'Data element'
            },
            period: {
                value: 'period',
                rawvalue: 'Period'
            },
            organisationunit: {
                value: 'organisationunit',
                rawvalue: 'Organisation unit'
            }
        },        
        chart: {
            x: 'x',
            series: 'series',
            category: 'category',
            filter: 'filter',
            column: 'column',
            column_stacked: 'column_stacked',
            bar: 'bar',
            bar_stacked: 'bar_stacked',
            line: 'line',
            area: 'area',
            pie: 'pie'
        }
    },
    style: {
        label: {
            period_group: 'font:bold 11px arial; color:#444; line-height:20px'
        }
    },
    layout: {
        west_cmp_width: 380,
        west_width: 424,
        east_tbar_height: 27
    }
};

Ext.onReady( function() {
    DHIS.initialize = function() {
        DHIS.store.column = DHIS.store.defaultChartStore;
        DHIS.store.column_stacked = DHIS.store.defaultChartStore;
        DHIS.store.bar_stacked = DHIS.store.bar;
        DHIS.store.line = DHIS.store.defaultChartStore;
        DHIS.store.area = DHIS.store.defaultChartStore;
        DHIS.store.pie = DHIS.store.defaultChartStore;
        
        DHIS.getChart = DHIS.exe.execute;
    };
    
    DHIS.util = {
        getCmp: function(q) {
            return DHIS.viewport.query(q)[0];
        },
        viewport: {
            getSize: function() {
                var c = Ext.getCmp('center');
                return {x: c.getWidth(), y: c.getHeight()};
            },
            getXY: function() {
                var c = Ext.getCmp('center');
                return {x: c.x + 15, y: c.y + 43};
            }
        },
        multiselect: {
            select: function(a, s) {
                var selected = a.getValue();
                if (selected.length) {
                    var array = [];
                    Ext.Array.each(selected, function(item) {
                        array.push({id: item, shortName: a.store.getAt(a.store.find('id', item)).data.shortName});
                    });
                    s.store.add(array);
                }
                this.filterAvailable(a, s);
            },            
            selectAll: function(a, s) {
                var array = [];
                a.store.each( function(r) {
                    array.push({id: r.data.id, shortName: r.data.shortName});
                });
                s.store.add(array);
                this.filterAvailable(a, s);
            },            
            unselect: function(a, s) {
                var selected = s.getValue();
                if (selected.length) {
                    Ext.Array.each(selected, function(item) {
                        s.store.remove(s.store.getAt(s.store.find('id', item)));
                    });                    
                    this.filterAvailable(a, s);
                }
            },
            unselectAll: function(a, s) {
                s.store.removeAll();
                a.store.clearFilter();
            },
            filterAvailable: function(a, s) {
                a.store.filterBy( function(r) {
                    var filter = true;
                    s.store.each( function(r2) {
                        if (r.data.id === r2.data.id) {
                            filter = false;
                        }
                    });
                    return filter;
                });
            }
        },
        fieldset: {
            collapseOthers: function(name) {
                for (var p in DHIS.conf.finals.dimension) {
                    if (DHIS.conf.finals.dimension[p].value !== name) {
                        DHIS.util.getCmp('fieldset[name="' + DHIS.conf.finals.dimension[p].value + '"]').collapse();
                    }
                }
            },
            toggleIndicator: function() {
                DHIS.util.getCmp('fieldset[name="' + DHIS.conf.finals.dimension.indicator.value + '"]').toggle();
            },
            toggleDataElement: function() {
                DHIS.util.getCmp('fieldset[name="' + DHIS.conf.finals.dimension.dataelement.value + '"]').toggle();
            },
            togglePeriod: function() {
                DHIS.util.getCmp('fieldset[name="' + DHIS.conf.finals.dimension.period.value + '"]').toggle();
            },
            toggleOrganisationUnit: function() {
                DHIS.util.getCmp('fieldset[name="' + DHIS.conf.finals.dimension.organisationunit.value + '"]').toggle();
            }
        },
        button: {
            getValue: function() {
                for (var i = 0; i < DHIS.cmp.charttype.length; i++) {
                    if (DHIS.cmp.charttype[i].pressed) {
                        return DHIS.cmp.charttype[i].name;
                    }
                }
            },
            toggleHandler: function(b) {
                if (!b.pressed) {
                    b.toggle();
                }
            }
        },
        store: {
            addToStorage: function(s) {
                s.each( function(r) {
                    if (!s.storage[r.data.id]) {
                        s.storage[r.data.id] = {id: r.data.id, shortName: r.data.shortName, name: r.data.shortName, parent: s.parent};
                    }
                });
            },
            loadFromStorage: function(s) {
                var items = [];
                s.removeAll();
                for (var obj in s.storage) {
                    if (s.storage[obj].parent === s.parent) {
                        items.push(s.storage[obj]);
                    }
                }
                items = Ext.Array.sort(items);
                s.add(items);
            },
            containsParent: function(s) {
                for (var obj in s.storage) {
                    if (s.storage[obj].parent === s.parent) {
                        return true;
                    }
                }
                return false;
            }
        },
        dimension: {
            indicator: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each( DHIS.state.conf.indicators, function(r) {
                        a.push('indicatorIds=' + r);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [];
                    DHIS.util.getCmp('multiselect[name="selectedIndicators"]').store.each( function(r) {
                        a.push(DHIS.util.chart.getEncodedSeriesName(r.data.shortName));
                    });
                    if (exception && !a.length) {
                        alert('No indicators selected');
                    }
                    return a;
                }
            },
            dataelement: {
                getUrl: function(isFilter) {
                    var a = [];
                    DHIS.state.conf.dataelements.each( function(r) {
                        a.push('dataElementIds=' + r.data.id);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [];
                    DHIS.util.getCmp('multiselect[name="selectedDataElements"]').store.each( function(r) {
                        a.push(DHIS.util.chart.getEncodedSeriesName(r.data.shortName));
                    });
                    if (exception && !a.length) {
                        alert('No data elements selected');
                    }
                    return a;
                }
            },
            period: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.conf.periods, function(r) {
						a.push(r + '=true')
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [],
                        cmp = DHIS.cmp.dimension.period;
                    Ext.Array.each(cmp, function(item) {
                        if (item.getValue()) {
                            Ext.Array.each(DHIS.init.system.periods[item.paramName], function(item) {
                                a.push(DHIS.util.chart.getEncodedSeriesName(item.name));
                            });
                        }
                    });
                    if (exception && !a.length) {
                        alert('No periods selected');
                    }
                    return a;
                },
                getNameById: function(id) {
                    for (var obj in DHIS.init.system.periods) {
                        var a = DHIS.init.system.periods[obj];
                        for (var i = 0; i < a.length; i++) {
                            if (a[i].id == id) {
                                return a[i].name;
                            }
                        };
                    }
                }
            },
            organisationunit: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.conf.organisationunits, function(r) {
						a.push('organisationUnitIds=' + r)
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [],
                        treepanel = DHIS.util.getCmp('treepanel'),
                        selection = treepanel.getSelectionModel().getSelection();
                    if (!selection.length) {
                        selection = [treepanel.getRootNode()];
                        treepanel.selectRoot();
                    }
                    Ext.Array.each(selection, function(r) {
                        a.push(DHIS.util.chart.getEncodedSeriesName(r.data.text));
                    });
                    if (exception && !a.length) {
                        alert('No organisation units selected');
                    }
                    return a;                        
                }
            }
        },
        chart: {
            getEncodedSeriesName: function(text) {
                return text.replace(/\./g,'');
            },
            getLegend: function(len) {
                return {
                    position: len > 6 ? 'right' : 'top',
                    boxStroke: '#ffffff',
                    boxStrokeWidth: 0
                };
            },
            getGrid: function() {
                return {
                    opacity: 1,
                    fill: '#f1f1f1',
                    stroke: '#aaa',
                    'stroke-width': 0.2
                };
            },
            line: {
                getSeriesArray: function() {
                    var a = [];
                    for (var i = 0; i < DHIS.store.chart.left.length; i++) {
                        a.push({
                            type: 'line',
                            axis: 'left',
                            xField: DHIS.store.chart.bottom,
                            yField: DHIS.store.chart.left[i]
                        });
                    }
                    return a;
                }
            }
        },
        combobox: {
            filter: {
                clearValue: function(v, cb, i, d) {
                    if (v === cb.getValue()) {
                        cb.clearValue();
                    }
                    else if ((v === i || v === d) && (cb.getValue() === i || cb.getValue() === d)) {
                        cb.clearValue();
                    }
                },
                category: function(vp) {
                    var cbs = vp.query('combobox[name="' + DHIS.conf.finals.chart.series + '"]')[0],
                        cbc = vp.query('combobox[name="' + DHIS.conf.finals.chart.category + '"]')[0],
                        cbf = vp.query('combobox[name="' + DHIS.conf.finals.chart.filter + '"]')[0],
                        v = cbs.getValue(),
                        i = DHIS.conf.finals.dimension.indicator.value,
                        d = DHIS.conf.finals.dimension.dataelement.value,
                        p = DHIS.conf.finals.dimension.period.value,
                        o = DHIS.conf.finals.dimension.organisationunit.value,
                        index = 0;
                        
                    this.clearValue(v, cbc, i, d);
                    this.clearValue(v, cbf, i, d);
                    
                    cbc.filterArray = [!(v === i || v === d), !(v === i || v === d), !(v === p), !(v === o)];
                    cbc.store.filterBy( function(r) {
                        return cbc.filterArray[index++];
                    });
                    
                    this.filter(vp);
                },                
                filter: function(vp) {
                    var cbc = vp.query('combobox[name="' + DHIS.conf.finals.chart.category + '"]')[0],
                        cbf = vp.query('combobox[name="' + DHIS.conf.finals.chart.filter + '"]')[0],
                        v = cbc.getValue(),
                        i = DHIS.conf.finals.dimension.indicator.value,
                        d = DHIS.conf.finals.dimension.dataelement.value,
                        p = DHIS.conf.finals.dimension.period.value,
                        o = DHIS.conf.finals.dimension.organisationunit.value,
                        index = 0;
                        
                    this.clearValue(v, cbf, i, d);
                        
                    cbf.filterArray = Ext.Array.clone(cbc.filterArray);
                    cbf.filterArray[0] = cbf.filterArray[0] ? !(v === i || v === d) : false;
                    cbf.filterArray[1] = cbf.filterArray[1] ? !(v === i || v === d) : false;
                    cbf.filterArray[2] = cbf.filterArray[2] ? !(v === p) : false;
                    cbf.filterArray[3] = cbf.filterArray[3] ? !(v === o) : false;
                    
                    cbf.store.filterBy( function(r) {
                        return cbf.filterArray[index++];
                    });
                }
            }
        },
		window: {
			datatable: {
				getHeight: function() {
					if (DHIS.value.values.length) {
						if (Ext.isWindows && Ext.isGecko) {
							return 22 * DHIS.value.values.length + 57;
						}
						else if (Ext.isWindows && Ext.isIE) {
							return 21 * DHIS.value.values.length + 58;
						}
						else {
							return 21 * DHIS.value.values.length + 57;
						}
					}
				}
			}
		},
        number: {
            isInteger: function(n) {
                var str = new String(n);
                if (str.indexOf('.') > -1) {
                    var d = str.substr(str.indexOf('.') + 1);
                    return (d.length === 1 && d == '0');
                }
                return false;
            },
            allValuesAreIntegers: function(values) {
                for (var i = 0; i < values.length; i++) {
                    if (!this.isInteger(values[i].v)) {
                        return false;
                    }
                }
                return true;
            },
            getChartAxisFormatRenderer: function() {
                return this.allValuesAreIntegers(DHIS.value.values) ? '0' : '0.0';
            }
        }
    };
    
    DHIS.store = {
        datatable: null,
        getDataTableStore: function(exe) {
            this.datatable = Ext.create('Ext.data.Store', {
                fields: [
                    DHIS.state.getIndiment().value,
                    DHIS.conf.finals.dimension.period.value,
                    DHIS.conf.finals.dimension.organisationunit.value,
                    'v'
                ],
                data: DHIS.value.values
            });
            
            if (exe) {
                DHIS.datatable.getDataTable(true);
            }
            else {
                return this.datatable;
            }
            
        },
        chart: null,
        getChartStore: function() {
            this[DHIS.state.type]();
        },
        defaultChartStore: function() {
            var keys = [];
            Ext.Array.each(DHIS.chart.data, function(item) {
                keys = Ext.Array.merge(keys, Ext.Object.getKeys(item));
            });
            this.chart = Ext.create('Ext.data.Store', {
                fields: keys,
                data: DHIS.chart.data
            });
            this.chart.bottom = [DHIS.conf.finals.chart.x];
            this.chart.left = keys.slice(0);
            for (var i = 0; i < this.chart.left.length; i++) {
                if (this.chart.left[i] === DHIS.conf.finals.chart.x) {
                    this.chart.left.splice(i, 1);
                }
            }
            
			DHIS.chart.getChart(true);
        },
        bar: function() {
            var properties = Ext.Object.getKeys(DHIS.chart.data[0]);
            this.chart = Ext.create('Ext.data.Store', {
                fields: properties,
                data: DHIS.chart.data
            });
            this.chart.left = properties.slice(0, 1);
            this.chart.bottom = properties.slice(1, properties.length);
            
			DHIS.chart.getChart(true);
        }
    };
    
    DHIS.state = {
		conf: null,
        type: DHIS.conf.finals.chart.column,
        indiment: [],
        period: [],
        organisationunit: [],
        series: {
            cmp: null,
            dimension: DHIS.conf.finals.dimension.indicator.value,
            data: []
        },
        category: {
            cmp: null,
            dimension: DHIS.conf.finals.dimension.period.value,
            data: []
        },
        filter: {
            cmp: null,
            dimension: DHIS.conf.finals.dimension.organisationunit.value,
            data: []
        },
        getState: function(conf) {
            this.resetState(conf);
            
            this.type = conf.type;
            this.series.dimension = conf.series;
            this.category.dimension = conf.category;
            this.filter.dimension = conf.filter;
            
            //DHIS.getChart({
				//type: 'column',
				//indicators: [359596,359596],
				//periods: ['monthsThisYear'],
				//organisationunits: [525],
				//series: 'i',
				//category: 'p',
				//filter: 'o',
				//div: 'bar_chart_1'
			//});
            
			DHIS.value.getValues();
        },
        getIndiment: function() {
            var i = DHIS.conf.finals.dimension.indicator.value;
            return (this.series.dimension === i || this.category.dimension === i || this.filter.dimension === i) ?
                DHIS.conf.finals.dimension.indicator : DHIS.conf.finals.dimension.dataelement;
        },
        isIndicator: function() {
            var i = DHIS.conf.finals.dimension.indicator.value;
            return (this.series.dimension === i || this.category.dimension === i || this.filter.dimension === i);
        },
        resetState: function(conf) {
			this.conf = conf;
            this.indiment = null;
            this.period = null;
            this.organisationunit = null;
            this.series.dimension = null;
            this.series.data = null;
            this.category.dimension = null;
            this.category.data = null;
            this.filter.dimension = null;
            this.filter.data = null;
        }
    };
    
    DHIS.value = {
        values: [],
        getValues: function() {
            var params = [],
                indicator = DHIS.conf.finals.dimension.indicator.value,
                dataelement = DHIS.conf.finals.dimension.dataelement.value,
                series = DHIS.state.series.dimension,
                category = DHIS.state.category.dimension,
                filter = DHIS.state.filter.dimension,
                indiment = DHIS.state.getIndiment().value,
                url = DHIS.state.isIndicator() ? DHIS.conf.finals.ajax.url_indicator : DHIS.conf.finals.ajax.url_dataelement;
                
            params = params.concat(DHIS.util.dimension[series].getUrl());
            params = params.concat(DHIS.util.dimension[category].getUrl());
            params = params.concat(DHIS.util.dimension[filter].getUrl(true));
alert(params);            
            
            var baseUrl = DHIS.conf.finals.ajax.url_visualizer + url + '.action';
            Ext.Array.each(params, function(item) {
                baseUrl = Ext.String.urlAppend(baseUrl, item);
            });
alert(baseUrl);            
            
            Ext.Ajax.request({
                url: baseUrl,
                success: function(r) {
                    DHIS.value.values = Ext.JSON.decode(r.responseText).values;
                    
                    if (!DHIS.value.values.length) {
                        alert('no data values');
                        return;
                    }
                    
                    Ext.Array.each(DHIS.value.values, function(item) {
                        item.v = parseFloat(item.v);
                    });
                    
					DHIS.chart.getData();
                }
            });
        }
    };
    
    DHIS.chart = {
        data: [],        
        getData: function() {
            this.data = [];
			
            Ext.Array.each(DHIS.state.category.data, function(item) {
                var obj = {};
                obj[DHIS.conf.finals.chart.x] = item;
                DHIS.chart.data.push(obj);
            });
            
            Ext.Array.each(DHIS.chart.data, function(item) {
                for (var i = 0; i < DHIS.state.series.data.length; i++) {
                    for (var j = 0; j < DHIS.value.values.length; j++) {
                        if (DHIS.value.values[j][DHIS.state.category.dimension] === item[DHIS.conf.finals.chart.x] && DHIS.value.values[j][DHIS.state.series.dimension] === DHIS.state.series.data[i]) {
                            item[DHIS.value.values[j][DHIS.state.series.dimension]] = DHIS.value.values[j].v;
                            break;
                        }
                    }
                }
            });
                
			DHIS.store.getChartStore(true);
        },        
        chart: null,
        getChart: function() {
            this[DHIS.state.type]();
			this.reload();
        },
        column: function(stacked) {
            this.chart = Ext.create('Ext.chart.Chart', {
				renderTo: DV.state.conf.div,
                width: DHIS.util.viewport.getSize().x,
                height: DHIS.util.viewport.getSize().y,
                animate: true,
                store: DHIS.store.chart,
                legend: DHIS.util.chart.getLegend(DHIS.state.series.data.length),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DHIS.store.chart.left,
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        },
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer())
                        }
                    },
                    {
                        title: DHIS.init.isInit ? 'Categories' : DHIS.conf.finals.dimension[DHIS.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: DHIS.store.chart.bottom
                    }
                ],
                series: [
                    {
                        type: 'column',
                        axis: 'left',
                        xField: DHIS.store.chart.bottom,
                        yField: DHIS.store.chart.left,
                        stacked: stacked,
                        style: {
                            opacity: 0.8
                        }
                    }
                ]
            });
        },
        column_stacked: function() {
            this.column(true);
        },
        bar: function(stacked) {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DHIS.util.viewport.getSize().x,
                height: DHIS.util.viewport.getSize().y,
                animate: true,
                store: DHIS.store.chart,
                legend: DHIS.util.chart.getLegend(DHIS.state.series.data.length),
                axes: [
                    {
                        title: DHIS.conf.finals.dimension[DHIS.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'left',
                        fields: DHIS.store.chart.left
                    },
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'bottom',
                        minimum: 0,
                        fields: DHIS.store.chart.bottom,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer())
                        },
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    }
                ],
                series: [
                    {
                        type: 'bar',
                        axis: 'bottom',
                        xField: DHIS.store.chart.left,
                        yField: DHIS.store.chart.bottom,
                        stacked: stacked,
                        style: {
                            opacity: 0.8
                        }
                    }
                ]
            });
        },
        bar_stacked: function() {
            this.bar(true);
        },
        line: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DHIS.util.viewport.getSize().x,
                height: DHIS.util.viewport.getSize().y,
                animate: true,
                store: DHIS.store.chart,
                legend: DHIS.util.chart.getLegend(DHIS.state.series.data.length),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DHIS.store.chart.left,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer())
                        },
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    },
                    {
                        title: DHIS.conf.finals.dimension[DHIS.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: DHIS.store.chart.bottom
                    }
                ],
                series: DHIS.util.chart.line.getSeriesArray()
            });
        },
        area: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DHIS.util.viewport.getSize().x,
                height: DHIS.util.viewport.getSize().y,
                animate: true,
                store: DHIS.store.chart,
                legend: DHIS.util.chart.getLegend(DHIS.state.series.data.length),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DHIS.store.chart.left,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer())
                        },
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    },
                    {
                        title: DHIS.conf.finals.dimension[DHIS.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: DHIS.store.chart.bottom
                    }
                ],
                series: [{
                    type: 'area',
                    axis: 'left',
                    xField: DHIS.store.chart.bottom[0],
                    yField: DHIS.store.chart.left,
                    style: {
                        opacity: 0.65
                    }
                }]
            });
        },
        pie: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DHIS.util.viewport.getSize().x,
                height: DHIS.util.viewport.getSize().y,
                animate: true,
                shadow: true,
                store: DHIS.store.chart,
                legend: DHIS.util.chart.getLegend(DHIS.state.category.data.length),
                insetPadding: 60,
                series: [{
                    type: 'pie',
                    field: DHIS.store.chart.left[0],
                    showInLegend: true,
                    tips: {
                        trackMouse: false,
                        width: 160,
                        height: 31,
                        renderer: function(i) {
                            this.setTitle('<span class="DHIS-chart-tips">' + i.data.x + ': <b>' + i.data[DHIS.store.chart.left[0]] + '</b></span>');
                        }
                    },
                    label: {
                        field: DHIS.store.chart.bottom[0]
                    },
                    highlight: {
                        segment: {
                            margin: 10
                        }
                    },
                    style: {
                        opacity: 0.9
                    }
                }]
            });
        },
        reload: function() {
            var c = Ext.getCmp('center');
            c.removeAll(true);
            c.add(this.chart);
        }
    };
    
    DHIS.datatable = {
        datatable: null,
        getDataTable: function(exe) {
            this.datatable = Ext.create('Ext.grid.Panel', {
                height: DHIS.util.viewport.getSize().y - DHIS.conf.layout.east_tbar_height,
                scroll: 'vertical',
                cls: 'DHIS-datatable',
                columns: [
                    {
                        text: DHIS.state.getIndiment().rawvalue,
                        dataIndex: DHIS.state.getIndiment().value,
                        width: 150
                    },
                    {
                        text: DHIS.conf.finals.dimension.period.rawvalue,
                        dataIndex: DHIS.conf.finals.dimension.period.value,
                        width: 100,
                        sortable: false
                    },
                    {
                        text: DHIS.conf.finals.dimension.organisationunit.rawvalue,
                        dataIndex: DHIS.conf.finals.dimension.organisationunit.value,
                        width: 150
                    },
                    {
                        text: 'Value',
                        dataIndex: 'v',
                        width: 80
                    }
                ],
                store: DHIS.store.datatable,
                listeners: {
                    afterrender: function() {
                        DHIS.cmp.datatable = this;
                    }
                }
            });
            
            if (exe) {
                this.reload();
            }
            else {
                return this.datatable;
            }
        },
        reload: function() {
            var c = DHIS.util.getCmp('panel[region="east"]');
            c.removeAll(true);
            c.add(this.datatable);
        }            
    };
    
    DHIS.exe = {
        execute: function(conf) {
			DHIS.state.getState(conf);
		}
    };
    
    DHIS.initialize();
});
