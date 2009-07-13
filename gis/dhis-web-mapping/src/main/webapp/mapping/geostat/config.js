// COUNTRY DEPENDENT

STATIC1_LAYERNAME = 'Main roads';
STATIC1_URL = 'geojson/sl_mainroads';

PHU_P2 = new Array(2, 'SL districts'); // phu grand parent
PHU_P1 = new Array(3, 'SL chiefdoms'); // phu parent
PHU = new Array(4, 'SL facilities');

ORGUNIT = new Array(PHU_P2, PHU_P1, PHU);

COUNTRY_LONGITUDE = -11.8;
COUNTRY_LATITUDE = 8.5;
COUNTRY_ZOOM = 8;






// LAYERS/WIDGETS
INIT_URL = 'geojson/init';
CHOROPLETH_LAYERNAME = 'Thematic map';

// MAP
init_longitude = 15;
init_latitude = 0;
init_zoom = 3;

// AJAX REQUESTS
path = '../../dhis-web-mapping/';
type = '.action';

// LAYOUT
west_width = 270; // viewport west
north_height = 0;
south_height = 70; // viewport south

gridpanel_width = west_width - 15;
gridpanel_height = 700;
combo_width = 150;

// Ext.message
msg_highlight_start = '<b><font color="#555555">';
msg_highlight_end = '</font></b>';
        