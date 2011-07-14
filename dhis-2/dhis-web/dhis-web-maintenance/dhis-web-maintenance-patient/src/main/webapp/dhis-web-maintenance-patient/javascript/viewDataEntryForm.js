
var dataElementSelector;
var otherProgramStageDataElements;
var existedDataEntry;

jQuery(function(){
	dataElementSelector = jQuery("#dataElementSelection").dialog({
		title: i18n_dataelement,
		minWidth: 650,
		minHeight: 250,
		width:650,
		autoOpen: false,
		zIndex:99999
	});
	
	otherProgramStageDataElements = jQuery("#otherProgramStageDataElements").dialog({
		title: i18n_dataelement_of_orther_program_stage,
		minWidth: 650,
		minHeight: 250,
		width:650,
		autoOpen: false,
		zIndex:99999
	});
	
	existedDataEntry = jQuery("#existedDataEntry").dialog({
		title: i18n_choose_existing_dataentry,
		minWidth: 400,
		minHeight: 80,
		width:400,
		autoOpen: false,
		zIndex:99999
	});	
});

function openOtherProgramStageDataElements()
{
	otherProgramStageDataElements.dialog("open");
}
	
function openDataElementSelector()
{
	dataElementSelector.dialog("open");
}	

function openloadExistedForm()
{
	existedDataEntry.dialog("open");
}

function loadExistedForm()
{
	jQuery.post("showDataEntryForm.action",{
		dataEntryFormId: getFieldValue( 'existedDataEntryId' )
	}, function( html ){
		jQuery("#designTextarea").ckeditorGet().setData( html );

		var dataEntryFormField = byId('existedDataEntryId');
		var optionField = dataEntryFormField.options[dataEntryFormField.selectedIndex];

		setFieldValue('dataEntryFormId', optionField.value );
		setFieldValue('name', optionField.text );
		
		checkValueIsExist('name', 'validateDataEntryForm.action', {dataEntryFormId:getFieldValue('dataEntryFormId')});
	});
}

function deleteDataEntryForm( associationId )
{
	if( window.confirm( i18n_delete_confirm ) )
	{
		window.location = 'delDataEntryForm.action?associationId=' + associationId;
	}
}

function getProgramStageDataElements( id )
{
	var dataElements = jQuery( "#otherProgramStageDataElements #dataElementIds" );
	dataElements.empty();
	var dataElementIdsStore = jQuery( "#otherProgramStageDataElements #dataElementIdsStore" );
	dataElementIdsStore.empty();
	
	jQuery( "#otherProgramStageDataElements #optionComboIds" ).empty();
	if( id != '' ){
		jQuery.post("getSelectedDataElements.action",{
			associationId: id
		}, function( xml ){			
			jQuery( xml ).find( 'dataElement' ).each( function(i, item ){			
				dataElements.append("<option value='" + jQuery( item ).find( "json" ).text() + "'>" + jQuery( item ).find( "name" ).text() + "</option>");
				dataElementIdsStore.append("<option value='" + jQuery( item ).find( "json" ).text() + "'>" + jQuery( item ).find( "name" ).text() + "</option>");
			});
		});
	}
}

function getOptionCombos( dataElement, target )
{
	var dataElement = JSON.parse( dataElement );
	
	var optionCombo = jQuery( target );
	
	if( dataElement.type=='string' ){
		optionCombo.attr("multiple", "multiple" );
	}else{
		optionCombo.removeAttr( "multiple" );
	}
	
	jQuery.postJSON("../dhis-web-commons-ajax-json/getCategoryOptionCombos.action", {
		id: dataElement.id
	}, function( json ){		
		optionCombo.empty();
		jQuery.each( json.categoryOptionCombos, function(i, item ){
			optionCombo.append( "<option value='{\"id\":\"" + item.id + "\",\"name\":\"" + item.name + "\",\"default\":\"" + item.default + "\"}' selected='true'>" + item.name + "</option>" );
		});
	});
}

function getSelectedValues( jQueryString )
{
	var result = new Array();
	jQuery.each( jQuery( jQueryString ).children(), function(i, item ){
		if( item.selected==true){
			result.push( JSON.parse( item.value ) );
		}
	});
	
	return result;
}


function checkExisted( id )
{	
	var result = false;
	var html = jQuery("#designTextarea").ckeditorGet().getData();
	var input = jQuery( html ).find("select, :text");

	input.each( function(i, item){		
		if( id == item.id ) result = true;		
	});

	return result;
}

function filterDataElements( filter, container, list )
{
	var filterLower = filter.toString().toLowerCase();
	
	var dataElementList = jQuery( container + " " + list );
	dataElementList.empty();
	
	jQuery( container + " " + list + "Store" ).children().each( function(i, item){
		item = jQuery( item );		
		var toMatch = item.text().toString().toLowerCase();		
        if( toMatch.indexOf(filterLower) != -1 ){
			dataElementList.append( "<option value='" + item.attr('value') + "'>" + item.text() + "</option>" );
		}
	});	
}

function validateDataEntryForm( )
{
	$.postJSON(
    	    'validateDataEntryForm.action',
    	    {
    	        dataEntryFormId: getFieldValue('dataEntryFormId'),
				name: getFieldValue('name')
    	    },
    	    function( json )
    	    {
    	    	if ( json.response == "success" )
    	    	{
					byId( 'saveDataEntryForm' ).submit(); 
    	    	}
    	    	else if ( json.response == "error" )
    	    	{
    	    		setHeaderMessage( json.message );
    	    	}
    	    }
    	);
}

function insertDataElement( source, associationId )
{
	var oEditor = jQuery("#designTextarea").ckeditorGet();

	var dataElement = JSON.parse( jQuery( source + ' #dataElementIds').val() );

	if( dataElement == null )
	{
		jQuery( source + " #message_").html( "<b>" + i18n_specify_dataelememt + "</b>" );
		return;
	} else {
		jQuery( source + " #message_").html( "" );
	}

	var categoryOptionCombos = getSelectedValues( source + ' #optionComboIds' );

	var dataElementId = dataElement.id;	
	var dataElementName = dataElement.name;	
	var dataElementType = dataElement.type;
	
	var id = "";

	var htmlCode = "";

	if( dataElementType == "string" )
	{
		if( categoryOptionCombos[0].default == 'true' )
		{		
			var titleValue = dataElementId + "." + dataElementName 
					+ "-" + categoryOptionCombos[0].id + "." + categoryOptionCombos[0].id 
					+ " "+dataElementType+"\"";
			var displayName = dataElementName + "-" + optionComboName + " ]";
			id  = associationId + "-" + dataElementId + "-" + categoryOptionCombos[0].id +"-val";
			htmlCode += "<input name=\"entryfield\" id=\""+ id + "\" value=\"" + displayName + "\" title=\"" + displayName + "\" onkeypress=\"return keyPress(event, this)\" >";			
		}else{	
			var titleValue = "-- " + dataElementId + "." + dataElementName + " ("+dataElementType+") --";
			var displayName = dataElementName;
			id = associationId + "-" + dataElementId + "-val"; 
			htmlCode = "<input name=\"entryselect\" id=\"" + id + "\" value=\"" + displayName + "\" title=\"" + displayName + "\">";
		}		
	} 
	else if ( dataElementType == "bool" )
	{
		var titleValue = "-- " + dataElementId + "." + dataElementName + " ("+dataElementType+") --";
		var displayName = dataElementName;
		id = associationId + "-" + dataElementId + "-val" ;
		htmlCode = "<input name=\"entryselect\" id=\"" + id + "\" value=\"" + displayName + "\" title=\"" + displayName + "\">";
	} 
	else if ( dataElementType == "date" )
	{
		var titleValue = "-- " + dataElementId + "." + dataElementName + " ("+dataElementType+") --";
		var displayName = dataElementName;
		id = associationId + "-" + dataElementId + "-val" ;
		htmlCode = "<input name=\"entryfield\" id=\"" + id + "\" value=\"" + displayName + "\" title=\"" + displayName + "\">";
	} 
	else if ( dataElementType == "int" ) 
	{
		jQuery.each( categoryOptionCombos, function(i, item ){
			optionComboName = item.name;
			optionComboId = item.id;
			var titleValue = "-- " + dataElementId + "." + dataElementName + "-" + optionComboId + "." + optionComboName+" ("+dataElementType+") --";
			var displayName = dataElementName + "-" + optionComboName;
			id = associationId + "-" + dataElementId + "-" + optionComboId + "-val";
			htmlCode += "<input title=\"" + titleValue + "\" value=\"" + displayName + "\" name=\"entryfield\" id=\"" + id + "\" />";
		});
	}
	
	if( checkExisted( id ) )
	{		
		jQuery( source + " #message_").html( "<b>" + i18n_dataelement_is_inserted + "</b>" );
		return;
	}else{
		jQuery( source + " #message_").html("");
	}

	oEditor.insertHtml( htmlCode );
}
