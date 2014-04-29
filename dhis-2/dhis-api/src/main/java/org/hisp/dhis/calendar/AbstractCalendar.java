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

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractCalendar implements Calendar
{
    protected static final String[] DEFAULT_I18N_MONTH_NAMES = new String[]{
        "month.january",
        "month.february",
        "month.march",
        "month.april",
        "month.may",
        "month.june",
        "month.july",
        "month.august",
        "month.september",
        "month.october",
        "month.november",
        "month.december"
    };

    protected static final String[] DEFAULT_I18N_MONTH_SHORT_NAMES = new String[]{
        "month.short.january",
        "month.short.february",
        "month.short.march",
        "month.short.april",
        "month.short.may",
        "month.short.june",
        "month.short.july",
        "month.short.august",
        "month.short.september",
        "month.short.october",
        "month.short.november",
        "month.short.december"
    };

    protected static final String[] DEFAULT_I18N_DAY_NAMES = new String[]{
        "weekday.monday",
        "weekday.tuesday",
        "weekday.wednesday",
        "weekday.thursday",
        "weekday.friday",
        "weekday.saturday",
        "weekday.sunday"
    };

    protected static final String[] DEFAULT_I18N_DAY_SHORT_NAMES = new String[]{
        "weekday.short.monday",
        "weekday.short.tuesday",
        "weekday.short.wednesday",
        "weekday.short.thursday",
        "weekday.short.friday",
        "weekday.short.saturday",
        "weekday.short.sunday"
    };

    protected static final String DEFAULT_ISO8601_DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public String defaultDateFormat()
    {
        return DEFAULT_ISO8601_DATE_FORMAT;
    }

    @Override
    public String formattedDate( DateUnit dateUnit )
    {
        return defaultDateFormat()
            .replace( "yyyy", String.format( "%04d", dateUnit.getYear() ) )
            .replace( "MM", String.format( "%02d", dateUnit.getMonth() ) )
            .replace( "dd", String.format( "%02d", dateUnit.getDay() ) );
    }

    @Override
    public String formattedIsoDate( DateUnit dateUnit )
    {
        dateUnit = toIso( dateUnit );
        DateTime dateTime = dateUnit.toDateTime();
        DateTimeFormatter format = DateTimeFormat.forPattern( defaultDateFormat() );

        return format.print( dateTime );
    }

    @Override
    public DateUnit toIso( int year, int month, int day )
    {
        return toIso( new DateUnit( year, month, day ) );
    }

    @Override
    public DateUnit toIso( String date )
    {
        DateTimeFormatter format = DateTimeFormat.forPattern( defaultDateFormat() );
        DateTime dateTime = format.parseDateTime( date );

        return toIso( DateUnit.fromDateTime( dateTime ) );
    }

    @Override
    public DateUnit fromIso( int year, int month, int day )
    {
        return fromIso( new DateUnit( year, month, day ) );
    }

    @Override
    public DateUnit today()
    {
        DateTime dateTime = DateTime.now( ISOChronology.getInstance() );
        return fromIso( DateUnit.fromDateTime( dateTime ) );
    }

    @Override
    public int monthsInYear()
    {
        return 12;
    }

    @Override
    public int daysInWeek()
    {
        return 7;
    }

    @Override
    public String nameOfMonth( int month )
    {
        if ( month > DEFAULT_I18N_MONTH_NAMES.length || month <= 0 )
        {
            return null;
        }

        return DEFAULT_I18N_MONTH_NAMES[month - 1];
    }

    @Override
    public String shortNameOfMonth( int month )
    {
        if ( month > DEFAULT_I18N_MONTH_SHORT_NAMES.length || month <= 0 )
        {
            return null;
        }

        return DEFAULT_I18N_MONTH_SHORT_NAMES[month - 1];
    }

    @Override
    public String nameOfDay( int day )
    {
        if ( day > DEFAULT_I18N_DAY_NAMES.length || day <= 0 )
        {
            return null;
        }

        return DEFAULT_I18N_DAY_NAMES[day - 1];
    }

    @Override
    public String shortNameOfDay( int day )
    {
        if ( day > DEFAULT_I18N_DAY_SHORT_NAMES.length || day <= 0 )
        {
            return null;
        }

        return DEFAULT_I18N_DAY_SHORT_NAMES[day - 1];
    }
}
