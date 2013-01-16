package org.hisp.dhis.analytics;

import java.util.List;

public class DimensionOption
{
    private String dimension;
    
    private String option;
    
    public DimensionOption( String dimension, String option )
    {
        this.dimension = dimension;
        this.option = option;
    }

    public String getDimension()
    {
        return dimension;
    }

    public void setDimension( String dimension )
    {
        this.dimension = dimension;
    }

    public String getOption()
    {
        return option;
    }

    public void setOption( String option )
    {
        this.option = option;
    }
    
    public static String asOptionKey( List<DimensionOption> options )
    {
        StringBuilder builder = new StringBuilder();
        
        if ( options != null && !options.isEmpty() )
        {
            for ( DimensionOption option : options )
            {
                builder.append( option.getOption() ).append( DataQueryParams.DIMENSION_SEP );
            }
            
            builder.deleteCharAt( builder.length() - 1 );
        }
        
        return builder.toString();
    }
    
    public static String getPeriodOption( List<DimensionOption> options )
    {
        if ( options != null && !options.isEmpty() )
        {
            for ( DimensionOption option : options )
            {
                if ( DataQueryParams.PERIOD_DIM_ID.equals( option.getDimension() ) )
                {
                    return option.getDimension();
                }
            }
        }
        
        return null;
    }
    
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( dimension == null ) ? 0 : dimension.hashCode() );
        result = prime * result + ( ( option == null ) ? 0 : option.hashCode() );
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
        
        DimensionOption other = (DimensionOption) object;
        
        if ( dimension == null )
        {
            if ( other.dimension != null )
            {
                return false;
            }
        }
        else if ( !dimension.equals( other.dimension ) )
        {
            return false;
        }
        
        if ( option == null )
        {
            if ( other.option != null )
            {
                return false;
            }
        }
        else if ( !option.equals( other.option ) )
        {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString()
    {
        return "[" + dimension + ", " + option + "]";
    }
}
