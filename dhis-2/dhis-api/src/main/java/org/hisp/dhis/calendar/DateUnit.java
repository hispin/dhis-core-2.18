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

import javax.validation.constraints.NotNull;
import java.util.GregorianCalendar;

/**
 * Class representing a specific calendar date.
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 * @see DateInterval
 * @see Calendar
 */
public class DateUnit
{
    /**
     * Year of date. Required.
     */
    @NotNull
    int year;

    /**
     * Month of date. Required.
     */
    @NotNull
    int month;

    /**
     * Day of date. Required.
     */
    @NotNull
    int day;

    /**
     * Day of week, numbering is unspecified and left up to user.
     */
    int dayOfWeek;

    public DateUnit()
    {
    }

    public DateUnit( int year, int month, int day )
    {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public DateUnit( int year, int month, int day, int dayOfWeek )
    {
        this( year, month, day );
        this.dayOfWeek = dayOfWeek;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear( int year )
    {
        this.year = year;
    }

    public int getMonth()
    {
        return month;
    }

    public void setMonth( int month )
    {
        this.month = month;
    }

    public int getDay()
    {
        return day;
    }

    public void setDay( int day )
    {
        this.day = day;
    }

    public int getDayOfWeek()
    {
        return dayOfWeek;
    }

    public void setDayOfWeek( int dayOfWeek )
    {
        this.dayOfWeek = dayOfWeek;
    }

    public DateTime toDateTime()
    {
        return new DateTime( year, month, day, 0, 0, ISOChronology.getInstance() );
    }

    public DateTime toDateTime( Chronology chronology )
    {
        return new DateTime( year, month, day, 0, 0, chronology );
    }

    public java.util.Calendar toJdkCalendar()
    {
        return new GregorianCalendar( year, month - 1, day );
    }

    public static DateUnit fromDateTime( DateTime dateTime )
    {
        return new DateUnit( dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth(), dateTime.getDayOfWeek() );
    }

    public static DateUnit fromJdkCalendar( java.util.Calendar calendar )
    {
        return new DateUnit( calendar.get( java.util.Calendar.YEAR ), calendar.get( java.util.Calendar.MONTH ),
            calendar.get( java.util.Calendar.DAY_OF_MONTH ), calendar.get( java.util.Calendar.DAY_OF_WEEK ) );
    }

    @Override
    public String toString()
    {
        return "DateUnit{" +
            "year=" + year +
            ", month=" + month +
            ", day=" + day +
            ", dayOfWeek=" + dayOfWeek +
            '}';
    }
}
