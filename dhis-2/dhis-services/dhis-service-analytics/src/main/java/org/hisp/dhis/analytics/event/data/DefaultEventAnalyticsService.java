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

import static org.hisp.dhis.analytics.DataQueryParams.DIMENSION_NAME_SEP;
import static org.hisp.dhis.common.DimensionalObject.ORGUNIT_DIM_ID;
import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.analytics.AnalyticsService;
import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.IllegalQueryException;
import org.hisp.dhis.analytics.event.EventAnalyticsManager;
import org.hisp.dhis.analytics.event.EventAnalyticsService;
import org.hisp.dhis.analytics.event.EventQueryParams;
import org.hisp.dhis.analytics.event.EventQueryPlanner;
import org.hisp.dhis.analytics.event.QueryItem;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.Pager;
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
import org.hisp.dhis.system.util.Timer;
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
    private static final String ITEM_ORG_UNIT_NAME = "ouname";
    private static final String ITEM_ORG_UNIT_CODE = "oucode";
    private static final String ITEM_GENDER = "gender";
    private static final String ITEM_ISDEAD = "isdead";
    
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
    
    @Autowired
    private EventQueryPlanner queryPlanner;
    
    @Autowired
    private AnalyticsService analyticsService;

    // -------------------------------------------------------------------------
    // EventAnalyticsService implementation
    // -------------------------------------------------------------------------

    //TODO order the event analytics tables up front to avoid default sorting in queries
    //TODO filter items support
    //TODO relative and fixed periods support
    
    public Grid getAggregatedEventData( EventQueryParams params )
    {
        queryPlanner.validate( params );

        Grid grid = new ListGrid();

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        for ( QueryItem item : params.getItems() )
        {
            grid.addHeader( new GridHeader( item.getItem().getUid(), item.getItem().getName() ) );
        }
        
        grid.addHeader( new GridHeader( PERIOD_DIM_ID, "Period" ) );
        grid.addHeader( new GridHeader( ORGUNIT_DIM_ID, "Organisation unit" ) );
        grid.addHeader( new GridHeader( "value", "Value" ) );

        // ---------------------------------------------------------------------
        // Data
        // ---------------------------------------------------------------------

        //TODO relative periods
                
        List<EventQueryParams> queries = queryPlanner.planQuery( params );

        for ( EventQueryParams query : queries )
        {
            analyticsManager.getAggregatedEventData( query, grid );
        }        

        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------

        Map<Object, Object> metaData = new HashMap<Object, Object>();        
        metaData.put( AnalyticsService.NAMES_META_KEY, getUidNameMap( params ) );
        grid.setMetaData( metaData );

        return grid;        
    }
    
    public Grid getEvents( EventQueryParams params )
    {
        queryPlanner.validate( params );

        Grid grid = new ListGrid();

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        grid.addHeader( new GridHeader( ITEM_EVENT, "Event" ) );
        grid.addHeader( new GridHeader( ITEM_PROGRAM_STAGE, "Program stage" ) );
        grid.addHeader( new GridHeader( ITEM_EXECUTION_DATE, "Execution date" ) );
        grid.addHeader( new GridHeader( ITEM_ORG_UNIT, "Organisation unit" ) );
        grid.addHeader( new GridHeader( ITEM_ORG_UNIT_NAME, "Organisation unit name" ) );
        grid.addHeader( new GridHeader( ITEM_ORG_UNIT_CODE, "Organisation unit code" ) );

        for ( QueryItem queryItem : params.getItems() )
        {
            IdentifiableObject item = queryItem.getItem();
            
            grid.addHeader( new GridHeader( item.getName(), item.getUid() ) );
        }

        // ---------------------------------------------------------------------
        // Data
        // ---------------------------------------------------------------------

        Timer t = new Timer().start();
        
        List<EventQueryParams> queries = queryPlanner.planQuery( params );
        
        t.getSplitTime( "Planned query, got: " + queries.size() );
        
        int count = 0;
        
        for ( EventQueryParams query : queries )
        {
            if ( params.isPaging() )
            {
                count += analyticsManager.getEventCount( query );
            }
            
            analyticsManager.getEvents( query, grid );
        }
        
        t.getTime( "Queried events, got: " + grid.getHeight() );
        
        // ---------------------------------------------------------------------
        // Meta-data
        // ---------------------------------------------------------------------

        Map<Object, Object> metaData = new HashMap<Object, Object>();        
        metaData.put( AnalyticsService.NAMES_META_KEY, getUidNameMap( params ) );

        if ( params.isPaging() )
        {
            Pager pager = new Pager( params.getPageWithDefault(), count, params.getPageSizeWithDefault() );
            metaData.put( AnalyticsService.PAGER_META_KEY, pager );
        }
        
        grid.setMetaData( metaData );
        
        return grid;
    }

    public EventQueryParams getFromUrl( String program, String stage, String startDate, String endDate, 
        Set<String> dimension, String ouMode )
    {
        return getFromUrl( program, stage, startDate, endDate, dimension, ouMode, null, null, null, null );
    }
    
    public EventQueryParams getFromUrl( String program, String stage, String startDate, String endDate, 
        Set<String> dimension, String ouMode, Set<String> asc, Set<String> desc, Integer page, Integer pageSize )
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
        
        if ( startDate != null && endDate != null )
        {
            try
            {
                start = DateUtils.getMediumDate( startDate );
                end = DateUtils.getMediumDate( endDate );
            }
            catch ( RuntimeException ex )
            {
                throw new IllegalQueryException( "Start date or end date is invalid: " + startDate + " - " + endDate );
            }
        }
        
        for ( String it : dimension )
        {
            String dim = DataQueryParams.getDimensionFromParam( it );
            
            if ( ORGUNIT_DIM_ID.equals( dim ) || PERIOD_DIM_ID.equals( dim ) )
            {
                List<String> items = DataQueryParams.getDimensionItemsFromParam( it );
                params.getDimensions().addAll( analyticsService.getDimension( dim, items, null, null ) );
            }
            else if ( !it.contains( DIMENSION_NAME_SEP ) )
            {
                params.getItems().add( getItem( pr, it, null, null ) );
            }
            else // Filter
            {
                String[] split = it.split( DIMENSION_NAME_SEP );
                
                if ( split == null || split.length != 3 )
                {
                    throw new IllegalQueryException( "Item filter has invalid format: " + it );
                }
                
                params.getItems().add( getItem( pr, split[0], split[1], split[2] ) );
            }
        }
        
        for ( NameableObject object : params.getOrganisationUnits() )
        {
            OrganisationUnit unit = (OrganisationUnit) object;
            unit.setLevel( organisationUnitService.getLevelOfOrganisationUnit( unit.getUid() ) );
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
                
        if ( params.getOrganisationUnits().isEmpty() )
        {
            throw new IllegalQueryException( "At least one organisation unit must be specified" );
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
        params.setOrganisationUnitMode( ouMode );
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
        
        Program program = params.getProgram();
        ProgramStage stage = params.getProgramStage();
        
        map.put( program.getUid(), program.getName() );
        
        if ( stage != null )
        {
            map.put( stage.getUid(), stage.getName() );
        }
        
        return map;
    }
    
    private String getSortItem( String item, Program program )
    {
        if ( !ITEM_EXECUTION_DATE.equals( item ) && getItem( program, item, null, null ) == null )
        {
            throw new IllegalQueryException( "Descending sort item is invalid: " + item );
        }
        
        return item;
    }
    
    private QueryItem getItem( Program program, String item, String operator, String filter )
    {
        if ( ITEM_GENDER.equalsIgnoreCase( item ) )
        {
            return new QueryItem( new BaseIdentifiableObject( ITEM_GENDER, ITEM_GENDER, ITEM_GENDER ), operator, filter );
        }
        
        if ( ITEM_ISDEAD.equalsIgnoreCase( item ) )
        {
            return new QueryItem( new BaseIdentifiableObject( ITEM_ISDEAD, ITEM_ISDEAD, ITEM_ISDEAD ), operator, filter );
        }
        
        DataElement de = dataElementService.getDataElement( item );
        
        if ( de != null && program.getAllDataElements().contains( de ) )
        {
            return new QueryItem( de, operator, filter );
        }
        
        PatientAttribute at = attributeService.getPatientAttribute( item );
        
        if ( at != null && program.getPatientAttributes().contains( at ) )
        {
            return new QueryItem( at, operator, filter );
        }
        
        PatientIdentifierType it = identifierTypeService.getPatientIdentifierType( item );
        
        if ( it != null && program.getPatientIdentifierTypes().contains( it ) )
        {
            return new QueryItem( it, operator, filter );
        }
        
        throw new IllegalQueryException( "Item identifier does not reference any item part of the program: " + item );           
    }
}
