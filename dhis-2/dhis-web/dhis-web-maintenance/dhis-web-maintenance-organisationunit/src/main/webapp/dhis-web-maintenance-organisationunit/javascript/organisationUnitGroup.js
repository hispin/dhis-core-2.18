
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOrganisationUnitGroupDetails( unitId )
{
    var request = new Request();
    request.setResponseTypeXML( 'organisationUnitGroup' );
    request.setCallbackSuccess( organisationUnitGroupReceived );
    request.send( 'getOrganisationUnitGroup.action?id=' + unitId );
}

function organisationUnitGroupReceived( unitGroupElement )
{
    setInnerHTML( 'nameField', getElementValue( unitGroupElement, 'name' ) );
    setInnerHTML( 'memberCountField', getElementValue( unitGroupElement, 'memberCount' ) );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove organisation unit group
// -----------------------------------------------------------------------------

function removeOrganisationUnitGroup( unitGroupId, unitGroupName )
{
	removeItem( unitGroupId, unitGroupName, confirm_to_delete_org_unit_group, 'removeOrganisationUnitGroup.action' );
}