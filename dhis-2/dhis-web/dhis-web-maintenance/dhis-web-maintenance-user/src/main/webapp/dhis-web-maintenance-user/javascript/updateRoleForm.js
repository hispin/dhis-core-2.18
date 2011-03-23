jQuery( document ).ready( function()
{
	jQuery( "#name" ).focus();

	var r = getValidationRules();

	var rules = {
		name : {
			required : true,
			rangelength : r.role.name.length
		},
		description : {
			required : true,
			rangelength : r.role.description.length
		}
	};

	validation2( 'updateRoleForm', function( form )
	{
		selectAllById( 'selectedList' );
		selectAllById( 'selectedListAuthority' );
		form.submit();
	}, {
		'rules' : rules
	} );

	jQuery( "#name" ).attr( "maxlength", r.role.name.length[1] );
	jQuery( "#description" ).attr( "maxlength", r.role.description.length[1] );

	/* remote validation */
	checkValueIsExist( "name", "validateRole.action", {
		id : getFieldValue( 'id' )
	} );

	sortList( 'availableListAuthority', 'ASC' );
	sortList( 'selectedListAuthority', 'ASC' );
} );
