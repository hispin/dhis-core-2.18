
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
			
	$.getJSON( 'organisationUnitHasPatients.action?orgunitId=' + orgUnits[0], {}
		, function( json ) 
		{
			var type = json.response;
			setFieldValue('selectedOrgunitText', json.message );
				
			if( type == 'success' )
			{
						
				$( "#loaderDiv" ).show();
				jQuery.postJSON( "patientform.action",
					{
					}, 
					function( json ) 
					{    
						clearListById('programId');
						addOptionById( 'programId', "0", i18n_select_program );
						for ( i in json.programs ) 
						{
							addOptionById( 'programId', json.programs[i].id, json.programs[i].name );
						} 
						
						showById('searchPatientDiv');
						enable('listPatientBtn');
						setInnerHTML('warnmessage','');
						
						$( "#loaderDiv" ).hide();
					});
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


function searchingAttributeOnChange()
{
	var value = byId('searchingAttributeId').value;
	
	if(value == '0')
	{
		byId('programId').style.display = 'block';
		byId('searchText').style.display = 'none';
	}
	else
	{
		byId('searchText').style.display = 'block';
		byId('programId').style.display = 'none';
		byId('programId').selectedIndex = 0;
	}
}

// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientDetails( patientId )
{
    //$.post( 'getPatient.action', { id:patientId }, patientReceived );
	$.ajax({
		url: 'getPatient.action?id=' + patientId,
		cache: false,
		dataType: "xml",
		success: patientReceived
	});
}

function patientReceived( patientElement )
{   
	// ----------------------------------------------------------------------------
	// Get common-information
    // ----------------------------------------------------------------------------
	
	var id = $(patientElement).find( "id" ).text();
	var fullName = $(patientElement).find( "fullName" ).text();
	var gender = $(patientElement).find( "gender" ).text();
	var dobType = $(patientElement).find( "dobType" ).text();
	var birthDate = $(patientElement).find( "dateOfBirth" ).text();
	var bloodGroup= $(patientElement).find( "bloodGroup" ).text();
    
	var commonInfo =  '<strong>'  + i18n_id + ':</strong> ' + id + "<br>" 
					+ '<strong>' + i18n_full_name + ':</strong> ' + fullName + "<br>" 
					+ '<strong>' + i18n_gender + ':</strong> ' + gender+ "<br>" 
					+ '<strong>' + i18n_dob_type + ':</strong> ' + dobType+ "<br>" 
					+ '<strong>' + i18n_date_of_birth + ':</strong> ' + birthDate+ "<br>" 
					+ '<strong>' + i18n_blood_group  + ':</strong> ' + bloodGroup;
	
	setInnerHTML( 'commonInfoField', commonInfo );
	
	// ----------------------------------------------------------------------------
	// Get identifiers
    // ----------------------------------------------------------------------------
	
    var identifierText = '';
	
	var identifiers = $(patientElement).find( "identifier" );
	$( identifiers ).each( function( i, item )
        {
            identifierText += $( item ).text() + '<br>';		
        } );
	
	setInnerHTML( 'identifierField', identifierText );
	
	// ----------------------------------------------------------------------------
	// Get attributes
    // ----------------------------------------------------------------------------
	
    var attributeValues = '';
	
	var attributes = $(patientElement).find( "attribute" );
	$( attributes ).each( function( i, item )
        {
            attributeValues += '<strong>' + $( item ).find( 'name' ).text() + ':</strong> ' + $( item ).find( 'value' ).text() + '<br>';				
        } );
	
	setInnerHTML( 'attributeField', attributeValues );
    
	// ----------------------------------------------------------------------------
	// Get programs
    // ----------------------------------------------------------------------------
	
    var programName = '';
	
	var programs = $( patientElement ).find( "program" );
	$( programs ).each( function( i, item )
        {
            programName += $( item ).text() + '<br>';
        } );
		
	setInnerHTML( 'programField', programName );
   
    showDetails();
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
	
	if( getFieldValue('searchText') == '' )
	{
		$('#listPatientDiv').html( "<i style='color:red'>" + i18n_specify_a_search_criteria + "</i>" );
		showById( 'listPatientDiv' );
		return;
	}
	
	contentDiv = 'listPatientDiv';
	$( "#loaderDiv" ).show();
	$('#listPatientDiv').load("searchPatient.action", 
		{
			searchText: getFieldValue('searchText'), 
			searchingAttributeId: getFieldValue('searchingAttributeId'), 
			sortPatientAttributeId: getFieldValue('sortPatientAttributeId'), 
			programId: getFieldValue('programId') 
		}
		, function(){
			showById('listPatientDiv');
			$( "#loaderDiv" ).hide();
		});
}

function sortPatients()
{
	hideById( 'listPatientDiv' );
	
	contentDiv = 'listPatientDiv';
	$( "#loaderDiv" ).show();
	$('#listPatientDiv').load("searchPatient.action", 
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
	$.post( 'validatePatient.action?' + getIdParams( ), 
		{ 
			checkedDuplicate: checkedDuplicate,
			fullName: jQuery( '#addPatientForm [id=fullName]' ).val(),
			gender: jQuery( '#addPatientForm [id=gender]' ).val(),
			dobType: jQuery( '#addPatientForm [id=dobType]' ).val(),
			birthDate: jQuery( '#addPatientForm [id=birthDate]' ).val(),
			ageType: jQuery( '#addPatientForm [id=ageType]' ).val(),
			age: jQuery( '#addPatientForm [id=age]' ).val(), 
			gender: jQuery( '#addPatientForm [id=gender]' ).val(),
			underAge: jQuery( '#addPatientForm [id=underAge]' ).is(":checked"),
			representativeId: jQuery( '#addPatientForm [id=representativeId]' ).val(),
			relationshipTypeId: jQuery( '#addPatientForm [id=relationshipTypeId]' ).val()
		}, addValidationCompleted );
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
		if( !checkedDuplicate )
			showListPatientDuplicate(data, true);
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
    	if( !checkedDuplicate )
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
		}, function( xmlObject )
		{
			checkDuplicateCompleted( xmlObject );
		});
}
function checkDuplicateCompleted( messageElement )
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
    	showListPatientDuplicate( messageElement, false );
    }
}

/**
 * Show list patient duplicate  by jQuery thickbox plugin
 * @param rootElement : root element of the response xml
 * @param validate  :  is TRUE if this method is called from validation method  
 */
function showListPatientDuplicate(rootElement, validate)
{
	var message = $(rootElement).find('message').text();
	var patients = $(rootElement).find('patient');
	
	var sPatient = "";
	$( patients ).each( function( i, patient )
        {
			sPatient += "<hr style='margin:5px 0px;'><table>";
			sPatient += "<tr><td><strong>" + i18n_patient_system_id + "</strong></td><td>" + $(patient).find('systemIdentifier').text() + "</td></tr>" ;
			sPatient += "<tr><td><strong>" + i18n_patient_fullName + "</strong></td><td>" + $(patient).find('fullName').text() + "</td></tr>" ;
			sPatient += "<tr><td><strong>" + i18n_patient_gender + "</strong></td><td>" + $(patient).find('gender').text() + "</td></tr>" ;
			sPatient += "<tr><td><strong>" + i18n_patient_date_of_birth + "</strong></td><td>" + $(patient).find('dateOfBirth').text() + "</td></tr>" ;
			sPatient += "<tr><td><strong>" + i18n_patient_age + "</strong></td><td>" + $(patient).find('age').text() + "</td></tr>" ;
			sPatient += "<tr><td><strong>" + i18n_patient_blood_group + "</strong></td><td>" + $(patient).find('bloodGroup').text() + "</td></tr>";
        	
			var identifiers = $(patient).find('identifier');
        	if( identifiers.length > 0 )
        	{
        		sPatient += "<tr><td colspan='2'><strong>" + i18n_patient_identifiers + "</strong></td></tr>"
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
        		sPatient += "<tr><td colspan='2'><strong>"+i18n_patient_attributes+"</strong></td></tr>"
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
		
		jQuery("#thickboxContainer","#hiddenModalContent").html("").append(sPatient);
		if( !validate ) jQuery("#btnCreateNew","#hiddenModalContent").click(function(){window.parent.tb_remove();});
		else jQuery("#btnCreateNew","#hiddenModalContent").click(function(){window.parent.tb_remove();window.parent.checkedDuplicate = true;window.parent.validatePatient();});
		tb_show( message, "#TB_inline?height=500&width=500&inlineId=hiddenModalContent", null);
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
			jQuery('#searchPatientsByNameDiv').dialog('close');
			window.parent.tb_remove();
			jQuery('#loaderDiv').hide();
		});
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
	dateSaver.save();s
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
	$.ajaxSettings.cache = false;

	hideById('listPatientDiv');
	hideById('addPatientDiv');
	hideById('updatePatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	
	jQuery('#listPatientDiv').load("searchPatient.action", {}
		, function(){
			showById('selectDiv');
			showById('searchPatientDiv');
			showById('listPatientDiv');
			$( "#loaderDiv" ).hide();
		});
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
	jQuery('#listPatientDiv').load('searchPatient.action',{
			listAll:true,
			sortPatientAttributeId: getFieldValue('sortPatientAttributeId')
		},
		function(){
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
	jQuery("#" + divname + " [id=age]").change(function() {
		jQuery("#" + divname + " [id=birthDate]").val("");
	});
	
	jQuery("#" + divname + " [id=birthDate]").change(function() {
		jQuery("#" + divname + " [id=age]").val("");
	});
	
	jQuery("#" + divname + " [id=checkDuplicateBtn]").click(function() {
		checkDuplicate( divname );
	});
	
	jQuery("#" + divname + " [id=dobType]").click(function() {
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
