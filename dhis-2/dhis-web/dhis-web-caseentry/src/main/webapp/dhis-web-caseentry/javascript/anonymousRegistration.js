
function organisationUnitSelected( orgUnits )
{
	disable('executionDate');
	setFieldValue('executionDate', '');
	$('#executionDate').unbind('change');
	
	disable('createEventBtn');
	disable('deleteCurrentEventBtn');
	
	
	$.postJSON( 'loadAnonymousPrograms.action',{}
		, function( json ) 
		{
			clearListById( 'programId' );
			addOptionById( 'programId', '', i18n_please_select );
			
			for ( i in json.programs ) 
			{
				$('#programId').append('<option value=' + json.programs[i].id + ' singleevent="true">' + json.programs[i].name + '</option>');
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
			loadEventRegistrationForm();
			
	});
}

function loadEventRegistrationForm()
{
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageId:getFieldValue('programStageId')
		},function( )
		{
			enable('executionDate');
			hideById('loaderDiv');
			showById('dataEntryFormDiv');
			
			var programStageInstanceId = getFieldValue('programStageInstanceId');
			
			if( programStageInstanceId == '' )
			{
				$('#executionDate').unbind('change');
				disable('deleteCurrentEventBtn');
				enable('createEventBtn');
			}
			else
			{
				disable('createEventBtn');
				enable('deleteCurrentEventBtn');
			}
			
		} );
}

function createNewEvent()
{
	saveExecutionDate( getFieldValue('programStageId'), getFieldValue('executionDate') );
	loadEventRegistrationForm();
	
	disable('createEventBtn');
	enable('deleteCurrentEventBtn');
	
	$('#executionDate').change(function() {
			saveExecutionDate( getFieldValue('programStageId'), getFieldValue('executionDate') );
	});
}

function deleteCurrentEvent()
{	
	jQuery.postJSON( "removeCurrentEncounter.action",
		{
			programInstanceId: getFieldValue('programInstanceId')
		}, 
		function( json ) 
		{    
			var type = json.response;
			
			if( type == 'success' )
			{
				showSuccessMessage( i18n_delete_current_event_success );
				hideById('dataEntryFormDiv');
				setFieldValue('executionDate','');
				$('#executionDate').unbind('change');
				disable('deleteCurrentEventBtn');
				enable('createEventBtn');
			}
			else if( type == 'input' )
			{
				showWarningMessage( json.message );
			}
		});
}