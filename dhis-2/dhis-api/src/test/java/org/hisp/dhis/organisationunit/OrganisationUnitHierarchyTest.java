package org.hisp.dhis.organisationunit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class OrganisationUnitHierarchyTest
{
    @Test
    public void testGetChildren()
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
        assertTrue( hierarchy.getChildren( 2 ).contains( 4 ) );
        assertTrue( hierarchy.getChildren( 2 ).contains( 10 ) );
        assertTrue( hierarchy.getChildren( 2 ).contains( 11 ) );
        assertTrue( hierarchy.getChildren( 2 ).contains( 12 ) );

        assertEquals( 1, hierarchy.getChildren( 11 ).size() );
        assertTrue( hierarchy.getChildren( 11 ).contains( 11 ) );
    }
}
