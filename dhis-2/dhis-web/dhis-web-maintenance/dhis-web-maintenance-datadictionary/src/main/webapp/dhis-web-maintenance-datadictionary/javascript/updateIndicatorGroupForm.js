jQuery( document ).ready( function()
{
    validation2( 'updateIndicatorGroupForm', function( form )
    {
        form.submit();
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembers' );
        },
        'rules' : getValidationRules( "indicatorGroup" )
    } );
} );
