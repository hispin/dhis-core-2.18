package org.hisp.dhis.reporting.document.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.document.Document;
import org.hisp.dhis.document.DocumentService;

import com.opensymphony.xwork.Action;

public class GetAllDocumentsAction
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
    // Output
    // -------------------------------------------------------------------------

    private List<Document> documents;

    public List<Document> getDocuments()
    {
        return documents;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        documents = new ArrayList<Document>( documentService.getAllDocuments() );
        
        return SUCCESS;
    }
}
