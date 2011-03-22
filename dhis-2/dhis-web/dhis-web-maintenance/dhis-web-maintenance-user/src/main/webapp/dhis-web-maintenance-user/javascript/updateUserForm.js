jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		rawPassword : {
			password : true,
			notequalto : '#username',
			range : r.user.password.range
		},
		retypePassword : {
			required : false,
			equalTo : '#rawPassword',
			range : r.user.password.range
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

	validation2( 'updateUserForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			listValidator( 'roleValidator', 'selectedList' );
		},
		'rules' : rules
	} );

	jQuery( "#rawPassword" ).attr( "maxlength", r.user.password.range[1] );
	jQuery( "#retypePassword" ).attr( "maxlength", r.user.password.range[1] );
	jQuery( "#surname" ).attr( "maxlength", r.user.name.range[1] );
	jQuery( "#firstName" ).attr( "maxlength", r.user.name.range[1] );
	jQuery( "#email" ).attr( "maxlength", r.user.email.range[1] );
	jQuery( "#phoneNumber" ).attr( "maxlength", r.user.phone.range[1] );

} );
