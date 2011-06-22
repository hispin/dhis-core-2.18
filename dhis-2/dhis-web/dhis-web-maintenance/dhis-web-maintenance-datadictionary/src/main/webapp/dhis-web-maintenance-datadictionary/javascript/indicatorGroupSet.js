// -----------------------------------------------------------------------------
// Show Data Element Group Set details
// -----------------------------------------------------------------------------

function showIndicatorGroupSetDetails( id )
{
    var request = new Request();
    request.setResponseTypeXML( 'indicatorGroupSet' );
    request.setCallbackSuccess( showDetailsCompleted );
    request.sendAsPost( "id=" + id );
    request.send( "../dhis-web-commons-ajax/getIndicatorGroupSet.action" );
}

function showDetailsCompleted( indicatorGroupSet )
{
    setInnerHTML( 'nameField', getElementValue( indicatorGroupSet, 'name' ) );
    setInnerHTML( 'memberCountField', getElementValue( indicatorGroupSet, 'memberCount' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Delete Indicator Group Set
// -----------------------------------------------------------------------------

function deleteIndicatorGroupSet( groupSetId, groupSetName )
{
    removeItem( groupSetId, groupSetName, i18n_confirm_delete, "deleteIndicatorGroupSet.action" );
}
