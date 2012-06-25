
function multiDataEntryOrgunitSelected( orgUnits, orgUnitNames )
{
	hideById("listPatient");
	jQuery('#programDiv').load("getPrograms.action",{}, 
		function()
		{
			showById('programDiv');
			hideById('btnBack');
			setFieldValue( 'orgunitName', orgUnitNames[0] );
			hideLoader();
		});
}

selection.setListenerFunction( multiDataEntryOrgunitSelected );

function selectProgram( programId, programName )
{
	setInnerHTML('listPatient', '');
	contentDiv = 'listPatient';
	showLoader();
	jQuery('#listPatient').load("getDataRecords.action",
		{
			programId:programId,
			sortPatientAttributeId:0
		}, 
		function()
		{
			hideById('programDiv');
			
			setFieldValue('programId', programId);
			setInnerHTML('programName', programName);
			showById('programName');
			
			showById('btnBack');
			showById("listPatient");
			hideLoader();
		});
}

function backButtonOnClick()
{
	hideById("listPatient");
	hideById('btnBack');
	hideById('programName');
	showById('programDiv');
}
function viewPrgramStageRecords( programStageInstanceId ) 
{
	jQuery("#patientList input[name='programStageBtn']").each(function(i,item){
		jQuery(item).removeClass('stage-object-selected');
	});
	jQuery( '#' + prefixId + programStageInstanceId ).addClass('stage-object-selected');
	
	$('#contentDataRecord').dialog('destroy').remove();
    $('<div id="contentDataRecord">' ).load("viewProgramStageRecords.action",
		{
			programStageInstanceId: programStageInstanceId
		}).dialog(
		{
			title:i18n_program_stage,
			maximize:true, 
			closable:true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width:800,
			height:400
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
