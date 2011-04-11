jQuery( document ).ready( function()
{
	validation2( 'addUserGroupForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			listValidator( 'memberValidator', 'groupMembers' );
		},
		'rules' : getValidationRules("userGroup")
	} );

	/* remote validation */
	checkValueIsExist( "name", "validateUserGroup.action" );
} );
