package org.hisp.dhis.caseaggregation;

import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataentryform.DataEntryFormStore;
import org.hisp.dhis.patientdatavalue.PatientDataValue;

public interface CaseAggregationMappingStore
{
    String ID = CaseAggregationMappingStore.class.getName();

    // ----------------------------------------------------------------
    // Case Aggregation Mapping
    // ----------------------------------------------------------------

    void addCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping );

    void updateCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping );

    void deleteCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping );

    CaseAggregationMapping getCaseAggregationMappingByOptionCombo( DataElement dataElement,
        DataElementCategoryOptionCombo optionCombo );
    
    int executeMappingQuery( String query );
    
    List<PatientDataValue> executeMappingQueryForListPatientDataValue( String query );
}
