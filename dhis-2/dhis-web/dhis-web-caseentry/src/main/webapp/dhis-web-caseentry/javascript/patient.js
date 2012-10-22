
function organisationUnitSelected( orgUnits, orgUnitNames )
{	
	showById('selectDiv');
	showById('searchDiv');
	showById('mainLinkLbl');
	hideById('listPatientDiv');
	hideById('editPatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationPatientDiv');
	hideById('patientDashboard');
	enable('listPatientBtn');
	enable('addPatientBtn');
	enable('advancedSearchBtn');
	enable('searchObjectId');
	setFieldValue("orgunitName", orgUnitNames[0]);
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// List && Search patients
// -----------------------------------------------------------------------------

function listAllPatient()
{
	hideById('listPatientDiv');
	hideById('editPatientDiv');
	hideById('migrationPatientDiv');
	hideById('advanced-search');
	
	jQuery('#loaderDiv').show();
	contentDiv = 'listPatientDiv';
	jQuery('#listPatientDiv').load('searchRegistrationPatient.action',{
			listAll:true
		},
		function(){
			statusSearching = 0;
			showById('listPatientDiv');
			jQuery('#loaderDiv').hide();
		});
	hideLoader();
}

function advancedSearch( params )
{
	$.ajax({
		url: 'searchRegistrationPatient.action',
		type:"POST",
		data: params,
		success: function( html ){
				statusSearching = 1;
				setInnerHTML( 'listPatientDiv', html );
				showById('listPatientDiv');
				setFieldValue('listAll',false);
				jQuery( "#loaderDiv" ).hide();
			}
		});
}

// -----------------------------------------------------------------------------
// Remove patient
// -----------------------------------------------------------------------------

function removePatient( patientId, fullName )
{
	removeItem( patientId, fullName, i18n_confirm_delete, 'removePatient.action' );
}

// -----------------------------------------------------------------------------
// Add Patient
// -----------------------------------------------------------------------------

function showAddPatientForm()
{
	hideById('listPatientDiv');
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('migrationPatientDiv');
	
	jQuery('#loaderDiv').show();
	jQuery('#editPatientDiv').load('showAddPatientForm.action'
		, function()
		{
			showById('editPatientDiv');
			jQuery('#loaderDiv').hide();
		});
	
}

function validateAddPatient()
{	
	$("#patientForm :input").attr("disabled", true);
	$.ajax({
		type: "POST",
		url: 'validatePatient.action',
		data: getParamsForDiv('patientForm'),
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
		addPatient();
	}
	else
	{
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
			
		$("#patientForm :input").attr("disabled", false);
	}
}

function addPatient()
{
	$.ajax({
      type: "POST",
      url: 'addPatient.action',
      data: getParamsForDiv('patientForm'),
      success: function(json) {
		var patientId = json.message.split('_')[0];
		jQuery('#resultSearchDiv').dialog('close');
		showPatientDashboardForm( patientId );
      }
     });
    return false;
}

// ----------------------------------------------------------------
// Click Back to main form
// ----------------------------------------------------------------

function onClickBackBtn()
{
	showById('mainLinkLbl');
	showById('selectDiv');
	showById('searchDiv');
	showById('listPatientDiv');
	
	hideById('editPatientDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationPatientDiv');
	setInnerHTML('patientDashboard','');
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
	
	showById('mainLinkLbl');
	showById('selectDiv');
	showById('searchDiv');
	
	if( statusSearching == 0)
	{
		listAllPatient();
	}
	else if( statusSearching == 1 )
	{
		validateAdvancedSearch();
	}
}

//------------------------------------------------------------------------------
// Load data entry form
//------------------------------------------------------------------------------

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
		},function( )
		{
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
			else if( executionDate != ''){
				if ( completed == 'false' )
				{
					disableCompletedButton(false);
				}
				else if( completed == 'true' )
				{
					disableCompletedButton(true);
				}
			}
			resize();
			hideLoader();
			hideById('contentDiv');
			$(window).scrollTop(200);
		} );
}
