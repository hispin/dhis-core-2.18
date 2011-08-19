// -----------------------------------------------------------------------------
// Organisation unit selection listener
// -----------------------------------------------------------------------------

$( document ).ready( function()
{
    selection.setListenerFunction( organisationUnitSelected, true );
} );

function organisationUnitSelected( orgUnitIds )
{
    window.location.href = 'organisationUnit.action';
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOrganisationUnitDetails( unitId )
{
    var request = new Request();
    request.setResponseTypeXML( 'organisationUnit' );
    request.setCallbackSuccess( organisationUnitReceived );
    request.send( '../dhis-web-commons-ajax/getOrganisationUnit.action?id=' + unitId );
}

function organisationUnitReceived( unitElement )
{
    setInnerHTML( 'nameField', getElementValue( unitElement, 'name' ) );
    setInnerHTML( 'shortNameField', getElementValue( unitElement, 'shortName' ) );
    setInnerHTML( 'openingDateField', getElementValue( unitElement, 'openingDate' ) );

    var orgUnitCode = getElementValue( unitElement, 'code' );
    setInnerHTML( 'codeField', orgUnitCode ? orgUnitCode : '[' + none + ']' );

    var closedDate = getElementValue( unitElement, 'closedDate' );
    setInnerHTML( 'closedDateField', closedDate ? closedDate : '[' + none + ']' );

    var commentValue = getElementValue( unitElement, 'comment' );
    setInnerHTML( 'commentField', commentValue ? commentValue.replace( /\n/g, '<br>' ) : '[' + none + ']' );

    var active = getElementValue( unitElement, 'active' );
    setInnerHTML( 'activeField', active == 'true' ? yes : no );

    var url = getElementValue( unitElement, 'url' );
    setInnerHTML( 'urlField', url ? '<a href="' + url + '">' + url + '</a>' : '[' + none + ']' );

    var lastUpdated = getElementValue( unitElement, 'lastUpdated' );
    setInnerHTML( 'lastUpdatedField', lastUpdated ? lastUpdated : '[' + none + ']' );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove organisation unit
// -----------------------------------------------------------------------------

function removeOrganisationUnit( unitId, unitName )
{
    removeItem( unitId, unitName, confirm_to_delete_org_unit, 'removeOrganisationUnit.action', subtree.refreshTree );
}
