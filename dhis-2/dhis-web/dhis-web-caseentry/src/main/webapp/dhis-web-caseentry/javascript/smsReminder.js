isAjax = true;
var generateResultParams = "";

function orgunitSelected( orgUnits, orgUnitNames )
{
	var width = jQuery('#program').width();
	jQuery('#program').width(width-30);
	showById( "programLoader" );
	disable('program');
	disable('listEntityInstanceBtn');
	showById('mainLinkLbl');
	showById('searchDiv');
	hideById('listEventDiv');
	hideById('listEventDiv');
	hideById('entityInstanceDashboard');
	hideById('smsManagementDiv');
	hideById('sendSmsFormDiv');
	hideById('editEntityInstanceDiv');
	hideById('resultSearchDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationEntityInstanceDiv');

	clearListById('program');
	$('#contentDataRecord').html('');
	setFieldValue('orgunitName', orgUnitNames[0]);
	setFieldValue('orgunitId', orgUnits[0]);
	jQuery.get("getPrograms.action",{}, 
		function(json)
		{
			var count = 0;
			for ( i in json.programs ) {
				if(json.programs[i].type==1){
					count++;
					jQuery( '#program').append( '<option value="' + json.programs[i].id +'" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
				}
			}
			if(count==0){
				jQuery( '#program').prepend( '<option value="" selected>' + i18n_none_program + '</option>' );
			}
			else if(count>1){
				jQuery( '#program').prepend( '<option value="" selected>' + i18n_please_select + '</option>' );
				enable('listEntityInstanceBtn');
			}
			
			enableBtn();
			hideById('programLoader');
			jQuery('#program').width(width);
			enable('program');
		});
}

selection.setListenerFunction( orgunitSelected );

// --------------------------------------------------------------------
// List all events
// --------------------------------------------------------------------

function listAllTrackedEntityInstance( page )
{
	hideById('listEventDiv');
	hideById('advanced-search');
	contentDiv = 'listEventDiv';
	$('#contentDataRecord').html('');
	hideById('advanced-search');
	
	var params = "orgUnit=" + getFieldValue("orgunitId");
	params += "&ouMode=SELECTED";
	params += "&program=" + getFieldValue('program');
	if( $('#followup').attr('checked')=='checked'){
		params += "followUp=true";
	}
	$.ajax({
		type : "GET",
		url : "../api/events.json",
		data : params,
		dataType : "json",
		success : function(json) {
			setInnerHTML('listEventDiv', displayEventList(json, page));
			showById('listEventDiv');
			jQuery('#loaderDiv').hide();
			setTableStyles();
		}
	});
	
}


function displayEventList(json, page) {

	var table = "";
	
	// Header
	if (json.pager.total > 0) {
		table += "<p>" + i18n_total_result + " : " + json.pager.total
				+ "</p>";
	} else {
		table += "<p>" + i18n_no_result_found + "</p>";
	}
	
	if( json.pager.total > 0 ){
		// Event list
		table += "<table class='listTable' width='100%'>";
		
		table += "<col width='30' />";
		table += "<col />"; // Ordered no.
		table += "<col />"; // Event-date
		for(var i in json.events[0].dataValues.length ){
			table += "<col />";
		}
		table += "<col width='200' />";
		
		table += "<thead><tr><th>#</th>";
		table += "<th>" + i18n_event_date + "</th>";
		for(var i in json.events[0].dataValues ){
			table += "<th>" + json.events[0].dataValues[i].dataElement + "</th>";
		}
		table += "<th>" + i18n_operations + "</th>";
		table += "</tr></thead>";
		
		table += "<tbody id='list'>";
		for ( var i in json.events) {
			var cols = json.events[i];
			var uid = cols.event;
			var teiUid = cols.trackedEntityInstance;
			var no = eval(json.pager.page);
			no = (no - 1) * json.pager.pageSize + eval(i) + 1;
			table += "<tr id='tr" + uid + "'>";
			table += "<td>" + no + "</td>";
			table += "<td>" + json.events[i].eventDate + "</td>";
			for (var j in cols.dataValues) {
				table += "<td>" +  cols.dataValues[j].value + "</td>";
			}
			
			// Operations column
			table += "<td>";
			table += "<a href=\"javascript:isDashboard=false;showTrackedEntityInstanceDashboardForm( '"
					+ teiUid
					+ "' )\" title='"
					+ i18n_dashboard
					+ "'><img src='../images/enroll.png' alt='"
					+ i18n_dashboard
					+ "'></a>";
			table += "<a href=\"javascript:programTrackingList( '" + uid + "', false ) \" "
					+ " title='"
					+ i18n_edit
					+ "'><img src= '../images/edit.png' alt='"
					+ i18n_edit
					+ "'></a>";
			table += "</td>";
			table += "</tr>";
		}
		table += "</tbody>";
		table += "</table>";
	
		table += paging(json, page);
	}
	return table;
}

// Paging

function paging(json, page) {
	var table = "<table width='100%' style='background-color: #ebf0f6;'><tr><td colspan='"
			+ json.width + "'>";
	table += "<div class='paging'>";
	table += "<span class='first' title='" + i18n_first + "'>««</span>";
	table += "<span class='prev' title='" + i18n_prev + "'>«</span>";
	for (var i = 1; i <= json.pager.pageCount; i++) {
		if (i == page) {
			table += "<span class='page' title='" + i18n_page + " " + i + "'>"
					+ i + "</span>";
		} else {
			table += "<a class='page' title='" + i18n_page + " " + i
					+ "' href='javascript:listAllTrackedEntityInstance( " + i
					+ ");'>" + i + "</a>";
		}
		table += "<span class='seperator'>|</span>";
	}
	table += "<span class='next' title='" + i18n_next + "'>» </span>";
	table += "<span class='last' title='" + i18n_last + "'>»»</span>";
	table += "</div>";
	table += "</tr></table>";
	return table;
}

// --------------------------------------------------------------------
// Search events
// --------------------------------------------------------------------

followup = true;

function advancedSearch( params, page )
{
	setFieldValue('listAll', "false");
	$('#contentDataRecord').html('');
	$('#listEventDiv').html('');
	hideById('listEventDiv');
	showLoader();
	params += "&orgUnit=" + getFieldValue("orgunitId");
	$.ajax({
		url : '../api/events.json',
		type : "GET",
		data : params,
		success : function(json) {
			setInnerHTML('listEventDiv', displayEventList(json, page));
			showById('listEventDiv');
			jQuery('#loaderDiv').hide();
			setTableStyles();
		}
	});
	
}

function exportXlsFile()
{
	var url = "getActivityPlanRecords.action?type=xls&trackingReport=true&" + generateResultParams;
	window.location.href = url;
}

// --------------------------------------------------------------------
// program tracking form
// --------------------------------------------------------------------

function programTrackingList( programStageInstanceId, isSendSMS ) 
{
	hideById('listEventDiv');
	hideById('searchDiv');
	showLoader();
	setFieldValue('sendToList', "false");
	$('#smsManagementDiv' ).load("programTrackingList.action",
		{
			programStageInstanceId: programStageInstanceId
		}
		, function(){
			hideById('mainLinkLbl');
			hideById('mainFormLink');
			hideById('searchDiv');
			hideById('listEventDiv');
			showById('smsManagementDiv');
			hideLoader();
		});
}

// --------------------------------------------------------------------
// Send SMS 
// --------------------------------------------------------------------

function showSendSmsForm()
{
	jQuery('#sendSmsToListForm').dialog({
			title: i18n_send_message,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 420,
			height: 200
		});
}

function sendSmsToList()
{
	params = getSearchParams();
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

// --------------------------------------------------------------------
// Post Comments/Send Message
// --------------------------------------------------------------------

function keypressOnMessage(event, field, programStageInstanceId )
{
	var key = getKeyCode( event );
	if ( key==13 ){ // Enter
		sendSmsOneTrackedEntityInstance( field, programStageInstanceId );
	}
}

// --------------------------------------------------------------------
// Dashboard
// --------------------------------------------------------------------

function loadDataEntry( programStageInstanceId )
{
	setInnerHTML('dataEntryFormDiv', '');
	showById('dataEntryFormDiv');
	showById('executionDateTB');
	setFieldValue( 'dueDate', '' );
	setFieldValue( 'executionDate', '' );
	disable('validationBtn');
	disableCompletedButton(true);
	disable('uncompleteBtn');
	jQuery( 'input[id=programStageInstanceId]').val( programStageInstanceId );
	
	$('#executionDate').unbind("change");
	$('#executionDate').change(function() {
		saveExecutionDate( getFieldValue('programId'), programStageInstanceId, byId('executionDate') );
	});
	
	jQuery(".stage-object-selected").removeClass('stage-object-selected');
	var selectedProgramStageInstance = jQuery( '#' + prefixId + programStageInstanceId );
	selectedProgramStageInstance.addClass('stage-object-selected');
	setFieldValue( 'programStageId', selectedProgramStageInstance.attr('psid') );
	
	showLoader();	
	$( '#dataEntryFormDiv' ).load( "dataentryform.action", 
		{ 
			programStageInstanceId: programStageInstanceId
		},function()
		{
			setFieldValue( 'programStageInstanceId', programStageInstanceId );
			var executionDate = jQuery('#executionDate').val();
			var completed = jQuery('#entryFormContainer input[id=completed]').val();
			var irregular = jQuery('#entryFormContainer input[id=irregular]').val();
			var reportDateDes = jQuery("#ps_" + programStageInstanceId).attr("reportDateDes");
			setInnerHTML('reportDateDescriptionField',reportDateDes);
			enable('validationBtn');
			if( executionDate == '' )
			{
				disable('validationBtn');
			}
			else if( executionDate != '' && completed == 'false' )
			{
				disableCompletedButton(false);
			}
			else if( completed == 'true' )
			{
				disableCompletedButton(true);
			}
			resize();
			hideLoader();
			hideById('contentDiv'); 
			jQuery('#dueDate').focus();
		});
}

function entryFormContainerOnReady(){}

// --------------------------------------------------------------------
// Show main form
// --------------------------------------------------------------------

function onClickBackBtn()
{
	showById('mainLinkLbl');
	showById('searchDiv');
	showById('listEventDiv');
	hideById('migrationEntityInstanceDiv');
	hideById('smsManagementDiv');
	hideById('entityInstanceDashboard');
	
	if( eventList == 1){
		listAllTrackedEntityInstance();
	}
	else if( eventList == 2){
		validateAdvancedSearch();
	}
}

// load program instance history
function programTrackingReport( programInstanceId )
{
	$('#programTrackingReportDiv').load("getProgramReportHistory.action", 
		{
			programInstanceId:programInstanceId
		}).dialog(
		{
			title:i18n_program_report,
			maximize:true, 
			closable:true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width:850,
			height:500
		});
}

function getProgramStageInstanceById(programStageInstanceId)
{
	$('#tab-2').load("getProgramStageInstanceById.action", 
	{
		programStageInstanceId:programStageInstanceId
	});
}
