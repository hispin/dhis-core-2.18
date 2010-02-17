
// -----------------------------------------------------------------------------
// Organisation unit selection listener
// -----------------------------------------------------------------------------

function organisationUnitSelected( orgUnitIds )
{
    window.location.href = 'organisationUnit.action';
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOrganisationUnitDetails( unitId )
{
    var request = new Request();
    request.setResponseTypeXML( 'organisationUnit' );
    request.setCallbackSuccess( organisationUnitReceived );
    request.send( 'getOrganisationUnit.action?id=' + unitId );
}

function organisationUnitReceived( unitElement )
{
    setFieldValue( 'nameField', getElementValue( unitElement, 'name' ) );
    setFieldValue( 'shortNameField', getElementValue( unitElement, 'shortName' ) );
    setFieldValue( 'openingDateField', getElementValue( unitElement, 'openingDate' ) );
    
    var closedDate = getElementValue( unitElement, 'closedDate' );
    setFieldValue( 'closedDateField', closedDate ? closedDate : '[' + none + ']' );

    var commentValue = getElementValue( unitElement, 'comment' );
    setFieldValue( 'commentField', commentValue ? commentValue.replace( /\n/g, '<br>' ) : '[' + none + ']' );
    
    var active = getElementValue( unitElement, 'active' );
    setFieldValue( 'activeField', active == 'true' ? yes : no );
    
    var url = getElementValue( unitElement, 'url' );
    setFieldValue( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + none + ']' );
    
    var lastUpdated = getElementValue( unitElement, 'lastUpdated' );
    setFieldValue( 'lastUpdatedField', lastUpdated ? lastUpdated : '[' + none + ']' );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove organisation unit
// -----------------------------------------------------------------------------

function removeOrganisationUnit( unitId, unitName )
{
	removeItem( unitId, unitName, confirm_to_delete_org_unit, 'removeOrganisationUnit.action' );
}

// -----------------------------------------------------------------------------
// Add organisation unit
// -----------------------------------------------------------------------------

function validateAddOrganisationUnit()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.send( 'validateOrganisationUnit.action?name=' + getFieldValue( 'name' ) +
        '&shortName=' + getFieldValue( 'shortName' ) +
        '&openingDate=' + getFieldValue( 'openingDate' ) );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addOrganisationUnitForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( adding_the_org_unit_failed + ':\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update organisation unit
// -----------------------------------------------------------------------------

function validateUpdateOrganisationUnit()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( 'validateOrganisationUnit.action?id=' + getFieldValue( 'id' ) +
        '&name=' + getFieldValue( 'name' ) +
        '&shortName=' + getFieldValue( 'shortName' ) +
        '&code=' + getFieldValue( 'code' ) +
        '&openingDate=' + getFieldValue( 'openingDate' ) +
        '&closedDate=' + getFieldValue( 'closedDate' ) );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'updateOrganisationUnitForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( saving_the_org_unit_failed + ':\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
