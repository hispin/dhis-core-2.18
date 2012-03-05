
function organisationUnitSelected( orgUnits )
{
	hideById('contentDiv');
	setFieldValue('startDate', '');
	setFieldValue('endDate', '');
	
	$.getJSON( 'loadAnonymousPrograms.action',{}
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

			if( json.programInstances.length > 0 )
			{
				enable('generateBtn');
			}
			else
			{
				disable('generateBtn');
			}
			
		} );
}

selection.setListenerFunction( organisationUnitSelected );

function loadGeneratedReport()
{
	showLoader();
	isAjax = true;
	jQuery( "#contentDiv" ).load( "generateSingleEventReport.action",
	{
		programInstanceId: jQuery('select[id=programId] option:selected').attr('programInstanceId'),
		startDate: getFieldValue('startDate'),
		endDate: getFieldValue('endDate')
	}, function() 
	{ 
		hideLoader();
		hideById( 'message' );
		showById( 'contentDiv' );
	});
	
	return false;
}

function showDetails( programStageInstanceId ) 
{	
	$( '#viewRecordsDiv' )
		.load( 'viewAnonymousEvents.action',
			{
				programStageInstanceId: programStageInstanceId
			}
			,function( )
			{
				showById('entryFormContainer');
				jQuery("#entryFormContainer :input").attr("disabled", true);
				jQuery("#entryFormContainer .ui-datepicker-trigger").each(function()
				{
					jQuery(this).hide();
				});
				 
						
			}).dialog({
				title: i18n_reports,
				maximize: true, 
				closable: true,
				modal:false,
				overlay:{background:'#000000', opacity:0.1},
				width: 800,
				height: 400
			});
}

function entryFormContainerOnReady (){}