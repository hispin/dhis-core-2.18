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
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.common.SetMap;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.program.Program;

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
    
    private String query;
    
    private List<QueryItem> items = new ArrayList<QueryItem>();

    private Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();
    
    private Program program;
    
    private TrackedEntity trackedEntity;
    
    private String organisationUnitMode;

    private Integer page;
    
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
     * Indicates whether this params specifies any query items.
     */
    public boolean hasItems()
    {
        return items != null && !items.isEmpty();
    }
    
    /**
     * Indicates whether this params specifies a program.
     */
    public boolean hasProgram()
    {
        return program != null;
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
    public boolean isOrganisationUnitMode( String mode )
    {
        return organisationUnitMode != null && organisationUnitMode.equalsIgnoreCase( mode );
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

    public List<QueryItem> getItems()
    {
        return items;
    }

    public void setItems( List<QueryItem> items )
    {
        this.items = items;
    }

    public String getOrganisationUnitMode()
    {
        return organisationUnitMode;
    }

    public void setOrganisationUnitMode( String organisationUnitMode )
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
