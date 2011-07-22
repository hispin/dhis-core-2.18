jQuery( document ).ready( function()
{
    validation2( 'updateReportGroupForm', function( form )
    {
        form.submit()
    }, {
        'beforeValidateHandler' : function()
        {
            listValidator( 'memberValidator', 'groupMembers' );
        },
        'rules' : getValidationRules( "reportGroup" )
    });
	
	checkValueIsExist( "name", "validateReportGroup.action", {id: getFieldValue( "id" )});
});
