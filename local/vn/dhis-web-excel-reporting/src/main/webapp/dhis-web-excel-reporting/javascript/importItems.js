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
ImportItemsSaved = null;
importItemsCurTarget = null;
importItemsDuplicated = null;

function copySelectedItemToGroup() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( copySelectedItemToGroupReceived );
	request.send( "getAllImportReport.action" );

}

function copySelectedItemToGroupReceived( xmlObject ) {

	var reports = xmlObject.getElementsByTagName("importReport");
	var selectList = document.getElementById("targetGroup");
	var options = selectList.options;
	
	options.length = 0;
	
	for( var i = 0 ; i < reports.length ; i++ ) {
	
		var id = reports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = reports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
	
	openDialog( dialog1 );
}

function validateCopyImportItemsToImportReport() {

	sheetId	= byId("targetSheetNo").value;
	
	var message = '';
	
	if ( sheetId < 1 )
	{
		message = i18n_input_sheet_no;
	}
	
	if ( byId("targetGroup").value == -1 )
	{
		message += "<br/>" + i18n_choose_import_report;
	}
	
	if ( message.length > 0 )
	{
		setMessage( message );
		return;
	}
	
	importItemsCurTarget = null;
	importItemsDuplicated = null;
	
	importItemsCurTarget = new Array();
	importItemsDuplicated = new Array();
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateCopyImportItemsToImportReportReceived );
	request.send( "getImportItemsByGroup.action?importReportId=" + byId("targetGroup").value );
}

function validateCopyImportItemsToImportReportReceived( xmlObject ) {
	
	var items = xmlObject.getElementsByTagName('importItem');
	
	for (var i = 0 ;  i < items.length ; i ++) {
	
		importItemsCurTarget.push(items[i].getElementsByTagName('name')[0].firstChild.nodeValue);
	}
	
	splitDuplicatedImportItems( 'importItemChecked', 'importItemID', 'importItemName' );
	
	saveCopiedImportItemsToImportReport();
}

function splitDuplicatedImportItems( itemCheckID, itemIDAttribute, itemNameAttribute ) {

	var flag = -1;
	var itemsChecked = new Array();
	var listRadio = document.getElementsByName( itemCheckID );

	ImportItemsSaved = null;
	ImportItemsSaved = new Array();
	
	for (var i = 0 ; i < listRadio.length ; i++) {
	
		if ( listRadio.item(i).checked ) {
			itemsChecked.push( listRadio.item(i).getAttribute(itemIDAttribute) + "#" + listRadio.item(i).getAttribute(itemNameAttribute));
		}
	}
	
	noItemsChecked = itemsChecked.length;
	
	for (var i in itemsChecked)
	{
		flag = i;
		
		for (var j in importItemsCurTarget)
		{
			if ( itemsChecked[i].split("#")[1] == importItemsCurTarget[j] )
			{
				flag = -1;
				importItemsDuplicated.push( itemsChecked[i].split("#")[1] );
				break;
			}
		}
		if ( flag >= 0 )
		{
			ImportItemsSaved.push( itemsChecked[i].split("#")[0] );
		}
	}
}

warningMessages = "";

function saveCopiedImportItemsToImportReport() {
	
	warningMessages = "";
	// If have ImportItem(s) in Duplicating list
	// preparing the warning message
	if ( importItemsDuplicated.length > 0 ) {

		warningMessages += 
		"<b>[" + (importItemsDuplicated.length) + "/" + (noItemsChecked) + "]</b>:: "
		+ i18n_copy_items_duplicated
		+ "<br/><br/>";
		
		for (var i in importItemsDuplicated) {
		
			warningMessages +=
			"<b>(*)</b> "
			+ importItemsDuplicated[i] 
			+ "<br/><br/>";
		}
		
		warningMessages += "<br/>";
	}
	
	// If have also ImportItem(s) in Copying list
	// do copy and prepare the message notes
	if ( ImportItemsSaved.length > 0 ) {
	
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( saveCopiedImportItemsToImportReportReceived );	
		
		var params = "importReportDestId=" + byId("targetGroup").value;
			params += "&sheetNo=" + sheetId;
			
		for (var i in importItemsSaved)
		{
			params += "&itemIds=" + importItemsSaved[i];
		}
		
		request.sendAsPost(params);
		request.send( "copyImportItemsToImportReport.action");
	}
	// If have no any ImportItem(s) will be copied
	// and also have ImportItem(s) in Duplicating list
	else if ( importItemsDuplicated.length > 0 ) {

		setMessage( warningMessages );
	}
		
	closeDialog( dialog1 );
}

function saveCopiedImportItemsToImportReportReceived( data ) {
	
	var type = data.getAttribute("type");
	
	if ( type == "success" ) {
	
		warningMessages +=
		" ======= Sheet [" + sheetId + "] ========"
		+ "<br/><b>[" + (ImportItemsSaved.length) + "/" + (noItemsChecked) + "]</b>:: "
		+ i18n_copy_successful
		+ "<br/>======================<br/><br/>";

	}
	
	setMessage( warningMessages );
}

// ----------------------------------------------------
// Select Items ALL
// ----------------------------------------------------

function selectedItemsAll() {
    
	var checked = byId('checkAll').checked;
	
	var list = document.getElementsByName('importItemChecked');
	
	for (var i=0 ;i<list.length; i++)
	{
		list.item(i).checked = checked;
	}
}