// ========================================================================================================================
// EXCEL ITEM GROUP
// ========================================================================================================================
/*
*	Open Add Excel item group form
*/

function openAddExcelItemGroup(){
	$("#name").attr("disabled", false);
	$("#type").attr("disabled", false);
	$("#divExcelitemGroup").showAtCenter( true );
}

/*
*	Open Update Excel item group form
*/
function openUpdateExcelItemGroup( id ){
	$.post("getExcelItemGroup.action",{id:id},
	function ( xmlObject ){
		
		$("#id").val(id);
		$("#name").val( xmlObject.getElementsByTagName('name')[0].firstChild.nodeValue );
		$("#excelTemplateFile").val( xmlObject.getElementsByTagName('excelTemplateFile')[0].firstChild.nodeValue);
		$("#type").val( xmlObject.getElementsByTagName('type')[0].firstChild.nodeValue);
		$("#periodType").val( xmlObject.getElementsByTagName('periodType')[0].firstChild.nodeValue);
		
		$("#divExcelitemGroup").showAtCenter( true );
		$("#name").attr("disabled", true);
		$("#type").attr("disabled", true);
		
	},'xml');	
}

/*
*	Validate Update Excel item group
*/
function validateExcelItemGroup(){
	$.post("validateExcelItemGroup.action",{		
		name:$("#name").val(),
		excelTemplateFile:$("#excelTemplateFile").val(),
		type:$("#type").val()
	},function(xmlObject){
		var xmlObject = xmlObject.getElementsByTagName('message')[0];
		var type = xmlObject.getAttribute( 'type' );
		if(type=='error')
		{
			setMessage(xmlObject.firstChild.nodeValue);
		}else if(type=='success')
		{
			if(mode == 'add'){
				addExcelItemGroup();
			}else{
				updateExcelItemGroup();
			}
			
		}
	},'xml');	
}

function addExcelItemGroup(){
	
	$.post("addExcelItemGroup.action",{
		name:$("#name").val(),
		excelTemplateFile:$("#excelTemplateFile").val(),
		type:$("#type").val(),
		periodTypeName:$("#periodType").val()
	},function(data){
		window.location.reload();
	},'xml');	
}


function updateExcelItemGroup(){
	$.post("updateExcelItemGroup.action",{
		id:$("#id").val(),
		name:$("#name").val(),
		excelTemplateFile:$("#excelTemplateFile").val(),
		type:$("#type").val(),
		periodTypeName:$("#periodType").val()
	},function(data){
		window.location.reload();
	},'xml');	
}

function deleteExcelItemGroup(id){
	if(window.confirm(i18n_confirm_delete)){
		$.post("deleteExcelItemGroup.action",{
				id:id
			},function(data){
				window.location.reload();
			},'xml');
	}
}

// --------------------------------------------------------------------
// DATA ELEMENT GROUP
// --------------------------------------------------------------------

/*
* 	Open Add Data Element Group Order 
*/
function openAddDataElementGroupOrder(){
	getALLDataElementGroups();
	document.forms['dataElementGroups'].action = "addDataElementGroupOrderForCategory.action";
}
/*
* 	Get Data Elements By Data Element Group
*/
function getDataElementsByGroup( id ){
	
	if(id==null)
		return;

	var url = "../dhis-web-commons-ajax/getDataElements.action?id=" + id;

	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( getDataElementsByGroupReceived );	
	request.send( url );	
}

function getDataElementsByGroupReceived( datalement ){
	
	var dataElements = datalement.getElementsByTagName( "dataElement" );
	
	var listDataElement = document.getElementById('availableDataElements');
	
	listDataElement.options.length = 0;
	
	for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var name = dataElements[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        var option = new Option(name, id);
		
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
		
		listDataElement.add(option, null);
    }
	
	var availableDataElements = document.getElementById('availableDataElements');
	var dataElementIds = document.getElementById('dataElementIds');
	for(var i=0;i<availableDataElements.options.length;i++){
		for(var j=0;j<dataElementIds.options.length;j++){				
			if(availableDataElements.options[i].value==dataElementIds.options[j].value){					
				availableDataElements.options[i].style.display='none';				
			}
		}
	}
	
	$("#dataElementGroups").showAtCenter( true );	
}

function getALLDataElementGroups(){
	
	$.get("getAllDataElementGroups.action",{},
	function(data){
		
		var availableDataElementGroups = document.getElementById('availableDataElementGroups');
		
		availableDataElementGroups.options.length = 0;
		
		var dataElementGroups = data.getElementsByTagName('dataElementGroups')[0].getElementsByTagName('dataElementGroup');
		
		availableDataElementGroups.options.add(new Option("ALL", null));	
		
		for(var i=0;i<dataElementGroups.length;i++){
			
			var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
			var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
			
			var option = new Option( name, id );
			availableDataElementGroups.add(option, null);
		}			
		getDataElementsByGroup($("#availableDataElementGroups").val());
	},'xml');
}

/*
* 	Add Data Element Group Order
*/
function submitDataElementGroupOrder(){
	
	if($("#name").val()=='') setMessage(i18n_name_is_null);	
	else{
		selectAllById('dataElementIds');
		document.forms['dataElementGroups'].submit();
	}
}
/*
* 	Delete Data Element Order
*/

function deleteDataElementOrder( id ){
	if(window.confirm(i18n_confirm_delete)){
		$.post("deleteDataElementGroupOrderForCategory.action",{id:id}, function (data){window.location.reload()},'xml');		
	}
}

/*
* 	Open Update Data Element Order
*/

function openUpdateDataElementOrder( id ){
	
	$("#dataElementGroupOrderId").val( id );
	$.post("getDataElementGroupOrderForCategory.action",{id:id},
	function(data){
		var listDataElement = document.getElementById('dataElementIds');
		listDataElement.options.length = 0;
		data = data.getElementsByTagName('dataElementGroupOrder')[0];
		$("#name").val(data.getElementsByTagName('name')[0].firstChild.nodeValue);
		$("#code").val(data.getElementsByTagName('code')[0].firstChild.nodeValue);
		var dataElements = data.getElementsByTagName('dataElements')[0].getElementsByTagName('dataElement');
		
		for(var i=0;i<dataElements.length;i++){
			var name = dataElements[i].getElementsByTagName('name')[0].firstChild.nodeValue;
			var id = dataElements[i].getElementsByTagName('id')[0].firstChild.nodeValue;
			
			var option = new Option( name, id );
			option.onmousemove  = function(e){
				showToolTip( e, this.text);
			}
	
			listDataElement.add(option, null);
		}
		
		document.forms['dataElementGroups'].action = "updateDataElementGroupOrderForCategory.action";
		
		getALLDataElementGroups();
	},'xml');
}
/*
* 	Update Sorted Data Element 
*/
function updateSortedDataElement(){	
	var dataElements = document.getElementsByName('dataElement');
	var dataElementIds = new Array();
	for(var i=0;i<dataElements.length;i++){		
		dataElementIds.push(dataElements.item(i).value);
	}
	
	$.post("updateSortedDataElementsForCategory.action",{
		id:id,
		dataElementIds:dataElementIds
	},function (data){
		history.go(-1);
	},'xml');	
}

function updateDataElementGroupOrder(){
	var dataElements = document.getElementsByName('dataElementGroupOrder');
	var url = "updateSortDataElementGroupOrderForCategory.action?excelItemGroupId=" + $("#id").val();
	for(var i=0;i<dataElements.length;i++){			
		url += "&dataElementGroupOrderId=" + dataElements.item(i).value;
	}
	window.location = url;
	
}

// -----------------------------------------------------------------------------
function showToolTip( e, value){
	
	var tooltipDiv = byId('tooltip');
	tooltipDiv.style.display = 'block';
	
	var posx = 0;
    var posy = 0;
	
    if (!e) var e = window.event;
    if (e.pageX || e.pageY)
    {
        posx = e.pageX;
        posy = e.pageY;
    }
    else if (e.clientX || e.clientY)
    {
        posx = e.clientX;
        posy = e.clientY;
    }
	
	tooltipDiv.style.left= posx  + 8 + 'px';
	tooltipDiv.style.top = posy  + 8 + 'px';
	tooltipDiv.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +   value;
}

function hideToolTip(){
	byId('tooltip').style.display = 'none';
}
