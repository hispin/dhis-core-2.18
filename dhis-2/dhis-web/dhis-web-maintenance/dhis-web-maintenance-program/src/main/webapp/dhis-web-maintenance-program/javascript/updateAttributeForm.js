jQuery(document).ready(	function(){
	validation( 'updateAttributeForm', function(form){
		form.submit();
	});
	
	checkValueIsExist( "name", "validateAttribute.action", {id:getFieldValue('id')});
});		