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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.hisp.dhis.aggregation.AggregatedDataValue;
import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.system.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultAnalyticsService
    implements AnalyticsService
{
    //TODO select from correct shard
    //TODO period aggregation for multiple period types
    //TODO hierarchy aggregation for org units at multiple levels
    //TODO indicator aggregation
    
    @Autowired
    private AnalyticsManager analyticsManager;
    
    public List<AggregatedDataValue> getAggregatedDataValueTotals( DataQueryParams params ) throws Exception
    {
        Timer t = new Timer().start();
        
        List<DataQueryParams> queries = QueryPlanner.planQuery( params, 6 );
        
        List<Future<List<AggregatedDataValue>>> futures = new ArrayList<Future<List<AggregatedDataValue>>>();
        
        List<AggregatedDataValue> values = new ArrayList<AggregatedDataValue>();
        
        for ( DataQueryParams query : queries )
        {
            futures.add( analyticsManager.getAggregatedDataValueTotals( query ) );
        }
        
        for ( Future<List<AggregatedDataValue>> future : futures )
        {
            values.addAll( future.get() );
        }
        
        t.getTime( "Got aggregated values" );
        
        return values;
    }
}
