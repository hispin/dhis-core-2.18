var COLOR_GREEN = '#b9ffb9';
var COLOR_YELLOW = '#fffe8c';
var COLOR_WHITE = '#ffffff';
var COLOR_ORANGE = '#ff6600';
var COLOR_RED = '#ff8a8a';
var COLOR_GREY = '#cccccc';

function organisationUnitSelected( orgUnits, orgunitNames )
{	
	showById('selectDiv');
	hideById('listPatientDiv');
	showById('searchPatientDiv');
	setFieldValue('selectedOrgunitText', orgunitNames[0] );
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// Remove patient
// -----------------------------------------------------------------------------

function removePatient( patientId, fullName )
{
	removeItem( patientId, fullName, i18n_confirm_delete, 'removePatient.action' );
}

function sortPatients()
{
	hideById( 'listPatientDiv' );
	
	contentDiv = 'listPatientDiv';
	jQuery( "#loaderDiv" ).show();
	jQuery('#listPatientDiv').load("searchRegistrationPatient.action", 
		{
			sortPatientAttributeId: getFieldValue('sortPatientAttributeId')
		}
		, function(){
			showById('listPatientDiv');
			jQuery( "#loaderDiv" ).hide();
		});
}

// -----------------------------------------------------------------------------
// Add Patient
// -----------------------------------------------------------------------------

function validateAddPatient()
{	
	$("#editPatientDiv :input").attr("disabled", true);
	$.ajax({
		type: "POST",
		url: 'validatePatient.action',
		data: getParamsForDiv('editPatientDiv'),
		success:addValidationCompleted
     });	
}

function addValidationCompleted( data )
{
    var type = jQuery(data).find('message').attr('type');
	var message = jQuery(data).find('message').text();
	
	if ( type == 'success' )
	{
		removeDisabledIdentifier( );
		addPatient( );
	}
	else
	{
		$("#editPatientDiv :input").attr("disabled", true);
		if ( type == 'error' )
		{
			showErrorMessage( i18n_adding_patient_failed + ':' + '\n' + message );
		}
		else if ( type == 'input' )
		{
			showWarningMessage( message );
		}
		else if( type == 'duplicate' )
		{
			showListPatientDuplicate(data, false);
		}
			
		$("#editPatientDiv :input").attr("disabled", false);
	}
}


// -----------------------------------------------------------------------------
// Update Patient
// -----------------------------------------------------------------------------

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
// get and build a param String of all the identifierType id and its value
// excluding inherited identifiers
function getIdParams()
{
	var params = "";
	jQuery("input.idfield").each(function(){
		if( jQuery(this).val() && !jQuery(this).is(":disabled") )
			params += "&" + jQuery(this).attr("name") +"="+ jQuery(this).val();
	});
	return params;
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
		jQuery('<div id="representativeDiv">' ).load( 'showAddRepresentative.action' ).dialog({
			title: i18n_child_representative,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 450
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
// Add Patient
// ----------------------------------------------------------------

function showAddPatientForm()
{
	hideById('listPatientDiv');
	hideById('selectDiv');
	hideById('searchPatientDiv');
	hideById('migrationPatientDiv');
	
	jQuery('#loaderDiv').show();
	jQuery('#editPatientDiv').load('showAddPatientForm.action'
		, function()
		{
			showById('editPatientDiv');
			jQuery('#loaderDiv').hide();
		});
	
}

function addPatient()
{
	$.ajax({
      type: "POST",
      url: 'addPatient.action',
      data: getParamsForDiv('editPatientDiv'),
      success: function(json) {
		var type = json.response;
		showProgramEnrollmentSelectForm( json.message );
		jQuery('#resultSearchDiv').dialog('close');
      }
     });
    return false;
}

// ----------------------------------------------------------------
// Update Patient
// ----------------------------------------------------------------

function showUpdatePatientForm( patientId )
{
	hideById('listPatientDiv');
	setInnerHTML('editPatientDiv', '');
	hideById('selectDiv');
	hideById('searchPatientDiv');
	hideById('migrationPatientDiv');
				
	jQuery('#loaderDiv').show();
	jQuery('#editPatientDiv').load('showUpdatePatientForm.action',
		{
			id:patientId
		}, function()
		{
			showById('editPatientDiv');
			jQuery('#searchPatientsDiv').dialog('close');
			jQuery('#loaderDiv').hide();
		});
		
	jQuery('#resultSearchDiv').dialog('close');
}

function updatePatient()
{
	$.ajax({
      type: "POST",
      url: 'updatePatient.action',
      data: getParamsForDiv('editPatientDiv'),
      success: function( json ) {
		showProgramEnrollmentSelectForm( getFieldValue('id') );
      }
     });
}

// ----------------------------------------------------------------
// Enrollment program
// ----------------------------------------------------------------

function showProgramEnrollmentSelectForm( patientId )
{
	hideById('listPatientDiv');
	hideById('editPatientDiv');
	hideById('selectDiv');
	hideById('searchPatientDiv');
	hideById('migrationPatientDiv');
				
	jQuery('#loaderDiv').show();
	jQuery('#enrollmentDiv').load('showProgramEnrollmentForm.action',
		{
			id:patientId
		}, function()
		{	
			showById('enrollmentDiv');
			
			jQuery('#loaderDiv').hide();
		});
}

function showProgramEnrollmentForm( patientId, programId )
{				
	if( programId == 0 )
	{
		jQuery('#enrollBtn').attr('value',i18n_enroll_to_program);
		hideEnrolmentField();

		hideById('programEnrollmentDiv');
		
		return;
	}
		
	jQuery('#loaderDiv').show();
	jQuery('#programEnrollmentDiv').load('enrollmentform.action',
		{
			patientId:patientId,
			programId:programId
		}, function()
		{
			showById('programEnrollmentDiv');
			showEnrolmentField();
			
			var singleEvent = jQuery('#enrollmentDiv [name=programId] option:selected').attr('singleevent');
			
			if(singleEvent=='true')
			{
				hideEnrolmentField();
			}
			else
			{
				showEnrolmentField();
				var hideDateOfIncident = jQuery('#programEnrollmentSelectDiv [name=programId] option:selected').attr('hidedateofincident');
					
				if( hideDateOfIncident=='true')
				{
					hideById( 'dateOfIncidentTR');
				}
				else
				{
					showById( 'dateOfIncidentTR');
				}
			}
			
			jQuery('#loaderDiv').hide();
		});
}

function validateProgramEnrollment()
{	
	$.ajax({
		type: "GET",
		url: 'validatePatientProgramEnrollment.action',
		data: getParamsForDiv('programEnrollmentSelectDiv'),
		success: function(json) {
			hideById('message');
			var type = json.response;
			if ( type == 'success' )
			{
				saveProgramEnrollment();
			}
			else if ( type == 'error' )
			{
				setMessage( i18n_program_enrollment_failed + ':' + '\n' + message );
			}
			else if ( type == 'input' )
			{
				setMessage( json.message );
			}
      }
    });
    return false;
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

// ----------------------------------------------------------------
// Un-Enrollment program
// ----------------------------------------------------------------

function showUnenrollmentSelectForm( patientId )
{
	hideById('listPatientDiv');
	hideById('editPatientDiv');
	hideById('selectDiv');
	hideById('searchPatientDiv');
	hideById('migrationPatientDiv');
				
	jQuery('#loaderDiv').show();
	jQuery('#enrollmentDiv').load('showProgramUnEnrollmentForm.action',
		{
			patientId:patientId
		}, function()
		{
			showById('enrollmentDiv');
			jQuery('#loaderDiv').hide();
		});
}

function showUnenrollmentForm( programInstanceId )
{				
	if( programInstanceId == 0 )
	{
		hideById( 'unenrollmentFormDiv' );
		return;
	}
	
	jQuery('#loaderDiv').show();
	jQuery.getJSON( "getProgramInstance.action",
		{
			programInstanceId:programInstanceId
		}, 
		function( json ) 
		{   
			showById( 'unenrollmentFormDiv' );
			jQuery( "#loaderDiv" ).hide();
		});
}

function unenrollmentForm( programInstanceId )
{				
	if( programInstanceId == 0 )
	{
		disable('enrollBtn');
		return;
	}
		
	$.ajax({
		type: "POST",
		url: 'removeEnrollment.action',
		data: getParamsForDiv('enrollmentDiv'),
		success: function( json ) 
		{
			showSuccessMessage( i18n_unenrol_success );
			hideEnrolmentField( 'enrollmentDateTR' );
			jQuery('#enrollmentDiv [name=programId]').val('0');
			hideById( 'programEnrollmentDiv' );
		}
    });
}

//-----------------------------------------------------------------------------
//Save
//-----------------------------------------------------------------------------

function saveDueDate( programStageInstanceId, programStageInstanceName )
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

//----------------------------------------------------
// Show relationship with new patient
//----------------------------------------------------

function showRelationshipList( patientId )
{
	hideById('addRelationshipDiv');
	
	if ( getFieldValue('isShowPatientList') == 'false' )
	{
		hideById('selectDiv');
		hideById('searchPatientDiv');
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
	else
	{
		loadPatientList();
	}
}

// ----------------------------------------------------------------
// Click Back to Search button
// ----------------------------------------------------------------

function onClickBackBtn()
{
	showById('selectDiv');
	showById('searchPatientDiv');
	showById('listPatientDiv');
	
	hideById('editPatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationPatientDiv');
}

function loadPatientList()
{
	hideById('editPatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('dataRecordingSelectDiv');
	hideById('dataEntryFormDiv');
	hideById('migrationPatientDiv');
	
	showById('selectDiv');
	showById('searchPatientDiv');
	
	if( statusSearching == 0)
	{
		loadAllPatients();
	}
	else if( statusSearching == 1 )
	{
		searchAdvancedPatients();
	}
}

// -----------------------------------------------------------------------------
// Load all patients
// -----------------------------------------------------------------------------

function loadAllPatients()
{
	hideById('listPatientDiv');
	
	var sortPatientAttributeId = getFieldValue('sortPatientAttributeId');
	
	jQuery('#loaderDiv').show();
	contentDiv = 'listPatientDiv';
	jQuery('#listPatientDiv').load('searchRegistrationPatient.action',{
			listAll:true,
			sortPatientAttributeId: (sortPatientAttributeId ? sortPatientAttributeId : "")
		},
		function(){
			statusSearching = 0;
			showById('listPatientDiv');
			jQuery('#loaderDiv').hide();
		});
	hideLoader();
}

function addPhoneToList( elementList, _id, _patientName, _phoneNo )
{
	var list = jQuery( "#" + elementList );
	list.append( "<option value='" + _id + "'>\"" + _patientName + " <" + _phoneNo + ">" + "\"</option>" );
	
	jQuery( "tr#tr" + _id ).hide();
}


function removePhoneFromList( elementList, _id )
{
	var list = jQuery( "#" + elementList + " option[value='" + _id + "']" ).remove();
	
	jQuery( "tr#tr" + _id ).show();
}

//-----------------------------------------------------------------------------
// Saver objects
//-----------------------------------------------------------------------------

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


// -----------------------------------------------------------------------------
// remove value of all the disabled identifier fields
// an identifier field is disabled when its value is inherited from another person ( underAge is true ) 
// we don't save inherited identifiers. Only save the representative id.
// -----------------------------------------------------------------------------

function removeDisabledIdentifier()
{
	jQuery("input.idfield").each(function(){
		if( jQuery(this).is(":disabled"))
			jQuery(this).val("");
	});
}

function addEventForPatientForm( divname )
{
	jQuery("#" + divname + " [id=searchPatientByNameBtn]").click(function() {
		getPatientsByName( divname );
	});
	
	jQuery("#" + divname + " [id=checkDuplicateBtn]").click(function() {
		checkDuplicate( divname );
	});
	
	jQuery("#" + divname + " [id=dobType]").change(function() {
		dobTypeOnChange( divname );
	});
}

function showRepresentativeInfo( patientId)
{
	jQuery('#representativeInfo' ).dialog({
			title: i18n_representative_info,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 400,
			height: 300
		});
}

function hideEnrolmentField()
{
	setFieldValue( 'enrollmentDate', '' );
	setFieldValue( 'dateOfIncident', '' );
	hideById('enrollmentDateTR');
	hideById('dateOfIncidentTR');
	hideById('enrollBtn');
}
  
function showEnrolmentField()
{
	showById('enrollmentDateTR');
	showById('dateOfIncidentTR');
	showById('enrollBtn');
}

function saveIdentifierAndAttribute()
{
	$.ajax({
			type: "POST",
			url: 'savePatientIdentifierAndAttribute.action',
			data: getParamsForDiv('programEnrollmentInforForm'),
			success: function(json) 
			{
				showSuccessMessage( i18n_save_success );
			}
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
	hideById('migrationPatientDiv');
	jQuery('#dataRecordingSelectDiv').load( 'selectDataRecording.action', 
		{
			patientId: patientId
		},
		function()
		{
			jQuery('#dataRecordingSelectDiv [id=patientInfoDiv]').hide();
			jQuery('#dataRecordingSelectDiv [id=backBtnFromEntry]').hide();
			showById('dataRecordingSelectDiv');
			
			var programId = jQuery('#programEnrollmentSelectDiv [id=programId] option:selected').val();
			$('#dataRecordingSelectDiv [id=programId]').val( programId );
			$('#dataRecordingSelectDiv [id=programIdTR]').hide();
			
			loadProgramStages();
			hideLoader();
			hideById('contentDiv');
		});
}

function searchPatient()
{
	$.ajax({
		url: 'searchRegistrationPatient.action',
		type:"POST",
		data: getParamsForDiv('searchPatientDiv'),
		success: function( html ){
				statusSearching = 1;
				setInnerHTML( 'listPatientDiv', html );
				showById('listPatientDiv');
				jQuery( "#loaderDiv" ).hide();
			}
		});
}

//--------------------------------------------------------------------------------------------
// Migration patient
//--------------------------------------------------------------------------------------------

function getPatientLocation( patientId )
{
	hideById('listPatientDiv');
	hideById('selectDiv');
	hideById('searchPatientDiv');
				
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

function verifyOrgunitRegistration( patientId )
{
	$.getJSON( 'verifyOrgunitRegistration.action', {}
		, function( json ) 
		{
			var type = json.response;
			if( type == 'success' )
			{
				registerPatientLocation( patientId );
			}
			else if( type == 'input' )
			{
				showWarningMessage( i18n_can_not_register_patient_for_orgunit);
			}
		} );
}

function registerPatientLocation( patientId )
{
	$.getJSON( 'registerPatientLocation.action',{ patientId:patientId }
		, function( json ) 
		{
			showSuccessMessage( i18n_save_success );
		} );
}
