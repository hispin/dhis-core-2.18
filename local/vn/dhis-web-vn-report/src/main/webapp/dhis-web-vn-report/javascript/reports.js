function updateOrgUnitByReport(){
	
	var list = document.getElementById('selectedGroups').options;
	var selectedGroups = '';	
	for(var i=0; i< list.length; i++){
		selectedGroups = selectedGroups + "&selectedGroups=" + ( list[i].value );
	}
	
	var  id = document.getElementById('orgUnitId').value;
	
	var url = 'updateReportExcelGroupListing.action?id='+id + selectedGroups;	
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.send(url);	
}

var strReportType = '';

function validateAddReport(){
	var id = getFieldValue("id");
	var name = getFieldValue("name");
	var excel = getFieldValue("excelTemplateFile");
	var reportType = getFieldValue("reportType");
	strReportType = reportType;
	var periodRow = getFieldValue("periodRow");
	var periodCol = getFieldValue("periodColumn");
	var organsationRow = getFieldValue("organisationRow");
	var organsationCol = getFieldValue("organisationColumn");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateAddReportCompleted );
    request.send( 'validateReport.action?name=' + name +"&id=" + id + "&excel=" + excel + "&reportType=" + reportType );    
	
}

function validateAddReportCompleted( xmlObject ){
	var type = xmlObject.getAttribute( 'type' );
	
    if(type=='error')
    {
        setMessage(xmlObject.firstChild.nodeValue);
    }
    if(type=='success')
    {		
		if (mode == 'ADD') {
			addReport();
		}
		else {
			updateReport();
		}
    }
}

function addReport(){
	var name = getFieldValue("name");
	var excel = getFieldValue("excelTemplateFile");
	var reportType = getFieldValue("reportType");
	var periodRow = getFieldValue("periodRow");
	var periodCol = getFieldValue("periodColumn");
	var organsationRow = getFieldValue("organisationRow");
	var organsationCol = getFieldValue("organisationColumn");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( addReportCompleted );
	var url = 'addReportExcelForm.action?name=' + name;
	url += "&reportType=" + reportType;
	url += "&periodRow=" + periodRow;
	url += "&periodCol=" + periodCol;
	url += "&organisationRow=" + organsationRow;
	url += "&organisationCol=" + organsationCol;
	url += "&excel=" + excel;
    request.send( url );
}

function updateReport(){
	var id = getFieldValue("id");	
	var excel = getFieldValue("excelTemplateFile");
	var reportType = getFieldValue("reportType");
	var periodRow = getFieldValue("periodRow");
	var periodCol = getFieldValue("periodColumn");
	var organsationRow = getFieldValue("organisationRow");
	var organsationCol = getFieldValue("organisationColumn");
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( addReportCompleted );
	var url = 'updateReportExcelForm.action?id=' + id;
	url += "&reportType=" + reportType;
	url += "&periodRow=" + periodRow;
	url += "&periodCol=" + periodCol;
	url += "&organisationRow=" + organsationRow;
	url += "&organisationCol=" + organsationCol;
	url += "&excel=" + excel;
    request.send( url );
}

function addReportCompleted( xmlObject ){	
	window.location.reload();
}



function getDataElementByGroup( id ){

	var url = "../dhis-web-commons-ajax/getDataElements.action?id=" + id;

	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( getDataElementByGroupReceived );
	request.send( url );	
}

function getDataElementByGroupReceived( datalement ){
	var dataelEments = xmlObject.getElementsByTagName( "dataElement" );
	for ( var i = 0; i < dataelEments.length; i++ )
    {
        var id = dataelEments[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var name = indicators[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;            
    }
}
function getHeader(){
	//var html = "table
}



