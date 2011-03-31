package org.hisp.dhis.caseentry.action.report;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
import org.hisp.dhis.patient.PatientAttributeGroupService;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientIdentifierService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;

import com.opensymphony.xwork2.Action;

public class GetPatientDetailsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientService patientService;

    private PatientIdentifierService patientIdentifierService;

    private ProgramService programService;

    private PatientAttributeValueService patientAttributeValueService;

    private PatientAttributeService patientAttributeService;

    private PatientAttributeGroupService patientAttributeGroupService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    private Patient patient;

    private PatientIdentifier patientIdentifier;

    private Collection<Program> programs;

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    private Collection<PatientAttribute> noGroupAttributes;

    private Collection<PatientAttributeGroup> attributeGroups;

    private Collection<PatientIdentifierType> identifierTypes;

    private Map<Integer, String> identiferMap;

    private String childContactName;

    private String childContactType;

    private String systemIdentifier;

    private String benicode;

    private String yearcode;

    private String progcode;

    private String orgunitcode;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        patient = patientService.getPatient( id );

        patientIdentifier = patientIdentifierService.getPatientIdentifier( patient );

        identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        identiferMap = new HashMap<Integer, String>();

        PatientIdentifierType idType = null;
        Patient representative = patient.getRepresentative();

        if ( patient.isUnderAge() && representative != null )
        {
            for ( PatientIdentifier representativeIdentifier : representative.getIdentifiers() )
            {
                if ( representativeIdentifier.getIdentifierType() != null
                    && representativeIdentifier.getIdentifierType().isRelated() )
                {
                    identiferMap.put( representativeIdentifier.getIdentifierType().getId(), representativeIdentifier
                        .getIdentifier() );
                }
            }
        }

        for ( PatientIdentifier identifier : patient.getIdentifiers() )
        {
            idType = identifier.getIdentifierType();
            if ( idType != null )
            {
                identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );

                if ( idType.getFormat().equals( "State Format" ) )
                {
                    String iden = identifier.getIdentifier();
                    benicode = iden.substring( 12, 16 );// abcdefghi1121111
                    yearcode = iden.substring( 10, 12 );
                    progcode = iden.substring( 9, 10 );
                    orgunitcode = iden.substring( 0, 9 );
                }
            }
            else
            {
                systemIdentifier = identifier.getIdentifier();
            }
        }

        for ( PatientAttribute patientAttribute : patient.getAttributes() )
        {
            patientAttributeValueMap.put( patientAttribute.getId(), PatientAttributeValue.UNKNOWN );
        }

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
            .getPatientAttributeValues( patient );

        for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
        {
            if ( PatientAttribute.TYPE_COMBO.equalsIgnoreCase( patientAttributeValue.getPatientAttribute()
                .getValueType() ) )
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getPatientAttributeOption().getName() );
            }
            else
            {
                patientAttributeValueMap.put( patientAttributeValue.getPatientAttribute().getId(),
                    patientAttributeValue.getValue() );
            }
        }

        programs = programService.getAllPrograms();

        noGroupAttributes = patientAttributeService.getPatientAttributesNotGroup();

        attributeGroups = patientAttributeGroupService.getAllPatientAttributeGroups();

        return SUCCESS;

    }

    // -----------------------------------------------------------------------------
    // Getter / Setter
    // -----------------------------------------------------------------------------

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public void setPatientAttributeGroupService( PatientAttributeGroupService patientAttributeGroupService )
    {
        this.patientAttributeGroupService = patientAttributeGroupService;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Patient getPatient()
    {
        return patient;
    }

    public PatientIdentifier getPatientIdentifier()
    {
        return patientIdentifier;
    }

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    public Map<Integer, String> getPatientAttributeValueMap()
    {
        return patientAttributeValueMap;
    }

    public Collection<PatientAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }

    public Collection<PatientAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    public Map<Integer, String> getIdentiferMap()
    {
        return identiferMap;
    }

    public String getChildContactName()
    {
        return childContactName;
    }

    public String getChildContactType()
    {
        return childContactType;
    }

    public String getSystemIdentifier()
    {
        return systemIdentifier;
    }

    public String getBenicode()
    {
        return benicode;
    }

    public String getOrgunitcode()
    {
        return orgunitcode;
    }

    public String getProgcode()
    {
        return progcode;
    }

    public String getYearcode()
    {
        return yearcode;
    }

}
