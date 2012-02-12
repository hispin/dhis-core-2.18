package org.hisp.dhis.system.notification;

import java.util.List;

public interface Notifier
{
    void notify( NotificationCategory category, String message );
    
    void notify(  NotificationLevel level, NotificationCategory category, String message );
    
    List<Notification> getNotifications( int max );
    
    List<Notification> getNotifications( NotificationCategory category, int max );
}
