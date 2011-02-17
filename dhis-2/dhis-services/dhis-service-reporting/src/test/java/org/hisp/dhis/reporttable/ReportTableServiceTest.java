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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.amplecode.quick.BatchHandler;
import org.amplecode.quick.BatchHandlerFactory;
import org.hisp.dhis.DhisTest;
import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.aggregation.AggregatedIndicatorValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.batchhandler.AggregatedDataValueBatchHandler;
import org.hisp.dhis.jdbc.batchhandler.AggregatedIndicatorValueBatchHandler;
import org.hisp.dhis.mock.MockI18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.period.YearlyPeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportTableServiceTest
    extends DhisTest
{
    private ReportTableService reportTableService;
    
    private BatchHandlerFactory batchHandlerFactory;
    
    private List<DataElement> dataElements;
    private List<DataElementCategoryOptionCombo> categoryOptionCombos;
    private List<Indicator> indicators;
    private List<DataSet> dataSets;
    private List<Period> periods;
    private List<Period> relativePeriods;
    private List<OrganisationUnit> units;

    private PeriodType montlyPeriodType;
    private PeriodType yearlyPeriodType;

    private DataElement dataElementA;
    private DataElement dataElementB;

    private DataElementCategoryOption categoryOptionA;
    private DataElementCategoryOption categoryOptionB;
    
    private DataElementCategory categoryA;
    
    private DataElementCategoryCombo categoryComboA;
    
    private DataElementCategoryOptionCombo categoryOptionComboA;
    private DataElementCategoryOptionCombo categoryOptionComboB;  

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
    
    private int dataElementIdA;
    private int dataElementIdB;
    
    private int categoryOptionComboIdA;
    private int categoryOptionComboIdB;
    
    private int indicatorIdA;
    private int indicatorIdB;
    
    private int periodIdA;
    private int periodIdB;
    
    private int unitIdA;
    private int unitIdB;

    private RelativePeriods relatives;
        
    private I18nFormat i18nFormat;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        reportTableService = (ReportTableService) getBean( ReportTableService.ID );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        categoryService = (DataElementCategoryService) getBean( DataElementCategoryService.ID );        
        
        indicatorService = (IndicatorService) getBean( IndicatorService.ID );
        dataSetService = (DataSetService) getBean( DataSetService.ID );
        periodService = (PeriodService) getBean( PeriodService.ID );
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        batchHandlerFactory = (BatchHandlerFactory) getBean( "batchHandlerFactory" );
        
        dataElements = new ArrayList<DataElement>();
        categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();
        indicators = new ArrayList<Indicator>();
        dataSets = new ArrayList<DataSet>();
        periods = new ArrayList<Period>();
        relativePeriods = new ArrayList<Period>();
        units = new ArrayList<OrganisationUnit>();
        
        montlyPeriodType = PeriodType.getPeriodTypeByName( MonthlyPeriodType.NAME );
        yearlyPeriodType = PeriodType.getPeriodTypeByName( YearlyPeriodType.NAME );        
        
        // ---------------------------------------------------------------------
        // Setup Dimensions
        // ---------------------------------------------------------------------

        categoryOptionA = new DataElementCategoryOption( "Male" );
        categoryOptionB = new DataElementCategoryOption( "Female" );
        
        categoryService.addDataElementCategoryOption( categoryOptionA );
        categoryService.addDataElementCategoryOption( categoryOptionB );

        categoryA = new DataElementCategory( "Gender" );
        categoryA.getCategoryOptions().add( categoryOptionA );
        categoryA.getCategoryOptions().add( categoryOptionB );

        categoryService.addDataElementCategory( categoryA );

        categoryComboA = new DataElementCategoryCombo( "Gender" );
        categoryComboA.getCategories().add( categoryA );        
        
        categoryService.addDataElementCategoryCombo( categoryComboA );
        
        categoryService.generateOptionCombos( categoryComboA );

        Iterator<DataElementCategoryOptionCombo> categoryOptionCombosIterator = categoryService.getAllDataElementCategoryOptionCombos().iterator();
        
        categoryOptionCombosIterator.next(); // Omit default
        categoryOptionComboA = categoryOptionCombosIterator.next();
        categoryOptionComboB = categoryOptionCombosIterator.next();
        
        categoryOptionComboIdA = categoryOptionComboA.getId();
        categoryOptionComboIdB = categoryOptionComboB.getId();
                
        categoryOptionCombos.add( categoryOptionComboA );        
        categoryOptionCombos.add( categoryOptionComboB );
        
        // ---------------------------------------------------------------------
        // Setup DataElements
        // ---------------------------------------------------------------------

        dataElementA = createDataElement( 'A' );
        dataElementB = createDataElement( 'B' );
        
        dataElementIdA = dataElementService.addDataElement( dataElementA );
        dataElementIdB = dataElementService.addDataElement( dataElementB );
                
        dataElements.add( dataElementA );
        dataElements.add( dataElementB );

        // ---------------------------------------------------------------------
        // Setup Indicators
        // ---------------------------------------------------------------------

        indicatorType = createIndicatorType( 'A' );
        
        indicatorService.addIndicatorType( indicatorType );
        
        indicatorA = createIndicator( 'A', indicatorType );
        indicatorB = createIndicator( 'B', indicatorType );
        
        indicatorIdA = indicatorService.addIndicator( indicatorA );
        indicatorIdB = indicatorService.addIndicator( indicatorB );
                
        indicators.add( indicatorA );
        indicators.add( indicatorB );

        // ---------------------------------------------------------------------
        // Setup DataSets
        // ---------------------------------------------------------------------

        dataSetA = createDataSet( 'A', montlyPeriodType );
        dataSetB = createDataSet( 'B', montlyPeriodType );
        
        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        
        dataSets.add( dataSetA );
        dataSets.add( dataSetB );

        // ---------------------------------------------------------------------
        // Setup Periods
        // ---------------------------------------------------------------------

        periodA = createPeriod( montlyPeriodType, getDate( 2008, 1, 1 ), getDate( 2008, 1, 31 ) );
        periodB = createPeriod( montlyPeriodType, getDate( 2008, 2, 1 ), getDate( 2008, 2, 28 ) );
        
        periodIdA = periodService.addPeriod( periodA );
        periodIdB = periodService.addPeriod( periodB );
                
        periods.add( periodA );
        periods.add( periodB );

        periodC = createPeriod( montlyPeriodType, getDate( 2008, 3, 1 ), getDate( 2008, 3, 31 ) );
        periodD = createPeriod( yearlyPeriodType, getDate( 2008, 1, 1 ), getDate( 2008, 12, 31 ) );
        
        periodService.addPeriod( periodC );
        periodService.addPeriod( periodD );
        
        periodC.setName( RelativePeriods.REPORTING_MONTH );
        periodD.setName( RelativePeriods.THIS_YEAR );
        
        relativePeriods.add( periodC );
        relativePeriods.add( periodD );

        // ---------------------------------------------------------------------
        // Setup OrganisationUnits
        // ---------------------------------------------------------------------

        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B' );
        
        unitIdA = organisationUnitService.addOrganisationUnit( unitA );
        unitIdB = organisationUnitService.addOrganisationUnit( unitB );
        
        units.add( unitA );
        units.add( unitB );

        relatives = new RelativePeriods();
        
        relatives.setReportingMonth( true );
        relatives.setThisYear( true );
        
        i18nFormat = new MockI18nFormat();
        
        BatchHandler<AggregatedIndicatorValue> indicatorValueBatchHandler = batchHandlerFactory.createBatchHandler( AggregatedIndicatorValueBatchHandler.class );
        
        indicatorValueBatchHandler.init();
        
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdA, 8, unitIdA, 8, 1, 11, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdA, 8, unitIdB, 8, 1, 12, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdA, 8, unitIdA, 8, 1, 13, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdA, 8, unitIdB, 8, 1, 14, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdB, 8, unitIdA, 8, 1, 15, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdB, 8, unitIdB, 8, 1, 16, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdB, 8, unitIdA, 8, 1, 17, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdA, periodIdB, 8, unitIdB, 8, 1, 18, 0, 0 ) );        
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdA, 8, unitIdA, 8, 1, 19, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdA, 8, unitIdB, 8, 1, 20, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdA, 8, unitIdA, 8, 1, 21, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdA, 8, unitIdB, 8, 1, 22, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdB, 8, unitIdA, 8, 1, 23, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdB, 8, unitIdB, 8, 1, 24, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdB, 8, unitIdA, 8, 1, 25, 0, 0 ) );
        indicatorValueBatchHandler.addObject( new AggregatedIndicatorValue( indicatorIdB, periodIdB, 8, unitIdB, 8, 1, 26, 0, 0 ) );
        
        indicatorValueBatchHandler.flush();
        
        BatchHandler<AggregatedDataValue> dataValueBatchHandler = batchHandlerFactory.createBatchHandler( AggregatedDataValueBatchHandler.class );
        
        dataValueBatchHandler.init();
        
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdB, periodIdA, 8, unitIdA, 8, 11 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdB, periodIdA, 8, unitIdB, 8, 12 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdB, periodIdB, 8, unitIdA, 8, 13 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdB, periodIdB, 8, unitIdB, 8, 14 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdA, periodIdA, 8, unitIdA, 8, 15 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdA, periodIdA, 8, unitIdB, 8, 16 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdA, periodIdB, 8, unitIdA, 8, 17 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdA, categoryOptionComboIdA, periodIdB, 8, unitIdB, 8, 18 ) );        
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdB, periodIdA, 8, unitIdA, 8, 19 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdB, periodIdA, 8, unitIdB, 8, 20 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdB, periodIdB, 8, unitIdA, 8, 21 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdB, periodIdB, 8, unitIdB, 8, 22 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdA, periodIdA, 8, unitIdA, 8, 23 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdA, periodIdA, 8, unitIdB, 8, 24 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdA, periodIdB, 8, unitIdA, 8, 25 ) );
        dataValueBatchHandler.addObject( new AggregatedDataValue( dataElementIdB, categoryOptionComboIdA, periodIdB, 8, unitIdB, 8, 26 ) );
        
        dataValueBatchHandler.flush();
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
    public void testCreateDataElementReportTableA()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test
    public void testCreateDataElementReportTableB()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, false, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test
    public void testCreateDataElementReportTableC()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test
    public void testCreateDataElementWithCategoryOptionComboReportTableA()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            categoryComboA, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test    
    public void testCreateDataElementWithCategoryOptionComboReportTableB()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            categoryComboA, false, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test
    public void testCreateDataElementWithCategoryOptionComboReportTableC()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            categoryComboA, true, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test
    public void testCreateIndicatorReportTableA()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test
    public void testCreateIndicatorReportTableB()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, false, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test
    public void testCreateIndicatorReportTableC()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test
    public void testCreateDataSetReportTableA()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test
    public void testCreateDataSetReportTableB()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, false, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }

    @Test
    public void testCreateDataSetReportTableC()
    {
        ReportTable reportTable = new ReportTable( "Prescriptions", false,
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, relativePeriods, units, new ArrayList<OrganisationUnit>(), 
            null, true, false, true, relatives, null, i18nFormat, "january_2000" );

        reportTableService.saveReportTable( reportTable );
        
        reportTable.init();
        
        reportTableService.createReportTable( reportTable, false );
    }
}
