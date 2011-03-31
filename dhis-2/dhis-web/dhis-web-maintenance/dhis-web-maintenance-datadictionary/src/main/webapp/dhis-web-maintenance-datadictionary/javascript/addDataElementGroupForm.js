jQuery(document).ready(function() {
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.dataElementGroup.name.rangelength,
			alphanumericwithbasicpuncspaces : true,
			notOnlyDigits : true,
			firstletteralphabet : true
		}
	};

	validation2('addDataElementGroupForm', function(form) {
		form.submit()
	}, {
		'beforeValidateHandler' : beforeSubmit,
		'rules' : rules
	});

	jQuery("#name").attr("maxlength", r.dataElementGroup.name.rangelength[1]);

	checkValueIsExist("name", "validateDataElementGroup.action");
});
