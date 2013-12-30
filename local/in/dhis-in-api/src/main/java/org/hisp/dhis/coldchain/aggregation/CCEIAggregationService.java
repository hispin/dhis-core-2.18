package org.hisp.dhis.coldchain.aggregation;

import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

public interface CCEIAggregationService
{
    String ID = CCEIAggregationService.class.getName();  
    
    String getQueryTemplate( String lookupName, Map<String,String> params );
    
    Map<String, Integer> calculateStorageCapacityData( DataElement dataElement, Set<OrganisationUnit> orgUnits, Set<OrganisationUnitGroup> orgUnitGroups );
    
    String importData( Map<String, Integer> aggregationResultMap, Period period );
    
}
