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

package org.hisp.dhis.period;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Chau Thu Tran
 * 
 * @version FinancialAprilPeriodType.java Nov 3, 2010 12:55:07 PM
 */
public class FinancialAprilPeriodType
    extends CalendarPeriodType
{
    /**
     * The name of the FinancialAprilPeriods, which is "FinancialApril".
     */
    public static final String NAME = "FinancialApril";

    public static final int FREQUENCY_ORDER = 365;
    
    private static final int BASE_MONTH = Calendar.APRIL;

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Period createPeriod()
    {
        return createPeriod( createCalendarInstance() );
    }

    @Override
    public Period createPeriod( Date date )
    {
        return createPeriod( createCalendarInstance( date ) );
    }

    private Period createPeriod( Calendar cal )
    {
        boolean past = cal.get( Calendar.MONTH ) >= BASE_MONTH;
        
        cal.set( Calendar.YEAR, past ? cal.get( Calendar.YEAR ) : cal.get( Calendar.YEAR ) - 1 );
        cal.set( Calendar.MONTH, BASE_MONTH );
        cal.set( Calendar.DATE, 1 );

        Date startDate = cal.getTime();

        cal.add( Calendar.YEAR, 1 );
        cal.set( Calendar.DAY_OF_YEAR, cal.get( Calendar.DAY_OF_YEAR ) - 1  );

        return new Period( this, startDate, cal.getTime() );
    }

    @Override
    public int getFrequencyOrder()
    {
        return FREQUENCY_ORDER;
    }

    // -------------------------------------------------------------------------
    // CalendarPeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public Period getNextPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.add( Calendar.YEAR, 1 );
        return createPeriod( cal );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.add( Calendar.YEAR, -1 );
        return createPeriod( cal );
    }

    /**
     * Generates FinancialAprilPeriods for the last 5, current and next 5 years.
     */
    @Override
    public List<Period> generatePeriods( Date date )
    {
        ArrayList<Period> years = new ArrayList<Period>();

        Calendar cal = createCalendarInstance( date );
        cal.set( Calendar.YEAR, cal.get( Calendar.YEAR ) + cal.get( Calendar.MONDAY ) / 7 - 11);
        cal.set( Calendar.DAY_OF_YEAR, cal.getActualMinimum( Calendar.DAY_OF_YEAR ) + 90 );

        for ( int i = 0; i < 11; ++i )
        {
            Date startDate = cal.getTime();

            cal.add( Calendar.DAY_OF_YEAR, -1 );
            cal.add( Calendar.YEAR, 1 );
            years.add( new Period( this, startDate, cal.getTime() ) );

            cal.add( Calendar.DAY_OF_YEAR, 1 );
        }

        return years;
    }

    @Override
    public String getIsoDate( Period period )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public Period createPeriod( String isoDate )
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public String getIsoFormat()
    {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
