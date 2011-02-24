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

package org.hisp.dhis.period;

import static junit.framework.Assert.assertEquals;

import java.util.Calendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Lars Helge Overland
 */

public class FinancialAprilPeriodTypeTest
{
    private Calendar startCal;
    private Calendar endCal;
    private FinancialAprilPeriodType periodType;
    
    @Before
    public void before()
    {
        startCal = PeriodType.createCalendarInstance();
        endCal = PeriodType.createCalendarInstance();
        periodType = new FinancialAprilPeriodType();
    }
    
    @Test
    public void testCreatePeriod()
    {
        Calendar testCal = PeriodType.createCalendarInstance();
        testCal.set( 2009, Calendar.FEBRUARY, 15 );

        startCal.set( 2008, Calendar.APRIL, 1 );
        endCal.set( 2009, Calendar.MARCH, 31 );
        
        Period period = periodType.createPeriod( testCal.getTime() );
        
        assertEquals( startCal.getTime(), period.getStartDate() );
        assertEquals( endCal.getTime(), period.getEndDate() );
        
        testCal.set( 2009, Calendar.SEPTEMBER, 12 );

        period = periodType.createPeriod( testCal.getTime() );

        startCal.set( 2009, Calendar.APRIL, 1 );
        endCal.set( 2010, Calendar.MARCH , 31 );
        
        assertEquals( startCal.getTime(), period.getStartDate() );
        assertEquals( endCal.getTime(), period.getEndDate() );
    }

    @Test
    public void testGeneratePeriods()
    {
        Calendar testCal = PeriodType.createCalendarInstance();
        testCal.set( 2009, 1, 15 );
        
        List<Period> periods = periodType.generatePeriods( testCal.getTime() );
        
        startCal.set( 1998, Calendar.APRIL, 1 );
        endCal.set( 1999, Calendar.MARCH, 31 );
        
        Period startPeriod = periods.get( 0 );
        assertEquals( startCal.getTime(), startPeriod.getStartDate() );
        assertEquals( endCal.getTime(), startPeriod.getEndDate() );
        
        startCal = PeriodType.createCalendarInstance();
        startCal.set( 2008, Calendar.APRIL, 1 );
        endCal = PeriodType.createCalendarInstance();
        endCal.set( 2009, Calendar.MARCH, 31 );
        
        Period endPeriod = periods.get( periods.size() - 1 );
        assertEquals( startCal.getTime(), endPeriod.getStartDate() );
        assertEquals( endCal.getTime(), endPeriod.getEndDate() );
    }
}
