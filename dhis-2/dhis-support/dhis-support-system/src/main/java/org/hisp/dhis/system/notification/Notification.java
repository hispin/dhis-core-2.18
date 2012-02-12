package org.hisp.dhis.system.notification;

import java.util.Date;

import org.hisp.dhis.common.CodeGenerator;

public class Notification
{
    private String uid;
    
    private NotificationLevel level;
    
    private NotificationCategory category;
    
    private Date time;
    
    private String message;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public Notification()
    {
        this.uid = CodeGenerator.generateCode();
    }

    public Notification( NotificationLevel level, NotificationCategory category, Date time, String message )
    {
        this.uid = CodeGenerator.generateCode();
        this.level = level;
        this.category = category;
        this.time = time;
        this.message = message;
    }

    // -------------------------------------------------------------------------
    // Get and set
    // -------------------------------------------------------------------------

    public NotificationLevel getLevel()
    {
        return level;
    }

    public String getUid()
    {
        return uid;
    }

    public void setUid( String uid )
    {
        this.uid = uid;
    }

    public void setLevel( NotificationLevel level )
    {
        this.level = level;
    }

    public NotificationCategory getCategory()
    {
        return category;
    }

    public void setCategory( NotificationCategory category )
    {
        this.category = category;
    }

    public Date getTime()
    {
        return time;
    }

    public void setTime( Date time )
    {
        this.time = time;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    // -------------------------------------------------------------------------
    // equals, hashCode, toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return uid.hashCode();
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
        
        final Notification other = (Notification) object;
        
        return uid.equals( other.uid );
    }

    @Override
    public String toString()
    {
        return "[Level: " + level + ", category: " + category + ", time: " + time + ", message: " + message + "]";
    }
}
