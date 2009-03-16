  // reference local blank image
  Ext.BLANK_IMAGE_URL = '../../mfbase/ext/resources/images/default/s.gif';

  Ext.onReady(function()
  {
      Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
      
      myMap:null;
      
      map = new OpenLayers.Map($('olmap'));
      this.myMap = map;
      
      features = null;
      

      var jpl_wms = new OpenLayers.Layer.WMS("Satellite",
                                             "http://labs.metacarta.com/wms-c/Basic.py?", 
                                             {layers: 'satellite', format: 'image/png'});
                                             
                                             
      var vmap0 = new OpenLayers.Layer.WMS("OpenLayers WMS",
                                           "../../../geoserver/wms?", 
                                           {layers: 'who:sl_init'});
                                           
                                           
      // define choropleth layer and its styling
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

      // define proportional symbol layer and its styling
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




      // create select feature control for choropleth layer
      selectFeatureChoropleth = new OpenLayers.Control.newSelectFeature( choroplethLayer,
          { onClickSelect: onClickSelectChoropleth, onClickUnselect: onClickUnselectChoropleth,
            onHoverSelect: onHoverSelectChoropleth, onHoverUnselect: onHoverUnselectChoropleth }
      );
      
      selectFeaturePoint = new OpenLayers.Control.newSelectFeature( propSymbolLayer,
          { onClickSelect: onClickSelectPoint, onClickUnselect: onClickUnselectPoint,
            onHoverSelect: onHoverSelectPoint, onHoverUnselect: onHoverUnselectPoint }
      );
      

      
      map.addControl(selectFeatureChoropleth);
      map.addControl(selectFeaturePoint);
      selectFeatureChoropleth.activate();
      selectFeaturePoint.activate();

      


      map.setCenter(new OpenLayers.LonLat(-11.8, 8.5), 8);


      
      // create choropleth widget
      choropleth = new mapfish.widgets.geostat.Choropleth({
                              id: 'choropleth',
                              map: map,
                              layer: choroplethLayer,
                              title: 'Choropleth',
                              nameAttribute: "NAME",
                              indicators: [['value', 'Indicator']],
                              url: '../../../geoserver/wfs?request=GetFeature&typename=who:sl_init&outputformat=json&version=1.0.0',
                              featureSelection: false,
                              loadMask: {msg: 'Loading Data...', msgCls: 'x-mask-loading'},
                              defaults: {
                                  width: 130
                              },
                              listeners: {
                       //           collapse: {
                                      // hide layer if collapsed
                                    //  fn: function() {
                                    //      this.layer.setVisibility(false);
                                    //  }
                      //            },
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

      // create proportional symbol layer
      var propSymbol = new mapfish.widgets.geostat.ProportionalSymbol({
                                map: map,
                                layer: propSymbolLayer,
                                title: 'Proportional symbol',
                                nameAttribute: "ouname",
                                indicators: [['PERIMETER', 'Perimeter']],
                                url: '../../../geoserver/wfs?request=GetFeature&typename=who:clinics&outputformat=json&version=1.0.0',
                                featureSelection: false,
                                loadMask : {msg: 'Loading Data...', msgCls: 'x-mask-loading'},
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

/*
var propSymbol = new mapfish.widgets.geostat.ProportionalSymbol({
            'map': map,
            layer: propSymbolLayer,
            title: 'Proportional Symbol',
            'nameAttribute': 'name',
            'indicators': [['population', 'Population']],
            'url': 'cities',
            'loadMask' : {msg: 'Loading Data...', msgCls: 'x-mask-loading'},
            defaults: {
                                  width: 130
                              },
                              listeners: {
                                  collapse: {
                                      // hide layer if collapsed
                                      fn: function() {
                                          this.layer.setVisibility(false);
                                      }
                                  },
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
                          
*/                          


      // create viewport
      viewport = new Ext.Viewport({
          layout: 'border',
          items:[
              new Ext.BoxComponent({ // raw
                  region: 'north',
                  el: 'north',
                  height: 32
              }),{
                  region: 'south',
                  contentEl: 'south',
                  id: 'south-panel',
                  split: true,
                  height: south_height,
                  minSize: 100,
                  maxSize: 100,
                  collapsible: true,
                  title: 'Information',
                  margins: '0 0 0 0',
                  bodyStyle: 'padding:5px; font-family:tahoma; font-size:12px'
              },{
                  region: 'east',
                  title: ' ',
                  width: 200,
                  collapsible: true,
                  collapsed: true,
                  margins: '0 0 0 5',
                  defaults: {
                    border: true,
                    frame: true
                  },
                  layout: 'border',
                  items: [{
                      title: 'Layers',
                      region: 'north',
                      autoHeight: true,
                      xtype: 'layertree',
                      map: map
                  },{
                      title: 'Overview Map',
                      region: 'center',
                      height: 300,
                      html:'<div id="overviewmap"></div>'
                  },{
                      title: 'Position',
                      region: 'south',
                      collapsible: true,
                      height: 65,
                      contentEl: 'position'
                  }]
              },{
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
                      propSymbol
                  ]
              },{
                  region: 'center',
                  id: 'center',
                  title: 'Map',
                  xtype: 'mapcomponent',
                  map: map
              }
           ]
      });
      map.addControl(new OpenLayers.Control.MousePosition({displayClass: "void", 
                                                           div: $('mouseposition'), 
                                                           prefix: 'x: ',
                                                           separator: '<br/>y: '}));
  
      map.addControl(new OpenLayers.Control.OverviewMap({div: $('overviewmap')}));
      
      Ext.get('loading').fadeOut({remove: true});
      
      });
      


      // CHOROPLETH SELECT FEATURES
      
      function onHoverSelectChoropleth(feature)
      {
          var center_panel = Ext.getCmp('center');
          var south_panel = Ext.getCmp('south-panel');

          var height = 230;
          var padding_x = 15;
          var padding_y = 22;
          
          var x = center_panel.x + padding_x;
          var y = south_panel.y - height - padding_y;

//          alert(feature.geometry.getBounds().getCenterLonLat());

          popup_feature = new Ext.Window({
              title: 'Feature',
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
          
          if (choropleth.selectedLevel == 1) {
              var html = style + '<b>' + shpcols[choropleth.selectedLevel][0].type + ':</b>' + space + feature.attributes[shpcols[choropleth.selectedLevel][0].name] + '</p>';
              html += '<br>';
              html += style + '<b>Value:</b>' + space + feature.attributes[shpcols[choropleth.selectedLevel][0].value] + '</p>';
          }
          
          if (choropleth.selectedLevel == 2) {
              var html = style + '<b>' + shpcols[choropleth.selectedLevel][0].type + ':</b>' + space + feature.attributes[shpcols[choropleth.selectedLevel][0].name] + '</p>';
              html += style + '<b>' + shpcols[choropleth.selectedLevel-1][0].type + ':</b>' + space + feature.attributes[shpcols[choropleth.selectedLevel][0].parent1] + '</p>';
              html += '<br>';
              html += style + '<b>Value:</b>' + space + feature.attributes[shpcols[choropleth.selectedLevel][0].value] + '</p>';
          }
          
          if (choropleth.selectedLevel == 3) {
              var html = style + '<b>' + shpcols[choropleth.selectedLevel][0].type + ':</b>' + space + feature.attributes[shpcols[choropleth.selectedLevel][0].name] + '</p>';
              html += style + '<b>' + shpcols[choropleth.selectedLevel-1][0].type + ':</b>' + space + feature.attributes[shpcols[choropleth.selectedLevel][0].parent1] + '</p>';
              html += style + '<b>' + shpcols[choropleth.selectedLevel-2][0].type + ':</b>' + space + feature.attributes[shpcols[choropleth.selectedLevel][0].parent2] + '</p>';
              html += '<br>';
              html += style + '<b>Value:</b>' + space + feature.attributes[shpcols[choropleth.selectedLevel][0].value] + '</p>';
          }

          popup_feature.html = html;
          popup_feature.show();
          
          south_panel.body.dom.innerHTML = 'More information about the selected area may be listed here.';
      }
      
      function onHoverUnselectChoropleth(feature)
      {
          var infoPanel = Ext.getCmp('south-panel');
//          infoPanel.body.dom.innerHTML = '';
          
          popup_feature.hide();
      }
      
      function onClickSelectChoropleth(feature)
      {
          var selected = Ext.getCmp('grid_gp').getSelectionModel().getSelected();
          organisationUnitId = selected.data["id"],
          geoCode = feature.attributes["NAME"];
          
          if (!selected) {
            alert("ikke valgt");
          }
          else {
            Ext.Ajax.request
            ( 
              {
                  url: 'http://localhost:' + localhost_port + '/dhis-webservice/updateOrganisationUnitGeoCode.service',
                  method: 'GET',
                  params: { organisationUnitId: organisationUnitId, geoCode: geoCode },
                  
                  success: function( responseObject )
                  {
                        alert("OK");
                  },
                  failure: function()
                  {
                      alert( 'Status', 'Error while retrieving data' );
                  } 
              }
            );
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
          var center_panel = Ext.getCmp('center');
          var south_panel = Ext.getCmp('south-panel');

          var height = 230;
          var padding_x = 15;
          var padding_y = 22;
          
          var x = center_panel.x + padding_x;
          var y = south_panel.y - height - padding_y;

//          alert(feature.geometry.getBounds().getCenterLonLat());

          popup_orgunit = new Ext.Window({
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
      }
      
      function onHoverUnselectPoint(feature)
      {
          var infoPanel_orgunit = Ext.getCmp('south-panel');
          infoPanel_orgunit.body.dom.innerHTML = '';
          
          popup_orgunit.hide();
      }
      
      function onClickSelectPoint(feature)
      {
      
      }
      
      function onClickUnselectPoint(feature)
      {
      
      }


      // GET DATA

      function getChoroplethData()
      {
        var indicatorId = Ext.getCmp('indicator_cb').getValue();
        var periodId = Ext.getCmp('period_cb').getValue();
        var level = Ext.getCmp('level_cb').getValue();

alert(indicatorId + "\n" + periodId + "\n" + level);        
        
        if (choropleth.isDrillDown) {
            level = choropleth.selectedLevel + 1;
        }
        
        var url = 'http://localhost:' + localhost_port + '/dhis-webservice/getMapValues.service';
        format = 'json';
        
        Ext.Ajax.request
        ( 
          {
          url: url,
          method: 'GET',
          params: { indicatorId: indicatorId, periodId: periodId, level: level, format: format },
          
          success: function( responseObject )
          {
              dataReceivedChoropleth( responseObject.responseText );
          },
          failure: function()
          {
              alert( 'Status', 'Error while retrieving data' );
          } 
          }
        );
        
      }
      
      function getPointData()
      {
        var indicatorId = Ext.getCmp('indicator_cb').getValue();
        var periodId = Ext.getCmp('period_cb').getValue();
        var level = pointLayer;
        
        var url = 'http://localhost:' + localhost_port + '/dhis-webservice/getMapValues.service';
        format = 'json';
        
        Ext.Ajax.request
        ( 
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
          }
        );
        
      }
      
      function getAssignOrganisationUnitData()
      {
          var layers = this.myMap.getLayersByName(choroplethLayerName);
          features = layers[0]["features"];
          var featuresLength = features.length;
       
          for ( var j=0; j < featuresLength; j++ ) 
          {
              features[j].attributes["value"] = 0;
          }
      }




    // DATA RECEIVED

    function dataReceivedChoropleth( responseText )
    {
      var layers = this.myMap.getLayersByName(choroplethLayerName);
      var level = choropleth.selectedLevel;
      var features = layers[0]["features"];
      var featuresLength = features.length;
      var data = Ext.util.JSON.decode(responseText);
      var dataLength = data.mapvalues.length;
      
      for ( var j=0; j < featuresLength; j++ ) 
      {
          features[j].attributes["value"] = 0;

          for ( var i=0; i < dataLength; i++ )
          {
              if (features[j].attributes[shpcols[level][0].geocode] == data.mapvalues[i].geoCode)
              {
                  features[j].attributes["value"] = data.mapvalues[i].value;
              }
          }
       }
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