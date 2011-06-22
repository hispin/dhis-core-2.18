// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showDataDictionaryDetails( dataDictionaryId )
{
    var request = new Request();
    request.setResponseTypeXML( 'dataDictionary' );
    request.setCallbackSuccess( dataDictionaryReceived );
    request.send( 'getDataDictionary.action?id=' + dataDictionaryId );
}

function dataDictionaryReceived( dataDictionaryElement )
{
    setInnerHTML( 'nameField', getElementValue( dataDictionaryElement, 'name' ) );

    var description = getElementValue( dataDictionaryElement, 'description' );
    setInnerHTML( 'descriptionField', description ? description : '[' + i18n_none + ']' );

    var region = getElementValue( dataDictionaryElement, 'region' );
    setInnerHTML( 'regionField', region ? region : '[' + i18n_none + ']' );

    showDetails();
}

// -----------------------------------------------------------------------------
// Change DataDictionary
// -----------------------------------------------------------------------------

function dataDictionaryChanged( list )
{
    var id = list.options[list.selectedIndex].value;

    var url = "setCurrentDataDictionary.action?id=" + id;

    window.location.href = url;
}

// -----------------------------------------------------------------------------
// Remove DataDictionary
// -----------------------------------------------------------------------------

function removeDataDictionary( dataDictionaryId, dataDictionaryName )
{
    removeItem( dataDictionaryId, dataDictionaryName, i18n_confirm_delete, 'removeDataDictionary.action' );
}
