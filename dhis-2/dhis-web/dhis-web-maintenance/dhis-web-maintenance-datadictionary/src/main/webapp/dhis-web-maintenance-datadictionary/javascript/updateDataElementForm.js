jQuery(document).ready(	function() {
	var r = getValidationRules();
	
	var rules = {
		name: {
			required:true,
			rangelength: [3, 150],
			alphanumericwithbasicpuncspaces:true,
			notOnlyDigits:true
		},
		shortName: {
			required:true,
			rangelength: [2, 20],
			alphanumericwithbasicpuncspaces:true,
			notOnlyDigits:true
		},
		alternativeName: {
			rangelength: [3, 150],
			alphanumericwithbasicpuncspaces:true,
			notOnlyDigits:true
		},
		code: {
			rangelength: [3, 40],
			alphanumericwithbasicpuncspaces:true,
			notOnlyDigits:false
		},
		description: {
			rangelength: [3, 250],
			alphanumericwithbasicpuncspaces:true,
			notOnlyDigits:true			
		},
		url: {
			 url: true,
			 rangelength: [0, 255]
		}
	};

	validation2( 'updateDataElementForm', function( form ) { form.submit(); }, {
		'beforeValidateHandler': function() {
			getDataElementIdsForValidate();
			getFactors();	
			setFieldValue( 'submitCategoryComboId', getFieldValue( 'selectedCategoryComboId' ) );
			setFieldValue( 'submitValueType', getFieldValue( 'valueType' ) );
		},
		'rules': rules 
	});

	jQuery("#name").attr("maxlength", "150");
	jQuery("#shortName").attr("maxlength", "20");
	jQuery("#alternativeName").attr("maxlength", "150");
	jQuery("#code").attr("maxlength", "40");
	jQuery("#description").attr("maxlength", "250");
	jQuery("#url").attr("maxlength", "255");
});
