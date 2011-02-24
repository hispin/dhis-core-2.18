package org.hisp.dhis.period;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Cal
{
    private Calendar calendar;
    
    public Cal()
    {
        calendar = new GregorianCalendar();
        calendar.clear();
    }

    /**
    * @param year the year starting at AD 1.
    * @param month the month starting at 1.
    * @param day the day of the month starting at 1.
    */
    public Cal( int year, int month, int day )
    {
        calendar = new GregorianCalendar();
        calendar.clear();
        set( year, month, day );
    }

    /**
     * Returns the value of the given calendar field.
     * 
     * @param field the field.
     */
    public int get( int field )
    {
        return calendar.get( field );
    }
    
    /**
     * Sets the current time.
     * 
     * @param year the year starting at AD 1.
     * @param month the month starting at 1.
     * @param day the day of the month starting at 1.
     */
    public Cal set( int year, int month, int day )
    {
        calendar.set( year, month - 1, day );
        return this;
    }
    
    /**
     * Returns the current date the cal.
     */
    public Date time()
    {
        return calendar.getTime();
    }
}
