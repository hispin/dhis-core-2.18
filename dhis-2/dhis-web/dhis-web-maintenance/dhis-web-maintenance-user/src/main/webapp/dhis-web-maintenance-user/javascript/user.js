
// -----------------------------------------------------------------------------
// Search users
// -----------------------------------------------------------------------------

function searchUserName()
{
	var key = getFieldValue( 'key' );
    
    if ( key != '' ) 
    {
		jQuery( '#userForm' ).load( 'searchUser.action', {key:key}, unLockScreen );
    	lockScreen();
    }
    else 
    {
    	jQuery( '#userForm' ).submit();
    }
}

function getInactiveUsers()
{
	var months = $( '#months' ).val();
	
	window.location.href = 'alluser.action?months=' + months;
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showUserDetails( userId )
{
    var request = new Request();
    request.setResponseTypeXML( 'user' );
    request.setCallbackSuccess( userReceived );
    request.send( 'getUser.action?id=' + userId );
}

function userReceived( userElement )
{
    setInnerHTML( 'usernameField', getElementValue( userElement, 'username' ) );
	
	var fullName = getElementValue( userElement, 'firstName' ) + ", " + getElementValue( userElement, 'surname' );
    setInnerHTML( 'fullNameField', fullName );

    var email = getElementValue( userElement, 'email' );
    setInnerHTML( 'emailField', email ? email : '[' + i18n_none + ']' );

    var phoneNumber = getElementValue( userElement, 'phoneNumber' );
	setInnerHTML( 'phoneNumberField', phoneNumber ? phoneNumber : '[' + i18n_none + ']' );
	
	var lastLogin = getElementValue( userElement, 'lastLogin' );;
	setInnerHTML( 'lastLoginField', lastLogin ? lastLogin : '[' + i18n_none + ']' );
	
	var temp = '';
	var orgunits = userElement.getElementsByTagName( 'orgunit' );
	for( var i = 0 ; i < orgunits.length ; i ++ )
	{
		temp += orgunits[i].firstChild.nodeValue + "<br/>";
	}
	setInnerHTML( 'assignedOrgunitField', temp ? temp : '[' + i18n_none + ']' );
	
	temp = '';
	var roles = userElement.getElementsByTagName( 'role' );
	for( var i = 0 ; i < roles.length ; i ++ )
	{
		temp += roles[i].firstChild.nodeValue + "<br/>";
	}
	setInnerHTML( 'roleField', temp ? temp : '[' + i18n_none + ']' );
	
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove user
// -----------------------------------------------------------------------------

function removeUser( userId, username )
{
	removeItem( userId, username, i18n_confirm_delete, 'removeUser.action' );
}

// -----------------------------------------------------------------------------
// Usergroup functionality
// -----------------------------------------------------------------------------

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
    setInnerHTML( 'noOfGroupField', getElementValue( userGroupElement, 'noOfUsers' ) );

    showDetails();
}

function removeUserGroup( userGroupId, userGroupName )
{
    removeItem( userGroupId, userGroupName, i18n_confirm_delete, 'removeUserGroup.action' );
}
