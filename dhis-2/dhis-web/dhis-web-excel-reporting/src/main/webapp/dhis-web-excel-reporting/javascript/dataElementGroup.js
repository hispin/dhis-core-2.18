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
	
	var availableDataElementGroups = document.getElementById('availableDataElementGroups');
	availableDataElementGroups.options.length = 0;
	var dataElementGroups = xmlObject.getElementsByTagName('dataElementGroup');
	availableDataElementGroups.options.add(new Option("ALL", null));
	for(var i=0;i<dataElementGroups.length;i++){
		var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
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
	var listDataElement = document.getElementById('availableDataElements');
	listDataElement.options.length = 0;
	for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var name = dataElements[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;  
		//listDataElement.options.add(new Option(name, id));  
		var option = new Option( name, id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
		listDataElement.add( option, null );
    }
	
	var availableDataElements = document.getElementById('availableDataElements');
	var selectedDataElements = document.getElementById('dataElementIds');
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
	var url = "getDataElementGroupOrder.action?id=" + id;
	request.send(url);
}

function openUpdateDataElementOrderReceived(xmlObject)
{
		var listDataElement = document.getElementById('dataElementIds');
		listDataElement.options.length = 0;
		byId("name").value = xmlObject.getElementsByTagName('name')[0].firstChild.nodeValue;
		byId("code").value = getElementValue(xmlObject,'code');
		var dataElements = xmlObject.getElementsByTagName('dataElements')[0].getElementsByTagName('dataElement');
		
		for(var i=0;i<dataElements.length;i++){
			var name = dataElements[i].getElementsByTagName('name')[0].firstChild.nodeValue;
			var id = dataElements[i].getElementsByTagName('id')[0].firstChild.nodeValue;
			var option =  new Option( name, id );
			option.onmousemove  = function(e){
				showToolTip( e, this.text);
			}
			listDataElement.options.add(option);
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

