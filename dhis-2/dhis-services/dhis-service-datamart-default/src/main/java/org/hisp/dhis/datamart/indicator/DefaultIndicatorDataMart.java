package org.hisp.dhis.datamart.indicator;

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

import static org.hisp.dhis.datamart.util.ParserUtil.generateExpression;
import static org.hisp.dhis.options.SystemSettingManager.KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART;
import static org.hisp.dhis.system.util.DateUtils.DAYS_IN_YEAR;
import static org.hisp.dhis.system.util.MathUtils.calculateExpression;
import static org.hisp.dhis.system.util.MathUtils.getRounded;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.aggregation.cache.AggregationCache;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.jdbc.batchhandler.AggregatedIndicatorValueBatchHandler;
import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.DateUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: DefaultIndicatorDataMart.java 6069 2008-10-28 17:31:02Z larshelg $
 */
public class DefaultIndicatorDataMart
    implements IndicatorDataMart
{
    private static final int DECIMALS = 1;
    
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private BatchHandlerFactory batchHandlerFactory;

    public void setBatchHandlerFactory( BatchHandlerFactory batchHandlerFactory )
    {
        this.batchHandlerFactory = batchHandlerFactory;
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

    private CrossTabService crossTabService;

    public void setCrossTabService( CrossTabService crossTabService )
    {
        this.crossTabService = crossTabService;
    }    

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private AggregationCache aggregationCache;

    public void setAggregationCache( AggregationCache aggregationCache )
    {
        this.aggregationCache = aggregationCache;
    }

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }
    
    // -------------------------------------------------------------------------
    // IndicatorDataMart implementation
    // -------------------------------------------------------------------------
    
    public int exportIndicatorValues( final Collection<Integer> indicatorIds, final Collection<Integer> periodIds, 
        final Collection<Integer> organisationUnitIds, final Collection<DataElementOperand> operands )
    {
        final Collection<DataElementOperand> sumOperands = filterOperands( operands, DataElement.AGGREGATION_OPERATOR_SUM );
        final Collection<DataElementOperand> averageOperands = filterOperands( operands, DataElement.AGGREGATION_OPERATOR_AVERAGE );
        
        final Map<DataElementOperand, Integer> sumOperandIndexMap = crossTabService.getOperandIndexMap( sumOperands );
        final Map<DataElementOperand, Integer> averageOperandIndexMap = crossTabService.getOperandIndexMap( averageOperands );
        
        final Collection<Indicator> indicators = indicatorService.getIndicators( indicatorIds );        
        final Collection<Period> periods = periodService.getPeriods( periodIds );
        final Collection<OrganisationUnit> organisationUnits = organisationUnitService.getOrganisationUnits( organisationUnitIds );

        final BatchHandler<AggregatedIndicatorValue> batchHandler = batchHandlerFactory.createBatchHandler( AggregatedIndicatorValueBatchHandler.class );

        batchHandler.init();
        
        int count = 0;
        int level = 0;
        
        Map<DataElementOperand, Double> sumIntValueMap = null;
        Map<DataElementOperand, Double> averageIntValueMap = null;
        
        Map<String, Map<DataElementOperand, Double>> valueMapMap = null;
        
        Map<DataElementOperand, Double> numeratorValueMap = null;
        Map<DataElementOperand, Double> denominatorValueMap = null;
        
        PeriodType periodType = null;
        
        double numeratorValue = 0.0;
        double denominatorValue = 0.0;
        
        double annualizationFactor = 0.0;
        double factor = 0.0;
        double aggregatedValue = 0.0;
        double annualizedFactor = 0.0;
        
        final boolean omitZeroNumerator = (Boolean) systemSettingManager.getSystemSetting( KEY_OMIT_INDICATORS_ZERO_NUMERATOR_DATAMART, false );
        
        final AggregatedIndicatorValue indicatorValue = new AggregatedIndicatorValue();
        
        for ( final OrganisationUnit unit : organisationUnits )
        {
            level = aggregationCache.getLevelOfOrganisationUnit( unit.getId() );
            
            for ( final Period period : periods )
            {
                sumIntValueMap = sumIntAggregator.getAggregatedValues( sumOperandIndexMap, period, unit, level );                
                averageIntValueMap = averageIntAggregator.getAggregatedValues( averageOperandIndexMap, period, unit, level );
                
                valueMapMap = new HashMap<String, Map<DataElementOperand, Double>>( 2 );
                
                valueMapMap.put( DataElement.AGGREGATION_OPERATOR_SUM, sumIntValueMap );
                valueMapMap.put( DataElement.AGGREGATION_OPERATOR_AVERAGE, averageIntValueMap );

                periodType = period.getPeriodType();
                
                for ( final Indicator indicator : indicators )
                {
                    // ---------------------------------------------------------
                    // Numerator
                    // ---------------------------------------------------------

                    numeratorValueMap = valueMapMap.get( indicator.getNumeratorAggregationOperator() );
                    
                    numeratorValue = calculateExpression( generateExpression( indicator.getNumerator(), numeratorValueMap ) );
                    
                    // ---------------------------------------------------------
                    // Denominator
                    // ---------------------------------------------------------

                    denominatorValueMap = valueMapMap.get( indicator.getDenominatorAggregationOperator() );
                    
                    denominatorValue = calculateExpression( generateExpression( indicator.getDenominator(), denominatorValueMap ) );

                    // ---------------------------------------------------------
                    // AggregatedIndicatorValue
                    // ---------------------------------------------------------

                    if ( denominatorValue != 0 && !( omitZeroNumerator && numeratorValue == 0 ) )
                    {
                        annualizationFactor = getAnnualizationFactor( indicator, period );
                        
                        factor = indicator.getIndicatorType().getFactor();
                        
                        aggregatedValue = ( numeratorValue / denominatorValue ) * factor * annualizationFactor;
                        
                        annualizedFactor = factor * annualizationFactor;

                        indicatorValue.clear();
                        
                        indicatorValue.setIndicatorId( indicator.getId() );
                        indicatorValue.setPeriodId( period.getId() );
                        indicatorValue.setPeriodTypeId( periodType.getId() );
                        indicatorValue.setOrganisationUnitId( unit.getId() );
                        indicatorValue.setLevel( level );
                        indicatorValue.setAnnualized( getAnnualizationString( indicator.getAnnualized() ) );
                        indicatorValue.setFactor( annualizedFactor );
                        indicatorValue.setValue( getRounded( aggregatedValue, DECIMALS ) );
                        indicatorValue.setNumeratorValue( getRounded( numeratorValue, DECIMALS ) );
                        indicatorValue.setDenominatorValue( getRounded( denominatorValue, DECIMALS ) );
                        
                        batchHandler.addObject( indicatorValue );
                        
                        count++;
                    }
                }
            }
        }
        
        batchHandler.flush();
        
        return count;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<DataElementOperand> filterOperands( final Collection<DataElementOperand> operands, final String aggregationOperator )
    {
        final Collection<DataElementOperand> filteredOperands = new ArrayList<DataElementOperand>();
        
        for ( final DataElementOperand operand : operands )
        {
            final DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
            
            if ( aggregationOperator.equals( dataElement.getAggregationOperator() ) )
            {
                filteredOperands.add( operand );
            }
        }
        
        return filteredOperands;
    }
    
    private double getAnnualizationFactor( final Indicator indicator, final Period period )
    {
        double factor = 1.0;
        
        if ( indicator.getAnnualized() != null && indicator.getAnnualized() )
        {
            final int daysInPeriod = DateUtils.daysBetween( period.getStartDate(), period.getEndDate() ) + 1;
            
            factor = DAYS_IN_YEAR / daysInPeriod;
        }
        
        return factor;
    }
    
    private String getAnnualizationString( final Boolean annualized )
    {
        return ( annualized == null || !annualized ) ? FALSE : TRUE;
    }
}
