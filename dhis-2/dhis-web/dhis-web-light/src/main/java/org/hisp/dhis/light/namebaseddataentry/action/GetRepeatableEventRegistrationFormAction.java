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
package org.hisp.dhis.light.namebaseddataentry.action;

import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

public class GetRepeatableEventRegistrationFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private ProgramStageService programStageService;

    public ProgramStageService getProgramStageService()
    {
        return programStageService;
    }

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer programInstanceId;

    public Integer getProgramInstanceId()
    {
        return programInstanceId;
    }

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }

    private Integer programStageId;

    public Integer getProgramStageId()
    {
        return programStageId;
    }

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }
    
    private ProgramStage programStage;
    
    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( ProgramStage programStage )
    {
        this.programStage = programStage;
    }
    
    private Integer currentProgramStageInstanceId;
    
    public Integer getCurrentProgramStageInstanceId()
    {
        return currentProgramStageInstanceId;
    }

    public void setCurrentProgramStageInstanceId( Integer currentProgramStageInstanceId )
    {
        this.currentProgramStageInstanceId = currentProgramStageInstanceId;
    }

    @Override
    public String execute()
        throws Exception
    {
        programStage = programStageService.getProgramStage( programStageId );
        return SUCCESS;
    }

}
