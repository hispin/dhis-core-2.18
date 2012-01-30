jQuery( document ).ready( function()
{
	validation( 'updateProgramStageForm', function( form ){ 
		form.submit() ;
	}, function(){
		selectedDataElementsValidator = jQuery( "#selectedDataElementsValidator" );
		selectedDataElementsValidator.empty();
		
		compulsories = jQuery( "#compulsories" );
		compulsories.empty();
		
		showOnReport = jQuery( "#showOnReport" );
		showOnReport.empty();
		
		var hasDeShowReport = false;
		jQuery("#selectedList").find("tr").each( function( i, item ){ 
			selectedDataElementsValidator.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
			var compulsory = jQuery( item ).find( "input[name='compulsory']:first");
			var isShow = jQuery( item ).find( "input[name='isShow']:first");

			var checked = compulsory.attr('checked') ? true : false;
			compulsories.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
			
			checked = isShow.attr('checked') ? true : false;
			showOnReport.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
			if( checked )
			{
				hasDeShowReport = true;
				showOnReport = jQuery( "#checkShowOnReport" )..append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
			}
		});
	});
	
	checkValueIsExist( "name", "validateProgramStage.action", {id:getFieldValue('id')});	
});

