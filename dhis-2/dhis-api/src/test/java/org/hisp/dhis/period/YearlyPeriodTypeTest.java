package org.hisp.dhis.period;

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

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */
public class YearlyPeriodTypeTest
{
    private Calendar startCal;
    private Calendar endCal;
    private Calendar testCal;
    private CalendarPeriodType periodType;
    
    @Before
    public void before()
    {
        startCal = PeriodType.createCalendarInstance();
        endCal = PeriodType.createCalendarInstance();
        testCal = PeriodType.createCalendarInstance();
        periodType = new YearlyPeriodType();
    }
    
    @Test
    public void testCreatePeriod()
    {
        testCal.set( 2009, Calendar.AUGUST, 15 );
        
        startCal.set( 2009, Calendar.JANUARY, 1 );
        endCal.set( 2009, Calendar.DECEMBER, 31 );

        Period period = periodType.createPeriod( testCal.getTime() );
        
        assertEquals( startCal.getTime(), period.getStartDate() );
        assertEquals( endCal.getTime(), period.getEndDate() );
        
        testCal.set( 2009, Calendar.APRIL, 15 );
        
        period = periodType.createPeriod( testCal.getTime() );
        
        assertEquals( startCal.getTime(), period.getStartDate() );
        assertEquals( endCal.getTime(), period.getEndDate() );
    }

    @Test
    public void testGetNextPeriod()
    {
        testCal.set( 2009, Calendar.AUGUST, 15 );

        Period period = periodType.createPeriod( testCal.getTime() );
        
        period = periodType.getNextPeriod( period );

        startCal.set( 2010, Calendar.JANUARY, 1 );
        endCal.set( 2010, Calendar.DECEMBER, 31 );

        assertEquals( startCal.getTime(), period.getStartDate() );
        assertEquals( endCal.getTime(), period.getEndDate() );
    }
    
    @Test
    public void testGetPreviousPeriod()
    {
        testCal.set( 2009, Calendar.AUGUST, 15 );

        Period period = periodType.createPeriod( testCal.getTime() );
        
        period = periodType.getPreviousPeriod( period );

        startCal.set( 2008, Calendar.JANUARY, 1 );
        endCal.set( 2008, Calendar.DECEMBER, 31 );

        assertEquals( startCal.getTime(), period.getStartDate() );
        assertEquals( endCal.getTime(), period.getEndDate() );
    }
}
