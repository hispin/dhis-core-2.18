// SHAPE FILES

choropleth1 = '../../../geoserver/wfs?request=GetFeature&typename=who:admin0&outputformat=json&version=1.0.0'; // country
choropleth2 = '../../../geoserver/wfs?request=GetFeature&typename=who:admin1&outputformat=json&version=1.0.0'; // provinces
choropleth3 = '../../../geoserver/wfs?request=GetFeature&typename=who:admin2&outputformat=json&version=1.0.0'; // districts
choropleth4 = '../../../geoserver/wfs?request=GetFeature&typename=who:admin3&outputformat=json&version=1.0.0'; // chiefdoms

shapefiles = new Array(choropleth1, choropleth2, choropleth3, choropleth4);

shpcols = {
    1: [ { type: "Province", name: "NAME", geocode: "NAME", value: "value" } ],
    2: [ { type: "District", name: "NAME", geocode: "NAME", value: "value", parent1: "ADM1_NAME" } ],
    3: [ { type: "Chiefdom", name: "CHIEFDOM", geocode: "CHIEFDOM", value: "value", parent1: "PROVINCE", parent2: "DISTRICT" } ],
    4: [ { type: "Org. unit", name: "name", geocode: "ID", value: "value", parent1: "PROVINCE", parent2: "DISTRICT", parent3: "CHIEFDOM" } ]
};

pointLayer = 4; // the shpcols point layer number


// TOMCAT PORT

localhost_port = 8180;


