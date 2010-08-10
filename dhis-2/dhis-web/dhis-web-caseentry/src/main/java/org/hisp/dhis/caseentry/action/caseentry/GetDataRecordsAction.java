package org.hisp.dhis.caseentry.action.caseentry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;

import com.opensymphony.xwork2.Action;

public class GetDataRecordsAction implements Action
{
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

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;
    
    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Integer sortPatientAttributeId;
    
    public void setSortPatientAttributeId( Integer sortPatientAttributeId )
    {
        this.sortPatientAttributeId = sortPatientAttributeId;
    }

    public Integer getSortPatientAttributeId()
    {
        return sortPatientAttributeId;
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

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    Collection<ProgramInstance> programInstances = new ArrayList<ProgramInstance>();

    public Collection<ProgramInstance> getProgramInstances()
    {
        return programInstances;
    }

    private Map<Integer, String> colorMap = new HashMap<Integer, String>();

    public Map<Integer, String> getColorMap()
    {
        return colorMap;
    }
    
    private Map<Patient, ProgramInstance> programInstanceMap = new HashMap<Patient, ProgramInstance>();
    
    public Map<Patient, ProgramInstance> getProgramInstanceMap()
    {
        return programInstanceMap;
    }
    
    private Map<Patient, PatientAttributeValue> patinetAttributeValueMap = new HashMap<Patient, PatientAttributeValue>();
    
    public Map<Patient, PatientAttributeValue> getPatinetAttributeValueMap()
    {
        return patinetAttributeValueMap;
    }

    Collection<Patient> patientListByOrgUnit;
    
    public Collection<Patient> getPatientListByOrgUnit()
    {
        return patientListByOrgUnit;
    }
    
    List<Program> programs;
    
    public List<Program> getPrograms()
    {
        return programs;
    }
    
    private Collection<PatientAttribute> patientAttributes = new ArrayList<PatientAttribute>();
    
    public Collection<PatientAttribute> getPatientAttributes()
    {
        return patientAttributes;
    }
    
    private PatientAttribute sortingAttribute;
    
    public PatientAttribute getSortingAttribute()
    {
        return sortingAttribute;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Patient Attribute List
        // ---------------------------------------------------------------------
        patientAttributes = patientAttributeService.getAllPatientAttributes();

        organisationUnit = selectedStateManager.getSelectedOrganisationUnit();
        
        programs = new ArrayList<Program>( programService.getPrograms( organisationUnit ) );
        
        if( programId == 0 )
        {
            selectedStateManager.clearSelectedProgram();
            
            return SUCCESS;
        }
        
        program = programService.getProgram( programId );

        selectedStateManager.setSelectedProgram( program );
        
        // ---------------------------------------------------------------------
        // Getting the list of Patients that are related to selected OrganisationUnit
        // ---------------------------------------------------------------------
        
        patientListByOrgUnit = new ArrayList<Patient>();
        
        patientListByOrgUnit.addAll( patientService.getPatientsByOrgUnit( organisationUnit ) );
        
        if( sortPatientAttributeId != null )
        {
            sortingAttribute = patientAttributeService.getPatientAttribute( sortPatientAttributeId );
        }
        
        // ---------------------------------------------------------------------
        // Program instances for the selected program
        // ---------------------------------------------------------------------
        
        Collection<ProgramInstance> selectedProgramInstances = programInstanceService.getProgramInstances( program );
        
        Collection<ProgramStageInstance> programStageInstances = new ArrayList<ProgramStageInstance>();

        for ( ProgramInstance programInstance : selectedProgramInstances )
        {
            Patient patient = programInstance.getPatient();
            
            //taking patient present in selected orgunit
            if ( !patientListByOrgUnit.contains( patient ) || programInstance.getEndDate() != null )
            {
                patientListByOrgUnit.remove( patient );
                continue;
            }
            
            if ( !programInstance.isCompleted() )
            {
                programInstanceMap.put( patient, programInstance );
                
                programInstances.add( programInstance );
                
                PatientAttributeValue patientAttributeValue = patientAttributeValueService.getPatientAttributeValue( patient, sortingAttribute );
                
                patinetAttributeValueMap.put( patient, patientAttributeValue );
                
                System.out.println( patient.getFullName() );
            }
            else
            {
                patientListByOrgUnit.remove( patient );
            }

            programStageInstances.addAll( programInstance.getProgramStageInstances() );
        }
        
        // Sorting PatientList by slected Patient Attribute
        
        if( sortPatientAttributeId != null )
        {
            patientListByOrgUnit = patientService.sortPatientsByAttribute( programInstanceMap.keySet(), sortingAttribute );
        }
        else
        {
            patientListByOrgUnit = programInstanceMap.keySet();
        }

        System.out.println("sortPatientAttributeId : "+sortPatientAttributeId);
        colorMap = programStageInstanceService.colorProgramStageInstances( programStageInstances );


        return SUCCESS;
    }    

}
