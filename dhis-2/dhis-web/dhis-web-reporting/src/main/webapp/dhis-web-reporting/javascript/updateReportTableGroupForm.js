jQuery( document ).ready( function()
{
    validation2( 'updateReportTableGroupForm', function( form )
    {
        form.submit()
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembers' );
        },
        'rules' : getValidationRules( "reportTableGroup" )
    } );
	
	checkValueIsExist( "name", "validateReportTableGroup.action", {id: getFieldValue( "id" )});
	
} );
