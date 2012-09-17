// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramDetails( programId )
{
	jQuery.getJSON( "getProgram.action", {
		id:programId
	}, function(json){
		setInnerHTML( 'nameField', json.program.name );
		setInnerHTML( 'descriptionField', json.program.description );
		
		var type = i18n_multiple_events_with_registration;
		if( json.program.type == "2" )
			type = i18n_single_event_with_registration;
		else if( json.program.type == "3"  )
			type = i18n_single_event_without_registration;
		setInnerHTML( 'typeField', type );  
		
		var displayProvidedOtherFacility = ( json.program.displayProvidedOtherFacility == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'displayProvidedOtherFacilityField', displayProvidedOtherFacility );   	
		
		var displayIncidentDate = ( json.program.displayIncidentDate == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'displayIncidentDateField', displayIncidentDate );   	
		
		setInnerHTML( 'dateOfEnrollmentDescriptionField', json.program.dateOfEnrollmentDescription );   
		setInnerHTML( 'dateOfIncidentDescriptionField', json.program.dateOfIncidentDescription );   		
		setInnerHTML( 'programStageCountField',  json.program.programStageCount );
		setInnerHTML( 'durationInDaysField',  json.program.maxDay );
		setInnerHTML( 'noAttributesField', json.program.noAttributes );
		setInnerHTML( 'noIdentifierTypesField', json.program.noIdentifierTypes );
		
		showDetails();
	});   
}

// -----------------------------------------------------------------------------
// Remove Program
// -----------------------------------------------------------------------------

function removeProgram( programId, name )
{
	removeItem( programId, name, i18n_confirm_delete, 'removeProgram.action' );
}

function programOnChange()
{
	var type = getFieldValue('type');
	
	// anonymous
	if(type == "3")
	{
		disable('dateOfEnrollmentDescription');
		disable("displayIncidentDate");
		disable("dateOfIncidentDescription");
	}
	else{
		enable('dateOfEnrollmentDescription');
		enable("displayIncidentDate");
		if(byId('displayIncidentDate').checked){
			enable("dateOfIncidentDescription");
		}
		else {
			disable("dateOfIncidentDescription");
		}
	}
}

// -----------------------------------------------------------------------------
// select identifiers / attributes
// -----------------------------------------------------------------------------

function selectProperties()
{
	var selectedList = jQuery("#selectedList");
	jQuery("#availablePropertyIds").children().each(function(i, item){
		if( item.selected ){
			html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectDataElement( this )'><td onmousedown='select(event,this)'>" + item.text + "</td>";
			html += "<td align='center'><input type='checkbox' name='displayed' value='" + item.value + "'";
			if( item.value.match("^attr_")=="attr_" )
			{
				html += " style='display:none' ";
			}
			html += "></td></tr>";
			selectedList.append( html );
			jQuery( item ).remove();
		}
	});
}


function selectAllProperties()
{
	var selectedList = jQuery("#selectedList");
	jQuery("#availablePropertyIds").children().each(function(i, item){
		html = "<tr class='selected' id='" + item.value + "' ondblclick='unSelectDataElement( this )'><td onmousedown='select(this)'>" + item.text + "</td>";
		html += "<td align='center'><input type='checkbox' name='displayed' value='" + item.value + "'";
		if( item.value.match("^attr_")=="attr_" )
		{
			html += " style='display:none' ";
		}
		html += "'></td></tr>";
		selectedList.append( html );
		jQuery( item ).remove();
	});
}

function unSelectProperties()
{
	var availableList = jQuery("#availablePropertyIds");
	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		if( item.hasClass("selected") )
		{		
			availableList.append( "<option value='" + item.attr( "id" ) + "' selected='true'>" + item.find("td:first").text() + "</option>" );
			item.remove();
		}
	});
}


function unSelectAllProperties()
{
	var availableList = jQuery("#availablePropertyIds");
	jQuery("#selectedList").find("tr").each( function( i, item ){
		item = jQuery(item);
		availableList.append( "<option value='" + item.attr( "id" ) + "' selected='true'>" + item.find("td:first").text() + "</option>" );
		item.remove();
	});
}

function select( event, element )
{
	if ( !getKeyCode( event ) )// Ctrl
	{
		jQuery("#selectedList .selected").removeClass( 'selected' );
	}
	
	element = jQuery( element ).parent();
	if( element.hasClass( 'selected') ) element.removeClass( 'selected' );
	else element.addClass( 'selected' );
}

function getKeyCode(e)
{
	var ctrlPressed=0;

	if (parseInt(navigator.appVersion)>3) {

		var evt = e ? e:window.event;

		if (document.layers && navigator.appName=="Netscape"
		&& parseInt(navigator.appVersion)==4) {
			// NETSCAPE 4 CODE
			var mString =(e.modifiers+32).toString(2).substring(3,6);
			ctrlPressed =(mString.charAt(1)=="1");
		}
		else {
			// NEWER BROWSERS [CROSS-PLATFORM]
			ctrlPressed=evt.ctrlKey;
		}
	}
	return ctrlPressed;
}