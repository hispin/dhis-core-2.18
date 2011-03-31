jQuery(document).ready(function() {
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.indicatorType.name.rangelength,
			alphanumericwithbasicpuncspaces : r.indicatorType.name.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.indicatorType.name.firstletteralphabet
		},
		factor : {
			required : true,
			rangelength : r.indicatorType.factor.rangelength
		}
	};

	validation2('updateIndicatorTypeForm', function(form) {
		form.submit()
	}, {
		'rules' : rules
	});

	jQuery("#name").attr("maxlength", r.indicatorType.name.rangelength[1]);
	jQuery("#factor").attr("maxlength", r.indicatorType.factor.rangelength[1]);

	var nameField = document.getElementById( 'name' );
	nameField.select();
	nameField.focus();
});
