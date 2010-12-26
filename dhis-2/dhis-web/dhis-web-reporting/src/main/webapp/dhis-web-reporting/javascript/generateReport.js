
var MODE_REPORT = "report";
var MODE_TABLE = "table";

var outputFormat = null;

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

function generateReport( format )
{
	outputFormat = format;
	
	if ( validationError() )
	{
		return false;
	}
		
    setWaitMessage( i18n_please_wait );
        
    var url = "createTable.action?id=" + $( "#id" ).val() + 
    	"&doDataMart=" + $( "#doDataMart" ).val() + "&mode=" + $( "#mode" ).val();
    
    if ( $( "#reportingPeriod" ).length )
    {
        url += "&reportingPeriod=" + $( "#reportingPeriod" ).val();
    }
        
    if ( paramOrganisationUnit != null )
    {
        url += "&organisationUnitId=" + paramOrganisationUnit;
    }
    
    var request = new Request();
    request.setCallbackSuccess( generateReportReceived );    
    request.send( url );
}

function generateReportReceived( messageElement )
{   
    getReportStatus();
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
        
        if ( $( "#mode" ).val() == MODE_REPORT )
        {
        	window.location.href = $( "#url" ).val();
        }
        else if ( $( "#mode" ).val() == MODE_TABLE )
        {
        	window.location.href = "exportTable.action?id=" + $( "#id" ).val() + "&type=" + outputFormat;
        }
    }
    else if ( statusMessage == null )
    {
        setWaitMessage( i18n_please_wait );
        
        waitAndGetReportStatus( 2000 );
    }
    else
    {
        setWaitMessage( i18n_please_wait + " - " + statusMessage );
        
        waitAndGetReportStatus( 2000 );
    }
}

function waitAndGetReportStatus( millis )
{
    setTimeout( "getReportStatus();", millis );
}
