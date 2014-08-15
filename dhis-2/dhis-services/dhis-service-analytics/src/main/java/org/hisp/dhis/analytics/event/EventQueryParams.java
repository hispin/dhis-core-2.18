package org.hisp.dhis.analytics.event;

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

import static org.hisp.dhis.common.DimensionalObject.PERIOD_DIM_ID;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.analytics.DataQueryParams;
import org.hisp.dhis.analytics.Partitions;
import org.hisp.dhis.analytics.SortOrder;
import org.hisp.dhis.common.DimensionalObject;
import org.hisp.dhis.common.NameableObject;
import org.hisp.dhis.common.NameableObjectUtils;
import org.hisp.dhis.common.QueryItem;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramStage;

/**
 * @author Lars Helge Overland
 */
public class EventQueryParams
    extends DataQueryParams
{
    private Program program;
    
    private ProgramStage programStage;
    
    private Date startDate;
    
    private Date endDate;
    
    private List<QueryItem> items = new ArrayList<>();
    
    private List<QueryItem> itemFilters = new ArrayList<>();

    private List<String> asc = new ArrayList<>();
    
    private List<String> desc = new ArrayList<>();
    
    private String organisationUnitMode;
        
    private Integer page;
    
    private Integer pageSize;

    private SortOrder sortOrder;
    
    private Integer limit;
    
    private boolean uniqueInstances;
    
    private boolean coordinatesOnly;
    
    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------
    
    private String periodType;
    
    private boolean aggregate;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EventQueryParams()
    {
    }
    
    @Override
    public EventQueryParams instance()
    {
        EventQueryParams params = new EventQueryParams();

        params.dimensions = new ArrayList<>( this.dimensions );
        params.filters = new ArrayList<>( this.filters );
        params.aggregationType = this.aggregationType;

        params.partitions = new Partitions( this.partitions );
        params.periodType = this.periodType;
        
        params.program = this.program;
        params.programStage = this.programStage;
        params.startDate = this.startDate;
        params.endDate = this.endDate;
        params.items = new ArrayList<>( this.items );
        params.itemFilters = new ArrayList<>( this.itemFilters );
        params.asc = new ArrayList<>( this.asc );
        params.desc = new ArrayList<>( this.desc );
        params.organisationUnitMode = this.organisationUnitMode;
        params.page = this.page;
        params.pageSize = this.pageSize;
        params.sortOrder = this.sortOrder;
        params.limit = this.limit;
        params.uniqueInstances = this.uniqueInstances;
        params.coordinatesOnly = this.coordinatesOnly;
        
        params.periodType = this.periodType;
        params.aggregate = this.aggregate;
        
        return params;
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------
    
    /**
     * Replaces periods with start and end dates, using the earliest start date
     * from the periods as start date and the latest end date from the periods
     * as end date. Remove the period dimension or filter.
     */
    public void replacePeriodsWithStartEndDates()
    {
        List<Period> periods = NameableObjectUtils.asTypedList( getDimensionOrFilter( PERIOD_DIM_ID ), Period.class );
        
        for ( Period period : periods )
        {
            Date start = period.getStartDate();
            Date end = period.getEndDate();
            
            if ( startDate == null || ( start != null && start.before( startDate ) ) )
            {
                startDate = start;
            }
            
            if ( endDate == null || ( end != null && end.after( endDate ) ) )
            {
                endDate = end;
            }
        }
        
        removeDimensionOrFilter( PERIOD_DIM_ID );
    }

    /**
     * Returns a list of query items which occur more than once.
     */
    public List<QueryItem> getDuplicateQueryItems()
    {
        Set<QueryItem> dims = new HashSet<>();
        List<QueryItem> duplicates = new ArrayList<>();
        
        for ( QueryItem dim : items )
        {
            if ( !dims.add( dim ) )
            {
                duplicates.add( dim );
            }
        }
        
        return duplicates;
    }
    
    public boolean isOrganisationUnitMode( String mode )
    {
        return organisationUnitMode != null && organisationUnitMode.equalsIgnoreCase( mode );
    }
    
    public boolean hasStartEndDate()
    {
        return startDate != null && endDate != null;
    }
        
    public Set<OrganisationUnit> getOrganisationUnitChildren()
    {
        Set<OrganisationUnit> children = new HashSet<>();
        
        for ( NameableObject object : getDimensionOrFilter( DimensionalObject.ORGUNIT_DIM_ID ) )
        {
            OrganisationUnit unit = (OrganisationUnit) object;            
            children.addAll( unit.getChildren() );
        }
        
        return children;
    }
    
    public boolean isSorting()
    {
        return ( asc != null && !asc.isEmpty() ) || ( desc != null && !desc.isEmpty() );
    }
    
    public boolean isPaging()
    {
        return page != null || pageSize != null;
    }

    public int getPageWithDefault()
    {
        return page != null && page > 0 ? page : 1;
    }
    
    public int getPageSizeWithDefault()
    {
        return pageSize != null && pageSize >= 0 ? pageSize : 50;
    }

    public int getOffset()
    {
        return ( getPageWithDefault() - 1 ) * getPageSizeWithDefault();
    }
    
    public boolean hasSortOrder()
    {
        return sortOrder != null;
    }
    
    public boolean hasLimit()
    {
        return limit != null && limit > 0;
    }
    
    /**
     * Indicates whether the program of this query requires registration of
     * tracked entity instances.
     */
    public boolean isProgramRegistration()
    {
        return program != null && program.isRegistration();
    }
    
    /**
     * Returns a negative integer in case of ascending sort order, a positive in
     * case of descending sort order and 0 in case of no sort order.
     */
    public int getSortOrderAsInt()
    {
        return SortOrder.ASC.equals( sortOrder ) ? -1 : SortOrder.DESC.equals( sortOrder ) ? 1 : 0;
    }
    
    public String toString()
    {
        return "[" +
            "Program: " + program + ", " +
            "Stage: " + programStage + ", " +
            "Start date: " + startDate + ", " +
            "End date: " + endDate + ", " +
            "Items " + items + ", " +
            "Item filters: " + itemFilters + ", " +
            "Dimensions: " + dimensions + ", " +
            "Filters: " + filters + "]";
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate( Date startDate )
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }

    public List<QueryItem> getItems()
    {
        return items;
    }

    public void setItems( List<QueryItem> items )
    {
        this.items = items;
    }

    public List<QueryItem> getItemFilters()
    {
        return itemFilters;
    }

    public void setItemFilters( List<QueryItem> itemFilters )
    {
        this.itemFilters = itemFilters;
    }

    public List<String> getAsc()
    {
        return asc;
    }

    public List<DimensionalObject> getDimensions()
    {
        return dimensions;
    }

    public void setDimensions( List<DimensionalObject> dimensions )
    {
        this.dimensions = dimensions;
    }

    public void setAsc( List<String> asc )
    {
        this.asc = asc;
    }

    public List<String> getDesc()
    {
        return desc;
    }

    public void setDesc( List<String> desc )
    {
        this.desc = desc;
    }

    public String getOrganisationUnitMode()
    {
        return organisationUnitMode;
    }

    public void setOrganisationUnitMode( String organisationUnitMode )
    {
        this.organisationUnitMode = organisationUnitMode;
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

    public SortOrder getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( SortOrder sortOrder )
    {
        this.sortOrder = sortOrder;
    }

    public Integer getLimit()
    {
        return limit;
    }

    public void setLimit( Integer limit )
    {
        this.limit = limit;
    }
    
    public boolean isUniqueInstances()
    {
        return uniqueInstances;
    }

    public void setUniqueInstances( boolean uniqueInstances )
    {
        this.uniqueInstances = uniqueInstances;
    }

    public boolean isCoordinatesOnly()
    {
        return coordinatesOnly;
    }

    public void setCoordinatesOnly( boolean coordinatesOnly )
    {
        this.coordinatesOnly = coordinatesOnly;
    }

    public String getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    public boolean isAggregate()
    {
        return aggregate;
    }

    public void setAggregate( boolean aggregate )
    {
        this.aggregate = aggregate;
    }
}
