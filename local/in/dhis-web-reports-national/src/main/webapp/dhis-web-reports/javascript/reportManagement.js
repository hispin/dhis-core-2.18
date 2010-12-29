// -----------------------------------------------------------------------------
// Report details form
// -----------------------------------------------------------------------------

function checkStartDate( dtStr )
{
	
	if( isDate( dtStr ) )
	{
		var splitDate = dtStr.split("-");
		var temDay = splitDate[2];
		if( parseInt( temDay,10 ) > 1 )
		{
			alert("Please select start day of the month");
			return false;
		}		
		return true;
	}
	else
	{
		return false;
	}
}

function checkEndDate( dtStr )
{
	if( isDate( dtStr ) )
	{
		var splitDate = dtStr.split("-");
		var temDay = splitDate[2];
		if( parseInt( temDay,10 ) < 30 )
		{
			alert("Please select end day of the month");
			return false;
		}		
		return true;
	}
	else
	{
		return false;
	}
}





function showReportDetails(reportId) {
	/* var request = new Request();
	request.setResponseTypeXML('report');
	request.setCallbackSuccess(reportRecieved);
	request.send('getReport.action?reportId=' + reportId); */
	
	$.post("getReport.action",
		{
			reportId : reportId
		},
		function (data)
		{
			reportRecieved(data);
		},'xml');
}

function reportRecieved(reportElement) {
	
	byId('idField').innerHTML = reportElement.getElementsByTagName( 'id' )[0].firstChild.nodeValue;

	byId('nameField').innerHTML = reportElement.getElementsByTagName( 'name' )[0].firstChild.nodeValue;

	byId('modelField').innerHTML = reportElement.getElementsByTagName( 'model' )[0].firstChild.nodeValue;

	byId('frequencyField').innerHTML = reportElement.getElementsByTagName( 'frequency' )[0].firstChild.nodeValue;

	byId('reportTypeField').innerHTML = reportElement.getElementsByTagName( 'reportType' )[0].firstChild.nodeValue;

	byId('excelTemplateField').innerHTML = reportElement.getElementsByTagName( 'exceltemplate' )[0].firstChild.nodeValue;

	byId('xmlTemplateField').innerHTML = reportElement.getElementsByTagName( 'xmltemplate' )[0].firstChild.nodeValue;

	showDetails();
}
// -----------------------------------------------------------------------------
// Delete Report
// -----------------------------------------------------------------------------

function removeReport(reportId, reportName) {
	var result = window.confirm(i18n_confirm_delete + '\n\n' + "Report Id ="
			+ reportId + '\n\n' + "Report Name =" + reportName);

	if (result) {
		window.location.href = 'delReport.action?reportId=' + reportId;
	}
}

// ----------------------------------------------------------------------
// Validation for Report Add & Update
// ----------------------------------------------------------------------

function validateAddReport() {
	/*
	 * var excelnameValue = document.getElementById( 'excelname' ).value; var
	 * xmlnameValue = document.getElementById( 'xmlname' ).value;
	 * 
	 * if( excelnameValue == null || excelnameValue == "" ) { //alert("Please
	 * enter Excel Template Name"); return false; document.getElementById(
	 * 'message' ).innerHTML = "Please Specify Excel Template Name";
	 * document.getElementById( 'message' ).style.display = 'block'; return
	 * false; } else if( xmlnameValue == null || xmlnameValue == "" ) {
	 * //alert("Please enter XML Template Name"); return false;
	 * document.getElementById( 'message' ).innerHTML = "Please Specify XML
	 * Template Name"; document.getElementById( 'message' ).style.display =
	 * 'block'; return false; }
	 */

	/* var request = new Request();
	request.setResponseTypeXML('message');
	request.setCallbackSuccess(addreportValidationCompleted);

	var requestString = 'validateReport.action?name='
			+ document.getElementById('name').value + '&excelnameValue='
			+ document.getElementById('excelname').value + '&xmlnameValue='
			+ document.getElementById('xmlname').value;

	request.send(requestString); */
	
	$.post("validateReport.action",
		{
			name : byId('name').value,
			excelnameValue : byId('excelname').value,
			xmlnameValue : byId('xmlname').value
		},
		function (data)
		{
			addreportValidationCompleted(data);
		},'xml');

	return false;
}

function addreportValidationCompleted(messageElement) {
	
	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if (type == 'success') {
		document.forms['addReportForm'].submit();
	} else if (type == 'input') {
		setMessage( messageElement.firstChild.nodeValue );
	}
}

function validateEditReport() {
    /*
	var excelnameValue = document.getElementById('excelname').value;
	var xmlnameValue = document.getElementById('xmlname').value;

	if (excelnameValue == null || excelnameValue == "") {
		// alert("Please enter Excel Template Name"); return false;
		document.getElementById('message').innerHTML = "Please Specify Excel Template Name";
		document.getElementById('message').style.display = 'block';
		return false;
	} else if (xmlnameValue == null || xmlnameValue == "") {
		// alert("Please enter XML Template Name"); return false;
		document.getElementById('message').innerHTML = "Please Specify XML Template Name";
		document.getElementById('message').style.display = 'block';
		return false;
	}
	*/
	
	/* var request = new Request();
	request.setResponseTypeXML('message');
	request.setCallbackSuccess(editreportValidationCompleted);

	var requestString = 'validateReport.action?name='
			+ document.getElementById('name').value + '&reportId='
			+ document.getElementById('reportId').value + '&excelnameValue='
			+ document.getElementById('excelname').value + '&xmlnameValue='
			+ document.getElementById('xmlname').value;

	request.send(requestString); */
	
	$.post("validateReport.action",
		{
			name : byId('name').value,
			reportId : byId('reportId').value,
			excelnameValue : byId('excelname').value,
			xmlnameValue : byId('xmlname').value
		},
		function (data)
		{
			editreportValidationCompleted(data);
		},'xml');

	return false;
}

function editreportValidationCompleted(messageElement) {
	//var type = messageElement.getAttribute('type');
	//var message = messageElement.firstChild.nodeValue;

	messageElement = messageElement.getElementsByTagName( "message" )[0];
	var type = messageElement.getAttribute( "type" );
	
	if (type == 'success') {
		document.forms['editReportForm'].submit();
	} else if (type == 'input') {
		//document.getElementById('message').innerHTML = message;
		//document.getElementById('message').style.display = 'block';
		setMessage( messageElement.firstChild.nodeValue );
	}
}


function getOUDetails(orgUnitIds)
{
    /* //var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;
	
    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsRecevied );
    //request.send( url );

    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds;
    request.sendAsPost( params );
    request.send( requestString ); */
	
	$.post("getOrgUnitDetails.action",
			{
				orgUnitId : orgUnitIds
			},
			function (data)
			{
				getOUDetailsRecevied(data);
			},'xml');

    getReports();
}

function getOUDetailsForOuWiseProgressReport(orgUnitIds)
{
    /* //var url = "getOrgUnitDetails.action?orgUnitId=" + orgUnitIds;

    var request = new Request();
    request.setResponseTypeXML( 'orgunit' );
    request.setCallbackSuccess( getOUDetailsRecevied );
    //request.send( url );

    var requestString = "getOrgUnitDetails.action";
    var params = "orgUnitId=" + orgUnitIds;
    request.sendAsPost( params );
    request.send( requestString ); */
	
	$.post("getOrgUnitDetails.action",
		{
			orgUnitId : orgUnitIds
		},
		function (data)
		{
			getOUDetailsRecevied(data);
		},'xml');

}

function getOUDetailsRecevied(xmlObject)
{
		
    var orgUnits = xmlObject.getElementsByTagName("orgunit");

    for ( var i = 0; i < orgUnits.length; i++ )
    {
        var id = orgUnits[ i ].getElementsByTagName("id")[0].firstChild.nodeValue;
        var orgUnitName = orgUnits[ i ].getElementsByTagName("name")[0].firstChild.nodeValue;
        var level = orgUnits[ i ].getElementsByTagName("level")[0].firstChild.nodeValue;
		alert( orgUnitName );
		
        document.reportForm.ouNameTB.value = orgUnitName;
        //document.reportForm.ouLevelTB.value = level;
    }    		
}

// ----------------------------------------------------------------------
// Get Periods
// ----------------------------------------------------------------------

function getPeriods() {
	var periodTypeList = document.getElementById('periodTypeId');
	var periodTypeId = periodTypeList.options[periodTypeList.selectedIndex].value;
	var availablePeriods = document.getElementById('availablePeriods');

	if (periodTypeId != "NA") 
	{
		/* var url = "getPeriods.action?id=" + periodTypeId;

		var request = new Request();
		request.setResponseTypeXML('period');
		request.setCallbackSuccess(getPeriodsReceived);
		request.send(url); */
		
		$.post("getPeriods.action",
			{
				id : periodTypeId
			},
			function (data)
			{
				getPeriodsReceived(data);
			},'xml');
			
	} else 
	{
		document.reportForm.generate.disabled=true;
		clearList(availablePeriods);
		clearList(reportsList);
	}
	var ouId = document.getElementById('ouIDTB').value;
	//var reportType = document.getElementById('reportTypeTB').value;
	 var reportType = document.reportForm.reportTypeNameTB.value;

	getReports(ouId, reportType);
}

function getPeriodsReceived(xmlObject) {
	var availablePeriods = document.getElementById("availablePeriods");

	clearList(availablePeriods);

	var periods = xmlObject.getElementsByTagName("period");
	
    if( periods.length > 0 )
    {
        document.reportForm.generate.disabled=false;
    }

	for ( var i = 0; i < periods.length; i++) {
		var id = periods[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var periodName = periods[i].getElementsByTagName("periodname")[0].firstChild.nodeValue;

		/* var option = document.createElement("option");
		option.value = id;
		option.text = periodName;
		availablePeriods.add(option, null); */
		
		$("#availablePeriods").append("<option value='"+ id +"'>" + periodName + "</option>");
	}
}

function getPeriodsForCumulative()
{
    //document.reportForm.generate.disabled=false;
    var periodTypeList = document.getElementById( "periodTypeId" );
    var periodTypeId = periodTypeList.options[ periodTypeList.selectedIndex ].value;
    var reportsList = document.getElementById( "reportList" );
	
    if ( periodTypeId != "NA" )
    {
        var ouId = document.reportForm.ouIDTB.value;
        var reportTypeName = document.reportForm.reportTypeNameTB.value;
  
        getReports(ouId, reportTypeName);
        document.reportForm.generate.disabled=false;
    }
    else
    {
    
        document.reportForm.generate.disabled=true;
        clearList( reportsList );
        jQuery("#startDate").val("");
        jQuery("#endDate").val("");
        document.reportForm.startDate = "";
        document.reportForm.endDate = " ";
    }
 
}

// ----------------------------------------------------------------------
// Get Reports
// ----------------------------------------------------------------------

//function getReports(ouId, reportType) 
function getReports(orgUnitIds, reportTypeName) 
{
	//alert("inside function getReports");
	var periodTypeList = document.getElementById('periodTypeId');
	var periodType = periodTypeList.options[periodTypeList.selectedIndex].value;
	// var autogenvalue = document.getElementById( 'autogen' ).value;
	
	//alert( periodType + "," + orgUnitIds + "," + reportTypeName );
	
	if ( periodType != "NA" && orgUnitIds != null && reportTypeName != "" ) 
	{
		
		var url = "getReports.action?periodType=" + periodType + "&ouId=" + orgUnitIds + "&reportType=" + reportTypeName;

		var request = new Request();
		request.setResponseTypeXML('report');
		request.setCallbackSuccess(getReportsReceived);
		request.send(url); 
		
		/*
		$.post("getReports.action",
			{
				periodType : periodType,
				ouId : ouId,
				reportType : reportType
			},
			function (data)
			{
				getReportsReceived(data);
			},'xml');
			*/
	}
}

function getReportsReceived(xmlObject) {
	var reportsList = document.getElementById("reportList");
	var orgUnitName = document.getElementById("ouNameTB");

	clearList(reportsList);

	var reports = xmlObject.getElementsByTagName("report");
	for ( var i = 0; i < reports.length; i++) {
		var id = reports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = reports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		var ouName = reports[i].getElementsByTagName("ouName")[0].firstChild.nodeValue;

		orgUnitName.value = ouName;

		/* var option = document.createElement("option");
		option.value = id;
		option.text = name;
		reportsList.add(option, null); */
		
		$("#reportList").append("<option value='"+ id +"'>" + name + "</option>");
	}
}
