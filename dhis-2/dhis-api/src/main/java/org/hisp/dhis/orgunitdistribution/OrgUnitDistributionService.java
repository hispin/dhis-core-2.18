package org.hisp.dhis.orgunitdistribution;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;

public interface OrgUnitDistributionService
{
    final String ID = OrgUnitDistributionService.class.getName();
    
    Grid getOrganisationUnitDistribution( OrganisationUnitGroupSet groupSet, OrganisationUnit parent );
}
