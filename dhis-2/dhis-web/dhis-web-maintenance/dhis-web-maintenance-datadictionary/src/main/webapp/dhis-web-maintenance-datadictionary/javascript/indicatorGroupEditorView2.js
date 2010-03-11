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

function initList() 
{
	var list = document.getElementById('availableGroups');
	var id;

	for (id in availableGroups) {		
		var option = new Option( availableGroups[id], id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
        list.add( option, null );  
		
	}

	list = document.getElementById('availableIndicators');

	for (id in availableIndicators) {		
		var option = new Option( availableIndicators[id], id );
		option.onmousemove  = function(e){
			showToolTip( e, this.text);
		}
        list.add( option, null );  
	}
	
}

/*==============================================================================
 *Move selected indicator 
 *==============================================================================*/

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

/*==============================================================================
 * Get Indicator Groups contain indicator
 *==============================================================================*/

function getAssignedIndicatorGroups( indicatorId )
{
	var request = new Request();
    request.setResponseTypeXML( 'indicatorGroups' );
    request.setCallbackSuccess( getAssignedIndicatorGroupsCompleted );
    request.send( 'getAssignedIndicatorGroups.action?indicatorId=' + indicatorId );    
}

function getAssignedIndicatorGroupsCompleted( indicatorGroups )
{
	assignedGroups = new Object();
	
	var availableIndicatorGroups = indicatorGroups.getElementsByTagName( 'indicatorGroup' );
	
	for( var i=0;i<availableIndicatorGroups.length;i++)
	{
		var id = availableIndicatorGroups.item(i).getElementsByTagName( 'id' )[0].firstChild.nodeValue;
		var name = availableIndicatorGroups.item(i).getElementsByTagName( 'name' )[0].firstChild.nodeValue;
		assignedGroups[id] = name;		
	}
	
	filterAssignedGroups();
}

/*==============================================================================
 *	Filter Select List
 *==============================================================================*/

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
				showToolTip( e, this.text);
			}
	        list.add( option, null );  
        }
    }
}

function filterAvailableIndicators()
{
    var filter = document.getElementById( 'availableIndicatorsFilter' ).value;
    var list = document.getElementById( 'availableIndicators' );
    
    list.options.length = 0;
    
    for ( var id in availableIndicators )
    {
        var value = availableIndicators[id];
        
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

/*==============================================================================
 *  New  Indicator Group
 *==============================================================================*/

function showAddIndicatorGroupForm()
{
	document.getElementById( 'groupName' ).value='';    
    document.getElementById( 'addRenameGroupButton' ).onclick=validateAddIndicatorGroup;
    setPositionCenter( 'addIndicatorGroupForm' );	
    showDivEffect();
	toggleById('addIndicatorGroupForm');
}

function validateAddIndicatorGroup()
{
	var name = getFieldValue('groupName');
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateAddIndicatorGroupReceived );
	request.sendAsPost( "name=" + name );
    request.send( 'validateIndicatorGroup.action' ); 
}

function validateAddIndicatorGroupReceived( xmlObject )
{
    var type = xmlObject.getAttribute( 'type' );
    
    if ( type=='input' )
    {
        setHeaderDelayMessage(xmlObject.firstChild.nodeValue);
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
	request.sendAsPost( "name=" + name + "&mode=editor" );
    request.send( 'addIndicatorGroupEditor.action' );    
}

function createNewGroupReceived( xmlObject )
{       
    var id = xmlObject.getElementsByTagName( "id" )[0].firstChild.nodeValue;
    var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;
    availableGroups[id] = name;
    filterAvailableGroups();
    toggleById( 'addIndicatorGroupForm' );
    deleteDivEffect();  
}

/*==============================================================================
 * Update Indicator Group
 *==============================================================================*/

function showRenameIndicatorGroupForm()
{
	var list = byId('availableGroups');
	
	if( list.value== '' )
	{
		setHeaderDelayMessage(i18n_select_indicator_group);
	}
	else
	{
		document.getElementById( 'groupName' ).value = list.options[ list.selectedIndex ].text
		document.getElementById( 'addRenameGroupButton' ).onclick=validateRenameIndicatorGroup;
		setPositionCenter( 'addIndicatorGroupForm' );	
		showDivEffect();
		toggleById('addIndicatorGroupForm');
	}	
} 

function validateRenameIndicatorGroup()
{
	var name = getFieldValue('groupName');
	var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( validateRenameIndicatorGroupReceived );
	request.sendAsPost("name=" + name);
	request.send( 'validateIndicatorGroup.action' ); 	
}

function validateRenameIndicatorGroupReceived( xmlObject )
{
    var type = xmlObject.getAttribute( 'type' );
    
    if( type=='input' )
    {
        setHeaderDelayMessage(xmlObject.firstChild.nodeValue);
    }
    
    if( type=='success' )
    {
        renameGroup();
    }
}

function renameGroup()
{
	var name = document.getElementById( 'groupName' ).value;    
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( createNewGroupReceived );
	var params = "name=" + name +  "&mode=editor&id=" +  byId('availableGroups').value;
	request.sendAsPost( params );
    request.send( 'renameIndicatorGroupEditor.action');	
}


/*==============================================================================
 * Update Member of Indicator Group
 *==============================================================================*/
 
function assignGroupsForIndicator()
{
	try
	{		
	    var indicatorId = byId('availableIndicators').value;	
		
	    var request = new Request();

	    var requestString = 'asignGroupsForIndicator.action';

	    var params = "indicatorId=" + indicatorId;
		params += "&mode=editor";

	    var selectedGroups = byId( 'assignedGroups' );

	    for ( var i = 0; i < selectedGroups.options.length; ++i)
	    {
	        params += '&indicatorGroups=' + selectedGroups.options[i].value;
	    }   
	    request.sendAsPost( params );
	    request.setResponseTypeXML( 'xmlObject' );  
	    request.setCallbackSuccess( assignGroupsForIndicatorReceived );
	    request.send( requestString );  
	}
	catch( e )
	{
		setHeaderDelayMessage( i18n_select_indicator_group );
	}
}

function assignGroupsForIndicatorReceived( xmlObject )
{	
    setHeaderDelayMessage( i18n_update_success );
}

/*==============================================================================
 * Delete Indicator Group
 *==============================================================================*/
 
function deleteIndicatorGroup()
{
	if( window.confirm( i18n_confirm_delete ) )
	{
		var list = byId('availableGroups');
	
		if( list.value== '' )
		{
			setHeaderDelayMessage(i18n_select_indicator_group);
		}else{			
			var request = new Request();
			request.setResponseTypeXML( 'xmlObject' );
			request.setCallbackSuccess( deleteIndicatorGroupReceived );
			request.send( 'deleteIndicatorGroupEditor.action?id=' + list.value ); 	
		}
	}
}

function deleteIndicatorGroupReceived( xmlObject )
{
    var type = xmlObject.getAttribute( 'type' );    
    
    if ( type == 'success' )
    {
		var list = byId('availableGroups');
        list.remove( list.selectedIndex );
    }
}
 
 