package org.hisp.dhis.calendar;

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

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class ChronologyBasedCalendar extends AbstractCalendar
{
    private final Chronology chronology;

    protected ChronologyBasedCalendar( Chronology chronology )
    {
        this.chronology = chronology;
    }

    @Override
    public DateUnit toIso( DateUnit dateUnit )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        dateTime = dateTime.withChronology( ISOChronology.getInstance() );

        return DateUnit.fromDateTime( dateTime );
    }

    @Override
    public DateUnit fromIso( DateUnit dateUnit )
    {
        DateTime dateTime = dateUnit.toDateTime( ISOChronology.getInstance() );
        dateTime = dateTime.withChronology( chronology );
        return DateUnit.fromDateTime( dateTime );
    }

    @Override
    public DateInterval toInterval( DateUnit dateUnit, DateInterval.DateIntervalType type )
    {
        switch ( type )
        {
            case ISO8601_YEAR:
                return toYearIsoInterval( dateUnit );
            case ISO8601_MONTH:
                return toMonthIsoInterval( dateUnit );
            case ISO8601_WEEK:
                return toWeekIsoInterval( dateUnit );
        }

        return null;
    }

    private DateInterval toYearIsoInterval( DateUnit dateUnit )
    {
        DateUnit from = new DateUnit( dateUnit.getYear(), 1, 1 );
        DateUnit to = new DateUnit( dateUnit.getYear(), monthsInYear(), daysInMonth( dateUnit.getYear(), monthsInYear() ) );

        from.setDayOfWeek( isoWeekday( from ) );
        to.setDayOfWeek( isoWeekday( to ) );

        return new DateInterval( from, to, DateInterval.DateIntervalType.ISO8601_YEAR );
    }

    private DateInterval toMonthIsoInterval( DateUnit dateUnit )
    {
        DateUnit from = new DateUnit( dateUnit.getYear(), dateUnit.getMonth(), 1 );
        DateUnit to = new DateUnit( dateUnit.getYear(), dateUnit.getMonth(), daysInMonth( dateUnit.getYear(), dateUnit.getMonth() ) );

        from.setDayOfWeek( isoWeekday( from ) );
        to.setDayOfWeek( isoWeekday( to ) );

        return new DateInterval( from, to, DateInterval.DateIntervalType.ISO8601_MONTH );
    }

    private DateInterval toWeekIsoInterval( DateUnit dateUnit )
    {
        DateTime dateTime = new DateTime( dateUnit.getYear(), dateUnit.getMonth(), dateUnit.getDay(), 0, 0, chronology );

        DateTime from = dateTime.weekOfWeekyear().toInterval().getStart();
        DateTime to = dateTime.weekOfWeekyear().toInterval().getEnd().minusDays( 1 );

        return new DateInterval( DateUnit.fromDateTime( from ), DateUnit.fromDateTime( to ), DateInterval.DateIntervalType.ISO8601_WEEK );
    }

    @Override
    public int monthsInYear()
    {
        DateTime dateTime = new DateTime( 1, 1, 1, 0, 0, chronology );
        return dateTime.monthOfYear().getMaximumValue();
    }

    @Override
    public int daysInWeek()
    {
        DateTime dateTime = new DateTime( 1, 1, 1, 0, 0, chronology );
        return dateTime.dayOfWeek().getMaximumValue();
    }

    @Override
    public int daysInYear( int year )
    {
        DateTime dateTime = new DateTime( year, 1, 1, 0, 0, chronology );
        return (int) dateTime.year().toInterval().toDuration().getStandardDays();
    }

    @Override
    public int daysInMonth( int year, int month )
    {
        DateTime dateTime = new DateTime( year, month, 1, 0, 0, chronology );
        return (int) dateTime.monthOfYear().toInterval().toDuration().getStandardDays();
    }

    @Override
    public int isoWeek( DateUnit dateUnit )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return dateTime.getWeekOfWeekyear();
    }

    @Override
    public int week( DateUnit dateUnit )
    {
        return isoWeek( dateUnit );
    }

    @Override
    public int isoWeekday( DateUnit dateUnit )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        dateTime = dateTime.withChronology( ISOChronology.getInstance() );
        return dateTime.getDayOfWeek();
    }

    @Override
    public int weekday( DateUnit dateUnit )
    {
        DateTime dateTime = dateUnit.toDateTime( chronology );
        return dateTime.getDayOfWeek();
    }
}
