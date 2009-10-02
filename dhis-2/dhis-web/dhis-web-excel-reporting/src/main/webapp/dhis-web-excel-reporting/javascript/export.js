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
<<<<<<< TREE

	if(byId('advancedCheck').checked){
		
		generateAdvancedReportExcel();
		
	}else{
		var reportId = $('#report').val();
		var periodId = $('#period').val();
		
		window.location = "generateReportExcel.action?reportId=" + reportId + "&periodId=" + periodId ;
	}
}

// Previewed Report Excel //
function previewReport() {
	
	var reportId = getFieldValue('report');
	var periodId = getFieldValue('period');
	var url = "previewReport.action?reportId=" + reportId + "&periodId=" + periodId;
	
	setMessage(globalMessage);
	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewReportReceived );
	request.send( url );
	
	//window.open(url, 'Preview Report Form', "toolbar='0',scrollbars='1',location='0',statusbar='0',menubar='0',resizable='1',width='700',height='900'");
}

function previewReportReceived( reportXML ) {

	//var type = xmlObject.getAttribute( 'type' );
	
	initNewArray();
	setMergedNumberForEachCell( reportXML );
	exportXMLtoHTML( reportXML );
	
}

aData	= null;
aMerged = null;


function initNewArray() {

	aData		= null;
	aMerged		= null;
	aData 		= new Array();
	aMerged 	= new Array();
}


function setMergedNumberForEachCell( parentElement ) {
	
	var cells 	 = parentElement.getElementsByTagName( 'cell' );
	
	for (var i = 0 ; i < cells.length ; i ++) {
		
		var _iRow		 = cells[i].getAttribute( 'iRow' );
		var _iCol		 = cells[i].getAttribute( 'iCol' );
		var _iMerged	 = cells[i].firstChild.nodeValue;
		
		if ( (_iRow > 0) || (_iCol > 0) ) {
		
			aData[i] = (_iRow-1) + "#" + (_iCol-1);
			
			if ( _iMerged < 0 ) {
			
				_iMerged = -_iMerged;
			}
			aMerged[i] = _iMerged;
		}
	}
}
	

//--------------------------------------------------------------------------------------------------------//

function getMergedNumberForEachCell( sKey )
{			
	for (var i = 0 ; i < aData.length ; i ++) {
	
		if ( sKey == aData[i] ) {

			return Number(aMerged[i]);
		}
	}
return 1;
}


//--------------------------------------------------------------------------------------------------------//

function exportXMLtoHTML( parentElement ) {

	var _index		= 0;
	var sHTML		= "";
	var _sPattern	= "";
	var _title		= parentElement.getElementsByTagName( 'name' )[0].firstChild.nodeValue;
	
	var rows = parentElement.getElementsByTagName( 'row' );
	
	for (var i = 0 ; i < rows.length ; i ++) {
		
		_index		= 0;
		sHTML 		= sHTML + "<tr>";
		
		var cols = rows[i].getElementsByTagName( 'col' );
		
		for (var j = 0 ; j < cols.length ; ) {
			
			var _number	= cols[j].getAttribute( 'number' );
	
			var _bgcolour	 = new Array(1);
			_bgcolour[0]	 = "#E1FFFF";
			var background	 = cols[j].getElementsByTagName( 'background' )[0];
			
			if ( background != null ) {
				_bgcolour = background.getAttribute( 'colour' ).split(' ');
			}
			
			// Printing out the unformatted cells
			
			for (; _index < _number ; _index ++) {
				
				sHTML 	= sHTML 
							+ "<td "
							+ "bgcolor='" + _bgcolour[0] 
							+ "'/>";
			}
			
			
			
			if ( _index == _number ) {
				

				
				var _number_of_merged_cell = 1;
				var _sData		 = cols[j].getElementsByTagName( 'data' )[0].firstChild.nodeValue;					
				var _align		 = cols[j].getElementsByTagName( 'format' )[0].getAttribute( 'align' );
				var _valign		 = cols[j].getElementsByTagName( 'format' )[0].getAttribute( 'valign' );
				var _colour		 = cols[j].getElementsByTagName( 'font' )[0].getAttribute( 'colour' );
				var _italic		 = cols[j].getElementsByTagName( 'font' )[0].getAttribute( 'italic' );
				var _underline	 = cols[j].getElementsByTagName( 'font' )[0].getAttribute( 'underline' );
				var _point_size	 = cols[j].getElementsByTagName( 'font' )[0].getAttribute( 'point_size' );
				var _bold_weight = cols[j].getElementsByTagName( 'font' )[0].getAttribute( 'bold_weight' );
				

				
				// Checking for Alignment //
				if ( _align == "centre" ) {
					_align 	= "center";
				}

			
				// If this cell is merged
				// Key with form: Row#Col
				
				_sPattern 				= i + "#" + _number;
				_number_of_merged_cell 	= getMergedNumberForEachCell( _sPattern );
								
				// Jumping for <For Loop> AND <Empty Cells>
				j 		= Number(j) + Number(_number_of_merged_cell);
				_index 	= Number(_index) + Number(_number_of_merged_cell);

			
				sHTML 	= sHTML 
							+ "<td align='" + _align 
							+ "' valign='" + _valign 
							+ "' bgcolor='" + _bgcolour[0] 
							+ "' colspan='" + _number_of_merged_cell 
							+ "'style='border-style: solid; border-width: 0px; width:10%'>";
							
					
				// Setting for FONT //
				sFont = "<font"
						+ " color='" + _colour + "'"
						+ " size='" + _point_size/4 + "'>"
						+ _sData
						+ "</font>";
				
				// Checking for BOLD //
				if ( _bold_weight != 400 ) {
					sFont = "<b>" + sFont + "</b>";
				}
				
				// Checking for ITALIC //
				if ( _italic == true ) {
					sFont = "<i>" + sFont + "</i>";
				}
				
				// Checking for UNDERLINE //
				if ( _underline != "none" ) {
					sFont = "<u>" + sFont + "</u>";
				}
				
				sHTML	= sHTML + sFont + "</td>";		
			}
			/**/
		}
		sHTML 			= sHTML + "</tr>";
	}
	
	sHTML = "<html><head><title>" 
				+ _title
				+ "</title></head><body><table border='1' width='100%' >"
				+ sHTML
				+ "</table></body></html>";
				
	document.write(sHTML);
}
// END OF Previewed Report Excel //

function generateAdvancedReportExcel() {	

	//var reportId = $('#report').val();
	//var periodId = $('#period').val();
	
	//window.location = "generateAdvancedReportExcel.action?reportId=" + reportId + "&periodId=" + periodId + "&reportType=category";

	var reportId = $('#report').val();
	var periodId = $('#period').val();
	var organisationGroupId = byId('availableOrgunitGroups').value;
	var url =  "generateAdvancedReportExcel.action?reportId=" + reportId ;
	url += "&periodId=" + periodId ;
	url += "&organisationGroupId=" + organisationGroupId;
	url += "&reportType=normal";
	
	window.location = url;
	
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
		//$("#generate_advanced_report").show();

		//$("#generate_report").hide();
		//$("#generateAdvance").show();
		
		//hideById('generate_report');
		//showById('generate_advanced_report'); 
		//showById('generateAdvance')
	} 
	else
	{ 
		
		
		byId('availableOrgunitGroups').disabled=true;
		//$("#generate_advanced_report").hide();
		//$("#generate_report").show();
		//$("#generateAdvance").hide();
		
		//byId('generate_report').style.visibility = true;
		//byId(('generate_advanced_report').style.visibility = false;
		
		//hideById('generate_advanced_report'); 
		//showById('generate_report'); 
		
		//hideById('generateAdvance');
	}
	
}
	
	$("#loading").showAtCenter( true );	
	$.post("generateReportExcelAjax.action",{
		reportId:$('#report').val(),
		periodId:$('#period').val()
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
	//var sheetId  = $('#period').val();
	
	//window.open("openPreviewReport.action?reportId=" + reportId + "&periodId=" + periodId + "&sheetId=" + sheetId,"_blank","width=900,height=600,scrollbars=yes,menubar=yes,resizable=yes");
	window.open("openPreviewReport.action?reportId=" + reportId + "&periodId=" + periodId, "_blank", "width=900,height=600,scrollbars=yes,menubar=yes,resizable=yes");
}
