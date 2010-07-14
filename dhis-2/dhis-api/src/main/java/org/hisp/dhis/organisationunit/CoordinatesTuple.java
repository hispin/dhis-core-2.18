package org.hisp.dhis.organisationunit;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesTuple
{
    private List<String> coordinatesTuple = new ArrayList<String>();

    public void addCoordinates( String coordinates )
    {
        this.coordinatesTuple.add( coordinates );
    }
    
    public long getNumberOfCoordinates()
    {
        return this.coordinatesTuple.size();
    }
    
    public List<String> getCoordinatesTuple()
    {
        return coordinatesTuple;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        
        for ( String c : coordinatesTuple )
        {
            result = prime * result + c.hashCode();
        }
        
        return result;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        
        if ( o == null )
        {
            return false;
        }
        
        if ( getClass() != o.getClass() )
        {
            return false;
        }
        
        final CoordinatesTuple other = (CoordinatesTuple) o;

        if ( coordinatesTuple.size() != other.getCoordinatesTuple().size() )
        {
            return false;
        }
        
        for ( int i = 0; i < coordinatesTuple.size(); i++ )
        {
            if ( !coordinatesTuple.get( i ).equals( other.getCoordinatesTuple().get( i ) ) )
            {
                return false;
            }
        }
        
        return true;
    }    
}
