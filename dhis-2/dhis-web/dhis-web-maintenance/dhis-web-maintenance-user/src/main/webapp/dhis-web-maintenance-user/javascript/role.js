
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showRoleDetails( roleId )
{
    var request = new Request();
    request.setResponseTypeXML( 'role' );
    request.setCallbackSuccess( roleReceived );
    request.send( 'getRole.action?id=' + roleId );
}

function roleReceived( xmlObject )
{
    setInnerHTML( 'nameField', getElementValue( xmlObject, 'name' ) );
    setInnerHTML( 'membersField', getElementValue( xmlObject, 'members' ) );
    setInnerHTML( 'dataSetsField', getElementValue( xmlObject, 'dataSets' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove role
// -----------------------------------------------------------------------------

function removeRole(id, role)
{
	removeItem( id, role, i18n_confirm_delete, 'removeRole.action' );
}