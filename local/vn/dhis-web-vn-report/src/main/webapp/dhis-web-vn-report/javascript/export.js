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

globalMessage = null;
generic_type = null;

function validateGenerateReport(message) {

	var reportId = getFieldValue('report');
	var periodId = getFieldValue('period');

	globalMessage = message;
	
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
		if ( generic_type == 'preview' ) {
			
			previewReport();
		}
		else {
			generateReport();
		}
	}
}

function generateReport() {
	
	setMessage(globalMessage);

	var reportId = getFieldValue('report');
	var periodId = getFieldValue('period');
	
	window.location = "generateReport.action?reportId=" + reportId + "&periodId=" + periodId + "&reportType=category";

	setTimeout('setMessage("Finished !")', 3000);
	
	//var request = new Request();
	//request.setResponseTypeXML( 'xmlObject' );
	//request.setCallbackSuccess( generateReportCompleted );
	//request.send( "generateReport.action?reportId=" + reportId + "&periodId=" + periodId); 
}
function generateReportCompleted( xmlObject ){
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

