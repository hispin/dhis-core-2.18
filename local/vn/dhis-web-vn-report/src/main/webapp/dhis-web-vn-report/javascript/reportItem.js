function filterSheet(){
	window.location = "listReportItem.action?reportId=" +  getFieldValue("reportId") + "&sheetNo=" + getFieldValue("sheetNoFilter");
}
function validateAddReportItem(){

	var id = getFieldValue("id");
	var name = getFieldValue("name");
	var itemType = getFieldValue("itemType");
	var periodType = getFieldValue("periodType");
	var expression = getFieldValue("expression");
	var row = getFieldValue("row");
	var column = getFieldValue("column");	
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateAddReportItemCompleted );
	url = "validateReportItem.action";
	url += "?name=" + name;
	url += "&reportItemId=" + id;	
	url += "&expression=" + expression;
	url += "&row=" + row;
	url += "&column=" + column;
	url += "&mode=" + mode;
	url += "&reportId=" + reportId;	
    request.send( url );    
	
}

function validateAddReportItemCompleted( xmlObject ){
	var type = xmlObject.getAttribute( 'type' );
    
    if(type=='error')
    {
        setMessage(xmlObject.firstChild.nodeValue);
    }
    if(type=='success')
    {
		if(mode=='ADD'){
			//document.forms['reportItem'].action="addReportItem.action";
			//document.forms['reportItem'].submit();
			addReportItem();
		}else{
			//document.forms['reportItem'].action="updateReportItem.action";
			//document.forms['reportItem'].submit();
			updateReportItem();
		}      
    }
}
function addReportItem(){
	var name = getFieldValue("name");
	var itemType = getFieldValue("itemType");
	var periodType = getFieldValue("periodType");
	var expression = getFieldValue("expression");
	var row = getFieldValue("row");
	var column = getFieldValue("column");	
	var sheetNo = getFieldValue("sheetNo");	
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( addReportItemCompleted );
	url = "addReportItem.action";
	url += "?reportId=" + reportId;	
	url += "&name=" + name;	
	url += "&itemType=" + itemType;	
	url += "&periodType=" + periodType;
	url += "&expression=" + htmlEncode(expression);
	url += "&row=" + row;
	url += "&column=" + column;
	url += "&sheetNo=" + sheetNo;
	
    request.send( url );  
}

function updateReportItem(){
	var id = getFieldValue("id");
	var name = getFieldValue("name");
	var itemType = getFieldValue("itemType");
	var periodType = getFieldValue("periodType");
	var expression = getFieldValue("expression");
	var row = getFieldValue("row");
	var column = getFieldValue("column");	
	var sheetNo = getFieldValue("sheetNo");	
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( addReportItemCompleted );
	url = "updateReportItem.action";	
	url += "?id=" + id;	
	url += "&name=" + name;	
	url += "&itemType=" + itemType;	
	url += "&periodType=" + periodType;
	url += "&expression=" + expression;
	url += "&row=" + row;
	url += "&column=" + column;
	url += "&reportId=" + reportId;
	url += "&sheetNo=" + sheetNo;
	
	
    request.send( url ); 
}

function addReportItemCompleted( xmlObject ){
	window.location.reload();
}

function getIndicatorGroups(){
	var list = byId('dataElementGroup');
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
	request.send( "../dhis-web-commons-ajax/getIndicators.action?id=" + getFieldValue('dataElementGroup'));
}
function filterIndicatorsCompleted( xmlObject ){
	var indiatorList = byId( "avilableDataElements" );
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
	var dataElementGroup = document.getElementById( "dataElementGroup" );
	var dataElementGroupId = dataElementGroup.options[ dataElementGroup.selectedIndex ].value;	
	
	var url = "getFilteredDataElements.action?dataElementGroupId=" + dataElementGroupId;
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getFilteredDataElementsReceived );
    request.send( url );
}

function getFilteredDataElementsReceived( xmlObject )
{
	var operandList = byId( "availableDataElements" );
			
	operandList.options.length = 0;
	
	var operands = xmlObject.getElementsByTagName( "operand" );
	
	for ( var i = 0; i < operands.length; i++)
	{
		var id = operands[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var elementName = operands[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
		var option = document.createElement( "option" );
		option.value = "[" + id + "]";
		option.text = elementName;
		operandList.add( option, null );	
	}
}

function insertFormulaText( value ) {
	setFieldValue('formula', getFieldValue('formula') + value);
}

function validateFormula() {
	
	var itemTypeValue = getFieldValue('itemType');
	var formulaValue = getFieldValue('formula');
	
	if ( itemTypeValue == 'element_optioncombo' ) {
	
		if ( (formulaValue != null) && (formulaValue != "") ) {
			setFieldValue('expression', getFieldValue('formula'));
			hideById('formulaDiv');
		}
		return;
	}
	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateFormulaCompleted );

	request.send( "validateDenum.action?formula=" + htmlEncode(getFieldValue('formula') ) + "&mode=" + getFieldValue('itemType'));
}


function validateFormulaCompleted( xmlObject ) {
	var type = xmlObject.getAttribute( 'type' );
    
    if(type=='error')
    {
        setMessage(xmlObject.firstChild.nodeValue);
    }
    if(type=='success')
    {
		setFieldValue('expression', getFieldValue('formula'));
		hideById('formulaDiv');
	}
}


function getReportItems( reportId ){
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getReportItemsCompleted );
	request.send( "getReportItems.action?formula=" + htmlEncode(getFieldValue('formula') ) + "&reportId=" + reportId);
}
function getReportItemsCompleted( xmlObject ){
	var operandList = document.getElementById( "availableDataElements" );
			
	operandList.options.length = 0;
	
	var reportItems = xmlObject.getElementsByTagName( "reportItem" );
	
	for ( var i = 0; i < reportItems.length; i++)
	{		
		var name = reportItems[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		var row = reportItems[ i ].getElementsByTagName( "row" )[0].firstChild.nodeValue;
		var column = reportItems[ i ].getElementsByTagName( "column" )[0].firstChild.nodeValue;
		
		var option = document.createElement( "option" );
		option.value = "[" + row + "." + column + "]";
		option.text = name;
		operandList.add( option, null );	
	}
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
