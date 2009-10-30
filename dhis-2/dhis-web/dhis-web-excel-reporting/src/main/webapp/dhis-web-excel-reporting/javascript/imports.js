function organisationUnitSelected( orgUnits ){
	window.location = "getImportingParams.action";
}
selection.setListenerFunction( organisationUnitSelected );

function deleteExcelFile( name ){
	if(window.confirm(i18n_confirm_delete)){
		window.location = "deleteExcelFile.action?fileName=" + name;
	}
}
var fileName;
function viewData(){
	window.location = "viewExcelFileDataValue.action?fileName=" + fileName + "&reportId=" + document.getElementById("targetReport").value;
}

function openImportForm( name ){
	fileName = name;
	getALLReport();
	showDivEffect();
	setPositionCenter('importForm');
	showById('importForm');
}

function getALLReport(){
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getALLReportCompleted );
	request.send( "getALLReportAjax.action");
}

function getALLReportCompleted( xmlObject ){
	var reports = xmlObject.getElementsByTagName("report");
	var selectList = document.getElementById("targetReport");
	var options = selectList.options;
	options.length = 0;
	for(i=0;i<reports.length;i++){
		var id = reports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = reports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
}
function currentYear(){
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getListPeriodCompleted );
	request.send( 'getPeriod.action?mode=current'); 
}

function lastYear(){
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getListPeriodCompleted );
	request.send( 'getPeriod.action?mode=previous'); 
}

function nextYear(){
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getListPeriodCompleted );
	request.send( 'getPeriod.action?mode=next'); 
}

function getListPeriodCompleted( xmlObject ){
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

// -----------------------------------------------------------------------------
// IMPORT DATA FROM EXCEL FILE INTO DATABASE
// -----------------------------------------------------------------------------

function importData(){
	
	var excelItemGroupId = document.getElementById('excelItemGroupId').value;
	var upload = document.getElementById('uploadFileName').value;
	var periodId = document.getElementById('period').value;
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( Completed );
	
	// URL
	url = 'importData.action?excelItemGroupId='+excelItemGroupId;
	// USER choose reportItem
	var preview = byId('showValue').style.display;
	
	if(preview == 'block'){
		
		var excelItems = document.getElementsByName('excelItems');
		for(var i=0;i<excelItems.length;i++){
			if(excelItems[i].checked ){
				url +='&excelItemIds=' + excelItems[i].value;
			}
		}
	}
	
	url += '&uploadFileName='+ upload;
	url += '&periodId='+ periodId;
	request.send(url); 
}

function Completed( xmlObject ){
	
	if(document.getElementById('message') != null){
		document.getElementById('message').style.display = 'block';
		document.getElementById('message').innerHTML = xmlObject.firstChild.nodeValue;
	}
}

function getPreviewImportData(fileExcel){
	
	var request = new Request();
	
	request.setResponseTypeXML( 'xmlObject' );
	
	request.setCallbackSuccess( getReportItemValuesReceived );
	
	var excelItemGroupId = byId("excelItemGroupId").value;
	
	request.send( "previewData.action?excelItemGroupId=" + excelItemGroupId +"&uploadFileName=" + fileExcel);
}

function getReportItemValuesReceived( xmlObject ){
	
	byId('selectAll').checked = false;
	var availableDiv = byId('showValue');
	availableDiv.style.display = 'block';
	
	var availableObjectList = xmlObject.getElementsByTagName('excelItemValue');
	
	var myTable = byId('showExcelItemValues');
	var tBody = myTable.getElementsByTagName('tbody')[0];
	
	for(var i = byId("showExcelItemValues").rows.length; i > 1;i--)
	{
		byId("showExcelItemValues").deleteRow(i -1);
	}

	for(var i=0;i<availableObjectList.length;i++){
		
		// get values
		var reportItermValue = availableObjectList.item(i);
		// add new row
		var newTR = document.createElement('tr');
		// create new column
		var newTD2 = document.createElement('td');
		newTD2.innerHTML = reportItermValue.getElementsByTagName('name')[0].firstChild.nodeValue;
		// create new column
		var newTD3 = document.createElement('td');
		var value = reportItermValue.getElementsByTagName('value')[0].firstChild.nodeValue;
		newTD3.innerHTML = value;
		// create new column
		var newTD1 = document.createElement('td');
		var id = reportItermValue.getElementsByTagName('id')[0].firstChild.nodeValue;
		if(value!=0){
			newTD1.innerHTML= "<input type='checkbox' name='excelItems' id='excelItems' value='" + id + "'>" ;
		}
		
		newTR.appendChild (newTD1);
		newTR.appendChild (newTD2);
		newTR.appendChild (newTD3);
		// add row into the table
		tBody.appendChild(newTR);
	}
}
 
function selectAll(){
	 
	var select = byId('selectAll').checked;
	
	var reportItems = document.getElementsByName('excelItems');
	
	for(var i=0;i<reportItems.length;i++){
		reportItems[i].checked = select;
	 }
 }

// --------------------------------------------------------------------
// PERIOD TYPE
// --------------------------------------------------------------------

function getPeriodsByPeriodTypeName(excelItemGroupId) {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'getPeriods.action?excelItemGroupId=' + excelItemGroupId);
}

function responseListPeriodReceived( xmlObject ) {

	clearListById('period');
	var list = xmlObject.getElementsByTagName('period');
	for ( var i = 0; i < list.length; i++ )
    {
        item = list[i];  
        var id = item.getElementsByTagName('id')[0].firstChild.nodeValue;
        var name = item.getElementsByTagName('name')[0].firstChild.nodeValue;
		addOption('period', name, id);
    }
}

function lastPeriod() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'previousPeriods.action' ); 
}

function nextPeriod() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( responseListPeriodReceived );
	request.send( 'nextPeriods.action' ); 
}
