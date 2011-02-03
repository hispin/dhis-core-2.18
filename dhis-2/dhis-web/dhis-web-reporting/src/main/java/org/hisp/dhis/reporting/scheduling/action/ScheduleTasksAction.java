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

import org.hisp.dhis.common.ServiceProvider;
import org.hisp.dhis.completeness.DataSetCompletenessService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datamart.DataMartService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.scheduling.DataMartTask;
import org.hisp.dhis.system.scheduling.DataSetCompletenessTask;
import org.hisp.dhis.system.scheduling.Scheduler;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class ScheduleTasksAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private Scheduler scheduler;
    
    public void setScheduler( Scheduler scheduler )
    {
        this.scheduler = scheduler;
    }

    private DataMartService dataMartService;

    public void setDataMartService( DataMartService dataMartService )
    {
        this.dataMartService = dataMartService;
    }
    
    private ServiceProvider<DataSetCompletenessService> serviceProvider;

    public void setServiceProvider( ServiceProvider<DataSetCompletenessService> serviceProvider )
    {
        this.serviceProvider = serviceProvider;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
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
        DataMartTask dataMartTask = new DataMartTask( dataMartService, dataElementService, indicatorService, organisationUnitService );
        DataSetCompletenessTask completenessTask = new DataSetCompletenessTask( serviceProvider.provide( "registration" ), dataSetService, organisationUnitService );
        
        if ( !statusOnly )
        {
            if ( execute )
            {
                scheduler.executeTask( dataMartTask );
                scheduler.executeTask( completenessTask );
            }
            else if ( scheduler.getTaskStatus( DataMartTask.class ).equals( Scheduler.STATUS_RUNNING ) )
            {
                scheduler.stopTask( DataMartTask.class );
                scheduler.stopTask( DataSetCompletenessTask.class );
            }
            else
            {
                scheduler.scheduleTask( dataMartTask, Scheduler.CRON_NIGHTLY_2AM );
                scheduler.scheduleTask( completenessTask, Scheduler.CRON_NIGHTLY_1AM );
            }
        }
        
        status = scheduler.getTaskStatus( DataMartTask.class );
        
        running = scheduler.getTaskStatus( DataMartTask.class ).equals( Scheduler.STATUS_RUNNING );
        
        return SUCCESS;
    }
}
