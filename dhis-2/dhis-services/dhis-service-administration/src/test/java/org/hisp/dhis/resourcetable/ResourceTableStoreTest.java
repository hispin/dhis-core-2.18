package org.hisp.dhis.resourcetable;

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

import org.hisp.dhis.DhisSpringTest;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ResourceTableStoreTest
    extends DhisSpringTest
{
    private ResourceTableStore resourceTableStore;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        resourceTableStore = (ResourceTableStore) getBean( ResourceTableStore.ID );
    }
    
    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testOrganisationUnitStructure()
    {
        OrganisationUnitStructure structure1 = new OrganisationUnitStructure();
        OrganisationUnitStructure structure2 = new OrganisationUnitStructure();
        OrganisationUnitStructure structure3 = new OrganisationUnitStructure();
        
        structure1.setIdLevel1( 1 );
        structure2.setIdLevel1( 1 );
        structure2.setIdLevel2( 2 );
        structure3.setIdLevel1( 1 );
        structure3.setIdLevel2( 2 );
        structure3.setIdLevel3( 3 );

        structure1.setGeoCodeLevel1( "A" );
        structure2.setGeoCodeLevel1( "A" );
        structure2.setGeoCodeLevel1( "B" );
        structure3.setGeoCodeLevel1( "A" );
        structure3.setGeoCodeLevel1( "B" );
        structure3.setGeoCodeLevel1( "C" );
        
        resourceTableStore.addOrganisationUnitStructure( structure1 );
        resourceTableStore.addOrganisationUnitStructure( structure2 );
        resourceTableStore.addOrganisationUnitStructure( structure3 );
                
        assertEquals( 3, resourceTableStore.getOrganisationUnitStructures().size() );

        assertEquals( 3, resourceTableStore.deleteOrganisationUnitStructures() );

        assertEquals( 0, resourceTableStore.getOrganisationUnitStructures().size() );
    }

    @Test
    public void testGroupSetStructure()
    {
        GroupSetStructure structure1 = new GroupSetStructure( 1, 1, 1 );
        GroupSetStructure structure2 = new GroupSetStructure( 2, 2, 2 );
        GroupSetStructure structure3 = new GroupSetStructure( 3, 3, 3 );

        resourceTableStore.addGroupSetStructure( structure1 );
        resourceTableStore.addGroupSetStructure( structure2 );
        resourceTableStore.addGroupSetStructure( structure3 );

        assertEquals( 3, resourceTableStore.getGroupSetStructures().size() );

        assertEquals( 3, resourceTableStore.deleteGroupSetStructures() );
        
        assertEquals( 0, resourceTableStore.getGroupSetStructures().size() );
    }

    @Test
    public void testDataElementCategoryOptionComboName()
    {
        DataElementCategoryOptionComboName name1 = new DataElementCategoryOptionComboName( 1, "A" );
        DataElementCategoryOptionComboName name2 = new DataElementCategoryOptionComboName( 2, "B" );
        DataElementCategoryOptionComboName name3 = new DataElementCategoryOptionComboName( 3, "C" );
        
        resourceTableStore.addDataElementCategoryOptionComboName( name1 );
        resourceTableStore.addDataElementCategoryOptionComboName( name2 );
        resourceTableStore.addDataElementCategoryOptionComboName( name3 );
        
        assertEquals( 3, resourceTableStore.getDataElementCategoryOptionComboNames().size() );
        
        assertEquals( 3, resourceTableStore.deleteDataElementCategoryOptionComboNames() );

        assertEquals( 0, resourceTableStore.getDataElementCategoryOptionComboNames().size() );
    }
}
