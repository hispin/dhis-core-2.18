package org.hisp.dhis.dimension;

import java.util.ArrayList;
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
        StringBuffer identifier = new StringBuffer( TYPE_GROUP_SET + SEPARATOR_TYPE );
        
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

    public String getDimensionSetType()
    {
        return getDimensionSetId().split( SEPARATOR_TYPE )[0];
    }
    
    public boolean isDimensionSetType( String type )
    {
        return getDimensionSetType() != null ? getDimensionSetType().equals( type ) : false;
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
    // toString
    // -------------------------------------------------------------------------
    
    public String toString()
    {
        return "[" + getName() + "]";
    }
}
