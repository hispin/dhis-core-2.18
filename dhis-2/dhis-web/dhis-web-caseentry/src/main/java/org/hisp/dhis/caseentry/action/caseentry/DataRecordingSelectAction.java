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
package org.hisp.dhis.caseentry.action.caseentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class DataRecordingSelectAction
    implements Action
{
    Log log = LogFactory.getLog( DataRecordingSelectAction.class );

    private static final String DATAENTRY_FORM = "dataentryform";

    private static final String CUSTOM_DATAENTRY_FORM = "customentryform";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

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

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public Integer getProgramId()
    {
        return programId;
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

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }

    private Collection<Program> programs = new ArrayList<Program>();

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    private Collection<ProgramStage> programStages;

    public Collection<ProgramStage> getProgramStages()
    {
        return programStages;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private ProgramInstance programInstance;

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    private ProgramStageInstance programStageInstance;

    public ProgramStageInstance getProgramStageInstance()
    {
        return programStageInstance;
    }

    private Map<Integer, String> colorMap = new HashMap<Integer, String>();

    public Map<Integer, String> getColorMap()
    {
        return colorMap;
    }

    private boolean customDataEntryFormExists;

    public boolean getCustomDataEntryFormExists()
    {
        return customDataEntryFormExists;
    }

    private String useDefaultForm;

    public String getUseDefaultForm()
    {
        return useDefaultForm;
    }

    public void setUseDefaultForm( String useDefaultForm )
    {
        this.useDefaultForm = useDefaultForm;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Validate selected OrganisationUnit && Patient
        // ---------------------------------------------------------------------
    
        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();
        
        if ( organisationUnit == null || id == null )
        {
            programId = null;
            programStageId = null;
            
            selectedStateManager.clearSelectedPatient();
            selectedStateManager.clearSelectedProgram();
            selectedStateManager.clearSelectedProgramStage();
            
            return SUCCESS;
        }
        
        patient = patientService.getPatient( id );
        
        selectedStateManager.setSelectedPatient( patient );
        
        // ---------------------------------------------------------------------
        // Load Enrolled Programs
        // ---------------------------------------------------------------------
        
        for ( Program program : patient.getPrograms() )
        {
            if ( program.getOrganisationUnits().contains( organisationUnit ) )
            {
                programs.add( program );
            }
        }
        
        // ---------------------------------------------------------------------
        // Validate selected Program
        // ---------------------------------------------------------------------

        Program selectedProgram;
        
        if ( programId != null )
        {
            selectedProgram = programService.getProgram( programId );
        }
        else
        {
            selectedProgram = selectedStateManager.getSelectedProgram();
        }

        if ( selectedProgram != null && programs.contains( selectedProgram ) )
        {
            programId = selectedProgram.getId();
            selectedStateManager.setSelectedProgram( selectedProgram );
        }
        else
        {
            programId = null;
            programStageId = null;

            selectedStateManager.clearSelectedProgram();
            selectedStateManager.clearSelectedProgramStage();

            return SUCCESS;
        }
        
        // ---------------------------------------------------------------------
        // Load the active program instance completed = false we need the
        // corresponding stage execution date
        // ---------------------------------------------------------------------

        Collection<ProgramInstance> progamInstances = programInstanceService.getProgramInstances( patient,
            selectedProgram, false );
        
        if ( progamInstances == null || progamInstances.iterator() == null || !progamInstances.iterator().hasNext() )
            return SUCCESS;
        
        programInstance = progamInstances.iterator().next();
        
        colorMap = programStageInstanceService.colorProgramStageInstances( programInstance.getProgramStageInstances() );
        
        // ---------------------------------------------------------------------
        // Load ProgramStages
        // ---------------------------------------------------------------------

        programStages = selectedProgram.getProgramStages();
        
        // ---------------------------------------------------------------------
        // Validate selected ProgramStage
        // ---------------------------------------------------------------------

        ProgramStage selectedProgramStage;

        if ( programStageId != null )
        {
            selectedProgramStage = programStageService.getProgramStage( programStageId );
        }
        else
        {
            selectedProgramStage = selectedStateManager.getSelectedProgramStage();
        }

        if ( selectedProgramStage != null && programStages.contains( selectedProgramStage ) )
        {
            programStageId = selectedProgramStage.getId();
            selectedStateManager.setSelectedProgramStage( selectedProgramStage );
        }

        else
        {
            programStageId = null;
            selectedStateManager.clearSelectedProgramStage();

            return SUCCESS;
        }
        
        // ---------------------------------------------------------------------
        // Load the programStageInstance we need the corresponding execution
        // date
        // ---------------------------------------------------------------------

        ProgramInstance programInstance = progamInstances.iterator().next();
        
        programStageInstance = programStageInstanceService.getProgramStageInstance( programInstance,
            selectedProgramStage );
        
        // ---------------------------------------------------------------------
        // Check if there is custom DataEntryForm
        // ---------------------------------------------------------------------

        if ( selectedProgramStage.getDataEntryForm() != null )
        {
            customDataEntryFormExists = true;
        }

        if ( customDataEntryFormExists && useDefaultForm == null )
        {
            return CUSTOM_DATAENTRY_FORM;
        }
        
        return DATAENTRY_FORM;

    }
}
