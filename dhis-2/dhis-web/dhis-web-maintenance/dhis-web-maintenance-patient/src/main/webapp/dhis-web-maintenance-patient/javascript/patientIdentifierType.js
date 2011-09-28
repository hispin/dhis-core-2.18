// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showPatientIdentifierTypeDetails( patientIdentifierTypeId )
{
	jQuery.post( 'getPatientIdentifierType.action', { id: patientIdentifierTypeId },
		function ( json ) {
			setInnerHTML( 'idField', json.patientAttributeType.id );
			setInnerHTML( 'nameField', json.patientAttributeType.name );	
			setInnerHTML( 'descriptionField', json.patientAttributeType.description );
			
			var boolValueMap = { 'true':i18n_yes, 'false':i18n_no };
			var boolType = json.patientAttributeType.mandatory;
			setInnerHTML( 'mandatoryField', boolValueMap[boolType] );
			
			boolType = json.patientAttributeType.related;
			setInnerHTML( 'relatedField', boolValueMap[boolType] );
			setInnerHTML( 'noCharsField', json.patientAttributeType.noChars );
			
			var valueTypeMap = { 'text':i18n_string, 'number':i18n_number, 'letter':i18n_alphabet };
			var valueType = json.patientAttributeType.type;
			setInnerHTML( 'typeField', valueTypeMap[valueType] );
			
			showDetails();
	});
}

// -----------------------------------------------------------------------------
// Remove Patient Identifier Type
// -----------------------------------------------------------------------------

function removePatientIdentifierType( patientIdentifierTypeId, name )
{
    removeItem( patientIdentifierTypeId, name, i18n_confirm_delete, 'removePatientIdentifierType.action' );
}