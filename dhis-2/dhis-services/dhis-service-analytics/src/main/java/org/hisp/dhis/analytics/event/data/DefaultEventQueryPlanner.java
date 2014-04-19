package org.hisp.dhis.analytics.event.data;

/*
 * Copyright (c) 2004-2014, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.QueryPlanner;
import org.hisp.dhis.analytics.event.EventAnalyticsManager;
import org.hisp.dhis.analytics.event.EventAnalyticsService;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.EventQueryPlanner;
import org.hisp.dhis.analytics.table.PartitionUtils;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hisp.dhis.analytics.AnalyticsTableManager.EVENT_ANALYTICS_TABLE_NAME;

/**
 * @author Lars Helge Overland
 */
public class DefaultEventQueryPlanner
    implements EventQueryPlanner
{
    private static final Log log = LogFactory.getLog( DefaultEventQueryPlanner.class );
    
    @Autowired
    private QueryPlanner queryPlanner;

    @Autowired
    private EventAnalyticsManager analyticsManager;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;

    // -------------------------------------------------------------------------
    // EventQueryPlanner implementation
    // -------------------------------------------------------------------------

    @Override
    public void validate( EventQueryParams params )
        throws IllegalQueryException
    {
        String violation = null;

        if ( params == null )
        {
            throw new IllegalQueryException( "Params cannot be null" );
        }
        
        if ( !params.hasOrganisationUnits() )
        {
            violation = "At least one organisation unit must be specified";
        }
        
        if ( !params.hasPeriods() && ( params.getStartDate() == null || params.getEndDate() == null ) )
        {
            violation = "Start and end date or at least one period must be specified";
        }
        
        if ( params.getStartDate() != null && params.getEndDate() != null && params.getStartDate().after( params.getEndDate() ) )
        {
            violation = "Start date is after end date: " + params.getStartDate() + " - " + params.getEndDate();
        }

        if ( params.getPage() != null && params.getPage() <= 0 )
        {
            violation = "Page number must be positive: " + params.getPage();
        }
        
        if ( params.getPageSize() != null && params.getPageSize() < 0 )
        {
            violation = "Page size must be zero or positive: " + params.getPageSize();
        }
        
        if ( params.hasLimit() && params.getLimit() > EventAnalyticsService.MAX_ROWS_LIMIT )
        {
            violation = "Limit of: " + params.getLimit() + " is larger than max limit: " + EventAnalyticsService.MAX_ROWS_LIMIT;
        }
        
        if ( violation != null )
        {
            log.warn( "Validation failed: " + violation );
            
            throw new IllegalQueryException( violation );
        }
    }
    
    @Override
    public List<EventQueryParams> planAggregateQuery( EventQueryParams params )
    {
        Set<String> validPartitions = analyticsManager.getAnalyticsTables( params.getProgram() );

        List<EventQueryParams> queries = new ArrayList<EventQueryParams>();
        
        List<EventQueryParams> groupedByPartition = groupByPartition( params, validPartitions );
        
        for ( EventQueryParams byPartition : groupedByPartition )
        {
            List<EventQueryParams> groupedByOrgUnitLevel = convert( queryPlanner.groupByOrgUnitLevel( byPartition ) );
            
            for ( EventQueryParams byOrgUnitLevel : groupedByOrgUnitLevel )
            {
                queries.addAll( convert( queryPlanner.groupByPeriodType( byOrgUnitLevel ) ) );
            }
        }
        
        return queries;
    }

    @Override
    public EventQueryParams planEventQuery( EventQueryParams params )
    {
        Set<String> validPartitions = analyticsManager.getAnalyticsTables( params.getProgram() );

        String tableSuffix = "_" + params.getProgram().getUid();
        
        if ( params.hasStartEndDate() )
        {
            Period queryPeriod = new Period();
            queryPeriod.setStartDate( params.getStartDate() );
            queryPeriod.setEndDate( params.getEndDate() );            
            params.setPartitions( PartitionUtils.getPartitions( queryPeriod, EVENT_ANALYTICS_TABLE_NAME, tableSuffix, validPartitions ) );
        }
        
        if ( params.hasDimensionOrFilter( DimensionalObject.ORGUNIT_DIM_ID ) )
        {
            for ( NameableObject object : params.getDimensionOrFilter( DimensionalObject.ORGUNIT_DIM_ID ) )
            {
                OrganisationUnit unit = (OrganisationUnit) object; 
                unit.setLevel( organisationUnitService.getLevelOfOrganisationUnit( unit.getUid() ) );
            }
        }
        
        //TODO periods, convert to start/end dates
        
        return params;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private List<EventQueryParams> groupByPartition( EventQueryParams params, Set<String> validPartitions )
    {
        String tableSuffix = "_" + params.getProgram().getUid();
        
        if ( params.hasStartEndDate() )
        {
            List<EventQueryParams> queries = new ArrayList<EventQueryParams>();
            
            Period queryPeriod = new Period();
            queryPeriod.setStartDate( params.getStartDate() );
            queryPeriod.setEndDate( params.getEndDate() );
            
            EventQueryParams query = params.instance();
            query.setPartitions( PartitionUtils.getPartitions( queryPeriod, EVENT_ANALYTICS_TABLE_NAME, tableSuffix, validPartitions ) );
            
            if ( query.getPartitions().hasAny() )
            {
                queries.add( query );
            }
            
            return queries;
        }
        else // Aggregate only
        {
            return convert( queryPlanner.groupByPartition( params, EVENT_ANALYTICS_TABLE_NAME, tableSuffix ) );
        }
    }
        
    private static List<EventQueryParams> convert( List<DataQueryParams> params )
    {
        List<EventQueryParams> eventParams = new ArrayList<EventQueryParams>();
        
        for ( DataQueryParams param : params )
        {
            eventParams.add( (EventQueryParams) param );
        }
        
        return eventParams;
    }    
}
