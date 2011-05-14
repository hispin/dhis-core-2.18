package org.hisp.dhis.scheduling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.system.scheduling.Scheduler;

public class DefaultSchedulingManager
    implements SchedulingManager
{
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
            if ( scheduler.getTaskStatus( tasks.get( key ).getClass() ).equals( Scheduler.STATUS_RUNNING ) )
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
