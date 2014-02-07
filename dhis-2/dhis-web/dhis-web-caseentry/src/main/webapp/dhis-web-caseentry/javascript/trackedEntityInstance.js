function organisationUnitSelected( orgUnits, orgUnitNames )
{	
	showById('selectDiv');
	showById('searchDiv');
	showById( "programLoader" );
	disable('programIdAddEntityInstance');
	showById('mainLinkLbl');
	hideById('listEntityInstanceDiv');
	hideById('editEntityInstanceDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationEntityInstanceDiv');
	hideById('entityInstanceDashboard');
	enable('listEntityInstanceBtn');
	enable('addEntityInstanceBtn');
	enable('advancedSearchBtn');
	enable('searchObjectId');
	setInnerHTML('entityInstanceDashboard','');
	setInnerHTML('editEntityInstanceDiv','');
	
	setFieldValue("orgunitName", orgUnitNames[0]);
	
	clearListById('programIdAddEntityInstance');
	jQuery.get("getAllPrograms.action",{}, 
		function(json)
		{
			jQuery( '#programIdAddEntityInstance').append( '<option value="">' + i18n_view_all + '</option>' );
			for ( i in json.programs ) {
				if(json.programs[i].type==1){
					jQuery( '#programIdAddEntityInstance').append( '<option value="' + json.programs[i].id +'" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
				}
			}
			enableBtn();
			hideById('programLoader');
			enable('programIdAddEntityInstance');
		});
}

selection.setListenerFunction( organisationUnitSelected );

// -----------------------------------------------------------------------------
// List && Search entityInstances
// -----------------------------------------------------------------------------

function TrackedEntityInstance()
{
	var entityInstanceId;
	
	this.advancedSearch = function(params)
	{
		$.ajax({
			url: 'searchRegistrationTrackedEntityInstance.action',
			type:"POST",
			data: params,
			success: function( html ){
					setTableStyles();
					statusSearching = 1;
					setInnerHTML( 'listEntityInstanceDiv', html );
					showById('listEntityInstanceDiv');
					setFieldValue('listAll',false);
					showById('hideSearchCriteriaDiv');
					jQuery( "#loaderDiv" ).hide();
				}
			});
	};
	
	this.validate = function( programId )
	{
		setMessage('');
		if( jQuery('.underAge').prop('checked')=='true' ){
			if ( getFieldValue('representativeId') == '' )
			{
				setMessage( i18n_please_choose_representative_for_this_under_age_tracked_entity_instance );
				$("#entityInstanceForm :input").attr("disabled", false);
				$("#entityInstanceForm").find("select").attr("disabled", false);
				return false;
			}
			
			if ( getFieldValue('relationshipTypeId') == '' )
			{
				setMessage( i18n_please_choose_relationshipType_for_this_under_age_tracked_entity_instance );
				$("#entityInstanceForm :input").attr("disabled", false);
				$("#entityInstanceForm").find("select").attr("disabled", false);
				return false;
			}
		}
		
		var params = "";
		if( programId !== "undefined" ){
			params = "programId=" + programId + "&" 
		}
		params += getParamsForDiv('entityInstanceForm');
		$("#entityInstanceForm :input").attr("disabled", true);
		$("#entityInstanceForm").find("select").attr("disabled", true);
		var json = null;
		$.ajax({
			type: "POST",
			url: 'validateTrackedEntityInstance.action',
			data: params,
			datatype: "json",
			async: false,
			success: function(data) {
				json = data;
			}
		});

		var response = json.response;
		var message = json.message;
		
		if ( response == 'success' )
		{
			if( message == 0 ){
				return true;
			}
			else {
			
				if( message == 1 ){
					setMessage( i18n_adding_tracked_entity_instance_failed + ':' + '\n' + i18n_duplicate_identifier );
				}
				else if( message == 2 ){
					setMessage( i18n_adding_tracked_entity_instance_failed + ':' + '\n' + i18n_this_tracked_entity_instance_could_not_be_enrolled_please_check_validation_criteria );
				}
				
				$("#entityInstanceForm :input").attr("disabled", false);
				$("#entityInstanceForm").find("select").attr("disabled", false);
				return false;
			}
		}
		else
		{
			if ( response == 'error' )
			{
				setMessage( i18n_adding_tracked_entity_instance_failed + ':' + '\n' + message );
			}
			else if ( response == 'input' )
			{
				setMessage( message );
			}
			else if( response == 'duplicate' )
			{
				showListTrackedEntityInstanceDuplicate(data, false);
			}
			
			$("#entityInstanceForm :input").attr("disabled", false);
			$("#entityInstanceForm").find("select").attr("disabled", false);
			return false;
		}
	};
	
	this.add = function( programId, related, params, isContinue)
	{
		if( !this.validate(programId) ) return;
		
		$.ajax({
		  type: "POST",
		  url: 'addTrackedEntityInstance.action',
		  data: params,
		  success: function(json) {
			if(json.response=='success')
			{
				var entityInstanceUid = json.message.split('_')[0];
				var entityInstanceId = json.message.split('_')[1];
				var	dateOfIncident = jQuery('#entityInstanceForm [id=dateOfIncident]').val();
				var enrollmentDate = jQuery('#entityInstanceForm [id=enrollmentDate]').val();
				
				// Enroll entityInstance into the program
				if( programId && enrollmentDate )
				{
					jQuery.postJSON( "saveProgramEnrollment.action",
					{
						entityInstanceId: entityInstanceId,
						programId: programId,
						dateOfIncident: dateOfIncident,
						enrollmentDate: enrollmentDate
					}, 
					function( json ) 
					{    
						if(isContinue){
							jQuery("#entityInstanceForm :input").each( function(){
								var type = $(this).attr('type'),
									id = $(this).attr('id');
								
								if( type != 'button' && type != 'submit' && id != 'enrollmentDate' )
								{
									$(this).val("");
								}
							});
							$("#entityInstanceForm :input").prop("disabled", false);
							$("#entityInstanceForm").find("select").prop("disabled", false);
						}
						else{
							showTrackedEntityInstanceDashboardForm( entityInstanceUid );
						}
					});
				}
				else if(isContinue){
						jQuery("#entityInstanceForm :input").each( function(){
							var type = $(this).attr('type'),
								id = $(this).attr('id');
						
							if( type != 'button' && type != 'submit' && id != 'enrollmentDate' )
							{
								$(this).val("");
							}
						});
						$("#entityInstanceForm :input").prop("disabled", false);
						$("#entityInstanceForm").find("select").prop("disabled", false);
				}
				else
				{
					$("#entityInstanceForm :input").attr("disabled", false);
					$("#entityInstanceForm").find("select").attr("disabled", false);
					showTrackedEntityInstanceDashboardForm( entityInstanceUid );
				}
			}
		  }
		 });
	};
	
	this.update = function()
	{
		if( !this.validate(getFieldValue('programIdAddEntityInstance')) ) return;
		
		var params = 'programId=' + getFieldValue('programIdAddEntityInstance') 
		+ '&' + getParamsForDiv('editEntityInstanceDiv');
		$.ajax({
		  type: "POST",
		  url: 'updateTrackedEntityInstance.action',
		  data: params,
		  success: function( json ) {
				showTrackedEntityInstanceDashboardForm( getFieldValue('uid') );
				$("#entityInstanceForm :input").attr("disabled", false);
				$("#entityInstanceForm").find("select").attr("disabled", false);
		  }
		 });
	};
	
	this.remove = function( confirm_delete_tracked_entity_instance )
	{
		removeItem( this.entityInstanceId, "", confirm_delete_tracked_entity_instance, 'removeTrackedEntityInstance.action' );
	};
	
}

TrackedEntityInstance.listAll = function()
{
	jQuery('#loaderDiv').show();
	contentDiv = 'listEntityInstanceDiv';
	if( getFieldValue('programIdAddEntityInstance')=='')
	{
		jQuery('#listEntityInstanceDiv').load('searchRegistrationTrackedEntityInstance.action',{
				listAll:true
			},
			function(){
				setTableStyles();
				statusSearching = 0;
				showById('listEntityInstanceDiv');
				jQuery('#loaderDiv').hide();
			});
	}
	else 
	{
		jQuery('#listEntityInstanceDiv').load('searchRegistrationTrackedEntityInstance.action',{
				listAll:false,
				searchByUserOrgunits: false,
				searchBySelectedOrgunit: true,
				programId: getFieldValue('programIdAddEntityInstance'),
				searchTexts: 'prg_' + getFieldValue('programIdAddEntityInstance'),
				statusEnrollment: getFieldValue('statusEnrollment')
			},
			function(){
				setTableStyles();
				statusSearching = 0;
				showById('listEntityInstanceDiv');
				jQuery('#loaderDiv').hide();
			});
	}
	
}

function listAllTrackedEntityInstance()
{
	jQuery('#loaderDiv').show();
	hideById('listEntityInstanceDiv');
	hideById('editEntityInstanceDiv');
	hideById('migrationEntityInstanceDiv');
	hideById('advanced-search');
	showById('searchByIdTR');
	TrackedEntityInstance.listAll();
}

function advancedSearch( params )
{
	var entityInstance = new TrackedEntityInstance();
	entityInstance.advancedSearch( params );
}

// -----------------------------------------------------------------------------
// Remove entityInstance
// -----------------------------------------------------------------------------

function removeTrackedEntityInstance( entityInstanceId )
{
	var entityInstance = new TrackedEntityInstance();
	entityInstance.entityInstanceId = entityInstanceId;
	entityInstance.remove( i18n_confirm_delete_entityInstance );
}

// -----------------------------------------------------------------------------
// Add TrackedEntityInstance
// -----------------------------------------------------------------------------

function addTrackedEntityInstance( programId, related, isContinue )
{		
	var entityInstance = new TrackedEntityInstance();
	var params = 'programId=' + programId + '&' + getParamsForDiv('entityInstanceForm');
	entityInstance.add(programId,related,params, isContinue );
	registrationProgress = true;
    return false;
}

function updateTrackedEntityInstance()
{		
	var entityInstance = new TrackedEntityInstance();
	var params = getParamsForDiv('entityInstanceForm');
	entityInstance.update();
    return false;
}

function showAddTrackedEntityInstanceForm( entityInstanceId, programId, relatedProgramId, related )
{
	hideById('listEntityInstanceDiv');
	hideById('selectDiv');
	hideById('searchDiv');
	hideById('migrationEntityInstanceDiv');
	hideById('listRelationshipDiv');
	setInnerHTML('addRelationshipDiv','');
	setInnerHTML('entityInstanceDashboard','');
	
	jQuery('#loaderDiv').show();
	jQuery('#editEntityInstanceDiv').load('showAddTrackedEntityInstanceForm.action',
		{
			programId: programId,
			entityInstanceId: entityInstanceId,
			relatedProgramId: relatedProgramId,
			related: related
		}, function()
		{
			showById('editEntityInstanceDiv');
			showById('entityInstanceMamagementLink');
			if(related){
				setFieldValue('relationshipId',entityInstanceId);
			}
			jQuery('#loaderDiv').hide();
		});
	
}

// ----------------------------------------------------------------
// Click Back to main form
// ----------------------------------------------------------------

function onClickBackBtn()
{
	showById('mainLinkLbl');
	showById('selectDiv');
	showById('searchDiv');
	showById('listEntityInstanceDiv');
	
	hideById('editEntityInstanceDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('migrationEntityInstanceDiv');
	setInnerHTML('entityInstanceDashboard','');
	loadTrackedEntityInstanceList();
}

function loadTrackedEntityInstanceList()
{
	hideById('editEntityInstanceDiv');
	hideById('enrollmentDiv');
	hideById('listRelationshipDiv');
	hideById('addRelationshipDiv');
	hideById('dataRecordingSelectDiv');
	hideById('dataEntryFormDiv');
	hideById('migrationEntityInstanceDiv');
	setInnerHTML('entityInstanceDashboard','');
	setInnerHTML('editEntityInstanceDiv','');

	showById('mainLinkLbl');
	showById('selectDiv');
	showById('searchDiv');
	if(statusSearching==2)
	{
		return;
	}
	else if( statusSearching == 0)
	{
		TrackedEntityInstance.listAll();
	}
	else if( statusSearching == 1 )
	{
		validateAdvancedSearch();
	}
	else if( statusSearching == 3 )
	{
		showById('listTrackedEntityInstanceDiv');
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
			
			if(registrationProgress)
			{
				var reportDateToUse = selectedProgramStageInstance.attr('reportDateToUse');
				if(reportDateToUse != "undefined" && reportDateToUse!='' && $('#executionDate').val() == '' ){
					$('#executionDate').val(reportDateToUse);
					$('#executionDate').change();
				}
			}
			registrationProgress = false;
		
		} );
	
}
