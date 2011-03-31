jQuery(document).ready(function() {
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.indicator.name.rangelength,
			alphanumericwithbasicpuncspaces : r.indicator.name.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.indicator.name.firstletteralphabet
		},
		shortName : {
			required : true,
			rangelength : r.indicator.shortName.rangelength,
			alphanumericwithbasicpuncspaces : r.indicator.shortName.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.indicator.shortName.firstletteralphabet
		},
		alternativeName : {
			rangelength : r.indicator.alternativeName.rangelength,
			alphanumericwithbasicpuncspaces : r.indicator.alternativeName.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.indicator.alternativeName.firstletteralphabet
		},
		code : {
			rangelength : r.indicator.code.rangelength,
			alphanumericwithbasicpuncspaces : r.indicator.code.alphanumericwithbasicpuncspaces,
			notOnlyDigits : r.indicator.code.notOnlyDigits
		},
		description : {
			rangelength : r.indicator.description.rangelength,
			alphanumericwithbasicpuncspaces : r.indicator.description.alphanumericwithbasicpuncspaces,
			firstletteralphabet : r.indicator.description.firstletteralphabet
		},
		indicatorTypeId : {
			required : true
		},
		url : {
			url : true,
			rangelength : r.indicator.url.rangelength
		},
		denominator : {
			required : true
		}
	};

	validation2('addIndicatorForm', function(form) {
		form.submit();
	}, {
		'rules' : rules
	});

	jQuery("#name").attr("maxlength", r.indicator.name.rangelength[1]);
	jQuery("#shortName").attr("maxlength", r.indicator.shortName.rangelength[1]);
	jQuery("#alternativeName").attr("maxlength", r.indicator.alternativeName.rangelength[1]);
	jQuery("#code").attr("maxlength", r.indicator.code.rangelength[1]);
	jQuery("#description").attr("maxlength", r.indicator.description.rangelength[1]);
	jQuery("#url").attr("maxlength", r.indicator.url.rangelength[1]);

	checkValueIsExist("name", "validateIndicator.action");
	checkValueIsExist("shortName", "validateIndicator.action");
	checkValueIsExist("alternativeName", "validateIndicator.action");
	checkValueIsExist("code", "validateIndicator.action");
});
