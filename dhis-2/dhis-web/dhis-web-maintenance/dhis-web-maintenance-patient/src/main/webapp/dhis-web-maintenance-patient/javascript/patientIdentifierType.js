// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientIdentifierTypeDetails( patientIdentifierTypeId )
{
    var request = new Request();
    request.setResponseTypeXML( 'patientIdentifierType' );
    request.setCallbackSuccess( patientIdentifierTypeReceived );
    request.send( 'getPatientIdentifierType.action?id=' + patientIdentifierTypeId );
}

function patientIdentifierTypeReceived( patientIdentifierTypeElement )
{
	setInnerHTML( 'idField', getElementValue( patientIdentifierTypeElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( patientIdentifierTypeElement, 'name' ) );	
    setInnerHTML( 'descriptionField', getElementValue( patientIdentifierTypeElement, 'description' ) );
    setInnerHTML( 'formatField', getElementValue( patientIdentifierTypeElement, 'format' ) );
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove Patient Attribute
// -----------------------------------------------------------------------------

function removePatientIdentifierType( patientIdentifierTypeId, name )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removePatientIdentifierTypeCompleted );
        request.send('removePatientIdentifierType.action?id=' + patientIdentifierTypeId);
        return false;
    }
}

function removePatientIdentifierTypeCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	window.location.href = 'patientIdentifierType.action';
    }
    else if ( type = 'error' )
    {
        setInnerHTML( 'warningField', message );
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add Patient Attribute
// -----------------------------------------------------------------------------

function validateAddPatientIdentifierType()
{
	
	var url = 'validatePatientIdentifierType.action?' +
			'nameField=' + getFieldValue( 'nameField' ) +			
	        '&description=' + getFieldValue( 'description' );
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );    
    request.send( url );        

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addPatientIdentifierTypeForm' ); 
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_patient_atttibute_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
// -----------------------------------------------------------------------------
// Update Patient Identifier Type
// -----------------------------------------------------------------------------

function validateUpdatePatientIdentifierType()
{
	
    var url = 'validatePatientIdentifierType.action?' + 
    		'id=' + getFieldValue( 'id' ) +
    		'&nameField=' + getFieldValue( 'nameField' ) +			
	        '&description=' + getFieldValue( 'description' );
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );   
    
    request.send( url );
        
    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	var form = document.getElementById( 'updatePatientIdentifierTypeForm' );        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_program_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
