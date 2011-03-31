jQuery(document).ready(function() {
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.dataElementGroupSet.name.rangelength
		}
	};

	validation2('addDataElementGroupSet', function(form) {
		form.submit()
	}, {
		'beforeValidateHandler' : beforeSubmit,
		'rules' : rules
	});

	jQuery("#name").attr("maxlength", r.dataElementGroupSet.name.rangelength[1]);

	checkValueIsExist("name", "validateDataElementGroupSet.action");
});
