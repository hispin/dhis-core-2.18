jQuery(document).ready(	function(){
	validation2( 'updateAttributeForm', function( form )
	{
		form.submit();
	}, {
		'rules' : getValidationRules( "trackedEntityAttribute" )
	} );
		
	
	checkValueIsExist( "name", "validateAttribute.action", {id:getFieldValue('id')});
	checkValueIsExist( "shortName", "validateAttribute.action", {id:getFieldValue('id')});
	checkValueIsExist( "code", "validateAttribute.action", {id:getFieldValue('id')});
});