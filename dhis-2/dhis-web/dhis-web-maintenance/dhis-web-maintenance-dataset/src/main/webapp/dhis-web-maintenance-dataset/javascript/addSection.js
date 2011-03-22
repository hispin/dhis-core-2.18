jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		sectionName : {
			required : true,
			range : r.section.name.range
		},
		selectedList : {
			required : true
		}
	};

	validation2( 'addSectionForm', function( form )
	{
		form.submit()
	}, {
		'beforeValidateHandler' : function()
		{
			selectAllById( 'selectedList' );
		},
		'rules' : rules
	} );

	jQuery( "#sectionName" ).attr( "maxlength", r.section.name.range[1] );

	checkValueIsExist( "sectionName", "validateSection.action", {
		dataSetId : function()
		{
			return jQuery( "#dataSetId" ).val();
		},
		name : function()
		{
			return jQuery( "#sectionName" ).val();
		}
	} );
} );
