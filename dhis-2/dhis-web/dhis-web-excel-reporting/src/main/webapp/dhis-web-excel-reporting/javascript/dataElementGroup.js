
function addOptionToListWithToolTip( list, optionValue, optionText )
{
    var option = document.createElement( "option" );
    option.value = optionValue;
    option.text = optionText;
	option.onmousemove = function(e) {
		showToolTip(e, optionText);
	}
    list.add( option, null );
}

/*
* 	Open Add Data Element Group Order 
*/

function openAddDataElementGroupOrder(){
	getALLDataElementGroups();
	document.forms['dataElementGroups'].action = "addDataElementGroupOrder.action";
}

function getALLDataElementGroups(){
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( getALLDataElementGroupsReceived );
	
	var url = "getAllDataElementGroups.action";
	
	request.send(url);

}

function getALLDataElementGroupsReceived(xmlObject){
	
	var availableDataElementGroups = byId('availableDataElementGroups');
	availableDataElementGroups.options.length = 0;
	var dataElementGroups = xmlObject.getElementsByTagName('dataElementGroup');
	availableDataElementGroups.options.add(new Option("ALL", null));
	for(var i=0;i<dataElementGroups.length;i++){
		var id = getElementValue(dataElementGroups.item(i), 'id');
		var name = getElementValue(dataElementGroups.item(i), 'name');
		availableDataElementGroups.options.add(new Option(name, id));			
	}			
	getDataElementsByGroup(byId("availableDataElementGroups").value);
}
/*
* 	Get Data Elements By Data Element Group
*/
function getDataElementsByGroup( id ){

	var url = "../dhis-web-commons-ajax/getDataElements.action?id=" + id;

	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( getDataElementsByGroupReceived );	
	request.send( url );	
}

function getDataElementsByGroupReceived( datalement ){
	var dataElements = datalement.getElementsByTagName( "dataElement" );
	var listDataElement = byId('availableDataElements');
	listDataElement.options.length = 0;
	for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = getElementValue(dataElements[i], 'id');
        var name = getElementValue(dataElements[i], 'name');
		addOptionToListWithToolTip( listDataElement, id, name );
    }
	
	var availableDataElements = byId('availableDataElements');
	var selectedDataElements = byId('dataElementIds');
	for(var i=0;i<availableDataElements.options.length;i++){
		for(var j=0;j<selectedDataElements.options.length;j++){				
			if(availableDataElements.options[i].value==selectedDataElements.options[j].value){					
				availableDataElements.options[i].style.display='none';			
			}
		}
	}		
	
	$("#dataElementGroups").showAtCenter( true );	
}
/*
* 	Add Data Element Group Order
*/
function submitDataElementGroupOrder(){
	if(byId("name").value =='') setMessage(i18n_name_is_null);	
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
		var request = new Request();
		request.setResponseTypeXML( 'datalement' );
		request.setCallbackSuccess( deleteDataElementOrderReceived );
		var url = "deleteDataElementGroupOrder.action?id=" + id;
		request.send( url );
		
	}
}

function deleteDataElementOrderReceived(datalement){
	window.location.reload();
}

/*
* 	Open Update Data Element Order
*/

function openUpdateDataElementOrder( id ){
	
	byId("dataElementGroupOrderId").value = id;
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( openUpdateDataElementOrderReceived );
	var url = "getDataElementGroupOrderForCategory.action?id=" + id;
	request.send(url);
}

function openUpdateDataElementOrderReceived(xmlObject)
{
		var listDataElement = byId('dataElementIds');
		listDataElement.options.length = 0;
		byId("name").value = getElementValue(xmlObject, 'name');
		byId("code").value = getElementValue(xmlObject, 'code');
		var dataElements = xmlObject.getElementsByTagName('dataElements')[0].getElementsByTagName('dataElement');
		
		for(var i=0;i<dataElements.length;i++){
			var name = getElementValue(dataElements[i], 'name');
			var id = getElementValue(dataElements[i], 'id');
			addOptionToListWithToolTip( listDataElement, id, name );
		}
		
		document.forms['dataElementGroups'].action = "updateDataElementGroupOrder.action";		
		getALLDataElementGroups();
}
/*
* 	Update Sorted Data Element 
*/
function updateSortedDataElement(){	

	moveAllById( 'availableList', 'selectedList' );
	selectAllById('selectedList');
	document.forms[0].submit();
	
}
/*
*	Update data element group order
*/

function updateDataElementGroupOrder(){
	var dataElements = document.getElementsByName('dataElementGroupOrder');
	var url = "updateSortDataElementGroupOrder.action?reportId=" + reportId;
	for(var i=0;i<dataElements.length;i++){			
		url += "&dataElementGroupOrderId=" + dataElements.item(i).value;
	}
	window.location = url;
	
}
