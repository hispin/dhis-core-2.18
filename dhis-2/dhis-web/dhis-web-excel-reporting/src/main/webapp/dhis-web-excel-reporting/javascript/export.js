function organisationUnitSelected( orgUnits )
{	
	window.location.reload();
}

selection.setListenerFunction( organisationUnitSelected );

function getReportExcelsByGroup(){
	$.post("getReportExcelsByGroup.action",
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


function lastYear(){
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getListPeriodCompleted );
	request.send( 'getPeriods.action?mode=previous'); 
}

function nextYear(){
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getListPeriodCompleted );
	request.send( 'getPeriods.action?mode=next'); 
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

function generateReportExcel() {	

	//if(byId('advancedCheck').checked){
		
		//generateAdvancedReportExcel();
		
	//}else{
		$("#loading").showAtCenter( true );	
		$.post("generateReportExcel.action",{
		reportId:$('#report').val(),
		periodId:$('#period').val()
		},function(data){		
			window.location = "downloadExcelOutput.action";
			deleteDivEffect();
			$("#loading").hide();		
		},'xml');
	//}
}

function generateAdvancedReportExcel() {	

	$("#loading").showAtCenter( true );	
		$.post("generateAdvancedReportExcel.action",{
		reportId:$('#report').val(),
		periodId:$('#period').val(),
		organisationGroupId: byId('availableOrgunitGroups').value
		},function(data){		
			window.location = "downloadExcelOutput.action";
			deleteDivEffect();
			$("#loading").hide();		
		},'xml');
	
}

generic_type = '';

function validateGenerateReport(message) {

	setMessage(message);
	
	$.post("validateGenerateReport.action", {
		reportId:$("#report").val(),
		periodId:$("#period").val()
	},	function ( xmlObject ) {
			xmlObject = xmlObject.getElementsByTagName( 'message' )[0];
			var type = xmlObject.getAttribute( 'type' );
			
			if ( type == 'error' )
			{
				setMessage( xmlObject.firstChild.nodeValue );
			}
			else
			{
				if ( generic_type == 'preview' ) {
				
					openPreviewReport();
				}
				else {
					generateReportExcel();
				}
			}
	}, "xml");
}

function getNoSheetsOfReportExcel() {

	if ( $("#report").val() == '-1' ) {
		clearListById('sheetNoExcelFile');
	}
	else {
		$.post("getListSheet.action", {
			reportId:$("#report").val()
		}, 		function ( xmlObject ) {
				clearListById('sheetNoExcelFile');
				xmlObject = xmlObject.getElementsByTagName('sheets')[0];
				nodes	  = xmlObject.getElementsByTagName('sheet');
				for (var i = 0 ; i < nodes.length ; i++)
				{
					var id = nodes[i].getElementsByTagName('id')[0].firstChild.nodeValue;
					var name = nodes[i].getElementsByTagName('name')[0].firstChild.nodeValue;
					addOption('sheetNoExcelFile', name, id);
				}
		}, "xml");
	}
}

function openPreviewReport() {
	
	var reportId = $('#report').val();
	var periodId = $('#period').val();	
	var url = "openPreviewReport.action?reportId=" + reportId + "&periodId=" + periodId + "&sheetId=1";
	
	
	if ( byId('advancedCheck').checked ) {
		
		url = url + "&orgunitGroupId=" + byId('availableOrgunitGroups').value;
	}
	
	window.open( url, "_blank", "width=900,height=600,scrollbars=yes,menubar=yes,resizable=yes" );
}