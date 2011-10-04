DV = {};
DV.conf = {
    finals: {
        ajax: {
            url_visualizer: '../',
            url_commons: '../../dhis-web-commons-ajax-json/',
            url_portal: '../../dhis-web-portal/'
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
            series: 'series',
            category: 'category',
            filter: 'filter',
            column: 'column',
            line: 'line',
            pie: 'pie'
        }
    },
    style: {
        label: {
            period: 'font:bold 11px arial,ubuntu; color:#444; line-height:20px',
        }
    },
    layout: {
        west_cmp_width: 380
    }
};

Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', 'lib/ext-ux');
Ext.require('Ext.ux.form.MultiSelect');

Ext.onReady( function() {
    document.body.oncontextmenu = function(){return false;};
    Ext.QuickTips.init();
    Ext.override(Ext.form.FieldSet,{setExpanded:function(a){var b=this,c=b.checkboxCmp,d=b.toggleCmp,e;a=!!a;if(c){c.setValue(a)}if(d){d.setType(a?"up":"down")}if(a){e="expand";b.removeCls(b.baseCls+"-collapsed")}else{e="collapse";b.addCls(b.baseCls+"-collapsed")}b.collapsed=!a;b.doComponentLayout();b.fireEvent(e,b);return b}});
    
    Ext.Ajax.request({
        url: DV.conf.finals.ajax.url_visualizer + 'initialize.action',
        success: function(r) {
            DV.init = Ext.JSON.decode(r.responseText);
        
    DV.util = {
        getCmp: function(q) {
            return DV.viewport.query(q)[0];
        },
        viewport: {
            getSize: function() {
                var c = DV.util.getCmp('panel[region="center"]');
                return { x: c.getWidth(), y: c.getHeight() };
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
            toggleHandler: function(b) {
                if (!b.pressed) {
                    b.toggle();
                }
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
                getNames: function() {
                    var a = [];
                    DV.util.getCmp('multiselect[name="selectedIndicators"]').store.each( function(r) {
                        a.push(r.data.shortName);
                    });
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
                getNames: function() {
                    var a = [];
                    DV.util.getCmp('multiselect[name="selectedDataElements"]').store.each( function(r) {
                        a.push(r.data.shortName);
                    });
                    return a;
                }
            },
            period: {
                getUrl: function(isFilter) {
                    var a = [],
                        cmp = DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension.period.value + '"]').cmp;
                    for (var i = 0; i < cmp.length; i++) {
                        if (cmp[i].getValue()) {
                            a.push(cmp[i].paramName + '=true');
                        }
                    }
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function() {
                    var a = [],
                        cmp = DV.util.getCmp('fieldset[name="' + DV.conf.finals.dimension.period.value + '"]').cmp;
                    Ext.Array.each(cmp, function(item) {
                        if (item.getValue()) {
                            Ext.Array.each(DV.init.system.periods[item.paramName], function(item) {
                                a.push(item.name);
                            });
                        }
                    });
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
                    }
                    Ext.Array.each(selection, function(r) {
                        a.push('organisationUnitIds=' + r.data.id);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function() {
                    var a = [],
                        treepanel = DV.util.getCmp('treepanel'),
                        selection = treepanel.getSelectionModel().getSelection();
                    if (!selection.length) {
                        selection = [treepanel.getRootNode()];
                    }
                    Ext.Array.each(selection, function(r) {
                        a.push(r.data.text);
                    });
                    return a;
                }
            }
        }                
    };
    
    DV.store = {
        dimension: function() {
            return Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: [
                    { id: DV.conf.finals.dimension.indicator.value, name: DV.conf.finals.dimension.indicator.rawvalue },
                    { id: DV.conf.finals.dimension.dataelement.value, name: DV.conf.finals.dimension.dataelement.rawvalue },
                    { id: DV.conf.finals.dimension.period.value, name: DV.conf.finals.dimension.period.rawvalue },
                    { id: DV.conf.finals.dimension.organisationunit.value, name: DV.conf.finals.dimension.organisationunit.rawvalue }
                ]
            });
        },        
        indicator: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name', 'shortName'],
                proxy: {
                    type: 'ajax',
                    baseUrl: DV.conf.finals.ajax.url_commons + 'getIndicatorsMinified.action',
                    url: DV.conf.finals.ajax.url_commons + 'getIndicatorsMinified.action',
                    reader: {
                        type: 'json',
                        root: 'indicators'
                    }
                },
                storage: {},
                addToStorage: function() {
                    st = this.storage;
                    this.each( function(r) {
                        if (!st[r.data.id]) {
                            st[r.data.id] = {name: r.data.shortName, parent: this.param};
                        }
                    });
                },
                listeners: {
                    load: function(s) {
                        s.addToStorage(s);
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
                    baseUrl: DV.conf.finals.ajax.url_commons + 'getDataElementsMinified.action',
                    url: DV.conf.finals.ajax.url_commons + 'getDataElementsMinified.action',
                    reader: {
                        type: 'json',
                        root: 'dataElements'
                    }
                },
                storage: {},
                addToStorage: function() {
                    st = this.storage;
                    this.each( function(r) {
                        if (!st[r.data.id]) {
                            st[r.data.id] = {name: r.data.shortName, parent: this.param};
                        }
                    });
                },
                listeners: {
                    load: function(s) {
                        s.addToStorage(s);
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
        chart: null,        
        getChartStore: function(exe) {
            var properties = Ext.Object.getKeys(DV.data.data[0]);

            this.chart = Ext.create('Ext.data.Store', {
                fields: properties,
                data: DV.data.data
            });
            
            this.chart.bottom = properties.slice(0, 1);
            this.chart.left = properties.slice(1, properties.length);
            
            if (exe) {
                DV.chart.getChart(true);
            }
            else {
                return DV.store.chart;
            }
        }
    };
    
    DV.state = {        
        indiment: [],        
        period: [],        
        organisationunit: [],        
        series: {
            dimension: DV.conf.finals.dimension.indicator.value,
            data: []
        },        
        category: {
            dimension: DV.conf.finals.dimension.period.value,
            data: []
        },        
        filter: {
            dimension: DV.conf.finals.dimension.organisationunit.value,
            data: []
        },        
        getState: function(exe) {
            this.resetState();
            
            var indicator = DV.conf.finals.dimension.indicator.value,
                indiment = (this.series.dimension === indicator || this.category.dimension === indicator || this.filter.dimension === indicator) ?
                    DV.conf.finals.dimension.indicator.value : DV.conf.finals.dimension.dataelement.value,
                period = DV.conf.finals.dimension.period.value,
                organisationunit = DV.conf.finals.dimension.organisationunit.value;
            
            this.indiment = DV.util.dimension[indiment].getNames();
            this.period = DV.util.dimension[period].getNames();
            this.organisationunit = DV.util.dimension[organisationunit].getNames();
            
            if (!this.indiment.length || !this.period.length || !this.organisationunit.length) {
                alert("form is not complete");
                return;
            }
    
            DV.state.indicator = DV.state.indiment;
            DV.state.dataelement = DV.state.indiment;
            
            this.series.data = this[this.series.dimension];
            this.category.data = this[this.category.dimension];
            this.filter.data = this[this.filter.dimension].slice(0,1);
            
            if (exe) {
                DV.data.getValues(true);
            }
        },        
        resetState: function() {
            this.indiment = null;
            this.period = null;
            this.organisationunit = null;
            this.series.data = null;
            this.category.data = null;
            this.filter.data = null;
        }
    };
    
    DV.data = {
        values: null,        
        getValues: function(exe) {
            var params = [],
                indicator = DV.conf.finals.dimension.indicator.value,
                dataelement = DV.conf.finals.dimension.dataelement.value,
                series = DV.state.series.dimension,
                category = DV.state.category.dimension,
                filter = DV.state.filter.dimension,
                indiment = (series === indicator || category === indicator || filter === indicator) ? indicator : dataelement,
                url = (series === indicator || category === indicator || filter === indicator) ? 'Indicator' : 'Data';
            
            params = params.concat(DV.util.dimension[series].getUrl());
            params = params.concat(DV.util.dimension[category].getUrl());
            params = params.concat(DV.util.dimension[filter].getUrl(true));
            
            var baseUrl = DV.conf.finals.ajax.url_visualizer + 'getAggregated' + url + 'Values.action';
            for (var i = 0; i < params.length; i++) {
                baseUrl = Ext.String.urlAppend(baseUrl, params[i]);
            }
            
            Ext.Ajax.request({
                url: baseUrl,
                success: function(r) {
                    DV.data.values = Ext.JSON.decode(r.responseText).values;
                    Ext.Array.each(DV.data.values, function(item) {
                        item[indiment] = DV.store[indiment].available.storage[item.i].name;
                        item[DV.conf.finals.dimension.period.value] = DV.util.dimension.period.getNameById(item.p);
                        item[DV.conf.finals.dimension.organisationunit.value] = DV.util.getCmp('treepanel').store.getNodeById(item.o).data.text;
                    });
                    
                    if (exe) {
                        DV.data.getData(true);
                    }
                    else {
                        return DV.data.values;
                    }                    
                }
            });
        },        
        data: [],        
        getData: function(exe) {
            this.data = [];
            
            Ext.Array.each(DV.state.category.data, function(item) {
                DV.data.data.push({x: item});
            });
            
            Ext.Array.each(DV.data.data, function(item) {
                for (var i = 0; i < DV.data.values.length; i++) {
                    if (DV.data.values[i][DV.state.category.dimension] === item.x) {
                        item[DV.data.values[i][DV.state.series.dimension]] = DV.data.values[i].v;
                    }
                }
            });

            if (exe) {
                DV.store.getChartStore(true);
            }
            else {
                return DV.data.data;
            }
        }
    };
    
    DV.chart = {
        type: DV.conf.finals.chart.category,        
        chart: null,        
        getChart: function(exe) {
            this.chart = Ext.create('Ext.chart.Chart', {
                width: DV.util.viewport.getSize().x,
                height: DV.util.viewport.getSize().y,
                animate: true,
                store: DV.store.chart,
                legend: {
                    position: 'top'
                },
                axes: [
                    {
                        title: 'Value',
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        grid: true,
                        fields: DV.store.chart.left,
                        label: {
                            renderer: Ext.util.Format.numberRenderer('0,0')
                        }
                    },
                    {
                        title: DV.conf.finals.dimension[DV.state.category.dimension].rawvalue,
                        type: 'Category',
                        position: 'bottom',
                        fields: DV.store.chart.bottom
                    }
                ],
                series: [
                    {
                        type: 'column',
                        axis: 'left',
                        xField: DV.store.chart.bottom,
                        yField: DV.store.chart.left
                    }
                ]
            });
            
            if (exe) {
                DV.chart.reload();
            }
            else {
                return DV.chart.chart;
            }
        },        
        reload: function() {
            var c = DV.util.getCmp('panel[region="center"]');
            c.removeAll(true);
            c.add(this.chart);
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
                resizable: true,
                resizeHandles: 'e',
                items: [
                    {
                        xtype: 'toolbar',
                        layout: 'anchor',
                        height: 44,
                        style: 'padding-top:2px; border-style:none',
                        defaults: {
                            width: 40,
                            height: 40,
                            toggleGroup: 'chartsettings',
                            handler: DV.util.button.toggleHandler
                        },
                        items: [
                            {
                                xtype: 'label',
                                text: 'Chart type',
                                style: 'font-size:11px; font-weight:bold; padding:0 8px 0 10px'
                            },
                            {
                                icon: 'images/column.png',
                                tooltip: 'Column chart',
                                pressed: true
                            },
                            {
                                icon: 'images/column.png',
                                disabled: true
                            },
                            {
                                icon: 'images/column.png',
                                disabled: true
                            },
                            {
                                icon: 'images/column.png',
                                disabled: true
                            },
                            {
                                icon: 'images/column.png',
                                disabled: true
                            }
                        ]
                    },                    
                    {
                        xtype: 'toolbar',
                        id: 'chartsettings_tb',
                        height: 48,
                        style: 'padding:4px 0 0 8px; border-left:0 none; border-right:0 none; border-bottom:0 none',
                        items: [
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                layout: 'anchor',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: 'Series',
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { html: '<div style="height:2px"></div>', bodyStyle: 'border-style:none;background-color:transparent' },
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
                                        filter: function(cb, vp) {
                                            var v = cb.getValue(),
                                                c = vp.query('combobox[name="' + DV.conf.finals.chart.category + '"]')[0],
                                                f = vp.query('combobox[name="' + DV.conf.finals.chart.filter + '"]')[0],
                                                i = DV.conf.finals.dimension.indicator.value,
                                                d = DV.conf.finals.dimension.dataelement.value,
                                                p = DV.conf.finals.dimension.period.value,
                                                o = DV.conf.finals.dimension.organisationunit.value,
                                                index = 0;
                                            
                                            if (v === i || v === d) {
                                                cb.filterArray = [false, false, true, true];
                                            }
                                            else if (v === p) {
                                                cb.filterArray = [true, true, false, true];
                                            }
                                            else if (v === o) {
                                                cb.filterArray = [true, true, true, false];
                                            }
                                            
                                            var fn = function(cmp) {
                                                cmp.store.filterBy( function(r) {
                                                    return cb.filterArray[index++];
                                                });
                                                if (v === cmp.getValue()) {
                                                    cmp.clearValue();
                                                }
                                                else if ((v === i || v === d) && (cmp.getValue() === i || cmp.getValue() === d)) {
                                                    cmp.clearValue();
                                                }
                                            };
                                            
                                            fn(c);                                    
                                            index = 0;
                                            fn(f);
                                        },
                                        listeners: {
                                            select: function(cb) {
                                                cb.filter(cb, DV.viewport);
                                                DV.state.series.dimension = cb.getValue();
                                            }
                                        }
                                    }
                                ]
                            },                            
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                layout: 'anchor',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: 'Category',
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { html: '<div style="height:2px"></div>', bodyStyle: 'border-style:none;background-color:transparent' },
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
                                        filter: function(cb, vp) {
                                            var v = cb.getValue(),
                                                s = vp.query('combobox[name="' + DV.conf.finals.chart.series + '"]')[0],
                                                f = vp.query('combobox[name="' + DV.conf.finals.chart.filter + '"]')[0],
                                                i = DV.conf.finals.dimension.indicator.value,
                                                d = DV.conf.finals.dimension.dataelement.value,
                                                p = DV.conf.finals.dimension.period.value,
                                                o = DV.conf.finals.dimension.organisationunit.value,
                                                index = 0;
                                            
                                            cb.filterArray = Ext.Array.clone(s.filterArray);
                                            
                                            if (cb.getValue() === i || cb.getValue() === d) {
                                                cb.filterArray[0] = false;
                                                cb.filterArray[1] = false;
                                            }
                                            else if (cb.getValue() === p) {
                                                cb.filterArray[2] = false;
                                            }
                                            else if (cb.getValue() === o) {
                                                cb.filterArray[3] = false;
                                            }
                                            
                                            f.store.filterBy( function(r) {
                                                return cb.filterArray[index++];
                                            });
                                            if (v === f.getValue()) {
                                                f.clearValue();
                                            }
                                            else if ((v === i || v === d) && (f.getValue() === i || f.getValue() === d)) {
                                                f.clearValue();
                                            }
                                        },
                                        listeners: {
                                            select: function(cb) {
                                                cb.filter(cb, DV.viewport);
                                                DV.state.category.dimension = cb.getValue();
                                            }
                                        }
                                    }
                                ]
                            },                            
                            {
                                xtype: 'panel',
                                bodyStyle: 'border-style:none; background-color:transparent; padding:0 2px',
                                layout: 'anchor',
                                items: [
                                    {
                                        xtype: 'label',
                                        text: 'Filter',
                                        style: 'font-size:11px; font-weight:bold; padding:0 3px'
                                    },
                                    { html: '<div style="height:2px"></div>', bodyStyle: 'border-style:none;background-color:transparent' },
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
                                name: DV.conf.finals.dimension.indicator.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleIndicator();" class="dv-fieldset-title-link">Indicators</a>',
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
                                                store.param = cb.getValue();
                                                store.load({params: {id: cb.getValue()}});
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
                                                        text: '>',
                                                        handler: function() {
                                                            DV.util.multiselect.select(DV.util.getCmp('multiselect[name="availableIndicators"]'),
                                                                DV.util.getCmp('multiselect[name="selectedIndicators"]'));
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        text: '>>',
                                                        handler: function() {
                                                            DV.util.multiselect.selectAll(DV.util.getCmp('multiselect[name="availableIndicators"]'),
                                                                DV.util.getCmp('multiselect[name="selectedIndicators"]'));
                                                        }
                                                    }
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
                                                    {
                                                        xtype: 'button',
                                                        text: '<<',
                                                        handler: function() {
                                                            DV.util.multiselect.unselectAll(DV.util.getCmp('multiselect[name="availableIndicators"]'),
                                                                DV.util.getCmp('multiselect[name="selectedIndicators"]'));
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        text: '<',
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
                                                store.param = cb.getValue();
                                                store.load({params: {id: cb.getValue()}});
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
                                                        text: '>',
                                                        handler: function() {
                                                            DV.util.multiselect.select(DV.util.getCmp('multiselect[name="availableDataElements"]'),
                                                                DV.util.getCmp('multiselect[name="selectedDataElements"]'));
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        text: '>>',
                                                        handler: function() {
                                                            DV.util.multiselect.selectAll(DV.util.getCmp('multiselect[name="availableDataElements"]'),
                                                                DV.util.getCmp('multiselect[name="selectedDataElements"]'));
                                                        }
                                                    }
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
                                                    {
                                                        xtype: 'button',
                                                        text: '<<',
                                                        handler: function() {
                                                            DV.util.multiselect.unselectAll(DV.util.getCmp('multiselect[name="availableDataElements"]'),
                                                                DV.util.getCmp('multiselect[name="selectedDataElements"]'));
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        text: '<',
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
                                                        afterrender: function(chb) {
                                                            if (chb.xtype === 'checkbox') {
                                                                chb.up('fieldset').cmp.push(chb);
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        xtype: 'label',
                                                        text: 'Months',
                                                        style: DV.conf.style.label.period
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastMonth',
                                                        boxLabel: 'Last month'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'monthsThisYear',
                                                        boxLabel: 'Months this year'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'monthsLastYear',
                                                        boxLabel: 'Months last year',
                                                        checked: true
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
                                                                chb.up('fieldset').cmp.push(chb);
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        xtype: 'label',
                                                        text: 'Quarters',
                                                        style: DV.conf.style.label.period
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
                                                                chb.up('fieldset').cmp.push(chb);
                                                            }
                                                        }
                                                    }
                                                },
                                                items: [
                                                    {
                                                        xtype: 'label',
                                                        text: 'Years',
                                                        style: DV.conf.style.label.period
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
                                        selectRoot: function() {
                                            if (!this.getSelectionModel().getSelection().length) {
                                                this.getSelectionModel().select(this.getRootNode());
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
                                            itemcontextmenu: function(a,b,c,d,e) {
                                                //console.log(e);
                                            }
                                        }
                                    }
                                ],
                                listeners: {
                                    expand: function(fs) {
                                        DV.util.fieldset.collapseOthers(this.name);
                                        fs.down('treepanel').selectRoot();
                                    }
                                }
                            }
                        ]
                    }
                ],
                listeners: {
                    collapse: function(p) {                    
                        p.collapsed = true;
                        DV.util.getCmp('button[name="resize"]').setText('<span style="font-weight:bold">>>></span>');
                    },
                    expand: function(p) {
                        p.collapsed = false;
                        DV.util.getCmp('button[name="resize"]').setText('<span style="font-weight:bold"><<<</span>');
                    }
                }
            },
            {   
                region: 'center',
                layout: 'fit',
                bodyStyle: 'padding:10px',
                tbar: [
                    {
                        xtype: 'button',
                        name: 'resize',
                        text: '<span style="font-weight:bold"><<<</span>',
                        style: 'color:red',
                        tooltip: 'Show/hide panel',
                        handler: function() {
                            var p = DV.util.getCmp('panel[region="west"]');
                            if (p.collapsed) {
                                p.expand();
                            }
                            else {
                                p.collapse();
                            }
                        }
                    },
                    ' ',
                    {
                        xtype: 'button',
                        text: 'Update',
                        cls: 'x-btn-text-icon',
                        icon: 'images/refresh.png',
                        handler: function() {
                            DV.state.getState(true);
                        }
                    },
                    {
                        xtype: 'label',
                        text: '',
                        style: 'font-weight:bold; padding:0 4px'
                    },
                    '->',
                    {
                        xtype: 'button',
                        text: 'Exit..',
                        handler: function() {
                            window.location.href = DV.conf.finals.ajax.url_portal + 'redirect.action';
                        }
                    }
                ]
            }
        ],
        listeners: {
            afterrender: function(vp) {
                var s = this.query('combobox[name="' + DV.conf.finals.chart.series + '"]')[0];
                s.filter(s, vp);
                var c = this.query('combobox[name="' + DV.conf.finals.chart.category + '"]')[0];
                c.filter(c, vp);
            },
            resize: function() {
                this.query('panel[region="west"]')[0].setWidth(424);
            }
        }
    });
    
    }});
});
