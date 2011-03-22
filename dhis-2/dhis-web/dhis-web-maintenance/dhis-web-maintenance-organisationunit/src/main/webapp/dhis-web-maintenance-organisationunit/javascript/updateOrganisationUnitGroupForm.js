jQuery(document).ready(	function(){
	var r = getValidationRules();
	
	var rules = {
		name: {
			required:true,
			rangelength : r.organisationUnitGroup.name.length
		}
	};

	validation2( 'updateOrganisationUnitGroupForm', function( form ) { form.submit() },
	{
		'rules': rules
	});	

	jQuery( "#name" ).attr( "maxlength", r.organisationUnitGroup.name.length[1] );
});	
