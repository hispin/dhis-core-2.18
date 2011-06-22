function beforeSubmit()
{
    memberValidator = jQuery( "#memberValidator" );
    memberValidator.children().remove();

    jQuery.each( jQuery( "#groupMembers" ).children(), function( i, item )
    {
        item.selected = 'selected';
        memberValidator.append( '<option value="' + item.value + '" selected="selected">' + item.value + '</option>' );
    } );
}

// -----------------------------------------------------------------------------
// Validate Add Data Element Group
// -----------------------------------------------------------------------------

function validateAddDataElementGroupSet()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( validateAddDataElementGroupSetCompleted );
    request.sendAsPost( "name=" + getFieldValue( "name" ) );
    request.send( "validateDataElementGroupSet.action" );
}

function validateAddDataElementGroupSetCompleted( message )
{
    var type = message.getAttribute( "type" );

    if ( type == "success" )
    {
        selectAllById( "groupMembers" );
        document.forms['addDataElementGroupSet'].submit();
    } else
    {
        setMessage( message.firstChild.nodeValue );
    }
}

// -----------------------------------------------------------------------------
// Delete Data Element Group
// -----------------------------------------------------------------------------

function deleteDataElementGroupSet( groupSetId, groupSetName )
{

    removeItem( groupSetId, groupSetName, i18n_confirm_delete, "deleteDataElementGroupSet.action" );
}

// -----------------------------------------------------------------------------
// Show Data Element Group Set details
// -----------------------------------------------------------------------------

function showDataElementGroupSetDetails( id )
{

    var request = new Request();
    request.setResponseTypeXML( 'dataElementGroupSet' );
    request.setCallbackSuccess( showDetailsCompleted );
    request.send( "../dhis-web-commons-ajax/getDataElementGroupSet.action?id=" + id );
}

function showDetailsCompleted( dataElementGroupSet )
{

    setInnerHTML( 'nameField', getElementValue( dataElementGroupSet, 'name' ) );
    setInnerHTML( 'memberCountField', getElementValue( dataElementGroupSet, 'memberCount' ) );

    showDetails();
}
