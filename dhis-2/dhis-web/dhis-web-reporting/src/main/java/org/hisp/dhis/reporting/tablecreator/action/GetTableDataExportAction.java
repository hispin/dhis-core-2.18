package org.hisp.dhis.reporting.tablecreator.action;

import java.io.InputStream;

import org.hisp.dhis.importexport.ExportParams;
import org.hisp.dhis.importexport.ExportService;
import org.hisp.dhis.importexport.ImportExportServiceManager;

import com.opensymphony.xwork.Action;

public class GetTableDataExportAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ImportExportServiceManager serviceManager;

    public void setServiceManager( ImportExportServiceManager serviceManager )
    {
        this.serviceManager = serviceManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private String exportFormat;

    public void setExportFormat( String exportFormat )
    {
        this.exportFormat = exportFormat;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }
    
    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        ExportService exportService = serviceManager.getExportService( exportFormat );
        
        ExportParams params = new ExportParams();
        
        params.getReportTables().add( id );
        
        inputStream = exportService.exportData( params );
        /*
        int l = 0;
        while ( ( l = inputStream.read() ) != -1 )
            System.out.println( l);
        */
        fileName = "ReportTable.zip";
        
        return SUCCESS;
    }
}
