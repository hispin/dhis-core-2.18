//=========================================
// REPORT EXCEL CORE ACTION
//=========================================

var strReportType = '';
var reportId;

/*
*	Open Add Report Excel Window
*/
function openAddReportExcel(){		
	byId("reportExcelAddUpdateButton").onclick = function (e) {validateAddReportExcel();};
	enable("name");	
	enable("reportType");
	$("#report").showAtCenter( true );
}

/*
*	Validate Add Report Excel
*/
function validateAddReportExcel(){
	
	strReportType = byId("reportType").value;
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( validateAddReportExcelReceived );
	var params = "name=" + byId("name").value;
		params += "&excel=" + byId("excelTemplateFile").value;
		params += "&reportType=" + byId("reportType").value;
		params += "&periodRow=" + byId("periodRow").value;
		params += "&periodCol=" + byId("periodColumn").value;
		params += "&organisationRow=" + byId("organisationRow").value;
		params += "&organisationCol=" + byId("organisationColumn").value;
		params += "&groupName=" + byId("group").value;
	request.sendAsPost(params);
	request.send( "validateAddReportExcel.action" );
	
	/* $.post("validateAddReportExcel.action",{
		name:$("#name").val(),
		excel:$("#excelTemplateFile").val(),
		reportType:$("#reportType").val(),
		periodRow:$("#periodRow").val(),
		periodCol:$("#periodColumn").val(),
		organisationRow:$("#organisationRow").val(),
		organisationCol:$("#organisationColumn").val(),
		groupName:$("#group").val()
	},function(xmlObject){
		var xmlObject = xmlObject.getElementsByTagName('message')[0];
		var type = xmlObject.getAttribute( 'type' );
		if(type=='error')
		{
			setMessage(xmlObject.firstChild.nodeValue);
		}else if(type=='success')
		{		
			addReportExcel();
		}
	},'xml');	*/
}

function validateAddReportExcelReceived(xmlObject){
	var type = xmlObject.getAttribute( 'type' );
	if(type=='error')
	{
		setMessage(xmlObject.firstChild.nodeValue);
	}else if(type=='success')
	{		
		addReportExcel();			
	}
}

/*
*	Add Report Excel
*/
function addReportExcel(){	
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( Completed );
	var params = "name=" + byId("name").value;
		params += "&excel=" + byId("excelTemplateFile").value;
		params += "&reportType=" + byId("reportType").value;
		params += "&periodRow=" + byId("periodRow").value;
		params += "&periodCol=" + byId("periodColumn").value;
		params += "&organisationRow=" + byId("organisationRow").value;
		params += "&organisationCol=" + byId("organisationColumn").value;
		params += "&group=" + byId("group").value;
	
	request.sendAsPost(params);
	request.send( "addReportExcel.action" );	

	
/*	$.post("addReportExcel.action",{		
		name:$("#name").val(),
		excel:$("#excelTemplateFile").val(),
		reportType:$("#reportType").val(),
		periodRow:$("#periodRow").val(),
		periodCol:$("#periodColumn").val(),
		organisationRow:$("#organisationRow").val(),
		organisationCol:$("#organisationColumn").val(),
		group:$("#group").val()
	},function(data){
		window.location.reload();
	},'xml');	*/
	
}

function Completed(xmlObject){
	window.location.reload();
}
/*
*	Delete Report Excel
*/
function deleteReportExcel( id ){
	if(window.confirm(i18n_confirm_delete)){
		window.location = "deleteReportExcel.action?id=" + id;
	}
}



/*
*	Open Update Report Excel Window
*/
function openUpdateReportReportExcel( id ){
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( openUpdateReportReportExcelReceived );
	request.send( "getReportExcel.action?id=" + id );
	
	/* 
	$.post("getReportExcel.action",{id:id},
	function ( xmlObject ){
		var report = xmlObject.getElementsByTagName('report')[0];
		reportId =  getElementValue(report, 'id') ;
		$("#name").val( getElementValue(report, 'name') );
		$("#excelTemplateFile").val( getElementValue(report, 'excelTemplateFile') );
		$("#periodRow").val( getElementValue(report, 'periodRow') );
		$("#periodColumn").val( getElementValue(report, 'periodColumn') );
		$("#organisationRow").val( getElementValue(report, 'organisationRow') );
		$("#organisationColumn").val( getElementValue(report, 'organisationColumn') );
		$("#reportType").val( getElementValue(report, 'reportType') );
		$("#group").val( getElementValue(report, 'group') );	
		
		$("#reportExcelAddUpdateButton").click( validateUpdateReportExcel );		
		$("#report").showAtCenter( true );	
		$("#name").attr("disabled", false);	
		$("#reportType").attr("disabled", true);
	},'xml');	*/
}

function openUpdateReportReportExcelReceived(xmlObject){
	reportId = getElementValue(xmlObject, 'id') ;
	byId("name").value = getElementValue(xmlObject, 'name') ;
	byId("excelTemplateFile").value = getElementValue(xmlObject, 'excelTemplateFile') ;
	byId("periodRow").value = getElementValue(xmlObject, 'periodRow') ;
	byId("periodColumn").value = getElementValue(xmlObject, 'periodColumn') ;
	byId("organisationRow").value = getElementValue(xmlObject, 'organisationRow') ;
	byId("organisationColumn").value = getElementValue(xmlObject, 'organisationColumn') ;
	byId("reportType").value = getElementValue(xmlObject, 'reportType') ;
	byId("group").value = getElementValue(xmlObject, 'group') ;	
	
	byId("reportExcelAddUpdateButton").onclick = function(e){ validateUpdateReportExcel();};		
	$("#report").showAtCenter( true );	
	enable("name");	
	disable("reportType");
}

/*
*	Validate Update Report Excel
*/
function validateUpdateReportExcel(){
	
	strReportType = byId("reportType").value;
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( validateUpdateReportExcelReceived );
	var params = "id=" + reportId;
		params += "&name=" + byId("name").value;
		params += "&excel=" + byId("excelTemplateFile").value;
		params += "&reportType=" + byId("reportType").value;
		params += "&periodRow=" + byId("periodRow").value;
		params += "&periodCol=" + byId("periodColumn").value;
		params += "&organisationRow=" + byId("organisationRow").value;
		params += "&organisationCol=" + byId("organisationColumn").value;
		params += "&groupName=" + byId("group").value;
	request.sendAsPost(params);
	request.send( "validateUpdateReportExcel.action" );
	
	
	/*$.post("validateUpdateReportExcel.action",{		
		id:reportId,
		name:$("#name").val(),
		excel:$("#excelTemplateFile").val(),
		reportType:$("#reportType").val(),
		periodRow:$("#periodRow").val(),
		periodCol:$("#periodColumn").val(),
		organisationRow:$("#organisationRow").val(),
		organisationCol:$("#organisationColumn").val(),
		group:$("#group").val()
	},function(xmlObject){
		var xmlObject = xmlObject.getElementsByTagName('message')[0];
		var type = xmlObject.getAttribute( 'type' );
		if(type=='error')
		{
			setMessage(xmlObject.firstChild.nodeValue);
		}else if(type=='success')
		{		
			updateReportExcel();			
		}
	},'xml');	*/
}

function validateUpdateReportExcelReceived (xmlObject){
	
	var type = xmlObject.getAttribute( 'type' );
	if(type=='error')
	{
		setMessage(xmlObject.firstChild.nodeValue);
	}else if(type=='success')
	{		
		updateReportExcel();			
	}
}

function updateReportExcel(){
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( Completed );
	var params = "id=" + reportId;
		params += "&name=" + byId("name").value;
		params += "&excel=" + byId("excelTemplateFile").value;
		params += "&periodRow=" + byId("periodRow").value;
		params += "&periodCol=" + byId("periodColumn").value;
		params += "&organisationRow=" + byId("organisationRow").value;
		params += "&organisationCol=" + byId("organisationColumn").value;
		params += "&group=" + byId("group").value;
	request.sendAsPost(params);
	
	request.send( "updateReportExcel.action" );
	
	
	/* $.post("updateReportExcel.action",{
		id:reportId,	
		name:$("#name").val(),
		excel:$("#excelTemplateFile").val(),		
		periodRow:$("#periodRow").val(),
		periodCol:$("#periodColumn").val(),
		organisationRow:$("#organisationRow").val(),
		organisationCol:$("#organisationColumn").val(),
		group:$("#group").val()
	},function(data){
		window.location.reload();
	},'xml');	*/
}



//=========================================
// REPORT EXCEL CATEGORY ACTION
//=========================================

/*
* Open Add DataElement Group
*/

function openAddDataElementGroups(id){
	reportId = id;
	getALLDataElementGroups();		
}

/*
* Get ALL DataElement Group
*/

function getALLDataElementGroups(){
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( getALLDataElementGroupsReceived );
	request.send( "getAllDataElementGroups.action" );
	
	
	/* $.get("getAllDataElementGroups.action",{},
	function(data){
		var availableDataElementGroups = document.getElementById('availableDataElementGroups');
		availableDataElementGroups.options.length = 0;
		var dataElementGroups = data.getElementsByTagName('dataElementGroups')[0].getElementsByTagName('dataElementGroup');
		for(var i=0;i<dataElementGroups.length;i++){
			var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
			var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
			availableDataElementGroups.options.add(new Option(name, id));			
		}
		getReportDataElementGroups(reportId);
	},'xml'); */
}

function getALLDataElementGroupsReceived( data ){
	var availableDataElementGroups = document.getElementById('availableDataElementGroups');
	availableDataElementGroups.options.length = 0;
	var dataElementGroups = data.getElementsByTagName('dataElementGroups')[0].getElementsByTagName('dataElementGroup');
	for(var i=0;i<dataElementGroups.length;i++){
		var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
		availableDataElementGroups.options.add(new Option(name, id));			
	}
	getReportDataElementGroups(reportId);
}
/*
* Get DataElement Order of Report Excel
*/

function getReportDataElementGroups( id ){
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getReportDataElementGroupsReceived );
	request.send( "getReportExcel.action?id=" + id );	
	
	
	/* $.get("getReportExcel.action",{id:id},		
	function(data){
		var selectedDataElementGroups = document.getElementById('selectedDataElementGroups');
		selectedDataElementGroups.options.length = 0;
		var dataElementGroups = data.getElementsByTagName('dataElementGroups')[0].getElementsByTagName('dataElementGroup');
		for(var i=0;i<dataElementGroups.length;i++){
			var id = dataElementGroups.item(i).getElementsByTagName('dataElementGroupId')[0].firstChild.nodeValue;
			var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
			selectedDataElementGroups.options.add(new Option(name, id));
		}		
		var availableDataElementGroups = document.getElementById('availableDataElementGroups');
		var selectedDataElementGroups = document.getElementById('selectedDataElementGroups');
		for(var i=0;i<availableDataElementGroups.options.length;i++){
			for(var j=0;j<selectedDataElementGroups.options.length;j++){				
				if(availableDataElementGroups.options[i].value==selectedDataElementGroups.options[j].value){					
					availableDataElementGroups.options[i] = null;
				}
			}
		}	
		$("#dataElementGroups").showAtCenter( true );
	},'xml');	*/
}

function getReportDataElementGroupsReceived(data){
	var selectedDataElementGroups = document.getElementById('selectedDataElementGroups');
	selectedDataElementGroups.options.length = 0;
	var dataElementGroups = data.getElementsByTagName('dataElementGroups')[0].getElementsByTagName('dataElementGroup');
	for(var i=0;i<dataElementGroups.length;i++){
		var id = dataElementGroups.item(i).getElementsByTagName('dataElementGroupId')[0].firstChild.nodeValue;
		var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
		selectedDataElementGroups.options.add(new Option(name, id));
	}		
	var availableDataElementGroups = document.getElementById('availableDataElementGroups');
	var selectedDataElementGroups = document.getElementById('selectedDataElementGroups');
	for(var i=0;i<availableDataElementGroups.options.length;i++){
		for(var j=0;j<selectedDataElementGroups.options.length;j++){				
			if(availableDataElementGroups.options[i].value==selectedDataElementGroups.options[j].value){					
				availableDataElementGroups.options[i] = null;
			}
		}
	}	
	$("#dataElementGroups").showAtCenter( true );
}

/*
*   Update Data Element Group Order
*/

function updateDataElementGroupOrder(){
	var url = "updateDataElementGroupOrder.action?reportId=" + reportId;
	var selectedDataElementGroups = document.getElementById('selectedDataElementGroups').options;
	for(var i=0;i<selectedDataElementGroups.length;i++){
		url += "&dataElementGroupsId=" + selectedDataElementGroups[i].value
	}
	
	window.location = url;
}


function backupReportExcel( id ){
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( backupReportExcelReceived );
	request.send( "backupReportExcel.action?id=" + id );	
	
	/* $.post("backupReportExcel.action",{id:id},function(data){
		window.location = "downloadFile.action?outputFormat=application/xml-external-parsed-entity";
	},'xml'); */
}

function backupReportExcelReceived(data){
	window.location = "downloadFile.action?outputFormat=application/xml-external-parsed-entity";
}

function restoreReportExcel(){
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( restoreReportExcelReceived );
	request.send( "restoreReportExcel.action");	
	
	/* $.post("restoreReportExcel.action",{},function(xmlObject){
		var xmlObject = xmlObject.getElementsByTagName('message')[0];
		var type = xmlObject.getAttribute( 'type' );
		if(type=='error')
		{
			setMessage(xmlObject.firstChild.nodeValue);
		}else if(type=='success')
		{		
			window.location.reload();	
		}
	},'xml');	*/
}

function restoreReportExcelReceived(xmlObject){
	var type = xmlObject.getAttribute( 'type' );
	if(type=='error')
	{
		setMessage(xmlObject.firstChild.nodeValue);
	}else if(type=='success')
	{		
		window.location.reload();	
	}
}