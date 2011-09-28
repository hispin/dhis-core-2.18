
// -----------------------------------------------------------------------------
// Export to PDF file
// -----------------------------------------------------------------------------

function exportPDF( type )
{	
	var params = "type=" + type;
	params += "&months=" + jQuery( '#months' ).val();

	exportPdfByType( type, params );
}

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
    jQuery.post( 'getUser.action', { id: userId }, function ( json ) {
		setInnerHTML( 'usernameField', json.user.username );
		
		var fullName = json.user.firstName + ", " + json.user.surname;
		setInnerHTML( 'fullNameField', fullName );

		var email = json.user.email;
		setInnerHTML( 'emailField', email ? email : '[' + i18n_none + ']' );

		var phoneNumber = json.user.phoneNumber;
		setInnerHTML( 'phoneNumberField', phoneNumber ? phoneNumber : '[' + i18n_none + ']' );
		
		var lastLogin = json.user.lastLogin;
		setInnerHTML( 'lastLoginField', lastLogin ? lastLogin : '[' + i18n_none + ']' );
		
		var temp = '';
		var orgunits = json.user.orgunits;
		for( var i = 0 ; i < orgunits.length ; i ++ )
		{
			temp += orgunits[i].name + "<br/>";
		}
		setInnerHTML( 'assignedOrgunitField', temp ? temp : '[' + i18n_none + ']' );
		
		temp = '';
		var roles = json.user.roles;
		for( var i = 0 ; i < roles.length ; i ++ )
		{
			temp += roles[i].name + "<br/>";
		}
		setInnerHTML( 'roleField', temp ? temp : '[' + i18n_none + ']' );
		
		showDetails();
	});
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
    jQuery.post( 'getUserGroup.action', { userGroupId: userGroupId },
		function ( json ) {
			setInnerHTML( 'nameField', json.userGroup.name );
			setInnerHTML( 'noOfGroupField', json.userGroup.noOfUsers );

			showDetails();
	});
}

function removeUserGroup( userGroupId, userGroupName )
{
    removeItem( userGroupId, userGroupName, i18n_confirm_delete, 'removeUserGroup.action' );
}
