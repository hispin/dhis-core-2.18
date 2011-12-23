DV = {};
DV.conf = {
    init: {
        data: [
            {x: 'Category 1', 'Series 1': 41, 'Series 2': 69, 'Series 3': 63, 'Series 4': 51},
            {x: 'Category 2', 'Series 1': 51, 'Series 2': 42, 'Series 3': 58, 'Series 4': 52},
            {x: 'Category 3', 'Series 1': 44, 'Series 2': 71, 'Series 3': 62, 'Series 4': 54}
        ],
        jsonfy: function(r) {
            r = Ext.JSON.decode(r.responseText);
            var obj = {system: {rootNode: {id: r.rn[0], name: r.rn[1], level: 1}, periods: {}, user: {id: r.user.id, isAdmin: r.user.isAdmin}}};
            for (var relative in r.p) {
                obj.system.periods[relative] = [];
                for (var i = 0; i < r.p[relative].length; i++) {
                    obj.system.periods[relative].push({id: r.p[relative][i][0], name: r.p[relative][i][1]});
                }
            }
            return obj;
        }
    },
    finals: {
        ajax: {
            path_visualizer: '../',
            path_commons: '../../dhis-web-commons-ajax-json/',
            path_api: '../../api/',
            path_portal: '../../dhis-web-portal/',
            initialize: 'initialize.action',
            redirect: 'redirect.action',
            data_get: 'getAggregatedValues.action',
            indicator_get: 'getIndicatorsMinified.action',
            indicatorgroup_get: 'getIndicatorGroupsMinified.action',
            dataelement_get: 'getDataElementsMinified.action',
            dataelementgroup_get: 'getDataElementGroupsMinified.action',
            dataelement_get: 'getDataElementsMinified.action',
            organisationunitchildren_get: 'getOrganisationUnitChildren.action',
            favorite_addorupdate: 'addOrUpdateChart.action',
            favorite_addorupdatesystem: 'addOrUpdateSystemChart.action',            
            favorite_get: 'charts/',
            favorite_getall: 'getAllCharts.action',
            favorite_delete: 'deleteCharts.action'
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
        },
        image: {
            png: 'png',
            pdf: 'pdf'
        },
        cmd: {
            init: 'init'
        }
    },
    chart: {
        inset: 30
    },
    layout: {
        west_width: 424,
        west_fieldset_width: 402,
        center_tbar_height: 31,
        east_tbar_height: 31,
        east_gridcolumn_height: 30,
        form_label_width: 55,
        window_favorite_ypos: 100,
        window_confirm_width: 250,
        grid_favorite_width: 420
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
        url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.initialize,
        success: function(r) {
            
    DV.init = DV.conf.init.jsonfy(r);    
    DV.init.initialize = function() {
        DV.util.combobox.filter.category();
        DV.store.column = DV.store.defaultChartStore;
        DV.store.stackedcolumn = DV.store.defaultChartStore;
        DV.store.stackedbar = DV.store.bar;
        DV.store.line = DV.store.defaultChartStore;
        DV.store.area = DV.store.defaultChartStore;
        DV.store.pie = DV.store.defaultChartStore;
        DV.chart.data = DV.conf.init.data;
        
        DV.init.cmd = DV.util.getUrlParam('uid') || DV.conf.finals.cmd.init;
        DV.exe.execute(true, DV.init.cmd);
    };
    
    DV.cmp = {
        region: {},
        charttype: [],
        settings: {},
        fieldset: {},
        dimension: {
            indicator: {},
            dataelement: {},
            period: [],
            organisationunit: {}
        },
        toolbar: {
            menuitem: {}
        },
        favorite: {
            rename: {}
        }
    };
    
    DV.util = {
        getCmp: function(q) {
            return DV.viewport.query(q)[0];
        },
        getUrlParam: function(strParam) {            
            var output = '';
            var strHref = window.location.href;
            if (strHref.indexOf('?') > -1 ) {
                var strQueryString = strHref.substr(strHref.indexOf('?'));
                var aQueryString = strQueryString.split('&');
                for (var iParam = 0; iParam < aQueryString.length; iParam++) {
                    if (aQueryString[iParam].indexOf(strParam + '=') > -1) {
                        var aParam = aQueryString[iParam].split('=');
                        output = aParam[1];
                        break;
                    }
                }
            }
            return unescape(output);
        },
        viewport: {
            getSize: function() {
                return {x: DV.cmp.region.center.getWidth(), y: DV.cmp.region.center.getHeight()};
            },
            getXY: function() {
                return {x: DV.cmp.region.center.x + 15, y: DV.cmp.region.center.y + 43};
            },
            getPageCenterX: function(cmp) {
                return ((screen.width/2)-(cmp.width/2));
            },
            getPageCenterY: function(cmp) {
                return ((screen.height/2)-((cmp.height/2)-100));
            }
        },
        multiselect: {
            select: function(a, s) {
                var selected = a.getValue();
                if (selected.length) {
                    var array = [];
                    Ext.Array.each(selected, function(item) {
                        array.push({id: item, s: a.store.getAt(a.store.findExact('id', item)).data.s});
                    });
                    s.store.add(array);
                }
                this.filterAvailable(a, s);
            },
            selectAll: function(a, s) {
                var array = [];
                a.store.each( function(r) {
                    array.push({id: r.data.id, s: r.data.s});
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
                a.store.sort('s', 'ASC');
            }
        },
        fieldset: {
            toggleIndicator: function() {
                DV.cmp.fieldset.indicator.toggle();
            },
            toggleDataElement: function() {
                DV.cmp.fieldset.dataelement.toggle();
            },
            togglePeriod: function() {
                DV.cmp.fieldset.period.toggle();
            },
            toggleOrganisationUnit: function() {
                DV.cmp.fieldset.organisationunit.toggle();
            },
            toggleOptions: function() {
                DV.cmp.fieldset.options.toggle();
            },
            collapseFieldsets: function(fieldsets) {
                for (var i = 0; i < fieldsets.length; i++) {
                    fieldsets[i].collapse();
                }
            }
        },
        button: {
            type: {
                getValue: function() {
                    for (var i = 0; i < DV.cmp.charttype.length; i++) {
                        if (DV.cmp.charttype[i].pressed) {
                            return DV.cmp.charttype[i].name;
                        }
                    }
                },
                setValue: function(type) {
                    for (var i = 0; i < DV.cmp.charttype.length; i++) {
                        DV.cmp.charttype[i].toggle(DV.cmp.charttype[i].name === type);
                    }
                },
                toggleHandler: function(b) {
                    if (!b.pressed) {
                        b.toggle();
                    }
                }
            }
        },
        store: {
            addToStorage: function(s, records) {
                s.each( function(r) {
                    if (!s.storage[r.data.id]) {
                        s.storage[r.data.id] = {id: r.data.id, s: r.data.s, name: r.data.s, parent: s.parent};
                    }
                });
                if (records) {
                    Ext.Array.each(records, function(r) {
                        if (!s.storage[r.data.id]) {
                            s.storage[r.data.id] = {id: r.data.id, s: r.data.s, name: r.data.s, parent: s.parent};
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
                s.sort('s', 'ASC');
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
                getIds: function(exception) {
                    var a = [];
                    DV.cmp.dimension.indicator.selected.store.each( function(r) {
                        a.push(r.data.id);
                    });
                    if (exception && !a.length) {
                        alert('No indicators selected');
                    }
                    return a;
                }
            },
            dataelement: {
                getIds: function(exception) {
                    if (DV.cmp.dimension.dataelement.selected.store) {
                        var a = [];
                        DV.cmp.dimension.dataelement.selected.store.each( function(r) {
                            a.push(r.data.id);
                        });
                        if (exception && !a.length) {
                            alert('No data elements selected');
                        }
                        return a;
                    }
                    else {
                        alert('Data element store does not exist');
                    }
                }
            },
            data: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DV.state.indicatorIds, function(r) {
                        a.push('indicatorIds=' + r);
                    });
                    Ext.Array.each(DV.state.dataelementIds, function(r) {
                        a.push('dataElementIds=' + r);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [];
                    DV.cmp.dimension.indicator.selected.store.each( function(r) {
                        a.push(DV.util.string.getEncodedString(r.data.s));
                    });
                    if (DV.cmp.dimension.dataelement.selected.store) {
                        DV.cmp.dimension.dataelement.selected.store.each( function(r) {
                            a.push(DV.util.string.getEncodedString(r.data.s));
                        });
                    }
                    if (exception && !a.length) {
                        alert('No indicators or data elements selected');
                    }
                    return a;
                }                    
            },
            period: {
                getUrl: function(isFilter) {
                    var a = [];
                    for (var r in DV.state.relativePeriods) {
                        if (DV.state.relativePeriods[r]) {
                            Ext.Array.each(DV.init.system.periods[r], function(item) {
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
                                a.push(DV.util.string.getEncodedString(item.name));
                            });
                        }
                    });
                    if (exception && !a.length) {
                        alert('No periods selected');
                    }
                    return a;
                },
                getNamesByRelativePeriodsObject: function(rp) {
                    var relatives = [],
                        names = [];
                    for (var r in rp) {
                        if (rp[r]) {
                            relatives.push(r);
                        }
                    }
                    for (var i = 0; i < relatives.length; i++) {
                        var r = DV.init.system.periods[relatives[i]] || [];
                        for (var j = 0; j < r.length; j++) {
                            names.push(r[j].name);
                        }
                    }
                    return names;
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
                },
                getIds: function(exception) {
                    var a = [],
                        cmp = DV.cmp.dimension.period;
                    Ext.Array.each(cmp, function(item) {
                        if (item.getValue()) {
                            a.push(item.paramName);
                        }
                    });
                    if (exception && !a.length) {
                        alert('No periods selected');
                    }
                    return a;
                },
                getRelativePeriodObject: function(exception) {
                    var a = {},
                        cmp = DV.cmp.dimension.period,
                        valid = false;
                    Ext.Array.each(cmp, function(item) {
                        a[item.paramName] = item.getValue();
                        valid = item.getValue() ? true : valid;
                    });
                    if (exception && !valid) {
                        alert('No periods selected');
                    }
                    return a;
                }   
            },
            organisationunit: {
                getUrl: function(isFilter) {
                    var a = [];
                    Ext.Array.each(DV.state.organisationunitIds, function(item) {
                        a.push('organisationUnitIds=' + item);
                    });
                    return (isFilter && a.length > 1) ? a.slice(0,1) : a;
                },
                getNames: function(exception) {
                    var a = [],
                        tp = DV.cmp.dimension.organisationunit.treepanel,
                        selection = tp.getSelectionModel().getSelection();
                    if (!selection.length) {
                        selection = [tp.getRootNode()];
                        tp.selectRoot();
                    }
                    Ext.Array.each(selection, function(r) {
                        a.push(DV.util.string.getEncodedString(r.data.text));
                    });
                    if (exception && !a.length) {
                        alert('No organisation units selected');
                    }
                    return a;
                },
                getIds: function(exception) {
                    var a = [],
                        tp = DV.cmp.dimension.organisationunit.treepanel,
                        selection = tp.getSelectionModel().getSelection();
                    if (!selection.length) {
                        selection = [tp.getRootNode()];
                        tp.selectRoot();
                    }
                    Ext.Array.each(selection, function(r) {
                        a.push(DV.util.string.getEncodedString(r.data.id));
                    });
                    if (exception && !a.length) {
                        alert('No organisation units selected');
                    }
                    return a;
                }                    
            }
        },
        mask: {
            setMask: function(cmp, str) {
                if (DV.mask) {
                    DV.mask.hide();
                }
                DV.mask = new Ext.LoadMask(cmp, {msg: str});
                DV.mask.show();
            }
        },
        chart: {
            getLegend: function(len) {
                len = len ? len : DV.store.chart.left.length;
                return {
                    position: len > 5 ? 'right' : 'top',
                    labelFont: '15px arial',
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
                    text: DV.init.cmd === DV.conf.finals.cmd.init ? 'Example chart' : DV.state.filter.names[0],
                    font: 'bold 15px arial',
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
            label: {
                getCategoryLabel: function() {
                    return {
                        font: '14px arial',
                        rotate: {
                            degrees: 330
                        }
                    };
                },
                getNumericLabel: function() {
                    return {
                        font: '13px arial',
                        renderer: Ext.util.Format.numberRenderer(DV.util.number.getChartAxisFormatRenderer())
                    };
                }
            },
            bar: {
                getCategoryLabel: function() {
                    return {
                        font: '14px arial'
                    };
                }
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
            },
            pie: {
                getTitle: function() {
                    return [
                        {
                            type: 'text',
                            text: DV.state.filter.names[0],
                            font: 'bold 15px arial',
                            fill: '#222',
                            width: 300,
                            height: 20,
                            x: 28,
                            y: 16
                        },
                        {
                            type: 'text',
                            text: DV.store.chart.left[0],
                            font: 'bold 13px arial',
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
        combobox: {
            filter: {
                clearValue: function(v, cb, i, d) {
                    if (v === cb.getValue()) {
                        cb.clearValue();
                    }
                },
                category: function() {
                    var cbs = DV.cmp.settings.series,
                        cbc = DV.cmp.settings.category,
                        cbf = DV.cmp.settings.filter,
                        v = cbs.getValue(),
                        d = DV.conf.finals.dimension.data.value,
                        p = DV.conf.finals.dimension.period.value,
                        o = DV.conf.finals.dimension.organisationunit.value,
                        index = 0;
                        
                    this.clearValue(v, cbc);
                    this.clearValue(v, cbf);
                    
                    cbc.filterArray = [!(v === d), !(v === p), !(v === o)];
                    cbc.store.filterBy( function(r) {
                        return cbc.filterArray[index++];
                    });
                    
                    this.filter();
                },                
                filter: function() {
                    var cbc = DV.cmp.settings.category,
                        cbf = DV.cmp.settings.filter,
                        v = cbc.getValue(),
                        d = DV.conf.finals.dimension.data.value,
                        p = DV.conf.finals.dimension.period.value,
                        o = DV.conf.finals.dimension.organisationunit.value,
                        index = 0;
                        
                    this.clearValue(v, cbf);
                        
                    cbf.filterArray = Ext.Array.clone(cbc.filterArray);
                    cbf.filterArray[0] = cbf.filterArray[0] ? !(v === d) : false;
                    cbf.filterArray[1] = cbf.filterArray[1] ? !(v === p) : false;
                    cbf.filterArray[2] = cbf.filterArray[2] ? !(v === o) : false;
                    
                    cbf.store.filterBy( function(r) {
                        return cbf.filterArray[index++];
                    });
                }
            }
        },
        checkbox: {
            setRelativePeriods: function(rp) {
                for (var r in rp) {
                    var cmp = DV.util.getCmp('checkbox[paramName="' + r + '"]');
                    if (cmp) {
                        cmp.setValue(rp[r]);
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
        },
        string: {
            getEncodedString: function(text) {
                return text.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'');
            }
        },
        value: {
            jsonfy: function(r) {
                r = Ext.JSON.decode(r.responseText);
                var values = [];
                for (var i = 0; i < r.length; i++) {
                    values.push({v: r[i][0], d: r[i][1], p: r[i][2], o: r[i][3]});
                }
                return values;
            }
        },
        crud: {
            favorite: {
                create: function(fn, isUpdate) {
                    DV.util.mask.setMask(DV.cmp.favorite.window, 'Saving...');
                    var params = DV.state.getParams();
                    params.name = DV.cmp.favorite.name.getValue();
                    params.trendLine = DV.cmp.favorite.trendline.getValue();
                    params.hideSubtitle = DV.cmp.favorite.hidesubtitle.getValue();
                    params.hideLegend = DV.cmp.favorite.hidelegend.getValue();
                    params.userOrganisationUnit = DV.cmp.favorite.userorganisationunit.getValue();
                    if (DV.cmp.favorite.xaxislabel.getValue()) {
                        params.xAxisLabel = DV.cmp.favorite.xaxislabel.getValue();
                    }
                    if (DV.cmp.favorite.yaxislabel.getValue()) {
                        params.yAxisLabel = DV.cmp.favorite.yaxislabel.getValue();
                    }
                    if (DV.cmp.favorite.targetlinevalue.getValue()) {
                        params.targetLineValue = DV.cmp.favorite.targetlinevalue.getValue();
                    }
                    if (params.targetLineValue && !DV.cmp.favorite.targetlinelabel.isDisabled()) {
                        params.targetLineLabel = DV.cmp.favorite.targetlinelabel.getValue();
                    }
                    
                    if (isUpdate) {
                        var store = DV.store.favorite;
                        params.uid = store.getAt(store.findExact('name', params.name)).data.id;
                    }
                    var url = DV.cmp.favorite.system.getValue() ? DV.conf.finals.ajax.favorite_addorupdatesystem : DV.conf.finals.ajax.favorite_addorupdate;
                    
                    Ext.Ajax.request({
                        url: DV.conf.finals.ajax.path_visualizer + url,
                        params: params,
                        success: function() {
                            DV.store.favorite.load({callback: function() {
                                DV.mask.hide();
                                if (fn) {
                                    fn();
                                }
                            }});
                        }
                    });
                },
                update: function(fn) {
                    DV.util.crud.favorite.create(fn, true);
                },
                updateName: function(name) {
                    if (DV.store.favorite.findExact('name', name) != -1) {
                        alert('Name is already in use');
                        return;
                    }
                    DV.util.mask.setMask(DV.cmp.favorite.window, 'Renaming...');
                    var r = DV.cmp.favorite.grid.getSelectionModel().getSelection()[0];
                    Ext.Ajax.request({
                        url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.favorite_addorupdate,
                        params: {uid: r.data.id, name: name},
                        success: function() {
                            DV.store.favorite.load({callback: function() {
                                DV.cmp.favorite.rename.window.close();
                                DV.mask.hide();
                                DV.cmp.favorite.grid.getSelectionModel().select(DV.store.favorite.getAt(DV.store.favorite.findExact('name', name)));
                                DV.cmp.favorite.name.setValue(name);
                            }});
                        }
                    });
                },
                del: function(fn) {
                    DV.util.mask.setMask(DV.cmp.favorite.window, 'Deleting...');
                    var baseurl = DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.favorite_delete,
                        selection = DV.cmp.favorite.grid.getSelectionModel().getSelection();
                    Ext.Array.each(selection, function(item) {
                        baseurl = Ext.String.urlAppend(baseurl, 'uids=' + item.data.id);
                    });
                    Ext.Ajax.request({
                        url: baseurl,
                        success: function() {
                            DV.store.favorite.load({callback: function() {
                                DV.mask.hide();
                                if (fn) {
                                    fn();
                                }
                            }});
                        }
                    }); 
                }
            }
        }
    };
    
    DV.store = {
        dimension: function() {
            return Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: [
                    {id: DV.conf.finals.dimension.data.value, name: DV.conf.finals.dimension.data.rawvalue},
                    {id: DV.conf.finals.dimension.period.value, name: DV.conf.finals.dimension.period.rawvalue},
                    {id: DV.conf.finals.dimension.organisationunit.value, name: DV.conf.finals.dimension.organisationunit.rawvalue}
                ]
            });
        },
        indicator: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 's'],
                proxy: {
                    type: 'ajax',
                    url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.indicator_get,
                    reader: {
                        type: 'json',
                        root: 'indicators'
                    }
                },
                storage: {},
                listeners: {
                    load: function(s) {
                        DV.util.store.addToStorage(s);
                        DV.util.multiselect.filterAvailable(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 's'],
                data: []
            })
        },
        dataelement: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 's'],
                proxy: {
                    type: 'ajax',
                    url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.dataelement_get,
                    reader: {
                        type: 'json',
                        root: 'dataElements'
                    }
                },
                storage: {},
                listeners: {
                    load: function(s) {
                        DV.util.store.addToStorage(s);
                        DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 's'],
                data: []
            })
        },
        datatable: null,
        getDataTableStore: function(exe) {
            this.datatable = Ext.create('Ext.data.Store', {
                fields: [
                    DV.conf.finals.dimension.data.value,
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
        },
        favorite: Ext.create('Ext.data.Store', {
            fields: ['id', 'name', 'lastUpdated', 'userId'],
            proxy: {
                type: 'ajax',
                url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.favorite_getall,
                reader: {
                    type: 'json',
                    root: 'charts'
                }
            },
            isLoaded: false,
            sorting: {
                field: 'lastUpdated',
                direction: 'DESC'
            },
            sortStore: function() {
                this.sort(this.sorting.field, this.sorting.direction);
            },
            listeners: {
                load: function(s) {
                    s.filterBy(function(r) {
                        if (r.data.userId == DV.init.system.user.id) {
                            return true;
                        }
                        if (!r.data.userId && DV.init.system.user.isAdmin) {
                            return true;
                        }
                        return false;
                    });
                        
                    s.sortStore();
                    s.each(function(r) {
                        r.data.lastUpdated = r.data.lastUpdated.substr(0,16);
                        r.data.icon = '<img src="images/favorite.png" />';
                        r.commit();
                    });
                    if (!s.isLoaded) {
                        s.isLoaded = true;
                    }
                }
            }
        })            
    };
    
    DV.state = {
        type: DV.conf.finals.chart.column,
        series: {
            dimension: DV.conf.finals.dimension.data.value,
            names: []
        },
        category: {
            dimension: DV.conf.finals.dimension.period.value,
            names: []
        },
        filter: {
            dimension: DV.conf.finals.dimension.organisationunit.value,
            names: []
        },
        indicatorIds: [],
        dataelementIds: [],
        relativePeriods: {},
        organisationunitIds: [],
        isRendered: false,
        getState: function(exe) {
            this.resetState();
            
            var tmp_series_dimension = DV.cmp.settings.series.getValue();
            var tmp_series_names = DV.util.dimension[tmp_series_dimension].getNames(true);
            
            var tmp_category_dimension = DV.cmp.settings.category.getValue();
            var tmp_category_names = DV.util.dimension[tmp_category_dimension].getNames(true);
            
            var tmp_filter_dimension = DV.cmp.settings.filter.getValue();
            var tmp_filter_names = DV.util.dimension[tmp_filter_dimension].getNames(true).slice(0,1);
            
            if (!tmp_series_names.length || !tmp_category_names.length || !tmp_filter_names.length) {
                return;
            }
            
            this.type = DV.util.button.type.getValue();
            
            this.series.dimension = tmp_series_dimension;
            this.series.names = tmp_series_names;
            
            this.category.dimension = tmp_category_dimension;
            this.category.names = tmp_category_names;
            
            this.filter.dimension = tmp_filter_dimension;
            this.filter.names = tmp_filter_names;
            
            this.indicatorIds = DV.util.dimension.indicator.getIds();
            this.dataelementIds = DV.util.dimension.dataelement.getIds();
            this.relativePeriods = DV.util.dimension.period.getRelativePeriodObject();
            this.organisationunitIds = DV.util.dimension.organisationunit.getIds();
            
            this.isRendered = true;
            DV.util.mask.setMask(DV.chart.chart, 'Loading...');
            
            if (exe) {
                DV.value.getValues(true);
            }
        },
        getParams: function() {
            var obj = {};
            obj.type = this.type.toUpperCase();
            obj.series = this.series.dimension.toUpperCase();
            obj.category = this.category.dimension.toUpperCase();
            obj.filter = this.filter.dimension.toUpperCase();
            obj.indicatorIds = this.indicatorIds;
            obj.dataElementIds = this.dataelementIds;
            obj.organisationUnitIds = this.organisationunitIds;
            obj = Ext.Object.merge(obj, this.relativePeriods);
            return obj;            
        },
        setState: function(exe, uid) {
            if (uid) {
                this.resetState();
                DV.util.mask.setMask(DV.cmp.region.center, 'Loading...');
                Ext.Ajax.request({
                    url: DV.conf.finals.ajax.path_api + DV.conf.finals.ajax.favorite_get + uid + '.json',
                    scope: this,
                    success: function(r) {
                        if (!r.responseText) {
                            DV.mask.hide();
                            alert('Invalid uid');
                            return;
                        }
                        var f = Ext.JSON.decode(r.responseText),
                            indiment = [];
                        f.type = f.type.toLowerCase();
                        f.series = f.series.toLowerCase();
                        f.category = f.category.toLowerCase();
                        f.filter = f.filter.toLowerCase();
                        
                        f.names = {
                            data: [],
                            period: [],
                            organisationunit: []
                        };
                        
                        this.type = f.type;
                        DV.util.button.type.setValue(this.type);
                        
                        this.series.dimension = f.series;
                        DV.cmp.settings.series.setValue(DV.conf.finals.dimension[this.series.dimension].value);
                        DV.util.combobox.filter.category();
                        
                        this.category.dimension = f.category;
                        DV.cmp.settings.category.setValue(DV.conf.finals.dimension[this.category.dimension].value);
                        DV.util.combobox.filter.filter();
                        
                        this.filter.dimension = f.filter;
                        DV.cmp.settings.filter.setValue(DV.conf.finals.dimension[this.filter.dimension].value);
                        
                        if (f.indicators) {
                            var records = [];
                            for (var i = 0; i < f.indicators.length; i++) {
                                indiment.push(f.indicators[i]);
                                this.indicatorIds.push(f.indicators[i].internalId);
                                records.push({id: f.indicators[i].internalId, s: f.indicators[i].shortName, name: f.indicators[i].shortName, parent: null});
                            }
                            DV.store.indicator.selected.removeAll();
                            DV.store.indicator.selected.add(records);
                            DV.util.store.addToStorage(DV.store.indicator.available, records);
                            DV.util.multiselect.filterAvailable(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                        }
                        if (f.dataElements) {
                            var records = [];
                            for (var i = 0; i < f.dataElements.length; i++) {
                                indiment.push(f.dataElements[i]);
                                this.dataelementIds.push(f.dataElements[i].internalId);
                                records.push({id: f.dataElements[i].internalId, s: f.dataElements[i].shortName, name: f.dataElements[i].shortName, parent: null});
                            }
                            DV.store.dataelement.selected.removeAll();
                            DV.store.dataelement.selected.add(records);
                            DV.util.store.addToStorage(DV.store.dataelement.available, records);
                            DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                        }
                        for (var i = 0; i < indiment.length; i++) {
                            f.names.data.push(indiment[i].shortName);
                        }
                        
                        this.relativePeriods = f.relativePeriods;
                        DV.util.checkbox.setRelativePeriods(this.relativePeriods);
                        f.names.period = DV.util.dimension.period.getNamesByRelativePeriodsObject(this.relativePeriods);
                        
                        for (var i = 0; i < f.organisationUnits.length; i++) {
                            this.organisationunitIds.push(f.organisationUnits[i].internalId);
                            f.names.organisationunit.push(f.organisationUnits[i].name);
                            DV.cmp.dimension.organisationunit.treepanel.storage[f.organisationUnits[i].internalId] = f.organisationUnits[i].name;
                        }
                        
                        this.series.names = f.names[this.series.dimension];
                        this.category.names = f.names[this.category.dimension];
                        this.filter.names = f.names[this.filter.dimension];
                        
                        DV.cmp.favorite.trendline.setValue(f.regression);
                        DV.cmp.favorite.hidesubtitle.setValue(f.hideSubtitle);
                        DV.cmp.favorite.hidelegend.setValue(f.hideLegend);
                        DV.cmp.favorite.userorganisationunit.setValue(f.userOrganisationUnit);
                        DV.cmp.favorite.xaxislabel.setValue(f.domainAxisLabel);
                        DV.cmp.favorite.yaxislabel.setValue(f.rangeAxisLabel);
                        DV.cmp.favorite.targetlinevalue.setValue(f.targetLineValue);
                        DV.cmp.favorite.targetlinelabel.xable();
                        DV.cmp.favorite.targetlinelabel.setValue(f.targetLineLabel);
                        
                        this.isRendered = true;
                        
                        if (exe) {
                            DV.value.getValues(true);
                        }
                    }
                });
            }
        },
        resetState: function() {
            this.type = null;
            this.series.dimension = null;
            this.series.names = [];
            this.category.dimension = null;
            this.category.names = [];
            this.filter.dimension = null;
            this.filter.names = [];
            this.indicatorIds = [];
            this.dataelementIds = [];
            this.relativePeriods = {};
            this.organisationunitIds = [];
        }
    };
    
    DV.value = {
        values: [],
        getValues: function(exe) {            
            var params = [],
                i = DV.conf.finals.dimension.indicator.value,
                d = DV.conf.finals.dimension.dataelement.value;
                
            params = params.concat(DV.util.dimension[DV.state.series.dimension].getUrl());
            params = params.concat(DV.util.dimension[DV.state.category.dimension].getUrl());
            params = params.concat(DV.util.dimension[DV.state.filter.dimension].getUrl(true));
            
            var baseurl = DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.data_get;
            Ext.Array.each(params, function(item) {
                baseurl = Ext.String.urlAppend(baseurl, item);
            });
            
            Ext.Ajax.request({
                url: baseurl,
                success: function(r) {
                    DV.value.values = DV.util.value.jsonfy(r);
                    if (!DV.value.values.length) {
                        DV.mask.hide();
                        alert('No data');
                        return;
                    }
                    
                    var storage = Ext.Object.merge(DV.store[i].available.storage, DV.store[d].available.storage);
                    Ext.Array.each(DV.value.values, function(item) {
                        item[DV.conf.finals.dimension.data.value] = DV.util.string.getEncodedString(storage[item.d].name);
                        item[DV.conf.finals.dimension.period.value] = DV.util.string.getEncodedString(DV.util.dimension.period.getNameById(item.p));
                        item[DV.conf.finals.dimension.organisationunit.value] = DV.util.string.getEncodedString(DV.cmp.dimension.organisationunit.treepanel.findNameById(item.o));
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
            
            Ext.Array.each(DV.state.category.names, function(item) {
                var obj = {};
                obj[DV.conf.finals.chart.x] = item;
                DV.chart.data.push(obj);
            });
            
            Ext.Array.each(DV.chart.data, function(item) {
                for (var i = 0; i < DV.state.series.names.length; i++) {
                    item[DV.state.series.names[i]] = 0;
                }
            });

            Ext.Array.each(DV.chart.data, function(item) {
                for (var i = 0; i < DV.state.series.names.length; i++) {
                    for (var j = 0; j < DV.value.values.length; j++) {
                        if (DV.value.values[j][DV.state.category.dimension] === item[DV.conf.finals.chart.x] && DV.value.values[j][DV.state.series.dimension] === DV.state.series.names[i]) {
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
                animate: true,
                store: DV.store.chart,
                insetPadding: DV.conf.chart.inset,
                items: DV.util.chart.getTitle(),
                legend: DV.util.chart.getLegend(),
                axes: [
                    {
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DV.store.chart.left,
                        label: DV.util.chart.label.getNumericLabel(),
                        grid: {
                            even: DV.util.chart.getGrid()
                        }
                    },
                    {
                        type: 'Category',
                        position: 'bottom',
                        fields: DV.store.chart.bottom,
                        label: DV.util.chart.label.getCategoryLabel()
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
        stackedcolumn: function() {
            this.column(true);
        },
        bar: function(stacked) {
            this.chart = Ext.create('Ext.chart.Chart', {
                animate: true,
                store: DV.store.chart,
                insetPadding: DV.conf.chart.inset,
                items: DV.util.chart.getTitle(),
                legend: DV.util.chart.getLegend(DV.store.chart.bottom.length),
                axes: [
                    {
                        type: 'Category',
                        position: 'left',
                        fields: DV.store.chart.left,
                        label: DV.util.chart.bar.getCategoryLabel()
                    },
                    {
                        type: 'Numeric',
                        position: 'bottom',
                        minimum: 0,
                        fields: DV.store.chart.bottom,
                        label: DV.util.chart.label.getNumericLabel(),
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
        stackedbar: function() {
            this.bar(true);
        },
        line: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                animate: true,
                store: DV.store.chart,
                insetPadding: DV.conf.chart.inset,
                items: DV.util.chart.getTitle(),
                legend: DV.util.chart.getLegend(),
                axes: [
                    {
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DV.store.chart.left,
                        label: DV.util.chart.label.getNumericLabel(),
                        grid: {
                            even: DV.util.chart.getGrid()
                        }
                    },
                    {
                        type: 'Category',
                        position: 'bottom',
                        fields: DV.store.chart.bottom,
                        label: DV.util.chart.label.getCategoryLabel()
                    }
                ],
                series: DV.util.chart.line.getSeriesArray()
            });
        },
        area: function() {
            this.chart = Ext.create('Ext.chart.Chart', {
                animate: true,
                store: DV.store.chart,
                insetPadding: DV.conf.chart.inset,
                items: DV.util.chart.getTitle(),
                legend: DV.util.chart.getLegend(),
                axes: [
                    {
                        type: 'Numeric',
                        position: 'left',
                        minimum: 0,
                        fields: DV.store.chart.left,
                        label: DV.util.chart.label.getNumericLabel(),
                        grid: {
                            even: DV.util.chart.getGrid()
                        }
                    },
                    {
                        type: 'Category',
                        position: 'bottom',
                        fields: DV.store.chart.bottom,
                        label: DV.util.chart.label.getCategoryLabel()
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
                animate: true,
                shadow: true,
                store: DV.store.chart,
                insetPadding: 60,
                items: DV.util.chart.pie.getTitle(),
                legend: DV.util.chart.getLegend(DV.state.category.names.length),
                series: [{
                    type: 'pie',
                    field: DV.store.chart.left[0],
                    showInLegend: true,
                    tips: DV.util.chart.pie.getTips(),
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
            DV.cmp.region.center.removeAll(true);
            DV.cmp.region.center.add(this.chart);
            
            if (DV.init.cmd !== DV.conf.finals.cmd.init) {
                DV.mask.hide();
                DV.store.getDataTableStore(true);
            }
            else {
                DV.init.cmd = false;
            }
        }
    };
    
    DV.datatable = {
        datatable: null,
        getDataTable: function(exe) {
            this.datatable = Ext.create('Ext.grid.Panel', {
                height: DV.util.viewport.getSize().y - 1,
                scroll: 'vertical',
                cls: 'dv-datatable',
                columns: [
                    {
                        text: DV.conf.finals.dimension.data.rawvalue,
                        dataIndex: DV.conf.finals.dimension.data.value,
                        width: 150,
                        height: DV.conf.layout.east_gridcolumn_height
                    },
                    {
                        text: DV.conf.finals.dimension.period.rawvalue,
                        dataIndex: DV.conf.finals.dimension.period.value,
                        width: 100,
                        height: DV.conf.layout.east_gridcolumn_height,
                        sortable: false
                    },
                    {
                        text: DV.conf.finals.dimension.organisationunit.rawvalue,
                        dataIndex: DV.conf.finals.dimension.organisationunit.value,
                        width: 150,
                        height: DV.conf.layout.east_gridcolumn_height
                    },
                    {
                        text: 'Value',
                        dataIndex: 'v',
                        width: 80,
                        height: DV.conf.layout.east_gridcolumn_height
                    }
                ],
                store: DV.store.datatable
            });
            
            if (exe) {
                this.reload();
            }
            else {
                return this.datatable;
            }
        },
        reload: function() {
            DV.cmp.region.east.removeAll(true);
            DV.cmp.region.east.add(this.datatable);
            DV.init.cmd = false;
        }            
    };
    
    DV.exe = {
        execute: function(exe, cmd) {
            if (cmd) {
                if (cmd === DV.conf.finals.cmd.init) {
                    DV.store.getChartStore(exe);
                }
                else {
                    DV.state.setState(true, cmd);
                }
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
                            handler: DV.util.button.type.toggleHandler,
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
                                name: DV.conf.finals.chart.stackedcolumn,
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
                                name: DV.conf.finals.chart.stackedbar,
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
                                        cls: 'dv-combo',
                                        name: DV.conf.finals.chart.series,
                                        emptyText: 'Series',
                                        queryMode: 'local',
                                        editable: false,
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_fieldset_width / 3) - 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.data.value,
                                        listeners: {
                                            afterrender: function() {
                                                DV.cmp.settings.series = this;
                                            },
                                            select: function() {
                                                DV.util.combobox.filter.category();
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
                                        cls: 'dv-combo',
                                        name: DV.conf.finals.chart.category,
                                        emptyText: 'Category',
                                        queryMode: 'local',
                                        editable: false,
                                        lastQuery: '',
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_fieldset_width / 3) - 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.period.value,
                                        listeners: {
                                            afterrender: function(cb) {
                                                DV.cmp.settings.category = this;
                                            },
                                            select: function(cb) {
                                                DV.util.combobox.filter.filter();
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
                                        cls: 'dv-combo',
                                        name: DV.conf.finals.chart.filter,
                                        emptyText: 'Filter',
                                        queryMode: 'local',
                                        editable: false,
                                        lastQuery: '',
                                        valueField: 'id',
                                        displayField: 'name',
                                        width: (DV.conf.layout.west_fieldset_width / 3) - 4,
                                        store: DV.store.dimension(),
                                        value: DV.conf.finals.dimension.organisationunit.value,
                                        listeners: {
                                            afterrender: function(cb) {
                                                DV.cmp.settings.filter = this;
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
                                cls: 'dv-fieldset',
                                name: DV.conf.finals.dimension.indicator.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleIndicator();" class="dv-fieldset-title-link">Indicators</a>',
                                collapsible: true,
                                width: DV.conf.layout.west_fieldset_width,
                                items: [
                                    {
                                        xtype: 'combobox',
                                        cls: 'dv-combo',
                                        style: 'margin-bottom:8px',
                                        width: DV.conf.layout.west_fieldset_width - 22,
                                        valueField: 'id',
                                        displayField: 'name',
                                        fieldLabel: 'Select group',
                                        labelStyle: 'padding-left:7px;',
                                        labelWidth: 90,
                                        editable: false,
                                        queryMode: 'remote',
                                        store: Ext.create('Ext.data.Store', {
                                            fields: ['id', 'name', 'index'],
                                            proxy: {
                                                type: 'ajax',
                                                url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.indicatorgroup_get,
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
                                                    DV.util.multiselect.filterAvailable(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
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
                                                cls: 'dv-toolbar-multiselect-left',
                                                width: (DV.conf.layout.west_fieldset_width - 22) / 2,
                                                displayField: 's',
                                                valueField: 'id',
                                                queryMode: 'remote',
                                                store: DV.store.indicator.available,
                                                tbar: [
                                                    {
                                                        xtype: 'label',
                                                        text: 'Available indicators',
                                                        cls: 'dv-toolbar-multiselect-left-label'
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowright.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.select(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowrightdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.selectAll(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                                                        }
                                                    },
                                                    ' '
                                                ],
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.dimension.indicator.available = this;
                                                    },
                                                    afterrender: function() {
                                                        this.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.select(this, DV.cmp.dimension.indicator.selected);
                                                        }, this);
                                                    }
                                                }
                                            },                                            
                                            {
                                                xtype: 'multiselect',
                                                id: 'selectedIndicators',
                                                name: 'selectedIndicators',
                                                cls: 'dv-toolbar-multiselect-right',
                                                width: (DV.conf.layout.west_fieldset_width - 22) / 2,
                                                displayField: 's',
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
                                                            DV.util.multiselect.unselectAll(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleft.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselect(DV.cmp.dimension.indicator.available, DV.cmp.dimension.indicator.selected);
                                                        }
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'label',
                                                        text: 'Selected indicators',
                                                        cls: 'dv-toolbar-multiselect-right-label'
                                                    }
                                                ],
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.dimension.indicator.selected = this;
                                                    },
                                                    afterrender: function() {
                                                        this.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.unselect(DV.cmp.dimension.indicator.available, this);
                                                        }, this);
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    afterrender: function() {
                                        DV.cmp.fieldset.indicator = this;
                                    },
                                    expand: function() {
                                        DV.util.fieldset.collapseFieldsets([DV.cmp.fieldset.dataelement, DV.cmp.fieldset.period, DV.cmp.fieldset.organisationunit, DV.cmp.fieldset.options]);
                                    }
                                }
                            },
                            {
                                xtype: 'fieldset',
                                cls: 'dv-fieldset',
                                name: DV.conf.finals.dimension.dataelement.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleDataElement();" class="dv-fieldset-title-link">Data elements</a>',
                                collapsed: true,
                                collapsible: true,
                                items: [
                                    {
                                        xtype: 'combobox',
                                        cls: 'dv-combo',
                                        style: 'margin-bottom:8px',
                                        width: DV.conf.layout.west_fieldset_width - 22,
                                        valueField: 'id',
                                        displayField: 'name',
                                        fieldLabel: 'Select group',
                                        labelStyle: 'padding-left:7px;',
                                        labelWidth: 90,
                                        editable: false,
                                        queryMode: 'remote',
                                        store: Ext.create('Ext.data.Store', {
                                            fields: ['id', 'name', 'index'],
                                            proxy: {
                                                type: 'ajax',
                                                url: DV.conf.finals.ajax.path_commons + DV.conf.finals.ajax.dataelementgroup_get,
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
                                                    DV.util.multiselect.filterAvailable(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
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
                                            Ext.create('Ext.ux.form.MultiSelect', {
                                                id: 'availableDataElements',
                                                name: 'availableDataElements',
                                                cls: 'dv-toolbar-multiselect-left',
                                                width: (DV.conf.layout.west_fieldset_width - 22) / 2,
                                                displayField: 's',
                                                valueField: 'id',
                                                queryMode: 'remote',
                                                store: DV.store.dataelement.available,
                                                tbar: [
                                                    {
                                                        xtype: 'label',
                                                        text: 'Available data elements',
                                                        cls: 'dv-toolbar-multiselect-left-label'
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowright.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.select(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowrightdouble.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.selectAll(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                                                        }
                                                    },
                                                    ' '
                                                ],
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.dimension.dataelement.available = this;
                                                    },                                                                
                                                    afterrender: function() {
                                                        this.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.select(this, DV.cmp.dimension.dataelement.selected);
                                                        }, this);
                                                    }
                                                }
                                            }),                                            
                                            {
                                                xtype: 'multiselect',
                                                id: 'selectedDataElements',
                                                name: 'selectedDataElements',
                                                cls: 'dv-toolbar-multiselect-right',
                                                width: (DV.conf.layout.west_fieldset_width - 22) / 2,
                                                displayField: 's',
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
                                                            DV.util.multiselect.unselectAll(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                                                        }
                                                    },
                                                    {
                                                        xtype: 'button',
                                                        icon: 'images/arrowleft.png',
                                                        width: 22,
                                                        handler: function() {
                                                            DV.util.multiselect.unselect(DV.cmp.dimension.dataelement.available, DV.cmp.dimension.dataelement.selected);
                                                        }
                                                    },
                                                    '->',
                                                    {
                                                        xtype: 'label',
                                                        text: 'Selected data elements',
                                                        cls: 'dv-toolbar-multiselect-right-label'
                                                    }
                                                ],
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.dimension.dataelement.selected = this;
                                                    },          
                                                    afterrender: function() {
                                                        this.boundList.on('itemdblclick', function() {
                                                            DV.util.multiselect.unselect(DV.cmp.dimension.dataelement.available, this);
                                                        }, this);
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    afterrender: function() {
                                        DV.cmp.fieldset.dataelement = this;
                                    },
                                    expand: function() {
                                        DV.util.fieldset.collapseFieldsets([DV.cmp.fieldset.indicator, DV.cmp.fieldset.period, DV.cmp.fieldset.organisationunit, DV.cmp.fieldset.options]);
                                    }
                                }
                            },
                            {
                                xtype: 'fieldset',
                                cls: 'dv-fieldset',
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
                                                bodyStyle: 'border-style:none; padding:0 0 0 10px',
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
                                                        cls: 'dv-label-period-heading'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastMonth',
                                                        boxLabel: 'Last month'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'last12Months',
                                                        boxLabel: 'Last 12 months',
                                                        checked: true
                                                    }
                                                ]
                                            },
                                            {
                                                xtype: 'panel',
                                                layout: 'anchor',
                                                bodyStyle: 'border-style:none; padding:0 0 0 32px',
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
                                                        text: 'Quarters',
                                                        cls: 'dv-label-period-heading'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastQuarter',
                                                        boxLabel: 'Last quarter'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'last4Quarters',
                                                        boxLabel: 'Last 4 quarters'
                                                    }
                                                ]
                                            },
                                            {
                                                xtype: 'panel',
                                                layout: 'anchor',
                                                bodyStyle: 'border-style:none; padding:0 0 0 32px',
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
                                                        text: 'Six-months',
                                                        cls: 'dv-label-period-heading'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'lastSixMonth',
                                                        boxLabel: 'Last six-month'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'last2SixMonths',
                                                        boxLabel: 'Last 2 six-months'
                                                    }
                                                ]
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border-style:none',
                                        items: [
                                            {
                                                xtype: 'panel',
                                                layout: 'anchor',
                                                bodyStyle: 'border-style:none; padding:5px 0 0 10px',
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
                                                        text: 'Years',
                                                        cls: 'dv-label-period-heading'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'thisYear',
                                                        boxLabel: 'This year'
                                                    },
                                                    {
                                                        xtype: 'checkbox',
                                                        paramName: 'last5Years',
                                                        boxLabel: 'Last 5 years'
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    afterrender: function() {
                                        DV.cmp.fieldset.period = this;
                                    },
                                    expand: function() {
                                        DV.util.fieldset.collapseFieldsets([DV.cmp.fieldset.indicator, DV.cmp.fieldset.dataelement, DV.cmp.fieldset.organisationunit, DV.cmp.fieldset.options]);
                                    }
                                }
                            },
                            {
                                xtype: 'fieldset',
                                cls: 'dv-fieldset',
                                name: DV.conf.finals.dimension.organisationunit.value,
                                title: '<a href="javascript:DV.util.fieldset.toggleOrganisationUnit();" class="dv-fieldset-title-link">Organisation units</a>',
                                collapsed: true,
                                collapsible: true,
                                items: [
                                    {
                                        xtype: 'treepanel',
                                        cls: 'dv-tree',
                                        height: 300,
                                        width: DV.conf.layout.west_fieldset_width - 22,
                                        autoScroll: true,
                                        multiSelect: true,
                                        isRendered: false,
                                        storage: {},
                                        selectRoot: function() {
                                            if (this.isRendered) {
                                                if (!this.getSelectionModel().getSelection().length) {
                                                    this.getSelectionModel().select(this.getRootNode());
                                                }
                                            }
                                        },
                                        findNameById: function(id) {
                                            var n = this.store.getNodeById(id) ? this.store.getNodeById(id).data.text : null;                                            
                                            if (!n) {
                                                for (var k in this.storage) {
                                                    if (k === id) {
                                                        n = this.storage[k];
                                                    }
                                                }
                                            }
                                            return n;
                                        },
                                        store: Ext.create('Ext.data.TreeStore', {
                                            proxy: {
                                                type: 'ajax',
                                                url: DV.conf.finals.ajax.path_visualizer + DV.conf.finals.ajax.organisationunitchildren_get
                                            },
                                            root: {
                                                id: DV.init.system.rootNode.id,
                                                text: DV.init.system.rootNode.name,
                                                expanded: false
                                            }
                                        }),
                                        listeners: {
                                            added: function() {
                                                DV.cmp.dimension.organisationunit.treepanel = this;
                                            },
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
                                    afterrender: function() {
                                        DV.cmp.fieldset.organisationunit = this;
                                    },
                                    expand: function(fs) {
                                        DV.util.fieldset.collapseFieldsets([DV.cmp.fieldset.indicator, DV.cmp.fieldset.dataelement, DV.cmp.fieldset.period, DV.cmp.fieldset.options]);
                                        var tp = DV.cmp.dimension.organisationunit.treepanel;
                                        if (!tp.isRendered) {
                                            tp.isRendered = true;
                                            tp.getRootNode().expand();
                                            tp.selectRoot();
                                        }
                                    }
                                }
                            },
                            {
                                xtype: 'fieldset',
                                cls: 'dv-fieldset',
                                name: 'options',
                                title: '<a href="javascript:DV.util.fieldset.toggleOptions();" class="dv-fieldset-title-link-alt1">Chart options</a>',
                                collapsed: true,
                                collapsible: true,
                                items: [
                                    {
                                        html: 'NB! These fields are for the PNG version only',
                                        bodyStyle: 'border:0 none; color:#555; font-style:italic; padding-bottom:10px'
                                    },
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border-style:none; padding-bottom:10px',
                                        items: [
                                            {
                                                xtype: 'checkbox',
                                                cls: 'dv-checkbox-alt1',
                                                style: 'margin-right:26px',
                                                boxLabel: 'Trend line',
                                                labelWidth: DV.conf.layout.form_label_width,
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.favorite.trendline = this;
                                                    }
                                                }
                                            },
                                            {
                                                xtype: 'checkbox',
                                                cls: 'dv-checkbox-alt1',
                                                style: 'margin-right:26px',
                                                boxLabel: 'Hide subtitle',
                                                labelWidth: DV.conf.layout.form_label_width,
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.favorite.hidesubtitle = this;
                                                    }
                                                }
                                            },
                                            {
                                                xtype: 'checkbox',
                                                cls: 'dv-checkbox-alt1',
                                                style: 'margin-right:26px',
                                                boxLabel: 'Hide legend',
                                                labelWidth: DV.conf.layout.form_label_width,
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.favorite.hidelegend = this;
                                                    }
                                                }
                                            },
                                            {
                                                xtype: 'checkbox',
                                                cls: 'dv-checkbox-alt1',
                                                boxLabel: 'User orgunit',
                                                labelWidth: DV.conf.layout.form_label_width,
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.favorite.userorganisationunit = this;
                                                    }
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border:0 none; padding-bottom:8px',
                                        items: [
                                            {
                                                xtype: 'textfield',
                                                cls: 'dv-textfield-alt1',
                                                style: 'margin-right:4px',
                                                fieldLabel: 'X axis label',
                                                labelAlign: 'top',
                                                labelSeparator: '',
                                                maxLength: 100,
                                                enforceMaxLength: true,
                                                labelWidth: DV.conf.layout.form_label_width,
                                                width: 188,
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.favorite.xaxislabel = this;
                                                    }
                                                }
                                            },
                                            {
                                                xtype: 'textfield',
                                                cls: 'dv-textfield-alt1',
                                                fieldLabel: 'Y axis label',
                                                labelAlign: 'top',
                                                labelSeparator: '',
                                                maxLength: 100,
                                                enforceMaxLength: true,
                                                labelWidth: DV.conf.layout.form_label_width,
                                                width: 187,
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.favorite.yaxislabel = this;
                                                    }
                                                }
                                            }
                                        ]
                                    },
                                    {
                                        xtype: 'panel',
                                        layout: 'column',
                                        bodyStyle: 'border:0 none',
                                        items: [
                                            {
                                                xtype: 'numberfield',
                                                cls: 'dv-textfield-alt1',
                                                style: 'margin-right:4px',
                                                hideTrigger: true,
                                                fieldLabel: 'Target line value',
                                                labelAlign: 'top',
                                                labelSeparator: '',
                                                maxLength: 100,
                                                enforceMaxLength: true,
                                                width: 188,
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.favorite.targetlinevalue = this;
                                                    },
                                                    change: function() {
                                                        DV.cmp.favorite.targetlinelabel.xable();
                                                    }
                                                }
                                            },
                                            {
                                                xtype: 'textfield',
                                                cls: 'dv-textfield-alt1',
                                                fieldLabel: 'Target line label',
                                                labelAlign: 'top',
                                                labelSeparator: '',
                                                maxLength: 100,
                                                enforceMaxLength: true,
                                                width: 187,
                                                disabled: true,
                                                xable: function() {
                                                    if (DV.cmp.favorite.targetlinevalue.getValue()) {
                                                        this.enable();
                                                    }
                                                    else {
                                                        this.disable();
                                                    }
                                                },
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.favorite.targetlinelabel = this;
                                                    }
                                                }
                                            }
                                        ]
                                    }
                                ],
                                listeners: {
                                    afterrender: function() {
                                        DV.cmp.fieldset.options = this;
                                    },
                                    expand: function() {
                                        DV.util.fieldset.collapseFieldsets([DV.cmp.fieldset.indicator, DV.cmp.fieldset.dataelement, DV.cmp.fieldset.period, DV.cmp.fieldset.organisationunit]);
                                    }
                                }
                            }
                        ]
                    }
                ],
                listeners: {
                    afterrender: function() {
                        DV.cmp.region.west = this;
                    },
                    collapse: function() {                    
                        this.collapsed = true;
                        DV.cmp.toolbar.resizewest.setText('>>>');
                    },
                    expand: function() {
                        this.collapsed = false;
                        DV.cmp.toolbar.resizewest.setText('<<<');
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
                    cls: 'dv-toolbar',
                    height: DV.conf.layout.center_tbar_height,
                    defaults: {
                        height: 26
                    },
                    items: [
                        {
                            xtype: 'button',
                            name: 'resizewest',
							cls: 'dv-toolbar-btn-2',
                            text: '<<<',
                            tooltip: 'Show/hide chart settings',
                            handler: function() {
                                var p = DV.cmp.region.west;
                                if (p.collapsed) {
                                    p.expand();
                                }
                                else {
                                    p.collapse();
                                }
                            },
                            listeners: {
                                added: function() {
                                    DV.cmp.toolbar.resizewest = this;
                                }
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-1',
                            text: 'Update',
                            handler: function() {
                                DV.exe.execute(true, DV.init.cmd);
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: 'Data table',
                            handler: function() {
                                var p = DV.cmp.region.east;
                                if (p.collapsed && p.items.length) {
                                    p.expand();
                                    DV.cmp.toolbar.resizeeast.show();
                                    DV.exe.datatable(true);
                                }
                                else {
                                    p.collapse();
                                    DV.cmp.toolbar.resizeeast.hide();
                                }
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: 'Favorites..',
                            listeners: {
                                afterrender: function(b) {
                                    this.menu = Ext.create('Ext.menu.Menu', {
                                        shadowOffset: 1,
                                        showSeparator: false,
                                        items: [
                                            {
                                                text: 'Manage favorites',
                                                iconCls: 'dv-menu-item-edit',
                                                handler: function() {
                                                    if (DV.cmp.favorite.window) {
                                                        DV.cmp.favorite.window.show();
                                                    }
                                                    else {
                                                        DV.cmp.favorite.window = Ext.create('Ext.window.Window', {
                                                            title: 'Manage favorites',
                                                            iconCls: 'dv-window-title-favorite',
                                                            bodyStyle: 'padding:8px; background-color:#fff',
															width: DV.conf.layout.grid_favorite_width,
                                                            closeAction: 'hide',
                                                            resizable: false,
                                                            modal: true,
                                                            resetForm: function() {
                                                                DV.cmp.favorite.name.setValue('');
                                                                DV.cmp.favorite.system.setValue(false);
                                                            },
                                                            items: [
                                                                {
                                                                    xtype: 'form',
                                                                    bodyStyle: 'border-style:none',
                                                                    items: [
                                                                        {
                                                                            xtype: 'textfield',
                                                                            cls: 'dv-textfield',
                                                                            fieldLabel: 'Name',
                                                                            maxLength: 160,
                                                                            enforceMaxLength: true,
                                                                            labelWidth: DV.conf.layout.form_label_width,
                                                                            width: DV.conf.layout.grid_favorite_width - 28,
                                                                            listeners: {
                                                                                added: function() {
                                                                                    DV.cmp.favorite.name = this;
                                                                                },
                                                                                change: function() {
                                                                                    DV.cmp.favorite.save.xable();
                                                                                }
                                                                            }
                                                                        },
                                                                        {
                                                                            xtype: 'checkbox',
                                                                            cls: 'dv-checkbox',
                                                                            style: 'padding-bottom:2px',
                                                                            fieldLabel: 'System',
                                                                            labelWidth: DV.conf.layout.form_label_width,
                                                                            disabled: !DV.init.system.user.isAdmin,
                                                                            listeners: {
                                                                                added: function() {
                                                                                    DV.cmp.favorite.system = this;
                                                                                }
                                                                            }
                                                                        }
                                                                    ]
                                                                },
                                                                {
                                                                    xtype: 'grid',
                                                                    width: DV.conf.layout.grid_favorite_width - 28,
                                                                    scroll: 'vertical',
                                                                    multiSelect: true,
                                                                    columns: [
                                                                        {
                                                                            dataIndex: 'name',
                                                                            width: DV.conf.layout.grid_favorite_width - 139,
                                                                            style: 'display:none'
                                                                        },
                                                                        {
                                                                            dataIndex: 'lastUpdated',
                                                                            width: 111,
                                                                            style: 'display:none'
                                                                        }
                                                                    ],
                                                                    setHeightInWindow: function(store) {
                                                                        var h = (store.getCount() * 23) + 30,
                                                                            sh = DV.util.viewport.getSize().y * 0.6;
                                                                        this.setHeight(h > sh ? sh : h);
                                                                        this.doLayout();
                                                                        this.up('window').doLayout();
                                                                    },
                                                                    store: DV.store.favorite,
                                                                    tbar: {
                                                                        id: 'favorite_t',
                                                                        cls: 'dv-toolbar',
                                                                        height: 30,
                                                                        defaults: {
                                                                            height: 24
                                                                        },
                                                                        items: [
                                                                            {
                                                                                text: 'Sort by..',
                                                                                cls: 'dv-toolbar-btn-2',
                                                                                listeners: {
                                                                                    added: function() {
                                                                                        DV.cmp.favorite.sortby = this;
                                                                                    },
                                                                                    afterrender: function(b) {
                                                                                        this.addCls('dv-menu-togglegroup');
                                                                                        this.menu = Ext.create('Ext.menu.Menu', {
                                                                                            shadowOffset: 1,
                                                                                            showSeparator: false,
                                                                                            width: 109,
                                                                                            height: 70,
                                                                                            items: [
                                                                                                {
                                                                                                    xtype: 'radiogroup',
                                                                                                    cls: 'dv-radiogroup',
                                                                                                    columns: 1,
                                                                                                    vertical: true,
                                                                                                    items: [
                                                                                                        {
                                                                                                            boxLabel: 'Name',
                                                                                                            name: 'sortby',
                                                                                                            handler: function() {
                                                                                                                if (this.getValue()) {
                                                                                                                    var store = DV.store.favorite;
                                                                                                                    store.sorting.field = 'name';
                                                                                                                    store.sorting.direction = 'ASC';
                                                                                                                    store.sortStore();
                                                                                                                    this.up('menu').hide();
                                                                                                                }
                                                                                                            }
                                                                                                        },
                                                                                                        {
                                                                                                            boxLabel: 'System',
                                                                                                            name: 'sortby',
                                                                                                            handler: function() {
                                                                                                                if (this.getValue()) {
                                                                                                                    var store = DV.store.favorite;
                                                                                                                    store.sorting.field = 'userId';
                                                                                                                    store.sorting.direction = 'ASC';
                                                                                                                    store.sortStore();
                                                                                                                    this.up('menu').hide();
                                                                                                                }
                                                                                                            }
                                                                                                        },
                                                                                                        {
                                                                                                            boxLabel: 'Last updated',
                                                                                                            name: 'sortby',
                                                                                                            checked: true,
                                                                                                            handler: function() {
                                                                                                                if (this.getValue()) {
                                                                                                                    var store = DV.store.favorite;
                                                                                                                    store.sorting.field = 'lastUpdated';
                                                                                                                    store.sorting.direction = 'DESC';
                                                                                                                    store.sortStore();
                                                                                                                    this.up('menu').hide();
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    ]
                                                                                                }
                                                                                            ]
                                                                                        });
                                                                                    }
                                                                                }
                                                                            },
                                                                            '->',
                                                                            {
                                                                                text: 'Rename',
                                                                                cls: 'dv-toolbar-btn-2',
                                                                                disabled: true,
                                                                                xable: function() {
                                                                                    if (DV.cmp.favorite.grid.getSelectionModel().getSelection().length == 1) {
                                                                                        DV.cmp.favorite.rename.button.enable();
                                                                                    }
                                                                                    else {
                                                                                        DV.cmp.favorite.rename.button.disable();
                                                                                    }
                                                                                },
                                                                                handler: function() {
                                                                                    var selected = DV.cmp.favorite.grid.getSelectionModel().getSelection()[0];
                                                                                    var w = Ext.create('Ext.window.Window', {
                                                                                        title: 'Rename favorite',
                                                                                        layout: 'fit',
                                                                                        width: DV.conf.layout.window_confirm_width,
                                                                                        bodyStyle: 'padding:10px 5px; background-color:#fff; text-align:center',
                                                                                        modal: true,
                                                                                        cmp: {},
                                                                                        items: [
                                                                                            {
                                                                                                xtype: 'textfield',
                                                                                                cls: 'dv-textfield',
                                                                                                maxLength: 160,
                                                                                                enforceMaxLength: true,
                                                                                                value: selected.data.name,
                                                                                                listeners: {
                                                                                                    added: function() {
                                                                                                        this.up('window').cmp.name = this;
                                                                                                    },
                                                                                                    change: function() {
                                                                                                        this.up('window').cmp.rename.xable();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        ],
                                                                                        bbar: [
                                                                                            {
                                                                                                text: 'Cancel',
                                                                                                handler: function() {
                                                                                                    this.up('window').close();
                                                                                                }
                                                                                            },
                                                                                            '->',
                                                                                            {
                                                                                                text: 'Rename',
                                                                                                disabled: true,
                                                                                                xable: function() {
                                                                                                    var value = this.up('window').cmp.name.getValue();
                                                                                                    if (value && value != DV.cmp.favorite.name.getValue()) {
                                                                                                        this.enable();
                                                                                                    }
                                                                                                    else {
                                                                                                        this.disable();
                                                                                                    }
                                                                                                },
                                                                                                handler: function() {
                                                                                                    DV.util.crud.favorite.updateName(this.up('window').cmp.name.getValue());
                                                                                                },
                                                                                                listeners: {
                                                                                                    afterrender: function() {
                                                                                                        this.up('window').cmp.rename = this;
                                                                                                    },
                                                                                                    change: function() {
                                                                                                        this.xable();
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        ],
                                                                                        listeners: {
                                                                                            afterrender: function() {
                                                                                                DV.cmp.favorite.rename.window = this;
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                    w.setPosition((screen.width/2)-(DV.conf.layout.window_confirm_width/2), DV.conf.layout.window_favorite_ypos + 100, true);
                                                                                    w.show();
                                                                                },
                                                                                listeners: {
                                                                                    added: function() {
                                                                                        DV.cmp.favorite.rename.button = this;
                                                                                    }
                                                                                }
                                                                            },
                                                                            {
                                                                                text: 'Delete',
                                                                                cls: 'dv-toolbar-btn-2',
                                                                                disabled: true,
                                                                                xable: function() {
                                                                                    if (DV.cmp.favorite.grid.getSelectionModel().getSelection().length) {
                                                                                        DV.cmp.favorite.del.enable();
                                                                                    }
                                                                                    else {
                                                                                        DV.cmp.favorite.del.disable();
                                                                                    }
                                                                                },
                                                                                handler: function() {
                                                                                    var sel = DV.cmp.favorite.grid.getSelectionModel().getSelection();
                                                                                    if (sel.length) {
                                                                                        var str = '';
                                                                                        for (var i = 0; i < sel.length; i++) {
                                                                                            var out = sel[i].data.name.length > 35 ? (sel[i].data.name.substr(0,35) + '...') : sel[i].data.name;
                                                                                            str += '<br/>' + out;
                                                                                        }
                                                                                        var w = Ext.create('Ext.window.Window', {
                                                                                            title: 'Delete favorites',
                                                                                            width: DV.conf.layout.window_confirm_width,
                                                                                            bodyStyle: 'padding:10px 5px; background-color:#fff; text-align:center',
                                                                                            modal: true,
                                                                                            items: [
                                                                                                {
                                                                                                    html: 'Are you sure?',
                                                                                                    bodyStyle: 'border-style:none'
                                                                                                },
                                                                                                {
                                                                                                    html: str,
                                                                                                    cls: 'dv-window-confirm-list'
                                                                                                }                                                                                                    
                                                                                            ],
                                                                                            bbar: [
                                                                                                {
                                                                                                    text: 'Cancel',
                                                                                                    handler: function() {
                                                                                                        this.up('window').close();
                                                                                                    }
                                                                                                },
                                                                                                '->',
                                                                                                {
                                                                                                    text: 'Delete',
                                                                                                    handler: function() {
                                                                                                        this.up('window').close();
                                                                                                        DV.util.crud.favorite.del(function() {
                                                                                                            DV.cmp.favorite.name.setValue('');
                                                                                                            DV.cmp.favorite.window.down('grid').setHeightInWindow(DV.store.favorite);
                                                                                                        });                                                                                                        
                                                                                                    }
                                                                                                }
                                                                                            ]
                                                                                        });
                                                                                        w.setPosition((screen.width/2)-(DV.conf.layout.window_confirm_width/2), DV.conf.layout.window_favorite_ypos + 100, true);
                                                                                        w.show();
                                                                                    }
                                                                                },
                                                                                listeners: {
                                                                                    added: function() {
                                                                                        DV.cmp.favorite.del = this;
                                                                                    }
                                                                                }
                                                                            }
                                                                        ]
                                                                    },
                                                                    listeners: {
                                                                        added: function() {
                                                                            DV.cmp.favorite.grid = this;
                                                                        },
                                                                        itemclick: function(g, r) {
                                                                            DV.cmp.favorite.name.setValue(r.data.name);
                                                                            DV.cmp.favorite.system.setValue(r.data.userId ? false : true);
                                                                            DV.cmp.favorite.rename.button.xable();
                                                                            DV.cmp.favorite.del.xable();
                                                                        },
                                                                        itemdblclick: function() {
                                                                            DV.cmp.favorite.save.handler();
                                                                        }
                                                                    }
                                                                }
                                                            ],
                                                            bbar: {
                                                                cls: 'dv-toolbar',
                                                                height: 29,
                                                                defaults: {
                                                                    height: 24
                                                                },
                                                                items: [
                                                                    '->',
                                                                    {
                                                                        text: 'Save',
                                                                        disabled: true,
                                                                        xable: function() {
                                                                            if (DV.state.isRendered && DV.cmp.favorite.name.getValue()) {
                                                                                this.enable();
                                                                            }
                                                                            else {
                                                                                this.disable();
                                                                            }
                                                                        },
                                                                        handler: function() {
                                                                            var value = DV.cmp.favorite.name.getValue();
                                                                            if (DV.state.isRendered && value) {
                                                                                if (DV.store.favorite.findExact('name', value) != -1) {
                                                                                    var item = value.length > 40 ? (value.substr(0,40) + '...') : value;
                                                                                    var w = Ext.create('Ext.window.Window', {
                                                                                        title: 'Save favorite',
                                                                                        width: DV.conf.layout.window_confirm_width,
                                                                                        bodyStyle: 'padding:10px 5px; background-color:#fff; text-align:center',
                                                                                        modal: true,
                                                                                        items: [
                                                                                            {
                                                                                                html: 'Are you sure?',
                                                                                                bodyStyle: 'border-style:none'
                                                                                            },
                                                                                            {
                                                                                                html: '<br/>' + item,
                                                                                                cls: 'dv-window-confirm-list'
                                                                                            }
                                                                                        ],
                                                                                        bbar: [
                                                                                            {
                                                                                                text: 'Cancel',
                                                                                                handler: function() {
                                                                                                    DV.cmp.favorite.window.close();
                                                                                                }
                                                                                            },
                                                                                            '->',
                                                                                            {
                                                                                                text: 'Overwrite',
                                                                                                handler: function() {
                                                                                                    this.up('window').close();
                                                                                                    DV.util.crud.favorite.update(function() {
                                                                                                        DV.cmp.favorite.window.resetForm();
                                                                                                    });
                                                                                                    
                                                                                                }
                                                                                            }
                                                                                        ]
                                                                                    });
                                                                                    w.setPosition((screen.width/2)-(DV.conf.layout.window_confirm_width/2), DV.conf.layout.window_favorite_ypos + 100, true);
                                                                                    w.show();
                                                                                }
                                                                                else {
                                                                                    DV.util.crud.favorite.create(function() {
                                                                                        DV.cmp.favorite.window.resetForm();
                                                                                        DV.cmp.favorite.window.down('grid').setHeightInWindow(DV.store.favorite);
                                                                                    });
                                                                                }                                                                                    
                                                                            }
                                                                        },
                                                                        listeners: {
                                                                            added: function() {
                                                                                DV.cmp.favorite.save = this;
                                                                            }
                                                                        }
                                                                    }
                                                                ]
                                                            },
                                                            listeners: {
                                                                show: function() {                                               
                                                                    DV.cmp.favorite.save.xable();
                                                                    this.down('grid').setHeightInWindow(DV.store.favorite);
                                                                }
                                                            }
                                                        });
                                                        var w = DV.cmp.favorite.window;
                                                        w.setPosition((screen.width/2)-(DV.conf.layout.grid_favorite_width/2), DV.conf.layout.window_favorite_ypos, true);
                                                        w.show();
                                                    }
                                                },
                                                listeners: {
                                                    added: function() {
                                                        DV.cmp.toolbar.menuitem.datatable = this;
                                                    }
                                                }
                                            },
                                            '-',
                                            {
                                                xtype: 'grid',
                                                cls: 'dv-menugrid',
                                                width: 420,
                                                scroll: 'vertical',
                                                columns: [
                                                    {
                                                        dataIndex: 'icon',
                                                        width: 25,
                                                        style: 'display:none'
                                                    },
                                                    {
                                                        dataIndex: 'name',
                                                        width: 289,
                                                        style: 'display:none'
                                                    },
                                                    {
                                                        dataIndex: 'lastUpdated',
                                                        width: 106,
                                                        style: 'display:none'
                                                    }
                                                ],
                                                setHeightInMenu: function(store) {
                                                    var h = store.getCount() * 26,
                                                        sh = DV.util.viewport.getSize().y * 0.6;
                                                    this.setHeight(h > sh ? sh : h);
                                                    this.doLayout();
                                                    this.up('menu').doLayout();
                                                },
                                                store: DV.store.favorite,
                                                listeners: {
                                                    itemclick: function(g, r) {
                                                        g.getSelectionModel().select([], false);
                                                        this.up('menu').hide();
                                                        DV.exe.execute(true, r.data.id);
                                                    }
                                                }
                                            }
                                        ],
                                        listeners: {
                                            show: function() {
                                                if (!DV.store.favorite.isLoaded) {
                                                    DV.store.favorite.load({scope: this, callback: function() {
                                                        this.down('grid').setHeightInMenu(DV.store.favorite);
                                                    }});
                                                }
                                                else {
                                                    this.down('grid').setHeightInMenu(DV.store.favorite);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: 'Download as..',
                            execute: function(type) {
                                var svg = document.getElementsByTagName('svg');
                                
                                if (svg.length < 1) {
                                    alert('Please use Chrome, Firefox, Opera or Safari to export images');
                                    return;
                                }
                                
                                document.getElementById('titleField').value = DV.state.filter.names[0] || 'Example chart';
                                document.getElementById('svgField').value = svg[0].parentNode.innerHTML;
                                document.getElementById('typeField').value = type;
                                
                                var exportForm = document.getElementById('exportForm');
                                exportForm.action = '../exportImage.action';
                                
                                if (svg[0].parentNode.innerHTML && type) {
                                    exportForm.submit();
                                }
                                else {
                                    alert("No svg/format");
                                }
                            },
                            listeners: {
                                afterrender: function(b) {
                                    this.menu = Ext.create('Ext.menu.Menu', {
                                        shadowOffset: 1,
                                        showSeparator: false,
                                        items: [
                                            {
                                                text: 'Image (PNG)',
                                                iconCls: 'dv-menu-item-png',
                                                minWidth: 105,
                                                handler: function() {
                                                    b.execute(DV.conf.finals.image.png);
                                                }
                                            },
                                            {
                                                text: 'PDF',
                                                iconCls: 'dv-menu-item-pdf',
                                                minWidth: 105,
                                                handler: function() {
                                                    b.execute(DV.conf.finals.image.pdf);
                                                }
                                            }
                                        ]                                            
                                    });
                                }
                            }
                        },
                        '->',
                        {
                            xtype: 'button',
							cls: 'dv-toolbar-btn-2',
                            text: 'Exit',
                            handler: function() {
                                window.location.href = DV.conf.finals.ajax.path_portal + DV.conf.finals.ajax.redirect;
                            }
                        },
                        {
                            xtype: 'button',
                            name: 'resizeeast',
							cls: 'dv-toolbar-btn-2',
                            text: '>>>',
                            tooltip: 'Hide data table',
                            hidden: true,
                            handler: function() {
                                DV.cmp.region.east.collapse();
                                this.hide();
                            },
                            listeners: {
                                added: function() {
                                    DV.cmp.toolbar.resizeeast = this;
                                }
                            }
                        }
                    
                    ]
                },
                listeners: {
                    added: function() {
                        DV.cmp.region.center = this;
                    }
                }
            },
            {
                region: 'east',
                preventHeader: true,
                collapsible: true,
                collapsed: true,
                collapseMode: 'mini',
                width: 498,
                listeners: {
                    afterrender: function() {
                        DV.cmp.region.east = this;
                    }
                }
            }
        ],
        listeners: {
            afterrender: function() {
                DV.init.initialize();
            },
            resize: function(vp) {
                DV.cmp.region.west.setWidth(DV.conf.layout.west_width);
                
                if (DV.datatable.datatable) {
                    DV.datatable.datatable.setHeight(DV.util.viewport.getSize().y - DV.conf.layout.east_tbar_height);
                }
            }
        }
    });
    
    }});
});
