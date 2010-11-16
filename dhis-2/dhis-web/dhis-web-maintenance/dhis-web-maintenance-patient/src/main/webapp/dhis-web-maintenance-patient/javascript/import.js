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
	$.ajaxFileUpload
	(
		{
			url:'validateUploadExcelFile.action',
			secureuri:false,
			fileElementId:'upload',
			dataType: 'xml',
			success: function (data, status)
			{
				data = data.getElementsByTagName('message')[0]; 
				var type = data.getAttribute("type");
				if(type=='error'){                    
					showErrorMessage(data.firstChild.nodeValue);
				}else{
					byId('importingParam').submit();
				} 
			},
			error: function (data, status, e)
			{
			
			}
		}
	);
}