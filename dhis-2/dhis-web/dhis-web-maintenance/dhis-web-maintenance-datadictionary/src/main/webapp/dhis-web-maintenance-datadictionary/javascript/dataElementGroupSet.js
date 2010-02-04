// -----------------------------------------------------------------------------
// Validate Update Data Element Group
// -----------------------------------------------------------------------------

function validateUpdateDataElementGroupSet(){

	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( validateUpdateDataElementGroupSetCompleted );
	request.sendAsPost( "id=" + getFieldValue("id") + "&name=" +  getFieldValue("name"));
	request.send( "validateDataElementGroupSet.action");    
	
}

function validateUpdateDataElementGroupSetCompleted( message ){
	var type = message.getAttribute("type");
	if(type=="success"){
		selectAllById("groupMembers");
		document.forms['updateDataElementGroupSet'].submit();
	}else{
		setMessage(message.firstChild.nodeValue);
	}
}

// -----------------------------------------------------------------------------
// Validate Add Data Element Group
// -----------------------------------------------------------------------------

function validateAddDataElementGroupSet(){		
	
	var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( validateAddDataElementGroupSetCompleted );    
	request.sendAsPost( "name=" +  getFieldValue("name") );
	request.send( "validateDataElementGroupSet.action");
	
}

function validateAddDataElementGroupSetCompleted( message ){
	var type = message.getAttribute("type");
	if(type=="success"){
		selectAllById("groupMembers");
		document.forms['addDataElementGroupSet'].submit();
	}else{
		setMessage(message.firstChild.nodeValue);
	}
}

// -----------------------------------------------------------------------------
// Delete Data Element Group
// -----------------------------------------------------------------------------

function deleteDataElementGroupSet( id ){
	
	deleteItem( id, "", i18n_confirm_delete, "deleteDataElementGroupSet.action" );
}

// -----------------------------------------------------------------------------
// Show Data Element Group Set details
// -----------------------------------------------------------------------------

function showDataElementGroupSetDetails( id ){

	var request = new Request();
    request.setResponseTypeXML( 'dataElementGroupSet' );
    request.setCallbackSuccess( showDetailsCompleted );
    request.send( "showDataElementGroupSetDetails.action?id=" + id);
	
}


function showDetailsCompleted( dataElementGroupSet ){

	setFieldValue( 'nameField', getElementValue( dataElementGroupSet, 'name' ) );
    setFieldValue( 'memberCountField', getElementValue( dataElementGroupSet, 'memberCount' ) );

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

    list = document.getElementById( 'availableDataElementGroups' );

    for ( id in availableDataElementGroups )
    {
        list.add( new Option( availableDataElementGroups[id], id ), null );
    }
}

function filterAvailableDataElementGroups()
{
    var filter = document.getElementById( 'availableDataElementGroupsFilter' ).value;
    var list = document.getElementById( 'availableDataElementGroups' );
    
    list.options.length = 0;
    
    for ( var id in availableDataElementGroups )
    {
        var value = availableDataElementGroups[id];
        
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
    var list = document.getElementById( 'availableDataElementGroups' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        groupMembers[id] = availableDataElementGroups[id];
        
        delete availableDataElementGroups[id];        
    }
    
    filterGroupMembers();
    filterAvailableDataElementGroups();
}

function removeGroupMembers()
{
    var list = document.getElementById( 'groupMembers' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableDataElementGroups[id] = groupMembers[id];
        
        delete groupMembers[id];        
    }
    
    filterGroupMembers();
    filterAvailableDataElementGroups();
}


