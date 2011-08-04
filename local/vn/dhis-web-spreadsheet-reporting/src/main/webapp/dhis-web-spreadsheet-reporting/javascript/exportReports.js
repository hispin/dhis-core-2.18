function backupExportReport( id )
{
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( backupExportReportReceived );
	request.send( "backupExportReport.action?id=" + id );	

}

function backupExportReportReceived( data ) {

	window.location = "downloadFile.action?outputFormat=application/xml-external-parsed-entity";
}

function restoreExportReport() {
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( restoreExportReportReceived );
	request.send( "restoreExportReport.action");	

}

function restoreExportReportReceived( xmlObject ) {

	var type = xmlObject.getAttribute( 'type' );
	
	if ( type == 'error' )
	{
		setMessage( xmlObject.firstChild.nodeValue );
	}
	else if ( type == 'success' )
	{
		window.location.reload();	
	}
}

function getListExcelTemplate()
{
	var request = new Request();
    request.setResponseTypeXML( 'files' );
    request.setCallbackSuccess( getListExcelTemplateReceived );
	request.send( "getListExcelTemplateFile.action");	
}

function getListExcelTemplateReceived( files )
{
	var html = "<ul>";
	var excels = files.getElementsByTagName( "file");
	
	for( var i=0;i<excels.length;i++)
	{
		html += "<li onclick='selectExcelTemplate(this);'>" + excels[i].firstChild.nodeValue + "</li>";
	}
	
	html += "</ul>";
	
	byId("listExcelTemplate").innerHTML = html;
	showById( 'listExcelTemplate' );
}

function selectExcelTemplate( li )
{
	byId( 'excelTemplateFile' ).value = li.innerHTML;
	hideById( 'listExcelTemplate' );
}
