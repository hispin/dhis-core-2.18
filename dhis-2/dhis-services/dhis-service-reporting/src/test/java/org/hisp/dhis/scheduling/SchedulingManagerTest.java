package org.hisp.dhis.scheduling;

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

import static org.hisp.dhis.scheduling.SchedulingManager.TASK_ANALYTICS_ALL;
import static org.hisp.dhis.scheduling.SchedulingManager.TASK_DATAMART_LAST_6_MONTHS;
import static org.hisp.dhis.scheduling.SchedulingManager.TASK_RESOURCE_TABLE;
import static org.hisp.dhis.system.scheduling.Scheduler.CRON_DAILY_0AM;
import static org.hisp.dhis.system.scheduling.Scheduler.CRON_DAILY_0AM_EXCEPT_SUNDAY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.DhisSpringTest;
import org.hisp.dhis.common.ListMap;
import org.hisp.dhis.system.scheduling.Scheduler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class SchedulingManagerTest
    extends DhisSpringTest
{
    @Autowired
    private SchedulingManager schedulingManager;

    @Test
    public void testScheduleTasks()
    {
        Map<String, String> keyCronMap = new HashMap<String, String>();
        keyCronMap.put( TASK_RESOURCE_TABLE, CRON_DAILY_0AM );
        keyCronMap.put( TASK_ANALYTICS_ALL, CRON_DAILY_0AM );
        keyCronMap.put( TASK_DATAMART_LAST_6_MONTHS, CRON_DAILY_0AM_EXCEPT_SUNDAY );
                
        schedulingManager.scheduleTasks( keyCronMap );
        
        ListMap<String, String> cronKeyMap = schedulingManager.getCronKeyMap();
        
        assertEquals( 2, cronKeyMap.size() );
        assertTrue( cronKeyMap.containsKey( CRON_DAILY_0AM ) );
        assertTrue( cronKeyMap.containsKey( CRON_DAILY_0AM_EXCEPT_SUNDAY ) );
        assertEquals( 2, cronKeyMap.get( CRON_DAILY_0AM ).size() );
        assertEquals( 1, cronKeyMap.get( CRON_DAILY_0AM_EXCEPT_SUNDAY ).size() );
        
        assertEquals( Scheduler.STATUS_RUNNING, schedulingManager.getTaskStatus() );
    }

    @Test
    public void testStopTasks()
    {
        Map<String, String> keyCronMap = new HashMap<String, String>();
        keyCronMap.put( TASK_RESOURCE_TABLE, CRON_DAILY_0AM );
        keyCronMap.put( TASK_ANALYTICS_ALL, CRON_DAILY_0AM );

        assertEquals( Scheduler.STATUS_NOT_STARTED, schedulingManager.getTaskStatus() );
        
        schedulingManager.scheduleTasks( keyCronMap );
        
        assertEquals( Scheduler.STATUS_RUNNING, schedulingManager.getTaskStatus() );
        
        schedulingManager.stopTasks();

        assertEquals( Scheduler.STATUS_NOT_STARTED, schedulingManager.getTaskStatus() );
    }

    @Test
    public void testIsScheduled()
    {
        Map<String, String> keyCronMap = new HashMap<String, String>();
        keyCronMap.put( TASK_RESOURCE_TABLE, CRON_DAILY_0AM );
        keyCronMap.put( TASK_ANALYTICS_ALL, CRON_DAILY_0AM );

        schedulingManager.scheduleTasks( keyCronMap );
        
        assertTrue( schedulingManager.isScheduled( TASK_RESOURCE_TABLE ) );
        assertFalse( schedulingManager.isScheduled( TASK_DATAMART_LAST_6_MONTHS ) );
    }
}
