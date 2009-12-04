function organisationUnitSelected( orgUnits )
{	
	window.location.reload();
}

selection.setListenerFunction( organisationUnitSelected );

function getReportExcelsByGroup() {
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getReportExcelsByGroupReceived);
    request.sendAsPost("group=" + byId('group').value);
	request.send( "getReportExcelsByGroup.action");
	
}

function getReportExcelsByGroupReceived(xmlObject){
	clearListById('report');
	var list = xmlObject.getElementsByTagName("report");
	for(var i=0;i<list.length;i++){
		var item = list[i];
		var id = item.getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
		addOption('report',name,id);
		
		// selectedReport is a global variable
		if ( id == selectedReport) {
			byId('report').options[i].selected = true;
		}
	}
}

function getPeriodsByPeriodTypeName() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'getPeriodsByPeriodTypeDB.action');
}

function lastPeriod() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'previousPeriodsDB.action' ); 
}

function nextPeriod() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'nextPeriodsDB.action' ); 
}

function responseListPeriodReceived( xmlObject ) {

	clearListById('period');
	var nodes = xmlObject.getElementsByTagName('period');
	for ( var i = 0; i < nodes.length; i++ )
    {
        node = nodes.item(i);  
        var id = node.getElementsByTagName('id')[0].firstChild.nodeValue;
        var name = node.getElementsByTagName('name')[0].firstChild.nodeValue;
		addOption('period', name, id);
    }
}

function generateReportExcel() {
	
	$("#loading").showAtCenter( true );
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( generateReportExcelReceived );
	var params = "reportId=" + byId('report').value;
	params += "&periodId=" + byId('period').value;
	request.sendAsPost(params);
	request.send( 'generateReportExcel.action');
	
}

function generateReportExcelReceived(xmlObject){
	window.location = "downloadFile.action";
	deleteDivEffect();
	$("#loading").hide();
}

function getALLReportExcelByGroup(){

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getALLReportExcelByGroupReceived );
	request.send( 'getALLReportExcelByGroup.action?group=' + byId("group").value);
	
}

function getALLReportExcelByGroupReceived(xmlObject){
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

	$("#loading").showAtCenter( true );	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( generateReportExcelReceived );
	var params = "reportId=" + byId('report').value;
	params += "&periodId=" + byId('period').value;
	request.sendAsPost(params);
	request.send( 'generateAdvancedReportExcel.action');
	
}

generic_type = '';

function validateExportReport() {

	if ( byId("report").value == -1 ) {
	
		setMessage(i18n_select_report);
		return;
	}
	
	if ( generic_type == 'preview' ) {
	
		previewReport();
	}
	else if ( generic_type == 'normal' ) {
	
		generateReportExcel();
	}
	else {
	
		generateAdvancedReportExcel();
	}
}
