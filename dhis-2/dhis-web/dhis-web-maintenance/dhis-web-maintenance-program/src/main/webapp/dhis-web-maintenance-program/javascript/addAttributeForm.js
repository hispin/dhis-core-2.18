jQuery(document).ready(	function(){
		validation( 'addAttributeForm', function(form){
			form.submit();
		})
	
		checkValueIsExist( "name", "validateAttribute.action");
	});	