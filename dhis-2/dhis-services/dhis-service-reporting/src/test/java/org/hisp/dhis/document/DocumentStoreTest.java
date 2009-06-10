package org.hisp.dhis.document;

import java.util.Collection;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

import static junit.framework.Assert.*;

public class DocumentStoreTest
    extends DhisSpringTest
{
    private DocumentStore documentStore;
    
    private Document documentA;
    private Document documentB;
    private Document documentC;
    
    @Override
    public void setUpTest()
    {
        documentStore = (DocumentStore) getBean( DocumentStore.ID );
        
        documentA = new Document( "DocumentA", "UrlA", true );
        documentB = new Document( "DocumentB", "UrlB", true );
        documentC = new Document( "DocumentC", "UrlC", false );
    }

    @Test
    public void testSaveGet()
    {
        int id = documentStore.saveDocument( documentA );
        
        assertEquals( documentA, documentStore.getDocument( id ) );
    }

    @Test
    public void testDelete()
    {
        int idA = documentStore.saveDocument( documentA );
        int idB = documentStore.saveDocument( documentB );
        
        assertNotNull( documentStore.getDocument( idA ) );
        assertNotNull( documentStore.getDocument( idB ) );
        
        documentStore.deleteDocument( documentA );
        
        assertNull( documentStore.getDocument( idA ) );
        assertNotNull( documentStore.getDocument( idB ) );
        
        documentStore.deleteDocument( documentB );

        assertNull( documentStore.getDocument( idA ) );
        assertNull( documentStore.getDocument( idB ) );
    }

    @Test
    public void testGetAll()
    {
        documentStore.saveDocument( documentA );
        documentStore.saveDocument( documentB );
        documentStore.saveDocument( documentC );
        
        Collection<Document> actual = documentStore.getAllDocuments();
        
        assertEquals( 3, actual.size() );
        assertTrue( actual.contains( documentA ) );
        assertTrue( actual.contains( documentB ) );
        assertTrue( actual.contains( documentC ) );        
    }

    @Test
    public void testGetByName()
    {
        documentStore.saveDocument( documentA );
        documentStore.saveDocument( documentB );
        documentStore.saveDocument( documentC );
        
        assertEquals( documentA, documentStore.getDocumentByName( "DocumentA" ) );
    }
}
