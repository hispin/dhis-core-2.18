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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.analytics.AggregationType;
import org.hisp.dhis.analytics.AnalyticsManager;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.system.util.ListMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AnalyticsManagerTest
    extends DhisSpringTest
{
    @Autowired
    private AnalyticsManager analyticsManager;
    
    @Test
    public void testReplace()
    {
        DataQueryParams params = new DataQueryParams();
        params.setDataElements( Arrays.asList( "de1", "de2" ) );
        params.setPeriods( Arrays.asList( "2012" ) );
        params.setOrganisationUnits( Arrays.asList( "ou1" ) );
        params.setDataPeriodType( new YearlyPeriodType() );
        params.setAggregationType( AggregationType.AVERAGE_DISAGGREGATION );
        
        Map<String, Double> dataValueMap = new HashMap<String, Double>();
        dataValueMap.put( "de1-2012-ou1", 1d );
        dataValueMap.put( "de2-2012-ou1", 1d );
        
        ListMap<String, String> dataPeriodAggregationPeriodMap = new ListMap<String, String>();
        dataPeriodAggregationPeriodMap.putValue( "2012", "2012Q1" );
        dataPeriodAggregationPeriodMap.putValue( "2012", "2012Q2" );
        dataPeriodAggregationPeriodMap.putValue( "2012", "2012Q3" );
        dataPeriodAggregationPeriodMap.putValue( "2012", "2012Q4" );
        
        analyticsManager.replaceDataPeriodsWithAggregationPeriods( dataValueMap, params, dataPeriodAggregationPeriodMap );
        
        assertEquals( 8, dataValueMap.size() );
        
        assertTrue( dataValueMap.keySet().contains( "de1-2012Q1-ou1" ) );
        assertTrue( dataValueMap.keySet().contains( "de1-2012Q2-ou1" ) );
        assertTrue( dataValueMap.keySet().contains( "de1-2012Q3-ou1" ) );
        assertTrue( dataValueMap.keySet().contains( "de1-2012Q4-ou1" ) );
        assertTrue( dataValueMap.keySet().contains( "de2-2012Q1-ou1" ) );
        assertTrue( dataValueMap.keySet().contains( "de2-2012Q2-ou1" ) );
        assertTrue( dataValueMap.keySet().contains( "de2-2012Q3-ou1" ) );
        assertTrue( dataValueMap.keySet().contains( "de2-2012Q4-ou1" ) );
    }
}
