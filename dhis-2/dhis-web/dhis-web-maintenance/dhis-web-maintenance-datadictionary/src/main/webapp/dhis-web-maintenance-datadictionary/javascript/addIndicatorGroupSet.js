jQuery(document).ready(function() {
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.indicatorGroupSet.name.rangelength
		}
	};

	validation2('addIndicatorGroupSet', function(form) {
		form.submit()
	}, {
		'beforeValidateHandler' : function() {
			listValidator('memberValidator', 'groupMembers');
		},
		'rules' : rules
	});

	jQuery("#name").attr("maxlength", r.indicatorGroupSet.name.rangelength[1]);

	checkValueIsExist("name", "validateIndicatorGroupSet.action");
});
