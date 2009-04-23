package org.hisp.dhis.mapping;

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

import org.hisp.dhis.DhisConvenienceTest;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class MappingStoreTest
    extends DhisConvenienceTest
{
    private MappingStore mappingStore;
    
    private OrganisationUnit organisationUnit;
    
    private OrganisationUnitLevel organisationUnitLevel;
    
    private Map mapA;
    private Map mapB;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        mappingStore = (MappingStore) getBean( MappingStore.ID );
        
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        organisationUnit = createOrganisationUnit( 'A' );
        organisationUnitLevel = new OrganisationUnitLevel( 1, "Level" );
        
        organisationUnitService.addOrganisationUnit( organisationUnit );
        organisationUnitService.addOrganisationUnitLevel( organisationUnitLevel );
        
        mapA = createMap( 'A', organisationUnit, organisationUnitLevel );
        mapB = createMap( 'B', organisationUnit, organisationUnitLevel );        
    }

    // -------------------------------------------------------------------------
    // Map tests
    // -------------------------------------------------------------------------

    public void testAddMap()
    {
        int idA = mappingStore.addMap( mapA );
        int idB = mappingStore.addMap( mapB );
        
        assertEquals( mapA, mappingStore.getMap( idA ) );
        assertEquals( mapB, mappingStore.getMap( idB ) );
    }
    
    public void testDeleteMap()
    {
        int idA = mappingStore.addMap( mapA );
        int idB = mappingStore.addMap( mapB );
        
        assertNotNull( mappingStore.getMap( idA ) );
        assertNotNull( mappingStore.getMap( idB ) );
        
        mappingStore.deleteMap( mapA );

        assertNull( mappingStore.getMap( idA ) );
        assertNotNull( mappingStore.getMap( idB ) );

        mappingStore.deleteMap( mapB );

        assertNull( mappingStore.getMap( idA ) );
        assertNull( mappingStore.getMap( idB ) );        
    }
}
