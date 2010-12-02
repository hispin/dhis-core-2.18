package org.hisp.dhis.web.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;

public class PeriodUtil
{
    public static Period getPeriod( String periodName, PeriodType periodType ) throws IllegalArgumentException
    {

        if ( periodType instanceof DailyPeriodType )
        {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat formatter = new SimpleDateFormat( pattern );
            Date date;
            try
            {
                date = formatter.parse( periodName );
            }
            catch ( ParseException e )
            {
                throw new IllegalArgumentException( "Couldn't make a period of type " + periodType.getName()
                    + " and name " + periodName, e );
            }
            DailyPeriodType dailyPeriodType = new DailyPeriodType();
            return dailyPeriodType.createPeriod( date );

        }

        if ( periodType instanceof WeeklyPeriodType )
        {
            int dashIndex = periodName.indexOf( '-' );

            if (dashIndex < 0) {
                return null;
            }

            int week = Integer.parseInt( periodName.substring( 0, dashIndex ) );
            int year = Integer.parseInt( periodName.substring( dashIndex + 1, periodName.length() ) );

            Calendar cal = Calendar.getInstance();
            cal.set( Calendar.YEAR, year );
            cal.set( Calendar.WEEK_OF_YEAR, week );
            cal.setFirstDayOfWeek( Calendar.MONDAY );

            WeeklyPeriodType weeklyPeriodType = new WeeklyPeriodType();
            return weeklyPeriodType.createPeriod( cal.getTime() );
        }

        if ( periodType instanceof MonthlyPeriodType )
        {
            int dashIndex = periodName.indexOf( '-' );

            if (dashIndex < 0) {
                return null;
            }

            int month = Integer.parseInt( periodName.substring( 0, dashIndex ) );
            int year = Integer.parseInt( periodName.substring( dashIndex + 1, periodName.length() ) );

            Calendar cal = Calendar.getInstance();
            cal.set( Calendar.YEAR, year );
            cal.set( Calendar.MONTH, month );

            MonthlyPeriodType monthlyPeriodType = new MonthlyPeriodType();
            return monthlyPeriodType.createPeriod( cal.getTime() );
        }

        if ( periodType instanceof YearlyPeriodType )
        {
            Calendar cal = Calendar.getInstance();
            cal.set( Calendar.YEAR, Integer.parseInt( periodName ) );

            YearlyPeriodType yearlyPeriodType = new YearlyPeriodType();

            return yearlyPeriodType.createPeriod( cal.getTime() );
        }

        if ( periodType instanceof QuarterlyPeriodType )
        {
            Calendar cal = Calendar.getInstance();

            int month = 0;
            if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Jan" ) )
            {
                month = 1;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Apr" ) )
            {
                month = 4;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Jul" ) )
            {
                month = 6;
            }
            else if ( periodName.substring( 0, periodName.indexOf( " " ) ).equals( "Oct" ) )
            {
                month = 10;
            }

            int year = Integer.parseInt( periodName.substring( periodName.lastIndexOf( " " ) + 1 ) );

            cal.set( Calendar.MONTH, month );
            cal.set( Calendar.YEAR, year );

            QuarterlyPeriodType quarterlyPeriodType = new QuarterlyPeriodType();
            if ( month != 0 )
            {
                return quarterlyPeriodType.createPeriod( cal.getTime() );
            }

        }

        throw new IllegalArgumentException( "Couldn't make a period of type " + periodType.getName() + " and name "
            + periodName );
    }

}
