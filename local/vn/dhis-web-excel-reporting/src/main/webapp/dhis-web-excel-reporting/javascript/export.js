function organisationUnitSelected( orgUnits )
{	
	getExportReportsByGroup();	
}

selection.setListenerFunction( organisationUnitSelected );

function getExportReportsByGroup() {
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getExportReportsByGroupReceived );
    request.sendAsPost("group=" + byId('group').value);
	request.send( "getExportReportsByGroup.action");
	
}

function getExportReportsByGroupReceived( xmlObject ) {

	clearListById('exportReport');
	var list = xmlObject.getElementsByTagName("exportReport");
	
	for(var i=0;i<list.length;i++){
		var item = list[i];
		var id = item.getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
		addOption('exportReport',name,id);	
	}
	
	var selectedOrganisationUnit = null;
	
	try{
	
		selectedOrganisationUnit = xmlObject.getElementsByTagName('organisationUnit')[0].firstChild.nodeValue;	
		
		enable("group");
		enable("exportReport");
		enable("period");
		enable("generateExportReport");
		enable("previewButton");
		enable("nextPeriod");
		enable("lastPeriod");		
	
	}catch(e){
		disable("group");
		disable("exportReport");
		disable("period");
		disable("generateExportReport");
		disable("previewButton");
		disable("nextPeriod");
		disable("lastPeriod");		
	}
	
	byId("selectedOrganisationUnit").innerHTML = selectedOrganisationUnit; 
}

function getPreviousPeriod() 
{
	jQuery.postJSON('previousPeriodsGeneric.action', responseListPeriodReceived );	
}

function getNextPeriod() 
{
	jQuery.postJSON('nextPeriodsGeneric.action', responseListPeriodReceived );	
}

function responseListPeriodReceived( json ) 
{	
	clearListById('period');
	
	jQuery.each( json.periods, function(i, item ){
		addOption('period', item.name , i );
	});
	
}

function generateExportReport() {
	
	var exportReport = getFieldValue('exportReport');
	if(exportReport.length == 0){
		showErrorMessage( i18n_specify_export_report );
		return;
	}
	
	lockScreen();
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( generateExportReportReceived );
	
	var params = "exportReportId=" + byId('exportReport').value;
	params += "&periodIndex=" + byId('period').value;
	request.sendAsPost(params);
	request.send( 'generateExportReport.action');
	
}

function generateExportReportReceived( xmlObject ) {

	var type = xmlObject.getAttribute("type");
	
	if( type == "success" ) {
	
		window.location = "downloadFile.action";		
		unLockScreen();
	}
}

function getALLExportReportByGroup() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getALLExportReportByGroupReceived );
	request.sendAsPost( "group=" + byId("group").value );
	request.send( 'getALLExportReportByGroup.action');
	
}

function getALLExportReportByGroupReceived( xmlObject ) {

	clearListById('exportReport');
	var list = xmlObject.getElementsByTagName("exportReport");
	
	for(var i=0;i<list.length;i++){
		var item = list[i];
		var id = item.getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
		addOption('exportReport',name,id);
	}
}

function generateAdvancedExportReport() {	

	lockScreen();	
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( generateExportReportReceived );
	
	var params = "exportReportId=" + byId('exportReport').value;
	params += "&periodId=" + byId('period').value;
	params += "&organisationGroupId=" + byId("availableOrgunitGroups").value;
	request.sendAsPost(params);
	request.send( 'generateAdvancedExportReport.action');
	
}
