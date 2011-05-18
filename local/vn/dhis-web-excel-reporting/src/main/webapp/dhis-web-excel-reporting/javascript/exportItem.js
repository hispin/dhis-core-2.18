function changeItemType()
{
	value = getFieldValue( 'itemType' );
	enable( 'expression-button' );
	
	setFieldValue( 'reportItem input[id=expression]', getFieldValue( 'reportItem input[id=currentExpression]') );
	
	if( value == 'dataelement' ){
		byId('expression-button' ).onclick = deExpressionBuilderForm;
	}else if( value == 'indicator' ){
		byId('expression-button' ).onclick = inExpressionBuilderForm ;
	}else if( value == 'formulaexcel' ){
		byId('expression-button' ).onclick = excelFormulaExpressionBuilderForm ;
	}else if( value == 'organisation' || value == 'serial' || value == 'dataelement_code' || value == 'dataelement_name' ){
		disable( 'expression-button' );
		setFieldValue( 'reportItem input[id=expression]', value );
		removeValidatorRulesById( 'reportItem input[id=expression]' );
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


function validateAddReportExcelItem( form )
{
	jQuery.postJSON('validationReportExcelItem.action',
	{
		reportId: getFieldValue( 'reportId' ),
		sheetNo: getFieldValue( 'sheetNo' ),
		row: getFieldValue( 'row' ),
		column: getFieldValue( 'column' ),
		name: getFieldValue( 'name' )

	},function( json ){
		if(json.response == 'success'){					
			form.submit();
		}else{
			showErrorMessage( json.message );
		}
	});
}

function validateUpdateReportExcelItem( form )
{
	jQuery.postJSON('validationReportExcelItem.action',
	{
		id: getFieldValue( 'id' ),
		reportId: getFieldValue( 'reportId' ),
		sheetNo: getFieldValue( 'sheetNo' ),
		row: getFieldValue( 'row' ),
		column: getFieldValue( 'column' ),
		name: getFieldValue( 'name' )

	},function( json ){
		if(json.response == 'success'){					
			form.submit();
		}else{
			showErrorMessage( json.message );
		}
	});
}

/*
*	Delete multi report item
*/

function deleteMultiReportItem( confirm )
{
	if ( window.confirm( confirm ) ) 
	{			
		var listRadio = document.getElementsByName( 'reportItemCheck' );
		var url = "deleteMultiReportItem.action?";
		var j = 0;
		
		for( var i=0; i < listRadio.length; i++ )
		{
			var item = listRadio.item(i);
			
			if( item.checked == true )
			{		
				url += "ids=" + item.getAttribute( 'reportItemID' );
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
*	COPY REPORT ITEM(s) TO ANOTHER REPORTEXCEL
*/
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
	
	showPopupWindowById( 'copyToReport', 450, 170 );
}


/*
*	Validate Copy Report Items to another ReportExcel
*/

sheetId = 0;
NumberOfItemsChecked = 0;
ItemsSaved = null;
itemsCurTarget = null;
itemsDuplicated = null;
warningMessages = "";

function validateCopyReportItemsToReportExcel() {

	sheetId	= $( "#targetSheetNo" ).val();
	
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
	request.send( "getReportExcelItemsBySheet.action" );
	
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

function saveCopyReportItemsToReportExcel() {
	
	warningMessages = " ======= Sheet [" + sheetId + "] ========";
	
	// If have ReportItem(s) in Duplicating list
	// Preparing the warning message
	if ( itemsDuplicated.length > 0 )
	{
		setUpDuplicatedItemsMessage();
	}
	
	// If have also ReportItem(s) in Copying list
	// Do copy and prepare the message notes
	if ( ItemsSaved.length > 0 )
	{
		var url = "copyReportExcelItems.action";
			url += "?reportId=" + $("#targetReport").val();
			url += "&sheetNo=" + sheetId;
			
		for (var i in ItemsSaved)
		{
			url += "&reportItems=" + ItemsSaved[i];
		}
		
		executeCopyItems( url );
	}
	// If have no any ReportItem(s) will be copied
	// and also have ReportItem(s) in Duplicating list
	else if ( itemsDuplicated.length > 0 )
	{
		setMessage( warningMessages );
	}
		
	hideById('copyToReport'); 
	unLockScreen();
}


/** 
*	COPY SELECTED REPORTITEM(s) TO EXCEL_ITEM_GROUP
*/

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
	
	showPopupWindowById("copyToExcelItem", 450,180 );
}

/*
*	Validate copy Report Items to Excel Item Group
*/

function validateCopyReportItemsToExcelItemGroup() {

	sheetId	= $("#targetExcelItemGroupSheetNo").val();
	
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

function saveCopiedReportItemsToExcelItemGroup() {
	
	warningMessages = " ======= Sheet [" + sheetId + "] ========";
	
	// If have ReportItem(s) in Duplicating list
	// preparing the warning message
	if ( itemsDuplicated.length > 0 )
	{
		setUpDuplicatedItemsMessage();
	}
	
	// If have also ReportItem(s) in Copying list
	// do copy and prepare the message notes
	if ( ItemsSaved.length > 0 )
	{
		var url = "copyExcelItems.action";
			url += "?excelItemGroupId=" + $("#targetExcelItemGroup").val();
			url += "&sheetNo=" + sheetId;
			
		for (var i in ItemsSaved)
		{
			url += "&reportItemIds=" + ItemsSaved[i];
		}
	
		executeCopyItems( url );
	}
	// If have no any ReportItem(s) will be copied
	// and also have ReportItem(s) in Duplicating list
	else if ( itemsDuplicated.length > 0 )
	{
		setMessage( warningMessages );
	}
		
	hideById("copyToExcelItem");
	unLockScreen();
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
				+ "<br/>======================<br/><br/>";
			}
			
			setMessage( warningMessages );
		}
	);
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
	if(mode=='update'){
		updateFormulaText( 'formula' );
	}
	
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
		
		var option = new Option( elementName, id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
		dataElementList.add( option, null );
	}
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
	
	var formula = byId("formulaIndicator").value;
	for ( id in indicatorGroups )
	{
		list.add(  new Option( indicatorGroups[id], id ), null );
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

	var optionCombo = byId("optionCombos_");
	var dataElementComboId = "[*." + optionCombo.value + "]";
	byId("categoryFormula").value = dataElementComboId;
	byId("categoryFormulaDiv").innerHTML = "*." + optionCombo[optionCombo.selectedIndex].text ;
}

function clearFormula(formulaFieldName){
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
