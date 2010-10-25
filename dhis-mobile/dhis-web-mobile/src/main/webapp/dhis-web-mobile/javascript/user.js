function removeUserGroup( userGroupId, userGroupName )
{
    removeItem( userGroupId, userGroupName, i18n_confirm_delete, "removeUserGroup.action" );
}




function showUserGroupDetails( userGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'userGroup' );
    request.setCallbackSuccess( userGroupReceived );
    request.send( 'getUserGroup.action?userGroupId=' + userGroupId );
}

function userGroupReceived( userGroupElement )
{
    setInnerHTML( 'nameField', getElementValue( userGroupElement, 'name' ) );
    setInnerHTML( 'idField', getElementValue( userGroupElement, 'id' ) );
    setInnerHTML( 'noOfGroupField', getElementValue( userGroupElement, 'noOfGroup' ) );

    showDetails();
}