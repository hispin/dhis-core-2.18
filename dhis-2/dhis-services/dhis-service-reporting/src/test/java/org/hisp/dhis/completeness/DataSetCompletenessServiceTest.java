package org.hisp.dhis.completeness;

/*
 * Copyright (c) 2004-2012, University of Oslo
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
import static junit.framework.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.hisp.dhis.DhisTest;
import org.hisp.dhis.dataset.CompleteDataSetRegistration;
import org.hisp.dhis.dataset.CompleteDataSetRegistrationService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.external.location.LocationManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DataSetCompletenessServiceTest
    extends DhisTest
{
    private LocationManager locationManager;
    
    private CompleteDataSetRegistrationService registrationService;
    
    private DataSetCompletenessService completenessService;
    
    private PeriodType periodType;
    
    private Period periodA;
    private Period periodB;
    private Period periodC;
    
    private int periodIdA;
    private int periodIdC;
    
    private OrganisationUnit unitA;
    private OrganisationUnit unitB;
    private OrganisationUnit unitC;
    private OrganisationUnit unitD;
    private OrganisationUnit unitE;
    private OrganisationUnit unitF;
    private OrganisationUnit unitG;
    private OrganisationUnit unitH;
        
    private int unitIdA;
    private int unitIdB;
    private int unitIdC;
    private Collection<Integer> unitIdsA;
    
    private DataSet dataSetA;
    private DataSet dataSetB;
    private DataSet dataSetC;
    
    private int dataSetIdA;
    
    private Collection<OrganisationUnit> sources = new HashSet<OrganisationUnit>();

    private Date onTimeA;
    private Date tooLateA;
    private Date onTimeB;
    private Date tooLateB;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    @Override
    public void setUpTest()
    {
        locationManager = (LocationManager) getBean( "locationManager" );
        
        setExternalTestDir( locationManager );
        
        periodService = (PeriodService) getBean( PeriodService.ID );
        
        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );
        
        dataSetService = (DataSetService) getBean( DataSetService.ID );
        
        registrationService = (CompleteDataSetRegistrationService) getBean( CompleteDataSetRegistrationService.ID );
        
        completenessService = (DataSetCompletenessService) getBean( "registrationDataCompletenessService" );

        periodType = new MonthlyPeriodType();
        
        periodA = createPeriod( periodType, getDate( 2000, 1, 1 ), getDate( 2000, 1, 31 ) );
        periodB = createPeriod( periodType, getDate( 2000, 2, 1 ), getDate( 2000, 2, 28 ) );
        periodC = createPeriod( new QuarterlyPeriodType(), getDate( 2000, 1, 1 ), getDate( 2000, 3, 31 ) );        
        
        periodIdA = periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        periodIdC = periodService.addPeriod( periodC );

        unitA = createOrganisationUnit( 'A' );
        unitB = createOrganisationUnit( 'B' );
        unitC = createOrganisationUnit( 'C' );
        unitD = createOrganisationUnit( 'D' );
        unitE = createOrganisationUnit( 'E' );
        unitF = createOrganisationUnit( 'F' );
        unitG = createOrganisationUnit( 'G' );
        unitH = createOrganisationUnit( 'H' );
        
        unitB.setParent( unitA );
        unitC.setParent( unitA );
        unitE.setParent( unitB );
        unitF.setParent( unitB );
        unitG.setParent( unitC );
        unitH.setParent( unitC );
        
        unitA.getChildren().add( unitB );
        unitA.getChildren().add( unitC );
        unitB.getChildren().add( unitE );
        unitB.getChildren().add( unitF );
        unitC.getChildren().add( unitG );
        unitC.getChildren().add( unitH );
        
        unitIdA = organisationUnitService.addOrganisationUnit( unitA );
        unitIdB = organisationUnitService.addOrganisationUnit( unitB );
        unitIdC = organisationUnitService.addOrganisationUnit( unitC );
        organisationUnitService.addOrganisationUnit( unitD );
        organisationUnitService.addOrganisationUnit( unitE );
        organisationUnitService.addOrganisationUnit( unitF );
        organisationUnitService.addOrganisationUnit( unitG );
        organisationUnitService.addOrganisationUnit( unitH );
        
        unitIdsA = new HashSet<Integer>();
        unitIdsA.add( unitIdB );
        unitIdsA.add( unitIdC );
        
        sources.add( unitA );
        sources.add( unitB );
        sources.add( unitC );
        
        dataSetA = createDataSet( 'A', periodType );
        dataSetB = createDataSet( 'B', periodType );
        dataSetC = createDataSet( 'C', periodType );

        onTimeA = getDate( 2000, 2, 10 );
        tooLateA = getDate( 2000, 2, 25 );
        onTimeB = getDate( 2000, 3, 10 );
        tooLateB = getDate( 2000, 3, 25 );
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
    public void testGetPercentage()
    {
        DataSetCompletenessResult resultA = new DataSetCompletenessResult( dataSetA.getName(), 20, 15, 10 );
        DataSetCompletenessResult resultB = new DataSetCompletenessResult( dataSetA.getName(), 0, 15, 10 );
        
        assertEquals( 75.0, resultA.getPercentage() );
        assertEquals( 0.0, resultB.getPercentage() );
        
        assertEquals( 50.0, resultA.getPercentageOnTime() );
        assertEquals( 0.0, resultB.getPercentageOnTime() );
    }

    @Test
    public void testGetDataSetCompletenessByDataSetA()
    {
        dataSetA.getSources().add( unitA );
        dataSetA.getSources().add( unitB );
        
        dataSetB.getSources().add( unitA );
        dataSetB.getSources().add( unitB );

        dataSetC.getSources().add( unitA );
        dataSetC.getSources().add( unitB );
        
        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        dataSetService.addDataSet( dataSetC );
        
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitA, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitB, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodB, unitA, tooLateB, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitD, tooLateA, "" ) );
        
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetB, periodA, unitA, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetB, periodA, unitC, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetB, periodB, unitB, onTimeB, "" ) );

        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetC, periodA, unitC, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetC, periodB, unitA, tooLateB, "" ) );
        
        Collection<DataSetCompletenessResult> results = completenessService.getDataSetCompleteness( periodIdA, unitIdA );
        
        assertNotNull( results );
        assertEquals( 3, results.size() );        
        assertTrue( results.contains( new DataSetCompletenessResult( dataSetA.getName(), 2, 2, 0 ) ) );
        assertTrue( results.contains( new DataSetCompletenessResult( dataSetB.getName(), 2, 1, 0 ) ) );
        assertTrue( results.contains( new DataSetCompletenessResult( dataSetC.getName(), 2, 0, 0 ) ) );  

        results = completenessService.getDataSetCompleteness( periodIdC, unitIdA );
   
        assertNotNull( results );
        assertEquals( 3, results.size() );
        assertTrue( results.contains( new DataSetCompletenessResult( dataSetA.getName(), 6, 3, 0 ) ) );
        assertTrue( results.contains( new DataSetCompletenessResult( dataSetB.getName(), 6, 2, 1 ) ) );
        assertTrue( results.contains( new DataSetCompletenessResult( dataSetC.getName(), 6, 1, 0 ) ) );            
    }

    @Test
    public void testGetDataSetCompletenessByDataSetB()
    {
        dataSetA.getSources().add( unitA );
        dataSetA.getSources().add( unitB );
        dataSetA.getSources().add( unitC );

        dataSetB.getSources().add( unitB );
        dataSetB.getSources().add( unitC );

        dataSetC.getSources().add( unitB );
        dataSetC.getSources().add( unitC );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        dataSetService.addDataSet( dataSetC );
        
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitA, tooLateA, "" ) );
        
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetB, periodA, unitA, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetB, periodA, unitB, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetB, periodA, unitC, tooLateA, "" ) );
        
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetC, periodA, unitA, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetC, periodA, unitB, tooLateA, "" ) );

        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodB, unitA, tooLateB, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitD, tooLateA, "" ) );
        
        Collection<DataSetCompletenessResult> results = completenessService.getDataSetCompleteness( periodIdA, unitIdA );
        
        DataSetCompletenessResult resultA = new DataSetCompletenessResult( dataSetA.getName(), 3, 1, 0 );
        DataSetCompletenessResult resultB = new DataSetCompletenessResult( dataSetB.getName(), 2, 2, 0 );
        DataSetCompletenessResult resultC = new DataSetCompletenessResult( dataSetC.getName(), 2, 1, 0 );

        assertNotNull( results );
        assertEquals( 3, results.size() );        
        assertTrue( results.contains( resultA ) );
        assertTrue( results.contains( resultB ) );
        assertTrue( results.contains( resultC ) );
    }

    @Test
    public void testGetDataSetCompletenessByDataSetC()
    {
        dataSetA.getSources().add( unitA );
        dataSetA.getSources().add( unitB );
        dataSetA.getSources().add( unitC );
        dataSetA.getSources().add( unitE );
        dataSetA.getSources().add( unitF );
        dataSetA.getSources().add( unitG );
        dataSetA.getSources().add( unitH );
        
        dataSetService.addDataSet( dataSetA );

        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitB, onTimeA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitC, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitE, onTimeA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitF, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitG, onTimeA, "" ) );

        Collection<DataSetCompletenessResult> results = completenessService.getDataSetCompleteness( periodIdA, unitIdA );
        
        DataSetCompletenessResult resultA = new DataSetCompletenessResult( dataSetA.getName(), 7, 5, 3 );
        
        assertNotNull( results );
        assertEquals( 1, results.size() );        
        assertTrue( results.contains( resultA ) );
    }

    @Test
    public void testGetDataSetCompletenessByOrganisationUnitA()
    {
        dataSetA.getSources().add( unitE );
        dataSetA.getSources().add( unitF );
        dataSetA.getSources().add( unitG );
        dataSetA.getSources().add( unitH );
        
        dataSetIdA = dataSetService.addDataSet( dataSetA );
        
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitE, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitF, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitG, tooLateA, "" ) );
        
        Collection<DataSetCompletenessResult> results = completenessService.getDataSetCompleteness( periodIdA, unitIdsA, dataSetIdA );
        
        DataSetCompletenessResult resultA = new DataSetCompletenessResult( unitB.getName(), 2, 2, 0 );
        DataSetCompletenessResult resultB = new DataSetCompletenessResult( unitC.getName(), 2, 1, 0 );
        
        assertNotNull( results );
        assertEquals( 2, results.size() );
        assertTrue( results.contains( resultA ) );
        assertTrue( results.contains( resultB ) );
    }

    @Test
    public void testGetDataSetCompletenessByOrganisationUnitB()
    {
        dataSetA.getSources().add( unitE );
        dataSetA.getSources().add( unitF );
        dataSetA.getSources().add( unitG );

        dataSetIdA = dataSetService.addDataSet( dataSetA );

        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitE, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitG, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitH, tooLateA, "" ) );

        Collection<DataSetCompletenessResult> results = completenessService.getDataSetCompleteness( periodIdA, unitIdsA, dataSetIdA );
        
        DataSetCompletenessResult resultA = new DataSetCompletenessResult( unitB.getName(), 2, 1, 0 );
        DataSetCompletenessResult resultB = new DataSetCompletenessResult( unitC.getName(), 1, 1, 0 );
                
        assertNotNull( results );
        assertEquals( 2, results.size() );
        assertTrue( results.contains( resultA ) );
        assertTrue( results.contains( resultB ) );        
    }

    @Test
    public void testGetDataSetCompletenessByOrganisationUnitC()
    {
        dataSetA.getSources().add( unitE );
        dataSetA.getSources().add( unitF );
        dataSetA.getSources().add( unitG );
        dataSetA.getSources().add( unitH );

        dataSetIdA = dataSetService.addDataSet( dataSetA );

        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitE, onTimeA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitF, tooLateA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitG, onTimeA, "" ) );
        registrationService.saveCompleteDataSetRegistration( new CompleteDataSetRegistration( dataSetA, periodA, unitH, tooLateA, "" ) );

        Collection<DataSetCompletenessResult> results = completenessService.getDataSetCompleteness( periodIdA, unitIdsA, dataSetIdA );

        DataSetCompletenessResult resultA = new DataSetCompletenessResult( unitB.getName(), 2, 2, 1 );
        DataSetCompletenessResult resultB = new DataSetCompletenessResult( unitC.getName(), 2, 2, 1 );
                
        assertNotNull( results );
        assertEquals( 2, results.size() );
        assertTrue( results.contains( resultA ) );
        assertTrue( results.contains( resultB ) );        
    }
}
