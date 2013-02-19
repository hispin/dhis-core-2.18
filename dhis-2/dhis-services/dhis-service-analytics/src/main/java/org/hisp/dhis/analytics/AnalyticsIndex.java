package org.hisp.dhis.analytics;

public class AnalyticsIndex
{
    private String table;
    
    private String column;

    public AnalyticsIndex( String table, String column )
    {
        this.table = table;
        this.column = column;
    }
    
    public String getTable()
    {
        return table;
    }

    public void setTable( String table )
    {
        this.table = table;
    }

    public String getColumn()
    {
        return column;
    }

    public void setColumn( String column )
    {
        this.column = column;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + column.hashCode();
        result = prime * result + table.hashCode();
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
        
        AnalyticsIndex other = (AnalyticsIndex) object;
        
        return column.equals( other.column ) && table.equals( other.table );
    }
}
