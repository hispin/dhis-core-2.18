jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.organisationUnitGroupSet.name.rangelength
		},
		description : {
			required : true,
			rangelength : r.organisationUnitGroupSet.description.rangelength
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

	jQuery( "#name" ).attr( "maxlength", r.organisationUnitGroupSet.name.rangelength[1] );
	jQuery( "#description" ).attr( "maxlength", r.organisationUnitGroupSet.description.rangelength[1] );

	checkValueIsExist( "name", "validateOrganisationUnitGroupSet.action", {
		id : $organisationUnitGroupSet.id
	} );

	changeCompulsory( getFieldValue( 'compulsory' ) );
} );
