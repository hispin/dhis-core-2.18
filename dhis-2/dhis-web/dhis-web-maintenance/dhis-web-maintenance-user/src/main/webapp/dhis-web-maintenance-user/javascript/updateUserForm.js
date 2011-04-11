jQuery( document ).ready( function()
{
	validation2( 'updateUserForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			listValidator( 'roleValidator', 'selectedList' );
		},
		'rules' : getValidationRules("user")
	} );

	jQuery("#cancel").click(function() {
		referrerBack( "alluser.action" );
	});		
} );
