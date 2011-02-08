package org.hisp.dhis.reporttable;

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
import static junit.framework.Assert.assertNotNull;
import static org.hisp.dhis.reporttable.ReportTable.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.DhisTest;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.completeness.DataSetCompletenessResult;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.batchhandler.AggregatedDataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AggregatedIndicatorValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.DataSetCompletenessResultBatchHandler;
import org.hisp.dhis.mock.MockI18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.reporttable.jdbc.ReportTableManager;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportTableManagerTest
    extends DhisTest
{
    private ReportTableManager reportTableManager;

    private BatchHandlerFactory batchHandlerFactory;

    private List<DataElement> dataElements;
    private List<Indicator> indicators;
    private List<DataSet> dataSets;
    private List<Period> periods;
    private List<Period> relativePeriods;
    private List<OrganisationUnit> units;

    private PeriodType monthlyPeriodType;
    private PeriodType yearlyPeriodType;

    private DataElementCategoryOptionCombo categoryOptionComboA;
    private DataElementCategoryOptionCombo categoryOptionComboB;
        
    private DataElementCategoryCombo categoryComboA;
    
    private DataElement dataElementA;
    private DataElement dataElementB;
        
    private IndicatorType indicatorType;
    
    private Indicator indicatorA;
    private Indicator indicatorB;
    
    private DataSet dataSetA;
    private DataSet dataSetB;
    
    private Period periodA;
    private Period periodB;
    private Period periodC;
    private Period periodD;
    
    private OrganisationUnit unitA;
    private OrganisationUnit unitB;

    private RelativePeriods relatives;
        
    private I18nFormat i18nFormat;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        reportTableManager = (ReportTableManager) getBean( ReportTableManager.ID );

        batchHandlerFactory = (BatchHandlerFactory) getBean( "batchHandlerFactory" );
        
        dataElements = new ArrayList<DataElement>();
        indicators = new ArrayList<Indicator>();
        dataSets = new ArrayList<DataSet>();
        periods = new ArrayList<Period>();
        relativePeriods = new ArrayList<Period>();
        units = new ArrayList<OrganisationUnit>();
        
        monthlyPeriodType = PeriodType.getPeriodTypeByName( MonthlyPeriodType.NAME );
        yearlyPeriodType = PeriodType.getPeriodTypeByName( YearlyPeriodType.NAME );
        
        indicatorType = createIndicatorType( 'A' );

        categoryOptionComboA = createCategoryOptionCombo( 'A', 'A', 'B' );
        categoryOptionComboB = createCategoryOptionCombo( 'B', 'C', 'D' );
        
        categoryOptionComboA.setId( 'A' );
        categoryOptionComboB.setId( 'B' );
        
        categoryComboA = new DataElementCategoryCombo( "CategoryComboA" );        
        categoryComboA.setId( 'A' );
        categoryComboA.getOptionCombos().add( categoryOptionComboA );
        categoryComboA.getOptionCombos().add( categoryOptionComboB );
                
        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        
        dataElementA.setId( 'A' );
        dataElementB.setId( 'B' );
        
        dataElements.add( dataElementA );
        dataElements.add( dataElementB );
        
        indicatorA = createIndicator( 'A', indicatorType );
        indicatorB = createIndicator( 'B', indicatorType );
        
        indicatorA.setId( 'A' );
        indicatorB.setId( 'B' );
        
        indicators.add( indicatorA );
        indicators.add( indicatorB );
        
        dataSetA = createDataSet( 'A', monthlyPeriodType );
        dataSetB = createDataSet( 'B', monthlyPeriodType );
        
        dataSetA.setId( 'A' );
        dataSetB.setId( 'B' );
        
        dataSets.add( dataSetA );
        dataSets.add( dataSetB );
        
        periodA = createPeriod( monthlyPeriodType, getDate( 2008, 1, 1 ), getDate( 2008, 1, 31 ) );
        periodB = createPeriod( monthlyPeriodType, getDate( 2008, 2, 1 ), getDate( 2008, 2, 28 ) );
        
        periodA.setId( 'A' );
        periodB.setId( 'B' );
        
        periods.add( periodA );
        periods.add( periodB );

        periodC = createPeriod( monthlyPeriodType, getDate( 2008, 3, 1 ), getDate( 2008, 3, 31 ) );
        periodD = createPeriod( yearlyPeriodType, getDate( 2008, 1, 1 ), getDate( 2008, 12, 31 ) );
        
        periodC.setId( 'C' );
        periodD.setId( 'D' );
        
        periodC.setName( RelativePeriods.REPORTING_MONTH );
        periodD.setName( RelativePeriods.THIS_YEAR );
        
        relativePeriods.add( periodC );
        relativePeriods.add( periodD );
        
        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B' );
        
        unitA.setId( 'A' );
        unitB.setId( 'B' );
        
        units.add( unitA );
        units.add( unitB );

        relatives = new RelativePeriods();
        
        relatives.setReportingMonth( true );
        relatives.setThisYear( true );
        
        i18nFormat = new MockI18nFormat();
    }

    @Override
    public boolean emptyDatabaseAfterTest()
    {
        return true;
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testCreateReportTable()
    {
        ReportTable reportTable = new ReportTable( "Immunization", ReportTable.MODE_INDICATORS, false,
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        reportTableManager.createReportTable( reportTable );
        
        reportTable = new ReportTable( "Immunization", ReportTable.MODE_DATAELEMENTS, false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        reportTableManager.createReportTable( reportTable );
        
        reportTable = new ReportTable( "Immunization", ReportTable.MODE_DATAELEMENTS, false,
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        reportTableManager.createReportTable( reportTable );
    }

    @Test
    public void testRemoveReportTable()
    {
        ReportTable reportTable = new ReportTable( "Immunization", ReportTable.MODE_INDICATORS, false,
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        reportTableManager.removeReportTable( reportTable );

        reportTable = new ReportTable( "Immunization", ReportTable.MODE_DATAELEMENTS, false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        reportTableManager.removeReportTable( reportTable );

        reportTable = new ReportTable( "Immunization", ReportTable.MODE_DATAELEMENTS, false,
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        reportTableManager.removeReportTable( reportTable );
    }

    @Test
    public void testGetAggregatedValueMapForIndicator()
    {
        BatchHandler<AggregatedIndicatorValue> batchHandler = batchHandlerFactory.createBatchHandler( AggregatedIndicatorValueBatchHandler.class );
        
        batchHandler.init();
        
        batchHandler.addObject( new AggregatedIndicatorValue( 'A', 'A', 8, 'A', 8, 1, 10, 20, 2 ) );
        batchHandler.addObject( new AggregatedIndicatorValue( 'A', 'A', 8, 'B', 8, 1, 10, 20, 2 ) );
        batchHandler.addObject( new AggregatedIndicatorValue( 'A', 'B', 8, 'A', 8, 1, 10, 20, 2 ) );
        batchHandler.addObject( new AggregatedIndicatorValue( 'A', 'B', 8, 'B', 8, 1, 10, 20, 2 ) );
        batchHandler.addObject( new AggregatedIndicatorValue( 'B', 'A', 8, 'A', 8, 1, 10, 20, 2 ) );
        batchHandler.addObject( new AggregatedIndicatorValue( 'B', 'A', 8, 'B', 8, 1, 10, 20, 2 ) );
        batchHandler.addObject( new AggregatedIndicatorValue( 'B', 'B', 8, 'A', 8, 1, 10, 20, 2 ) );
        batchHandler.addObject( new AggregatedIndicatorValue( 'B', 'B', 8, 'B', 8, 1, 10, 20, 2 ) );
        
        batchHandler.flush();
        
        ReportTable reportTable = new ReportTable( "Immunization", ReportTable.MODE_INDICATORS, false,
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        Map<String, Double> map = reportTableManager.getAggregatedValueMap( reportTable, null, null, null, unitA );
        
        assertNotNull( map );
        assertEquals( 4, map.entrySet().size() );
        
        Map<String, Double> reference = new HashMap<String, Double>();
        
        reference.put( INDICATOR_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ), 10.0 );
        reference.put( INDICATOR_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ), 10.0 );
        reference.put( INDICATOR_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ), 10.0 );
        reference.put( INDICATOR_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ), 10.0 );
        
        assertEquals( reference, map );
    }

    @Test
    public void testGetAggregatedValueForDataElement()
    {
        BatchHandler<AggregatedDataValue> batchHandler = batchHandlerFactory.createBatchHandler( AggregatedDataValueBatchHandler.class );
        
        batchHandler.init();
        
        batchHandler.addObject( new AggregatedDataValue( 'A', 1, 'A', 1, 'A', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'A', 1, 'A', 1, 'B', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'A', 1, 'B', 1, 'A', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'A', 1, 'B', 1, 'B', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'B', 1, 'A', 1, 'A', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'B', 1, 'A', 1, 'B', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'B', 1, 'B', 1, 'A', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'B', 1, 'B', 1, 'B', 2, 10.0 ) );
        
        batchHandler.flush();        

        ReportTable reportTable = new ReportTable( "Immunization", ReportTable.MODE_DATAELEMENTS, false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        Map<String, Double> map = reportTableManager.getAggregatedValueMap( reportTable, null, null, null, unitA );
        
        assertNotNull( map );
        assertEquals( 4, map.entrySet().size() );
        
        Map<String, Double> reference = new HashMap<String, Double>();
        
        reference.put( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ), 10.0 );
        reference.put( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ), 10.0 );
        reference.put( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ), 10.0 );
        reference.put( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ), 10.0 );
        
        assertEquals( reference, map );
    }

    @Test
    public void testGetAggregatedValueForDataElementWithCategoryOptionCombo()
    {
        BatchHandler<AggregatedDataValue> batchHandler = batchHandlerFactory.createBatchHandler( AggregatedDataValueBatchHandler.class );
        
        batchHandler.init();
        
        batchHandler.addObject( new AggregatedDataValue( 'A', 'A', 'A', 3, 'A', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'A', 'A', 'A', 3, 'B', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'A', 'B', 'B', 3, 'A', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'A', 'B', 'B', 3, 'B', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'B', 'A', 'A', 3, 'A', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'B', 'A', 'A', 3, 'B', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'B', 'B', 'B', 3, 'A', 2, 10.0 ) );
        batchHandler.addObject( new AggregatedDataValue( 'B', 'B', 'B', 3, 'B', 2, 10.0 ) );
        
        batchHandler.flush();

        ReportTable reportTable = new ReportTable( "Immunization", ReportTable.MODE_DATAELEMENTS, false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            categoryComboA, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        Map<String, Double> map = reportTableManager.getAggregatedValueMap( reportTable, null, null, null, unitA );

        assertNotNull( map );
        assertEquals( 4, map.entrySet().size() );
        
        Map<String, Double> reference = new HashMap<String, Double>();
        
        reference.put( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ), 10.0 );
        reference.put( DATAELEMENT_ID + Integer.valueOf( 'A' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ), 10.0 );
        reference.put( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ), 10.0 );
        reference.put( DATAELEMENT_ID + Integer.valueOf( 'B' ) + SEPARATOR + CATEGORYCOMBO_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ), 10.0 );
        
        assertEquals( reference, map );
    }

    @Test
    public void testGetAggregatedValueForDataSet()
    {
        BatchHandler<DataSetCompletenessResult> batchHandler = batchHandlerFactory.createBatchHandler( DataSetCompletenessResultBatchHandler.class );
        
        batchHandler.init();
        
        batchHandler.addObject( new DataSetCompletenessResult( 'A', 'A', "PeriodA", 'A', "NameA", 20, 10, 5 ) );
        batchHandler.addObject( new DataSetCompletenessResult( 'A', 'A', "PeriodA", 'B', "NameA", 20, 10, 5 ) );
        batchHandler.addObject( new DataSetCompletenessResult( 'A', 'B', "PeriodA", 'A', "NameA", 20, 10, 5 ) );
        batchHandler.addObject( new DataSetCompletenessResult( 'A', 'B', "PeriodA", 'B', "NameA", 20, 10, 5 ) );
        batchHandler.addObject( new DataSetCompletenessResult( 'B', 'A', "PeriodA", 'A', "NameA", 20, 10, 5 ) );
        batchHandler.addObject( new DataSetCompletenessResult( 'B', 'A', "PeriodA", 'B', "NameA", 20, 10, 5 ) );
        batchHandler.addObject( new DataSetCompletenessResult( 'B', 'B', "PeriodA", 'A', "NameA", 20, 10, 5 ) );
        batchHandler.addObject( new DataSetCompletenessResult( 'B', 'B', "PeriodA", 'B', "NameA", 20, 10, 5 ) );
        
        batchHandler.flush();        

        ReportTable reportTable = new ReportTable( "Immunization", ReportTable.MODE_DATASETS, false,
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        Map<String, Double> map = reportTableManager.getAggregatedValueMap( reportTable, null, null, null, unitA );
        
        assertNotNull( map );
        assertEquals( 4, map.entrySet().size() );
        
        Map<String, Double> reference = new HashMap<String, Double>();
        
        reference.put( DATASET_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ), 50.0 );
        reference.put( DATASET_ID + Integer.valueOf( 'A' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ), 50.0 );
        reference.put( DATASET_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'A' ), 50.0 );
        reference.put( DATASET_ID + Integer.valueOf( 'B' ) + SEPARATOR + PERIOD_ID + Integer.valueOf( 'B' ), 50.0 );
        
        assertEquals( reference, map );
    }
}

