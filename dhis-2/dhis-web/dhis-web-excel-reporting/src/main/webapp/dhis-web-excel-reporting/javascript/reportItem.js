/*
* 	Delete Report Excel Item
*/
function deleteReportExcelItem( id ) {

	if ( window.confirm(i18n_confirm_delete) ) {
		
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( Completed );
		request.send("deleteReportExcelItem.action?id=" + id);
	
	}
}

function Completed ( xmlObject ) {

	window.location.reload();
}

/*
* 	Get Report Excel Item by Sheet
*/
function getReportItemBySheet() {

	window.location = "listReportExcelItemAction.action?reportId=" +  getFieldValue("reportId") + "&sheetNo=" + getFieldValue("sheetNoFilter");
}

/*
* 	Open add report item
*/
function openAddReportItemForm() {

	byId( "reportItemButton" ).onclick = function(e) {
	
		validateAddReportExcelItem();
	}
	
	byId( "sheetNo" ).value = byId( "sheetNoFilter" ).value;		
	$( "#report" ).showAtCenter( true );
}	

/*
* 	Open update report item
*/

function openUpdateReportItem( id ) {
		
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( openUpdateReportItemReceived );
	request.send( "getReportExcelItem.action?id=" + id );
	
}	

function openUpdateReportItemReceived( xmlObject ) {
	
	byId( "id" ).value = getElementValue( xmlObject, 'id' );
	byId( "name" ).value = getElementValue( xmlObject, 'name' );
	byId( "itemType" ).value = getElementValue( xmlObject, 'itemType' );
	byId( "periodType" ).value = getElementValue( xmlObject, 'periodType' );
	byId( "row" ).value = getElementValue( xmlObject, 'row' );
	byId( "column" ).value = getElementValue( xmlObject, 'column' );
	byId( "expression" ).value = getElementValue( xmlObject, 'expression' );
	byId( "sheetNo" ).value = getElementValue( xmlObject, 'sheetNo' );
	
	byId( "reportItemButton" ).onclick = function(e) {
	
		validateUpdateReportExcelItem();
	};
	
	$("#report").showAtCenter( true );	
}

/*
* 	Validate Add Report Excel Item
*/

function validateAddReportExcelItem() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validateAddReportExcelItemReceived );
	
	var params = "name=" + byId("name").value;
	params += "&expression=" + byId("expression").value;
	params += "&row=" + byId("row").value;
	params += "&column=" + byId("column").value;
	params += "&sheetNo=" + byId("sheetNo").value;
	params += "&reportId=" + byId("reportId").value;
	
	request.sendAsPost(params);
	request.send( "validateAddReportExcelItem.action" );
	
}

function validateAddReportExcelItemReceived( xmlObject ) {

	var type = xmlObject.getAttribute( 'type' );
	
	if ( type =='error' )
	{
		setMessage( xmlObject.firstChild.nodeValue );
	}
	
	if ( type=='success' )
	{
		addReportExcelItem();    
	}
}

/*
* 	Add Report Excel Item
*/
	
function addReportExcelItem() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( Completed );
	
	var params = "name=" + byId("name").value;
	params += "&expression=" + byId("expression").value;
	params += "&row=" + byId("row").value;
	params += "&column=" + byId("column").value;
	params += "&reportId=" + reportId;
	params += "&itemType=" + byId("itemType").value;
	params += "&periodType=" + byId("periodType").value;
	params += "&sheetNo=" + byId("sheetNo").value;
	
	request.sendAsPost(params);
	request.send("addReportExcelItem.action");
	
}

/*
* 	Validate Update Report Excel Item
*/

function validateUpdateReportExcelItem() {

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validateUpdateReportExcelItemReceived );
	
	var params = "name=" + byId("name").value;
	params += "&reportItemId=" + byId("id").value;
	params += "&expression=" + byId("expression").value;
	params += "&row=" + byId("row").value;
	params += "&column=" + byId("column").value;
	params += "&sheetNo=" + byId("sheetNo").value;
	params += "&reportId=" + reportId;
	
	request.sendAsPost(params);
	request.send( "validateUpdateReportExcelItem.action" );
	
}

function validateUpdateReportExcelItemReceived( xmlObject ) {
	
	var type = xmlObject.getAttribute( 'type' );
	
	if ( type =='error' )
	{
		setMessage( xmlObject.firstChild.nodeValue );
	}
	
	if ( type =='success' )
	{
		updateReportExcelItem();    
	}
}

/*
* 	Update Report Excel Item
*/

function updateReportExcelItem() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( Completed );
	
	var params = "id=" + byId("id").value;
		params += "&name=" + byId("name").value;
		params += "&expression=" + byId("expression").value;
		params += "&row=" + byId("row").value;
		params += "&column=" + byId("column").value;
		params += "&reportId=" + reportId;
		params += "&itemType=" + byId("itemType").value;
		params += "&periodType=" + byId("periodType").value;
		params += "&sheetNo=" + byId("sheetNo").value;
	
	request.sendAsPost(params);
	request.send("updateReportExcelItem.action");
	
}

function insertFormulaText( sourceId, targetId ) {

	byId(targetId).value += byId(sourceId).value;
}

function insertOperation( target, value ) {

	byId(target).value += value;
}

function selectALL( checked ) {

	var listRadio = document.getElementsByName('reportItemCheck');
	
	for (var i = 0 ; i < listRadio.length ; i++) {
	
		listRadio.item(i).checked = checked;
	}
}

/**
*	COPY REPORT ITEM(s) TO ANOTHER REPORTEXCEL
*/
//function copySelectedItem() {
function copySelectedReportItemToReport() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( copySelectedReportItemToReportReceived );
	request.send( "getAllReportExcels.action" );

}

function copySelectedReportItemToReportReceived( xmlObject ) {

	var reports = xmlObject.getElementsByTagName("report");
	var selectList = document.getElementById("targetReport");
	var options = selectList.options;
	
	options.length = 0;
	
	for( var i = 0 ; i < reports.length ; i++ ) {
	
		var id = reports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = reports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
	
	$("#copyTo").showAtCenter( true );
}


/*
*	Validate Copy Report Items to another ReportExcel
*/

sheetId = 0;
NoItemsChecked = 0;
ItemsSaved = null;
itemsCurTarget = null;
itemsDuplicated = null;

function validateCopyReportItemsToReportExcel() {

	sheetId	= byId( "targetSheetNo" ).value;
	
	var message = '';
	
	if ( sheetId < 1 ) {
	
		message = i18n_input_sheet_no;
	}
	
	if ( byId("targetReport").value == -1 )
	{
		message += "<br/>"+ i18n_choose_report;
	}
	
	if ( message.length > 0 )
	{
		setMessage( message );
		return;
	}
	
	itemsCurTarget = null;
	itemsDuplicated = null;
	
	itemsCurTarget = new Array();
	itemsDuplicated = new Array();

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validateCopyReportItemsToReportExcelReceived );
	
	var param = "reportId=" + byId("targetReport").value;
		param += "&sheetNo=" + sheetId;
	
	request.sendAsPost( param );
	request.send( "getReportExcelItems.action" );
	
}

function validateCopyReportItemsToReportExcelReceived( data ) {

	var items = data.getElementsByTagName('reportItem');
		
	for (var i = 0 ; i < items.length ; i ++) 
	{
		itemsCurTarget.push(items[i].getElementsByTagName('name')[0].firstChild.nodeValue);
	}
	
	splitDuplicatedItems( 'reportItemCheck', 'reportItemID', 'reportItemName' );
	saveCopyReportItemsToReportExcel();
}

function splitDuplicatedItems( itemCheckID, itemIDAttribute, itemNameAttribute ) {

	var flag = -1;
	var itemsChecked = new Array();
	var listRadio = document.getElementsByName( itemCheckID );

	ItemsSaved = null;
	ItemsSaved = new Array();
	
	for (var i = 0 ; i < listRadio.length ; i++) {
	
		if ( listRadio.item(i).checked ) {
			itemsChecked.push( listRadio.item(i).getAttribute(itemIDAttribute) + "#" + listRadio.item(i).getAttribute(itemNameAttribute));
		}
	}
	
	NoItemsChecked = itemsChecked.length;
	
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

function saveCopyReportItemsToReportExcel() {
	
	var warningMessage = " ======= Sheet [" + sheetId + "] =======<br/>";
	
	// If have ReportItem(s) in Duplicating list
	// preparing the warning message
	if ( itemsDuplicated.length > 0 ) {

		warningMessage += 
		"<b>[" + (itemsDuplicated.length) + "/" + (NoItemsChecked) + "]</b>:: "
		+ i18n_copy_items_duplicated
		+ "<br/><br/>";
		
		for (var i in itemsDuplicated) {
		
			warningMessage +=
			"<b>(*)</b> "
			+ itemsDuplicated[i] 
			+ "<br/><br/>";
		}
		
		warningMessage += "======================<br/><br/>";
	}
	
	// If have also ReportItem(s) in Copying list
	// do copy and prepare the message notes
	if ( ItemsSaved.length > 0 ) {
	
		$.post("copyReportExcelItems.action",
		{
			reportId:$("#targetReport").val(),
			sheetNo:sheetId,
			reportItems:ItemsSaved
		},
		function (data)
		{
			var data = data.getElementsByTagName("message")[0];	
			var type = data.getAttribute("type");
			
			if ( type == "success" ) {
				
				warningMessage +=
				"<br/><b>[" + (ItemsSaved.length) + "/" + (NoItemsChecked) + "]</b>:: "
				+ i18n_copy_successful
				+ "<br/>======================<br/><br/>";
			}
			
			setMessage( warningMessage );
			
		},'xml');
	}
	// If have no any ReportItem(s) will be copied
	// and also have ReportItem(s) in Duplicating list
	else if ( itemsDuplicated.length > 0 ) {

		setMessage( warningMessage );
	}
		
	$("#copyTo").hide();
	deleteDivEffect();
}


/** 
*	COPY SELECTED REPORTITEM(s) TO EXCEL_ITEM_GROUP
*/

//function copySelectedExcelItemForm() {
function copySelectedReportItemToExcelItemGroup() {
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( copySelectedReportItemToExcelItemGroupReceived );
	request.send( "getAllExcelItemGroup.action" );
}

function copySelectedReportItemToExcelItemGroupReceived( xmlObject ) {
	
	var groups = xmlObject.getElementsByTagName("excelitemgroup");
	var selectList = document.getElementById("targetExcelItemGroup");
	var options = selectList.options;
	
	options.length = 0;
	
	for( var i = 0 ; i < groups.length ; i++ ) {
	
		var id = groups[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = groups[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
	
	$("#copyToExcelItem").showAtCenter( true );
}

/*
*	Validate copy Report Items to Excel Item Group
*/

//function validateCopyExcelItems() {
function validateCopyReportItemsToExcelItemGroup() {

	sheetId	= byId("targetExcelItemGroupSheetNo").value;
	
	var message = '';
	
	if ( sheetId < 1 )
	{
		message = i18n_input_sheet_no;
	}
	
	if ( byId("targetExcelItemGroup").value == -1 )
	{
		message += "<br/>" + i18n_choose_report;
	}
	
	if ( message.length > 0 )
	{
		setMessage( message );
		return;
	}
	
	itemsCurTarget = null;
	itemsDuplicated = null;
	
	itemsCurTarget = new Array();
	itemsDuplicated = new Array();
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateCopyReportItemsToExcelItemGroupReceived );
	request.send( "getExcelItemsByGroup.action?excelItemGroupId=" + byId("targetExcelItemGroup").value );
	
}

function validateCopyReportItemsToExcelItemGroupReceived( xmlObject ) {
	
	var items = xmlObject.getElementsByTagName('excelitem');
	
	for (var i = 0 ;  i < items.length ; i ++) {
	
		itemsCurTarget.push(items[i].getElementsByTagName('name')[0].firstChild.nodeValue);
	}
	
	splitDuplicatedItems( 'reportItemCheck', 'reportItemID', 'reportItemName' );
	
	saveCopiedReportItemsToExcelItemGroup();
}

warningMessages = "";

function saveCopiedReportItemsToExcelItemGroup() {
	
	warningMessages = "";
	
	// If have ReportItem(s) in Duplicating list
	// preparing the warning message
	if ( itemsDuplicated.length > 0 ) {

		warningMessages += 
		"<b>[" + (itemsDuplicated.length) + "/" + (NoItemsChecked) + "]</b>:: "
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
	
	// If have also ReportItem(s) in Copying list
	// do copy and prepare the message notes
	if ( ItemsSaved.length > 0 ) {
	
		var request = new Request();
		request.setResponseTypeXML( 'xmlObject' );
		request.setCallbackSuccess( saveCopyExcelItemsReceived );	
		
		var params = "excelItemGroupId=" + byId("targetExcelItemGroup").value;
			params += "&sheetNo=" + sheetId;
			
		for (var i in ItemsSaved)
		{
			params += "&reportItemIds=" + ItemsSaved[i];
		}
			
		request.sendAsPost(params);
		request.send( "copyExcelItems.action");
	}
	// If have no any ReportItem(s) will be copied
	// and also have ReportItem(s) in Duplicating list
	else if ( itemsDuplicated.length > 0 ) {

		setMessage( warningMessages );
	}
		
	hideById("copyToExcelItem");
	deleteDivEffect();
}

function saveCopyExcelItemsReceived( data ) {
	
	var type = data.getAttribute("type");
	
	if ( type == "success" ) {
	
		warningMessages +=
		" ======= Sheet [" + sheetId + "] ========"
		+ "<br/><b>[" + (ItemsSaved.length) + "/" + (NoItemsChecked) + "]</b>:: "
		+ i18n_copy_successful
		+ "<br/>======================<br/><br/>";
		
	}
	
	setMessage( warningMessages );
}


/**
* Open dataelement expression
*/
function openDataElementExpression() {

	byId( "formula" ).value = byId( "expression" ).value;
	
	getALLDataElementGroup();
	getDataElementsByGroup();
	enable("dataElementGroup");
	enable("availableDataElements");
	byId("availableDataElements").onchange = function(e){ getOptionCombos() };
	
	$("#normal").showAtCenter( true );
}

/**
* Get All dataelement group
*/
	
function getALLDataElementGroup() {

	var list = byId('dataElementGroup');
	
	list.options.length = 0;
	list.add( new Option( "ALL", "ALL" ), null );
	
	for ( id in dataElementGroups )
	{
		list.add( new Option( dataElementGroups[id], id ), null );
		//var option = new Option( dataElementGroups[id], id );
		//list.add( option , null );
	}
	
}

/**
* Get DataElements By Group
*/

function getDataElementsByGroup( )
{
	var dataElementGroupId = $("#dataElementGroup").val();
	var url = "../dhis-web-commons-ajax/getDataElements.action?id=" + $("#dataElementGroup").val();
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getDataElementsByGroupCompleted );
	request.send( url );	
}

function getDataElementsByGroupCompleted( xmlObject ){

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

function getOptionCombos() {
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getOptionCombosReceived );
	request.send( "getOptionCombos.action?dataElementId=" + byId("availableDataElements").value);

}

function getOptionCombosReceived( xmlObject ) {

	xmlObject = xmlObject.getElementsByTagName('categoryOptions')[0];
	
	var optionComboList = byId( "optionCombos" );			
	optionComboList.options.length = 0;		
	var optionCombos = xmlObject.getElementsByTagName( "categoryOption" );
	
	for ( var i = 0; i < optionCombos.length; i++)
	{
		var id = optionCombos[ i ].getAttribute('id');
		var name = optionCombos[ i ].firstChild.nodeValue;			
		var option = document.createElement( "option" );
		
		option.value = id ;
		option.text = name;
		optionComboList.add( option, null );	
	}
}

function insertDataElementId() {

	var dataElementComboId = "[" + byId("availableDataElements").value + "." + byId("optionCombos").value + "]";
	byId("formula").value += dataElementComboId;
}

/**
* Indicator Report item type
*/
function openIndicatorExpression() {

	byId("formulaIndicator").value = byId("expression").value;
	
	getIndicatorGroups();
	filterIndicators();	
	enable("indicatorGroups");
	enable("availableIndicators");
	setPositionCenter( 'indicatorForm' );
	
	$("#indicatorForm").show();
}

function getIndicatorGroups() {

	var list = byId('indicatorGroups');
	
	list.options.length = 0;
	list.add( new Option( "ALL", "ALL" ), null );
	
	for ( id in indicatorGroups )
	{
		list.add( new Option( indicatorGroups[id], id ), null );
	}
}

function filterIndicators() {

	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( filterIndicatorsCompleted );
	request.send( "../dhis-web-commons-ajax/getIndicators.action?id=" + $("#indicatorGroups").val());
}

function filterIndicatorsCompleted( xmlObject ) {

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
	}
}

/**
* Open Category Expression
*/
function openCategoryExpression() {
	
	byId("categoryFormula").value = byId("expression").value;
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( openCategoryExpressionReceived );
	request.send("getReportExcel.action?id=" + reportId);
	
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
	
	showDivEffect();
	$( "#category" ).show();	
}


/**
* Get DataElement Group Order
*/

function getDataElementGroupOrder() {
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getDataElementGroupOrderReceived );
	request.send( "getDataElementGroupOrder.action?id=" + $("#dataElementGroup_").val() );

}

function getDataElementGroupOrderReceived( data ) {

	var availableDataElements = byId('availableDataElements_');
	availableDataElements.options.length = 0;
	var dataelEments = data.getElementsByTagName( "dataElement" );	
	
	for ( var i = 0; i < dataelEments.length; i++ )
	{			
		var id = dataelEments[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var name = dataelEments[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;       
		//availableDataElements.options.add(new Option(name, id));
		var option = new Option( name, id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
		availableDataElements.add(option, null);
	}
}

function getOptionCombos_() {

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

function insertDataElementId_() {

	var dataElementComboId = "[*." + byId("optionCombos_").value + "]";
	byId("categoryFormula").value += dataElementComboId;
}
