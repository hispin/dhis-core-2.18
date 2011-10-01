// -----------------------------------------------------------------------------
// DataMartExport details
// -----------------------------------------------------------------------------

function showDataMartExportDetails( id )
{
    jQuery.post( 'getDataMartExport.action', { id: id }, function ( json ) {
		setInnerHTML( "nameField", json.dataMartExport.name );
		setInnerHTML( "dataElementField", json.dataMartExport.dataElements );
		setInnerHTML( "indicatorField", json.dataMartExport.indicators );
		setInnerHTML( "organisationUnitField", json.dataMartExport.organisationUnits );
		setInnerHTML( "periodField", json.dataMartExport.periods );
		
		showDetails();
	});
}

// -----------------------------------------------------------------------------
// Export
// -----------------------------------------------------------------------------

function getExportStatus()
{
    jQuery.get( 'getExportStatus.action', {}, function ( json ) {
	
		var message = json.status.message;
		
		if ( json.status.running == "true" )
		{
			setWaitMessage( message );
			waitAndGetExportStatus( 2000 );
		}
		else setMessage( message );
	});
}

function waitAndGetExportStatus( millis )
{
	setTimeout( "getExportStatus();", millis );
}

function cancelExport()
{
	var url = "cancelExport.action";
		
	var request = new Request();  
    request.send( url );
}

function removeDatamartExport( exportId, exportName )
{
	removeItem( exportId, exportName, i18n_confirm_delete, 'removeDataMartExport.action' );
}