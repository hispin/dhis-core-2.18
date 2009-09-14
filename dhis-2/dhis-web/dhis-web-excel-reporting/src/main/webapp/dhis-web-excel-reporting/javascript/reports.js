//=========================================
// REPORT EXCEL CORE ACTION
//=========================================

var strReportType = '';
var reportId;

/*
*	Open Add Report Excel Window
*/
function openAddReportExcel(){		
	$("#reportExcelAddUpdateButton").click(validateAddReportExcel);
	$("#name").attr("disabled", false);	
	$("#reportType").attr("disabled", false);
	showDivEffect();
	$("#report").showAtCenter();
}

/*
*	Validate Add Report Excel
*/
function validateAddReportExcel(){
	strReportType = $("#reportType").val();
	$.post("validateAddReportExcel.action",{		
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
			addReportExcel();			
		}
	},'xml');	
}

/*
*	Add Report Excel
*/
function addReportExcel(){	
	$.post("addReportExcel.action",{		
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
	},'xml');	
	
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
		showDivEffect();
		$("#report").showAtCenter();	
		$("#name").attr("disabled", false);	
		$("#reportType").attr("disabled", true);
	},'xml');	
}

/*
*	Validate Update Report Excel
*/
function validateUpdateReportExcel(){
	strReportType = $("#reportType").val();
	$.post("validateUpdateReportExcel.action",{		
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
	},'xml');	
}

function updateReportExcel(){
	$.post("updateReportExcel.action",{
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
	},'xml');	
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

/*
* Get DataElement Order of Report Excel
*/

function getReportDataElementGroups( id ){
	$.get("getReportExcel.action",{id:id},		
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
		showDivEffect();
		$("#dataElementGroups").showAtCenter();
	},'xml');	
}

/*
* Update Data Element Group Order
*/

function updateDataElementGroupOrder(){
	var url = "updateDataElementGroupOrder.action?reportId=" + reportId;
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



