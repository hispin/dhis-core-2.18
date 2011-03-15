package org.hisp.dhis.common;

public interface AggregatedValue
{
    int getElementId();
    
    int getPeriodId();
    
    int getOrganisationUnitId();
    
    double getValue();
}
