// ========================================================================================================================
// EXCEL ITEM GROUP
// ========================================================================================================================

// --------------------------------------------------------------------
// DATA ELEMENT GROUP
// --------------------------------------------------------------------

/*
* 	Open Add Data Element Group Order 
*/
function openAddDataElementGroupOrder() {

	getALLDataElementGroups();
	document.forms['dataElementGroups'].action = "addDataElementGroupOrderForCategory.action";
}
/*
* 	Get Data Elements By Data Element Group
*/
function getDataElementsByGroup( id ) {
	
	if ( id == null )
	{
		return;
	}

	var url = "../dhis-web-commons-ajax/getDataElements.action?id=" + id;

	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( getDataElementsByGroupReceived );	
	request.send( url );	
}

function getDataElementsByGroupReceived( datalement ) {
	
	var dataElements = datalement.getElementsByTagName( "dataElement" );
	
	var listDataElement = document.getElementById('availableDataElements');
	
	listDataElement.options.length = 0;
	
	for ( var i = 0; i < dataElements.length; i++ )
    {
        var id = dataElements[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
        var name = dataElements[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
        var option = new Option( name, id );
		
		option.onmousemove  = function( e ) {
		
			showToolTip( e, this.text );
		}
		
		listDataElement.add( option, null );
    }
	
	var availableDataElements = document.getElementById('availableDataElements');
	var dataElementIds = document.getElementById('dataElementIds');
	
	for( var i = 0 ; i < availableDataElements.options.length ; i++ ) {
	
		for ( var j = 0 ; j < dataElementIds.options.length ; j++ ) {				
		
			if ( availableDataElements.options[i].value == dataElementIds.options[j].value ) {
			
				availableDataElements.options[i].style.display='none';				
			}
		}
	}
	
	$("#dataElementGroups").showAtCenter( true );	
}

function getALLDataElementGroups() {
	
	var request = new Request();
	request.setResponseTypeXML( 'datalement' );
	request.setCallbackSuccess( getALLDataElementGroupsReceived );	
	request.send( "getAllDataElementGroups.action" );	
	
}

function getALLDataElementGroupsReceived( data ) {
	
	var availableDataElementGroups = document.getElementById('availableDataElementGroups');
		
	availableDataElementGroups.options.length = 0;
		
	var dataElementGroups = data.getElementsByTagName('dataElementGroup');
		
	availableDataElementGroups.options.add(new Option("ALL", null));	
		
	for( var i = 0 ; i < dataElementGroups.length ; i++ ) {
			
		var id = dataElementGroups.item(i).getElementsByTagName('id')[0].firstChild.nodeValue;
		var name = dataElementGroups.item(i).getElementsByTagName('name')[0].firstChild.nodeValue;
		
		var option = new Option( name, id );
		availableDataElementGroups.add(option, null);
	}
	
	getDataElementsByGroup(byId("availableDataElementGroups").value);
}

