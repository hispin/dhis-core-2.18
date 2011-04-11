jQuery(document).ready(function() {
	validation2('updateOrganisationUnitForm', function(form) {
		selectAllById("dataSets");
		form.submit();

		/*
		 * if(validateFeatureType(this.coordinates, this.featureType))
		 * {form.submit();}
		 */
	}, {
		'rules' : getValidationRules("organisationUnit")
	});
});
