jQuery( document ).ready( function()
{
    validation2( 'editUserGroupForm', function( form )
    {
        form.submit();
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembersList' );
        },
        'rules' : getValidationRules( "userGroup" )
    } );
} );
