
// -----------------------------------------------------------------------------
// Add Relationship Patient
// -----------------------------------------------------------------------------

function showAddRelationshipPatient( patientId, isShowPatientList )
{
	hideById( 'selectDiv' );
	hideById( 'searchPatientDiv' );
	hideById( 'listPatientDiv' );
	hideById( 'listRelationshipDiv' );
	setInnerHTML('addPatientDiv', '');
	setInnerHTML('updatePatientDiv', '');
	
	jQuery('#loaderDiv').show();
	jQuery('#addRelationshipDiv').load('showAddRelationshipPatient.action',
		{
			id:patientId
		}, function()
		{
			showById('addRelationshipDiv');
			setFieldValue( 'isShowPatientList', isShowPatientList );
			jQuery('#loaderDiv').hide();
		});
}

function validateAddRelationshipPatient()
{
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addRelationshipPatientCompleted ); 
	request.sendAsPost( getParamsForDiv('addRelationshipDiv') );	
    request.send( "validateAddRelationshipPatient.action" );        

    return false;
}

function addRelationshipPatientCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	removeRelationshipDisabledIdentifier();
    	addRelationshipPatient();
    }
    else if ( type == 'error' )
    {
        showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        showWarningMessage( message );
    }
    else if( type == 'duplicate' )
    {
    	if( !checkedDuplicate )
		{
    		showListPatientDuplicate(messageElement, true);
		}
    }
}

function addRelationshipPatient()
{
	jQuery('#loaderDiv').show();
	$.ajax({
      type: "POST",
      url: 'addRelationshipPatient.action',
      data: getParamsForDiv('addRelationshipDiv'),
      success: function( json ) {
		hideById('addRelationshipDiv');
		showById('selectDiv');
		showById('searchPatientDiv');
		showById('listPatientDiv');
		jQuery('#loaderDiv').hide();
		
		if( getFieldValue( 'isShowPatientList' ) == 'false' )
		{
			showRelationshipList( getFieldValue('id') );
		}else
		{
			loadPatientList();
		}
      }
     });
    return false;
}

//remove value of all the disabled identifier fields
//an identifier field is disabled when its value is inherited from another person ( underAge is true ) 
//we don't save inherited identifiers. Only save the representative id.
function removeRelationshipDisabledIdentifier()
{
	jQuery("#addRelationshipPatientForm :input.idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).val("");
	});
}


//------------------------------------------------------------------------------
// Relationship partner
//------------------------------------------------------------------------------

function manageRepresentative( patientId, partnerId )
{		
	$('#relationshipDetails').dialog('destroy').remove();
	$('<div id="relationshipDetails">' ).load( 'getPartner.action', 
		{
			patientId: patientId,
			partnerId: partnerId
		}).dialog({
			title: i18n_relationship_management,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 400,
			height: 300
		});
}

function saveRepresentative( patientId, representativeId, copyAttribute )
{
	$.post( 'saveRepresentative.action', 
		{ 
			patientId:patientId, 
			representativeId: representativeId,
			copyAttribute: copyAttribute	
		}, saveRepresentativeCompleted );
}

function saveRepresentativeCompleted( messageElement )
{
	var type = $(messageElement).find('message').attr('type');
	var message = $(messageElement).find('message').text();
		
	if( type == 'success' )
	{
		jQuery('#relationshipDetails').dialog('close');
	}	
	else if( type == 'error' )
	{
		showErrorMessage( i18n_saving_representative_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		showWarningMessage( message );
	}
}

function removeRepresentative( patientId, representativeId )
{	
	$.post( 'removeRepresentative.action', 
		{ 
			patientId:patientId, 
			representativeId: representativeId 
		}, removeRepresentativeCompleted );
}

function removeRepresentativeCompleted( messageElement )
{
	var type = $(messageElement).find('message').attr('type');
	var message = $(messageElement).find('message').text();
	
	if( type == 'success' )
	{
		$('#relationshipDetails').dialog('close');
	}	
	else if( type == 'error' )
	{
		showErrorMessage( i18n_removing_representative_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		showWarningMessage( message );
	}
}