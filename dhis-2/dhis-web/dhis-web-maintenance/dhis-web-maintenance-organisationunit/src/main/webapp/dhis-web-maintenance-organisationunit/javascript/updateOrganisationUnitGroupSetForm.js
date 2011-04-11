jQuery(document).ready(function() {
	validation2('updateOrganisationUnitGroupSetForm', function(form) {
		validateAddOrganisationGroupSet(form)
	}, {
		'beforeValidateHandler' : function() {
			selectAllById('selectedGroups');
		},
		'rules' : getValidationRules("organisationUnitGroupSet")
	});

	checkValueIsExist("name", "validateOrganisationUnitGroupSet.action", {
		id : $organisationUnitGroupSet.id
	});

	changeCompulsory(getFieldValue('compulsory'));
});
