
//------------------------------------------------------------------------------
// Add Relationship
//------------------------------------------------------------------------------

function showAddRelationship( entityInstanceId )
{
	hideById('listRelationshipDiv');
	
	jQuery('#loaderDiv').show();
	jQuery('#addRelationshipDiv').load('showAddRelationshipForm.action',
		{
			entityInstanceId:entityInstanceId
		}, function()
		{
			jQuery('[name=addRelationShipLink]').hide();
			showById('addRelationshipDiv');
			hideById('entityInstanceForm');
			jQuery('#loaderDiv').hide();
		});
}

// -----------------------------------------------------------------------------
// Add Relationship TrackedEntityInstance
// -----------------------------------------------------------------------------


//remove value of all the disabled identifier fields
//an identifier field is disabled when its value is inherited from another person ( underAge is true ) 
//we don't save inherited identifiers. Only save the representative id.
function removeRelationshipDisabledIdentifier()
{
	jQuery("#addRelationshipEntityInstanceForm :input.idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).val("");
	});
}


//------------------------------------------------------------------------------
// Relationship partner
//------------------------------------------------------------------------------

function manageRepresentative( entityInstanceId, partnerId )
{		
	$('#relationshipDetails').dialog('destroy').remove();
	$('<div id="relationshipDetails">' ).load( 'getPartner.action', 
		{
			entityInstanceId: entityInstanceId,
			partnerId: partnerId
		}).dialog({
			title: i18n_set_as_representative,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 400,
			height: 300
		});
}

function saveRepresentative( entityInstanceId, representativeId, copyAttribute )
{
	$.post( 'saveRepresentative.action', 
		{ 
			entityInstanceId:entityInstanceId, 
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

function removeRepresentative( entityInstanceId, representativeId )
{	
	$.post( 'removeRepresentative.action', 
		{ 
			entityInstanceId:entityInstanceId, 
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

//-----------------------------------------------------------------------------
// Search Relationship Partner
//-----------------------------------------------------------------------------

function validateSearchPartner()
{
	hideById('searchRelationshipDiv');
	$.ajax({
		url: 'validateSearchRelationship.action',
		type:"POST",
		data: getParamsForDiv('relationshipSelectForm'),
		dataType: "xml",
		success: searchValidationCompleted
		}); 
}

function searchValidationCompleted( messageElement )
{
	messageElement = messageElement.getElementsByTagName( 'message' )[0];
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if( type == 'success' )
	{
		jQuery('#loaderDiv').show();
		$.ajax({
			type: "GET",
			url: 'searchRelationshipTrackedEntityInstance.action',
			data: getParamsForDiv('relationshipSelectForm'),
			success: function( html ) {
				setInnerHTML('searchRelationshipDiv',html);
				showById('searchRelationshipDiv');
				jQuery('#loaderDiv').hide();
			}
		});
		return false;
	}
	else if( type == 'error' )
	{
		setHeaderMessage( i18n_searching_tracked_entity_instance_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		setHeaderMessage( message );
	}
}

function validateAddRelationship(partnerId)
{
	var relationshipTypeId = jQuery( '#relationshipSelectForm [id=relationshipTypeId] option:selected' ).val();	
	if( relationshipTypeId==''){
		setHeaderMessage( i18n_please_select_relationship_type );
		return;
	}
	if( partnerId==null){
		setHeaderMessage( i18n_please_select_a_tracked_entity_instance_for_setting_relationship );
		return;
	}
	addRelationship(partnerId);
}

function addRelationship(partnerId) 
{
	var relationshipTypeId = jQuery( '#relationshipSelectForm [id=relationshipTypeId] option:selected' ).val();	
	var relTypeId = relationshipTypeId.substr( 0, relationshipTypeId.indexOf(':') );
	var relName = relationshipTypeId.substr( relationshipTypeId.indexOf(':') + 1, relationshipTypeId.length );
	
	var params = 'entityInstanceId=' + getFieldValue('entityInstanceId') + 
		'&partnerId=' + partnerId + 
		'&relationshipTypeId=' + relTypeId +
		'&relationshipName=' + relName ;
	
	$.ajax({
		url: 'saveRelationship.action',
		type:"POST",
		data: params,
		dataType: "xml",
		success:  function( messageElement ) {
			messageElement = messageElement.getElementsByTagName( 'message' )[0];
			var type = messageElement.getAttribute( 'type' );
			var message = messageElement.firstChild.nodeValue;
			
			if( type == 'success' ){
				jQuery('#searchRelationshipDiv [id=tr' + partnerId + ']').css("background-color","#C0C0C0")
				setHeaderMessage( i18n_save_success );
			}	
			else if( type == 'error' ){
				setHeaderMessage( i18n_adding_relationship_failed + ':' + '\n' + message );
			}
			else if( type == 'input' ){
				setHeaderMessage( message );
			}
		}
	}); 
		
	return false;
}

//------------------------------------------------------------------------------
// Remove Relationship
//------------------------------------------------------------------------------

function removeRelationship( relationshipId, entityInstanceA, aIsToB, entityInstanceB )
{	
	removeItem( relationshipId, entityInstanceA + ' is ' + aIsToB + ' to ' + entityInstanceB, i18n_confirm_delete_relationship, 'removeRelationship.action' );
}