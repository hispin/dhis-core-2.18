package org.hisp.dhis.reporting.document.action;

import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;

import com.opensymphony.xwork.Action;

public class GetDocumentAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DocumentService documentService;

    public void setDocumentService( DocumentService documentService )
    {
        this.documentService = documentService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Document document;

    public Document getDocument()
    {
        return document;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        document = documentService.getDocument( id );
        
        return SUCCESS;
    }
}

