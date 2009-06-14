package org.hisp.dhis.jdbc.batchhandler;

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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Collection;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionService;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.system.util.UUIdUtils;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataElementCategoryOptionBatchHandlerTest
    extends DhisTest
{
    private BatchHandlerFactory batchHandlerFactory;
    
    private BatchHandler batchHandler;
    
    private DataElementCategoryOption categoryOptionA;
    private DataElementCategoryOption categoryOptionB;
    private DataElementCategoryOption categoryOptionC;
    
    private final String UUID = UUIdUtils.getUUId();
    
    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        categoryOptionService = (DataElementCategoryOptionService) getBean( DataElementCategoryOptionService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        batchHandlerFactory = (BatchHandlerFactory) getBean( "batchHandlerFactory" );
        
        batchHandler = batchHandlerFactory.createBatchHandler( DataElementCategoryOptionBatchHandler.class );

        batchHandler.init();
        
        categoryOptionA = new DataElementCategoryOption( "categoryOptionA", UUID );
        categoryOptionB = new DataElementCategoryOption( "categoryOptionB", UUID );
        categoryOptionC = new DataElementCategoryOption( "categoryOptionC" );
    }

    @Override
    public void tearDownTest()
    {
        batchHandler.flush();
    }
    
    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddObject()
    {
        batchHandler.addObject( categoryOptionA );
        batchHandler.addObject( categoryOptionB );
        batchHandler.addObject( categoryOptionC );
        
        batchHandler.flush();
        
        Collection<DataElementCategoryOption> categoryOptions = categoryOptionService.getAllDataElementCategoryOptions();
        
        assertTrue( categoryOptions.contains( categoryOptionA ) );
        assertTrue( categoryOptions.contains( categoryOptionB ) );
        assertTrue( categoryOptions.contains( categoryOptionC ) );
    }

    @Test
    public void testInsertObject()
    {
        int idA = batchHandler.insertObject( categoryOptionA, true );
        int idB = batchHandler.insertObject( categoryOptionB, true );
        int idC = batchHandler.insertObject( categoryOptionC, true );
        
        assertNotNull( categoryOptionService.getDataElementCategoryOption( idA ) );
        assertNotNull( categoryOptionService.getDataElementCategoryOption( idB ) );
        assertNotNull( categoryOptionService.getDataElementCategoryOption( idC ) );
    }

    @Test
    public void testUpdateObject()
    {
        int id = categoryOptionService.addDataElementCategoryOption( categoryOptionA );
        
        categoryOptionA.setName( "UpdatedName" );
        
        batchHandler.updateObject( categoryOptionA );
        
        assertEquals( "UpdatedName", categoryOptionService.getDataElementCategoryOption( id ).getName() );
    }

    @Test
    public void testGetObjectIdentifier()
    {
        int referenceId = categoryOptionService.addDataElementCategoryOption( categoryOptionA );
        
        int retrievedId = batchHandler.getObjectIdentifier( "categoryOptionA" );
        
        assertEquals( referenceId, retrievedId );
    }

    @Test
    public void testObjectExists()
    {
        categoryOptionService.addDataElementCategoryOption( categoryOptionA );
        
        assertTrue( batchHandler.objectExists( categoryOptionA ) );
        
        assertFalse( batchHandler.objectExists( categoryOptionB ) );
    }
}
