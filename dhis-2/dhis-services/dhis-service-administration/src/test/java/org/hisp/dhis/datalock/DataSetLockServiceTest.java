package org.hisp.dhis.datalock;

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
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.source.Source;
import org.junit.Test;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class DataSetLockServiceTest
    extends DataSetLockTest
{
    @Override
    public void setUpTest()
        throws Exception
    {
        setUpDataSetLockTest();
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void assertEq( DataSetLock dataSetLock, DataSet dataSet, Period period, Set<Source> sources )
    {
        assertEquals( dataSet, dataSetLock.getDataSet() );
        assertEquals( period, dataSetLock.getPeriod() );
        assertEquals( sources, dataSetLock.getSources() );
    }

    // -------------------------------------------------------------------------
    // DataSetLock
    // -------------------------------------------------------------------------

    @Test
    public void testAddDataSetLock()
    {
        Set<Source> lockSources = new HashSet<Source>();
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
        Set<Source> lockSources = new HashSet<Source>();
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
        Set<Source> lockSources = new HashSet<Source>();
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
        Set<Source> lockSources = new HashSet<Source>();
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
        Set<Source> lockSources = new HashSet<Source>();
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
        Set<Source> lockSources = new HashSet<Source>();
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
        Set<Source> lockSources = new HashSet<Source>();
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
        Set<Source> lockSources = new HashSet<Source>();
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
        Set<Source> lockSources = new HashSet<Source>();
        initOrgunitSet( lockSources, unitB, unitC );

        DataSetLock dataSetLockAA = createDataSetLock( dataSetA, periodA, lockSources, user1, new Date() );

        int idAA = dataSetLockService.addDataSetLock( dataSetLockAA );
        dataSetLockAA = dataSetLockService.getDataSetLock( idAA );

        // assertSame( dataSetLockAA,
        // dataSetLockService.getDataSetLockByDataSetPeriodAndSource( dataSetA,
        // periodA, unitC ) );
        assertNull( dataSetLockService.getDataSetLockByDataSetPeriodAndSource( dataSetA, periodA, unitC ) );

        lockSources.add( unitI );
        lockSources.remove( unitC );
        DataSetLock dataSetLockBB = createDataSetLock( dataSetB, periodB, lockSources, user2, new Date() );

        dataSetLockService.addDataSetLock( dataSetLockBB );
        assertNull( dataSetLockService.getDataSetLockByDataSetPeriodAndSource( dataSetB, periodB, unitC ) );
    }

    @Test
    public void testGetDataSetLocksBySource()
    {
        Set<Source> lockSources = new HashSet<Source>();
        initOrgunitSet( lockSources, unitB, unitC, unitI );

        DataSetLock dataSetLockAA = createDataSetLock( dataSetA, periodA, lockSources, user1, new Date() );
        DataSetLock dataSetLockBB = createDataSetLock( dataSetB, periodB, lockSources, user2, new Date() );
        DataSetLock dataSetLockCC = createDataSetLock( dataSetC, periodC, lockSources, user3, new Date() );

        int idAA = dataSetLockService.addDataSetLock( dataSetLockAA );
        int idBB = dataSetLockService.addDataSetLock( dataSetLockBB );
        int idCC = dataSetLockService.addDataSetLock( dataSetLockCC );

        Collection<DataSetLock> dataSetLocks = new HashSet<DataSetLock>( dataSetLockService
            .getDataSetLocksBySource( unitI ) );

        //assertEquals( 3, dataSetLocks.size() );
        assertNotSame( 3, dataSetLocks.size() );
        assertTrue( !dataSetLocks.contains( dataSetLockService.getDataSetLock( idAA ) ) );
        assertTrue( !dataSetLocks.contains( dataSetLockService.getDataSetLock( idBB ) ) );
        assertTrue( !dataSetLocks.contains( dataSetLockService.getDataSetLock( idCC ) ) );
    }
}
