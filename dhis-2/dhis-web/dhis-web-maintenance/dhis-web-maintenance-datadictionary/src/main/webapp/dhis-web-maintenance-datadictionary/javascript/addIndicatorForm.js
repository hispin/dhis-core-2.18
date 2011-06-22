jQuery( document ).ready( function()
{
    validation2( 'addIndicatorForm', function( form )
    {
        form.submit();
    }, {
        'rules' : getValidationRules( "indicator" )
    } );

    checkValueIsExist( "name", "validateIndicator.action" );
    checkValueIsExist( "shortName", "validateIndicator.action" );
    checkValueIsExist( "alternativeName", "validateIndicator.action" );
    checkValueIsExist( "code", "validateIndicator.action" );
} );
