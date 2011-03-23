jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		rawPassword : {
			password : true,
			notequalto : '#username',
			rangelength : r.user.password.length
		},
		retypePassword : {
			required : false,
			equalTo : '#rawPassword',
			rangelength : r.user.password.length
		},
		surname : {
			required : true,
			rangelength : r.user.name.length
		},
		firstName : {
			required : true,
			rangelength : r.user.name.length
		},
		email : {
			email : true,
			rangelength : r.user.email.length
		},
		phoneNumber : {
			rangelength : r.user.phone.length
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

	jQuery( "#rawPassword" ).attr( "maxlength", r.user.password.length[1] );
	jQuery( "#retypePassword" ).attr( "maxlength", r.user.password.length[1] );
	jQuery( "#surname" ).attr( "maxlength", r.user.name.length[1] );
	jQuery( "#firstName" ).attr( "maxlength", r.user.name.length[1] );
	jQuery( "#email" ).attr( "maxlength", r.user.email.length[1] );
	jQuery( "#phoneNumber" ).attr( "maxlength", r.user.phone.length[1] );

} );
