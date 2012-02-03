// ---------------------------------------------------------------------------
// Dialog
// ---------------------------------------------------------------------------

function setUpDialog( elementId, title, width, height )
{
	var dialog = jQuery( '#'+elementId ).dialog({
		title: title,
		modal: true,
		autoOpen: false,
		minWidth: width,
		minHeight: height,
		width: width,
		height: height
	});
	
	return dialog;
}

function openDialog( _dialog )
{
	_dialog.dialog( 'open' );
}

function closeDialog( _dialog )
{
	_dialog.dialog( 'close' );
}

// ---------------------------------------------------------------------------------
// Methods
// ---------------------------------------------------------------------------------	

function toggleAll( elementList, checked ) {

	var list = jQuery( elementList );
	
	for ( var i in list )
	{
		list[i].checked = checked;
	}
}

function exportItemSelect( checked )
{
	toggleAll( "input[type=checkbox][name=exportItemCheck]", checked );
}

function changeItemType()
{
	value = getFieldValue( 'itemType' );
	enable( 'expression-button' );
	
	setFieldValue( 'exportItem input[id=expression]', getFieldValue( 'exportItem input[id=currentExpression]') );
	
	if( value == 'dataelement' ){
		byId('expression-button' ).onclick = deExpressionBuilderForm;
	}else if( value == 'indicator' ){
		byId('expression-button' ).onclick = inExpressionBuilderForm ;
	}else if( value == 'formulaexcel' ){
		byId('expression-button' ).onclick = excelFormulaExpressionBuilderForm ;
	}else if( value == 'organisation' || value == 'serial' || value == 'dataelement_code' || value == 'dataelement_name' ){
		disable( 'expression-button' );
		setFieldValue( 'exportItem input[id=expression]', value );
		removeValidatorRulesById( 'exportItem input[id=expression]' );
		removeValidatorRulesById( 'dataelement textarea[id=formula]' );
	}
}

function insertOperation( value ) {
	byId('formula').value += value;	
}

function cleanFormula()
{
	setFieldValue( 'formula','');
	setInnerHTML( 'expression-description', '');
} 

function insertExpression() 
{
	if( category ) var expression = "[*." + getFieldValue("elementSelect")+ "]";
	else var expression = getFieldValue("elementSelect");
	setFieldValue( 'formula', getFieldValue( 'formula') + expression );

	getExpression();
}

function getExpression()
{
	jQuery.postJSON( '../dhis-web-commons-ajax-json/getExpressionText.action',
	{ expression: getFieldValue('formula')}, function( json ){
		if(json.response == 'success'){
			setInnerHTML( 'expression-description', json.message );
		}
	});		
}

function validateAddExportItem( form )
{
	jQuery.postJSON('validationExportItem.action',
	{
		exportReportId: getFieldValue( 'exportReportId' ),
		name: getFieldValue( 'name' ),
		sheetNo: getFieldValue( 'sheetNo' ),
		row: getFieldValue( 'row' ),
		column: getFieldValue( 'column' )

	},function( json ){
		if(json.response == 'success'){					
			form.submit();
		}else{
			showErrorMessage( json.message );
		}
	});
}

function validateUpdateExportItem( form )
{
	jQuery.postJSON('validationExportItem.action',
	{
		id: getFieldValue( 'id' ),
		exportReportId: getFieldValue( 'exportReportId' ),
		name: getFieldValue( 'name' ),
		sheetNo: getFieldValue( 'sheetNo' ),
		row: getFieldValue( 'row' ),
		column: getFieldValue( 'column' )
		
	},function( json ){
		if(json.response == 'success'){
			form.submit();
		}else{
			showErrorMessage( json.message );
		}
	});
}

/*
*	Delete multi export item
*/

function deleteMultiExportItem( confirm )
{
	if ( window.confirm( confirm ) ) 
	{			
		var listRadio = document.getElementsByName( 'exportItemCheck' );
		var url = "deleteMultiExportItem.action?";
		var j = 0;
		
		for( var i=0; i < listRadio.length; i++ )
		{
			var item = listRadio.item(i);
			
			if( item.checked == true )
			{		
				url += "ids=" + item.getAttribute( 'exportItemID' );
				url += (i < listRadio.length-1) ? "&" : "";
				j++;
			}
		}
		
		if( j>0 )
		{
			$.getJSON(
				url, {}, function( json )
				{
					if ( json.response == "success" )
					{
						window.location.reload();
					}
					else if ( json.response == "error" )
					{
						setMessage( json.message ); 
					}
				}
			);
		}		
	}
}

/**
 *	COPY EXPORT_ITEM(s) TO ANOTHER EXPORT_REPORT
 */
function copyExportItemToExportReport() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( copyExportItemToExportReportReceived );
	request.send( "getAllExportReportByType.action?reportType=" + getFieldValue( "exportReportType" ) );
}

function copyExportItemToExportReportReceived( xmlObject ) {

	var exportReports = xmlObject.getElementsByTagName("exportReport");
	var selectList = document.getElementById("targetExportReport");
	var options = selectList.options;
	
	options.length = 0;
	
	for( var i = 0 ; i < exportReports.length ; i++ ) {
	
		var id = exportReports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = exportReports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
	
	openDialog( dialog1 );
}


/**
 *	Validate Copy Export Items to another Export Report
 */

sheetId = 0;
NumberOfItemsChecked = 0;
ItemsSaved = null;
itemsCurTarget = null;
itemsDuplicated = null;
warningMessages = "";

function validateCopyExportItemsToExportReport()
{
	if ( jQuery( 'input:checked' ).length == 0 )
	{
		setMessage( i18n_no_item );
		return;
	}

	sheetId	= getFieldValue( "targetSheetNo" );
	
	if ( sheetId < 1 )
	{
		setMessage( i18n_input_sheet_no );
		return;
	}
	
	if ( getFieldValue("targetExportReport") == -1 )
	{
		setMessage( i18n_choose_export_report );
		return;
	}
	
	itemsCurTarget = null;
	itemsDuplicated = null;
	
	itemsCurTarget = new Array();
	itemsDuplicated = new Array();

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validateCopyExportItemsToExportReportReceived );
	
	var param = "exportReportId=" + getFieldValue("targetExportReport");
		param += "&sheetNo=" + sheetId;
	
	request.sendAsPost( param );
	request.send( "getExportItemsBySheet.action" );
	
}

function validateCopyExportItemsToExportReportReceived( data ) {

	var items = data.getElementsByTagName('exportItem');
		
	for (var i = 0 ; i < items.length ; i ++) 
	{
		itemsCurTarget.push(items[i].getElementsByTagName('name')[0].firstChild.nodeValue);
	}
	
	splitDuplicatedItems( 'exportItemID', 'exportItemName' );
	saveCopyExportItemsToExportReport();
}

function splitDuplicatedItems( itemIDAttribute, itemNameAttribute )
{
	var flag = -1;
	var itemsChecked = new Array();
	var listRadio = document.getElementsByName( 'exportItemCheck' );

	ItemsSaved = null;
	ItemsSaved = new Array();
	
	for (var i = 0 ; i < listRadio.length ; i++)
	{
		if ( listRadio.item(i).checked )
		{
			itemsChecked.push( listRadio.item(i).getAttribute(itemIDAttribute) + "#" + listRadio.item(i).getAttribute(itemNameAttribute));
		}
	}
	
	NumberOfItemsChecked = itemsChecked.length;
	
	for (var i in itemsChecked)
	{
		flag = i;
		
		for (var j in itemsCurTarget)
		{
			if ( itemsChecked[i].split("#")[1] == itemsCurTarget[j] )
			{
				flag = -1;
				itemsDuplicated.push( itemsChecked[i].split("#")[1] );
				break;
			}
		}
		if ( flag >= 0 )
		{
			ItemsSaved.push( itemsChecked[i].split("#")[0] );
		}
	}
}

function saveCopyExportItemsToExportReport() {
	
	warningMessages = " ======= Sheet [" + sheetId + "] ========";
	
	// If have ExportItem(s) in Duplicating list
	// Preparing the warning message
	if ( itemsDuplicated.length > 0 )
	{
		setUpDuplicatedItemsMessage();
	}
	
	// If have also ExportItem(s) in Copying list
	// Do copy and prepare the message notes
	if ( ItemsSaved.length > 0 )
	{
		var url = "copyExportItemToExportReport.action";
			url += "?exportReportId=" + getFieldValue("targetExportReport");
			url += "&sheetNo=" + sheetId;
			
		for (var i in ItemsSaved)
		{
			url += "&exportItems=" + ItemsSaved[i];
		}
		
		executeCopyItems( url );
	}
	// If have no any ExportItem(s) will be copied
	// and also have ExportItem(s) in Duplicating list
	else if ( itemsDuplicated.length > 0 )
	{
		setMessage( warningMessages );
	}
		
	closeDialog( dialog1 );
}


/** 
*	COPY SELECTED EXPORT_ITEM(s) TO IMPORT_REPORT
*/

function copyExportItemToImportReport()
{
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( copyExportItemToImportReportReceived );
	request.send( "getAllImportReportByType.action?reportType=" + getFieldValue( "exportReportType" ) );
}

function copyExportItemToImportReportReceived( xmlObject ) {
	
	var groups = xmlObject.getElementsByTagName("importReport");
	var selectList = document.getElementById("targetImportReport");
	var options = selectList.options;
	
	options.length = 0;
	
	for( var i = 0 ; i < groups.length ; i++ ) {
	
		var id = groups[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = groups[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
	
	openDialog( dialog2 );
}

/*
*	Validate copy Export Items to Import Report
*/

function validateCopyExportItemsToImportReport() {

	if ( jQuery( 'input:checked' ).length == 0 )
	{
		setMessage( i18n_no_item );
		return;
	}

	sheetId	= getFieldValue("targetImportReportSheetNo");
	
	if ( sheetId < 1 )
	{
		setMessage( i18n_input_sheet_no );
		return;
	}
	
	if ( getFieldValue("targetImportReport") == -1 )
	{
		setMessage( i18n_choose_import_report );
		return;
	}
	
	itemsCurTarget = null;
	itemsDuplicated = null;
	
	itemsCurTarget = new Array();
	itemsDuplicated = new Array();
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateCopyExportItemsToImportReportReceived );
	request.send( "getImportItemsByImportReport.action?importReportId=" + getFieldValue("targetImportReport") );
}

function validateCopyExportItemsToImportReportReceived( xmlObject )
{	
	var items = xmlObject.getElementsByTagName('importItem');
	
	for (var i = 0 ;  i < items.length ; i ++)
	{
		itemsCurTarget.push(items[i].getElementsByTagName('name')[0].firstChild.nodeValue);
	}
	
	splitDuplicatedItems( 'exportItemID', 'exportItemName' );
	saveCopiedExportItemsToImportReport();
}

function saveCopiedExportItemsToImportReport() {
	
	warningMessages = " ======= Sheet [" + sheetId + "] ========";
	
	// If have ExportItem(s) in Duplicating list
	// preparing the warning message
	if ( itemsDuplicated.length > 0 )
	{
		setUpDuplicatedItemsMessage();
	}
	
	// If have also ExportItem(s) in Copying list
	// do copy and prepare the message notes
	if ( ItemsSaved.length > 0 )
	{
		var url = "copyExportItemToImportReport.action";
			url += "?importReportId=" + getFieldValue("targetImportReport");
			url += "&sheetNo=" + sheetId;
			
		for (var i in ItemsSaved)
		{
			url += "&exportItemIds=" + ItemsSaved[i];
		}
	
		executeCopyItems( url );
	}
	// If have no any ExportItem(s) will be copied
	// and also have ExportItem(s) in Duplicating list
	else if ( itemsDuplicated.length > 0 )
	{
		setMessage( warningMessages );
	}
		
	closeDialog( dialog2 );
}

function setUpDuplicatedItemsMessage()
{		
	warningMessages += 
	"<br/><b>[" + (itemsDuplicated.length) + "/" + (NumberOfItemsChecked) + "]</b>:: "
	+ i18n_copy_items_duplicated
	+ "<br/><br/>";
	
	for (var i in itemsDuplicated)
	{
		warningMessages +=
		"<b>(*)</b> "
		+ itemsDuplicated[i] 
		+ "<br/><br/>";
	}
	
	warningMessages += "<br/>";
}

function executeCopyItems( url )
{
	$.postJSON(
		url, {}, function ( json )
		{
			if ( json.response == "success" )
			{	
				warningMessages +=
				"<br/><b>[" + (ItemsSaved.length) + "/" + (NumberOfItemsChecked) + "]</b>:: "
				+ i18n_copy_successful
				+ "<br/>=======================<br/><br/>";
			}
			
			setMessage( warningMessages );
		}
	);
}

/**
* Indicator Export item type
*/
function openIndicatorExpression()
{
	byId("formulaIndicator").value = byId("expression").value;
	
	getIndicatorGroups();
	filterIndicators();	
	enable("indicatorGroups");
	enable("availableIndicators");
	setPositionCenter( 'indicatorForm' );
	
	$("#indicatorForm").show();
}

function getIndicatorGroups()
{
	var list = byId('indicatorGroups');
	
	list.options.length = 0;
	list.add( new Option( "ALL", "ALL" ), null );
	
	var formula = byId("formulaIndicator").value;
	for ( id in indicatorGroups )
	{
		list.add(  new Option( indicatorGroups[id], id ), null );
	}
}

function filterIndicators()
{
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( filterIndicatorsCompleted );
	request.send( "../dhis-web-commons-ajax/getIndicators.action?id=" + $("#indicatorGroups").val());
}

function filterIndicatorsCompleted( xmlObject )
{
	var indiatorList = byId( "availableIndicators" );
	indiatorList.options.length = 0;
	
	var indicators = xmlObject.getElementsByTagName( "indicator" );
	
	for ( var i = 0; i < indicators.length; i++ )
	{
		var id = indicators[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var indicatorName = indicators[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		var option = document.createElement( "option" );
		
		option.value = "[" + id + "]";
		option.text = indicatorName;
		indiatorList.add( option, null );	
		
		var formula = byId('formulaIndicator').value;
		if(formula==option.value){
			option.selected = true;
			byId("formulaIndicatorDiv").innerHTML = indicatorName;
		}
	}
}

/**
* Open Category Expression
*/
function openCategoryExpression()
{	
	byId("categoryFormula").value = byId("expression").value;
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( openCategoryExpressionReceived );
	request.send("getExportReport.action?id=" + exportReportId);	
}

function openCategoryExpressionReceived( data ) {

	var selectedDataElementGroups = document.getElementById('dataElementGroup_');
	selectedDataElementGroups.options.length = 0;
	var dataElementGroups = data.getElementsByTagName('dataElementGroup');
	
	for( var i = 0 ; i < dataElementGroups.length ; i++ ) {
	
		var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
		//selectedDataElementGroups.options.add(new Option(name, id));
		
		var option = new Option( name, id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
		selectedDataElementGroups.add(option, null);
	}
	
	getDataElementGroupOrder();
	setPositionCenter( 'category' );
	enable( "dataElementGroup_" );
	enable( "availableDataElements_" );
	byId( "availableDataElements_" ).onchange = function(e){ getOptionCombos_() };
	
	$( "#category" ).show();	
}

function getOptionCombos_()
{
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getOptionCombos_Received );
	request.send( "getOptionCombos.action?dataElementId=" + byId("availableDataElements_").value );	
}

function getOptionCombos_Received( xmlObject ) {

	xmlObject = xmlObject.getElementsByTagName('categoryOptions')[0];		
	
	var optionComboList = byId( "optionCombos_" );			
	optionComboList.options.length = 0;		
	var optionCombos = xmlObject.getElementsByTagName( "categoryOption" );
	
	for ( var i = 0; i < optionCombos.length; i++ )
	{
		var id = optionCombos[ i ].getAttribute('id');
		var name = optionCombos[ i ].firstChild.nodeValue;			
		var option = document.createElement( "option" );
		
		option.value = id ;
		option.text = name;
		optionComboList.add( option, null );
	}
}

function insertDataElementId_()
{
	var optionCombo = byId("optionCombos_");
	var dataElementComboId = "[*." + optionCombo.value + "]";
	byId("categoryFormula").value = dataElementComboId;
	byId("categoryFormulaDiv").innerHTML = "*." + optionCombo[optionCombo.selectedIndex].text ;
}

function clearFormula(formulaFieldName)
{
	byId(formulaFieldName).value = '';
	byId(formulaFieldName + "Div").innerHTML = ''
}
// -------------------------------------------------------------------------------
// Show textFormula
// -------------------------------------------------------------------------------

function updateFormulaText( formulaFieldName )
{		
	var formula = htmlEncode( byId( formulaFieldName ).value );
	var url = "getFormulaText.action?formula=" + formula;
	
	var request = new Request();
	request.setCallbackSuccess( updateFormulaTextReceived );
    request.send( url );
}

function updateFormulaTextReceived( messageElement )
{
	byId( "formulaDiv").innerHTML = messageElement;
}
