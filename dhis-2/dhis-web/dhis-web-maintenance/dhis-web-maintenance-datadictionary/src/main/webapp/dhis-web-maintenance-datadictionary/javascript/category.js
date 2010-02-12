
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
	removeItem( categoryId, categoryName, i18n_confirm_delete, 'removeDataElementCategory.action' );
}

function addCategoryOptionToCategory( categoryName )
{
	if ( listContainsById( 'categoryOptionNames', categoryName ) )
	{
		setMessage( i18n_category_option_name_already_exists );
	}
	else
	{
		hideById( "message" );
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

    requestString += "&conceptName=" + htmlEncode( document.getElementById( 'conceptName' ).value );

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

    requestString += "&conceptName=" + htmlEncode( document.getElementById( 'conceptName' ).value );

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

function validateAddCategoryOption() {

	var categoryName = $( "#categoryOptionName" ).val();
	
	$.post( "validateDataElementCategoryOption.action", 
	{
		name:categoryName
	},
	function ( xmlObject ) 
	{
		xmlObject = xmlObject.getElementsByTagName( "message" )[0];
		var type = xmlObject.getAttribute( "type" );
		
		if ( type == "input" ) 
		{
			setMessage( xmlObject.firstChild.nodeValue );
		}
		else 
		{
			addCategoryOptionToCategory( categoryName );
		}
	}, "xml");
}

