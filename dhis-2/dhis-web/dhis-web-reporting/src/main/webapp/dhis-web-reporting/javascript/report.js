
function addReport()
{
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
