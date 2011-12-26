package org.hisp.dhis.reporting.tablecreator.action;

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

import static org.hisp.dhis.reporttable.ReportTableInternalProcess.PROCESS_TYPE;
import static org.hisp.dhis.util.InternalProcessUtil.PROCESS_KEY_REPORT;
import static org.hisp.dhis.util.InternalProcessUtil.setCurrentRunningProcess;

import java.util.Date;

import org.amplecode.cave.process.ProcessCoordinator;
import org.amplecode.cave.process.ProcessExecutor;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.reporttable.ReportTableInternalProcess;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class CreateTableAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ProcessCoordinator processCoordinator;

    public void setProcessCoordinator( ProcessCoordinator processCoordinator )
    {
        this.processCoordinator = processCoordinator;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String mode;

    public void setMode( String mode )
    {
        this.mode = mode;
    }

    private String reportingPeriod;

    public void setReportingPeriod( String reportingPeriod )
    {
        this.reportingPeriod = reportingPeriod;
    }

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        String owner = currentUserService.getCurrentUsername();

        ProcessExecutor executor = processCoordinator.newProcess( PROCESS_TYPE, owner );
        
        ReportTableInternalProcess process = (ReportTableInternalProcess) executor.getProcess();

        Date date = reportingPeriod != null ? DateUtils.getMediumDate( reportingPeriod ) : new Date();
        
        process.setId( id );
        process.setMode( mode );
        process.setReportingPeriod( date );
        process.setOrganisationUnitId( organisationUnitId );
        process.setFormat( format );

        processCoordinator.requestProcessExecution( executor );

        setCurrentRunningProcess( PROCESS_KEY_REPORT, executor.getId() );

        return SUCCESS;
    }
}
