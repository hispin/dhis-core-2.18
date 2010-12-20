/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.hisp.dhis.period;


import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;

/**
 *
 * @author bobj
 */
public class StringFormatTest {

    private static Date getDate( int year, int month, int day )
    {
        final Calendar calendar = Calendar.getInstance();

        calendar.clear();
        calendar.set( year, month - 1, day );

        return calendar.getTime();
    }

    @Test
    public void testStringFormat()
    {
        Period day1 = new Period(new DailyPeriodType(),getDate(2010,1,1), getDate(2010,1,1));
        Period week52 = new Period(new WeeklyPeriodType(),getDate(2009,12,21), getDate(2009,12,27));
        Period week53 = new Period(new WeeklyPeriodType(),getDate(2009,12,28), getDate(2010,1,3));
        Period week1 = new Period(new WeeklyPeriodType(),getDate(2010,1,4), getDate(2010,1,11));
        Period month1 = new Period(new MonthlyPeriodType(),getDate(2010,1,1), getDate(2010,1,1));
        Period year1 = new Period(new YearlyPeriodType(),getDate(2010,1,1), getDate(2010,1,1));
        Period quarter1 = new Period(new QuarterlyPeriodType(),getDate(2010,1,1), getDate(2010,1,1));
        Period semester1 = new Period(new SixMonthlyPeriodType(),getDate(2010,1,1), getDate(2010,1,1));

        assertEquals("Day format", "20100101", day1.getIsoDate());
        assertEquals("Week format", "2009W52", week52.getIsoDate());
        assertEquals("Week format", "2009W53", week53.getIsoDate());
        assertEquals("Week format", "2010W1", week1.getIsoDate());
        assertEquals("Month format", "201001", month1.getIsoDate());
        assertEquals("Year format", "2010", year1.getIsoDate());
        assertEquals("Quarter format", "2010Q1", quarter1.getIsoDate());
        assertEquals("Semester format", "2010S1", semester1.getIsoDate());
    }
}
