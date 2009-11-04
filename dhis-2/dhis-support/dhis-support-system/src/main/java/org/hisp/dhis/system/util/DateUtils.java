package org.hisp.dhis.system.util;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.validator.DateValidator;
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class DateUtils
{
    public static final double DAYS_IN_YEAR = 365.0;
    
    private static final long MS_PER_DAY = 86400000;
    
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    
    /**
     * Formats a Date to the IXF date format which is YYYY-MM-DD'T'HH:MM:SS.
     * @param date the Date to parse.
     * @return A formatted date string.
     */
    public static String getLongDateString( Date date )
    {
        final SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern( "yyyy-MM-dd'T'HH:mm:ss" );
        
        return date != null ? format.format( date ) : null;
    }
    
    /**
     * Formats the current Date to the IXF date format which is YYYY-MM-DD'T'HH:MM:SS.
     * @return A formatted date string.
     */
    public static String getLongDateString()
    {
        return getLongDateString( Calendar.getInstance().getTime() );
    }
    
    /**
     * Formats a Date to the format YYYY-MM-DD.
     * @param date the Date to parse.
     * @return A formatted date string. Null if argument is null.
     */
    public static String getMediumDateString( Date date )
    {
        final SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern( DEFAULT_DATE_FORMAT );
        
        return date != null ? format.format( date ) : null;
    }

    /**
     * Formats a Date to the format YYYY-MM-DD.
     * @param date the Date to parse.
     * @param defaultValue the return value if the date argument is null.
     * @return A formatted date string. The defaultValue argument if date argument is null.
     */
    public static String getMediumDateString( Date date, String defaultValue )
    {
        return date != null ? getMediumDateString( date ) : defaultValue;
    }
    
    /**
     * Formats the current Date to the format YYYY-MM-DD.
     * @return A formatted date string.
     */
    public static String getMediumDateString()
    {
        return getMediumDateString( Calendar.getInstance().getTime() );
    }
    
    /**
     * Parses a date from a String on the format YYYY-MM-DD.
     * @param dateString the String to parse.
     * @return a Date based on the given String.
     */
    public static Date getMediumDate( String dateString )
    {
        try
        {
            final SimpleDateFormat format = new SimpleDateFormat();
            format.applyPattern( DEFAULT_DATE_FORMAT );
        
            return dateString != null && dateIsValid( dateString ) ? format.parse( dateString ) : null;
        }
        catch ( ParseException ex )
        {
            throw new RuntimeException( "Failed to parse medium date", ex ); 
        }
    }
        
    /**
     * Tests if the given base date is between the given start date and end date, including the dates themselves.
     * 
     * @param baseDate the date used as base for the test. 
     * @param startDate the start date.
     * @param endDate the end date.
     * @return <code>true</code> if the base date is between the start date and end date, <code>false</code> otherwise.
     */
    public static boolean between( Date baseDate, Date startDate, Date endDate )
    {
        if ( startDate.equals( endDate ) || endDate.before( startDate ) )
        {
            return false;
        }
        
        if ( ( startDate.before( baseDate ) || startDate.equals( baseDate ) ) && ( endDate.after( baseDate ) || endDate.equals( baseDate ) ) )
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * Tests if the given base date is strictly between the given start date and end date.
     * 
     * @param baseDate the date used as base for the test. 
     * @param startDate the start date.
     * @param endDate the end date.
     * @return <code>true</code> if the base date is between the start date and end date, <code>false</code> otherwise.
     */
    public static boolean strictlyBetween( Date baseDate, Date startDate, Date endDate )
    {
        if ( startDate.equals( endDate ) || endDate.before( startDate ) )
        {
            return false;
        }
        
        if ( startDate.before( baseDate ) && endDate.after( baseDate ) )
        {
            return true;
        }
        
        return false;
    }
    
    /**
     * Returns the number of days since 01/01/1970. The value is rounded off to 
     * the floor value and does not take daylight saving time into account.
     * 
     * @param date the date.
     * @return number of days since Epoch.
     */
    public static long getDays( Date date )
    {
        return date.getTime() / MS_PER_DAY;
    }
    
    /**
     * Returns the number of days between the start date (inclusive) and end 
     * date (exclusive). The value is rounded off to the floor value and does 
     * not take daylight saving time into account.
     * 
     * @param startDate the start-date.
     * @param endDate the end-date.
     * @return the number of days between the start and end-date.
     */
    public static long getDays( Date startDate, Date endDate )
    {
        return ( endDate.getTime() - startDate.getTime() ) / MS_PER_DAY;
    }

    /**
     * Returns the number of days between the start date (inclusive) and end 
     * date (inclusive). The value is rounded off to the floor value and does 
     * not take daylight saving time into account.
     * 
     * @param startDate the start-date.
     * @param endDate the end-date.
     * @return the number of days between the start and end-date.
     */
    public static long getDaysInclusive( Date startDate, Date endDate )
    {
        return ( getDays( startDate, endDate ) + 1 );
    }
    
    /**
     * Calculates the number of days between the start and end-date.
     * 
     * @param startDate the start date.
     * @param endDate the end date.
     * @return the number of days between the start and end date.
     */
    public static int daysBetween( Date startDate, Date endDate )
    {
        final Days days = Days.daysBetween( new DateTime( startDate ), new DateTime( endDate ) );
        
        return days.getDays();
    }
    
    /**
     * Calculates the number of days between Epoch and the given date.
     * @param date the date.
     * @return the number of days between Epoch and the given date.
     */
    public static int daysSince1900( Date date )
    {
        final Calendar calendar = Calendar.getInstance();
        
        calendar.clear();
        calendar.set( 1900, 0, 1 );
        
        return daysBetween( calendar.getTime(), date );
    }
    
    /**
     * Returns Epoch date, ie. 01/01/1970.
     * @return Epoch date, ie. 01/01/1970.
     */
    public static Date getEpoch()
    {
        final Calendar calendar = Calendar.getInstance();
        
        calendar.clear();
        calendar.set( 1970, 0, 1 );
        
        return calendar.getTime();        
    }

    /**
     * Returns a date formatted in ANSI SQL.
     * 
     * @param date the Date.
     * @return a date String.
     */
    public static String getSqlDateString( Date date )
    {
        Calendar cal = Calendar.getInstance();
        
        cal.setTime( date );
        
        int year = cal.get( Calendar.YEAR );
        int month = cal.get( Calendar.MONTH ) + 1;
        int day = cal.get( Calendar.DAY_OF_MONTH );
        
        String yearString = String.valueOf( year );
        String monthString = month < 10 ? "0" + month : String.valueOf( month );
        String dayString = day < 10 ? "0" + day : String.valueOf( day );
        
        return yearString + "-" + monthString + "-" + dayString;
    }
    /**
     * This method checks whether the String inDate is a valid date following the format "yyyy-MM-dd".
     * 
     * @param date the string to be checked.
     * @return true/false depending on whether the string is a date according to the format "yyyy-MM-dd".
     */
    public static boolean dateIsValid( String dateString )
    {        
        return DateValidator.getInstance().isValid( dateString, DEFAULT_DATE_FORMAT, true );
    }
    
    /**
     * This method converts a string from the date format "yyyy-MM-dd" to "dd-MMM-yyyy".
     * 
     * @param date is the string to be converted.
     * @return converted string if the date is valid, else the original string is returned
     */
    public static String convertDate( String dateString )
    {
        if ( !dateIsValid( dateString ) )
        {
            return dateString;
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat( DEFAULT_DATE_FORMAT );

        try
        {
            Date date = dateFormat.parse( dateString );
            dateFormat.applyPattern( "dd MMM yyyy" );

            return dateFormat.format( date );
        }
        catch ( ParseException pe )
        {
            throw new RuntimeException( "Date string could not be parsed: " + dateString );
        }
    }
}
