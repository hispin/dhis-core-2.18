
aKey	= null;
aMerged = null;
globalReportId = 0;
globalPeriodId = 0;

function previewReport(reportId, periodId, sheetId, orgunitGroupId, message) {
	
	var url = "reportId="+reportId+"&periodId="+periodId+"&sheetId="+sheetId;
	
	if ( orgunitGroupId > 0 ) {
	
		url = "previewAdvancedReportExcel.action?orgunitGroupId=" + orgunitGroupId + "&" + url;
	}
	else {
		url = "previewReportExcel.action?" + url;
	}
	
	alert(url);
	
	globalReportId = reportId;
	globalPeriodId = periodId;
	
	setMessage(message);
	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewReportReceived );
	request.send( url );
}

function previewReportReceived( reportXML ) {

	setMergedNumberForEachCell( reportXML );
	exportFromXMLtoHTML( reportXML );
}

function setMergedNumberForEachCell( parentElement ) {
	
	aKey 		= new Array();
	aMerged 	= new Array();
	
	var cells 	 = parentElement.getElementsByTagName( 'cell' );
	
	for (var i = 0 ; i < cells.length ; i ++) {
		
		aKey[i]	= cells[i].getAttribute( 'iKey' );
		aMerged[i]	= cells[i].firstChild.nodeValue;
	}
}

function getMergedNumberForEachCell( sKey ) {
	for (var i = 0 ; i < aKey.length ; i ++) {
	
		if ( sKey == aKey[i] ) {

			return Number(aMerged[i]);
		}
	}
return 1;
}

function exportFromXMLtoHTML( parentElement ) {

	var _index		= 0;
	var _sHTML		= "";
	var _sPattern	= "";
	var _rows 		= new Array();
	var _cols 		= new Array();
	var _sheets		= parentElement.getElementsByTagName( 'sheet' );
	var _title		= parentElement.getElementsByTagName( 'name' )[0].firstChild.nodeValue;
		
	_sHTML = 
	"<html><head><title>"+_title+"<title>"
	+"<link rel='stylesheet' type='text/css' href='style/previewStyle.css'/></head>"
	+"<body><table>";
		
	document.write(_sHTML);

	for (var s = 0 ; s < _sheets.length ; s ++) {
	
		_rows = _sheets[s].getElementsByTagName( 'row' );

		for (var i = 0 ; i < _rows.length ; i ++) {
		
			_index		= 0;
			document.write("<tr>");
			
			_cols = _rows[i].getElementsByTagName( 'col' );
			
			for (var j 	= 0 ; j < _cols.length ; ) {
				
				var _number	= _cols[j].getAttribute( 'no' );
				
				// Printing out the unformatted cells
				for (; _index < _number ; _index ++) {
					
					document.write("<td/>");
				}

				if ( _index == _number ) {
					
					var _no_of_merged_cell = 1;
					var _sData		 = _cols[j].getElementsByTagName( 'data' )[0].firstChild.nodeValue;					
					var _align		 = _cols[j].getElementsByTagName( 'format' )[0].getAttribute( 'align' );
				
					// If this cell is merged - Key's form: Sheet#Row#Col
					_sPattern 			=  s + "#" + i + "#" + _number;
					_no_of_merged_cell 	= getMergedNumberForEachCell( _sPattern );
					
					// Jumping for <For Loop> AND <Empty Cells>
					j 		= Number(j) + Number(_no_of_merged_cell);
					_index 	= Number(_index) + Number(_no_of_merged_cell);

					document.write("<td align='" + _align + "' colspan='" + _no_of_merged_cell) ;
					
					if ( isNaN(_sData) == false ) {
						
						document.write("' class='formatNumber");
					}
					document.write("'>"+ _sData + "</td>");
				}
			}
			document.write("</tr>");
		}
		document.write("<br/>");
	}
	document.write("</table></body></html>");
	
	window.status= "DATAWARE HOUSE - "+ _title;
	window.stop();
}
// END OF Previewed Report Excel //

