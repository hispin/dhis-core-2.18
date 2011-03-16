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

	validation2( 'updateOrganisationUnitForm', undefined, {
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", r.organisationUnit.name.length );
	jQuery( "#shortName" ).attr( "maxlength", r.organisationUnit.shortName.length );
	jQuery( "#code" ).attr( "maxlength", r.organisationUnit.code.length );
	jQuery( "#url" ).attr( "maxlength", r.organisationUnit.url.length );
	jQuery( "#contactPerson" ).attr( "maxlength", r.organisationUnit.contactPerson.length );
	jQuery( "#address" ).attr( "maxlength", r.organisationUnit.address.length );
	jQuery( "#email" ).attr( "maxlength", r.organisationUnit.email.length );
	jQuery( "#phoneNumber" ).attr( "maxlength", r.organisationUnit.phoneNumber.length );
} );
