
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUserDetails( userId )
{
    var request = new Request();
    request.setResponseTypeXML( 'user' );
    request.setCallbackSuccess( userReceived );
    request.send( 'getUser.action?id=' + userId );
}

function userReceived( userElement )
{
    setInnerHTML( 'usernameField', getElementValue( userElement, 'username' ) );
    setInnerHTML( 'surnameField', getElementValue( userElement, 'surname' ) );
    setInnerHTML( 'firstNameField', getElementValue( userElement, 'firstName' ) );

    var email = getElementValue( userElement, 'email' );
    setInnerHTML( 'emailField', email ? email : '[' + i18n_none + ']' );

    var phoneNumber = getElementValue( userElement, 'phoneNumber' );
	setInnerHTML( 'phoneNumberField', phoneNumber ? phoneNumber : '[' + i18n_none + ']' );

    showDetails();
}

// -----------------------------------------------------------------------------
// Add / remove organisation units
// -----------------------------------------------------------------------------

function selectAllInGroup()
{
    var id = getListValue( "organisationUnitGroup" );
    
    var request = new Request();
    request.setCallbackSuccess( groupReceived );
    request.send( 'selectOrganisationUnitGroupMembers.action?organisationUnitGroupId=' + id );    
}

function removeAllInGroup()
{
    var id = getListValue( "organisationUnitGroup" );
    
    var request = new Request();
    request.setCallbackSuccess( groupReceived );
    request.send( 'removeOrganisationUnitGroupMembers.action?organisationUnitGroupId=' + id );
}

function groupReceived()
{
    selectionTree.buildSelectionTree();
}

// -----------------------------------------------------------------------------
// Remove user
// -----------------------------------------------------------------------------

function removeUser( userId, username )
{
	removeItem( userId, username, i18n_confirm_delete, 'removeUser.action' );
}

// -----------------------------------------------------------------------------
// Add user
// -----------------------------------------------------------------------------

function validateAddUser()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.send( 'validateUser.action?username=' + getFieldValue( 'username' ) +
        '&surname=' + getFieldValue( 'surname' ) +
        '&firstName=' + getFieldValue( 'firstName' ) +
        '&rawPassword=' + getFieldValue( 'rawPassword' ) + 
        '&retypePassword=' + getFieldValue( 'retypePassword' ) +
        '&email=' + getFieldValue( 'email' ) );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        selectAll( document.getElementById( 'selectedList' ) );

        var form = document.getElementById( 'addUserForm' );
        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_user_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update user
// -----------------------------------------------------------------------------

function validateUpdateUser()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( 'validateUser.action?id=' + getFieldValue( 'id' ) +
        '&username=' + getFieldValue( 'username' ) +
        '&surname=' + getFieldValue( 'surname' ) +
        '&firstName=' + getFieldValue( 'firstName' ) +
        '&rawPassword=' + getFieldValue( 'rawPassword' ) + 
        '&retypePassword=' + getFieldValue( 'retypePassword' ) +
        '&email=' + getFieldValue( 'email' ) );
        
    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        selectAll( document.getElementById( 'selectedList' ) );

        var form = document.getElementById( 'updateUserForm' );
        
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
