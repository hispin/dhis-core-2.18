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

package org.hisp.dhis.patient.action.program;

import java.util.Collection;

import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElementValidation;
import org.hisp.dhis.program.ProgramStageDataElementValidationService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * @version ShowValidationDEAssociationsFormAction.java May 5, 2010 9:35:09 AM
 */
public class ShowValidationDEAssociationsFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramService programService;

    private ProgramStageDataElementValidationService programStageDEValidationService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Program program;

    private Collection<ProgramStage> stages;
    
    private Collection<ProgramStageDataElementValidation> validations;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public Program getProgram()
    {
        return program;
    }

    public Collection<ProgramStageDataElementValidation> getValidations()
    {
        return validations;
    }

    public void setProgramStageDEValidationService( ProgramStageDataElementValidationService programStageDEValidationService )
    {
        this.programStageDEValidationService = programStageDEValidationService;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public Collection<ProgramStage> getStages()
    {
        return stages;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        program = programService.getProgram( id );

        stages = program.getProgramStages();

        validations = programStageDEValidationService.getProgramStageDataElementValidations( program );

        return SUCCESS;
    }
}
