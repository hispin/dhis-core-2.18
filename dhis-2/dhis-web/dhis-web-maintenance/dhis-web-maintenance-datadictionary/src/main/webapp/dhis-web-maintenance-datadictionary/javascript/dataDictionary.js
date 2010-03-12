
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

// -----------------------------------------------------------------------------
// Init lists
// -----------------------------------------------------------------------------

function initLists()
{
    var id;

    for ( id in selectedDataElements )
    {
        $("#selectedDataElements").append( $( "<option></option>" ).attr( "value",id ).text( selectedDataElements[id] )) ;
    }

    for ( id in availableDataElements )
    {
        $("#availableDataElements").append( $( "<option></option>" ).attr( "value",id ).text( availableDataElements[id] )) ;
    }

    for ( id in selectedIndicators )
    {
        $("#selectedIndicators").append( $( "<option></option>" ).attr( "value",id ).text( selectedIndicators[id] )) ;
    }

    for ( id in availableIndicators )
    {
        $("#availableIndicators").append( $( "<option></option>" ).attr( "value",id ).text( availableIndicators[id] )) ;
    }
}

// -----------------------------------------------------------------------------
// DataElement filters
// -----------------------------------------------------------------------------

function filterSelectedDataElements()
{
    var filter = document.getElementById( 'selectedDataElementsFilter' ).value;
    
    var list = document.getElementById( 'selectedDataElements' );
    
    list.options.length = 0;
    
    for ( var id in selectedDataElements )
    {
        var value = selectedDataElements[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterAvailableDataElements()
{
    var filter = document.getElementById( 'availableDataElementsFilter' ).value;
    
    var list = document.getElementById( 'availableDataElements' );
    
    list.options.length = 0;
    
    for ( var id in availableDataElements )
    {
        var value = availableDataElements[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

// -----------------------------------------------------------------------------
// Indicator filters
// -----------------------------------------------------------------------------

function filterSelectedIndicators()
{
    var filter = document.getElementById( 'selectedIndicatorsFilter' ).value;
    
    var list = document.getElementById( 'selectedIndicators' );
    
    list.options.length = 0;
    
    for ( var id in selectedIndicators )
    {
        var value = selectedIndicators[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
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
            list.add( new Option( value, id ), null );
        }
    }
}

// -----------------------------------------------------------------------------
// DataElement lists
// -----------------------------------------------------------------------------

function addDataElements()
{
    var list = document.getElementById( 'availableDataElements' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        selectedDataElements[id] = availableDataElements[id];
        
        delete availableDataElements[id];        
    }
    
    filterSelectedDataElements();
    filterAvailableDataElements();
}

function removeDataElements()
{
    var list = document.getElementById( 'selectedDataElements' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableDataElements[id] = selectedDataElements[id];
        
        delete selectedDataElements[id];        
    }
    
    filterSelectedDataElements();
    filterAvailableDataElements();
}

// -----------------------------------------------------------------------------
// Indicator lists
// -----------------------------------------------------------------------------

function addIndicators()
{
    var list = document.getElementById( 'availableIndicators' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        selectedIndicators[id] = availableIndicators[id];
        
        delete availableIndicators[id];        
    }
    
    filterSelectedIndicators();
    filterAvailableIndicators();
}

function removeIndicators()
{
    var list = document.getElementById( 'selectedIndicators' );

    while ( list.selectedIndex != -1 )
    {
        var id = list.options[list.selectedIndex].value;

        list.options[list.selectedIndex].selected = false;

        availableIndicators[id] = selectedIndicators[id];
        
        delete selectedIndicators[id];        
    }
    
    filterSelectedIndicators();
    filterAvailableIndicators();
}
