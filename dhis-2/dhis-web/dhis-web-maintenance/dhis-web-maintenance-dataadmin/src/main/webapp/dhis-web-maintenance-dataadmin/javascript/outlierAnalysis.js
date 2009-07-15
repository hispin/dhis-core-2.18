/**
 * @author Jon Moen Drange
 *
 * Javascript file for outlier analysis web UI. Includes validation
 * of forms and some other functions to fetch choices for the form.
 *
 */

/******************************************************************
 * Validates the form before posting.
 *
 * (Saves the user for extra waiting time and the
 *  server for extra processing.)
 *
 ******************************************************************/
 
function validate()
{
	// validating date (copied from dataBrowser.js)
	
	var fromDate = document.getElementById( "fromDate" ).value.split('-');
    var toDate = document.getElementById( "toDate" ).value.split('-');
    
    if ( fromDate[0] != "" && fromDate.length != 3 )
    {
        setMessage( "Please enter valid from date" );
        return false;
    }
    
    if ( toDate[0] != "" && toDate.length != 3 )
    {
        setMessage( "Please enter valid to date" );
        return false;
    }
    
    if ( fromDate[0] != "" && toDate[0] != "" )
    {
		if ( fromDate[0] > toDate[0] )
		{
			setMessage( "From date is later than to date" );
			return false;
		}

		if ( fromDate[0] == toDate[0] && fromDate[1] > toDate[1] )
		{
			setMessage( "From date is later than to date" );
			return false;
		}

		if ( fromDate[0] == toDate[0] && fromDate[1] == toDate[1] && fromDate[2] > toDate[2] ) 
		{
			setMessage( "From date is later than to date" );
			return false;
		}
	}

	// validating data set / data elements selection

	var dataElements = document.getElementById( "dataElementsById" );
	
	if ( dataElements.options.length == 0 )
	{
		setMessage( "No data elements are selected." );
		return false;
	}
	
	// selected outlier type
	
	var outlierType = document.getElementById( "outlierType" );
	
	if ( outlierType.value == "null" )
	{
		setMessage( "No outlier type is selected." );
		return false;
	}
	
	// validating given standard deviation
	
	if ( outlierType.value == "stddev" )
	{
		var stddev = document.getElementById( "standardDeviation").value;
		
		if ( isNaN(stddev) )
		{
			setMessage( "Given standard deviation is not a number." );
			return false;
		}
		else if ( stddev < 0.1 || stddev > 10.0 )
		{
			setMessage( "The standard deviation should be a number between 0.1 and 10.0." );
			return false;
		}
	}
	
	// client side validation OK
	
	selectAllById( "dataElementsById" ); // setting data elements to "selected" before posting
	
	return true;
}


/******************************************************************
 * AJAX.
 * 
 * Fetching data elements of a given data set from the server.
 * Clearing both boxes first, then adding the fetched data elements
 * in the left (the one with id="unselectedDataElements").
 * 
 ******************************************************************/

 
function getAndClearSelectBox()
{
	var unselect = document.getElementById('unselectedDataElements');
	var select   = document.getElementById('dataElementsById');
	 
	while (select.childNodes.length > 0)
	{
		select.removeChild( select.firstChild );
	}
	
	while (unselect.childNodes.length > 0)
	{
		unselect.removeChild( unselect.firstChild );
	}
	
	unselect.disabled = false;
	select.disabled = false;
	
	return unselect;	
}

 
function fetchDataElementsSuccess( response )
{
	var root = response;
	
	var select = getAndClearSelectBox();

	var n = 0;
	
	for (i = 0; i < root.childNodes.length; i++)
	{
		if (root.childNodes[i].nodeName != 'dataElement') continue;
		
		id   = root.childNodes[i].getElementsByTagName('id')[0].childNodes[0].nodeValue;
		name = root.childNodes[i].getElementsByTagName('name')[0].childNodes[0].nodeValue;

		select.options[ n++ ] = new Option( ''+name, ''+id );
	}

	if ( n == 0 )
	{
		// No data elements in given data set. Disable the boxes
		
		select.options[ 0 ] = new Option('[ no data elements ]', '-1');
		
		document.getElementById('dataElementsById').disabled = true;
		select.disabled = true;
	}
	
}

function FetchDataElementsError( httpStatusCode )
{
	// error fetching data: set to "error" and disable the box
	
	var select = getAndClearSelectBox();
	
	select.options[ 0 ] = new Option('[ error fetching data elements ]', '-1');
	
	select.disabled = true;
	
	setMessage( 'An error occured while fetching data elements.' +
				' (HTTP status code: ' + httpStatusCode + ')' );
}

function fetchDataElements()
{
	// new data set selected. fetch a data elements for that data set
	
	var dataset = document.getElementById('dataset').value;
	
	var request = new Request();
	
	request.setResponseTypeXML( 'dataElements' );
	request.setCallbackSuccess( fetchDataElementsSuccess );
	request.setCallbackError( FetchDataElementsError );
	
	request.send('../dhis-web-commons-ajax/getDataElements.action?dataSetId=' + dataset);
}


/******************************************************************
 * Exchanges the visible form, depending on the a dropdown menu.
 *
 * The available choices should be among these two:
 *  - Min-Max, or
 *  - Standard Deviation
 *
 ******************************************************************/
 
function chooseType()
{
	var stddev = document.getElementById('stddevform');
	
	var choice = document.getElementById('outlierType').value;
	
	if ( choice == 'minmax' )
	{
		stddev.style.display = 'none';
	}
	else if ( choice == 'stddev' )
	{
		stddev.style.display = '';
	}
	else
	{
		stddev.style.display = 'none';
	}
}

 