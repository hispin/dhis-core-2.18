/*
* 	Delete Report Excel Item
*/
function deleteReportExcelItem( id ){
	if(window.confirm(i18n_confirm_delete)){
		$.get("deleteReportExcelItem.action",{id:id}, function(data){
			window.location.reload();
		});		
	}		
}
/*
* 	Get Report Excel Item by Sheet
*/
function getReportItemBySheet(){
	window.location = "listReportExcelItemAction.action?reportId=" +  getFieldValue("reportId") + "&sheetNo=" + getFieldValue("sheetNoFilter");
}

/*
* 	Open add report item
*/
function openAddReportItemForm(){
	$("#reportItemButton").click(validateAddReportExcelItem);
	$("#sheetNo").val($("#sheetNoFilter").val());		
	$("#report").showAtCenter( true );	
}	

/*
* 	Open update report item
*/

function openUpdateReportItem( id ){
	$.get("getReportExcelItem.action",{id:id}, function(data){
		
		var reportItem = data.getElementsByTagName('reportItem')[0];
		
		$("#id").val(getElementValue(reportItem, 'id'));		
		$("#name").val(getElementValue(reportItem, 'name'));
		$("#itemType").val(getElementValue(reportItem, 'itemType'));
		$("#periodType").val(getElementValue(reportItem, 'periodType'));		
		$("#row").val(getElementValue(reportItem, 'row'));
		$("#column").val(getElementValue(reportItem, 'column'));		
		$("#expression").val(getElementValue(reportItem, 'expression'));
		$("#sheetNo").val(getElementValue(reportItem, 'sheetNo'));					
		$("#reportItemButton").click(validateUpdateReportExcelItem);
		$("#report").showAtCenter( true );		
		
	},"xml");
	
}	

/*
* 	Validate Add Report Excel Item
*/

function validateAddReportExcelItem(){
	
	$.post("validateAddReportExcelItem.action",{		
		name:$("#name").val(),		
		expression:$("#expression").val(),
		row:$("#row").val(),
		column:$("#column").val(),		
		reportId:reportId
	}, function (data){
		var xmlObject = data.getElementsByTagName('message')[0];
		var type = xmlObject.getAttribute( 'type' );
		if(type=='error')
		{
			setMessage(xmlObject.firstChild.nodeValue);
		}
		if(type=='success')
		{
			addReportExcelItem();    
		}
	},'xml');	
	
}

function addReportExcelItem(){
	$.post("addReportExcelItem.action",{
		name:$("#name").val(),		
		expression:$("#expression").val(),
		row:$("#row").val(),
		column:$("#column").val(),		
		reportId:reportId,
		itemType:$("#itemType").val(),
		periodType:$("#periodType").val(),
		sheetNo:$("#sheetNo").val()
	}, function (data){
		window.location.reload();
	},'xml');
}

/*
* 	Validate Update Report Excel Item
*/

function validateUpdateReportExcelItem(){
	
	$.post("validateUpdateReportExcelItem.action",{
		name:$("#name").val(),
		reportItemId:$("#id").val(),
		expression:$("#expression").val(),
		row:$("#row").val(),
		column:$("#column").val(),		
		reportId:reportId
	}, function (data){
		var xmlObject = data.getElementsByTagName('message')[0];
		var type = xmlObject.getAttribute( 'type' );
		if(type=='error')
		{
			setMessage(xmlObject.firstChild.nodeValue);
		}
		if(type=='success')
		{
			updateReportExcelItem();    
		}
	},'xml');	
	
}

function updateReportExcelItem(){
	$.post("updateReportExcelItem.action",{
		id:$("#id").val(),
		name:$("#name").val(),		
		expression:$("#expression").val(),
		row:$("#row").val(),
		column:$("#column").val(),		
		reportId:reportId,
		itemType:$("#itemType").val(),
		periodType:$("#periodType").val(),
		sheetNo:$("#sheetNo").val()
	}, function (data){
		window.location.reload();
	},'xml');	
}



function insertFormulaText(sourceId, targetId) {	
	$("#" + targetId).html($("#"+targetId).html() + $("#"+sourceId).val());
}

function insertOperation(target, value ){
	$("#" + target).html($("#" + target).html() + value);
}

function selectALL( checked ){
	var listRadio = document.getElementsByName('reportItemCheck');	
	for(var i=0;i<listRadio.length;i++){
		listRadio.item(i).checked = checked;
	}
}

/*
*	COPY REPORT ITEM 
*/
function copySelectedItem(){
	$.post("getAllReportExcels.action",{},
	function (xmlObject){
		xmlObject = xmlObject.getElementsByTagName('reports')[0];
		var reports = xmlObject.getElementsByTagName("report");
		var selectList = document.getElementById("targetReport");
		var options = selectList.options;
		options.length = 0;
		for(i=0;i<reports.length;i++){
			var id = reports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
			var name = reports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
			options.add(new Option(name,id), null);
		}	
	$("#copyTo").showAtCenter( false );
	},'xml');	
}

function saveCopyItems(){
	var reportItems = new Array();	
	var listRadio = document.getElementsByName('reportItemCheck');	
	for(var i=0;i<listRadio.length;i++){
		if(listRadio.item(i).checked){
			reportItems.push(listRadio.item(i).value);
		}
	}	
	$.post("copyReportExcelItems.action",{
		reportId:$("#targetReport").val(),
		sheetNo:$("#targetSheetNo").val(),
		reportItems:reportItems
	}, function (data) {
		$("#copyTo").hide();
	},'xml');		
}

/**
* DataElement Report type
*/
function openDataElementExpression(){
	$("#formula").html($("#expression").val());
	getALLDataElementGroup();
	getDataElementsByGroup();
	$("#dataElementGroup").attr("disabled", false);
	$("#availableDataElements").attr("disabled", false);
	$("#availableDataElements").change(getOptionCombos);		
	$("#normal").showAtCenter( true );
}

function getALLDataElementGroup(){
	var list = byId('dataElementGroup');
	list.options.length = 0;
	list.add( new Option( "ALL", "ALL" ), null );
	for ( id in dataElementGroups )
	{
		list.add( new Option( dataElementGroups[id], id ), null );
	}
}

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

function getOptionCombos(){
	$.get("getOptionCombos.action",{dataElementId:$("#availableDataElements").val()},
	function(xmlObject){
		var xmlObject = xmlObject.getElementsByTagName('optionCombo')[0];
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
	,'xml');	
}

function insertDataElementId(){
	var dataElementComboId = "[" + $("#availableDataElements").val() + "." + $("#optionCombos").val() + "]";
	$("#formula").html($("#formula").html() + dataElementComboId);
}

/**
* Indicator Report item type
*/
function openIndicatorExpression(){
	$("#formulaIndicator").html($("#expression").val());
	getIndicatorGroups();
	filterIndicators();	
	$("#indicatorGroups").attr("disabled", false);
	$("#availableIndicators").attr("disabled", false);
	setPositionCenter( 'indicatorForm' );	
	$("#indicatorForm").show();
}

function getIndicatorGroups(){
	var list = byId('indicatorGroups');
	list.options.length = 0;
	list.add( new Option( "ALL", "ALL" ), null );
	for ( id in indicatorGroups )
	{
		list.add( new Option( indicatorGroups[id], id ), null );
	}
}

function filterIndicators(){
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( filterIndicatorsCompleted );
	request.send( "../dhis-web-commons-ajax/getIndicators.action?id=" + $("#indicatorGroups").val());
}
function filterIndicatorsCompleted( xmlObject ){
	var indiatorList = byId( "availableIndicators" );
	indiatorList.options.length = 0;
	
	var indicators = xmlObject.getElementsByTagName( "indicator" );
	for ( var i = 0; i < indicators.length; i++)
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
* Category Report item type
*/
function openCategoryExpression(){
	$("#categoryFormula").html($("#expression").val());
	$.get("getReportExcel.action",{id:reportId},		
	function(data){
		var selectedDataElementGroups = document.getElementById('dataElementGroup_');
		selectedDataElementGroups.options.length = 0;
		var dataElementGroups = data.getElementsByTagName('dataElementGroups')[0].getElementsByTagName('dataElementGroup');
		for(var i=0;i<dataElementGroups.length;i++){
			var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
			var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
			selectedDataElementGroups.options.add(new Option(name, id));
		}	
		getDataElementGroupOrder();
		setPositionCenter( 'category' );
		$("#dataElementGroup_").attr("disabled", false);
		$("#availableDataElements_").attr("disabled", false);
		$("#availableDataElements_").change(getOptionCombos_);
		showDivEffect();
		$("#category").show();			
			
	},'xml');	
	
}

function getDataElementGroupOrder(){
	$.get("getDataElementGroupOrder.action",{id:$("#dataElementGroup_").val()},
	function( data ){
		data = data.getElementsByTagName('dataElements')[0];
		var availableDataElements = document.getElementById('availableDataElements_');
		availableDataElements.options.length = 0;
		var dataelEments = data.getElementsByTagName( "dataElement" );	
		for ( var i = 0; i < dataelEments.length; i++ )
		{			
			var id = dataelEments[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
			var name = dataelEments[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;       
			availableDataElements.options.add(new Option(name, id));
		}
	},'xml');
}

function getOptionCombos_(){
	$.get("getOptionCombos.action",{dataElementId:$("#availableDataElements_").val()},
	function(xmlObject){
		var xmlObject = xmlObject.getElementsByTagName('optionCombo')[0];
		xmlObject = xmlObject.getElementsByTagName('categoryOptions')[0];		
		var optionComboList = byId( "optionCombos_" );			
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
	,'xml');
}

function insertDataElementId_(){
	var dataElementComboId = "[*." + $("#optionCombos_").val() + "]";
	$("#categoryFormula").html($("#categoryFormula").html() + dataElementComboId);
}

/*
* Organisation Unit Listing Report
*/


