package org.hisp.dhis.datamart.aggregation.cache;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElementOperand;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ConversionUtils;

/**
 * @author Lars Helge Overland
 */
public class MemoryAggregationCache
    implements AggregationCache
{
    private static final String SEPARATOR = "-";
    
    // -------------------------------------------------------------------------
    // Cache
    // -------------------------------------------------------------------------

    private final Map<String, Collection<Integer>> intersectingPeriodCache = new HashMap<String,Collection<Integer>>();

    private final Map<String, Collection<Integer>> periodBetweenDatesCache = new HashMap<String,Collection<Integer>>();

    private final Map<String, Collection<Integer>> periodBetweenDatesPeriodTypeCache = new HashMap<String,Collection<Integer>>();

    private final Map<String, Period> periodCache = new HashMap<String,Period>();

    private final Map<String, Integer> organisationUnitLevelCache = new HashMap<String, Integer>();
    
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
    
    // -------------------------------------------------------------------------
    // AggregationCache implementation
    // -------------------------------------------------------------------------

    public Collection<Integer> getIntersectingPeriods( final Date startDate, final Date endDate )
    {
        final String key = startDate.toString() + SEPARATOR + endDate.toString();
        
        Collection<Integer> periods = null;
        
        if ( ( periods = intersectingPeriodCache.get( key ) ) != null )
        {
            return periods;
        }
        
        periods = ConversionUtils.getIdentifiers( Period.class, periodService.getIntersectingPeriods( startDate, endDate ) );
        
        intersectingPeriodCache.put( key, periods );
        
        return periods;
    }

    public Collection<Integer> getPeriodsBetweenDates( final Date startDate, final Date endDate )
    {
        final String key = startDate.toString() + SEPARATOR + endDate.toString();
        
        Collection<Integer> periods = null;
        
        if ( ( periods = periodBetweenDatesCache.get( key ) ) != null )
        {
            return periods;
        }
        
        periods = ConversionUtils.getIdentifiers( Period.class, periodService.getPeriodsBetweenDates( startDate, endDate ) );
        
        periodBetweenDatesCache.put( key, periods );
        
        return periods;
    }

    public Collection<Integer> getPeriodsBetweenDatesPeriodType( final PeriodType periodType, final Date startDate, final Date endDate ) //TODO remove?
    {
        final String key = periodType.getName() + SEPARATOR + startDate.toString() + SEPARATOR + endDate.toString();
        
        Collection<Integer> periods = null;
        
        if ( ( periods = periodBetweenDatesPeriodTypeCache.get( key ) ) != null )
        {
            return periods;
        }
        
        periods = ConversionUtils.getIdentifiers( Period.class, periodService.getPeriodsBetweenDates( periodType, startDate, endDate ) );
        
        periodBetweenDatesPeriodTypeCache.put( key, periods );
        
        return periods;
    }
    
    public Period getPeriod( final int id )
    {
        final String key = String.valueOf( id );
        
        Period period = null;
        
        if ( ( period = periodCache.get( key ) ) != null )
        {
            return period;
        }
        
        period = periodService.getPeriod( id );
        
        periodCache.put( key, period );
        
        return period;
    }

    public int getLevelOfOrganisationUnit( final int id )
    {
        final String key = String.valueOf( id );
        
        Integer level = null;
        
        if ( ( level = organisationUnitLevelCache.get( key ) ) != null )
        {
            return level;
        }
                
        level = organisationUnitService.getLevelOfOrganisationUnit( id );
        
        organisationUnitLevelCache.put( key, level );
        
        return level;
    }
    
    public void filterForAggregationLevel( Set<Integer> organisationUnits, DataElementOperand operand, int unitLevel )
    {
        final Iterator<Integer> iter = organisationUnits.iterator();
        
        while ( iter.hasNext() )
        {
            final Integer orgUnitId = iter.next();
            
            final int dataValueLevel = operand.isHasAggregationLevels() ? getLevelOfOrganisationUnit( orgUnitId ) : 0;
            
            if ( operand.isHasAggregationLevels() && !operand.aggregationLevelIsValid( unitLevel, dataValueLevel ) )
            {
                iter.remove();
            }
        }        
    }
    
    public void clearCache()
    {
        intersectingPeriodCache.clear();
        periodBetweenDatesCache.clear();
        periodBetweenDatesPeriodTypeCache.clear();
        periodCache.clear();
        organisationUnitLevelCache.clear();
    }
}
