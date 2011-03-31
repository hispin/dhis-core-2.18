package org.hisp.dhis.caseentry.action.caseentry;

import java.util.ArrayList;
import java.util.Collection;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

public class MultiDataEntrySelectAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
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

    private Collection<Program> programs = new ArrayList<Program>();

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    private Collection<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();

    public Collection<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        patientAttributes = patientAttributeService.getAllPatientAttributes();

        // ---------------------------------------------------------------------
        // Validate selected OrganisationUnit
        // ---------------------------------------------------------------------

        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();

        if ( organisationUnit == null )
        {
            programId = null;

            selectedStateManager.clearSelectedProgram();

            return SUCCESS;
        }

        // ---------------------------------------------------------------------
        // Load assigned Programs
        // ---------------------------------------------------------------------

        programs = programService.getPrograms( organisationUnit );

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

            selectedStateManager.clearSelectedProgram();

            return SUCCESS;
        }

        return SUCCESS;
    }

}
