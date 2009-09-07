package org.hisp.dhis.reports;

import java.io.Serializable;

public class Report_inDesign implements Serializable
{    
    /**
     * Service Type (like dataelement, indicator etc.)
     */
    private String stype;
    
    /**
     * Perid Type (like Cumulative, Previous year etc.)
     */
    private String ptype;
    
    /**
     * Sheet number
     */
    private int sheetno;
    
    /**
     * Row number
     */
    private int rowno;
    
    /**
     * Column number
     */
    private int colno;
    
    /**
     * Number of Merged cells by rows
     */
    private int rowmerge;
    
    /**
     * Number of Merged cells by columns
     */
    private int colmerge;
    
    /**
     * Formula to calculate the values.
     */
    private String expression;
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public Report_inDesign()
    {
        
    }
    
    public Report_inDesign(String stype, String ptype, int sheetno, int rowno, int colno, int rowmerge, int colmerge, String expression)
    {
        this.stype = stype;
        this.ptype = ptype;
        this.sheetno = sheetno;
        this.rowno = rowno;
        this.colno = colno;
        this.rowmerge = rowmerge;
        this.colmerge = colmerge;
        this.expression = expression;        
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public String getStype()
    {
        return stype;
    }

    public void setStype( String stype )
    {
        this.stype = stype;
    }

    public String getPtype()
    {
        return ptype;
    }

    public void setPtype( String ptype )
    {
        this.ptype = ptype;
    }

    public int getSheetno()
    {
        return sheetno;
    }

    public void setSheetno( int sheetno )
    {
        this.sheetno = sheetno;
    }

    public int getRowno()
    {
        return rowno;
    }

    public void setRowno( int rowno )
    {
        this.rowno = rowno;
    }

    public int getColno()
    {
        return colno;
    }

    public void setColno( int colno )
    {
        this.colno = colno;
    }

    public int getRowmerge()
    {
        return rowmerge;
    }

    public void setRowmerge( int rowmerge )
    {
        this.rowmerge = rowmerge;
    }

    public int getColmerge()
    {
        return colmerge;
    }

    public void setColmerge( int colmerge )
    {
        this.colmerge = colmerge;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }
    
    // -------------------------------------------------------------------------
    // Report Design Constants
    // -------------------------------------------------------------------------
    
    // Constants for stype
    public final String ST_DATAELEMENT = "dataelement";
    public final String ST_DATAELEMENT_NO_REPEAT = "dataelementnorepeat";
    public final String ST_LLDATAELEMENT = "lldataelement";
    public final String ST_INDICATOR = "indicator";
    
    // Constants for ptype
    public final String PT_CMCY = "CMCY";
    public final String PT_CMPY = "CMPY";
    public final String PT_CCMCY = "CCMCY";
    public final String PT_CCMPY = "CCMPY";
    public final String PT_PMCY = "PMCY";
    
    // Constants for LineListDataElementMapping
    public final String E_FACILITY = "FACILITY";
    public final String E_PERIOD_MONTH = "PERIOD-MONTH";
    public final String E_PERIOD_YEAR = "PERIOD-YEAR";
    
}
