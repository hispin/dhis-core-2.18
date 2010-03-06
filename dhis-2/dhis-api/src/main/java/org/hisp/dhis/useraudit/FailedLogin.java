package org.hisp.dhis.useraudit;

import java.util.Date;

public class FailedLogin
{
    private int id;
    
    private String username;
    
    private Date date;
    
    public FailedLogin()
    {
    }
    
    public FailedLogin( String username, Date date )
    {
        this.username = username;
        this.date = date;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate( Date date )
    {
        this.date = date;
    }
}
