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

import java.util.Arrays;
import java.util.List;

import org.hisp.dhis.analytics.DataQueryParams;
import org.junit.Test;

import static org.hisp.dhis.analytics.DataQueryParams.*;
import static org.junit.Assert.*;

public class QueryPlannerTest
{
    @Test
    public void getPartitionDimension()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( "a", "b", "c", "d" ) );
        params.setOrganisationUnits( Arrays.asList( "a", "b", "c", "d", "e" ) );
        params.setPeriods( Arrays.asList( "2000Q1", "2000Q2", "2000Q3", "2000Q4", "2001Q1", "2001Q2" ) );
        
        assertEquals( DATAELEMENT_DIM_ID, QueryPlanner.getPartitionDimension( params, 3 ) );
        assertEquals( DATAELEMENT_DIM_ID, QueryPlanner.getPartitionDimension( params, 4 ) );
        assertEquals( ORGUNIT_DIM_ID, QueryPlanner.getPartitionDimension( params, 5 ) );
        assertEquals( PERIOD_DIM_ID, QueryPlanner.getPartitionDimension( params, 6 ) );
        assertEquals( PERIOD_DIM_ID, QueryPlanner.getPartitionDimension( params, 7 ) );
    }
    
    @Test
    public void planQuery()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( "a", "b", "c", "d" ) );
        params.setOrganisationUnits( Arrays.asList( "a", "b", "c", "d", "e" ) );
        params.setPeriods( Arrays.asList( "2000Q1", "2000Q2", "2000Q3", "2000Q4", "2001Q1", "2001Q2" ) );
        
        List<DataQueryParams> queries = QueryPlanner.planQuery( params, 4 );
        
        assertEquals( 4, queries.size() );
    }
}
