
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
    setInnerHTML( 'surnameField', getElementValue( userElement, 'surname' ) );
    setInnerHTML( 'firstNameField', getElementValue( userElement, 'firstName' ) );

    var email = getElementValue( userElement, 'email' );
    setInnerHTML( 'emailField', email ? email : '[' + i18n_none + ']' );

    var phoneNumber = getElementValue( userElement, 'phoneNumber' );
	setInnerHTML( 'phoneNumberField', phoneNumber ? phoneNumber : '[' + i18n_none + ']' );

	var numberOrgunit = getElementValue( userElement, 'numberOrgunit' );
	setInnerHTML( 'numberOrgunitField', numberOrgunit ? numberOrgunit : '[' + i18n_none + ']' );
	
	var lastLogin = getElementValue( userElement, 'lastLogin' );;
	setInnerHTML( 'lastLoginField', lastLogin ? lastLogin : '[' + i18n_none + ']' );
	
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
