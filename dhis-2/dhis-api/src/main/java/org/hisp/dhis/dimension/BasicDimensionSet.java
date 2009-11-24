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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class BasicDimensionSet
    implements DimensionSet
{
    private List<? extends Dimension> dimensions;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    protected BasicDimensionSet()
    {   
    }
    
    public BasicDimensionSet( List<? extends Dimension> dimensions )
    {
        this.dimensions = dimensions;
    }
    
    public BasicDimensionSet( Dimension... dimensions )
    {
        this.dimensions = Arrays.asList( dimensions );
    }
    
    // -------------------------------------------------------------------------
    // DimensionSet implementation
    // -------------------------------------------------------------------------

    public String getName()
    {
        StringBuffer name = new StringBuffer();
        
        Iterator<? extends Dimension> iterator = getDimensions().iterator(); 
        
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

    public String getDimensionSetId()
    {
        StringBuffer identifier = new StringBuffer( DimensionType.DATAELEMENTGROUPSET.name() + SEPARATOR_TYPE );
        
        Iterator<? extends Dimension> iterator = getDimensions().iterator(); 
        
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

    public DimensionType getDimensionType()
    {
        return DimensionType.valueOf( getDimensionSetId().split( SEPARATOR_TYPE )[0] );
    }
    
    public List<? extends Dimension> getDimensions()
    {
        return dimensions;
    }

    public List<? extends DimensionOptionElement> getDimensionOptionElements()
    {
        List<DimensionOptionElement> dimensionOptionElements = new ArrayList<DimensionOptionElement>();

        for ( Dimension dimension : getDimensions() )
        {
            for ( DimensionOption dimensionOption : dimension.getDimensionOptions() )
            {
                dimensionOptionElements.addAll( dimensionOption.getDimensionOptionElements() );
            }
        }

        return dimensionOptionElements;
    }
    
    // -------------------------------------------------------------------------
    // equals, hashCode, toString
    // -------------------------------------------------------------------------
        
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
        
        BasicDimensionSet other = (BasicDimensionSet) object;
        
        if ( dimensions == null )
        {
            if ( other.dimensions != null )
            {
                return false;
            }
        }
        else if ( !( new HashSet<Dimension>( dimensions ).equals( new HashSet<Dimension>( other.dimensions ) ) ) )
        {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode()
    {
        return dimensions == null ? 0 : dimensions.hashCode();
    }

    public String toString()
    {
        return "[" + getName() + "]";
    }
}
