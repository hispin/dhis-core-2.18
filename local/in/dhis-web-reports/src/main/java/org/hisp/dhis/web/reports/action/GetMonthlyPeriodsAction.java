package org.hisp.dhis.web.reports.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.web.reports.PeriodStartDateComparator;

import com.opensymphony.xwork.Action;

/**
 * @author Kristian Nordal
 * @version $Id$
 */
public class GetMonthlyPeriodsAction
    implements Action
{
    private List<Period> monthlyPeriods;

    private String monthlyPeriodTypeId;

    // ----------------------------------------------------------------------
    // Dependencies
    // ----------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // ----------------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------------

    public List getMonthlyPeriods()
    {
        return monthlyPeriods;
    }

    public String getMonthlyPeriodTypeId()
    {
        return monthlyPeriodTypeId;
    }

    // ----------------------------------------------------------------------
    // Execute
    // ----------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Collection periodTypes = periodService.getAllPeriodTypes();

        PeriodType type = null;

        Iterator iter = periodTypes.iterator();

        while ( iter.hasNext() )
        {
            PeriodType periodType = (PeriodType) iter.next();

            if ( periodType.getName().toLowerCase().trim().equals( "monthly" ) )
            {
                type = periodType;
            }
        }

        if ( type != null )
        {
            monthlyPeriods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( type ) );
            Collections.sort( monthlyPeriods, new PeriodStartDateComparator() );

            return SUCCESS;
        }
        else
        {
            return ERROR;
        }
    }
}
