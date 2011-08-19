jQuery( document ).ready( function()
{
    validation2( 'updateOrganisationUnitGroupForm', function( form )
    {
        form.submit();
    }, {
        'rules' : getValidationRules( "organisationUnitGroup" )
    } );
} );
