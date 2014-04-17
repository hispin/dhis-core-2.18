package org.hisp.dhis.trackedentity;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.OrganisationUnitSelectionMode;
import org.hisp.dhis.common.QueryFilter;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.common.SetMap;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStatus;

/**
 * @author Lars Helge Overland
 */
public class TrackedEntityInstanceQueryParams
{
    public static final String TRACKED_ENTITY_INSTANCE_ID = "instance";
    public static final String CREATED_ID = "created";
    public static final String LAST_UPDATED_ID = "lastupdated";
    public static final String ORG_UNIT_ID = "ou";
    public static final String TRACKED_ENTITY_ID = "te";
    public static final String TRACKED_ENTITY_ATTRIBUTE_ID = "teattribute";
    public static final String TRACKED_ENTITY_ATTRIBUTE_VALUE_ID = "tevalue";
    
    public static final String META_DATA_NAMES_KEY = "names";
    public static final String PAGER_META_KEY = "pager";
    
    /**
     * Query value, will apply to all relevant attributes.
     */
    private String query;
    
    /**
     * Attributes to be included in the response. Can be used to filter response.
     */
    private List<QueryItem> attributes = new ArrayList<QueryItem>();

    /**
     * Filters for the response.
     */
    private List<QueryItem> filters = new ArrayList<QueryItem>();
    
    /**
     * Organisation units for which instances in the response were registered at.
     * Is related to the specified OrganisationUnitMode.
     */
    private Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();
    
    /**
     * Program for which instances in the response must be enrolled in.
     */
    private Program program;
    
    /**
     * Status of the tracked entity instance in the given program.
     */
    private ProgramStatus programStatus;
    
    /**
     * Indicates whether tracked entity instance is marked for follow up for the
     * specified program.
     */
    private Boolean followUp;
    
    /**
     * Enrollment dates for the given program.
     */
    private List<QueryFilter> programDates = new ArrayList<QueryFilter>();
    
    /**
     * Tracked entity of the instances in the response.
     */
    private TrackedEntity trackedEntity;
    
    /**
     * Selection mode for the specified organisation units.
     */
    private OrganisationUnitSelectionMode organisationUnitMode;

    /**
     * Indicates whether not to include meta data in the response.
     */
    private boolean skipMeta;

    /**
     * Page number.
     */
    private Integer page;
    
    /**
     * Page size.
     */
    private Integer pageSize;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public TrackedEntityInstanceQueryParams()
    {
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------
    
    /**
     * Performs a set of operations on this params.
     * 
     * <ul>
     * <li>
     * If a query item is specified as an attribute item as well as a filter 
     * item, the filter item will be removed. In that case, if the attribute 
     * item does not have a filter value and the filter item has a filter value, 
     * it will be applied to the attribute item.
     * </li>
     * </ul> 
     */
    public void conform()
    {
        Iterator<QueryItem> filterIter = filters.iterator();
        
        while ( filterIter.hasNext() )
        {
            QueryItem filter = filterIter.next();
        
            int index = attributes.indexOf( filter ); // Filter present as attr
            
            if ( index >= 0 )
            {
                QueryItem attribute = attributes.get( index );
                
                if ( !attribute.hasFilter() )
                {
                    attribute.setOperator( filter.getOperator() );
                    attribute.setFilter( filter.getFilter() );
                }
                
                filterIter.remove();
            }
        }
    }
    
    /**
     * Returns a mapping between level and organisation units.
     */
    public SetMap<Integer, OrganisationUnit> getLevelOrgUnitMap()
    {
        SetMap<Integer, OrganisationUnit> setMap = new SetMap<Integer, OrganisationUnit>();
        
        for ( OrganisationUnit ou : organisationUnits )
        {
            setMap.putValue( ou.getLevel(), ou );
        }
        
        return setMap;
    }
    
    /**
     * Indicates whether this is a logical OR query, meaning that a query string
     * is specified and instances which matches this query on one or more attributes
     * should be included in the response. The opposite is an item-specific query,
     * where the instances which matches the specific attributes should be included.
     */
    public boolean isOrQuery()
    {
        return hasQuery();
    }
    
    /**
     * Indicates whether this params specifies a query.
     */
    public boolean hasQuery()
    {
        return query != null && !query.isEmpty();
    }
    
    /**
     * Returns a list of attributes and filters combined.
     */
    public List<QueryItem> getAttributesAndFilters()
    {
        List<QueryItem> items = new ArrayList<QueryItem>();
        items.addAll( attributes );
        items.addAll( filters );
        return items;
    }

    /**
     * Returns a list of attributes which appear more than once.
     */
    public List<QueryItem> getDuplicateAttributes()
    {
        Set<QueryItem> items = new HashSet<QueryItem>();
        List<QueryItem> duplicates = new ArrayList<QueryItem>();
        
        for ( QueryItem item : getAttributes() )
        {
            if ( !items.add( item ) )
            {
                duplicates.add( item );
            }
        }
        
        return duplicates;
    }

    /**
     * Returns a list of attributes which appear more than once.
     */
    public List<QueryItem> getDuplicateFilters()
    {
        Set<QueryItem> items = new HashSet<QueryItem>();
        List<QueryItem> duplicates = new ArrayList<QueryItem>();
        
        for ( QueryItem item : getFilters() )
        {
            if ( !items.add( item ) )
            {
                duplicates.add( item );
            }
        }
        
        return duplicates;
    }
        
    /**
     * Add the given attributes to this params if they are not already present.
     */
    public void addAttributesIfNotExist( List<QueryItem> attrs )
    {
        for ( QueryItem attr : attrs )
        {
            if ( attributes != null && !attributes.contains( attr ) )
            {
                attributes.add( attr );            
            }
        }
    }
    
    /**
     * Adds the given filters to this params if they are not already present.
     */
    public void addFiltersIfNotExist( List<QueryItem> filtrs )
    {
        for ( QueryItem filter : filtrs )
        {
            if ( filters != null && !filters.contains( filter ) )
            {
                filters.add( filter );
            }
        }
    }
    
    /**
     * Indicates whether this params specifies any attributes and/or filters.
     */
    public boolean hasAttributesOrFilters()
    {
        return hasAttributes() || hasFilters();
    }

    /**
     * Indicates whether this params specifies any attributes.
     */
    public boolean hasAttributes()
    {
        return attributes != null && !attributes.isEmpty();
    }
    
    /**
     * Indicates whether this params specifies any filters.
     */
    public boolean hasFilters()
    {
        return filters != null && !filters.isEmpty();
    }

    /**
     * Indicates whether this params specifies any organisation units.
     */
    public boolean hasOrganisationUnits()
    {
        return organisationUnits != null && !organisationUnits.isEmpty();
    }
    
    /**
     * Indicates whether this params specifies a program.
     */
    public boolean hasProgram()
    {
        return program != null;
    }
    
    /**
     * Indicates whether this params specifies a program status.
     */
    public boolean hasProgramStatus()
    {
        return programStatus != null;
    }
    
    /**
     * Indicates whether this params specifies follow up for the given program.
     * Follow up can be specified as true or false.
     */
    public boolean hasFollowUp()
    {
        return followUp != null;
    }
    
    /**
     * Indicates whether this params specifies any program dates.
     * @return
     */
    public boolean hasProgramDates()
    {
        return programDates != null && !programDates.isEmpty();
    }
    
    /**
     * Indicates whether this params specifies a tracked entity.
     */
    public boolean hasTrackedEntity()
    {
        return trackedEntity != null;
    }
    
    /**
     * Indicates whethert this params is of the given organisation unit mode.
     */
    public boolean isOrganisationUnitMode( OrganisationUnitSelectionMode mode )
    {
        return organisationUnitMode != null && organisationUnitMode.equals( mode );
    }
    
    /**
     * Indicates whether paging is enabled.
     */
    public boolean isPaging()
    {
        return page != null || pageSize != null;
    }

    /**
     * Returns the page number, falls back to default value of 1 if not specified.
     */
    public int getPageWithDefault()
    {
        return page != null && page > 0 ? page : 1;
    }
    
    /**
     * Returns the page size, falls back to default value of 50 if not specified.
     */
    public int getPageSizeWithDefault()
    {
        return pageSize != null && pageSize >= 0 ? pageSize : 50;
    }

    /**
     * Returns the offset based on the page number and page size.
     */
    public int getOffset()
    {
        return ( getPageWithDefault() - 1 ) * getPageSizeWithDefault();
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getQuery()
    {
        return query;
    }

    public void setQuery( String query )
    {
        this.query = query;
    }

    public List<QueryItem> getAttributes()
    {
        return attributes;
    }

    public void setAttributes( List<QueryItem> attributes )
    {
        this.attributes = attributes;
    }

    public List<QueryItem> getFilters()
    {
        return filters;
    }

    public void setFilters( List<QueryItem> filters )
    {
        this.filters = filters;
    }

    public OrganisationUnitSelectionMode getOrganisationUnitMode()
    {
        return organisationUnitMode;
    }

    public void setOrganisationUnitMode( OrganisationUnitSelectionMode organisationUnitMode )
    {
        this.organisationUnitMode = organisationUnitMode;
    }

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public ProgramStatus getProgramStatus()
    {
        return programStatus;
    }

    public void setProgramStatus( ProgramStatus programStatus )
    {
        this.programStatus = programStatus;
    }

    public Boolean getFollowUp()
    {
        return followUp;
    }

    public void setFollowUp( Boolean followUp )
    {
        this.followUp = followUp;
    }

    public List<QueryFilter> getProgramDates()
    {
        return programDates;
    }

    public void setProgramDates( List<QueryFilter> programDates )
    {
        this.programDates = programDates;
    }

    public TrackedEntity getTrackedEntity()
    {
        return trackedEntity;
    }

    public void setTrackedEntity( TrackedEntity trackedEntity )
    {
        this.trackedEntity = trackedEntity;
    }

    public Set<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( Set<OrganisationUnit> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

    public boolean isSkipMeta()
    {
        return skipMeta;
    }

    public void setSkipMeta( boolean skipMeta )
    {
        this.skipMeta = skipMeta;
    }
    
    public Integer getPage()
    {
        return page;
    }

    public void setPage( Integer page )
    {
        this.page = page;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize( Integer pageSize )
    {
        this.pageSize = pageSize;
    }
}
