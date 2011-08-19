jQuery( document ).ready( function()
{
    validation2( 'updateDataElementGroupForm', function( form )
    {
        form.submit();
    }, {
        'beforeValidateHandler' : beforeSubmit,
        'rules' : getValidationRules( "dataElementGroup" )
    } );

    checkValueIsExist( "name", "validateDataElementGroup.action", {
        id : getFieldValue( 'id' )
    } );
} );
