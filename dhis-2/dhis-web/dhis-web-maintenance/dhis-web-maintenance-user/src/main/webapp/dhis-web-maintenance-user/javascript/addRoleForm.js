jQuery( document ).ready( function()
{
	jQuery( "#name" ).focus();

	var rules = {
		name : {
			required : true,
			minlength : 2
		},
		description : {
			required : true
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

	jQuery( "#name" ).attr( "maxlength", "140" );

	/* remote validation */
	checkValueIsExist( "name", "validateRole.action" );

	sortList('availableListAuthority','ASC');
} );
