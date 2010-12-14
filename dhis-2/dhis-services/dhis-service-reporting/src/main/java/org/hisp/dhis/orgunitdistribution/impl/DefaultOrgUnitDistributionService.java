package org.hisp.dhis.orgunitdistribution.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitGroupNameComparator;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitNameComparator;
import org.hisp.dhis.orgunitdistribution.OrgUnitDistributionService;
import org.hisp.dhis.system.grid.ListGrid;
import org.springframework.transaction.annotation.Transactional;

public class DefaultOrgUnitDistributionService
    implements OrgUnitDistributionService
{
    private static final Comparator<OrganisationUnit> ORGUNIT_COMPARATOR = new OrganisationUnitNameComparator();
    private static final Comparator<OrganisationUnitGroup> ORGUNIT_GROUP_COMPARATOR = new OrganisationUnitGroupNameComparator();
    
    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public Grid getOrganisationUnitDistribution( OrganisationUnitGroupSet groupSet, OrganisationUnit parent )
    {
        Grid grid = new ListGrid();
        grid.nextRow();
        
        List<OrganisationUnit> units = new ArrayList<OrganisationUnit>( parent.getChildren() );
        List<OrganisationUnitGroup> groups = new ArrayList<OrganisationUnitGroup>( groupSet.getOrganisationUnitGroups() );
        
        Collections.sort( units, ORGUNIT_COMPARATOR );
        Collections.sort( groups, ORGUNIT_GROUP_COMPARATOR );
        
        grid.addValue( "" ); // First header row column is empty
        
        for ( OrganisationUnitGroup group : groups ) // Header row
        {
            grid.addValue( group.getName() );
        }
        
        for ( OrganisationUnit unit : units ) // Rows
        {            
            grid.nextRow();
            grid.addValue( unit.getName() );
            
            Collection<OrganisationUnit> subTree = organisationUnitService.getOrganisationUnitWithChildren( unit.getId() ); 
            
            for ( OrganisationUnitGroup group : groups ) // Columns
            {
                Collection<OrganisationUnit> result = CollectionUtils.intersection( subTree, group.getMembers() );
                
                grid.addValue( result != null ? String.valueOf( result.size() ) : String.valueOf( 0 ) );
            }
        }
        
        return grid;
    }
}
