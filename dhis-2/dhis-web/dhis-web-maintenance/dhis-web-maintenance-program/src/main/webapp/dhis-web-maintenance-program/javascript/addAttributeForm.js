jQuery(document).ready(	function(){
	validation2( 'addAttributeForm', function( form )
	{
		form.submit();
	}, {
		'rules' : getValidationRules( "trackedEntityAttribute" )
	} );
		
	checkValueIsExist( "name", "validateAttribute.action");
	checkValueIsExist( "shortName", "validateAttribute.action");
	checkValueIsExist( "code", "validateAttribute.action");
});	