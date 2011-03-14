jQuery( document ).ready( function()
{
	var rules = {
		username : {
			required : true,
			firstletteralphabet : true,
			minlength : 2,
			alphanumeric : true
		},
		rawPassword : {
			required : false,
			password : true,
			rangelength : [ 8, 35 ],
			notequalto : '#username'
		},
		retypePassword : {
			required : true,
			equalTo : '#rawPassword'
		},
		surname : {
			required : true,
			minlength : 2
		},
		firstName : {
			required : true,
			minlength : 2
		},
		email : {
			email : true
		},
		phoneNumber : {},
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

	jQuery( "#username" ).attr( "maxlength", "140" );
	jQuery( "#rawPassword" ).attr( "maxlength", "35" );
	jQuery( "#retypePassword" ).attr( "maxlength", jQuery( "#rawPassword" ).attr( "maxlength" ) );
	jQuery( "#surname" ).attr( "maxlength", "140" );
	jQuery( "#firstName" ).attr( "maxlength", "140" );
	jQuery( "#email" ).attr( "maxlength", "160" );
	jQuery( "#phoneNumber" ).attr( "maxlength", "80" );

	/* remote validation */
	checkValueIsExist( "username", "validateUser.action" );
} );
