var duplicate = false;
jQuery( document ).ready( function()
{
	showHideUserGroup();
	validation( 'updateProgramStageForm', function( form ){ 
		form.submit();
	}, function(){
		var selectedDataElementsValidator = jQuery( "#selectedDataElementsValidator" );
		selectedDataElementsValidator.empty();
		
		var compulsories = jQuery( "#compulsories" );
		compulsories.empty();
		
		var displayInReports = jQuery( "#displayInReports" );
		displayInReports.empty();
		
		var allowDateInFutures = jQuery( "#allowDateInFutures" );
		allowDateInFutures.empty();
		
		var displayAsRadioButtons = jQuery( "#displayAsRadioButtons" );
		displayAsRadioButtons.empty();
		
		var daysAllowedSendMessages = jQuery( "#daysAllowedSendMessages" );
		daysAllowedSendMessages.empty();
		
		var templateMessages = jQuery( "#templateMessages" );
		templateMessages.empty();
		
		var allowProvidedElsewhere = jQuery( "#allowProvidedElsewhere" );
		allowProvidedElsewhere.empty();
		
		var sendTo = jQuery( "#sendTo" );
		sendTo.empty();
		
		var whenToSend = jQuery( "#whenToSend" );
		whenToSend.empty();
		
		var userGroup = jQuery( "#userGroup" );
		userGroup.empty();
		
		jQuery("#selectedList").find("tr").each( function( i, item ){ 
			
			selectedDataElementsValidator.append( "<option value='" + item.id + "' selected='true'>" + item.id + "</option>" );
			
			var compulsory = jQuery( item ).find( "input[name='compulsory']:first");
			var checked = compulsory.attr('checked') ? true : false;
			compulsories.append( "<option value='" + checked + "' selected='true'></option>" );
			
			var allowProvided = jQuery( item ).find( "input[name='allowProvided']:first");
			checked = allowProvided.attr('checked') ? true : false;
			allowProvidedElsewhere.append( "<option value='" + checked + "' selected='true'></option>" );
			
			var displayInReport = jQuery( item ).find( "input[name='displayInReport']:first");
			checked = displayInReport.attr('checked') ? true : false;
			displayInReports.append( "<option value='" + checked + "' selected='true'></option>" );
		
			var allowDateInFuture = jQuery( item ).find( "input[name='allowDateInFuture']:first");
			checked = allowDateInFuture.attr('checked') ? true : false;
			allowDateInFutures.append( "<option value='" + checked + "' selected='true'></option>" );
			
			var displayAsRadioButton = jQuery( item ).find( "input[name='displayAsRadioButton']:first");
			checked = displayAsRadioButton.attr('checked') ? true : false;
			displayAsRadioButtons.append( "<option value='" + checked + "' selected='true'></option>" );
		});
		jQuery(".daysAllowedSendMessage").each( function( i, item ){ 
			daysAllowedSendMessages.append( "<option value='" + jQuery(item).attr('realvalue') + "' selected='true'></option>" );
		});
		jQuery(".templateMessage").each( function( i, item ){ 
			templateMessages.append( "<option value='" + item.value + "' selected='true'></option>" );
		});
		jQuery(".sendTo").each( function( i, item ){ 
			sendTo.append( "<option value='" + item.value + "' selected='true'></option>" );
		});
		jQuery(".whenToSend").each( function( i, item ){ 
			whenToSend.append( "<option value='" + item.value + "' selected='true'></option>" );
		});
		jQuery(".userGroup").each( function( i, item ){ 
			userGroup.append( "<option value='" + item.value + "' selected='true'></option>" );
		});
	});
	
	checkValueIsExist( "name", "validateProgramStage.action", {id:getFieldValue('programId'), programStageId:getFieldValue('id')});	
	
	jQuery("#availableList").dhisAjaxSelect({
		source: "../dhis-web-commons-ajax-json/getDataElements.action?domain=patient",
		iterator: "dataElements",
		connectedTo: 'selectedDataElementsValidator',
		handler: function(item) {
			var option = jQuery("<option />");
			option.text( item.name );
			option.attr( "value", item.id );
			
			if( item.optionSet == "true"){
				option.attr( "valuetype", "optionset" );
			}
			else{
				option.attr( "valuetype", item.type );
			}
			
			var flag = false;
			jQuery("#selectedList").find("tr").each( function( k, selectedItem ){ 
				if(selectedItem.id == item.id )
				{
					flag = true;
					return;
				}
			});
			
			if(!flag) return option;
		}
	});
});
