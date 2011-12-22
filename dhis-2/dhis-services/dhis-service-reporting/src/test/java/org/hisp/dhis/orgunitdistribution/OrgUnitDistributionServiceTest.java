package org.hisp.dhis.orgunitdistribution;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.junit.Test;

public class OrgUnitDistributionServiceTest
    extends DhisSpringTest
{
    private OrganisationUnitService organisationUnitService;

    private OrganisationUnitGroupService organisationUnitGroupService;
    
    private OrgUnitDistributionService distributionService;

    @Override
    public void setUpTest()
        throws Exception
    {
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        organisationUnitGroupService = (OrganisationUnitGroupService) getBean( OrganisationUnitGroupService.ID );
        
        distributionService = (OrgUnitDistributionService) getBean( OrgUnitDistributionService.ID );
    }

    @Test
    public void testGetOrganisationUnitsByNameAndGroups()
    {
        OrganisationUnit unitA = createOrganisationUnit( 'A' );
        OrganisationUnit unitB = createOrganisationUnit( 'B', unitA );
        unitA.getChildren().add( unitB );
        OrganisationUnit unitC = createOrganisationUnit( 'C', unitA );
        unitA.getChildren().add( unitC );
        organisationUnitService.addOrganisationUnit( unitA );
        organisationUnitService.addOrganisationUnit( unitB );
        organisationUnitService.addOrganisationUnit( unitC );
        
        OrganisationUnitGroup groupA = createOrganisationUnitGroup( 'A' );
        OrganisationUnitGroup groupB = createOrganisationUnitGroup( 'B' );
        
        groupA.getMembers().add( unitA );
        groupA.getMembers().add( unitB );
        groupB.getMembers().add( unitC );
        
        organisationUnitGroupService.addOrganisationUnitGroup( groupA );
        organisationUnitGroupService.addOrganisationUnitGroup( groupB );
        
        OrganisationUnitGroupSet groupSet = createOrganisationUnitGroupSet( 'A' );
        groupSet.getOrganisationUnitGroups().add( groupA );
        groupSet.getOrganisationUnitGroups().add( groupB );
        
        organisationUnitGroupService.addOrganisationUnitGroupSet( groupSet );
        
        Grid grid = distributionService.getOrganisationUnitDistribution( groupSet, unitA, false );
        assertNotNull( grid );
        assertEquals( 4, grid.getWidth() ); // Including total
        assertEquals( 3, grid.getHeight() ); // Including total
    }   
}
