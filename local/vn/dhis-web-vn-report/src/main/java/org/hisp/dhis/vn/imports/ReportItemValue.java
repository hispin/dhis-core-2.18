package org.hisp.dhis.vn.imports;

import org.hisp.dhis.vn.report.ReportItem;

public class ReportItemValue
{
    private ReportItem reportItem;

    private String value;

    public ReportItemValue( ReportItem reportItem, String value )
    {
        super();
        this.reportItem = reportItem;
        this.value = value;
    }

    public ReportItem getReportItem()
    {
        return reportItem;
    }

    public void setReportItem( ReportItem reportItem )
    {
        this.reportItem = reportItem;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

}
