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
	
	clearListById('programIdAddPatient');
	jQuery.get("getAllPrograms.action",{}, 
		function(json)
		{
			jQuery( '#programIdAddPatient').append( '<option value="">' + i18n_view_all + '</option>' );
			for ( i in json.programs ) {
				if(json.programs[i].type==1){
					jQuery( '#programIdAddPatient').append( '<option value="' + json.programs[i].id +'" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
				}
			}
			enableBtn();
			hideById('programLoader');
			enable('programIdAddPatient');
		});
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// List && Search patients
// -----------------------------------------------------------------------------

function Patient()
{
	var patientId;
	var	fullName;
	
	this.advancedSearch = function(params)
	{
		$.ajax({
			url: 'searchRegistrationPatient.action',
			type:"POST",
			data: params,
			success: function( html ){
					setTableStyles();
					statusSearching = 1;
					setInnerHTML( 'listPatientDiv', html );
					showById('listPatientDiv');
					setFieldValue('listAll',false);
					jQuery( "#loaderDiv" ).hide();
				}
			});
	};
	
	this.remove = function( confirm_delete_patient )
	{
		removeItem( this.patientId, this.fullName, confirm_delete_patient, 'removePatient.action' );
	};
	
	this.add = function(params,isContinue)
	{
		$.ajax({
		  type: "POST",
		  url: 'addPatient.action',
		  data: params,
		  success: function(json) {
			if(json.response=='success')
			{
				var patientId = json.message.split('_')[0];
				var programId = getFieldValue('programIdAddPatient');
				var	dateOfIncident = jQuery('#patientForm [id=dateOfIncident]').val();
				var enrollmentDate = jQuery('#patientForm [id=enrollmentDate]').val();
						
				if( getFieldValue('programIdAddPatient')!='' && enrollmentDate != '')
				{
					jQuery.postJSON( "saveProgramEnrollment.action",
					{
						patientId: patientId,
						programId: programId,
						dateOfIncident: dateOfIncident,
						enrollmentDate: enrollmentDate
					}, 
					function( json ) 
					{    
						if(isContinue){
							jQuery("#patientForm :input").each( function(){
								if( $(this).attr('id') != "registrationDate" 
									&& $(this).attr('type') != 'button'
									&& $(this).attr('type') != 'submit' 
									&& $(this).attr('id') !='enrollmentDate' )
								{
									$(this).val("");
								}
							});
							$("#patientForm :input").attr("disabled", false);
							$("#patientForm").find("select").attr("disabled", false);
						}
						else{
							showPatientDashboardForm( patientId );
						}
					});
				}
				else if(isContinue){
						jQuery("#patientForm :input").each( function(){
							if( $(this).attr('id') != "registrationDate" 
								&& $(this).attr('type') != 'button'
								&& $(this).attr('type') != 'submit'  )
							{
								$(this).val("");
							}
						});
						$("#patientForm :input").attr("disabled", false);
						$("#patientForm").find("select").attr("disabled", false);
				}
				else
				{
					$("#patientForm :input").attr("disabled", false);
					$("#patientForm").find("select").attr("disabled", false);
					showPatientDashboardForm( patientId );
				}
			}
		  }
		 });
	}
}

Patient.listAll = function()
{
	jQuery('#loaderDiv').show();
	contentDiv = 'listPatientDiv';
	if( getFieldValue('programIdAddPatient')=='')
	{
		jQuery('#listPatientDiv').load('searchRegistrationPatient.action',{
				listAll:true
			},
			function(){
				setTableStyles();
				statusSearching = 0;
				showById('listPatientDiv');
				jQuery('#loaderDiv').hide();
			});
	}
	else 
	{
		jQuery('#listPatientDiv').load('searchRegistrationPatient.action',{
				listAll:false,
				searchByUserOrgunits: false,
				searchBySelectedOrgunit: true,
				programIds: getFieldValue('programIdAddPatient'),
				searchTexts: 'prg_' + getFieldValue('programIdAddPatient')
			},
			function(){
				setTableStyles();
				statusSearching = 0;
				showById('listPatientDiv');
				jQuery('#loaderDiv').hide();
			});
	}
}

function listAllPatient()
{
	jQuery('#loaderDiv').show();
	hideById('listPatientDiv');
	hideById('editPatientDiv');
	hideById('migrationPatientDiv');
	hideById('advanced-search');
	
	Patient.listAll();
}

function advancedSearch( params )
{
	var patient = new Patient();
	patient.advancedSearch( params );
}

// -----------------------------------------------------------------------------
// Remove patient
// -----------------------------------------------------------------------------

function removePatient( patientId, fullName, i18n_confirm_delete_patient )
{
	var patient = new Patient();
	patient.patientId = patientId;
	patient.fullName = fullName;
	patient.remove( i18n_confirm_delete_patient );
}

// -----------------------------------------------------------------------------
// Add Patient
// -----------------------------------------------------------------------------

function addPatient( isContinue )
{		
	var patient = new Patient();
	var params = 'programId=' + getFieldValue('programIdAddPatient') + '&' + getParamsForDiv('patientForm');
	patient.add(params);
    return false;
}

function showAddPatientForm()
{
	hideById('listPatientDiv');
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('migrationPatientDiv');
	
	jQuery('#loaderDiv').show();
	jQuery('#editPatientDiv').load('showAddPatientForm.action?programId=' + getFieldValue('programIdAddPatient')
		, function()
		{
			showById('editPatientDiv');
			jQuery('#loaderDiv').hide();
		});
	
}

function validateAddPatient( isContinue )
{	
	var params = "programId=" + getFieldValue('programIdAddPatient') + "&" + getParamsForDiv('patientForm');
	$("#patientForm :input").attr("disabled", true);
	$("#patientForm").find("select").attr("disabled", true);
	$.ajax({
		type: "POST",
		url: 'validatePatient.action',
		data: params,
		success: function(data){
			addValidationCompleted(data,isContinue);
		}
	});	
}

function addValidationCompleted( data, isContinue )
{
    var type = jQuery(data).find('message').attr('type');
	var message = jQuery(data).find('message').text();
	
	if ( type == 'success' )
	{
		removeDisabledIdentifier( );
		addPatient( isContinue );
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
		$("#patientForm").find("select").attr("disabled", false);
	}
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
	loadPatientList();
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
	if(statusSearching==2)
	{
		return;
	}
	else if( statusSearching == 0)
	{
		Patient.listAll();
	}
	else if( statusSearching == 1 )
	{
		validateAdvancedSearch();
	}
	else if( statusSearching == 3 )
	{
		showById('listPatientDiv');
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
	setFieldValue( 'programStageInstanceId', programStageInstanceId );
			
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
			var editDataEntryForm = getFieldValue('editDataEntryForm');
			if(editDataEntryForm=='true')
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
					if ( completed == 'false' ){
						disableCompletedButton(false);
					}
					else if( completed == 'true' ){
						disableCompletedButton(true);
					}
				}
				
				var linkedEvent = jQuery(".stage-object-selected").attr("linkedEvent");
				if( linkedEvent=='true' ) {
					blockEntryForm();
					disable('executionDate');
				}
				else{
					enable('executionDate');
				}
				$(window).scrollTop(200);
			}
			else
			{
				blockEntryForm();
				disable('executionDate');
				hideById('inputCriteriaDiv');
			}
			
			resize();
			hideLoader();
			hideById('contentDiv');
		} );
}
