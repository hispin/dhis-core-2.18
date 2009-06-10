package org.hisp.dhis.document;

import java.util.Collection;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

import static junit.framework.Assert.*;

public class DocumentServiceTest
    extends DhisSpringTest
{
    private DocumentService documentService;
    
    private Document documentA;
    private Document documentB;
    private Document documentC;
    
    @Override
    public void setUpTest()
    {
        documentService = (DocumentService) getBean( DocumentService.ID );
        
        documentA = new Document( "DocumentA", "UrlA", true );
        documentB = new Document( "DocumentB", "UrlB", true );
        documentC = new Document( "DocumentC", "UrlC", false );
    }

    @Test
    public void testSaveGet()
    {
        int id = documentService.saveDocument( documentA );
        
        assertEquals( documentA, documentService.getDocument( id ) );
    }

    @Test
    public void testDelete()
    {
        int idA = documentService.saveDocument( documentA );
        int idB = documentService.saveDocument( documentB );
        
        assertNotNull( documentService.getDocument( idA ) );
        assertNotNull( documentService.getDocument( idB ) );
        
        documentService.deleteDocument( documentA );
        
        assertNull( documentService.getDocument( idA ) );
        assertNotNull( documentService.getDocument( idB ) );
        
        documentService.deleteDocument( documentB );

        assertNull( documentService.getDocument( idA ) );
        assertNull( documentService.getDocument( idB ) );
    }

    @Test
    public void testGetAll()
    {
        documentService.saveDocument( documentA );
        documentService.saveDocument( documentB );
        documentService.saveDocument( documentC );
        
        Collection<Document> actual = documentService.getAllDocuments();
        
        assertEquals( 3, actual.size() );
        assertTrue( actual.contains( documentA ) );
        assertTrue( actual.contains( documentB ) );
        assertTrue( actual.contains( documentC ) );        
    }

    @Test
    public void testGetByName()
    {
        documentService.saveDocument( documentA );
        documentService.saveDocument( documentB );
        documentService.saveDocument( documentC );
        
        assertEquals( documentA, documentService.getDocumentByName( "DocumentA" ) );
    }
}
