
aKey	= null;
aMerged = null;
globalReportId = 0;
globalPeriodId = 0;

function getNoSheetsOfReportExcel( reportId ) {

	if ( reportId == '-1' ) {
		clearListById('sheetNoExcelFile');
	}
	else {
		$.post("getListSheet.action", {
			reportId:reportId
		}, 	function ( xmlObject ) {
			clearListById('sheetNoExcelFile');
			xmlObject = xmlObject.getElementsByTagName('sheets')[0];
			nodes	  = xmlObject.getElementsByTagName('sheet');
			for (var i = 0 ; i < nodes.length ; i++)
			{
				var id = nodes[i].getElementsByTagName('id')[0].firstChild.nodeValue;
				var name = nodes[i].getElementsByTagName('name')[0].firstChild.nodeValue;
				addOptionToList(document.getElementById('sheetNoExcelFile'), id, name);
			}
		}, "xml");
	}
}

function previewReport(reportId, periodId, sheetId, orgunitGroupId, message) {
	
	var url = "reportId="+reportId+"&periodId="+periodId+"&sheetId="+sheetId;
	
	if ( orgunitGroupId > 0 ) {
	
		url = "previewAdvancedReportExcel.action?orgunitGroupId=" + orgunitGroupId + "&" + url;
	}
	else {
		url = "previewReportExcel.action?" + url;
	}
	
	setMessage(message);
	disable('sheetNoExcelFile');
	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewReportReceived );
	request.send( url );
	
}

function previewReportReceived( reportXML ) {

	setMergedNumberForEachCell( reportXML );
	exportFromXMLtoHTML( reportXML );
	
	hideMessage();
	enable('sheetNoExcelFile');
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
	var _sHTML		= "<table>";
	var _sPattern	= "";
	var _rows 		= new Array();
	var _cols 		= new Array();
	var _sheets		= parentElement.getElementsByTagName( 'sheet' );
	var _title		= parentElement.getElementsByTagName( 'name' )[0].firstChild.nodeValue;
	
	for (var s = 0 ; s < _sheets.length ; s ++) {
	
		_rows = _sheets[s].getElementsByTagName( 'row' );

		for (var i = 0 ; i < _rows.length ; i ++) {
		
			_index		= 0;
			_sHTML = _sHTML + "<tr>";
			
			_cols = _rows[i].getElementsByTagName( 'col' );
			
			for (var j 	= 0 ; j < _cols.length ; ) {
				
				var _number	= _cols[j].getAttribute( 'no' );
				
				// Printing out the unformatted cells
				for (; _index < _number ; _index ++) {
					
					_sHTML = _sHTML + "<td/>";
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

					_sHTML = _sHTML + "<td align='" + _align + "' colspan='" + _no_of_merged_cell;
					
					if ( isNaN(_sData) == false ) {
						
						_sHTML = _sHTML + "' class='formatNumber";
					}
					_sHTML = _sHTML + "'>"+ _sData + "</td>";
				}
			}
			_sHTML = _sHTML + "</tr>";
		}
		_sHTML = _sHTML + "<br/>";
	}
	_sHTML = _sHTML + "</table>";
	
	document.getElementById("previewContentDiv").innerHTML = _sHTML;
	
	window.status= "DATAWARE HOUSE - "+ _title;
	window.stop();
}
// END OF Previewed Report Excel //

/**
 * Clears the list.
 *
 * @param listId the id of the list.
 */
function clearListById( listId ) {
    var list = document.getElementById( listId );
    clearList( list );
}

/**
 * Clears the list.
 * @param list the list.
 */
function clearList( list ) {
    list.options.length = 0;
}