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
import org.hisp.dhis.calendar.DateUnit;

import java.util.Date;
import java.util.List;

/**
 * Abstract class for SixMonthly period types, including those starting
 * at the beginning of the calendar year and those starting at the beginning
 * of other months.
 *
 * @author Jim Grace
 */

public abstract class SixMonthlyAbstractPeriodType
    extends CalendarPeriodType
{
    private static final long serialVersionUID = -7135018015977806913L;

    public static final int FREQUENCY_ORDER = 182;

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract int getBaseMonth();

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public Period createPeriod( DateUnit dateUnit )
    {
        int year = getCalendar().monthsInYear() * dateUnit.getYear();
        int yearMonth = year + dateUnit.getMonth() - getBaseMonth();

        // TODO how should we handle years with odd number of months? (Ethiopian)
        int months = (((yearMonth % 12) / 6) * 6) + getBaseMonth();

        DateUnit start = new DateUnit( dateUnit );
        start.setDay( 1 );
        start.setMonth( 1 );
        start = getCalendar().plusMonths( start, months );
        start.setDayOfWeek( getCalendar().weekday( start ) );

        DateUnit end = new DateUnit( start );
        end = getCalendar().plusMonths( end, 5 );
        end.setDay( getCalendar().daysInMonth( end.getYear(), end.getMonth() ) );
        end.setDayOfWeek( getCalendar().weekday( end ) );

        return toIsoPeriod( start, end );
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
        DateUnit dateUnit = getCalendar().fromIso( DateUnit.fromJdkDate( period.getStartDate() ) );
        dateUnit = getCalendar().plusMonths( dateUnit, 6 );

        return createPeriod( getCalendar().toIso( dateUnit ) );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        DateUnit dateUnit = getCalendar().fromIso( DateUnit.fromJdkDate( period.getStartDate() ) );
        dateUnit = getCalendar().minusMonths( dateUnit, 6 );

        return createPeriod( getCalendar().toIso( dateUnit ) );
    }

    /**
     * Generates six-monthly Periods for the whole year in which the given
     * Period's startDate exists.
     */
    @Override
    public List<Period> generatePeriods( DateUnit dateUnit )
    {
        Period period = createPeriod( dateUnit );
        dateUnit = createLocalDateUnitInstance( period.getStartDate() );

        List<Period> periods = Lists.newArrayList();

        if ( dateUnit.getMonth() == (getBaseMonth() + 1) )
        {
            periods.add( period );
            periods.add( getNextPeriod( period ) );
        }
        else
        {
            periods.add( getPreviousPeriod( period ) );
            periods.add( period );
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
        Period period = createPeriod( date );

        List<Period> periods = Lists.newArrayList();

        periods.add( getPreviousPeriod( period ) );
        periods.add( period );

        return periods;
    }

    @Override
    public List<Period> generateRollingPeriods( DateUnit dateUnit )
    {
        return generateRollingPeriods( getCalendar().toIso( dateUnit ).toJdkDate() );
    }

    @Override
    public Date getRewindedDate( Date date, Integer rewindedPeriods )
    {
        date = date != null ? date : new Date();
        rewindedPeriods = rewindedPeriods != null ? rewindedPeriods : 1;

        DateUnit dateUnit = createLocalDateUnitInstance( date );
        getCalendar().minusMonths( dateUnit, rewindedPeriods * 6 );

        return getCalendar().toIso( dateUnit ).toJdkDate();
    }
}
