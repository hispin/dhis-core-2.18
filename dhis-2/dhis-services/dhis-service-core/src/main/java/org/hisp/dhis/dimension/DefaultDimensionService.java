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

import static org.hisp.dhis.dimension.DimensionType.CATEGORY;
import static org.hisp.dhis.dimension.DimensionType.DATAELEMENTGROUPSET;
import static org.hisp.dhis.dimension.DimensionType.INDICATORGROUPSET;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.system.util.ConversionUtils;

/**
 * @author Lars Helge Overland
 * @version $Id: Indicator.java 5540 2008-08-19 10:47:07Z larshelg $
 */
public class DefaultDimensionService
    implements DimensionService
{
    private static final String SEPARATOR_TYPE = "_";
    private static final String SEPARATOR_ID = "-";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // DimensionService implementation
    // -------------------------------------------------------------------------
    
    public DimensionSet getDimensionSet( String dimensionSetId )
    {
        if ( dimensionSetId != null )
        {
            for ( DimensionSet dimensionSet : getDataElementDimensionSets() )
            {
                if ( dimensionSet.getDimensionSetId().equals( dimensionSetId ) )
                {
                    return dimensionSet;
                }
            }
        }
        
        return null;
    }
    
    public Collection<DimensionSet> getDataElementDimensionSets()
    {
        List<DimensionSet> dimensionSets = new ArrayList<DimensionSet>();
        
        dimensionSets.addAll( categoryService.getAllDataElementCategoryCombos() );
        
        for ( Set<DataElementGroupSet> dimensionSet : getDistinctDataElementDimensionSets() )
        {
            dimensionSets.add( new BasicDimensionSet( new ArrayList<Dimension>( dimensionSet ) ) );
        }
        
        return dimensionSets;
    }
    
    public Collection<DataElement> getDataElements( DimensionSet dimensionSet )
    {
        Collection<DataElement> dataElements = null;

        if ( dimensionSet != null && dimensionSet.getDimensionType().equals( CATEGORY ) )
        {
            Integer id = getDimensionSetIdentifiers( dimensionSet.getDimensionSetId() )[0];

            DataElementCategoryCombo categoryCombo = categoryService.getDataElementCategoryCombo( id );

            dataElements = dataElementService.getDataElementByCategoryCombo( categoryCombo );
        }
        else if ( dimensionSet != null && dimensionSet.getDimensionType().equals( DATAELEMENTGROUPSET ) )
        {
            Integer[] ids = getDimensionSetIdentifiers( dimensionSet.getDimensionSetId() );
    
            Set<DataElementGroupSet> groupSets = getDataElementDimensionSet( ids );
    
            dataElements = dataElementService.getDataElementsByGroupSets( groupSets );
        }
        else if ( dimensionSet != null && dimensionSet.getDimensionType().equals( INDICATORGROUPSET ) )
        {            
            throw new UnsupportedOperationException(); // TODO implement
        }
        
        return dataElements;
    }
        
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------
    
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
    
    private Set<Set<DataElementGroupSet>> getDistinctDataElementDimensionSets()
    {
        Set<Set<DataElementGroupSet>> dimensionSets = new HashSet<Set<DataElementGroupSet>>();
        
        for ( DataElement dataElement : dataElementService.getDataElementsWithGroupSets() )
        {
            dimensionSets.add( new HashSet<DataElementGroupSet>( dataElement.getGroupSets() ) );
        }
        
        return dimensionSets;
    }
}
