
var MODE_REPORT = "report";
var MODE_TABLE = "table";

// -----------------------------------------------------------------------------
// Report params
// -----------------------------------------------------------------------------

var paramOrganisationUnit = null;

function paramOrganisationUnitSet( id )
{
	paramOrganisationUnit = id;
}

// -----------------------------------------------------------------------------
// Validation
// -----------------------------------------------------------------------------

function validationError()
{
	if ( $( "#selectionTree" ).length && paramOrganisationUnit == null )
	{
		setMessage( i18n_please_select_unit );
		return true;
	}
	
	return false;
}	

// -----------------------------------------------------------------------------
// Report
// -----------------------------------------------------------------------------

function generateReport()
{
	if ( validationError() )
	{
		return false;
	}
		
    setWaitMessage( i18n_please_wait );
    
    var doDataMart = ( $( "#doDataMart" ).length && $( "#doDataMart" ).val() == "true" );
    
    if ( doDataMart )
    {    
	    var url = "createTable.action?" + getUrlParams();
	    
	    var request = new Request();
	    request.setCallbackSuccess( getReportStatus );    
	    request.send( url );
    }
    else
    {
    	viewReport();
    }
}

function getReportStatus()
{   
    var url = "getStatus.action";
    
    var request = new Request();
    request.setResponseTypeXML( "status" );
    request.setCallbackSuccess( reportStatusReceived );    
    request.send( url );
}

function reportStatusReceived( xmlObject )
{
    var statusMessage = getElementValue( xmlObject, "statusMessage" );
    var finished = getElementValue( xmlObject, "finished" );
    
    if ( finished == "true" )
    {
        setMessage( i18n_process_completed );        
    	viewReport();
    }
    else
    {
        setTimeout( "getReportStatus();", 1500 );
    }
}

function viewReport( urlParams )
{
	var mode = $( "#mode" ).val();
	
    if ( mode == MODE_REPORT )
    {
    	window.location.href = "renderReport.action?" + getUrlParams();
    }
    else // MODE_TABLE
    {
    	window.location.href = "exportTable.action?type=html&" + getUrlParams();
    }
}

function getUrlParams()
{
	 var url = "id=" + $( "#id" ).val() + "&mode=" + $( "#mode" ).val();
	    
    if ( $( "#reportingPeriod" ).length )
    {
        url += "&reportingPeriod=" + $( "#reportingPeriod" ).val();
    }
        
    if ( paramOrganisationUnit != null )
    {
        url += "&organisationUnitId=" + paramOrganisationUnit;
    }
	
	return url;
}

// -----------------------------------------------------------------------------
// Report table
// -----------------------------------------------------------------------------

function exportReport( type )
{
	var url = "exportTable.action?type=" + type + "&useLast=true";
	
	url += $( "#id" ).length ? ( "&id=" + $( "#id" ).val() ) : "";
	
	window.location.href = url;
}
