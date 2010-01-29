// ========================================================================================================================
// EXCEL TEMPLATE MANAGER
// ========================================================================================================================

/*
*	Delete Excel Template
*/
function deleteExcelTemplate( fileName ) {

	if ( window.confirm(i18n_confirm_delete) ) {
	
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( deleteExcelTemplateReceived );
		request.send( "deleteExcelTemplate.action?fileName=" + fileName );
	}
}

function deleteExcelTemplateReceived( xmlObject ) {

	var type = xmlObject.getAttribute( 'type' );
	
	if ( type == 'error' )
	{
		setMessage( xmlObject.firstChild.nodeValue );
	}
	else
	{
		window.location.href = 'listAllExcelTemplates.action';
	}
}

function validateUploadExcelTemplate ( fileName, columnIndex ) {

    var list = byId( 'list' );
    
    var rows = list.getElementsByTagName( 'tr' );
    
    for ( var i = 0; i < rows.length; i++ )
    {
        var cell = rows[i].getElementsByTagName( 'td' )[columnIndex-1];
        var value = cell.firstChild.nodeValue;
		
        if ( value.toLowerCase().indexOf( fileName.toLowerCase() ) != -1 )
        {
            // file is existsing
			return window.confirm( i18n_confirm_override );
        }
    }
      
	// normally upload
	return true;
}