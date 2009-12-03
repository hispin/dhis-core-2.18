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
	
	/* $.post("getReportExcelsByGroup.action",
    {
        group:$("#group").val()
    }, function( xmlObject ){       
        xmlObject = xmlObject.getElementsByTagName("reports")[0];
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
    }, "xml"); */
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
	
	
	/* $.post("generateReportExcel.action",{
	reportId:$('#report').val(),
	periodId:$('#period').val()
	},function(data){		
		window.location = "downloadFile.action";
		deleteDivEffect();
		$("#loading").hide();		
	},'xml'); */
	
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
	
	/* $.post("getALLReportExcelByGroup.action",
    {
        group:$("#group").val()
    }, function( xmlObject ){       
        xmlObject = xmlObject.getElementsByTagName("reports")[0];
		clearListById('report');
		var list = xmlObject.getElementsByTagName("report");
		for(var i=0;i<list.length;i++){
			var item = list[i];
			var id = item.getElementsByTagName('id')[0].firstChild.nodeValue;
			var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
			addOption('report',name,id);
		}
    }, "xml"); */
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
	
	/*	$.post("generateAdvancedReportExcel.action",{
		reportId:$('#report').val(),
		periodId:$('#period').val(),
		organisationGroupId: byId('availableOrgunitGroups').value
		},function(data){		
			window.location = "downloadFile.action";
			deleteDivEffect();
			$("#loading").hide();		
		},'xml'); */
	
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
