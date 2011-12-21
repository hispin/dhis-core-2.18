
function organisationUnitSelected( orgUnits, orgUnitNames )
{
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

selection.setListenerFunction( organisationUnitSelected );
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

//--------------------------------------------------------------------------------------------
// Show search-form
//--------------------------------------------------------------------------------------------

function showSearchForm()
{
	hideById('dataRecordingSelectDiv');
	hideById('dataEntryFormDiv');
	showById('searchPatientDiv');
	showById('contentDiv');
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
 
function validateSearch( event )
{	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( searchValidationCompleted );
	request.sendAsPost('searchText=' + getFieldValue( 'searchText' ));
	request.send( 'validateSearch.action' );
}

function searchValidationCompleted( messageElement )
{
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

//------------------------------------------------------
// Multi Data-entry
//------------------------------------------------------

function multiDataEntryOrgunitSelected( orgUnits )
{
	hideById("listPatient");
	jQuery.postJSON( "getPrograms.action",
	{
	}, 
	function( json ) 
	{    
		enable('programId');
		enable('patientAttributeId');
		
		clearListById('programId');
		if(json.programs.length == 0)
		{
			disable('programId');
			disable('patientAttributeId');
		}
		else
		{
			addOptionById( 'programId', "0", i18n_select );
			
			for ( var i in json.programs ) 
			{
				addOptionById( 'programId', json.programs[i].id, json.programs[i].name );
			} 
		}	
		setFieldValue( 'orgunitName', json.organisationUnit );
	});
}

function selectProgram()
{
	setInnerHTML('listPatient', '');
	if( getFieldValue('programId') == 0 )
	{
		hideById('listPatient');
		return;
	}
	
	contentDiv = 'listPatient';
	showLoader();
	jQuery('#listPatient').load("getDataRecords.action",
		{
			programId:getFieldValue('programId'),
			sortPatientAttributeId: getFieldValue('patientAttributeId')
		}, 
		function()
		{
			showById("listPatient");
			hideLoader();
		});
}

function viewPrgramStageRecords( programStageInstanceId ) 
{
	$('#contentDataRecord').dialog('destroy').remove();
    $('<div id="contentDataRecord">' ).load("viewProgramStageRecords.action",
		{
			programStageInstanceId: programStageInstanceId
		}).dialog(
		{
			title: 'ProgramStage',
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 400
		});
}

function loadProgramStageRecords( programStageInstanceId ) 
{
	setInnerHTML('dataEntryFormDiv', '');
	showLoader();
    $('#dataEntryFormDiv' ).load("loadProgramStageRecords.action",
		{
			programStageInstanceId: programStageInstanceId
		}, function() {
			hideLoader();
		});
}

// -------------------------------------------------------------------------
// Show Patient chart list
// -------------------------------------------------------------------------

function patientChartList( patientId )
{
    $( '#patientChartListDiv' ).load('patientChartList.action?patientId=' + patientId ).dialog( {
        autoOpen : true,
        modal : true,
        height : 400,
        width : 500,
        resizable : false,
        title : 'Viewing Chart'
    } );
}
