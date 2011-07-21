function showReportGroupDetails( reportGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'reportGroup' );
    request.setCallbackSuccess( reportGroupReceived );
    request.send( 'getReportGroup.action?id=' + reportGroupId );
}

function reportGroupReceived( reportGroupElement )
{
    setInnerHTML( 'nameField', getElementValue( reportGroupElement, 'name' ) );
    setInnerHTML( 'memberCountField', getElementValue( reportGroupElement, 'memberCount' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove report group
// -----------------------------------------------------------------------------

function removeReportGroup( reportGroupId, reportGroupName )
{
    removeItem( reportGroupId, reportGroupName, i18n_confirm_delete, 'removeReportGroup.action' );
}
