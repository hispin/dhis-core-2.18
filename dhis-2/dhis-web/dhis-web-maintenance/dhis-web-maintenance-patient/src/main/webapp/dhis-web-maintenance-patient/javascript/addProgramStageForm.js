jQuery( document ).ready( function()
{
	validation( 'addProgramStageForm', function( form ){ 
		form.submit();
	}, function(){
		
		compulsories = jQuery( "#compulsories" );
		compulsories.empty();
		
		jQuery("#selectedList").find("tr").each( function( i, item ){ 
			selectedDataElementsValidator.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
			var compulsory = jQuery( item ).find( "input[name='compulsory']:first");
			var checked = compulsory.attr('checked') ? true : false;
			compulsories.append( "<option value='" + checked + "' selected='true'>" + checked + "</option>" );
		});
	});
	
	jQuery("#availableList").dhisAjaxSelect({
			source: "../dhis-web-commons-ajax-json/getDataElements.action?domain=patient",
			iterator: "dataElements",
			connectedTo: 'selectedDataElementsValidator',
			handler: function(item) {
				var option = jQuery("<option />");
				option.text( item.name );
				option.attr( "value", item.id );

				return option;
			}
		});
		
	checkValueIsExist( "name", "validateProgramStage.action");	
});
