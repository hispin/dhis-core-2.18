jQuery(document).ready(function() {
	var r = getValidationRules();
	
	var rules = {
		dataSetIds: {
			required: true
		}
	};
	
	validation2( "minMaxGeneratingForm", function() {
		if(isGenerate) {
			generateMinMaxValue();
		} else {
			removeMinMaxValue();
		}
	}, {
		'rules': rules
	});
});

var isGenerate = true;
var numberOfSelects = 0;
