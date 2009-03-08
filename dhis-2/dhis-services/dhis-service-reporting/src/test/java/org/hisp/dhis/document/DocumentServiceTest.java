package org.hisp.dhis.document;

import java.util.Collection;

import org.hisp.dhis.DhisSpringTest;

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
    
    public void testSaveGet()
    {
        int id = documentService.saveDocument( documentA );
        
        assertEquals( documentA, documentService.getDocument( id ) );
    }

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
    
    public void testGetByName()
    {
        documentService.saveDocument( documentA );
        documentService.saveDocument( documentB );
        documentService.saveDocument( documentC );
        
        assertEquals( documentA, documentService.getDocumentByName( "DocumentA" ) );
    }
}
