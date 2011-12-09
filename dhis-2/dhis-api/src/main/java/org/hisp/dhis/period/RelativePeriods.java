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
import java.util.Set;

import org.hisp.dhis.i18n.I18nFormat;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class RelativePeriods
    implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 2949655296199662273L;

    public static final String REPORTING_MONTH = "reporting_month";
    public static final String REPORTING_BIMONTH = "reporting_bimonth";
    public static final String REPORTING_QUARTER = "reporting_quarter";
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
    
    public static final String[] MONTHS_LAST_12 = {
        "month1",
        "month2",
        "month3",
        "month4",
        "month5",
        "month6",
        "month7",
        "month8",
        "month9",
        "month10",
        "month11",
        "month12" };
    
    public static final String[] BIMONTHS_LAST_6 = {
        "bimonth1",
        "bimonth2",
        "bimonth3",
        "bimonth4",
        "bimonth5",
        "bimonth6" };
    
    public static final String[] QUARTERS_THIS_YEAR = {
        "quarter1",
        "quarter2",
        "quarter3",
        "quarter4" };
    
    public static final String[] SIXMONHTS_LAST_2 = {
        "sixmonth1",
        "sixmonth2" };

    public static final String[] QUARTERS_LAST_YEAR = {
        "quarter1_last_year",
        "quarter2_last_year",
        "quarter3_last_year",
        "quarter4_last_year" };
        
    public static final String[] LAST_5_YEARS = {
        "year_minus_4",
        "year_minus_3",
        "year_minus_2",
        "year_minus_1",
        "year_this" };

    private static final int MONTHS_IN_YEAR = 12;
    
    private Boolean reportingMonth = false;
    
    private Boolean reportingBimonth = false;
    
    private Boolean reportingQuarter = false;
    
    private Boolean monthsThisYear = false;
    
    private Boolean quartersThisYear = false;
    
    private Boolean thisYear = false;
    
    private Boolean monthsLastYear = false;
    
    private Boolean quartersLastYear = false;
    
    private Boolean lastYear = false;

    private Boolean last5Years = false;
    
    private Boolean last12Months = false;
    
    private Boolean last6BiMonths = false;
    
    private Boolean last4Quarters = false;
    
    private Boolean last2SixMonths = false;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public RelativePeriods()
    {   
    }
    
    /**
     * @param reportingMonth reporting month
     * @param reportingBimonth reporting bi-month
     * @param reportingQuarter reporting quarter
     * @param monthsThisYear months this year
     * @param quartersThisYear quarters this year
     * @param thisYear this year
     * @param monthsLastYear months last year
     * @param quartersLastYear quarters last year
     * @param lastYear last year
     * @param last5Years last 5 years
     * @param last12Months last 12 months
     * @param last6BiMonths last 6 bi-months
     * @param last4Quarters last 4 quarters
     * @param last2SixMonths last 2 six-months
     */
    public RelativePeriods( boolean reportingMonth, boolean reportingBimonth, boolean reportingQuarter,
        boolean monthsThisYear, boolean quartersThisYear, boolean thisYear,
        boolean monthsLastYear, boolean quartersLastYear, boolean lastYear, boolean last5Years,
        boolean last12Months, boolean last6BiMonths, boolean last4Quarters, boolean last2SixMonths )
    {
        this.reportingMonth = reportingMonth;
        this.reportingBimonth = reportingBimonth;
        this.reportingQuarter = reportingQuarter;
        this.monthsThisYear = monthsThisYear;
        this.quartersThisYear = quartersThisYear;
        this.thisYear = thisYear;
        this.monthsLastYear = monthsLastYear;
        this.quartersLastYear = quartersLastYear;
        this.lastYear = lastYear;
        this.last5Years = last5Years;
        this.last12Months = last12Months;
        this.last6BiMonths = last6BiMonths;
        this.last4Quarters = last4Quarters;
        this.last2SixMonths = last2SixMonths;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------
    
    /**
     * Sets all options to false.
     */
    public RelativePeriods clear()
    {
        this.reportingMonth = false;
        this.reportingBimonth = false;
        this.reportingQuarter = false;
        this.monthsThisYear = false;
        this.quartersThisYear = false;
        this.thisYear = false;
        this.monthsLastYear = false;
        this.quartersLastYear = false;
        this.lastYear = false;
        this.last5Years = false;
        this.last12Months = false;
        this.last6BiMonths = false;
        this.last4Quarters = false;
        this.last2SixMonths = false;
        
        return this;
    }

    /**
     * Returns the period type for the option with the lowest frequency.
     * 
     * @return the period type.
     */
    public PeriodType getPeriodType()
    {
        if ( isReportingMonth() )
        {
            return PeriodType.getPeriodTypeByName( MonthlyPeriodType.NAME );
        }
        
        if ( isReportingBimonth() )
        {
            return PeriodType.getPeriodTypeByName( BiMonthlyPeriodType.NAME );
        }
        
        if ( isReportingQuarter() )
        {
            return PeriodType.getPeriodTypeByName( QuarterlyPeriodType.NAME );
        }
        
        return PeriodType.getPeriodTypeByName( YearlyPeriodType.NAME );
    }
    
    /**
     * Return the name of the reporting period.
     * 
     * @param date the start date of the reporting period.
     * @param format the i18n format.
     * @return the name of the reporting period.
     */
    public String getReportingPeriodName( Date date, I18nFormat format )
    {
        Period period = getPeriodType().createPeriod( date );
        return format.formatPeriod( period );
    }

    /**
     * Return the name of the reporting period. The current date is set to 
     * todays date minus one month.
     * 
     * @param format the i18n format.
     * @return the name of the reporting period.
     */
    public String getReportingPeriodName( I18nFormat format )
    {
        Period period = getPeriodType().createPeriod( getDate( 1, new Date() ) );
        return format.formatPeriod( period );
    }
    
    /**
     * Gets a list of Periods relative to current date.
     */
    public List<Period> getRelativePeriods()
    {
        return getRelativePeriods( getDate( 1, new Date() ), null, false );
    }

    /**
     * Gets a list of Periods based on the given input and the state of this 
     * RelativePeriods. The current date is set to todays date minus one month.
     * 
     * @param format the i18n format.
     * @return a list of relative Periods.
     */
    public List<Period> getRelativePeriods( I18nFormat format, boolean dynamicNames )
    {
        return getRelativePeriods( getDate( 1, new Date() ), format, dynamicNames );
    }
    
    /**
     * Gets a list of Periods based on the given input and the state of this 
     * RelativePeriods.
     * 
     * @param date the date representing now.
     * @param format the i18n format.
     * @return a list of relative Periods.
     */
    public List<Period> getRelativePeriods( Date date, I18nFormat format, boolean dynamicNames )
    {
        List<Period> periods = new ArrayList<Period>();
        
        if ( isReportingMonth() )
        {
            periods.add( getRelativePeriod( new MonthlyPeriodType(), REPORTING_MONTH, date, dynamicNames, format ) );
        }
        
        if ( isReportingBimonth() )
        {
            periods.add( getRelativePeriod( new BiMonthlyPeriodType(), REPORTING_BIMONTH, date, dynamicNames, format ) );
        }
        
        if ( isReportingQuarter() )
        {
            periods.add( getRelativePeriod( new QuarterlyPeriodType(), REPORTING_QUARTER, date, dynamicNames, format ) );
        }
        
        if ( isMonthsThisYear() )
        {
            periods.addAll( getRelativePeriodList( new MonthlyPeriodType(), MONTHS_THIS_YEAR, date, dynamicNames, format ) );
        }
        
        if ( isQuartersThisYear() )
        {
            periods.addAll( getRelativePeriodList( new QuarterlyPeriodType(), QUARTERS_THIS_YEAR, date, dynamicNames, format ) );
        }
        
        if ( isThisYear() )
        {
            periods.add( getRelativePeriod( new YearlyPeriodType(), THIS_YEAR, date, dynamicNames, format ) );
        }

        if ( isLast5Years() )
        {
            periods.addAll( getRelativePeriodList( new YearlyPeriodType().generateLast5Years( date ), LAST_5_YEARS, dynamicNames, format ) );
        }
        
        if ( isLast12Months() )
        {
            periods.addAll( getRelativePeriodList( new MonthlyPeriodType().generateRollingPeriods( date ), MONTHS_LAST_12, dynamicNames, format ) );
        }
        
        if ( isLast6BiMonths() )
        {
            periods.addAll( getRelativePeriodList( new BiMonthlyPeriodType().generateRollingPeriods( date ), BIMONTHS_LAST_6, dynamicNames, format ) );
        }
        
        if ( isLast4Quarters() )
        {
            periods.addAll( getRelativePeriodList( new QuarterlyPeriodType().generateRollingPeriods( date ), QUARTERS_THIS_YEAR, dynamicNames, format ) );
        }
        
        if ( isLast2SixMonths() )
        {
            periods.addAll( getRelativePeriodList( new SixMonthlyPeriodType().generateRollingPeriods( date ), SIXMONHTS_LAST_2, dynamicNames, format ) );
        }
        
        date = getDate( MONTHS_IN_YEAR, date );
        
        if ( isMonthsLastYear() )
        {
            periods.addAll( getRelativePeriodList( new MonthlyPeriodType(), MONTHS_LAST_YEAR, date, dynamicNames, format ) );
        }
        
        if ( isQuartersLastYear() )
        {
            periods.addAll( getRelativePeriodList( new QuarterlyPeriodType(), QUARTERS_LAST_YEAR, date, dynamicNames, format ) );
        }
        
        if ( isLastYear() )
        {
            periods.add( getRelativePeriod( new YearlyPeriodType(), LAST_YEAR, date, dynamicNames, format ) );
        }
        
        return periods;
    }

    /**
     * Returns a list of relative periods. The name will be dynamic depending on
     * the dynamicNames argument. The short name will always be dynamic.
     * 
     * @param periodType the period type.
     * @param periodNames the array of period names.
     * @param date the current date.
     * @param dynamicNames indication of whether dynamic names should be used.
     * @param format the I18nFormat.
     * @return a list of periods.
     */
    private List<Period> getRelativePeriodList( CalendarPeriodType periodType, String[] periodNames, Date date, boolean dynamicNames, I18nFormat format )
    {
        return getRelativePeriodList( periodType.generatePeriods( date ), periodNames, dynamicNames, format );
    }

    /**
     * Returns a list of relative periods. The name will be dynamic depending on
     * the dynamicNames argument. The short name will always be dynamic.
     * 
     * @param relatives the list of periods.
     * @param periodNames the array of period names.
     * @param dynamicNames indication of whether dynamic names should be used.
     * @param format the I18nFormat.
     * @return a list of periods.
     */
    private List<Period> getRelativePeriodList( List<Period> relatives, String[] periodNames, boolean dynamicNames, I18nFormat format )
    {
        List<Period> periods = new ArrayList<Period>();
        
        int c = 0;
        
        for ( Period period : relatives )
        {
            periods.add( setName( period, periodNames[c++], dynamicNames, format ) );
        }
        
        return periods;
    }

    /**
     * Returns relative period. The name will be dynamic depending on the
     * dynamicNames argument. The short name will always be dynamic.
     * 
     * @param periodType the period type.
     * @param periodName the period name.
     * @param date the current date.
     * @param dynamicNames indication of whether dynamic names should be used.
     * @param format the I18nFormat.
     * @return a list of periods.
     */
    private Period getRelativePeriod( CalendarPeriodType periodType, String periodName, Date date, boolean dynamicNames, I18nFormat format )
    {
        return setName( periodType.createPeriod( date ), periodName, dynamicNames, format );
    }
    
    /**
     * Sets the name and short name of the given Period.
     * 
     * @param period the period.
     * @param periodName the period name.
     * @param dynamicNames indication of whether dynamic names should be used.
     * @param format the I18nFormat.
     * @return a period.
     */
    private Period setName( Period period, String periodName, boolean dynamicNames, I18nFormat format )
    {
        period.setName( dynamicNames && format != null ? format.formatPeriod( period ) : periodName );
        period.setShortName( format != null ? format.formatPeriod( period ) : null );        
        return period;
    }
    
    /**
     * Returns a date.
     * 
     * @param months the number of months to subtract from the current date.
     * @param date the date representing now, ignored if null.
     * @return a date.
     */
    public Date getDate( int months, Date date )
    {
        Calendar cal = PeriodType.createCalendarInstance();
        
        if ( date != null ) // For testing purposes
        {
            cal.setTime( date );
        }
        
        cal.add( Calendar.MONTH, ( months * -1 ) );        
        
        return cal.getTime();
    }

    /**
     * Creates an instance of RelativePeriods based on given set of PeriodType
     * names.
     * 
     * @return a RelativePeriods instance.
     */
    public RelativePeriods getRelativePeriods( Set<String> periodTypes )
    {
        RelativePeriods relatives = new RelativePeriods();
        
        if ( periodTypes == null || periodTypes.isEmpty() )
        {
            relatives.setLast12Months( true );
            relatives.setLast4Quarters( true );
            relatives.setLastYear( true );
        }
        else
        {
            relatives.setLast12Months( periodTypes.contains( MonthlyPeriodType.NAME ) );
            relatives.setLast6BiMonths( periodTypes.contains( BiMonthlyPeriodType.NAME ) );
            relatives.setLast4Quarters( periodTypes.contains( QuarterlyPeriodType.NAME ) );
            relatives.setLast2SixMonths( periodTypes.contains( SixMonthlyPeriodType.NAME ) );
            relatives.setLastYear( periodTypes.contains( YearlyPeriodType.NAME ) );
        }
        
        return relatives;
    }
    
    // -------------------------------------------------------------------------
    // Is methods
    // -------------------------------------------------------------------------

    public boolean isReportingMonth()
    {
        return reportingMonth != null && reportingMonth;
    }
    
    public boolean isReportingBimonth()
    {
        return reportingBimonth != null && reportingBimonth;
    }
    
    public boolean isReportingQuarter()
    {
        return reportingQuarter != null && reportingQuarter;
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
    
    public boolean isLast5Years()
    {
        return last5Years != null && last5Years;
    }
    
    public boolean isLast12Months()
    {
        return last12Months != null && last12Months;
    }
    
    public boolean isLast6BiMonths()
    {
        return last6BiMonths != null && last6BiMonths;
    }
    
    public boolean isLast4Quarters()
    {
        return last4Quarters != null && last4Quarters;
    }
        
    public boolean isLast2SixMonths()
    {
        return last2SixMonths != null && last2SixMonths;
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

    public Boolean getReportingBimonth()
    {
        return reportingBimonth;
    }

    public void setReportingBimonth( Boolean reportingBimonth )
    {
        this.reportingBimonth = reportingBimonth;
    }

    public Boolean getReportingQuarter()
    {
        return reportingQuarter;
    }

    public void setReportingQuarter( Boolean reportingQuarter )
    {
        this.reportingQuarter = reportingQuarter;
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

    public Boolean getLast5Years()
    {
        return last5Years;
    }

    public void setLast5Years( Boolean last5Years )
    {
        this.last5Years = last5Years;
    }

    public Boolean getLast12Months()
    {
        return last12Months;
    }

    public void setLast12Months( Boolean last12Months )
    {
        this.last12Months = last12Months;
    }

    public Boolean getLast6BiMonths()
    {
        return last6BiMonths;
    }

    public void setLast6BiMonths( Boolean last6BiMonths )
    {
        this.last6BiMonths = last6BiMonths;
    }

    public Boolean getLast4Quarters()
    {
        return last4Quarters;
    }

    public void setLast4Quarters( Boolean last4Quarters )
    {
        this.last4Quarters = last4Quarters;
    }

    public Boolean getLast2SixMonths()
    {
        return last2SixMonths;
    }

    public void setLast2SixMonths( Boolean last2SixMonths )
    {
        this.last2SixMonths = last2SixMonths;
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
        result = prime * result + ( ( reportingBimonth == null ) ? 0 : reportingBimonth.hashCode() );
        result = prime * result + ( ( reportingQuarter == null ) ? 0 : reportingQuarter.hashCode() );
        result = prime * result + ( ( monthsThisYear == null ) ? 0 : monthsThisYear.hashCode() );
        result = prime * result + ( ( quartersThisYear == null ) ? 0 : quartersThisYear.hashCode() );
        result = prime * result + ( ( thisYear == null ) ? 0 : thisYear.hashCode() );
        result = prime * result + ( ( monthsLastYear == null ) ? 0 : monthsLastYear.hashCode() );
        result = prime * result + ( ( quartersLastYear == null ) ? 0 : quartersLastYear.hashCode() );
        result = prime * result + ( ( lastYear == null ) ? 0 : lastYear.hashCode() );
        result = prime * result + ( ( last5Years == null ) ? 0 : last5Years.hashCode() );
        result = prime * result + ( ( last12Months == null ) ? 0 : last12Months.hashCode() );
        result = prime * result + ( ( last6BiMonths == null ) ? 0 : last6BiMonths.hashCode() );
        result = prime * result + ( ( last4Quarters == null ) ? 0 : last4Quarters.hashCode() );
        result = prime * result + ( ( last2SixMonths == null ) ? 0 : last2SixMonths.hashCode() );
        
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

        if ( reportingBimonth == null )
        {
            if ( other.reportingBimonth != null )
            {
                return false;
            }
        }
        else if ( !reportingBimonth.equals( other.reportingBimonth ) )
        {
            return false;
        }

        if ( reportingQuarter == null )
        {
            if ( other.reportingQuarter != null )
            {
                return false;
            }
        }
        else if ( !reportingQuarter.equals( other.reportingQuarter ) )
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

        if ( last5Years == null )
        {
            if ( other.last5Years != null )
            {
                return false;
            }
        }
        else if ( !last5Years.equals( other.last5Years ) )
        {
            return false;
        }

        if ( last12Months == null )
        {
            if ( other.last12Months != null )
            {
                return false;
            }
        }
        else if ( !last12Months.equals( other.last12Months ) )
        {
            return false;
        }

        if ( last6BiMonths == null )
        {
            if ( other.last6BiMonths != null )
            {
                return false;
            }
        }
        else if ( !last6BiMonths.equals( other.last6BiMonths ) )
        {
            return false;
        }

        if ( last4Quarters == null )
        {
            if ( other.last4Quarters != null )
            {
                return false;
            }
        }
        else if ( !last4Quarters.equals( other.last4Quarters ) )
        {
            return false;
        }

        if ( last2SixMonths == null )
        {
            if ( other.last2SixMonths != null )
            {
                return false;
            }
        }
        else if ( !last2SixMonths.equals( other.last2SixMonths ) )
        {
            return false;
        }
        
        return true;
    }
}
