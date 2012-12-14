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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.Timer;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultAnalyticsService
    implements AnalyticsService
{
    private static final String VALUE_NAME = "value";
    
    //TODO period aggregation for multiple period types
    //TODO hierarchy aggregation for org units at multiple levels
    //TODO indicator aggregation
    //TODO dimensional data analysis
    
    @Autowired
    private AnalyticsManager analyticsManager;
    
    public Grid getAggregatedDataValueTotals( DataQueryParams params ) throws Exception
    {
        Map<String, Double> map = getAggregatedDataValueMap( params );
        
        Grid grid = new ListGrid();
        
        for ( String col : params.getDimensionNames() )
        {
            grid.addHeader( new GridHeader( col, false, true ) );
            grid.addHeader( new GridHeader( VALUE_NAME, false, false ) );
        }
        
        for ( Map.Entry<String, Double> entry : map.entrySet() )
        {
            grid.addRow();
            grid.addValues( entry.getKey().split( AnalyticsManager.SEP ) );
            grid.addValue( entry.getValue() );
        }
        
        return grid;
    }
    
    public Map<String, Double> getAggregatedDataValueMap( DataQueryParams params ) throws Exception
    {
        Timer t = new Timer().start();

        List<DataQueryParams> queries = QueryPlanner.planQuery( params, 6 );
        
        t.getTime( "Planned query" );
        
        List<Future<Map<String, Double>>> futures = new ArrayList<Future<Map<String, Double>>>();
        
        Map<String, Double> map = new HashMap<String, Double>();
        
        for ( DataQueryParams query : queries )
        {
            futures.add( analyticsManager.getAggregatedDataValueTotals( query ) );
        }
        
        for ( Future<Map<String, Double>> future : futures )
        {
            Map<String, Double> taskValues = future.get();
            
            if ( taskValues != null )
            {
                map.putAll( taskValues );
            }
        }
        
        t.getTime( "Got aggregated values" );
        
        return map;
    }
}
