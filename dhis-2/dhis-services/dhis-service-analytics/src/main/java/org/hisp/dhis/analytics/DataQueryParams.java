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

import org.hisp.dhis.common.CombinationGenerator;
import org.hisp.dhis.common.Dxf2Namespace;
import org.hisp.dhis.common.IdentifiableObject;
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
    
    private static final String DIMENSION_NAME_SEP = ":";
    private static final String OPTION_SEP = ",";
    public static final char DIMENSION_SEP = '-';
    
    private static final DimensionOption[] DIM_OPT_ARR = new DimensionOption[0];
    private static final DimensionOption[][] DIM_OPT_2D_ARR = new DimensionOption[0][];
    
    private Map<String, List<IdentifiableObject>> dimensions = new HashMap<String, List<IdentifiableObject>>();
    
    private boolean categories = false;

    private Map<String, List<IdentifiableObject>> filters = new HashMap<String, List<IdentifiableObject>>();
    
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
    
    public DataQueryParams( Map<String, List<IdentifiableObject>> dimensions, boolean categories, Map<String, List<IdentifiableObject>> filters )
    {
        this.dimensions = dimensions;
        this.categories = categories;
        this.filters = filters;
    }
    
    public DataQueryParams( DataQueryParams params )
    {
        this.dimensions = new HashMap<String, List<IdentifiableObject>>( params.getDimensions() );
        this.categories = params.isCategories();
        this.filters = new HashMap<String, List<IdentifiableObject>>( params.getFilters() );
        
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
        List<String> list = getDimensionNamesIgnoreCategories();

        if ( categories )
        {
            list.add( CATEGORYOPTIONCOMBO_DIM_ID );
        }
        
        return list;
    }
    
    /**
     * Creates a list of the names of all dimensions for this query. If the period
     * type property is set, the period dimension name will be replaced by the name
     * of the period type, if present. If the organisation unit level property
     * is set, the organisation unit dimension name will be replaced by the name
     * of the organisation unit level column. Does not include the categories
     * dimension, even if the categories property of this object is true.
     */
    public List<String> getDimensionNamesIgnoreCategories()
    {
        List<String> list = getDimensionNamesAsList();
        
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
     * Returns the index of the data element dimension in the dimension map.
     */
    public int getDataElementDimensionIndex()
    {
        return getDimensionNamesAsList().indexOf( DATAELEMENT_DIM_ID );
    }

    /**
     * Returns the index of the category option combo dimension in the dimension map.
     */
    public int getCategoryOptionComboDimensionIndex()
    {
        return getDimensionNamesAsList().indexOf( CATEGORYOPTIONCOMBO_DIM_ID );
    }
    
    /**
     * Returns the index of the period dimension in the dimension map.
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
    public Map<String, List<IdentifiableObject>> getDimensionMap()
    {
        Map<String, List<IdentifiableObject>> map = new HashMap<String, List<IdentifiableObject>>();

        map.putAll( dimensions );
        map.remove( INDICATOR_DIM_ID );
        
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
    public ListMap<IdentifiableObject, IdentifiableObject> getDataPeriodAggregationPeriodMap()
    {
        ListMap<IdentifiableObject, IdentifiableObject> map = new ListMap<IdentifiableObject, IdentifiableObject>();

        if ( dataPeriodType != null )
        {
            for ( IdentifiableObject aggregatePeriod : this.getPeriods() )
            {
                Period dataPeriod = dataPeriodType.createPeriod( ((Period) aggregatePeriod).getStartDate() );
                
                map.putValue( dataPeriod, aggregatePeriod );
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
    public void replaceAggregationPeriodsWithDataPeriods( ListMap<IdentifiableObject, IdentifiableObject> dataPeriodAggregationPeriodMap )
    {
        if ( isAggregationType( AVERAGE_DISAGGREGATION ) &&  dataPeriodType != null )
        {
            this.periodType = this.dataPeriodType.getName();
            
            setPeriods( new ArrayList<IdentifiableObject>( getDataPeriodAggregationPeriodMap().keySet() ) );
        }
    }
    
    /**
     * Returns a mapping between the uid and name for all options in all dimensions.
     */
    public Map<String, String> getUidNameMap()
    {
        Map<String, String> map = new HashMap<String, String>();
        
        for ( String dimension : dimensions.keySet() )
        {
            for ( IdentifiableObject idObject : dimensions.get( dimension ) )
            {
                map.put( idObject.getUid(), idObject.getDisplayName() );
            }
        }
        
        for ( String filter : filters.keySet() )
        {
            for ( IdentifiableObject idObject : filters.get( filter ) )
            {
                map.put( idObject.getUid(), idObject.getDisplayName() );
            }
        }
        
        return map;
    }
    
    /**
     * Generates all permutations of the dimension options for this query.
     */
    public List<List<DimensionOption>> getDimensionOptionPermutations()
    {
        List<DimensionOption[]> dimensionOptions = new ArrayList<DimensionOption[]>();
        
        List<String> dimensionNames = getDimensionNamesAsList();

        List<String> ignoreDims = Arrays.asList( DATAELEMENT_DIM_ID, CATEGORYOPTIONCOMBO_DIM_ID, INDICATOR_DIM_ID );
        
        for ( String dim : dimensionNames )
        {
            if ( !ignoreDims.contains( dim ) )
            {
                List<DimensionOption> options = new ArrayList<DimensionOption>();
                
                for ( IdentifiableObject option : dimensions.get( dim ) )
                {
                    options.add( new DimensionOption( dim, option.getUid() ) );
                }
                
                dimensionOptions.add( options.toArray( DIM_OPT_ARR ) );
            }
        }
        
        CombinationGenerator<DimensionOption> generator = new CombinationGenerator<DimensionOption>( dimensionOptions.toArray( DIM_OPT_2D_ARR ) );
        
        return generator.getCombinations();
    }
    
    // -------------------------------------------------------------------------
    // Static methods
    // -------------------------------------------------------------------------

    public static String getDimensionFromParam( String param )
    {
        return param != null && param.split( DIMENSION_NAME_SEP ).length > 0 ? param.split( DIMENSION_NAME_SEP )[0] : null;
    }
    
    public static List<String> getDimensionOptionsFromParam( String param )
    {
        if ( param != null && param.split( DIMENSION_NAME_SEP ).length > 0 )
        {
            return Arrays.asList( param.split( DIMENSION_NAME_SEP )[1].split( OPTION_SEP ) );
        }
        
        return null;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    /**
     * Returns the dimension names as a list. The indicator key is included as
     * indicator is not a true dimension, rather a formula based on the data
     * element dimension.
     */
    private List<String> getDimensionNamesAsList()
    {
        List<String> list = new ArrayList<String>( dimensions.keySet() );
        list.remove( INDICATOR_DIM_ID );
        return list;
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
    public Map<String, List<IdentifiableObject>> getDimensions()
    {
        return dimensions;
    }

    public void setDimensions( Map<String, List<IdentifiableObject>> dimensions )
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
    public Map<String, List<IdentifiableObject>> getFilters()
    {
        return filters;
    }

    public void setFilters( Map<String, List<IdentifiableObject>> filters )
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
  
    public List<IdentifiableObject> getDimensionOrFilter( String key )
    {
        return dimensions.containsKey( key ) ? dimensions.get( key ) : filters.get( key );
    }
    
    public void resetDimensionOrFilter( String key, List<IdentifiableObject> values )
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
  
    public List<IdentifiableObject> getIndicators()
    {
        return dimensions.get( INDICATOR_DIM_ID );
    }
    
    public void setIndicators( List<IdentifiableObject> indicators )
    {
        dimensions.put( INDICATOR_DIM_ID, indicators );
    }
    
    public List<IdentifiableObject> getDataElements()
    {
        return dimensions.get( DATAELEMENT_DIM_ID );
    }
    
    public void setDataElements( List<IdentifiableObject> dataElements )
    {
        dimensions.put( DATAELEMENT_DIM_ID, dataElements );
    }
    
    public List<IdentifiableObject> getPeriods()
    {
        return dimensions.get( PERIOD_DIM_ID );
    }
    
    public void setPeriods( List<IdentifiableObject> periods )
    {
        dimensions.put( PERIOD_DIM_ID, periods );
    }

    public List<IdentifiableObject> getOrganisationUnits()
    {
        return dimensions.get( ORGUNIT_DIM_ID );
    }
    
    public void setOrganisationUnits( List<IdentifiableObject> organisationUnits )
    {
        dimensions.put( ORGUNIT_DIM_ID, organisationUnits );
    }
    
    // -------------------------------------------------------------------------
    // Get and set helpers for filters
    // -------------------------------------------------------------------------

    public List<IdentifiableObject> getFilterDatElements()
    {
        return filters.get( DATAELEMENT_DIM_ID );
    }
    
    public void setFilterDataElements( List<IdentifiableObject> dataElements )
    {
        filters.put( DATAELEMENT_DIM_ID, dataElements );
    }
    
    public List<IdentifiableObject> getFilterPeriods()
    {
        return filters.get( PERIOD_DIM_ID );
    }
    
    public void setFilterPeriods( List<IdentifiableObject> periods )
    {
        filters.put( PERIOD_DIM_ID, periods );
    }
    
    public List<IdentifiableObject> getFilterOrganisationUnits()
    {
        return filters.get( ORGUNIT_DIM_ID );
    }
    
    public void setFilterOrganisationUnits( List<IdentifiableObject> organisationUnits )
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
