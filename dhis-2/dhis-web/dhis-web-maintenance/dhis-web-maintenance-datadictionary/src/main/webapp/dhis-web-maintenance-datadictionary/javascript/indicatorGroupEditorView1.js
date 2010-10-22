function showToolTip( e, value )
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
	tooltipDiv.innerHTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + value;
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
	var	list = byId( listId );
	list.options.length = 0;
	
	if ( listId == 'indicatorGroups' )
	{
		for (var id in indicatorGroups)
		{
			addOptionToListWithToolTip( list, id, indicatorGroups[id] );
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
	refreshListById ('indicatorGroups');
	refreshListById ('availableIndicators');
}

/*==============================================================================
 * Get Indicator
 *==============================================================================*/
 
function getIndicatorGroup( listbox )
{
	selectedIndicators = new Object();
    var id = listbox.options[ listbox.selectedIndex ].value;  
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( getIndicatorGroupCompleted );
    request.send( 'getIndicatorGroupEditor.action?id=' + id );    
}

function getIndicatorGroupCompleted( xmlObject )
{
    var selectedList = byId( 'selectedIndicators' );   
    selectedList.length = 0;
    var groupName = getElementValue( xmlObject, 'name');
    var indicatorList = xmlObject.getElementsByTagName('indicators')[0].getElementsByTagName('indicator');
	
    for ( var i = 0; i < indicatorList.length; i++ )
    {
        indicator = indicatorList.item(i);  
        var id = getElementValue( indicator, 'id' );
        var name = getElementValue( indicator, 'name' );
		addOptionToListWithToolTip( selectedList, id, name );
		selectedIndicators[id] = name;
    }
	
	refreshListById( 'availableIndicators' );
	visableAvailableIndicators();
    enable('availableIndicators');
	setInnerHTML('groupNameView', groupName);
}

function visableAvailableIndicators()
{
	var selectedList = byId( 'selectedIndicators' );
	var availableList = byId( 'availableIndicators' );
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
	var name = getFieldValue( 'groupName' );    
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( createNewGroupReceived );
	request.sendAsPost('name=' + name + '&mode=editor' );
    request.send( 'addIndicatorGroupEditor.action'  );    
}

function createNewGroupReceived( xmlObject )
{       
	var id = getElementValue( xmlObject, 'id' );
	var name = getElementValue( xmlObject, 'name' );
    var list = byId( 'indicatorGroups' );
    var option = new Option( name, id );
	option.selected = true;
	option.onmousemove  = function(e){
		showToolTip(e, this.text);
	}
	list.add(option , null );
	indicatorGroups[id] = name;
    byId( 'groupNameView' ).innerHTML = name;
    hideById( 'addIndicatorGroupForm' );
    unLockScreen();  
}

/*==============================================================================
 * Update Indicator Group
 *==============================================================================*/

function showRenameIndicatorGroupForm()
{
	var list = byId('indicatorGroups');
	
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
	var name = byId( 'groupName' ).value;    
    var request = new Request();
    request.setResponseTypeXML( 'xmlObject' );
    request.setCallbackSuccess( renameGroupReceived );
	request.sendAsPost('name=' + name + '&mode=editor&id=' + byId('indicatorGroups').value);
    request.send( 'renameIndicatorGroupEditor.action');	
}

function renameGroupReceived( xmlObject )
{
	var id = getElementValue( xmlObject, 'id' );
	var name = getElementValue( xmlObject, 'name' );
    var list = byId( 'indicatorGroups' );
    var option = list.options[ list.selectedIndex ];
	option.text = name;
	option.onmousemove = function(e) {
		showToolTip(e, name);
	}
	indicatorGroups[ id ] = name;
    byId( 'groupNameView' ).innerHTML = name;        
    hideById( 'addIndicatorGroupForm' );
    unLockScreen(); 
}

/*==============================================================================
 * Update Member of Indicator Group
 *==============================================================================*/
 
function updateIndicatorGroupMembers()
{
	try
	{
	    var id = $("#indicatorGroups").val();	    
	    var request = new Request();
	    var requestString = 'updateIndicatorGroupEditor.action';
	    var params = "id=" + id;
		params += "&mode=editor";

	    var selectedIndicatorMembers = byId( 'selectedIndicators' );

	    for ( var i = 0; i < selectedIndicatorMembers.options.length; ++i)
	    {
	        params += '&groupMembers=' + selectedIndicatorMembers.options[i].value;
	    }   
	    request.sendAsPost( params );
	    request.setResponseTypeXML( 'xmlObject' );  
	    request.setCallbackSuccess( updateIndicatorGroupMembersReceived );
	    request.send( requestString );  
	}
	catch( e )
	{
		setHeaderDelayMessage( i18n_select_indicator_group );
	}
}

function updateIndicatorGroupMembersReceived( xmlObject )
{   
    var indicatorGroupsSelect = byId( 'indicatorGroups' );
    setHeaderDelayMessage( i18n_update_success + " : " + indicatorGroupsSelect.options[ indicatorGroupsSelect.selectedIndex ].text );
}

/*==============================================================================
 * Delete Indicator Group
 *==============================================================================*/

function deleteIndicatorGroup()
{
	var indicatorGroupsSelect = byId( 'indicatorGroups' );

	try {
		var id = indicatorGroupsSelect.options[ indicatorGroupsSelect.selectedIndex ].value;
		var name = indicatorGroupsSelect.options[ indicatorGroupsSelect.selectedIndex ].text;
	
		if ( window.confirm( i18n_confirm_delete + '\n\n' + name ) )
		{
			$.getJSON
			(
				'deleteIndicatorGroupEditor.action',
				{
					"id": id
				},
				function( json )
				{
					if ( json.response == "success" )
					{
						var indicatorGroupsSelect = byId( 'indicatorGroups' );
						indicatorGroupsSelect.remove( indicatorGroupsSelect.selectedIndex );
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