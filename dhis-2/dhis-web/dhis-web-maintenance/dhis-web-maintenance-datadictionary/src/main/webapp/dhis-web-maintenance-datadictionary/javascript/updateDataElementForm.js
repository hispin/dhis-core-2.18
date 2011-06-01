jQuery( document ).ready( function()
{
    validation2( 'updateDataElementForm', function( form )
    {
        dhis2.select.selectAll( $( '#aggregationLevels' ) );
        form.submit();
    }, {
        'beforeValidateHandler' : function()
        {
            setFieldValue( 'submitCategoryComboId', getFieldValue( 'selectedCategoryComboId' ) );
            setFieldValue( 'submitValueType', getFieldValue( 'valueType' ) );
        },
        'rules' : getValidationRules( "dataElement" )
    } );
} );
