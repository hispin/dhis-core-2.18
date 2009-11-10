// ========================================================================================================================
// EXCEL ITEM GROUP
// ========================================================================================================================

// ========================================================================================================================
// Open Add Excel item form
// ========================================================================================================================

function openAddExcelItem(){
	$("#name").attr("disabled", false);
	$("#divExcelitem").showAtCenter( true );
}

// ========================================================================================================================
// Open Update Excel item form
// ========================================================================================================================

function openUpdateExcelItem( id ){
	$.post("getExcelItem.action",{id:id},
	function ( xmlObject ){
		
		$("#id").val(id);
		$("#name").val( xmlObject.getElementsByTagName('name')[0].firstChild.nodeValue );
		$("#expression").val( xmlObject.getElementsByTagName('expression')[0].firstChild.nodeValue);
		$("#row").val( xmlObject.getElementsByTagName('row')[0].firstChild.nodeValue  );
		$("#column").val( xmlObject.getElementsByTagName('column')[0].firstChild.nodeValue  );
		$("#sheetNo").val( xmlObject.getElementsByTagName('sheetNo')[0].firstChild.nodeValue  );
		
		$("#divExcelitem").showAtCenter( true );
		$("#name").attr("disabled", true);
		
	},'xml');	
}

// ========================================================================================================================
// Validate Update Excel item group
// ========================================================================================================================

function validateExcelItem(){
	$.post("validateExcelItem.action",{
		name:$("#name").val(),
		expression:$("#expression").val(),
		row:$("#row").val(),
		column:$("#column").val(),
		sheetNo:$("#sheetNo").val()
	},function(xmlObject){
		var xmlObject = xmlObject.getElementsByTagName('message')[0];
		var type = xmlObject.getAttribute( 'type' );
		if(type=='error')
		{
			setMessage(xmlObject.firstChild.nodeValue);
		}else if(type=='success')
		{		
			if(mode == 'add'){
				addExcelItem();
			}else{
				updateExcelItem();
			}
			
		}
	},'xml');	
}

// ========================================================================================================================
// Add Excel item
// ========================================================================================================================

function addExcelItem(){
	var excelItemGroupId = getParamByURL("excelItemGroupId");
	$.post("addExcelItem.action",{
		name:$("#name").val(),
		expression:$("#expression").val(),
		row:$("#row").val(),
		column:$("#column").val(),
		sheetNo:$("#sheetNo").val(),
		excelItemGroupId:getParamByURL("excelItemGroupId")
	},function(data){
		window.location.reload();
	},'xml');	
}

// ========================================================================================================================
// Update Excel Item
// ========================================================================================================================

function updateExcelItem(){
	$.post("updateExcelItem.action",{
		id:$("#id").val(),
		name:$("#name").val(),
		expression:$("#expression").val(),
		row:$("#row").val(),
		column:$("#column").val(),
		sheetNo:$("#sheetNo").val()
	},function(data){
		window.location.reload();
	},'xml');	
}

// ========================================================================================================================
// Delete Excel Item
// ========================================================================================================================

function deleteExcelItem(id){
	if(window.confirm(i18n_confirm_delete)){
		$.post("deleteExcelItem.action",{
				id:id
			},function(data){
				window.location.reload();
			},'xml');
	}
}

// ========================================================================================================================
// Get parram from URL
// ========================================================================================================================

function getParamByURL(param){
	var name = param.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	var regexS = "[\\?&]"+name+"=([^&#]*)";
	var regex = new RegExp( regexS );
	var results = regex.exec( window.location.href );

	return ( results == null ) ? "" : results[1];
}

// ===============================================================================
// Open Expression Form
// ===============================================================================

function openExpressionBuild(){
	
	$("#formula").html($("#expression").val());
	getALLDataElementGroup();
	getDataElementsByGroup();
	$("#dataElementGroup").attr("disabled", false);
	$("#availableDataElements").attr("disabled", false);
	$("#availableDataElements").change(getOptionCombos);		
	$("#divExpression").showAtCenter( true );
}

// ===============================================================================
// Get all Dataelement Group
// ===============================================================================

function getALLDataElementGroup(){
	var list = byId('dataElementGroup');
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

// ===============================================================================
// Get OptionCombos by DataElement
// ===============================================================================

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

// ===============================================================================
// Insert dataelement's id into the Formular textbox
// ===============================================================================

function insertDataElementId(){
	var dataElementComboId = "[" + $("#availableDataElements").val() + "." + $("#optionCombos").val() + "]";
	$("#formula").val($("#formula").val() + dataElementComboId);
}

// ===============================================================================
// Insert operators into the Formular textbox
// ===============================================================================

function insertOperation(target, value ){
	$("#" + target).val($("#" + target).val() + value);
}

// ===============================================================================
// Copy selected items Form
// ===============================================================================

function copySelectedExcelItemForm() {
	
	$.post("getAllExcelItemGroup.action",{},
	function (xmlObject){
		xmlObject = xmlObject.getElementsByTagName('excelitemgroups')[0];
		var groups = xmlObject.getElementsByTagName("excelitemgroup");
		var selectList = document.getElementById("targetExcelItemGroup");
		var options = selectList.options;
		options.length = 0;
		for(i=0;i<groups.length;i++){
			var id = groups[i].getElementsByTagName("id")[0].firstChild.nodeValue;
			var name = groups[i].getElementsByTagName("name")[0].firstChild.nodeValue;
			options.add(new Option(name,id), null);
		}
		
	$("#copyToExcelItem").showAtCenter( true );
	},'xml');
}


// ===============================================================================
// Validate copy Excel Items
// ===============================================================================

sheetId = 0;
reportItemIds = null;
excelItemsCurTarget = null;
excelItemsDuplicated = null;

function validateCopyExcelItems() {

	excelItemsCurTarget = new Array();
	excelItemsDuplicated = new Array();

	sheetId	= $("#targetExcelItemGroupSheetNo").val();
	
	$.post("getExcelItemsByGroup.action",
	{
		excelItemGroupId:$("#targetExcelItemGroup").val(),
		sheetNo:sheetId
	},
	function (data)
	{
		data = data.getElementsByTagName('excelItems')[0];		
		var items = data.getElementsByTagName('excelitem');
		
		for (var i = 0 ;  i < items.length ; i ++) {
		
			excelItemsCurTarget.push(items[i].getElementsByTagName('name')[0].firstChild.nodeValue);
		}
		
		splitDuplicatedItems();
		
		saveCopyExcelItems();
		
	}, "xml");
}

function splitDuplicatedItems() {

	var flag = -1;
	var reportItemsChecked = new Array();
	var listRadio = document.getElementsByName('reportItemCheck');
	
	reportItemIds = null;
	reportItemIds = new Array();
	
	for (var i = 0 ; i < listRadio.length ; i++) {
		if ( listRadio.item(i).checked ) {
			reportItemsChecked.push( listRadio.item(i).getAttribute("reportItemID") + "#" + listRadio.item(i).getAttribute("reportItemName"));
		}
	}
	
	for (var i in reportItemsChecked)
	{
		flag = i;
		
		for (var j in excelItemsCurTarget)
		{
			if ( reportItemsChecked[i].split("#")[1] == excelItemsCurTarget[j] )
			{
				flag = -1;
				excelItemsDuplicated.push( reportItemsChecked[i].split("#")[1] );
				break;
			}
		}
		
		if ( flag != -1 )
		{
			reportItemIds.push( reportItemsChecked[i].split("#")[0] );
		}
	}
}

function saveCopyExcelItems() {
	
	var excelItemsDuplicatedList = '';
	
	if (excelItemsDuplicated.length > 0) {
	
		excelItemsDuplicatedList = "Sheet [" + sheetId + "] - " + i18n_copy_items_duplicated + "<br>";
		
		for (var i in excelItemsDuplicated) {
		
			excelItemsDuplicatedList += "&nbsp;&nbsp;&nbsp;-&nbsp;&nbsp;" + excelItemsDuplicated[i] + "<br>";
		}
	
	}
	
	if (reportItemIds.length > 0) {
	
		$.post("copyExcelItems.action",
		{
			excelItemGroupId:$("#targetExcelItemGroup").val(),
			sheetNo:sheetId,
			reportItemIds:reportItemIds
		},
		function (data)
		{
		
		},'xml');
	}
	
	if(reportItemIds.length == 0){
		setMessage( excelItemsDuplicatedList );
	}else{
		if (excelItemsDuplicated.length > 0)
			 excelItemsDuplicatedList += "<br>==========<br>" + i18n_copy_successful;
		else
			excelItemsDuplicatedList += i18n_copy_successful;
		setMessage( excelItemsDuplicatedList);
	}
	
	$("#copyToExcelItem").hide();
	deleteDivEffect();
}