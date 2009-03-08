package org.hisp.dhis.reporting.document.action;

import java.io.InputStream;

import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.external.location.LocationManager;

import com.opensymphony.xwork.Action;

public class LoadDocumentFromFileSystemAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }
    
    private DocumentService documentService;

    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }   

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
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

    public String execute()
        throws Exception
    {
        Document document = documentService.getDocument( id );
        
        inputStream = locationManager.getInputStream( document.getUrl(), DocumentService.DIR );
        
        fileName = document.getUrl();
        
        return SUCCESS;
    }
}
