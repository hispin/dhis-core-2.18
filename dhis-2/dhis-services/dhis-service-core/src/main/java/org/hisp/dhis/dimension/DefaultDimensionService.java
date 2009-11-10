package org.hisp.dhis.dimension;

/*
 * Copyright (c) 2004-2007, University of Oslo
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.system.util.ConversionUtils;

/**
 * The DimensionSet identifier is on the format 
 * [TYPE]SEPARATOR_TYPE[id]SEPARATOR_ID[id]SEPARATOR[id], for instance
 * groupSet_1-2-3 and categoryCombo_1 .
 * 
 * @author Lars Helge Overland
 * @version $Id: Indicator.java 5540 2008-08-19 10:47:07Z larshelg $
 */
public class DefaultDimensionService
    implements DimensionService
{
    final String TYPE_CATEGORY_COMBO = "categoryCombo";
    final String TYPE_GROUP_SET = "groupSet";
    final String SEPARATOR_TYPE = "_";
    final String SEPARATOR_ID = "-";
    final String SEPARATOR_NAME = ",";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;
    
    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // DimensionService implementation
    // -------------------------------------------------------------------------
    
    public Map<String, String> getDataElementDimensionSets()
    {
        Map<String, String> dimensions = new HashMap<String, String>();
        
        for ( DataElementCategoryCombo categoryCombo : categoryService.getAllDataElementCategoryCombos() )
        {            
            dimensions.put( getCategoryComboIdentifier( categoryCombo ), categoryCombo.getName() );
        }
        
        for ( Set<DataElementGroupSet> dimensionSet : getDistinctDataElemenDimensionSets() )
        {
            dimensions.put( getGroupSetIdentifier( dimensionSet ), getGroupSetName( dimensionSet ) );
        }
        
        return dimensions;
    }
    
    public Collection<DataElement> getDataElements( String identifier )
    {        
        if ( identifier != null )
        {
            if ( getDimensionSetType( identifier ).equals( TYPE_CATEGORY_COMBO ) )
            {
                DataElementCategoryCombo categoryCombo = categoryService.getDataElementCategoryCombo( getDimensionSetIdentifiers( identifier )[0] );

                return dataElementService.getDataElementByCategoryCombo( categoryCombo );
            }
            else // TYPE_GROUP_SET
            {
                Set<DataElementGroupSet> dimensionSet = getDataElementDimensionSet( getDimensionSetIdentifiers( identifier ) );
                
                // TODO return dataElementService.getDataElementsByDimensionSet( dimensionSet );
            }
        }
        
        return null;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
    private String getDimensionSetType( String identifier )
    {
        return identifier.split( SEPARATOR_TYPE )[0];
    }
    
    private Integer[] getDimensionSetIdentifiers( String identifier )
    {
        return ConversionUtils.getIntegerArray( identifier.split( SEPARATOR_TYPE )[1].split( SEPARATOR_ID ) );
    }
    
    private Set<DataElementGroupSet> getDataElementDimensionSet( Integer[] identifiers )
    {
        Set<DataElementGroupSet> dimensionSet = new HashSet<DataElementGroupSet>();
        
        for ( Integer id : identifiers )
        {
            dimensionSet.add( dataElementService.getDataElementGroupSet( id ) );
        }
        
        return dimensionSet;
    }
    
    private Set<Set<DataElementGroupSet>> getDistinctDataElemenDimensionSets()
    {
        Set<Set<DataElementGroupSet>> dimensionSets = new HashSet<Set<DataElementGroupSet>>();
        
        for ( DataElement dataElement : dataElementService.getDataElementsWithGroupSets() )
        {
            dimensionSets.add( new HashSet<DataElementGroupSet>( dataElement.getGroupSets() ) );
        }
        
        return dimensionSets;
    }
    
    private String getCategoryComboIdentifier( DataElementCategoryCombo categoryCombo )
    {
        return TYPE_CATEGORY_COMBO + SEPARATOR_TYPE + categoryCombo.getId();
    }
    
    private String getGroupSetIdentifier( Set<DataElementGroupSet> groupSets )
    {
        StringBuffer identifier = new StringBuffer( TYPE_GROUP_SET + SEPARATOR_TYPE );
        
        Iterator<DataElementGroupSet> iterator = groupSets.iterator(); 
        
        while ( iterator.hasNext() )
        {
            identifier.append( iterator.next().getId() );
            
            if ( iterator.hasNext() )
            {
                identifier.append( SEPARATOR_ID );
            }
        }
        
        return identifier.toString();
    }
    
    private String getGroupSetName( Set<DataElementGroupSet> groupSets )
    {
        StringBuffer name = new StringBuffer();
        
        Iterator<DataElementGroupSet> iterator = groupSets.iterator(); 
        
        while ( iterator.hasNext() )
        {
            name.append( iterator.next().getName() );
            
            if ( iterator.hasNext() )
            {
                name.append( SEPARATOR_NAME );
            }
        }
        
        return name.toString();
    }
}
