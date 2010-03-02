//  Country dependent

    FACILITY_LEVEL = 4;

//  MapFish print module

    var layerOverrides = {
        "World": {overview: true},
        Countries: { format: 'image/svg+xml' }
    };

    var printConfigUrl = '../../pdf/info.json';

//  Ajax requests

    path = '../'; // dhis-web-mapping
    type = '.action';

	path_geoserver = '../../../geoserver/';
	ows = 'ows?service=WMS&request=GetCapabilities';
	wfs = 'wfs?request=GetFeature&typename=';	
	output = '&outputformat=json&version=1.0.0';
	
//	Help strings

	thematicMap = 'thematicMap';
	mapRegistration = 'map';
	organisationUnitAssignment = 'mapOrganisationUnitRelation';
	overlayRegistration = 'overlay';
	administration = 'administration';
	favorites = 'favoriteMapView';
	legendSets = 'legendSet';

//  Layout

    west_width = 270; // viewport west
    north_height = 0; // viewport north
    south_height = 70; // viewport south
    gridpanel_width = west_width - 15;
    multiselect_width = 210;
	combo_width = 150;
	combo_list_width = combo_width + 17;
	combo_width2 = 133;
	combo_list_width2 = combo_width2 + 17;
	combo_width_fieldset = 112;
	combo_list_width_fieldset = combo_width_fieldset + 17;
	combo_number_width = 65;
	combo_number_list_width = combo_number_width + 17;
	
	emptytext = '';
	labelseparator = '';
	
//	Styles

	assigned_row_color = '#90ee90';
	unassigned_row_color = '#ffffff';
	
//	DHIS variables

	MAP_SOURCE_TYPE_DATABASE = 'database';
	MAP_SOURCE_TYPE_GEOJSON = 'geojson';
	MAP_SOURCE_TYPE_SHAPEFILE = 'shapefile';

//  Ext.message

    msg_highlight_start = '<b><span color="#555555">';
    msg_highlight_end = '</span></b>';      