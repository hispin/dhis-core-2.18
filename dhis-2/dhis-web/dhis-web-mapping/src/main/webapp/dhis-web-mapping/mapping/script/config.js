//  Country dependent

    FACILITY_LEVEL = 4;

//  MapFish print module

    printConfigUrl = '../../../print-servlet-1.2-SNAPSHOT/pdf/info.json';

//  Ajax requests

    path = '../'; // dhis-web-mapping
    type = '.action';

	path_geoserver = '../../../geoserver/';
	ows = 'ows?service=WMS&request=GetCapabilities';
	wfs = 'wfs?request=GetFeature&typename=';	
	output = '&outputformat=json&version=1.0.0';	

//  Layout

    west_width = 270; // viewport west
    north_height = 0; // viewport north
    south_height = 70; // viewport south

    gridpanel_width = west_width - 15;
    
    multiselect_width = 230;
	
    combo_width = 150;
	combo_list_width = combo_width + 17;
	
	combo_width2 = 133;
	combo_list_width2 = combo_width2 + 17;
	
	combo_width_fieldset = 112;
	combo_list_width_fieldset = combo_width_fieldset + 17;
	
	combo_number_width = 65;
	combo_number_list_width = combo_number_width + 17;
	
    // MENU_TEXTCOLOR = '#666'; // label
	// MENU_TEXTCOLOR_INFO = '#222'; // info text
    MENU_TITLECOLOR_LIGHT = '#444'; // panel title
	// MENU_TITLECOLOR_ADMIN = '#111';
	MENU_EMPTYTEXT = '';
	MENU_LABELSEPARATOR = '';
	
//	Styles

	AA_DARK = 'font-family:arial; font-weight:bold; font-size:11px; color:#111; letter-spacing:0px;';
	AA_MED = 'font-family:lucida sans unicode,arial; font-weight:normal; font-size:11px; color:#333; letter-spacing:0px;';
	AA_LIGHT = 'font-family:lucida sans unicode,arial; font-weight:normal; font-size:11px; color:#888; letter-spacing:0px;';
	LABEL = 'padding-bottom:2px; padding-left:3px;';
	
	ASSIGNED_ROW_COLOR = '#b1ffa1';
	UNASSIGNED_ROW_COLOR = '#ffffff';

//  Ext.message

    msg_highlight_start = '<b><font color="#555555">';
    msg_highlight_end = '</font></b>';      