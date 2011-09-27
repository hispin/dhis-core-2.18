
function organisationUnitSelected( orgUnits )
{	
	showById('selectDiv');
	disable('listPatientBtn');
	
	hideById('searchPatientDiv');
	hideById('listPatientDiv');
	hideById('addPatientDiv');
	hideById('updatePatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
			
	$.postJSON( 'organisationUnitHasPatients.action', {orgunitId:orgUnits[0]}
		, function( json ) 
		{
			var type = json.response;
			setFieldValue('selectedOrgunitText', json.message );
				
			if( type == 'success' )
			{
				showById('searchPatientDiv');
				enable('listPatientBtn');
				setInnerHTML('warnmessage','');
				setFieldValue('selectedOrgunitText', json.message );
			}
			else if( type == 'input' )
			{
				setInnerHTML('warnmessage', i18n_can_not_register_patient_for_orgunit);
				disable('listPatientBtn');
			}
		} );
}

selection.setListenerFunction( organisationUnitSelected );

//------------------------------------------------------------------------------
// Search patients by selected attribute
//------------------------------------------------------------------------------

function searchingAttributeOnChange( this_ )
{	
	var container = jQuery(this_).parent().parent().attr('id');
	var attributeId = jQuery('#' + container+ ' [id=searchingAttributeId]').val(); 
	var element = $('#' + container+ ' [id=searchText]');
	var valueType = jQuery('#' + container+ ' [id=searchingAttributeId] option:selected').attr('valueType');
	
	if( attributeId == '0' )
	{
		element.replaceWith( programComboBox );
	}
	else if ( valueType=='YES/NO' )
	{
		element.replaceWith( trueFalseBox );
	}
	else
	{
		element.replaceWith( searchTextBox );
	}
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientDetails( patientId )
{
    $('#detailsArea').load("getPatientDetails.action", 
		{
			id:patientId
		}
		, function( html ){
			setInnerHTML( 'detailsArea', html );
			showDetails();
		});
}

// -----------------------------------------------------------------------------
// Remove patient
// -----------------------------------------------------------------------------

function removePatient( patientId, fullName )
{
	removeItem( patientId, fullName, i18n_confirm_delete, 'removePatient.action' );
}

//-----------------------------------------------------------------------------
// Search Patient
//-----------------------------------------------------------------------------

function searchPatientsOnKeyUp( event )
{
	var key = getKeyCode( event );
	
	if ( key==13 )// Enter
	{
		searchPatients();
	}
}

function getKeyCode(e)
{
	 if (window.event)
		return window.event.keyCode;
	 return (e)? e.which : null;
}

function searchPatients()
{
	hideById( 'listPatientDiv' );
	var searchTextFields = jQuery('[name=searchText]');
	var flag = true;
	$( searchTextFields ).each( function( i, item )
    {
		if( jQuery( item ).val() == '' )
		{
			showWarningMessage( i18n_specify_search_criteria );
			flag = false;
		}
	});
	
	if(!flag) return;
	
	contentDiv = 'listPatientDiv';
	$( "#loaderDiv" ).show();
	$.ajax({
		url: 'searchRegistrationPatient.action',
		type:"POST",
		data: getParamsForDiv('searchPatientDiv'),
		success: function( html ){
				statusSearching = 1;
				setInnerHTML( 'listPatientDiv', html );
				showById('listPatientDiv');
				$( "#loaderDiv" ).hide();
			}
		});
}

function sortPatients()
{
	hideById( 'listPatientDiv' );
	
	contentDiv = 'listPatientDiv';
	$( "#loaderDiv" ).show();
	$('#listPatientDiv').load("searchRegistrationPatient.action", 
		{
			sortPatientAttributeId: getFieldValue('sortPatientAttributeId')
		}
		, function(){
			showById('listPatientDiv');
			$( "#loaderDiv" ).hide();
		});
}

// -----------------------------------------------------------------------------
// Add Patient
// -----------------------------------------------------------------------------

function validateAddPatient()
{	
	$.ajax({
		type: "POST",
		url: 'validatePatient.action',
		data: getParamsForDiv('addPatientDiv'),
		success:addValidationCompleted
     });	
}

function addValidationCompleted( data )
{
    var type = $(data).find('message').attr('type');
	var message = $(data).find('message').text();
	
	if ( type == 'success' )
	{
		removeDisabledIdentifier( );
		addPatient( );
	}
	else if ( type == 'error' )
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
}


// -----------------------------------------------------------------------------
// Update Patient
// -----------------------------------------------------------------------------

function validateUpdatePatient()
{
    $.post( 'validatePatient.action?' + getIdParams( ), 
		{ 
			id: jQuery( '#updatePatientForm [id=id]' ).val(),
			fullName: jQuery( '#updatePatientForm [id=fullName]' ).val(),
			gender: jQuery( '#updatePatientForm [id=gender]' ).val(),
			birthDate: jQuery( '#updatePatientForm [id=birthDate]' ).val()
		}, updateValidationCompleted );
}

function updateValidationCompleted( messageElement )
{
    var type = $(messageElement).find('message').attr('type');
	var message = $(messageElement).find('message').text();
    
    if ( type == 'success' )
    {
    	removeDisabledIdentifier();
    	updatePatient();
    }
    else if ( type == 'error' )
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
	$.post( 'validatePatient.action', 
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
	var type = $(messageElement).find('message').attr('type');
	var message = $(messageElement).find('message').text();
    
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
	var message = $(rootElement).find('message').text();
	var patients = $(rootElement).find('patient');
	
	var sPatient = "";
	$( patients ).each( function( i, patient )
        {
			sPatient += "<hr style='margin:5px 0px;'><table>";
			sPatient += "<tr><td><strong>" + i18n_patient_system_id + "</strong></td><td>" + $(patient).find('systemIdentifier').text() + "</td></tr>" ;
			sPatient += "<tr><td><strong>" + i18n_patient_full_name + "</strong></td><td>" + $(patient).find('fullName').text() + "</td></tr>" ;
			sPatient += "<tr><td><strong>" + i18n_patient_gender + "</strong></td><td>" + $(patient).find('gender').text() + "</td></tr>" ;
			sPatient += "<tr><td><strong>" + i18n_patient_date_of_birth + "</strong></td><td>" + $(patient).find('dateOfBirth').text() + "</td></tr>" ;
			sPatient += "<tr><td><strong>" + i18n_patient_age + "</strong></td><td>" + $(patient).find('age').text() + "</td></tr>" ;
			sPatient += "<tr><td><strong>" + i18n_patient_blood_group + "</strong></td><td>" + $(patient).find('bloodGroup').text() + "</td></tr>";
        	
			var identifiers = $(patient).find('identifier');
        	if( identifiers.length > 0 )
        	{
        		sPatient += "<tr><td colspan='2'><strong>" + i18n_patient_identifiers + "</strong></td></tr>";

        		$( identifiers ).each( function( i, identifier )
				{
        			sPatient +="<tr class='identifierRow'>"
        				+"<td><strong>" + $(identifier).find('name').text() + "</strong></td>"
        				+"<td>" + $(identifier).find('value').text() + "</td>	"	
        				+"</tr>";
        		});
        	}
			
        	var attributes = $(patient).find('attribute');
        	if( attributes.length > 0 )
        	{
        		sPatient += "<tr><td colspan='2'><strong>" + i18n_patient_attributes + "</strong></td></tr>";

        		$( attributes ).each( function( i, attribute )
				{
        			sPatient +="<tr class='attributeRow'>"
        				+"<td><strong>" + $(attribute).find('name').text() + "</strong></td>"
        				+"<td>" + $(attribute).find('value').text() + "</td>	"	
        				+"</tr>";
        		});
        	}
        	sPatient += "<tr><td colspan='2'><input type='button' id='"+ $(patient).find('id').first().text() + "' value='" + i18n_edit_this_patient + "' onclick='showUpdatePatientForm(this.id)'/></td></tr>";
        	sPatient += "</table>";
		});
		
		var result = i18n_duplicate_warning;
		if( !validate )
		{
			result += "<input type='button' value='" + i18n_create_new_patient + "' onClick='removeDisabledIdentifier( );addPatient();'/>";
			result += "<br><hr style='margin:5px 0px;'>";
		}
		
		result += "<br>" + sPatient;
		$('#resultSearchDiv' ).html( result );
		$('#resultSearchDiv' ).dialog({
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
		$('#representativeDiv').dialog('destroy').remove();
		$('<div id="representativeDiv">' ).load( 'showAddRepresentative.action' ).dialog({
			title: i18n_child_representative,
			maximize: true, 
			closable: true,
			modal:true,
			overlay:{background:'#000000', opacity:0.1},
			width: 800,
			height: 400
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
	setInnerHTML('updatePatientDiv', '');
	
	hideById('selectDiv');
	hideById('searchPatientDiv');
				
	jQuery('#loaderDiv').show();
	jQuery('#addPatientDiv').load('showAddPatientForm.action'
		, function()
		{
			showById('addPatientDiv');
			jQuery('#loaderDiv').hide();
		});
	
}

function addPatient()
{
	$.ajax({
      type: "POST",
      url: 'addPatient.action',
      data: getParamsForDiv('addPatientDiv'),
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
	setInnerHTML('addPatientDiv', '');
	
	hideById('selectDiv');
	hideById('searchPatientDiv');
				
	jQuery('#loaderDiv').show();
	jQuery('#updatePatientDiv').load('showUpdatePatientForm.action',
		{
			id:patientId
		}, function()
		{
			showById('updatePatientDiv');
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
      data: getParamsForDiv('updatePatientDiv'),
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
	hideById('addPatientDiv');
	hideById('updatePatientDiv');
	
	hideById('selectDiv');
	hideById('searchPatientDiv');
				
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
		disable('enrollBtn');
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
			enable('enrollBtn');
			jQuery('#loaderDiv').hide();
		});
}

function validateProgramEnrollment()
{	
	$.ajax({
		type: "POST",
		url: 'validatePatientProgramEnrollment.action',
		data: getParamsForDiv('programEnrollmentSelectDiv'),
		success: function(json) {
			var type = json.response;
			if ( type == 'success' )
			{
				saveProgramEnrollment();
			}
			else if ( type == 'error' )
			{
				showErrorMessage( i18n_program_enrollment_failed + ':' + '\n' + message );
			}
			else if ( type == 'input' )
			{
				showWarningMessage( json.message );
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
	hideById('addPatientDiv');
	hideById('updatePatientDiv');
	
	hideById('selectDiv');
	hideById('searchPatientDiv');
				
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
		return;
	}
	
	jQuery('#loaderDiv').show();
	jQuery.postJSON( "getProgramInstance.action",
		{
			programInstanceId:programInstanceId
		}, 
		function( json ) 
		{   
			setFieldValue( 'enrollmentDate', json.dateOfIncident );
			setFieldValue( 'dateOfIncident', json.enrollmentDate );
			setFieldValue( 'dateOfEnrollmentDescription', json.dateOfEnrollmentDescription );
			setFieldValue( 'dateOfIncidentDescription', json.dateOfIncidentDescription );
			showById( 'unenrollmentFormDiv' );
			$( "#loaderDiv" ).hide();
		});
}

function unenrollmentForm( programInstanceId )
{				
	if( programInstanceId == 0 )
	{
		disable('enrollBtn');
		return;
	}
		
	jQuery('#loaderDiv').show();
	$.ajax({
      type: "POST",
      url: 'removeEnrollment.action',
      data: getParamsForDiv('enrollmentDiv'),
      success: function( json ) 
	  {
		var list = byId( 'programInstanceId' );
		list.remove( list.selectedIndex );
		
		if( list.value == 0 )
		{
			hideById( 'unenrollmentFormDiv' );
		}
		jQuery('#loaderDiv').hide();
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
	
	var dateSaver = new DateSaver( programStageInstanceId, field.value, '#ccffcc' );
	dateSaver.save();
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
	
	hideById('addPatientDiv');
	hideById('updatePatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
}

function loadPatientList()
{
	hideById('addPatientDiv');
	hideById('updatePatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	
	showById('selectDiv');
	showById('searchPatientDiv');
	
	if( statusSearching == 0)
	{
		loadAllPatients();
	}
	else if( statusSearching == 1 )
	{
		searchPatients();
	}
}

// -----------------------------------------------------------------------------
// Load all patients
// -----------------------------------------------------------------------------

function loadAllPatients()
{
	hideById('listPatientDiv');
	hideById('addPatientDiv');
	hideById('updatePatientDiv');
	
	jQuery('#loaderDiv').show();
	contentDiv = 'listPatientDiv';
	jQuery('#listPatientDiv').load('searchRegistrationPatient.action',{
			listAll:true,
			sortPatientAttributeId: getFieldValue('sortPatientAttributeId')
		},
		function(){
			statusSearching = 0;
			showById('listPatientDiv');
			jQuery('#loaderDiv').hide();
		});
	hideLoader();
}

//-----------------------------------------------------------------------------
// Saver objects
//-----------------------------------------------------------------------------

function DateSaver( programStageInstanceId_, dueDate_, resultColor_ )
{
	var SUCCESS = '#ccffcc';
	var ERROR = '#ccccff';
	
	var programStageInstanceId = programStageInstanceId_;	
	var dueDate = dueDate_;
	var resultColor = resultColor_;	

	this.save = function()
	{
		var request = new Request();
		request.setCallbackSuccess( handleResponse );
		request.setCallbackError( handleHttpError );
		request.setResponseTypeXML( 'status' );
		request.send( 'saveDueDate.action?programStageInstanceId=' + programStageInstanceId + '&dueDate=' + dueDate );
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
			markValue( ERROR );
			window.alert( i18n_saving_value_failed_status_code + '\n\n' + code );
		}
	}

	function handleHttpError( errorCode )
	{
		markValue( ERROR );
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


// -----------------------------------------------------------------------------
// Show Details
// -----------------------------------------------------------------------------

function showDetails()
{
	var detailArea = $("#detailsArea");
	var top = (f_clientHeight() / 2) - 200;	
	if ( top < 0 ) top = 0; 
    var left = screen.width - detailArea.width() - 100;
    detailArea.css({"left":left+"px","top":top+"px"});
    detailArea.show('fast');
    
}

/**
 *  Get document width, hieght, scroll positions
 *  Work with all browsers
 * @return
 */

function f_clientWidth() {
	return f_filterResults (
		window.innerWidth ? window.innerWidth : 0,
		document.documentElement ? document.documentElement.clientWidth : 0,
		document.body ? document.body.clientWidth : 0
	);
}
function f_clientHeight() {
	return f_filterResults (
		window.innerHeight ? window.innerHeight : 0,
		document.documentElement ? document.documentElement.clientHeight : 0,
		document.body ? document.body.clientHeight : 0
	);
}
function f_scrollLeft() {
	return f_filterResults (
		window.pageXOffset ? window.pageXOffset : 0,
		document.documentElement ? document.documentElement.scrollLeft : 0,
		document.body ? document.body.scrollLeft : 0
	);
}
function f_scrollTop() {
	return f_filterResults (
		window.pageYOffset ? window.pageYOffset : 0,
		document.documentElement ? document.documentElement.scrollTop : 0,
		document.body ? document.body.scrollTop : 0
	);
}
function f_filterResults(n_win, n_docel, n_body) {
	var n_result = n_win ? n_win : 0;
	if (n_docel && (!n_result || (n_result > n_docel)))
		n_result = n_docel;
	return n_body && (!n_result || (n_result > n_body)) ? n_body : n_result;
}

// -----------------------------------------------------------------------------
// Advanced search
// -----------------------------------------------------------------------------

function addAttributeOption()
{
	var rowId = 'advSearchBox' + jQuery('#advancedSearchTB select[name=searchingAttributeId]').length + 1;
	var contend  = '<td>' + getInnerHTML('searchingAttributeIdTD') + '</td>';
		contend += '<td>' + searchTextBox ;
		contend += '<input type="button" value="-" onclick="removeAttributeOption(' + "'" + rowId + "'" + ');"></td>';
		contend = '<tr id="' + rowId + '">' + contend + '</tr>';

	$('#advancedSearchTB > tbody:last').append( contend );
}	

function removeAttributeOption( rowId )
{
	jQuery( '#' + rowId ).remove();
}		

