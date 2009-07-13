// Chau Thu Tran create

package org.hisp.dhis.vn.report;

public class ReportExcelListing extends ReportExcel 
{
	
    public ReportExcelListing()
    {
        super();
    }
    
    public ReportExcelListing( String name, String excelTemplateFile, int periodRow, int periodColumn,
        int organisationRow, int organisationColumn )
    {
        super( name, excelTemplateFile, periodRow, periodColumn, organisationRow, organisationColumn );
       
    }
    
    public String getReportType()
    {
        return ReportExcel.TYPE.LISTING;
    }
}
