/*
*  Report category
*/
var reportId = "";
function openAddDataElementGroups(id){
	reportId = id;
	getALLDataElementGroups();		
}

function getALLDataElementGroups(){
	$.get("getAllDataElementGroups.action",{},
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
	},'xml');
}

function getReportDataElementGroups( id ){
	$.get("getReport.action",{id:id},		
	function(data){
		var selectedDataElementGroups = document.getElementById('selectedDataElementGroups');
		selectedDataElementGroups.options.length = 0;
		var dataElementGroups = data.getElementsByTagName('dataElementGroups')[0].getElementsByTagName('dataElementGroup');
		for(var i=0;i<dataElementGroups.length;i++){
			var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
			var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
			selectedDataElementGroups.options.add(new Option(name, id));
		}		
		var availableDataElementGroups = document.getElementById('availableDataElementGroups');
		for(var i=0;i<availableDataElementGroups.options.length;i++){
			for(var j=0;j<selectedDataElementGroups.options.length;j++){
				if(availableDataElementGroups.options[i].value==selectedDataElementGroups.options[j].value){
					availableDataElementGroups.options[i] = null;
				}
			}
		}
		setPositionCenter( 'dataElementGroups' );
		showDivEffect();
		$("#dataElementGroups").show();
	},'xml');	
}

function updateReportDataElementGroup(){
	var url = "updateDataElementGroup4ReportCategory.action?reportId=" + reportId;
	var selectedDataElementGroups = document.getElementById('selectedDataElementGroups').options;
	for(var i=0;i<selectedDataElementGroups.length;i++){
		url += "&dataElementGroupsId=" + selectedDataElementGroups[i].value
	}
	
	window.location = url;
}

// Sort data element

function sortDataElement(id){
	$.get("getReport.action",{id:id},		
	function(data){
		var selectedDataElementGroups = document.getElementById('availableDataElementGroups_1');
		selectedDataElementGroups.options.length = 0;
		var dataElementGroups = data.getElementsByTagName('dataElementGroups')[0].getElementsByTagName('dataElementGroup');
		for(var i=0;i<dataElementGroups.length;i++){
			var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
			var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
			selectedDataElementGroups.options.add(new Option(name, id));
		}	
	setPositionCenter( 'sortDataElementGroup' );
	showDivEffect();
	$("#sortDataElementGroup").show();
		getDataElementGroupById($("#availableDataElementGroups").val());		
		
	},'xml');	
}

function getDataElementGroupById(){
	var url = "getDataElementOrder.action?id=" + $("#availableDataElementGroups").val();
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( getDataElementGroupByIdReceived );
	request.send( url );	
}

function getDataElementGroupByIdReceived( datalement ){
	var html = "";
	var dataelEments = datalement.getElementsByTagName( "dataElement" );	
	for ( var i = 0; i < dataelEments.length; i++ )
    {
		
        var id = dataelEments[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var name = dataelEments[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;       
		html += "<li class='ui-state-default' name='dataElement' value='"+id+"'><span class='ui-icon ui-icon-arrowthick-2-n-s'></span>" + name + "</li>";
    }

	$("#sortable1").html(html);	
}

function updateSortedDataElement(){	
	var dataElements = document.getElementsByName('dataElement');
	var dataElementIds = new Array();
	for(var i=0;i<dataElements.length;i++){		
		dataElementIds.push(dataElements.item(i).value);
	}
	
	$.post("updateSortedDataElements.action",{
		id:$("#availableDataElementGroups").val(),
		dataElementIds:dataElementIds
	},function (data){
		setMessage(data.getElementsByTagName('message')[0].firstChild.nodeValue);
	},'xml');	
}




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
	var group = $("#group").val();
	
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
	url += "&group=" + group;
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
	var group = $("#group").val();
	
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
	url += "&group=" + group;
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



