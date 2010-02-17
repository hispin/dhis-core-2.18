
function sortOrderSubmit()
{
    var datasetId = document.getElementById('dataSetId').value;
    
    if ( datasetId=="null" )
    {
        window.alert( "Please select dataset" );
    }
    else
    {
        window.location = "sortOrderSection.action?dataSetId=" + datasetId;
    }    
}

function getSectionByDataSet(dataSetId)
{
    window.location = "section.action?dataSetId=" + dataSetId;
}

function removeSection( sectionId, sectionName )
{
	removeItem( sectionId, sectionName, i18n_confirm_delete, "removeSection.action" );
}

function addSectionSubmit()
{
    var dataset = document.getElementById('dataSetId').value;
    
    if( dataset=="null" )
    {
        window.alert( "Please select dataset" );
    }
    else
    {
        window.location.href="addSectionAction.action?dataSetId=" + document.getElementById('dataSetId').value;
    }
}

function getDataElementByDataSet( dataSetId )
{
  var request = new Request();

  var requestString = 'filterAvailableDataElementsByDataSet.action';
  
  var params = 'dataSetId=' + dataSetId;

  var selectedList = document.getElementById( 'selectedList' );

  for ( var i = 0; i < selectedList.options.length; ++i)
  {
	params += '&selectedDataElements=' + selectedList.options[i].value;
	// process list.options[i].value / list.options[i].text
  }
  
  var availableList = document.getElementById( 'availableList' );
  availableList.options.length = 0;

  request.setResponseTypeXML( 'dataElementGroup' );
  request.setCallbackSuccess( filterByDataElementGroupCompleted );
  request.sendAsPost( params );
  request.send( requestString );
}

function addSectionValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      // Both edit and add form has id='dataSetForm'      
      document.forms['addSectionForm'].submit();
  }
  /**
  else if ( type == 'error' )
  {
      window.alert( 'Adding the organisation unit failed with the following message:\n' + message );
  }
  */
  else if ( type == 'input' )
  {
    document.getElementById( 'message' ).innerHTML = message;
    document.getElementById( 'message' ).style.display = 'block';
  }
}

function validateAddSection()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( addSectionValidationCompleted );

  var requestString = 'validateSection.action?name=' + 
    getFieldValue( 'sectionName' ) + '&label=' + getFieldValue( 'sectionLabel' );
  
  request.send( requestString );

  return false;
}
