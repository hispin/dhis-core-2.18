jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			alphanumericwithbasicpuncspaces : r.userGroup.name.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.userGroup.name.firstletteralphabet,
			rangelength : r.userGroup.name.rangelength
		},
		memberValidator : {
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

	jQuery( "#name" ).attr( "maxlength", r.userGroup.name.rangelength[1] );
} );
