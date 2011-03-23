jQuery( document ).ready( function()
{
	var r = getValidationRules();

	var rules = {
		sectionName : {
			required : true,
			rangelength : r.section.name.rangelength
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

	jQuery( "#sectionName" ).attr( "maxlength", r.section.name.rangelength[1] );

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
