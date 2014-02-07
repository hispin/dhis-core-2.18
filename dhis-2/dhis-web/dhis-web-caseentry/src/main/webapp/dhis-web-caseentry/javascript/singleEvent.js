
var _continue = false;

function orgunitSelected( orgUnits, orgUnitNames )
{	
	var width = jQuery('#programIdAddEntityInstance').width();
	jQuery('#programIdAddEntityInstance').width(width-30);
	showById( "programLoader" );
	disable('programIdAddEntityInstance');
	hideById('addNewDiv');
	organisationUnitSelected( orgUnits, orgUnitNames );
	clearListById('programIdAddEntityInstance');
	$.postJSON( 'singleEventPrograms.action', {}, function( json )
		{
			var count = 0;
			for ( i in json.programs ) {
				if( json.programs[i].type==2){
					jQuery( '#programIdAddEntityInstance').append( '<option value="' + json.programs[i].id +'" programStageId="' + json.programs[i].programStageId + '" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
					count++;
				}
			}
			
			if(count==0){
				jQuery( '#programIdAddEntityInstance').prepend( '<option value="" >' + i18n_none_program + '</option>' );
			}
			else if(count>1){
				jQuery( '#programIdAddEntityInstance').prepend( '<option value="" selected>' + i18n_please_select + '</option>' );
				enable('addEntityInstanceBtn');
			}
			
			enableBtn();
			hideById('programLoader');
			jQuery('#programIdAddEntityInstance').width(width);
			enable('programIdAddEntityInstance');
		});
}
selection.setListenerFunction( orgunitSelected );

function showAddTrackedEntityInstanceForm()
{
	hideById('dataEntryMenu');
	showById('eventActionMenu');
	showById('nextEventLink');
	hideById('contentDiv');
	hideById('searchDiv');
	hideById('advanced-search');
	setInnerHTML('addNewDiv','');
	setInnerHTML('dataRecordingSelectDiv','');
	jQuery('#loaderDiv').show();
	jQuery('#addNewDiv').load('showEventWithRegistrationForm.action',
		{
			programId: getFieldValue('programIdAddEntityInstance')
		}, function()
		{
			setInnerHTML('singleProgramName',jQuery('#programIdAddEntityInstance option:selected').text());	unSave = true;
			showById('singleProgramName');
			showById('addNewDiv');
			jQuery('#loaderDiv').hide();
		});
}

function showUpdateTrackedEntityInstanceForm( entityInstanceId )
{
	hideById('dataEntryMenu');
	showById('eventActionMenu');
	hideById('nextEventLink');
	setInnerHTML('singleProgramName',jQuery('#programIdAddEntityInstance option:selected').text());	
	showById('singleProgramName');
	setInnerHTML('addNewDiv','');
	unSave = false;
	showSelectedDataRecoding(entityInstanceId, getFieldValue('programIdAddEntityInstance'));
}

function addEventForEntityInstanceForm( divname )
{
	jQuery("#" + divname + " [id=checkDuplicateBtn]").click(function() {
		checkDuplicate( divname );
	});
}

function validateData()
{
	var params = "programId=" + getFieldValue('programIdAddEntityInstance') + "&" + getParamsForDiv('entityInstanceForm');
	$("#entityInstanceForm :input").attr("disabled", true);
	$("#entryForm :input").attr("disabled", true);
	$.ajax({
		type: "POST",
		url: 'validateTrackedEntityInstance.action',
		data: params,
		success: function( json ){
			var type = json.response;
			var message = json.message;
			
			if ( type == 'success' ){
				if( message == 0 ){
					addTrackedEntityInstance();
				}
				else if( message == 1 ){
					showErrorMessage( i18n_adding_tracked_entity_instance_failed + ':' + '\n' + i18n_duplicate_identifier );
				}
				else if( message == 2 ){
					showErrorMessage( i18n_adding_tracked_entity_instance_failed + ':' + '\n' + i18n_this_tracked_entity_instance_could_not_be_enrolled_please_check_validation_criteria );
				}
			}
			else{
				$("#entityInstanceForm :input").attr("disabled", true);
				if ( type == 'error' )
				{
					showErrorMessage( i18n_adding_tracked_entity_instance_failed + ':' + '\n' + message );
				}
				else if ( type == 'input' )
				{
					showWarningMessage( message );
				}
				else if( type == 'duplicate' )
				{
					showListTrackedEntityInstanceDuplicate(data, false);
				}
					
				$("#entityInstanceForm :input").attr("disabled", false);
			}
		}
    });	
}

function addTrackedEntityInstance()
{
	$.ajax({
		type: "POST",
		url: 'addTrackedEntityInstance.action',
		data: getParamsForDiv('entityInstanceForm'),
		success: function(json) {
			var entityInstanceId = json.message.split('_')[1];
			addData( getFieldValue('programIdAddEntityInstance'), entityInstanceId );
		}
     });
}

function addData( programId, entityInstanceId )
{		
	var params = "programId=" + getFieldValue('programIdAddEntityInstance');
		params += "&entityInstanceId=" + entityInstanceId;
		params += "&" + getParamsForDiv('entryForm');
		
	$.ajax({
		type: "POST",
		url: 'saveValues.action',
		data: params,
		success: function(json) {
			if( _continue==true )
			{
				$("#entityInstanceForm :input").attr("disabled", false);
				$("#entryForm :input").attr("disabled", false);
				jQuery('#entityInstanceForm :input').each(function()
				{
					var type=$( this ).attr('type');
					if(type=='checkbox'){
						this.checked = false;
					}
					if(type!='button'){
						$( this ).val('');
					}
					enable(this.id);
				});
				jQuery('#entryForm :input').each(function()
				{
					var type=$( this ).attr('type');
					if(type=='checkbox'){
						this.checked = false;
					}
					else if(type!='button'){
						$( this ).val('');
					}
				});
			}
			else
			{
				setInnerHTML('singleProgramName','');
				hideById('addNewDiv');
				if( getFieldValue('listAll')=='true'){
					listAllTrackedEntityInstance();
				}
				else{
					showById('searchDiv');
					showById('contentDiv');
				}
			}
			backEventList();
			showSuccessMessage( i18n_save_success );
		}
     });
    return false;
}

function showEntryFormDiv()
{
	hideById('singleEventForm');
	jQuery("#resultSearchDiv").dialog("close");
}

function backEventList()
{
	showById('dataEntryMenu');
	hideById('eventActionMenu');
	hideById('singleProgramName');
	showSearchForm();
	if( getFieldValue('listAll')=='true'){
		listAllTrackedEntityInstance();
	}
	hideById('backBtnFromEntry');
}

// --------------------------------------------------------
// Check an available person allowed to enroll a program
// --------------------------------------------------------

function validateAllowEnrollment( entityInstanceId, programId  )
{	
	jQuery.getJSON( "validateProgramEnrollment.action",
		{
			entityInstanceId: entityInstanceId,
			programId: programId
		}, 
		function( json ) 
		{    
			jQuery('#loaderDiv').hide();
			hideById('message');
			var type = json.response;
			if ( type == 'success' ){
				showSelectedDataRecoding(entityInstanceId, programId );
			}
			else if ( type == 'input' ){
				showWarningMessage( json.message );
			}
		});
}

function completedAndAddNewEvent()
{
	_continue=true;
	jQuery("#singleEventForm").submit();
}
