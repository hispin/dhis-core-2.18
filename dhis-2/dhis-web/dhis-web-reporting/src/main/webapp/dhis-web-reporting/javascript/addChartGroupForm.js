jQuery( document ).ready( function()
{
    validation2( 'addChartGroupForm', function( form )
    {
        form.submit()
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembers' );
        },
        'rules' : getValidationRules( "chartGroup" )
    } );

    checkValueIsExist( "name", "validateChartGroup.action" );

    var nameField = document.getElementById( 'name' );
    nameField.select();
    nameField.focus();
} );
