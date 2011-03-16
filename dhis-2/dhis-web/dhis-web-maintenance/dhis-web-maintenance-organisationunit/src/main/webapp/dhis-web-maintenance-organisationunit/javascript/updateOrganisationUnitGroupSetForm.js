jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.organisationUnitGroupSet.name.length
		},
		description : {
			required : true,
			rangelength : r.organisationUnitGroupSet.description.length
		}
	};

	validation2( 'updateOrganisationUnitGroupSetForm', function( form )
	{
		validateAddOrganisationGroupSet( form )
	}, {
		'beforeValidateHandler' : function()
		{
			selectAllById( 'selectedGroups' );
		},
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", r.organisationUnitGroupSet.name.length );
	jQuery( "#description" ).attr( "maxlength", r.organisationUnitGroupSet.description.length );

	checkValueIsExist( "name", "validateOrganisationUnitGroupSet.action", {
		id : $organisationUnitGroupSet.id
	} );

	changeCompulsory( getFieldValue( 'compulsory' ) );
} );
