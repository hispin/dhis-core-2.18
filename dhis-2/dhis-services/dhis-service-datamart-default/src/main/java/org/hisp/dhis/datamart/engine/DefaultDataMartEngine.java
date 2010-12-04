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

import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_AVERAGE;
import static org.hisp.dhis.dataelement.DataElement.AGGREGATION_OPERATOR_SUM;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_BOOL;
import static org.hisp.dhis.dataelement.DataElement.VALUE_TYPE_INT;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.RandomStringUtils;
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
import org.hisp.dhis.datamart.util.ParserUtil;
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

        state.setMessage( "deleting_existing_aggregated_data" );

        // ---------------------------------------------------------------------
        // Delete existing aggregated data
        // ---------------------------------------------------------------------

        aggregatedDataValueService.deleteAggregatedDataValues( dataElementIds, periodIds, organisationUnitIds );

        aggregatedDataValueService.deleteAggregatedIndicatorValues( indicatorIds, periodIds, organisationUnitIds );

        log.info( "Deleted existing aggregated data: " + TimeUtils.getHMS() );

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
        // Filter and get operands
        // ---------------------------------------------------------------------
        
        Collection<DataElementOperand> nonCalculatedOperands = categoryService.getOperands( nonCalculatedDataElements );
        Collection<DataElementOperand> indicatorOperands = categoryService.populateOperands( getOperandsInIndicators( indicators ) );
        Collection<DataElementOperand> calculatedOperands = categoryService.populateOperands( getOperandsInCalculatedDataElements( calculatedDataElements ) );
        
        Set<DataElementOperand> allOperands = new HashSet<DataElementOperand>();
        allOperands.addAll( nonCalculatedOperands );
        allOperands.addAll( indicatorOperands );
        allOperands.addAll( calculatedOperands );

        final Collection<DataElementOperand> sumIntDataElementOperands = ParserUtil.filterOperands(
            nonCalculatedOperands, VALUE_TYPE_INT, AGGREGATION_OPERATOR_SUM );
        final Collection<DataElementOperand> averageIntDataElementOperands = ParserUtil.filterOperands(
            nonCalculatedOperands, VALUE_TYPE_INT, AGGREGATION_OPERATOR_AVERAGE );
        final Collection<DataElementOperand> sumBoolDataElementOperands = ParserUtil.filterOperands(
            nonCalculatedOperands, VALUE_TYPE_BOOL, AGGREGATION_OPERATOR_SUM );
        final Collection<DataElementOperand> averageBoolDataElementOperands = ParserUtil.filterOperands(
            nonCalculatedOperands, VALUE_TYPE_BOOL, AGGREGATION_OPERATOR_AVERAGE );

        log.info( "Filtered data elements: " + TimeUtils.getHMS() );

        // ---------------------------------------------------------------------
        // Create and trim crosstabtable
        // ---------------------------------------------------------------------

        String key = RandomStringUtils.randomAlphanumeric( 8 );

        if ( crossTabService.validateCrossTabTable( allOperands ) != 0 )
        {
            int excess = crossTabService.validateCrossTabTable( allOperands );

            log.warn( "Cannot crosstabulate since the number of data elements exceeded maximum columns: " + excess );

            state.setMessage( "could_not_export_too_many_data_elements" );

            return 0;
        }

        log.info( "Validated crosstab table: " + TimeUtils.getHMS() );

        state.setMessage( "crosstabulating_data" );

        Collection<Integer> childrenIds = organisationUnitService.getOrganisationUnitHierarchy().getChildren(
            organisationUnitIds );

        Collection<Integer> intersectingPeriodIds = ConversionUtils.getIdentifiers( Period.class, periodService.getIntersectionPeriods( periods ) );
        
        final Collection<DataElementOperand> emptyOperands = crossTabService.populateCrossTabTable(
            allOperands, intersectingPeriodIds, childrenIds, key );
        
        log.info( "Populated crosstab table: " + TimeUtils.getHMS() );

        if ( emptyOperands == null )
        {
           return 0;
        }
        
        crossTabService.trimCrossTabTable( emptyOperands, key );

        log.info( "Trimmed crosstab table: " + TimeUtils.getHMS() );

        // ---------------------------------------------------------------------
        // Data element export
        // ---------------------------------------------------------------------

        state.setMessage( "exporting_data_for_data_elements" );

        if ( sumIntDataElementOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( sumIntDataElementOperands, periods, organisationUnits,
                sumIntAggregator, key );

            log.info( "Exported values for data element operands with sum aggregation operator of type number ("
                + sumIntDataElementOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( averageIntDataElementOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( averageIntDataElementOperands, periods,
                organisationUnits, averageIntAggregator, key );

            log.info( "Exported values for data element operands with average aggregation operator of type number ("
                + averageIntDataElementOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( averageIntDataElementOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( averageIntDataElementOperands, periods,
                organisationUnits, averageIntSingleValueAggregator, key );

            log.info( "Exported values for data element operands with average aggregation operator with single value of type number ("
                    + averageIntDataElementOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( sumBoolDataElementOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( sumBoolDataElementOperands, periods, organisationUnits,
                sumBoolAggregator, key );

            log.info( "Exported values for data element operands with sum aggregation operator of type yes/no ("
                + sumBoolDataElementOperands.size() + "): " + TimeUtils.getHMS() );
        }

        if ( averageBoolDataElementOperands.size() > 0 )
        {
            count += dataElementDataMart.exportDataValues( averageBoolDataElementOperands, periods,
                organisationUnits, averageBoolAggregator, key );

            log.info( "Exported values for data element operands with average aggregation operator of type yes/no ("
                + averageBoolDataElementOperands.size() + "): " + TimeUtils.getHMS() );
        }

        state.setMessage( "exporting_data_for_indicators" );

        // ---------------------------------------------------------------------
        // Indicator export
        // ---------------------------------------------------------------------

        if ( indicators != null && indicators.size() > 0 )
        {
            count += indicatorDataMart.exportIndicatorValues( indicators, periods, organisationUnits, indicatorOperands, key );

            log.info( "Exported values for indicators (" + indicators.size() + "): " + TimeUtils.getHMS() );
        }

        state.setMessage( "exporting_data_for_calculated_data_elements" );

        // ---------------------------------------------------------------------
        // Calculated data element export
        // ---------------------------------------------------------------------

        if ( calculatedDataElements != null && calculatedDataElements.size() > 0 )
        {
            count += calculatedDataElementDataMart.exportCalculatedDataElements( calculatedDataElements, periods, organisationUnits, calculatedOperands, key );

            log.info( "Exported values for calculated data elements (" + calculatedDataElements.size() + "): " + TimeUtils.getHMS() );
        }

        crossTabService.dropCrossTabTable( key );

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
            Set<DataElementOperand> temp = expressionService.getOperandsInExpression( indicator.getNumerator() );
            operands.addAll( temp != null ? temp : new HashSet<DataElementOperand>() );
            
            temp = expressionService.getOperandsInExpression( indicator.getDenominator() );            
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
