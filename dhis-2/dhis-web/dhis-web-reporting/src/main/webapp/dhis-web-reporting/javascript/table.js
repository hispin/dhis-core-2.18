
function removeTable( tableId, tableName )
{
    removeItem( tableId, tableName, i18n_confirm_delete, "removeTable.action" );
}

function addReportTableToDashboard( id )
{
    var dialog = window.confirm( i18n_confirm_add_report_table_to_dashboard );

    if ( dialog )
    {
        $.get( "addReportTableToDashboard.action?id=" + id );
    }
}