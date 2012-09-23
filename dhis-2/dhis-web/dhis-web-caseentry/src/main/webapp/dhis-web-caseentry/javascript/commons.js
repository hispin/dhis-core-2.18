var prefixId = 'ps_';
var COLOR_RED = "#fb4754";
var COLOR_GREEN = "#8ffe8f";
var COLOR_YELLOW = "#f9f95a";
var COLOR_LIGHTRED = "#fb6bfb";
var COLOR_GREY = "#bbbbbb";
var COLOR_LIGHT_RED = "#ff7676";
var COLOR_LIGHT_YELLOW = "#ffff99";
var COLOR_LIGHT_GREEN = "#ccffcc";
var COLOR_LIGHT_LIGHTRED = "#ff99ff";
var COLOR_LIGHT_GREY = "#ddd";
var MARKED_VISIT_COLOR = '#000000';
var SUCCESS_COLOR = '#ccffcc';
var ERROR_COLOR = '#ccccff';
var SAVING_COLOR = '#ffffcc';
var SUCCESS = 'success';
var ERROR = 'error';

// Disable caching for ajax requests in general 

$( document ).ready( function() {
	$.ajaxSetup ({
    	cache: false
	});
} );

function dobTypeOnChange( container ){

	var type = jQuery('#' + container + ' [id=dobType]').val();
	if(type == 'V' || type == 'D')
	{
		jQuery('#' + container + ' [id=age]').rules("remove");
		jQuery('#' + container + ' [id=age]').css("display","none");
		
		jQuery('#' + container + ' [id=birthDate]').rules("add",{required:true});
		datePickerValid( container + ' [id=birthDate]' );
		jQuery('#' + container + ' [id=birthDate]').css("display","");
	}
	else if(type == 'A')
	{
		jQuery('#' + container + ' [id=age]').rules("add",{required:true, number: true});
		jQuery('#' + container + ' [id=age]').css("display","");
		
		jQuery('#' + container + ' [id=birthDate]').rules("remove","required");
		$('#' + container+ ' [id=birthDate]').datepicker("destroy");
		jQuery('#' + container + ' [id=birthDate]').css("display","none");
	}
	else 
	{
		jQuery('#' + container + ' [id=age]').rules("remove");
		jQuery('#' + container + ' [id=age]').css("display","");
		
		jQuery('#' + container + ' [id=birthDate]').rules("remove","required");
		$('#' + container+ ' [id=birthDate]').datepicker("destroy");
		jQuery('#' + container + ' [id=birthDate]').css("display","none");
	}
}

// ----------------------------------------------------------------------------
// Search patients by name
// ----------------------------------------------------------------------------

function getPatientsByName( divname )
{	
	var fullName = jQuery('#' + divname + ' [id=fullName]').val().replace(/^\s+|\s+$/g,"");
	if( fullName.length > 0) 
	{
		contentDiv = 'resultSearchDiv';
		$('#resultSearchDiv' ).load("getPatientsByName.action",
			{
				fullName: fullName
			}).dialog({
				title: i18n_search_result,
				maximize: true, 
				closable: true,
				modal:true,
				overlay:{ background:'#000000', opacity: 0.8},
				width: 800,
				height: 400
		});
	}
	else
	{
		alert( i18n_no_patients_found );
	}
}

// -----------------------------------------------------------------------------
// Advanced search
// -----------------------------------------------------------------------------

function addAttributeOption()
{
	var rowId = 'advSearchBox' + jQuery('#advancedSearchTB select[name=searchObjectId]').length + 1;
	var contend  = '<td>' + getInnerHTML('searchingAttributeIdTD') + '</td>';
		contend += '<td>' + searchTextBox ;
		contend += '&nbsp;<input type="button" class="small-button" value="-" onclick="removeAttributeOption(' + "'" + rowId + "'" + ');"></td>';
		contend = '<tr id="' + rowId + '">' + contend + '</tr>';

	jQuery('#advancedSearchTB').append( contend );
}	

function removeAttributeOption( rowId )
{
	jQuery( '#' + rowId ).remove();
}	

//------------------------------------------------------------------------------
// Search patients by selected attribute
//------------------------------------------------------------------------------

function searchObjectOnChange( this_ )
{	
	var container = jQuery(this_).parent().parent().attr('id');
	var attributeId = jQuery('#' + container + ' [id=searchObjectId]').val(); 
	var element = jQuery('#' + container + ' [id=searchText]');
	var valueType = jQuery('#' + container+ ' [id=searchObjectId] option:selected').attr('valueType');
	
	jQuery('#searchText_' + container).removeAttr('readonly', false);
	jQuery('#searchText_' + container).val("");
	if( attributeId == 'fixedAttr_birthDate' )
	{
		element.replaceWith( getDateField( container ) );
		datePickerValid( 'searchText_' + container );
		return;
	}
	
	$( '#searchText_' + container ).datepicker("destroy");
	$('#' + container + ' [id=dateOperator]').replaceWith("");
	if( attributeId == 'prg' )
	{
		element.replaceWith( programComboBox );
	}
	else if ( attributeId=='fixedAttr_gender' )
	{
		element.replaceWith( getGenderSelector() );
	}
	else if ( attributeId=='fixedAttr_age' )
	{
		element.replaceWith( getAgeTextBox() );
	}
	else if ( valueType=='YES/NO' )
	{
		element.replaceWith( getTrueFalseBox() );
	}
	else
	{
		element.replaceWith( searchTextBox );
	}
}

function getTrueFalseBox()
{
	var trueFalseBox  = '<select id="searchText" name="searchText">';
	trueFalseBox += '<option value="true">' + i18n_yes + '</option>';
	trueFalseBox += '<option value="false">' + i18n_no + '</option>';
	trueFalseBox += '</select>';
	return trueFalseBox;
}
	
function getGenderSelector()
{
	var genderSelector = '<select id="searchText" name="searchText">';
		genderSelector += '<option value="M">' + i18n_male + '</option>';
		genderSelector += '<option value="F">' + i18n_female + '</option>';
		genderSelector += '<option value="T">' + i18n_transgender + '</option>';
		genderSelector += '</select>';
	return genderSelector;
}

function getAgeTextBox( container )
{
	var ageField = '<select id="dateOperator" style="width:40px;" name="dateOperator" ><option value="="> = </option><option value="<"> < </option><option value="<="> <= </option><option value=">"> > </option><option value=">="> >= </option></select>';
	ageField += '<input type="text" id="searchText_' + container + '" name="searchText" style="width:200px;">';
	return ageField;
}

function getDateField( container )
{
	var dateField = '<select id="dateOperator" name="dateOperator" style="width:30px"><option value=">"> > </option><option value=">="> >= </option><option value="="> = </option><option value="<"> < </option><option value="<="> <= </option></select>';
	dateField += '<input type="text" id="searchText_' + container + '" name="searchText" maxlength="30" style="width:18em" onkeyup="searchPatientsOnKeyUp( event );">';
	return dateField;
}

//-----------------------------------------------------------------------------
// Search Patient
//-----------------------------------------------------------------------------

function searchPatientsOnKeyUp( event )
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

function validateAdvancedSearch()
{
	hideById( 'listPatientDiv' );
	var flag = true;
	var dateOperator = '';
	
	if (getFieldValue('searchByProgramStage') == "false"){
		jQuery("#searchDiv :input").each( function( i, item )
		{
			var elementName = $(this).attr('name');
			if( elementName=='searchText' && jQuery( item ).val() == '')
			{
				showWarningMessage( i18n_specify_search_criteria );
				flag = false;
			}
		});
	}
	
	if(flag){
		contentDiv = 'listPatientDiv';
		jQuery( "#loaderDiv" ).show();
		advancedSearch( getSearchParams() );
	}
}

function getSearchParams()
{
	var params = "";
	var programIds = "";
	var programStageId = jQuery('#programStageAddPatient').val();
	if( getFieldValue('searchByProgramStage') == "true" && programStageId!=''){
		var statusEvent = jQuery('#programStageAddPatientTR [id=statusEvent]').val();
		var startDueDate = getFieldValue('startDueDate');
		var endDueDate = getFieldValue('endDueDate');
		params += '&searchTexts=stat_' + getFieldValue('programIdAddPatient') 
			   + '_' + startDueDate + '_' + endDueDate + '_' + statusEvent;
		if( statusEvent != '3' && statusEvent != '4' && statusEvent != '0' )
		{
			params += "_" + getFieldValue('orgunitId');
		}
	}
	
	var flag = false;
	jQuery( '#advancedSearchTB tr' ).each( function( i, row ){
		var dateOperator = "";
		var p = "";
		jQuery( this ).find(':input').each( function( idx, item ){
			if( idx == 0){
				p = "&searchTexts=" + item.value;
				if(item.value=='prg'){
					programIds += '&programIds=';
					flag = true;
				}
			}
			else if( item.name == 'dateOperator'){
				dateOperator = item.value;
			}
			else if( item.name == 'searchText'){
				if( item.value!='')
				{
					p += "_";
					if ( dateOperator.length >0 ) {
						p += dateOperator + "'" +  item.value.toLowerCase() + "'";
					}
					else{
						p += htmlEncode( item.value.toLowerCase().replace(/^\s*/, "").replace(/\s*$/, "") );
					}
					
					if( flag ){
						programIds += item.value;
						flag = false;
					}
				}
				else {
					p = "";
				}
			}
		})
		
		var searchInAllFacility = byId('searchInAllFacility').checked;
		if( getFieldValue('searchByProgramStage') == "true" 
			&& !searchInAllFacility){
			p += "_" + getFieldValue('orgunitId');
		}
		params += p;
	});
		
	params += '&listAll=false';
	if( getFieldValue('searchByProgramStage') == "false"){
		var searchInAllFacility = byId('searchInAllFacility').checked;
		params += '&searchBySelectedOrgunit=' + !searchInAllFacility;
	}
	else
	{
		params += '&searchBySelectedOrgunit=false';
	}
	params += programIds;
	
	return params;
}

// ----------------------------------------------------------------------------
// Show patients
// ----------------------------------------------------------------------------

function isDeathOnChange()
{
	var isDeath = byId('isDead').checked;
	if(isDeath)
	{
		showById('deathDateTR');
	}
	else
	{
		hideById('deathDateTR');
	}
}

// ----------------------------------------------------------------
// Get Params form Div
// ----------------------------------------------------------------

function getParamsForDiv( patientDiv)
{
	var params = '';
	var dateOperator = '';
	jQuery("#" + patientDiv + " :input").each(function()
		{
			var elementId = $(this).attr('id');
			
			if( $(this).attr('type') == 'checkbox' )
			{
				var checked = jQuery(this).attr('checked') ? true : false;
				params += elementId + "=" + checked + "&";
			}
			else if( elementId =='dateOperator' )
			{
				dateOperator = jQuery(this).val();
			}
			else if( $(this).attr('type') != 'button' )
			{
				var value = "";
				if( jQuery(this).val()!= null && jQuery(this).val() != '' )
				{
					value = htmlEncode(jQuery(this).val());
				}
				if( dateOperator != '' )
				{
					value = dateOperator + "'" + value + "'";
					dateOperator = "";
				}
				params += elementId + "="+ value + "&";
			}
		});
		
	return params;
}

// -----------------------------------------------------------------------------
// View patient details
// -----------------------------------------------------------------------------

function showPatientDetails( patientId )
{
    $('#detailsInfo').load("getPatientDetails.action", 
		{
			id:patientId
		}
		, function( ){
		}).dialog({
			title: i18n_patient_details,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 450,
			height: 300
		});
}

function showPatientHistory( patientId )
{
	$('#detailsInfo').load("getPatientHistory.action", 
		{
			patientId:patientId
		}
		, function( ){
			
		}).dialog({
			title: i18n_patient_details_and_history,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 520
		});
}

function exportPatientHistory( patientId, type )
{
	var url = "getPatientHistory.action?patientId=" + patientId + "&type=" + type;
	window.location.href = url;
}

function setEventColorStatus( programStageInstanceId, status )
{
	var boxElement = jQuery('#ps_' + programStageInstanceId );
	var dueDateElementId = 'value_' + programStageInstanceId + '_date';
	var status = eval(status);
	
	switch(status)
	{
		case 1:
			boxElement.css('border-color', COLOR_GREEN);
			boxElement.css('background-color', COLOR_LIGHT_GREEN);
			jQuery("#" + dueDateElementId ).datepicker("destroy");
			disable( dueDateElementId );
			return;
		case 2:
			boxElement.css('border-color', COLOR_LIGHTRED);
			boxElement.css('background-color', COLOR_LIGHT_LIGHTRED);
			datePicker( dueDateElementId );
			enable( dueDateElementId );
			return;
		case 3:
			boxElement.css('border-color', COLOR_YELLOW);
			boxElement.css('background-color', COLOR_LIGHT_YELLOW);
			datePicker( dueDateElementId );
			enable( dueDateElementId );
			return;
		case 4:
			boxElement.css('border-color', COLOR_RED);
			boxElement.css('background-color', COLOR_LIGHT_RED);
			datePicker( dueDateElementId );
			enable( dueDateElementId );
			return;
		case 5:
			boxElement.css('border-color', COLOR_GREY);
			boxElement.css('background-color', COLOR_LIGHT_GREY);
			disable( 'ps_' + programStageInstanceId );
			jQuery( "#" + dueDateElementId ).datepicker("destroy");
			disable( dueDateElementId );
			return;
		default:
		  return;
	}
}


// -----------------------------------------------------------------------------
// check duplicate patient
// -----------------------------------------------------------------------------

function checkDuplicate( divname )
{
	$.postUTF8( 'validatePatient.action', 
		{
			fullName: jQuery( '#' + divname + ' [id=fullName]' ).val(),
			dobType: jQuery( '#' + divname + ' [id=dobType]' ).val(),
			gender: jQuery( '#' + divname + ' [id=gender]' ).val(),
			birthDate: jQuery( '#' + divname + ' [id=birthDate]' ).val(),        
			age: jQuery( '#' + divname + ' [id=age]' ).val()
		}, function( xmlObject, divname )
		{
			checkDuplicateCompleted( xmlObject, divname );
		});
}

function checkDuplicateCompleted( messageElement, divname )
{
	checkedDuplicate = true;    
	var type = jQuery(messageElement).find('message').attr('type');
	var message = jQuery(messageElement).find('message').text();
    
    if( type == 'success')
    {
    	showSuccessMessage(i18n_no_duplicate_found);
    }
    if ( type == 'input' )
    {
        showWarningMessage(message);
    }
    else if( type == 'duplicate' )
    {
    	showListPatientDuplicate( messageElement, true );
    }
}

function enableBtn(){
	var programIdAddPatient = getFieldValue('programIdAddPatient');
	if( programIdAddPatient!='' ){
		enable('listPatientBtn');
		enable('addPatientBtn');
		enable('advancedSearchBtn');
		jQuery('#advanced-search :input').each( function( idx, item ){
			enable(this.id);
		});
	}
	else
	{
		disable('listPatientBtn');
		disable('addPatientBtn');
		disable('advancedSearchBtn');
		jQuery('#advanced-search :input').each( function( idx, item ){
			disable(this.id);
		});
	}
}

function enableRadioButton( programId )
{
	var prorgamStageId = jQuery('#programStageAddPatient').val();
	if( prorgamStageId== ''){
		jQuery('#programStageAddPatientTR [name=statusEvent]').attr("disabled", true);
	}
	else{
		jQuery('#programStageAddPatientTR [name=statusEvent]').removeAttr("disabled");
	}
}

function showColorHelp()
{
	jQuery('#colorHelpDiv').dialog({
		title: i18n_color_quick_help,
		maximize: true, 
		closable: true,
		modal:false,
		width: 380,
		height: 250
	}).show('fast');
}

// ----------------------------------------------------------------------------
// Move stage-flow
// ----------------------------------------------------------------------------

function moveLeft( programInstanceFlowDiv ){
	jQuery("#" + programInstanceFlowDiv).animate({scrollLeft: "-=200"}, 'fast');
}

function moveRight(programInstanceFlowDiv){
	jQuery("#" + programInstanceFlowDiv).animate({scrollLeft: "+=200"}, 'fast');
}

// ----------------------------------------------------------------------------
// Create New Event
// ----------------------------------------------------------------------------

function showCreateNewEvent( programInstanceId, programStageId )
{
	setInnerHTML('createEventMessage_' + programInstanceId, '');
	jQuery('#createNewEncounterDiv_' + programInstanceId ).dialog({
			title: i18n_create_new_event,
			maximize: true, 
			closable: true,
			modal:false,
			overlay:{background:'#000000', opacity:0.1},
			width: 450,
			height: 160
		}).show('fast');
		
	if( programStageId != undefined )
	{
		jQuery('#repeatableProgramStageId').val(programStageId);
	}
	setSuggestedDueDate( programInstanceId );
}

function setSuggestedDueDate( programInstanceId )
{
	var standardInterval =  jQuery('#repeatableProgramStageId option:selected').attr('standardInterval');
	var date = new Date();
	var d = date.getDate() + eval(standardInterval);
	var m = date.getMonth();
	var y = date.getFullYear();
	var edate= new Date(y, m, d);
	jQuery( '#dueDateNewEncounter_' + programInstanceId ).datepicker( "setDate" , edate );
}

function closeDueDateDiv( programInstanceId )
{
	jQuery('#createNewEncounterDiv_' + programInstanceId).dialog('close');
}

//------------------------------------------------------
// Register Irregular-encounter
//------------------------------------------------------

function registerIrregularEncounter( programInstanceId, programStageId, programStageName, dueDate )
{
	setInnerHTML('createEventMessage_' + programInstanceId, '');
	jQuery.postJSON( "registerIrregularEncounter.action",
		{ 
			programInstanceId:programInstanceId,
			programStageId: programStageId, 
			dueDate: dueDate 
		}, 
		function( json ) 
		{   
			var programStageInstanceId = json.message;
			disableCompletedButton(false);
			
			var elementId = prefixId + programStageInstanceId;
			var flag = false;
			var programType = jQuery('.stage-object-selected').attr('type');
			
			jQuery("#programStageIdTR_" + programInstanceId + " input[name='programStageBtn']").each(function(i,item){
				var element = jQuery(item);
				var dueDateInStage = element.attr('dueDate');
				if( dueDate < dueDateInStage && !flag)
				{	
					jQuery('<td>'
						+ '<span id="org_' + programStageInstanceId + '">' + getFieldValue('orgunitName') + '</span><br>'
						+ '<input name="programStageBtn" '
						+ 'id="' + elementId + '" ' 
						+ 'psid="' + programStageId + '" '
						+ 'programType="' + programType + '" '
						+ 'psname="' + programStageName + '" '
						+ 'dueDate="' + dueDate + '" '
						+ 'value="'+ programStageName + '&#13;&#10;' + dueDate + '" '
						+ 'onclick="javascript:loadDataEntry(' + programStageInstanceId + ')" '
						+ 'type="button" class="stage-object" '
						+ '></td>'
						+ '<td id="arrow_' + programStageInstanceId + '"><img src="images/rightarrow.png"></td>')
					.insertBefore(element.parent());
					flag = true;
				}
			});
			
			if( !flag )
			{
				jQuery("#programStageIdTR_" + programInstanceId).append('<td><img src="images/rightarrow.png"></td>'
					+ '<td>'
					+ '<span id="org_' + programStageInstanceId + '">' + getFieldValue('orgunitName') + '</span><br>'
					+ '<input name="programStageBtn" '
					+ 'id="' + elementId + '" ' 
					+ 'psid="' + programStageId + '" '
					+ 'programType="' + programType + '" '
					+ 'psname="' + programStageName + '" '
					+ 'dueDate="' + dueDate + '" '
					+ 'value="'+ programStageName + '&#13;&#10;' + dueDate + '" '
					+ 'onclick="javascript:loadDataEntry(' + programStageInstanceId + ')" '
					+ 'type="button" class="stage-object" '
					+ '></td>');
			}
			if( jQuery('#tb_' + programInstanceId + " :input" ).length > 4 ){
				jQuery('#tb_' + programInstanceId + ' .arrow-left').removeClass("hidden");
				jQuery('#tb_' + programInstanceId + ' .arrow-right').removeClass("hidden");
			}
			
			if( dueDate < getCurrentDate() ){
				setEventColorStatus( programStageInstanceId, 4 );
			}
			else{
				setEventColorStatus( programStageInstanceId, 3 );
			}
			
			jQuery('#ps_' + programStageInstanceId ).focus();
			jQuery('#createNewEncounterDiv_' + programInstanceId).dialog("close");
			resetActiveEvent( programStageInstanceId );
			loadDataEntry( programStageInstanceId );
			showSuccessMessage(i18n_create_event_success);
		});
}

function disableCompletedButton( disabled )
{
	if(disabled){
		disable('completeBtn');
		disable('completeAndAddNewBtn');
		enable('uncompleteBtn');
		enable('uncompleteAndAddNewBtn');
	}
	else{
		enable('completeBtn');
		enable('completeAndAddNewBtn');
		disable('uncompleteBtn');
		disable('uncompleteAndAddNewBtn');
	}
}

// load program instance history
function programReports( programInstanceId )
{
	$('#programReportDiv').load("getProgramReportHistory.action", {programInstanceId:programInstanceId});
}

// load SMS message and comments
function getEventMessages( programInstanceId )
{
	$('#eventMessagesDiv').load("getEventMessages.action", {programInstanceId:programInstanceId});
}

// ----------------------------------------------------------------
// Dash board
// ----------------------------------------------------------------

function activeProgramInstanceDiv( programInstanceId )
{
	jQuery(".selected").each(function(){
		jQuery(this).removeClass();
	});
	
	jQuery("#infor_" + programInstanceId).each(function(){
		jQuery(this).addClass('selected bold');
	});
	
	showById('pi_' + programInstanceId);
}

function hideProgramInstanceDiv( programInstanceId )
{
	hideById('pi_' + programInstanceId);
	jQuery('#pi_' + programInstanceId).removeClass("link-area-active");
	jQuery("#img_" + programInstanceId).attr('src','');
}

function showPatientDashboardForm( patientId )
{
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('listPatientDiv');
	hideById('editPatientDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationPatientDiv');
				
	jQuery('#loaderDiv').show();
	jQuery('#patientDashboard').load('patientDashboard.action',
		{
			patientId:patientId
		}, function()
		{	
			showById('patientDashboard');
			jQuery('#loaderDiv').hide();
		});
}

function loadActiveProgramStageRecords(programInstanceId, activeProgramStageInstanceId)
{
	jQuery('#loaderDiv').show();
	jQuery('#programEnrollmentDiv').load('enrollmentform.action',
		{
			programInstanceId:programInstanceId
		}, function()
		{
			showById('programEnrollmentDiv');
			var type = jQuery('#tr_'+programInstanceId).attr('programType');
			if(type=='2'){
				hideById('programInstanceDiv');
				var programStageInstanceId = jQuery('#tr_'+programInstanceId).attr('programStageInstanceId');
				loadDataEntry( programStageInstanceId );
			}
			else{
				showById('programInstanceDiv');
			}
			activeProgramInstanceDiv( programInstanceId );
			if( activeProgramStageInstanceId )
			{
				loadDataEntry( activeProgramStageInstanceId );
			}
			jQuery('#loaderDiv').hide();
		});
}

function loadProgramStageRecords( programStageInstanceId, completed ) 
{
	showLoader();
    jQuery('#dataEntryFormDiv').load( "viewProgramStageRecords.action",
		{
			programStageInstanceId: programStageInstanceId
		}, function() {
			if(completed){
				jQuery( "#dataEntryFormDiv :input").each(function(){
					disable(this.id);
				});
			}
			showById('dataEntryFormDiv');
			hideLoader();
		});
}

function updateEnrollment( patientId, programId, programInstanceId, programName )
{
	var dateOfIncident = jQuery('#tab-3 [id=dateOfIncident]').val();
	var enrollmentDate = jQuery('#tab-3 [id=enrollmentDate]').val();
	
	jQuery.postJSON( "saveProgramEnrollment.action",
		{
			patientId: getFieldValue('patientId'),
			programId: programId,
			dateOfIncident: dateOfIncident,
			enrollmentDate: enrollmentDate
		}, 
		function( json ) 
		{    
			var infor = programName + " (" + enrollmentDate + ")";
			setInnerHTML("infor_" + programInstanceId, infor );
			showSuccessMessage(i18n_enrol_success);
		});
}

//-----------------------------------------------------------------------------
// Save due-date
//-----------------------------------------------------------------------------

function saveDueDate( programInstanceId, programStageInstanceId, programStageInstanceName )
{
	var field = document.getElementById( 'value_' + programStageInstanceId + '_date' );
	var dateOfIncident = new Date( byId('dateOfIncident').value );
	var dueDate = new Date(field.value);
	
	if( dueDate < dateOfIncident )
	{
		field.style.backgroundColor = '#FFCC00';
		alert( i18n_date_less_incident );
		return;
	}
	
	field.style.backgroundColor = '#ffffcc';
	
	var dateDueSaver = new DateDueSaver( programStageInstanceId, field.value, '#ccffcc' );
	dateDueSaver.save();
}

function DateDueSaver( programStageInstanceId_, dueDate_, resultColor_ )
{
	var programStageInstanceId = programStageInstanceId_;	
	var dueDate = dueDate_;
	var resultColor = resultColor_;	

	this.save = function()
	{
		var params = 'programStageInstanceId=' + programStageInstanceId + '&dueDate=' + dueDate;
		$.ajax({
			   type: "POST",
			   url: "saveDueDate.action",
			   data: params,
			   dataType: "xml",
			   success: function(result){
					handleResponse (result);
			   },
			   error: function(request,status,errorThrown) {
					handleHttpError (request);
			   }
			});
	};

	function handleResponse( rootElement )
	{
		var codeElement = rootElement.getElementsByTagName( 'code' )[0];
		var code = parseInt( codeElement.firstChild.nodeValue );
   
		if ( code == 0 )
		{
			var box = jQuery('#ps_' + programStageInstanceId );
			box.attr('dueDate', dueDate );
			var boxName = box.attr('psname') + getInnerHTML('enterKey') + dueDate;
			box.val( boxName );
			if( dueDate < getCurrentDate() )
			{
				box.css('border-color', COLOR_RED);
				box.css('background-color', COLOR_LIGHT_RED);
				jQuery('#stat_' + programStageInstanceId + " option[value=3]").remove();
				jQuery('#stat_' + programStageInstanceId ).prepend("<option value='4' selected>" + i18n_overdue + "</option>");
			}
			else
			{
				box.css('border-color', COLOR_YELLOW);
				box.css('background-color', COLOR_LIGHT_YELLOW);
				jQuery('#stat_' + programStageInstanceId + " option[value=4]").remove();
				jQuery('#stat_' + programStageInstanceId ).prepend("<option value='3' selected>" + i18n_scheduled_in_future + "</option>");
			}
			markValue( resultColor );                   
		}
		else
		{
			markValue( COLOR_GREY );
			window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
		}
	}

	function handleHttpError( errorCode )
	{
		markValue( COLOR_GREY );
		window.alert( i18n_saving_value_failed_error_code + '\n\n' + errorCode );
	}   

	function markValue( color )
	{       
   
		var element = document.getElementById( 'value_' + programStageInstanceId + '_date' );	
           
		element.style.backgroundColor = color;
	}
}

function resize(){
	var width = 400;
	var w = $(window).width(); 
	if(jQuery(".patient-object").length > 1 ){
		width += 150;
	}
	if(jQuery(".show-new-event").length > 0 ){
		width += 150;
	}
	
	$('.stage-flow').each(function(){
		var programInstanceId = this.id.split('_')[1];
		if(jQuery(this).find(".table-flow").outerWidth() > jQuery(this).width() ){
			jQuery('#tb_' + programInstanceId ).find('.arrow-left').removeClass("hidden");
			jQuery('#tb_' + programInstanceId ).find('.arrow-right').removeClass("hidden");
		}
		else{
			jQuery('#tb_' + programInstanceId ).find('.arrow-left').addClass("hidden");
			jQuery('#tb_' + programInstanceId ).find('.arrow-right').addClass("hidden");
		}
	});
	
	$('.stage-flow').css('width', w-width); 
	$('.table-flow').css('width', w-width); 
	$('.table-flow tr').each(function(){
		jQuery(this).find('td:visible').attr("width", "10px");
		jQuery(this).find('td:hidden').removeAttr("width");
		jQuery(this).find('td:last').removeAttr("width");
	});
}

function setEventStatus( field, programStageInstanceId )
{	
	var status = field.value;
	field.style.backgroundColor = SAVING_COLOR;
	jQuery.postUTF8( 'setEventStatus.action',
		{
			programStageInstanceId:programStageInstanceId,
			status:field.value
		}, function ( json )
		{
			jQuery('#ps_' + programStageInstanceId).attr('status',field.value);
			setEventColorStatus( programStageInstanceId, field.value );
			resetActiveEvent( programStageInstanceId );
			if( status==1){
				hideById('del_' + programStageInstanceId);
			}
			else{
				showById('del_' + programStageInstanceId);
				if( status==5){
					var id = 'ps_' + programStageInstanceId;
					if( jQuery(".stage-object-selected").attr('id')==id )
					{
						hideById('entryForm');
						hideById('executionDateTB');
						hideById('inputCriteriaDiv');
					}
				}
			}
			field.style.backgroundColor = SUCCESS_COLOR;
		} );
}

function resetActiveEvent( programStageInstanceId )
{
	var activeProgramInstance = jQuery(".selected");
	if( activeProgramInstance.length > 0 )
	{
		var programInstanceId = activeProgramInstance.attr('id').split('_')[1];
		var hasActiveEvent = false;
		jQuery(".stage-object").each(function(){
			var status = jQuery(this).attr('status');
			if(status !=1 && status != 5 && !hasActiveEvent){
				var value = jQuery(this).val();
				var programStageInstanceId = jQuery(this).attr('id').split('_')[1];
				
				jQuery('#td_' + programInstanceId).attr("onClick", "javascript:loadActiveProgramStageRecords("+ programInstanceId +", "+ programStageInstanceId  +")")
				jQuery('#tr2_' + programInstanceId).html("<a>>>" + value + "</a>");
				jQuery('#tr2_' + programInstanceId).attr("onClick", "javascript:loadActiveProgramStageRecords("+ programInstanceId +", "+ programStageInstanceId + ")");
				
				var id = 'ps_' + programStageInstanceId;
				enable('ps_' + programStageInstanceId );
				if( jQuery(".stage-object-selected").attr('id')!=jQuery(this).attr('id') )
				{
					hideById('entryForm');
					hideById('executionDateTB');
					hideById('inputCriteriaDiv');
				}
				hasActiveEvent = true;
			}
		});
		
		if( !hasActiveEvent ){
			jQuery('#td_' + programInstanceId).attr("onClick", "javascript:loadActiveProgramStageRecords("+ programInstanceId +", false)")
			jQuery('#tr2_' + programInstanceId).html("");
			jQuery('#tr2_' + programInstanceId).attr("onClick", "");
			
			hideById('entryForm');
			hideById('executionDateTB');
			hideById('inputCriteriaDiv');
		}
	}	
}

function removeEvent( programStageInstanceId, isEvent )
{	
    var result = window.confirm( i18n_comfirm_delete_event );
    
    if ( result )
    {
    	$.postJSON(
    	    "removeCurrentEncounter.action",
    	    {
    	        "id": programStageInstanceId   
    	    },
    	    function( json )
    	    { 
    	    	if ( json.response == "success" )
    	    	{
					jQuery( "tr#tr" + programStageInstanceId ).remove();
	                
					jQuery( "table.listTable tbody tr" ).removeClass( "listRow listAlternateRow" );
	                jQuery( "table.listTable tbody tr:odd" ).addClass( "listAlternateRow" );
	                jQuery( "table.listTable tbody tr:even" ).addClass( "listRow" );
					jQuery( "table.listTable tbody" ).trigger("update");
					
					hideById('smsManagementDiv');
					if(isEvent)
					{
						showById('searchDiv');
						showById('listPatientDiv');
					}
					var id = 'ps_'+programStageInstanceId;
					var programInstanceId = jQuery('#' + id).attr('programInstanceId');
					if(jQuery(".stage-object-selected").attr('id')== id)
					{
						hideById('entryForm');
						hideById('executionDateTB');
						hideById('inputCriteriaDiv');
					}
					jQuery('#ps_' + programStageInstanceId).remove();
					jQuery('#arrow_' + programStageInstanceId).remove();
					jQuery('#org_' + programStageInstanceId).remove();
					
					reloadOneRecord( programInstanceId );
					showSuccessMessage( i18n_delete_success );
					resize();
    	    	}
    	    	else if ( json.response == "error" )
    	    	{ 
					showWarningMessage( json.message );
    	    	}
    	    }
    	);
    }
}
