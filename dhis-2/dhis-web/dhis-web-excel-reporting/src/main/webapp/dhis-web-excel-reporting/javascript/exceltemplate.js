// ========================================================================================================================
// EXCEL TEMPLATE MANAGER
// ========================================================================================================================

/*
*	Delete Excel Template
*/
function deleteExcelTemplate( fileName ) {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( deleteExcelTemplateReceived );
	request.send( "deleteExcelTemplate.action?fileName=" + fileName );
	
}

function deleteExcelTemplateReceived( xmlObject ) {

	var type = xmlObject.getAttribute( 'type' );
	
	if ( type == 'error' )
	{
		setMessage(xmlObject.firstChild.nodeValue);
	}
	else
	{
		window.location.href = 'excelTemplateList.action';
	}
}