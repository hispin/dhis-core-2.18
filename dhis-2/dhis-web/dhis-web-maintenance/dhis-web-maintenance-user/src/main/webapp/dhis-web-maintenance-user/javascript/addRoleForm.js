jQuery( document ).ready( function()
{
	jQuery( "#name" ).focus();

	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.role.name.rangelength
		},
		description : {
			required : true,
			rangelength : r.role.description.rangelength
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

	jQuery( "#name" ).attr( "maxlength", r.role.name.rangelength[1] );
	jQuery( "#description" ).attr( "maxlength", r.role.description.rangelength[1] );

	/* remote validation */
	checkValueIsExist( "name", "validateRole.action" );

	sortList( 'availableListAuthority', 'ASC' );
} );
