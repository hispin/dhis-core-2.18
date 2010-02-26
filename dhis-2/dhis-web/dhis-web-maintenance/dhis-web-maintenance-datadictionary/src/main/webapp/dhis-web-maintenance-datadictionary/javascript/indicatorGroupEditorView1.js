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
	var list = document.getElementById('indicatorGroups');
	var id;

	for (id in indicatorGroups) {		
		var option = new Option( indicatorGroups[id], id );
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

	if (list.selectedIndex == -1) {
		list.disabled = true;
	}
}

/*==============================================================================
 *Move selected indicator 
 *==============================================================================*/

function addSelectedIndicators()
{
    var list = document.getElementById( 'availableIndicators' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        selectedIndicators[id] = availableIndicators[id];       
    
    }
    filterSelectedIndicators();
    filterAvailableIndicators();
}

function removeSelectedIndicators()
{
    var list = document.getElementById( 'selectedIndicators' );   

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false; 
        
        delete selectedIndicators[id];        
    }
    
    filterSelectedIndicators();
    filterAvailableIndicators();
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
    var selectedList = document.getElementById( 'selectedIndicators' );   
    selectedList.length = 0;
    var name = xmlObject.getElementsByTagName('name')[0].firstChild.nodeValue;
    var indicatorList = xmlObject.getElementsByTagName('indicators')[0].getElementsByTagName('indicator');	
    for ( var i = 0; i < indicatorList.length; i++ )
    {
        indicator = indicatorList.item(i);  
        var id = indicator.getElementsByTagName('id')[0].firstChild.nodeValue;
        var name = indicator.getElementsByTagName('name')[0].firstChild.nodeValue;
        selectedIndicators[id] = name;
    }
    filterSelectedIndicators();       
    document.getElementById('availableIndicators').disabled=false;
	visableAvailableIndicators();
    
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
 *   Filter Indicator 
 *==============================================================================*/

function filterSelectedIndicators()
{
	var filter = document.getElementById( 'selecteIndicatorsFilter' ).value;
    var list = document.getElementById( 'selectedIndicators' );
    
    list.options.length = 0;
    
    for ( var id in selectedIndicators )
    {
        var value = selectedIndicators[id];
        
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

function filterIndicatorGroups()
{
    var filter = document.getElementById( 'indicatorGroupsFilter' ).value;
    var list = document.getElementById( 'indicatorGroups' );
    
    list.options.length = 0;
    
    for ( var id in indicatorGroups )
    {
        var value = indicatorGroups[id];
        
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
    request.send( 'validateIndicatorGroup.action?name=' + name ); 
}

function validateAddIndicatorGroupReceived( xmlObject )
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
    request.send( 'addIndicatorGroupEditor.action?name=' + name + "&mode=editor"  );    
}

function createNewGroupReceived( xmlObject )
{       
    var id = xmlObject.getElementsByTagName( "id" )[0].firstChild.nodeValue;
    var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;
    var list = document.getElementById( 'indicatorGroups' );
    var option = new Option( name, id );
    option.selected = true;
    list.add(option , null );   
    document.getElementById( 'groupNameView' ).innerHTML = name;        
    selectedIndicators = new Object();
    filterSelectedIndicators();
    toggleById( 'addIndicatorGroupForm' );
    deleteDivEffect();  
}

/*==============================================================================
 * Update Indicator Group
 *==============================================================================*/

function showRenameIndicatorGroupForm()
{
	var list = byId('indicatorGroups');
	
	if( list.value== '' )
	{
		alert(i18n_select_indicator_group);
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
	request.send( 'validateIndicatorGroup.action?name=' + name ); 	
}

function validateRenameIndicatorGroupReceived( xmlObject )
{
    var type = xmlObject.getAttribute( 'type' );
    
    if( type=='input' )
    {
        alert(xmlObject.firstChild.nodeValue);
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
    request.setCallbackSuccess( renameGroupReceived );
    request.send( 'renameIndicatorGroupEditor.action?name=' + name + "&mode=editor&id=" +  byId('indicatorGroups').value);	
}

function renameGroupReceived( xmlObject )
{
	var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;
    var list = document.getElementById( 'indicatorGroups' );
    list.options[ list.selectedIndex ].text = name; 
    document.getElementById( 'groupNameView' ).innerHTML = name;        
    showHideDiv( 'addIndicatorGroupForm' );
    deleteDivEffect(); 
}

/*==============================================================================
 * Update Member of Indicator Group
 *==============================================================================*/
 
function updateIndicatorGroupMembers()
{
	try
	{
		var indicatorGroupsSelect = document.getElementById( 'indicatorGroups' );
	    var id = indicatorGroupsSelect.options[ indicatorGroupsSelect.selectedIndex ].value;	
		
	    var request = new Request();

	    var requestString = 'updateIndicatorGroupEditor.action';

	    var params = "id=" + id;
		params += "&mode=editor";

	    var selectedIndicatorMembers = document.getElementById( 'selectedIndicators' );

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
		alert( i18n_select_indicator_group );
	}
}

function updateIndicatorGroupMembersReceived( xmlObject ){       
    
	var name = xmlObject.getElementsByTagName( "name" )[0].firstChild.nodeValue;
    setMessage(i18n_update_success + " : " + name);
}

/*==============================================================================
 * Delete Indicator Group
 *==============================================================================*/
 
function deleteIndicatorGroup()
{
	if( window.confirm( i18n_confirm_delete ) )
	{
		var indicatorGroupsSelect = document.getElementById( 'indicatorGroups' );
	    var id = indicatorGroupsSelect.options[ indicatorGroupsSelect.selectedIndex ].value;	
		
		var request = new Request();
	    request.setResponseTypeXML( 'xmlObject' );
	    request.setCallbackSuccess( deleteIndicatorGroupReceived );
		request.send( 'deleteIndicatorGroupEditor.action?id=' + id ); 	
	}
}

function deleteIndicatorGroupReceived( xmlObject )
{
    var type = xmlObject.getAttribute( 'type' );    
    
    if ( type == 'success' )
    {
		var indicatorGroupsSelect = document.getElementById( 'indicatorGroups' );
        indicatorGroupsSelect.remove( indicatorGroupsSelect.selectedIndex );
    }
}
 
 