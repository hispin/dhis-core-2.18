
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showDataElementCategoryOptionDetails( dataElementCategoryOptionId )
{
    var request = new Request();
    request.setResponseTypeXML( 'dataElementCategoryOption' );
    request.setCallbackSuccess( dataElementCategoryOptionReceived );
    request.send( 'getDataElementCategoryOption.action?dataElementCategoryOptionId=' + dataElementCategoryOptionId );
}

function dataElementCategoryOptionReceived( dataElementCategoryOptionElement )
{
    setFieldValue( 'idField', getElementValue( dataElementCategoryOptionElement, 'id' ) );
    setFieldValue( 'nameField', getElementValue( dataElementCategoryOptionElement, 'name' ) );
    setFieldValue( 'shortNameField', getElementValue( dataElementCategoryOptionElement, 'shortName' ) );
            
    showDetails();
}

function getDataElementCategoryOptions( dataElementCategoryOptionGroupId, type )
{	
    var url = "getDataElementCategoryOptions.action?";

    if ( dataElementCategoryOptionGroupId == '[select]' )
    {
    	return;
    }

	if ( dataElementCategoryOptionGroupId != null )
	{
		url += "dataElementCategoryOptionGroupId=" + dataElementCategoryOptionGroupId;				
	}
	
    var request = new Request();
    request.setResponseTypeXML( 'dataElementCategoryOption' );
    request.setCallbackSuccess( getDataElementCategoryOptionsReceived );
    request.send( url );	
}

function getDataElementCategoryOptionsReceived( xmlObject )
{	
	var availableDataElementCategoryOptions = document.getElementById( "availableDataElementCategoryOptions" );
	
	clearList( availableDataElementCategoryOptions );
	
	var dataElementCategoryOptions = xmlObject.getElementsByTagName( "dataElementCategoryOption" );
	
	for ( var i = 0; i < dataElementCategoryOptions.length; i++ )
	{
		var id = dataElementCategoryOptions[ i ].getElementsByTagName( "id" )[0].firstChild.nodeValue;
		var dataElementCategoryOptionName = dataElementCategoryOptions[ i ].getElementsByTagName( "name" )[0].firstChild.nodeValue;
		
		var option = document.createElement( "option" );
		option.value = id;
		option.text = dataElementCategoryOptionName;
		option.title = dataElementCategoryOptionName;
		availableDataElementCategoryOptions.add( option, null );
	}
}

// -----------------------------------------------------------------------------
// Remove data element
// -----------------------------------------------------------------------------

function removeDataElementCategoryOption( dataElementCategoryOptionId, dataElementCategoryOptionName )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + dataElementCategoryOptionName );
    
    if ( result )
    {
       	var request = new Request();
        request.setResponseTypeXML( 'message' );
        request.setCallbackSuccess( removeDataElementCategoryOptionCompleted );
        request.send( 'removeDataElementCategoryOption.action?id=' + dataElementCategoryOptionId );
    }    
}

function removeDataElementCategoryOptionCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'categoryOption.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

// -----------------------------------------------------------------------------
// Add data element
// -----------------------------------------------------------------------------

function validateAddDataElementCategoryOption()
{
    var url = 'validateDataElementCategoryOption.action?' + 
        '&name=' + htmlEncode( getFieldValue( 'name' ) ) +
        '&shortName=' + htmlEncode( getFieldValue( 'shortName' ) );
    
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addValidationCompleted );    
    request.send( url );

    return false;
}

/**
 * Make a CGI parameter string for the given field name and set of values
 * 
 * @param fieldName name of the field to make a string for
 * @param values array of values to add to the string
 * @returns String on the form '&fieldName=value1...$fieldName=valueN'
 */
function makeValueString( fieldName, values )
{
	var valueStr = "";
	for ( var i = 0, value; value = values[i]; i++ )
	{
		valueStr += "&" + fieldName + "=" + value;
	}
	
	return valueStr;
}

function addValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'addDataElementCategoryOptionForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_adding_data_element_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}

/**
 * Returns the first value of the specified select box
 * 
 * @param selectId
 * @return The first (or only) String value of the given select box, 
 * 		or null if no options are selected
 */
function getSelectValue( selectId )
{
	var select = document.getElementById( selectId );
	var option = select.options[select.selectedIndex];
	
	if ( option )
	{
		return option.value;
	}
	else
	{
		return null;
	}
}

/**
 * Returns the values for the specified select box
 * 
 * @param id id of the select box to get values for
 * @return Array of String values from the given select box,
 * 		or an empty array if no options are selected
 */
function getSelectValues( selectId )
{
	var select = document.getElementById( selectId );
	var values = [];
	for ( var i = 0, option; option = select.options[i]; i++ )
	{
		if ( option.selected )
		{
			values.push(option.value);
		}
	}
	
	return values;
}

/**
 * Returns the value for the specified checkbox
 * 
 * @param checkboxId id of the checkbox to get a value for
 * @return String value for the specified checkbox,
 * 		or null if the checkbox is not checked
 */
function getCheckboxValue( checkboxId )
{
	var checkbox = document.getElementById( checkboxId );
	
	return ( checkbox.checked ? checkbox.value : null );
}

/**
 * Returns the values for a set of inputs with the same name,
 * under a specified parent node.
 * 
 * @param parentId id of the parent node to limit the search to
 * @param fieldName form name of the inputs to get values for
 * @return Array with the String values for the specified inputs,
 * 		or an empty Array if no inputs with that name exist under the specified parent node
 */
function getInputValuesByParentId( parentId, fieldName )
{
	var node = document.getElementById(parentId);
	
	if ( ! node )
	{
		return [];
	}
	
	var inputs = node.getElementsByTagName("input");
	values = [];
	
	for ( var i = 0, input; input = inputs[i]; i++ )
	{
		if ( input.name == fieldName )
		{
			values.push(input.value);
		}
	}
	
	return values;	
}

// -----------------------------------------------------------------------------
// Update data element
// -----------------------------------------------------------------------------

function validateUpdateDataElementCategoryOption()
{
    var url = 'validateDataElementCategoryOption.action?' +
        '&id=' + getFieldValue( 'id' ) +
        '&name=' + htmlEncode( getFieldValue( 'name' ) ) +
        '&shortName=' + htmlEncode( getFieldValue( 'shortName' ) );
        
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( updateValidationCompleted );
    request.send( url );

    return false;
}

function updateValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        var form = document.getElementById( 'updateDataElementCategoryOptionForm' );
        form.submit();
    }
    else if ( type == 'error' )
    {
        window.alert( i18n_saving_data_element_failed + ':' + '\n' + message );
    }
    else if ( type == 'input' )
    {
        document.getElementById( 'message' ).innerHTML = message;
        document.getElementById( 'message' ).style.display = 'block';
    }
}
