// -----------------------------------------------------------------------------
// Validate Update  Indicator Group Set
// -----------------------------------------------------------------------------

function validateUpdateIndicatorGroupSet(){

	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( validateUpdateIndicatorGroupSetCompleted );
	request.sendAsPost( "id=" + getFieldValue("id") + "&name=" +  getFieldValue("name"));
	request.send( "validateIndicatorGroupSet.action");    	
}

function validateUpdateIndicatorGroupSetCompleted( message ){
	var type = message.getAttribute("type");
	if(type=="success"){
		selectAllById("groupMembers");
		document.forms['updateIndicatorGroupSet'].submit();
	}else{
		setMessage(message.firstChild.nodeValue);
	}
}

// -----------------------------------------------------------------------------
// Validate Add Indicator Group Set
// -----------------------------------------------------------------------------

function validateAddIndicatorGroupSet(){	

	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( validateAddIndicatorGroupSetCompleted );
	request.sendAsPost( "name=" + getFieldValue("name") );
	request.send( "validateIndicatorGroupSet.action");    
	
}

function validateAddIndicatorGroupSetCompleted( message ){
	var type = message.getAttribute("type");
	if(type=="success"){
		selectAllById("groupMembers");
		document.forms['addIndicatorGroupSet'].submit();
	}else{
		setMessage(message.firstChild.nodeValue);
	}
}

// -----------------------------------------------------------------------------
// Delete Indicator Group Set
// -----------------------------------------------------------------------------

function deleteIndicatorGroupSet( id ){
	if(window.confirm(i18n_confirm_delete)){
		window.location = "deleteIndicatorGroupSet.action?id=" + id;
	}
}

// -----------------------------------------------------------------------------
// Show Data Element Group Set details
// -----------------------------------------------------------------------------

function showIndicatorGroupSetDetails( id ){

	var request = new Request();
    request.setResponseTypeXML( 'indicatorGroupSet' );
    request.setCallbackSuccess( showDetailsCompleted );
	request.sendAsPost( "id=" + id );
	request.send( "showIndicatorGroupSetDetails.action"); 
	
}

function showDetailsCompleted( indicatorGroupSet ){

	setFieldValue( 'nameField', getElementValue( indicatorGroupSet, 'name' ) );
    setFieldValue( 'memberCountField', getElementValue( indicatorGroupSet, 'memberCount' ) );

    showDetails();
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

    list = document.getElementById( 'availableIndicatorGroups' );

    for ( id in availableIndicatorGroups )
    {
        list.add( new Option( availableIndicatorGroups[id], id ), null );
    }
}

function filterAvailableIndicatorGroups()
{
    var filter = document.getElementById( 'availableIndicatorGroupsFilter' ).value;
    var list = document.getElementById( 'availableIndicatorGroups' );
    
    list.options.length = 0;
    
    for ( var id in availableIndicatorGroups )
    {
        var value = availableIndicatorGroups[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
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

function addGroupMembers()
{
    var list = document.getElementById( 'availableIndicatorGroups' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        groupMembers[id] = availableIndicatorGroups[id];
        
        delete availableIndicatorGroups[id];        
    }
    
    filterGroupMembers();
    filterAvailableIndicatorGroups();
}

function removeGroupMembers()
{
    var list = document.getElementById( 'groupMembers' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableIndicatorGroups[id] = groupMembers[id];
        
        delete groupMembers[id];        
    }
    
    filterGroupMembers();
    filterAvailableIndicatorGroups();
}


