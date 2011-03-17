jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		sectionName : {
			required : true,
			rangelength : r.section.name.length
		}
	};

	validation2( 'editDataSetForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			selectAllById( 'selectedList' )
		},
		'rules' : rules
	} );

	jQuery( "#sectionName" ).attr( "maxlength", r.section.name.length[1] );

	checkValueIsExist( "sectionName", "validateSection.action", {
		dataSetId : jQuery( "#dataSetId" ).val(),
		name : jQuery( "#sectionName" ).val(),
		sectionId : jQuery( "#sectionId" ).val()
	} );
} );
