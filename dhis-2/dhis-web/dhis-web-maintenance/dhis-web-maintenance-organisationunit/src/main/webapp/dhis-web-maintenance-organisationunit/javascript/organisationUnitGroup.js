
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOrganisationUnitGroupDetails( unitId )
{
    var request = new Request();
    request.setResponseTypeXML( 'organisationUnitGroup' );
    request.setCallbackSuccess( organisationUnitGroupReceived );
    request.send( 'getOrganisationUnitGroup.action?id=' + unitId );
}

function organisationUnitGroupReceived( unitGroupElement )
{
    setFieldValue( 'nameField', getElementValue( unitGroupElement, 'name' ) );
    setFieldValue( 'memberCountField', getElementValue( unitGroupElement, 'memberCount' ) );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove organisation unit group
// -----------------------------------------------------------------------------

function removeOrganisationUnitGroup( unitGroupId, unitGroupName )
{
    var result = window.confirm( confirm_to_delete_org_unit_group + '\n\n' + unitGroupName );
    
    if ( result )
    {
        window.location.href = 'removeOrganisationUnitGroup.action?id=' + unitGroupId;
    }
}

// -----------------------------------------------------------------------------
// Add organisation unit group
// -----------------------------------------------------------------------------

function validateAddOrganisationUnitGroup()
{
	var params = 'name=' + getFieldValue( 'name' ) + "&" + getQueryStringFromList( 'groupMembers', 'groupMembers' );
	
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.sendAsPost( params );
    request.send( 'validateOrganisationUnitGroup.action' );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        selectAllById( 'groupMembers' );        
        document.getElementById( 'addOrganisationUnitGroupForm' ).submit();
    }
    else if ( type == 'error' )
    {
        window.alert( adding_the_org_unit_group_failed + ':\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

function getFieldValue( fieldId )
{
    return document.getElementById( fieldId ).value;
}

// -----------------------------------------------------------------------------
// Update organisation unit group
// -----------------------------------------------------------------------------

function validateUpdateOrganisationUnitGroup()
{
	var params = 'id=' + getFieldValue( 'id' ) + '&name=' + getFieldValue( 'name' ) + "&" + getQueryStringFromList( 'groupMembers', 'groupMembers' );
		
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.sendAsPost( params );
    request.send( 'validateOrganisationUnitGroup.action' );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;

    if ( type == 'success' )
    {
    	selectAllById( 'groupMembers' );
        document.getElementById( 'updateOrganisationUnitGroupForm' ).submit();
    }
    else if ( type == 'error' )
    {
        window.alert( saving_the_org_unit_group_failed + ':\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
