package org.hisp.dhis.analytics.data;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Cal;
import org.hisp.dhis.period.PeriodType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class QueryPlannerTest
    extends DhisSpringTest
{
    @Autowired
    private QueryPlanner queryPlanner;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // Fixture
    // -------------------------------------------------------------------------

    private DataElement deA;
    private DataElement deB;
    private DataElement deC;
    private DataElement deD;
    
    private OrganisationUnit ouA;
    private OrganisationUnit ouB;
    private OrganisationUnit ouC;
    private OrganisationUnit ouD;
    private OrganisationUnit ouE;

    @Override
    public void setUpTest()
    {
        deA = createDataElement( 'A' );
        deB = createDataElement( 'B' );
        deC = createDataElement( 'C' );
        deD = createDataElement( 'D' );
        
        dataElementService.addDataElement( deA );
        dataElementService.addDataElement( deB );
        dataElementService.addDataElement( deC );
        dataElementService.addDataElement( deD );
        
        ouA = createOrganisationUnit( 'A' );
        ouB = createOrganisationUnit( 'B' );
        ouC = createOrganisationUnit( 'C' );
        ouD = createOrganisationUnit( 'D' );
        ouE = createOrganisationUnit( 'E' );
        
        organisationUnitService.addOrganisationUnit( ouA );
        organisationUnitService.addOrganisationUnit( ouB );
        organisationUnitService.addOrganisationUnit( ouC );
        organisationUnitService.addOrganisationUnit( ouD );
        organisationUnitService.addOrganisationUnit( ouE );
    }

    // -------------------------------------------------------------------------
    // Tests
    // -------------------------------------------------------------------------
    
    /**
     * Query spans 2 partitions. Splits in 2 queries for each partition, then
     * splits in 2 queries on organisation units to satisfy optimal for a total 
     * of 4 queries.
     */
    @Test
    public void planQueryA()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( deA.getUid(), deB.getUid(), deC.getUid(), deD.getUid() ) );
        params.setOrganisationUnits( Arrays.asList( ouA.getUid(), ouB.getUid(), ouC.getUid(), ouD.getUid(), ouE.getUid() ) );
        params.setPeriods( Arrays.asList( "2000Q1", "2000Q2", "2000Q3", "2000Q4", "2001Q1", "2001Q2" ) );
        
        List<DataQueryParams> queries = queryPlanner.planQuery( params, 4 );
        
        assertEquals( 4, queries.size() );
        
        for ( DataQueryParams query : queries )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
        }
    }
    
    /**
     * Query spans 3 period types. Splits in 3 queries for each period type, then
     * splits in 2 queries on organisation units to satisfy optimal for a total 
     * of 6 queries.
     */
    @Test
    public void planQueryB()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( deA.getUid(), deB.getUid(), deC.getUid(), deD.getUid() ) );
        params.setOrganisationUnits( Arrays.asList( ouA.getUid(), ouB.getUid(), ouC.getUid(), ouD.getUid(), ouE.getUid() ) );
        params.setPeriods( Arrays.asList( "2000Q1", "2000Q2", "2000", "200002", "200003", "200004" ) );
        
        List<DataQueryParams> queries = queryPlanner.planQuery( params, 4 );
        
        assertEquals( 6, queries.size() );
        
        for ( DataQueryParams query : queries )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
        }
    }
    
    /**
     * Query spans 3 organisation unit levels. Splits in 3 queries for each level,
     * then splits in 2 queries on organisation units to satisfy optimal for a total 
     * of 5 queries, as there are only 5 organisation units in total.
     */
    @Test
    public void planQueryC()
    {
        ouB.setParent( ouA );
        ouC.setParent( ouA );
        ouD.setParent( ouB );
        ouE.setParent( ouC );
        ouA.getChildren().add( ouB );
        ouA.getChildren().add( ouC );
        ouD.getChildren().add( ouB );
        ouC.getChildren().add( ouE );
        organisationUnitService.updateOrganisationUnit( ouA );
        organisationUnitService.updateOrganisationUnit( ouB );
        organisationUnitService.updateOrganisationUnit( ouC );
        organisationUnitService.updateOrganisationUnit( ouD );
        organisationUnitService.updateOrganisationUnit( ouE );
        
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( deA.getUid(), deB.getUid(), deC.getUid(), deD.getUid() ) );
        params.setOrganisationUnits( Arrays.asList( ouA.getUid(), ouB.getUid(), ouC.getUid(), ouD.getUid(), ouE.getUid() ) );
        params.setPeriods( Arrays.asList( "2000Q1", "2000Q2", "2000Q3" ) );
        
        List<DataQueryParams> queries = queryPlanner.planQuery( params, 4 );
        
        assertEquals( 5, queries.size() );
        
        for ( DataQueryParams query : queries )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
        }
    }
    
    /**
     * Splits on 3 data elements.
     */
    @Test
    public void planQueryD()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( deA.getUid(), deB.getUid(), deC.getUid() ) );
        params.setOrganisationUnits( Arrays.asList( ouA.getUid() ) );
        params.setPeriods( Arrays.asList( "200001", "200002", "200003", "200004", "200005", "200006", "200007", "200008", "200009" ) );
        
        List<DataQueryParams> queries = queryPlanner.planQuery( params, 4 );
        
        assertEquals( 3, queries.size() );
        
        for ( DataQueryParams query : queries )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
        }
    }
    
    /**
     * Splits on 3 data elements. No organisation units specified.
     */
    @Test
    public void planQueryE()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( deA.getUid(), deB.getUid(), deC.getUid() ) );
        params.setPeriods( Arrays.asList( "200001", "200002", "200003", "200004", "200005", "200006", "200007", "200008", "200009" ) );

        List<DataQueryParams> queries = queryPlanner.planQuery( params, 4 );

        assertEquals( 3, queries.size() );

        for ( DataQueryParams query : queries )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
        }
    }

    /**
     * Splits on 5 organisation units. No data elements units specified.
     */
    @Test
    public void planQueryF()
    {
        DataQueryParams params = new DataQueryParams();
        params.setOrganisationUnits( Arrays.asList( ouA.getUid(), ouB.getUid(), ouC.getUid(), ouD.getUid(), ouE.getUid() ) );
        params.setPeriods( Arrays.asList( "200001", "200002", "200003", "200004", "200005", "200006", "200007", "200008", "200009" ) );

        List<DataQueryParams> queries = queryPlanner.planQuery( params, 4 );

        assertEquals( 3, queries.size() );

        for ( DataQueryParams query : queries )
        {
            assertTrue( samePeriodType( query.getPeriods() ) );
            assertTrue( samePartition( query.getPeriods() ) );
        }
    }
    
    /**
     * Expected to fail because of no periods specified.
     */
    @Test( expected = IllegalArgumentException.class )
    public void planQueryG()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( deA.getUid(), deB.getUid(), deC.getUid() ) );
        params.setOrganisationUnits( Arrays.asList( ouA.getUid(), ouB.getUid(), ouC.getUid(), ouD.getUid(), ouE.getUid() ) );

        queryPlanner.planQuery( params, 4 );
    }

    /**
     * Query filters span 2 partitions. Splits in 2 queries for each partition, 
     * then splits in 2 queries on organisation units to satisfy optimal for a 
     * total of 4 queries.
     */
    @Test
    public void planQueryH()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( deA.getUid(), deB.getUid(), deC.getUid(), deD.getUid() ) );
        params.setOrganisationUnits( Arrays.asList( ouA.getUid(), ouB.getUid(), ouC.getUid(), ouD.getUid(), ouE.getUid() ) );
        params.setFilterPeriods( Arrays.asList( "2000Q1", "2000Q2", "2000Q3", "2000Q4", "2001Q1", "2001Q2" ) );
        
        List<DataQueryParams> queries = queryPlanner.planQuery( params, 4 );
        
        assertEquals( 4, queries.size() );
    }

    /**
     * Query spans 3 period types. Splits in 3 queries for each period type, then
     * splits in 2 queries on data elements units to satisfy optimal for a total 
     * of 6 queries.
     */
    @Test
    public void planQueryI()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( deA.getUid(), deB.getUid(), deC.getUid(), deD.getUid() ) );
        params.setFilterOrganisationUnits( Arrays.asList( ouA.getUid(), ouB.getUid(), ouC.getUid(), ouD.getUid(), ouE.getUid() ) );
        params.setPeriods( Arrays.asList( "2000Q1", "2000Q2", "2000", "200002", "200003", "200004" ) );
        
        List<DataQueryParams> queries = queryPlanner.planQuery( params, 4 );
        
        assertEquals( 6, queries.size() );
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private static boolean samePeriodType( List<String> isoPeriods )
    {
        Iterator<String> periods = new ArrayList<String>( isoPeriods ).iterator();
        
        PeriodType first = PeriodType.getPeriodTypeFromIsoString( periods.next() );
        
        while ( periods.hasNext() )
        {
            PeriodType next = PeriodType.getPeriodTypeFromIsoString( periods.next() );
            
            if ( !first.equals( next ) )
            {   
                return false;
            }
        }
        
        return true;
    }
    
    private static boolean samePartition( List<String> isoPeriods )
    {
        Iterator<String> periods = new ArrayList<String>( isoPeriods ).iterator();
        
        int year = new Cal().set( PeriodType.getPeriodFromIsoString( periods.next() ).getStartDate() ).getYear();
        
        while ( periods.hasNext() )
        {
            int next = new Cal().set( PeriodType.getPeriodFromIsoString( periods.next() ).getStartDate() ).getYear();
            
            if ( year != next )
            {   
                return false;
            }
        }
        
        return true;
    }
}
