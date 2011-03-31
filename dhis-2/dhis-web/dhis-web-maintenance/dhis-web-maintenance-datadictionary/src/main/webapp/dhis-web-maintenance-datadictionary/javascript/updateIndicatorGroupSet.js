jQuery(document).ready(function() {
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.indicatorGroupSet.name.rangelength
		}
	};

	validation('updateIndicatorGroupSet', function(form) {
		form.submit()
	}, function() {
		listValidator('memberValidator', 'groupMembers');
	});

	jQuery("#name").attr("maxlength", r.indicatorGroupSet.name.rangelength[1]);
});
