
// -----------------------------------------------------------------------------
// Delete DataEntryForm
// -----------------------------------------------------------------------------

function removeDataEntryForm( dataEntryFormId, dataEntryFormName )
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( removeDataEntryFormCompleted );

  var requestString = 'delDataEntryForm.action?dataEntryFormId=' + dataEntryFormId;
  var result = window.confirm( i18n_confirm_delete + '\n\n' + dataEntryFormName );

  if ( result )
  {
    request.send( requestString );
  }
  
  return false;
}

function removeDataEntryFormCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
  else
  {
  	window.location.href = 'index.action';
  }
}


// ----------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------

function validateDataEntryForm()
{
  if( jQuery("#comboSaveMethod").val() == "design" )
  {
	  var request = new Request();
	  request.setResponseTypeXML( 'message' );
	  request.setCallbackSuccess( dataEntryFormValidationCompleted );
	
	  var requestString = 'validateDataEntryForm.action';
	  
	  var params = 'name=' + document.getElementById( 'nameField' ).value;
	  if(stat == "ADD")
	  {
	  	 
	  }	
	  else
	  {
	    params += '&dataEntryFormId=' + dataEntryFormId;      
	  }        
	
	  params += '&programStageId=' + document.getElementById( 'associationIdField' ).value;
	    
	  var htmlCode = document.getElementById( 'designTextarea' ).value;
	  params += '&designCode=' + htmlCode;
	  
	  request.sendAsPost( params );
	  request.send( requestString );
	  return false;
  }else {
	  return true;
  }
  
 
}

function dataEntryFormValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {  
      document.forms['saveDataEntryForm'].submit();
  }
  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
  else if ( type == 'mismatch' )
  {
    var result = window.confirm( message );

    if ( result )
    {
      document.forms['saveDataEntryForm'].submit();
    }
  }
}

// -----------------------------------------------------------------------------
// Find Selected DataElement Count in the DataEntryForm
// -----------------------------------------------------------------------------

function findDataElementCount()
{
  var request = new Request();
  request.setResponseTypeXML( 'programStage' );
  request.setCallbackSuccess( findDataElementCountCompleted );

  // Clear the list
  var dataElementList = document.getElementById( 'dataElementSelector' );
  dataElementList.options.length = 0;

  var requestString = 'getSelectedDataElements.action';
  
  var params = 'programStageId=' + document.getElementById( 'programStageIdField' ).value;
        
  params += '&designCode=' + htmlCode;
  
  request.sendAsPost( params );
  request.send( requestString );distinct

  return false;
}

function findDataElementCountCompleted( dataSetElement )
{
  var dataElements = dataSetElement.getElementsByTagName( 'dataElements' )[0];
  var dataElementList = dataElements.getElementsByTagName( 'dataElement' );

  var dataElementSelector = document.getElementById( 'dataElementSelector' );
  
  for ( var i = 0; i < dataElementList.length; i++ )
  {
    var dataElement = dataElementList[i];
    var name = dataElement.firstChild.nodeValue;
    var id = dataElement.getAttribute( 'id' );	
		
	var option = new Option( name, id );
	    
    dataElementSelector.add( option, null );
  }
}

function onloadFunction()
{ 	
	htmlCode = FCK.GetXHTML( FCKConfig.FormatSource );
    findDataElementCount();
} 

function showDataEntryCode(this_)
{
	jQuery("#codeContainer").load("showDataEntryForm.action?dataEntryFormId="+jQuery(this_).val(),
			function(){
				jQuery("#codeContainer input").each( 
						function(){
							jQuery(this).attr({'disabled':'disabled'});
						}
				);
			}
	);
}

function toggleDiv()
{
	jQuery(".container").toggle();
}

function submitForm(form)
{
	closeDialog();
	jQuery("#saveDataEntryForm").submit();
}
function closeDialog()
{
//	oFCKeditor.FCKSelectProgramStageElement
}

