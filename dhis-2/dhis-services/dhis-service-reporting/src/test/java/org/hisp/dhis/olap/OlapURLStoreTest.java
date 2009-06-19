package org.hisp.dhis.olap;

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
import static junit.framework.Assert.assertTrue;

import java.util.Collection;

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class OlapURLStoreTest
    extends DhisSpringTest
{
    private OlapURLStore olapURLStore;
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        olapURLStore = (OlapURLStore) getBean( OlapURLStore.ID );
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testSaveOlapURL()
    {
        OlapURL olapURL = createOlapURL( 'A' );
        
        int id = olapURLStore.save( olapURL );
        
        olapURL = olapURLStore.get( id );
        
        assertEquals( "OlapURLA", olapURL.getName() );
        assertEquals( "URLA", olapURL.getUrl() );
    }

    @Test
    public void testDeleteOlapURL()
    {
        OlapURL olapURLA = createOlapURL( 'A' );
        OlapURL olapURLB = createOlapURL( 'B' );
        
        int idA = olapURLStore.save( olapURLA );
        int idB = olapURLStore.save( olapURLB );
        
        assertNotNull( olapURLStore.get( idA ) );
        assertNotNull( olapURLStore.get( idB ) );
        
        olapURLStore.delete( olapURLA );

        assertNull( olapURLStore.get( idA ) );
        assertNotNull( olapURLStore.get( idB ) );

        olapURLStore.delete( olapURLB );
        
        assertNull( olapURLStore.get( idA ) );
        assertNull( olapURLStore.get( idB ) );
    }

    @Test
    public void testGetAllOlapURLs()
    {
        OlapURL olapURLA = createOlapURL( 'A' );
        OlapURL olapURLB = createOlapURL( 'B' );
        
        olapURLStore.save( olapURLA );
        olapURLStore.save( olapURLB );
        
        Collection<OlapURL> olapURLs = olapURLStore.getAll();
        
        assertNotNull( olapURLs );
        assertEquals( 2, olapURLs.size() );
        
        assertTrue( olapURLs.contains( olapURLA ) );
        assertTrue( olapURLs.contains( olapURLB ) );
    }
}
