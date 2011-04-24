jQuery(document).ready(	function() {
	validation2( 'addDataElementForm', function( form ){ form.submit(); }, {
		'beforeValidateHandler': function() {
			setFieldValue( 'submitCategoryComboId', getFieldValue( 'selectedCategoryComboId' ) );
			setFieldValue( 'submitValueType', getFieldValue( 'valueType' ) );
		},
		'rules': getValidationRules("dataElement")
	});

	checkValueIsExist( "name", "validateDataElement.action");
	checkValueIsExist( "shortName", "validateDataElement.action");
	checkValueIsExist( "alternativeName", "validateDataElement.action");
});
