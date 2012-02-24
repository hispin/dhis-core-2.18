
// -----------------------------------------------------------------------------
// Delete DataEntryForm
// -----------------------------------------------------------------------------

function removeDataEntryForm( dataSetIdField, dataEntryFormId, dataEntryFormName )
{
  var result = window.confirm( i18n_confirm_delete + '\n\n' + dataEntryFormName );

  if ( result )
  {
	window.location.href = 'delDataEntryForm.action?dataSetId=' + dataSetIdField + "&dataEntryFormId=" + dataEntryFormId;
  }
}

// ----------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------

function validateDataEntryForm()
{  
  var params  = 'name=' + document.getElementById( 'nameField' ).value;
	  params += '&dataSetId=' + document.getElementById( 'dataSetIdField' ).value;
  if(stat == "EDIT")
  {
    params += '&dataEntryFormId=' + dataEntryFormId;      
  }        

  if(autoSave == false)
  {
	$.ajax({
	   type: "POST",
	   url: "validateDataEntryForm.action",
	   data: params,
	   dataType: "xml",
	   success: dataEntryFormValidationCompleted
	});
  }
  else{
	$.ajax({
	   type: "POST",
	   url: "validateDataEntryForm.action",
	   data: params,
	   dataType: "xml",
	   success: autoSaveDataEntryFormValidationCompleted
	});
  }
}

function dataEntryFormValidationCompleted( messageElement )
{
  messageElement = messageElement.getElementsByTagName( 'message' )[0];
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
  clearListById('dataElementSelector');
  
   jQuery.postUTF8( 'getSelectedDataElements.action',
	{
		dataSetId:document.getElementById( 'dataSetIdField' ).value,
		designCode:htmlCode
	},findDataElementCountCompleted );
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


// TODO: remove this? does not seem to be used anywhere, updating to ckeditor just in case
function onloadFunction()
{
  htmlCode = $("#designTextarea").ckeditorGet().getData();
  findDataElementCount();
} 
// -----------------------------------------------------------------------------
// Auto-save DataEntryForm
// -----------------------------------------------------------------------------

function autoSaveDataEntryFormValidationCompleted( messageElement )
{
  messageElement = messageElement.getElementsByTagName( 'message' )[0];
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
     autoSaveDataEntryForm();
  }
  else if ( type == 'input' )
  {
	 setMessage( message );
  }
  else if ( type == 'mismatch' )
  {
    var result = window.confirm( message );

    if ( result )
    {
      autoSaveDataEntryForm();
    }
  }
}

function autoSaveDataEntryForm() {
	var field = $("#designTextarea").ckeditorGet();
	var designTextarea = htmlEncode(field.getData());
	var params = 'nameField=' + getFieldValue('nameField');
		params += '&designTextarea=' + designTextarea;
		params += '&dataSetIdField=' + getFieldValue('dataSetIdField');
		
	if(byId('dataEntryFormId') != null){
		params += '&dataEntryFormId=' + getFieldValue('dataEntryFormId');
	}
	
	$.ajax({
		   type: "POST",
		   url: "autoSaveDataEntryForm.action",
		   data: params,
		   dataType: "xml",
		   success: function(xmlObject){
				setMessage(i18n_save_success); 
				stat = "EDIT";
				dataEntryFormId = xmlObject.getElementsByTagName( 'message' )[0].firstChild.nodeValue;
				enable('delete');
		   }
		});
}