
// -----------------------------------------------------------------------------
// View details
// -----------------------------------------------------------------------------

function showDataElementCategoryDetails( categoryId )
{	
    var request = new Request();
    request.setResponseTypeXML( 'dataElementCategory' );
    request.setCallbackSuccess( dataElementCategoryReceived );
    request.send( 'getDataElementCategory.action?id=' + categoryId );
}

function dataElementCategoryReceived( categoryElement )
{
    setFieldValue( 'nameField', getElementValue( categoryElement, 'name' ) );    
    setFieldValue( 'categoryOptionsCountField', getElementValue( categoryElement, 'categoryOptionCount' ) );
          
    showDetails();
}

// -----------------------------------------------------------------------------
// Delete Category
// -----------------------------------------------------------------------------

function removeDataElementCategory( categoryId, categoryName )
{
  var result = window.confirm( i18n_confirm_delete + '\n\n' + categoryName );

  if ( result )
  {
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( removeDataElementCategoryCompleted );
    request.send( 'removeDataElementCategory.action?id=' + categoryId );
  }
}

function removeDataElementCategoryCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;
    
    if ( type == 'success' )
    {
        window.location.href = 'category.action';
    }
    else if ( type = 'error' )
    {
        setFieldValue( 'warningField', message );
        
        showWarning();
    }
}

function addCategoryOptionToCategory()
{
	var categoryName = document.getElementById( 'categoryOptionName' ).value;
	
	if ( categoryName == "" )
	{
		setMessage( i18n_specify_category_option_name );
	}
	else if ( listContainsById( 'categoryOptionNames', categoryName ) )
	{
		setMessage( i18n_category_option_name_already_exists );
	}
	else
	{
	   addOption( 'categoryOptionNames', categoryName, categoryName );
	
	   document.getElementById( 'categoryOptionName' ).value = "";
	}
}

// ----------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------

function validateAddDataElementCategory()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addDataElementCategoryValidationCompleted );

    if ( document.getElementById( 'categoryOptionNames' ).options.length == 0 )
    {
        setMessage( i18n_must_include_category_option );
        return;
    }

    var requestString = 'validateDataElementCategory.action?name=' + htmlEncode( document.getElementById( 'name' ).value );
  
    requestString += "&" + getParamString( 'categoryOptionNames' );

    request.send( requestString );
  
    return false;
}

function addDataElementCategoryValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;

    if ( type == 'success' )
    {
  	    selectAllById( 'categoryOptionNames' );
        document.getElementById( 'addDataElementCategoryForm' ).submit();
    }  
    else if ( type == 'input' )
    {
  	    setMessage( message );
    }
}

function validateEditDataElementCategory()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( editDataElementCategoryValidationCompleted );
  
    var requestString = 'validateDataElementCategory.action?id=' + document.getElementById( 'id' ).value + 
        '&name=' + htmlEncode( document.getElementById( 'name' ).value );

    requestString += "&" + getParamString( 'categoryOptions' );
  
    request.send( requestString );
    
    return false;
}

function editDataElementCategoryValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;

    if ( type == 'success' )
    {
        selectAllById( 'categoryOptions' );
        document.getElementById( 'editDataElementCategoryForm' ).submit();
    }
    else if ( type == 'input' )
    {
        setMessage( message );
    }
}
