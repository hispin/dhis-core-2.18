jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		username : {
			required : true,
			firstletteralphabet : r.user.username.firstletteralphabet,
			alphanumeric : r.user.username.alphanumeric,
			range : r.user.username.range
		},
		rawPassword : {
			required : true,
			password : true,
			notequalto : '#username',
			range : r.user.password.range
		},
		retypePassword : {
			required : true,
			equalTo : '#rawPassword'
		},
		surname : {
			required : true,
			range : r.user.name.range
		},
		firstName : {
			required : true,
			range : r.user.name.range
		},
		email : {
			email : true,
			range : r.user.email.range
		},
		phoneNumber : {
			range : r.user.phone.range
		},
		roleValidator : {
			required : true
		}
	};

	validation2( 'addUserForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			listValidator( 'roleValidator', 'selectedList' );
		},
		'rules' : rules
	} );

	jQuery( "#username" ).attr( "maxlength", r.user.username.range[1] );
	jQuery( "#rawPassword" ).attr( "maxlength", r.user.password.range[1] );
	jQuery( "#retypePassword" ).attr( "maxlength", r.user.password.range[1] );
	jQuery( "#surname" ).attr( "maxlength", r.user.name.range[1] );
	jQuery( "#firstName" ).attr( "maxlength", r.user.name.range[1] );
	jQuery( "#email" ).attr( "maxlength", r.user.email.range[1] );
	jQuery( "#phoneNumber" ).attr( "maxlength", r.user.phone.range[1] );

	/* remote validation */
	checkValueIsExist( "username", "validateUser.action" );
} );
