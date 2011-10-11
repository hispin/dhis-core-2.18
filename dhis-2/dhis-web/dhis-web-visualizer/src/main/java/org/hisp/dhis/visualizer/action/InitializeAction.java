package org.hisp.dhis.visualizer.action;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.RelativePeriods;

import com.opensymphony.xwork2.Action;

/**
 * @author Jan Henrik Overland
 * @version $Id$
 */
public class InitializeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private OrganisationUnit rootNode;

    public OrganisationUnit getRootNode()
    {
        return rootNode;
    }
    
    private List<Period> lastMonth;
    
    public List<Period> getLastMonth()
    {
        return lastMonth;
    }

    private List<Period> monthsThisYear;

    public List<Period> getMonthsThisYear()
    {
        return monthsThisYear;
    }
    
    private List<Period> monthsLastYear;

    public List<Period> getMonthsLastYear()
    {
        return monthsLastYear;
    }
    
    private List<Period> lastQuarter;

    public List<Period> getLastQuarter()
    {
        return lastQuarter;
    }
    
    private List<Period> quartersThisYear;

    public List<Period> getQuartersThisYear()
    {
        return quartersThisYear;
    }
    
    private List<Period> quartersLastYear;

    public List<Period> getQuartersLastYear()
    {
        return quartersLastYear;
    }
    
    private List<Period> thisYear;

    public List<Period> getThisYear()
    {
        return thisYear;
    }
    
    private List<Period> lastYear;

    public List<Period> getLastYear()
    {
        return lastYear;
    }
    
    private List<Period> lastFiveYears;

    public List<Period> getLastFiveYears()
    {
        return lastFiveYears;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Collection<OrganisationUnit> rootUnits = new ArrayList<OrganisationUnit>( organisationUnitService
            .getOrganisationUnitsAtLevel( 1 ) );

        rootNode = rootUnits.size() > 0 ? rootUnits.iterator().next() : new OrganisationUnit();
        
        RelativePeriods rp = new RelativePeriods();
        
        rp.clear().setReportingMonth( true );
        lastMonth = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );
        
        rp.clear().setMonthsThisYear( true );
        monthsThisYear = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );
        
        rp.clear().setMonthsLastYear( true );
        monthsLastYear = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );
        
        rp.clear().setReportingQuarter( true );
        lastQuarter = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );
        
        rp.clear().setQuartersThisYear( true );
        quartersThisYear = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );
        
        rp.clear().setQuartersLastYear( true );
        quartersLastYear = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );
        
        rp.clear().setThisYear( true );
        thisYear = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );
        
        rp.clear().setLastYear( true );
        lastYear = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );
        
        rp.clear().setLast5Years( true );
        lastFiveYears = periodService.reloadPeriods( setNames( rp.getRelativePeriods() ) );

        return SUCCESS;
    }
    
    private List<Period> setNames( List<Period> periods )
    {
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }
        
        return periods;
    }
}