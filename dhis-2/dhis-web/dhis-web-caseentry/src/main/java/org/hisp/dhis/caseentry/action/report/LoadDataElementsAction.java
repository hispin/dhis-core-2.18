package org.hisp.dhis.caseentry.action.report;

/*
 * Copyright (c) 2004-2013, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
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

import java.util.Collection;
import java.util.HashSet;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.program.ProgramStageSectionService;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $LoadDataElementsAction.java Feb 29, 2012 9:40:40 AM$
 */
public class LoadDataElementsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private ProgramStageService programStageService;

    public void setProgramStageService( ProgramStageService programStageService )
    {
        this.programStageService = programStageService;
    }

    private ProgramStageSectionService programStageSectionService;

    public void setProgramStageSectionService( ProgramStageSectionService programStageSectionService )
    {
        this.programStageSectionService = programStageSectionService;
    }

    private PatientIdentifierTypeService identifierTypeService;

    public void setIdentifierTypeService( PatientIdentifierTypeService identifierTypeService )
    {
        this.identifierTypeService = identifierTypeService;
    }

    private PatientAttributeService attributeService;

    public void setAttributeService( PatientAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private String programId;

    public void setProgramId( String programId )
    {
        this.programId = programId;
    }

    private String programStageId;

    public void setProgramStageId( String programStageId )
    {
        this.programStageId = programStageId;
    }

    private Integer sectionId;

    public void setSectionId( Integer sectionId )
    {
        this.sectionId = sectionId;
    }

    private Collection<PatientIdentifierType> identifierTypes = new HashSet<PatientIdentifierType>();

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    private Collection<PatientAttribute> patientAttributes = new HashSet<PatientAttribute>();

    public Collection<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    private Collection<ProgramStageDataElement> psDataElements;

    public Collection<ProgramStageDataElement> getPsDataElements()
    {
        return psDataElements;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( programId != null )
        {
            Program program = programService.getProgram( programId );
            if ( program.isRegistration() )
            {
                Collection<PatientIdentifierType> identifierTypes = identifierTypeService
                    .getAllPatientIdentifierTypes();
                Collection<PatientAttribute> patientAttributes = attributeService.getAllPatientAttributes();

                Collection<Program> programs = programService.getAllPrograms();
                programs.remove( program );

                for ( Program _program : programs )
                {
                    identifierTypes.removeAll( _program.getPatientIdentifierTypes() );
                    patientAttributes.removeAll( _program.getPatientAttributes() );
                }
            }
        }

        if ( programStageId != null )
        {
            psDataElements = programStageService.getProgramStage( programStageId ).getProgramStageDataElements();
        }
        else if ( sectionId != null )
        {
            ProgramStageSection section = programStageSectionService.getProgramStageSection( sectionId );
            psDataElements = section.getProgramStageDataElements();
        }

        return SUCCESS;
    }
}
