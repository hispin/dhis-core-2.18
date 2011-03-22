jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.organisationUnit.name.length
		},
		shortName : {
			required : true,
			rangelength : r.organisationUnit.shortName.length
		},
		code : {
			required : true,
			rangelength : r.organisationUnit.code.length
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
			rangelength : r.organisationUnit.url.length
		},
		contactPerson : {
			rangelength : r.organisationUnit.contactPerson.length
		},
		address : {
			rangelength : r.organisationUnit.address.length
		},
		email : {
			email : true,
			rangelength : r.organisationUnit.email.length
		},
		phoneNumber : {
			rangelength : r.organisationUnit.phoneNumber.length
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

	jQuery( "#name" ).attr( "maxlength", r.organisationUnit.name.length[1] );
	jQuery( "#shortName" ).attr( "maxlength", r.organisationUnit.shortName.length[1] );
	jQuery( "#code" ).attr( "maxlength", r.organisationUnit.code.length[1] );
	jQuery( "#url" ).attr( "maxlength", r.organisationUnit.url.length[1] );
	jQuery( "#contactPerson" ).attr( "maxlength", r.organisationUnit.contactPerson.length[1] );
	jQuery( "#address" ).attr( "maxlength", r.organisationUnit.address.length[1] );
	jQuery( "#email" ).attr( "maxlength", r.organisationUnit.email.length[1] );
	jQuery( "#phoneNumber" ).attr( "maxlength", r.organisationUnit.phoneNumber.length[1] );

	checkValueIsExist( "name", "validateOrganisationUnit.action" );
	datePickerValid( 'openingDate', false );

	var nameField = document.getElementById( 'name' );
	nameField.select();
	nameField.focus();
} );
