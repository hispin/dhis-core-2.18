
// -----------------------------------------------------------------------------
// DataSet details form
// -----------------------------------------------------------------------------

function showDataSetDetails( dataSetId )
{
  var request = new Request();
  request.setResponseTypeXML( 'dataSet' );
  request.setCallbackSuccess( dataSetRecieved );
  request.send( 'getDataSet.action?dataSetId=' + dataSetId );
}

function dataSetRecieved( dataSetElement )
{
  setFieldValue( 'idField', getElementValue( dataSetElement, 'id' ) );
  setFieldValue( 'nameField', getElementValue( dataSetElement, 'name' ) );

  setFieldValue( 'frequencyField', getElementValue( dataSetElement, 'frequency' ) );

  setFieldValue( 'dataElementCountField', getElementValue( dataSetElement, 'dataElementCount' ) );

  setFieldValue( 'dataEntryFormField', getElementValue( dataSetElement, 'dataentryform' ) );

  showDetails();
}

// -----------------------------------------------------------------------------
// Delete DataSet
// -----------------------------------------------------------------------------

var tmpDataSetId;

var tmpSource;

function removeDataSet( dataSetId, dataSetName )
{
  removeItem( dataSetId, dataSetName, i18n_confirm_delete, 'delDataSet.action' );
}

// ----------------------------------------------------------------------
// DataEntryForm
// ----------------------------------------------------------------------

function viewDataEntryForm( dataSetId )
{
  window.location.href = 'viewDataEntryForm.action?dataSetId=' + dataSetId;
}

// ----------------------------------------------------------------------
// Filter by DataElementGroup
// ----------------------------------------------------------------------

function filterByDataElementGroup( selectedDataElementGroup )
{
  var request = new Request();

  var requestString = 'filterAvailableDataElementsByDataElementGroup.action';
  
  var params = 'dataElementGroupId=' + selectedDataElementGroup;

  var selectedList = document.getElementById( 'selectedList' );

  for ( var i = 0; i < selectedList.options.length; ++i)
  {
  	params += '&selectedDataElements=' + selectedList.options[i].value;
  }

  // Clear the list
  var availableList = document.getElementById( 'availableList' );

  availableList.options.length = 0;

  request.setResponseTypeXML( 'dataElementGroup' );
  request.setCallbackSuccess( filterByDataElementGroupCompleted );
  request.sendAsPost( params );
  request.send( requestString );
}

function filterByDataElementGroupCompleted( dataElementGroup )
{
  var dataElements = dataElementGroup.getElementsByTagName( 'dataElements' )[0];
  var dataElementList = dataElements.getElementsByTagName( 'dataElement' );

  var availableList = document.getElementById( 'availableList' );
  
  for ( var i = 0; i < dataElementList.length; i++ )
  {
    var dataElement = dataElementList[i];
    var name = dataElement.firstChild.nodeValue;
    var id = dataElement.getAttribute( 'id' );

    availableList.add( new Option( name, id ), null );
  }
}

// ----------------------------------------------------------------------
// Validation
// ----------------------------------------------------------------------

function validateAddDataSet()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( addDataSetValidationCompleted ); 
  
  var requestString = 'validateDataSet.action?name=' + getFieldValue( 'name' ) +
                      '&shortName=' + getFieldValue( 'shortName' ) +
                      '&code=' + getFieldValue( 'code' );

  request.send( requestString );

  return false;
}

function addDataSetValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      // Both edit and add form has id='dataSetForm'      
	  selectAllById('selectedList');
      document.forms['addDataSetForm'].submit();
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

function validateEditDataSet()
{
  var request = new Request();
  request.setResponseTypeXML( 'message' );
  request.setCallbackSuccess( editDataSetValidationCompleted );

  var requestString = 'validateDataSet.action?name=' + getFieldValue( 'name' ) +
                      '&shortName=' + getFieldValue( 'shortName' ) +
                      '&code=' + getFieldValue( 'code' ) +
  		              '&dataSetId=' + getFieldValue( 'dataSetId' );

  request.send( requestString );

  return false;
}

function editDataSetValidationCompleted( messageElement )
{
  var type = messageElement.getAttribute( 'type' );
  var message = messageElement.firstChild.nodeValue;

  if ( type == 'success' )
  {
      // Both edit and add form has id='dataSetForm'
	  selectAllById('selectedList');
      document.forms['editDataSetForm'].submit();
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

// ----------------------------------------------------------------------
// List
// ----------------------------------------------------------------------

function initLists()
{
    var id;
	
	var list = document.getElementById( 'selectedList' );
	
    for ( id in dataSetMembers )
    {
        list.add( new Option( dataSetMembers[id], id ), null );
    }	
	
    list = document.getElementById( 'availableList' );
    
    for ( id in availableDataElements )
    {
        list.add( new Option( availableDataElements[id], id ), null );
    }
}

function filterDataSetMembers()
{
	var filter = document.getElementById( 'dataSetMembersFilter' ).value;
    var list = document.getElementById( 'selectedList' );
    
    list.options.length = 0;
    
    for ( var id in dataSetMembers )
    {
        var value = dataSetMembers[id];
        
        if ( value.toLowerCase().indexOf( filter.toLowerCase() ) != -1 )
        {
            list.add( new Option( value, id ), null );
        }
    }
}

function filterAvailableDataElements()
{
	var filter = document.getElementById( 'availableDataElementsFilter' ).value;
    var list = document.getElementById( 'availableList' );
    
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

function addDataSetMembers()
{
	var list = document.getElementById( 'availableList' );

    while ( list.selectedIndex != -1 )
    {
		var selectedList = byId( 'selectedList' );
        var id = list.options[list.selectedIndex].value;
		
		dataSetMembers[id] = availableDataElements[id];
		
		addOptionToList( selectedList, id, dataSetMembers[id] );
		
        list.remove( list.selectedIndex );
        
        delete availableDataElements[id];        
    }
}

function removeDataSetMembers()
{
	var list = document.getElementById( 'selectedList' );

    while ( list.selectedIndex != -1 )
    {
		var availableList = byId( 'availableList' );
        var id = list.options[list.selectedIndex].value;

        availableDataElements[id] = dataSetMembers[id];
		
		addOptionToList( availableList, id, availableDataElements[id] );
		
		list.remove( list.selectedIndex );
        
        delete dataSetMembers[id];        
    }
}
