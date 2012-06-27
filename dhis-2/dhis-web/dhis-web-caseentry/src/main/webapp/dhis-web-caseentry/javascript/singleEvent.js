function orgunitSelected( orgUnits, orgUnitNames )
{	
	organisationUnitSelected( orgUnits, orgUnitNames );
	clearListById('programIdAddPatient');
	$.postJSON( 'singleEventPrograms.action', {}, function( json )
		{
			jQuery( '#programIdAddPatient').append( '<option value="" programStageId="">[' + i18n_please_select + ']</option>' );
			for ( i in json.programs ) {
				jQuery( '#programIdAddPatient').append( '<option value="' + json.programs[i].id +'" programStageId="' + json.programs[i].programStageId + '" type="' + json.programs[i].type + '">' + json.programs[i].name + '</option>' );
			}
		});
}
selection.setListenerFunction( orgunitSelected );

function showAddPatientForm()
{
	hideById('contentDiv');
	hideById('searchDiv');
	hideById('advanced-search');
	
	jQuery('#loaderDiv').show();
	jQuery('#addNewDiv').load('showEventWithRegistrationForm.action',
		{
			programId: getFieldValue('programIdAddPatient')
		}, function()
		{
			showById('addNewDiv');
			showById('entryForm');
			jQuery('#loaderDiv').hide();
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

function validateData()
{
	addPatient();
}

function addPatient()
{
	$.ajax({
		type: "POST",
		url: 'addPatient.action',
		data: getParamsForDiv('editPatientDiv'),
		success: function(json) {
			var patientId = json.message.split('_')[0];
			addData( getFieldValue('programIdAddPatient'), patientId )
		}
     });
}

function addData( programId, patientId )
{		
	var params = "programId=" + getFieldValue('programIdAddPatient');
		params += "&patientId=" + patientId;
		params += "&" + getParamsForDiv('dataForm');
		
	$.ajax({
		type: "POST",
		url: 'saveValues.action',
		data: params,
		success: function(json) {
			showSuccessMessage( i18n_save_success );
		  }
     });
    return false;
}

