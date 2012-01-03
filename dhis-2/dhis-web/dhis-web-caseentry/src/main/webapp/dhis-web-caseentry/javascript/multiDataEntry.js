
function multiDataEntryOrgunitSelected( orgUnits, orgUnitNames )
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
		setFieldValue( 'orgunitName', orgUnitNames[0] );
	});
}

selection.setListenerFunction( multiDataEntryOrgunitSelected );



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
