
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showRoleDetails( roleId )
{
    var request = new Request();
    request.setResponseTypeXML( 'role' );
    request.setCallbackSuccess( roleReceived );
    request.send( 'getRole.action?id=' + roleId );
}

function roleReceived( xmlObject )
{
    setInnerHTML( 'nameField', getElementValue( xmlObject, 'name' ) );
    setInnerHTML( 'membersField', getElementValue( xmlObject, 'members' ) );
    setInnerHTML( 'dataSetsField', getElementValue( xmlObject, 'dataSets' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove role
// -----------------------------------------------------------------------------

function removeRole(id, role)
{
	removeItem( id, role, i18n_confirm_delete, 'removeRole.action' );
}

// -----------------------------------------------------------------------------
// Add role
// -----------------------------------------------------------------------------

function validateAddRole()
{
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    
    request.send( 'validateRole.action?name=' + getFieldValue( 'name' ) +
        '&description=' + getFieldValue( 'description' ) );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        selectAll( document.getElementById( 'selectedList' ) );
		selectAll( document.getElementById( 'selectedListAuthority' ) );
        var form = document.getElementById( 'addRoleForm' );
        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_role_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update role
// -----------------------------------------------------------------------------

function validateUpdateRole()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( 'validateRole.action?id=' + getFieldValue( 'id' ) +
        '&name=' + getFieldValue( 'name' ) +
        '&description=' + getFieldValue( 'description' ) );
    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        selectAll( document.getElementById( 'selectedList' ) );
		selectAll( document.getElementById( 'selectedListAuthority' ) );
        var form = document.getElementById( 'updateRoleForm' );
        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_user_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
