function organisationUnitSelected( orgUnits ){
	//window.location = "getExcelFileByOrganisationUnit.action";
	window.location = "getInformation.action";
	
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

function importData(){
	
	var reportId = document.getElementById('reportId').value;
	var upload = document.getElementById('uploadFileName').value;
	var periodId = document.getElementById('periodId').value;
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( Completed );
	url = 'importData.action?reportId='+reportId;
	url += '&uploadFileName='+ upload;
	url += '&periodId='+ periodId;
	
	request.send(url); 
}

function Completed( xmlObject ){
	
	if(document.getElementById('message') != null){
		document.getElementById('message').style.display = 'block';
		document.getElementById('message').innerHTML = xmlObject.firstChild.nodeValue;
	}
	
	//window.location.reload();
	
}
