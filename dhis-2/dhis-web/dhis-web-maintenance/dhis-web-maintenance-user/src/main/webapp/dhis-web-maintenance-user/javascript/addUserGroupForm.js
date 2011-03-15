jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			alphanumericwithbasicpuncspaces : r.userGroup.name.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.userGroup.name.firstletteralphabet,
			rangelength : r.userGroup.name.length
		},
		memberValidator : {
			required : true
		}
	};

	validation2( 'addUserGroupForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			listValidator( 'memberValidator', 'groupMembers' );
		},
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", r.userGroup.name.length[1] );

	/* remote validation */
	checkValueIsExist( "name", "validateUserGroup.action" );
} );
