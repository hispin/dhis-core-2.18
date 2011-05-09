// -----------------------------------------------------------------------------
// DataSet details form
// -----------------------------------------------------------------------------

function showDataSetDetails( dataSetId )
{
  var request = new Request();
  request.setResponseTypeXML( 'dataSet' );
  request.setCallbackSuccess( dataSetRecieved );
  request.send( '../dhis-web-commons-ajax/getDataSet.action?id=' + dataSetId );
}

function dataSetRecieved( dataSetElement )
{
  setInnerHTML( 'nameField', getElementValue( dataSetElement, 'name' ) );
  setInnerHTML( 'frequencyField', getElementValue( dataSetElement, 'frequency' ) );
  setInnerHTML( 'dataElementCountField', getElementValue( dataSetElement, 'dataElementCount' ) );
  setInnerHTML( 'dataEntryFormField', getElementValue( dataSetElement, 'dataentryform' ) );

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
// Filter by DataElementGroup and PeriodType
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

function filterByIndicatorGroup( selectedIndicatorGroup )
{
  var request = new Request();
  
  var requestString = 'filterAvailableIndicatorsByIndicatorGroup.action';

  var params = 'indicatorGroupId=' + selectedIndicatorGroup;

  var selectedList = document.getElementById( 'indicatorSelectedList' );

  for ( var i = 0; i < selectedList.options.length; ++i)
  {
  	params += '&selectedIndicators=' + selectedList.options[i].value;
  }

  // Clear the list
  var availableList = document.getElementById( 'indicatorAvailableList' );

  availableList.options.length = 0;

  request.setResponseTypeXML( 'indicatorGroup' );
  request.setCallbackSuccess( filterByIndicatorGroupCompleted );
  request.sendAsPost( params );
  request.send( requestString );
}

function filterByIndicatorGroupCompleted( indicatorGroup )
{
  var indicators = indicatorGroup.getElementsByTagName( 'indicators' )[0];
  var indicatorList = indicators.getElementsByTagName( 'indicator' );

  alert(indicatorList.length);
  
  var availableList = document.getElementById( 'indicatorAvailableList' );

  for ( var i = 0; i < indicatorList.length; i++ )
  {
    var indicator = indicatorList[i];
    var name = indicator.firstChild.nodeValue;
    var id = indicator.getAttribute( 'id' );

    availableList.add( new Option( name, id ), null );
  }
}
