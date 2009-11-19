package org.hisp.dhis.datamart;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.aggregation.AggregatedMapValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DeflatedDataValue;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

/**
 * @author Lars Helge Overland
 * @version $Id: DataMartStore.java 6517 2008-11-27 13:32:33Z larshelg $
 */
public interface DataMartStore
{
    String ID = DataMartStore.class.getName();
    
    final double NO_VALUES_REGISTERED = -1;

    // ----------------------------------------------------------------------
    // AggregatedDataValue
    // ----------------------------------------------------------------------
    
    /**
     * Gets the total aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement.
     * @param period The Period.
     * @param organisationUnit The OrganisationUnit.
     * @return the aggregated value.
     */
    Double getTotalAggregatedValue( DataElement dataElement, Period period, OrganisationUnit organisationUnit );

    /**
     * Gets the total aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement.
     * @param categoryOption the DataElementCategoryOption.
     * @param period The Period.
     * @param organisationUnit The OrganisationUnit.
     * @return the aggregated value.
     */
    Double getTotalAggregatedValue( final DataElement dataElement, 
        final DataElementCategoryOption categoryOption, final Period period, final OrganisationUnit organisationUnit );
    
    /**
     * Gets the aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement.
     * @param period The Period.
     * @param organisationUnit The OrganisationUnit.
     * @return the aggregated value, or -1 if no value exists.
     */
    double getAggregatedValue( DataElement dataElement, Period period, OrganisationUnit organisationUnit );
    
    /**
     * Gets the aggregated value from the datamart table for the given parameters.
     * 
     * @param dataElement The DataElement.
     * @param categoryOptionCombo The DataElementCategoryOptionCombo.
     * @param period The Period.
     * @param organisationUnit The OrganisationUnit.
     * @return the aggregated value, or -1 if no value exists.
     */
    double getAggregatedValue( DataElement dataElement, DataElementCategoryOptionCombo categoryOptionCombo, Period period, OrganisationUnit organisationUnit );
    
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

    // ----------------------------------------------------------------------
    // AggregatedIndicatorValue
    // ----------------------------------------------------------------------

    /**
     * Gets the aggregated value from the datamart table for the given parameters.
     * 
     * @param indicator The Indicator.
     * @param period The Period.
     * @param organisationUnit The OrganisationUnit.
     * @return the aggregated value, or -1 if no value exists.
     */
    double getAggregatedValue( Indicator indicator, Period period, OrganisationUnit unit );

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

    // ----------------------------------------------------------------------
    // AggregatedMapValue
    // ----------------------------------------------------------------------
    
    /**
     * Retrieves the AggregatedMapValues for the given arguments.
     * 
     * @param indicatorId the Indicator identifier.
     * @param periodId the Period identifier.
     * @param level the OrganisationUnit level.
     */
    Collection<AggregatedMapValue> getAggregatedMapValues( int indicatorId, int periodId, int level );
    
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
    Collection<DeflatedDataValue> getDeflatedDataValues( final int dataElementId, final int periodId, final Collection<Integer> sourceIds );
    
    /**
     * Gets a DataValues. Note that this is a "deflated" data value as the objects
     * in the composite identifier only has its id property populated.
     * 
     * @param dataElementId the DataElement identifier.
     * @param categoryOptionComboId the DataElementCategoryOptionCombo identifier.
     * @param periodId the Period identifier.
     * @param sourceId the Source identifier.
     */
    DataValue getDataValue( final int dataElementId, final int categoryOptionComboId, final int periodId, final int sourceId );
    
    /**
     * Gets all DataValues for the given DataElement identifer, collection of Period identifers, and collection of Sources
     * grouped by Period.
     * 
     * @param dataElementId the DataElement identifier.
     * @param periodIds the collection of Period identifiers.
     * @param sourceIds the collection of Source identifiers.
     * @return collection of DataValues.
     */
    Collection<DataValue> getDataValues( int dataElementId, Collection<Integer> periodIds, Collection<Integer> sourceIds );
    
    /**
     * Gets a Map with entries containing Operand and value for all DataValues registered for the given Period and Source.
     * 
     * @param periodId the Period identifier.
     * @param sourceId the Source identifier.
     * @return map of data values.
     */
    Map<Operand, String> getDataValueMap( int periodId, int sourceId );

    // ----------------------------------------------------------------------
    // CrossTabDataValue
    // ----------------------------------------------------------------------
    
    /**
     * Gets all CrossTabDataValues for the given collection of period ids and source ids.
     * 
     * @param dataElementIds the dataelement identifiers.
     * @param periodIds the period identifiers.
     * @param sourceIds the source identifiers.
     * @return collection of CrossTabDataValues.
     */
    Collection<CrossTabDataValue> getCrossTabDataValues( Map<Operand, Integer> operandIndexMap, Collection<Integer> periodIds, Collection<Integer> sourceIds );

    /**
     * Gets all CrossTabDataValues for the given collection of period ids and the source id.
     * 
     * @param dataElementIds the dataelement identifiers.
     * @param periodIds the period identifiers.
     * @param sourceId the source identifier.
     * @return collection of CrossTabDataValues.
     */
    Collection<CrossTabDataValue> getCrossTabDataValues( Map<Operand, Integer> operandIndexMap, Collection<Integer> periodIds, int sourceId );

    // ----------------------------------------------------------------------
    // Period
    // ----------------------------------------------------------------------
    
    /**
     * Delets periods of type relative.
     * 
     * @return the number of affected rows.
     */
    int deleteRelativePeriods();
}
