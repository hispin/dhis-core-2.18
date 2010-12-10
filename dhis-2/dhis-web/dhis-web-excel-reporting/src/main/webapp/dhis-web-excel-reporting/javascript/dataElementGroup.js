
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

function openAddDataElementGroupOrder()
{
	dataDictionary.loadDataElementGroups( "#availableDataElementGroups" );
	
	dataDictionary.loadDataElementsByGroup( "" , "#availableDataElements" );	
		
	jQuery("#dataElementGroups").dialog("open");
	
	jQuery( "#dataElementGroupsForm" ).attr( "action","addDataElementGroupOrder.action");
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

function openUpdateDataElementOrder( id )
{
	
	setFieldValue("dataElementGroupOrderId", id );
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( openUpdateDataElementOrderReceived );
	var url = "getDataElementGroupOrderForCategory.action?id=" + id;
	request.send(url);
}

function openUpdateDataElementOrderReceived(xmlObject)
{
	var listDataElement = jQuery('#dataElementIds');
	listDataElement.empty();
	byId("name").value = getElementValue(xmlObject, 'name');
	byId("code").value = getElementValue(xmlObject, 'code');
	var dataElements = xmlObject.getElementsByTagName('dataElements')[0].getElementsByTagName('dataElement');
	
	for(var i=0;i<dataElements.length;i++)
	{
		var name = getElementValue(dataElements[i], 'name');
		var id = getElementValue(dataElements[i], 'id');
		listDataElement.append('<option value="' + id + '">' + name + '</option>');
	}
	
	dataDictionary.loadDataElementGroups( "#availableDataElementGroups" );
	
	dataDictionary.loadDataElementsByGroup( "" , "#availableDataElements" );	
		
	jQuery("#dataElementGroups").dialog("open");
	
	jQuery( "#dataElementGroupsForm" ).attr( "action","updateDataElementGroupOrder.action");
	
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
