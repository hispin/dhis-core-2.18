
var tableIndex;

function removeReportElement( report, id, index )
{
	tableIndex = index;
	
	var url = "removeReportElement.action?report=" + report + "&elementId=" + id;
	
	var request = new Request();
    request.setCallbackSuccess( removeReportElementReceived );
    request.send( url );
}
	
function removeReportElementReceived()
{
	document.getElementById("elementTable").deleteRow( tableIndex );
}

function removeChartElement( report, id, index )
{
	tableIndex = index;
	
	var url = "removeChartElement.action?report=" + report + "&elementId=" + id;
		
	var request = new Request();
    request.setCallbackSuccess( removeChartElementReceived );
    request.send( url );
}

function removeChartElementReceived()
{
	document.getElementById("elementTable").deleteRow( tableIndex );
}
	
function viewReport()
{
	document.getElementById("reportForm").action = "loadReport.action";
	document.getElementById("reportForm").submit();
}

function addDataElementToReport()
{
	document.getElementById("reportForm").action = "addDataElementToReport.action";
	document.getElementById("reportForm").submit();
}

function addDataElementToChart()
{
	document.getElementById("reportForm").action = "addDataElementToChart.action";
	document.getElementById("reportForm").submit();
}

function addIndicatorToReport()
{
	document.getElementById("reportForm").action = "addIndicatorToReport.action";
	document.getElementById("reportForm").submit();
}

function addIndicatorToChart()
{
	document.getElementById("reportForm").action = "addIndicatorToChart.action";
	document.getElementById("reportForm").submit();
}

function setDesignTemplate( report, designTemplate )
{
	var url = "setDesignTemplate.action?report=" + report + "&designTemplate=" + designTemplate;
	
	var request = new Request();
    request.send( url );
}

function setChartTemplate( report, chartTemplate )
{
	var url = "setChartTemplate.action?report=" + report + "&chartTemplate=" + chartTemplate;
	
	var request = new Request();
    request.send( url );
}

function addReport()
{
	document.getElementById("reportForm").action = "addReport.action";
	document.getElementById("reportForm").submit();
}

function loadReport( id )
{
	document.getElementById("report").value = id;
	document.getElementById("reportForm").action = "loadReport.action";
	document.getElementById("reportForm").submit();
}

function unloadReport()
{
	document.getElementById("reportForm").action = "unloadReport.action";
	document.getElementById("reportForm").submit();
}

function deleteReport( id )
{
	var ok = confirm(confirm_to_delete_report);
	
	if ( ok )
	{
		document.getElementById("report").value = id;
		document.getElementById("reportForm").action = "deleteReport.action";
		document.getElementById("reportForm").submit();
	}
}

function listReports()
{
	document.getElementById("reportForm").action = "listReports.action";
	document.getElementById("reportForm").submit();
}

function moveUpReportElement( id )
{
	document.getElementById("elementId").value = id;
	document.getElementById("reportForm").action = "moveUpReportElement.action";
	document.getElementById("reportForm").submit();
}

function moveDownReportElement( id )
{
	document.getElementById("elementId").value = id;
	document.getElementById("reportForm").action = "moveDownReportElement.action";
	document.getElementById("reportForm").submit();
}

function generateDesign()
{
	document.getElementById("reportForm").action = "generateDesign.action";
	document.getElementById("reportForm").submit();
}

function listDesigns()
{
	document.getElementById("reportForm").action = "loadDesigns.action";
	document.getElementById("reportForm").submit();
}

function previewReport()
{
	document.getElementById("reportForm").action = "previewReport.action";
	document.getElementById("reportForm").submit();
}

function generateReport()
{
	document.getElementById("reportForm").action = "generateReport.action";
	document.getElementById("reportForm").submit();
}


	
