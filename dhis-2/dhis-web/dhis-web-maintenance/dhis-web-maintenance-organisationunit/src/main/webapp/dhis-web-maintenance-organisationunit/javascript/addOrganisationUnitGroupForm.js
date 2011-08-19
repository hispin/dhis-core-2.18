jQuery( document ).ready( function()
{
    validation2( 'addOrganisationUnitGroupForm', function( form )
    {
        form.submit();
    }, {
        'rules' : getValidationRules( "organisationUnitGroup" )
    } );

    checkValueIsExist( "name", "validateOrganisationUnitGroup.action" );
} );
