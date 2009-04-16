// reference local blank image
Ext.BLANK_IMAGE_URL = '../../mfbase/ext/resources/images/default/s.gif';

Ext.onReady(function()
{
    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

    myMap: null;

    map = new OpenLayers.Map($('olmap'));
    this.myMap = map;

    features = null;
    features_choropleth = null;
    features_mapping = null;

    var jpl_wms = new OpenLayers.Layer.WMS("Satellite",
                                           "http://labs.metacarta.com/wms-c/Basic.py?", 
                                           {layers: 'satellite', format: 'image/png'});
                                     
    var vmap0 = new OpenLayers.Layer.WMS("OpenLayers WMS",
                                         "pageload_geojson.txt");
                                   
    var choroplethLayer = new OpenLayers.Layer.Vector(choroplethLayerName, {
        'visibility': false,
        'styleMap': new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
                    {'fillOpacity': 1, 'strokeColor': '#222222', 'strokeWidth': 1, 'opacity': 0.6},
                    OpenLayers.Feature.Vector.style['default']
                )
            ),
            'select': new OpenLayers.Style(
                {'fillOpacity': 1, 'strokeColor': '#000000', 'strokeWidth': 2, 'cursor': 'pointer'}
            )
        })
    });

    var propSymbolLayer = new OpenLayers.Layer.Vector(propSymbolLayerName, {
        'visibility': false,
        'styleMap': new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
                    {'fillOpacity': 0.6, 'fillColor': 'Khaki', 'strokeWidth': 1, 'strokeColor': '#222222' },
                    OpenLayers.Feature.Vector.style['default']
                )
            ),
            'select': new OpenLayers.Style(
                {'strokeWidth': 2, 'strokeColor': '#000000', 'cursor': 'pointer'}
            )
        })
    });

    map.addLayers([jpl_wms, vmap0, choroplethLayer, propSymbolLayer]);

    selectFeatureChoropleth = new OpenLayers.Control.newSelectFeature(
        choroplethLayer,
        {onClickSelect: onClickSelectChoropleth, onClickUnselect: onClickUnselectChoropleth,
        onHoverSelect: onHoverSelectChoropleth, onHoverUnselect: onHoverUnselectChoropleth}
    );

    selectFeaturePoint = new OpenLayers.Control.newSelectFeature( propSymbolLayer,
        {onClickSelect: onClickSelectPoint, onClickUnselect: onClickUnselectPoint,
        onHoverSelect: onHoverSelectPoint, onHoverUnselect: onHoverUnselectPoint }
    );

    map.addControl(selectFeatureChoropleth);
    map.addControl(selectFeaturePoint);
    selectFeatureChoropleth.activate();
    selectFeaturePoint.activate();

    map.setCenter(new OpenLayers.LonLat(init_longitude, init_latitude), init_zoom); // config.js
    
    organisationUnitLevelStore = new Ext.data.JsonStore({
        url: localhost + '/dhis-webservice/getOrganisationUnitLevels.service',
        baseParams: { format: 'json' },
        root: 'organisationUnitLevels',
        fields: ['id', 'level', 'name'],
        autoLoad: true
    });

    organisationUnitStore = new Ext.data.JsonStore({
        url: localhost + '/dhis-webservice/getOrganisationUnitsAtLevel.service',
        root: 'organisationUnits',
        fields: ['id', 'name'],
        sortInfo: { field: 'name', direction: 'ASC' },
        autoLoad: false
    });
    
    existingMapsStore = new Ext.data.JsonStore({
            url: localhost + '/dhis-webservice/getAllMaps.service',
            baseParams: { format: 'jsonmin' },
            root: 'maps',
            fields: ['id', 'mapLayerPath', 'organisationUnitLevel'],
            autoLoad: true
    });

    var organisationUnitCombo = new Ext.form.ComboBox({
        id: 'organisationunit_cb',
        fieldLabel: 'Organisation unit',
        typeAhead: true,
        editable: false,
        valueField: 'name',
        displayField: 'name',
        emptyText: 'Required',
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: organisationUnitStore
    });
    
    var organisationUnitLevelCombo = new Ext.form.ComboBox({
        id: 'organisationunitlevel_cb',
        fieldLabel: 'Level',
        typeAhead: true,
        editable: false,
        valueField: 'level',
        displayField: 'name',
        emptyText: 'Required',
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        minListWidth: combo_width + 26,
        store: organisationUnitLevelStore,
        listeners: {
            'select': {
                fn: function() {
                    var level1 = Ext.getCmp('newmap_cb').getValue();
                    var level2 = Ext.getCmp('organisationunitlevel_cb').getValue();
                    var orgunit = Ext.getCmp('organisationunit_cb').getValue();
                    
                    if (level1 >= level2)
                    {
                        organisationUnitLevelCombo2.reset();
                        
                        Ext.Msg.show({
                        title:'Register shapefiles',
                        msg: '<p style="padding-top:8px">Level must be lower than ' + orgunit + '!</p>',
                        buttons: Ext.Msg.OK,
                        animEl: 'elId',
                        maxWidth: 300,
                        icon: Ext.MessageBox.ERROR
                        });
                        return;
                    }
                    
                    
                },
                scope: this
            }
        }
    });

    var mapLayerPathTextField = new Ext.form.TextField({
        id: 'maplayerpath_tf',
        emptyText: 'Required',
        width: combo_width
    });
    
    var uniqueColumnTextField = new Ext.form.TextField({
        id: 'uniquecolumn_tf',
        emptyText: 'Required',
        width: combo_width
    });
    
    var nameColumnTextField = new Ext.form.TextField({
        id: 'namecolumn_tf',
        emptyText: 'Required',
        width: combo_width
    });
    
    var longitudeTextField = new Ext.form.TextField({
        id: 'longitude_tf',
        emptyText: 'Required',
        width: combo_width
    });
    
    var latitudeTextField = new Ext.form.TextField({
        id: 'latitude_tf',
        emptyText: 'Required',
        width: combo_width
    });
    
    var zoomComboBox = new Ext.form.ComboBox({
        id: 'zoom_cb',
        editable: false,
        emptyText: 'Required',
        displayField: 'value',
        valueField: 'value',
        width: combo_width,
        minListWidth: combo_width + 26,
        triggerAction: 'all',
        mode: 'local',
        value: 5,
        store: new Ext.data.SimpleStore({
            fields: ['value'],
            data: [[3], [4], [5], [6], [7], [8]]
        })
    });
    
    var newMapButton = new Ext.Button({
        id: 'newmap_b',
        text: 'Register map',
        handler: function()
        {
            var nm = Ext.getCmp('newmap_cb').getValue();
            var oui = Ext.getCmp('organisationunit_cb').getValue();
            var ouli = Ext.getCmp('organisationunitlevel_cb').getValue();
            var mlp = Ext.getCmp('maplayerpath_tf').getValue();
            var uc = Ext.getCmp('uniquecolumn_tf').getValue();
            var nc = Ext.getCmp('namecolumn_tf').getValue();
            var lon = Ext.getCmp('longitude_tf').getValue();
            var lat = Ext.getCmp('latitude_tf').getValue();
            var zoom = Ext.getCmp('zoom_cb').getValue();
            
            if (!nm || !mlp || !oui || !ouli || !uc || !nc || !lon || !lat)
            {
                Ext.MessageBox.alert('Error', 'Form is not complete');
                return;
            }
            
            Ext.Ajax.request(
            {
                url: localhost + '/dhis-webservice/addOrUpdateMap.service',
                method: 'GET',
                params: { mapLayerPath: mlp, organisationUnitId: oui, organisationUnitLevelId: ouli, uniqueColumn: uc, nameColumn: nc,
                          longitude: lon, latitude: lat, zoom: zoom},

                success: function( responseObject )
                {
                    Ext.Msg.show({
                        title:'Register shapefiles',
                        msg: '<p style="padding-top:8px">The map <b>' + mlp + '</b> was successfully registered!</b></p>',
                        buttons: Ext.Msg.OK,
                        animEl: 'elId',
                        minWidth: 400,
                        icon: Ext.MessageBox.INFO
                    });
                    
                    Ext.getCmp('map_cb').getStore().reload();
                    Ext.getCmp('maps_cb').getStore().reload();
                },
                failure: function()
                {
                    alert( 'Status', 'Error while saving data' );
                }
            });
        }
    });
    
    var editMapButton = new Ext.Button({
        id: 'editmap_b',
        text: 'Save changes',
        handler: function()
        {
            var em = Ext.getCmp('editmap_cb').getValue();
            var uc = Ext.getCmp('uniquecolumn_tf').getValue();
            var nc = Ext.getCmp('namecolumn_tf').getValue();
            var lon = Ext.getCmp('longitude_tf').getValue();
            var lat = Ext.getCmp('latitude_tf').getValue();
            var zoom = Ext.getCmp('zoom_cb').getValue();
            
            if (!em || !uc || !nc || !lon || !lat)
            {
                Ext.MessageBox.alert('Error', 'Form is not complete');
                return;
            }
            
            Ext.Ajax.request(
            {
                url: localhost + '/dhis-webservice/addOrUpdateMap.service',
                method: 'GET',
                params: { mapLayerPath: mlp, organisationUnitId: oui, organisationUnitLevelId: ouli, uniqueColumn: uc, nameColumn: nc,
                          longitude: lon, latitude: lat, zoom: zoom},

                success: function( responseObject )
                {
                    Ext.Msg.show({
                        title:'Register shapefiles',
                        msg: '<p style="padding-top:8px">The map <b>' + mlp + '</b> was successfully updated!</b></p>',
                        buttons: Ext.Msg.OK,
                        animEl: 'elId',
                        minWidth: 400,
                        icon: Ext.MessageBox.INFO
                    });
                    
                    Ext.getCmp('map_cb').getStore().reload();
                    Ext.getCmp('maps_cb').getStore().reload();
                },
                failure: function()
                {
                    alert( 'Status', 'Error while saving data' );
                }
            });
        }
    });
    
    var deleteMapButton = new Ext.Button({
        id: 'deletemap_b',
        text: 'Delete map',
        handler: function()
        {
            var mlp = Ext.getCmp('deletemap_cb').getValue();
            
            if (!mlp)
            {
                Ext.MessageBox.alert('Error', 'Choose a map');
                return;
            }
            
            Ext.Ajax.request(
            {
                url: localhost + '/dhis-webservice/deleteMapByMapLayerPath.service',
                method: 'GET',
                params: { mapLayerPath: mlp },

                success: function( responseObject )
                {
                    Ext.Msg.show({
                        title:'Register shapefiles',
                        msg: '<p style="padding-top:8px">The map <b>' + mlp + '</b> was successfully deleted!</b></p>',
                        buttons: Ext.Msg.OK,
                        animEl: 'elId',
                        minWidth: 400,
                        icon: Ext.MessageBox.INFO
                    });
                    
                    Ext.getCmp('map_cb').getStore().reload();
                    Ext.getCmp('maps_cb').getStore().reload();
                },
                failure: function()
                {
                    alert( 'Status', 'Error while saving data' );
                }
            });
        }
    });

    rootmap = new Ext.Panel({
        id: 'rootmap',
        title: 'Register shapefiles',
        items:
        [
            {
                xtype: 'tabpanel',
                activeTab: 0,
                deferredRender: false,
                plain: true,
                defaults: {layout: 'fit', bodyStyle: 'padding:8px'},
                listeners: {
                    tabchange: function(panel, tab) {
                        if (tab.id == 0)
                        {
                            Ext.getCmp('panel1_p').setVisible(true);
                            Ext.getCmp('panel2_p').setVisible(true);
                            Ext.getCmp('newmap_b').setVisible(true);
                            Ext.getCmp('editmap_b').setVisible(false);
                            Ext.getCmp('deletemap_b').setVisible(false);
                        }
                        
                        else if (tab.id == 1)
                        {
                            Ext.getCmp('panel1_p').setVisible(false);
                            Ext.getCmp('panel2_p').setVisible(true);
                            Ext.getCmp('newmap_b').setVisible(false);
                            Ext.getCmp('editmap_b').setVisible(true);
                            Ext.getCmp('deletemap_b').setVisible(false);
                        }
                        
                        else if (tab.id == 2)
                        {
                            Ext.getCmp('panel1_p').setVisible(false);
                            Ext.getCmp('panel2_p').setVisible(false);
                            Ext.getCmp('newmap_b').setVisible(false);
                            Ext.getCmp('editmap_b').setVisible(false);
                            Ext.getCmp('deletemap_b').setVisible(true);
                        }
                    }
                },
                items:
                [
                    {
                        title:'New map',
                        id: '0',
                        items:
                        [
                            {
                                xtype: 'combo',
                                id: 'newmap_cb',
                                typeAhead: true,
                                editable: false,
                                valueField: 'level',
                                displayField: 'name',
                                emptyText: 'Select organisation unit level',
                                mode: 'remote',
                                forceSelection: true,
                                triggerAction: 'all',
                                selectOnFocus: true,
                                width: combo_width,
                                store: organisationUnitLevelStore,
                                listeners: {
                                    'select': {
                                        fn: function() {
                                            var level = Ext.getCmp('newmap_cb').getValue();
                                            organisationUnitStore.baseParams = { level: level, format: 'json' };
                                            organisationUnitStore.reload();
                                        },
                                        scope: this
                                    }
                                }
                            }
                        ]
                    },
                    
                    {
                        title:'Edit map',
                        id: '1',
                        items:
                        [
                            {
                                xtype: 'combo',
                                id: 'editmap_cb',
                                typeAhead: true,
                                editable: false,
                                valueField: 'mapLayerPath',
                                displayField: 'mapLayerPath',
                                emptyText: 'Select map',
                                mode: 'remote',
                                forceSelection: true,
                                triggerAction: 'all',
                                selectOnFocus: true,
                                width: combo_width,
                                store: existingMapsStore,
                                listeners:
                                {
                                    'select':
                                    {
                                        fn: function()
                                        {
                                            var mlp = Ext.getCmp('editmap_cb').getValue();
                                            
                                            Ext.Ajax.request( 
                                            {
                                                url: localhost + '/dhis-webservice/getMapByMapLayerPath.service',
                                                method: 'GET',
                                                params: { mapLayerPath: mlp, format: 'json' },

                                                success: function( responseObject )
                                                {
                                                    var map = Ext.util.JSON.decode( responseObject.responseText ).map;
                                                    
                                                    Ext.getCmp('uniquecolumn_tf').setValue(map.uniqueColumn);
                                                    Ext.getCmp('namecolumn_tf').setValue(map.nameColumn);
                                                    Ext.getCmp('longitude_tf').setValue(map.longitude);
                                                    Ext.getCmp('latitude_tf').setValue(map.latitude);
                                                    Ext.getCmp('zoom_cb').setValue(map.zoom);
                                                    
                                                },
                                                failure: function()
                                                {
                                                    alert( 'Error while retrieving data: getAssignOrganisationUnitData' );
                                                } 
                                            });
                                                                                
                                            
                                            
                                        },
                                        scope: this
                                    }
                                }
                            }
                        ]
                    },
                    
                    {
                        title:'Delete map',
                        id: '2',
                        items:
                        [
                            {
                                xtype: 'combo',
                                id: 'deletemap_cb',
                                typeAhead: true,
                                editable: false,
                                valueField: 'mapLayerPath',
                                displayField: 'mapLayerPath',
                                emptyText: 'Select map',
                                mode: 'remote',
                                forceSelection: true,
                                triggerAction: 'all',
                                selectOnFocus: true,
                                width: combo_width,
                                store: existingMapsStore
                            }
                        ]
                    }
                ]
            },
            
            { html: '<br>' },
            
            {
                xtype: 'panel',
                id: 'panel1_p',
                items:
                [
                    { html: '<p style="padding-bottom:4px">Register map for this organisation unit:</p>' }, organisationUnitCombo, { html: '<br>' },
                    { html: '<p style="padding-bottom:4px">Organisation unit level:</p>' }, organisationUnitLevelCombo, { html: '<br>' },
                    { html: '<p style="padding-bottom:4px">Geoserver map layer path:</p>' }, mapLayerPathTextField, { html: '<br>' }
                ]
            },
            
            {
                xtype: 'panel',
                id: 'panel2_p',
                items:
                [
                    { html: '<p style="padding-bottom:4px">Unique column:</p>' }, uniqueColumnTextField, { html: '<br>' },
                    { html: '<p style="padding-bottom:4px">Name column:</p>' }, nameColumnTextField, { html: '<br>' },
                    { html: '<p style="padding-bottom:4px">Longitude:</p>' }, longitudeTextField, { html: '<br>' },
                    { html: '<p style="padding-bottom:4px">Latitude:</p>' }, latitudeTextField, { html: '<br>' },
                    { html: '<p style="padding-bottom:4px">Zoom:</p>' }, zoomComboBox, { html: '<br>' }
                ]
            },

            newMapButton,
            
            editMapButton,
            
            deleteMapButton
        ]
    });
    
    
    // create choropleth widget
    choropleth = new mapfish.widgets.geostat.Choropleth({
        id: 'choropleth',
        map: map,
        layer: choroplethLayer,
        title: 'Thematic map',
        nameAttribute: "NAME",
        indicators: [['value', 'Indicator']],
        url: 'pageload_geojson.txt',
        featureSelection: false,
        loadMask: {msg: 'Loading shapefile...', msgCls: 'x-mask-loading'},
        legendDiv: 'choroplethLegend',
        defaults: {width: 130},
        listeners: {
            expand: {
                fn: function() {
                    choroplethLayer.setVisibility(false);
                    choropleth.classify(false);
                    if (features_choropleth != null) {
                        features = features_choropleth;
                    }
                }
            }
        }
    });
    
    mapping = new mapfish.widgets.geostat.Mapping({
        id: 'mapping',
        map: map,
        layer: choroplethLayer,
        title: 'Assign organisation units',
        nameAttribute: 'NAME',
        indicators: [['value', 'Indicator']],
        url: 'pageload_geojson.txt',
        featureSelection: false,
        loadMask: {msg: 'Loading shapefile...', msgCls: 'x-mask-loading'},
        legendDiv: 'choroplethLegend',
        defaults: {width: 130},
        listeners: {
            expand: {
                // show layer if expanded
                fn: function() {
                    choroplethLayer.setVisibility(false);
                    if (features_mapping != null) {
                        features = features_mapping;
                    }
                }
            }
        }
    });

    var propSymbol = new mapfish.widgets.geostat.ProportionalSymbol({
        id: 'propsymbol',
        map: map,
        layer: propSymbolLayer,
        title: 'Proportional symbol',
        nameAttribute: "ouname",
        indicators: [['PERIMETER', 'Perimeter']],
        url: 'pageload_geojson.txt',
        featureSelection: false,
        loadMask : {msg: 'Loading Data...', msgCls: 'x-mask-loading'},
        defaults: {width: 130},
        listeners: {
            expand: {
                fn: function() {
                    if (this.classificationApplied) {
                        this.layer.setVisibility(true);
                    }
                }
            }
        }
    });

    viewport = new Ext.Viewport({
        layout: 'border',
        items:
        [
            new Ext.BoxComponent(
            {
                // raw
                region: 'north',
                id: 'north',
                el: 'north',
                height: north_height
            }),
            
            {
                region: 'south',
                contentEl: 'south',
                id: 'south-panel',
                split: true,
                height: south_height,
                minSize: 50,
                maxSize: 200,
                collapsible: true,
                title: 'Status',
                margins: '0 0 0 0',
                bodyStyle: 'padding:5px; font-family:tahoma; font-size:12px'
            },
            
            {
                region: 'east',
                title: ' ',
                width: 200,
                collapsible: true,
                margins: '0 0 0 5',
                defaults: {
                    border: true,
                    frame: true
                },
                layout: 'anchor',
                items:
                [
                    {
                        title: 'Layers',
                        autoHeight: true,
                        xtype: 'layertree',
                        map: map,
                        anchor: '100%'
                    },
                    
                    {
                        title: 'Overview Map',
                        autoHeight: true,
                        html:'<div id="overviewmap"></div>',
                        anchor: '100%'
                    },
                    
                    {
                        title: 'Position',
                        height: 65,
                        contentEl: 'position',
                        anchor: '100%'
                    },
                    
                    {
                        title: 'Legend',
                        minHeight: 65,
                        autoHeight: true,
                        contentEl: 'legend',
                        anchor: '100%'
                    }
                ]
            },
            
            {
                region: 'west',
                id: 'west',
                title: '',
                split: true,
                collapsible: true,
                width: west_width,
                minSize: 175,
                maxSize: 500,
                margins: '0 0 0 5',
                layout: 'accordion',
                defaults: {
                    border: true,
                    frame: true
                },
                items:
                [
                    choropleth,
                    mapping,
                    rootmap
                ]
            },
            
            {
                region: 'center',
                id: 'center',
                title: 'Map',
                xtype: 'mapcomponent',
                map: map
            }
        ]
    });

    map.addControl(new OpenLayers.Control.MousePosition(
    {
        displayClass: "void", 
        div: $('mouseposition'), 
        prefix: 'x: ',
        separator: '<br/>y: '
    }));

    map.addControl(new OpenLayers.Control.OverviewMap({div: $('overviewmap')}));

    Ext.get('loading').fadeOut({remove: true});
});

function onHoverSelectChoropleth(feature)
{
    var center_panel = Ext.getCmp('center');
    var south_panel = Ext.getCmp('south-panel');

    var height = 230;
    var padding_x = 15;
    var padding_y = 22;

    var x = center_panel.x + padding_x;
    var y = south_panel.y - height - padding_y;

    popup_feature = new Ext.Window({
        title: 'Organisation unit',
        width: 190,
        height: height,
        layout: 'fit',
        plain: true,
        bodyStyle: 'padding:5px',
        x: x,
        y: y
    });    

    style = '<p style="margin-top: 5px; padding-left:5px;">';
    space = '&nbsp;&nbsp;';
    bs = '<b>';
    be = '</b>';
    lf = '<br>';
    pe = '</p>';
    
    var html = style + feature.attributes[mapData.map.nameColumn] + pe;
    html += style + bs + 'Value:' + be + space + feature.attributes['value'] + pe;
    
    popup_feature.html = html;
    popup_feature.show();
}

function onHoverUnselectChoropleth(feature)
{
    var infoPanel = Ext.getCmp('south-panel');

    popup_feature.hide();
}

function onClickSelectChoropleth(feature)
{
    if (!Ext.getCmp('grid_gp').getSelectionModel().getSelected())
    {
        alert('First, select an organisation unit from the list');
        return;
    }
    
    var selected = Ext.getCmp('grid_gp').getSelectionModel().getSelected();
    var organisationUnitId = selected.data['organisationUnitId'];
    var organisationUnit = selected.data['organisationUnit'];
    
    var uniqueColumn = mapData.map.uniqueColumn;
    var mlp = mapData.map.mapLayerPath;
    var featureId = feature.attributes[uniqueColumn];

    Ext.Ajax.request( 
    {
        url: localhost + '/dhis-webservice/addOrUpdateMapOrganisationUnitRelation.service',
        method: 'GET',
        params: { mapLayerPath: mlp, organisationUnitId: organisationUnitId, featureId: featureId },

        success: function( responseObject )
        {
            var south_panel = Ext.getCmp('south-panel');
            south_panel.body.dom.innerHTML = organisationUnit + '<font color="#444444"> assigned to </font>' + featureId + "!";
            
            setMapData('assignment');
        },
        failure: function()
        {
            alert( 'Status', 'Error while retrieving data' );
        } 
    });
    
    popup_feature.hide();
}

function onClickUnselectChoropleth(feature) {}


function onHoverSelectPoint(feature)
{
/*
    var center_panel = Ext.getCmp('center');
    var south_panel = Ext.getCmp('south-panel');

    var height = 230;
    var padding_x = 15;
    var padding_y = 22;

    var x = center_panel.x + padding_x;
    var y = south_panel.y - height - padding_y;

    popup_orgunit = new Ext.Window(
    {
        title: 'Organisation unit',
        width: 190,
        height: height,
        layout: 'fit',
        plain: true,
        bodyStyle: 'padding:5px',
        x: x,
        y: y
    });

    style = '<p style="margin-top: 5px; padding-left:5px;">';

    var html = style + '<b>' + shpcols[1][0].type + ': </b>' + feature.attributes[shpcols[pointLayer][0].parent1] + '</p>';
    html += style + '<b>' + shpcols[2][0].type + ': </b>' + feature.attributes[shpcols[pointLayer][0].parent2] + '</p>';
    html += style + '<b>' + shpcols[3][0].type + ': </b>' + feature.attributes[shpcols[pointLayer][0].parent3] + '</p>';
    html += style + '<b>' + shpcols[4][0].type + ': </b>' + feature.attributes[shpcols[pointLayer][0].name] + '</p>';
    html += '<br>';
    html += style + '<b>Value: </b>' + feature.attributes[shpcols[pointLayer][0].value] + '</p>';

    popup_orgunit.html = html;
    popup_orgunit.show();

    var infoPanel_orgunit = Ext.getCmp('south-panel');
    infoPanel_orgunit.body.dom.innerHTML = 'More information about the selected organisation unit may be listed here.';
*/    
}

function onHoverUnselectPoint(feature)
{
/*
    var infoPanel_orgunit = Ext.getCmp('south-panel');
    infoPanel_orgunit.body.dom.innerHTML = '';

    popup_orgunit.hide();
*/    
}

function onClickSelectPoint(feature) {}

function onClickUnselectPoint(feature) {}



mapData = null;

function loadMapData(redirect)
{
    Ext.Ajax.request( 
    {
        url: localhost + '/dhis-webservice/getMapByMapLayerPath.service',
        method: 'GET',
        params: { mapLayerPath: 'who:Indian_state', format: 'json' },

        success: function( responseObject )
        {
            mapData = Ext.util.JSON.decode(responseObject.responseText);

            map.setCenter(new OpenLayers.LonLat(mapData.map.longitude, mapData.map.latitude), mapData.map.zoom);

            if (redirect == 'choropleth') {
                getChoroplethData(); }
            if (redirect == 'point') {
                getPointData(); }
            if (redirect == 'assignment') {
                getAssignOrganisationUnitData(); }
        },
        failure: function()
        {
            alert( 'Error while retrieving map data: setMapData' );
        } 
    });
}


function getChoroplethData()
{
    var indicatorId = Ext.getCmp('indicator_cb').getValue();
    var periodId = Ext.getCmp('period_cb').getValue();
    var level = mapData.map.organisationUnitLevel;

    Ext.Ajax.request( 
    {
        url: localhost + '/dhis-webservice/getMapValues.service',
        method: 'GET',
        params: { indicatorId: indicatorId, periodId: periodId, level: level, format: 'json' },

        success: function( responseObject )
        {
            dataReceivedChoropleth( responseObject.responseText );
        },
        failure: function()
        {
            alert( 'Status', 'Error while retrieving data' );
        } 
    });
}


function getPointData()
{
    var indicatorId = Ext.getCmp('indicator_cb').getValue();
    var periodId = Ext.getCmp('period_cb').getValue();
    var level = pointLayer;

    var url = localhost + '/dhis-webservice/getMapValues.service';
    format = 'json';

    Ext.Ajax.request( 
    {
        url: url,
        method: 'GET',
        params: { indicatorId: indicatorId, periodId: periodId, level: level, format: format },

        success: function( responseObject )
        {
            dataReceivedPoint( responseObject.responseText );
        },
        failure: function()
        {
            alert( 'Status', 'Error while retrieving data' );
        } 
    });
}


function getAssignOrganisationUnitData()
{
    var mlp = mapData.map.mapLayerPath;
    
    Ext.Ajax.request( 
    {
        url: localhost + '/dhis-webservice/getAvailableMapOrganisationUnitRelations.service',
        method: 'GET',
        params: { mapLayerPath: mlp, format: 'json' },

        success: function( responseObject )
        {
            dataReceivedAssignOrganisationUnit( responseObject.responseText );
        },
        failure: function()
        {
            alert( 'Error while retrieving data: getAssignOrganisationUnitData' );
        } 
    });
}

function dataReceivedChoropleth( responseText )
{
    var layers = this.myMap.getLayersByName(choroplethLayerName);
    var features = layers[0]['features'];
    
    var mapvalues = Ext.util.JSON.decode(responseText).mapvalues;
    
    var mlp = mapData.map.mapLayerPath;
    var uniqueColumn = mapData.map.uniqueColumn;
    
    Ext.Ajax.request( 
    {
        url: localhost + '/dhis-webservice/getAvailableMapOrganisationUnitRelations.service',
        method: 'GET',
        params: { mapLayerPath: mlp, format: 'json' },

        success: function( responseObject )
        {
            var relations = Ext.util.JSON.decode(responseObject.responseText).mapOrganisationUnitRelations;
            
            for (var i=0; i < relations.length; i++)
            {
                var orgunitid = relations[i].organisationUnitId;
                var featureid = relations[i].featureId;
                
                for (var j=0; j < mapvalues.length; j++)
                {
                    if (orgunitid == mapvalues[j].organisationUnitId)
                    {
                        for (var k=0; k < features.length; k++)
                        {
                            if (features[k].attributes['value'] == null)
                            {
                                features[k].attributes['value'] = 0;
                            }
                            
                            if (featureid == features[k].attributes[uniqueColumn])
                            {
                                features[k].attributes['value'] = mapvalues[j].value;
                            }
                        }
                    }
                }
            }
            
            features_choropleth = features;
        },
        failure: function()
        {
            alert( 'Error while retrieving data: dataReceivedChoropleth' );
        } 
    });
}

function dataReceivedPoint( responseText )
{
    var layers = this.myMap.getLayersByName(propSymbolLayerName);
    var features = layers[0]["features"];
    var featuresLength = features.length;
    var data = Ext.util.JSON.decode(responseText);
    var dataLength = data.mapvalues.length;

    for ( var j=0; j < featuresLength; j++ ) 
    {
        features[j].attributes["value"] = 0;
        
        for ( var i=0; i < dataLength; i++ )
        {
            if (features[j].attributes[shpcols[pointLayer][0].geocode] == data.mapvalues[i].geoCode)
            {
                features[j].attributes["name"] = data.mapvalues[i].orgUnit;
                features[j].attributes["value"] = data.mapvalues[i].value;
            }
        }
    }
}

function dataReceivedAssignOrganisationUnit( responseText )
{
    var layers = this.myMap.getLayersByName(choroplethLayerName);
    features = layers[0]['features'];
    
    var relations = Ext.util.JSON.decode(responseText).mapOrganisationUnitRelations;
    
    var uniqueColumn = mapData.map.uniqueColumn;   
    
    for (var i=0; i < features.length; i++)
    {
        var featureId = features[i].attributes[uniqueColumn];
        features[i].attributes['value'] = 0;
        
        for (var j=0; j < relations.length; j++)
        {
            if (relations[j].featureId == featureId)
            {
                features[i].attributes['value'] = 1;
            }
        }
    }
    
    features_mapping = features;
}
