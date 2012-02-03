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

// ========================================================================================================================
// IMPORT REPORT
// ========================================================================================================================

function changeItemType()
{
	var type = getFieldValue( 'importReportType' );
	
	if( type == 'NORMAL' ){
		byId('expression-button' ).onclick = openExpressionBuild;
	}else {
		byId('expression-button' ).onclick = caExpressionBuilderForm;
	}	
}

// -----------------------------------------------------------------------
// Open Expression Form for Normal Import Report
// -----------------------------------------------------------------------

// Open Expression Form
function openExpressionBuild() {
	
	byId("formula").value = byId("expression").value;
	dataDictionary.loadDataElementGroups( "#divExpression select[id=dataElementGroup]" );
	dataDictionary.loadAllDataElements( "#divExpression select[id=availableDataElements]" );
	
	openDialog( divExpressionDialog );
}

// Insert operand into the Formular textbox
function insertDataElementId() {

	var dataElementComboId = "[" + byId("availableDataElements").value + "." + byId("optionCombos").value + "]";
	byId("formula").value += dataElementComboId;
}

// Insert operators into the Formular textbox
function insertOperation( target, value ) {

	byId( target ).value += value;
}

// Update expression for item
function updateNormalExpression()
{
	expression = jQuery( '#divExpression textarea[id=formula]' ).val();
	setFieldValue( 'expression', getFieldValue('formula' ) );
	
	closeDialog( divExpressionDialog );
}

// -----------------------------------------------------------------------
// Open Expression Form for Catagory Import Report
// -----------------------------------------------------------------------

// Open Expression Form
function caExpressionBuilderForm()
{
	dataDictionary.loadDataElementGroups( "#divCategory select[id=dataElementGroup]" );
	dataDictionary.loadAllDataElements( "#divCategory select[id=availableDataElements]" );
	
	setFieldValue( 'divCategory textarea[id=formula]', getFieldValue('expression') );
	openDialog( divCategoryDialog );
}

// Insert operand into the Formular textbox
function insertExpression() 
{
	var expression = "[*." + getFieldValue("divCategory select[id=optionCombos]") + "]";
	setFieldValue( 'divCategory textarea[id=formula]', getFieldValue( 'divCategory textarea[id=formula]') + expression );
}

// Update expression for item
function updateCaExpression()
{
	expression = jQuery( '#divCategory textarea[id=formula]' ).val();
	setFieldValue( 'expression', expression );
	closeDialog( divCategoryDialog );
}

// Get option combos for selected dataelement
function getOptionCombos(id, target, button)
{
	dataDictionary.loadCategoryOptionComboByDE( id, target);
	disable( button );
}

// -----------------------------------------------------------------------
// Get Dataelement by Group
// -----------------------------------------------------------------------

function getDataElements( id, target )
{
	dataDictionary.loadDataElementsByGroup( id, target );
}

// -----------------------------------------------------------------------
// SAVE COPY IMPORT ITEM(s) TO IMPORT_REPORT
// -----------------------------------------------------------------------

sheetId = 0;
noItemsChecked = 0;
ItemsSaved = null;
itemsCurTarget = null;
itemsDuplicated = null;

function copyImportItemToImportReport() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( copyImportItemToImportReportReceived );
	request.send( "getAllImportReportByType.action?reportType=" + getFieldValue( "importReportType" ) );

}

function copyImportItemToImportReportReceived( xmlObject ) {

	var reports = xmlObject.getElementsByTagName("importReport");
	var selectList = document.getElementById("targetImportReport");
	var options = selectList.options;
	
	options.length = 0;
	
	for( var i = 0 ; i < reports.length ; i++ ) {
	
		var id = reports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = reports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
	
	openDialog( dialog1 );
}

function copyImportItemToExportReport() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( copyImportItemToExportReportReceived );
	request.send( "getAllExportReportByType.action?reportType=" + getFieldValue( "importReportType" ) );
}

function copyImportItemToExportReportReceived( xmlObject ) {

	var exportReports = xmlObject.getElementsByTagName("exportReport");
	var selectList = document.getElementById("targetExportReport");
	var options = selectList.options;
	
	options.length = 0;
	
	for( var i = 0 ; i < exportReports.length ; i++ ) {
	
		var id = exportReports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = exportReports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
	
	loadItemTypes( jQuery("#targetExportReport").val() );
	
	openDialog( dialog2 );
}

function validateCopyImportItemsToImportReport() {

	if ( jQuery( 'input:checked' ).length == 0 )
	{
		setMessage( i18n_no_item );
		return;
	}

	sheetId	= getFieldValue( "copyToImportReport input[id=targetSheetNo]" );

	if ( sheetId < 1 )
	{
		setMessage( i18n_input_sheet_no );
		return;
	}
	
	if ( byId("targetImportReport").value == -1 )
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
    request.setCallbackSuccess( validateCopyImportItemsToImportReportReceived );
	request.send( "getImportItemsByImportReport.action?importReportId=" + byId("targetImportReport").value );
}

function validateCopyImportItemsToImportReportReceived( xmlObject ) {
	
	var items = xmlObject.getElementsByTagName('importItem');
	
	for (var i = 0 ;  i < items.length ; i ++) {
	
		itemsCurTarget.push(items[i].getElementsByTagName('name')[0].firstChild.nodeValue);
	}
	
	splitDuplicatedImportItems( 'importItemID', 'importItemName' );
	saveCopiedImportItemsToImportReport();
}


function validateCopyImportItemsToExportReport()
{
	if ( jQuery( 'input:checked' ).length == 0 )
	{
		setMessage( i18n_no_item );
		return;
	}

	sheetId	= getFieldValue( "copyToExportReport input[id=targetSheetNo]" );
	
	if ( sheetId < 1 )
	{
		setMessage( i18n_input_sheet_no );
		return;
	}
	
	if ( getFieldValue( "targetExportReport" ) == -1 )
	{
		setMessage( "<br/>" + i18n_choose_import_report);
		return;
	}
	
	itemsCurTarget = null;
	itemsDuplicated = null;
	
	itemsCurTarget = new Array();
	itemsDuplicated = new Array();
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateCopyImportItemsToExportReportReceived );
	
	var param = "exportReportId=" + getFieldValue( "targetExportReport" );
		param += "&sheetNo=" + sheetId;
	
	request.sendAsPost( param );
	request.send( "getExportItemsBySheet.action" );
}

function validateCopyImportItemsToExportReportReceived( xmlObject ) {
	
	var items = xmlObject.getElementsByTagName( 'exportItem' );
	
	for (var i = 0 ;  i < items.length ; i ++)
	{
		itemsCurTarget.push(items[i].getElementsByTagName( 'name' )[0].firstChild.nodeValue);
	}
	
	splitDuplicatedImportItems( 'importItemID', 'importItemName' );
	saveCopiedImportItemsToExportReport();
}

function splitDuplicatedImportItems( itemIDAttribute, itemNameAttribute ) {

	var flag = -1;
	var itemsChecked = new Array();
	var listRadio = document.getElementsByName( 'importItemChecked' );

	ItemsSaved = null;
	ItemsSaved = new Array();
	
	for (var i = 0 ; i < listRadio.length ; i++) {
	
		if ( listRadio.item(i).checked ) {
			itemsChecked.push( listRadio.item(i).getAttribute(itemIDAttribute) + "#" + listRadio.item(i).getAttribute(itemNameAttribute));
		}
	}
	
	noItemsChecked = itemsChecked.length;
	
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

warningMessages = "";

function saveCopiedImportItemsToImportReport() {
	
	warningMessages = "";
	// If have ImportItem(s) in Duplicating list
	// preparing the warning message
	if ( itemsDuplicated.length > 0 ) {

		warningMessages += 
		"<b>[" + (itemsDuplicated.length) + "/" + (noItemsChecked) + "]</b>:: "
		+ i18n_copy_items_duplicated
		+ "<br/><br/>";
		
		for (var i in itemsDuplicated) {
		
			warningMessages +=
			"<b>(*)</b> "
			+ itemsDuplicated[i] 
			+ "<br/><br/>";
		}
		
		warningMessages += "<br/>";
	}
	
	// If have also ImportItem(s) in Copying list
	// do copy and prepare the message notes
	if ( ItemsSaved.length > 0 ) {
	
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( saveCopiedImportItemsReceived );	
		
		var params = "importReportDestId=" + byId("targetImportReport").value;
			params += "&sheetNo=" + sheetId;
			
		for (var i in ItemsSaved)
		{
			params += "&itemIds=" + ItemsSaved[i];
		}
		
		request.sendAsPost(params);
		request.send( "copyImportItemToImportReport.action");
	}
	// If have no any ImportItem(s) will be copied
	// and also have ImportItem(s) in Duplicating list
	else if ( itemsDuplicated.length > 0 ) {

		setMessage( warningMessages );
	}
		
	closeDialog( dialog1 );
}

function saveCopiedImportItemsToExportReport() {
	
	warningMessages = "";
	// If have ImportItem(s) in Duplicating list
	// preparing the warning message
	if ( itemsDuplicated.length > 0 ) {

		warningMessages += 
		"<b>[" + (itemsDuplicated.length) + "/" + (noItemsChecked) + "]</b>:: "
		+ i18n_copy_items_duplicated
		+ "<br/><br/>";
		
		for (var i in itemsDuplicated) {
		
			warningMessages +=
			"<b>(*)</b> "
			+ itemsDuplicated[i] 
			+ "<br/><br/>";
		}
		
		warningMessages += "<br/>";
	}
	
	// If have also ImportItem(s) in Copying list
	// do copy and prepare the message notes
	if ( ItemsSaved.length > 0 ) {
	
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( saveCopiedImportItemsReceived );	
		
		var params = "exportReportId=" + getFieldValue( "targetExportReport" );
			params += "&periodType=" + getFieldValue( "periodType" );
			params += "&itemType=" + getFieldValue( "itemType" );
			params += "&sheetNo=" + sheetId;
			
		for (var i in ItemsSaved)
		{
			params += "&itemIds=" + ItemsSaved[i];
		}
		
		request.sendAsPost(params);
		request.send( "copyImportItemToExportReport.action");
	}
	// If have no any ImportItem(s) will be copied
	// and also have ImportItem(s) in Duplicating list
	else if ( itemsDuplicated.length > 0 ) {

		setMessage( warningMessages );
	}
		
	closeDialog( dialog2 );
}

function saveCopiedImportItemsReceived( data ) {
	
	var type = data.getAttribute("type");
	
	if ( type == "success" ) {
	
		warningMessages +=
		" ======= Sheet [" + sheetId + "] ========"
		+ "<br/><b>[" + (ItemsSaved.length) + "/" + (noItemsChecked) + "]</b>:: "
		+ i18n_copy_successful
		+ "<br/>======================<br/><br/>";

	}
	
	setMessage( warningMessages );
}