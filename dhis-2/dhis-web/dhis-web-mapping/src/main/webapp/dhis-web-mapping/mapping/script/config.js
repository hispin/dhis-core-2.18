var GLOBALS = {};

GLOBALS.config = {

//  Ajax requests

    path_mapping: '../',
    path_commons: '../../dhis-web-commons-ajax-json/',
    path_geoserver: '../../../geoserver/',
    type: '.action',
	
	ows: 'ows?service=WMS&request=GetCapabilities',
	wfs: 'wfs?request=GetFeature&typename=',	
	output: '&outputformat=json&version=1.0.0',
	
//	Help strings

	thematicMap: 'gisThematicMap',
    thematicMap2: 'gisThematicMap2',
	mapRegistration: 'gisMap',
	organisationUnitAssignment: 'gisMapOrganisationUnitRelation',
    overlayRegistration: 'gisOverlay',
	administration: 'gisAdministration',
	favorites: 'gisFavoriteMapView',
	legendSets: 'gisLegendSet',
	pdfprint: 'gisPdfPrint',

//  Layout

    north_height: 0, // viewport north
    west_width: 270, // viewport west
    gridpanel_width: 270 - 15,
    multiselect_width: 210,
	combo_width: 150,
	combo_width_fieldset: 112,
	combo_list_width_fieldset: 112 + 17,
	combo_number_width: 65,
	
	emptytext: '',
	labelseparator: '',
	
//	Styles

	assigned_row_color: '#90ee90',
	unassigned_row_color: '#ffffff',
	
//	DHIS variables

	map_source_type_database: 'database',
	map_source_type_geojson: 'geojson',
	map_source_type_shapefile: 'shapefile',
	map_legend_type_automatic: 'automatic',
	map_legend_type_predefined: 'predefined',
    map_layer_type_baselayer: 'baselayer',
    map_layer_type_overlay: 'overlay',
	map_value_type_indicator: 'indicator',
	map_value_type_dataelement: 'dataelement',
    map_date_type_fixed: 'fixed',
    map_date_type_start_end: 'start-end',
    
//  MapFish

    classify_with_bounds: 1,
    classify_by_equal_intervals: 2,
    classify_by_quantils: 3,

//  MapFish print module

    layerOverrides: {"World": {overview: true}, Countries: {format: 'image/svg+xml'}},
    printConfigUrl: '../../pdf/info.json'
};