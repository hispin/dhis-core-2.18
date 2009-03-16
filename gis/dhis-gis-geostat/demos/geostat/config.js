// TOMCAT PORT
localhost_port = 8180;


// SHAPE FILES
empty = '';
level_1 = '../../../geoserver/wfs?request=GetFeature&typename=who:admin1&outputformat=json&version=1.0.0'; // provinces
level_2 = '../../../geoserver/wfs?request=GetFeature&typename=who:admin2&outputformat=json&version=1.0.0'; // districts
level_3 = '../../../geoserver/wfs?request=GetFeature&typename=who:admin3&outputformat=json&version=1.0.0'; // chiefdoms

shapefiles = new Array(empty, level_1, level_2, level_3);

shpcols = {

    1: [ { type: "Province", // What kind of orgunit. Displayed in the info box.
           name: "NAME", // Shapefile column holding the name of the orgunit. Displayed in the info box.
           geocode: "NAME", // Shapefile column holding a unique value
           value: "value" } ], // Must be set to "value"
    
    2: [ { type: "District",
           name: "NAME",
           geocode: "NAME",
           value: "value", // Must be set to "value"
           parent1: "ADM1_NAME" } ], // Shapefile column holding the name of the parent orgunit (1 level above)
           
    3: [ { type: "Chiefdom",
           name: "CHIEFDOM",
           geocode: "CHIEFDOM",
           value: "value", // Must be set to "value"
           parent1: "DISTRICT", // Shapefile column holding the name of the parent orgunit (1 level above)
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


// LAYER NAMES
choroplethLayerName = "Choropleth";
propSymbolLayerName = "Proportional Symbol";


// LAYOUT
west_width = 270; // viewport west
south_height = 100; // viewport south

gridpanel_width = 255;
combo_width = 150;