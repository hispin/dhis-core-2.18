jQuery(document).ready(function() {

	selection.setListenerFunction(organisationUnitModeSelected);
	datePickerInRange('fromDate', 'toDate');

	validation2('databrowser', function(form) {
		validateBeforeSubmit(form);
	}, {
		'rules' : getValidationRules("dataBrowser")
	});
});

var flag;
window.onload = modeHandler;
