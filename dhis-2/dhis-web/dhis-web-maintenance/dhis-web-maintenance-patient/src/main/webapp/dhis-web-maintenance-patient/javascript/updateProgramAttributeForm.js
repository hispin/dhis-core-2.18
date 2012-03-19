jQuery(document).ready(	function(){
			
	validation( 'updateProgramAttributeForm', function(form){
		if( isSubmit && ATTRIBUTE_OPTION.checkOnSubmit() ) {
			form.submit();
		}
	}, function(){
		isSubmit = true;
		jQuery.each($('#updateProgramAttributeForm').serializeArray(), function(i, field) {
			if( field.value == ""){
				setInnerHTML("attrMessage", i18n_field_is_required);
				isSubmit = false;
			}
		});
	}); 
		
	checkValueIsExist( "name", "validateProgramAttribute.action", {id:getFieldValue('id')});
});		