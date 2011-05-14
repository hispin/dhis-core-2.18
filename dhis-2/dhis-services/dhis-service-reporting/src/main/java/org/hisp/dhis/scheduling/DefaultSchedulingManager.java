package org.hisp.dhis.scheduling;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.system.scheduling.Scheduler;

/**
 * @author Lars Helge Overland
 */
public class DefaultSchedulingManager
    implements SchedulingManager
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;
    
    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private Scheduler scheduler;

    public void setScheduler( Scheduler scheduler )
    {
        this.scheduler = scheduler;
    }

    private Map<String, Runnable> tasks = new HashMap<String, Runnable>();

    public void setTasks( Map<String, Runnable> tasks )
    {
        this.tasks = tasks;
    }

    // -------------------------------------------------------------------------
    // SchedulingManager implementation
    // -------------------------------------------------------------------------

    public void scheduleTasks()
    {
        scheduler.scheduleTask( getRunnables(), Scheduler.CRON_NIGHTLY_1AM );
    }
    
    public void stopTasks()
    {
        scheduler.stopTask( Runnables.class );
    }
    
    public void executeTasks()
    {
        scheduler.executeTask( getRunnables() );
    }
    
    public String getTaskStatus()
    {
        return scheduler.getTaskStatus( Runnables.class );
    }
    
    public Set<String> getRunningTaskKeys()
    {
        final Set<String> keys = new HashSet<String>();
        
        for ( String key : tasks.keySet() )
        {
            if ( Scheduler.STATUS_RUNNING.equals( scheduler.getTaskStatus( tasks.get( key ).getClass() ) ) )
            {
                keys.add( key );
            }
        }
        
        return keys;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private Runnables getRunnables()
    {
        final Runnables runnables = new Runnables();
        
        for ( String key : tasks.keySet() )
        {
            boolean schedule = (Boolean) systemSettingManager.getSystemSetting( key, false );
            
            if ( schedule )
            {
                runnables.addRunnable( tasks.get( key ) );
            }
            else
            {
                scheduler.stopTask( tasks.get( key ).getClass() );
            }
        }
        
        return runnables;
    }
}
