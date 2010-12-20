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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * PeriodType for quarterly Periods. A valid quarterly Period has startDate set
 * to the first day of a calendar quarter, and endDate set to the last day of
 * the same quarter.
 * 
 * @author Torgeir Lorange Ostby
 * @version $Id: QuarterlyPeriodType.java 2971 2007-03-03 18:54:56Z torgeilo $
 */
public class QuarterlyPeriodType
    extends CalendarPeriodType
{
    /**
     * The name of the QuarterlyPeriodType, which is "Quarterly".
     */
    public static final String NAME = "Quarterly";

    public static final int FREQUENCY_ORDER = 92;

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Period createPeriod()
    {
        return createPeriod( createCalendarInstance() );
    }

    @Override
    public Period createPeriod( Date date )
    {
        return createPeriod( createCalendarInstance( date ) );
    }

    private Period createPeriod( Calendar cal )
    {
        cal.set( Calendar.MONTH, cal.get( Calendar.MONTH ) - cal.get( Calendar.MONTH ) % 3 );
        cal.set( Calendar.DAY_OF_MONTH, 1 );

        Date startDate = cal.getTime();

        cal.add( Calendar.MONTH, 2 );
        cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );

        return new Period( this, startDate, cal.getTime() );
    }

    @Override
    public int getFrequencyOrder()
    {
        return FREQUENCY_ORDER;
    }

    // -------------------------------------------------------------------------
    // CalendarPeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public Period getNextPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.set( Calendar.MONTH, cal.get( Calendar.MONTH ) - cal.get( Calendar.MONTH ) % 3 + 3 );
        cal.set( Calendar.DAY_OF_MONTH, 1 );

        Date startDate = cal.getTime();

        cal.add( Calendar.MONTH, 2 );
        cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );

        return new Period( this, startDate, cal.getTime() );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.set( Calendar.MONTH, cal.get( Calendar.MONTH ) - cal.get( Calendar.MONTH ) % 3 - 3 );
        cal.set( Calendar.DAY_OF_MONTH, 1 );

        Date startDate = cal.getTime();

        cal.add( Calendar.MONTH, 2 );
        cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );

        return new Period( this, startDate, cal.getTime() );
    }
    
    /**
     * Generates quarterly Periods for the whole year in which the given
     * Period's startDate exists.
     */
    @Override
    public List<Period> generatePeriods( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        cal.set( Calendar.DAY_OF_YEAR, 1 );

        int year = cal.get( Calendar.YEAR );

        ArrayList<Period> quarters = new ArrayList<Period>();

        while ( cal.get( Calendar.YEAR ) == year )
        {
            Date startDate = cal.getTime();
            cal.add( Calendar.MONTH, 2 );
            cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );
            quarters.add( new Period( this, startDate, cal.getTime() ) );
            cal.add( Calendar.DAY_OF_YEAR, 1 );
        }

        return quarters;
    }

    @Override
    public String getIsoDate( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        int year = cal.get( Calendar.YEAR);
        int month = cal.get( Calendar.MONTH);

        String periodString = null;

        switch (month) {
            case Calendar.JANUARY:
                periodString = year + "Q1";
                break;
            case Calendar.APRIL:
                periodString = year + "Q2";
                break;
            case Calendar.JULY:
                periodString = year + "Q3";
                break;
            case Calendar.OCTOBER:
                periodString = year + "Q1";
                break;
            default:
                throw new RuntimeException("Not a valid quarterly period");
        }

        return periodString;
    }

}
