jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		nameField : {
			required : true,
			rangelength : r.dataEntry.name.length
		}
	};

	validation2( 'saveDataEntryForm', function()
	{
		autoSave = false;
		validateDataEntryForm();
	}, {
		'rules' : rules
	} );

	jQuery( "#nameField" ).attr( "maxlength", r.dataEntry.name.length[1] );

	jQuery( "#dataElementSelection" ).resizable( {
		minHeight : 210,
		minWidth : 400,
		width : 400,
		alsoResize : "#dataElementList"
	} );

	jQuery( "#dataElementSelection" ).draggable( {
		handle : 'h3'
	} );

	leftBar.hideAnimated();

	select( 1 );

} );

function timedCount()
{
	validateDataEntryForm();
	t = setTimeout( "timedCount()", 20000 );// 1000 -> 1s
	byId( 'message' ).style.display = 'none';
}

function select( id )
{
	if (selected != null) {
		jQuery( "#tr" + selected ).removeClass( "selected" );
	}

	jQuery( "#tr" + id ).addClass( "selected" );

	selected = id;
}

function insertDataElement()
{
	var oEditor = FCKeditorAPI.GetInstance( 'designTextarea' );
	var viewByValue = getFieldValue( 'viewBySelector' );

	var json = JSON.parse( jQuery( "#json_" + selected ).val() );

	var dataElementId = json.dataElement.id;
	var dataElementName = json.dataElement.name;
	var dataElementType = json.dataElement.type;
	var optionComboName = json.optionCombo.name;
	var optionComboId = json.optionCombo.id;

	if (viewByValue == "deid")
		dispName = "[ " + dataElementId;
	else if (viewByValue == "deshortname")
		dispName = "[ " + json.dataElement.shortName;
	else
		dispName = "[ " + json.dataElement.name;

	var titleValue = "-- " + dataElementId + ". " + dataElementName + " " + optionComboId + ". " + optionComboName
			+ " (" + dataElementType + ") --";

	var displayName = dispName + " - " + optionComboName + " ]";
	var dataEntryId = "value[" + dataElementId + "].value:value[" + optionComboId + "].value";
	var boolDataEntryId = "value[" + dataElementId + "].value:value[" + optionComboId + "].value";

	viewByValue = "@@" + viewByValue + "@@";

	var id = "";
	var html = "";

	if (dataElementType == "bool") {
		id = boolDataEntryId;
		html = "<input title=\"" + titleValue + "\" view=\"" + viewByValue + "\" value=\"" + displayName + "\" id=\""
				+ boolDataEntryId + "\" style=\"width:4em;text-align:center\"/>";
	} else {
		id = dataEntryId;
		html = "<input title=\"" + titleValue + "\" view=\"" + viewByValue + "\" value=\"" + displayName + "\" id=\""
				+ dataEntryId + "\" style=\"width:4em;text-align:center\"/>";
	}

	if (checkExisted( id )) {
		jQuery( "#message_" ).html( "<b>" + i18n_dataelement_is_inserted + "</b>" );
		return;
	} else {
		jQuery( "#message_" ).html( "" );
	}

	oEditor.InsertHtml( html );

}

function checkExisted( id )
{
	var result = false;
	var html = FCKeditorAPI.GetInstance( 'designTextarea' ).GetHTML();
	var input = jQuery( html ).find( "select, :text" );
	input.each( function( i, item )
	{
		if (id == item.id)
			result = true;
	} );

	return result;
}
