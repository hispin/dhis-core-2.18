// ========================================================================================================================
// EXCEL ITEM GROUP
// ========================================================================================================================

// ========================================================================================================================
// Open Add Excel item form
// ========================================================================================================================

function openAddExcelItem() 
{	
	byId( "okButton" ).onclick = validateAddExcelItem;
	enable( "name" );
	$( "#divExcelitem" ).showAtCenter( true );
}

// ========================================================================================================================
// Open Update Excel item form
// ========================================================================================================================

function openUpdateExcelItem( id ) 
{	
	var request = new Request();
	request.setResponseTypeXML( 'excelItem' );
	request.setCallbackSuccess( openUpdateExcelItemReceived );	
	request.send( "getExcelItem.action?id=" + id );		
}

function openUpdateExcelItemReceived( xmlObject ) 
{
	
	byId("id").value = xmlObject.getElementsByTagName('id')[0].firstChild.nodeValue;
	byId("name").value = xmlObject.getElementsByTagName('name')[0].firstChild.nodeValue;
	byId("expression").value = xmlObject.getElementsByTagName('expression')[0].firstChild.nodeValue;
	byId("row").value = xmlObject.getElementsByTagName('row')[0].firstChild.nodeValue;
	byId("column").value = xmlObject.getElementsByTagName('column')[0].firstChild.nodeValue;
	byId("sheetNo").value = xmlObject.getElementsByTagName('sheetNo')[0].firstChild.nodeValue;	
	byId( "okButton" ).onclick = validateUpdateExcelItem;
	enable("name");
	$("#divExcelitem").showAtCenter( true );
	
}

// ========================================================================================================================
// Validate Add Excel item 
// ========================================================================================================================

function validateAddExcelItem() 
{
	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( validateAddExcelItemReceived );	
	var params = "name=" + byId("name").value;
	params += "&expression=" + byId("expression").value;
	params += "&row=" + byId("row").value;
	params += "&column=" + byId("column").value;
	params += "&sheetNo=" + byId("sheetNo").value;
	params += "&excelItemGroupId=" + byId( "excelItemGroupId" ).value; 
	
	
	request.sendAsPost( params );
	
	request.send( "validateExcelItem.action" );	
	
}

function validateAddExcelItemReceived( message )
{
	var type = message.getAttribute( 'type' );
	
	if ( type == 'error' )
	{
		setMessage(message.firstChild.nodeValue);		
	}
	else if ( type == 'success' )
	{				
		addExcelItem();		
	}
}
// ========================================================================================================================
// Validate Update Excel item 
// ========================================================================================================================

function validateUpdateExcelItem() 
{	
	var request = new Request();
	request.setResponseTypeXML( 'message' );
	request.setCallbackSuccess( validateUpdateExcelItemReceived );
	
	var params = "name=" + byId("name").value;
	params += "&expression=" + byId("expression").value;
	params += "&row=" + byId("row").value;
	params += "&column=" + byId("column").value;
	params += "&sheetNo=" + byId("sheetNo").value;
	params += "&excelItemGroupId=" + byId( "excelItemGroupId" ).value; 
	params += "&id=" + byId( "id" ).value; 
	
	request.sendAsPost( params );
	
	request.send( "validateExcelItem.action" );	
	
}

function validateUpdateExcelItemReceived( xmlObject ) 
{
	
	var type = xmlObject.getAttribute( 'type' );
	
	if ( type == 'error' )
	{
		setMessage(xmlObject.firstChild.nodeValue);
	}
	else if ( type == 'success' )
	{		
		updateExcelItem();		
	}
}
// ========================================================================================================================
// Add Excel item
// ========================================================================================================================

function addExcelItem() {
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( Completed );
	var params = "name=" + byId("name").value;	
	params += "&expression=" + byId("expression").value; 
	params += "&row=" + byId("row").value; 
	params += "&column=" + byId("column").value; 
	params += "&sheetNo=" + byId("sheetNo").value; 
	params += "&excelItemGroupId=" + byId( "excelItemGroupId" ).value; 
	request.sendAsPost( params );
	request.send( "addExcelItem.action" );
}

function Completed( xmlObject ) {

	window.location.reload();
}

// ========================================================================================================================
// Update Excel Item
// ========================================================================================================================

function updateExcelItem() {
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( Completed );
	
	var params = "id=" + byId("id").value; 
	params += "&name=" + byId("name").value;	
	params += "&expression=" + byId("expression").value; 
	params += "&row=" + byId("row").value; 
	params += "&column=" + byId("column").value; 
	params += "&sheetNo=" + byId("sheetNo").value; 
	params += "&excelItemGroupId=" + byId( "excelItemGroupId" ).value; 
	request.sendAsPost( params );
	request.send( "updateExcelItem.action" );	
	
}
// ===============================================================================
// Open Expression Form
// ===============================================================================

function openExpressionBuild() {
	
	byId("formula").value = byId("expression").value;
	
	getALLDataElementGroup();
	getDataElementsByGroup();
	enable("dataElementGroup");
	enable("availableDataElements");
	
	$( "#availableDataElements" ).change(getOptionCombos);		
	$( "#divExpression" ).showAtCenter( true );
	
}

// ===============================================================================
// Get all Dataelement Group
// ===============================================================================

function getALLDataElementGroup() {

	var list = byId( 'dataElementGroup' );
	
	list.options.length = 0;
	list.add( new Option( "ALL", "ALL" ), null );
	
	for ( id in dataElementGroups )
	{
		list.add( new Option( dataElementGroups[id], id ), null );
	}
}

// ===============================================================================
// Get DataElements by Group
// ===============================================================================

function getDataElementsByGroup()
{		
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getDataElementsByGroupCompleted );
    var url = "../dhis-web-commons-ajax/getDataElements.action?id=" + byId("dataElementGroup").value;
	request.send( url );	
}

function getDataElementsByGroupCompleted( xmlObject ) {

	var dataElementList = byId( "availableDataElements" );
		
	dataElementList.options.length = 0;
	
	var dataelements = xmlObject.getElementsByTagName( "dataElement" );
	
	for ( var i = 0; i < dataelements.length; i++)
	{
		var id = dataelements[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var elementName = dataelements[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		var option = new Option( elementName, id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
		dataElementList.add( option, null );		
	}
}

// ===============================================================================
// Get OptionCombos by DataElement
// ===============================================================================

function getOptionCombos() {
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getOptionCombosReceived);
	request.send( "getOptionCombos.action?dataElementId=" + byId("availableDataElements").value );	
}

function getOptionCombosReceived( xmlObject ) {
	
	xmlObject = xmlObject.getElementsByTagName('categoryOptions')[0];
	
	var optionComboList = byId( "optionCombos" );			
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

// ===============================================================================
// Insert dataelement's id into the Formular textbox
// ===============================================================================

function insertDataElementId() {

	var dataElementComboId = "[" + byId("availableDataElements").value + "." + byId("optionCombos").value + "]";
	byId("formula").value += dataElementComboId;
}

// ===============================================================================
// Insert operators into the Formular textbox
// ===============================================================================

function insertOperation( target, value ) {

	byId( target ).value += value;
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
	
	$("#copyTo").showAtCenter( true );
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
		
	hideById("copyTo");
	deleteDivEffect();
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