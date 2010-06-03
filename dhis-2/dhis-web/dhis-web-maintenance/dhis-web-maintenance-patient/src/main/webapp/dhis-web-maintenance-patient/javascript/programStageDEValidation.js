//-----------------------------------------------------------------------------
// Add Programstage - DataElement Validation
//-----------------------------------------------------------------------------

function validateProgramStageDEValidation()
{
	var description = byId( 'description' ).value;
	var leftProgramStageId = getFieldValue( 'leftProgramStageId' );
	var	leftDataElementId = getFieldValue( 'leftDataElementId' );
	var	operator = getFieldValue( 'operator' );
	var	rightProgramStageId = getFieldValue( 'rightProgramStageId' );
	var	rightDataElementId = getFieldValue( 'rightDataElementId' );
	
	if( isEmpty(description) || isEmpty(leftProgramStageId) ||
		isEmpty(leftDataElementId) ||isEmpty(operator) ||
		isEmpty(rightProgramStageId) ||isEmpty(rightDataElementId) )
	{
		setMessage(i18n_fill_fields_in_programstage_dataelement_validation);
		return;
	}
	addProgramStageDEValidation();
}

function isEmpty(value){
	return (value == '') ? true: false;
}

//-----------------------------------------------------------------------------
// Add Programstage - DataElement Validation
//-----------------------------------------------------------------------------

function addProgramStageDEValidation()
{
	var params = 'description=' + getFieldValue( 'description' );
		params += '&leftProgramStageId=' + getFieldValue( 'leftProgramStageId' );
		params += '&leftDataElementId=' + getFieldValue( 'leftDataElementId' );
		params += '&operator=' + getFieldValue( 'operator' );
		params += '&rightProgramStageId=' + getFieldValue( 'rightProgramStageId' );
		params += '&rightDataElementId=' + getFieldValue( 'rightDataElementId' );

	var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( function(data){
			window.location.reload();
		} );
	
		request.sendAsPost( params );
		request.send( "addProgramStageDEValidation.action");
}

// -----------------------------------------------------------------------------
// Remove Criteria
// -----------------------------------------------------------------------------

function removeProgramStageDEValidation( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'removeProgramStageDEValidation.action' );
}
