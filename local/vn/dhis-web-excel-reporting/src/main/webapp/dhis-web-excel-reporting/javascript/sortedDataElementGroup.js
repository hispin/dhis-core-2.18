// -----------------------------------------------------------------------------
// ----------------------- DATA ELEMENT GROUP ----------------------------------
// -----------------------------------------------------------------------------

// -----------------------------------------------------------------------------
// Open Add Data Element Group
// -----------------------------------------------------------------------------

function openAddDataElementGroupOrder(){
	getALLDataElementGroups();
	document.forms['dataElementGroups'].action = "addDataElementGroupOrderForCategory.action";
}

// -----------------------------------------------------------------------------
// Add DataElement Group
// -----------------------------------------------------------------------------

function submitDataElementGroupOrder(){
	
	if(byId("name").value =='') setMessage(i18n_name_is_null);	
	else{
		selectAllById('dataElementIds');
		document.forms['dataElementGroups'].submit();
	}
}

// -----------------------------------------------------------------------------
// Delete DataElement Group
// -----------------------------------------------------------------------------

function deleteDataElementGroupOrder( id ){
	if(window.confirm(i18n_confirm_delete)){
		var request = new Request();
		request.setResponseTypeXML( 'datalement' );
		request.setCallbackSuccess( Completed );
		request.send("deleteDataElementGroupOrderForCategory.action?id=" + id);
		
	}
}

function Completed(xmlObject){
	window.location.reload();
}
// -----------------------------------------------------------------------------
// Get All Data Element Group
// -----------------------------------------------------------------------------

function getALLDataElementGroups(){
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( getALLDataElementGroupsReceived );
	request.send("getAllDataElementGroups.action");

}

function getALLDataElementGroupsReceived ( data ){
	var availableDataElementGroups = document.getElementById('availableDataElementGroups');
	availableDataElementGroups.options.length = 0;
	var dataElementGroups = data.getElementsByTagName('dataElementGroup');
	availableDataElementGroups.options.add(new Option("ALL", null));	
	for(var i=0;i<dataElementGroups.length;i++){
		var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
		availableDataElementGroups.options.add(new Option(name, id));			
	}			
	getDataElementsByGroup($("#availableDataElementGroups").val());
}

// -----------------------------------------------------------------------------
// Update Order of DataElement Group
// -----------------------------------------------------------------------------

function updateDataElementGroupOrder(){
	
 	var dataElements = document.getElementsByName('dataElementGroupOrder');
	var url = "updateSortedDataElementGroupOrderForCategory.action?excelItemGroupId=" + $('#id').val();
	for(var i=0;i<dataElements.length;i++){			
		url += "&dataElementGroupOrderId=" + dataElements.item(i).value;
	}
	
	var request = new Request();
	request.setResponseTypeXML( 'reportXML' );
	request.setCallbackSuccess( updateDataElementGroupOrderReceived );
	request.send( url );
}

function updateDataElementGroupOrderReceived(xmlObject){
	setMessage(xmlObject.firstChild.nodeValue);
}

// -----------------------------------------------------------------------------
// ----------------------------- DATA ELEMENT ----------------------------------
// -----------------------------------------------------------------------------

// -----------------------------------------------------------------------------
// Get DataElements by DataElement Group
// -----------------------------------------------------------------------------

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

// -----------------------------------------------------------------------------
// Open Update data element order
// -----------------------------------------------------------------------------

function openUpdateDataElementOrder( id ){
	
	byId("dataElementGroupOrderId").value = id;
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( openUpdateDataElementOrderReceived );
	request.send("getDataElementGroupOrderForCategory.action?id=" + id);
	
}

function openUpdateDataElementOrderReceived(data){
	var listDataElement = document.getElementById('dataElementIds');
	listDataElement.options.length = 0;
	byId("name").value = getElementValue(data,'name' );//data.getElementsByTagName('name')[0].firstChild.nodeValue;
	byId("code").value = getElementValue(data,'code' );// data.getElementsByTagName('code')[0].firstChild.nodeValue;
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
}

// -----------------------------------------------------------------------------
// Update sortesd order of DataElement
// -----------------------------------------------------------------------------

function updateSortedDataElement(){	

	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( updateSortedDataElementCompleted );
	var params = "id=" + byId('dataElementGroupOrderId').value;
		params += "&"+getQueryStringFromList( 'sortdataElement', 'dataElementIds');
	request.sendAsPost( params );
	request.send("updateSortedDataElementsForCategory.action");

}

function updateSortedDataElementCompleted( xmlObject ){
	history.go(-1);
}
// -----------------------------------------------------------------------------
// SHOW TOOLTIP
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

// -----------------------------------------------------------------------------
// HIDE TOOLTIP
// -----------------------------------------------------------------------------

function hideToolTip(){
	byId('tooltip').style.display = 'none';
}
