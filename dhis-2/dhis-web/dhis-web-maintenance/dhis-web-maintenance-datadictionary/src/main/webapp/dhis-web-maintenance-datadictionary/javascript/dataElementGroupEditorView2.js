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
// ========================================================================
function initAllList()
{
    var list = document.getElementById( 'availableGroups' );
    var id;

    for ( id in availableGroups )
    {
		var option = new Option( availableGroups[id], id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
        list.add( option, null );
    }

    list = document.getElementById( 'availableDataElements' );

    for ( id in availableDataElements )
    {
		var option = new Option( availableDataElements[id], id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}		
        list.add( option, null );        
    }    
}

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
	var dataElementGroups = xmlObject.getElementsByTagName( 'dataElementGroup' );
	
	for( var i=0;i<dataElementGroups.length;i++)
	{
		var id = dataElementGroups.item(i).getElementsByTagName( 'id' )[0].firstChild.nodeValue;
		var name = dataElementGroups.item(i).getElementsByTagName( 'name' )[0].firstChild.nodeValue;
		assignedGroups[id] = name;		
	}
	
	filterAssignedGroups();
	
}


function addSelectedGroups()
{
    var list = document.getElementById( 'availableGroups' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        assignedGroups[id] = availableGroups[id];

    }

    filterAssignedGroups();   
}

function removeSelectedGroups()
{
    var list = document.getElementById( 'assignedGroups' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;      

		availableGroups[id] = assignedGroups[id];		

        delete assignedGroups[id];
    }

    filterAssignedGroups();    
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
            var option = new Option( value, id );
			option.onmousemove  = function(e){
				showToolTip( e, this.text);
			}
	        list.add( option, null );
	    }
    }
}

function filterAssignedGroups()
{
    var filter = document.getElementById( 'assignedGroupsFilter' ).value;
    var list = document.getElementById( 'assignedGroups' );

    list.options.length = 0;

    for ( var id in assignedGroups )
    {
        var value = assignedGroups[id];

        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
			var option = new Option( value, id );
			option.onmousemove  = function(e){
				showToolTip( e, value);				
			}
            list.add( option, null );			

        }
    }
}

function filterAvailableGroups()
{
    var filter = document.getElementById( 'availableGroupsFilter' ).value;
    var list = document.getElementById( 'availableGroups' );

    list.options.length = 0;

    for ( var id in availableGroups )
    {
        var value = availableGroups[id];

        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
			var option = new Option( value, id );
			option.onmousemove  = function(e){
				showToolTip( e, value);				
			}
            list.add( option, null );			

        }
    }
}

function deleteDataElementGroup()
{
    var dataElementGroupsSelect = document.getElementById( 'availableGroups' );

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

    if ( type=='success' )
    {
        var dataElementGroupsSelect = document.getElementById( 'availableGroups' );
        dataElementGroupsSelect.remove( dataElementGroupsSelect.selectedIndex );                
    }
}

function showRenameDataElementGroupForm()
{
    var dataElementGroupsSelect = document.getElementById( 'availableGroups' );

    try
    {
        var name = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].text;
        document.getElementById( 'addRenameGroupButton' ).onclick=validateRenameDataElementGroup;
        setPositionCenter( 'addDataElementGroupForm' );
		showById('addDataElementGroupForm');
		document.getElementById( 'groupName' ).value = name;
        showDivEffect();
    }
    catch(e)
    {
        alert(i18n_select_dataelement_group);
    }
}

function validateRenameDataElementGroup()
{
    var dataElementGroupsSelect = document.getElementById( 'availableGroups' );
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

    if ( type=='input' )
    {
        alert(xmlObject.firstChild.nodeValue);
    }
    if ( type=='success' )
    {
        renameDataElementGroup();
    }
}

function renameDataElementGroup()
{
    var dataElementGroupsSelect = document.getElementById( 'availableGroups' );
    var id = dataElementGroupsSelect.options[ dataElementGroupsSelect.selectedIndex ].value;
    var name = document.getElementById( 'groupName' ).value;
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( renameDataElementGroupReceived );
    request.send( 'renameDataElementGroupEditor.action?id=' + id + '&name=' + name );

}

function renameDataElementGroupReceived( xmlObject )
{
    var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;
    var list = document.getElementById( 'availableGroups' );
    list.options[ list.selectedIndex ].text = name;
    document.getElementById( 'groupNameView' ).innerHTML = name;
    showHideDiv( 'addDataElementGroupForm' );
    deleteDivEffect();
}

function showAddDataElementGroupForm()
{
    document.getElementById( 'groupName' ).value='';
    document.getElementById( 'addRenameGroupButton' ).onclick=validateAddDataElementGroup;
    setPositionCenter( 'addDataElementGroupForm' );
	showById('addDataElementGroupForm');
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

    if ( type=='input' )
    {
        alert(xmlObject.firstChild.nodeValue);
    }
    if ( type=='success' )
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

function createNewGroupReceived( xmlObject )
{
    var id = xmlObject.getElementsByTagName( "id" )[0].firstChild.nodeValue;
    var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;    
	availableGroups[id] = name;
    filterAvailableGroups();
    showHideDiv( 'addDataElementGroupForm' );
    deleteDivEffect();
}

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