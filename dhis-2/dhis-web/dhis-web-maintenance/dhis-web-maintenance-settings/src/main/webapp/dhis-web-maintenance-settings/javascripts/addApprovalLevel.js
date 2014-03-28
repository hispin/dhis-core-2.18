jQuery( document ).ready( function()
{
    jQuery( "#name" ).focus();

    validation2( 'addApprovalLevelForm', function( form )
    {
        form.submit();
    }, {
        'rules' : getValidationRules( "dataApprovalLevel" )
    } );
} );
