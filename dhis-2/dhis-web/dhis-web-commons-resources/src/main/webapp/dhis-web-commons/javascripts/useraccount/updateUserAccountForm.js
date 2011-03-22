jQuery( document ).ready( function()
{
	var r = getValidationRules();

	/* validation */
	var rules = {
		oldPassword : {
			required : true
		},
		rawPassword : {
			required : false,
			password : true,
			range : r.user.password.range,
			notequalto : '#username'
		},
		retypePassword : {
			required : false,
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
		}
	}

	validation2( 'updateUserinforForm', updateUser, {
		'rules' : rules
	} );

	jQuery( "#rawPassword" ).attr( "maxlength", r.user.password.range[1] );
	jQuery( "#retypePassword" ).attr( "maxlength", r.user.password.range[1] );
	jQuery( "#surname" ).attr( "maxlength", r.user.name.range[1] );
	jQuery( "#firstName" ).attr( "maxlength", r.user.name.range[1] );
	jQuery( "#email" ).attr( "maxlength", r.user.email.range[1] );
	jQuery( "#phoneNumber" ).attr( "maxlength", r.user.phone.range[1] );
	/* end validation */

	var oldPassword = byId( 'oldPassword' );
	oldPassword.select();
	oldPassword.focus();
} );

function updateUser()
{
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( updateUserReceived );

	var params = "id=" + byId( 'id' ).value;
	params += "&oldPassword=" + byId( 'oldPassword' ).value;
	params += "&rawPassword=" + byId( 'rawPassword' ).value;
	params += "&retypePassword=" + byId( 'retypePassword' ).value;
	params += "&surname=" + byId( 'surname' ).value;
	params += "&firstName=" + byId( 'firstName' ).value;
	params += "&email=" + byId( 'email' ).value;
	params += "&phoneNumber=" + byId( 'phoneNumber' ).value;
	request.sendAsPost( params );
	request.send( 'updateUserAccount.action' );
}

function updateUserReceived( xmlObject )
{
	setMessage( xmlObject.firstChild.nodeValue );
}
