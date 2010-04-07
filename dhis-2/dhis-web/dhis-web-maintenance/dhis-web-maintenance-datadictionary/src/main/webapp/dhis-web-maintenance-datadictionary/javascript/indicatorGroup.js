
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showIndicatorGroupDetails( indicatorGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'indicatorGroup' );
    request.setCallbackSuccess( indicatorGroupReceived );
    request.send( 'getIndicatorGroup.action?id=' + indicatorGroupId );
}

function indicatorGroupReceived( indicatorGroupElement )
{
    setFieldValue( 'nameField', getElementValue( indicatorGroupElement, 'name' ) );
    setFieldValue( 'memberCountField', getElementValue( indicatorGroupElement, 'memberCount' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove indicator group
// -----------------------------------------------------------------------------

function removeIndicatorGroup( indicatorGroupId, indicatorGroupName )
{
	removeItem( indicatorGroupId, indicatorGroupName, i18n_confirm_delete, 'removeIndicatorGroup.action' );
}

// -----------------------------------------------------------------------------
// Add indicator group
// -----------------------------------------------------------------------------

function validateAddIndicatorGroup()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.send( 'validateIndicatorGroup.action?name=' + getFieldValue( 'name' ) );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var availableIndicators = document.getElementById( 'availableIndicators' );
        availableIndicators.selectedIndex = -1;
        
        var groupMembers = document.getElementById( 'groupMembers' );
        for ( var i = 0; i < groupMembers.options.length; ++i )
        {
            groupMembers.options[i].selected = true;
        }
        
        var form = document.getElementById( 'addIndicatorGroupForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_indicator_group_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update indicator group
// -----------------------------------------------------------------------------

function validateUpdateIndicatorGroup()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( 'validateIndicatorGroup.action?id=' + getFieldValue( 'id' ) +
        '&name=' + getFieldValue( 'name' ) );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var availableIndicators = document.getElementById( 'availableIndicators' );
        availableIndicators.selectedIndex = -1;
        
        var groupMembers = document.getElementById( 'groupMembers' );
        for ( var i = 0; i < groupMembers.options.length; ++i )
        {
            groupMembers.options[i].selected = true;
        }

        var form = document.getElementById( 'updateIndicatorGroupForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_indicator_group_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
