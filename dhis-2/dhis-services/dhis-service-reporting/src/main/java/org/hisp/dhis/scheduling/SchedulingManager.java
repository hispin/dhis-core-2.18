package org.hisp.dhis.scheduling;

import java.util.Set;

public interface SchedulingManager
{
    void scheduleTasks();
    
    void stopTasks();
    
    void executeTasks();
    
    String getTaskStatus();
    
    Set<String> getRunningTaskKeys();
}
