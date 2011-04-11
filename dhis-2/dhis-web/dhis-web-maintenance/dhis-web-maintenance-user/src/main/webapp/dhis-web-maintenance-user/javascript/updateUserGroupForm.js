jQuery( document ).ready( function()
{
	validation2( 'editUserGroupForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			listValidator( 'memberValidator', 'groupMembers' );
		},
		'rules' : getValidationRules("userGroup")
	} );
} );
