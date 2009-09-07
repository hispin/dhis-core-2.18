function deleteReportItem( id ){
	if(window.confirm(i18n_confirm_delete)){
		$.get("deleteReportItem.action",{id:id}, function(data){
			window.location.reload();
		});		
	}		
}	

function openAddReportItemForm(){
	$("#sheetNo").val($("#sheetNoFilter").val());		
	setPositionCenter( 'report' );
	showDivEffect();
	$("#report").show();
	mode = "ADD";
}

function openUpdateReportItem( id ){
	$.get("getReportItem.action",{id:id}, function(data){
		
		var reportItem = data.getElementsByTagName('reportItem')[0];
		
		$("#id").val(getElementValue(reportItem, 'id'));		
		$("#name").val(getElementValue(reportItem, 'name'));
		$("#itemType").val(getElementValue(reportItem, 'itemType'));
		$("#periodType").val(getElementValue(reportItem, 'periodType'));		
		$("#row").val(getElementValue(reportItem, 'row'));
		$("#column").val(getElementValue(reportItem, 'column'));		
		$("#expression").val(getElementValue(reportItem, 'expression'));
		$("#sheetNo").val(getElementValue(reportItem, 'sheetNo'));	
				
		setPositionCenter( 'report' );
		showDivEffect();
		$("#report").show();
		mode = "UPDATE";
		
	},"xml");
	
}	


function filterSheet(){
	window.location = "listReportItem.action?reportId=" +  getFieldValue("reportId") + "&sheetNo=" + getFieldValue("sheetNoFilter");
}
function validateAddReportItem(){
	
	$.post("validateReportItem.action",{
		name:$("#name").val(),
		reportItemId:$("#id").val(),
		expression:$("#expression").val(),
		row:$("#row").val(),
		column:$("#column").val(),
		mode:mode,
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
			if(mode=='ADD'){		
				addReportItem();
			}else{		
				updateReportItem();
			}      
		}
	},'xml');
	
	
	
}

function addReportItem(){
	$.post("addReportItem.action",{
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

function updateReportItem(){
	$.post("updateReportItem.action",{
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

function copySelectedItem(){
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( copySelectedItemCompleted );
	request.send( "getALLReportAjax.action");	
	
}

function copySelectedItemCompleted(xmlObject){
	var reports = xmlObject.getElementsByTagName("report");
	var selectList = document.getElementById("targetReport");
	var options = selectList.options;
	options.length = 0;
	for(i=0;i<reports.length;i++){
		var id = reports[i].getElementsByTagName("id")[0].firstChild.nodeValue;
		var name = reports[i].getElementsByTagName("name")[0].firstChild.nodeValue;
		options.add(new Option(name,id), null);
	}
	setPositionCenter( 'copyTo' );	
	showById( 'copyTo' );
}

function saveCopyItems(){
	var targetReportId = getFieldValue("targetReport");
	var targetSheetNo = getFieldValue("targetSheetNo");
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( saveCopyItemsCompleted );
	var reportItems = "";
	var url = "copyReportItems.action?reportId=" + targetReportId + "&sheetNo=" +  targetSheetNo;
	
	var listRadio = document.getElementsByName('reportItemCheck');	
	for(var i=0;i<listRadio.length;i++){
		if(listRadio.item(i).checked){
			reportItems += "&reportItems=" + listRadio.item(i).value;
		}
	}	
	request.send( url + reportItems );	
}
function saveCopyItemsCompleted( xmlObject ){
	hideById('copyTo');
}

/**
* Calculation ReportItem type
*/
function openCalculationExpression( reportId ){	
	$("#formulaCalculation").html($("#expression").val());
	$.get("getReportItems.action",
	{reportId:reportId},
	function (data){
		var xmlObject = data.getElementsByTagName('reportItems')[0];		
		var availableReportItemList = byId( "availableReportItems" );		
		availableReportItemList.options.length = 0;
		
		var reportItems = xmlObject.getElementsByTagName( "reportItem" );
		
		for ( var i = 0; i < reportItems.length; i++)
		{		
			var name = reportItems[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
			var row = reportItems[ i ].getElementsByTagName( "row" )[0].firstChild.nodeValue;
			var column = reportItems[ i ].getElementsByTagName( "column" )[0].firstChild.nodeValue;
			
			var option = document.createElement( "option" );
			option.value = "[" + row + "." + column + "]";
			option.text = name;
			availableReportItemList.add( option, null );	
		}		
		setPositionCenter( 'calculation' );	
		$("#calculation").show();
	},
	'xml');	
}

function insertCalculation(){
	$("#formulaCalculation").html($("#formulaCalculation").html() + $("#availableReportItems").val());
}

/**
* DataElement Report type
*/
function openDataElementExpression(){
	$("#formula").html($("#expression").val());
	getDataElementGroups();
	filterDataElements();
	$("#dataElementGroup").attr("disabled", false);
	$("#availableDataElements").attr("disabled", false);
	$("#availableDataElements").change(getOptionCombos);	
	setPositionCenter( 'normal' );	
	$("#normal").show();
}

function getDataElementGroups(){
	var list = byId('dataElementGroup');
	list.options.length = 0;
	list.add( new Option( "ALL", "ALL" ), null );
	for ( id in dataElementGroups )
	{
		list.add( new Option( dataElementGroups[id], id ), null );
	}
}

function filterDataElements( )
{		
	var dataElementGroupId = $("#dataElementGroup").val();
	$.get("getFilteredDataElements.action",{dataElementGroupId:dataElementGroupId},
	function(xmlObject){
		var xmlObject = xmlObject.getElementsByTagName('dataelements')[0];
		var dataElementList = byId( "availableDataElements" );
			
		dataElementList.options.length = 0;
		
		var dataelements = xmlObject.getElementsByTagName( "dataelement" );
		
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
	,'xml');	
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



