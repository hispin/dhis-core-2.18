jQuery(document).ready(function() {
	jQuery("#name").focus();

	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.validationRule.name.rangelength
		},
		description : {
			rangelength : r.validationRule.description.rangelength
		},
		periodTypeName : {
			required : true
		},
		operator : {
			required : true
		},
		leftSideExpression : {
			required : true
		},
		rightSideExpression : {
			required : true
		}
	};

	validation2('addValidationRuleForm', function(form) {
		form.submit();
	}, {
		'rules' : rules
	});

	jQuery("#name").attr("maxlength", r.validationRule.name.rangelength[1]);
	jQuery("#description").attr("maxlength", r.validationRule.description.rangelength[1]);

	checkValueIsExist("name", "validateValidationRule.action");
});
