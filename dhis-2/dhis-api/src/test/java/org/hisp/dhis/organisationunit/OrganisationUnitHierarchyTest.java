package org.hisp.dhis.organisationunit;

/*
 * Copyright (c) 2004-2010, University of Oslo
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
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class OrganisationUnitHierarchyTest
{
    @Test
    public void testGetChildrenA()
    {
        List<OrganisationUnitRelationship> relationships = new ArrayList<OrganisationUnitRelationship>();
        
        relationships.add( new OrganisationUnitRelationship( 1, 2 ) );
        relationships.add( new OrganisationUnitRelationship( 1, 3 ) );
        relationships.add( new OrganisationUnitRelationship( 2, 4 ) );
        relationships.add( new OrganisationUnitRelationship( 2, 5 ) );
        relationships.add( new OrganisationUnitRelationship( 2, 6 ) );
        relationships.add( new OrganisationUnitRelationship( 3, 7 ) );
        relationships.add( new OrganisationUnitRelationship( 3, 8 ) );
        relationships.add( new OrganisationUnitRelationship( 3, 9 ) );
        relationships.add( new OrganisationUnitRelationship( 4, 10 ) );
        relationships.add( new OrganisationUnitRelationship( 4, 11 ) );
        relationships.add( new OrganisationUnitRelationship( 4, 12 ) );
        
        OrganisationUnitHierarchy hierarchy = new OrganisationUnitHierarchy( relationships );
        
        testHierarchy( hierarchy );
    }
    
    @Test
    public void testGetChildrenB()
    {
        Map<Integer, Set<Integer>> relationships = new HashMap<Integer, Set<Integer>>();
        
        relationships.put( 1, getSet( 2, 3 ) );
        relationships.put( 2, getSet( 4, 5, 6 ) );
        relationships.put( 3, getSet( 7, 8, 9 ) );
        relationships.put( 4, getSet( 10, 11, 12 ) );

        OrganisationUnitHierarchy hierarchy = new OrganisationUnitHierarchy( relationships );
        
        testHierarchy( hierarchy );
    }
    
    private void testHierarchy( OrganisationUnitHierarchy hierarchy )
    {
        assertEquals( 12, hierarchy.getChildren( 1 ).size() );
        
        assertEquals( 7, hierarchy.getChildren( 2 ).size() );
        assertTrue( hierarchy.getChildren( 2 ).contains( 2 ) );
        assertTrue( hierarchy.getChildren( 2 ).contains( 4 ) );
        assertTrue( hierarchy.getChildren( 2 ).contains( 5 ) );
        assertTrue( hierarchy.getChildren( 2 ).contains( 6 ) );
        assertTrue( hierarchy.getChildren( 2 ).contains( 10 ) );
        assertTrue( hierarchy.getChildren( 2 ).contains( 11 ) );
        assertTrue( hierarchy.getChildren( 2 ).contains( 12 ) );
        
        assertEquals( 4, hierarchy.getChildren( 3 ).size() );
        assertTrue( hierarchy.getChildren( 3 ).contains( 3 ) );
        assertTrue( hierarchy.getChildren( 3 ).contains( 7 ) );
        assertTrue( hierarchy.getChildren( 3 ).contains( 8 ) );
        assertTrue( hierarchy.getChildren( 3 ).contains( 9 ) );

        assertEquals( 4, hierarchy.getChildren( 4 ).size() );
        assertTrue( hierarchy.getChildren( 4 ).contains( 4 ) );
        assertTrue( hierarchy.getChildren( 4 ).contains( 10 ) );
        assertTrue( hierarchy.getChildren( 4 ).contains( 11 ) );
        assertTrue( hierarchy.getChildren( 4 ).contains( 12 ) );

        assertEquals( 1, hierarchy.getChildren( 11 ).size() );
        assertTrue( hierarchy.getChildren( 11 ).contains( 11 ) );
        
        assertFalse( hierarchy.getChildren( 2 ).contains( 3 ) );
        assertFalse( hierarchy.getChildren( 2 ).contains( 8 ) );
    }
    
    private Set<Integer> getSet( Integer... ints )
    {
        Set<Integer> set = new HashSet<Integer>();
        
        for ( Integer i : ints )
        {
            set.add( i );
        }
        
        return set;
    }
}
