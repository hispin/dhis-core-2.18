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
	else if ( listId == 'availableIndicators' )
	{
		for (var id in availableIndicators)
		{		
			addOptionToListWithToolTip( list, id, availableIndicators[id] );
		}
	}
}

function initList() 
{
	refreshListById( 'availableGroups' );
	refreshListById( 'availableIndicators' );
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
	var availableList = byId('availableIndicators');
	var groupName = availableList.options[ availableList.selectedIndex ].text;
	var availableIndicatorGroups = indicatorGroups.getElementsByTagName( 'indicatorGroup' );
	var list = byId('assignedGroups');
	list.options.length = 0;
	
	for( var i=0;i<availableIndicatorGroups.length;i++)
	{
		var id = getElementValue( availableIndicatorGroups.item(i), 'id' );
		var name = getElementValue( availableIndicatorGroups.item(i), 'name' );
		addOptionToListWithToolTip( list, id, name );
		assignedGroups[id] = name;
	}
	
	refreshListById( 'availableGroups' );
	visableAvailableIndicators();
	enable('availableGroups');
	setInnerHTML('groupNameView', groupName);
}

function visableAvailableIndicators()
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
 *  New  Indicator Group
 *==============================================================================*/

function showAddIndicatorGroupForm()
{
	byId( 'groupName' ).value='';    
    byId( 'addRenameGroupButton' ).onclick=validateAddIndicatorGroup;
    showPopupWindowById( 'addIndicatorGroupForm', 450, 70 );
}

function validateAddIndicatorGroup()
{
	$.postJSON(
		"validateIndicatorGroup.action",
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
	request.sendAsPost( "name=" + name + "&mode=editor" );
    request.send( 'addIndicatorGroupEditor.action' );    
}

function createNewGroupReceived( xmlObject )
{
    var id = getElementValue( xmlObject, 'id' );
    var name = getElementValue( xmlObject, 'name' );
    var list = byId( 'availableGroups' );
    var option = new Option( name, id );
	option.selected = true;
	option.onmousemove  = function(e){
		showToolTip( e, this.text);
	}
	list.add(option , null );
	availableGroups[id] = name;
	byId( 'groupNameView' ).innerHTML = name;
    hideById( 'addIndicatorGroupForm' );
    unLockScreen();  
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
		byId( 'groupName' ).value = list.options[ list.selectedIndex ].text
		byId( 'addRenameGroupButton' ).onclick=validateRenameIndicatorGroup;
		showPopupWindowById( 'addIndicatorGroupForm', 450, 70 );
	}	
} 

function validateRenameIndicatorGroup()
{
	$.postJSON(
		"validateIndicatorGroup.action",
		{
			"name": getFieldValue( 'groupName' )
		},
		function( json )
		{
			if ( json.response == "success" )
			{
				renameGroup();
			}
			else
			{
				alert(json.message);
			}
		}
	);	

}

function renameGroup()
{
	var name = getFieldValue( 'groupName' );    
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( renameGroupReceived );
	var params = "name=" + name + "&mode=editor&id=" + byId('availableGroups').value;
	request.sendAsPost( params );
    request.send( 'renameIndicatorGroupEditor.action');	
}

function renameGroupReceived( xmlObject )
{
	var id = getElementValue( xmlObject, 'id' );
	var name = getElementValue( xmlObject, 'name' );
    var list = byId( 'availableGroups' );
	var option = list.options[ list.selectedIndex ];
	option.text = name;
	option.onmousemove = function(e) {
		showToolTip(e, name);
	}
	availableGroups[ id ] = name;
    byId( 'groupNameView' ).innerHTML = name;        
    hideById( 'addIndicatorGroupForm' );
    unLockScreen(); 
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
	var list = byId('availableGroups');
	
	try {
		var id = list.options[ list.selectedIndex ].value;
		var name = list.options[ list.selectedIndex ].text;

		if ( window.confirm( i18n_confirm_delete ) )
		{
			$.getJSON
			(
				'deleteIndicatorGroupEditor.action',
				{
					"id": list.value
				},
				function( json )
				{
					if ( json.response == "success" )
					{
						var list = byId('availableGroups');
						list.remove( list.selectedIndex );
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
		setHeaderDelayMessage(i18n_select_indicator_group);
	}
}