package org.hisp.dhis.sandbox.excelimport.api;

public class ExcelMetaParameters
{
    private Integer sourceid;
    
    private Integer periodid;
    
    private String excelFileName;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ExcelMetaParameters()
    {
        
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Integer getSourceid()
    {
        return sourceid;
    }

    public void setSourceid( Integer sourceid )
    {
        this.sourceid = sourceid;
    }

    public Integer getPeriodid()
    {
        return periodid;
    }

    public void setPeriodid( Integer periodid )
    {
        this.periodid = periodid;
    }

    public String getExcelFileName()
    {
        return excelFileName;
    }

    public void setExcelFileName( String excelFileName )
    {
        this.excelFileName = excelFileName;
    }

    
}
