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

	if(byId('advancedCheck').checked){
		
		generateAdvancedReportExcel();
		
	}else{
		$("#loading").showAtCenter( true );	
		$.post("generateReportExcel.action",{
		reportId:$('#report').val(),
		periodId:$('#period').val()
		},function(data){		
			window.location = "downloadExcelOutput.action";
			deleteDivEffect();
			$("#loading").hide();		
		},'xml');
	}
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

function openGenerateAdvance(  ){		
		
	if(byId('advancedCheck').checked)
	{
		var availableList = byId('availableOrgunitGroups');
		
		if(availableList.options.length == 0){
			
			$.get("organisationUnitGroupList.action", 
				{}, 
				function (data){
				
					var xmlObject = data.getElementsByTagName('organisationUnitGroups')[0];
					var availableObjectList = xmlObject.getElementsByTagName('organisationUnitGroup');
		
					for(var i=0;i<availableObjectList.length;i++){
						var element = availableObjectList.item(i);
						var name = element.getElementsByTagName('id')[0].firstChild.nodeValue;
						var label = element.getElementsByTagName('name')[0].firstChild.nodeValue;
						availableList.add(new Option(label, name),null);
					}
				},'xml'); 
		}

		byId('availableOrgunitGroups').disabled = false;
	
	}else{
		byId('availableOrgunitGroups').disabled = true;
	}
			
	
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

	window.open("openPreviewReport.action?reportId=" + reportId + "&periodId=" + periodId, "_blank", "width=900,height=600,scrollbars=yes,menubar=yes,resizable=yes");
}
