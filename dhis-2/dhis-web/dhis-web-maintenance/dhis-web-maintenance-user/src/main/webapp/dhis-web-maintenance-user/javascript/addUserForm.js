jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		username : {
			required : true,
			firstletteralphabet : r.user.username.firstletteralphabet,
			alphanumeric : r.user.username.alphanumeric,
			rangelength : r.user.username.rangelength
		},
		rawPassword : {
			required : true,
			password : true,
			notequalto : '#username',
			rangelength : r.user.password.rangelength
		},
		retypePassword : {
			required : true,
			equalTo : '#rawPassword'
		},
		surname : {
			required : true,
			rangelength : r.user.name.rangelength
		},
		firstName : {
			required : true,
			rangelength : r.user.name.rangelength
		},
		email : {
			email : true,
			rangelength : r.user.email.rangelength
		},
		phoneNumber : {
			rangelength : r.user.phone.rangelength
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

	jQuery( "#username" ).attr( "maxlength", r.user.username.rangelength[1] );
	jQuery( "#rawPassword" ).attr( "maxlength", r.user.password.rangelength[1] );
	jQuery( "#retypePassword" ).attr( "maxlength", r.user.password.rangelength[1] );
	jQuery( "#surname" ).attr( "maxlength", r.user.name.rangelength[1] );
	jQuery( "#firstName" ).attr( "maxlength", r.user.name.rangelength[1] );
	jQuery( "#email" ).attr( "maxlength", r.user.email.rangelength[1] );
	jQuery( "#phoneNumber" ).attr( "maxlength", r.user.phone.rangelength[1] );

	/* remote validation */
	checkValueIsExist( "username", "validateUser.action" );
} );
