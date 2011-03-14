jQuery( document ).ready( function()
{
	var rules = {
		'name' : {
			required : true,
			minlength : 2,
			alphanumericwithbasicpuncspaces : true,
			firstletteralphabet : true
		},
		'memberValidator' : {
			required : true
		}
	};

	validation2( 'editUserGroupForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			listValidator( 'memberValidator', 'groupMembers' );
		},
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", "210" );
} );
