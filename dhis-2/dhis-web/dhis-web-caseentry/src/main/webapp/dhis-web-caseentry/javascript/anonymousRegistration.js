
function organisationUnitSelected( orgUnits, orgUnitNames )
{
	setFieldValue( 'orgunitId', orgUnits[0] );
	setFieldValue( 'orgunitName', orgUnitNames[0] );
	hideById('listDiv');
	hideById('dataEntryInfor');
}

selection.setListenerFunction( organisationUnitSelected );

function getDataElements()
{
	hideById('dataEntryInfor');
	hideById('listDiv');
	clearListById('dataElementId');
	programStageId = jQuery('#programId option:selected').attr('programStageId');
	setFieldValue('programStageId', programStageId );
	
	if( programStageId == '')
	{
		removeAllAttributeOption();
		jQuery('#criteriaDiv :input').each( function( idx, item ){
			disable(this.id);
		});
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
			clearListById('dataElementId');
			clearListById('compulsoryDE');
			
			jQuery( '#dataElementId').append( '<option value="">[' + i18n_please_select + ']</option>' );
			for ( i in json.programStageDataElements ) {
				jQuery( '#dataElementId').append( '<option value="' + json.programStageDataElements[i].id + '">' + json.programStageDataElements[i].name + '</option>' );
				
				if( json.programStageDataElements[i].compulsory=='true' ){
					jQuery( '#compulsoryDE').append( '<option value="' + json.programStageDataElements[i].id + '"></option>');
				}
			}
			
			jQuery('#criteriaDiv :input').each( function( idx, item ){
				enable(this.id);
			});
		});
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
	if( listAll && jQuery( '#compulsoryDE option' ).length == 0 ){
		flag = false;
	}
	else if( !listAll )
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
		jQuery( '#advancedSearchTB tbody tr' ).each( function(){
			var searchingValue = '';
			jQuery( this ).find(':input').each( function( idx, item ){
				var input = jQuery( item );
				if( input.attr('type') != 'button' ){
					if( idx==0 && input.val()!=''){
						searchingValue = 'de_' + input.val() + '_false_';
					}
					else if( input.val()!='' ){
						searchingValue += getValueFormula(input.val().toLowerCase());
					}
				}
			});
			params += '&searchingValues=' + searchingValue;
		})
	}
	
	params += '&facilityLB=' + $('input[name=facilityLB]:checked').val();
	params += '&level=' + $('input[name=level]:checked').val();
	params += '&orgunitIds=' + getFieldValue('orgunitId');
	params += '&programStageId=' + getFieldValue('programStageId');
	params += '&orderByOrgunitAsc=' + true;
	
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

function removeEvent( psId )
{	
	removeItem( psId, '', i18n_comfirm_delete_event, 'removeCurrentEncounter.action' );	
}

function showUpdateEvent( psId )
{
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('listDiv');
	setFieldValue('programStageInstanceId', psId);
	showLoader();
	
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageInstanceId: psId
		},function( )
		{
			var programName  = jQuery('#programId option:selected').text();
				programName += ' - ' + i18n_report_date + ' : ' + jQuery('#incidentDate').val();
			
			setInnerHTML('programName', programName );
			
			if( getFieldValue('completed')=='true' ){
				disable('completeBtn');
			}
			else{
				enable('completeBtn');
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
}

function showAddEventForm()
{
	jQuery.postJSON( "createAnonymousEncounter.action",
		{
			programId: jQuery('#programId option:selected').val(),
			executionDate: getFieldValue('executionDateNewEvent')
		}, 
		function( json ) 
		{    
			if(json.response=='success')
			{
				setFieldValue('programStageInstanceId', json.message );
				showUpdateEvent( json.message )
			}
			else
			{
				showWarningMessage( json.message );
			}
		});
}

function loadEventRegistrationForm()
{
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('listDiv');
	showLoader();
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageInstanceId:getFieldValue('programStageInstanceId')
		},function( )
		{
			hideById('loaderDiv');
			showById('dataEntryFormDiv');
			
			var programStageInstanceId = getFieldValue('programStageInstanceId');
			if( programStageInstanceId == '' )
			{
				enable('createEventBtn');
				disable('deleteCurrentEventBtn');
				disable('completeBtn');
				enable( 'executionDate' );
				$('#executionDate').bind('change');
			}
			else
			{
				enable( 'executionDate' );
				if( getFieldValue('completed') == 'true')
				{
					enable('createEventBtn');
					disable('deleteCurrentEventBtn');
					disable('completeBtn');
					jQuery('#executionDate').unbind('change');
				} 
				else
				{
					disable('createEventBtn');
					enable('deleteCurrentEventBtn');
					enable('completeBtn');
					jQuery('#executionDate').bind('change');
				}
			}
			
		} );
}

function loadEventForm()
{	
	hideById('dataEntryFormDiv');
	setFieldValue('executionDate', '');
	disable( 'executionDate' );
	disable('createEventBtn');
	disable('deleteCurrentEventBtn');
		
	var programId = getFieldValue('programId');
	if( programId == '' )
	{
		$('#executionDate').unbind('change');
		return;
	}
	
	showLoader();
	
	jQuery.getJSON( "loadProgramStageInstances.action",
		{
			programId: programId
		}, 
		function( json ) 
		{    
			if( json.programStageInstances.length > 0 )
			{
				setFieldValue( 'programStageInstanceId', json.programStageInstances[0].id );
				setFieldValue( 'selectedProgramId', programId );
				$('#executionDate').bind('change');
				loadEventRegistrationForm();
			}
			else
			{
				enable( 'executionDate' );
				enable('createEventBtn');
				disable('deleteCurrentEventBtn');
				disable('completeBtn');
				hideById('loaderDiv');
			}	
	});
}

function createNewEvent()
{
	jQuery.postJSON( "createAnonymousEncounter.action",
		{
			programInstanceId: jQuery('select[id=programId] option:selected').attr('programInstanceId'),
			executionDate: getFieldValue('executionDate')
		}, 
		function( json ) 
		{    
			selection.enable();
			
			if(json.response=='success')
			{
				disable('createEventBtn');
				enable('deleteCurrentEventBtn');
				setFieldValue('programStageInstanceId', json.message );
				
				selection.disable();
				
				loadEventRegistrationForm();
			}
			else
			{
				showWarningMessage( json.message );
			}
		});
}
