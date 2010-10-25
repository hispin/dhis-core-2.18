/*
 * Copyright (C) 2007-2008  Camptocamp
 *
 * This file is part of MapFish Client
 *
 * MapFish Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MapFish Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MapFish Client.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @requires core/GeoStat/Choropleth.js
 * @requires core/Color.js
 */

Ext.namespace('mapfish.widgets', 'mapfish.widgets.geostat');

mapfish.widgets.geostat.Mapping = Ext.extend(Ext.FormPanel, {

    layer: null,
    
    format: null,

    url: null,

    featureSelection: true,

    nameAttribute: null,

    indicator: null,

    indicatorText: null,

    coreComp: null,

    classificationApplied: false,

    ready: false,

    border: false,

    loadMask: false,

    labelGenerator: null,
     
    newUrl: false,
	
	relation: false,
    
    mapData: false,
    
    labels: false,
    
    stores: false,
	
    initComponent : function() {
        
        var mapStore = new Ext.data.JsonStore({
            url: GLOBALS.conf.path_mapping + 'getAllMaps' + GLOBALS.conf.type,
            baseParams: { format: 'jsonmin' },
            root: 'maps',
            fields: ['id', 'name', 'mapLayerPath', 'organisationUnitLevel'],
            autoLoad: false
        });
            
        var gridStore = new Ext.data.JsonStore({
            url: GLOBALS.conf.path_mapping + 'getAvailableMapOrganisationUnitRelations' + GLOBALS.conf.type,
            root: 'mapOrganisationUnitRelations',
            fields: ['id', 'organisationUnit', 'organisationUnitId', 'featureId'],
            sortInfo: { field: 'organisationUnit', direction: 'ASC' },
			idProperty: 'organisationUnit',
            autoLoad: false
        });

        var gridView = new Ext.grid.GridView({ 
            forceFit: true,
            getRowClass: function(row,index) {
                var cls = ''; 
                switch (row.data.featureId) {
                    case '': 
                        cls = 'row-unassigned';
                        break;
                    default:
                        cls = 'row-assigned';
                }
                return cls;
            }
        });
        
        this.stores = {
            mapStore: mapStore,
            gridStore: gridStore
        };
    
        this.items =
        [
            {
                xtype: 'combo',
                id: 'maps_cb',
                fieldLabel: i18n_map,
                typeAhead: true,
                editable: false,
                valueField: 'mapLayerPath',
                displayField: 'name',
                mode: 'remote',
                forceSelection: true,
                triggerAction: 'all',
                emptyText: GLOBALS.conf.emptytext,
                selectOnFocus: true,
				labelSeparator: GLOBALS.conf.labelseparator,
                width: GLOBALS.conf.combo_width,
                minListWidth: GLOBALS.conf.combo_width,
                store: mapStore,
                listeners: {
                    'select': {
                        fn: function() {
                            var mlp = Ext.getCmp('maps_cb').getValue();
							
                            Ext.getCmp('grid_gp').getStore().setBaseParam('mapLayerPath', this.getValue());
                            Ext.getCmp('grid_gp').getStore().load();
							
							Ext.getCmp('filter_tf').enable();
							
                            mapping.loadByUrl(mlp);
                        }
                    }
                }
            },
			
			{
				xtype: 'textfield',
				id: 'filter_tf',
				fieldLabel: i18n_filter,
				labelSeparator: GLOBALS.conf.labelseparator,
				isFormField: true,
				width: GLOBALS.conf.combo_width,
				enableKeyEvents: true,
				disabled: true,
				listeners: {
					'keyup': {
						fn: function() {
							var p = Ext.getCmp('filter_tf').getValue();
							gridStore.filter('organisationUnit', p, true, false);
						}
					}
				}
			},

            {
                xtype: 'grid',
                id: 'grid_gp',
                store: gridStore,
                columns: [ { header: i18n_organisation_units, id: 'organisationUnitId', dataIndex: 'organisationUnit', sortable: true, width: GLOBALS.conf.gridpanel_width } ],
				autoExpandColumn: 'organisationUnitId',
				enableHdMenu: true,
                width: GLOBALS.conf.gridpanel_width,
                height: GLOBALS.util.getGridPanelHeight(),
                view: gridView,
                style: 'left:0px',
                bbar: new Ext.StatusBar({
					defaultText: '',
                    id: 'relations_sb',
                    items:
                    [
                        {
                            xtype: 'button',
                            id: 'autoassign_be',
                            text: i18n_assign_all,
							cls: 'aa_med',
                            isVisible: false,
                            handler: function()
                            {
                                if (!Ext.getCmp('maps_cb').getValue()) {
                                    Ext.message.msg(false, i18n_please_select_map);
                                    return;
                                }
                                mapping.autoAssign(true);
                            },
                            scope: this
                        },
                        {
                            xtype: 'button',
                            id: 'removeallrelations_b',
                            text: i18n_remove_all,
							cls: 'aa_med',
                            isVisible: false,
                            handler: function() {
                                if (!Ext.getCmp('maps_cb').getValue()) {
                                    Ext.message.msg(false, i18n_please_select_map );
                                    return;
                                }
                                
                                var mlp = Ext.getCmp('maps_cb').getValue();
                                
                                Ext.Ajax.request({
                                    url: GLOBALS.conf.path_mapping + 'deleteMapOrganisationUnitRelationsByMap' + GLOBALS.conf.type,
                                    method: 'GET',
                                    params: { mapLayerPath: mlp },
                                    success: function( responseObject ) {
                                        var mlp = Ext.getCmp('maps_cb').getValue();
                                        Ext.getCmp('grid_gp').getStore().setBaseParam('mapLayerPath', mlp);
                                        Ext.getCmp('grid_gp').getStore().load();
                                        
                                        Ext.message.msg(true, i18n_all_relations_for_the_map + '<span class="x-msg-hl"> ' + Ext.getCmp('maps_cb').getRawValue() + '</span> ' + i18n_removed);
                                        
                                        mapping.classify(true, true);
                                    }
                                });
                            },
                            scope: this
                        },
                        {
                            xtype: 'button',
                            id: 'removerelation_b',
                            text: i18n_remove_selected,
							cls: 'aa_med',
                            isVisible: false,
                            handler: function()
                            {
                                if (!Ext.getCmp('maps_cb').getValue()) {
                                    Ext.message.msg(false, i18n_please_select_map);
                                    return;
                                }
                                
								var selection = Ext.getCmp('grid_gp').getSelectionModel().getSelections();
								var mlp = Ext.getCmp('maps_cb').getValue();
								var msg;
								
                                if (selection == '') {
                                    Ext.message.msg(false, i18n_please_select_least_one_organisation_unit_in_the_list);
                                    return;
                                }
								
								var params = '?organisationUnitIds=' + selection[0].data['organisationUnitId'];
								
								if (selection.length > 1) {
									for (var i = 1; i < selection.length; i++) {
										params += '&organisationUnitIds=' + selection[i].data['organisationUnitId'];
									}
									msg = i18n_selected_relations_removed;
								}
								else {
									msg = '<span class="x-msg-hl">' + selection[0].data['organisationUnit'] + '</span> ' + i18n_removed;
								}
								
								params += '&mapLayerPath=' + mlp;
								
								Ext.Ajax.request({
									url: GLOBALS.conf.path_mapping + 'deleteMapOrganisationUnitRelations' + type + params,
									method: 'GET',
									success: function(r) {
										Ext.getCmp('grid_gp').getStore().setBaseParam('mapLayerPath', mlp);
										Ext.getCmp('grid_gp').getStore().load();
										
										Ext.message.msg(true, msg);
										
										mapping.classify(true, true);
									}
								});
                            },
                            scope: this
                        }
                    ]
                }),
				listeners: {
					'cellclick': {
						fn: function(grid, rowIndex) {
							if (mapping.relation) {
								var id = grid.getStore().getAt(rowIndex).get('organisationUnitId');
								var name = grid.getStore().getAt(rowIndex).get('organisationUnit');
								var mlp = Ext.getCmp('maps_cb').getValue();
								
								Ext.Ajax.request({
									url: GLOBALS.conf.path_mapping + 'getMapOrganisationUnitRelationByFeatureId' + GLOBALS.conf.type,
									method: 'POST',
									params: {featureId:mapping.relation, mapLayerPath:mlp},
									success: function(r) {
										var mour = Ext.util.JSON.decode(r.responseText).mapOrganisationUnitRelation[0];
										if (mour.featureId == '') {
											Ext.Ajax.request({
												url: GLOBALS.conf.path_mapping + 'addOrUpdateMapOrganisationUnitRelation' + GLOBALS.conf.type,
												method: 'POST',
												params: {mapLayerPath:mlp, organisationUnitId:id, featureId:mapping.relation},
												success: function() {
													Ext.message.msg(true, '<span class="x-msg-hl">' + mapping.relation + '</span> (' + i18n_in_the_map + ') ' + i18n_assigned_to + ' <span class="x-msg-hl">' + name + '</span> (' + i18n_database + ').');
													Ext.getCmp('grid_gp').getStore().load();
													selectFeaturePopup.hide();
													mapping.relation = false;
													Ext.getCmp('filter_tf').setValue('');
													mapping.classify(true, true);
												}
											});
										}
										else {
											Ext.message.msg(false, '<span class="x-msg-hl">' + name + '</span> ' + i18n_is_already_assigned );
										}
									}
								});
							}
						}
					}
				}
             }
        ];

        mapfish.widgets.geostat.Choropleth.superclass.initComponent.apply(this);
    },
    
    setUrl: function(url) {
        this.url = url;
        this.coreComp.setUrl(this.url);
    },

    /**
     * Method: requestSuccess
     *      Calls onReady callback function and mark the widget as ready.
     *      Called on Ajax request success.
     */
    requestSuccess: function(request) {
        this.ready = true;

        // if widget is rendered, hide the optional mask
        if (this.loadMask && this.rendered) {
            this.loadMask.hide();
        }
    },

    /**
     * Method: requestFailure
     *      Displays an error message on the console.
     *      Called on Ajax request failure.
     */
    requestFailure: function(request) {
        OpenLayers.Console.error( i18n_ajax_request_failed );
    },

    /**
     * Method: getColors
     *    Retrieves the colors from form elements
     *
     * Returns:
     * {Array(<mapfish.Color>)} an array of two colors (start, end)
     */
    getColors: function() {
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(Ext.getCmp('colorA_cf').getValue());
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(Ext.getCmp('colorB_cf').getValue());
        return [colorA, colorB];
    },
    
    validateForm: function(exception) {
        if (!Ext.getCmp('maps_cb').getValue()) {
                if (exception) {
                    Ext.message.msg(false, i18n_please_select_map );
                }
                return false;
        }
        return true;
    },
    
    loadByUrl: function(url) {
        if (url != mapping.newUrl) {
            mapping.newUrl = url;
            
            if (MAPSOURCE == GLOBALS.conf.map_source_type_geojson) {
                mapping.setUrl(GLOBALS.conf.path_mapping + 'getGeoJsonFromFile.action?name=' + url);
            }
			else if (MAPSOURCE == GLOBALS.conf.map_source_type_shapefile) {
				mapping.setUrl(GLOBALS.conf.path_geoserver + GLOBALS.conf.wfs + url + GLOBALS.conf.output);
			}
        }
    },
    
    applyValues: function(color, noCls) {
        var options = {};
        
        mapping.indicator = 'value';
        options.indicator = mapping.indicator;
        options.method = 2;
        options.numClasses = noCls;
        
        var colorA = new mapfish.ColorRgb();
        colorA.setFromHex(color);
        var colorB = new mapfish.ColorRgb();
        colorB.setFromHex(GLOBALS.conf.assigned_row_color);
        options.colors = [colorA, colorB];
        
        mapping.coreComp.updateOptions(options);
        mapping.coreComp.applyClassification();
        mapping.classificationApplied = true;
        
        MASK.hide();
    },
    
    autoAssign: function(position) {
        MASK.msg = i18n_loading ;
        MASK.show();

        var level = this.mapData.organisationUnitLevel;

        Ext.Ajax.request({
            url: GLOBALS.conf.path_mapping + 'getOrganisationUnitsAtLevel' + GLOBALS.conf.type,
            method: 'POST',
            params: {level: level},
            scope: this,
            success: function(r) {
                var organisationUnits = Ext.util.JSON.decode(r.responseText).organisationUnits;
                var nameColumn = this.mapData.nameColumn;
                var mlp = this.mapData.mapLayerPath;
                var count_match = 0;
                var relations = '';
                
                for (var i = 0; i < this.layer.features.length; i++) {
                    this.layer.features[i].attributes.compareName = this.layer.features[i].attributes[nameColumn].split(' ').join('').toLowerCase();
                }
        
                for (var i = 0; i < organisationUnits.length; i++) {
                    organisationUnits[i].compareName = organisationUnits[i].name.split(' ').join('').toLowerCase();
                }
                
                for (var i = 0; i < organisationUnits.length; i++) {
                    for (var j = 0; j < this.layer.features.length; j++) {
                        if (this.layer.features[j].attributes.compareName == organisationUnits[i].compareName) {
                            count_match++;
                            relations += organisationUnits[i].id + '::' + this.layer.features[j].attributes[nameColumn] + ';;';
                            break;
                        }
                    }
                }
                
                MASK.msg = count_match == 0 ? i18n_no + ' ' + i18n_organisation_units + ' ' +  i18n_assigned + '...' : + i18n_assigning +' ' + count_match + ' '+ i18n_organisation_units + '...';
                MASK.show();

                Ext.Ajax.request({
                    url: GLOBALS.conf.path_mapping + 'addOrUpdateMapOrganisationUnitRelations' + GLOBALS.conf.type,
                    method: 'POST',
                    params: {mapLayerPath:mlp, relations:relations},
                    scope: this,
                    success: function(r) {
                        MASK.msg = i18n_applying_organisation_units_relations ;
                        MASK.show();
                        
                        Ext.message.msg(true, '<span class="x-msg-hl">' + count_match + '</span> '+ i18n_organisation_units_assigned + ' (map <span class="x-msg-hl">' + this.layer.features.length + '</span>, db <span class="x-msg-hl">' + organisationUnits.length + '</span>)');
                       
                        Ext.getCmp('grid_gp').getStore().load();
                        mapping.classify(false, position);
                    }
                });
            }
        });
    },        

    classify: function(exception, position) {
        if (mapping.validateForm(exception)) {
            MASK.msg = i18n_creating_map;
            MASK.show();
            
            Ext.Ajax.request({
                url: GLOBALS.conf.path_mapping + 'getMapByMapLayerPath' + GLOBALS.conf.type,
                method: 'POST',
                params: {mapLayerPath: mapping.newUrl},
                scope: this,
                success: function(r) {
                    this.mapData = Ext.util.JSON.decode(r.responseText).map[0];
                    
                    this.mapData.organisationUnitLevel = parseFloat(this.mapData.organisationUnitLevel);
                    this.mapData.longitude = parseFloat(this.mapData.longitude);
                    this.mapData.latitude = parseFloat(this.mapData.latitude);
                    this.mapData.zoom = parseFloat(this.mapData.zoom);
            
                    if (!position) {
                        MAP.zoomToExtent(this.layer.getDataExtent());
                    }

                    var mlp = this.mapData.mapLayerPath;
                    var relations =	Ext.getCmp('grid_gp').getStore();
                    var nameColumn = this.mapData.nameColumn;
                    var noCls = 1;
                    var noAssigned = 0;
        
                    for (var i = 0; i < this.layer.features.length; i++) {
                        this.layer.features[i].attributes.value = 0;
                        this.layer.features[i].attributes.labelString = '';

                        for (var j = 0; j < relations.getTotalCount(); j++) {
                            var name = this.layer.features[i].attributes[nameColumn];
                            if (relations.getAt(j).data.featureId == name) {
                                this.layer.features[i].attributes.value = 1;
                                this.layer.features[i].attributes.labelString = name;
                                noAssigned++;
                                noCls = noCls < 2 ? 2 : noCls;
                                break;
                            }
                        }
                    }

                    var color = noCls > 1 && noAssigned == this.layer.features.length ? GLOBALS.conf.assigned_row_color : GLOBALS.conf.unassigned_row_color;
                    noCls = noCls > 1 && noAssigned == this.layer.features.length ? 1 : noCls;
                    
                    mapping.applyValues(color, noCls);
                }
            });
        }
    },

    onRender: function(ct, position) {
        mapfish.widgets.geostat.Choropleth.superclass.onRender.apply(this, arguments);
        if(this.loadMask){
            this.loadMask = new Ext.LoadMask(this.bwrap, this.loadMask);
            this.loadMask.show();
        }

        var coreOptions = {
            'layer': this.layer,
            'format': this.format,
            'url': this.url,
            'requestSuccess': this.requestSuccess.createDelegate(this),
            'requestFailure': this.requestFailure.createDelegate(this),
            'featureSelection': this.featureSelection,
            'nameAttribute': this.nameAttribute,
            'legendDiv': this.legendDiv,
            'labelGenerator': this.labelGenerator
        };

        this.coreComp = new mapfish.GeoStat.Choropleth(this.map, coreOptions);
    }
});

Ext.reg('mapping', mapfish.widgets.geostat.Mapping);