function showChartGroupDetails( chartGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'chartGroup' );
    request.setCallbackSuccess( chartGroupReceived );
    request.send( 'getChartGroup.action?id=' + chartGroupId );
}

function chartGroupReceived( chartGroupElement )
{
    setInnerHTML( 'nameField', getElementValue( chartGroupElement, 'name' ) );
    setInnerHTML( 'memberCountField', getElementValue( chartGroupElement, 'memberCount' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove chart group
// -----------------------------------------------------------------------------

function removeChartGroup( chartGroupId, chartGroupName )
{
    removeItem( chartGroupId, chartGroupName, i18n_confirm_delete, 'removeChartGroup.action' );
}
