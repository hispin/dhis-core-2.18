jQuery(document).ready(	function(){
		
	validation( 'addProgramAttributeForm', function(form){
		if( isSubmit && ATTRIBUTE_OPTION.checkOnSubmit() ) {
			form.submit();
		}
	}, function(){
		isSubmit = true;
		jQuery.each($('#addProgramAttributeForm').serializeArray(), function(i, field) {
			if( field.value == ""){
				setInnerHTML("attrMessage", i18n_field_is_required);
				isSubmit = false;
			}
		});
	});
	
	jQuery("#attributeComboRow").hide();
		
	checkValueIsExist( "name", "validateProgramAttribute.action");
});		