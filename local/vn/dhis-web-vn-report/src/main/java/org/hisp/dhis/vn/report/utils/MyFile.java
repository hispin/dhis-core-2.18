package org.hisp.dhis.vn.report.utils;

public class MyFile
{
    private String name;

    private String createDate;

    private double size;

    public MyFile( String name, String createDate, double size )
    {
        super();
        this.name = name;
        this.createDate = createDate;
        this.size = size;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate( String createDate )
    {
        this.createDate = createDate;
    }

    public double getSize()
    {
        return size;
    }

    public void setSize( double size )
    {
        this.size = size;
    }

}
