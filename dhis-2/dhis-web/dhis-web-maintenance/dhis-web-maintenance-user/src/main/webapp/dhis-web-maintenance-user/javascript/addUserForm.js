jQuery( document ).ready( function()
{
    validation2( 'addUserForm', function( form )
    {
        jQuery( "#selectedList" ).children().attr( "selected", true );
        form.submit();
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'roleValidator', 'selectedList' );
        },
        'rules' : getValidationRules( "user" )
    } );

    /* remote validation */
    checkValueIsExist( "username", "validateUser.action" );

    jQuery( "#cancel" ).click( function()
    {
        referrerBack( "alluser.action" );
    } );
} );
