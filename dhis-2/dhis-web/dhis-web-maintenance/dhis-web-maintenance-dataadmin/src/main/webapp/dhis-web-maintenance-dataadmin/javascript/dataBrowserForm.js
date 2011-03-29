jQuery(document).ready(function() {
	datePickerInRange( 'fromDate' , 'toDate' );

	var r = getValidationRules();
	
	var rules = {
		periodTypeId: {
			required: true
		},
		mode: {
			required: true
		}
	};

	validation2( 'databrowser', function( form ){ 			
		form.submit();
	}, {
		'rules': rules
	});		
});

var flag;
window.onload = modeHandler;
