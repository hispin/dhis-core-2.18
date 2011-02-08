package org.hisp.dhis.period;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class RelativePeriods
    implements Serializable
{
    public static final String REPORTING_MONTH = "reporting_month";
    public static final String THIS_YEAR = "year";    
    public static final String LAST_YEAR = "last_year";
        
    public static final String[] MONTHS_THIS_YEAR = {
        "january",
        "february",
        "march",
        "april",
        "may",
        "june",
        "july",
        "august",
        "september",
        "october",
        "november",
        "december" };

    public static final String[] MONTHS_LAST_YEAR = {
        "january_last_year",
        "february_last_year",
        "march_last_year",
        "april_last_year",
        "may_last_year",
        "june_last_year",
        "july_last_year",
        "august_last_year",
        "september_last_year",
        "october_last_year",
        "november_last_year",
        "december_last_year" };
    
    public static final String[] QUARTERS_THIS_YEAR = {
        "quarter1",
        "quarter2",
        "quarter3",
        "quarter4" };

    public static final String[] QUARTERS_LAST_YEAR = {
        "quarter1_last_year",
        "quarter2_last_year",
        "quarter3_last_year",
        "quarter4_last_year" };

    private static final int MONTHS_IN_YEAR = 12;
    
    private Boolean reportingMonth = false;
    
    private Boolean monthsThisYear = false;
    
    private Boolean quartersThisYear = false;
    
    private Boolean thisYear = false;
    
    private Boolean monthsLastYear = false;
    
    private Boolean quartersLastYear = false;
    
    private Boolean lastYear = false;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public RelativePeriods()
    {   
    }
    
    /**
     * @param reportingMonth reporting month
     * @param monthsThisYear months this year
     * @param quartersThisYear quarters this year
     * @param thisYear this year
     * @param monthsLastYear months last year
     * @param quartersLastYear quarters last year
     * @param lastYear last year
     */
    public RelativePeriods( boolean reportingMonth, boolean monthsThisYear, boolean quartersThisYear, boolean thisYear,
        boolean monthsLastYear, boolean quartersLastYear, boolean lastYear )
    {
        this.reportingMonth = reportingMonth;
        this.monthsThisYear = monthsThisYear;
        this.quartersThisYear = quartersThisYear;
        this.thisYear = thisYear;
        this.monthsLastYear = monthsLastYear;
        this.quartersLastYear = quartersLastYear;
        this.lastYear = lastYear;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------
    
    /**
     * Returns the name of the reporting month.
     * 
     * @param months the number of months back in time representing the current month.
     * @param format the i18n format.
     * @return the name of the reporting month.
     */
    public String getReportingMonthName( int months, I18nFormat format )
    {
        Period period = new MonthlyPeriodType().createPeriod( getDate( months, null ) );
        return format.formatPeriod( period );
    }
    
    /**
     * Gets a list of Periods relative to current date.
     */
    public List<Period> getRelativePeriods()
    {
        return getRelativePeriods( 1, null, null, false );
    }
    
    /**
     * Gets a list of Periods based on the given input and the state of this RelativePeriods.
     * 
     * @param months the number of months back in time representing the current month.
     * @param format the i18n format.
     * @return a list of relative Periods.
     */
    public List<Period> getRelativePeriods( int months, I18nFormat format, boolean dynamicNames )
    {
        return getRelativePeriods( months, null, format, dynamicNames );
    }
    
    /**
     * Gets a list of Periods based on the given input and the state of this RelativePeriods.
     * 
     * @param months the number of months back in time representing the current reporting month.
     * @param date the date representing now (for testing purposes).
     * @param format the i18n format.
     * @return a list of relative Periods.
     */
    protected List<Period> getRelativePeriods( int months, Date date, I18nFormat format, boolean dynamicNames )
    {
        List<Period> periods = new ArrayList<Period>();
        
        Date current = getDate( months, date );
        
        if ( isReportingMonth() )
        {
            periods.add( getRelativePeriod( new MonthlyPeriodType(), REPORTING_MONTH, current, dynamicNames, format ) );
        }
        
        if ( isMonthsThisYear() )
        {
            periods.addAll( getRelativePeriodList( new MonthlyPeriodType(), MONTHS_THIS_YEAR, current, dynamicNames, format ) );
        }
        
        if ( isQuartersThisYear() )
        {
            periods.addAll( getRelativePeriodList( new QuarterlyPeriodType(), QUARTERS_THIS_YEAR, current, dynamicNames, format ) );
        }
        
        if ( isThisYear() )
        {
            periods.add( getRelativePeriod( new YearlyPeriodType(), THIS_YEAR, current, dynamicNames, format ) );
        }
        
        current = getDate( MONTHS_IN_YEAR, current );
        
        if ( isMonthsLastYear() )
        {
            periods.addAll( getRelativePeriodList( new MonthlyPeriodType(), MONTHS_LAST_YEAR, current, dynamicNames, format ) );
        }
        
        if ( isQuartersLastYear() )
        {
            periods.addAll( getRelativePeriodList( new QuarterlyPeriodType(), QUARTERS_LAST_YEAR, current, dynamicNames, format ) );
        }
        
        if ( isLastYear() )
        {
            periods.add( getRelativePeriod( new YearlyPeriodType(), LAST_YEAR, current, dynamicNames, format ) );
        }
        
        return periods;
    }

    /**
     * Returns a list of relative periods. The name will be dynamic depending on
     * the dynamicNames argument. The short name will always be dynamic.
     * 
     * @param periodType the period type.
     * @param periodNames the array of period names.
     * @param current the current date.
     * @param dynamicNames indication of whether dynamic names should be used.
     * @param format the I18nFormat.
     * @return a list of periods.
     */
    private List<Period> getRelativePeriodList( CalendarPeriodType periodType, String[] periodNames, Date current, boolean dynamicNames, I18nFormat format )
    {
        List<Period> relatives = periodType.generatePeriods( current );
        List<Period> periods = new ArrayList<Period>();
        
        int c = 0;
        
        for ( Period period : relatives )
        {
            period.setName( dynamicNames ? format.formatPeriod( period ) : MONTHS_THIS_YEAR[c++] );
            period.setShortName( format.formatPeriod( period ) );
            periods.add( period );
        }
        
        return periods;
    }

    /**
     * Returns relative period. The name will be dynamic depending on the
     * dynamicNames argument. The short name will always be dynamic.
     * 
     * @param periodType the period type.
     * @param periodName the period name.
     * @param current the current date.
     * @param dynamicNames indication of whether dynamic names should be used.
     * @param format the I18nFormat.
     * @return a list of periods.
     */
    private Period getRelativePeriod( CalendarPeriodType periodType, String periodName, Date current, boolean dynamicNames, I18nFormat format )
    {
        Period period = periodType.createPeriod( current );
        period.setName( dynamicNames ? format.formatPeriod( period ) : periodName );
        period.setShortName( format.formatPeriod( period ) );
        
        return period;
    }
    
    /**
     * Returns a date.
     * 
     * @param months the number of months to subtract from the current date.
     * @param now the date representing now, ignored if null.
     * @return a date.
     */
    private Date getDate( int months, Date now )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        
        if ( now != null ) // For testing purposes
        {
            cal.setTime( now );
        }
        
        cal.add( Calendar.MONTH, ( months * -1 ) );        
        
        return cal.getTime();
    }
    
    public boolean isReportingMonth()
    {
        return reportingMonth != null && reportingMonth;
    }
    
    public boolean isMonthsThisYear()
    {
        return monthsThisYear != null && monthsThisYear;
    }
    
    public boolean isQuartersThisYear()
    {
        return quartersThisYear != null && quartersThisYear;
    }
    
    public boolean isThisYear()
    {
        return thisYear != null && thisYear;
    }
    
    public boolean isMonthsLastYear()
    {
        return monthsLastYear != null && monthsLastYear;
    }
    
    public boolean isQuartersLastYear()
    {
        return quartersLastYear != null && quartersLastYear;
    }
    
    public boolean isLastYear()
    {
        return lastYear != null && lastYear;
    }
        
    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------

    public Boolean getReportingMonth()
    {
        return reportingMonth;
    }

    public void setReportingMonth( Boolean reportingMonth )
    {
        this.reportingMonth = reportingMonth;
    }

    public Boolean getMonthsThisYear()
    {
        return monthsThisYear;
    }

    public void setMonthsThisYear( Boolean monthsThisYear )
    {
        this.monthsThisYear = monthsThisYear;
    }

    public Boolean getQuartersThisYear()
    {
        return quartersThisYear;
    }

    public void setQuartersThisYear( Boolean quartersThisYear )
    {
        this.quartersThisYear = quartersThisYear;
    }

    public Boolean getThisYear()
    {
        return thisYear;
    }

    public void setThisYear( Boolean thisYear )
    {
        this.thisYear = thisYear;
    }

    public Boolean getMonthsLastYear()
    {
        return monthsLastYear;
    }

    public void setMonthsLastYear( Boolean monthsLastYear )
    {
        this.monthsLastYear = monthsLastYear;
    }

    public Boolean getQuartersLastYear()
    {
        return quartersLastYear;
    }

    public void setQuartersLastYear( Boolean quartersLastYear )
    {
        this.quartersLastYear = quartersLastYear;
    }

    public Boolean getLastYear()
    {
        return lastYear;
    }

    public void setLastYear( Boolean lastYear )
    {
        this.lastYear = lastYear;
    }

    // -------------------------------------------------------------------------
    // Equals, hashCode, and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        
        int result = 1;

        result = prime * result + ( ( reportingMonth == null ) ? 0 : reportingMonth.hashCode() );
        result = prime * result + ( ( monthsThisYear == null ) ? 0 : monthsThisYear.hashCode() );
        result = prime * result + ( ( quartersThisYear == null ) ? 0 : quartersThisYear.hashCode() );
        result = prime * result + ( ( thisYear == null ) ? 0 : thisYear.hashCode() );
        result = prime * result + ( ( monthsLastYear == null ) ? 0 : monthsLastYear.hashCode() );
        result = prime * result + ( ( quartersLastYear == null ) ? 0 : quartersLastYear.hashCode() );
        result = prime * result + ( ( lastYear == null ) ? 0 : lastYear.hashCode() );
        
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        final RelativePeriods other = (RelativePeriods) object;
        
        if ( reportingMonth == null )
        {
            if ( other.reportingMonth != null )
            {
                return false;
            }
        }
        else if ( !reportingMonth.equals( other.reportingMonth ) )
        {
            return false;
        }
        
        if ( monthsThisYear == null )
        {
            if ( other.monthsThisYear != null )
            {
                return false;
            }
        }
        else if ( !monthsThisYear.equals( other.monthsThisYear ) )
        {
            return false;
        }
        
        if ( quartersThisYear == null )
        {
            if ( other.quartersThisYear != null )
            {
                return false;
            }
        }
        else if ( !quartersThisYear.equals( other.quartersThisYear ) )
        {
            return false;
        }
        
        if ( thisYear == null )
        {
            if ( other.thisYear != null )
            {
                return false;
            }
        }
        else if ( !thisYear.equals( other.thisYear ) )
        {
            return false;
        }
        
        if ( monthsLastYear == null )
        {
            if ( other.monthsLastYear != null )
            {
                return false;
            }
        }
        else if ( !monthsLastYear.equals( other.monthsLastYear ) )
        {
            return false;
        }
                
        if ( quartersLastYear == null )
        {
            if ( other.quartersLastYear != null )
            {
                return false;
            }
        }
        else if ( !quartersLastYear.equals( other.quartersLastYear ) )
        {
            return false;
        }
        
        if ( lastYear == null )
        {
            if ( other.lastYear != null )
            {
                return false;
            }
        }
        else if ( !lastYear.equals( other.lastYear ) )
        {
            return false;
        }
                
        return true;
    }
}
