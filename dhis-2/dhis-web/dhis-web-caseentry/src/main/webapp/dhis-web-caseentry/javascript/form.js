
function organisationUnitSelected( orgUnits, orgUnitNames )
{
	jQuery('#createNewEncounterDiv').dialog('close');
	setInnerHTML( 'contentDiv', '' );
	setFieldValue( 'orgunitName', orgUnitNames[0] );
	
	hideById('dataEntryFormDiv');
	hideById('dataRecordingSelectDiv');
	showById('searchDiv');
	
	enable('searchObjectId');
	jQuery('#searchText').removeAttr('readonly');
	enable('searchBtn');	
	enable('listEntityInstanceBtn');
}
//------------------------------------------------------------------------------
// Load data entry form
//------------------------------------------------------------------------------

function loadDataEntry( programStageInstanceId )
{
	setInnerHTML('dataEntryFormDiv', '');
	showById('executionDateTB');
	showById('dataEntryFormDiv');
	setFieldValue( 'dueDate', '' );
	setFieldValue( 'executionDate', '' );
	disableCompletedButton(true);
	disable('uncompleteBtn');
	jQuery( 'input[id=programStageInstanceId]').val(programStageInstanceId );
			
	showLoader();	
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageInstanceId: programStageInstanceId
		},function( )
		{
			var executionDate = jQuery('#dataRecordingSelectDiv input[id=executionDate]').val();
			var completed = jQuery('#entryFormContainer input[id=completed]').val();
			var irregular = jQuery('#entryFormContainer input[id=irregular]').val();
			showById('inputCriteriaDiv');
			if( executionDate != '' && completed == 'false' )
			{
				disableCompletedButton(false);
			}
			else if( completed == 'true' )
			{
				disableCompletedButton(true);
			}
			showById('entryForm');
			hideLoader();
			hideById('contentDiv'); 
		} );
}

//--------------------------------------------------------------------------------------------
// Show search-form
//--------------------------------------------------------------------------------------------

function showSearchForm()
{
	hideById('dataRecordingSelectDiv');
	hideById('dataEntryFormDiv');
	hideById('addNewDiv');
	showById('searchDiv');
	showById('contentDiv');
	showById('mainLinkLbl');
	jQuery('#createNewEncounterDiv').dialog('close');
	jQuery('#resultSearchDiv').dialog('close');
}

//--------------------------------------------------------------------------------------------
// Show all entityInstances in select orgunit
//--------------------------------------------------------------------------------------------

isAjax = true;
function listAllTrackedEntityInstance()
{
	hideById('advanced-search');
	showLoader();
	jQuery('#contentDiv').load( 'listAllTrackedEntityInstances.action',{
			listAll:false,
			programId:	getFieldValue("programIdAddEntityInstance"),
			searchTexts: "prg_" + getFieldValue("programIdAddEntityInstance"),
			searchByUserOrgunits: false,
			searchBySelectedOrgunit:true
		},
		function()
		{
			hideById('dataRecordingSelectDiv');
			hideById('dataEntryFormDiv');
			showById('searchDiv');
			setInnerHTML('searchInforTD', i18n_list_all_tracked_entity_instances );
			setFieldValue('listAll', true);
			hideLoader();
		});
}

//-----------------------------------------------------------------------------
// Search EntityInstance
//-----------------------------------------------------------------------------

function searchEntityInstancesOnKeyUp( event )
{
	var key = getKeyCode( event );
	
	if ( key==13 )// Enter
	{
		validateAdvancedSearch();
	}
}

function getKeyCode(e)
{
	 if (window.event)
		return window.event.keyCode;
	 return (e)? e.which : null;
}

//--------------------------------------------------------------------------------------------
// Show selected data-recording
//--------------------------------------------------------------------------------------------

function showSelectedDataRecoding( entityInstanceId, programId )
{
	showLoader();
	hideById('searchDiv');
	hideById('dataEntryFormDiv');
	jQuery('#dataRecordingSelectDiv').load( 'selectDataRecording.action', 
		{
			entityInstanceId: entityInstanceId
		},
		function()
		{
			showById('dataRecordingSelectDiv');
			hideLoader();
			hideById('contentDiv');
			hideById('contentDiv');
			hideById('mainLinkLbl');
			setInnerHTML('singleProgramName',jQuery('#programIdAddEntityInstance option:selected').text());
			loadProgramStages( entityInstanceId, programId );
		});
}

function advancedSearch( params )
{
	$.ajax({
		url: 'searchTrackedEntityInstance.action',
		type:"POST",
		data: params,
		success: function( html ){
				statusSearching = 1;
				setInnerHTML( 'contentDiv', html );
				showById('contentDiv');
				setInnerHTML('searchInforTD', i18n_search_tracked_entity_instances );
				setFieldValue('listAll',false);
				jQuery( "#loaderDiv" ).hide();
			}
		});
}

//--------------------------------------------------------------------------------------------
// Load program-stages by the selected program
//--------------------------------------------------------------------------------------------

function loadProgramStages( entityInstanceId, programId )
{
	jQuery.getJSON( "loadProgramStageInstances.action",
		{
			entityInstanceId:entityInstanceId,
			programId: programId
		},  
		function( json ) 
		{   
			if( json.programStageInstances == 0)
			{
				createProgramInstance( entityInstanceId, programId );
			}
			else
			{
				jQuery("#selectForm [id=programStageId]").attr('psid', json.programStageInstances[0].programStageId);	
				loadDataEntry( json.programStageInstances[0].id );
			}
		});
}

function createProgramInstance( entityInstanceId, programId )
{
	jQuery.postJSON( "saveProgramEnrollment.action",
		{
			entityInstanceId: entityInstanceId,
			programId: programId,
			dateOfIncident: getCurrentDate(),
			enrollmentDate: getCurrentDate()
		}, 
		function( json ) 
		{
			jQuery("#selectForm [id=programStageId]").attr('psid', json.programStageId);	
			loadDataEntry( json.activeProgramStageInstanceId );
		});
};		
