
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
    setFieldValue( 'usernameField', getElementValue( userElement, 'username' ) );
    setFieldValue( 'surnameField', getElementValue( userElement, 'surname' ) );
    setFieldValue( 'firstNameField', getElementValue( userElement, 'firstName' ) );

    var email = getElementValue( userElement, 'email' );
    setFieldValue( 'emailField', email ? email : '[' + i18n_none + ']' );

    var phoneNumber = getElementValue( userElement, 'phoneNumber' );
	setFieldValue( 'phoneNumberField', phoneNumber ? phoneNumber : '[' + i18n_none + ']' );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove user
// -----------------------------------------------------------------------------

function removeUser( userId, username )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + username );
    
    if ( result )
    {
        window.location.href = 'removeUser.action?id=' + userId;
    }
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
