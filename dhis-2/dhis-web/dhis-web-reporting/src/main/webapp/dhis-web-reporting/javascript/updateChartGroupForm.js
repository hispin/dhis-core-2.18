jQuery( document ).ready( function()
{
    validation2( 'updateChartGroupForm', function( form )
    {
        form.submit()
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembers' );
        },
        'rules' : getValidationRules( "chartGroup" )
    } );
	
	checkValueIsExist( "name", "validateChartGroup.action", {id: $chartGroup.id});
	
} );
