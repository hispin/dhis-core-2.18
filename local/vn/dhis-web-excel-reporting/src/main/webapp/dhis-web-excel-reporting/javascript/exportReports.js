//=========================================
// EXPORT REPORT CATEGORY ACTION
//=========================================

/*
* Open Add DataElement Group
*/

function openAddDataElementGroups( id ) {

	exportReportId = id;
	getALLDataElementGroups();		
}

/*
* Get ALL DataElement Group
*/

function getALLDataElementGroups() {
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( getALLDataElementGroupsReceived );
	request.send( "getAllDataElementGroups.action" );

}

function getALLDataElementGroupsReceived( data ) {

	var availableDataElementGroups = document.getElementById('availableDataElementGroups');
	availableDataElementGroups.options.length = 0;
	var dataElementGroups = data.getElementsByTagName('dataElementGroups')[0].getElementsByTagName('dataElementGroup');
	
	for( var i = 0 ; i < dataElementGroups.length ; i++ ) {
	
		var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
		availableDataElementGroups.options.add(new Option(name, id));			
	}
	getReportDataElementGroups(exportReportId);
}
/*
* Get DataElement Order of Export Report
*/

function getExportReportDataElementGroups( id ) {
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getExportReportDataElementGroupsReceived );
	request.send( "getExportReport.action?id=" + id );	

}

function getExportReportDataElementGroupsReceived( data ) {

	var selectedDataElementGroups = document.getElementById('selectedDataElementGroups');
	selectedDataElementGroups.options.length = 0;
	var dataElementGroups = data.getElementsByTagName('dataElementGroups')[0].getElementsByTagName('dataElementGroup');
	
	for( var i = 0 ; i < dataElementGroups.length ; i++ ) {
	
		var id = dataElementGroups.item(i).getElementsByTagName('dataElementGroupId')[0].firstChild.nodeValue;
		var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
		selectedDataElementGroups.options.add(new Option(name, id));
	}
	
	var availableDataElementGroups = document.getElementById('availableDataElementGroups');
	var selectedDataElementGroups = document.getElementById('selectedDataElementGroups');
	
	for ( var i = 0 ; i < availableDataElementGroups.options.length ; i++ ) {
	
		for ( var j = 0 ; j < selectedDataElementGroups.options.length ; j++ ) {
		
			if ( availableDataElementGroups.options[i].value == selectedDataElementGroups.options[j].value ) {
			
				availableDataElementGroups.options[i] = null;
			}
		}
	}
	showPopupWindowById( "dataElementGroups", 550, 270 );
}

/*
*   Update Data Element Group Order
*/

function updateDataElementGroupOrder() {

	var url = "updateDataElementGroupOrder.action?exportReportId=" + exportReportId;
	var selectedDataElementGroups = document.getElementById('selectedDataElementGroups').options;
	
	for( var i = 0 ; i < selectedDataElementGroups.length ; i++ ) {
	
		url += "&dataElementGroupsId=" + selectedDataElementGroups[i].value
	}
	
	window.location = url;
}


function backupExportReport( id ) {

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
