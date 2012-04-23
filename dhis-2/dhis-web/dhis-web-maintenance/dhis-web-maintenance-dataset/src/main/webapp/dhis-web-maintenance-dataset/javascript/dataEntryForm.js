
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
	$.postUTF8( 'validateDataEntryForm.action',
	{
		name: $( '#nameField' ).val(),
		dataSetId: $( '#dataSetIdField' ).val(),
		dataEntryFormId: dataEntryFormId
	}, 
	function( json ) 
	{
		if ( autoSave == false )
		{
			dataEntryFormValidationCompleted( json );
		}
		else
		{
			autoSaveDataEntryFormValidationCompleted( json );
		}
	} );
}

function dataEntryFormValidationCompleted( json )
{
	if ( json.response == 'success' )
	{  
		$( '#saveDataEntryForm' ).submit();
	}
	else if ( json.response = 'input' )
	{
		setHeaderDelayMessage( json.message );
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

// -----------------------------------------------------------------------------
// Auto-save DataEntryForm
// -----------------------------------------------------------------------------

function autoSaveDataEntryFormValidationCompleted( json )
{
	if ( json.response == 'success' )
	{
		autoSaveDataEntryForm();
	}
	else if ( json.response = 'input' )
	{
		setHeaderDelayMessage( json.message );
	}
}

function autoSaveDataEntryForm() 
{
	var field = $("#designTextarea").ckeditorGet();
	var designTextarea = field.getData();
	
	$.postUTF8( 'autoSaveDataEntryForm.action',
	{
		nameField: getFieldValue('nameField'),
		designTextarea: designTextarea,
		dataSetIdField: getFieldValue('dataSetIdField'),
		dataEntryFormId: dataEntryFormId
	},
	function( xmlObject ) 
	{
		setHeaderDelayMessage( i18n_save_success ); 
		stat = "EDIT";
		dataEntryFormId = xmlObject.getElementsByTagName( 'message' )[0].firstChild.nodeValue;
		enable('delete');
	} );
}