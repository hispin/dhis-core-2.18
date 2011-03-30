jQuery(document).ready(	function(){
	var r = getValidationRules();
	
	var rules = {
		name: {
			required: true,
			rangelength: [2, 160]
		},
		description: {
			rangelength: [0, 255]
		},
		region: {
			rangelength: [0, 255]
		},
		memberValidator: {
			required: true			
		},
		memberValidatorIn: {
			required: true
		}
	};

	validation2( 'updateDataDictionaryForm', function( form ){ form.submit()}, {
		'beforeValidateHandler': function(){
			listValidator( 'memberValidator', 'selectedDataElements' );
			listValidator( 'memberValidatorIn', 'selectedIndicators' );
		},
		'rules': rules
	});

	jQuery("#name").attr("maxlength", "160");
	jQuery("#description").attr("maxlength", "255");
	jQuery("#region").attr("maxlength", "255");
});
