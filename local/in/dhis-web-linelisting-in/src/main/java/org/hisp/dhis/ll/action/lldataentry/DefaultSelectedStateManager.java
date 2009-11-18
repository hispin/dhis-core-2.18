package org.hisp.dhis.ll.action.lldataentry;

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

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.linelisting.LineListGroup;
import org.hisp.dhis.linelisting.LineListService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.ActionContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserStore;

public class DefaultSelectedStateManager
    implements SelectedStateManager
{
    private static final Log LOG = LogFactory.getLog( DefaultSelectedStateManager.class );

    public static final String SESSION_KEY_SELECTED_GROUP_ID = "data_entry_selected_group_id";

    public static final String SESSION_KEY_SELECTED_PERIOD_INDEX = "data_entry_selected_period_index";

    public static final String SESSION_KEY_BASE_PERIOD = "data_entry_base_period";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LineListService lineListService;

    public void setLineListService( LineListService lineListService )
    {
        this.lineListService = lineListService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }
    
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Cache
    // -------------------------------------------------------------------------

    private ThreadLocal<List<Period>> generatedPeriodsCache = new ThreadLocal<List<Period>>();

    // -------------------------------------------------------------------------
    // SelectedStateManager implementation
    // -------------------------------------------------------------------------

    public OrganisationUnit getSelectedOrganisationUnit()
    {
        return selectionManager.getSelectedOrganisationUnit();
    }

    @SuppressWarnings( "unchecked" )
    public void setSelectedLineListGroup( LineListGroup lineListGroup )
    {
        getSession().put( SESSION_KEY_SELECTED_GROUP_ID, lineListGroup.getId() );
    }

    public LineListGroup getSelectedLineListGroup()
    {
        Integer id = (Integer) getSession().get( SESSION_KEY_SELECTED_GROUP_ID );

        if ( id == null )
        {
            return null;
        }

        return lineListService.getLineListGroup( id );
    }

    public void clearSelectedLineListGroup()
    {
        getSession().remove( SESSION_KEY_SELECTED_GROUP_ID );
    }

    @SuppressWarnings( "unchecked" )
    public void setSelectedPeriodIndex( Integer index )
    {
        getSession().put( SESSION_KEY_SELECTED_PERIOD_INDEX, index );
    }

    public Integer getSelectedPeriodIndex()
    {
        return (Integer) getSession().get( SESSION_KEY_SELECTED_PERIOD_INDEX );
    }

    public Period getSelectedPeriod()
    {
        Integer index = getSelectedPeriodIndex();

        if ( index == null )
        {
            return null;
        }

        List<Period> periods = getPeriodList();

        if ( index >= 0 && index < periods.size() )
        {
            return periods.get( index );
        }

        return null;
    }

    public void clearSelectedPeriod()
    {
        getSession().remove( SESSION_KEY_SELECTED_PERIOD_INDEX );
    }

    public List<Period> getPeriodList()
    {
        List<Period> periods = generatedPeriodsCache.get();
        Period basePeriod = getBasePeriod();

        if ( periods == null || periods.size() == 0
            || !periods.get( 0 ).getPeriodType().equals( basePeriod.getPeriodType() ) || !periods.contains( basePeriod ) )
        {
            CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();

            LOG.debug( "Generated periods cache invalid, generating new periods based on " + basePeriod );

            periods = periodType.generatePeriods( basePeriod );

            generatedPeriodsCache.set( periods );
        }
        
        Date now = new Date();       
        
        Iterator<Period> iterator = periods.iterator();
        
        while( iterator.hasNext() )
        {
        	Period period = iterator.next();
        	
        	if ( period.getStartDate().after( now ) )
        	{
        		iterator.remove();
        	}        		
        }

        return periods;
    }

    @SuppressWarnings( "unchecked" )
    public void nextPeriodSpan()
    {
        List<Period> periods = getPeriodList();
        CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();

        Period basePeriod = periods.get( periods.size() - 1 );
        Period newBasePeriod = periodType.getNextPeriod( basePeriod );

        getSession().put( SESSION_KEY_BASE_PERIOD, newBasePeriod );

        generatedPeriodsCache.remove();
    }

    @SuppressWarnings( "unchecked" )
    public void previousPeriodSpan()
    {
        List<Period> periods = getPeriodList();
        CalendarPeriodType periodType = (CalendarPeriodType) getPeriodType();

        Period basePeriod = periods.get( 0 );
        Period newBasePeriod = periodType.getPreviousPeriod( basePeriod );

        getSession().put( SESSION_KEY_BASE_PERIOD, newBasePeriod );

        generatedPeriodsCache.remove();
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    private PeriodType getPeriodType()
    {
        LineListGroup lineListGroup = getSelectedLineListGroup();

        if ( lineListGroup == null )
        {
            throw new IllegalStateException( "Cannot ask for PeriodType when no LineListGroup is selected" );
        }

        return lineListGroup.getPeriodType();
    }

    @SuppressWarnings( "unchecked" )
    private Period getBasePeriod()
    {
        Period basePeriod = (Period) getSession().get( SESSION_KEY_BASE_PERIOD );
        PeriodType periodType = getPeriodType();

        if ( basePeriod == null )
        {
            LOG.debug( "getBasePeriod(): Base period is null, creating new." );

            basePeriod = periodType.createPeriod();
            getSession().put( SESSION_KEY_BASE_PERIOD, basePeriod );
        }
        else if ( !basePeriod.getPeriodType().equals( periodType ) )
        {
            LOG.debug( "getBasePeriod(): Wrong type of base period, transforming." );

            basePeriod = periodType.createPeriod( basePeriod.getStartDate() );
            getSession().put( SESSION_KEY_BASE_PERIOD, basePeriod );
        }

        return basePeriod;
    }

    private static final Map getSession()
    {
        return ActionContext.getContext().getSession();
    }

    public List<LineListGroup> loadLineListGroupsForSelectedOrgUnit(OrganisationUnit organisationUnit)
    {
        List<LineListGroup> lineListGroups = new ArrayList<LineListGroup>( lineListService.getLineListGroupsBySource( organisationUnit ) );

        return lineListGroups;
    }


}
