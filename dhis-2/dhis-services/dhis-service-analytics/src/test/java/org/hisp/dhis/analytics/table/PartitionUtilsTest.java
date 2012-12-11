package org.hisp.dhis.analytics.table;

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

import static org.hisp.dhis.analytics.AnalyticsTableManager.TABLE_NAME;
import static org.hisp.dhis.analytics.AnalyticsTableManager.TABLE_NAME_TEMP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.hisp.dhis.period.Cal;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.junit.Test;

public class PartitionUtilsTest
{
    @Test
    public void testGetTableNames()
    {
        Cal cal = new Cal();
        Date earliest = cal.set( 2000, 5, 4 ).time();
        Date latest = cal.set( 2001, 2, 10 ).time();
        
        List<String> tables = PartitionUtils.getTempTableNames( earliest, latest );
        
        assertEquals( 4, tables.size() );
        assertTrue( tables.contains( TABLE_NAME_TEMP + "_2000Q2" ) );
        assertTrue( tables.contains( TABLE_NAME_TEMP + "_2000Q3" ) );
        assertTrue( tables.contains( TABLE_NAME_TEMP + "_2000Q4" ) );
        assertTrue( tables.contains( TABLE_NAME_TEMP + "_2001Q1" ) );
    }
    
    @Test
    public void testGetTable()
    {
        assertEquals( TABLE_NAME + "_2000Q4", PartitionUtils.getTable( "200011" ) );
        assertEquals( TABLE_NAME + "_2000Q1", PartitionUtils.getTable( "2000W02" ) );
        assertEquals( TABLE_NAME + "_2000Q2", PartitionUtils.getTable( "2000Q2" ) );
        assertEquals( TABLE_NAME + "_2000Q3", PartitionUtils.getTable( "2000S2" ) );
        assertEquals( TABLE_NAME + "_2000Q1", PartitionUtils.getTable( "2000" ) );
    }
    
    @Test
    public void testGetPeriod()
    {
        Cal cal = new Cal();
        
        Period q2 = new QuarterlyPeriodType().createPeriod( cal.set( 2000, 4, 1 ).time() );
        Period q4 = new QuarterlyPeriodType().createPeriod( cal.set( 2000, 10, 1 ).time() );
        
        assertEquals( q2, PartitionUtils.getPeriod( TABLE_NAME_TEMP + "_2000Q2" ) );
        assertEquals( q4, PartitionUtils.getPeriod( TABLE_NAME_TEMP + "_2000Q4" ) );
    }
}
