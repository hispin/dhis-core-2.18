package org.hisp.dhis.reporttable;

public class ReportTableColumn
{
    private int id;
    
    private String name;
    
    private String header;
    
    private boolean hidden;
    
    public ReportTableColumn()
    {   
    }
    
    public ReportTableColumn( String name, String header, boolean hidden )
    {
        this.name = name;
        this.header = header;
        this.hidden = hidden;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getHeader()
    {
        return header;
    }

    public void setHeader( String header )
    {
        this.header = header;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden( boolean hidden )
    {
        this.hidden = hidden;
    }
}
