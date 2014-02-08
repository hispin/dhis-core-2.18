package org.hisp.dhis.period;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
 * PeriodType for six-monthly Periods aligned to a financial year starting in
 * April or October. A valid April six-monthly Period has startDate set to
 * either April 1st or October 1st, and endDate set to the last day of the
 * fifth month after the startDate.
 *
 * @author Torgeir Lorange Ostby
 * @author Jim Grace
 */

public class SixMonthlyAprilPeriodType
        extends SixMonthlyAbstractPeriodType
{
    private static final long serialVersionUID = -2770872821413382644L;

    private static final String ISO_FORMAT = "yyyyAprilSn";

    private static final int BASE_MONTH = Calendar.APRIL;

    private static final String BASE_MONTH_NAME = "April";

    /**
     * The name of the SixMonthlyPeriodType, which is "SixMonthly".
     */
    public static final String NAME = "SixMonthlyApril";

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public int getBaseMonth()
    {
        return BASE_MONTH;
    }

    // -------------------------------------------------------------------------
    // CalendarPeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public String getIsoDate( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH );

        return year + BASE_MONTH_NAME + Semester.getByMonth( month ).name();
    }

    @Override
    public Period createPeriod( String isoDate )
    {
        int year = Integer.parseInt( isoDate.substring( 0, 4 ) );
        int month = Semester.valueOf( isoDate.substring( 9, 11 ) ).getMonth();

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
        S1( Calendar.APRIL ), S2( Calendar.OCTOBER );

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
                case Calendar.APRIL:
                    return S1;
                case Calendar.OCTOBER:
                    return S2;
                default:
                    throw new IllegalArgumentException( "Not a valid six-monthly starting month" );
            }
        }
    }
}
