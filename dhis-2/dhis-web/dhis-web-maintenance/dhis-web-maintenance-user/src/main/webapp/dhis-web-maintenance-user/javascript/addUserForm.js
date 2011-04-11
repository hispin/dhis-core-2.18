jQuery( document ).ready( function()
{
	validation2( 'addUserForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			listValidator( 'roleValidator', 'selectedList' );
		},
		'rules' : getValidationRules("user")
	} );
	
	/* remote validation */
	checkValueIsExist( "username", "validateUser.action" );

	jQuery("#cancel").click(function() {
		referrerBack( "alluser.action" );
	});		
});
