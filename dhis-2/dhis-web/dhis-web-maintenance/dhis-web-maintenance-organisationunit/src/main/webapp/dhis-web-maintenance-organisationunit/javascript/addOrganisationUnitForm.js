jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			range : r.organisationUnit.name.range
		},
		shortName : {
			required : true,
			range : r.organisationUnit.shortName.range
		},
		code : {
			required : true,
			range : r.organisationUnit.code.range
		},
		openingDate : {
			required : true
		},
		comment : {

		},
		coordinates : {

		},
		featureType : {

		},
		url : {
			url : true,
			range : r.organisationUnit.url.range
		},
		contactPerson : {
			range : r.organisationUnit.contactPerson.range
		},
		address : {
			range : r.organisationUnit.address.range
		},
		email : {
			email : true,
			range : r.organisationUnit.email.range
		},
		phoneNumber : {
			range : r.organisationUnit.phoneNumber.range
		}
	};

	validation2( 'addOrganisationUnitForm', function(form) {
		selectAllById("dataSets");
		form.submit();

		/* if(validateFeatureType(this.coordinates, this.featureType)) { form.submit(); } */
		/*  return false; */
	}, {
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", r.organisationUnit.name.range[1] );
	jQuery( "#shortName" ).attr( "maxlength", r.organisationUnit.shortName.range[1] );
	jQuery( "#code" ).attr( "maxlength", r.organisationUnit.code.range[1] );
	jQuery( "#url" ).attr( "maxlength", r.organisationUnit.url.range[1] );
	jQuery( "#contactPerson" ).attr( "maxlength", r.organisationUnit.contactPerson.range[1] );
	jQuery( "#address" ).attr( "maxlength", r.organisationUnit.address.range[1] );
	jQuery( "#email" ).attr( "maxlength", r.organisationUnit.email.range[1] );
	jQuery( "#phoneNumber" ).attr( "maxlength", r.organisationUnit.phoneNumber.range[1] );

	checkValueIsExist( "name", "validateOrganisationUnit.action" );
	datePickerValid( 'openingDate', false );

	var nameField = document.getElementById( 'name' );
	nameField.select();
	nameField.focus();
} );
