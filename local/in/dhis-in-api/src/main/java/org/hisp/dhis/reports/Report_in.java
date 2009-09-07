package org.hisp.dhis.reports;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.source.Source;

public class Report_in
    implements Serializable
{

    /**
     * The unique identifier for this Report_in
     */
    private int id;

    /**
     * Name of Report_in. Required and unique.
     */
    private String name;

    /**
     * Model of the Report_in (like Static, dynamic etc.). Required.
     */
    private String model;

    /**
     * The PeriodType indicating the frequency that this Report_in should be
     * used
     */
    private PeriodType periodType;

    private String excelTemplateName;

    private String xmlTemplateName;

    private String reportType;

    /**
     * All Sources that are generating this Report_in.
     */
    private Set<Source> sources = new HashSet<Source>();

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------

    public Report_in()
    {

    }

    public Report_in( String name, String model, PeriodType periodType, String excelTemplateName,
        String xmlTemplateName, String reportType )
    {
        this.name = name;
        this.model = model;
        this.periodType = periodType;
        this.excelTemplateName = excelTemplateName;
        this.xmlTemplateName = xmlTemplateName;
        this.reportType = reportType;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return name.hashCode();
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

        if ( !(o instanceof Report_in) )
        {
            return false;
        }

        final Report_in other = (Report_in) o;

        return name.equals( other.getName() );
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

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

    public String getModel()
    {
        return model;
    }

    public void setModel( String model )
    {
        this.model = model;
    }

    public PeriodType getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType( PeriodType periodType )
    {
        this.periodType = periodType;
    }

    public String getExcelTemplateName()
    {
        return excelTemplateName;
    }

    public void setExcelTemplateName( String excelTemplateName )
    {
        this.excelTemplateName = excelTemplateName;
    }

    public String getXmlTemplateName()
    {
        return xmlTemplateName;
    }

    public void setXmlTemplateName( String xmlTemplateName )
    {
        this.xmlTemplateName = xmlTemplateName;
    }

    public String getReportType()
    {
        return reportType;
    }

    public void setReportType( String reportType )
    {
        this.reportType = reportType;
    }

    public Set<Source> getSources()
    {
        return sources;
    }

    public void setSources( Set<Source> sources )
    {
        this.sources = sources;
    }

}
