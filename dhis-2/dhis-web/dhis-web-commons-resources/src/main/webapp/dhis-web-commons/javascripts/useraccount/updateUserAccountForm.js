jQuery( document ).ready( function()
{
	var rules = getValidationRules("user");

	/* some customization is needed for the updateUserAccount validation rules */
    rules["rawPassword"].required = false;
    rules["retypePassword"].required = false;

    rules["oldPassword"] = {
			required: true
	};

	validation2( 'updateUserinforForm', updateUser, {
		'rules' : rules
	} );

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
