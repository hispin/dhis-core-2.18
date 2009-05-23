package org.hisp.dhis.datamart.calculateddataelement;

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
import static org.hisp.dhis.system.util.MathUtils.calculateExpression;
import static org.hisp.dhis.system.util.MathUtils.getRounded;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.dataelement.CalculatedDataElement;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionComboService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataelement.Operand;
import org.hisp.dhis.datamart.aggregation.dataelement.DataElementAggregator;
import org.hisp.dhis.datamart.crosstab.CrossTabService;
import org.hisp.dhis.jdbc.BatchHandler;
import org.hisp.dhis.jdbc.BatchHandlerFactory;
import org.hisp.dhis.jdbc.batchhandler.AggregatedDataValueBatchHandler;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
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
    
    private DataElementCategoryOptionComboService categoryOptionComboService;

    public void setCategoryOptionComboService( DataElementCategoryOptionComboService categoryOptionComboService )
    {
        this.categoryOptionComboService = categoryOptionComboService;
    }

    // -------------------------------------------------------------------------
    // CalculatedDataElementDataMart implementation
    // -------------------------------------------------------------------------

    public int exportCalculatedDataElements( final Collection<Integer> calculatedDataElementIds, final Collection<Integer> periodIds,
        final Collection<Integer> organisationUnitIds, final Collection<Operand> operands )
    {
        final Collection<Operand> sumOperands = filterOperands( operands, DataElement.AGGREGATION_OPERATOR_SUM );
        final Collection<Operand> averageOperands = filterOperands( operands, DataElement.AGGREGATION_OPERATOR_AVERAGE );
        
        final Map<Operand, Integer> sumOperandIndexMap = crossTabService.getOperandIndexMap( sumOperands );
        final Map<Operand, Integer> averageOperandIndexMap = crossTabService.getOperandIndexMap( averageOperands );
        
        final Collection<DataElement> calculatedDataElements = dataElementService.getDataElements( calculatedDataElementIds );       
        final Collection<Period> periods = periodService.getPeriods( periodIds );
        final Collection<OrganisationUnit> organisationUnits = organisationUnitService.getOrganisationUnits( organisationUnitIds );

        final DataElementCategoryOptionCombo categoryOptionCombo = categoryOptionComboService.getDefaultDataElementCategoryOptionCombo();
        
        final BatchHandler batchHandler = batchHandlerFactory.createBatchHandler( AggregatedDataValueBatchHandler.class );
        
        batchHandler.init();

        int count = 0;
        int level = 0;

        Map<Operand, Double> sumIntValueMap = null;
        Map<Operand, Double> averageIntValueMap = null;
        
        Map<String, Map<Operand, Double>> valueMapMap = null;
        
        Map<Operand, Double> valueMap = null;
        
        CalculatedDataElement calculatedDataElement = null;

        PeriodType periodType = null;
        
        double aggregatedValue = 0.0;
        
        final AggregatedDataValue dataValue = new AggregatedDataValue();

        for ( final OrganisationUnit unit : organisationUnits )
        {
            level = organisationUnitService.getLevelOfOrganisationUnit( unit );
            
            for ( final Period period : periods )
            {
                sumIntValueMap = sumIntAggregator.getAggregatedValues( sumOperandIndexMap, period, unit, level );                
                averageIntValueMap = averageIntAggregator.getAggregatedValues( averageOperandIndexMap, period, unit, level );
                                
                valueMapMap = new HashMap<String, Map<Operand, Double>>( 2 );
                
                valueMapMap.put( DataElement.AGGREGATION_OPERATOR_SUM, sumIntValueMap );
                valueMapMap.put( DataElement.AGGREGATION_OPERATOR_AVERAGE, averageIntValueMap );

                periodType = period.getPeriodType();
                
                for ( final DataElement element : calculatedDataElements )
                {
                    calculatedDataElement = (CalculatedDataElement) element;
                    
                    valueMap = valueMapMap.get( calculatedDataElement.getAggregationOperator() );
                    
                    aggregatedValue = calculateExpression( generateExpression( calculatedDataElement.getExpression().getExpression(), valueMap ) );
                    
                    //TODO improve logic for performance
                    
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

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Collection<Operand> filterOperands( final Collection<Operand> operands, final String aggregationOperator )
    {
        final Collection<Operand> filteredOperands = new ArrayList<Operand>();
        
        for ( final Operand operand : operands )
        {
            final DataElement dataElement = dataElementService.getDataElement( operand.getDataElementId() );
            
            if ( aggregationOperator.equals( dataElement.getAggregationOperator() ) )
            {
                filteredOperands.add( operand );
            }
        }
        
        return filteredOperands;
    }    
}
