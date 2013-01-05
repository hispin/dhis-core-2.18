package org.hisp.dhis.analytics;

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

import static org.hisp.dhis.analytics.AggregationType.AVERAGE_DISAGGREGATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.CollectionUtils;
import org.hisp.dhis.system.util.ListMap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement( localName = "dxf2", namespace = Dxf2Namespace.NAMESPACE )
public class DataQueryParams
{
    public static final String INDICATOR_DIM_ID = "in";
    public static final String DATAELEMENT_DIM_ID = "de";
    public static final String CATEGORYOPTIONCOMBO_DIM_ID = "coc";
    public static final String PERIOD_DIM_ID = "pe";
    public static final String ORGUNIT_DIM_ID = "ou";
    public static final String VALUE_ID = "value";    
    public static final String LEVEL_PREFIX = "uidlevel";

    private static final String DIMENSION_SEP = ";";
    private static final String DIMENSION_NAME_SEP = ":";
    private static final String OPTION_SEP = ",";
    
    private Map<String, List<String>> dimensions = new HashMap<String, List<String>>();
    
    private boolean categories = false;

    private Map<String, List<String>> filters = new HashMap<String, List<String>>();
    
    // -------------------------------------------------------------------------
    // Transient properties
    // -------------------------------------------------------------------------
    
    private transient String tableName;

    private transient String periodType;
    
    private transient int organisationUnitLevel;
    
    private transient AggregationType aggregationType;
    
    private transient PeriodType dataPeriodType;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
  
    public DataQueryParams()
    {
    }
    
    public DataQueryParams( Map<String, List<String>> dimensions, boolean categories, Map<String, List<String>> filters )
    {
        this.dimensions = dimensions;
        this.categories = categories;
        this.filters = filters;
    }
    
    public DataQueryParams( DataQueryParams params )
    {
        this.dimensions = new HashMap<String, List<String>>( params.getDimensions() );
        this.categories = params.isCategories();
        this.filters = new HashMap<String, List<String>>( params.getFilters() );
        
        this.tableName = params.getTableName();
        this.periodType = params.getPeriodType();
        this.organisationUnitLevel = params.getOrganisationUnitLevel();
        this.aggregationType = params.getAggregationType();
        this.dataPeriodType = params.getDataPeriodType();
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    /**
     * Creates a list of the names of all dimensions for this query. If the period
     * type property is set, the period dimension name will be replaced by the name
     * of the period type, if present. If the organisation unit level property
     * is set, the organisation unit dimension name will be replaced by the name
     * of the organisation unit level column.
     */
    public List<String> getDimensionNames()
    {
        List<String> list = getDimensionNamesAsList();
        
        if ( categories )
        {
            list.add( CATEGORYOPTIONCOMBO_DIM_ID );
        }
        
        if ( list.contains( PERIOD_DIM_ID ) && periodType != null )
        {
            list.set( list.indexOf( PERIOD_DIM_ID ), periodType );
        }
        
        if ( list.contains( ORGUNIT_DIM_ID ) && organisationUnitLevel != 0 )
        {
            list.set( list.indexOf( ORGUNIT_DIM_ID ), LEVEL_PREFIX + organisationUnitLevel );
        }
                
        return list;
    }
    
    /**
     * Returns the index of the period dimension in the index list.
     */
    public int getPeriodDimensionIndex()
    {
        return getDimensionNamesAsList().indexOf( PERIOD_DIM_ID );
    }
    
    /**
     * Returns a list of the names of all filters.
     */
    public List<String> getFilterNames()
    {
        return new ArrayList<String>( filters.keySet() );
    }
    
    /**
     * Returns a mapping between the dimension names and dimension values. Inserts
     * keys and values for the current period type column name and organisation 
     * unit level name, if the period type property and organisation unit level
     * property are set.
     */
    public Map<String, List<String>> getDimensionMap()
    {
        Map<String, List<String>> map = new HashMap<String, List<String>>();

        map.putAll( dimensions );
        
        if ( periodType != null )
        {
            map.put( periodType, dimensions.get( PERIOD_DIM_ID ) );
        }
        
        if ( organisationUnitLevel != 0 )
        {
            map.put( LEVEL_PREFIX + organisationUnitLevel, dimensions.get( ORGUNIT_DIM_ID ) );
        }
        
        return map;
    }
    
    /**
     * Returns the dimensions which are part of dimensions and filters. If any
     * such dimensions exist this object is in an illegal state.
     */
    public Collection<String> dimensionsAsFilters()
    {
        return CollectionUtils.intersection( dimensions.keySet(), filters.keySet() );
    }
    
    /**
     * Indicates whether periods are present as a dimension or as a filter. If
     * not this object is in an illegal state.
     */
    public boolean hasPeriods()
    {
        return dimensions.containsKey( PERIOD_DIM_ID ) || filters.containsKey( PERIOD_DIM_ID );
    }
    
    /**
     * Indicates whether this object is of the given aggregation type.
     */
    public boolean isAggregationType( AggregationType aggregationType )
    {
        return this.aggregationType != null && this.aggregationType.equals( aggregationType );
    }

    /**
     * Creates a mapping between the data periods, based on the data period type
     * for this query, and the aggregation periods for this query.
     */
    public ListMap<String, String> getDataPeriodAggregationPeriodMap()
    {
        ListMap<String, String> map = new ListMap<String, String>();

        if ( dataPeriodType != null )
        {
            for ( String period : this.getPeriods() )
            {
                Period aggregatePeriod = PeriodType.getPeriodFromIsoString( period );
                
                Period dataPeriod = dataPeriodType.createPeriod( aggregatePeriod.getStartDate() );
                
                map.putValue( dataPeriod.getIsoDate(), period );
            }
        }
        
        return map;
    }
    
    /**
     * Replaces the periods of this query with the corresponding data periods.
     * Sets the period type to the data period type. This method is relevant only 
     * when then the data period type has lower frequency than the aggregation 
     * period type.
     */
    public void replaceAggregationPeriodsWithDataPeriods( ListMap<String, String> dataPeriodAggregationPeriodMap )
    {
        if ( isAggregationType( AVERAGE_DISAGGREGATION ) &&  dataPeriodType != null )
        {
            this.periodType = this.dataPeriodType.getName();
            
            setPeriods( new ArrayList<String>( getDataPeriodAggregationPeriodMap().keySet() ) );
        }
    }

    /**
     * Creates an instance based on a URL.
     */
    public static DataQueryParams getFromUrl( String dimensions, String filters, boolean categories )
    {
        DataQueryParams params = new DataQueryParams();
        
        params.getDimensions().putAll( getDimension( dimensions ) );
        params.getFilters().putAll( getDimension( filters ) );
        params.setCategories( categories );
        
        return params;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Gets a mapping between dimension name and dimension options for the given
     * query parameter.
     */
    private static Map<String, List<String>> getDimension( String requestParam )
    {
        Map<String, List<String>> map = new HashMap<String, List<String>>();

        if ( requestParam == null || requestParam.isEmpty() )
        {
            return map;
        }
        
        String[] dimensions = requestParam.split( DIMENSION_SEP );
        
        for ( String dimension : dimensions )
        {
            String[] elements = dimension.split( DIMENSION_NAME_SEP );
            
            if ( elements[0] != null && !elements[0].isEmpty() && elements[1] != null && !elements[1].isEmpty() )
            {                
                List<String> options = Arrays.asList( elements[1].split( OPTION_SEP ) );
            
                map.put( elements[0], options );
            }
        }
        
        return map;
    }

    /**
     * Returns the dimension names as a list.
     */
    private List<String> getDimensionNamesAsList()
    {
        return new ArrayList<String>( dimensions.keySet() );
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( categories ? 1231 : 1237);
        result = prime * result + ( ( dimensions == null ) ? 0 : dimensions.hashCode() );
        result = prime * result + ( ( filters == null ) ? 0 : filters.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object object )
    {
        if ( this == object )
        {
            return true;
        }
        
        if ( object == null )
        {
            return false;
        }
        
        if ( getClass() != object.getClass() )
        {
            return false;
        }
        
        DataQueryParams other = (DataQueryParams) object;
        
        if ( dimensions == null )
        {
            if ( other.dimensions != null )
            {
                return false;
            }
        }
        else if ( !dimensions.equals( other.dimensions ) )
        {
            return false;
        }
        
        if ( categories != other.categories )
        {
            return false;
        }

        if ( filters == null )
        {
            if ( other.filters != null )
            {
                return false;
            }
        }
        else if ( !filters.equals( other.filters ) )
        {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString()
    {
        return "[Dimensions: " + dimensions + ", Filters: " + filters + "]";
    }
    
    // -------------------------------------------------------------------------
    // Get and set methods for serialize properties
    // -------------------------------------------------------------------------
  
    @JsonProperty( value = "dimensions" )
    public Map<String, List<String>> getDimensions()
    {
        return dimensions;
    }

    public void setDimensions( Map<String, List<String>> dimensions )
    {
        this.dimensions = dimensions;
    }

    @JsonProperty( value = "categories" )
    public boolean isCategories()
    {
        return categories;
    }

    public void setCategories( boolean categories )
    {
        this.categories = categories;
    }

    @JsonProperty( value = "filters" )
    public Map<String, List<String>> getFilters()
    {
        return filters;
    }

    public void setFilters( Map<String, List<String>> filters )
    {
        this.filters = filters;
    }

    // -------------------------------------------------------------------------
    // Get and set methods for transient properties
    // -------------------------------------------------------------------------
  
    public String getTableName()
    {
        return tableName;
    }

    public void setTableName( String tableName )
    {
        this.tableName = tableName;
    }

    public String getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( String periodType )
    {
        this.periodType = periodType;
    }

    public int getOrganisationUnitLevel()
    {
        return organisationUnitLevel;
    }

    public void setOrganisationUnitLevel( int organisationUnitLevel )
    {
        this.organisationUnitLevel = organisationUnitLevel;
    }

    // -------------------------------------------------------------------------
    // Get and set helpers for dimensions or filter
    // -------------------------------------------------------------------------
  
    public List<String> getDimensionOrFilter( String key )
    {
        return dimensions.containsKey( key ) ? dimensions.get( key ) : filters.get( key );
    }
    
    public void resetDimensionOrFilter( String key, List<String> values )
    {
        if ( dimensions.containsKey( key ) )
        {
            dimensions.put( key, values );
        }
        else if ( filters.containsKey( key ) )
        {
            filters.put( key, values );
        }
    }
    
    // -------------------------------------------------------------------------
    // Get and set helpers for dimensions
    // -------------------------------------------------------------------------
  
    public List<String> getDatElements()
    {
        return dimensions.get( DATAELEMENT_DIM_ID );
    }
    
    public void setDataElements( List<String> dataElements )
    {
        dimensions.put( DATAELEMENT_DIM_ID, dataElements );
    }
    
    public List<String> getPeriods()
    {
        return dimensions.get( PERIOD_DIM_ID );
    }
    
    public void setPeriods( List<String> periods )
    {
        dimensions.put( PERIOD_DIM_ID, periods );
    }

    public List<String> getOrganisationUnits()
    {
        return dimensions.get( ORGUNIT_DIM_ID );
    }
    
    public void setOrganisationUnits( List<String> organisationUnits )
    {
        dimensions.put( ORGUNIT_DIM_ID, organisationUnits );
    }
    
    // -------------------------------------------------------------------------
    // Get and set helpers for filters
    // -------------------------------------------------------------------------

    public List<String> getFilterDatElements()
    {
        return filters.get( DATAELEMENT_DIM_ID );
    }
    
    public void setFilterDataElements( List<String> dataElements )
    {
        filters.put( DATAELEMENT_DIM_ID, dataElements );
    }
    
    public List<String> getFilterPeriods()
    {
        return filters.get( PERIOD_DIM_ID );
    }
    
    public void setFilterPeriods( List<String> periods )
    {
        filters.put( PERIOD_DIM_ID, periods );
    }
    
    public List<String> getFilterOrganisationUnits()
    {
        return filters.get( ORGUNIT_DIM_ID );
    }
    
    public void setFilterOrganisationUnits( List<String> organisationUnits )
    {
        filters.put( ORGUNIT_DIM_ID, organisationUnits );
    }

    public AggregationType getAggregationType()
    {
        return aggregationType;
    }

    public void setAggregationType( AggregationType aggregationType )
    {
        this.aggregationType = aggregationType;
    }

    public PeriodType getDataPeriodType()
    {
        return dataPeriodType;
    }

    public void setDataPeriodType( PeriodType dataPeriodType )
    {
        this.dataPeriodType = dataPeriodType;
    }
}
