
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showOlapURLDetails( id )
{
    var request = new Request();
    request.setResponseTypeXML( 'url' );
    request.setCallbackSuccess( olapURLReceived );
    request.send( 'getOlapURL.action?id=' + id );
}

function olapURLReceived( xmlObject )
{
    setFieldValue( 'nameField', getElementValue( xmlObject, 'name' ) );
    setFieldValue( 'urlField', getElementValue( xmlObject, 'url' ) );
    
    showDetails();
}

// -----------------------------------------------------------------------------
// Remove
// -----------------------------------------------------------------------------

function removeView( viewId )
{
	var dialog = confirm( i18n_confirm_remove_view );
	
	if ( dialog )
	{
		window.location.href = "deleteOlapURL.action?id=" + viewId;
	}
}

// -----------------------------------------------------------------------------
// Dashboard
// -----------------------------------------------------------------------------

function addToDashboard( id )
{
    var dialog = window.confirm( i18n_confirm_add_to_dashboard );
    
    if ( dialog )
    {
        var request = new Request(); 
        request.send( "addOlapUrlToDashboard.action?id=" + id );
    }
}
