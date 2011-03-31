jQuery(document).ready(function() {
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.indicatorGroup.name.rangelength,
			alphanumericwithbasicpuncspaces : true,
			firstletteralphabet : true
		}
	};

	validation2('addIndicatorGroupForm', function(form) {
		form.submit()
	}, {
		'beforeValidateHandler' : function() {
			listValidator('memberValidator', 'groupMembers');
		},
		'rules' : rules
	});

	jQuery("#name").attr("maxlength", r.indicatorGroup.name.rangelength[1]);

	checkValueIsExist("name", "validateIndicatorGroup.action");

	var nameField = document.getElementById('name');
	nameField.select();
	nameField.focus();
});
