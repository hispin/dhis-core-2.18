
function orgunitSelected( orgUnits, orgUnitNames )
{
	hideById("listPatientDiv");
	clearListById('programIdAddPatient');
	$('#contentDataRecord').html('');
	setFieldValue('orgunitName', orgUnitNames[0]);
	setFieldValue('orgunitId', orgUnits[0]);
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
	setFieldValue('listAll', "true");
	hideById('listPatientDiv');
	contentDiv = 'listPatientDiv';
	$('#contentDataRecord').html('');
	hideById('advanced-search');
	
	var date = new Date();
	var d = date.getDate() - 1;
	var m = date.getMonth();
	var y1 = date.getFullYear() - 100;
	var y2 = date.getFullYear();
	var startDate = jQuery.datepicker.formatDate( dateFormat, new Date(y1, m, d) );
	var endDate = jQuery.datepicker.formatDate( dateFormat, new Date(y2, m, d) );
	
	var programId = getFieldValue('programIdAddPatient');
	var searchTexts = "stat_" + programId + "_" 
				+ startDate + "_" + endDate + "_" 
				+ getFieldValue('orgunitId') + "_4";
	
	showLoader();
	jQuery('#listPatientDiv').load('getSMSPatientRecords.action',
		{
			programId:programId,
			listAll:false,
			searchBySelectedOrgunit: false,
			searchTexts: searchTexts
		}, 
		function()
		{
			setInnerHTML('searchInforLbl',i18n_list_all_patients);
			showById('colorHelpLink');
			showById('listPatientDiv');
			resize();
			hideLoader();
		});
}

function advancedSearch( params )
{
	setFieldValue('listAll', "false");
	$('#contentDataRecord').html('');
	params += "&programId=" + getFieldValue('programIdAddPatient');
	$.ajax({
		url: 'getSMSPatientRecords.action',
		type:"POST",
		data: params,
		success: function( html ){
			jQuery('#listPatientDiv').html(html);
			showById('colorHelpLink');
			showById('listPatientDiv');
			resize();
			hideLoader();
		}
	});
}

function getOutboundSmsList( programStageInstanceId, isSendSMS ) 
{
	setFieldValue('sendToList', "false");
	setInnerHTML('patientProgramTrackingDiv', '');
	$('#smsManagementDiv' ).load("getOutboundSmsList.action",
		{
			programStageInstanceId: programStageInstanceId
		}
		, function(){
			hideById('searchDiv');
			hideById('listPatientDiv');
			showById('smsManagementDiv');
		});
}

function showSendSmsForm(programStageName, programStageInstanceId)
{
	setFieldValue( 'programStageInstanceId', programStageInstanceId );
	setFieldValue( 'programStageName', programStageName );
	$('#sendSmsFormDiv' ).load("getGateway.action").dialog(
		{
			title:i18n_send_sms,
			maximize:true, 
			closable:true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width:400,
			height:250
		});
}

function showCommentList( programStageInstanceId, isSendSMS ) 
{
	setFieldValue('sendToList', "false");
	$('#smsManagementDiv' ).load("getOutboundSmsList.action",
		{
			programStageInstanceId: programStageInstanceId
		}
		, function(){
			hideById('smsManagementForm');
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
	var sendToList = getFieldValue('sendToList');
	if( sendToList == 'false'){	
		sendSmsOnePatient()
	}
	else{
		sendSmsToList();
	}
	
}

function sendSmsOnePatient()
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
				var currentTime = date.getHours() + ":" + date.getMinutes();
				jQuery('#commentTB').prepend("<tr><td>" + getFieldValue('currentDate') + " " + currentTime + "</td>"
					+ "<td>" + getFieldValue('programStageName') + "</td>"
					+ "<td>" + getFieldValue('currentUsername') + "</td>"
					+ "<td>" + getFieldValue('smsMessage') + "</td></tr>");
				var noMessage = eval( getInnerHTML('noMessageDiv_' + programStageInstanceId)) + 1;
			}
			else {
				showErrorMessage( json.message, 7000 );
			}
			
			if( jQuery("#commentTB tr.hidden").length > 0 ){
				commentDivToggle(true);
			}
			else{
				commentDivToggle(false);
			}
			jQuery('#sendSmsFormDiv').dialog('close');
		}); 
}

function sendSmsToList()
{
	params = getSearchParams();
	params += "&gatewayId=" + getFieldValue( 'gatewayId' );
	params += "&msg=" + getFieldValue( 'smsMessage' );
	params += "&programStageInstanceId=" + getFieldValue('programStageInstanceId');
	$.ajax({
		url: 'sendSMSTotList.action',
		type:"POST",
		data: params,
		success: function( json ){
			if ( json.response == "success" ) {
				var programStageName = getFieldValue('programStageName');
				var currentTime = date.getHours() + ":" + date.getMinutes();
				jQuery('#commentTB').prepend("<tr><td>" + getFieldValue("currentDate") + " " + currentTime + "</td>"
						+ "<td>" + programStageName + "</td>"
						+ "<td>" + getFieldValue( 'smsMessage' ) + "</td></tr>");
				showSuccessMessage( json.message );
			}
			else {
				showErrorMessage( json.message );
			}
			jQuery('#sendSmsFormDiv').dialog('close')
		}
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

function keypress(event, field, programStageInstanceId )
{
	var key = getKeyCode( event );
	if ( key==13 ){ // Enter
		addComment( field, programStageInstanceId );
	}
}

function addComment( field, programStageInstanceId )
{
	field.style.backgroundColor = SAVING_COLOR;
	var commentText = getFieldValue( 'commentText' );
	if( commentText != '')
	{
		jQuery.postUTF8( 'addPatientComment.action',
			{
				programStageInstanceId: programStageInstanceId,
				commentText: commentText 
			}, function ( json )
			{
				var programStageName = jQuery("#ps_" + programStageInstanceId).attr('programStageName');
				var date = new Date();
				var currentTime = date.getHours() + ":" + date.getMinutes();
				jQuery('#commentTB').prepend("<tr><td>" + getFieldValue("currentDate") + " " + currentTime + "</td>"
						+ "<td>" + programStageName + "</td>"
						+ "<td>" + getFieldValue('currentUsername') + "</td>"
						+ "<td>" + commentText + "</td></tr>");
				field.value="";
				showSuccessMessage( i18n_comment_added );
				field.style.backgroundColor = SUCCESS_COLOR;
				
				if( jQuery("#commentTB tr.hidden").length > 0 ){
					commentDivToggle(true);
				}
				else{
					commentDivToggle(false);
				}
			});
	}
}

function removeComment( programStageInstanceId, commentId )
{
	jQuery.postUTF8( 'removePatientComment.action',
		{
			programStageInstanceId:programStageInstanceId,
			id: commentId
		}, function ( json )
		{
			showSuccessMessage( json.message );
			hideById( 'comment_' + commentId );
		} );
}

function eventFlowToggle( programInstanceId )
{
	jQuery("#tb_" + programInstanceId + " .stage-object").each( function(){
		var programStageInstance = this.id.split('_')[1];
		jQuery('#arrow_' + programStageInstance ).toggle();
		jQuery('#ps_' + programStageInstance ).toggle();
		jQuery(this).removeClass("stage-object-selected");
	});
		
	if( jQuery("#tb_" + programInstanceId + " .searched").length>0)
	{	
		var id = jQuery("#tb_" + programInstanceId + " .searched").attr('id').split('_')[1];
		showById("arrow_" + id);
		showById("ps_" + id );
	}
	resize();
}

// --------------------------------------------------------------------
// Patient program tracking
// --------------------------------------------------------------------

function showPatientProgramTracking(programInstanceId)
{
	hideById("listPatientDiv");
	hideById("searchDiv");
	setInnerHTML("smsManagementDiv", "");
	showLoader();	
	$( '#patientProgramTrackingDiv' ).load( "patientProgramTracking.action", 
		{ 
			programInstanceId:programInstanceId
		},function( )
		{
			showById('patientProgramTrackingDiv');
			hideLoader();
		});
}

function commentDivToggle(isHide)
{
	var index = 1;
	jQuery("#commentTB tr").removeClass("hidden");
	jQuery("#commentTB tr").each( function(){
		if(isHide && index > 5){
			jQuery(this).addClass("hidden");
		}
		else if(!isHide){		
			jQuery(this).removeClass("hidden");
		}
		index++;
	});
	
	if( isHide ){
		showById('showCommentBtn');
		hideById('hideCommentBtn');
	}
	else
	{
		hideById('showCommentBtn');
		showById('hideCommentBtn');
	}
}

function onClickBackBtn()
{
	showById('searchDiv');
	showById('listPatientDiv');
	hideById('smsManagementDiv');
	hideById('patientProgramTrackingDiv');	
}

function reloadRecordList()
{
	var listAll = getFieldValue('listAll');
	var startDate = getFieldValue('startDueDate');
	var endDate = getFieldValue('endDueDate');
	var status = getFieldValue('statusEvent');
	if( listAll )
	{
		var date = new Date();
		var d = date.getDate() - 1;
		var m = date.getMonth();
		var y1 = date.getFullYear() - 100;
		var y2 = date.getFullYear();
		startDate = jQuery.datepicker.formatDate( dateFormat, new Date(y1, m, d) );
		endDate = jQuery.datepicker.formatDate( dateFormat, new Date(y2, m, d) );
		status = 4;
	}
	
	jQuery("#patientList .stage-object").each( function(){
		var id = this.id.split('_')[1];
		var dueDate = jQuery(this).attr('dueDate');
		var statusEvent = jQuery(this).attr('status');
		var programInstanceId = jQuery(this).attr('programInstanceId');
		if( dueDate >= startDate && dueDate <= endDate && statusEvent == status )
		{
			if( jQuery("#tb_" + programInstanceId + " .searched").length > 0 ){
				jQuery("#ps_" + id ).addClass("stage-object-selected searched");
				hideById("ps_" + id )
				hideById('arrow_' + id );
			}
			jQuery("#ps_" + id ).addClass("stage-object-selected searched");
		}
		else
		{
			hideById('arrow_' + id );
			hideById('ps_' + id );
		}
	});
	jQuery(".arrow-left").hide();
	jQuery(".arrow-right").hide();
}

function reloadOneRecord( programInstanceId )
{
	if(jQuery("#tb_" + programInstanceId + " .searched").length == 0 ){
		var total = eval(getInnerHTML('totalTd')) - 1;
		setInnerHTML('totalTd', total)
		hideById("event_" + programInstanceId );
	}
	else
	{
		var firstSearched = jQuery("#tb_" + programInstanceId + " .searched:first");
		firstSearched.show();
		showById('arrow_' + firstSearched.attr(programStageInstanceId) );
	}
}

