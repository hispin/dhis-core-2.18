jQuery( document ).ready( function()
{
    validation2( 'addIndicatorGroupForm', function( form )
    {
        form.submit();
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembers' );
        },
        'rules' : getValidationRules( "indicatorGroup" )
    } );

    checkValueIsExist( "name", "validateIndicatorGroup.action" );

    var nameField = document.getElementById( 'name' );
    nameField.select();
    nameField.focus();
} );
