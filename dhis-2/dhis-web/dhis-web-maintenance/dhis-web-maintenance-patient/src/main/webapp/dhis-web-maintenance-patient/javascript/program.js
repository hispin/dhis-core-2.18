// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showProgramDetails( programId )
{
	jQuery.post( "getProgram.action", {
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

// -----------------------------------------------------------------------------
// Create validation for dataelements into program-stages
// -----------------------------------------------------------------------------

var dataelementId;
function getDataElementsFromStage(stageId, dataelementId){

	this.dataelementId = dataelementId;
	clearListById(dataelementId);
	
	if(stageId != ''){
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( getDataElementsFromStageReceived);
		request.send( "getProgramStage.action?id=" + stageId );
	}	
}

function getDataElementsFromStageReceived(xmlObject){
	
	var dataelementField = byId(dataelementId);
	clearListById(dataelementId);
	
	xmlObject = xmlObject.getElementsByTagName('dataElements')[0];
	var dataelementList = xmlObject.getElementsByTagName( 'dataElement' );
  
	for ( var i = 0; i < dataelementList.length; i++ )
    {
        var id = dataelementList[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var name = dataelementList[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;

        var option = document.createElement("option");
        option.value = id;
        option.text = name;
        option.title = name;
        
        dataelementField.add(option, null);       	
    }
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

