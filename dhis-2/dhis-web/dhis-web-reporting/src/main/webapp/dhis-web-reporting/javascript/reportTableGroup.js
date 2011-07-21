function showReportTableGroupDetails( reportTableGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'reportTableGroup' );
    request.setCallbackSuccess( reportTableGroupReceived );
    request.send( 'getReportTableGroup.action?id=' + reportTableGroupId );
}

function reportTableGroupReceived( reportTableGroupElement )
{
    setInnerHTML( 'nameField', getElementValue( reportTableGroupElement, 'name' ) );
    setInnerHTML( 'memberCountField', getElementValue( reportTableGroupElement, 'memberCount' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove report table group
// -----------------------------------------------------------------------------

function removeReportTableGroup( reportTableGroupId, reportTableGroupName )
{
    removeItem( reportTableGroupId, reportTableGroupName, i18n_confirm_delete, 'removeReportTableGroup.action' );
}
