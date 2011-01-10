package org.hisp.dhis.datamart.calculateddataelement;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import static org.hisp.dhis.datamart.util.ParserUtil.generateExpression;
import static org.hisp.dhis.system.util.MathUtils.calculateExpression;
import static org.hisp.dhis.system.util.MathUtils.getRounded;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.jdbc.batchhandler.AggregatedDataValueBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitHierarchy;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DefaultCalculatedDataElementDataMart
    implements CalculatedDataElementDataMart
{
    private static final int DECIMALS = 1;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataElementAggregator sumIntAggregator;

    public void setSumIntAggregator( DataElementAggregator sumIntAggregator )
    {
        this.sumIntAggregator = sumIntAggregator;
    }

    private DataElementAggregator averageIntAggregator;

    public void setAverageIntAggregator( DataElementAggregator averageIntDataElementAggregator )
    {
        this.averageIntAggregator = averageIntDataElementAggregator;
    }

    private DataElementAggregator averageIntSingleValueAggregator;

    public void setAverageIntSingleValueAggregator( DataElementAggregator averageIntSingleValueAggregator )
    {
        this.averageIntSingleValueAggregator = averageIntSingleValueAggregator;
    }
    
    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private AggregationCache aggregationCache;

    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }    

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
    }

    // -------------------------------------------------------------------------
    // CalculatedDataElementDataMart implementation
    // -------------------------------------------------------------------------

    public int exportCalculatedDataElements( final Collection<CalculatedDataElement> calculatedDataElements, final Collection<Period> periods,
        final Collection<OrganisationUnit> organisationUnits, final Collection<DataElementOperand> operands, String key )
    {
        final DataElementCategoryOptionCombo categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        final BatchHandler<AggregatedDataValue> batchHandler = batchHandlerFactory.createBatchHandler( AggregatedDataValueBatchHandler.class ).init();

        final OrganisationUnitHierarchy hierarchy = organisationUnitService.getOrganisationUnitHierarchy().prepareChildren( organisationUnits );
        
        int count = 0;

        CalculatedDataElement calculatedDataElement = null;

        double aggregatedValue = 0.0;
        
        final AggregatedDataValue dataValue = new AggregatedDataValue();

        for ( final Period period : periods )
        {
            final PeriodType periodType = period.getPeriodType();

            final Collection<DataElementOperand> sumOperands = sumIntAggregator.filterOperands( operands, periodType );
            final Collection<DataElementOperand> averageOperands = averageIntAggregator.filterOperands( operands, periodType );
            final Collection<DataElementOperand> averageSingleValueOperands = averageIntSingleValueAggregator.filterOperands( operands, periodType );

            for ( final OrganisationUnit unit : organisationUnits )
            {
                final int level = aggregationCache.getLevelOfOrganisationUnit( unit.getId() );
                
                final Map<DataElementOperand, Double> sumIntValueMap = sumIntAggregator.getAggregatedValues( sumOperands, period, unit, level, hierarchy, key );                
                final Map<DataElementOperand, Double> averageIntValueMap = averageIntAggregator.getAggregatedValues( averageOperands, period, unit, level, hierarchy, key );
                final Map<DataElementOperand, Double> averageIntSingleValueMap = averageIntSingleValueAggregator.getAggregatedValues( averageSingleValueOperands, period, unit, level, hierarchy, key );
                
                final Map<DataElementOperand, Double> valueMap = new HashMap<DataElementOperand, Double>( sumIntValueMap );
                valueMap.putAll( averageIntValueMap );
                valueMap.putAll( averageIntSingleValueMap );
                
                for ( final DataElement element : calculatedDataElements )
                {
                    calculatedDataElement = (CalculatedDataElement) element;
                    
                    aggregatedValue = calculateExpression( generateExpression( calculatedDataElement.getExpression().getExpression(), valueMap ) );
                    
                    dataValue.clear();
                    
                    dataValue.setDataElementId( calculatedDataElement.getId() );
                    dataValue.setCategoryOptionComboId( categoryOptionCombo.getId() );
                    dataValue.setPeriodId( period.getId() );
                    dataValue.setPeriodTypeId( periodType.getId() );
                    dataValue.setOrganisationUnitId( unit.getId() );
                    dataValue.setLevel( level );
                    dataValue.setValue( getRounded( aggregatedValue, DECIMALS ) );

                    batchHandler.addObject( dataValue );
                    
                    count++;                    
                }
            }
        }

        batchHandler.flush();
        
        return count;
    }
}
