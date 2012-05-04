package org.hisp.dhis.period;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * PeriodType for six-monthly Periods. A valid six-monthly Period has startDate
 * set to either January 1st or July 1st, and endDate set to the last day of the
 * fifth month after the startDate.
 * 
 * @author Torgeir Lorange Ostby
 * @version $Id: SixMonthlyPeriodType.java 2971 2007-03-03 18:54:56Z torgeilo $
 */
public class SixMonthlyPeriodType
    extends CalendarPeriodType
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 5709134010793412705L;

    private static final String ISO_FORMAT = "yyyySn";

    /**
     * The name of the SixMonthlyPeriodType, which is "SixMonthly".
     */
    public static final String NAME = "SixMonthly";

    public static final int FREQUENCY_ORDER = 182;

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

    @Override
    public Period createPeriod( Calendar cal )
    {
        cal.set( Calendar.MONTH, cal.get( Calendar.MONTH ) - cal.get( Calendar.MONTH ) % 6 );
        cal.set( Calendar.DAY_OF_MONTH, 1 );

        Date startDate = cal.getTime();

        cal.add( Calendar.MONTH, 5 );
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
        cal.add( Calendar.MONTH, 6 );
        return createPeriod( cal );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.add( Calendar.MONTH, -6 );
        return createPeriod( cal );
    }

    /**
     * Generates six-monthly Periods for the whole year in which the given
     * Period's startDate exists.
     */
    @Override
    public List<Period> generatePeriods( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        cal.set( Calendar.DAY_OF_YEAR, 1 );

        int year = cal.get( Calendar.YEAR );
        ArrayList<Period> periods = new ArrayList<Period>();

        while ( cal.get( Calendar.YEAR ) == year )
        {
            periods.add( createPeriod( cal ) );
            cal.add( Calendar.MONTH, 6 );
        }

        return periods;
    }

    /**
     * Generates the last 2 six-months where the last one is the six-month
     * which the given date is inside.
     */
    @Override
    public List<Period> generateRollingPeriods( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        cal.set( Calendar.DAY_OF_MONTH, 1 );
        cal.add( Calendar.MONTH, ( ( cal.get( Calendar.MONTH ) % 6 ) * -1 ) - 6 );

        ArrayList<Period> periods = new ArrayList<Period>();
        
        for ( int i = 0; i < 2; i++ )
        {
            periods.add( createPeriod( cal ) );
            cal.add( Calendar.MONTH, 6 );
        }
        
        return periods;
    }
    
    @Override
    public String getIsoDate( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH );

        return year + Semester.getByMonth( month ).name();
    }

    @Override
    public Period createPeriod( String isoDate )
    {
        int year = Integer.parseInt( isoDate.substring( 0, 4 ) );
        int month = Semester.valueOf( isoDate.substring( 4, 6 ) ).getMonth();
        
        Calendar cal = createCalendarInstance();
        cal.set( year, month, 1 );
        return createPeriod( cal );
    }

    /**
     * n refers to the semester, can be [1-2].
     */
    @Override
    public String getIsoFormat()
    {
        return ISO_FORMAT;
    }

    public enum Semester
    {
        S1( Calendar.JANUARY ), S2( Calendar.JULY );

        private final int month;

        Semester( int month )
        {
            this.month = month;
        }

        public int getMonth()
        {
            return month;
        }

        public static Semester getByMonth( int month )
        {
            switch ( month )
            {
            case Calendar.JANUARY:
                return S1;
            case Calendar.JULY:
                return S2;
            default:
                throw new IllegalArgumentException( "Not a valid six-monthly starting month" );
            }
        }
    }
}
