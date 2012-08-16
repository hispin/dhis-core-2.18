
function orgunitSelected( orgUnits, orgUnitNames )
{
	hideById("listPatientDiv");
	clearListById('programIdAddPatient');
	$('#contentDataRecord').html('');
	jQuery.get("getPrograms.action",{}, 
		function(json)
		{
			jQuery( '#programIdAddPatient').append( '<option value="">' + i18n_please_select + '</option>' );
			for ( i in json.programs ) {
				if(json.programs[i].type==1){
					jQuery( '#programIdAddPatient').append( '<option value="' + json.programs[i].id +'" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
				}
			}
			enableBtn();
		});
}

selection.setListenerFunction( orgunitSelected );

function listAllPatient()
{
	hideById('listPatientDiv');
	contentDiv = 'listPatientDiv';
	$('#contentDataRecord').html('');
	showLoader();
	jQuery('#listPatientDiv').load('getSMSPatientRecords.action',
		{
			programId:getFieldValue('programIdAddPatient'),
			listAll:true
		}, 
		function()
		{
			showById('colorHelpLink');
			showById('listPatientDiv');
			hideLoader();
		});
}

function advancedSearch( params )
{
	$('#contentDataRecord').html('');
	params += "&searchTexts=prg_" + getFieldValue('programIdAddPatient');
	params += "&programId=" + getFieldValue('programIdAddPatient');
	$.ajax({
		url: 'getSMSPatientRecords.action',
		type:"POST",
		data: params,
		success: function( html ){
			jQuery('#listPatientDiv').html(html);
			showById('colorHelpLink');
			showById('listPatientDiv');
			hideLoader();
		}
	});
}

function getOutboundSmsList( programStageInstanceId, isSendSMS ) 
{
	$('#smsManagementDiv' ).load("getOutboundSmsList.action",
		{
			programStageInstanceId: programStageInstanceId
		}
		, function(){
			if(isSendSMS){
				$('#tabs').tabs({ selected: 0 }); 
			}
			else{
				$('#tabs').tabs({ selected: 1 });
			}
		}).dialog(
		{
			title:i18n_sms_message_management,
			maximize:true, 
			closable:true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width:800,
			height:500
		});
}

function sendSMS()
{
	programStageInstanceId = getFieldValue( 'programStageInstanceId' );
	jQuery.postUTF8( 'sendSMS.action',
	{
		programStageInstanceId: programStageInstanceId,
		gatewayId: getFieldValue( 'gatewayId' ),
		msg: getFieldValue( 'smsMessage' )
	}, function ( json )
	{
		if ( json.response == "success" ) {
			showSuccessMessage( json.message );
			jQuery('#smsManagementList').prepend("<tr><td>" + getFieldValue('currentDate') + "</td><td>" + getFieldValue('smsMessage') + "</td></tr>");
			var noMessage = eval( getInnerHTML('noMessageDiv_' + programStageInstanceId)) + 1;
			jQuery('#noMessageDiv_' + programStageInstanceId).html(noMessage);
		}
		else {
			showErrorMessage( json.message, 7000 );
		}
	} ); 
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
