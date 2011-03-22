jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			alphanumericwithbasicpuncspaces : r.userGroup.name.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.userGroup.name.firstletteralphabet,
			range : r.userGroup.name.range
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

	jQuery( "#name" ).attr( "maxlength", r.userGroup.name.range[1] );

	/* remote validation */
	checkValueIsExist( "name", "validateUserGroup.action" );
} );
