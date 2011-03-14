jQuery( document ).ready( function()
{
	var rules = {
		rawPassword : {
			rangelength : [ 8, 35 ],
			password : true,
			notequalto : '#username'
		},
		retypePassword : {
			rangelength : [ 8, 35 ],
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

	jQuery( "#rawPassword" ).attr( "maxlength", "35" );
	jQuery( "#retypePassword" ).attr( "maxlength", "35" );
	jQuery( "#surname" ).attr( "maxlength", "140" );
	jQuery( "#firstName" ).attr( "maxlength", "140" );
	jQuery( "#email" ).attr( "maxlength", "160" );
} );
