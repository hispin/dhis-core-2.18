function previewAdvandReport() 
{
	var params = "exportReportId=" + byId('exportReport').value + "&periodId=" + byId('period').value + "&organisationGroupId=" + byId("availableOrgunitGroups").value;
	
	lockScreen();
	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewExportReportReceived );
	request.sendAsPost( params );
	request.send( "previewAdvancedExportReport.action" );	
}

function previewExportReport() {
	
	var exportReport = getFieldValue('exportReport');
	if(exportReport.length == 0){
		showErrorMessage(i18n_specify_export_report);
		return;
	}
	
	lockScreen();
	
	var url = "previewExportReport.action?exportReportId=" + getListValue('exportReport') + "&periodIndex=" + getListValue('period');
	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewExportReportReceived );
	request.send( url );	
	
}

function previewExportReportReceived( parentElement ) 
{
	var aKey 	= new Array();
	var aMerged = new Array();	
	var cells 	= parentElement.getElementsByTagName( 'cell' );
	
	for (var i  = 0 ; i < cells.length ; i ++)
	{	
		aKey[i]		= cells[i].getAttribute( 'iKey' );
		aMerged[i]	= cells[i].firstChild.nodeValue;
	}

	var _index		= 0;
	var _orderSheet	= 0;
	var _sHTML		= "";
	var _sPattern	= "";
	var _rows 		= "";
	var _cols 		= "";
	var _sheets		= parentElement.getElementsByTagName( 'sheet' );
	var tabsHTML 	= '<div id="tabs">';
	var titleHTML 	= '<ul>';
	var contentsHTML= '';
	
	for (var s = 0 ; s < _sheets.length ; s ++)
	{
		// Create tab name
		titleHTML += '<li><a href="#tabs-' + s + '">' + getElementValue( _sheets[s], "name" ) + '</a></li>';
	
		_rows 		= _sheets[s].getElementsByTagName( 'row' );
		_orderSheet	= getRootElementAttribute( _sheets[s], "id" );
		
		contentsHTML += '<div id="tabs-' + s + '">';

		_sHTML = "<table class='formatTablePreview'>";
		
		for (var i = 0 ; i < _rows.length ; i ++)
		{
			_index	 = 0;
			_sHTML 	+= "<tr>";
			
			_cols 	 = _rows[i].getElementsByTagName( 'col' );
			
			for (var j 	= 0 ; j < _cols.length ; )
			{
				var _number	= getRootElementAttribute( _cols[j], 'no' );
				
				// Printing out the unformatted cells
				for (; _index < _number ; _index ++)
				{
					_sHTML += "<td/>";
				}

				if ( _index == _number )
				{
					var _colspan 	= 1;
					var _sData		= getElementValue( _cols[j], 'data' );
					var _align		= getElementAttribute( _cols[j], 'format', 'align' );
				
					// If this cell is merged - Key's form: Sheet#Row#Col
					_sPattern 		=  _orderSheet + "#" + i + "#" + _number;
					_colspan 		= getMergedNumberForEachCell( _sPattern, aKey, aMerged );
					
					// Jumping for <For Loop> AND <Empty Cells>
					j 		= Number(j) + Number(_colspan);
					_index 	= Number(_index) + Number(_colspan);
					
					_sHTML += "<td align='" + _align + "' colspan='" + _colspan + "' ";
					
					if ( !isNaN(_sData) && (_sData != "") )
					{
						_sHTML += "class='formatNumberPreview' "
							   + "title='" + i18n_value_rounded;
					}
					else
					{
						_sHTML += "class='formatStringPreview" ;
					}
					_sHTML += "'>" + _sData + "</td>";
				}
			}
			_sHTML += "</tr>";
		}
		_sHTML += "</table><br/>";		
		
		contentsHTML += _sHTML;
		contentsHTML += '</div>';
	}
	titleHTML += '</ul>';
	
	tabsHTML += titleHTML;
	tabsHTML += contentsHTML;	
	tabsHTML += '</div>';
	
	jQuery( '#previewDiv' ).html( tabsHTML );
	
	jQuery('#tabs').tabs({ collapsible : true });
	
	enable( 'printExcelReportButton' );

	unLockScreen();
}

function getMergedNumberForEachCell( sKey, aKey, aMerged ) {

	for (var i = 0 ; i < aKey.length ; i ++)
	{
		if ( sKey == aKey[i] )
		{
			return Number(aMerged[i]);
		}
	}
return 1;
}

function printExportReport()
{
	var tab = jQuery('#tabs').tabs('option', 'selected');
	jQuery( "#tabs-" + tab ).jqprint();
}