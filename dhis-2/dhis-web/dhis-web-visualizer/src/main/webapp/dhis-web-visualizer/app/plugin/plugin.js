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
        
        DHIS.getChart = DHIS.exe.addToQueue;
    };
    
    DHIS.projects = [];
    
    DHIS.util = {
        dimension: {
            indicator: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.state.conf.indicators, function(r) {
                        a.push('indicatorIds=' + r);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                }
            },
            dataelement: {
                getUrl: function(isFilter) {
                    var a = [];
                    DHIS.state.state.conf.dataelements.each( function(r) {
                        a.push('dataElementIds=' + r.data.id);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                }
            },
            period: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.state.conf.periods, function(r) {
						a.push(r + '=true')
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                }
            },
            organisationunit: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.state.conf.organisationunits, function(r) {
						a.push('organisationUnitIds=' + r)
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                }
            }
        },
        chart: {
            getEncodedSeriesName: function(text) {
                return text.replace(/\./g,'');q
            },
            getLegend: function() {
                var lp = DHIS.state.state.conf.legendPosition,
                    l = DHIS.state.state.series.data.length,
                    p = lp ? lp : (l > 5 ? 'right' : 'top');
                return {
                    position: p,
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
                getSeriesArray: function(project) {
                    var a = [];
                    for (var i = 0; i < project.store.left.length; i++) {
                        a.push({
                            type: 'line',
                            axis: 'left',
                            xField: project.store.bottom,
                            yField: project.store.left[i]
                        });
                    }
                    return a;
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
            getChartAxisFormatRenderer: function(values) {
                return this.allValuesAreIntegers(values) ? '0' : '0.0';
            }
        },
        string: {
            extendUrl: function(url) {
                if (url.charAt(url.length-1) !== '/') {
                    url += '/';
                }
                return url;
            }
        }
    };
    
    DHIS.store = {
        getChartStore: function(project) {
            this[project.state.type](project);
        },
        defaultChartStore: function(project) {
            var keys = [];
            Ext.Array.each(project.data, function(item) {
                keys = Ext.Array.merge(keys, Ext.Object.getKeys(item));
            });
            project.store = Ext.create('Ext.data.Store', {
                fields: keys,
                data: project.data
            });
            project.store.bottom = [DHIS.conf.finals.chart.x];
            project.store.left = keys.slice(0);
            for (var i = 0; i < project.store.left.length; i++) {
                if (project.store.left[i] === DHIS.conf.finals.chart.x) {
                    project.store.left.splice(i, 1);
                }
            }
            
			DHIS.chart.getChart(project);
        },
        bar: function(project) {
            var properties = Ext.Object.getKeys(project.data[0]);
            project.store = Ext.create('Ext.data.Store', {
                fields: properties,
                data: project.data
            });
            project.store.left = properties.slice(0, 1);
            project.store.bottom = properties.slice(1, properties.length);
            
			DHIS.chart.getChart(project);
        }
    };
    
    DHIS.state = {
        state: null,
        getState: function(conf) {
            var project = {
                state: {
                    conf: null,
                    type: null,
                    series: {
                        dimension: null,
                        data: []
                    },
                    category: {
                        dimension: null,
                        data: []
                    },
                    filter: {
                        dimension: null,
                        data: []
                    },
                    getIndiment: function() {
                        var i = DHIS.conf.finals.dimension.indicator.value;
                        return (this.series.dimension === i || this.category.dimension === i || this.filter.dimension === i) ?
                            DHIS.conf.finals.dimension.indicator : DHIS.conf.finals.dimension.dataelement;
                    },
                    isIndicator: function() {
                        var i = DHIS.conf.finals.dimension.indicator.value;
                        return (this.series.dimension === i || this.category.dimension === i || this.filter.dimension === i);
                    }
                }
            };
            
            var defaultConf = {
                type: 'column',
                stacked: false,
                indicators: [],
                periods: ['monthsThisYear'],
                organisationunits: [],
                series: 'indicator',
                category: 'period',
                filter: 'organisationunit',
                el: '',
                legendPosition: false,
                url: ''
            };
            
            project.state.conf = Ext.applyIf(conf, defaultConf);
            
            project.state.type = project.state.conf.type;
            project.state.series.dimension = project.state.conf.series;
            project.state.category.dimension = project.state.conf.category;
            project.state.filter.dimension = project.state.conf.filter;
            
            DHIS.state.state = project.state;
            
			DHIS.value.getValues(project);
        }
    };
    
    DHIS.value = {
        getValues: function(project) {
            var params = [],
                indicator = DHIS.conf.finals.dimension.indicator.value,
                dataelement = DHIS.conf.finals.dimension.dataelement.value,
                series = project.state.series.dimension,
                category = project.state.category.dimension,
                filter = project.state.filter.dimension,
                indiment = project.state.getIndiment().value,
                url = project.state.isIndicator() ? DHIS.conf.finals.ajax.url_indicator : DHIS.conf.finals.ajax.url_dataelement;
                
            params = params.concat(DHIS.util.dimension[series].getUrl());
            params = params.concat(DHIS.util.dimension[category].getUrl());
            params = params.concat(DHIS.util.dimension[filter].getUrl(true));
                        
            var baseUrl = DHIS.util.string.extendUrl(project.state.conf.url) + url + '.action';
            Ext.Array.each(params, function(item) {
                baseUrl = Ext.String.urlAppend(baseUrl, item);
            });
            
            Ext.Ajax.request({
                url: baseUrl,
                success: function(r) {
                    project.values = Ext.JSON.decode(r.responseText).values;
                    
                    if (!project.values.length) {
                        alert('no data values');
                        return;
                    }
                    
                    Ext.Array.each(project.values, function(item) {
						item.indicator = item.in;
						item.dataelement = item.in;
						item.period = item.pn;
						item.organisationunit = item.on;
						
                        Ext.Array.include(project.state.series.data, item[project.state.series.dimension]);
                        Ext.Array.include(project.state.category.data, item[project.state.category.dimension]);
                        Ext.Array.include(project.state.filter.data, item[project.state.filter.dimension]);
                        item.v = parseFloat(item.v);
                    });
                    
                    DHIS.state.state = project.state;
                    
					DHIS.chart.getData(project);
                }
            });
        }
    };
    
    DHIS.chart = {
        getData: function(project) {
            project.data = [];
			
            Ext.Array.each(project.state.category.data, function(item) {
                var obj = {};
                obj[DHIS.conf.finals.chart.x] = item;
                project.data.push(obj);
            });
            
            Ext.Array.each(project.data, function(item) {
                for (var i = 0; i < project.state.series.data.length; i++) {
                    for (var j = 0; j < project.values.length; j++) {
                        if (project.values[j][project.state.category.dimension] === item[DHIS.conf.finals.chart.x] && project.values[j][project.state.series.dimension] === project.state.series.data[i]) {
                            item[project.values[j][project.state.series.dimension]] = project.values[j].v;
                            break;
                        }
                    }
                }
            });
                
			DHIS.store.getChartStore(project);
        },
        el: null,
        getChart: function(project) {
            this.el = Ext.get(project.state.conf.el);
            this[project.state.type](project);
            DHIS.exe.execute();
        },
        column: function(project) {
            project.chart = Ext.create('Ext.chart.Chart', {
				renderTo: project.state.conf.el,
                width: project.state.conf.width || this.el.getWidth(),
                height: project.state.conf.height || this.el.getHeight(),
                animate: true,
                store: project.store,
                legend: DHIS.util.chart.getLegend(),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: project.store.left,
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        },
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer(project.values))
                        }
                    },
                    {
                        title: DHIS.conf.finals.dimension[project.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: project.store.bottom
                    }
                ],
                series: [
                    {
                        type: 'column',
                        axis: 'left',
                        xField: project.store.bottom,
                        yField: project.store.left,
                        stacked: project.state.conf.stacked,
                        style: {
                            opacity: 0.8
                        }
                    }
                ]
            });
            
            DHIS.projects.push(project);
        },
        bar: function(project) {
            project.chart = Ext.create('Ext.chart.Chart', {
				renderTo: project.state.conf.el,
                width: project.state.conf.width || this.el.getWidth(),
                height: project.state.conf.height || this.el.getHeight(),
                animate: true,
                store: project.store,
                legend: DHIS.util.chart.getLegend(),
                axes: [
                    {
                        title: DHIS.conf.finals.dimension[project.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'left',
                        fields: project.store.left
                    },
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'bottom',
                        minimum: 0,
                        fields: project.store.bottom,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer(project.values))
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
                        xField: project.store.left,
                        yField: project.store.bottom,
                        stacked: project.state.conf.stacked,
                        style: {
                            opacity: 0.8
                        }
                    }
                ]
            });
            
            DHIS.projects.push(project);
        },
        line: function(project) {
            project.chart = Ext.create('Ext.chart.Chart', {
				renderTo: project.state.conf.el,
                width: project.state.conf.width || this.el.getWidth(),
                height: project.state.conf.height || this.el.getHeight(),
                animate: true,
                store: project.store,
                legend: DHIS.util.chart.getLegend(),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: project.store.left,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer(project.values))
                        },
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    },
                    {
                        title: DHIS.conf.finals.dimension[project.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: project.store.bottom
                    }
                ],
                series: DHIS.util.chart.line.getSeriesArray(project)
            });
            
            DHIS.projects.push(project);
        },
        area: function(project) {
            project.chart = Ext.create('Ext.chart.Chart', {
				renderTo: project.state.conf.el,
                width: project.state.conf.width || this.el.getWidth(),
                height: project.state.conf.height || this.el.getHeight(),
                animate: true,
                store: project.store,
                legend: DHIS.util.chart.getLegend(),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: project.store.left,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer(project.values))
                        },
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    },
                    {
                        title: DHIS.conf.finals.dimension[project.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: project.store.bottom
                    }
                ],
                series: [{
                    type: 'area',
                    axis: 'left',
                    xField: project.store.bottom[0],
                    yField: project.store.left,
                    style: {
                        opacity: 0.65
                    }
                }]
            });
            
            DHIS.projects.push(project);
        },
        pie: function(project) {
            project.chart = Ext.create('Ext.chart.Chart', {
				renderTo: project.state.conf.el,
                width: project.state.conf.width || this.el.getWidth(),
                height: project.state.conf.height || this.el.getHeight(),
                animate: true,
                shadow: true,
                store: project.store,
                legend: DHIS.util.chart.getLegend(),
                insetPadding: 60,
                series: [{
                    type: 'pie',
                    field: project.store.left[0],
                    showInLegend: true,
                    tips: {
                        trackMouse: false,
                        width: 160,
                        height: 31,
                        renderer: function(i) {
                            this.setTitle('<span class="DHIS-chart-tips">' + i.data.x + ': <b>' + i.data[project.store.left[0]] + '</b></span>');
                        }
                    },
                    label: {
                        field: project.store.bottom[0]
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
            
            DHIS.projects.push(project);
        }
    };
    
    DHIS.exe = {
        allow: true,
        queue: [],
        addToQueue: function(obj) {
            DHIS.exe.queue.push(obj);
            
            if (DHIS.exe.allow) {
                DHIS.exe.allow = false;
                DHIS.exe.execute();
            }
        },
        execute: function() {
            if (this.queue.length) {
                DHIS.state.getState(this.queue.shift());
            }
		}
    };
    
    DHIS.initialize();
});
