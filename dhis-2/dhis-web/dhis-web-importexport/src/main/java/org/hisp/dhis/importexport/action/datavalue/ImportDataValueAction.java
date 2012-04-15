package org.hisp.dhis.importexport.action.datavalue;

/*
 * Copyright (c) 2012, University of Oslo
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.hisp.dhis.common.IdentifiableObject.IdentifiableProperty;
import org.hisp.dhis.dxf2.datavalueset.DataValueSetService;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.importexport.ImportStrategy;
import org.hisp.dhis.importexport.action.util.ImportDataValueTask;
import org.hisp.dhis.scheduling.TaskCategory;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.scheduling.Scheduler;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

import static org.hisp.dhis.importexport.action.util.ImportDataValueTask.FORMAT_CSV;

/**
 * @author Lars Helge Overland
 */
public class ImportDataValueAction
    implements Action
{
    @Autowired
    private DataValueSetService dataValueSetService;

    @Autowired
    private CurrentUserService currentUserService;
    
    @Autowired
    private Scheduler scheduler;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private File upload;

    public void setUpload( File upload )
    {
        this.upload = upload;
    }
    
    private boolean dryRun;

    public void setDryRun( boolean dryRun )
    {
        this.dryRun = dryRun;
    }

    private ImportStrategy strategy;

    public void setStrategy( String stgy )
    {
        this.strategy = ImportStrategy.valueOf( stgy );
    }
    
    private String importFormat;

    public void setImportFormat( String importFormat )
    {
        this.importFormat = importFormat;
    }

    public String getImportFormat()
    {
        return importFormat;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute()
        throws Exception
    {
        final TaskId taskId = new TaskId( TaskCategory.DATAVALUE_IMPORT, currentUserService.getCurrentUser() );

        final ImportOptions options = new ImportOptions( IdentifiableProperty.UID, IdentifiableProperty.UID, dryRun, strategy );
        
        final InputStream in = !FORMAT_CSV.equals( importFormat ) ? new BufferedInputStream( new FileInputStream( upload ) ) : null;
        
        final Reader reader = FORMAT_CSV.equals( importFormat ) ? new BufferedReader( new InputStreamReader( new FileInputStream( upload ) ) ) : null;

        scheduler.executeTask( new ImportDataValueTask( dataValueSetService, in, reader, options, taskId, importFormat ) );
        
        return SUCCESS;
    }
}
