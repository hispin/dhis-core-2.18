package org.hisp.dhis.reporting.scheduling.action;

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

import org.hisp.dhis.options.SystemSettingManager;
import org.hisp.dhis.scheduling.SchedulingManager;
import org.hisp.dhis.system.scheduling.Scheduler;

import com.opensymphony.xwork2.Action;

import static org.hisp.dhis.options.SystemSettingManager.*;

/**
 * @author Lars Helge Overland
 */
public class ScheduleTasksAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;
    
    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }
    
    private SchedulingManager schedulingManager;

    public void setSchedulingManager( SchedulingManager schedulingManager )
    {
        this.schedulingManager = schedulingManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private boolean execute;

    public void setExecute( boolean execute )
    {
        this.execute = execute;
    }
    
    private boolean statusOnly = false;

    public void setStatusOnly( boolean statusOnly )
    {
        this.statusOnly = statusOnly;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String status;

    public String getStatus()
    {
        return status;
    }

    private boolean running;

    public boolean isRunning()
    {
        return running;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        if ( !statusOnly )
        {
            if ( execute )
            {
                schedulingManager.executeTasks();
            }
            else if ( Scheduler.STATUS_RUNNING.equals( schedulingManager.getTaskStatus() ) )
            {
                systemSettingManager.saveSystemSetting( KEY_DATAMART_TASK, new Boolean( false ) );
                systemSettingManager.saveSystemSetting( KEY_DATASETCOMPLETENESS_TASK, new Boolean( false ) );
                
                schedulingManager.stopTasks();
            }
            else
            {
                systemSettingManager.saveSystemSetting( KEY_DATAMART_TASK, new Boolean( true) );
                systemSettingManager.saveSystemSetting( KEY_DATASETCOMPLETENESS_TASK, new Boolean( true ) );
                
                schedulingManager.scheduleTasks();
            }
        }

        status = schedulingManager.getTaskStatus();        
        running = Scheduler.STATUS_RUNNING.equals( status );
        
        return SUCCESS;
    }
}
