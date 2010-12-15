
var FORMAT_HTML = "html";
var FORMAT_XSL = "xsl";
var FORMAT_CSV = "csv";

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
        	if ( outputFormat == FORMAT_HTML )
        	{
        		window.location.href = "getTableData.action?id=" + $( "#id" ).val();
        	}
        	else if ( outputFormat == FORMAT_XSL )
        	{
        		window.location.href = "generateTableDataWorkbook.action?id=" + $( "#id" ).val();
        	}
        	else if ( outputFormat == FORMAT_CSV )
        	{
        		window.location.href = "getTableDataExport.action?exportFormat=CSV&id=" + $( "#id" ).val();
        	}
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

// -----------------------------------------------------------------------------
// ReportTable
// -----------------------------------------------------------------------------
/*
function createTable( tableId )
{
	if ( validationError() )
	{
		return false;
	}
	
    var url = "createTable.action?id=" + tableId + "&doDataMart=" + getListValue( "doDataMart" ) + "&mode=table";
    
    if ( document.getElementById( "reportingPeriod" ) != null )
    {
        url += "&reportingPeriod=" + getListValue( "reportingPeriod" );
    }
    
    if ( paramOrganisationUnit != null )
    {
        url += "&organisationUnitId=" + paramOrganisationUnit;
    }
    
    var request = new Request();
    request.setCallbackSuccess( createTableReceived );    
    request.send( url );
}

function createTableReceived( messageElement )
{
    getTableStatus();
}

function getTableStatus()
{
    var url = "getStatus.action";
    
    var request = new Request();
    request.setResponseTypeXML( 'status' );
    request.setCallbackSuccess( tableStatusReceived );    
    request.send( url );
}

function tableStatusReceived( xmlObject )
{
    var statusMessage = getElementValue( xmlObject, 'statusMessage' );
    var finished = getElementValue( xmlObject, 'finished' );
    
    if ( finished == "true" )
    {
        setMessage( i18n_process_completed );
    }
    else if ( statusMessage == null )
    {
        setWaitMessage( i18n_please_wait );
    }
    else
    {
        setWaitMessage( i18n_please_wait + " - " + statusMessage );
    }
    
    waitAndGetTableStatus( 2000 );
}

function waitAndGetTableStatus( millis )
{
    setTimeout( "getTableStatus();", millis );
}
*/
// -----------------------------------------------------------------------------
// Export ReportTable
// -----------------------------------------------------------------------------
/*
function exportToHtml( id )
{
	verifyAndExport( id, "getTableData.action?id=" + id );
}

function exportToExcel( id )
{	
	verifyAndExport( id, "generateTableDataWorkbook.action?id=" + id );
}

function exportToCsv( id )
{
	verifyAndExport( id, "getTableDataExport.action?exportFormat=CSV&id=" + id );
}

function verifyAndExport( id, url )
{   
    $.getJSON( 
       "verifyReportTable.action",
       { "id": id },
       function( json )
       {
           if ( json.response == "error" )
           {
               setHeaderDelayMessage( i18n_generate_before_export );
           }
           else
           {
               window.location.href = url;
           }
       } );
}
*/