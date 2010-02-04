// ========================================================================================================================
// EXCEL TEMPLATE MANAGER
// ========================================================================================================================

//----------------------------------------------------------
// Regular Expression using for checking excel's file name
//----------------------------------------------------------

regPattern = /^[^0-9\s\W][a-zA-Z0-9]{5,30}\.xl(?:sx|s)$/;

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

function openEditExcelTemplate() {
	
	$("#editExcelTemplateDiv").showAtCenter( true );
}

function validateRenamingExcelTemplate( fileName, columnIndex ) {
	
	var list = byId( 'list' );
	var rows = list.getElementsByTagName( 'tr' );
	
	for ( var i = 0 ; i < rows.length ; i++ )
	{
		var cell = rows[i].getElementsByTagName( 'td' )[columnIndex -1];
		var value = cell.firstChild.nodeValue;
		
		if ( value.toLowerCase() == fileName.toLowerCase() )
		{
			setMessage( i18n_file_exists );
			disable( "excelTemplateButtonRename" );
		}
		else if ( applyingPatternForFileName( fileName ) == null )
		{
			setMessage
			(
				'<b>' + i18n_filename_wellformed  + '</b><ul><li>'
				+ i18n_length_filename_min5_max30 + '</li><li>'
				+ i18n_use_only_letters_numbers_dot_only + '</li>'
			);
			disable( "excelTemplateButtonRename" );
		}
		else
		{
			hideMessage();
			enable( "excelTemplateButtonRename" );
		}
	}
}

function applyingPatternForFileName( fileName ) {
	
	return fileName.match( regPattern );
}