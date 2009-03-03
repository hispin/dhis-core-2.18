package org.hisp.dhis.reporttable.statement;

import org.hisp.dhis.reporttable.ReportTable;

public class GetReportTableStatement
    extends ReportTableStatement
{
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public GetReportTableStatement( ReportTable reportTable )
    {
        super( reportTable );
    }

    // -------------------------------------------------------------------------
    // ReportTableStatement implementation
    // -------------------------------------------------------------------------

    @Override
    protected void init( ReportTable reportTable )
    {
        statement = "SELECT * FROM " + reportTable.getTableName();
    }
}
