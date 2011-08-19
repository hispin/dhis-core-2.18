jQuery( document ).ready( function()
{
    validation2( 'addUserGroupForm', function( form )
    {
        form.submit();
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembersList' );
        },
        'rules' : getValidationRules( "userGroup" )
    } );

    /* remote validation */
    checkValueIsExist( "name", "validateUserGroup.action" );
} );
