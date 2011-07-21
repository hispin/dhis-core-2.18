jQuery( document ).ready( function()
{
    validation2( 'addReportGroupForm', function( form )
    {
        form.submit()
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembers' );
        },
        'rules' : getValidationRules( "reportGroup" )
    } );

    checkValueIsExist( "name", "validateReportGroup.action" );

    var nameField = document.getElementById( 'name' );
    nameField.select();
    nameField.focus();
} );
