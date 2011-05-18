// ========================================================================================================================
// EXCEL ITEM GROUP
// ========================================================================================================================

function changeItemType()
{
	var type = getFieldValue( 'excelItemGroupType' );
	
	if( type == 'NORMAL' ){
		byId('expression-button' ).onclick = openExpressionBuild;
	}else {
		byId('expression-button' ).onclick = caExpressionBuilderForm;
	}	
}

// -----------------------------------------------------------------------
// Open Expression Form for Normal Excel-Item group
// -----------------------------------------------------------------------

// Open Expression Form
function openExpressionBuild() {
	
	byId("formula").value = byId("expression").value;
	dataDictionary.loadDataElementGroups( "#divExpression select[id=dataElementGroup]" );
	dataDictionary.loadAllDataElements( "#divExpression select[id=availableDataElements]" );
	
	showPopupWindowById( 'divExpression', 600, 300 );
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
	hideById('divExpression'); 
	unLockScreen();
}

// -----------------------------------------------------------------------
// Open Expression Form for Catagory Excel-Item group
// -----------------------------------------------------------------------

// Open Expression Form
function caExpressionBuilderForm()
{
	dataDictionary.loadDataElementGroups( "#divCategory select[id=dataElementGroup]" );
	dataDictionary.loadAllDataElements( "#divCategory select[id=availableDataElements]" );
	
	setFieldValue( 'divCategory textarea[id=formula]', getFieldValue('expression') );
	showPopupWindowById( 'divCategory', 600, 320 );				
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
	hideById('divCategory'); 
	unLockScreen();
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
// SAVE COPY EXCEL ITEM(s) TO EXCEL_ITEM_GROUP
// -----------------------------------------------------------------------

sheetId = 0;
noItemsChecked = 0;
ExcelItemsSaved = null;
excelItemsCurTarget = null;
excelItemsDuplicated = null;

function copySelectedItemToGroup() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( copySelectedItemToGroupReceived );
	request.send( "getAllExcelItemGroup.action" );

}

function copySelectedItemToGroupReceived( xmlObject ) {

	var reports = xmlObject.getElementsByTagName("excelitemgroup");
	var selectList = document.getElementById("targetGroup");
	var options = selectList.options;
	
	options.length = 0;
	
	for( var i = 0 ; i < reports.length ; i++ ) {
	
		var id = reports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = reports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
	
	showPopupWindowById( 'copyTo', 480, 120 );
}

function validateCopyExcelItemsToExcelItemGroup() {

	sheetId	= byId("targetSheetNo").value;
	
	var message = '';
	
	if ( sheetId < 1 )
	{
		message = i18n_input_sheet_no;
	}
	
	if ( byId("targetGroup").value == -1 )
	{
		message += "<br/>" + i18n_choose_report;
	}
	
	if ( message.length > 0 )
	{
		setMessage( message );
		return;
	}
	
	excelItemsCurTarget = null;
	excelItemsDuplicated = null;
	
	excelItemsCurTarget = new Array();
	excelItemsDuplicated = new Array();
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateCopyExcelItemsToExcelItemGroupReceived );
	request.send( "getExcelItemsByGroup.action?excelItemGroupId=" + byId("targetGroup").value );
	
}

function validateCopyExcelItemsToExcelItemGroupReceived( xmlObject ) {
	
	var items = xmlObject.getElementsByTagName('excelitem');
	
	for (var i = 0 ;  i < items.length ; i ++) {
	
		excelItemsCurTarget.push(items[i].getElementsByTagName('name')[0].firstChild.nodeValue);
	}
	
	splitDuplicatedExcelItems( 'excelItemChecked', 'excelItemID', 'excelItemName' );
	
	saveCopiedExcelItemsToExcelItemGroup();
}

function splitDuplicatedExcelItems( itemCheckID, itemIDAttribute, itemNameAttribute ) {

	var flag = -1;
	var itemsChecked = new Array();
	var listRadio = document.getElementsByName( itemCheckID );

	ExcelItemsSaved = null;
	ExcelItemsSaved = new Array();
	
	for (var i = 0 ; i < listRadio.length ; i++) {
	
		if ( listRadio.item(i).checked ) {
			itemsChecked.push( listRadio.item(i).getAttribute(itemIDAttribute) + "#" + listRadio.item(i).getAttribute(itemNameAttribute));
		}
	}
	
	noItemsChecked = itemsChecked.length;
	
	for (var i in itemsChecked)
	{
		flag = i;
		
		for (var j in excelItemsCurTarget)
		{
			if ( itemsChecked[i].split("#")[1] == excelItemsCurTarget[j] )
			{
				flag = -1;
				excelItemsDuplicated.push( itemsChecked[i].split("#")[1] );
				break;
			}
		}
		if ( flag >= 0 )
		{
			ExcelItemsSaved.push( itemsChecked[i].split("#")[0] );
		}
	}
}

warningMessages = "";

function saveCopiedExcelItemsToExcelItemGroup() {
	
	warningMessages = "";
	// If have ReportItem(s) in Duplicating list
	// preparing the warning message
	if ( excelItemsDuplicated.length > 0 ) {

		warningMessages += 
		"<b>[" + (excelItemsDuplicated.length) + "/" + (noItemsChecked) + "]</b>:: "
		+ i18n_copy_items_duplicated
		+ "<br/><br/>";
		
		for (var i in excelItemsDuplicated) {
		
			warningMessages +=
			"<b>(*)</b> "
			+ excelItemsDuplicated[i] 
			+ "<br/><br/>";
		}
		
		warningMessages += "<br/>";
	}
	
	// If have also ReportItem(s) in Copying list
	// do copy and prepare the message notes
	if ( ExcelItemsSaved.length > 0 ) {
	
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( saveCopiedExcelItemsToExcelItemGroupReceived );	
		
		var params = "excelItemGroupDestId=" + byId("targetGroup").value;
			params += "&sheetNo=" + sheetId;
			
		for (var i in ExcelItemsSaved)
		{
			params += "&itemIds=" + ExcelItemsSaved[i];
		}
			
		request.sendAsPost(params);
		request.send( "copyExcelItemsToGroup.action");
	}
	// If have no any ReportItem(s) will be copied
	// and also have ReportItem(s) in Duplicating list
	else if ( excelItemsDuplicated.length > 0 ) {

		setMessage( warningMessages );
	}
		
	hideById('copyTo'); 
	unLockScreen();
}

function saveCopiedExcelItemsToExcelItemGroupReceived( data ) {
	
	var type = data.getAttribute("type");
	
	if ( type == "success" ) {
	
		warningMessages +=
		" ======= Sheet [" + sheetId + "] ========"
		+ "<br/><b>[" + (ExcelItemsSaved.length) + "/" + (noItemsChecked) + "]</b>:: "
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
	
	var list = document.getElementsByName('excelItemChecked');
	
	for (var i=0 ;i<list.length; i++)
	{
		list.item(i).checked = checked;
	}
}