package org.hisp.dhis.reporting.document.action;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;
import org.hisp.dhis.external.location.LocationManager;

import com.opensymphony.xwork.Action;

public class SaveDocumentAction
    implements Action
{
    private static final Log log = LogFactory.getLog( SaveDocumentAction.class );
    
    private static final String HTTP_PREFIX = "http://";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DocumentService documentService;

    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }

    private LocationManager locationManager;

    public void setLocationManager( LocationManager locationManager )
    {
        this.locationManager = locationManager;
    }
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String url;

    public void setUrl( String url )
    {
        this.url = url;
    }

    private Boolean external;

    public void setExternal( Boolean external )
    {
        this.external = external;
    }

    private File file;

    public void setUpload( File file )
    {
        this.file = file;
    }
    
    private String fileName;
    
    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }
    
    private String contentType;
    
    public void setUploadContentType( String contentType )
    {
        this.contentType = contentType;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( !external )
        {
            log.info( "Uploading file: '" + fileName + "', content-type: '" + contentType + "'" );

            File destination = locationManager.getFileForWriting( fileName, DocumentService.DIR );
            
            boolean fileMoved = file.renameTo( destination );
            
            if ( !fileMoved )
            {
                throw new RuntimeException( "File was not uploaded" );
            }
            
            url = fileName;
        }
        else
        {
            if ( !url.startsWith( HTTP_PREFIX ) )
            {
                url = HTTP_PREFIX + url;
            }
        }
        
        log.info( "Document name: '" + name + "', url: '" + url + "', external: '" + external + "'" );
        
        Document document = new Document( name, url, external );
        
        documentService.saveDocument( document );
        
        return SUCCESS;
    }
}
