package org.hisp.dhis.analytics;

import java.util.ArrayList;
import java.util.List;

public class Partitions
{
    private List<String> partitions = new ArrayList<String>();
    
    public Partitions()
    {
    }
    
    public Partitions( List<String> partitions )
    {
        this.partitions = partitions;
    }
    
    public Partitions add( String partition )
    {
        partitions.add( partition );
        return this;
    }
        
    public boolean isMultiple()
    {
        return partitions != null && partitions.size() > 1;
    }
    
    public String getSinglePartition()
    {
        return partitions.get( 0 );
    }

    public List<String> getPartitions()
    {
        return partitions;
    }

    public void setPartitions( List<String> partitions )
    {
        this.partitions = partitions;
    }

    @Override
    public String toString()
    {
        return partitions.toString();
    }
    
    @Override
    public int hashCode()
    {
        return partitions.hashCode();
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
        
        Partitions other = (Partitions) object;
        
        return partitions.equals( other.partitions );
    }
}
