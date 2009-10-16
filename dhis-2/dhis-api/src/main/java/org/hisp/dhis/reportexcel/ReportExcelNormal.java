package org.hisp.dhis.reportexcel;


public class ReportExcelNormal
    extends ReportExcel
{
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ReportExcelNormal()
    {
        super();
    }
    
    @Override
    public String getReportType()
    {       
        return ReportExcel.TYPE.NORMAL;
    }
}
