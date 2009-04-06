// reference local blank image
Ext.BLANK_IMAGE_URL = '../../mfbase/ext/resources/images/default/s.gif';

Ext.onReady(function()
{
    Ext.state.Manager.setProvider(new Ext.state.CookieProvider());

    myMap: null;

    map = new OpenLayers.Map($('olmap'));
    this.myMap = map;

    features = null;

    var jpl_wms = new OpenLayers.Layer.WMS('Satellite',
                                           'http://labs.metacarta.com/wms-c/Basic.py?', 
                                           {layers: 'satellite', format: 'image/png'});
                                     
    var vmap0 = new OpenLayers.Layer.WMS('OpenLayers WMS',
                                         geoserver + 'wms?', 
                                         {layers: default_map}); // config.js
                                   
    var choroplethLayer = new OpenLayers.Layer.Vector(choroplethLayerName, {
        'visibility': false,
        'styleMap': new OpenLayers.StyleMap({
            'default': new OpenLayers.Style(
                OpenLayers.Util.applyDefaults(
                    {'fillOpacity': 1, 'strokeColor': '#222222', 'strokeWidth': 1, 'opacity': 1},
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

    map.addLayers([vmap0, jpl_wms, choroplethLayer, propSymbolLayer]);

    selectFeatureChoropleth = new OpenLayers.Control.newSelectFeature( choroplethLayer,
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
        url: localhost + port + '/dhis-webservice/getOrganisationUnitLevels.service',
        baseParams: { format: 'json' },
        root: 'organisationUnitLevels',
        fields: ['id', 'level', 'name'],
        autoLoad: true
    });

    organisationUnitStore = new Ext.data.JsonStore({
        url: localhost + port + '/dhis-webservice/getOrganisationUnitsAtLevel.service',
        root: 'organisationUnits',
        fields: ['id', 'name'],
        sortInfo: { field: 'name', direction: 'ASC' },
        autoLoad: false
    });

    var organisationUnitLevelCombo = new Ext.form.ComboBox({
        id: 'organisationunitlevel_cb',
        fieldLabel: 'Level',
        typeAhead: true,
        editable: false,
        valueField: 'level',
        emptyText: 'Required',
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        store: organisationUnitLevelStore,
        listeners: {
            'select': {
                fn: function() {
                    var level = Ext.getCmp('organisationunitlevel_cb').getValue();
                    organisationUnitStore.baseParams = { level: level, format: 'json' };
                    organisationUnitStore.reload();
                },
                scope: this
            }
        }
    });
    
    var organisationUnitCombo = new Ext.form.ComboBox({
        id: 'organisationunit_cb',
        fieldLabel: 'Organisation unit',
        typeAhead: true,
        editable: false,
        valueField: 'id',
        emptyText: 'Required',
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        store: organisationUnitStore
    });
    
    var organisationUnitLevelCombo2 = new Ext.form.ComboBox({
        id: 'organisationunitlevel2_cb',
        fieldLabel: 'Level',
        typeAhead: true,
        editable: false,
        valueField: 'id',
        emptyText: 'Required',
        mode: 'remote',
        forceSelection: true,
        triggerAction: 'all',
        selectOnFocus: true,
        width: combo_width,
        store: organisationUnitLevelStore,
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

    var submitButton = new Ext.Button({
        id: 'submit_b',
        text: 'Register map',
        handler: function()
        {
            var mlp = Ext.getCmp('maplayerpath_tf').getValue();
            var oui = Ext.getCmp('organisationunit_cb').getValue();
            var ouli = Ext.getCmp('organisationunitlevel2_cb').getValue();
            var uc = Ext.getCmp('uniquecolumn_tf').getValue();
            var nc = Ext.getCmp('namecolumn_tf').getValue();
            
            if (!mlp || !oui || !ouli || !uc || !nc)
            {
                Ext.MessageBox.alert('Error', 'Form is not complete');
                return;
            }
            
            Ext.Ajax.request(
            {
                url: localhost + port + '/dhis-webservice/addOrUpdateMap.service',
                method: 'GET',
                params: { mapLayerPath: mlp, organisationUnitId: oui, organisationUnitLevelId: ouli, uniqueColumn: uc, nameColumn: nc },

                success: function( responseObject )
                {
                    alert( 'OK' );
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
        items: [ { html: '<p style="padding-bottom:4px">Load organisation units from level:</p>' }, organisationUnitLevelCombo, { html: '<br><br><br>' },
                 { html: '<p style="padding-bottom:4px">Register map for this organisation unit:</p>' }, organisationUnitCombo, { html: '<br>' },
                 { html: '<p style="padding-bottom:4px">Organisation unit level:</p>' }, organisationUnitLevelCombo2, { html: '<br>' },
                 { html: '<p style="padding-bottom:4px">Geoserver map layer path:</p>' }, mapLayerPathTextField, { html: '<br>' },
                 { html: '<p style="padding-bottom:4px">Unique column:</p>' }, uniqueColumnTextField, { html: '<br>' },
                 { html: '<p style="padding-bottom:4px">Name column:</p>' }, nameColumnTextField, { html: '<br>' },
                 submitButton ]
    });

    choropleth = new mapfish.widgets.geostat.Choropleth({
        id: 'choropleth',
        map: map,
        layer: choroplethLayer,
        title: 'Choropleth',
        nameAttribute: 'NAME',
        indicators: [['value', 'Indicator']],
        url: geoserver + 'wfs?request=GetFeature&typename=' + default_map + '&outputformat=json&version=1.0.0',
        featureSelection: false,
        loadMask: {msg: 'Loading shapefile...', msgCls: 'x-mask-loading'},
        'legendDiv': 'myChoroplethLegendDiv',
        defaults:{
            width: 130
        },
        listeners: {
            expand: {
                // show layer if expanded
                fn: function() {
                    if (this.classificationApplied) {
                        this.layer.setVisibility(true);
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
        url: geoserver + 'wfs?request=GetFeature&typename=' + default_map + '&outputformat=json&version=1.0.0',
        featureSelection: false,
        loadMask: {msg: 'Loading shapefile...', msgCls: 'x-mask-loading'},
        'legendDiv' : 'myChoroplethLegendDiv',
        defaults:{
            width: 130
        },
        listeners: {
            expand: {
                // show layer if expanded
                fn: function() {
                    if (this.classificationApplied) {
                        this.layer.setVisibility(true);
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
        nameAttribute: 'ouname',
        indicators: [['PERIMETER', 'Perimeter']],
        url: geoserver + 'wfs?request=GetFeature&typename=' + default_map + '&outputformat=json&version=1.0.0',
        featureSelection: false,
        loadMask : {msg: 'Loading shapefile...', msgCls: 'x-mask-loading'},
        defaults: {
            width: 130
        },
        listeners: {
            //         collapse: {
            // hide layer if collapsed
            //      fn: function() {
            //          this.layer.setVisibility(false);
            //        }
            //       },
            expand: {
                // show layer if expanded
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
                items: [
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
                } ]
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
                items: [
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
        displayClass: 'void', 
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
    var selected = Ext.getCmp('grid_gp').getSelectionModel().getSelected();
    var organisationUnitId = selected.data['organisationUnitId'];
    var organisationUnit = selected.data['organisationUnit'];
    
    var uniqueColumn = mapData.map.uniqueColumn;
    var mlp = mapData.map.mapLayerPath;
    var featureId = feature.attributes[uniqueColumn];

    if (!selected)
    {
        alert('First, select an organisation unit from the list');
    }
    else
    {
        Ext.Ajax.request( 
        {
            url: localhost + port + '/dhis-webservice/addOrUpdateMapOrganisationUnitRelation.service',
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
    }
    
    popup_feature.hide();

    /*
    var cll = feature.geometry.getBounds().getCenterLonLat();
    map.setCenter(new OpenLayers.LonLat(cll.lon, cll.lat), 9);
    choropleth.setUrl(shapefiles[choropleth.selectedLevel+1], true);
    */
}

function onClickUnselectChoropleth(feature)
{
}


// PROPORTIONAL SYMBOL SELECT FEATURES

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

function onClickSelectPoint(feature)
{
}

function onClickUnselectPoint(feature)
{
}


mapData = false;

function setMapData(param)
{
    Ext.Ajax.request( 
    {
        url: localhost + port + '/dhis-webservice/getMapByMapLayerPath.service',
        method: 'GET',
        params: { mapLayerPath: default_map, format: 'json' },

        success: function( responseObject )
        {
            mapData = Ext.util.JSON.decode(responseObject.responseText);
            
            if (param == 'choropleth') {
                getChoroplethData(); }
            if (param == 'point') {
                getPointData(); }
            if (param == 'assignment') {
                getAssignOrganisationUnitData(); }
        },
        failure: function()
        {
            alert( 'Error while retrieving map data: setMapData' );
        } 
    });
}


// GET DATA

function getChoroplethData()
{   
    var indicatorId = Ext.getCmp('indicator_cb').getValue();
    var periodId = Ext.getCmp('period_cb').getValue();
    var level = mapData.map.organisationUnitLevel;
    
    Ext.Ajax.request( 
    {
        url: localhost + port + '/dhis-webservice/getMapValues.service',
        method: 'GET',
        params: { indicatorId: indicatorId, periodId: periodId, level: level, format: 'json' },

        success: function( responseObject )
        {
            dataReceivedChoropleth( responseObject.responseText );
        },
        failure: function()
        {
            alert( 'Error while retrieving data: getChoroplethData' );
        } 
    });
}

function getPointData()
{
/*
    var indicatorId = Ext.getCmp('indicator_cb').getValue();
    var periodId = Ext.getCmp('period_cb').getValue();
    var level = pointLayer;

    Ext.Ajax.request( 
    {
        url: localhost + port + '/dhis-webservice/getMapValues.service',
        method: 'GET',
        params: { indicatorId: indicatorId, periodId: periodId, level: level, format: 'json' },

        success: function( responseObject )
        {
            dataReceivedPoint( responseObject.responseText );
        },
        failure: function()
        {
            alert( 'Error while retrieving data: getPointData' );
        } 
    });
*/
}

function getAssignOrganisationUnitData()
{
    var mlp = mapData.map.mapLayerPath;
    
    Ext.Ajax.request( 
    {
        url: localhost + port + '/dhis-webservice/getAvailableMapOrganisationUnitRelations.service',
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


// DATA RECEIVED

function dataReceivedChoropleth( responseText )
{
    var layers = this.myMap.getLayersByName(choroplethLayerName);
    var features = layers[0]['features'];
    
    var mapvalues = Ext.util.JSON.decode(responseText).mapvalues;
    
    var mlp = mapData.map.mapLayerPath;
    var uniqueColumn = mapData.map.uniqueColumn;
    
    Ext.Ajax.request( 
    {
        url: localhost + port + '/dhis-webservice/getAvailableMapOrganisationUnitRelations.service',
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
            
        },
        failure: function()
        {
            alert( 'Error while retrieving data: dataReceivedChoropleth' );
        } 
    });
}

function dataReceivedPoint( responseText )
{
/*
    var layers = this.myMap.getLayersByName(propSymbolLayerName);
    var features = layers[0]['features'];
    var featuresLength = features.length;
    var data = Ext.util.JSON.decode(responseText);
    var dataLength = data.mapvalues.length;

    for ( var j=0; j < featuresLength; j++ ) 
    {
        features[j].attributes['value'] = 0;
        
        for ( var i=0; i < dataLength; i++ )
        {
            if (features[j].attributes[shpcols[pointLayer][0].geocode] == data.mapvalues[i].geoCode)
            {
                features[j].attributes['name'] = data.mapvalues[i].orgUnit;
                features[j].attributes['value'] = data.mapvalues[i].value;
            }
        }
    }
*/
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
}
