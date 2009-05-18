package org.hisp.dhis.design;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.chart.Chart;
import org.hisp.dhis.reporttable.ReportTable;

public class Design
{
    private int id;
    
    private String name;
    
    private List<Chart> charts = new ArrayList<Chart>();
    
    private List<ReportTable> reportTables = new ArrayList<ReportTable>();
    
    public Design()
    {   
    }
    
    public Design( String name )
    {
        this.name = name;
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

    public List<Chart> getCharts()
    {
        return charts;
    }

    public void setCharts( List<Chart> charts )
    {
        this.charts = charts;
    }

    public List<ReportTable> getReportTables()
    {
        return reportTables;
    }

    public void setReportTables( List<ReportTable> reportTables )
    {
        this.reportTables = reportTables;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
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
        
        final Design other = (Design) object;
        
        return name.equals( other.name );
    }
}
