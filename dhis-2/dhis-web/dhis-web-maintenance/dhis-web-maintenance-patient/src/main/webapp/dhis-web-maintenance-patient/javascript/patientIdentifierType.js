// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientIdentifierTypeDetails( patientIdentifierTypeId )
{
    var request = new Request();
    request.setResponseTypeXML( 'patientIdentifierType' );
    request.setCallbackSuccess( patientIdentifierTypeReceived );
    request.send( 'getPatientIdentifierType.action?id=' + patientIdentifierTypeId );
}

function patientIdentifierTypeReceived( patientIdentifierTypeElement )
{
	setInnerHTML( 'idField', getElementValue( patientIdentifierTypeElement, 'id' ) );
	setInnerHTML( 'nameField', getElementValue( patientIdentifierTypeElement, 'name' ) );	
    setInnerHTML( 'descriptionField', getElementValue( patientIdentifierTypeElement, 'description' ) );
    setInnerHTML( 'formatField', getElementValue( patientIdentifierTypeElement, 'format' ) );
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove Patient Identifier Type
// -----------------------------------------------------------------------------

function removePatientIdentifierType( patientIdentifierTypeId, name )
{
    removeItem( patientIdentifierTypeId, name, i18n_confirm_delete, 'removePatientIdentifierType.action' );
}

// -----------------------------------------------------------------------------
// Add Patient Identifier Type
// -----------------------------------------------------------------------------

function showAddPatientIdentifierTypeForm( )
{
	hideById('identifierTypeList');
	jQuery('#loaderDiv').show();
	jQuery('#editIdentifierTypeForm').load('showAddPatientIdentifierTypeForm.action',
	{
	}, function()
	{
		showById('editIdentifierTypeForm');
		jQuery('#loaderDiv').hide();
	});
}

function addPatientIdentifierType()
{	
	$.ajax({
		type: "POST",
		url: 'addPatientIdentifierType.action',
		data: getParamsForDiv('addPatientIdentifierTypeForm'),
		success: function( json ) {
			if( json.response == 'success')
			{
				onClickBackBtn();
			}
		}
	});
	
    return false;
}

// -----------------------------------------------------------------------------
// Update Patient Identifier Type
// -----------------------------------------------------------------------------

function showUpdatePatientIdentifierTypeForm( identifierTypeId )
{
	hideById('identifierTypeList');
	jQuery('#loaderDiv').show();
	jQuery('#editIdentifierTypeForm').load('showUpdatePatientIdentifierTypeForm.action',
	{
		id:identifierTypeId
	}, function()
	{
		showById('editIdentifierTypeForm');
		jQuery('#loaderDiv').hide();
	});
}

function updatePatientIdentifierType()
{	
	$.ajax({
		type: "POST",
		url: 'updatePatientIdentifierType.action',
		data: getParamsForDiv('updatePatientIdentifierTypeForm'),
		success: function( json ) {
			if( json.response == 'success')
			{
				onClickBackBtn();
			}
		}
	});
	
    return false;
}

// ------------------------------------------------------------------
// Click Back button
// ------------------------------------------------------------------

function onClickBackBtn()
{
	hideById('editIdentifierTypeForm');	
	jQuery('#loaderDiv').show();
	jQuery('#identifierTypeList').load('patientIdentifierTypeList.action',
	{
	}, function()
	{
		showById('identifierTypeList');
		jQuery('#loaderDiv').hide();
	});
}	

