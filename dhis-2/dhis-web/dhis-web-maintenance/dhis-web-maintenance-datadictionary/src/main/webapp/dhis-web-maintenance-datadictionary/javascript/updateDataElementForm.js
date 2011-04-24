jQuery(document).ready(	function() {
	validation2( 'updateDataElementForm', function( form ) { form.submit(); }, {
		'beforeValidateHandler': function() {
			setFieldValue( 'submitCategoryComboId', getFieldValue( 'selectedCategoryComboId' ) );
			setFieldValue( 'submitValueType', getFieldValue( 'valueType' ) );
		},
		'rules': getValidationRules("dataElement")
	});
});
