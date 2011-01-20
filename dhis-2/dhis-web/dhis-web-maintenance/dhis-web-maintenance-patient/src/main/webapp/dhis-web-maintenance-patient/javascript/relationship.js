// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showRelationshipTypeDetails( relationshipTypeId )
{
    var request = new Request();
    request.setResponseTypeXML( 'relationshipType' );
    request.setCallbackSuccess( relationshipTypeReceived );
    request.send( 'getRelationshipType.action?id=' + relationshipTypeId );
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

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if( type == 'success' )
    {
    	var form = document.getElementById( 'updateRelationshipTypeForm' );        
        form.submit();
    }
    else if( type == 'error' )
    {
        window.alert( i18n_saving_program_failed + ':' + '\n' + message );
    }
    else if( type == 'input' )
    {
        setHeaderMessage( message );
    }
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

function showAddRelationship()
{
	window.location = "showAddRelationshipForm.action";
}

//-----------------------------------------------------------------------------
// Search Relationship Partner
//-----------------------------------------------------------------------------

function validateSearchPartner()
{	
	
	var url = 'validateSearch.action?' +
			'searchText=' + getFieldValue( 'searchText' );	
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( searchValidationCompleted );    
	request.send( url );        

	return false;
}

function searchValidationCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if( type == 'success' )
	{
		var form = document.getElementById( 'relationshipSelectForm' );        
		form.submit();
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
	var relationshipType = document.getElementById( 'relationshipTypeId' );
	var relationshipTypeId = relationshipType.options[relationshipType.selectedIndex].value;
	
	var partnerList = document.getElementById( 'availablePartnersList' );
	var partnerId = -1;
	
	if( partnerList.selectedIndex >= 0 )
	{		
		partnerId = partnerList.options[partnerList.selectedIndex].value;		
	}	
	
	if( relationshipTypeId == "null" || relationshipTypeId == "" )
	{
		window.alert( i18n_please_select_relationship_type );
		
		return;
	}
	
	if( partnerId == "null" || partnerId == "" || partnerId < 0 )
	{
		window.alert( i18n_please_select_partner );
		
		return;
	}
	
	var relTypeId = relationshipTypeId.substr( 0, relationshipTypeId.indexOf(':') );
	var relName = relationshipTypeId.substr( relationshipTypeId.indexOf(':') + 1, relationshipTypeId.length );
	
	var url = 'saveRelationship.action?' + 
		'partnerId=' + partnerId + 
		'&relationshipTypeId=' + relTypeId +
		'&relationshipName=' + relName ;
	
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
		window.location = "getRelationshipList.action";
	}	
	else if( type == 'error' )
	{
		window.alert( i18n_adding_relationship_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		setHeaderMessage( message );
	}
}

//------------------------------------------------------------------------------
// Remove Relationship
//------------------------------------------------------------------------------

function removeRelationship( relationshipId, patientA, aIsToB, patientB )
{	
	
    var result = window.confirm( i18n_confirm_delete_relationship + '\n\n' + patientA + ' is ' + aIsToB + ' to ' + patientB );
    
    if( result )
    {
    	window.location = 'removeRelationship.action?relationshipId=' + relationshipId;   	         
    }
}


/*function removeRelationship( relationshipId, patientA, aIsToB, patientB )
{	
	
    var result = window.confirm( i18n_confirm_delete_relationship + '\n\n' + patientA + ' is ' + aIsToB + ' to ' + patientB );
    
    if( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeRelationshipCompleted );
        request.send( 'removeRelationship.action?relationshipId=' + relationshipId );         
    }
}

function removeRelationshipCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;    
    
    if( type == 'success' )
	{
		window.location = "getRelationshipList.action";
	}	
	else if( type = 'error' )
    {
        setInnerHTML( 'warningField', message );
        
        showWarning();
    }
}*/

//------------------------------------------------------------------------------
// Relationship partner
//------------------------------------------------------------------------------

function manageRepresentative( patientId, partnerId )
{
    var request = new Request();
    request.setResponseTypeXML( 'partner' );
    request.setCallbackSuccess( representativeReceived );
    request.send( 'getPartner.action?patientId=' + patientId + '&partnerId=' + partnerId );
}

function representativeReceived( patientElement )
{		
	var partnerIsRepresentative = getElementValue( patientElement, 'partnerIsRepresentative' );	
	
	var partnerId = '<div><input type="hidden" id="partnerId" name="partnerId" value="' + getElementValue( patientElement, 'id' ) + '"></div>';
	var labelField;	
	var buttonFirstField;
	var buttonSecondField;
	
	if( partnerIsRepresentative == 'true' )
	{
		labelField = i18n_do_you_want_to_remove_this_one_from_being_representative;
		
		buttonFirstField = '<input type="button" value="' + i18n_yes + '" onclick="javascript:removeRepresentative()">'; 
		buttonSecondField = '&nbsp;';
	}
	else if( partnerIsRepresentative == 'false' )
	{
		labelField = i18n_do_you_want_to_make_this_one_a_representative;
		
		buttonFirstField = '<input type="button" value="' + i18n_yes + '" onclick="javascript:saveRepresentative( false )">';
		buttonSecondField= '<input type="button" value="' + i18n_yes_and_attribute + '" onclick="javascript:saveRepresentative( true )">';
	}	
	
	setInnerHTML( 'labelField', labelField );
	setInnerHTML( 'buttonFirstField', buttonFirstField );
	setInnerHTML( 'buttonSecondField', buttonSecondField );
	setInnerHTML( 'partnerIdField', partnerId );	
	setInnerHTML( 'fullNameField', getElementValue( patientElement, 'fullName' ) );
	setInnerHTML( 'genderField', getElementValue( patientElement, 'gender' ) );	
    setInnerHTML( 'dateOfBirthField', getElementValue( patientElement, 'dateOfBirth' ) );    
    setInnerHTML( 'ageField', getElementValue( patientElement, 'age' ) );
	
	var attributes = patientElement.getElementsByTagName( "attribute" );   
    
    var attributeValues = '';
	
	for( var i = 0; i < attributes.length; i++ )
	{		
		attributeValues = attributeValues + '<strong>' + attributes[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue  + ':  </strong>' + attributes[ i ].getElementsByTagName( "value" )[0].firstChild.nodeValue + '<br>';		
	}
	
	setInnerHTML( 'attributeField', attributeValues );   
   
    showPartnerDetail( true );
}

function showPartnerDetail( display )
{   
    var node = document.getElementById( 'relationshipPartnerContainer' );
    
    node.style.display = (display ? 'block' : 'none');   
}


function hideRelationshipPartnerContainer()
{   
    var node = document.getElementById( 'relationshipPartnerContainer' );
    
    node.style.display = 'none';   
}

function saveRepresentative( copyAttribute )
{	
	var representativeId = document.getElementById( 'partnerId' );
	
	var url = 'saveRepresentative.action?representativeId=' + representativeId.value + '&copyAttribute=' + copyAttribute;	
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( saveRepresentativeCompleted );    
	request.send( url );        

	return false;
}

function saveRepresentativeCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if( type == 'success' )
	{
		window.location = "getRelationshipList.action";
	}	
	else if( type == 'error' )
	{
		window.alert( i18n_saving_representative_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		setHeaderMessage( message );
	}
}

function removeRepresentative()
{	
	var representativeId = document.getElementById( 'partnerId' );
	
	var url = 'removeRepresentative.action?representativeId=' + representativeId.value;	
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( removeRepresentativeCompleted );    
	request.send( url );        

	return false;
	
}

function removeRepresentativeCompleted( messageElement )
{
	var type = messageElement.getAttribute( 'type' );
	var message = messageElement.firstChild.nodeValue;
	
	if( type == 'success' )
	{
		window.location = "getRelationshipList.action";
	}	
	else if( type == 'error' )
	{
		window.alert( i18n_removing_representative_failed + ':' + '\n' + message );
	}
	else if( type == 'input' )
	{
		setHeaderMessage( message );
	}
}

//----------------------------------------------------
// Add new relationship with new patient
// ---------------------------------------------------

function showAddRelationshipPatient()
{
	window.location = "showAddRelationshipPatient.action";
}