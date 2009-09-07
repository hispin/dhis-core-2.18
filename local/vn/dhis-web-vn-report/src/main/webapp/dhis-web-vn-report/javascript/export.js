function getReportByGroup(){
	$.get("getReportsByGroup.action",
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
    }, "xml");
}

function organisationUnitSelected( orgUnits )
{	
	window.location.reload();
}
function organisationUnitSelectedCompleted(xmlObject){
	setFieldValue('organisation',getElementValue(xmlObject, 'name'));
}

selection.setListenerFunction( organisationUnitSelected );

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
function validateGenerateReport(){
	var reportId = getFieldValue('report');
	var periodId = getFieldValue('period');

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validateGenerateReportCompleted );
	request.send( "validateGenerateReport.action?reportId=" + reportId + "&periodId=" + periodId); 
}
function validateGenerateReportCompleted( xmlObject ){
	var type = xmlObject.getAttribute( 'type' );
    
    if(type=='error')
    {
        setMessage(xmlObject.firstChild.nodeValue);
    }
    if(type=='success')
    {
		generateReport();
	}
}
function generateReport(){
	
	
	
	var reportId = getFieldValue('report');
	var periodId = getFieldValue('period');
	
	window.location = "generateReport.action?reportId=" + reportId + "&periodId=" + periodId;

	//var request = new Request();
	//request.setResponseTypeXML( 'xmlObject' );
	//request.setCallbackSuccess( generateReportCompleted );
	//request.send( "generateReport.action?reportId=" + reportId + "&periodId=" + periodId); 
}
function generateReportCompleted( xmlObject ){
}
