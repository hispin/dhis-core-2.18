var validationRules = {
	rules: {
		firstName: {
			required: true,
			rangelength: [ 1, 80 ]
		},
		surname: {
			required: true,
			rangelength: [ 1, 80 ]
		},
		username: {
			required: true,
			rangelength: [ 1, 80 ],
			remote: "../../api/account/username"
		},
		password: {
			required: true,
			rangelength: [ 1, 80 ]
		},
		email: {
			required: true,
			email: true,
			rangelength: [ 1, 80 ]
		}
	}
};

$( document ).ready( function() {
	
	Recaptcha.create( "6LcM6tcSAAAAANwYsFp--0SYtcnze_WdYn8XwMMk", "recaptchaDiv", {
		callback: Recaptcha.focus_response_field
	} );
	
	$( "#recaptchaValidationField" ).hide();
	
	$( "#accountForm" ).validate( {
		rules: validationRules.rules,
		submitHandler: accountSubmitHandler,
		errorPlacement: function( error, element ) {
			element.parent( "td" ).append( "<br>" ).append( error );
		}
	} );
	
	$.extend( jQuery.validator.messages, {
	    required: "This field is required",
	    rangelength: "Please enter a value between 1 and 80 characters long",
	    email: "Please enter a valid email address"
	} );
} );

function accountSubmitHandler()
{
	$.ajax( {
		url: "../../api/account",
		data: $( "#accountForm" ).serialize(),
		type: "POST",
		success: function( data ) {
			alert("Account created");
		},
		error: function( jqXHR, textStatus, errorThrown ) {
			$( "#messageSpan" ).show().text( jqXHR.responseText );
			Recaptcha.reload();
		}
	} );
}

function reloadRecaptcha()
{
	Recaptcha.reload();
}