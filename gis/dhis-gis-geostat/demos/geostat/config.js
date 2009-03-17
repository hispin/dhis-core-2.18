// TOMCAT PORT
localhost_port = 8180;


// SHAPE FILES
hidden = '../../../geoserver/wfs?request=GetFeature&typename=who:india_st&outputformat=json&version=1.0.0'; // init widget map. not visible. should be as light as possible
level_1 = '../../../geoserver/wfs?request=GetFeature&typename=who:india_st&outputformat=json&version=1.0.0'; // provinces
level_2 = '../../../geoserver/wfs?request=GetFeature&typename=who:india_st&outputformat=json&version=1.0.0'; // districts
level_3 = '../../../geoserver/wfs?request=GetFeature&typename=who:india_ds&outputformat=json&version=1.0.0'; // chiefdoms

shapefiles = new Array(hidden, level_1, level_2, level_3);

shpcols = {

    1: [ { type: "Territory", // What kind of orgunit. Displayed in the info box.
           name: "STATE", // Shapefile column holding the name of the orgunit. Displayed in the info box.
           geocode: "", // Shapefile column holding a unique value
           value: "value" } ], // Must be set to "value"
    
    2: [ { type: "State",
           name: "STATE",
           geocode: "STATE",
           value: "value", // Must be set to "value"
           parent1: "STATE" } ], // Shapefile column holding the name of the parent orgunit (1 level above)
           
    3: [ { type: "District",
           name: "DISTRICT",
           geocode: "DISTRICT",
           value: "value", // Must be set to "value"
           parent1: "STATE", // Shapefile column holding the name of the parent orgunit (1 level above)
           parent2: "PROVINCE" } ], // (2 levels above)
           
    4: [ { type: "Org. unit",
           name: "name",
           geocode: "ID",
           value: "value", // Must be set to "value"
           parent1: "CHIEFDOM", // Shapefile column holding the name of the parent orgunit (1 level above)
           parent2: "DISTRICT", // (2 levels above)
           parent3: "PROVINCE" } ] // (3 levels above)
};

pointLayer = 4; // the shpcols point layer number

init_map = 'who:india_st';
init_longitude = 82;
init_latitude = 22;
init_zoom = 5;



// LAYER NAMES
choroplethLayerName = "Choropleth";
propSymbolLayerName = "Proportional Symbol";


// LAYOUT
west_width = 270; // viewport west
north_height = 0;
south_height = 80; // viewport south

gridpanel_width = 255;
combo_width = 150;