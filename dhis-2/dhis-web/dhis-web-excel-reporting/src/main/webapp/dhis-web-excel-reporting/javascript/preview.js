
aKey	= null;
aMerged = null;

function previewReport(reportId, periodId, message) {
	
	//var url = "previewReportExcel.action?reportId="+reportId+"&periodId="+periodId+"&sheetId="+sheetId;
	var url = "previewReportExcel.action?reportId="+reportId+"&periodId="+periodId;
	
	setMessage(message);
	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewReportReceived );
	request.send( url );
	
	window.setTimeout('setMessage("Finished")', 5000);

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

function getMergedNumberForEachCell( sKey )
{			
	for (var i = 0 ; i < aKey.length ; i ++) {
	
		if ( sKey == aKey[i] ) {

			return Number(aMerged[i]);
		}
	}
return 1;
}

function exportFromXMLtoHTML( parentElement ) {

	var _index		= 0;
	var sHTML		= "";
	var _sPattern	= "";
	var rows 		= new Array();
	var cols 		= new Array();
	var sheets		= parentElement.getElementsByTagName( 'sheet' );
	var _title		= parentElement.getElementsByTagName( 'name' )[0].firstChild.nodeValue;
	
	sHTML = 
	"<html><head><title>"+_title+"<title></head>"
	+"<link rel='stylesheet' type='text/css' href='style/previewStyle.css' />"
	+"<body><table>";
	
	document.write(sHTML);
	
	for (var s = 0 ; s < sheets.length ; s ++) {
	
		rows = sheets[s].getElementsByTagName( 'row' );
		
		for (var i = 0 ; i < rows.length ; i ++) {
		
			_index		= 0;
			document.write("<tr>");
			
			cols = rows[i].getElementsByTagName( 'col' );
			
			for (var j 	= 0 ; j < cols.length ; ) {
				
				var _number	= cols[j].getAttribute( 'no' );
				
				// Printing out the unformatted cells
				
				for (; _index < _number ; _index ++) {
					
					document.write("<td/>");
				}

				if ( _index == _number ) {
					
					var _no_of_merged_cell = 1;
					var _sData		 = cols[j].getElementsByTagName( 'data' )[0].firstChild.nodeValue;					
					var _align		 = cols[j].getElementsByTagName( 'format' )[0].getAttribute( 'align' );
				
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
		document.write("<br/><br/>");
	}
	document.write("</table></body></html>");
	
	window.status= "DATAWARE HOUSE - "+ _title;
	window.stop();
}
// END OF Previewed Report Excel //

