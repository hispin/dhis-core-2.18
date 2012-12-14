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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hisp.dhis.common.Dxf2Namespace;

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
        
    private List<String> indicators = new ArrayList<String>();
    
    private List<String> dataElements = new ArrayList<String>();
    
    private List<String> periods = new ArrayList<String>();
    
    private List<String> organisationUnits = new ArrayList<String>();
    
    private Map<String, List<String>> dimensions = new HashMap<String, List<String>>();
    
    private boolean categories = false;
    
    private transient String tableName;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
  
    public DataQueryParams()
    {
    }
    
    public DataQueryParams( List<String> indicators, List<String> dataElements, List<String> periods,
        List<String> organisationUnits, Map<String, List<String>> dimensions, boolean categories )
    {
        this.indicators = indicators;
        this.dataElements = dataElements;
        this.periods = periods;
        this.organisationUnits = organisationUnits;
        this.dimensions = dimensions;
        this.categories = categories;
    }
    
    public DataQueryParams( DataQueryParams params )
    {
        this.indicators = params.getIndicators();
        this.dataElements = params.getDataElements();
        this.periods = params.getPeriods();
        this.organisationUnits = params.getOrganisationUnits();
        this.dimensions = params.getDimensions();
        this.categories = params.isCategories();
    }

    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------

    public SortedMap<String, List<String>> getDimensionValuesMap()
    {
        SortedMap<String, List<String>> map = new TreeMap<String, List<String>>();
        
        map.put( DATAELEMENT_DIM_ID, dataElements );
        map.put( ORGUNIT_DIM_ID, organisationUnits );
        map.put( PERIOD_DIM_ID, periods );
        
        if ( dimensions != null )
        {
            for ( String dimension : dimensions.keySet() )
            {
                map.put( dimension, dimensions.get( dimension ) );
            }
        }
        
        return map;
    }
    
    public List<String> getDimensionNames()
    {
        List<String> list = new ArrayList<String>();
        
        list.add( DATAELEMENT_DIM_ID );
        list.add( CATEGORYOPTIONCOMBO_DIM_ID );
        list.add( PERIOD_DIM_ID );
        list.add( ORGUNIT_DIM_ID );
        list.addAll( dimensions.keySet() );
        
        return list;
    }
    
    public List<String> getDynamicDimensionNames()
    {
        return new ArrayList<String>( dimensions.keySet() );
    }
        
    public void setDimension( String dimension, List<String> values )
    {
        if ( DATAELEMENT_DIM_ID.equals( dimension ) )
        {
            setDataElements( values );
        }
        else if ( PERIOD_DIM_ID.equals( dimension ) )
        {
            setPeriods( values );
        }
        else if ( ORGUNIT_DIM_ID.equals( dimension ) )
        {
            setOrganisationUnits( values );
        }
        else if ( dimensions.containsKey( dimension ) )
        {
            dimensions.put( dimension, values );
        }
    }
    
    public List<String> getDimension( String dimension )
    {
        if ( DATAELEMENT_DIM_ID.equals( dimension ) )
        {
            return dataElements;
        }
        else if ( PERIOD_DIM_ID.equals( dimension ) )
        {
            return periods;
        }
        else if ( ORGUNIT_DIM_ID.equals( dimension ) )
        {
            return organisationUnits;
        }
        else if ( dimensions != null && dimensions.containsKey( dimension ) )
        {
            return dimensions.get( dimension );
        }
        
        throw new IllegalArgumentException( dimension );
    }
    
    public String getLargestDimension()
    {
        Map<String, List<String>> map = getDimensionValuesMap();
        
        String dimension = map.keySet().iterator().next();
        int size = map.get( dimension ).size();
                
        for ( String dim : map.keySet() )
        {
            if ( map.get( dim ).size() > size )
            {
                dimension = dim;
                size = map.get( dim ).size();
            }
        }
        
        return dimension;
    }
    
    @Override
    public String toString()
    {
        return "[in: " + indicators + ", de: " + dataElements + ", pe: " + periods
            + ", ou: " + organisationUnits + "]";
    }
        
    // -------------------------------------------------------------------------
    // Get and set methods
    // -------------------------------------------------------------------------
  
    @JsonProperty( value = INDICATOR_DIM_ID )
    public List<String> getIndicators()
    {
        return indicators;
    }

    public void setIndicators( List<String> indicators )
    {
        this.indicators = indicators;
    }

    @JsonProperty( value = DATAELEMENT_DIM_ID )
    public List<String> getDataElements()
    {
        return dataElements;
    }

    public void setDataElements( List<String> dataElements )
    {
        this.dataElements = dataElements;
    }

    @JsonProperty( value = PERIOD_DIM_ID )
    public List<String> getPeriods()
    {
        return periods;
    }

    public void setPeriods( List<String> periods )
    {
        this.periods = periods;
    }

    @JsonProperty( value = ORGUNIT_DIM_ID )
    public List<String> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public void setOrganisationUnits( List<String> organisationUnits )
    {
        this.organisationUnits = organisationUnits;
    }

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

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName( String tableName )
    {
        this.tableName = tableName;
    }
}
