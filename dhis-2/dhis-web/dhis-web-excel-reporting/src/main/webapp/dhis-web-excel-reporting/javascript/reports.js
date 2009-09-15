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
	$("#report").showAtCenter( true );
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
		$("#report").showAtCenter( true );	
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
		$("#dataElementGroups").showAtCenter( true );
	},'xml');	
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

//===============================================
//  REPORT EXCEL ORGANISATION UNIT LISTING
//===============================================

/*
*   Update Data Element Group Order
*/









