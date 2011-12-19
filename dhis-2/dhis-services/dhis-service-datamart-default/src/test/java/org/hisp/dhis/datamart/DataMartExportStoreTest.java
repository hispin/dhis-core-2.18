package org.hisp.dhis.datamart;

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
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.GenericIdentifiableObjectStore;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@SuppressWarnings( "unchecked" )
public class DataMartExportStoreTest
    extends DhisSpringTest
{
    private GenericIdentifiableObjectStore<DataMartExport> dataMartExportStore;
    
    private DataMartExport exportA;
    private DataMartExport exportB;
    private DataMartExport exportC;
    
    private RelativePeriods relatives;
    
    private Set<DataElement> dataElements;
    private Set<Indicator> indicators;
    private Set<OrganisationUnit> organisationUnits;
    private Set<Period> periods;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------
    
    @Override
    public void setUpTest()
    {
        dataMartExportStore = (GenericIdentifiableObjectStore<DataMartExport>) getBean( "org.hisp.dhis.datamart.DataMartExportStore" );
        
        dataElementService = (DataElementService) getBean( DataElementService.ID );
        
        indicatorService = (IndicatorService) getBean( IndicatorService.ID );
        
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        periodService = (PeriodService) getBean( PeriodService.ID );
        
        dataElements = new HashSet<DataElement>();
        indicators =  new HashSet<Indicator>();
        organisationUnits = new HashSet<OrganisationUnit>();
        periods = new HashSet<Period>();
        
        DataElement dataElementA = createDataElement( 'A' );
        DataElement dataElementB = createDataElement( 'B' );
        
        dataElementService.addDataElement( dataElementA );
        dataElementService.addDataElement( dataElementB );
        
        dataElements.add( dataElementA );
        dataElements.add( dataElementB );        
        
        IndicatorType indicatorType = createIndicatorType( 'A' );
        
        indicatorService.addIndicatorType( indicatorType );
        
        Indicator indicatorA = createIndicator( 'A', indicatorType );
        Indicator indicatorB = createIndicator( 'B', indicatorType );
        
        indicatorService.addIndicator( indicatorA );
        indicatorService.addIndicator( indicatorB );
        
        indicators.add( indicatorA );
        indicators.add( indicatorA );
        
        OrganisationUnit unitA = createOrganisationUnit( 'A' );
        OrganisationUnit unitB = createOrganisationUnit( 'B' );
        
        organisationUnitService.addOrganisationUnit( unitA );
        organisationUnitService.addOrganisationUnit( unitB );
        
        organisationUnits.add( unitA );
        organisationUnits.add( unitB );
        
        Period periodA = createPeriod( new MonthlyPeriodType(), getDate( 2000, 1, 1 ), getDate( 2000, 31, 1 ) );
        Period periodB = createPeriod( new MonthlyPeriodType(), getDate( 2000, 2, 1 ), getDate( 2000, 28, 2 ) );
        
        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        
        periods.add( periodA );
        periods.add( periodB );
        
        relatives = new RelativePeriods( true, true, true, true, true, true, true, false, false, false, false, false, false, false, false );
        
        exportA = new DataMartExport( "ExportA", dataElements, indicators, organisationUnits, periods, relatives );
        exportB = new DataMartExport( "ExportB", dataElements, indicators, organisationUnits, periods, relatives );
        exportC = new DataMartExport( "ExportC", dataElements, indicators, organisationUnits, periods, relatives );        
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------
    
    @Test
    public void testSaveGet()
    {
        dataMartExportStore.save( exportA );
        dataMartExportStore.save( exportB );
        dataMartExportStore.save( exportC );
        
        assertEquals( exportA, dataMartExportStore.get( exportA.getId() ) );
        assertEquals( exportB, dataMartExportStore.get( exportB.getId() ) );
        assertEquals( exportC, dataMartExportStore.get( exportC.getId() ) );
        
        DataMartExport export = dataMartExportStore.get( exportA.getId() );
        
        assertEquals( dataElements, export.getDataElements() );
        assertEquals( indicators, export.getIndicators() );
        assertEquals( organisationUnits, export.getOrganisationUnits() );
        assertEquals( periods, export.getPeriods() );
        assertEquals( relatives, export.getRelatives() );
    }

    @Test
    public void testDelete()    
    {
        dataMartExportStore.save( exportA );
        dataMartExportStore.save( exportB );
        dataMartExportStore.save( exportC );
        
        assertNotNull( dataMartExportStore.get( exportA.getId() ) );
        assertNotNull( dataMartExportStore.get( exportB.getId() ) );
        assertNotNull( dataMartExportStore.get( exportC.getId() ) );
        
        dataMartExportStore.delete( exportA );
        
        assertNull( dataMartExportStore.get( exportA.getId() ) );
        assertNotNull( dataMartExportStore.get( exportB.getId() ) );
        assertNotNull( dataMartExportStore.get( exportC.getId() ) );

        dataMartExportStore.delete( exportB );
        
        assertNull( dataMartExportStore.get( exportA.getId() ) );
        assertNull( dataMartExportStore.get( exportB.getId() ) );
        assertNotNull( dataMartExportStore.get( exportC.getId() ) );        
    }

    @Test
    public void testGetAll()
    {
        dataMartExportStore.save( exportA );
        dataMartExportStore.save( exportB );
        dataMartExportStore.save( exportC );
        
        Collection<DataMartExport> expected = new ArrayList<DataMartExport>();
        expected.add( exportA );
        expected.add( exportB );
        expected.add( exportC );
        
        Collection<DataMartExport> actual = dataMartExportStore.getAll();
        
        assertEquals( expected, actual );
    }

    @Test
    public void testGetByName()
    {
        dataMartExportStore.save( exportA );
        dataMartExportStore.save( exportB );
        dataMartExportStore.save( exportC );
        
        DataMartExport actualA = dataMartExportStore.getByName( exportA.getName() );
        
        assertEquals( exportA, actualA );
    }
}
