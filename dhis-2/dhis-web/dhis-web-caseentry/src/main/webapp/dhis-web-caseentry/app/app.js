TR.conf = {
    init: {
		ajax: {
			jsonfy: function(r) {
				r = Ext.JSON.decode(r.responseText);
				var obj = {system: {rootnode: {id: r.rn[0], name: r.rn[1], level: 1}, user: {id: r.user.id, isadmin: r.user.isAdmin, organisationunit: {id: r.user.ou[0], name: r.user.ou[1]}}}};
				for (var program in r.programs) {
					obj.system.program = [];
					for (var i = 0; i < r.programs.length; i++) {
						obj.system.program.push({id: r.programs[i].id, name: r.programs[i].name, anonymous: r.programs[i].anonymous });
					}
				}
				
				for (var level in r.levels) {
					obj.system.level = [];
					for (var i = 0; i < r.levels.length; i++) {
						obj.system.level.push({value: r.levels[i].value, name: r.levels[i].name});
					}
				}
				
				return obj;
			}
		}
    },
    finals: {
        ajax: {
			path_lib: '../../dhis-web-commons/javascripts/',
            path_root: '../',
            path_commons: '../',
            path_api: '../../api/',
            path_images: 'images/',
			initialize: 'tabularInitialize.action',
			program_get: 'getReportPrograms.action',
			identifiertypes_get: 'loadReportIdentifierTypes.action',
			patientattributes_get: 'loadReportAttributes.action',
			programstages_get: 'loadReportProgramStages.action',
			dataelements_get: 'loadDataElements.action',
			organisationunitchildren_get: 'getOrganisationUnitChildren.action',
			generatetabularreport_get: 'generateTabularReport.action',
            redirect: 'index.action'
        },
        params: {
            data: {
                value: 'data',
                rawvalue: TR.i18n.regular_program,
                warning: {
					filter: TR.i18n.wm_multiple_filter_ind_de
				}
            },
            program: {
                value: 'program',
                rawvalue: TR.i18n.program
            },
            organisationunit: {
                value: 'organisationunit',
                rawvalue: TR.i18n.organisation_unit,
                warning: {
					filter: TR.i18n.wm_multiple_filter_orgunit
				}
            },
            identifierType: {
                value: 'identifierType',
                rawvalue: TR.i18n.identifiers
            },
            patientAttribute: {
                value: 'patientAttribute',
                rawvalue: TR.i18n.attributes
            },
            programStage: {
                value: 'programStage',
                rawvalue: TR.i18n.program_stage
            },
            dataelement: {
                value: 'dataelement',
                rawvalue: TR.i18n.data_elements
            }
        },
        data: {
			domain: 'domain_',
		},
		image: {
            xls: 'xls',
            pdf: 'pdf'
        },
        cmd: {
            init: 'init_',
            none: 'none_',
			urlparam: 'id'
        }
    },
    statusbar: {
		icon: {
			error: 'error_s.png',
			warning: 'warning.png',
			ok: 'ok.png'
		}
	},
    layout: {
        west_width: 424,
        west_fieldset_width: 402,
        west_width_subtractor: 18,
        west_fill: 117,
        west_fill_accordion_organisationunit: 50,
        west_maxheight_accordion_organisationunit: 225,
        center_tbar_height: 31,
        east_gridcolumn_height: 30,
        form_label_width: 90
    }
};

Ext.Loader.setConfig({enabled: true});
Ext.Loader.setPath('Ext.ux', TR.conf.finals.ajax.path_lib + 'ext-ux');
Ext.require('Ext.ux.form.MultiSelect');

Ext.onReady( function() {
    Ext.override(Ext.form.FieldSet,{setExpanded:function(a){var b=this,c=b.checkboxCmp,d=b.toggleCmp,e;a=!!a;if(c){c.setValue(a)}if(d){d.setType(a?"up":"down")}if(a){e="expand";b.removeCls(b.baseCls+"-collapsed")}else{e="collapse";b.addCls(b.baseCls+"-collapsed")}b.collapsed=!a;b.doComponentLayout();b.fireEvent(e,b);return b}});
    Ext.QuickTips.init();
    document.body.oncontextmenu = function(){return false;}; 
    
    Ext.Ajax.request({
        url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.initialize,
        success: function(r) {
            
    TR.init = TR.conf.init.ajax.jsonfy(r);    
    TR.init.initialize = function() {        
        TR.init.cmd = TR.util.getUrlParam(TR.conf.finals.cmd.urlparam) || TR.conf.finals.cmd.init;
    };
    
    TR.cmp = {
        region: {},
        settings: {},
        params: {
            program:{},
			identifierType: {},
			patientAttribute: {},
			programStage: {},
			dataelement: {},
			organisationunit: {},
			fixedAttributes:{
				checkbox: []
			}
        },
        options: {},
        toolbar: {
            menuitem: {}
        },
        statusbar: {}
    };
    
    TR.util = {
        getUrlParam: function(s) {
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
        },
        viewport: {
            getSize: function() {
                return {x: TR.cmp.region.center.getWidth(), y: TR.cmp.region.center.getHeight()};
            },
            getXY: function() {
                return {x: TR.cmp.region.center.x + 15, y: TR.cmp.region.center.y + 43};
            },
            getPageCenterX: function(cmp) {
                return ((screen.width/2)-(cmp.width/2));
            },
            getPageCenterY: function(cmp) {
                return ((screen.height/2)-((cmp.height/2)-100));
            },
            resizeParams: function() {
				var a = [TR.cmp.params.identifierType.panel, TR.cmp.params.patientAttribute.panel, 
						 TR.cmp.params.dataelement.panel, TR.cmp.params.organisationunit.treepanel];
				for (var i = 0; i < a.length; i++) {
					if (!a[i].collapsed) {
						a[i].fireEvent('expand');
					}
				}
			}
        },
        multiselect: {
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
				var elements = a.boundList.all.elements;
				for( var i=0; i< elements.length; i++ )
				{
					if( elements[i].style.display != 'none' )
					{
						array.push({id: a.store.getAt(i).data.id, name: a.store.getAt(i).data.name});
					}
				}
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
                var elements = s.boundList.all.elements;
				var index = 0;
				var arr = [];
				Ext.Array.each(s.store.data.items, function(item) {
					if( elements[index].style.display != 'none' )
					{
					  arr.push( item.data.id );
					}
					index++;
				}); 
				s.setValue(arr);
				this.unselect(a,s);
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
                a.store.sort('name', 'ASC');
            },
			filterSelector: function(selectors, queryString) {
                var elements = selectors.boundList.all.elements;

				for( var i=0; i< elements.length; i++ )
				{
					if( elements[i].innerHTML.toLowerCase().indexOf( queryString ) != -1 )
					{
						elements[i].style.display = 'block';
					}
					else
					{
						elements[i].style.display = 'none';
					}
				}
            },
            setHeight: function(ms, panel, fill) {
				for (var i = 0; i < ms.length; i++) {
					ms[i].setHeight(panel.getHeight() - 45);
				}
			}
        },
        store: {
            addToStorage: function(s, records) {
                s.each( function(r) {
                    if (!s.storage[r.data.id]) {
                        s.storage[r.data.id] = {id: r.data.id, name: TR.util.string.getEncodedString(r.data.name), parent: s.parent};
                    }
                });
                if (records) {
                    Ext.Array.each(records, function(r) {
                        if (!s.storage[r.data.id]) {
                            s.storage[r.data.id] = {id: r.data.id, name: TR.util.string.getEncodedString(r.data.name), parent: s.parent};
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
        },
        notification: {
			error: function(title, text) {
				title = title || '';
				text = text || '';
				Ext.create('Ext.window.Window', {
					title: title,
					cls: 'tr-messagebox',
					iconCls: 'tr-window-title-messagebox',
					modal: true,
					width: 300,
					items: [
						{
							xtype: 'label',
							width: 40,
							text: text
						}
					]
				}).show();
				TR.cmp.statusbar.panel.setWidth(TR.cmp.region.center.getWidth());
				TR.cmp.statusbar.panel.update('<img src="' + TR.conf.finals.ajax.path_images + TR.conf.statusbar.icon.error + '" style="padding:0 5px 0 0"/>' + text);
			},
			warning: function(text) {
				text = text || '';
				TR.cmp.statusbar.panel.setWidth(TR.cmp.region.center.getWidth());
				TR.cmp.statusbar.panel.update('<img src="' + TR.conf.finals.ajax.path_images + TR.conf.statusbar.icon.warning + '" style="padding:0 5px 0 0"/>' + text);
			},
			ok: function() {
				TR.cmp.statusbar.panel.setWidth(TR.cmp.region.center.getWidth());
				TR.cmp.statusbar.panel.update('<img src="' + TR.conf.finals.ajax.path_images + TR.conf.statusbar.icon.ok + '" style="padding:0 5px 0 0"/>&nbsp;&nbsp;');
			}				
		},
        mask: {
            showMask: function(cmp, str) {
                if (TR.mask) {
                    TR.mask.destroy();
                }
                TR.mask = new Ext.LoadMask(cmp, {msg: str});
                TR.mask.show();
            },
            hideMask: function() {
				if (TR.mask) {
					TR.mask.hide();
				}
			}
        },
		/*FIXME:This is probably not going to work as intended with UNICODE?*/
        string: {
            getEncodedString: function(text) {
                return text.replace(/[^a-zA-Z 0-9(){}<>_!+;:?*&%#-]+/g,'');
            }
        },
        getValueFormula: function( value )
		{
			if( value.indexOf('"') != value.lastIndexOf('"') )
			{
				value = value.replace(/"/g,"'");
			}
			// if key is [xyz] && [=xyz]
			if( value.indexOf("'")==-1 ){
				var flag = value.match(/[>|>=|<|<=|=|!=]+[ ]*/);
			
				if( flag == null )
				{
					value = "='"+ value + "'";
				}
				else
				{
					value = value.replace( flag, flag + "'");
					value +=  "'";
				}
			}
			// if key is ['xyz'] && [='xyz']
			// if( value.indexOf("'") != value.lastIndexOf("'") )
			else
			{
				var flag = value.match(/[>|>=|<|<=|=|!=]+[ ]*/);
			
				if( flag == null )
				{
					value = "="+ value;
				}
			}
			
			return value;
		},
		setEnabledFixedAttr: function()
		{
			var fixedAttributes = TR.cmp.params.fixedAttributes.checkbox;
			Ext.Array.each(fixedAttributes, function(item) {
				item.enable();
			});
		},
		setDisabledFixedAttr: function()
		{
			var fixedAttributes = TR.cmp.params.fixedAttributes.checkbox;
			Ext.Array.each(fixedAttributes, function(item) {
				item.setValue(false);
				item.disable();
			});
		},
		getSelectedFixedAttr: function()
		{
			var p = [];
			var fixedAttributes = TR.cmp.params.fixedAttributes.checkbox;
			Ext.Array.each(fixedAttributes, function(item) {
				if( item.value )
				{
					p.fixedAttributes.push( item.paramName );
				}
			});
			return p;
		},
	};
    
    TR.store = {
        program: Ext.create('Ext.data.Store', {
                fields: ['id', 'name', 'anonymous'],
				data:TR.init.system.program
            }),
		identifierType: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: TR.conf.finals.ajax.path_commons + TR.conf.finals.ajax.identifiertypes_get,
                    reader: {
                        type: 'json',
                        root: 'identifierTypes'
                    }
                },
				isloaded: false,
                storage: {},
                listeners: {
                    load: function(s) {
						this.isloaded = true;
                        TR.util.store.addToStorage(s);
                        TR.util.multiselect.filterAvailable(TR.cmp.params.identifierType.available, TR.cmp.params.identifierType.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: []
            })
        },
		patientAttribute: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: TR.conf.finals.ajax.path_commons + TR.conf.finals.ajax.patientattributes_get,
                    reader: {
                        type: 'json',
                        root: 'patientAttributes'
                    }
                },
				isloaded: false,
                storage: {},
                listeners: {
                    load: function(s) {
						this.isloaded = true;
                        TR.util.store.addToStorage(s);
                        TR.util.multiselect.filterAvailable(TR.cmp.params.identifierType.available, TR.cmp.params.identifierType.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: []
            })
        },
		programStage: Ext.create('Ext.data.Store', {
			fields: ['id', 'name'],
			proxy: {
				type: 'ajax',
				url: TR.conf.finals.ajax.path_commons + TR.conf.finals.ajax.programstages_get,
				reader: {
					type: 'json',
					root: 'programStages'
				}
			},
			listeners:{
				load: function(s) {
					Ext.override(Ext.LoadMask, {
						 onHide: function() {
							  this.callParent();
						 }
					});
				}
			}
		}),
		dataelement: {
            available: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                proxy: {
                    type: 'ajax',
                    url: TR.conf.finals.ajax.path_commons + TR.conf.finals.ajax.dataelements_get,
                    reader: {
                        type: 'json',
                        root: 'dataElements'
                    }
                },
                isloaded: false,
                storage: {},
                listeners: {
                    load: function(s) {
						this.isloaded = true;
                        TR.util.store.addToStorage(s);
                        TR.util.multiselect.filterAvailable(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
                    }
                }
            }),
            selected: Ext.create('Ext.data.Store', {
                fields: ['id', 'name'],
                data: []
            })
        },
        datatable: null,
        getDataTableStore: function() {
			
			this.datatable = Ext.create('Ext.data.Store', {
				fields: TR.value.fields,
				data: TR.value.values,
				remoteSort:true,
				autoLoad: false,
				proxy: {
					type: 'memory',
					reader: {
						type: 'json',
						root: 'items'
					}
				},
				storage: {}
			});
        }
	}
    
    TR.state = {
        currentPage: 1,
		total: 1,
		orderByOrgunitAsc: true,
		orderByExecutionDateByAsc: true,
		generateReport: function( type ) {
			// Validation
			if( !this.validation.objects() )
			{
				return;
			}
			// Get url
			var url = TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.generatetabularreport_get;
			// Export to XLS or PDF
			if( type)
			{
				window.location.href = url + "?" + this.getURLParams(type);
			}
			// Show report on grid
			else
			{
				TR.util.mask.showMask(TR.cmp.region.center, TR.i18n.loading);
			
				Ext.Ajax.request({
					url: url,
					method: "POST",
					scope: this,
					params: this.getParams(),
					success: function(r) {
						var json = Ext.JSON.decode(r.responseText);
						TR.state.total = json.total;
						TR.value.valueTypes = json.valueTypes;
						TR.value.fields = json.fields;
						TR.value.hidden= json.hidden;
						TR.value.columns = json.columns;
						TR.value.values = json.items;
						
						if ( json.items.length > 1 )
						{
							TR.store.getDataTableStore();
							TR.datatable.getDataTable();
							TR.datatable.setPagingToolbarStatus();
							
							Ext.getCmp('btnReset').enable();
							Ext.getCmp('btnFilter').enable();
							Ext.getCmp('btnClean').enable();
							Ext.getCmp('btnSortBy').enable();
							
							TR.util.mask.hideMask();
						}
						else
						{
							TR.util.mask.hideMask();
							TR.util.notification.error(TR.i18n.et_no_data, TR.i18n.et_no_data);
						}
					}
				});
			}
			TR.util.notification.ok();
		},
		getParams: function() {
			var p = {};
            p.startDate = TR.cmp.settings.startDate.rawValue;
            p.endDate = TR.cmp.settings.endDate.rawValue;
			p.facilityLB = TR.cmp.settings.facilityLB.getValue();
			p.level = TR.cmp.settings.level.getValue();
			// organisation unit
			p.orgunitId = TR.cmp.params.organisationunit.treepanel.getSelectionModel().getSelection()[0].data.id
			p.orderByOrgunitAsc = this.orderByOrgunitAsc;
			p.orderByExecutionDateByAsc= this.orderByExecutionDateByAsc;
			
			p.programStageId = TR.cmp.params.programStage.getValue();
			p.currentPage = this.currentPage;
			
			// Get fixed attributes
			p.fixedAttributes = TR.util.getSelectedFixedAttr();
			/*var fixedAttributes = TR.cmp.params.fixedAttributes.checkbox;
			Ext.Array.each(fixedAttributes, function(item) {
				if( item.value )
					p.fixedAttributes.push( item.paramName );
			});*/
			
			// Get searching values
			p.searchingValues = [];
			if( TR.store.datatable && TR.store.datatable.data.length)
			{
				var grid = TR.datatable.datatable;
				var cols = grid.columns;
				var editor = grid.getStore().getAt(0);
				var colLen = cols.length;
				for( var i=0; i<colLen; i++ )
				{
					var col = cols[i];	
					var dataIndex = col.dataIndex;
					
					if( dataIndex== 'col1' ) 
					{
						p.searchingValues.push( col.name + col.hidden + "_" );
					}
					else if( col.name )
					{
						var value = editor.data[dataIndex];
						if( value!=null && value!= '')
						{
							value = TR.util.getValueFormula(value);
						}
						p.searchingValues.push( col.name + col.hidden + "_" + value );
					}
				}
			}
			else
			{
				// Identifier Types
				TR.cmp.params.identifierType.selected.store.each( function(r) {
					p.searchingValues.push( 'iden_' + r.data.id + '_false_' );
				});
				// Patient Attributes
				TR.cmp.params.patientAttribute.selected.store.each( function(r) {
					p.searchingValues.push( 'attr_' + r.data.id + '_false_' );
				});
				// Data elements
				TR.cmp.params.dataelement.selected.store.each( function(r) {
					p.searchingValues.push( 'de_' + r.data.id +  '_false_' );
				});
			}
		
            return p;
        },
		getURLParams: function( type ) {
            var p = "";
            p += "startDate=" + TR.cmp.settings.startDate.rawValue;
            p += "&endDate=" + TR.cmp.settings.endDate.rawValue;
			p += "&facilityLB=" + TR.cmp.settings.facilityLB.getValue();
			p += "&level=" + TR.cmp.settings.level.getValue();
			p += "&orgunitId=" + TR.cmp.params.organisationunit.treepanel.getSelectionModel().getSelection()[0].data.id
			p += "&orderByOrgunitAsc=" + 'true';
			p += "&orderByExecutionDateByAsc=" +'true';
			p += "&programStageId=" + TR.cmp.params.programStage.getValue();
			p += "&type=" + type;
			
			// Get fixed attributes
			var fixedAttributes = TR.cmp.params.fixedAttributes.checkbox;
			Ext.Array.each(fixedAttributes, function(item) {
				if( item.value )
					p+="&fixedAttributes=" + item.paramName;
			});
			
			if( TR.store.datatable && TR.store.datatable.data.length)
			{
				var grid = TR.datatable.datatable;
				var cols = grid.columns;
				var editor = grid.getStore().getAt(0);
				var colLen = cols.length;
				for( var i=0; i<colLen; i++ )
				{
					var col = cols[i];	
					if( col.name )
					{
						var dataIndex = col.dataIndex;
						var value = editor.data[dataIndex];
						if( value!=null && value!= '')
						{
							value = TR.util.getValueFormula(value);
						}
						p += "&searchingValues=" +  col.name + col.hidden + "_" + value;
					}
				}
			}
			else
			{
				// Identifier Types
				TR.cmp.params.identifierType.selected.store.each( function(r) {
					p += "&searchingValues=" + 'iden_' + r.data.id + '_false_';
				});
				// Patient Attributes
				TR.cmp.params.patientAttribute.selected.store.each( function(r) {
					p += "&searchingValues=" +'attr_' + r.data.id + '_false_';
				});
				// Data elements
				TR.cmp.params.dataelement.selected.store.each( function(r) {
					p += "&searchingValues=" + 'de_' + r.data.id + '_false_';
				});
			}
            return p;
        },
		validation: {
			params: function() {
				if (!TR.c.params.program ) {
					TR.util.notification.error(TR.i18n.et_no_programs, TR.i18n.et_no_programs);
					return false;
				}
				return true;
			},
			objects: function() {
				
				if (TR.cmp.settings.program.getValue() == null) {
					TR.util.notification.error(TR.i18n.et_no_programs, TR.i18n.et_no_programs);
					return false;
				}
				
				if (!TR.cmp.params.organisationunit.treepanel.getSelectionModel().getSelection().length) {
					TR.util.notification.error(TR.i18n.et_no_orgunits, TR.i18n.em_no_orgunits);
					return false;
				}
				
				if (!TR.cmp.params.dataelement.selected.store.data.length) {
					TR.util.notification.error(TR.i18n.et_no_dataelement, TR.i18n.et_no_dataelement);
					return false;
				}
				
				return true;
			},
			
			render: function() {
				if (!TR.c.isrendered) {
					TR.cmp.toolbar.datatable.enable();
					TR.c.isrendered = true;
				}
			},
			response: function(r) {
				if (!r.responseText) {
					TR.util.mask.hideMask();
					TR.util.notification.error(TR.i18n.et_invalid_uid, TR.i18n.em_invalid_uid);
					return false;
				}
				return true;
			},
			value: function() {
				if (!TR.value.values.length) {
					TR.util.mask.hideMask();
					TR.util.notification.error(TR.i18n.et_no_data, TR.i18n.em_no_data);
					return false;
				}
				return true;
			}
		}
    };
    
    TR.value = {
		valueTypes: [],
		columns: [],
		fields: [],
		hidden: [],
		values: [],
		getValueType: function( index )
		{
			if( TR.value.valueTypes[index] == null )
			{
				return 'textfield';
			}
			
			return TR.value.valueTypes[index].valueType;
		},
		getSuggestedValues: function( index )
		{
			if( TR.value.valueTypes[index] == null )
			{
				return [];
			}
			
			return TR.value.valueTypes[index].suggestedValues;
		}
    };
      
    TR.datatable = {
        datatable: null,
		rowEditing: null,
		getDataTable: function() {
			
			var index = 1;
			
			var paramsLen = TR.cmp.params.identifierType.selected.store.data.length
						+ TR.cmp.params.patientAttribute.selected.store.data.length
						+ TR.cmp.params.dataelement.selected.store.data.length;
			var metaDatatColsLen = TR.value.columns.length - paramsLen ;
			
			// column
			var cols = [];
			cols[0] = {
				header: TR.i18n.no, 
				dataIndex: 'id',
				width: 50,
				height: TR.conf.layout.east_gridcolumn_height,
				sortable: false,
				draggable: false,
				hideable: false,
				menuDisabled: true
			};
			
			cols[1] = {
				header: TR.value.columns[1], 
				dataIndex: 'col1',
				height: TR.conf.layout.east_gridcolumn_height,
				name:"meta_1_",
				sortable: false,
				draggable: false,
				hideable: false
			};
				
			index = 2;
			for( index=2; index < metaDatatColsLen; index++ )
			{
				cols[index] = {
					header: TR.value.columns[index], 
					dataIndex: 'col' + index,
					height: TR.conf.layout.east_gridcolumn_height,
					name:"meta_" + index + "_",
					sortable: false,
					draggable: false,
					hidden: eval(TR.value.hidden[index])
				}
			}
			
			TR.cmp.params.identifierType.selected.store.each( function(r) {
				var dataIndex = "col" + index;
				cols[index] = { 
					header: r.data.name, 
					dataIndex: dataIndex,
					height: TR.conf.layout.east_gridcolumn_height,
					name: "iden_"+ r.data.id + "_",
					hidden: eval(TR.value.hidden[index]),
					sortable: false,
					draggable: true,
					editor: {
						xtype: 'textfield',
						allowBlank: true
					}
				};
				index++;
			});
			
			TR.cmp.params.patientAttribute.selected.store.each( function(r) {
				var dataIndex = "col" + index;
				cols[index] = { 
					header: r.data.name, 
					dataIndex: dataIndex,
					height: TR.conf.layout.east_gridcolumn_height,
					name: "attr_"+ r.data.id + "_",
					hidden: eval(TR.value.hidden[index]),
					flex:1,
					sortable: false,
					draggable: true,
					emptyText: TR.i18n.et_no_data,
					editor: {
							xtype: TR.value.getValueType(index),
							queryMode: 'local',
							editable: true,
							valueField: 'name',
							displayField: 'name',
							allowBlank: true,
							store:  new Ext.data.ArrayStore({
								fields: ['name'],
								data: TR.value.getSuggestedValues(index),
							})
						}
					};
				index++;
			});
			
			TR.cmp.params.dataelement.selected.store.each( function(r) {
				var dataIndex = "col" + index;
				cols[index] = { 
					header: r.data.name, 
					dataIndex: dataIndex,
					height: TR.conf.layout.east_gridcolumn_height,
					name: "de_"+ r.data.id + "_",
					hidden: eval(TR.value.hidden[index]),
					flex:1,
					sortable: false,
					draggable: true,
					editor: {
						xtype: TR.value.getValueType(index),
							queryMode: 'local',
							editable: true,
							valueField: 'name',
							displayField: 'name',
							allowBlank: true,
							store: new Ext.data.ArrayStore({
								fields: ['name'],
								data: TR.value.getSuggestedValues(index),
							})
					}
				};
				index++;
			});
			
			this.rowEditing = Ext.create('Ext.grid.plugin.RowEditing', {
				clicksToEdit: 1,
				editStyle: 'row',
				autoScroll: true,
				errorSummary: false,
				listeners: {
					beforeedit: function( editor, e) 
					{
						if( editor.rowIdx > 0 )
						{
							return false;
						}
					},
					edit: function( editor, e ){
						TR.exe.execute();
					},
					canceledit: function( grid, eOpts ){
						var grid = TR.datatable.datatable;
						grid.getView().getNode(0).classList.add('hidden');
					}
				}
			});
	
			// grid
			this.datatable = Ext.create('Ext.grid.Panel', {
                height: TR.util.viewport.getSize().y - 58,
				id: 'gridTable',
				columns: cols,
				scroll: 'both',
				title: TR.cmp.settings.program.rawValue + " - " + TR.cmp.params.programStage.rawValue + " " + TR.i18n.report,
				viewConfig: {
					getRowClass: function(record, rowIndex, rp, ds){ 
						if(rowIndex == 0){
							return 'filter-row hidden';
						} else {
						   return '';
						}
					}
				},
				bbar: [
					{
						xtype: 'button',
						icon: 'images/arrowleftdouble.png',
						id:'firstPageBtn',
						width: 22,
						handler: function() {
							TR.exe.paging(1);
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowleft.png',
						id:'previousPageBtn',
						width: 22,
						handler: function() {
							TR.exe.paging( eval(TR.cmp.settings.currentPage.rawValue) - 1 );
						}
					},
					{
						xtype: 'label',
						text: '|'
					},
					{
						xtype: 'label',
						text: TR.i18n.page
					},
					{
						xtype: 'textfield',
						cls: 'tr-textfield-alt1',
						id:'currentPage',
						value: TR.state.currentPage,
						listeners: {
							added: function() {
								TR.cmp.settings.currentPage = this;
							},						
							specialkey: function( textfield, e, eOpts ){
								
								if (e.keyCode == e.ENTER)
								{
									var oldValue = TR.state.currentPage;
									var newValue = textfield.rawValue;
									if( newValue < 1 || newValue > TR.state.total )
									{
										textfield.setValue(oldValue);
									}
									else
									{
										TR.exe.paging( newValue );
									}
								}
							}
						},
					},
					{
						xtype: 'label',
						text: ' of ' + TR.state.total + ' | '
					},
					{
						xtype: 'button',
						icon: 'images/arrowright.png',
						id:'nextPageBtn',
						handler: function() {
							TR.exe.paging( eval(TR.cmp.settings.currentPage.rawValue) + 1 );
						}
					},
					{
						xtype: 'button',
						icon: 'images/arrowrightdouble.png',
						id:'lastPageBtn',
						handler: function() {
							TR.exe.paging( TR.state.total );
						}
					},
					{
						xtype: 'label',
						text: '|'
					},
					{
						xtype: 'button',
						icon: 'images/refresh.png',
						handler: function() {
							TR.exe.paging( TR.cmp.settings.currentPage.rawValue );
						}
					}
				], 
				plugins: [this.rowEditing],
				store: TR.store.datatable
			});
										
			if (Ext.grid.RowEditor) {
				Ext.apply(Ext.grid.RowEditor.prototype, {
					saveBtnText : TR.i18n.filter,
					cancelBtnText : TR.i18n.cancel
				});
			}
			
			Ext.override(Ext.grid.header.Container, { 
				sortAscText: TR.i18n.asc,
				sortDescText: TR.i18n.desc, 
				columnsText: TR.i18n.show_hide_columns });

			TR.cmp.region.center.removeAll(true);
			TR.cmp.region.center.add(this.datatable);		
          	
            return this.datatable;
            
        },
        setPagingToolbarStatus: function() {
			if( TR.state.currentPage == TR.state.total 
				&& TR.state.total== 1 )
			{
				Ext.getCmp('firstPageBtn').disable();
				Ext.getCmp('previousPageBtn').disable();
				Ext.getCmp('nextPageBtn').disable();
				Ext.getCmp('lastPageBtn').disable();
			}
			else if( TR.state.currentPage == TR.state.total )
			{
				Ext.getCmp('firstPageBtn').enable();
				Ext.getCmp('previousPageBtn').enable();
				Ext.getCmp('nextPageBtn').disable();
				Ext.getCmp('lastPageBtn').disable();
			}
			else if( TR.state.currentPage == 1 )
			{
				Ext.getCmp('firstPageBtn').disable();
				Ext.getCmp('previousPageBtn').disable();
				Ext.getCmp('nextPageBtn').enable();
				Ext.getCmp('lastPageBtn').enable();
			}
			else
			{
				Ext.getCmp('firstPageBtn').enable();
				Ext.getCmp('previousPageBtn').enable();
				Ext.getCmp('nextPageBtn').enable();
				Ext.getCmp('lastPageBtn').enable();
			} 
        }           
    };
        
	TR.exe = {
		execute: function( type ) {
			TR.state.generateReport(type);
		},
		paging: function( currentPage )
		{
			TR.state.currentPage = currentPage;
			TR.exe.execute();
			Ext.getCmp('currentPage').setValue( currentPage );	
			TR.datatable.setPagingToolbarStatus();
		},
		reset: function() {
			TR.store.datatable.loadData([],false);
			this.execute();
		},
		datatable: function() {
			TR.store.getDataTableStore();
			TR.datatable.getDataTable();
			TR.datatable.setPagingToolbarStatus();
		}
    };
	
    TR.viewport = Ext.create('Ext.container.Viewport', {
        layout: 'border',
        renderTo: Ext.getBody(),
        isrendered: false,
        items: [
            {
                region: 'west',
                preventHeader: true,
                collapsible: true,
                collapseMode: 'mini',
                items: [
				{
					xtype: 'toolbar',
					style: 'padding-top:1px; border-style:none',
					items: [
						{
							xtype: 'panel',
							bodyStyle: 'border-style:none; background-color:transparent; padding:4px 0 0 8px',
                            items: [
                            {
								xtype: 'label',
								text: TR.i18n.programs,
								style: 'font-size:11px; font-weight:bold; padding:0 0 0 3px'
							},
							{ bodyStyle: 'padding:1px 0; border-style:none;	background-color:transparent' },
							{
								xtype: 'combobox',
								cls: 'tr-combo',
								name: TR.init.system.programs,
								emptyText: TR.i18n.please_select,
								queryMode: 'local',
								editable: false,
								valueField: 'id',
								displayField: 'name',
								width: TR.conf.layout.west_fieldset_width,
								store: TR.store.program,
								listeners: {
									added: function() {
										TR.cmp.settings.program = this;
									},
									select: function(cb) {
										var anonymous = cb.displayTplData[0].anonymous;
										if( anonymous=='false' )
										{
											// IDENTIFIER TYPE
											var storeIdentifierType = TR.store.identifierType.available;
											TR.store.identifierType.selected.loadData([],false);
											storeIdentifierType.parent = cb.getValue();
											
											if (TR.util.store.containsParent(storeIdentifierType)) {
												TR.util.store.loadFromStorage(storeIdentifierType);
												TR.util.multiselect.filterAvailable(TR.cmp.params.identifierType.available, TR.cmp.params.identifierType.selected);
											}
											else {
												storeIdentifierType.load({params: {programId: cb.getValue()}});
											}
											
											// PATIENT ATTRIBUTE
											var storePatientAttribute = TR.store.patientAttribute.available;
											storePatientAttribute.parent = cb.getValue();
											TR.store.patientAttribute.selected.loadData([],false);
											
											if (TR.util.store.containsParent(storePatientAttribute)) {
												TR.util.store.loadFromStorage(storePatientAttribute);
												TR.util.multiselect.filterAvailable(TR.cmp.params.patientAttribute.available, TR.cmp.params.patientAttribute.selected);
											}
											else {
												storePatientAttribute.load({params: {programId: cb.getValue()}});
											}
											TR.util.setEnabledFixedAttr();
										}
										else
										{
											TR.util.setDisabledFixedAttr();
											
											TR.store.identifierType.available.loadData([],false);
											TR.store.identifierType.selected.loadData([],false);
											
											TR.store.patientAttribute.available.loadData([],false);
											TR.store.patientAttribute.selected.loadData([],false);
										}
										
										// PROGRAM-STAGE										
										var storeProgramStage = TR.store.programStage;
										TR.store.dataelement.selected.loadData([],false);
										storeProgramStage.parent = cb.getValue();
										storeProgramStage.load({params: {programId: cb.getValue()}});
									}
								}
							},
							{
								xtype: 'panel',
								bodyStyle: 'border-style:none; background-color:transparent; padding:3px 0 0 0',
                                layout: 'column',
								items: [
                                    {
										xtype: 'datefield',
										cls: 'tr-textfield-alt1',
										id: 'startDate',
										fieldLabel: TR.i18n.start_date,
										labelStyle: 'padding-left:3px; font-weight:bold',
										labelAlign: 'top',
										labelSeparator: '',
										editable: false,
										style: 'margin-right:8px',
										width: TR.conf.layout.west_fieldset_width / 2 - 4,
										format: TR.i18n.format_date,
										value: new Date((new Date()).setMonth((new Date()).getMonth()-3)),
										listeners: {
											added: function() {
												TR.cmp.settings.startDate = this;
											}
										}
									},
									{
										xtype: 'datefield',
										cls: 'tr-textfield-alt1',
										id: 'endDate',
										fieldLabel: TR.i18n.end_date,
										labelStyle: 'padding-left:3px; font-weight:bold',
										labelWidth: TR.conf.layout.form_label_width,
										labelAlign: 'top',
										labelSeparator: '',
										editable: false,
										width: TR.conf.layout.west_fieldset_width / 2 - 4,
										format: TR.i18n.format_date,
										value: new Date(),
										listeners: {
											added: function() {
												TR.cmp.settings.endDate = this;
											}
										}
									}
								]
							}
							
							]
						}]
					},                            
					{
						xtype: 'panel',
                        bodyStyle: 'border-style:none; border-top:2px groove #eee; padding:10px 10px 0 10px;',
                        layout: 'fit',
                        items: [
							{
								xtype: 'panel',
								layout: 'accordion',
								activeOnTop: true,
								cls: 'tr-accordion',
								bodyStyle: 'border:0 none',
								height: 430,
								items: [
									// ORGANISATION UNIT
									{
										title: '<div style="height:17px">' + TR.i18n.organisation_units + '</div>',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'treepanel',
												cls: 'tr-tree',
												width: TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor,
												height: 273,
												autoScroll: true,
												multiSelect: false,
												isrendered: false,
												storage: {},
												addToStorage: function(objects) {
													for (var i = 0; i < objects.length; i++) {
														this.storage[objects[i].id] = objects[i];
													}
												},
												selectRoot: function() {
													if (this.isrendered) {
														if (!this.getSelectionModel().getSelection().length) {
															this.getSelectionModel().select(this.getRootNode());
														}
													}
												},
												findNameById: function(id) {
													var name = this.store.getNodeById(id) ? this.store.getNodeById(id).data.text : null;
													if (!name) {
														for (var k in this.storage) {
															if (k == id) {
																name = this.storage[k].name;
															}
														}
													}
													return name;
												},
												store: Ext.create('Ext.data.TreeStore', {
													proxy: {
														type: 'ajax',
														url: TR.conf.finals.ajax.path_root + TR.conf.finals.ajax.organisationunitchildren_get
													},
													root: {
														id: TR.init.system.rootnode.id,
														text: TR.init.system.rootnode.name,
														expanded: false
													}
												}),
												listeners: {
													added: function() {
														TR.cmp.params.organisationunit.treepanel = this;
													},
													afterrender: function( treePanel, eOpts )
													{
														treePanel.getSelectionModel().select( treePanel.getRootNode() );
													}
												}
											}
										],
										listeners: {
											added: function() {
												TR.cmp.params.organisationunit.panel = this;
											},
											expand: function() {
												TR.cmp.params.organisationunit.treepanel.setHeight(TR.cmp.params.organisationunit.panel.getHeight() - TR.conf.layout.west_fill_accordion_organisationunit);
											}
										}
									},
									
									// IDENTIFIER TYPE
									{
										title: '<div style="height:17px">' + TR.i18n.identifiers + '</div>',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none',
												items: [
													Ext.create('Ext.ux.form.MultiSelect', {
														name: 'availableIdentifierTypes',
														cls: 'tr-toolbar-multiselect-left',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2,
														displayField: 'name',
														valueField: 'id',
														queryMode: 'local',
														store: TR.store.identifierType.available,
														tbar: [
															{
																xtype: 'label',
																text: TR.i18n.available,
																cls: 'tr-toolbar-multiselect-left-label'
															},
															'->',
															{
																xtype: 'button',
																icon: 'images/arrowright.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.select(TR.cmp.params.identifierType.available, TR.cmp.params.identifierType.selected);
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowrightdouble.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.selectAll(TR.cmp.params.identifierType.available, TR.cmp.params.identifierType.selected);
																}
															},
															' '
														],
														listeners: {
															added: function() {
																TR.cmp.params.identifierType.available = this;
															},                                                                
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	TR.util.multiselect.select(this, TR.cmp.params.identifierType.selected);
																}, this);
															}
														}
													}),                                            
													{
														xtype: 'multiselect',
														name: 'selectedIdentifierTypes',
														cls: 'tr-toolbar-multiselect-right',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2,
														displayField: 'name',
														valueField: 'id',
														ddReorder: true,
														queryMode: 'local',
														store: TR.store.identifierType.selected,
														tbar: [
															' ',
															{
																xtype: 'button',
																icon: 'images/arrowleftdouble.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.unselectAll(TR.cmp.params.identifierType.available, TR.cmp.params.identifierType.selected);
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowleft.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.unselect(TR.cmp.params.identifierType.available, TR.cmp.params.identifierType.selected);
																}
															},
															'->',
															{
																xtype: 'label',
																text: TR.i18n.selected,
																cls: 'tr-toolbar-multiselect-right-label'
															}
														],
														listeners: {
															added: function() {
																TR.cmp.params.identifierType.selected = this;
															},          
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	TR.util.multiselect.unselect(TR.cmp.params.identifierType.available, this);
																}, this);
															}
														}
													}
												]
											}
										],
										listeners: {
											added: function() {
												TR.cmp.params.identifierType.panel = this;
											},
											expand: function() {
												TR.util.multiselect.setHeight(
													[TR.cmp.params.identifierType.available, TR.cmp.params.identifierType.selected],
													TR.cmp.params.identifierType.panel
												);
												
												var programId = TR.cmp.settings.program.getValue();													
												if (programId != null && !TR.store.identifierType.available.isloaded) {
													TR.store.identifierType.available.load({params: {programId: programId}});
												}
											}
										}
									},
									
									// PATIENT-ATTRIBUTE
									{
										title: '<div style="height:17px">' + TR.i18n.attributes + '</div>',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'label',
												text: TR.i18n.fixed_attributes,
												style: 'font-size:11px; font-weight:bold; color:#444; padding:0 0 0 3px'
											},
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none; padding:5px 0 10px 8px;',
												items: [
													{
														xtype: 'panel',
														layout: 'anchor',
														bodyStyle: 'border-style:none; padding:0 0 0 5px',
														defaults: {
															labelSeparator: '',
															listeners: {
																added: function(chb) {
																	if (chb.xtype === 'checkbox') {
																		TR.cmp.params.fixedAttributes.checkbox.push(chb);
																	}
																}
															}
														},
														items: [
															{
																xtype: 'checkbox',
																paramName: 'fullName',
																boxLabel: TR.i18n.full_name
															},
															{
																xtype: 'checkbox',
																paramName: 'gender',
																boxLabel: TR.i18n.gender
															},
															{
																xtype: 'checkbox',
																paramName: 'birthDate',
																boxLabel: TR.i18n.date_of_birth
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
																		TR.cmp.params.fixedAttributes.checkbox.push(chb);
																	}
																}
															}
														},
														items: [
															{
																xtype: 'checkbox',
																paramName: 'bloodGroup',
																boxLabel: TR.i18n.blood_group
															},
															{
																xtype: 'checkbox',
																paramName: 'phoneNumber',
																boxLabel: TR.i18n.phone_number
															},
															{
																xtype: 'checkbox',
																paramName: 'deathdate',
																boxLabel: TR.i18n.death_date
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
																		TR.cmp.params.fixedAttributes.checkbox.push(chb);
																	}
																}
															}
														},
														items: [
															{
																xtype: 'checkbox',
																paramName: 'registrationDate',
																boxLabel: TR.i18n.registration_date
															},
															{
																xtype: 'checkbox',
																paramName: 'dobType',
																boxLabel: TR.i18n.dob_type
															}
														]
													}
													
												]
											},											
											{
												xtype: 'label',
												text: TR.i18n.dynamic_attributes,
												style: 'font-size:11px; font-weight:bold; color:#444; padding:0 0 0 3px'
											},
											{
												xtype: 'panel',
												layout: 'column',
												bodyStyle: 'border-style:none; padding:5px 0 0 0',
												items: [
													Ext.create('Ext.ux.form.MultiSelect', {
														name: 'availablePatientAttributes',
														cls: 'tr-toolbar-multiselect-left',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2,
														height: 159,
														displayField: 'name',
														valueField: 'id',
														queryMode: 'local',
														store: TR.store.patientAttribute.available,
														tbar: [
															{
																xtype: 'label',
																text: TR.i18n.available,
																cls: 'tr-toolbar-multiselect-left-label'
															},
															'->',
															{
																xtype: 'button',
																icon: 'images/arrowright.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.select(TR.cmp.params.patientAttribute.available, TR.cmp.params.patientAttribute.selected);
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowrightdouble.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.selectAll(TR.cmp.params.patientAttribute.available, TR.cmp.params.patientAttribute.selected);
																}
															},
															' '
														],
														listeners: {
															added: function() {
																TR.cmp.params.patientAttribute.available = this;
															},                                                                
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	TR.util.multiselect.select(this, TR.cmp.params.patientAttribute.selected);
																}, this);
															}
														}
													}),                                            
													{
														xtype: 'multiselect',
														name: 'selectedPatientAttribute',
														cls: 'tr-toolbar-multiselect-right',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2,
														height: 159,
														displayField: 'name',
														valueField: 'id',
														ddReorder: true,
														queryMode: 'local',
														store: TR.store.patientAttribute.selected,
														tbar: [
															' ',
															{
																xtype: 'button',
																icon: 'images/arrowleftdouble.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.unselectAll(TR.cmp.params.patientAttribute.available, TR.cmp.params.patientAttribute.selected);
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowleft.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.unselect(TR.cmp.params.patientAttribute.available, TR.cmp.params.patientAttribute.selected);
																}
															},
															'->',
															{
																xtype: 'label',
																text: TR.i18n.selected,
																cls: 'tr-toolbar-multiselect-right-label'
															}
														],
														listeners: {
															added: function() {
																TR.cmp.params.patientAttribute.selected = this;
															},          
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	TR.util.multiselect.unselect(TR.cmp.params.patientAttribute.available, this);
																}, this);
															}
														}
													}
												]
											}
										],
										listeners: {
											added: function() {
												TR.cmp.params.patientAttribute.panel = this;
											},
											expand: function() {
												var programId = TR.cmp.settings.program.getValue();													
												if ( programId!=null && !TR.store.patientAttribute.available.isloaded ) {
													TR.store.patientAttribute.available.load({params: {programId: programId}});
												}
											}
										}
									},
									
									// DATA ELEMENTS
									{
										title: '<div style="height:17px">' + TR.i18n.data_elements + '</div>',
										hideCollapseTool: true,
										items: [
											{
												xtype: 'combobox',
												cls: 'tr-combo',
												id:'programStageCombobox',
												labelStyle: 'padding-left:7px;',
												labelWidth: 90,
												fieldLabel: TR.i18n.program_stage,
												emptyText: TR.i18n.please_select,
												queryMode: 'local',
												editable: false,
												valueField: 'id',
												displayField: 'name',
												style: 'margin-bottom:8px',
												width: TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor,
												store: TR.store.programStage,
												listeners: {
													added: function() {
														TR.cmp.params.programStage = this;
													},  
													select: function(cb) {
														var store = TR.store.dataelement.available;
														TR.store.dataelement.selected.loadData([],false);
														store.parent = cb.getValue();
														
														if (TR.util.store.containsParent(store)) {
															TR.util.store.loadFromStorage(store);
															TR.util.multiselect.filterAvailable(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
														}
														else {
															store.load({params: {programStageId: cb.getValue()}});
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
														xtype: 'toolbar',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2,
														cls: 'tr-toolbar-multiselect-left',
														items: [
															{
																xtype: 'label',	
																text: TR.i18n.available,
																cls: 'tr-toolbar-multiselect-left-label'
															},
															'->',
															{
																xtype: 'button',
																icon: 'images/arrowright.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.select(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowrightdouble.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.selectAll(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
																		TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}
															},
															''
														]
													},
													{
														xtype: 'toolbar',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2,
														cls: 'tr-toolbar-multiselect-left',
														items: [
															' ',
															{
																xtype: 'button',
																icon: 'images/arrowleftdouble.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.unselectAll(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.selected, Ext.getCmp('deFilterSelected').getValue());
																}
															},
															{
																xtype: 'button',
																icon: 'images/arrowleft.png',
																width: 22,
																handler: function() {
																	TR.util.multiselect.unselect(TR.cmp.params.dataelement.available, TR.cmp.params.dataelement.selected);
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.selected, Ext.getCmp('deFilterSelected').getValue());
																}
															},
															'->',
															{
																xtype: 'label',
																text: TR.i18n.selected,
																cls: 'tr-toolbar-multiselect-right-label'
															}
														]
													},	
													{
														xtype: 'multiselect',
														name: 'availableDataelements',
														cls: 'tr-toolbar-multiselect-left',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2,
														height: 215,
														displayField: 'name',
														valueField: 'id',
														queryMode: 'remote',
														store: TR.store.dataelement.available,
														tbar: [
															{
																xtype: 'textfield',
																emptyText: TR.i18n.filter,
																id: 'deFilterAvailable',
																width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2 - 64,
																listeners: {			
																	specialkey: function( textfield, e, eOpts ){
																		if ( e.keyCode == e.ENTER )
																		{
																			TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, textfield.rawValue.toLowerCase());	
																		}
																	}
																}
															},
															{
																xtype: 'button',
																icon: 'images/filter.png',
																tooltip: TR.i18n.filter,
																width: 24,
																handler: function() {
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}
															},
															{
																xtype: 'image',
																src: 'images/grid-split.gif'
															},
															{
																xtype: 'button',
																icon: 'images/clear-filter.png',
																tooltip: TR.i18n.clear,
																width: 24,
																handler: function() {
																	Ext.getCmp('deFilterAvailable').setValue('');
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}
															}
														],
														listeners: {
															added: function() {
																TR.cmp.params.dataelement.available = this;
															},                                                                
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	TR.util.multiselect.select(this, TR.cmp.params.dataelement.selected);
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}, this);
															}
														}
													},											
													{
														xtype: 'multiselect',
														name: 'selectedDataelements',
														cls: 'tr-toolbar-multiselect-right',
														width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2,
														height: 215,
														displayField: 'name',
														valueField: 'id',
														ddReorder: true,
														queryMode: 'remote',
														store: TR.store.dataelement.selected,
														tbar: [
															{
																xtype: 'textfield',
																emptyText: TR.i18n.filter,
																id: 'deFilterSelected',
																width: (TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor) / 2 - 64,
																listeners: {			
																	specialkey: function( textfield, e, eOpts ){
																		if ( e.keyCode == e.ENTER )
																		{
																			TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.selected, textfield.rawValue.toLowerCase());	
																		}
																	}
																}
															},
															{
																xtype: 'button',
																icon: 'images/filter.png',
																tooltip: TR.i18n.filter,
																width: 24,
																handler: function() {
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.selected, Ext.getCmp('deFilterSelected').getValue());
																}
															},
															{
																xtype: 'image',
																src: 'images/grid-split.gif'
															},
															{
																xtype: 'button',
																icon: 'images/clear-filter.png',
																tooltip: TR.i18n.clear,
																width: 24,
																handler: function() {
																	Ext.getCmp('deFilterSelected').setValue('');
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.selected, Ext.getCmp('deFilterSelected').getValue());
																}
															}
														],
														listeners: {
															added: function() {
																TR.cmp.params.dataelement.selected = this;
															},          
															afterrender: function() {
																this.boundList.on('itemdblclick', function() {
																	TR.util.multiselect.unselect(TR.cmp.params.dataelement.available, this);
																	TR.util.multiselect.filterSelector( TR.cmp.params.dataelement.available, Ext.getCmp('deFilterAvailable').getValue());
																}, this);
															}
														}
													}
												]
											}
										],
										listeners: {
											added: function() {
												TR.cmp.params.dataelement.panel = this;
											}
										}
									},
									
									// OPTIONS
									{
										title: '<div style="height:17px">' + TR.i18n.options + '</div>',
										hideCollapseTool: true,
										cls: 'tr-accordion-options',
										items: [
											{
												xtype: 'combobox',
												cls: 'tr-combo',
												id: 'facilityLBCombobox',
												fieldLabel: TR.i18n.use_data_from_level,
												labelWidth: 135,
												emptyText: TR.i18n.please_select,
												queryMode: 'local',
												editable: false,
												valueField: 'value',
												displayField: 'name',
												width: TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor,
												store:  new Ext.data.ArrayStore({
													fields: ['value', 'name'],
													data: [['all', TR.i18n.all], ['childrenOnly', TR.i18n.children_only], ['selected', TR.i18n.selected]],
												}),
												value: 'all',
												listeners: {
													added: function() {
														TR.cmp.settings.facilityLB = this;
													}
												}
											},
											{
												xtype: 'combobox',
												cls: 'tr-combo',
												id:'levelCombobox',
												fieldLabel: TR.i18n.show_hierachy_from_level,
												labelWidth: 135,
												name: TR.conf.finals.programs,
												emptyText: TR.i18n.please_select,
												queryMode: 'local',
												editable: false,
												valueField: 'value',
												displayField: 'name',
												width: TR.conf.layout.west_fieldset_width - TR.conf.layout.west_width_subtractor,
												store: Ext.create('Ext.data.Store', {
													fields: ['value', 'name'],
													data: TR.init.system.level,
												}),
												value: '1',
												listeners: {
													added: function() {
														TR.cmp.settings.level = this;
													}
												}
											}
										]
									}
								
								
								]
							}
						]
					}
					
					
				],
                listeners: {
                    added: function() {
                        TR.cmp.region.west = this;
                    },
                    collapse: function() {                    
                        this.collapsed = true;
                        TR.cmp.toolbar.resizewest.setText('>>>');
                    },
                    expand: function() {
                        this.collapsed = false;
                        TR.cmp.toolbar.resizewest.setText('<<<');
                    }
                }
            },
			// button for main form
            {
                id: 'center',
                region: 'center',
                layout: 'fit',
                bodyStyle: 'padding-top:0px, padding-bottom:0px',
                tbar: {
                    xtype: 'toolbar',
                    cls: 'tr-toolbar',
                    height: TR.conf.layout.center_tbar_height,
                    defaults: {
                        height: 26
                    },
                    items: [
                        {
                            xtype: 'button',
                            name: 'resizewest',
							cls: 'tr-toolbar-btn-2',
                            text: '<<<',
                            tooltip: TR.i18n.show_hide_settings,
                            handler: function() {
                                var p = TR.cmp.region.west;
                                if (p.collapsed) {
                                    p.expand();
                                }
                                else {
                                    p.collapse();
                                }
                            },
                            listeners: {
                                added: function() {
                                    TR.cmp.toolbar.resizewest = this;
                                }
                            }
                        },
                        {
                            xtype: 'button',
							cls: 'tr-toolbar-btn-1',
                            text: TR.i18n.update,
							handler: function() {
                                TR.exe.execute();
                            }
                        },
						{
						xtype: 'button',
						text: TR.i18n.filter,
						id: 'btnFilter',
						disabled: true,
						handler: function() {
							var grid = TR.datatable.datatable;
							var hidden = grid.getView().getNode(0).classList.contains('hidden');
							if( hidden )
							{
								grid.getView().getNode(0).classList.remove('hidden');
								var record = grid.getView().getRecord( grid.getView().getNode(0) );
								grid.getView().getSelectionModel().select(record, false, false);
								TR.datatable.rowEditing.startEdit(0, 0);
							}
							else {
								TR.exe.execute();
							}
						}
					},
					{
						xtype: 'button',
						text: TR.i18n.clear,
						id: 'btnClean',
						disabled: true,
						handler: function() {
							var grid = TR.datatable.datatable;
							var cols = grid.columns;
							var editor = grid.getStore().getAt(0);
							var colLen = cols.length;
							for( var i=1; i<colLen; i++ )
							{
								var col = cols[i];
								var dataIndex = col.dataIndex;
								TR.store.datatable.first().data[dataIndex] = "";
							}
							
							TR.exe.execute();
						}
					},
					{
						xtype: 'button',
						text: TR.i18n.sort_by,
						id: 'btnSortBy',
						disabled: true,
						execute: function() {
							TR.exe.execute();
						},
						listeners: {
							afterrender: function(b) {
								this.menu = Ext.create('Ext.menu.Menu', {
									margin: '2 0 0 0',
									shadow: false,
									showSeparator: false,
									items: [
										{
											text: TR.i18n.asc,
											iconCls: 'tr-menu-item-asc',
											minWidth: 105,
											handler: function() {
												TR.state.orderByOrgunitAsc = "true";
												b.execute();
											}
										},
										{
											text: TR.i18n.desc,
											iconCls: 'tr-menu-item-desc',
											minWidth: 105,
											handler: function() {
												TR.state.orderByOrgunitAsc = "false";
												b.execute();
											}
										}
									]                                            
								});
							}
						}
					},
					{
						xtype: 'button',
						cls: 'tr-toolbar-btn-2',
						text: TR.i18n.reset,
						id:'btnReset',
						width: 50,
						disabled: true,
						listeners: {
							click: function() {
								TR.exe.reset();
							}
						}
					},
					{
						xtype: 'button',
						text: TR.i18n.download + '..',
						execute: function(type) {
							TR.exe.execute( type );
						},
						listeners: {
							afterrender: function(b) {
								this.menu = Ext.create('Ext.menu.Menu', {
									margin: '2 0 0 0',
									shadow: false,
									showSeparator: false,
									items: [
										{
											text: TR.i18n.xls,
											iconCls: 'tr-menu-item-xls',
											minWidth: 105,
											handler: function() {
												b.execute(TR.conf.finals.image.xls);
											}
										},
										{
											text: TR.i18n.pdf,
											iconCls: 'tr-menu-item-pdf',
											minWidth: 105,
											handler: function() {
												b.execute(TR.conf.finals.image.pdf);
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
						cls: 'tr-toolbar-btn-2',
						text: 'Exit',
						handler: function() {
							window.location.href = TR.conf.finals.ajax.path_commons + TR.conf.finals.ajax.redirect;
						}
					},]
                },
                bbar: {
					items: [
						{
							xtype: 'panel',
							cls: 'tr-statusbar',
							height: 24,
							listeners: {
								added: function() {
									TR.cmp.statusbar.panel = this;
								}
							}
						}
					]
				},					
                listeners: {
                    added: function() {
                        TR.cmp.region.center = this;
                    },
                    resize: function() {
						if (TR.cmp.statusbar.panel) {
							TR.cmp.statusbar.panel.setWidth(TR.cmp.region.center.getWidth());
						}
					}
                }
            },
            {
                region: 'east',
                preventHeader: true,
                collapsible: true,
                collapsed: true,
                collapseMode: 'mini',
                listeners: {
                    afterrender: function() {
                        TR.cmp.region.east = this;
                    }
                }
            }
        ],
        listeners: {
            afterrender: function(vp) {
                TR.init.initialize(vp);
            },
            resize: function(vp) {
                TR.cmp.region.west.setWidth(TR.conf.layout.west_width);
                
				TR.util.viewport.resizeParams();
                
                if (TR.datatable.datatable) {
                    TR.datatable.datatable.setHeight( TR.util.viewport.getSize().y - 68 );
                }
            }
        }
    });
    
    }});
});
