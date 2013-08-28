
function importEventData()
{
	if ( !$( "#upload" ).val() )
	{
		setHeaderDelayMessage( "Please select a file to upload" );
		return false;
	}

	$( "#notificationTable" ).empty();
	$( "#importForm" ).submit();
}
