jQuery(document).ready(function() {
	jQuery("#name").focus();

	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.validationRuleGroup.name.rangelength
		},
		description : {
			rangelength : r.validationRuleGroup.description.rangelength
		}
	};

	validation2('updateValidationRuleGroupForm', function(form) {
		form.submit()
	}, {
		'beforeValidateHandler' : function() {
			selectAllById('groupMembers')
		},
		'rules' : rules
	});

	jQuery("#name").attr("maxlength", r.validationRuleGroup.name.rangelength[1]);
	jQuery("#description").attr("maxlength", r.validationRuleGroup.description.rangelength[1]);
	
	checkValueIsExist("name", "validateValidationRuleGroup.action", {
		id : getFieldValue('id')
	});

	initLists();
});
