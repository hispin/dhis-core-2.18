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
			rangelength: [ 1, 80 ]
		},
		password: {
			required: true,
			rangelength: [ 1, 80 ],
			notequalto : "#username",
		},
		retypePassword : {
			required: true,
			equalTo: "#password"
		},
		email: {
			required: true,
			email: true,
			rangelength: [ 1, 80 ]
		}
	},
	messages: {
		username: {
			remote: "Username is already taken"
		}
	}
};

$( document ).ready( function() {
	jQuery( "#accountForm" ).validate( {
		rules: validationRules.rules,
		messages: validationRules.messages,
		errorPlacement: function( error, element ) {
			element.parent( "td" ).append( "<br>" ).append( error );
		}
	} );
	
	jQuery.extend( jQuery.validator.messages, {
	    required: "This field is required",
	    rangelength: "Please enter a value between 1 and 80 characters long",
	    email: "Please enter a valid email address"
	} );
} );