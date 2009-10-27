// ========================================================================================================================
// EXCEL ITEM GROUP
// ========================================================================================================================

/*
*	Open Add Excel item form
*/

function openAddExcelItem(){
	$("#name").attr("disabled", false);
	$("#divExcelitem").showAtCenter( true );
}

/*
*	Open Update Excel item form
*/
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

/*
*	Validate Update Excel item group
*/
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

function deleteExcelItem(id){
	if(window.confirm(i18n_confirm_delete)){
		$.post("deleteExcelItem.action",{
				id:id
			},function(data){
				window.location.reload();
			},'xml');
	}
}

function getParamByURL(param){
	var name = param.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
	var regexS = "[\\?&]"+name+"=([^&#]*)";
	var regex = new RegExp( regexS );
	var results = regex.exec( window.location.href );

	return ( results == null ) ? "" : results[1];
}

// ===============================================================================
// 
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
	$("#formula").val($("#formula").val() + dataElementComboId);
}

function insertOperation(target, value ){
	$("#" + target).val($("#" + target).val() + value);
}