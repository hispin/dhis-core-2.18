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

function validateUploadExcelFile()
{
	jQuery( "#upload" ).upload( 'validateUploadExcelFile.action',
		function( data )
		{
			data = data.getElementsByTagName('message')[0]; 
			var type = data.getAttribute("type");
			
			if ( type=='error' ) showErrorMessage(data.firstChild.nodeValue);
			else byId('importingParam').submit();
		}, 'xml'
	);
}