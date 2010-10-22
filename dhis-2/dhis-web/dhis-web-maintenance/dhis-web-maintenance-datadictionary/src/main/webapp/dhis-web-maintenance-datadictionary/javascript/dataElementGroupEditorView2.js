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

function refreshListById( listId )
{
	var list = byId( listId );
	list.options.length = 0;
	
	if ( listId == 'availableGroups' )
	{
		for (var id in availableGroups)
		{		
			addOptionToListWithToolTip( list, id, availableGroups[id] );
		}
	}
	else if ( listId == 'availableDataElements' )
	{
		for (var id in availableDataElements)
		{		
			addOptionToListWithToolTip( list, id, availableDataElements[id] );
		}
	}
}

function initAllList() 
{
	refreshListById( 'availableGroups' );
	refreshListById( 'availableDataElements' );
}

/*==============================================================================
 * Get DataElement Groups contain dataelement
 *==============================================================================*/

function getAssignedDataElementGroups( id )
{	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getAssignedGroupsCompleted );
    request.send( 'getAssignedDataElementGroups.action?dataElementId=' + id );
}

function getAssignedGroupsCompleted( xmlObject )
{
	assignedGroups = new Object();
	var availableList = byId("availableDataElements");
	var name = availableList.options[ availableList.selectedIndex ].text;
	var dataElementGroups = xmlObject.getElementsByTagName( 'dataElementGroup' );
	var list = byId('assignedGroups');
	list.options.length = 0;
	
	for( var i=0;i<dataElementGroups.length;i++)
	{
		var id = getElementValue( dataElementGroups.item(i), 'id' );
		var value = getElementValue( dataElementGroups.item(i), 'name' );
		addOptionToListWithToolTip( list, id, value );
		assignedGroups[id] = value;		
	}
	
	refreshListById( 'availableGroups' );
	visableAvailableDataElements();
    enable('availableGroups');
	setInnerHTML('groupNameView', name);
}

function visableAvailableDataElements()
{
	var selectedList = byId( 'assignedGroups' );
	var availableList = byId( 'availableGroups' );
	var selectedOptions = selectedList.options;
	var availableOptions = availableList.options;
	
	for(var i=0;i<availableOptions.length;i++){
		availableList.options[i].style.display='block';
		for(var j=0;j<selectedOptions.length;j++){			
			if(availableOptions[i].value==selectedOptions[j].value){				
				availableList.options[i].style.display='none';
			}
		}
	}
}

/*==============================================================================
 *  New  DataElement Group
 *==============================================================================*/

function deleteDataElementGroup()
{
    var dataElementGroupsSelect = byId( 'availableGroups' );

    try
    {
        var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;
        var name =  dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].text;
		
        if ( window.confirm( i18n_confirm_delete + '\n\n' + name ) )
        {
		    $.getJSON
			(
				'deleteDataElemenGroupEditor.action',
				{
					"id": id
				},
				function( json )
				{
					if ( json.response == "success" )
					{
						var dataElementGroupsSelect = byId( 'availableGroups' );
						dataElementGroupsSelect.remove( dataElementGroupsSelect.selectedIndex );                
					}
					else if ( json.response == "error" )
					{
						setFieldValue( 'warningArea', json.message );
			
						showWarning();
					}
				}
			);
        }
    }
    catch(e)
    {
        setHeaderDelayMessage( i18n_select_dataelement_group );
    }
}

function showRenameDataElementGroupForm()
{
    var dataElementGroupsSelect = byId( 'availableGroups' );

    try
    {
        var name = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].text;
        byId( 'addRenameGroupButton' ).onclick=validateRenameDataElementGroup;
        showPopupWindowById( 'addDataElementGroupForm', 450, 70 );
		byId( 'groupName' ).value = name;
    }
    catch(e)
    {
        setHeaderDelayMessage(i18n_select_dataelement_group);
    }
}

function validateRenameDataElementGroup()
{
    var dataElementGroupsSelect = byId( 'availableGroups' );
    var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;
    var name = byId( 'groupName' ).value;
    var request = new Request();
	
    $.postJSON(
		"validateDataElementGroup.action",
		{ "id": id, "name": name },
		function( json )
		{
			if ( json.response == "success" )
			{
				renameDataElementGroup();
			}
			else
			{
				alert(json.message);
			}
		}
	);
}

function renameDataElementGroup()
{
    var dataElementGroupsSelect = byId( 'availableGroups' );
    var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;
    var name = byId( 'groupName' ).value;
    var request = new Request();
	
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( renameDataElementGroupReceived );
	request.sendAsPost('id=' + id + '&name=' + name);
    request.send( 'renameDataElementGroupEditor.action' );

}

function renameDataElementGroupReceived( xmlObject )
{
	var id = getElementValue( xmlObject, 'id' );
	var name = getElementValue( xmlObject, 'name' );
    var list = byId( 'availableGroups' );
    var option = list.options[ list.selectedIndex ];
	option.text = name;
	option.onmousemove = function(e) {
		showToolTip(e, name);
	}
	availableGroups[id] = name;
    $( '#groupNameView' ).html( name );
    hideById( 'addDataElementGroupForm' );
	unLockScreen();
}

function showAddDataElementGroupForm()
{
    byId( 'groupName' ).value='';
    byId( 'addRenameGroupButton' ).onclick=validateAddDataElementGroup;
	showPopupWindowById( 'addDataElementGroupForm', 450, 70 );
}

function validateAddDataElementGroup()
{
	$.postJSON(
		"validateDataElementGroup.action",
		{
			"name": getFieldValue( 'groupName' )
		},
		function( json )
		{
			if ( json.response == "success" )
			{
				createNewGroup();
			}
			else
			{
				alert(json.message);
			}
		}
	);	
}

function createNewGroup()
{
    var name = byId( 'groupName' ).value;
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( createNewGroupReceived );
	request.sendAsPost( 'name=' + name);
    request.send( 'addDataElementGroupEditor.action' );
}

function createNewGroupReceived( xmlObject )
{
	var id = getElementValue( xmlObject, 'id' );
	var name = getElementValue( xmlObject, 'name' );
	var list = byId( 'availableGroups' );
	var option = new Option( name, id );
	option.selected = true;
	option.onmousemove  = function(e){
		showToolTip( e, name);				
	}
    list.add( option, null );
	availableGroups[id] = name;
	$( '#groupNameView' ).html( name );
    hideById( 'addDataElementGroupForm' );
    unLockScreen();
}

/*==============================================================================
 *  New  Assign DataElement Groups for dataelement
 *==============================================================================*/

function assignGroupsForDataElement()
{
    var dataElementId = byId( 'availableDataElements' ).value;   
	
	if( dataElementId=="" )
	{
		setHeaderDelayMessage( i18n_select_dataelement );
		return;
	}

    var request = new Request();

    var requestString = 'asignGroupsForDataElement.action';

    var params = "dataElementId=" + dataElementId;

    var selectedGroups = byId( 'assignedGroups' );

    for ( var i = 0; i < selectedGroups.options.length; ++i)
    {
        params += '&dataElementGroups=' + selectedGroups.options[i].value;
    }
    request.sendAsPost( params );
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( assignGroupsForDataElementReceived );
    request.send( requestString );
}

function assignGroupsForDataElementReceived( message )
{	
	setHeaderDelayMessage( i18n_update_success );
}