package org.hisp.dhis.datamart.task;

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
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.hisp.dhis.datamart.DataMartScheduler;
import org.hisp.dhis.datamart.DataMartService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

/**
 * @author Lars Helge Overland
 */
public class SpringDataMartScheduler
    implements DataMartScheduler
{
    private Map<Integer, ScheduledFuture<?>> scheduledFutureMap = new HashMap<Integer, ScheduledFuture<?>>(); // Gives class state but no better way to handle this?

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataMartService dataMartService;
    
    public void setDataMartService( DataMartService dataMartService )
    {
        this.dataMartService = dataMartService;
    }

    private TaskScheduler taskScheduler;

    public void setTaskScheduler( TaskScheduler taskScheduler )
    {
        this.taskScheduler = taskScheduler;
    }

    // -------------------------------------------------------------------------
    // DataMartSceduler implementation
    // -------------------------------------------------------------------------

    public void scheduleDataMartExport( int id )
    {
        ScheduledFuture<?> future = taskScheduler.schedule( new DataMartTask( dataMartService, id ), new CronTrigger( CRON_NIGHTLY ) );
        
        scheduledFutureMap.put( id, future );
    }
    
    public boolean stopDataMartExport( int id )
    {
        ScheduledFuture<?> future = scheduledFutureMap.get( id );
        
        return future.cancel( true );
    }
    
    public String getDataMartExportStatus( int id )
    {
        ScheduledFuture<?> future = scheduledFutureMap.get( id );
        
        if ( future == null )
        {
            return STATUS_NOT_STARTED;
        }
        else if ( future.isCancelled() )
        {
            return STATUS_STOPPED;
        }
        else if ( future.isDone() )
        {
            return STATUS_DONE;
        }
        else
        {
            return STATUS_RUNNING;
        }   
    }
}
