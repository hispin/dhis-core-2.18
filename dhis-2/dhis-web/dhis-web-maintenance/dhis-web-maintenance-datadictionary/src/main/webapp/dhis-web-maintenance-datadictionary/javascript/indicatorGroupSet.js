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

function deleteIndicatorGroupSet( groupSetId, groupSetName ){
	
	removeItem( groupSetId, groupSetName, i18n_confirm_delete, "deleteIndicatorGroupSet.action" );
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
