
function organisationUnitSelected( orgUnits )
{
	disable('executionDate');
	setFieldValue('executionDate', '');
	
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
	if( getFieldValue('programId') == '' )
	{
		disable('executionDate');
		setFieldValue('executionDate', '');
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
		} );
}