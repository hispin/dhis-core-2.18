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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.jdbc.StatementBuilder;
import org.hisp.dhis.mock.MockI18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.RelativePeriods;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.reporttable.statement.CreateReportTableStatement;
import org.hisp.dhis.reporttable.statement.RemoveReportTableStatement;
import org.hisp.dhis.reporttable.statement.ReportTableStatement;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class ReportTableStatementTest
    extends DhisSpringTest
{
    private StatementBuilder statementBuilder;
    
    private List<DataElement> dataElements;
    private List<DataElementCategoryOptionCombo> categoryOptionCombos;
    private List<Indicator> indicators;
    private List<DataSet> dataSets;
    private List<Period> periods;
    private List<Period> relativePeriods;
    private List<OrganisationUnit> units;

    private DataElementCategoryCombo categoryCombo;
    
    private PeriodType monthlyPeriodType;
    private PeriodType yearlyPeriodType;
    
    private IndicatorType indicatorType;

    private RelativePeriods relatives;
        
    private I18nFormat i18nFormat;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
        throws Exception
    {
        statementBuilder = (StatementBuilder) getBean( "statementBuilder" );
        
        dataElements = new ArrayList<DataElement>();
        categoryOptionCombos = new ArrayList<DataElementCategoryOptionCombo>();
        indicators = new ArrayList<Indicator>();
        dataSets = new ArrayList<DataSet>();
        periods = new ArrayList<Period>();
        relativePeriods = new ArrayList<Period>();
        units = new ArrayList<OrganisationUnit>();
        
        monthlyPeriodType = PeriodType.getPeriodTypeByName( MonthlyPeriodType.NAME );
        yearlyPeriodType = PeriodType.getPeriodTypeByName( YearlyPeriodType.NAME );
        
        dataElements.add( createDataElement( 'A' ) );
        dataElements.add( createDataElement( 'B' ) );
        
        categoryOptionCombos.add( createCategoryOptionCombo( 'A', 'A', 'B' ) );
        categoryOptionCombos.add( createCategoryOptionCombo( 'B', 'C', 'D' ) );

        categoryCombo = new DataElementCategoryCombo( "CategoryComboA" );
        categoryCombo.setId( 'A' );
        categoryCombo.setOptionCombos( new HashSet<DataElementCategoryOptionCombo>( categoryOptionCombos ) );
        
        indicatorType = createIndicatorType( 'A' );
        
        indicators.add( createIndicator( 'A', indicatorType ) );
        indicators.add( createIndicator( 'B', indicatorType ) );
        
        dataSets.add( createDataSet( 'A', monthlyPeriodType ) );
        dataSets.add( createDataSet( 'B', monthlyPeriodType ) );
                
        periods.add( createPeriod( monthlyPeriodType, getDate( 2008, 1, 1 ), getDate( 2008, 1, 31 ) ) );
        periods.add( createPeriod( monthlyPeriodType, getDate( 2008, 2, 1 ), getDate( 2008, 2, 28 ) ) );

        Period periodC = createPeriod( monthlyPeriodType, getDate( 2008, 3, 1 ), getDate( 2008, 3, 31 ) );
        Period periodD = createPeriod( yearlyPeriodType, getDate( 2008, 1, 1 ), getDate( 2008, 12, 31 ) );
        
        periodC.setName( RelativePeriods.REPORTING_MONTH );
        periodD.setName( RelativePeriods.THIS_YEAR );
        
        periods.add( periodC );
        periods.add( periodD );
        
        units.add( createOrganisationUnit( 'A' ) );
        units.add( createOrganisationUnit( 'B' ) );        

        relatives = new RelativePeriods();
        
        relatives.setReportingMonth( true );
        relatives.setThisYear( true );

        i18nFormat = new MockI18nFormat();
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------

    @Test
    public void testCreateReportTableStatement()
    {
        ReportTable reportTable = new ReportTable( "Immunization", false,
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );
        
        reportTable.init();
        
        ReportTableStatement statement = new CreateReportTableStatement( reportTable, statementBuilder );

        assertNotNull( statement.getStatement() );
        
        reportTable = new ReportTable( "Immunization", false,
            dataElements, new ArrayList<Indicator>(), new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        statement = new CreateReportTableStatement( reportTable, statementBuilder );
        
        assertNotNull( statement.getStatement() );
        
        reportTable = new ReportTable( "Immunization", false,
            new ArrayList<DataElement>(), new ArrayList<Indicator>(), dataSets, periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        statement = new CreateReportTableStatement( reportTable, statementBuilder );

        assertNotNull( statement.getStatement() );
    }

    @Test
    public void testRemoveReportTableStatement()
    {
        ReportTable reportTable = new ReportTable( "Immunization", false,
            new ArrayList<DataElement>(), indicators, new ArrayList<DataSet>(), periods, relativePeriods, units, new ArrayList<OrganisationUnit>(),
            null, true, true, false, relatives, null, i18nFormat, "january_2000" );

        reportTable.init();
        
        ReportTableStatement statement = new RemoveReportTableStatement( reportTable );
        
        assertNotNull( statement.getStatement() );
        assertEquals( "DROP TABLE _report_immunization", statement.getStatement() );
    }
}
