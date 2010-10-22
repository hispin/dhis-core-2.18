function organisationUnitSelected( orgUnits )
{	
	getReportExcelsByGroup();	
}

selection.setListenerFunction( organisationUnitSelected );

function getReportExcelsByGroup() {
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getReportExcelsByGroupReceived );
    request.sendAsPost("group=" + byId('group').value);
	request.send( "getReportExcelsByGroup.action");
	
}

function getReportExcelsByGroupReceived( xmlObject ) {

	clearListById('report');
	var list = xmlObject.getElementsByTagName("report");
	
	for(var i=0;i<list.length;i++){
		var item = list[i];
		var id = item.getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
		addOption('report',name,id);	
	}
	
	var selectedOrganisationUnit = null;
	
	try{
	
		selectedOrganisationUnit = xmlObject.getElementsByTagName('organisationUnit')[0].firstChild.nodeValue;	
		
		enable("group");
		enable("report");
		enable("period");
		enable("generate_report");
		enable("previewButton");
		enable("nextPeriod");
		enable("lastPeriod");		
	
	}catch(e){
		disable("group");
		disable("report");
		disable("period");
		disable("generate_report");
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

function generateReportExcel() {
	
	var report = getFieldValue('report');
	if(report.length == 0){
		showErrorMessage( i18n_specify_report );
		return;
	}
	
	lockScreen();
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( generateReportExcelReceived );
	
	var params = "reportId=" + byId('report').value;
	params += "&periodIndex=" + byId('period').value;
	request.sendAsPost(params);
	request.send( 'generateReportExcel.action');
	
}

function generateReportExcelReceived( xmlObject ) {

	var type = xmlObject.getAttribute("type");
	
	if( type == "success" ) {
	
		window.location = "downloadFile.action";		
		unLockScreen();
	}
}

function getALLReportExcelByGroup() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getALLReportExcelByGroupReceived );
	request.sendAsPost( "group=" + byId("group").value );
	request.send( 'getALLReportExcelByGroup.action');
	
}

function getALLReportExcelByGroupReceived( xmlObject ) {

	clearListById('report');
	var list = xmlObject.getElementsByTagName("report");
	
	for(var i=0;i<list.length;i++){
		var item = list[i];
		var id = item.getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
		addOption('report',name,id);
	}
}

function generateAdvancedReportExcel() {	

	lockScreen();	
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( generateReportExcelReceived );
	
	var params = "reportId=" + byId('report').value;
	params += "&periodId=" + byId('period').value;
	params += "&organisationGroupId=" + byId("availableOrgunitGroups").value;
	request.sendAsPost(params);
	request.send( 'generateAdvancedReportExcel.action');
	
}
