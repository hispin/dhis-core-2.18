
var login = {};

login.localeCookie = "dhis2.locale";

$( document ).ready( function() 
{
    $( '#j_username' ).focus();

    $( '#loginForm').bind( 'submit', function() 
    {
        $( '#submit' ).attr( 'disabled', 'disabled' );
        $( '#reset' ).attr( 'disabled', 'disabled' );

        sessionStorage.removeItem( 'orgUnitSelected' );
    } );
    
    var locale = $.cookie( login.localeCookie );
    
    if ( undefined !== locale )
    {
    	login.changeLocale( locale );
    	$( '#localeSelect option[value="' + locale + '"]' ).attr( 'selected', 'selected' );
    }
} );

login.localeChanged = function()
{
	var locale = $( '#localeSelect :selected' ).val();
	login.changeLocale( locale );	
	$.cookie( login.localeCookie, locale );
}

login.changeLocale = function( locale )
{	
	$.get( 'loginStrings.action?loc=' + locale, function( json ) {
		$( '#createAccountButton' ).html( json.create_an_account );
		$( '#usernameLabel' ).html( json.login_username );
		$( '#passwordLabel' ).html( json.login_password );
		$( '#forgotPasswordLink' ).html( json.forgot_password );
		$( '#createAccountLink' ).html( json.create_an_account );
		$( '#loginMessage' ).html( json.wrong_username_or_password );
		$( '#poweredByLabel' ).html( json.powered_by );
	} );	
}