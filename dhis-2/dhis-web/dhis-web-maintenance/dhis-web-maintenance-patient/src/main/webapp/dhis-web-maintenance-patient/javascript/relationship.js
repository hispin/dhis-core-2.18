// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showRelationshipTypeDetails( relationshipTypeId )
{
  	$.ajax({
		url: 'getRelationshipType.action?id=' + relationshipTypeId,
		cache: false,
		dataType: "xml",
		success: relationshipTypeReceived
	});
}

function relationshipTypeReceived( relationshipTypeElement )
{
	setInnerHTML( 'idField', getElementValue( relationshipTypeElement, 'id' ) );
	setInnerHTML( 'aIsToBField', getElementValue( relationshipTypeElement, 'aIsToB' ) );	
	setInnerHTML( 'bIsToAField', getElementValue( relationshipTypeElement, 'bIsToA' ) );       
	setInnerHTML( 'descriptionField', getElementValue( relationshipTypeElement, 'description' ) );
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Add RelationshipType
// -----------------------------------------------------------------------------

function validateAddRelationshipType()
{
	$.postJSON(
    	    'validateRelationshipType.action',
    	    {
    	        "aIsToB": getFieldValue( 'aIsToB' ),
				"bIsToA": getFieldValue( 'bIsToA' )
    	    },
    	    function( json )
    	    {
    	    	if ( json.response == "success" )
    	    	{
					var form = document.getElementById( 'addRelationshipTypeForm' );        
					form.submit();
    	    	}else if ( json.response == "input" )
    	    	{
    	    		setHeaderMessage( json.message );
    	    	}
    	    	else if ( json.response == "error" )
    	    	{
    	    		setHeaderMessage( "i18n_adding_patient_atttibute_failed + ':' + '\n'" +json.message );
    	    	}
    	    }
    	);
}

// -----------------------------------------------------------------------------
// Update RelationshipType
// -----------------------------------------------------------------------------

function validateUpdateRelationshipType()
{
	$.postJSON(
    	    'validateRelationshipType.action',
    	    {
				"id": getFieldValue( 'id' ),
    	        "aIsToB": getFieldValue( 'aIsToB' ),
				"bIsToA": getFieldValue( 'bIsToA' )
    	    },
    	    function( json )
    	    {
    	    	if ( json.response == "success" )
    	    	{
					var form = document.getElementById( 'updateRelationshipTypeForm' );        
					form.submit();
    	    	}else if ( json.response == "input" )
    	    	{
    	    		setHeaderMessage( json.message );
    	    	}
    	    	else if ( json.response == "error" )
    	    	{
    	    		setHeaderMessage( "i18n_adding_patient_atttibute_failed + ':' + '\n'" +json.message );
    	    	}
    	    }
    	);
}

// -----------------------------------------------------------------------------
// Remove RelationshipType
// -----------------------------------------------------------------------------	

function removeRelationshipType( relationshipTypeId, aIsToB, bIsToA )
{
    removeItem( relationshipTypeId, aIsToB + "/" + bIsToA, i18n_confirm_delete, 'removeRelationshipType.action' );
}

//------------------------------------------------------------------------------
// Add Relationship
//------------------------------------------------------------------------------

function showAddRelationship( patientId )
{
	hideById('listRelationshipDiv');
	
	jQuery('#loaderDiv').show();
	jQuery('#addRelationshipDiv').load('showAddRelationshipForm.action',
		{
			patientId:patientId
		}, function()
		{
			showById('addRelationshipDiv');
			jQuery('#loaderDiv').hide();
		});
}

//-----------------------------------------------------------------------------
// Search Relationship Partner
//-----------------------------------------------------------------------------

function validateSearchPartner()
{	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( searchValidationCompleted );    
	request.sendAsPost(getParamsForDiv('relationshipSelectForm'));
	request.send( 'validateSearch.action' );        

	return false;
}

function searchValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if( type == 'success' )
	{
		jQuery('#loaderDiv').show();
		jQuery("#relationshipSelectForm :input").each(function()
			{
				jQuery(this).attr('disabled', 'disabled');
			});
			
		$.ajax({
			type: "GET",
			url: 'searchRelationshipPatient.action',
			data: getParamsForDiv('relationshipSelectForm'),
			success: function( json ) {
				clearListById('availablePartnersList');
				for ( i in json.patients ) 
				{
					addOptionById( 'availablePartnersList', json.patients[i].id, json.patients[i].fullName );
				} 
				
				jQuery("#relationshipSelectForm :input").each(function()
					{
						jQuery(this).removeAttr('disabled');
					});
					
				jQuery('#loaderDiv').hide();
			}
		});
		return false;
	}
	else if( type == 'error' )
	{
		window.alert( i18n_searching_patient_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		setHeaderMessage( message );
	}
}

function addRelationship() 
{
	var relationshipTypeId = jQuery( '#relationshipSelectForm [id=relationshipTypeId]' ).val();
	var partnerId = jQuery( '#relationshipSelectForm [id=availablePartnersList]' ).val();
	
	var relTypeId = relationshipTypeId.substr( 0, relationshipTypeId.indexOf(':') );
	var relName = relationshipTypeId.substr( relationshipTypeId.indexOf(':') + 1, relationshipTypeId.length );
	
	var url = 'saveRelationship.action?' + 
		'patientId=' + getFieldValue('patientId') + 
		'&partnerId=' + partnerId + 
		'&relationshipTypeId=' + relTypeId +
		'&relationshipName=' + relName ;
	
	jQuery('#loaderDiv').show();
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( addRelationshipCompleted );    
	request.send( url );
	
	return false;
	
}

function addRelationshipCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if( type == 'success' )
	{
		showSuccessMessage( i18n_save_success );
	}	
	else if( type == 'error' )
	{
		showErrorMessage( i18n_adding_relationship_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		showWarningMessage( message );
	}
	jQuery('#loaderDiv').hide();
}

//------------------------------------------------------------------------------
// Remove Relationship
//------------------------------------------------------------------------------

function removeRelationship( relationshipId, patientA, aIsToB, patientB )
{	
	removeItem( relationshipId, patientA + ' is ' + aIsToB + ' to ' + patientB, i18n_confirm_delete_relationship, 'removeRelationship.action' );
}

