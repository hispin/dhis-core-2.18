DHIS = {};
DHIS.conf = {
    finals: {
        ajax: {
            data_get: 'dhis-web-visualizer/app/getAggregatedValuesPlugin.action',
            favorite_get: 'api/charts/'
        },        
        dimension: {
            data: {
                value: 'data',
                rawvalue: 'Data'
            },
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
            stackedcolumn: 'stackedcolumn',
            bar: 'bar',
            stackedbar: 'stackedbar',
            line: 'line',
            area: 'area',
            pie: 'pie'
        }
    }
};

Ext.onReady( function() {
	
    DHIS.initialize = function() {
        DHIS.store.column = DHIS.store.defaultChartStore;
        DHIS.store.stackedcolumn = DHIS.store.defaultChartStore;
        DHIS.store.stackedbar = DHIS.store.bar;
        DHIS.store.line = DHIS.store.defaultChartStore;
        DHIS.store.area = DHIS.store.defaultChartStore;
        DHIS.store.pie = DHIS.store.defaultChartStore;
        
        DHIS.getChart = DHIS.exe.addToQueue;
    };
    
    DHIS.projects = [];
    
    DHIS.util = {
        dimension: {
            indicator: {
                getIdsFromObjects: function(indicators) {
                    var a = []
                    for (var i = 0; i < indicators.length; i++) {
                        a.push(indicators[i].internalId);
                    }
                    return a;
                }
            },
            dataelement: {
                getIdsFromObjects: function(dataelements) {
                    var a = []
                    for (var i = 0; i < dataelements.length; i++) {
                        a.push(dataelements[i].internalId);
                    }
                    return a;
                }
            },
            data: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.state.conf.indicators, function(r) {
                        a.push('indicatorIds=' + r);
                    });
                    Ext.Array.each(DHIS.state.state.conf.dataelements, function(r) {
                        a.push('dataElementIds=' + r);
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
                },
                getRelativesFromObject: function(obj) {
                    var a = [];
                    for (var k in obj) {
                        if (obj[k]) {
                            a.push(k);
                        }
                    }
                    return a;
                }
            },
            organisationunit: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DHIS.state.state.conf.organisationunits, function(r) {
						a.push('organisationUnitIds=' + r)
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getIdsFromObjects: function(organisationunits) {
                    var a = []
                    for (var i = 0; i < organisationunits.length; i++) {
                        a.push(organisationunits[i].internalId);
                    }
                    return a;
                }
            }
        },
        chart: {
            getLegend: function(len) {
                len = len ? len : 1;
                return {
                    position: len > 5 ? 'right' : 'top',
                    labelFont: '11px arial',
                    boxStroke: '#ffffff',
                    boxStrokeWidth: 0,
                    padding: 0
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
            getTitle: function() {
                return {
                    type: 'text',
                    text: DHIS.state.state.filter.names[0],
                    font: 'bold 13px arial',
                    fill: '#222',
                    width: 300,
                    height: 20,
                    x: 28,
                    y: 16
                };
            },
            getTips: function() {
                return {
                    trackMouse: true,
                    height: 31,
                    renderer: function(item) {
                    }
                };
            },
            setMask: function(str) {
                if (DHIS.mask) {
                    DHIS.mask.hide();
                }
                DHIS.mask = new Ext.LoadMask(DHIS.chart.chart, {msg: str});
                DHIS.mask.show();
            },
            label: {
                getCategoryLabel: function() {
                    return {
                        font: '11px arial',
                        rotate: {
                            degrees: 320
                        }
                    };
                },
                getNumericLabel: function(values) {
                    return {
                        font: '11px arial',
                        renderer: Ext.util.Format.numberRenderer(DHIS.util.number.getChartAxisFormatRenderer(values))
                    };
                }
            },
            bar: {
                getCategoryLabel: function() {
                    return {
                        font: '11px arial'
                    };
                }
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
            },
            pie: {
                getTitle: function(title, subtitle) {
                    return [
                        {
                            type: 'text',
                            text: title,
                            font: 'bold 13px arial',
                            fill: '#222',
                            width: 300,
                            height: 20,
                            x: 28,
                            y: 16
                        },
                        {
                            type: 'text',
                            text: subtitle,
                            font: 'bold 11px arial',
                            fill: '#777',
                            width: 300,
                            height: 20,
                            x: 28,
                            y: 36
                        }
                    ];                        
                },
                getTips: function() {
                    return {
                        trackMouse: true,
                        height: 47,
                        renderer: function(item) {
                            this.setWidth((item.data.x.length * 8) + 15);
                            this.setTitle('<span class="dv-chart-tips">' + item.data.x + '<br/><b>' + item.data[DV.store.chart.left[0]] + '</b></span>');
                        }
                    };
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
            getEncodedString: function(text) {
                return text.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'');
            },
            extendUrl: function(url) {
                if (url.charAt(url.length-1) !== '/') {
                    url += '/';
                }
                return url;
            }
        },
        value: {
            jsonfy: function(r) {
                r = Ext.JSON.decode(r.responseText);
                var object = {
                    values: [],
                    periods: r.p
                };
                for (var i = 0; i < r.v.length; i++) {
                    var obj = {};
                    obj.v = r.v[i][0];
                    obj[DHIS.conf.finals.dimension.data.value] = r.v[i][1];
                    obj[DHIS.conf.finals.dimension.period.value] = r.v[i][2];
                    obj[DHIS.conf.finals.dimension.organisationunit.value] = r.v[i][3];
                    object.values.push(obj);
                }
                return object;
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
                        names: []
                    },
                    category: {
                        dimension: null,
                        names: []
                    },
                    filter: {
                        dimension: null,
                        names: []
                    }
                }
            };
            
            var defaultConf = {
                type: 'column',
                stacked: false,
                indicators: [],
                periods: ['last12Months'],
                organisationunits: [],
                series: 'data',
                category: 'period',
                filter: 'organisationunit',
                el: '',
                legendPosition: false,
                url: ''
            };
            
            project.state.conf = Ext.applyIf(conf, defaultConf);
            project.state.conf.type = project.state.conf.type.toLowerCase();
            project.state.conf.series = project.state.conf.series.toLowerCase();
            project.state.conf.category = project.state.conf.category.toLowerCase();
            project.state.conf.filter = project.state.conf.filter.toLowerCase();
            
            project.state.type = project.state.conf.type;
            project.state.series.dimension = project.state.conf.series;
            project.state.category.dimension = project.state.conf.category;
            project.state.filter.dimension = project.state.conf.filter;
            
            DHIS.state.state = project.state;
            
			DHIS.value.getValues(project);
        },
        setState: function(conf) {
            if (conf.uid) {
                Ext.Ajax.request({
                    url: conf.url + DHIS.conf.finals.ajax.favorite_get + conf.uid + '.json',
                    scope: this,
                    success: function(r) {
                        if (!r.responseText) {
                            alert('Invalid uid');
                            return;
                        }
                        var f = Ext.JSON.decode(r.responseText);
                        conf.type = f.type.toLowerCase();
                        conf.periods = DHIS.util.dimension.period.getRelativesFromObject(f.relativePeriods);
                        conf.organisationunits = DHIS.util.dimension.organisationunit.getIdsFromObjects(f.organisationUnits);
                        conf.series = f.series.toLowerCase();
                        conf.category = f.category.toLowerCase();
                        conf.filter = f.filter.toLowerCase();
                        conf.legendPosition = conf.legendPosition || false;
                        
                        if (f.indicators) {
                            conf.indicators = DHIS.util.dimension.indicator.getIdsFromObjects(f.indicators);
                        }
                        if (f.dataelements) {
                            conf.dataelements = DHIS.util.dimension.dataelement.getIdsFromObjects(f.dataelements);
                        }
                        
                        this.getState(conf);                        
                    }
                });
            }
        }
    };
    
    DHIS.value = {
        getValues: function(project) {
            var params = [];                
            params = params.concat(DHIS.util.dimension[project.state.series.dimension].getUrl());
            params = params.concat(DHIS.util.dimension[project.state.category.dimension].getUrl());
            params = params.concat(DHIS.util.dimension[project.state.filter.dimension].getUrl(true));
                        
            var baseUrl = DHIS.util.string.extendUrl(project.state.conf.url) + DHIS.conf.finals.ajax.data_get;
            Ext.Array.each(params, function(item) {
                baseUrl = Ext.String.urlAppend(baseUrl, item);
            });
            
            Ext.Ajax.request({
                url: baseUrl,
                success: function(r) {
                    var json = DHIS.util.value.jsonfy(r);
                    project.values = json.values;
                    
                    if (!project.values.length) {
                        alert('No data values');
                        return;
                    }
                    
                    for (var i = 0; i < project.values.length; i++) {
                        project.values[i][DHIS.conf.finals.dimension.data.value] = DHIS.util.string.getEncodedString(project.values[i][DHIS.conf.finals.dimension.data.value]);
                        project.values[i][DHIS.conf.finals.dimension.period.value] = DHIS.util.string.getEncodedString(project.values[i][DHIS.conf.finals.dimension.period.value]);
                        project.values[i][DHIS.conf.finals.dimension.organisationunit.value] = DHIS.util.string.getEncodedString(project.values[i][DHIS.conf.finals.dimension.organisationunit.value]);
                    }                        
                    
                    Ext.Array.each(project.values, function(item) {
                        Ext.Array.include(project.state.series.names, DHIS.util.string.getEncodedString(item[project.state.series.dimension]));
                        Ext.Array.include(project.state.category.names, DHIS.util.string.getEncodedString(item[project.state.category.dimension]));
                        Ext.Array.include(project.state.filter.names, DHIS.util.string.getEncodedString(item[project.state.filter.dimension]));
                        item.v = parseFloat(item.v);
                    });
                    
                    for (var k in project.state.conf) {
                        if (project.state.conf[k] == 'period') {
                            project.state[k].names = json.periods;
                        }
                    }
                    
                    DHIS.state.state = project.state;
                    
					DHIS.chart.getData(project);
                }
            });
        }
    };
    
    DHIS.chart = {
        getData: function(project) {
            project.data = [];
			
            Ext.Array.each(project.state.category.names, function(item) {
                var obj = {};
                obj[DHIS.conf.finals.chart.x] = item;
                project.data.push(obj);
            });
            
            Ext.Array.each(project.data, function(item) {
                for (var i = 0; i < project.state.series.names.length; i++) {
                    item[project.state.series.names[i]] = 0;
                }
            });
            
            Ext.Array.each(project.data, function(item) {
                for (var i = 0; i < project.state.series.names.length; i++) {
                    for (var j = 0; j < project.values.length; j++) {
                        if (project.values[j][project.state.category.dimension] === item[DHIS.conf.finals.chart.x] && project.values[j][project.state.series.dimension] === project.state.series.names[i]) {
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
        column: function(project, isStacked) {
            project.chart = Ext.create('Ext.chart.Chart', {
				renderTo: project.state.conf.el,
                width: project.state.conf.width || this.el.getWidth(),
                height: project.state.conf.height || this.el.getHeight(),
                animate: true,
                store: project.store,
                items: DHIS.util.chart.getTitle(),
                legend: DHIS.util.chart.getLegend(project.store.left.length),
                axes: [
                    {
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: project.store.left,
                        label: DHIS.util.chart.label.getNumericLabel(project.values),
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    },
                    {
                        type: 'Category',
                        position: 'bottom',
                        fields: project.store.bottom,
                        label: DHIS.util.chart.label.getCategoryLabel()
                    }
                ],
                series: [
                    {
                        type: 'column',
                        axis: 'left',
                        xField: project.store.bottom,
                        yField: project.store.left,
                        stacked: isStacked,
                        style: {
                            opacity: 0.8
                        }
                    }
                ]
            });
            
            DHIS.projects.push(project);
        },
        stackedcolumn: function(project) {
            this.column(project, true);
        },
        bar: function(project, isStacked) {
            project.chart = Ext.create('Ext.chart.Chart', {
				renderTo: project.state.conf.el,
                width: project.state.conf.width || this.el.getWidth(),
                height: project.state.conf.height || this.el.getHeight(),
                animate: true,
                store: project.store,
                items: DHIS.util.chart.getTitle(),
                legend: DHIS.util.chart.getLegend(project.store.chart.bottom.length),
                axes: [
                    {
                        type: 'Category',
                        position: 'left',
                        fields: project.store.left,
                        label: DHIS.util.chart.bar.getCategoryLabel()
                    },
                    {
                        type: 'Numeric',
                        position: 'bottom',
                        minimum: 0,
                        fields: project.store.bottom,
                        label: DHIS.util.chart.label.getNumericLabel(project.values),
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
                        stacked: isStacked,
                        style: {
                            opacity: 0.8
                        }
                    }
                ]
            });
            
            DHIS.projects.push(project);
        },
        stackedbar: function(project) {
            this.bar(project, true);
        },
        line: function(project) {
            project.chart = Ext.create('Ext.chart.Chart', {
				renderTo: project.state.conf.el,
                width: project.state.conf.width || this.el.getWidth(),
                height: project.state.conf.height || this.el.getHeight(),
                animate: true,
                store: project.store,
                items: DHIS.util.chart.getTitle(),
                legend: DHIS.util.chart.getLegend(project.store.chart.left.length),
                axes: [
                    {
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: project.store.left,
                        label: DHIS.util.chart.label.getNumericLabel(project.values),
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    },
                    {
                        type: 'Category',
                        position: 'bottom',
                        fields: project.store.bottom,
                        label: DV.util.chart.label.getCategoryLabel()
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
                items: DHIS.util.chart.getTitle(),
                legend: DHIS.util.chart.getLegend(project.store.chart.left.length),
                axes: [
                    {
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: project.store.left,
                        label: DHIS.util.chart.label.getNumericLabel(project.values),
                        grid: {
                            even: DHIS.util.chart.getGrid()
                        }
                    },
                    {
                        type: 'Category',
                        position: 'bottom',
                        fields: project.store.bottom,
                        label: DHIS.util.chart.label.getCategoryLabel()
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
                insetPadding: 60,
                items: DHIS.util.chart.pie.getTitle(project.state.state.filter.names[0], project.store.left[0]),
                legend: DHIS.util.chart.getLegend(project.state.state.category.names.length),
                series: [{
                    type: 'pie',
                    field: project.store.left[0],
                    showInLegend: true,
                    tips: DHIS.util.chart.pie.getTips(),
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
        addToQueue: function(conf) {
            DHIS.exe.queue.push(conf);            
            if (DHIS.exe.allow) {
                DHIS.exe.allow = false;
                DHIS.exe.execute();
            }
        },
        execute: function() {
            if (this.queue.length) {
                var conf = this.queue.shift();
                if (conf.uid) {
                    DHIS.state.setState(conf);
                }
                else {
                    DHIS.state.getState(conf);
                }
            }
		}
    };
    
    DHIS.initialize();
});
