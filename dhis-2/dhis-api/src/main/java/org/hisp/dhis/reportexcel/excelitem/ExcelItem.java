package org.hisp.dhis.reportexcel.excelitem;

import java.io.Serializable;

public class ExcelItem implements Serializable
{

    private int id;

    private String name;

    private String expression;

    private int row;

    private int column;

    private int sheetNo;

    private ExcelItemGroup excelItemGroup;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ExcelItem()
    {

    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getRow()
    {
        return row;
    }

    public String getExpression()
    {
        return expression;
    }

    public void setExpression( String expression )
    {
        this.expression = expression;
    }

    public void setRow( int row )
    {
        this.row = row;
    }

    public int getColumn()
    {
        return column;
    }

    public void setColumn( int column )
    {
        this.column = column;
    }

    public ExcelItemGroup getExcelItemGroup()
    {
        return excelItemGroup;
    }

    public void setExcelItemGroup( ExcelItemGroup excelItemGroup )
    {
        this.excelItemGroup = excelItemGroup;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getSheetNo()
    {
        return sheetNo;
    }

    public void setSheetNo( int sheetNo )
    {
        this.sheetNo = sheetNo;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ExcelItem other = (ExcelItem) obj;
        if ( id != other.id )
            return false;
        return true;
    }

}
