
function organisationUnitSelected( orgUnits )
{
	hideById('dataEntryFormDiv');
	disable( 'executionDate' );
	setFieldValue('executionDate', '');
	$('#executionDate').unbind('change');
	
	disable('createEventBtn');
	disable('deleteCurrentEventBtn');
	
	$.postJSON( 'loadAnonymousPrograms.action',{}
		, function( json ) 
		{
			clearListById( 'programId' );
			addOptionById( 'programId', '', i18n_please_select );
			
			var preSelectedProgramId = getFieldValue('selectedProgramId');
			for ( i in json.programInstances ) 
			{ 
				if( preSelectedProgramId == json.programInstances[i].id )
				{
					$('#programId').append('<option selected value=' + json.programInstances[i].id + ' singleevent="true" programInstanceId=' + json.programInstances[i].programInstanceId + '>' + json.programInstances[i].name + '</option>');
				}
				else
				{
					$('#programId').append('<option value=' + json.programInstances[i].id + ' singleevent="true" programInstanceId=' + json.programInstances[i].programInstanceId + '>' + json.programInstances[i].name + '</option>');
				}
			}

			if( byId('programId').selectedIndex > 0 )
			{
				loadEventForm();
			}
		} );
}

selection.setListenerFunction( organisationUnitSelected );


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
	
	jQuery.postJSON( "loadProgramStages.action",
		{
			programId: programId
		}, 
		function( json ) 
		{    
			setFieldValue( 'programStageId', json.programStages[0].id );
			setFieldValue( 'selectedProgramId', programId );
			$('#executionDate').bind('change');
			
			if( json.programStageInstances.length > 0 )
			{
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

function loadEventRegistrationForm()
{
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageId:getFieldValue('programStageId')
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

function deleteCurrentEvent()
{	
	var result = window.confirm( i18n_comfirm_delete_current_event );
    
    if ( result )
    {
		jQuery.postJSON( "removeCurrentEncounter.action",
			{
				programStageInstanceId: getFieldValue('programStageInstanceId')
			}, 
			function( json ) 
			{    
				var type = json.response;
				
				if( type == 'success' )
				{
					hideById('dataEntryFormDiv');
					byId('programId').selectedIndex = 0;
					
					disable('deleteCurrentEventBtn');
					enable('createEventBtn');
					
					setFieldValue('executionDate','');
					enable( 'executionDate' );
					$('#executionDate').unbind('change');
					
					selection.enable();
					
					showSuccessMessage( i18n_delete_current_event_success );
				}
				else if( type == 'input' )
				{
					showWarningMessage( json.message );
				}
			});
	}
}
