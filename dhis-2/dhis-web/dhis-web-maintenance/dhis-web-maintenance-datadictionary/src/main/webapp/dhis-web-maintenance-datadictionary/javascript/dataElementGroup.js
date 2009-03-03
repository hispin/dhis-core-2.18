
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showDataElementGroupDetails( dataElementGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'dataElementGroup' );
    request.setCallbackSuccess( dataElementGroupReceived );
    request.send( 'getDataElementGroup.action?id=' + dataElementGroupId );
}

function dataElementGroupReceived( dataElementGroupElement )
{
    setFieldValue( 'nameField', getElementValue( dataElementGroupElement, 'name' ) );
    setFieldValue( 'memberCountField', getElementValue( dataElementGroupElement, 'memberCount' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove data element group
// -----------------------------------------------------------------------------

function removeDataElementGroup( dataElementGroupId, dataElementGroupName )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + dataElementGroupName );
    
    if ( result )
    {
        window.location.href = 'removeDataElementGroup.action?id=' + dataElementGroupId;
    }
}

// -----------------------------------------------------------------------------
// Add data element group
// -----------------------------------------------------------------------------

function validateAddDataElementGroup()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    request.send( 'validateDataElementGroup.action?name=' + getFieldValue( 'name' ) );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var availableDataElements = document.getElementById( 'availableDataElements' );
        availableDataElements.selectedIndex = -1;
        
        var groupMembers = document.getElementById( 'groupMembers' );
        for ( var i = 0; i < groupMembers.options.length; ++i )
        {
            groupMembers.options[i].selected = true;
        }
        
        var form = document.getElementById( 'addDataElementGroupForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_data_element_group_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update data element group
// -----------------------------------------------------------------------------

function validateUpdateDataElementGroup()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( 'validateDataElementGroup.action?id=' + getFieldValue( 'id' ) +
        '&name=' + getFieldValue( 'name' ) );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var availableDataElements = document.getElementById( 'availableDataElements' );
        availableDataElements.selectedIndex = -1;
        
        var groupMembers = document.getElementById( 'groupMembers' );
        for ( var i = 0; i < groupMembers.options.length; ++i )
        {
            groupMembers.options[i].selected = true;
        }

        var form = document.getElementById( 'updateDataElementGroupForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_data_element_group_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Select lists
// -----------------------------------------------------------------------------

function initLists()
{
    var list = document.getElementById( 'groupMembers' );
    var id;

    for ( id in groupMembers )
    {
        list.add( new Option( groupMembers[id], id ), null );
    }

    list = document.getElementById( 'availableDataElements' );

    for ( id in availableDataElements )
    {
        list.add( new Option( availableDataElements[id], id ), null );
    }
}

function filterGroupMembers()
{
    var filter = document.getElementById( 'groupMembersFilter' ).value;
    var list = document.getElementById( 'groupMembers' );
    
    list.options.length = 0;
    
    for ( var id in groupMembers )
    {
        var value = groupMembers[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterAvailableDataElements()
{
    var filter = document.getElementById( 'availableDataElementsFilter' ).value;
    var list = document.getElementById( 'availableDataElements' );
    
    list.options.length = 0;
    
    for ( var id in availableDataElements )
    {
        var value = availableDataElements[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function addGroupMembers()
{
    var list = document.getElementById( 'availableDataElements' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        groupMembers[id] = availableDataElements[id];
        
        delete availableDataElements[id];        
    }
    
    filterGroupMembers();
    filterAvailableDataElements();
}

function removeGroupMembers()
{
    var list = document.getElementById( 'groupMembers' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        //availableDataElements[id] = groupMembers[id];
        
        delete groupMembers[id];        
    }
    
    filterGroupMembers();
    filterAvailableDataElements();
}

// -----------------------------------------------------------------------------
// New data element group
// -----------------------------------------------------------------------------

function initAllList()
{
	var list = document.getElementById( 'dataElementGroups' );
    var id;

    for ( id in dataElementGroups )
    {
        list.add( new Option( dataElementGroups[id], id ), null );
    }

    list = document.getElementById( 'availableDataElements' );

    for ( id in availableDataElements )
    {
        list.add( new Option( availableDataElements[id], id ), null );
    }
	
	if(list.selectedIndex==-1){
		list.disabled = true;
	}
}

function addSelectDataElements()
{
	var list = document.getElementById( 'availableDataElements' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        selectedDataElements[id] = availableDataElements[id];       
    
    }
	filterSelectedDataElements();
	filterAvailableDataElements();
}

function removeSelectedDataElements()
{
    var list = document.getElementById( 'selectedDataElements' );	

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;
		
        //availableDataElements[id] = selectedDataElements[id];
        
        delete selectedDataElements[id];        
    }
    
    filterSelectedDataElements();
    filterAvailableDataElements();
}

function filterDataElementGroups()
{
	var filter = document.getElementById( 'dataElementGroupsFilter' ).value;
    var list = document.getElementById( 'dataElementGroups' );
    
    list.options.length = 0;
    
    for ( var id in dataElementGroups )
    {
        var value = dataElementGroups[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterSelectedDataElements()
{
	var filter = document.getElementById( 'selecteDataElementsFilter' ).value;
    var list = document.getElementById( 'selectedDataElements' );
    
    list.options.length = 0;
    
    for ( var id in selectedDataElements )
    {
        var value = selectedDataElements[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}


function getDataElementGroup( dataElementGroupList )
{			
	selectedDataElements = new Object();
	var id = dataElementGroupList.options[ dataElementGroupList.selectedIndex ].value;	
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getDataElementGroupCompleted );
    request.send( 'getDataElementGroupEditor.action?id=' + id );	
}

function getDataElementGroupCompleted( xmlObject )
{	
	var selectedList = document.getElementById( 'selectedDataElements' );	
	selectedList.length = 0;
	name = xmlObject.getElementsByTagName('name')[0].firstChild.nodeValue;
	var dataElementList = xmlObject.getElementsByTagName('dataElement');
	for ( var i = 0; i < dataElementList.length; i++ )
	{
		dataElement = dataElementList.item(i);	
		var id = dataElement.getAttribute('id');
		var value = dataElement.firstChild.nodeValue;		
		selectedDataElements[id] = value;
	}
	filterSelectedDataElements();	
	document.getElementById( 'groupNameView').innerHTML = i18n_member_of + " <b>" + name + "</b>";
	document.getElementById('availableDataElements').disabled=false;
	
}

function updateDataElementGroupMembers(){
	var dataElementGroupsSelect = document.getElementById( 'dataElementGroups' );
	var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;
	
	var request = new Request();

	var requestString = 'updateDataElementGroupEditor.action';

	var params = "id=" + id;

	var selectedDataElementMembers = document.getElementById( 'selectedDataElements' );

	for ( var i = 0; i < selectedDataElementMembers.options.length; ++i)
	{
		params += '&selectedDataElements=' + selectedDataElementMembers.options[i].value;
	}	
	request.sendAsPost( params );
	request.setResponseTypeXML( 'xmlObject' );	
	request.setCallbackSuccess( updateDataElementGroupMembersReceived );
	request.send( requestString );	
}

function updateDataElementGroupMembersReceived( xmlObject )
{		
	dataElementGroupsSelect = document.getElementById( 'dataElementGroups' );	
	document.getElementById('message').style.display='block';
	document.getElementById('message').innerHTML = i18n_update_success + " : " + dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].text;	
	
}

function deleteDataElementGroup()
{
	var dataElementGroupsSelect = document.getElementById( 'dataElementGroups' );		
	try
	{
		var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;
		var name =  dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].text;
		if( window.confirm( i18n_confirm_delete + '\n\n' + name ) )
		{
			var request = new Request();
			request.setResponseTypeXML( 'xmlObject' );	
			request.setCallbackSuccess( deleteDataElementGroupReceived );
			request.send( 'deleteDataElemenGroupEditor.action?id=' + id );	
		}			
	}
	catch(e)
	{
		alert( i18n_select_dataelement_group );
	}
}

function deleteDataElementGroupReceived( xmlObject )
{
	var type = xmlObject.getAttribute( 'type' );
	if(type=='success')
	{
		var dataElementGroupsSelect = document.getElementById( 'dataElementGroups' );			
		dataElementGroupsSelect.remove( dataElementGroupsSelect.selectedIndex );
		document.getElementById( 'groupNameView' ).innerHTML = "";
		selectedDataElements = new Object();
		filterSelectedDataElements();
	}
}

function showRenameDataElementGroupForm()
{		
	var dataElementGroupsSelect = document.getElementById( 'dataElementGroups' );
		
	try
	{
		var name = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].text;
		document.getElementById( 'addRenameGroupButton' ).onclick=validateRenameDataElementGroup;
		showDivCenter( 'addDataElementGroupForm' );
		showDivEffect();
		document.getElementById( 'groupName' ).value = name;
	}
	catch(e)
	{
		alert(i18n_select_dataelement_group);
	}
	
}

function validateRenameDataElementGroup()
{
	var dataElementGroupsSelect = document.getElementById( 'dataElementGroups' );
	var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;	
	var name = document.getElementById( 'groupName' ).value;	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validateRenameDataElementGroupReceived );
	request.send( 'validateDataElementGroup.action?id=' + id + '&name=' + name );	
}

function validateRenameDataElementGroupReceived( xmlObject )
{
	var type = xmlObject.getAttribute( 'type' );
	if(type=='input')
	{
		alert(xmlObject.firstChild.nodeValue);
	}
	if(type=='success')
	{
		renameDataElementGroup();
	}
}

function renameDataElementGroup()
{
	var dataElementGroupsSelect = document.getElementById( 'dataElementGroups' );
	var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;	
	var name = document.getElementById( 'groupName' ).value;		
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( renameDataElementGroupReceived );
	request.send( 'renameDataElementGroupEditor.action?id=' + id + '&name=' + name );
	
}

function renameDataElementGroupReceived( xmlObject ){	
	var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;
	var list = document.getElementById( 'dataElementGroups' );
	list.options[ list.selectedIndex ].text = name;	
	document.getElementById( 'groupNameView' ).innerHTML = name;		
	showHideDiv( 'addDataElementGroupForm' );
	deleteDivEffect();		
}

function showAddDataElementGroupForm()
{
	document.getElementById( 'groupName' ).value='';	
	document.getElementById( 'addRenameGroupButton' ).onclick=validateAddDataElementGroup;
	showDivCenter( 'addDataElementGroupForm' );
	showDivEffect();
}

function validateAddDataElementGroup()
{
	var name = document.getElementById( 'groupName' ).value;	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( validateAddDataElementGroupReceived );
	request.send( 'validateDataElementGroup.action?name=' + name );	
}

function validateAddDataElementGroupReceived( xmlObject )
{
	var type = xmlObject.getAttribute( 'type' );
	if(type=='input')
	{
		alert(xmlObject.firstChild.nodeValue);
	}
	if(type=='success')
	{
		createNewGroup();
	}
}

function createNewGroup()
{
	var name = document.getElementById( 'groupName' ).value;	
	var request = new Request();
	request.setResponseTypeXML( 'xmlObject' );
	request.setCallbackSuccess( createNewGroupReceived );
	request.send( 'addDataElementGroupEditor.action?name=' + name );	
}

function createNewGroupReceived( xmlObject ){		
	var id = xmlObject.getElementsByTagName( "id" )[0].firstChild.nodeValue;
	var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;
	var list = document.getElementById( 'dataElementGroups' );
	var option = new Option( name, id );
	option.selected = true;
	list.add(option , null );	
	document.getElementById( 'groupNameView' ).innerHTML = name;		
	selectedDataElements = new Object();
	filterSelectedDataElements();		
	showHideDiv( 'addDataElementGroupForm' );
	deleteDivEffect();	
}
