//------------------------------------------------------------------------------
// Save Configuration
//------------------------------------------------------------------------------

function saveConfiguration()
{	
	var url = 'saveConfiguration.action?' +
			'fileName=' + getFieldValue( 'fileName' );
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( saveConfigurationCompleted );    
    request.send( url );        

    return false;
}

function saveConfigurationCompleted( messageElement )
{
    showSuccessMessage( i18n_save_successfull );
}