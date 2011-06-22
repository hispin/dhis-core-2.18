function showIndicatorGroupDetails( indicatorGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'indicatorGroup' );
    request.setCallbackSuccess( indicatorGroupReceived );
    request.send( '../dhis-web-commons-ajax/getIndicatorGroup.action?id=' + indicatorGroupId );
}

function indicatorGroupReceived( indicatorGroupElement )
{
    setInnerHTML( 'nameField', getElementValue( indicatorGroupElement, 'name' ) );
    setInnerHTML( 'memberCountField', getElementValue( indicatorGroupElement, 'memberCount' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove indicator group
// -----------------------------------------------------------------------------

function removeIndicatorGroup( indicatorGroupId, indicatorGroupName )
{
    removeItem( indicatorGroupId, indicatorGroupName, i18n_confirm_delete, 'removeIndicatorGroup.action' );
}
