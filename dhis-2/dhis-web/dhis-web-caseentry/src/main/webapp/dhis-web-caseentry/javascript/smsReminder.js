
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
	
	var date = new Date();
	var d = date.getDate() - 1;
	var m = date.getMonth();
	var y1 = date.getFullYear() - 100;
	var y2 = date.getFullYear();
	var startDate = jQuery.datepicker.formatDate( dateFormat, new Date(y1, m, d) );
	var endDate = jQuery.datepicker.formatDate( dateFormat, new Date(y2, m, d) );
	
	var programId = getFieldValue('programIdAddPatient');
	var searchTexts = "stat_" + programId + "_4_" + startDate + "_" + endDate 
					+ "_" + getFieldValue('orgunitId');
	
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
			showById('colorHelpLink');
			showById('listPatientDiv');
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
				var programStageName = jQuery("#box_" + programStageInstanceId).attr('programStageName');
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
			jQuery('#td_' + programStageInstance ).toggle();
			jQuery(this).removeClass("stage-object-selected");
		});
	
	jQuery("#tb_" + programInstanceId + " .arrow-left").toggle();
	jQuery("#tb_" + programInstanceId + " .arrow-right").toggle();
	if( jQuery("#tb_" + programInstanceId + " .searched").length>0)
	{	
		var id = jQuery("#tb_" + programInstanceId + " .searched").attr('id').split('_')[1];
		showById("arrow_" + id);
		showById("td_" + id );
	}
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

function setEventStatus( field, programStageInstanceId )
{	
	field.style.backgroundColor = SAVING_COLOR;
	jQuery.postUTF8( 'setEventStatus.action',
		{
			programStageInstanceId:programStageInstanceId,
			status:field.value
		}, function ( json )
		{
			field.style.backgroundColor = SUCCESS_COLOR;
		} );
}

function removeEvent( programStageId )
{	
    var result = window.confirm( i18n_comfirm_delete_event );
    
    if ( result )
    {
    	$.postJSON(
    	    "removeCurrentEncounter.action",
    	    {
    	        "id": programStageId   
    	    },
    	    function( json )
    	    { 
    	    	if ( json.response == "success" )
    	    	{
					jQuery( "tr#tr" + programStageId ).remove();
	                
					jQuery( "table.listTable tbody tr" ).removeClass( "listRow listAlternateRow" );
	                jQuery( "table.listTable tbody tr:odd" ).addClass( "listAlternateRow" );
	                jQuery( "table.listTable tbody tr:even" ).addClass( "listRow" );
					jQuery( "table.listTable tbody" ).trigger("update");
					
					hideById('smsManagementDiv');
					
					showSuccessMessage( i18n_delete_success );
    	    	}
    	    	else if ( json.response == "error" )
    	    	{ 
					showWarningMessage( json.message );
    	    	}
    	    }
    	);
    }
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
