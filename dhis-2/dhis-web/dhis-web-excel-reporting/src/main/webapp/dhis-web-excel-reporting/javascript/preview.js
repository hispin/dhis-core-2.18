
aKey	= null;
aMerged = null;

function reportChanged() {
	
	window.location.href="selectExportReportParam.action?reportGroup=" + $("#group").val() + "&reportId=" + $("#report").val();
}

function previewReport() {
	
	var url = "reportId=" + $('#report').val() + "&periodId=" + $('#period').val() + "&sheetId=-1";
	
	if ( byId('availableOrgunitGroups') != null ) {
	
		url = "previewAdvancedReportExcel.action?orgunitGroupId=" + orgunitGroupId + "&" + url;
	}
	else {
		url = "previewReportExcel.action?" + url;
	}
	
	$("#loadingPreview").showAtCenter(true);
	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewReportReceived );
	request.send( url );
	
}
function previewReportReceived( reportXML ) {

	setMergedNumberForEachCell( reportXML );
	exportFromXMLtoHTML( reportXML );
	
	deleteDivEffect();
	$("#loadingPreview").hide();
	
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
	var _rows 		= "";
	var _cols 		= "";
	var _sheets		= parentElement.getElementsByTagName( 'sheet' );
	var _title		= parentElement.getElementsByTagName( 'name' )[0].firstChild.nodeValue;	
	
	for (var s = 0 ; s < _sheets.length ; s ++) {
	
		_rows = _sheets[s].getElementsByTagName( 'row' );

		_sHTML = "<table class='formatTablePreview'>";
		
		for (var i = 0 ; i < _rows.length ; i ++) {
		
			_index		= 0;
			_sHTML += "<tr>";
			
			_cols = _rows[i].getElementsByTagName( 'col' );
			
			for (var j 	= 0 ; j < _cols.length ; ) {
				
				var _number	= _cols[j].getAttribute( 'no' );
				
				// Printing out the unformatted cells
				for (; _index < _number ; _index ++) {
					
					_sHTML += "<td/>";
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

					_sHTML += "<td align='" + _align + "' colspan='" + _no_of_merged_cell;
					
					if ( isNaN(_sData) == false ) {
						
						_sHTML += "' class='formatNumberPreview";
					}
					else {
						_sHTML += "' class='formatStringPreview";
					}
					_sHTML += "'>"+ _sData + "</td>";
				}
			}
			_sHTML += "</tr>";
		}
		_sHTML += "</table><br/>";
		
		if ( byId("fragment-" + eval(s+1)) != null ) {
		
			byId("fragment-" + eval(s+1)).innerHTML = _sHTML;
		}
	}
	
	showById("tabs");
	
	window.status= "DATAWARE HOUSE - "+ _title;
	window.stop();
}
// END OF Previewed Report Excel //
