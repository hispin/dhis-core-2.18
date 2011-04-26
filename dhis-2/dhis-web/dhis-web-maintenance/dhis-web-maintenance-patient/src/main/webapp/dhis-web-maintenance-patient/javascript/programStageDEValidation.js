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
	jQuery.post( "addProgramStageDEValidation.action",
		{
			'programId': getFieldValue( 'programId' ),
			'description': getFieldValue( 'description' ),
			'leftProgramStageId': getFieldValue( 'leftProgramStageId' ),
			'leftDataElementId': getFieldValue( 'leftDataElementId' ),
			'operator': getFieldValue( 'operator' ),
			'rightProgramStageId': getFieldValue( 'rightProgramStageId' ),
			'rightDataElementId': getFieldValue( 'rightDataElementId' )
		},
		function(data){
			jQuery( "table.listTable tbody#list tr:last" ).after( data );
			jQuery( "table.listTable tbody tr" ).removeClass( "listRow listAlternateRow" );
			jQuery( "table.listTable tbody tr:odd" ).addClass( "listAlternateRow" );
			jQuery( "table.listTable tbody tr:even" ).addClass( "listRow" );
			jQuery( "table.listTable tbody" ).trigger("update");
		}
	);
}

// -----------------------------------------------------------------------------
// Remove Criteria
// -----------------------------------------------------------------------------

function removeProgramStageDEValidation( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'removeProgramStageDEValidation.action' );
}
