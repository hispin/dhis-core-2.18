DV = {};
DV.conf = {
    init: {
        data: [
            {x: 'Category 1', 'Series 1': 41, 'Series 2': 69, 'Series 3': 63, 'Series 4': 51},
            {x: 'Category 2', 'Series 1': 51, 'Series 2': 42, 'Series 3': 58, 'Series 4': 52},
            {x: 'Category 3', 'Series 1': 44, 'Series 2': 71, 'Series 3': 62, 'Series 4': 54}
        ]
    },
    finals: {
        ajax: {
            url_visualizer: '../',
            url_commons: '../../dhis-web-commons-ajax-json/',
            url_portal: '../../dhis-web-portal/',
            url_indicator: 'getAggregatedIndicatorValues',
            url_dataelement: 'getAggregatedDataValues'
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
    chart: {
        axis: {
            label: {
                rotate: {
                    degrees: 330
                }
            }
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
        center_tbar_height: 31,
        east_tbar_height: 31
    }
};

Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'lib/ext-ux');
Ext.require('Ext.ux.form.MultiSelect');

Ext.onReady( function() {
    Ext.override(Ext.form.FieldSet,{setExpanded:function(a){var b=this,c=b.checkboxCmp,d=b.toggleCmp,e;a=!!a;if(c){c.setValue(a)}if(d){d.setType(a?"up":"down")}if(a){e="expand";b.removeCls(b.baseCls+"-collapsed")}else{e="collapse";b.addCls(b.baseCls+"-collapsed")}b.collapsed=!a;b.doComponentLayout();b.fireEvent(e,b);return b}});
    Ext.QuickTips.init();
    document.body.oncontextmenu = function(){return false;};

    Ext.Ajax.request({
        url: DV.conf.finals.ajax.url_visualizer + 'initialize.action',
        success: function(r) {
            
    DV.init = Ext.JSON.decode(r.responseText);
    DV.init.isInit = true;
    DV.init.initialize = function(vp) {
        DV.util.combobox.filter.category(vp);
        
        DV.store.column = DV.store.defaultChartStore;
        DV.store.column_stacked = DV.store.defaultChartStore;
        DV.store.bar_stacked = DV.store.bar;
        DV.store.line = DV.store.defaultChartStore;
        DV.store.area = DV.store.defaultChartStore;
        DV.store.pie = DV.store.defaultChartStore;
        
        DV.chart.data = DV.conf.init.data;
        
        DV.exe.execute(true, DV.init.isInit);
    };
    
    DV.cmp = {
        charttype: [],
        dimension: {
            period: []
        },
        datatable: null
    };
    
    DV.util = {
        getCmp: function(q) {
            return DV.viewport.query(q)[0];
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
                for (var p in DV.conf.finals.dimension) {
                    if (DV.conf.finals.dimension[p].value !== name) {
                        DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension[p].value + '"]').collapse();
                    }
                }
            },
            toggleIndicator: function() {
                DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension.indicator.value + '"]').toggle();
            },
            toggleDataElement: function() {
                DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension.dataelement.value + '"]').toggle();
            },
            togglePeriod: function() {
                DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension.period.value + '"]').toggle();
            },
            toggleOrganisationUnit: function() {
                DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension.organisationunit.value + '"]').toggle();
            }
        },
        button: {
            getValue: function() {
                for (var i = 0; i < DV.cmp.charttype.length; i++) {
                    if (DV.cmp.charttype[i].pressed) {
                        return DV.cmp.charttype[i].name;
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
                    DV.util.getCmp('multiselect[name="selectedIndicators"]').store.each( function(r) {
                        a.push('indicatorIds=' + r.data.id);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [];
                    DV.util.getCmp('multiselect[name="selectedIndicators"]').store.each( function(r) {
                        a.push(DV.util.chart.getEncodedSeriesName(r.data.shortName));
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
                    DV.util.getCmp('multiselect[name="selectedDataElements"]').store.each( function(r) {
                        a.push('dataElementIds=' + r.data.id);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [];
                    DV.util.getCmp('multiselect[name="selectedDataElements"]').store.each( function(r) {
                        a.push(DV.util.chart.getEncodedSeriesName(r.data.shortName));
                    });
                    if (exception && !a.length) {
                        alert('No data elements selected');
                    }
                    return a;
                }
            },
            period: {
                getUrl: function(isFilter) {
                    var a = [],
                        cmp = DV.cmp.dimension.period;
                    for (var i = 0; i < cmp.length; i++) {
                        if (cmp[i].getValue()) {
                            Ext.Array.each(DV.init.system.periods[cmp[i].paramName], function(item) {
                                a.push('periodIds=' + item.id);
                            });
                        }
                    }
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [],
                        cmp = DV.cmp.dimension.period;
                    Ext.Array.each(cmp, function(item) {
                        if (item.getValue()) {
                            Ext.Array.each(DV.init.system.periods[item.paramName], function(item) {
                                a.push(DV.util.chart.getEncodedSeriesName(item.name));
                            });
                        }
                    });
                    if (exception && !a.length) {
                        alert('No periods selected');
                    }
                    return a;
                },
                getNameById: function(id) {
                    for (var obj in DV.init.system.periods) {
                        var a = DV.init.system.periods[obj];
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
                    var a = [],
                        treepanel = DV.util.getCmp('treepanel'),
                        selection = treepanel.getSelectionModel().getSelection();
                    if (!selection.length) {
                        selection = [treepanel.getRootNode()];
                        treepanel.selectRoot();
                    }
                    Ext.Array.each(selection, function(r) {
                        a.push('organisationUnitIds=' + r.data.id);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [],
                        treepanel = DV.util.getCmp('treepanel'),
                        selection = treepanel.getSelectionModel().getSelection();
                    if (!selection.length) {
                        selection = [treepanel.getRootNode()];
                        treepanel.selectRoot();
                    }
                    Ext.Array.each(selection, function(r) {
                        a.push(DV.util.chart.getEncodedSeriesName(r.data.text));
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
                    for (var i = 0; i < DV.store.chart.left.length; i++) {
                        a.push({
                            type: 'line',
                            axis: 'left',
                            xField: DV.store.chart.bottom,
                            yField: DV.store.chart.left[i]
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
                    var cbs = vp.query('combobox[name="' + DV.conf.finals.chart.series + '"]')[0],
                        cbc = vp.query('combobox[name="' + DV.conf.finals.chart.category + '"]')[0],
                        cbf = vp.query('combobox[name="' + DV.conf.finals.chart.filter + '"]')[0],
                        v = cbs.getValue(),
                        i = DV.conf.finals.dimension.indicator.value,
                        d = DV.conf.finals.dimension.dataelement.value,
                        p = DV.conf.finals.dimension.period.value,
                        o = DV.conf.finals.dimension.organisationunit.value,
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
                    var cbc = vp.query('combobox[name="' + DV.conf.finals.chart.category + '"]')[0],
                        cbf = vp.query('combobox[name="' + DV.conf.finals.chart.filter + '"]')[0],
                        v = cbc.getValue(),
                        i = DV.conf.finals.dimension.indicator.value,
                        d = DV.conf.finals.dimension.dataelement.value,
                        p = DV.conf.finals.dimension.period.value,
                        o = DV.conf.finals.dimension.organisationunit.value,
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
					if (DV.value.values.length) {
						if (Ext.isWindows && Ext.isGecko) {
							return 22 * DV.value.values.length + 57;
						}
						else if (Ext.isWindows && Ext.isIE) {
							return 21 * DV.value.values.length + 58;
						}
						else {
							return 21 * DV.value.values.length + 57;
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
                return this.allValuesAreIntegers(DV.value.values) ? '0' : '0.0';
            }
        }
    };
    
    DV.store = {
        dimension: function() {
            return Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: [
                    {id: DV.conf.finals.dimension.indicator.value, name: DV.conf.finals.dimension.indicator.rawvalue},
                    {id: DV.conf.finals.dimension.dataelement.value, name: DV.conf.finals.dimension.dataelement.rawvalue},
                    {id: DV.conf.finals.dimension.period.value, name: DV.conf.finals.dimension.period.rawvalue},
                    {id: DV.conf.finals.dimension.organisationunit.value, name: DV.conf.finals.dimension.organisationunit.rawvalue}
                ]
            });
        },        
        indicator: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name', 'shortName'],
                proxy: {
                    type: 'ajax',
                    url: DV.conf.finals.ajax.url_commons + 'getIndicatorsMinified.action',
                    reader: {
                        type: 'json',
                        root: 'indicators'
                    }
                },
                storage: {},
                listeners: {
                    load: function(s) {
                        DV.util.store.addToStorage(s);
                        DV.util.multiselect.filterAvailable(DV.util.getCmp('multiselect[name="availableIndicators"]'),
                            DV.util.getCmp('multiselect[name="selectedIndicators"]'));
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 'shortName'],
                data: []
            })
        },
        dataelement: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name', 'shortName'],
                proxy: {
                    type: 'ajax',
                    url: DV.conf.finals.ajax.url_commons + 'getDataElementsMinified.action',
                    reader: {
                        type: 'json',
                        root: 'dataElements'
                    }
                },
                storage: {},
                listeners: {
                    load: function(s) {
                        DV.util.store.addToStorage(s);
                        DV.util.multiselect.filterAvailable(DV.util.getCmp('multiselect[name="availableDataElements"]'),
                            DV.util.getCmp('multiselect[name="selectedDataElements"]'));
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 'shortName'],
                data: []
            })
        },
        datatable: null,
        getDataTableStore: function(exe) {
            this.datatable = Ext.create('Ext.data.Store', {
                fields: [
                    DV.state.getIndiment().value,
                    DV.conf.finals.dimension.period.value,
                    DV.conf.finals.dimension.organisationunit.value,
                    'v'
                ],
                data: DV.value.values
            });
            
            if (exe) {
                DV.datatable.getDataTable(true);
            }
            else {
                return this.datatable;
            }
            
        },
        chart: null,
        getChartStore: function(exe) {
            this[DV.state.type](exe);
        },
        defaultChartStore: function(exe) {
            var keys = [];
            Ext.Array.each(DV.chart.data, function(item) {
                keys = Ext.Array.merge(keys, Ext.Object.getKeys(item));
            });
            this.chart = Ext.create('Ext.data.Store', {
                fields: keys,
                data: DV.chart.data
            });
            this.chart.bottom = [DV.conf.finals.chart.x];
            this.chart.left = keys.slice(0);
            for (var i = 0; i < this.chart.left.length; i++) {
                if (this.chart.left[i] === DV.conf.finals.chart.x) {
                    this.chart.left.splice(i, 1);
                }
            }
            
            if (exe) {
                DV.chart.getChart(true);
            }
            else {
                return this.chart;
            }
        },
        bar: function(exe) {
            var properties = Ext.Object.getKeys(DV.chart.data[0]);
            this.chart = Ext.create('Ext.data.Store', {
                fields: properties,
                data: DV.chart.data
            });
            this.chart.left = properties.slice(0, 1);
            this.chart.bottom = properties.slice(1, properties.length);
            
            if (exe) {
                DV.chart.getChart(true);
            }
            else {
                return DV.store.chart;
            }
        }
    };
    
    DV.state = {
        type: DV.conf.finals.chart.column,
        indiment: [],
        period: [],
        organisationunit: [],
        series: {
            cmp: null,
            dimension: DV.conf.finals.dimension.indicator.value,
            data: []
        },
        category: {
            cmp: null,
            dimension: DV.conf.finals.dimension.period.value,
            data: []
        },
        filter: {
            cmp: null,
            dimension: DV.conf.finals.dimension.organisationunit.value,
            data: []
        },
        getState: function(exe) {
            this.resetState();
            
            this.type = DV.util.button.getValue();
            
            this.series.dimension = this.series.cmp.getValue();
            this.category.dimension = this.category.cmp.getValue();
            this.filter.dimension = this.filter.cmp.getValue();
                        
            var i = this.getIndiment().value,
                p = DV.conf.finals.dimension.period.value,
                o = DV.conf.finals.dimension.organisationunit.value;
                
            this.indiment = DV.util.dimension[i].getNames(true);
            this.period = DV.util.dimension[p].getNames(true);
            this.organisationunit = DV.util.dimension[o].getNames(true);
            
            if (!this.indiment.length || !this.period.length || !this.organisationunit.length) {
                return;
            }
            
            this.indicator = this.indiment;
            this.dataelement = this.indiment;
            
            this.series.data = this[this.series.dimension];
            this.category.data = this[this.category.dimension];
            this.filter.data = this[this.filter.dimension].slice(0,1);
            
            if (exe) {
                DV.value.getValues(true);
            }
        },
        getIndiment: function() {
            var i = DV.conf.finals.dimension.indicator.value;
            return (this.series.dimension === i || this.category.dimension === i || this.filter.dimension === i) ?
                DV.conf.finals.dimension.indicator : DV.conf.finals.dimension.dataelement;
        },
        isIndicator: function() {
            var i = DV.conf.finals.dimension.indicator.value;
            return (this.series.dimension === i || this.category.dimension === i || this.filter.dimension === i);
        },
        resetState: function() {
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
    
    DV.value = {
        values: [],
        getValues: function(exe) {
            var params = [],
                indicator = DV.conf.finals.dimension.indicator.value,
                dataelement = DV.conf.finals.dimension.dataelement.value,
                series = DV.state.series.dimension,
                category = DV.state.category.dimension,
                filter = DV.state.filter.dimension,
                indiment = DV.state.getIndiment().value,
                url = DV.state.isIndicator() ? DV.conf.finals.ajax.url_indicator : DV.conf.finals.ajax.url_dataelement;
                
            params = params.concat(DV.util.dimension[series].getUrl());
            params = params.concat(DV.util.dimension[category].getUrl());
            params = params.concat(DV.util.dimension[filter].getUrl(true));
            
            var baseUrl = DV.conf.finals.ajax.url_visualizer + url + '.action';
            Ext.Array.each(params, function(item) {
                baseUrl = Ext.String.urlAppend(baseUrl, item);
            });
            
            Ext.Ajax.request({
                url: baseUrl,
                success: function(r) {
                    DV.value.values = Ext.JSON.decode(r.responseText).values;
                    
                    if (!DV.value.values.length) {
                        alert('no data values');
                        return;
                    }
                    
                    Ext.Array.each(DV.value.values, function(item) {
                        item[indiment] = DV.store[indiment].available.storage[item.i].name;
                        item[DV.conf.finals.dimension.period.value] = DV.util.dimension.period.getNameById(item.p);
                        item[DV.conf.finals.dimension.organisationunit.value] = DV.util.getCmp('treepanel').store.getNodeById(item.o).data.text;
                        item.v = parseFloat(item.v);
                    });
                    
                    if (exe) {
                        DV.chart.getData(true);
                    }
                    else {
                        return DV.value.values;
                    }
                }
            });
        }
    };
    
    DV.chart = {
        data: [],        
        getData: function(exe) {
            this.data = [];
			
            Ext.Array.each(DV.state.category.data, function(item) {
                var obj = {};
                obj[DV.conf.finals.chart.x] = item;
                DV.chart.data.push(obj);
            });
            
            Ext.Array.each(DV.chart.data, function(item) {
                for (var i = 0; i < DV.state.series.data.length; i++) {
                    for (var j = 0; j < DV.value.values.length; j++) {
                        if (DV.value.values[j][DV.state.category.dimension] === item[DV.conf.finals.chart.x] && DV.value.values[j][DV.state.series.dimension] === DV.state.series.data[i]) {
                            item[DV.value.values[j][DV.state.series.dimension]] = DV.value.values[j].v;
                            break;
                        }
                    }
                }
            });
            
            if (exe) {
                DV.store.getChartStore(true);
            }
            else {
                return this.data;
            }
        },        
        chart: null,
        getChart: function(exe) {
            this[DV.state.type]();
            if (exe) {
                this.reload();
            }
            else {
                return this.chart;
            }
        },
        column: function(stacked) {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DV.util.viewport.getSize().x,
                height: DV.util.viewport.getSize().y,
                animate: true,
                store: DV.store.chart,
                legend: DV.util.chart.getLegend(DV.state.series.data.length),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DV.store.chart.left,
                        grid: {
                            even: DV.util.chart.getGrid()
                        },
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DV.util.number.getChartAxisFormatRenderer())
                        }
                    },
                    {
                        title: DV.init.isInit ? 'Categories' : DV.conf.finals.dimension[DV.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: DV.store.chart.bottom,
                        label: DV.conf.chart.axis.label
                    }
                ],
                series: [
                    {
                        type: 'column',
                        axis: 'left',
                        xField: DV.store.chart.bottom,
                        yField: DV.store.chart.left,
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
                width: DV.util.viewport.getSize().x,
                height: DV.util.viewport.getSize().y,
                animate: true,
                store: DV.store.chart,
                legend: DV.util.chart.getLegend(DV.state.series.data.length),
                axes: [
                    {
                        title: DV.conf.finals.dimension[DV.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'left',
                        fields: DV.store.chart.left
                    },
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'bottom',
                        minimum: 0,
                        fields: DV.store.chart.bottom,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DV.util.number.getChartAxisFormatRenderer())
                        },
                        grid: {
                            even: DV.util.chart.getGrid()
                        }
                    }
                ],
                series: [
                    {
                        type: 'bar',
                        axis: 'bottom',
                        xField: DV.store.chart.left,
                        yField: DV.store.chart.bottom,
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
                width: DV.util.viewport.getSize().x,
                height: DV.util.viewport.getSize().y,
                animate: true,
                store: DV.store.chart,
                legend: DV.util.chart.getLegend(DV.state.series.data.length),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DV.store.chart.left,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DV.util.number.getChartAxisFormatRenderer())
                        },
                        grid: {
                            even: DV.util.chart.getGrid()
                        }
                    },
                    {
                        title: DV.conf.finals.dimension[DV.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: DV.store.chart.bottom,
                        label: DV.conf.chart.axis.label
                    }
                ],
                series: DV.util.chart.line.getSeriesArray()
            });
        },
        area: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DV.util.viewport.getSize().x,
                height: DV.util.viewport.getSize().y,
                animate: true,
                store: DV.store.chart,
                legend: DV.util.chart.getLegend(DV.state.series.data.length),
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DV.store.chart.left,
                        label: {
                            renderer: Ext.util.Format.numberRenderer(DV.util.number.getChartAxisFormatRenderer())
                        },
                        grid: {
                            even: DV.util.chart.getGrid()
                        }
                    },
                    {
                        title: DV.conf.finals.dimension[DV.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: DV.store.chart.bottom,
                        label: DV.conf.chart.axis.label
                    }
                ],
                series: [{
                    type: 'area',
                    axis: 'left',
                    xField: DV.store.chart.bottom[0],
                    yField: DV.store.chart.left,
                    style: {
                        opacity: 0.65
                    }
                }]
            });
        },
        pie: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DV.util.viewport.getSize().x,
                height: DV.util.viewport.getSize().y,
                animate: true,
                shadow: true,
                store: DV.store.chart,
                legend: DV.util.chart.getLegend(DV.state.category.data.length),
                insetPadding: 60,
                series: [{
                    type: 'pie',
                    field: DV.store.chart.left[0],
                    showInLegend: true,
                    tips: {
                        trackMouse: false,
                        width: 160,
                        height: 31,
                        renderer: function(i) {
                            this.setTitle('<span class="dv-chart-tips">' + i.data.x + ': <b>' + i.data[DV.store.chart.left[0]] + '</b></span>');
                        }
                    },
                    label: {
                        field: DV.store.chart.bottom[0]
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
            c.down('label').setText(DV.state.filter.data[0] || 'Example chart');
            
            if (!DV.init.isInit) {
                DV.store.getDataTableStore(true);
            }
            
            DV.init.isInit = false;
        }
    };
    
    DV.datatable = {
        datatable: null,
        getDataTable: function(exe) {
            this.datatable = Ext.create('Ext.grid.Panel', {
                height: DV.util.viewport.getSize().y - DV.conf.layout.east_tbar_height,
                scroll: 'vertical',
                cls: 'dv-datatable',
                columns: [
                    {
                        text: DV.state.getIndiment().rawvalue,
                        dataIndex: DV.state.getIndiment().value,
                        width: 150
                    },
                    {
                        text: DV.conf.finals.dimension.period.rawvalue,
                        dataIndex: DV.conf.finals.dimension.period.value,
                        width: 100,
                        sortable: false
                    },
                    {
                        text: DV.conf.finals.dimension.organisationunit.rawvalue,
                        dataIndex: DV.conf.finals.dimension.organisationunit.value,
                        width: 150
                    },
                    {
                        text: 'Value',
                        dataIndex: 'v',
                        width: 80
                    }
                ],
                store: DV.store.datatable,
                listeners: {
                    afterrender: function() {
                        DV.cmp.datatable = this;
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
            var c = DV.util.getCmp('panel[region="east"]');
            c.removeAll(true);
            c.add(this.datatable);
        }            
    };
    
    DV.exe = {
        execute: function(exe, init) {
            if (init) {
                DV.store.getChartStore(exe);
            }
            else {
                DV.state.getState(exe);
            }
        },
        datatable: function(exe) {
            DV.store.getDataTableStore(exe);
        }
    };
        
    DV.viewport = Ext.create('Ext.container.Viewport', {
        layout: 'border',
        renderTo: Ext.getBody(),
        items: [
            {
                region: 'west',
                preventHeader: true,
                collapsible: true,
                collapseMode: 'mini',
                items: [
                    {
                        xtype: 'toolbar',
                        height: 45,
                        style: 'padding-top:1px; border-style:none',
                        defaults: {
                            height: 40,
                            toggleGroup: 'chartsettings',
                            handler: DV.util.button.toggleHandler,
                            listeners: {
                                afterrender: function(b) {
                                    if (b.xtype === 'button') {
                                        DV.cmp.charttype.push(b);
                                    }
                                }
                            }
                        },
                        items: [
                            {
                                xtype: 'label',
                                text: 'Chart type',
                                style: 'font-size:11px; font-weight:bold; padding:13px 8px 0 10px'
                            },
                            {
								xtype: 'button',
                                icon: 'images/column.png',
                                name: DV.conf.finals.chart.column,
                                tooltip: 'Column chart',
								width: 40,
                                pressed: true
                            },
                            {
								xtype: 'button',
                                icon: 'images/column-stacked.png',
                                name: DV.conf.finals.chart.column_stacked,
                                tooltip: 'Stacked column chart',
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/bar.png',
                                name: DV.conf.finals.chart.bar,
                                tooltip: 'Bar chart',
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/bar-stacked.png',
                                name: DV.conf.finals.chart.bar_stacked,
                                tooltip: 'Stacked bar chart',
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/line.png',
                                name: DV.conf.finals.chart.line,
                                tooltip: 'Line chart',
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/area.png',
                                name: DV.conf.finals.chart.area,
                                tooltip: 'Area chart',
								width: 40
                            },
                            {
								xtype: 'button',
                                icon: 'images/pie.png',
                                name: DV.conf.finals.chart.pie,
                                tooltip: 'Pie chart',
								width: 40
                            }
                        ]
                    },                    
                    {
                        xtype: 'toolbar',
                        id: 'chartsettings_tb',
                        height: 48,
                        items: [
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: 'Series',
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
                                    {
                                        xtype: 'combobox',
                                        name: DV.conf.finals.chart.series,
                                        emptyText: 'Series',
                                        queryMode: 'local',
                                        editable: false,
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_cmp_width / 3) + 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.indicator.value,
                                        listeners: {
                                            afterrender: function(cb) {
                                                DV.state[cb.name].cmp = cb;
                                            },
                                            select: function() {
                                                DV.util.combobox.filter.category(DV.viewport);
                                            }
                                        }
                                    }
                                ]
                            },                            
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: 'Category',
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
                                    {
                                        xtype: 'combobox',
                                        name: DV.conf.finals.chart.category,
                                        emptyText: 'Category',
                                        queryMode: 'local',
                                        editable: false,
                                        lastQuery: '',
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_cmp_width / 3) + 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.period.value,
                                        listeners: {
                                            afterrender: function(cb) {
                                                DV.state[cb.name].cmp = cb;
                                            },
                                            select: function(cb) {
                                                DV.util.combobox.filter.filter(DV.viewport);
                                            }
                                        }
                                    }
                                ]
                            },                            
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: 'Filter',
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
                                    {
                                        xtype: 'combobox',
                                        name: DV.conf.finals.chart.filter,
                                        emptyText: 'Filter',
                                        queryMode: 'local',
                                        editable: false,
                                        lastQuery: '',
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_cmp_width / 3) + 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.organisationunit.value,
                                        listeners: {
                                            afterrender: function(cb) {
                                                DV.state[cb.name].cmp = cb;
                                            },
                                            select: function(cb) {                     
                                                DV.state.filter.dimension = cb.getValue();
                                            }
                                        }
                                    }
                                ]
                            }
                        ]
                    },                    
                    {
                        xtype: 'panel',
                        bodyStyle: 'border-style:none; border-top:2px groove #eee; padding:10px;',
                        items: [
                            {
                                xtype: 'fieldset',
                                id: 'indicator_fs',
                                name: DV.conf.finals.dimension.indicator.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleIndicator();" class="dv-fieldset-title-link">Indicators</a>',
                                collapsible: true,
								width: DV.conf.layout.west_cmp_width + 22,
                                items: [
                                    {
                                        xtype: 'combobox',
                                        style: 'margin-bottom:8px',
                                        width: DV.conf.layout.west_cmp_width,
                                        valueField: 'id',
                                        displayField: 'name',
                                        fieldLabel: 'Select group',
                                        labelStyle: 'padding-left:7px;',
                                        labelWidth: 110,
                                        editable: false,
                                        queryMode: 'remote',
                                        store: Ext.create('Ext.data.Store', {
                                            fields: ['id', 'name', 'index'],
                                            proxy: {
                                                type: 'ajax',
                                                url: DV.conf.finals.ajax.url_commons + 'getIndicatorGroupsMinified.action',
                                                reader: {
                                                    type: 'json',
                                                    root: 'indicatorGroups'
                                                }
                                            },
                                            listeners: {
                                                load: function(s) {
                                                    s.add({id: 0, name: '[ All indicator groups ]', index: -1});
                                                    s.sort('index', 'ASC');
                                                }
                                            }
                                        }),
                                        listeners: {
                                            select: function(cb) {
                                                var store = DV.store.indicator.available;
                                                store.parent = cb.getValue();
                                                
                                                if (DV.util.store.containsParent(store)) {
                                                    DV.util.store.loadFromStorage(store);
                                                }
                                                else {
                                                    store.load({params: {id: cb.getValue()}});
                                                }
                                            }
                                        }
                                    },                                    
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border-style:none',
                                        items: [
                                            {
                                                xtype: 'multiselect',
                                                id: 'availableIndicators',
                                                name: 'availableIndicators',
                                                cls: 'multiselect',
                                                width: DV.conf.layout.west_cmp_width / 2,
                                                displayField: 'shortName',
                                                valueField: 'id',
                                                queryMode: 'remote',
                                                store: DV.store.indicator.available,
                                                tbar: [
                                                    {
                                                        xtype: 'label',
                                                        text: 'Available indicators',
                                                        style: 'padding-left:5px'
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowright.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.select(DV.util.getCmp('multiselect[name="availableIndicators"]'),
                                                                DV.util.getCmp('multiselect[name="selectedIndicators"]'));
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowrightdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.selectAll(DV.util.getCmp('multiselect[name="availableIndicators"]'),
                                                                DV.util.getCmp('multiselect[name="selectedIndicators"]'));
                                                        }
                                                    },
                                                    ' '
                                                ],
                                                listeners: {
                                                    afterrender: function(ms) {
                                                        ms.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.select(ms, DV.util.getCmp('multiselect[name="selectedIndicators"]'));
                                                        });
                                                    }
                                                }
                                            },                                            
                                            {
                                                xtype: 'multiselect',
                                                id: 'selectedIndicators',
                                                name: 'selectedIndicators',
                                                cls: 'multiselect',
                                                width: DV.conf.layout.west_cmp_width / 2,
                                                displayField: 'shortName',
                                                valueField: 'id',
                                                ddReorder: true,
                                                queryMode: 'local',
                                                store: DV.store.indicator.selected,
                                                tbar: [
                                                    ' ',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleftdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselectAll(DV.util.getCmp('multiselect[name="availableIndicators"]'),
                                                                DV.util.getCmp('multiselect[name="selectedIndicators"]'));
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleft.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselect(DV.util.getCmp('multiselect[name="availableIndicators"]'),
                                                                DV.util.getCmp('multiselect[name="selectedIndicators"]'));
                                                        }
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'label',
                                                        text: 'Selected indicators',
                                                        style: 'padding-right:5px'
                                                    }
                                                ],
                                                listeners: {
                                                    afterrender: function(ms) {
                                                        ms.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.unselect(DV.util.getCmp('multiselect[name="availableIndicators"]'), ms);
                                                        });
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    expand: function() {
                                        DV.util.fieldset.collapseOthers(this.name);
                                    }
                                }
                            },                            
                            {
                                xtype: 'fieldset',
                                id: 'dataelement_fs',
                                name: DV.conf.finals.dimension.dataelement.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleDataElement();" class="dv-fieldset-title-link">Data elements</a>',
                                collapsed: true,
                                collapsible: true,
                                items: [
                                    {
                                        xtype: 'combobox',
                                        style: 'margin-bottom:8px',
                                        width: DV.conf.layout.west_cmp_width,
                                        valueField: 'id',
                                        displayField: 'name',
                                        fieldLabel: 'Select group',
                                        labelStyle: 'padding-left:7px;',
                                        labelWidth: 110,
                                        editable: false,
                                        queryMode: 'remote',
                                        store: Ext.create('Ext.data.Store', {
                                            fields: ['id', 'name', 'index'],
                                            proxy: {
                                                type: 'ajax',
                                                url: DV.conf.finals.ajax.url_commons + 'getDataElementGroupsMinified.action',
                                                reader: {
                                                    type: 'json',
                                                    root: 'dataElementGroups'
                                                }
                                            },
                                            listeners: {
                                                load: function(s) {
                                                    s.add({id: 0, name: '[ All data element groups ]', index: -1});
                                                    s.sort('index', 'ASC');
                                                }
                                            }
                                        }),
                                        listeners: {
                                            select: function(cb) {
                                                var store = DV.store.dataelement.available;
                                                store.parent = cb.getValue();
                                                
                                                if (DV.util.store.containsParent(store)) {
                                                    DV.util.store.loadFromStorage(store);
                                                }
                                                else {
                                                    store.load({params: {id: cb.getValue()}});
                                                }
                                            }
                                        }
                                    },                                    
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border-style:none',
                                        items: [
                                            {
                                                xtype: 'multiselect',
                                                id: 'availableDataElements',
                                                name: 'availableDataElements',
                                                cls: 'multiselect',
                                                width: DV.conf.layout.west_cmp_width / 2,
                                                displayField: 'shortName',
                                                valueField: 'id',
                                                queryMode: 'remote',
                                                store: DV.store.dataelement.available,
                                                tbar: [
                                                    {
                                                        xtype: 'label',
                                                        text: 'Available data elements',
                                                        style: 'padding-left:5px'
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowright.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.select(DV.util.getCmp('multiselect[name="availableDataElements"]'),
                                                                DV.util.getCmp('multiselect[name="selectedDataElements"]'));
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowrightdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.selectAll(DV.util.getCmp('multiselect[name="availableDataElements"]'),
                                                                DV.util.getCmp('multiselect[name="selectedDataElements"]'));
                                                        }
                                                    },
                                                    ' '
                                                ],
                                                listeners: {
                                                    afterrender: function(ms) {
                                                        ms.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.select(ms, DV.util.getCmp('multiselect[name="selectedDataElements"]'));
                                                        });
                                                    }
                                                }
                                            },                                            
                                            {
                                                xtype: 'multiselect',
                                                id: 'selectedDataElements',
                                                name: 'selectedDataElements',
                                                cls: 'multiselect',
                                                width: DV.conf.layout.west_cmp_width / 2,
                                                displayField: 'shortName',
                                                valueField: 'id',
                                                ddReorder: true,
                                                queryMode: 'remote',
                                                store: DV.store.dataelement.selected,
                                                tbar: [
                                                    ' ',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleftdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselectAll(DV.util.getCmp('multiselect[name="availableDataElements"]'),
                                                                DV.util.getCmp('multiselect[name="selectedDataElements"]'));
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleft.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselect(DV.util.getCmp('multiselect[name="availableDataElements"]'),
                                                                DV.util.getCmp('multiselect[name="selectedDataElements"]'));
                                                        }
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'label',
                                                        text: 'Selected data elements',
                                                        style: 'padding-right:5px'
                                                    }
                                                ],
                                                listeners: {
                                                    afterrender: function(ms) {
                                                        ms.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.unselect(DV.util.getCmp('multiselect[name="availableDataElements"]'), ms);
                                                        });
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    expand: function() {
                                        DV.util.fieldset.collapseOthers(this.name);
                                    }
                                }
                            },
                            {
                                xtype: 'fieldset',
                                id: 'period_fs',
                                name: DV.conf.finals.dimension.period.value,
                                title: '<a href="javascript:DV.util.fieldset.togglePeriod();" class="dv-fieldset-title-link">Periods</a>',
                                collapsed: true,
                                collapsible: true,
                                cmp: [],
                                items: [
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border-style:none',
                                        items: [
                                            {
                                                xtype: 'panel',
                                                layout: 'anchor',
                                                bodyStyle: 'border-style:none; padding:0 40px 0 0px',
                                                defaults: {
                                                    labelSeparator: '',
                                                    listeners: {
                                                        added: function(chb) {
                                                            if (chb.xtype === 'checkbox') {
                                                                DV.cmp.dimension.period.push(chb);
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        xtype: 'label',
                                                        text: 'Months',
                                                        style: DV.conf.style.label.period_group
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastMonth',
                                                        boxLabel: 'Last month'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'monthsThisYear',
                                                        boxLabel: 'Months this year',
                                                        checked: true
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'monthsLastYear',
                                                        boxLabel: 'Months last year'
                                                    }
                                                ]
                                            },
                                            {
                                                xtype: 'panel',
                                                layout: 'anchor',
                                                bodyStyle: 'border-style:none; padding-right:40px',
                                                defaults: {
                                                    labelSeparator: '',
                                                    listeners: {
                                                        afterrender: function(chb) {
                                                            if (chb.xtype === 'checkbox') {
                                                                DV.cmp.dimension.period.push(chb);
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        xtype: 'label',
                                                        text: 'Quarters',
                                                        style: DV.conf.style.label.period_group
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastQuarter',
                                                        boxLabel: 'Last quarter'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'quartersThisYear',
                                                        boxLabel: 'Quarters this year'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'quartersLastYear',
                                                        boxLabel: 'Quarters last year'
                                                    }
                                                ]
                                            },
                                            {
                                                xtype: 'panel',
                                                layout: 'anchor',
                                                bodyStyle: 'border-style:none',
                                                defaults: {
                                                    labelSeparator: '',
                                                    listeners: {
                                                        afterrender: function(chb) {
                                                            if (chb.xtype === 'checkbox') {
                                                                DV.cmp.dimension.period.push(chb);
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        xtype: 'label',
                                                        text: 'Years',
                                                        style: DV.conf.style.label.period_group
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'thisYear',
                                                        boxLabel: 'This year'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastYear',
                                                        boxLabel: 'Last year'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastFiveYears',
                                                        boxLabel: 'Last 5 years'
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    expand: function() {
                                        DV.util.fieldset.collapseOthers(this.name);
                                    }
                                }
                            },                            
                            {
                                xtype: 'fieldset',
                                id: 'organisationunit_fs',
                                name: DV.conf.finals.dimension.organisationunit.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleOrganisationUnit();" class="dv-fieldset-title-link">Organisation units</a>',
                                collapsed: true,
                                collapsible: true,
                                items: [
                                    {
                                        xtype: 'treepanel',
                                        height: 300,
                                        width: DV.conf.layout.west_cmp_width,
                                        autoScroll: true,
                                        multiSelect: true,
                                        isRendered: false,
                                        selectRoot: function() {
                                            if (this.isRendered) {
                                                if (!this.getSelectionModel().getSelection().length) {
                                                    this.getSelectionModel().select(this.getRootNode());
                                                }
                                            }
                                        },
                                        store: Ext.create('Ext.data.TreeStore', {
                                            proxy: {
                                                type: 'ajax',
                                                url: DV.conf.finals.ajax.url_visualizer + 'getOrganisationUnitChildren.action'
                                            },
                                            root: {
                                                id: DV.init.system.rootNode.id,
                                                text: DV.init.system.rootNode.name,
                                                expanded: false
                                            }
                                        }),
                                        listeners: {
                                            itemcontextmenu: function(v, r, h, i, e) {
                                                if (v.menu) {
                                                    v.menu.destroy();
                                                }
                                                v.menu = Ext.create('Ext.menu.Menu', {
                                                    id: 'treepanel-contextmenu'
                                                });
                                                if (!r.data.leaf) {
                                                    v.menu.add({
                                                        id: 'treepanel-contextmenu-item',
                                                        text: 'Select all children',
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
                                    expand: function(fs) {
                                        DV.util.fieldset.collapseOthers(this.name);
                                        var tp = fs.down('treepanel');
                                        if (!tp.isRendered) {
                                            tp.isRendered = true;
                                            tp.getRootNode().expand();
                                            tp.selectRoot();
                                        }
                                    }
                                }
                            }
                        ]
                    }
                ],
                listeners: {
                    collapse: function(p) {                    
                        p.collapsed = true;
                        DV.util.getCmp('button[name="resizeleft"]').setText('<span style="font-weight:bold">>>></span>');
                    },
                    expand: function(p) {
                        p.collapsed = false;
                        DV.util.getCmp('button[name="resizeleft"]').setText('<span style="font-weight:bold"><<<</span>');
                    }
                }
            },
            {
                id: 'center',
                region: 'center',
                layout: 'fit',
                bodyStyle: 'padding:10px',
                tbar: {
                    xtype: 'toolbar',
                    height: DV.conf.layout.center_tbar_height,
                    items: [
                        {
                            xtype: 'button',
                            id: 'resizeleft_b',
                            name: 'resizeleft',
                            text: '<span style="font-weight:bold; color:#444;"><<<</span>',
                            tooltip: 'Show/hide chart settings',
                            handler: function() {
                                var p = DV.util.getCmp('panel[region="west"]');
                                if (p.collapsed) {
                                    p.expand();
                                }
                                else {
                                    p.collapse();
                                }
                            }
                        },'-',
                        {
                            xtype: 'button',
                            id: 'update_b',
                            text: '<b style="color:#444">Update</b>',
                            cls: 'x-btn-text-icon',
                            icon: 'images/refresh.png',
                            handler: function() {
                                DV.exe.execute(true, DV.init.isInit);
                            }
                        },'-',
                        {
                            xtype: 'button',
                            id: 'datatable_b',
                            text: '<b style="color:#444">Data table</b>',
                            cls: 'x-btn-text-icon',
                            icon: 'images/datatable.png',
                            handler: function(b) {
                                var p = DV.util.getCmp('panel[region="east"]');
                                if (p.collapsed && p.items.length) {
                                    p.expand();
                                    DV.exe.datatable(true);
                                }
                                else {
                                    p.collapse();
                                }
                            }
                        },
                        '-',' ',' ',
                        {
                            xtype: 'label',
                            text: 'Example chart',
                            style: 'font-weight:bold; padding:0 4px;'
                        },
                        '->',
                        {
                            xtype: 'button',
							id: 'exit_b',
                            text: '<b style="color:#444">Exit</b>',
                            cls: 'x-btn-text-icon',
                            icon: 'images/exit.png',
                            handler: function() {
                                window.location.href = DV.conf.finals.ajax.url_portal + 'redirect.action';
                            }
                        }
                    
                    ]
                }
            },
            {
                region: 'east',
                preventHeader: true,
                collapsible: true,
                collapsed: true,
                collapseMode: 'mini',
                width: 498,
                tbar: {
                    height: DV.conf.layout.east_tbar_height,
                    items: [
                        ' ',
                        {
                            xtype: 'label',
                            text: 'Data table',
                            style: 'font-weight:bold; color:#444; padding:0 4px'
                        }
                    ]
                }
            }
        ],
        listeners: {
            afterrender: function(vp) {
                DV.init.initialize(vp);
            },
            resize: function(vp) {
                vp.query('panel[region="west"]')[0].setWidth(DV.conf.layout.west_width);
                
                if (DV.cmp.datatable) {
                    DV.cmp.datatable.setHeight(DV.util.viewport.getSize().y - DV.conf.layout.east_tbar_height);
                }
            }
        }
    });
    
    }});
});
