jQuery( document ).ready( function()
{
    validation2( 'addDataElementGroupForm', function( form )
    {
        form.submit();
    }, {
        'beforeValidateHandler' : beforeSubmit,
        'rules' : getValidationRules( "dataElementGroup" )
    } );

    checkValueIsExist( "name", "validateDataElementGroup.action" );
} );
