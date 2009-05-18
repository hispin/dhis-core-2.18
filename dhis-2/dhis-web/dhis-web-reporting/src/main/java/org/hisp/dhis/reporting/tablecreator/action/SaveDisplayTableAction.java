package org.hisp.dhis.reporting.tablecreator.action;

import java.util.List;

import org.hisp.dhis.reporttable.ReportTable;
import org.hisp.dhis.reporttable.ReportTableColumn;
import org.hisp.dhis.reporttable.ReportTableService;

import com.opensymphony.xwork.Action;

public class SaveDisplayTableAction
    implements Action
{
    private static final String SEPARATOR = "-";

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

    private List<String> column;

    public void setColumn( List<String> column )
    {
        this.column = column;
    }
    
    public String execute()
    {
        ReportTable reportTable = reportTableService.getReportTable( id );
        
        reportTable.getDisplayColumns().clear();
        
        for ( String col : column )
        {
            String[] columns = col.split( SEPARATOR );
            
            if ( columns.length > 1 )
            {            
                ReportTableColumn displayColumn = new ReportTableColumn();
                
                displayColumn.setName( columns[0] );
                displayColumn.setHeader( columns[1] );
                displayColumn.setHidden( columns.length > 2 ? Boolean.valueOf( columns[2] ) : false );
                
                reportTable.getDisplayColumns().add( displayColumn );
            }
        }
        
        reportTableService.updateReportTable( reportTable );
        
        return SUCCESS;
    }
}
