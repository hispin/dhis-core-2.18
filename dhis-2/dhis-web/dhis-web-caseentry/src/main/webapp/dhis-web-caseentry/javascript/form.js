
function organisationUnitSelected( orgUnits, orgUnitNames )
{
	jQuery('#createNewEncounterDiv').dialog('close');
	setInnerHTML( 'contentDiv', '' );
	setFieldValue( 'orgunitName', orgUnitNames[0] );
	
	hideById('dataEntryFormDiv');
	hideById('dataRecordingSelectDiv');
	showById('searchPatientDiv');
	
	enable('searchingAttributeId');
	jQuery('#searchText').removeAttr('readonly');
	enable('searchBtn');	
	enable('listPatientBtn');
}

//--------------------------------------------------------------------------------------------
// Show search-form
//--------------------------------------------------------------------------------------------

function showSearchForm()
{
	hideById('dataRecordingSelectDiv');
	hideById('dataEntryFormDiv');
	showById('searchPatientDiv');
	showById('contentDiv');
	jQuery('#createNewEncounterDiv').dialog('close');
}

//--------------------------------------------------------------------------------------------
// Show all patients in select orgunit
//--------------------------------------------------------------------------------------------

isAjax = true;
function listAllPatient()
{
	showLoader();
	jQuery('#contentDiv').load( 'listAllPatients.action',{},
		function()
		{
			hideById('dataRecordingSelectDiv');
			hideById('dataEntryFormDiv');
			showById('searchPatientDiv');
			hideLoader();
		});
}

//--------------------------------------------------------------------------------------------
// Show selected data-recording
//--------------------------------------------------------------------------------------------

function showSelectedDataRecoding( patientId )
{
	showLoader();
	hideById('searchPatientDiv');
	hideById('dataEntryFormDiv');
	jQuery('#dataRecordingSelectDiv').load( 'selectDataRecording.action', 
		{
			patientId: patientId
		},
		function()
		{
			showById('dataRecordingSelectDiv');
			hideLoader();
			hideById('contentDiv');
		});
}

//-----------------------------------------------------------------------------
// Search Patient
//-----------------------------------------------------------------------------

function searchPatientsOnKeyUp( event )
{
	var key = getKeyCode( event );
	
	if ( key==13 )// Enter
	{
		validateSearch();
	}
}

function getKeyCode(e)
{
	 if (window.event)
		return window.event.keyCode;
	 return (e)? e.which : null;
}
 
function validateSearch()
{
	$.postUTF8( 'validateSearch.action',{ searchText:getFieldValue( 'searchText' )}
		, searchValidationCompleted	);
}

function searchValidationCompleted( messageElement )
{
    messageElement = messageElement.getElementsByTagName( 'message' )[0];
	var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
	
    if ( type == 'success' )
    {
		showLoader();
		hideById('dataEntryFormDiv');
		hideById('dataRecordingSelectDiv');
		$('#contentDiv').load( 'searchPatient.action', 
			{
				searchingAttributeId: getFieldValue('searchingAttributeId'), 
				searchText: getFieldValue('searchText')
			},
			function()
			{
				showById('searchPatientDiv');
				hideLoader();
			});
    }
    else if ( type == 'error' )
    {
        showErrorMessage( i18n_searching_patient_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        showWarningMessage( message );
    }
}

//--------------------------------------------------------------------------------------------
// Show selected data-recording
//--------------------------------------------------------------------------------------------

function showSelectedDataRecoding( patientId )
{
	showLoader();
	hideById('searchPatientDiv');
	hideById('dataEntryFormDiv');
	jQuery('#dataRecordingSelectDiv').load( 'selectDataRecording.action', 
		{
			patientId: patientId
		},
		function()
		{
			showById('dataRecordingSelectDiv');
			hideLoader();
			hideById('contentDiv');
		});
}

function searchPatient()
{
	$.ajax({
		url: 'searchPatient.action',
		type:"POST",
		data: getParamsForDiv('searchPatientDiv'),
		success: function( html ){
				statusSearching = 1;
				setInnerHTML( 'contentDiv', html );
				showById('contentDiv');
				jQuery( "#loaderDiv" ).hide();
			}
		});
}