/*
* 	Open Add Data Element Group Order 
*/
function openAddDataElementGroupOrder(){
	getALLDataElementGroups();
	document.forms['dataElementGroups'].action = "addDataElementGroupOrder.action";
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
			availableDataElementGroups.options.add(new Option(name, id));			
		}			
		getDataElementsByGroup($("#availableDataElementGroups").val());
	},'xml');
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
		listDataElement.options.add(new Option(name, id));          
    }
	
	var availableDataElements = document.getElementById('availableDataElements');
	var selectedDataElements = document.getElementById('selectedDataElements');
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
	if($("#name").val()=='') setMesaage(i18n_name_is_null);	
	else{
		selectAllById('selectedDataElements');
		document.forms['dataElementGroups'].submit();
	}
}

/*
* 	Delete Data Element Order
*/

function deleteDataElementOrder( id ){
	if(window.confirm(i18n_confirm_delete)){
		$.post("deleteDataElementGroupOrder.action",{id:id}, function (data){window.location.reload()},'xml');		
	}
}

/*
* 	Open Update Data Element Order
*/

function openUpdateDataElementOrder( id ){
	$("#dataElementGroupOrderId").val( id );
	$.post("getDataElementGroupOrder.action",{id:id},
	function(data){
		var listDataElement = document.getElementById('selectedDataElements');
		listDataElement.options.length = 0;
		data = data.getElementsByTagName('dataElementGroupOrder')[0];
		$("#name").val(data.getElementsByTagName('name')[0].firstChild.nodeValue);
		$("#code").val(data.getElementsByTagName('code')[0].firstChild.nodeValue);
		var dataElements = data.getElementsByTagName('dataElements')[0].getElementsByTagName('dataElement');
		for(var i=0;i<dataElements.length;i++){
			var name = dataElements[i].getElementsByTagName('name')[0].firstChild.nodeValue;
			var id = dataElements[i].getElementsByTagName('id')[0].firstChild.nodeValue;
			listDataElement.options.add(new Option(name, id));
		}
		
		document.forms['dataElementGroups'].action = "updateDataElementGroupOrder.action";		
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
	
	$.post("updateSortedDataElements.action",{
		id:id,
		dataElementIds:dataElementIds
	},function (data){
		history.go(-1);
	},'xml');	
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

