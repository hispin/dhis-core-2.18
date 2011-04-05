jQuery(document).ready(function() {

	selection.setListenerFunction( organisationUnitModeSelected );
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
		validateBeforeSubmit();
	}, { 'rules': rules });
});

var flag;
window.onload = modeHandler;
