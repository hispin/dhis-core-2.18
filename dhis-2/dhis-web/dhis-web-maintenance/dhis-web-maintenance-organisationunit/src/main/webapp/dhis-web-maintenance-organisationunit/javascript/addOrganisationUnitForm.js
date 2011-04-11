jQuery(document).ready(function() {
	validation2('addOrganisationUnitForm', function(form) {
		selectAllById("dataSets");
		form.submit();

		/*
		 * if(validateFeatureType(this.coordinates, this.featureType)) {
		 * form.submit(); }
		 */
		/* return false; */
	}, {
		'rules' : getValidationRules("organisationUnit")
	});

	checkValueIsExist("name", "validateOrganisationUnit.action");
	datePickerValid('openingDate', false);

	var nameField = document.getElementById('name');
	nameField.select();
	nameField.focus();
});
