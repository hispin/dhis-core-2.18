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
		
		var singleEvent = ( json.program.singleEvent == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'singleEventField', singleEvent );  
		
		var anonymous = ( json.program.anonymous == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'anonymousField', anonymous );   		
		
		var displayProvidedOtherFacility = ( json.program.displayProvidedOtherFacility == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'displayProvidedOtherFacilityField', displayProvidedOtherFacility );   	
		
		setInnerHTML( 'dateOfEnrollmentDescriptionField', json.program.dateOfEnrollmentDescription );   
		setInnerHTML( 'dateOfIncidentDescriptionField', json.program.dateOfIncidentDescription );   		
		setInnerHTML( 'programStageCountField',  json.program.programStageCount );
		setInnerHTML( 'maxDaysFromStartField',  json.program.maxDay );
		
		var hideIncidentDateField = ( json.program.hideDateOfIncident == 'true') ? i18n_yes : i18n_no;
		setInnerHTML( 'hideIncidentDateField',  hideIncidentDateField );
		
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

function singleEventOnChange()
{
	var checked = byId('singleEvent').checked;
	
	if(checked)
	{
		disable('dateOfEnrollmentDescription');
		enable('anonymous');
	}
	else
	{
		enable('dateOfEnrollmentDescription');
		byId('anonymous').checked = false;
		disable('anonymous');
	}
}

function hideIncidentDateOnchange()
{
	var checked = byId( 'hideDateOfIncident' ).checked;
	
	if( checked)
	{
		disable( 'dateOfIncidentDescription' );
	}
	else
	{
		enable( 'dateOfIncidentDescription' );
	}
}

