package org.hisp.dhis.aggregation;

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

import static junit.framework.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id: AggregationServiceTest.java 5942 2008-10-16 15:44:57Z larshelg $
 */
public class AggregationServiceTest
    extends DhisTest
{
    private final String T = "true";
    private final String F = "false";
    
    private AggregationService aggregationService;
    
    private DataElementCategoryCombo categoryCombo;
    
    private DataElementCategoryOptionCombo categoryOptionCombo;

    private Collection<Integer> dataElementIds;
    private Collection<Integer> periodIds;
    private Collection<Integer> organisationUnitIds;
    
    private DataElement dataElementA;
    private DataElement dataElementB;
    
    private DataSet dataSet;
    
    private Period periodA;
    private Period periodB;
    private Period periodC;
    
    private OrganisationUnit unitA;
    private OrganisationUnit unitB;
    private OrganisationUnit unitC;
    private OrganisationUnit unitD;
    private OrganisationUnit unitE;
    private OrganisationUnit unitF;
    private OrganisationUnit unitG;
    private OrganisationUnit unitH;
    private OrganisationUnit unitI;    

    private Date mar01 = getDate( 2005, 3, 1 );
    private Date mar31 = getDate( 2005, 3, 31 );
    private Date apr01 = getDate( 2005, 4, 1 );
    private Date apr30 = getDate( 2005, 4, 30 );
    private Date may01 = getDate( 2005, 5, 1 );
    private Date may31 = getDate( 2005, 5, 31 );
    
    @Override
    public void setUpTest()
    {
        aggregationService = (AggregationService) getBean( AggregationService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        indicatorService = (IndicatorService) getBean( IndicatorService.ID );

        dataSetService = (DataSetService) getBean( DataSetService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        dataValueService = (DataValueService) getBean( DataValueService.ID );

        expressionService = (ExpressionService) getBean( ExpressionService.ID );
        
        categoryCombo = categoryService.getDataElementCategoryComboByName( DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME );
        
        categoryOptionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        
        // ---------------------------------------------------------------------
        // Setup identifier Collections
        // ---------------------------------------------------------------------

        dataElementIds = new HashSet<Integer>();
        periodIds = new HashSet<Integer>();
        organisationUnitIds = new HashSet<Integer>();
        
        // ---------------------------------------------------------------------
        // Setup DataElements
        // ---------------------------------------------------------------------

        dataElementA = createDataElement( 'A', DataElement.VALUE_TYPE_INT, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );
        dataElementB = createDataElement( 'B', DataElement.VALUE_TYPE_BOOL, DataElement.AGGREGATION_OPERATOR_SUM, categoryCombo );

        dataElementIds.add( dataElementService.addDataElement( dataElementA ) );
        dataElementIds.add( dataElementService.addDataElement( dataElementB ) );

        // ---------------------------------------------------------------------
        // Setup DataSets (to get correct PeriodType for DataElements)
        // ---------------------------------------------------------------------

        dataSet = createDataSet( 'A', new MonthlyPeriodType() );
        dataSet.getDataElements().add( dataElementA );
        dataSet.getDataElements().add( dataElementB );
        dataSetService.addDataSet( dataSet );
        dataElementA.getDataSets().add( dataSet );
        dataElementB.getDataSets().add( dataSet );
        dataElementService.updateDataElement( dataElementA );
        dataElementService.updateDataElement( dataElementB );
        
        // ---------------------------------------------------------------------
        // Setup Periods
        // ---------------------------------------------------------------------
        
        PeriodType monthly = new MonthlyPeriodType();
        
        periodA = createPeriod( monthly, mar01, mar31 );
        periodB = createPeriod( monthly, apr01, apr30 );
        periodC = createPeriod( monthly, may01, may31 );
        
        periodIds.add( periodService.addPeriod( periodA ) );
        periodIds.add( periodService.addPeriod( periodB ) );
        periodIds.add( periodService.addPeriod( periodC ) );
        
        // ---------------------------------------------------------------------
        // Setup OrganisationUnits
        // ---------------------------------------------------------------------

        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B', unitA );
        unitC = createOrganisationUnit( 'C', unitA );
        unitD = createOrganisationUnit( 'D', unitB );
        unitE = createOrganisationUnit( 'E', unitB );
        unitF = createOrganisationUnit( 'F', unitB );
        unitG = createOrganisationUnit( 'G', unitF);
        unitH = createOrganisationUnit( 'H', unitF );
        unitI = createOrganisationUnit( 'I' );

        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitA ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitB ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitC ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitD ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitE ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitF ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitG ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitH ) );
        organisationUnitIds.add( organisationUnitService.addOrganisationUnit( unitI ) );
        
        // ---------------------------------------------------------------------
        // Setup DataValues
        // ---------------------------------------------------------------------

        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitC, "90", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitD, "10", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitE, "35", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitF, "25", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitG, "20", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodA, unitH, "60", categoryOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitC, "70", categoryOptionCombo ) );        
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitD, "40", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitE, "65", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitF, "55", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitG, "20", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodB, unitH, "15", categoryOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitC, "95", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitD, "40", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitE, "45", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitF, "30", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitG, "50", categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementA, periodC, unitH, "70", categoryOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitC, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitD, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitE, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitF, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitG, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodA, unitH, T, categoryOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitC, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitD, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitE, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitF, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitG, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodB, unitH, T, categoryOptionCombo ) );
        
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitC, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitD, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitE, F, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitF, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitG, T, categoryOptionCombo ) );
        dataValueService.addDataValue( createDataValue( dataElementB, periodC, unitH, T, categoryOptionCombo ) );
        
        aggregationService.clearCache();
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    @Test
    public void sumIntDataElement()
    {
        assertEquals( 90.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, mar31, unitC ) );
        assertEquals( 105.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, mar31, unitF ) );
        assertEquals( 150.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, mar31, unitB ) );

        assertEquals( 255.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, may31, unitC ) );
        assertEquals( 345.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, may31, unitF ) );
        assertEquals( 580.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, may31, unitB ) );
    }
    
    @Test
    public void sumBoolDataElement()
    {
        assertEquals( 1.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, mar31, unitC ) );
        assertEquals( 2.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, mar31, unitF ) );
        assertEquals( 3.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, mar31, unitB ) );

        assertEquals( 2.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, may31, unitC ) );
        assertEquals( 7.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, may31, unitF ) );
        assertEquals( 10.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, may31, unitB ) );
    }
    
    @Test
    public void averageIntDataElement()
    {
        dataElementA.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );
        dataElementService.updateDataElement( dataElementA );
        
        assertEquals( 90.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, mar31, unitC ) );
        assertEquals( 105.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, mar31, unitF ) );
        assertEquals( 150.0, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, mar31, unitB ) );

        assertEquals( 85.2, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, may31, unitC ), 0.1 );
        assertEquals( 115.3, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, may31, unitF ), 0.1 );
        // assertEquals( 193.3, aggregationService.getAggregatedDataValue( dataElementA, categoryOptionCombo, mar01, may31, unitB ), 0.1 );
        // got 193.80681818181816
    }
    
    @Test
    public void averageBoolDataElement()
    {
        dataElementB.setAggregationOperator( DataElement.AGGREGATION_OPERATOR_AVERAGE );
        dataElementService.updateDataElement( dataElementB );
        
        assertEquals( 1.0, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, mar31, unitC ) );
        assertEquals( 0.67, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, mar31, unitF ), 0.01 );
        assertEquals( 0.6, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, mar31, unitB ) );
        
        assertEquals( 0.66, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, may31, unitC ), 0.01 );
        assertEquals( 0.78, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, may31, unitF ), 0.01 );
        assertEquals( 0.67, aggregationService.getAggregatedDataValue( dataElementB, categoryOptionCombo, mar01, may31, unitB ), 0.01 );
    }
}
