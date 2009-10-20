
function showDataElementCategoryComboDetails( categoryComboId )
{
    var request = new Request();
    request.setResponseTypeXML( 'dataElementCategoryCombo' );
    request.setCallbackSuccess( dataElementCategoryComboReceived );
    request.send( 'getDataElementCategoryCombo.action?id=' + categoryComboId );
}

function dataElementCategoryComboReceived( dataElementCategoryComboElement )
{
    setFieldValue( 'nameField', getElementValue( dataElementCategoryComboElement, 'name' ) );
	setFieldValue( 'dataElementCategoryCountField', getElementValue( dataElementCategoryComboElement, 'dataElementCategoryCount' ) );
          
    showDetails();
}

// -----------------------------------------------------------------------------
// Delete Category
// -----------------------------------------------------------------------------

function removeDataElementCategoryCombo( categoryComboId, categoryComboName )
{
    var result = window.confirm( i18n_confirm_delete + '\n\n' + categoryComboName );

    if ( result )
    {
        window.location.href = 'removeDataElementCategoryCombo.action?id=' + categoryComboId;
    }
}

// ----------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------

function validateAddDataElementCategoryCombo()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( addDataElementCategoryComboValidationCompleted );

    var requestString = 'validateDataElementCategoryCombo.action';  

    var selectedList = document.getElementById( 'selectedList' );
  
    var params = 'name=' + htmlEncode( document.getElementById( 'name' ).value );

    for ( var i = 0; i < selectedList.options.length; ++i)
    {  	
        params += '&selectedCategories=' + selectedList.options[i].value;
    }
  
    request.sendAsPost( params );    
    request.send( requestString );
  
    return false;
}

function addDataElementCategoryComboValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;

    if ( type == 'success' )
    {           
        document.getElementById( 'addDataElementCategoryComboForm' ).submit();
    }  
    else if ( type == 'input' )
    {
  	    setMessage( message );
    }
}

function validateEditDataElementCategoryCombo()
{
    var request = new Request();
    request.setResponseTypeXML( 'message' );
    request.setCallbackSuccess( editDataElementCategoryComboValidationCompleted );
  
    var requestString = 'validateDataElementCategoryCombo.action';
    
    var selectedList = document.getElementById( 'selectedList' );
  
    var params = 'id=' + document.getElementById( 'id' ).value;
  	params += '&name=' + htmlEncode( document.getElementById( 'name' ).value );

    for ( var i = 0; i < selectedList.options.length; ++i)
    {   	
        params += '&selectedCategories=' + selectedList.options[i].value;
    }
  
    request.sendAsPost( params );    
    request.send( requestString );
      
    return false;
}

function editDataElementCategoryComboValidationCompleted( messageElement )
{
    var type = messageElement.getAttribute( 'type' );
    var message = messageElement.firstChild.nodeValue;

    if ( type == 'success' )
    {
        document.getElementById( 'editDataElementCategoryComboForm' ).submit();
    }
    else if ( type == 'input' )
    {
    	setMessage( message );
    }
}

