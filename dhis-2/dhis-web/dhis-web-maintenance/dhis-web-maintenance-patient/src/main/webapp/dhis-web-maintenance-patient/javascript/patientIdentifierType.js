// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientIdentifierTypeDetails( patientIdentifierTypeId )
{
	$.ajax({
		url: 'getPatientIdentifierType.action?id=' + patientIdentifierTypeId,
		cache: false,
		dataType: "xml",
		success: patientIdentifierTypeReceived
	});
}

function patientIdentifierTypeReceived( patientIdentifierTypeElement )
{
	setInnerHTML( 'idField', $( patientIdentifierTypeElement).find('id' ).text() );
	setInnerHTML( 'nameField', $( patientIdentifierTypeElement).find('name' ).text() );	
    setInnerHTML( 'descriptionField', $( patientIdentifierTypeElement).find('description' ).text() );
	
	var boolValueMap = { 'true':i18n_yes, 'false':i18n_no };
    var boolType = $( patientIdentifierTypeElement ).find('mandatory').text();    
    setInnerHTML( 'mandatoryField', boolValueMap[boolType] );
	
	boolType = $( patientIdentifierTypeElement ).find('related').text(); 
	setInnerHTML( 'relatedField', boolValueMap[boolType] );
	setInnerHTML( 'noCharsField', $( patientIdentifierTypeElement).find('noChars' ).text() );
	
	var valueTypeMap = { 'text':i18n_string, 'number':i18n_number, 'letter':i18n_alphabet };
    var valueType = $( patientIdentifierTypeElement ).find('type' ).text();    
	setInnerHTML( 'typeField', valueTypeMap[valueType] );
	
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove Patient Identifier Type
// -----------------------------------------------------------------------------

function removePatientIdentifierType( patientIdentifierTypeId, name )
{
    removeItem( patientIdentifierTypeId, name, i18n_confirm_delete, 'removePatientIdentifierType.action' );
}