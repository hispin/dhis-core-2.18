package org.hisp.dhis.patientdatavalue.aggregation;

import java.util.Date;

import org.hisp.dhis.organisationunit.OrganisationUnit;

public interface PatientDataValueAggregationEngine
{
    String ID = PatientDataValueAggregationEngine.class.getName();
    
    public void aggregate( Date startDate, Date endDate, OrganisationUnit organisationUnit );
}
