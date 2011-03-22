jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			range : r.organisationUnitGroupSet.name.range
		},
		description : {
			required : true,
			range : r.organisationUnitGroupSet.description.range
		}
	};

	validation2( 'addOrganisationUnitGroupSetForm', function( form )
	{
		validateAddOrganisationGroupSet( form )
	}, {
		'beforeValidateHandler' : function()
		{
			selectAllById( 'selectedGroups' );
		},
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", r.organisationUnitGroupSet.name.range[1] );
	jQuery( "#description" ).attr( "maxlength", r.organisationUnitGroupSet.description.range[1] );

	checkValueIsExist( "name", "validateOrganisationUnitGroupSet.action" );

	changeCompulsory( getFieldValue( 'compulsory' ) );
} );
