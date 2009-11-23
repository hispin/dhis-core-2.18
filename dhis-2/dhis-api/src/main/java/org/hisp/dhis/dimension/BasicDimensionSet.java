package org.hisp.dhis.dimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
        StringBuffer identifier = new StringBuffer( DimensionType.GROUPSET.name() + SEPARATOR_TYPE );
        
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
