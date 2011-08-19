jQuery( document ).ready( function()
{
    validation2( 'addReportTableGroupForm', function( form )
    {
        form.submit();
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembers' );
        },
        'rules' : getValidationRules( "reportTableGroup" )
    } );

    checkValueIsExist( "name", "validateReportTableGroup.action" );

    var nameField = document.getElementById( 'name' );
    nameField.select();
    nameField.focus();
} );
