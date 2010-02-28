package org.hisp.dhis.patient.action.patient;

import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;

import com.opensymphony.xwork2.Action;

public class SearchPersonAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer identifierTypeId; 
    
    private Integer attributeId;
    
    private String value;
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<Patient> patients;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        patients =  patientService.searchPatient( identifierTypeId, attributeId, value );
        
        return SUCCESS;
    }
    

    // -------------------------------------------------------------------------
    // Getter/Setter
    // -------------------------------------------------------------------------

    public void setIdentifierTypeId( Integer identifierTypeId )
    {
        this.identifierTypeId = identifierTypeId;
    }

    public void setAttributeId( Integer attributeId )
    {
        this.attributeId = attributeId;
    }

    public void setValue( String value )
    {
        this.value = value;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public Collection<Patient> getPatients()
    {
        return patients;
    }


}
