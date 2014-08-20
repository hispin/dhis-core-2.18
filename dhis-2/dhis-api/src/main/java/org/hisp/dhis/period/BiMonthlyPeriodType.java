package org.hisp.dhis.period;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import com.google.common.collect.Lists;
import org.hisp.dhis.calendar.Calendar;
import org.hisp.dhis.calendar.DateUnit;

import java.util.Date;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class BiMonthlyPeriodType
    extends CalendarPeriodType
{
    private static final String ISO_FORMAT = "yyyyMMB";

    /**
     * The name of the BiMonthlyPeriodType, which is "BiMonthly".
     */
    public static final String NAME = "BiMonthly";

    public static final int FREQUENCY_ORDER = 61;

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Period createPeriod( DateUnit dateUnit, Calendar calendar )
    {
        DateUnit start = new DateUnit( dateUnit );
        start.setMonth( ((start.getMonth() - 1) - (start.getMonth() - 1) % 2) + 1 );
        start.setDay( 1 );

        DateUnit end = new DateUnit( start );

        end = calendar.plusMonths( end, 1 );
        end.setDay( calendar.daysInMonth( end.getYear(), end.getMonth() ) );

        return toIsoPeriod( start, end, calendar );
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
    public Period getNextPeriod( Period period, Calendar calendar )
    {
        DateUnit dateUnit = calendar.fromIso( DateUnit.fromJdkDate( period.getStartDate() ) );
        dateUnit = calendar.plusMonths( dateUnit, 2 );

        return createPeriod( calendar.toIso( dateUnit ), calendar );
    }

    @Override
    public Period getPreviousPeriod( Period period, Calendar calendar )
    {
        DateUnit dateUnit = calendar.fromIso( DateUnit.fromJdkDate( period.getStartDate() ) );
        dateUnit = calendar.minusMonths( dateUnit, 2 );

        return createPeriod( calendar.toIso( dateUnit ), calendar );
    }

    /**
     * Generates bimonthly Periods for the whole year in which the start date of
     * the given Period exists.
     */
    @Override
    public List<Period> generatePeriods( DateUnit dateUnit )
    {
        Calendar cal = getCalendar();

        dateUnit.setMonth( 1 );
        dateUnit.setDay( 1 );

        List<Period> periods = Lists.newArrayList();

        int year = dateUnit.getYear();

        while ( dateUnit.getYear() == year )
        {
            periods.add( createPeriod( dateUnit, cal ) );
            dateUnit = cal.plusMonths( dateUnit, 2 );
        }

        return periods;
    }

    /**
     * Generates the last 6 bi-months where the last one is the bi-month
     * which the given date is inside.
     */
    @Override
    public List<Period> generateRollingPeriods( DateUnit dateUnit )
    {
        Calendar cal = getCalendar();

        dateUnit.setDay( 1 );
        dateUnit = cal.minusMonths( dateUnit, (dateUnit.getMonth() % 2) + 10 );

        List<Period> periods = Lists.newArrayList();

        for ( int i = 0; i < 6; i++ )
        {
            periods.add( createPeriod( dateUnit, cal ) );
            dateUnit = cal.plusMonths( dateUnit, 2 );
        }

        return periods;
    }

    @Override
    public String getIsoDate( DateUnit dateUnit )
    {
        return String.format( "%d%02dB", dateUnit.getYear(), (dateUnit.getMonth() + 1) / 2 );
    }

    @Override
    public String getIsoFormat()
    {
        return ISO_FORMAT;
    }

    @Override
    public Date getRewindedDate( Date date, Integer rewindedPeriods )
    {
        Calendar cal = getCalendar();

        date = date != null ? date : new Date();
        rewindedPeriods = rewindedPeriods != null ? rewindedPeriods : 1;

        DateUnit dateUnit = cal.fromIso( DateUnit.fromJdkDate( date ) );
        dateUnit = cal.minusMonths( dateUnit, rewindedPeriods );

        return cal.toIso( dateUnit ).toJdkDate();
    }
}
