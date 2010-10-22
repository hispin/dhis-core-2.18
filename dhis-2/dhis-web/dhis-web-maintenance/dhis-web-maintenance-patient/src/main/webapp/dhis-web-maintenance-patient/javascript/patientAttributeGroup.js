// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientAttributeGroupDetails( patientAttributeGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'patientAttributeGroup' );
    request.setCallbackSuccess( patientAttributeGroupReceived );
    request.send( 'getPatientAttributeGroup.action?id=' + patientAttributeGroupId );
}

function patientAttributeGroupReceived( patientAttributeGroupElement )
{
	setInnerHTML( 'idField', getElementValue( patientAttributeGroupElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( patientAttributeGroupElement, 'name' ) );	
    setInnerHTML( 'descriptionField', getElementValue( patientAttributeGroupElement, 'description' ) );
	setInnerHTML( 'requiredField', getElementValue( patientAttributeGroupElement, 'required' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove Patient Attribute
// -----------------------------------------------------------------------------

function removePatientAttributeGroup( patientAttributeGroupId, name )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + name );
    
    if ( result )
    {
    	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removePatientAttributeGroupCompleted );
        window.location.href = 'removePatientAttributeGroup.action?id=' + patientAttributeGroupId;
    }
}

function removePatientAttributeGroupCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'patientAttributeGroup.action';
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

function validateAddPatientAttributeGroup()
{
	var params  = 'nameField=' + getFieldValue( 'nameField' );		
	    params += '&description=' + getFieldValue( 'description' );
		params += '&' + getParamString( 'selectedAttributes' );
		
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
	request.sendAsPost(params);	
    request.send( 'validatePatientAttributeGroup.action' );        

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
		selectAllById('selectedAttributes');
        var form = document.getElementById( 'addPatientAttributeGroupForm' );        
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

function validateUpdatePatientAttributeGroup()
{
	var params  = 'id=' + getFieldValue( 'id' );
		params += '&nameField=' + getFieldValue( 'nameField' );		
	    params += '&description=' + getFieldValue( 'description' );
		params += '&' + getParamString( 'selectedAttributes' );
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationGroupCompleted );   
    request.sendAsPost(params);
    request.send( 'validatePatientAttributeGroup.action' );
        
    return false;
}

function updateValidationGroupCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
		selectAllById('selectedAttributes');
    	var form = document.getElementById( 'updatePatientAttributeGroupForm' );        
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

// -----------------------------------------------------------------------------
// Show and Hide tooltip
// -----------------------------------------------------------------------------

function patientAttributeGroupAssociation(){
	selectAllById('selectedAttributeGroups');
    var form = document.getElementById( 'patientAttributeGroupAssociationForm' );        
    form.submit();
}

// -----------------------------------------------------------------------------
// Show and Hide tooltip
// -----------------------------------------------------------------------------

function showToolTip( e, value){
	
	var tooltipDiv = byId('tooltip');
	tooltipDiv.style.display = 'block';
	
	var posx = 0;
    var posy = 0;
	
    if (!e) var e = window.event;
    if (e.pageX || e.pageY)
    {
        posx = e.pageX;
        posy = e.pageY;
    }
    else if (e.clientX || e.clientY)
    {
        posx = e.clientX;
        posy = e.clientY;
    }
	
	tooltipDiv.style.left= posx  + 8 + 'px';
	tooltipDiv.style.top = posy  + 8 + 'px';
	tooltipDiv.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +   value;
}

function hideToolTip(){
	byId('tooltip').style.display = 'none';
}