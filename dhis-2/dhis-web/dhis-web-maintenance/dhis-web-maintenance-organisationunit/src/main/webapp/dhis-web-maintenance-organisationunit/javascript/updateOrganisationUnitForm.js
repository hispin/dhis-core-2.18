jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.organisationUnit.name.rangelength
		},
		shortName : {
			required : true,
			rangelength : r.organisationUnit.shortName.rangelength
		},
		code : {
			required : true,
			rangelength : r.organisationUnit.code.rangelength
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
			rangelength : r.organisationUnit.url.rangelength
		},
		contactPerson : {
			rangelength : r.organisationUnit.contactPerson.rangelength
		},
		address : {
			rangelength : r.organisationUnit.address.rangelength
		},
		email : {
			email : true,
			rangelength : r.organisationUnit.email.rangelength
		},
		phoneNumber : {
			rangelength : r.organisationUnit.phoneNumber.rangelength
		}
	};

	validation2( 'updateOrganisationUnitForm', function(form) {
		selectAllById("dataSets");
		form.submit();

		/* if(validateFeatureType(this.coordinates, this.featureType)) {form.submit();} */
	}, {
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", r.organisationUnit.name.rangelength[1] );
	jQuery( "#shortName" ).attr( "maxlength", r.organisationUnit.shortName.rangelength[1] );
	jQuery( "#code" ).attr( "maxlength", r.organisationUnit.code.rangelength[1] );
	jQuery( "#url" ).attr( "maxlength", r.organisationUnit.url.rangelength[1] );
	jQuery( "#contactPerson" ).attr( "maxlength", r.organisationUnit.contactPerson.rangelength[1] );
	jQuery( "#address" ).attr( "maxlength", r.organisationUnit.address.rangelength[1] );
	jQuery( "#email" ).attr( "maxlength", r.organisationUnit.email.rangelength[1] );
	jQuery( "#phoneNumber" ).attr( "maxlength", r.organisationUnit.phoneNumber.rangelength[1] );
} );
