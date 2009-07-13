package org.hisp.dhis.vn.report;

public class ReportExcelNormal
    extends ReportExcel 
{   

    public ReportExcelNormal()
    {
        super();
    }
    
    

    public ReportExcelNormal( String name, String excelTemplateFile, int periodRow, int periodColumn,
        int organisationRow, int organisationColumn )
    {
        super( name, excelTemplateFile, periodRow, periodColumn, organisationRow, organisationColumn );
       
    }   

    public String getReportType()
    {
        return ReportExcel.TYPE.NORMAL;
    }

}
