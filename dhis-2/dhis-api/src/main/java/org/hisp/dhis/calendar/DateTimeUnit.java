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

import java.util.Date;

/**
 * Simple class that can hold both a TimeUnit and DateUnit which is useful in cases
 * where we are converting from a DateTime, Jdk Calendar or Jdk Date and don't want to
 * loose either date or time dimension.
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 * @see DateUnit
 * @see TimeUnit
 * @see org.joda.time.DateTime
 * @see java.util.Calendar
 * @see java.util.Date
 */
public class DateTimeUnit
{
    private DateUnit dateUnit;

    private TimeUnit timeUnit;

    public DateTimeUnit()
    {
    }

    public DateTimeUnit( DateUnit dateUnit, TimeUnit timeUnit )
    {
        this.dateUnit = dateUnit;
        this.timeUnit = timeUnit;
    }

    public DateUnit getDateUnit()
    {
        return dateUnit;
    }

    public void setDateUnit( DateUnit dateUnit )
    {
        this.dateUnit = dateUnit;
    }

    public TimeUnit getTimeUnit()
    {
        return timeUnit;
    }

    public void setTimeUnit( TimeUnit timeUnit )
    {
        this.timeUnit = timeUnit;
    }

    public static DateTimeUnit fromDateTime( DateTime dateTime )
    {
        DateUnit dateUnit = DateUnit.fromDateTime( dateTime );
        TimeUnit timeUnit = TimeUnit.fromDateTime( dateTime );

        return new DateTimeUnit( dateUnit, timeUnit );
    }

    public static DateTimeUnit fromJdkCalendar( java.util.Calendar calendar )
    {
        DateUnit dateUnit = DateUnit.fromJdkCalendar( calendar );
        TimeUnit timeUnit = TimeUnit.fromJdkCalendar( calendar );

        return new DateTimeUnit( dateUnit, timeUnit );
    }

    public static DateTimeUnit fromJdkDate( Date date )
    {
        return fromDateTime( new DateTime( date.getTime() ) );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        DateTimeUnit that = (DateTimeUnit) o;

        if ( dateUnit != null ? !dateUnit.equals( that.dateUnit ) : that.dateUnit != null ) return false;
        if ( timeUnit != null ? !timeUnit.equals( that.timeUnit ) : that.timeUnit != null ) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = dateUnit != null ? dateUnit.hashCode() : 0;
        result = 31 * result + (timeUnit != null ? timeUnit.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "DateTimeUnit{" +
            "dateUnit=" + dateUnit +
            ", timeUnit=" + timeUnit +
            '}';
    }
}
