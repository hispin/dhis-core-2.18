function beforeSubmit()
{
	memberValidator = jQuery( "#memberValidator");
	memberValidator.children().remove();
	
	jQuery.each( jQuery( "#groupMembers" ).children(), function(i, item){
		item.selected = 'selected';
		memberValidator.append( '<option value="' + item.value + '" selected="selected">' + item.value + '</option>');
	});
}
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showDataElementGroupDetails( dataElementGroupId )
{
    var request = new Request();
    request.setResponseTypeXML( 'dataElementGroup' );
    request.setCallbackSuccess( dataElementGroupReceived );
    request.send( '../dhis-web-commons-ajax/getDataElementGroup.action?id=' + dataElementGroupId );
}

function dataElementGroupReceived( dataElementGroupElement )
{
    setInnerHTML( 'nameField', getElementValue( dataElementGroupElement, 'name' ) );
    setInnerHTML( 'memberCountField', getElementValue( dataElementGroupElement, 'memberCount' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove data element group
// -----------------------------------------------------------------------------

function removeDataElementGroup( dataElementGroupId, dataElementGroupName )
{
    removeItem( dataElementGroupId, dataElementGroupName, i18n_confirm_delete, "removeDataElementGroup.action" );
}

// -----------------------------------------------------------------------------
// Search data element group
// -----------------------------------------------------------------------------

function searchDataElementGroup()
{
	var key = $( '#key' ).val();
	
    if ( key != null && key != '' ) 
    {
    	var url = 'searchDataElementGroup.action?key=' + key;
    	
    	$( '#content' ).load( url, unLockScreen );
    	
    	lockScreen();
    }
    else 
    {
    	window.location.href='dataElementGroup.action?' + params;
    }
}

function searchDataElementGroupPaging( currentPage, pageSize ) 
{
	var params = 'key=' + getFieldValue( 'key' );
		params += '&currentPage=' + currentPage;
		params += '&pageSize=' + pageSize;

    var url = 'searchDataElementGroup.action?' + params;
    
    if ( getFieldValue( 'key' ) != null && getFieldValue( 'key' ) != '' ) 
    {
    	$( '#content' ).load( url, null, unLockScreen );
    	
    	lockScreen();
    }
    else 
    {
    	window.location.href='dataElementGroup.action?' + params;
    }
}

function changePageSizeSearch()
{
    var pageSize = $( '#sizeOfPage' ).val();
    searchDataElementGroupPaging( 1, pageSize );
}

function jumpToPageSearch()
{
    var pageSize = $( '#sizeOfPage' ).val();
    var currentPage = $( '#jumpToPage' ).val();
    searchDataElementGroupPaging( currentPage, pageSize );
}
