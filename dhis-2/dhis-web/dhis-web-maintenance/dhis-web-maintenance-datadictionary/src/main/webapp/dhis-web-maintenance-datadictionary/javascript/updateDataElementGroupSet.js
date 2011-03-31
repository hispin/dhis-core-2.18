jQuery(document).ready(function() {
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.dataElementGroupSet.name.rangelength
		}
	};

	validation2('updateDataElementGroupSet', function(form) {
		form.submit()
	}, {
		'beforeValidateHandler' : beforeSubmit,
		'rules' : rules
	});

	jQuery("#name").attr("maxlength", r.dataElementGroupSet.name.rangelength[1]);

	checkValueIsExist("name", "validateDataElementGroupSet.action", {
		id : getFieldValue('id')
	});

	var nameField = document.getElementById('name');
	nameField.select();
	nameField.focus();
});
