
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

function showDataElementGroupOrderDetails( id )
{
	var request = new Request();
    request.setResponseTypeXML( 'dataElementGroupOrder' );
    request.setCallbackSuccess( showDataElementGroupOrderReceived );
    request.send( 'getDataElementGroupOrder.action?id=' + id );
}

function showDataElementGroupOrderReceived( dataElementGroupOrderElement )
{
    setInnerHTML( 'nameField', getElementValue( dataElementGroupOrderElement, 'name' ) );
    setInnerHTML( 'codeField', getElementValue( dataElementGroupOrderElement, 'code' ) );
    setInnerHTML( 'memberCountField', getElementValue( dataElementGroupOrderElement, 'memberCount' ) );

    showDetails();
}

/*
* 	Open Add Data Element Group Order 
*/
function openAddDataElementGroupOrder()
{
	dataDictionary.loadDataElementGroups( "#availableDataElementGroups" );
	dataDictionary.loadDataElementsByGroup( "" , "#availableDataElements" );	
	
	dialog.dialog("open");
	
	jQuery( "#dataElementGroupsForm" ).attr( "action", "addDataElementGroupOrderFor"+clazzName+".action?clazzName="+clazzName );
}

/*
* 	Open Update Data Element Order
*/

function openUpdateDataElementGroupOrder( id )
{
	setFieldValue("dataElementGroupOrderId", id );
	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( openUpdateDataElementGroupOrderReceived );
	var url = "getDataElementGroupOrder.action?id=" + id;
	request.send(url);
}

function openUpdateDataElementGroupOrderReceived(xmlObject)
{
	var listDataElement = jQuery('#dataElementIds');
	listDataElement.empty();
	setFieldValue( "name", getElementValue(xmlObject, 'name') );
	setFieldValue( "code", getElementValue(xmlObject, 'code') );
	var dataElements = xmlObject.getElementsByTagName('dataElements')[0].getElementsByTagName('dataElement');
	
	for ( var i=0 ; i < dataElements.length ; i++ )
	{
		var name = getElementValue(dataElements[i], 'name');
		var id = getElementValue(dataElements[i], 'id');
		listDataElement.append('<option value="' + id + '">' + name + '</option>');
	}
	
	dataDictionary.loadDataElementGroups( "#availableDataElementGroups" );
	dataDictionary.loadDataElementsByGroup( "" , "#availableDataElements" );	
		
	dialog.dialog("open");
	
	jQuery( "#dataElementGroupsForm" ).attr( "action","updateDataElementGroupOrderFor"+clazzName+".action");
	
}

/*
* 	Delete Data Element Order
*/
function deleteDataElementGroupOrder( id, name )
{
	removeItem( id, name, i18n_confirm_delete, 'deleteDataElementGroupOrder.action', function(){ window.location.reload(); } );
}

/*
*	Update data element group order
*/
function updateSortDataElementGroupOrder()
{
	var dataElements = document.getElementsByName('dataElementGroupOrder');
	var url = "updateSortDataElementGroupOrder.action?reportId=" + reportId;
	url += "&clazzName=" + clazzName;
	
	for ( var i=0 ; i < dataElements.length ; i++ )
	{
		url += "&dataElementGroupOrderId=" + dataElements.item(i).value;
	}
	
	jQuery.postJSON( url, {}, function( json ) {
		showSuccessMessage( json.message );
	});
}

function openSortDataElementForGroupOrder( id )
{
	window.location = "openSortDataElement.action?id="+id+"&reportId="+reportId+"&clazzName="+clazzName;
}

/*
* 	Update Sorted Data Element 
*/
function updateSortedDataElement()
{	
	moveAllById( 'availableList', 'selectedList' );
	selectAllById('selectedList');
	document.forms[0].submit();
}

/*
*	Tooltip
*/
function showToolTip( e, value)
{	
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

function hideToolTip()
{
	byId('tooltip').style.display = 'none';
}