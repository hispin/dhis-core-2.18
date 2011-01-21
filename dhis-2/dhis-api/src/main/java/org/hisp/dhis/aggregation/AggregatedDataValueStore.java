package org.hisp.dhis.aggregation;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.dimension.DimensionOption;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

public interface AggregatedDataValueStore
{
    String ID = AggregatedDataValueStore.class.getName();
    
    // ----------------------------------------------------------------------
    // AggregatedDataValue
    // ----------------------------------------------------------------------
    
    /**
     * Gets the total aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement identifier.
     * @param period The Period identifier.
     * @param organisationUnit The OrganisationUnit identifier.
     * @return the aggregated value.
     */
    Double getAggregatedDataValue( int dataElement, int period, int organisationUnit );

    /**
     * Gets the aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement.
     * @param categoryOptionCombo The DataElementCategoryOptionCombo.
     * @param period The Period.
     * @param organisationUnit The OrganisationUnit.
     * @return the aggregated value, or -1 if no value exists.
     */
    Double getAggregatedDataValue( int dataElement, int categoryOptionCombo, int period, int organisationUnit );
    
    /**
     * Gets the total aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement.
     * @param dimensionOptionElement the DimensionOptionElement.
     * @param period The Period.
     * @param organisationUnit The OrganisationUnit.
     * @return the aggregated value.
     */
    Double getAggregatedDataValue( DataElement dataElement, DimensionOption dimensionOption, Period period, OrganisationUnit organisationUnit );
    
    /**
     * Gets the aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement identifier.
     * @param categoryOptionCombo The DataElementCategoryOptionCombo identifier.
     * @param periods The collection of Periods.
     * @param organisationUnit The OrganisationUnit identifier.
     * @return the aggregated value.
     */
    Double getAggregatedDataValue( int dataElement, int categoryOptionCombo, Collection<Integer> periodIds, int organisationUnit );
    
    /**
     * Gets a collection of AggregatedDataValues.
     * 
     * @param dataElementId the DataElement identifier.
     * @param periodIds the collection of Period identifiers.
     * @param organisationUnitIds the collection of OrganisationUnit identifiers.
     * @return a collection of AggregatedDataValues.
     */
    Collection<AggregatedDataValue> getAggregatedDataValues( int dataElementId, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );
    
    /**
     * Deletes AggregatedDataValues registered for the given parameters.
     * 
     * @param dataElementIds a collection of DataElement identifiers.
     * @param periodIds a collection of Period identifiers.
     * @param organisationUnitIds a collection of OrganisationUnit identifiers.
     * @return the number of deleted AggregatedDataValues.
     */
    int deleteAggregatedDataValues( Collection<Integer> dataElementIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds );

    /**
     * Deletes all AggregatedDataValues.
     * 
     * @return the number of deleted AggregatedDataValues.
     * @throws AggregationStoreException
     */
    int deleteAggregatedDataValues();

    /**
     * Returns values for children of an orgunit at a particular level
     * @param orgunit the root organisationunit
     * @param level the level to retrieve values at
     * @param periods the period to retrieve values for
     * @return an iterator type object for retrieving the values
     */
    public StoreIterator<AggregatedDataValue> getAggregatedDataValuesAtLevel(OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods);

    /**
     * Returns count of agg data values for children of an orgunit at a particular level
     * @param orgunit the root organisationunit
     * @param level the level to retrieve values at
     * @param periods the periods to retrieve values for
     * @return an iterator type object for retrieving the values
     */
    public int countDataValuesAtLevel( OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods );

    /**
     * Creates indexes on the aggregateddatavalue and aggregatedindicatorvalue
     * tables.
     * 
     * @param dataElement indicates whether to create an index on aggregateddatavalue.
     * @param indicator indicates whether to create an index on aggregatedindicatorvalue.
     */
    void createIndex( boolean dataElement, boolean indicator );
    
    /**
     * Drops the indexes on the aggregateddatavalue and aggregatedindicatorvalue
     * tables.
     * 
     * @param dataElement indicates whether to drop the index on aggregateddatavalue.
     * @param indicator indicates whether to drop the index on aggregatedindicatorvalue.
     */
    void dropIndex( boolean dataElement, boolean indicator );
    
    // ----------------------------------------------------------------------
    // AggregatedDataMapValue
    // ----------------------------------------------------------------------
    
    /**
     * Retrieves the AggregatedDataMapValues for the given arguments.
     * 
     * @param dataElementId the DataElement identifier.
     * @param periodId the Period identifier.
     * @param level the OrganisationUnit level.
     */
    Collection<AggregatedMapValue> getAggregatedDataMapValues( int dataElementId, int periodId, int level );

    // ----------------------------------------------------------------------
    // AggregatedIndicatorValue
    // ----------------------------------------------------------------------

    /**
     * Gets the aggregated value from the datamart table for the given parameters.
     * 
     * @param indicator The Indicator identifier.
     * @param period The Period identifier.
     * @param organisationUnit The OrganisationUnit identifier.
     * @return the aggregated value, or -1 if no value exists.
     */
    Double getAggregatedIndicatorValue( int indicator, int period, int organisationUnit );

    /**
     * Gets a collection of AggregatedIndicatorValues.
     * 
     * @param periodIds the Period identifiers.
     * @param organisationUnitIds the OrganisationUnit identifiers.
     * @return a collection of AggregatedIndicatorValues.
     */
    Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );
    
    /**
     * Gets a collection of AggregatedIndicatorValues.
     * 
     * @param indicatorIds the Indicator identifiers.
     * @param periodIds the Period identifiers.
     * @param organisationUnitIds the OrganisationUnit identifiers.
     * @return a collection of AggregatedIndicatorValues.
     */
    Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds );
    
    /**
     * Deletes AggregatedIndicatorValue registered for the given parameters.
     * 
     * @param indicatorIds a collection of Indicator identifiers.
     * @param periodIds a collection of Period identifiers.
     * @param organisationUnitIds a collection of OrganisationUnit identifiers.
     * @return the number of deleted AggregatedIndicatorValues.
     */
    int deleteAggregatedIndicatorValues( Collection<Integer> indicatorIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds );
    
    /**
     * Deletes all AggregatedIndicatorValue.
     * 
     * @return the number of deleted AggregatedIndicatorValues.
     * @throws AggregationStoreException
     */
    int deleteAggregatedIndicatorValues();


    /**
     * Returns values for children of an orgunit at a particular level
     * @param orgunit the root organisationunit
     * @param level the level to retrieve values at
     * @param periods the period to retrieve values for
     * @return an iterator type object for retrieving the values
     */
    public StoreIterator<AggregatedIndicatorValue> getAggregatedIndicatorValuesAtLevel(OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods);

    /**
     * Returns count of agg indicator values for children of an orgunit at a particular level
     * @param orgunit the root organisationunit
     * @param level the level to retrieve values at
     * @param periods the periods to retrieve values for
     * @return an iterator type object for retrieving the values
     */
    public int countIndicatorValuesAtLevel( OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods );

    // ----------------------------------------------------------------------
    // AggregatedIndicatorMapValue
    // ----------------------------------------------------------------------
    
    /**
     * Retrieves the AggregatedIndicatorMapValues for the given arguments.
     * 
     * @param indicatorId the Indicator identifier.
     * @param periodId the Period identifier.
     * @param level the OrganisationUnit level.
     */
    Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, int periodId, int level );
    
    /**
     * Retrieves the AggregatedIndicatorMapValues for the given arguments.
     * 
     * @param indicatorId the Indicator identifier.
     * @param periodId the Period identifier.
     * @param level the OrganisationUnit level.
     * @param OrganisationUnitId the id of the organisationUnit.
     */
    Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, int periodId, int level, int organisationUnitId );
    
    /**
     * Retrieves the AggregatedIndicatorMapValues for the given arguments.
     * 
     * @param indicatorId the Indicator identifier.
     * @param periodId the Period identifier.
     * @param level the OrganisationUnit level.
     */
    Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, Collection<Integer> periodIds, int level, int organisationUnitId );
    
    // ----------------------------------------------------------------------
    // DataValue
    // ----------------------------------------------------------------------
    
    /**
     * Gets a Collection of DeflatedDataValues.
     * 
     * @param dataElementId the DataElement identifier.
     * @param periodId the Period identifier.
     * @param sourceIds the Collection of Source identifiers.
     */
    Collection<DeflatedDataValue> getDeflatedDataValues( int dataElementId, int periodId, Collection<Integer> sourceIds );
    
    /**
     * Gets a DataValues. Note that this is a "deflated" data value as the objects
     * in the composite identifier only has its id property populated.
     * 
     * @param dataElementId the DataElement identifier.
     * @param categoryOptionComboId the DataElementCategoryOptionCombo identifier.
     * @param periodId the Period identifier.
     * @param sourceId the Source identifier.
     */
    DataValue getDataValue( int dataElementId, int categoryOptionComboId, int periodId, int sourceId );
    
    /**
     * Gets a Map with entries containing Operand and value for all DataValues registered for the given Period and Source.
     * 
     * @param periodId the Period identifier.
     * @param sourceId the Source identifier.
     * @return map of data values.
     */
    Map<DataElementOperand, String> getDataValueMap( int periodId, int sourceId );
}
