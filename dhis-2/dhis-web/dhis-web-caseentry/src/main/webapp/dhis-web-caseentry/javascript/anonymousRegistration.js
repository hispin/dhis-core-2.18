
function organisationUnitSelected( orgUnits, orgUnitNames )
{
	hideById('dataEntryInfor');
	hideById('listDiv');
	
	jQuery.getJSON( "anonymousPrograms.action",{}, 
		function( json ) 
		{   
			clearListById('searchObjectId');
			clearListById('compulsoryDE');
			clearListById('programId');
			
			jQuery( '#programId').append( '<option value="" psid="">[' + i18n_please_select + ']</option>' );
			for ( i in json.programs ) {
				jQuery( '#programId').append( '<option value="' + json.programs[i].id +'" psid="' + json.programs[i].psid + '">' + json.programs[i].name + '</option>' );
			}
			
			disableCriteriaDiv();
		});
		
	setFieldValue( 'orgunitId', orgUnits[0] );
	setFieldValue( 'orgunitName', orgUnitNames[0] );
	hideById('listDiv');
	hideById('dataEntryInfor');
}

selection.setListenerFunction( organisationUnitSelected );

function disableCriteriaDiv()
{
	jQuery('#criteriaDiv :input').each( function( idx, item ){
		disable(this.id);
	});
	enable('orgunitName');
	enable('programId');
}

function enableCriteriaDiv()
{
	jQuery('#criteriaDiv :input').each( function( idx, item ){
		enable(this.id);
	});
}

function getDataElements()
{
	hideById('dataEntryInfor');
	hideById('listDiv');
	clearListById('searchObjectId');
	programStageId = jQuery('#programId option:selected').attr('psid');
	setFieldValue('programStageId', programStageId );
	setInnerHTML('reportDateDescriptionField', jQuery('#programId option:selected').attr('reportDateDes'));
	setInnerHTML('reportDateDescriptionField2', jQuery('#programId option:selected').attr('reportDateDes'));
	
	if( programStageId == '')
	{
		removeAllAttributeOption();
		disableCriteriaDiv();
		enable('orgunitName');
		enable('programId');
		hideById('listDiv');
		setFieldValue('searchText');
		return;
	}
	
	jQuery.getJSON( "getProgramStageDataElementList.action",
		{
			programStageId: getFieldValue('programStageId')
		}, 
		function( json ) 
		{   
			clearListById('searchObjectId');
			clearListById('compulsoryDE');
			
			jQuery( '#searchObjectId').append( '<option value="" >[' + i18n_please_select + ']</option>' );
			for ( i in json.programStageDataElements ) {
				jQuery( '#searchObjectId').append( '<option value="' + json.programStageDataElements[i].id + '" type="' + json.programStageDataElements[i].type +'">' + json.programStageDataElements[i].name + '</option>' );
				
				if( json.programStageDataElements[i].compulsory=='true' ){
					jQuery( '#compulsoryDE').append( '<option value="' + json.programStageDataElements[i].id + '"></option>');
				}
			}
			
			enableCriteriaDiv();
		});
}

function dataElementOnChange( this_ )
{
	var container = jQuery(this_).parent().parent().attr('id');
	var element = jQuery('#' + container + ' [id=searchText]');
	var valueType = jQuery('#' + container+ ' [id=searchObjectId] option:selected').attr('type');
	
	if( valueType == 'date' ){
		element.replaceWith( getDateField( container ) );
		datePickerValid( 'searchText_' + container );
		return;
	}
	else
	{
		$( '#searchText_' + container ).datepicker("destroy");
		$('#' + container + ' [id=dateOperator]').replaceWith("");
		
		if( valueType == 'bool' ){
			element.replaceWith( getTrueFalseBox() );
		}
		else if ( valueType=='optionset' ){
			element.replaceWith( searchTextBox );
			autocompletedFilterField( container + " [id=searchText]" , jQuery(this_).val() );
		}
		else{
			element.replaceWith( searchTextBox );
		}
	}
}

function autocompletedFilterField( idField, searchObjectId )
{
	var input = jQuery( "#" +  idField );
	input.autocomplete({
		delay: 0,
		minLength: 0,
		source: function( request, response ){
			$.ajax({
				url: "getOptions.action?id=" + searchObjectId + "&query=" + input.val(),
				dataType: "json",
				success: function(data) {
					response($.map(data.options, function(item) {
						return {
							label: item.o,
							id: item.o
						};
					}));
				}
			});
		},
		minLength: 2,
		select: function( event, ui ) {
			input.val(ui.item.value);
			input.autocomplete( "close" );
		}
	})
	.addClass( "ui-widget" );
}

function removeAllAttributeOption()
{
	jQuery( '#advancedSearchTB tbody tr' ).each( function( i, item )
    {
		if(i>0){
			jQuery( item ).remove();
		}
	})
}

function validateSearchEvents( listAll )
{	
	var flag = true;
	if( !listAll )
	{
		jQuery( '#advancedSearchTB tbody tr' ).each( function( i, row ){
			jQuery( this ).find(':input').each( function( idx, item ){
				var input = jQuery( item );
				if( input.attr('type') != 'button' && idx==0 && input.val()=='' ){
					showWarningMessage( i18n_specify_data_element );
					flag = false;
				}
			})
		});
	}
	
	if(flag){
		searchEvents( listAll );
	}
}

function searchEvents( listAll )
{
	hideById('dataEntryInfor');
	hideById('listDiv');
	setFieldValue('isShowEventList', listAll );
	
	var params = '';
	if(listAll){	
		params += '&startDate=';
		params += '&endDate=';
		jQuery( '#compulsoryDE option' ).each( function( i, item ){
			var input = jQuery( item );
			params += '&searchingValues=de_' + input.val() + '_false_';
		});
	}
	else{
		params += '&startDate=' + getFieldValue('startDate');
		params += '&endDate=' + getFieldValue('endDate');
		var value = '';
		var searchingValue = '';
		jQuery( '#advancedSearchTB tbody tr' ).each( function(){
			jQuery( this ).find(':input').each( function( idx, item ){
				var input = jQuery( item );
				if( input.attr('type') != 'button' ){
					if( idx==0 && input.val()!=''){
						searchingValue = 'de_' + input.val() + '_false_';
					}
					else if( input.val()!='' ){
						value += input.val().toLowerCase();
					}
				}
			});
			
			if( value !=''){
				searchingValue += getValueFormula(value);
			}
			params += '&searchingValues=' + searchingValue;
			searchingValue = '';
			value = '';
		})
	}
	
	params += '&facilityLB=' + $('input[name=facilityLB]:checked').val();
	params += '&level=' + $('select[id=level]').val();
	params += '&orgunitIds=' + getFieldValue('orgunitId');
	params += '&programStageId=' + jQuery('#programId option:selected').attr('psid');
	params += '&orderByOrgunitAsc=false';
	
	contentDiv = 'listDiv';
	showLoader();
	
	$.ajax({
		type: "POST",
		url: 'searchProgramStageInstances.action',
		data: params,
		success: function( html ){
			hideById('loaderDiv');
			hideById('dataEntryInfor');
			setInnerHTML( 'listDiv', html );
			
			var searchInfor = (listAll) ? i18n_list_all_events : i18n_search_events_by_dataelements;
			setInnerHTML( 'searchInforTD', searchInfor);
				
			showById('listDiv');
		}
    });
}

function getValueFormula( value )
{
	if( value.indexOf('"') != value.lastIndexOf('"') )
	{
		value = value.replace(/"/g,"'");
	}
	// if key is [xyz] && [=xyz]
	if( value.indexOf("'")==-1 ){
		var flag = value.match(/[>|>=|<|<=|=|!=]+[ ]*/);
	
		if( flag == null )
		{
			value = "='"+ value + "'";
		}
		else
		{
			value = value.replace( flag, flag + "'");
			value +=  "'";
		}
	}
	// if key is ['xyz'] && [='xyz']
	// if( value.indexOf("'") != value.lastIndexOf("'") )
	else
	{
		var flag = value.match(/[>|>=|<|<=|=|!=]+[ ]*/);
	
		if( flag == null )
		{
			value = "="+ value;
		}
	}
	
	return value;
}

function removeEvent( programStageId )
{	
	removeItem( programStageId, '', i18n_comfirm_delete_event, 'removeCurrentEncounter.action' );	
}

function showUpdateEvent( programStageInstanceId )
{
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('listDiv');
	setFieldValue('programStageInstanceId', programStageInstanceId);
	setInnerHTML('dataEntryFormDiv','');
	showLoader();
	
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageInstanceId: programStageInstanceId
		},function( )
		{
			var programName  = jQuery('#programId option:selected').text();
				programName += ' - ' + i18n_report_date + ' : ' + jQuery('#incidentDate').val();
			var programStageId = jQuery('#programId option:selected').attr('psid');
			jQuery('.stage-object-selected').attr('psid',programStageId);
			setInnerHTML('programName', programName );
			
			if( getFieldValue('completed')=='true' ){
				disableCompletedButton( true );
			}
			else{
				disableCompletedButton( false );
			}
				
			hideById('loaderDiv');
			showById('dataEntryInfor');
			showById('entryFormContainer');
		} );
}

function backEventList()
{
	hideById('dataEntryInfor'); 
	showById('selectDiv');
	showById('searchDiv');
	showById('listDiv');
	searchEvents( getFieldValue('isShowEventList') );
}

function showAddEventForm()
{
	setInnerHTML('dataEntryFormDiv','');
	setFieldValue('executionDate','');
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('listDiv');
	showById('dataEntryInfor');
}

function addNewEvent()
{
	jQuery.postJSON( "createAnonymousEncounter.action",
		{
			programId: jQuery('#programId option:selected').val(),
			executionDate: getFieldValue('executionDate')
		}, 
		function( json ) 
		{    
			if(json.response=='success')
			{
				setFieldValue('programStageInstanceId',json.message);
				showUpdateEvent( json.message )
			}
			else
			{
				showWarningMessage( json.message );
			}
		});
}

function completedAndAddNewEvent()
{
	doComplete( true );
}

function removeEmptyEvents()
{	
	var result = window.confirm( i18n_confirm_remove_empty_events );
    
    if ( result )
    {
		jQuery.getJSON( "removeEmptyEvents.action",
			{
				programStageId: jQuery('#selectDiv [id=programId] option:selected').attr('psid')
			}, 
			function( json ) 
			{   
				if(json.response=='success')
				{
					showSuccessMessage( i18n_remove_empty_events_success );
					validateSearchEvents( true )
				}
			});
	}
}
