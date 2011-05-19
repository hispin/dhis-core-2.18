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