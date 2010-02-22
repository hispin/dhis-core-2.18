package org.hisp.dhis.caseaggregation;

import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.period.Period;


public interface CaseAggregationMappingService 
{

	String ID = CaseAggregationMappingService.class.getName();
	
	//----------------------------------------------------------------
	// Case Aggregation Mapping
	//----------------------------------------------------------------

	void addCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping );
	
	void updateCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping );
	
	void deleteCaseAggregationMapping( CaseAggregationMapping caseAggregationMapping );
		
	CaseAggregationMapping getCaseAggregationMappingByOptionCombo( DataElement dataElement, DataElementCategoryOptionCombo optionCombo );
	
	int getCaseAggregateValue( OrganisationUnit orgUnit, Period period, CaseAggregationMapping caseAggregationMapping );
	
	CaseAggregationQuery scan( String input );
	
	List<PatientDataValue> getCaseAggregatePatientDataValue( OrganisationUnit orgUnit, Period period,
	        CaseAggregationMapping caseAggregationMapping );
		
}
