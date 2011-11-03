package org.hisp.dhis.dataanalyser.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

public class GetPatientDetailsAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }
    
    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private Patient patient;

    public Patient getPatient()
    {
        return patient;
    }   

    private PatientIdentifier patientIdentifier;

    public PatientIdentifier getPatientIdentifier()
    {
        return patientIdentifier;
    }

    private Collection<Program> programs;

    public Collection<Program> getPrograms()
    {
        return programs;
    }
    
    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        patient = patientService.getPatient( id );       

        patientIdentifier = patientIdentifierService.getPatientIdentifier( patient );        
        
        for( PatientAttribute patientAttribute : patient.getAttributes() )
        {
            patientAttributeValueMap.put( patientAttribute.getId(), PatientAttributeValue.UNKNOWN );
        }

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
            .getPatientAttributeValues( patient );       

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(), patientAttributeValue.getValue() );
        }

        programs = programService.getAllPrograms();

        return SUCCESS;
    }
}