jQuery(document).ready(	function(){
	validation( 'updateAttributeForm', function(form){
		form.submit();
	});
	
	checkValueIsExist( "name", "validateAttribute.action", {id:getFieldValue('id')});
	checkValueIsExist( "shortName", "validateAttribute.action", {id:getFieldValue('id')});
	checkValueIsExist( "code", "validateAttribute.action", {id:getFieldValue('id')});
});