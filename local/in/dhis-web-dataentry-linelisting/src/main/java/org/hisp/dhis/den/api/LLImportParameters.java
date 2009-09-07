package org.hisp.dhis.den.api;

import java.io.Serializable;

public class LLImportParameters implements Serializable
{
    private Integer sheetNo;
    
    private Integer rowNo;
    
    private Integer colNo;
    
    private String expression;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    public LLImportParameters()
    {
        
    }

    public LLImportParameters(Integer sheetNo, Integer rowNo, Integer colNo, String expression )
    {
        this.sheetNo = sheetNo;
        this.rowNo = rowNo;
        this.colNo = colNo;
        this.expression = expression;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public Integer getSheetNo()
    {
        return sheetNo;
    }

    public void setSheetNo( Integer sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    public Integer getRowNo()
    {
        return rowNo;
    }

    public void setRowNo( Integer rowNo )
    {
        this.rowNo = rowNo;
    }

    public Integer getColNo()
    {
        return colNo;
    }

    public void setColNo( Integer colNo )
    {
        this.colNo = colNo;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }
   
}
