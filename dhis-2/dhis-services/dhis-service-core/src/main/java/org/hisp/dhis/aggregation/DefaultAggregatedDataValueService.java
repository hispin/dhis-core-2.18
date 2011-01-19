package org.hisp.dhis.aggregation;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.aggregation.AggregatedDataValueStore;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.aggregation.AggregatedMapValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.dimension.DimensionOption;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.Period;

public class DefaultAggregatedDataValueService
    implements AggregatedDataValueService
{
    private AggregatedDataValueStore aggregatedDataValueStore;

    public void setAggregatedDataValueStore( AggregatedDataValueStore aggregatedDataValueStore )
    {
        this.aggregatedDataValueStore = aggregatedDataValueStore;
    }

    // -------------------------------------------------------------------------
    // AggregatedDataValue
    // -------------------------------------------------------------------------
    
    public Double getAggregatedDataValue( int dataElement, int period, int organisationUnit )
    {
        return aggregatedDataValueStore.getAggregatedDataValue( dataElement, period, organisationUnit );
    }
    
    public Double getAggregatedValue( DataElement dataElement, Period period, OrganisationUnit organisationUnit )
    {
        return aggregatedDataValueStore.getAggregatedDataValue( dataElement.getId(), period.getId(), organisationUnit.getId() );
    }
    
    public Double getAggregatedDataValue( int dataElement, int categoryOptionCombo, int period, int organisationUnit )
    {
        return aggregatedDataValueStore.getAggregatedDataValue( dataElement, categoryOptionCombo, period, organisationUnit );
    }
    
    public Double getAggregatedValue( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo, Period period, OrganisationUnit organisationUnit )
    {
        return aggregatedDataValueStore.getAggregatedDataValue( dataElement.getId(), categoryOptionCombo.getId(), period.getId(), organisationUnit.getId() );
    }

    public Double getAggregatedValue( int dataElement, int categoryOptionCombo, Collection<Integer> periodIds, int organisationUnit )
    {
        return aggregatedDataValueStore.getAggregatedDataValue( dataElement, categoryOptionCombo, periodIds, organisationUnit );
    }
    
    public Double getAggregatedValue( DataElement dataElement, DimensionOption dimensionOption, Period period, OrganisationUnit organisationUnit )
    {
        return aggregatedDataValueStore.getAggregatedDataValue( dataElement, dimensionOption, period, organisationUnit );
    }
    
    public Collection<AggregatedDataValue> getAggregatedDataValues( int dataElementId, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        return aggregatedDataValueStore.getAggregatedDataValues( dataElementId, periodIds, organisationUnitIds );
    }
    
    public int deleteAggregatedDataValues( Collection<Integer> dataElementIds, Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        return aggregatedDataValueStore.deleteAggregatedDataValues( dataElementIds, periodIds, organisationUnitIds );
    }
    
    public int deleteAggregatedDataValues()
    {
        return aggregatedDataValueStore.deleteAggregatedDataValues();
    }

    public StoreIterator<AggregatedDataValue> getAggregateDataValuesAtLevel(OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods)
    {
       return aggregatedDataValueStore.getAggregatedDataValuesAtLevel(orgunit, level, periods);
    }

    @Override
    public int countDataValuesAtLevel( OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods )
    {
        return aggregatedDataValueStore.countDataValuesAtLevel( orgunit, level, periods );
    }

    // -------------------------------------------------------------------------
    // AggregatedDataMapValue
    // -------------------------------------------------------------------------
    
    public Collection<AggregatedMapValue> getAggregatedDataMapValues( int dataElementId, int periodId, int level )
    {
        return aggregatedDataValueStore.getAggregatedDataMapValues( dataElementId, periodId, level );
    }
    
    // -------------------------------------------------------------------------
    // AggregatedIndicatorValue
    // -------------------------------------------------------------------------
    
    public Double getAggregatedIndicatorValue( int indicator, int period, int organisationUnit )
    {
        return aggregatedDataValueStore.getAggregatedIndicatorValue( indicator, period, organisationUnit );
    }
    
    public Double getAggregatedValue( Indicator indicator, Period period, OrganisationUnit organisationUnit )
    {
        return aggregatedDataValueStore.getAggregatedIndicatorValue( indicator.getId(), period.getId(), organisationUnit.getId() );
    }
    
    public Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        return aggregatedDataValueStore.getAggregatedIndicatorValues( periodIds, organisationUnitIds );
    }
    
    public Collection<AggregatedIndicatorValue> getAggregatedIndicatorValues( Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds )
    {
        return aggregatedDataValueStore.getAggregatedIndicatorValues( indicatorIds, periodIds, organisationUnitIds );
    }
    public int deleteAggregatedIndicatorValues( Collection<Integer> indicatorIds, Collection<Integer> periodIds,
        Collection<Integer> organisationUnitIds )
    {
        return aggregatedDataValueStore.deleteAggregatedIndicatorValues( indicatorIds, periodIds, organisationUnitIds );
    }
    public int deleteAggregatedIndicatorValues()
    {
        return aggregatedDataValueStore.deleteAggregatedIndicatorValues();
    }

    @Override
    public StoreIterator<AggregatedIndicatorValue> getAggregateIndicatorValuesAtLevel( OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods )
    {
        return aggregatedDataValueStore.getAggregatedIndicatorValuesAtLevel( orgunit, level, periods );
    }

    @Override
    public int countIndicatorValuesAtLevel( OrganisationUnit orgunit, OrganisationUnitLevel level, Collection<Period> periods )
    {
        return aggregatedDataValueStore.countIndicatorValuesAtLevel( orgunit, level, periods );
    }
// -------------------------------------------------------------------------
    // AggregatedIndicatorMapValue
    // -------------------------------------------------------------------------
    
    public Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, int periodId, int level )
    {
        return aggregatedDataValueStore.getAggregatedIndicatorMapValues( indicatorId, periodId, level );
    }
    
    public Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, int periodId, int level, int organisationUnitId )
    {
        return aggregatedDataValueStore.getAggregatedIndicatorMapValues( indicatorId, periodId, level, organisationUnitId );
    }
    
    public Collection<AggregatedMapValue> getAggregatedIndicatorMapValues( int indicatorId, Collection<Integer> periodIds, int level, int organisationUnitId )
    {
        return aggregatedDataValueStore.getAggregatedIndicatorMapValues( indicatorId, periodIds, level, organisationUnitId );
    }
    
    // -------------------------------------------------------------------------
    // DataValue
    // -------------------------------------------------------------------------
    
    public Collection<DeflatedDataValue> getDeflatedDataValues( int dataElementId, int periodId, Collection<Integer> sourceIds )
    {
        return aggregatedDataValueStore.getDeflatedDataValues( dataElementId, periodId, sourceIds );
    }
    
    public DataValue getDataValue( int dataElementId, int categoryOptionComboId, int periodId, int sourceId )
    {
        return aggregatedDataValueStore.getDataValue( dataElementId, categoryOptionComboId, periodId, sourceId );
    }
    
    public Map<DataElementOperand, String> getDataValueMap( int periodId, int sourceId )
    {
        return aggregatedDataValueStore.getDataValueMap( periodId, sourceId );
    }

}
