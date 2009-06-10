package org.hisp.dhis.source;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: SourceStoreTest.java 3200 2007-03-29 11:51:17Z torgeilo $
 */
public class SourceStoreTest
    extends DhisSpringTest
{
    private SourceStore sourceStore;

    // -------------------------------------------------------------------------
    // Set up/tear down
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        sourceStore = (SourceStore) getBean( SourceStore.ID );
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void addGetSource()
    {
        Source sourceA = new DummySource( "SourceA" );
        Source sourceB = new DummySource( "SourceB" );
        
        int idA = sourceStore.addSource( sourceA );
        int idB = sourceStore.addSource( sourceB );
        
        assertEquals( sourceA, sourceStore.getSource( idA ) );
        assertEquals( sourceB, sourceStore.getSource( idB ) );        
    }
    
    @Test
    public void updateSource()
    {
        DummySource source = new DummySource( "SourceA" );
        
        int id = sourceStore.addSource( source );
        
        assertEquals( source, sourceStore.getSource( id ) );
        
        source.setName( "SourceB" );
        
        sourceStore.updateSource( source );
        
        assertEquals( source, sourceStore.getSource( id ) );
    }
    
    @Test
    public void delete()
    {
        Source sourceA = new DummySource( "SourceA" );
        Source sourceB = new DummySource( "SourceB" );
        
        int idA = sourceStore.addSource( sourceA );
        int idB = sourceStore.addSource( sourceB );
        
        assertNotNull( sourceStore.getSource( idA ) );
        assertNotNull( sourceStore.getSource( idB ) );
        
        sourceStore.deleteSource( sourceA );
        
        assertNull( sourceStore.getSource( idA ) );
        assertNotNull( sourceStore.getSource( idB ) );
        
        sourceStore.deleteSource( sourceB );
        
        assertNull( sourceStore.getSource( idA ) );
        assertNull( sourceStore.getSource( idB ) );        
    }
    
    @Test
    public void getAll()
    {
        Source sourceA = new DummySource( "SourceA" );
        Source sourceB = new DummySource( "SourceB" );
        
        sourceStore.addSource( sourceA );
        sourceStore.addSource( sourceB );
                
        assertNotNull( sourceStore.getAllSources() );
        assertEquals( sourceStore.getAllSources().size(), 2 );
    }
}
