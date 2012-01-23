package org.hisp.dhis.datalock;

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
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class DataSetLockServiceTest
    extends DhisSpringTest
{
    protected DataSetLockService dataSetLockService;

    protected DataSet dataSetA;

    protected DataSet dataSetB;

    protected DataSet dataSetC;

    protected Period periodA;

    protected Period periodB;

    protected Period periodC;

    protected Period periodD;

    protected OrganisationUnit unitA;

    protected OrganisationUnit unitB;

    protected OrganisationUnit unitC;

    protected OrganisationUnit unitD;

    protected OrganisationUnit unitE;

    protected OrganisationUnit unitF;

    protected OrganisationUnit unitG;

    protected OrganisationUnit unitH;

    protected OrganisationUnit unitI;

    protected String user1;

    protected String user2;

    protected String user3;

    protected String user4;

    protected String user5;

    @Override
    public void setUpTest()
        throws Exception
    {
        dataSetService = (DataSetService) getBean( DataSetService.ID );

        dataSetLockService = (DataSetLockService) getBean( DataSetLockService.ID );

        periodService = (PeriodService) getBean( PeriodService.ID );

        organisationUnitService = (OrganisationUnitService) getBean( OrganisationUnitService.ID );

        // ---------------------------------------------------------------------
        // Setup Periods
        // ---------------------------------------------------------------------

        Iterator<PeriodType> periodTypeIt = periodService.getAllPeriodTypes().iterator();
        PeriodType periodTypeA = periodTypeIt.next(); // Daily
        PeriodType periodTypeB = periodTypeIt.next(); // Weekly

        Date mar01 = super.getDate( 2005, 3, 1 );
        Date mar31 = super.getDate( 2005, 3, 31 );
        Date apr01 = super.getDate( 2005, 4, 1 );
        Date apr30 = super.getDate( 2005, 4, 30 );
        Date may01 = super.getDate( 2005, 5, 1 );
        Date may31 = super.getDate( 2005, 5, 31 );

        periodA = super.createPeriod( periodTypeA, mar01, mar31 );
        periodB = super.createPeriod( periodTypeA, apr01, apr30 );
        periodC = super.createPeriod( periodTypeB, mar01, may31 );
        periodD = super.createPeriod( periodTypeB, may01, may31 );

        periodService.addPeriod( periodA );
        periodService.addPeriod( periodB );
        periodService.addPeriod( periodC );
        periodService.addPeriod( periodD );

        // ---------------------------------------------------------------------
        // Setup DataSets
        // ---------------------------------------------------------------------

        dataSetA = super.createDataSet( 'A', periodTypeA );
        dataSetB = super.createDataSet( 'B', periodTypeB );
        dataSetC = super.createDataSet( 'C', periodTypeB );

        dataSetService.addDataSet( dataSetA );
        dataSetService.addDataSet( dataSetB );
        dataSetService.addDataSet( dataSetC );

        // ---------------------------------------------------------------------
        // Setup OrganisationUnits
        // ---------------------------------------------------------------------

        unitA = super.createOrganisationUnit( 'A' );
        unitB = super.createOrganisationUnit( 'B', unitA );
        unitC = super.createOrganisationUnit( 'C', unitA );
        unitD = super.createOrganisationUnit( 'D', unitB );
        unitE = super.createOrganisationUnit( 'E', unitB );
        unitF = super.createOrganisationUnit( 'F', unitB );
        unitG = super.createOrganisationUnit( 'G', unitF );
        unitH = super.createOrganisationUnit( 'H', unitF );
        unitI = super.createOrganisationUnit( 'I' );

        organisationUnitService.addOrganisationUnit( unitA );
        organisationUnitService.addOrganisationUnit( unitB );
        organisationUnitService.addOrganisationUnit( unitC );
        organisationUnitService.addOrganisationUnit( unitD );
        organisationUnitService.addOrganisationUnit( unitE );
        organisationUnitService.addOrganisationUnit( unitF );
        organisationUnitService.addOrganisationUnit( unitG );
        organisationUnitService.addOrganisationUnit( unitH );
        organisationUnitService.addOrganisationUnit( unitI );

        // ---------------------------------------------------------------------
        // Setup Users
        // ---------------------------------------------------------------------

        user1 = "admin";
        user2 = "User2";
        user3 = "User3";
        user4 = "User4";
        user5 = "User5";
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    public void initOrgunitSet( Set<OrganisationUnit> units, OrganisationUnit... sources )
    {
        for ( OrganisationUnit s : sources )
        {
            units.add( s );
        }
    }

    private void assertEq( DataSetLock dataSetLock, DataSet dataSet, Period period, Set<OrganisationUnit> sources )
    {
        assertEquals( dataSet, dataSetLock.getDataSet() );
        assertEquals( period, dataSetLock.getPeriod() );
        assertEquals( sources, dataSetLock.getSources() );
    }

    // -------------------------------------------------------------------------
    // DataSetLock
    // -------------------------------------------------------------------------

    // Disabled for now. Will probably be removed altogether with the new dataset locking service.
    public void testAddDataSetLock()
    {
        Set<OrganisationUnit> lockSources = new HashSet<OrganisationUnit>();
        initOrgunitSet( lockSources, unitB, unitC, unitI );

        DataSetLock dataSetLockAA = createDataSetLock( dataSetA, periodA, lockSources, user1, new Date() );
        DataSetLock dataSetLockAB = createDataSetLock( dataSetA, periodB, lockSources, user1, new Date() );
        DataSetLock dataSetLockBA = createDataSetLock( dataSetB, periodA, lockSources, user1, new Date() );
        DataSetLock dataSetLockBB = createDataSetLock( dataSetB, periodB, lockSources, user1, new Date() );

        int idAA = dataSetLockService.addDataSetLock( dataSetLockAA );
        int idAB = dataSetLockService.addDataSetLock( dataSetLockAB );
        int idBA = dataSetLockService.addDataSetLock( dataSetLockBA );
        int idBB = dataSetLockService.addDataSetLock( dataSetLockBB );

        dataSetLockAA = dataSetLockService.getDataSetLock( idAA );
        dataSetLockAB = dataSetLockService.getDataSetLock( idAB );
        dataSetLockBA = dataSetLockService.getDataSetLock( idBA );
        dataSetLockBB = dataSetLockService.getDataSetLock( idBB );

        assertEquals( idAA, dataSetLockAA.getId() );
        assertEq( dataSetLockAA, dataSetA, periodA, lockSources );
        assertEquals( idAB, dataSetLockAB.getId() );
        assertEq( dataSetLockAB, dataSetA, periodB, lockSources );

        assertEquals( idBA, dataSetLockBA.getId() );
        assertEq( dataSetLockBA, dataSetB, periodA, lockSources );
        assertEquals( idBB, dataSetLockBB.getId() );
        assertEq( dataSetLockBB, dataSetB, periodB, lockSources );
    }

    @Test
    public void testUpdateDataSetLock()
    {
        Set<OrganisationUnit> lockSources = new HashSet<OrganisationUnit>();
        initOrgunitSet( lockSources, unitB, unitC );

        DataSetLock dataSetLock = createDataSetLock( dataSetC, periodC, lockSources, user2, new Date() );

        int id = dataSetLockService.addDataSetLock( dataSetLock );
        dataSetLock = dataSetLockService.getDataSetLock( id );

        assertEq( dataSetLock, dataSetC, periodC, lockSources );

        lockSources.add( unitI );
        dataSetLock.getSources().add( unitI );

        dataSetLockService.updateDataSetLock( dataSetLock );
        dataSetLock = dataSetLockService.getDataSetLock( id );

        assertTrue( dataSetLock.getSources().containsAll( lockSources ) );
    }

    @Test
    public void testDeleteAndGetDataSetLock()
    {
        Set<OrganisationUnit> lockSources = new HashSet<OrganisationUnit>();
        initOrgunitSet( lockSources, unitB, unitC );

        DataSetLock dataSetLockAA = createDataSetLock( dataSetA, periodA, lockSources, user1, new Date() );
        DataSetLock dataSetLockAB = createDataSetLock( dataSetA, periodB, lockSources, user1, new Date() );

        int idAA = dataSetLockService.addDataSetLock( dataSetLockAA );
        int idAB = dataSetLockService.addDataSetLock( dataSetLockAB );

        assertNotNull( dataSetLockService.getDataSetLock( idAA ) );
        assertNotNull( dataSetLockService.getDataSetLock( idAB ) );

        dataSetLockService.deleteDataSetLock( dataSetLockService.getDataSetLock( idAA ) );

        assertNull( dataSetLockService.getDataSetLock( idAA ) );
        assertNotNull( dataSetLockService.getDataSetLock( idAB ) );

        dataSetLockService.deleteDataSetLock( dataSetLockService.getDataSetLock( idAB ) );

        assertNull( dataSetLockService.getDataSetLock( idAA ) );
        assertNull( dataSetLockService.getDataSetLock( idAB ) );
    }

    @Test
    public void testGetDataSetLockByDataSet()
        throws Exception
    {
        Set<OrganisationUnit> lockSources = new HashSet<OrganisationUnit>();
        initOrgunitSet( lockSources, unitB, unitC, unitI );

        DataSetLock dataSetLockAB = createDataSetLock( dataSetA, periodB, lockSources, user1, new Date() );
        DataSetLock dataSetLockBA = createDataSetLock( dataSetB, periodA, lockSources, user1, new Date() );
        DataSetLock dataSetLockBB = createDataSetLock( dataSetB, periodB, lockSources, user1, new Date() );

        int idAB = dataSetLockService.addDataSetLock( dataSetLockAB );
        int idBA = dataSetLockService.addDataSetLock( dataSetLockBA );
        int idBB = dataSetLockService.addDataSetLock( dataSetLockBB );

        Collection<DataSetLock> dataSetLocks1 = new HashSet<DataSetLock>( dataSetLockService
            .getDataSetLockByDataSet( dataSetA ) );

        assertTrue( dataSetLocks1.remove( dataSetLockService.getDataSetLock( idAB ) ) );

        dataSetLocks1 = new HashSet<DataSetLock>( dataSetLockService.getDataSetLockByDataSet( dataSetB ) );
        Collection<DataSetLock> dataSetLocks2 = new HashSet<DataSetLock>();
        dataSetLocks2.add( dataSetLockService.getDataSetLock( idBA ) );
        dataSetLocks2.add( dataSetLockService.getDataSetLock( idBB ) );

        assertTrue( dataSetLocks1.containsAll( dataSetLocks2 ) );

    }

    @Test
    public void testGetDataSetLockByPeriod()
        throws Exception
    {
        Set<OrganisationUnit> lockSources = new HashSet<OrganisationUnit>();
        initOrgunitSet( lockSources, unitB, unitC, unitI );

        DataSetLock dataSetLockAB = createDataSetLock( dataSetA, periodB, lockSources, user1, new Date() );
        DataSetLock dataSetLockBA = createDataSetLock( dataSetB, periodA, lockSources, user1, new Date() );
        DataSetLock dataSetLockBB = createDataSetLock( dataSetB, periodB, lockSources, user1, new Date() );

        int idAB = dataSetLockService.addDataSetLock( dataSetLockAB );
        int idBA = dataSetLockService.addDataSetLock( dataSetLockBA );
        int idBB = dataSetLockService.addDataSetLock( dataSetLockBB );

        Collection<DataSetLock> dataSetLocks1 = new HashSet<DataSetLock>( dataSetLockService
            .getDataSetLockByPeriod( periodB ) );
        Collection<DataSetLock> dataSetLocks2 = new HashSet<DataSetLock>();

        dataSetLocks2.add( dataSetLockService.getDataSetLock( idAB ) );
        dataSetLocks2.add( dataSetLockService.getDataSetLock( idBB ) );

        assertTrue( dataSetLocks1.removeAll( dataSetLocks2 ) );

        dataSetLocks2.clear();
        dataSetLocks2.add( dataSetLockService.getDataSetLock( idBA ) );
        assertTrue( dataSetLocks2.containsAll( dataSetLockService.getDataSetLockByPeriod( periodA ) ) );

    }

    @Test
    public void testGetAllDataSetLocks()
    {
        Set<OrganisationUnit> lockSources = new HashSet<OrganisationUnit>();
        initOrgunitSet( lockSources, unitB, unitC, unitI );

        DataSetLock dataSetLockAA = createDataSetLock( dataSetA, periodA, lockSources, user1, new Date() );
        DataSetLock dataSetLockAB = createDataSetLock( dataSetA, periodB, lockSources, user1, new Date() );
        DataSetLock dataSetLockBA = createDataSetLock( dataSetB, periodA, lockSources, user2, new Date() );
        DataSetLock dataSetLockBB = createDataSetLock( dataSetB, periodB, lockSources, user2, new Date() );
        DataSetLock dataSetLockCC = createDataSetLock( dataSetC, periodC, lockSources, user3, new Date() );

        dataSetLockService.addDataSetLock( dataSetLockAA );
        dataSetLockService.addDataSetLock( dataSetLockAB );
        dataSetLockService.addDataSetLock( dataSetLockBA );
        dataSetLockService.addDataSetLock( dataSetLockBB );
        dataSetLockService.addDataSetLock( dataSetLockCC );

        Collection<DataSetLock> dataSetLocks = dataSetLockService.getAllDataSetLocks();

        assertEquals( dataSetLocks.size(), 5 );
        assertTrue( dataSetLocks.contains( dataSetLockAA ) );
        assertTrue( dataSetLocks.contains( dataSetLockAB ) );
        assertTrue( dataSetLocks.contains( dataSetLockBA ) );
        assertTrue( dataSetLocks.contains( dataSetLockBB ) );
        assertTrue( dataSetLocks.contains( dataSetLockCC ) );
    }

    @Test
    public void testGetDataSetLocks()
    {
        Set<OrganisationUnit> lockSources = new HashSet<OrganisationUnit>();
        initOrgunitSet( lockSources, unitB, unitC, unitI );

        DataSetLock dataSetLockAA = createDataSetLock( dataSetA, periodA, lockSources, user1, new Date() );
        DataSetLock dataSetLockBB = createDataSetLock( dataSetB, periodB, lockSources, user2, new Date() );
        DataSetLock dataSetLockCC = createDataSetLock( dataSetC, periodC, lockSources, user3, new Date() );

        int idAA = dataSetLockService.addDataSetLock( dataSetLockAA );
        int idBB = dataSetLockService.addDataSetLock( dataSetLockBB );
        int idCC = dataSetLockService.addDataSetLock( dataSetLockCC );

        Collection<Integer> dataSetLockIds = new ArrayList<Integer>();
        Collection<DataSetLock> dataSetLocks = new HashSet<DataSetLock>();

        dataSetLockIds.add( idAA );
        dataSetLockIds.add( idBB );
        dataSetLockIds.add( idCC );

        dataSetLocks = dataSetLockService.getAllDataSetLocks();

        assertEquals( dataSetLockIds.size(), 3 );
        assertTrue( dataSetLocks.containsAll( dataSetLockService.getDataSetLocks( dataSetLockIds ) ) );
    }

    @Test
    public void testGetDataSetLockByDataSetAndPeriod()
    {
        Set<OrganisationUnit> lockSources = new HashSet<OrganisationUnit>();
        initOrgunitSet( lockSources, unitB, unitC, unitI );

        DataSetLock dataSetLock = createDataSetLock( dataSetA, periodB, lockSources, user1, new Date() );

        int id = dataSetLockService.addDataSetLock( dataSetLock );
        dataSetLock = dataSetLockService.getDataSetLock( id );

        assertNotNull( dataSetLockService.getDataSetLockByDataSetAndPeriod( dataSetLock.getDataSet(), dataSetLock
            .getPeriod() ) );

    }

    @Test
    public void testGetDataSetLockByDataSetPeriodAndSource()
    {
        Set<OrganisationUnit> lockSources = new HashSet<OrganisationUnit>();
        initOrgunitSet( lockSources, unitB, unitC );

        DataSetLock dataSetLockAA = createDataSetLock( dataSetA, periodA, lockSources, user1, new Date() );

        int idAA = dataSetLockService.addDataSetLock( dataSetLockAA );
        dataSetLockAA = dataSetLockService.getDataSetLock( idAA );

        assertNotNull( dataSetLockService.getDataSetLockByDataSetPeriodAndSource( dataSetA, periodA, unitC ) );

        lockSources.add( unitI );
        lockSources.remove( unitC );
        DataSetLock dataSetLockBB = createDataSetLock( dataSetB, periodB, lockSources, user2, new Date() );

        dataSetLockService.addDataSetLock( dataSetLockBB );
        assertNull( dataSetLockService.getDataSetLockByDataSetPeriodAndSource( dataSetB, periodB, unitC ) );
    }

    @Test
    public void testGetDataSetLocksBySource()
    {
        Set<OrganisationUnit> lockSources = new HashSet<OrganisationUnit>();
        initOrgunitSet( lockSources, unitB, unitC, unitI );

        DataSetLock dataSetLockAA = createDataSetLock( dataSetA, periodA, lockSources, user1, new Date() );
        DataSetLock dataSetLockBB = createDataSetLock( dataSetB, periodB, lockSources, user2, new Date() );
        DataSetLock dataSetLockCC = createDataSetLock( dataSetC, periodC, lockSources, user3, new Date() );

        int idAA = dataSetLockService.addDataSetLock( dataSetLockAA );
        int idBB = dataSetLockService.addDataSetLock( dataSetLockBB );
        int idCC = dataSetLockService.addDataSetLock( dataSetLockCC );

        Collection<DataSetLock> dataSetLocks = new HashSet<DataSetLock>( dataSetLockService
            .getDataSetLocksBySource( unitI ) );

        assertEquals( 3, dataSetLocks.size() );
        assertTrue( dataSetLocks.contains( dataSetLockService.getDataSetLock( idAA ) ) );
        assertTrue( dataSetLocks.contains( dataSetLockService.getDataSetLock( idBB ) ) );
        assertTrue( dataSetLocks.contains( dataSetLockService.getDataSetLock( idCC ) ) );
    }
}
