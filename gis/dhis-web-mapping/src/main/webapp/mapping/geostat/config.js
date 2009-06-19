// AJAX REQUESTS

path = '../../dhis-web-mapping/';
type = '.action';

// MAP
init_longitude = 15;
init_latitude = 0;
init_zoom = 3;

// LAYERS/WIDGETS/URLS
INIT_URL = 'geojson/init';

CHOROPLETH_LAYERNAME = 'Thematic map';


STATIC1_LAYERNAME = 'Main roads';
STATIC1_URL = 'geojson/sl_mainroads';

// LAYOUT
west_width = 270; // viewport west
north_height = 0;
south_height = 70; // viewport south

gridpanel_width = west_width - 15;
gridpanel_height = 500;
combo_width = 150;
multiselect_height = 260;