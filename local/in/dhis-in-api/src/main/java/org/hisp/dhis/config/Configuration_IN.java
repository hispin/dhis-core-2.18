package org.hisp.dhis.config;

public class Configuration_IN
{

    public static final String KEY_MYSQLPATH = "mysqlpath";
    public static final String KEY_BACKUPDATAPATH = "backupdatapath";
    public static final String KEY_REPORTFOLDER = "reportfolder";
    
    public static final String DEFAULT_MYSQLPATH = "C:/DHIS2/mysql/bin/";
    public static final String DEFAULT_BACKUPDATAPATH = "C:/dhisdata/";
    public static final String DEFAULT_REPORTFOLDER = "ra_national"; 

    private int id;
    
    private String key;
    
    private String value;
    
    //-------------------------------------------------------------------------
    // Constructor
    //-------------------------------------------------------------------------
    
    public Configuration_IN()
    {
        
    }
    
    public Configuration_IN( String key, String value )
    {
        this.key = key;
        this.value = value;
    }
    
    //---------------------------------------------------------------
    // Getters and Setters
    //---------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey( String key )
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
    
}
