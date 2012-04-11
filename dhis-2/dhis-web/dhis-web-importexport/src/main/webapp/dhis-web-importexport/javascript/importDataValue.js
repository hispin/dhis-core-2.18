
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
	pingNotifications( 'DATAVALUE_IMPORT', 'notificationTable' );
	setTimeout( "pingNotificationsTimeout()", 2500 );
}
