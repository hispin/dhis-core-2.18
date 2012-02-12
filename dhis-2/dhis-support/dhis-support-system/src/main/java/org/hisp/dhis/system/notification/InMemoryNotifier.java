package org.hisp.dhis.system.notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

public class InMemoryNotifier
    implements Notifier
{
    private int MAX_SIZE = 1000;
    
    private List<Notification> notifications;
    
    @PostConstruct
    public void init()
    {
        notifications = new ArrayList<Notification>();
    }

    // -------------------------------------------------------------------------
    // Notifier implementation
    // -------------------------------------------------------------------------

    @Override
    public void notify( NotificationCategory category, String message )
    {
        notify( NotificationLevel.INFO, category, message );
    }
    
    @Override
    public void notify( NotificationLevel level, NotificationCategory category, String message )
    {
        Notification notification = new Notification( level, category, new Date(), message );
        
        notifications.add( 0, notification );
        
        if ( notifications.size() > MAX_SIZE )
        {
            notifications.remove( MAX_SIZE );
        }
    }

    @Override
    public List<Notification> getNotifications( int max )
    {
        max = max > notifications.size() ? notifications.size() : max;
        
        return notifications.subList( 0, max );
    }

    @Override
    public List<Notification> getNotifications( NotificationCategory category, int max )
    {
        List<Notification> list = new ArrayList<Notification>();
        
        for ( Notification notification : notifications )
        {
            if ( list.size() == max )
            {
                break;
            }
            
            if ( category.equals( notification.getCategory() ) )
            {
                list.add( notification );
            }
        }
        
        return list;
    }
}
