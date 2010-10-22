
var paramParentOrganisationUnit = null;
var paramOrganisationUnit = null;

function paramParentOrganisationUnitSet( id )
{
	paramParentOrganisationUnit = id;
}

function paramOrganisationUnitSet( id )
{
	paramOrganisationUnit = id;
}

var tempUrl = null;

function runAndViewReport( reportId, reportUrl )
{
    setWaitMessage( i18n_please_wait );
        
    var url = "createTable.action?id=" + reportId + "&doDataMart=" + getListValue( "doDataMart" ) + "&mode=report";
    
    if ( document.getElementById( "reportingPeriod" ) != null )
    {
        url += "&reportingPeriod=" + getListValue( "reportingPeriod" );
    }
    
    if ( paramParentOrganisationUnit != null )
    {
        url += "&parentOrganisationUnitId=" + paramParentOrganisationUnit;
    }
    
    if ( paramOrganisationUnit != null )
    {
        url += "&organisationUnitId=" + paramOrganisationUnit;
    }
    
	tempUrl = reportUrl;
    
    var request = new Request();
    request.setCallbackSuccess( runAndViewReportReceived );    
    request.send( url );
}

function runAndViewReportReceived( messageElement )
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
        
        window.location.href = tempUrl;
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

function addReport()
{
    selectAllById( "selectedReportTables" );
    
    document.getElementById( "reportForm" ).submit();
}

function removeReport( id )
{
	removeItem( id, "", i18n_confirm_remove_report, "removeReport.action" );
}

function addToDashboard( id )
{
    var dialog = window.confirm( i18n_confirm_add_to_dashboard );
    
    if ( dialog )
    {
        var request = new Request(); 
        request.send( "addReportToDashboard.action?id=" + id );
    }
}
