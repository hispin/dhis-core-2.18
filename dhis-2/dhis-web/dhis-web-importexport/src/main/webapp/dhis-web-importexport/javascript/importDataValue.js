
var pingTimeout = null;

function importDataValue()
{
	if ( !$( "#upload" ).val() )
	{
		setHeaderDelayMessage( "Please select a file to upload" );
		return false;
	}
	
	$( "#importForm" ).submit();
}

function pingNotificationsTimeout()
{
	pingNotifications( 'DATAVALUE_IMPORT', 'notificationTable', displayImportSummaryTimeout );	
	pingTimeout = setTimeout( "pingNotificationsTimeout()", 1500 );
}

function displayImportSummaryTimeout()
{
	setTimeout( "displayImportSummary()", 2000 );	
}

function displayImportSummary()
{	
	window.clearTimeout( pingTimeout );
	$( '#notificationDiv' ).hide();
	$( '#importSummaryDiv' ).show().load( 'getDataValueImportSummary.action' );
}