// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validateAddValidationRule()
{
    var params = 'name=' + getFieldValue( 'name' );
    params += '&operator=' + getFieldValue( 'operator' );
    params += '&leftSideExpression=' + getFieldValue( 'leftSideExpression' );
    params += '&leftSideDescription=' + getFieldValue( 'leftSideDescription' );
    params += '&rightSideExpression=' + getFieldValue( 'rightSideExpression' );
    params += '&rightSideDescription=' + getFieldValue( 'rightSideDescription' );
    params += '&periodTypeName=' + getFieldValue( 'periodTypeName' );
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.sendAsPost( params );
    request.send( 'validateValidationRule.action' );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;

    if ( type == 'success' )
    {
        byId( 'periodTypeName' ).disabled = false;

        var form = document.getElementById( 'addValidationRuleForm' );
        form.submit();
    } else if ( type == 'error' )
    {
        window.alert( i18n_adding_validation_rule_failed + ':' + '\n' + message );
    } else if ( type == 'input' )
    {
        setMessage( message );
    }
}

// -----------------------------------------------------------------------------
// Update validation rule
// -----------------------------------------------------------------------------

function validateUpdateValidationRule()
{
    var params = 'name=' + getFieldValue( 'name' );
    params += 'id=' + getFieldValue( 'id' );
    params += '&operator=' + getFieldValue( 'operator' );
    params += '&leftSideExpression=' + getFieldValue( 'leftSideExpression' );
    params += '&leftSideDescription=' + getFieldValue( 'leftSideDescription' );
    params += '&rightSideExpression=' + getFieldValue( 'rightSideExpression' );
    params += '&rightSideDescription=' + getFieldValue( 'rightSideDescription' );
    params += '&periodTypeName=' + getFieldValue( 'periodTypeName' );

    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.sendAsPost( params );
    request.send( 'validateValidationRule.action' );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;

    if ( type == 'success' )
    {
        byId( 'periodTypeName' ).disabled = false;

        var form = document.getElementById( 'updateValidationRuleForm' );
        form.submit();
    } else if ( type == 'error' )
    {
        window.alert( i18n_saving_validation_rule_failed + ':' + '\n' + message );
    } else if ( type == 'input' )
    {
        setMessage( message );
    }
}

// ---------------------------------------------------------------------
// disabled PeriodType field
// ---------------------------------------------------------------------
function disabledPeriodTypeField()
{
    if ( getFieldValue( 'leftSideExpression' ) == '' && getFieldValue( 'rightSideExpression' ) == '' )
    {
        byId( 'periodTypeName' ).disabled = false;
    } else
    {
        byId( 'periodTypeName' ).disabled = true;
    }
}
