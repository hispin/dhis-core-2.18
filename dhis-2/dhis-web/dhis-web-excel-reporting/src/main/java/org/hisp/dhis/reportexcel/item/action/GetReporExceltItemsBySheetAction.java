package org.hisp.dhis.reportexcel.item.action;

import java.util.Collection;

import org.hisp.dhis.reportexcel.ReportExcelItem;
import org.hisp.dhis.reportexcel.ReportExcelService;

import com.opensymphony.xwork2.Action;

public class GetReporExceltItemsBySheetAction
    implements Action
{

    private ReportExcelService reportExcelService;

    private Integer reportId;

    private Integer sheetNo;

    private Collection<ReportExcelItem> reportItems;

    public void setReportExcelService( ReportExcelService reportExcelService )
    {
        this.reportExcelService = reportExcelService;
    }

    public Collection<ReportExcelItem> getReportItems()
    {
        return reportItems;
    }

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    public void setSheetNo( Integer sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    @Override
    public String execute()
        throws Exception
    {
        reportItems = reportExcelService.getReportExcelItem( sheetNo, reportId );

        return SUCCESS;
    }

}
