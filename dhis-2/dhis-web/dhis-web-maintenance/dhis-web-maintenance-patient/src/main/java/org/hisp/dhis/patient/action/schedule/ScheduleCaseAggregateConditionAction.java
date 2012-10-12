/*
 * Copyright (c) 2004-2009, University of Oslo
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

package org.hisp.dhis.patient.action.schedule;

import static org.hisp.dhis.setting.SystemSettingManager.KEY_AGGREGATE_QUERY_BUILDER_ORGUNITGROUPSET_AGG_LEVEL;
import static org.hisp.dhis.setting.SystemSettingManager.KEY_SCHEDULED_AGGREGATE_QUERY_BUILDER_PERIOD_TYPES;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.patient.scheduling.CaseAggregateConditionSchedulingManager;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.system.scheduling.Scheduler;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version ScheduleCaseAggregateConditionAction.java 11:14:34 AM Oct 10, 2012 $
 */
public class ScheduleCaseAggregateConditionAction
    implements Action
{
    private static final String STRATEGY_LAST_12_DAILY = "last12Daily";

    private static final String STRATEGY_LAST_6_DAILY_6_TO_12_WEEKLY = "last6Daily6To12Weekly";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private CaseAggregateConditionSchedulingManager schedulingManager;

    public void setSchedulingManager( CaseAggregateConditionSchedulingManager schedulingManager )
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

    private Set<String> scheduledPeriodTypes = new HashSet<String>();

    public void setScheduledPeriodTypes( Set<String> scheduledPeriodTypes )
    {
        this.scheduledPeriodTypes = scheduledPeriodTypes;
    }

    private Integer orgUnitGroupSetAggLevel;

    public void setOrgUnitGroupSetAggLevel( Integer orgUnitGroupSetAggLevel )
    {
        this.orgUnitGroupSetAggLevel = orgUnitGroupSetAggLevel;
    }

    private String aggQueryBuilderStrategy;

    public void setAggQueryBuilderStrategy( String aggQueryBuilderStrategy )
    {
        this.aggQueryBuilderStrategy = aggQueryBuilderStrategy;
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

    @Override
    public String execute()
        throws Exception
    {
        if ( execute )
        {
            schedulingManager.executeTasks();
        }
        else
        {
            systemSettingManager.saveSystemSetting( KEY_SCHEDULED_AGGREGATE_QUERY_BUILDER_PERIOD_TYPES,
                (HashSet<String>) scheduledPeriodTypes );
            systemSettingManager.saveSystemSetting( KEY_AGGREGATE_QUERY_BUILDER_ORGUNITGROUPSET_AGG_LEVEL,
                orgUnitGroupSetAggLevel );

            if ( Scheduler.STATUS_RUNNING.equals( schedulingManager.getTaskStatus() ) )
            {
                schedulingManager.stopTasks();
            }
            else
            {
                Map<String, String> keyCronMap = new HashMap<String, String>();

                if ( STRATEGY_LAST_12_DAILY.equals( aggQueryBuilderStrategy ) )
                {
                    keyCronMap.put(
                        CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_LAST_12_MONTHS,
                        Scheduler.CRON_DAILY_0AM );
                }
                else if ( STRATEGY_LAST_6_DAILY_6_TO_12_WEEKLY.equals( aggQueryBuilderStrategy ) )
                {
                    keyCronMap.put( CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_LAST_6_MONTS,
                        Scheduler.CRON_DAILY_0AM_EXCEPT_SUNDAY );
                    keyCronMap.put(
                        CaseAggregateConditionSchedulingManager.TASK_AGGREGATE_QUERY_BUILDER_FROM_6_TO_12_MONTS,
                        Scheduler.CRON_WEEKLY_SUNDAY_0AM );
                }

                schedulingManager.scheduleTasks( keyCronMap );
            }
        }
        
        status = schedulingManager.getTaskStatus();

        running = Scheduler.STATUS_RUNNING.equals( status );

        return SUCCESS;
    }
}
