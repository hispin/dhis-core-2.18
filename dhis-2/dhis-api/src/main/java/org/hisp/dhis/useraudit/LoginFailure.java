package org.hisp.dhis.useraudit;

import java.util.Date;

public class LoginFailure
{
    private int id;
    
    private String username;
    
    private Date date;
    
    public LoginFailure()
    {
    }
    
    public LoginFailure( String username, Date date )
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
