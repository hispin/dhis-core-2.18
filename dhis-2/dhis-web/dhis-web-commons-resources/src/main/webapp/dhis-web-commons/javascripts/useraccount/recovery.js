
var login = {};
login.localeCookie = "dhis2.locale";

$( document ).ready( function() {

    var locale = $.cookie( login.localeCookie );
    
    if ( undefined !== locale && locale )
    {
    	login.changeLocale( locale );
    }
    
} );

function recoverAccount()
{
	var username = $.trim( $( "#username" ).val() );
	
	if ( username.length == 0 )
	{
		return false;
	}
	
	$.ajax( {
		url: "../../api/account/recovery",
		data: {
			username: username
		},
		type: "post",
		success: function( data ) {
			$( "#recoveryForm" ).hide();
			$( "#recoverySuccessMessage" ).fadeIn();
		},
		error: function( data ) {
			$( "#recoveryForm" ).hide();
			$( "#recoveryErrorMessage" ).fadeIn();
		}
	} );
}


login.changeLocale = function( locale )
{		
	$.get( 'recoveryStrings.action?loc=' + locale, function( json ) {				
		$('#account_recovery').html( json.account_recovery );
		$('#label_username').html( json.user_name );
		$('#recoveryButton').val( json.recover );
		$('#recoverySuccessMessage').html( json.recover_success_message );
		$('#recoveryErrorMessage').html( json.recover_error_message );
	} );	
}