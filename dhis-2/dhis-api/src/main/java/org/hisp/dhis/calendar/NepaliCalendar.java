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
import org.joda.time.Days;
import org.joda.time.chrono.ISOChronology;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class NepaliCalendar extends AbstractCalendar
{
    private final DateUnit startNepal = new DateUnit( 2000, 1, 1, java.util.Calendar.WEDNESDAY );

    private final DateUnit startIso = new DateUnit( 1943, 4, 14, java.util.Calendar.WEDNESDAY );

    private static final Calendar self = new NepaliCalendar();

    public static Calendar getInstance()
    {
        return self;
    }

    @Override
    public DateUnit toIso( DateUnit dateUnit )
    {
        DateTime dateTime = startIso.toDateTime();

        int totalDays = 0;

        for ( int year = startNepal.getYear(); year < dateUnit.getYear(); year++ )
        {
            totalDays += getYearTotal( year );
        }

        for ( int month = startNepal.getMonth(); month < dateUnit.getMonth(); month++ )
        {
            totalDays += conversionMap.get( dateUnit.getYear() )[month];
        }

        totalDays += dateUnit.getDay() - startNepal.getDay();

        dateTime = dateTime.plusDays( totalDays );

        return DateUnit.fromDateTime( dateTime );
    }

    @Override
    public DateUnit fromIso( DateUnit dateUnit )
    {
        DateTime start = startIso.toDateTime();
        DateTime end = dateUnit.toDateTime();

        int days = Days.daysBetween( start, end ).getDays();

        int curYear = startNepal.getYear();
        int curMonth = startNepal.getMonth();
        int curDay = startNepal.getDay();
        int dayOfWeek = startNepal.getDayOfWeek();

        while ( days != 0 )
        {
            // days in month
            int dm = conversionMap.get( curYear )[curMonth];

            curDay++;

            if ( curDay > dm )
            {
                curMonth++;
                curDay = 1;
            }

            if ( curMonth > 12 )
            {
                curYear++;
                curMonth = 1;
            }

            dayOfWeek++;

            if ( dayOfWeek > 7 )
            {
                dayOfWeek = 1;
            }

            days--;
        }

        return new DateUnit( curYear, curMonth, curDay, dayOfWeek );
    }

    @Override
    public int daysInYear( int year )
    {
        return getYearTotal( year );
    }

    @Override
    public int daysInMonth( int year, int month )
    {
        return conversionMap.get( year )[month];
    }

    @Override
    public int isoWeek( DateUnit dateUnit )
    {
        DateTime dateTime = toIso( dateUnit ).toDateTime( ISOChronology.getInstance() );
        return dateTime.getWeekyear();
    }

    @Override
    public int week( DateUnit dateUnit )
    {
        return isoWeek( dateUnit );
    }

    @Override
    public int isoWeekday( DateUnit dateUnit )
    {
        DateTime dateTime = toIso( dateUnit ).toDateTime( ISOChronology.getInstance() );
        return dateTime.getDayOfWeek();
    }

    @Override
    public int weekday( DateUnit dateUnit )
    {
        int dayOfWeek = (isoWeekday( dateUnit ) + 1);

        if ( dayOfWeek > 7 )
        {
            return 1;
        }

        return dayOfWeek;
    }

    private int getYearTotal( int year )
    {
        // if year total index is uninitialized, calculate and set in array
        if ( conversionMap.get( year )[0] == 0 )
        {
            for ( int j = 1; j <= 12; j++ )
            {
                conversionMap.get( year )[0] += conversionMap.get( year )[j];
            }
        }

        return conversionMap.get( year )[0];
    }

    //------------------------------------------------------------------------------------------------------------
    // Conversion map for Nepali calendar
    //
    // Based on map from:
    // http://forjavaprogrammers.blogspot.com/2012/06/how-to-convert-english-date-to-nepali.html
    //------------------------------------------------------------------------------------------------------------

    /**
     * Map that gives an array of month lengths based on Nepali year lookup.
     * Index 1 - 12 is used for months, index 0 is used to give year total (lazy calculated).
     */
    private final static Map<Integer, int[]> conversionMap = new HashMap<Integer, int[]>();

    static
    {
        conversionMap.put( 2000, new int[]{ 0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2001, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2002, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2003, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2004, new int[]{ 0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2005, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2006, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2007, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2008, new int[]{ 0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31 } );
        conversionMap.put( 2009, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2010, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2011, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2012, new int[]{ 0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30 } );
        conversionMap.put( 2013, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2014, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2015, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2016, new int[]{ 0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30 } );
        conversionMap.put( 2017, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2018, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2019, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2020, new int[]{ 0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2021, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2022, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30 } );
        conversionMap.put( 2023, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2024, new int[]{ 0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2025, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2026, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2027, new int[]{ 0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2028, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2029, new int[]{ 0, 31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2030, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2031, new int[]{ 0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2032, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2033, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2034, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2035, new int[]{ 0, 30, 32, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31 } );
        conversionMap.put( 2036, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2037, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2038, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2039, new int[]{ 0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30 } );
        conversionMap.put( 2040, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2041, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2042, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2043, new int[]{ 0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30 } );
        conversionMap.put( 2044, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2045, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2046, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2047, new int[]{ 0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2048, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2049, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30 } );
        conversionMap.put( 2050, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2051, new int[]{ 0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2052, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2053, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30 } );
        conversionMap.put( 2054, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2055, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2056, new int[]{ 0, 31, 31, 32, 31, 32, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2057, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2058, new int[]{ 0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2059, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2060, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2061, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2062, new int[]{ 0, 31, 31, 31, 32, 31, 31, 29, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2063, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2064, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2065, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2066, new int[]{ 0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 29, 31 } );
        conversionMap.put( 2067, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2068, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2069, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2070, new int[]{ 0, 31, 31, 31, 32, 31, 31, 29, 30, 30, 29, 30, 30 } );
        conversionMap.put( 2071, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2072, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2073, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 31 } );
        conversionMap.put( 2074, new int[]{ 0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2075, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2076, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30 } );
        conversionMap.put( 2077, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 30, 29, 31 } );
        conversionMap.put( 2078, new int[]{ 0, 31, 31, 31, 32, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2079, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 29, 30, 29, 30, 30 } );
        conversionMap.put( 2080, new int[]{ 0, 31, 32, 31, 32, 31, 30, 30, 30, 29, 29, 30, 30 } );
        conversionMap.put( 2081, new int[]{ 0, 31, 31, 32, 32, 31, 30, 30, 30, 29, 30, 30, 30 } );
        conversionMap.put( 2082, new int[]{ 0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30 } );
        conversionMap.put( 2083, new int[]{ 0, 31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30 } );
        conversionMap.put( 2084, new int[]{ 0, 31, 31, 32, 31, 31, 30, 30, 30, 29, 30, 30, 30 } );
        conversionMap.put( 2085, new int[]{ 0, 31, 32, 31, 32, 30, 31, 30, 30, 29, 30, 30, 30 } );
        conversionMap.put( 2086, new int[]{ 0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30 } );
        conversionMap.put( 2087, new int[]{ 0, 31, 31, 32, 31, 31, 31, 30, 30, 29, 30, 30, 30 } );
        conversionMap.put( 2088, new int[]{ 0, 30, 31, 32, 32, 30, 31, 30, 30, 29, 30, 30, 30 } );
        conversionMap.put( 2089, new int[]{ 0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30 } );
        conversionMap.put( 2090, new int[]{ 0, 30, 32, 31, 32, 31, 30, 30, 30, 29, 30, 30, 30 } );
    }
}
