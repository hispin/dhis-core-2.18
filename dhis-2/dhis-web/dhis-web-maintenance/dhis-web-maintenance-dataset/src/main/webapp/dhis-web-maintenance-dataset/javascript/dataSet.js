
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
