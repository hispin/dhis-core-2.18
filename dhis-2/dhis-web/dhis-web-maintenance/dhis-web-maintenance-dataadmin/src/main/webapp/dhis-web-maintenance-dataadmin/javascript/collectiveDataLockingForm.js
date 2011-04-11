jQuery(document).ready(function() {
	validation2('lockingForm', function(form) {
		validateCollectiveDataLockingForm(form);
	}, {
		'rules' : getValidationRules("dataLocking")
	});
});
