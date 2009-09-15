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
package org.hisp.dhis.reportexcel.export.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reportexcel.utils.DateUtils;

import com.opensymphony.xwork2.Action;
/**
 * @author Tran Thanh Tri
 * @version $Id$
 */
public class GetMonthlyPeriodsAction implements Action
{
    // -------------------------------------------
    // Dependency
    // -------------------------------------------

    private SelectionManager selectionManager;

    private PeriodService periodService;

    // -------------------------------------------
    // Input & Output
    // -------------------------------------------

    private String mode;

    private List<Period> periods;

    // -------------------------------------------
    // Getter & Setter
    // -------------------------------------------

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    public List<Period> getPeriods()
    {
        return periods;
    }

    public void setSelectionManager( SelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setMode( String mode )
    {
        this.mode = mode;
    }

    public String execute()
        throws Exception
    {
        int selectedYear = selectionManager.getSelectedYear();

        Date lastDateOfYear;

        if ( mode.equalsIgnoreCase( "current" ) )
        {
            selectedYear = DateUtils.getCurrentYear();

            int currentDay = DateUtils.getCurrentMonth();

            Calendar calendar = Calendar.getInstance();
            calendar.set( Calendar.YEAR, selectedYear );
            calendar.set( Calendar.MONTH, currentDay );
            calendar.set( Calendar.DATE, calendar.getActualMaximum( Calendar.DATE ) );

            lastDateOfYear = calendar.getTime();
        }

        if ( mode.equalsIgnoreCase( "next" ) )
        {
            selectedYear++;
        }
        if ( mode.equalsIgnoreCase( "previous" ) )
        {
            selectedYear--;
        }
        selectionManager.setSeletedYear( selectedYear );

        Date firstDateOfYear = DateUtils.getFirstDayOfYear( selectedYear );

        lastDateOfYear = DateUtils.getLastDayOfYear( selectedYear );

        PeriodType periodType = periodService.getPeriodTypeByName( "Monthly" );

        periods = new ArrayList<Period>( periodService.getIntersectingPeriodsByPeriodType( periodType, firstDateOfYear,
            lastDateOfYear ) );

        Collections.sort( periods, new PeriodComparator() );

        return SUCCESS;
    }

}
