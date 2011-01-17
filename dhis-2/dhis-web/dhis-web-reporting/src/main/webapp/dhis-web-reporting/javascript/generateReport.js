
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
        	var url = "exportTable.action?id=" + $( "#id" ).val() + "&type=html";
		    
		    if ( $( "#reportingPeriod" ).length )
		    {
		        url += "&reportingPeriod=" + $( "#reportingPeriod" ).val();
		    }
		        
		    if ( paramOrganisationUnit != null )
		    {
		        url += "&organisationUnitId=" + paramOrganisationUnit;
		    }
    
        	window.location.href = url;
        }
    }
    else if ( statusMessage == null )
    {
        setWaitMessage( i18n_please_wait );
        
        waitAndGetReportStatus( 1500 );
    }
    else
    {
        setWaitMessage( i18n_please_wait + " - " + statusMessage );
        
        waitAndGetReportStatus( 1500 );
    }
}

function waitAndGetReportStatus( millis )
{
    setTimeout( "getReportStatus();", millis );
}

function exportReport( type )
{
	var url = "exportTable.action?type=" + type + "&useLast=true";
	
	url += $( "#id" ).length ? ( "&id=" + $( "#id" ).val() ) : "";
	
	window.location.href = url;
}
