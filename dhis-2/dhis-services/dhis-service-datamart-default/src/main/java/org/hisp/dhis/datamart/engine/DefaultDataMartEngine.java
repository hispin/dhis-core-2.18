package org.hisp.dhis.datamart.engine;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.aggregation.AggregatedDataValueService;
import org.hisp.dhis.common.ProcessState;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.datamart.calculateddataelement.CalculatedDataElementDataMart;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.datamart.dataelement.DataElementDataMart;
import org.hisp.dhis.datamart.indicator.IndicatorDataMart;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.util.ConversionUtils;
import org.hisp.dhis.system.util.TimeUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author Lars Helge Overland
 */
public class DefaultDataMartEngine
    implements DataMartEngine
{
    private static final Log log = LogFactory.getLog( DefaultDataMartEngine.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    protected AggregationCache aggregationCache;

    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }

    private AggregatedDataValueService aggregatedDataValueService;

    public void setAggregatedDataValueService( AggregatedDataValueService aggregatedDataValueService )
    {
        this.aggregatedDataValueService = aggregatedDataValueService;
    }

    private CrossTabService crossTabService;

    public void setCrossTabService( CrossTabService crossTabService )
    {
        this.crossTabService = crossTabService;
    }

    private DataElementDataMart dataElementDataMart;

    public void setDataElementDataMart( DataElementDataMart dataElementDataMart )
    {
        this.dataElementDataMart = dataElementDataMart;
    }

    private IndicatorDataMart indicatorDataMart;

    public void setIndicatorDataMart( IndicatorDataMart indicatorDataMart )
    {
        this.indicatorDataMart = indicatorDataMart;
    }

    private CalculatedDataElementDataMart calculatedDataElementDataMart;

    public void setCalculatedDataElementDataMart( CalculatedDataElementDataMart calculatedDataElementDataMart )
    {
        this.calculatedDataElementDataMart = calculatedDataElementDataMart;
    }

    private DataElementAggregator sumIntAggregator;

    public void setSumIntAggregator( DataElementAggregator sumIntDataElementAggregator )
    {
        this.sumIntAggregator = sumIntDataElementAggregator;
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

    private DataElementAggregator sumBoolAggregator;

    public void setSumBoolAggregator( DataElementAggregator sumBooleanDataElementAggregator )
    {
        this.sumBoolAggregator = sumBooleanDataElementAggregator;
    }

    private DataElementAggregator averageBoolAggregator;

    public void setAverageBoolAggregator( DataElementAggregator averageBooleanDataElementAggregator )
    {
        this.averageBoolAggregator = averageBooleanDataElementAggregator;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private ExpressionService expressionService;

    public void setExpressionService( ExpressionService expressionService )
    {
        this.expressionService = expressionService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // DataMartEngine implementation
    // -------------------------------------------------------------------------

    @Transactional
    public int export( Collection<Integer> dataElementIds, Collection<Integer> indicatorIds,
        Collection<Integer> periodIds, Collection<Integer> organisationUnitIds, ProcessState state )
    {
        int count = 0;

        TimeUtils.start();

        // ---------------------------------------------------------------------
        // Get objects
        // ---------------------------------------------------------------------

        Collection<Indicator> indicators = indicatorService.getIndicators( indicatorIds );
        Collection<Period> periods = periodService.getPeriods( periodIds );
        Collection<OrganisationUnit> organisationUnits = organisationUnitService.getOrganisationUnits( organisationUnitIds );
        Collection<CalculatedDataElement> calculatedDataElements = dataElementService.getCalculatedDataElements( dataElementIds );
        Collection<DataElement> nonCalculatedDataElements = dataElementService.getDataElements( dataElementIds );
        nonCalculatedDataElements.removeAll( calculatedDataElements );

        // ---------------------------------------------------------------------
        // Explode indicator expressions
        // ---------------------------------------------------------------------

        for ( Indicator indicator : indicators )
        {
            indicator.setExplodedNumerator( expressionService.explodeExpression( indicator.getNumerator() ) );
            indicator.setExplodedDenominator( expressionService.explodeExpression( indicator.getDenominator() ) );
        }
        
        // ---------------------------------------------------------------------
        // Get operands
        // ---------------------------------------------------------------------
        
        Collection<DataElementOperand> nonCalculatedOperands = categoryService.getOperands( nonCalculatedDataElements );
        Collection<DataElementOperand> indicatorOperands = categoryService.populateOperands( getOperandsInIndicators( indicators ) );
        Collection<DataElementOperand> calculatedOperands = categoryService.populateOperands( getOperandsInCalculatedDataElements( calculatedDataElements ) );
        
        Set<DataElementOperand> allOperands = new HashSet<DataElementOperand>();
        allOperands.addAll( nonCalculatedOperands );
        allOperands.addAll( indicatorOperands );
        allOperands.addAll( calculatedOperands );

        log.info( "Filtered data elements, number of operands: " + allOperands.size() + ", " + TimeUtils.getHMS() );

        // ---------------------------------------------------------------------
        // Create and trim crosstabtable
        // ---------------------------------------------------------------------

        state.setMessage( "crosstabulating_data" );

        Collection<Integer> childrenIds = organisationUnitService.getOrganisationUnitHierarchy().getChildren( organisationUnitIds );
        Collection<Integer> intersectingPeriodIds = ConversionUtils.getIdentifiers( Period.class, periodService.getIntersectionPeriods( periods ) );

        List<String> keys = crossTabService.populateCrossTabTable( allOperands, intersectingPeriodIds, childrenIds );
        
        if ( CollectionUtils.isEmpty( allOperands ) || CollectionUtils.isEmpty( keys ) )
        {
            return 0;
        }

        log.info( "Number of crosstab tables: " + keys.size() + ", number of operands with data: " + allOperands.size() + ", " + TimeUtils.getHMS() );

        // ---------------------------------------------------------------------
        // Remove operands without data
        // ---------------------------------------------------------------------

        nonCalculatedOperands.retainAll( allOperands );
        indicatorOperands.retainAll( allOperands );
        calculatedOperands.retainAll( allOperands );

        // ---------------------------------------------------------------------
        // Delete existing aggregated data
        // ---------------------------------------------------------------------

        state.setMessage( "deleting_existing_aggregated_data" );

        aggregatedDataValueService.deleteAggregatedDataValues( dataElementIds, periodIds, organisationUnitIds );

        aggregatedDataValueService.deleteAggregatedIndicatorValues( indicatorIds, periodIds, organisationUnitIds );

        log.info( "Deleted existing aggregated data: " + TimeUtils.getHMS() );
        
        // ---------------------------------------------------------------------
        // Data element export
        // ---------------------------------------------------------------------

        state.setMessage( "exporting_data_for_data_elements" );

        if ( nonCalculatedOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( nonCalculatedOperands, periods, organisationUnits, sumIntAggregator, keys );

            log.info( "Exported values for data element operands with sum aggregation operator of type number: " + TimeUtils.getHMS() );
            
            count += dataElementDataMart.exportDataValues( nonCalculatedOperands, periods, organisationUnits, averageIntAggregator, keys );

            log.info( "Exported values for data element operands with average aggregation operator of type number: " + TimeUtils.getHMS() );
            
            count += dataElementDataMart.exportDataValues( nonCalculatedOperands, periods, organisationUnits, averageIntSingleValueAggregator, keys );

            log.info( "Exported values for data element operands with average aggregation operator with single value of type number: " + TimeUtils.getHMS() );
            
            count += dataElementDataMart.exportDataValues( nonCalculatedOperands, periods, organisationUnits, sumBoolAggregator, keys );

            log.info( "Exported values for data element operands with sum aggregation operator of type yes/no: " + TimeUtils.getHMS() );
            
            count += dataElementDataMart.exportDataValues( nonCalculatedOperands, periods, organisationUnits, averageBoolAggregator, keys );

            log.info( "Exported values for data element operands with average aggregation operator of type yes/no: " + TimeUtils.getHMS() );
        }

        state.setMessage( "exporting_data_for_indicators" );

        // ---------------------------------------------------------------------
        // Indicator export
        // ---------------------------------------------------------------------

        if ( indicators != null && indicators.size() > 0 )
        {
            count += indicatorDataMart.exportIndicatorValues( indicators, periods, organisationUnits, indicatorOperands, keys );

            log.info( "Exported values for indicators: " + TimeUtils.getHMS() );
        }

        state.setMessage( "exporting_data_for_calculated_data_elements" );

        // ---------------------------------------------------------------------
        // Calculated data element export
        // ---------------------------------------------------------------------

        if ( calculatedDataElements != null && calculatedDataElements.size() > 0 )
        {
            count += calculatedDataElementDataMart.exportCalculatedDataElements( calculatedDataElements, periods, organisationUnits, calculatedOperands, keys );

            log.info( "Exported values for calculated data elements: " + TimeUtils.getHMS() );
        }

        for ( String key : keys )
        {
            crossTabService.dropCrossTabTable( key );
        }

        log.info( "Export process completed: " + TimeUtils.getHMS() );

        TimeUtils.stop();

        aggregationCache.clearCache();

        return count;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Set<DataElementOperand> getOperandsInIndicators( Collection<Indicator> indicators )
    {
        final Set<DataElementOperand> operands = new HashSet<DataElementOperand>();
        
        for ( Indicator indicator : indicators )
        {
            Set<DataElementOperand> temp = expressionService.getOperandsInExpression( indicator.getExplodedNumerator() );
            operands.addAll( temp != null ? temp : new HashSet<DataElementOperand>() );
            
            temp = expressionService.getOperandsInExpression( indicator.getExplodedDenominator() );            
            operands.addAll( temp != null ? temp : new HashSet<DataElementOperand>() );
        }
        
        return operands;
    }
    
    private Set<DataElementOperand> getOperandsInCalculatedDataElements( final Collection<CalculatedDataElement> calculatedDataElements )
    {
        final Set<DataElementOperand> operands = new HashSet<DataElementOperand>();
        
        for ( final CalculatedDataElement calculatedDataElement : calculatedDataElements )
        {
            if ( calculatedDataElement != null && calculatedDataElement.getExpression() != null )
            {
                Set<DataElementOperand> temp = expressionService.getOperandsInExpression( calculatedDataElement.getExpression().getExpression() );
                operands.addAll( temp != null ? temp : new HashSet<DataElementOperand>() );
            }
        }
        
        return operands;
    }
}
