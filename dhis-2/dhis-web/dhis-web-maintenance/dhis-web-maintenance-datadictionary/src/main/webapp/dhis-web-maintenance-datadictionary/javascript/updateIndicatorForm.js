jQuery(document).ready(function() {
	validation2('updateIndicatorForm', function(form) {
		form.submit();
	}, {
		'rules' : getValidationRules("indicator")
	});

	checkValueIsExist("name", "validateIndicator.action", {
		id : getFieldValue('id')
	});
	checkValueIsExist("shortName", "validateIndicator.action", {
		id : getFieldValue('id')
	});
	checkValueIsExist("alternativeName", "validateIndicator.action", {
		id : getFieldValue('id')
	});
	checkValueIsExist("code", "validateIndicator.action", {
		id : getFieldValue('id')
	});
});
