
function organisationUnitSelected( orgUnits )
{
	disable('executionDate');
	setFieldValue('executionDate', '');
	
	disable('createEventBtn');
	disable('deleteCurrentEventBtn');
	
	
	$.postJSON( 'loadAnonymousPrograms.action',{}
		, function( json ) 
		{
			clearListById( 'programId' );
			addOptionById( 'programId', '', i18n_please_select );
			
			for ( i in json.programInstances ) 
			{
				$('#programId').append('<option value=' + json.programInstances[i].id + ' singleevent="true" programInstanceId=' + json.programInstances[i].programInstanceId + '>' + json.programInstances[i].name + '</option>');
			}			
			
		} );
}

selection.setListenerFunction( organisationUnitSelected );


function showEventForm()
{	
	setFieldValue('executionDate', '');
	
	if( getFieldValue('programId') == '' )
	{
		hideById('dataEntryFormDiv');
		return;
	}
	
	showLoader();
	
	jQuery.postJSON( "loadProgramStages.action",
		{
			programId: getFieldValue('programId')
		}, 
		function( json ) 
		{    
			setFieldValue( 'programStageId', json.programStages[0].id );
			
			if( json.programStageInstances.length > 0 )
			{
				loadEventRegistrationForm();
			}
			else
			{
				enable('executionDate');
				enable('createEventBtn');
				disable('deleteCurrentEventBtn');
				disable('completeBtn');
				disable('validationBtn');
				
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
				enable('executionDate');
				enable('createEventBtn');
				disable('deleteCurrentEventBtn');
				disable('completeBtn');
				disable('validationBtn');
				
				$('#executionDate').change(function() {
					saveExecutionDate( getFieldValue('programStageId'), getFieldValue('executionDate') );
				});
			}
			else
			{
				if( getFieldValue('completed') == 'true')
				{
					disable('executionDate');
					enable('createEventBtn');
					enable('deleteCurrentEventBtn');
					disable('completeBtn');
					disable('validationBtn');
				} 
				else
				{
					enable('executionDate');
					disable('createEventBtn');
					enable('deleteCurrentEventBtn');
					enable('completeBtn');
					enable('validationBtn');
					
					$('#executionDate').change(function() {
						saveExecutionDate( getFieldValue('programStageId'), getFieldValue('executionDate') );
					});
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
			if(json.response=='success')
			{
				disable('createEventBtn');
				enable('deleteCurrentEventBtn');
				setFieldValue('programStageInstanceId', json.message );
				
				loadEventRegistrationForm();
			}
			else
			{
				showWarmingMessage( json.message );
			}
			
		});
}

function deleteCurrentEvent()
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
				
				showSuccessMessage( i18n_delete_current_event_success );
				setFieldValue('executionDate','');
				
				disable('deleteCurrentEventBtn');
				enable('createEventBtn');
				
				enable('executionDate');
				
				$('#executionDate').unbind('change');
			}
			else if( type == 'input' )
			{
				showWarningMessage( json.message );
			}
		});
}

function afterCompleteStage()
{
	enable('createEventBtn');
}