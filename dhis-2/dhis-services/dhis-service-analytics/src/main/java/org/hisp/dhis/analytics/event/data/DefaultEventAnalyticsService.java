package org.hisp.dhis.analytics.event.data;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import static org.hisp.dhis.analytics.DataQueryParams.OPTION_SEP;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.IllegalQueryException;
import org.hisp.dhis.analytics.event.EventAnalyticsManager;
import org.hisp.dhis.analytics.event.EventAnalyticsService;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.QueryItem;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class DefaultEventAnalyticsService
    implements EventAnalyticsService
{
    private static final String ITEM_EVENT = "psi";
    private static final String ITEM_PROGRAM_STAGE = "ps";
    private static final String ITEM_EXECUTION_DATE = "executiondate";
    private static final String ITEM_ORG_UNIT = "ou";
    
    @Autowired
    private ProgramService programService;
    
    @Autowired
    private ProgramStageService programStageService;

    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private PatientAttributeService attributeService;

    @Autowired
    private PatientIdentifierTypeService identifierTypeService;
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private EventAnalyticsManager analyticsManager;

    // -------------------------------------------------------------------------
    // EventAnalyticsService implementation
    // -------------------------------------------------------------------------

    //TODO org unit children / descendants
    
    public Grid getEvents( EventQueryParams params )
    {
        Grid grid = new ListGrid();
                
        grid.addHeader( new GridHeader( "Event", ITEM_EVENT ) );
        grid.addHeader( new GridHeader( "Program stage", ITEM_PROGRAM_STAGE ) );
        grid.addHeader( new GridHeader( "Execution date", ITEM_EXECUTION_DATE ) );
        grid.addHeader( new GridHeader( "Organisation unit", ITEM_ORG_UNIT ) );

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        for ( QueryItem queryItem : params.getItems() )
        {
            IdentifiableObject item = queryItem.getItem();
            
            grid.addHeader( new GridHeader( item.getName(), item.getUid() ) );
        }

        // ---------------------------------------------------------------------
        // Data
        // ---------------------------------------------------------------------

        List<EventQueryParams> queries = EventQueryPlanner.planQuery( params );
        
        for ( EventQueryParams query : queries )
        {
            analyticsManager.getEvents( query, grid );
        }

        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------

        Map<Object, Object> metaData = new HashMap<Object, Object>();
        metaData.put( AnalyticsService.NAMES_META_KEY, getUidNameMap( params ) );
        grid.setMetaData( metaData );
        
        return grid;
    }
    
    public EventQueryParams getFromUrl( String program, String stage, String startDate, String endDate, String ou, 
        Set<String> item, Set<String> asc, Set<String> desc, Integer page, Integer pageSize )
    {
        EventQueryParams params = new EventQueryParams();
        
        Program pr = programService.getProgram( program );
        
        if ( pr == null )
        {
            throw new IllegalQueryException( "Program does not exist: " + program );
        }
        
        ProgramStage ps = programStageService.getProgramStage( stage );
        
        if ( stage != null && !stage.isEmpty() && ps == null )
        {
            throw new IllegalQueryException( "Program stage is specified but does not exist: " + stage );
        }
        
        Date start = null;
        Date end = null;
        
        try
        {
            start = DateUtils.getMediumDate( startDate );
            end = DateUtils.getMediumDate( endDate );
        }
        catch ( RuntimeException ex )
        {
            throw new IllegalQueryException( "Start date or end date is invalid: " + startDate + " - " + endDate );
        }
        
        if ( start == null || end == null )
        {
            throw new IllegalQueryException( "Start date or end date is invalid: " + startDate + " - " + endDate );
        }
        
        if ( start.after( end ) )
        {
            throw new IllegalQueryException( "Start date is after end date: " + startDate + " - " + endDate );
        }
        
        if ( item != null )
        {
            for ( String it : item )
            {
                if ( it != null && !it.contains( OPTION_SEP ) )
                {
                    params.getItems().add( new QueryItem( getItem( it, pr ) ) );
                }
                else if ( it != null )
                {
                    String[] split = it.split( OPTION_SEP );
                    
                    if ( split == null || split.length != 3 )
                    {
                        throw new IllegalQueryException( "Item filter has invalid format: " + it );
                    }
                    
                    params.getItems().add( new QueryItem( getItem( split[0], pr ), split[1], split[2] ) );
                }
            }
        }
        
        if ( asc != null )
        {
            for ( String sort : asc )
            {
                params.getAsc().add( getSortItem( sort, pr ) );
            }
        }

        if ( desc != null )
        {
            for ( String sort : desc )
            {
                params.getDesc().add( getSortItem( sort, pr ) );
            }
        }
        
        if ( ou != null )
        {
            String[] split = ou.split( OPTION_SEP );
            
            for ( String ouId : split )
            {
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouId );
                
                if ( orgUnit != null )
                {
                    params.getOrganisationUnits().add( orgUnit );
                }
            }
        }
        
        if ( page != null && page <= 0 )
        {
            throw new IllegalQueryException( "Page number must be positive: " + page );
        }
        
        if ( pageSize != null && pageSize < 0 )
        {
            throw new IllegalQueryException( "Page size must be zero or positive: " + pageSize );
        }
        
        params.setProgram( pr );
        params.setProgramStage( ps );
        params.setStartDate( start );
        params.setEndDate( end );
        params.setPage( page );
        
        if ( pageSize != null )
        {
            params.setPageSize( pageSize );
        }
        
        return params;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Map<String, String> getUidNameMap( EventQueryParams params )
    {
        Map<String, String> map = new HashMap<String, String>();
        
        map.put( params.getProgram().getUid(), params.getProgram().getName() );
        map.put( params.getProgramStage().getUid(), params.getProgramStage().getName() );
        
        return map;
    }
    
    private String getSortItem( String item, Program program )
    {
        if ( !ITEM_EXECUTION_DATE.equals( item ) && getItem( item, program ) == null )
        {
            throw new IllegalQueryException( "Descending sort item is invalid: " + item );
        }
        
        return item;
    }
    
    private IdentifiableObject getItem( String item, Program program )
    {
        DataElement de = dataElementService.getDataElement( item );
        
        if ( de != null && program.getAllDataElements().contains( de ) )
        {
            return de;
        }
        
        PatientAttribute at = attributeService.getPatientAttribute( item );
        
        if ( at != null && program.getPatientAttributes().contains( at ) )
        {
            return at;
        }
        
        PatientIdentifierType it = identifierTypeService.getPatientIdentifierType( item );
        
        if ( it != null && program.getPatientIdentifierTypes().contains( it ) )
        {
            return it;
        }
        
        throw new IllegalQueryException( "Item identifier does not reference any item part of the program: " + item );           
    }
}
