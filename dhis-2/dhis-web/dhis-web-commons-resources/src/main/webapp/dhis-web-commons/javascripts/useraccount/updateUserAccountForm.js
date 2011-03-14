jQuery(document).ready(function()
{
	/* validation */
	var rules = {
		oldPassword : {
			required : true
		},
		rawPassword : {
			required : false,
			password : true,
			rangelength : [ 8, 35 ],
			notequalto : '#username'
		},
		retypePassword : {
			required : false,
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
		phoneNumber : {}
	}

	validation2('updateUserinforForm', updateUser, {
		'rules' : rules
	});

	jQuery("#rawPassword").attr("maxlength", "35");
	jQuery("#retypePassword").attr("maxlength", jQuery("#rawPassword").attr("maxlength"));
	jQuery("#surname").attr("maxlength", "140");
	jQuery("#firstName").attr("maxlength", "140");
	jQuery("#email").attr("maxlength", "160");
	jQuery("#phoneNumber").attr("maxlength", "80");
	/* end validation */

	var oldPassword = byId('oldPassword');
	oldPassword.select();
	oldPassword.focus();
});

function updateUser()
{
	var request = new Request();
	request.setResponseTypeXML('xmlObject');
	request.setCallbackSuccess(updateUserReceived);

	var params = "id=" + byId('id').value;
	params += "&oldPassword=" + byId('oldPassword').value;
	params += "&rawPassword=" + byId('rawPassword').value;
	params += "&retypePassword=" + byId('retypePassword').value;
	params += "&surname=" + byId('surname').value;
	params += "&firstName=" + byId('firstName').value;
	params += "&email=" + byId('email').value;
	params += "&phoneNumber=" + byId('phoneNumber').value;
	request.sendAsPost(params);
	request.send('updateUserAccount.action');
}

function updateUserReceived( xmlObject )
{
	setMessage(xmlObject.firstChild.nodeValue);
}
