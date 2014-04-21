
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
			hideById('listEntityInstanceDiv'); 
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
	showById('listEntityInstanceDiv');
	showById('mainLinkLbl');
	jQuery('#createNewEncounterDiv').dialog('close');
	jQuery('#resultSearchDiv').dialog('close');
}

//--------------------------------------------------------------------------------------------
// Show all entityInstances in select orgunit
//--------------------------------------------------------------------------------------------

isAjax = true;
function listAllTrackedEntityInstance(page)
{
	hideById('advanced-search');
	showLoader();
	
	jQuery('#loaderDiv').show();
	listEntityInstanceDiv = 'listEntityInstanceDiv';
	var params = "page=" + page;
	if (getFieldValue('program') != '') {
		params += "&program=" + getFieldValue('program');
	}
	
	$('#attributeIds option').each(function(i, item){
		params += "&attribute=" + item.value;
	});
	
	$.ajax({
		type : "GET",
		url : "../api/trackedEntityInstances.json?ou="
				+ getFieldValue("orgunitId"),
		data : params,
		dataType : "json",
		success : function(json) {
			setInnerHTML('listEntityInstanceDiv', displayTEIList(json, page));
			showById('listEntityInstanceDiv');
			jQuery('#loaderDiv').hide();
			setTableStyles();
		}
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
		validateAdvancedSearch(1);
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
			hideById('listEntityInstanceDiv');
			hideById('listEntityInstanceDiv');
			hideById('mainLinkLbl');
			setInnerHTML('singleProgramName',jQuery('#program option:selected').text());
			loadProgramStages( entityInstanceId, programId );
		});
}

function advancedSearch( params, page )
{
	$.ajax({
		type : "GET",
		url : "../api/trackedEntityInstances.json",
		data : params,
		dataType : "json",
		success : function(json) {
			setInnerHTML('listEntityInstanceDiv', displayTEIList(json, page));
			showById('listEntityInstanceDiv');
			jQuery('#loaderDiv').hide();
			setTableStyles();
		}
	});
}

function displayTEIList(json, page) {

	// Header
	var title = "";
	
	if (getFieldValue('programIdAddTrackedEntity') != "") {
		var status = jQuery('#statusEnrollment option:selected').text();
		var programName = jQuery('#programIdAddTrackedEntity option:selected')
				.text();
		title = i18n_for + " " + status + " " + i18n_enrollments_in + " "
				+ programName + " " + i18n_program;
		setInnerHTML('enrollmentInfor', title);
	}
	var table = "<p>" + i18n_the_following_tracked_entity_instances_found_in
			+ " " + getFieldValue('orgunitName') + " " + title + "</p>";
	if (json.metaData.pager.total > 0) {
		table += "<p>" + i18n_total_result + " : " + json.metaData.pager.total
				+ "</p>";
	} else {
		table += "<p>" + i18n_no_result_found + "</p>";
	}
	
	// TEI list
	table += "<table class='listTable' width='100%'>";
	
	var idx = 4;
	if(getFieldValue('ouMode') != 'SELECTED'){
		var idx = 3;
	}	
	else if(getFieldValue('program') != '') {
		idx = 5;
	}
	table += "<col width='30' />";
	for (var i = idx; i < json.width; i++) {
		table += "<col />";
	}
	table += "<col width='200' />";
	table += "<thead><tr><th>#</th>";
	for (var i = idx; i < json.width; i++) {
		table += "<th>" + json.headers[i].column + "</th>";
	}
	table += "<th>" + i18n_operations + "</th>";
	table += "</tr></thead>";
	
	table += "<tbody id='list'>";
	for ( var i in json.rows) {
		var cols = json.rows[i];
		var uid = cols[0];
		var no = eval(json.metaData.pager.page);
		no = (no - 1) * 50 + eval(i) + 1;
		table += "<tr id='tr" + uid + "'>";
		table += "<td>" + no + "</td>";
		for (var j = idx; j < json.width; j++) {
			var colVal = cols[j];
			if (j == 4) {
				colVal = json.metaData.names[colVal];
			}
			table += "<td onclick=\"javascript:isDashboard=true;showTrackedEntityInstanceDashboardForm( '"
				+ uid
				+ "' )\" title='"
				+ i18n_dashboard
				+ "'>" + colVal + "</td>";
		}
		
		// Operations column
		table += "<td>";
		table += "<a href=\"javascript:isDashboard=true;showTrackedEntityInstanceDashboardForm( '"
				+ uid
				+ "' )\" title='"
				+ i18n_dashboard
				+ "'><img src='../images/enroll.png' alt='"
				+ i18n_dashboard
				+ "'></a>";
		table += "<a href=\"javascript:isDashboard=false;showUpdateTrackedEntityInstanceForm( '"
				+ uid
				+ "' )\" title='"
				+ i18n_data_entry
				+ "'><img src= '../images/edit.png' alt='"
				+ i18n_data_entry
				+ "'></a>";
		table += "<a href=\"javascript:setFieldValue( 'isShowEntityInstanceList', 'false' ); showRelationshipList('"
				+ uid
				+ "' )\" title='"
				+ i18n_manage_relationship
				+ "'><img src='../images/relationship.png' alt='"
				+ i18n_manage_relationship + "'></a>";
		if (canChangeLocation) {
			table += "<a href=\"javascript:isDashboard=false;getTrackedEntityInstanceLocation( '"
					+ uid
					+ "' );\" title='"
					+ i18n_change_location
					+ "'><img src='../icons/dataentry.png' alt='"
					+ i18n_change_location
					+ "' style='width:25px; height:25px'></a>";
		}
		table += "<a href=\"javascript:removeTrackedEntityInstance( '" + uid
				+ "', '', '" + i18n_confirm_delete_tracked_entity_instance
				+ "' )\" title='" + i18n_remove
				+ "'><img src='../images/delete.png' alt='" + i18n_remove
				+ "'></a>";
		table += "<a href=\"javascript:showTrackedEntityInstanceHistory( '"
				+ uid + "')\" title='"
				+ i18n_tracked_entity_instance_details_and_history
				+ "'><img src='../images/information.png' alt='"
				+ i18n_tracked_entity_instance_details_and_history + "'></a>";
		table += "</td>";
		table += "</tr>";
	}
	table += "</tbody>";
	table += "</table>";
	
	return table + paging(json, page);
}

// Paging

function paging(json, page) {
	var searchMethod = "listAllTrackedEntityInstance";
	if( isAdvancedSearch ){
		searchMethod = "validateAdvancedSearch";
	}
	
	var table = "<table width='100%' style='background-color: #ebf0f6;'><tr><td colspan='"
			+ json.width + "'>";
	table += "<div class='paging'>";
	table += "<span class='first' title='" + i18n_first + "'>««</span>";
	table += "<span class='prev' title='" + i18n_prev + "'>«</span>";
	for (var i = 1; i <= json.metaData.pager.pageCount; i++) {
		if (i == page) {
			table += "<span class='page' title='" + i18n_page + " " + i + "'>"
					+ i + "</span>";
		} else {
			table += "<a class='page' title='" + i18n_page + " " + i
					+ "' href='javascript:" + searchMethod + "( " + i
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
