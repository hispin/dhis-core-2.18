package org.hisp.dhis.system.notification;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

/**
 * @author Lars Helge Overland
 */
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
    public Notifier notify( NotificationCategory category, String message )
    {
        return notify( NotificationLevel.INFO, category, message, false );
    }
    
    @Override
    public Notifier notify( NotificationLevel level, NotificationCategory category, String message, boolean completed )
    {
        Notification notification = new Notification( level, category, new Date(), message, completed );
        
        notifications.add( 0, notification );
        
        if ( notifications.size() > MAX_SIZE )
        {
            notifications.remove( MAX_SIZE );
        }
        
        return this;
    }

    @Override
    public List<Notification> getNotifications( NotificationCategory category, int max )
    {
        List<Notification> list = new ArrayList<Notification>();
        
        if ( category != null )
        {
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
        }
        
        return list;
    }

    @Override
    public List<Notification> getNotifications( NotificationCategory category, String lastUid )
    {
        List<Notification> list = new ArrayList<Notification>();
        
        if ( category != null )
        {
            for ( Notification notification : notifications )
            {
                if ( lastUid != null && lastUid.equals( notification.getUid() ) )
                {
                    break;
                }
                
                if ( category.equals( notification.getCategory() ) )
                {
                    list.add( notification );
                }
            }
        }
        
        return list;
    }
    
    @Override
    public Notifier clear( NotificationCategory category )
    {
        if ( category != null )
        {
            Iterator<Notification> iter = notifications.iterator();
            
            while ( iter.hasNext() )
            {
                if ( category.equals( iter.next() ) )
                {
                    iter.remove();
                }
            }
        }
        
        return this;
    }
}
