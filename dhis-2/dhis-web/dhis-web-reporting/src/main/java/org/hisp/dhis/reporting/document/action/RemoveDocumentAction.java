package org.hisp.dhis.reporting.document.action;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.external.location.LocationManagerException;

import com.opensymphony.xwork.Action;

public class RemoveDocumentAction
    implements Action
{
    private static final Log log = LogFactory.getLog( RemoveDocumentAction.class );
    
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
    // Dependencies
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( id != null )
        {
            Document document = documentService.getDocument( id );
            
            if ( !document.isExternal() )
            {
                try
                {
                    File file = locationManager.getFileForReading( document.getUrl(), DocumentService.DIR );
                    
                    if ( file.delete() )
                    {
                        log.info( "Document " + document.getUrl() + " successfully deleted" );
                    }
                    else
                    {
                        log.warn( "Document " + document.getUrl() + " could not be deleted" );
                    }
                }
                catch ( LocationManagerException ex )
                {
                    log.warn( "An error occured while deleting " + document.getUrl() );
                }
            }
            
            documentService.deleteDocument( document );
        }
        
        return SUCCESS;
    }
}
