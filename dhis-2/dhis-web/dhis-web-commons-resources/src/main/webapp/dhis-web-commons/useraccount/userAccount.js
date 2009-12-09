// -----------------------------------------------------------------------------
// Update information of current user
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
        var form = document.getElementById( 'updateUserinforForm' );
        
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_user_failed + ':' + '\n' + message );
    }
}
