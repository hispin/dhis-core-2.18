package org.hisp.dhis.vn.reportitem.action;

import java.util.Collection;

import org.hisp.dhis.vn.report.ReportExcelInterface;
import org.hisp.dhis.vn.report.ReportExcelService;
import org.hisp.dhis.vn.report.ReportItem;
import org.hisp.dhis.vn.report.action.ActionSupport;

public class CopyReportItemsAction
    extends ActionSupport
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private ReportExcelService reportService;

    // -------------------------------------------
    // Input
    // -------------------------------------------

    private Integer reportId;

    private Integer sheetNo;

    private Collection<String> reportItems;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setReportService( ReportExcelService reportService )
    {
        this.reportService = reportService;
    }

    public Integer getReportId()
    {
        return reportId;
    }

    public Integer getSheetNo()
    {
        return sheetNo;
    }

    public void setReportId( Integer reportId )
    {
        this.reportId = reportId;
    }

    public void setReportItems( Collection<String> reportItems )
    {
        this.reportItems = reportItems;
    }

    public void setSheetNo( Integer sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    public String execute()
        throws Exception
    {
        ReportExcelInterface reportExcelInterface = reportService.getReport( reportId.intValue() );
        
        //Set<ReportItem> reportItems_ = new HashSet<ReportItem>();
        
            
        for(String itemId:this.reportItems){
            ReportItem reportItem = reportService.getReportItem( Integer.parseInt( itemId ) );
            ReportItem newReportItem = new ReportItem();
            newReportItem.setName( reportItem.getName() );
            newReportItem.setItemType( reportItem.getItemType() );
            newReportItem.setPeriodType( reportItem.getPeriodType() );
            newReportItem.setExpression( reportItem.getExpression() );
            newReportItem.setRow( reportItem.getRow() );
            newReportItem.setColumn( reportItem.getColumn() );
            newReportItem.setSheetNo( sheetNo );
            reportExcelInterface.addReportItem( reportItem );
            reportService.updateReport( reportExcelInterface );
            
        }   
        //Collection<ReportItem> currentReportItems = reportExcelInterface.getReportItems();
        //reportItems_.addAll( currentReportItems );
        //reportExcelInterface.setReportItems(reportItems_);
        
        
        
        
        return SUCCESS;
    }
}
