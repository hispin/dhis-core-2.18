
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
    setFieldValue( 'nameField', getElementValue( dataDictionaryElement, 'name' ) );
    
    var description = getElementValue( dataDictionaryElement, 'description' );
    setFieldValue( 'descriptionField', description ? description : '[' + i18n_none + ']' );
    
    var region = getElementValue( dataDictionaryElement, 'region' );
    setFieldValue( 'regionField', region ? region : '[' + i18n_none + ']' );
    
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

// -----------------------------------------------------------------------------
// Add DataDictionary
// -----------------------------------------------------------------------------

function validateAddDataDictionary()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );
    
    request.send( 'validateDataDictionary.action?name=' + getFieldValue( 'name' ) +
    	'&description=' + getFieldValue( 'description' ) +
    	'&region=' + getFieldValue( 'region' ) );

    return false;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	var availableDataElements = document.getElementById( 'availableDataElements' );
        availableDataElements.selectedIndex = -1;
        
        var selectedDataElements = document.getElementById( 'selectedDataElements' );
        for ( var i = 0; i < selectedDataElements.options.length; ++i )
        {
            selectedDataElements.options[i].selected = true;
        }
        
        var availableIndicators = document.getElementById( 'availableIndicators' );
        availableIndicators.selectedIndex = -1;
        
        var selectedIndicators = document.getElementById( 'selectedIndicators' );
        for ( var i = 0; i < selectedIndicators.options.length; ++i )
        {
            selectedIndicators.options[i].selected = true;
        }
        
        var form = document.getElementById( 'addDataDictionaryForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_datadictionary_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

// -----------------------------------------------------------------------------
// Update DataDictionary
// -----------------------------------------------------------------------------

function validateUpdateDataDictionary()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    
    request.send( 'validateDataDictionary.action?id=' + getFieldValue( 'id' ) +
        '&name=' + getFieldValue( 'name' ) +
        '&description=' + getFieldValue( 'description' ) +
        '&region=' + getFieldValue( 'region' ) );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
    	var availableDataElements = document.getElementById( 'availableDataElements' );
        availableDataElements.selectedIndex = -1;
        
        var selectedDataElements = document.getElementById( 'selectedDataElements' );
        for ( var i = 0; i < selectedDataElements.options.length; ++i )
        {
            selectedDataElements.options[i].selected = true;
        }
        
        var availableIndicators = document.getElementById( 'availableIndicators' );
        availableIndicators.selectedIndex = -1;
        
        var selectedIndicators = document.getElementById( 'selectedIndicators' );
        for ( var i = 0; i < selectedIndicators.options.length; ++i )
        {
            selectedIndicators.options[i].selected = true;
        }
        
        var form = document.getElementById( 'updateDataDictionaryForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_updating_datadictionary_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
