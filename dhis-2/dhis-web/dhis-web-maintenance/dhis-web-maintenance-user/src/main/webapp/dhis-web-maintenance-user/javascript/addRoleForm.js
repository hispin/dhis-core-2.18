jQuery( document ).ready( function()
{
	jQuery( "#name" ).focus();

	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			range : r.role.name.range
		},
		description : {
			required : true,
			range : r.role.description.range
		}
	}

	validation2( 'addRoleForm', function( form )
	{
		selectAllById( 'selectedList' );
		selectAllById( 'selectedListAuthority' );
		form.submit();
	}, {
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", r.role.name.range[1] );
	jQuery( "#description" ).attr( "maxlength", r.role.description.range[1] );

	/* remote validation */
	checkValueIsExist( "name", "validateRole.action" );

	sortList( 'availableListAuthority', 'ASC' );
} );
