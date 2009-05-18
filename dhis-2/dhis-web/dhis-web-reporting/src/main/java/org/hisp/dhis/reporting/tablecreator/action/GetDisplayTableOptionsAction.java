package org.hisp.dhis.reporting.tablecreator.action;

import java.util.List;

import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableColumn;
import org.hisp.dhis.reporttable.ReportTableService;

import com.opensymphony.xwork.Action;

public class GetDisplayTableOptionsAction
    implements Action
{
    private ReportTableService reportTableService;

    public void setReportTableService( ReportTableService reportTableService )
    {
        this.reportTableService = reportTableService;
    }

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private ReportTable reportTable;

    public ReportTable getReportTable()
    {
        return reportTable;
    }

    private List<ReportTableColumn> columns;

    public List<ReportTableColumn> getColumns()
    {
        return columns;
    }

    public String execute()
    {
        reportTable = reportTableService.getReportTable( id );
        
        reportTable.init();
        
        columns = reportTable.getFilledReportTableColumns();
        
        return SUCCESS;
    }
}
