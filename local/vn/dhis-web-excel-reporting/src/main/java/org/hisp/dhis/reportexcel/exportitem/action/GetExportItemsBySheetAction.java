package org.hisp.dhis.reportexcel.exportitem.action;

import java.util.Collection;

import org.hisp.dhis.reportexcel.ExportReportService;
import org.hisp.dhis.reportexcel.ReportExcelItem;

import com.opensymphony.xwork2.Action;

public class GetExportItemsBySheetAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ExportReportService exportReportService;

    public void setExportReportService( ExportReportService exportReportService )
    {
        this.exportReportService = exportReportService;
    }

    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private Integer exportReportId;

    public void setExportReportId( Integer exportReportId )
    {
        this.exportReportId = exportReportId;
    }

    private Integer sheetNo;

    public void setSheetNo( Integer sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    private Collection<ReportExcelItem> exportItems;

    public Collection<ReportExcelItem> getExportItems()
    {
        return exportItems;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        exportItems = exportReportService.getExportItem( sheetNo, exportReportId );

        return SUCCESS;
    }

}
