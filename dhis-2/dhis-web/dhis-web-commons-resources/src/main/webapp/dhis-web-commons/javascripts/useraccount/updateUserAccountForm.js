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
			rangelength : r.user.password.length,
			notequalto : '#username'
		},
		retypePassword : {
			required : false,
			equalTo : '#rawPassword'
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
		}
	}

	validation2( 'updateUserinforForm', updateUser, {
		'rules' : rules
	} );

	jQuery( "#rawPassword" ).attr( "maxlength", r.user.password.length[1] );
	jQuery( "#retypePassword" ).attr( "maxlength", r.user.password.length[1] );
	jQuery( "#surname" ).attr( "maxlength", r.user.name.length[1] );
	jQuery( "#firstName" ).attr( "maxlength", r.user.name.length[1] );
	jQuery( "#email" ).attr( "maxlength", r.user.email.length[1] );
	jQuery( "#phoneNumber" ).attr( "maxlength", r.user.phone.length[1] );
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
