
function addEventForPatientForm( divname )
{
	jQuery("#" + divname + " [id=checkDuplicateBtn]").click(function() {
		checkDuplicate( divname );
	});
	
	jQuery("#" + divname + " [id=dobType]").change(function() {
		dobTypeOnChange( divname );
	});
}

// -----------------------------------------------------------------------------
// Show relationship with new patient
// -----------------------------------------------------------------------------

function showRelationshipList( patientId )
{
	hideById('addRelationshipDiv');
	hideById('patientDashboard');
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('listPatientDiv');

	jQuery('#loaderDiv').show();
	jQuery('#listRelationshipDiv').load('showRelationshipList.action',
		{
			id:patientId
		}, function()
		{
			showById('listRelationshipDiv');
			jQuery('#loaderDiv').hide();
		});
}

// -----------------------------------------------------------------------------
// Update Patient
// -----------------------------------------------------------------------------

function showUpdatePatientForm( patientId )
{
	hideById('listPatientDiv');
	setInnerHTML('editPatientDiv', '');
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('migrationPatientDiv');
	hideById('patientDashboard');
	
	jQuery('#loaderDiv').show();
	jQuery('#editPatientDiv').load('showUpdatePatientForm.action',
		{
			id:patientId
		}, function()
		{
			jQuery('#searchPatientsDiv').dialog('close');
			jQuery('#loaderDiv').hide();
			showById('editPatientDiv');
		});
		
	jQuery('#resultSearchDiv').dialog('close');
}

function validateUpdatePatient()
{
	$("#editPatientDiv :input").attr("disabled", true);
	$.ajax({
		type: "POST",
		url: 'validatePatient.action',
		data: getParamsForDiv('editPatientDiv'),
		success:updateValidationCompleted
     });
}

function updateValidationCompleted( messageElement )
{
    var type = jQuery(messageElement).find('message').attr('type');
	var message = jQuery(messageElement).find('message').text();
    
    if ( type == 'success' )
    {
    	removeDisabledIdentifier();
    	updatePatient();
    }
	else
	{
		$("#editPatientDiv :input").attr("disabled", true);
		if ( type == 'error' )
		{
			showErrorMessage( i18n_saving_patient_failed + ':' + '\n' + message );
		}
		else if ( type == 'input' )
		{
			showWarningMessage( message );
		}
		else if( type == 'duplicate' )
		{
			showListPatientDuplicate(messageElement, true);
		}
		$("#editPatientDiv :input").attr("disabled", false);
	}
}

function updatePatient()
{
	$.ajax({
      type: "POST",
      url: 'updatePatient.action',
      data: getParamsForDiv('editPatientDiv'),
      success: function( json ) {
		showPatientDashboardForm( getFieldValue('id') );
      }
     });
}

function showRepresentativeInfo( patientId)
{
	jQuery('#representativeInfo' ).dialog({
			title: i18n_representative_info,
			maximize: true, 
			closable: true,
			modal: false,
			overlay: {background:'#000000', opacity:0.1},
			width: 400,
			height: 300
		});
}

function removeDisabledIdentifier()
{
	jQuery("input.idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).val("");
	});
}

/**
 * Show list patient duplicate  by jQuery thickbox plugin
 * @param rootElement : root element of the response xml
 * @param validate  :  is TRUE if this method is called from validation method  
 */
function showListPatientDuplicate( rootElement, validate )
{
	var message = jQuery(rootElement).find('message').text();
	var patients = jQuery(rootElement).find('patient');
	
	var sPatient = "";
	jQuery( patients ).each( function( i, patient )
        {
			sPatient += "<hr style='margin:5px 0px;'><table>";
			sPatient += "<tr><td class='bold'>" + i18n_patient_system_id + "</td><td>" + jQuery(patient).find('systemIdentifier').text() + "</td></tr>" ;
			sPatient += "<tr><td class='bold'>" + i18n_patient_full_name + "</td><td>" + jQuery(patient).find('fullName').text() + "</td></tr>" ;
			sPatient += "<tr><td class='bold'>" + i18n_patient_gender + "</td><td>" + jQuery(patient).find('gender').text() + "</td></tr>" ;
			sPatient += "<tr><td class='bold'>" + i18n_patient_date_of_birth + "</td><td>" + jQuery(patient).find('dateOfBirth').text() + "</td></tr>" ;
			sPatient += "<tr><td class='bold'>" + i18n_patient_age + "</td><td>" + jQuery(patient).find('age').text() + "</td></tr>" ;
			sPatient += "<tr><td class='bold'>" + i18n_patient_phone_number + "</td><td>" + jQuery(patient).find('phoneNumber').text() + "</td></tr>";
        	
			var identifiers = jQuery(patient).find('identifier');
        	if( identifiers.length > 0 )
        	{
        		sPatient += "<tr><td colspan='2' class='bold'>" + i18n_patient_identifiers + "</td></tr>";

        		jQuery( identifiers ).each( function( i, identifier )
				{
        			sPatient +="<tr class='identifierRow'>"
        				+"<td class='bold'>" + jQuery(identifier).find('name').text() + "</td>"
        				+"<td>" + jQuery(identifier).find('value').text() + "</td>	"	
        				+"</tr>";
        		});
        	}
			
        	var attributes = jQuery(patient).find('attribute');
        	if( attributes.length > 0 )
        	{
        		sPatient += "<tr><td colspan='2' class='bold'>" + i18n_patient_attributes + "</td></tr>";

        		jQuery( attributes ).each( function( i, attribute )
				{
        			sPatient +="<tr class='attributeRow'>"
        				+"<td class='bold'>" + jQuery(attribute).find('name').text() + "</td>"
        				+"<td>" + jQuery(attribute).find('value').text() + "</td>	"	
        				+"</tr>";
        		});
        	}
        	sPatient += "<tr><td colspan='2'><input type='button' id='"+ jQuery(patient).find('id').first().text() + "' value='" + i18n_edit_this_patient + "' onclick='showUpdatePatientForm(this.id)'/></td></tr>";
        	sPatient += "</table>";
		});
		
		var result = i18n_duplicate_warning;
		if( !validate )
		{
			result += "<input type='button' value='" + i18n_create_new_patient + "' onClick='removeDisabledIdentifier( );addPatient();'/>";
			result += "<br><hr style='margin:5px 0px;'>";
		}
		
		result += "<br>" + sPatient;
		jQuery('#resultSearchDiv' ).html( result );
		jQuery('#resultSearchDiv' ).dialog({
			title: i18n_duplicated_patient_list,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 400
		});
}

// -----------------------------------------------------------------------------
// Show representative form
// -----------------------------------------------------------------------------

function toggleUnderAge(this_)
{
	if( jQuery(this_).is(":checked"))
	{
		jQuery('#representativeDiv').dialog('destroy').remove();
		jQuery('<div id="representativeDiv">' ).load( 'showAddRepresentative.action',{},
			function(){
				$('#patientForm [id=birthDate]').attr('id','birthDate_id');
				$('#patientForm [id=birthDate_id]').attr('name','birthDate_id');
			}).dialog({
			title: i18n_child_representative,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 450,
			close:function()
			{
				$('#patientForm [id=birthDate_id]').attr('id','birthDate');
				$('#patientForm [id=birthDate]').attr('name','birthDate');
			}
		});
	}else
	{
		jQuery("#representativeDiv :input.idfield").each(function(){
			if( jQuery(this).is(":disabled"))
			{
				jQuery(this).removeAttr("disabled").val("");
			}
		});
		jQuery("#representativeId").val("");
		jQuery("#relationshipTypeId").val("");
	}
}

// ----------------------------------------------------------------
// Enrollment program
// ----------------------------------------------------------------

function showProgramEnrollmentForm( patientId )
{
	jQuery('#enrollmentDiv').load('showProgramEnrollmentForm.action',
		{
			id:patientId
		}).dialog({
			title: i18n_enroll_program,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 550,
			height: 450
		});
}

function programOnchange( programId )
{
	if( programId==0){
		hideById('enrollmentDateTR');
		hideById('dateOfIncidentTR');
	}
	else{
		var type = jQuery('#enrollmentDiv [name=programId] option:selected').attr('programType')
		if(type=='2'){
			hideById('enrollmentDateTR');
			hideById('dateOfIncidentTR');
			disable('enrollmentDate');
			disable('dateOfIncident');
		}
		else{
			showById( 'enrollmentDateTR');
			enable('enrollmentDate');
			
			var dateOfEnrollmentDescription = jQuery('#enrollmentDiv [name=programId] option:selected').attr('dateOfEnrollmentDescription');
			var dateOfIncidentDescription = jQuery('#enrollmentDiv [name=programId] option:selected').attr('dateOfIncidentDescription');
			setInnerHTML('enrollmentDateDescription', dateOfEnrollmentDescription);
			setInnerHTML('dateOfIncidentDescription', dateOfIncidentDescription);
			
			var displayIncidentDate = jQuery('#enrollmentDiv [name=programId] option:selected').attr('displayIncidentDate');
			if( displayIncidentDate=='true'){
				showById('dateOfIncidentTR');
				enable('dateOfIncident');
			}
			else{
				hideById('dateOfIncidentTR');
				disable('dateOfIncident');
			}
		}
		var programId = jQuery('#programEnrollmentSelectDiv [id=programId] option:selected').val();
		jQuery('#identifierAndAttributeDiv').load("getPatientIdentifierAndAttribute.action", 
		{
			id:programId
		}, function(){
			showById('identifierAndAttributeDiv');
		});
	}
}

function saveEnrollment( patientId, programId )
{
	var patientId = jQuery('#enrollmentDiv [id=patientId]').val();
	var programId = jQuery('#enrollmentDiv [id=programId] option:selected').val();
	var programName = jQuery('#enrollmentDiv [id=programId] option:selected').text();
	var dateOfIncident = jQuery('#enrollmentDiv [id=dateOfIncidentField]').val();
	var enrollmentDate = jQuery('#enrollmentDiv [id=enrollmentDateField]').val();
	
	jQuery.postJSON( "saveProgramEnrollment.action",
		{
			patientId: patientId,
			programId: programId,
			dateOfIncident: dateOfIncident,
			enrollmentDate: enrollmentDate
		}, 
		function( json ) 
		{    
			var programInstanceId = json.programInstanceId;
			var programStageInstanceId = json.activeProgramStageInstanceId;
			var programStageName = json.activeProgramStageName;
			var dueDate = json.dueDate;
			var type = jQuery('#enrollmentDiv [id=programId] option:selected').attr('programType');
			
			var activedRow = "<tr id='tr1_" + programInstanceId 
							+ "' type='" + type +"'"
							+ " programStageInstanceId='" + programStageInstanceId + "'>"
							+ " <td id='td_" + programInstanceId + "'>"
							+ " <a href='javascript:loadActiveProgramStageRecords(" + programInstanceId + "," + programStageInstanceId + ")'>"
							+ "<span id='infor_" + programInstanceId + "' class='selected bold'>" 
							+ programName + "(" + enrollmentDate + ")</span></a></td>"
							+ "</tr>";
			
			activedRow += "<tr id='tr2_" + programInstanceId +"'"+
						+ " onclick='javascript:loadActiveProgramStageRecords(" + programInstanceId + "," + programStageInstanceId + ")' style='cursor:pointer;'>"
						+ "<td colspan='2'><a>&#8226; " + programStageName + "(" + dueDate + ")</a></td></tr>";

			jQuery('#activeTB' ).prepend(activedRow);
			jQuery('#enrollmentDiv').dialog("close");
			saveIdentifierAndAttribute( patientId, programId,'identifierAndAttributeDiv' );
			loadProgramInstance( programInstanceId, false );
			showSuccessMessage(i18n_enrol_success);
		});
}

// ----------------------------------------------------------------
// Load program instance
// ----------------------------------------------------------------

function loadProgramInstance( programInstanceId, completed )
{				
	if( programInstanceId=='') {
		hideById('programEnrollmentDiv');
		return;
	}
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
			if( completed ){
				hideById('newEncounterBtn_' + programInstanceId);
			}
			jQuery('#loaderDiv').hide();
			resize();
		});
}

// ----------------------------------------------------------------
// Program enrollmment && unenrollment
// ----------------------------------------------------------------

function validateProgramEnrollment()
{	
	jQuery('#loaderDiv').show();
	$.ajax({
		type: "GET",
		url: 'validatePatientProgramEnrollment.action',
		data: getParamsForDiv('programEnrollmentSelectDiv'),
		success: function(json) {
			hideById('message');
			var type = json.response;
			if ( type == 'success' ){
				saveProgramEnrollment();
			}
			else if ( type == 'error' ){
				setMessage( i18n_program_enrollment_failed + ':' + '\n' + message );
			}
			else if ( type == 'input' ){
				setMessage( json.message );
			}
			jQuery('#loaderDiv').hide();
      }
    });
}

function saveProgramEnrollment()
{
	$.ajax({
		type: "POST",
		url: 'saveProgramEnrollment.action',
		data: getParamsForDiv('programEnrollmentSelectDiv'),
		success: function( html ) {
				setInnerHTML('programEnrollmentDiv', html );
				jQuery('#enrollBtn').attr('value',i18n_update);
				showSuccessMessage( i18n_enrol_success );
			}
		});
    return false;
}

function unenrollmentForm( programInstanceId )
{	
	$.ajax({
		type: "POST",
		url: 'removeEnrollment.action',
		data: "programInstanceId=" + programInstanceId,
		success: function( json ) 
		{
			jQuery('#completedList' ).append('<option value="' +  programInstanceId + '">' + getInnerHTML('infor_' + programInstanceId ) + '</option>');
			hideById('tr1_' + programInstanceId );
			hideById('tr2_' + programInstanceId );
			setInnerHTML('programEnrollmentDiv','');
			showSuccessMessage( i18n_unenrol_success );
		}
    });
	
	
}

// ----------------------------------------------------------------
// Identifiers && Attributes for selected program
// ----------------------------------------------------------------

function saveIdentifierAndAttribute( patientId, programId, paramsDiv)
{
	var params  = getParamsForDiv(paramsDiv);
		params += "&patientId=" + patientId;
		params +="&programId=" + programId;
	$.ajax({
			type: "POST",
			url: 'savePatientIdentifierAndAttribute.action',
			data: params,
			success: function(json) 
			{
				showSuccessMessage( i18n_save_success );
			}
		});
}

// ----------------------------------------------------------------
// Show selected data-recording
// ----------------------------------------------------------------

function showSelectedDataRecoding( patientId )
{
	showLoader();
	hideById('searchDiv');
	hideById('dataEntryFormDiv');
	hideById('migrationPatientDiv');
	hideById('dataRecordingSelectDiv');
	
	jQuery('#dataRecordingSelectDiv').load( 'selectDataRecording.action', 
		{
			patientId: patientId
		},
		function()
		{
			jQuery('#dataRecordingSelectDiv [id=backBtnFromEntry]').hide();
			showById('dataRecordingSelectDiv');
			
			var programId = jQuery('#programEnrollmentSelectDiv [id=programId] option:selected').val();
			$('#dataRecordingSelectDiv [id=programId]').val( programId );
			$('#dataRecordingSelectDiv [id=inputCriteria]').hide();
			showById('dataRecordingSelectDiv');
			hideLoader();
			hideById('contentDiv');
		});
}

// ----------------------------------------------------------------
// Patient Location
// ----------------------------------------------------------------

function getPatientLocation( patientId )
{
	hideById('listPatientDiv');
	hideById('selectDiv');
	hideById('searchDiv');
	setInnerHTML('patientDashboard','');
				
	jQuery('#loaderDiv').show();
	
	jQuery('#migrationPatientDiv').load("getPatientLocation.action", 
		{
			patientId: patientId
		}
		, function(){
			showById( 'migrationPatientDiv' );
			jQuery( "#loaderDiv" ).hide();
		});
}

function registerPatientLocation( patientId )
{
	$.getJSON( 'registerPatientLocation.action',{ patientId:patientId }
		, function( json ) 
		{
			showPatientDashboardForm(patientId);
			showSuccessMessage( i18n_save_success );
		} );
}

// ----------------------------------------------------------------
// List program-stage-instance of selected program
// ----------------------------------------------------------------

function getVisitSchedule( programInstanceId )
{
	$('#tab-3').load("getVisitSchedule.action", {programInstanceId:programInstanceId});
}
