package org.hisp.dhis.datamart;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementOperand;

public interface DataMartManager
{
    /**
     * Filters and returns the DataElementOperands with data from the given
     * collection of DataElementOperands.
     * 
     * @param operands the DataElementOperands.
     * @return the DataElementOperands with data.
     */
    Set<DataElementOperand> getOperandsWithData( Set<DataElementOperand> operands );

    Map<DataElementOperand, String> getDataValueMap( int periodId, int sourceId );

    /**
     * Creates indexes on the aggregateddatavalue and aggregatedindicatorvalue
     * tables.
     * 
     * @param dataElement indicates whether to create an index on aggregateddatavalue.
     * @param indicator indicates whether to create an index on aggregatedindicatorvalue.
     */
    void createAggregatedValueIndex( boolean dataElement, boolean indicator );
    
    /**
     * Drops the indexes on the aggregateddatavalue and aggregatedindicatorvalue
     * tables.
     * 
     * @param dataElement indicates whether to drop the index on aggregateddatavalue.
     * @param indicator indicates whether to drop the index on aggregatedindicatorvalue.
     */
    void dropAggregatedValueIndex( boolean dataElement, boolean indicator );
    
    /**
     * Deletes AggregatedDataValues registered for the given parameters.
     * 
     * @param periodIds a collection of Period identifiers.
     */
    void deleteAggregatedDataValues( Collection<Integer> periodIds );

    /**
     * Deletes AggregatedIndicatorValue registered for the given parameters.
     * 
     * @param periodIds a collection of Period identifiers.
     */
    void deleteAggregatedIndicatorValues( Collection<Integer> periodIds );
    
    void createAggregatedOrgUnitValueIndex( boolean dataElement, boolean indicator );
    
    void dropAggregatedOrgUnitValueIndex( boolean dataElement, boolean indicator );

    void deleteAggregatedOrgUnitIndicatorValues( Collection<Integer> periodIds );

    void deleteAggregatedOrgUnitDataValues( Collection<Integer> periodIds );
    
    void createTempAggregatedTables();
    
    void dropTempAggregatedTables();
    
    void copyAggregatedDataValuesFromTemp();
    
    void copyAggregatedIndicatorValuesFromTemp();
    
    void copyAggregatedOrgUnitDataValuesFromTemp();
    
    void copyAggregatedOrgUnitIndicatorValuesFromTemp();
}
