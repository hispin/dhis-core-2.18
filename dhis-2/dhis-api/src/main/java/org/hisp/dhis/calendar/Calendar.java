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

/**
 * Generic interface for representing a Calendar.
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 * @see DateUnit
 * @see DateInterval
 */
public interface Calendar
{
    /**
     * Name of this calendar.
     * @return Name of calendar.
     */
    String name();

    /**
     * Convert local calendar to an ISO 8601 DateUnit.
     * @param year  Local year
     * @param month Local month
     * @param day   Local day
     * @return DateUnit representing local date in ISO 8601
     * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">http://en.wikipedia.org/wiki/ISO_8601</a>
     */
    DateUnit toIso( int year, int month, int day );

    /**
     * Convert local calendar to an ISO 8601 DateUnit.
     * @param dateUnit DateUnit representing local year, month, day
     * @return DateUnit representing local date in ISO 8601
     * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">http://en.wikipedia.org/wiki/ISO_8601</a>
     */
    DateUnit toIso( DateUnit dateUnit );

    /**
     * Convert from local to ISO 8601 DateUnit.
     * @param year  ISO 8601 year
     * @param month ISO 8601 month
     * @param day   ISO 8601 day
     * @return DateUnit representing ISO 8601 in local
     * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">http://en.wikipedia.org/wiki/ISO_8601</a>
     */
    DateUnit fromIso( int year, int month, int day );

    /**
     * Convert from local to ISO 8601 DateUnit.
     * @param dateUnit DateUnit representing ISO 8601 year, month, day
     * @return DateUnit representing ISO 8601 in local
     * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">http://en.wikipedia.org/wiki/ISO_8601</a>
     */
    DateUnit fromIso( DateUnit dateUnit );

    /**
     * Gets this local year as a ISO 8601 interval
     * @param year Local year
     * @return ISO 8601 interval for year
     * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">http://en.wikipedia.org/wiki/ISO_8601</a>
     */
    DateInterval toIsoInterval( int year );

    /**
     * Gets this local year/month as a ISO 8601 interval
     * @param year  Local year
     * @param month Local month
     * @return ISO 8601 interval for year/month
     * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">http://en.wikipedia.org/wiki/ISO_8601</a>
     */
    DateInterval toIsoInterval( int year, int month );

    /**
     * Gets current date as local DateUnit
     * @return Today date as local DateUnit
     */
    DateUnit today();

    /**
     * Gets the number of months in a calendar year.
     * @return Number of months in a year
     */
    int monthsInYear();

    /**
     * Gets the number of days in a calendar week.
     * @return Number of days in a week
     */
    int daysInWeek();

    /**
     * Gets the number of days in a calendar year.
     * @return Number of days in this calendar year
     */
    int daysInYear( int year );

    /**
     * Gets the number of days in a calendar year/month.
     * @return Number of days in this calendar year/month
     */
    int daysInMonth( int year, int month );

    /**
     * Gets week number using local DateUnit, week number is calculated based on
     * ISO 8601 week numbers
     * @param dateUnit DateUnit representing local year, month, day
     * @return Week number
     * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">http://en.wikipedia.org/wiki/ISO_8601</a>
     * @see <a href="http://en.wikipedia.org/wiki/ISO_week_date">http://en.wikipedia.org/wiki/ISO_week_date</a>
     */
    int isoWeek( DateUnit dateUnit );

    /**
     * Returns week number using local DateUnit, week number is calculated based on local calendar.
     * @param dateUnit DateUnit representing local year, month, day
     * @return Week number
     */
    int week( DateUnit dateUnit );

    /**
     * Gets the ISO 8601 weekday for this local DateUnit, using ISO 8601 day numbering,
     * 1=Monday => 7=Sunday.
     * @param dateUnit DateUnit representing local year, month, day
     * @return Weekday number
     * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">http://en.wikipedia.org/wiki/ISO_8601</a>
     * @see <a href="http://en.wikipedia.org/wiki/ISO_week_date">http://en.wikipedia.org/wiki/ISO_week_date</a>
     */
    int isoWeekday( DateUnit dateUnit );

    /**
     * Gets the local weekday for this local DateUnit, using ISO 8601 day numbering,
     * 1=Monday => 7=Sunday.
     * @param dateUnit DateUnit representing local year, month, day
     * @return Weekday number
     * @see <a href="http://en.wikipedia.org/wiki/ISO_8601">http://en.wikipedia.org/wiki/ISO_8601</a>
     * @see <a href="http://en.wikipedia.org/wiki/ISO_week_date">http://en.wikipedia.org/wiki/ISO_week_date</a>
     */
    int weekday( DateUnit dateUnit );
}
