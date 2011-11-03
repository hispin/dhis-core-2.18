/**
 * Global variables
 */

isImport = false;
idTemp = null;
importlist = null;
importItemIds = new Array();

// ----------------------------------------------------------------------
// Methods
// ----------------------------------------------------------------------

function validatePreviewReport( isAdvanced )
{
	var exportReport = getFieldValue('exportReport');

	if ( exportReport.length == 0 )
	{
		showErrorMessage( i18n_specify_export_report );
		return;
	}
	
	lockScreen();

	jQuery.postJSON( 'validateGenerateReport.action',
	{
		'exportReportId': getFieldValue( 'exportReport' ),
		'periodIndex': getFieldValue( 'selectedPeriodId' )
	},
	function( json )
	{
		if ( json.response == "success" ) {
			if ( isAdvanced ) {
				previewAdvandReport();
			}
			else previewExportReport();
		}
		else {
			unLockScreen();
			showWarningMessage( json.message );
		}
	});
}

function previewExportReport() {

	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewExportReportReceived );
	request.send( 'previewExportReport.action' );
}

function previewAdvandReport() 
{	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( previewExportReportReceived );
	request.sendAsPost( params );
	request.send( "previewAdvancedExportReport.action?organisationGroupId=" + byId("availableOrgunitGroups").value );
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

		_sHTML = "<table class='ui-widget-content'>";
		
		for (var i = 0 ; i < _rows.length ; i ++)
		{
			_index	= 0;
			_sHTML += "<tr>";
			
			_cols 	= _rows[i].getElementsByTagName( 'col' );
			
			for (var j 	= 0 ; j < _cols.length ; )
			{
				var _number	= getRootElementAttribute( _cols[j], 'no' );
				var keyId 	= getRootElementAttribute( _cols[j], 'id' );
				
				// Printing out the unformatted cells
				for (; _index < _number ; _index ++)
				{
					_sHTML += "<td class='ui-widget-content'/>";
				}

				if ( _index == _number )
				{
					var _sData		= getElementValue( _cols[j], 'data' );
					var _align		= getElementAttribute( _cols[j], 'format', 'align' );
				
					// If this cell is merged - Key's form: Sheet#Row#Col
					_sPattern 		=  _orderSheet + "#" + i + "#" + _number;
					var _colspan 	= getMergedNumberForEachCell( aKey, _sPattern, aMerged );
					
					// Jumping for <For Loop> AND <Empty Cells>
					j 		= Number(j) + Number(_colspan);
					_index 	= Number(_index) + Number(_colspan);
					
					_sHTML += "<td align='" + _align + "' colspan='" + _colspan + "' ";
					_sHTML += "class='ui-widget-content";
					
					if ( keyId && keyId.length > 0 )
					{
						_sHTML += " ui-unselected' id='" + keyId;
					}
					else if ( !isImport && parseFloat(_sData) )
					{
						_sHTML += " ui-normal";
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
	jQuery( '#tabs' ).tabs({ collapsible : true });
	enable( 'printExcelReportButton' );
	applyStyleIntoPreview();
	unLockScreen();
}

function applyStyleIntoPreview()
{
	importlist = jQuery( 'table.ui-widget-content tr > td.ui-unselected' );
	
	if ( importlist.length > 0 )
	{
		importlist.mouseover(function()
		{
			jQuery(this).addClass( 'ui-mouseover' );
		});

		importlist.mouseout(function()
		{
			jQuery(this).removeClass( 'ui-mouseover' );
		});

		importlist.click(function()
		{
			idTemp = jQuery(this).attr( 'id' ) + "_" + jQuery(this).html();
			
			if ( jQuery.inArray(idTemp, importItemIds) != -1 )
			{
				importItemIds = jQuery.grep( importItemIds, function(value) {
					return value != idTemp
				});
			}
			else importItemIds.push( idTemp );
			
			jQuery(this).toggleClass( 'ui-selected' );
		});
	}
}

function getMergedNumberForEachCell( aKey, sKey, aMerged )
{
	for (var i = 0 ; i < aKey.length ; i ++)
	{
		if ( aKey[i] == sKey )
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