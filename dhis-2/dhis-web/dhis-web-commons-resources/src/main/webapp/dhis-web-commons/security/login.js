$( document ).ready( function() 
{
    $( '#j_username' ).focus();

    $( '#loginForm').bind( 'submit', function() 
    {
        $( '#submit' ).attr( 'disabled', 'disabled' );
        $( '#reset' ).attr( 'disabled', 'disabled' );

        sessionStorage.removeItem( 'orgUnitSelected' );
    } );
} );

var login = {};
