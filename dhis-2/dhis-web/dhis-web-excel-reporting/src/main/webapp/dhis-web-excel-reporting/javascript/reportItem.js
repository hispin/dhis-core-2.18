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

/*
*	COPY REPORT ITEM 
*/
function copySelectedItem() {
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( copySelectedItemReceived );
	request.send( "getAllReportExcels.action" );

}

function copySelectedItemReceived( xmlObject ) {

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
*	Validate Copy Report Items
*/

sheetId = 0;
iReportItemsChecked = 0;
reportItems = null;
reportItemsCurTarget = null;
reportItemsDuplicated = null;

function validateCopyReportItems() {

	sheetId	= byId( "targetSheetNo" ).value;
	
	var message = '';
	
	if ( sheetId < 1 ) {
	
		message = input_sheet_no;
	}
	
	if ( byId("targetReport").value == -1 )
	{
		message += "<br/>"+ choose_report;
	}
	
	if ( message.length > 0 )
	{
		setMessage( message );
		return;
	}
	
	reportItemsCurTarget = null;
	reportItemsDuplicated = null;
	
	reportItemsCurTarget = new Array();
	reportItemsDuplicated = new Array();

	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validateCopyReportItemsReceived );
	
	var param = "reportId=" + byId("targetReport").value;
		param += "&sheetNo=" + sheetId;
	
	request.sendAsPost( param );
	request.send( "getReportExcelItems.action" );
	
}

function validateCopyReportItemsReceived( data ) {

	var items = data.getElementsByTagName('reportItem');
		
	for (var i = 0 ; i < items.length ; i ++) 
	{
		reportItemsCurTarget.push(items[i].getElementsByTagName('name')[0].firstChild.nodeValue);
	}
	
	splitDuplicatedReportItems();
	saveCopyItems();
}


function splitDuplicatedReportItems() {

	var flag = -1;
	var reportItemsChecked = new Array();
	var listRadio = document.getElementsByName( 'reportItemCheck' );

	reportItems = null;
	reportItems = new Array();
	
	for (var i = 0 ; i < listRadio.length ; i++) {
	
		if ( listRadio.item(i).checked ) {
			reportItemsChecked.push( listRadio.item(i).getAttribute("reportItemID") + "#" + listRadio.item(i).getAttribute("reportItemName"));
		}
	}
	
	iReportItemsChecked = reportItemsChecked.length;
	
	for (var i in reportItemsChecked)
	{
		flag = i;
		
		for (var j in reportItemsCurTarget)
		{
			if ( reportItemsChecked[i].split("#")[1] == reportItemsCurTarget[j] )
			{
				flag = -1;
				reportItemsDuplicated.push( reportItemsChecked[i].split("#")[1] );
				break;
			}
		}
		if ( flag >= 0 )
		{
			reportItems.push( reportItemsChecked[i].split("#")[0] );
		}
	}
}

function saveCopyItems() {
	
	var warningMessage = " ======= Sheet [" + sheetId + "] =======<br/>";
	
	// If have ReportItem(s) in Duplicating list
	// preparing the warning message
	if ( reportItemsDuplicated.length > 0 ) {

		warningMessage += 
		"<b>[" + (reportItemsDuplicated.length) + "/" + (iReportItemsChecked) + "]</b>:: "
		+ i18n_copy_items_duplicated
		+ "<br/><br/>";
		
		for (var i in reportItemsDuplicated) {
		
			warningMessage +=
			"<b>(*)</b> "
			+ reportItemsDuplicated[i] 
			+ "<br/><br/>";
		}
		
		warningMessage += "======================<br/><br/>";
	}
	
	// If have also ReportItem(s) in Copying list
	// do copy and prepare the message notes
	if ( reportItems.length > 0 ) {
	
		$.post("copyReportExcelItems.action",
		{
			reportId:$("#targetReport").val(),
			sheetNo:sheetId,
			reportItems:reportItems
		},
		function (data)
		{
			var data = data.getElementsByTagName("message")[0];	
			var type = data.getAttribute("type");
			
			if ( type == "success" ) {
				
				warningMessage +=
				"<br/><b>[" + (reportItems.length) + "/" + (iReportItemsChecked) + "]</b>:: "
				+ i18n_copy_successful
				+ "<br/>======================<br/><br/>";
			}
			
			setMessage( warningMessage );
			
		},'xml');
	}
	// If have no any ReportItem(s) will be copied
	// and also have ReportItem(s) in Duplicating list
	else if ( reportItemsDuplicated.length > 0 ) {

		setMessage( warningMessage );
	}
		
	$("#copyTo").hide();
	deleteDivEffect();
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
		
		var option = document.createElement( "option" );
		option.value = id ;
		option.text = elementName;
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

	var dataElementComboId = "[" + $("#availableDataElements").val() + "." + $("#optionCombos").val() + "]";
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
		selectedDataElementGroups.options.add(new Option(name, id));
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

	var availableDataElements = document.getElementById('availableDataElements_');
	availableDataElements.options.length = 0;
	var dataelEments = data.getElementsByTagName( "dataElement" );	
	
	for ( var i = 0; i < dataelEments.length; i++ )
	{			
		var id = dataelEments[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var name = dataelEments[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;       
		availableDataElements.options.add(new Option(name, id));
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
