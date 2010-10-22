//  Country dependent variables

    FACILITY_LEVEL = 4;

//  MapFish print module

    var layerOverrides = {"World": {overview: true}, Countries: {format: 'image/svg+xml'}};
    var printConfigUrl = '../../pdf/info.json';

//  Ajax requests

    path_mapping = '../';
    path_commons = '../../dhis-web-commons-ajax-json/';
    path_geoserver = '../../../geoserver/';
    type = '.action';
	
	ows = 'ows?service=WMS&request=GetCapabilities';
	wfs = 'wfs?request=GetFeature&typename=';	
	output = '&outputformat=json&version=1.0.0';
	
//	Help strings

	thematicMap = 'gisThematicMap';
    thematicMap2 = 'gisThematicMap2';
	mapRegistration = 'gisMap';
	organisationUnitAssignment = 'gisMapOrganisationUnitRelation';
    overlayRegistration = 'gisOverlay';
	administration = 'gisAdministration';
	favorites = 'gisFavoriteMapView';
	legendSets = 'gisLegendSet';
	pdfprint = 'gisPdfPrint';

//  Layout

    west_width = 270; // viewport west
    north_height = 0; // viewport north
    south_height = 70; // viewport south
    gridpanel_width = west_width - 15;
    multiselect_width = 210;
	combo_width = 150;
	// combo_list_width = combo_width + 17;
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

	map_source_type_database = 'database';
	map_source_type_geojson = 'geojson';
	map_source_type_shapefile = 'shapefile';
	map_legend_type_automatic = 'automatic';
	map_legend_type_predefined = 'predefined';
    map_layer_type_baselayer = 'baselayer';
    map_layer_type_overlay = 'overlay';
	map_value_type_indicator = 'indicator';
	map_value_type_dataelement = 'dataelement';