// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showConceptDetails( conceptId )
{
    var request = new Request();
    request.setResponseTypeXML( 'concept' );
    request.setCallbackSuccess( conceptReceived );
    request.send( 'getConcept.action?id=' + conceptId );
}

function conceptReceived( conceptElement )
{
    setFieldValue( 'nameField', getElementValue( conceptElement, 'name' ) );

    showDetails();
}

// -----------------------------------------------------------------------------
// Remove category concept
// -----------------------------------------------------------------------------

function removeConcept( conceptId, conceptName )
{
    removeItem( conceptId, conceptName, i18n_confirm_delete, 'removeConcept.action' );
}

// -----------------------------------------------------------------------------
// Add/Update category concept
// -----------------------------------------------------------------------------

function validateAddUpdateConcept( mode )
{
    var name = $( "#name" ).val();

    $.getJSON( "validateAddUpdateConcept.action", {
        "name" : name,
        "mode" : mode
    }, function( json )
    {
        if ( json.response == "success" )
        {
            if ( mode == "add" )
            {
                byId( "addConceptForm" ).submit();
                return;
            }
            byId( "updateConceptForm" ).submit();
        } else if ( json.response == "input" )
        {
            setHeaderDelayMessage( json.message );
        }
    } );
}
