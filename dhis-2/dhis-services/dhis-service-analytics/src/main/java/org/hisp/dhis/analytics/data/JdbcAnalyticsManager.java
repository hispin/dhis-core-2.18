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

import static org.hisp.dhis.analytics.AggregationType.AVERAGE_INT_AGGREGATION;
import static org.hisp.dhis.analytics.AggregationType.AVERAGE_BOOL;
import static org.hisp.dhis.analytics.AggregationType.AVERAGE_INT_DISAGGREGATION;
import static org.hisp.dhis.analytics.AggregationType.COUNT;
import static org.hisp.dhis.analytics.DataQueryParams.DIMENSION_SEP;
import static org.hisp.dhis.analytics.DataQueryParams.VALUE_ID;
import static org.hisp.dhis.common.IdentifiableObjectUtils.getUids;
import static org.hisp.dhis.system.util.TextUtils.getQuotedCommaDelimitedString;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.Dimension;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ListMap;
import org.hisp.dhis.system.util.SqlHelper;
import org.hisp.dhis.system.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.Assert;

/**
 * This class is responsible for producing aggregated data values. It reads data
 * from the analytics table. Organisation units provided as arguments must be on
 * the same level in the hierarchy.
 * 
 * @author Lars Helge Overland
 */
public class JdbcAnalyticsManager
    implements AnalyticsManager
{
    //TODO optimize when all options in dimensions are selected
    
    private static final Log log = LogFactory.getLog( JdbcAnalyticsManager.class );
        
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------
    
    @Async
    public Future<Map<String, Double>> getAggregatedDataValues( DataQueryParams params )
    {
        ListMap<IdentifiableObject, IdentifiableObject> dataPeriodAggregationPeriodMap = params.getDataPeriodAggregationPeriodMap();
        params.replaceAggregationPeriodsWithDataPeriods( dataPeriodAggregationPeriodMap );
        
        params.populateDimensionNames();
        
        List<Dimension> selectDimensions = params.getSelectDimensions();
        List<Dimension> queryDimensions = params.getQueryDimensions();
        
        SqlHelper sqlHelper = new SqlHelper();

        int days = PeriodType.getPeriodTypeByName( params.getPeriodType() ).getFrequencyOrder();
        
        String sql = "select " + getCommaDelimitedString( selectDimensions ) + ", ";
        
        if ( params.isAggregationType( AVERAGE_INT_AGGREGATION ) )
        {
            sql += "sum(daysxvalue) / " + days;
        }
        else if ( params.isAggregationType( AVERAGE_BOOL ) )
        {
            sql += "sum(daysxvalue) / sum(daysno) * 100";
        }
        else if ( params.isAggregationType( COUNT ) )
        {
            sql += "count(value)";
        }
        else // SUM, AVERAGE_DISAGGREGATION and undefined //TODO
        {
            sql += "sum(value)";
        }
        
        sql += " as value from " + params.getTableName() + " ";
        
        for ( Dimension dim : queryDimensions )
        {
            if ( !dim.isAllOptions() )
            {
                sql += sqlHelper.whereAnd() + " " + dim.getDimensionName() + " in (" + getQuotedCommaDelimitedString( getUids( dim.getOptions() ) ) + " ) ";
            }
        }

        for ( Dimension filter : params.getFilters() )
        {
            if ( !filter.isAllOptions() )
            {
                sql += sqlHelper.whereAnd() + " " + filter.getDimensionName() + " in (" + getQuotedCommaDelimitedString( getUids( filter.getOptions() ) ) + " ) ";
            }
        }
        
        sql += "group by " + getCommaDelimitedString( selectDimensions );
    
        log.info( sql );
        
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet( sql );
        
        Map<String, Double> map = new HashMap<String, Double>();
        
        while ( rowSet.next() )
        {
            StringBuilder key = new StringBuilder();
            
            for ( Dimension dim : selectDimensions )
            {
                key.append( rowSet.getString( dim.getDimensionName() ) + DIMENSION_SEP );
            }
            
            key.deleteCharAt( key.length() - 1 );
            
            Double value = rowSet.getDouble( VALUE_ID );

            map.put( key.toString(), value );
        }
        
        replaceDataPeriodsWithAggregationPeriods( map, params, dataPeriodAggregationPeriodMap );
        
        return new AsyncResult<Map<String, Double>>( map );
    }

    public void replaceDataPeriodsWithAggregationPeriods( Map<String, Double> dataValueMap, DataQueryParams params, ListMap<IdentifiableObject, IdentifiableObject> dataPeriodAggregationPeriodMap )
    {
        if ( params.isAggregationType( AVERAGE_INT_DISAGGREGATION ) )
        {
            int periodIndex = params.getPeriodDimensionIndex();
            
            Set<String> keys = new HashSet<String>( dataValueMap.keySet() );
            
            for ( String key : keys )
            {
                String[] keyArray = key.split( DIMENSION_SEP );
                
                Assert.notNull( keyArray[periodIndex], keyArray.toString() );
                
                List<IdentifiableObject> periods = dataPeriodAggregationPeriodMap.get( PeriodType.getPeriodFromIsoString( keyArray[periodIndex] ) );
                
                Assert.notNull( periods, dataPeriodAggregationPeriodMap.toString() );
                
                Double value = dataValueMap.get( key );
                
                for ( IdentifiableObject period : periods )
                {
                    String[] keyCopy = keyArray.clone();
                    keyCopy[periodIndex] = ((Period) period).getIsoDate();
                    dataValueMap.put( TextUtils.toString( keyCopy, DIMENSION_SEP ), value );
                }
                
                dataValueMap.remove( key );
            }
        }
    }
    
    private static String getCommaDelimitedString( Collection<Dimension> dimensions )
    {
        final StringBuilder builder = new StringBuilder();
        
        if ( dimensions != null && !dimensions.isEmpty() )
        {
            for ( Dimension dimension : dimensions )
            {
                builder.append( dimension.getDimensionName() ).append( "," );
            }
            
            return builder.substring( 0, builder.length() - 1 );
        }
        
        return builder.toString();
    }
}
