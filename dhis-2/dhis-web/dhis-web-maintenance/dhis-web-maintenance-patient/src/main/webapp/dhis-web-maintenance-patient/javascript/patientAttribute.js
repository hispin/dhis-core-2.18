// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientAttributeDetails( patientAttributeId )
{
    var request = new Request();
    request.setResponseTypeXML( 'patientAttribute' );
    request.setCallbackSuccess( patientAttributeReceived );
    request.send( 'getPatientAttribute.action?id=' + patientAttributeId );
}

function patientAttributeReceived( patientAttributeElement )
{
	setFieldValue( 'idField', getElementValue( patientAttributeElement, 'id' ) );
	setFieldValue( 'nameField', getElementValue( patientAttributeElement, 'name' ) );	
    setFieldValue( 'descriptionField', getElementValue( patientAttributeElement, 'description' ) );
    
    var valueTypeMap = { 'int':i18n_number, 'bool':i18n_yes_no, 'string':i18n_text, 'date':i18n_date };
    var valueType = getElementValue( patientAttributeElement, 'valueType' );    
    
    setFieldValue( 'valueTypeField', valueTypeMap[valueType] );    
   
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove Patient Attribute
// -----------------------------------------------------------------------------

function removePatientAttribute( patientAttributeId, name )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removePatientAttributeCompleted );
        window.location.href = 'removePatientAttribute.action?id=' + patientAttributeId;
    }
}

function removePatientAttributeCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'patientAttribute.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add Patient Attribute
// -----------------------------------------------------------------------------

function validateAddPatientAttribute()
{
	
	var url = 'validatePatientAttribute.action?' +
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
        var form = document.getElementById( 'addPatientAttributeForm' );        
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
// Update Patient Attribute
// -----------------------------------------------------------------------------

function validateUpdatePatientAttribute()
{
	
    var url = 'validatePatientAttribute.action?' + 
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
    	var form = document.getElementById( 'updatePatientAttributeForm' );        
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