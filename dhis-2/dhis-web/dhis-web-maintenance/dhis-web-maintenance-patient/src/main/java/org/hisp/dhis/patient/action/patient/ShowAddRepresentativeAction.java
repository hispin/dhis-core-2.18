package org.hisp.dhis.patient.action.patient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeService;
import org.hisp.dhis.patient.PatientIdentifierType;
import org.hisp.dhis.patient.PatientIdentifierTypeService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;

import com.opensymphony.xwork2.Action;

public class ShowAddRepresentativeAction
    implements Action
{

    private static final String PARENTS = "Parent";

    private static final String GUARDIAN = "Guardian";

    private static final String CHILD = "Child";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private RelationshipTypeService relationshipTypeService;

    private PatientIdentifierTypeService patientIdentifierTypeService;

    private PatientAttributeService patientAttributeService;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<RelationshipType> relationshipTypes = new ArrayList<RelationshipType>();

    private Collection<PatientIdentifierType> identifierTypes;

    private Collection<PatientAttribute> attributes;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // //TODO Have to create identifier Group, it should base on age or
        // somewhat...
        // // Current ...hard code ...
        // RelationshipType r = relationshipTypeService.getRelationshipType(
        // PARENTS, CHILD );
        // relationshipTypes.add( r );
        // r = relationshipTypeService.getRelationshipType( GUARDIAN, CHILD );
        // relationshipTypes.add( r );

        relationshipTypes = new ArrayList<RelationshipType>( relationshipTypeService.getAllRelationshipTypes() );
        identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        attributes = patientAttributeService.getAllPatientAttributes();
        
        return SUCCESS;
    }

    // -----------------------------------------------------------------------------
    // Getter/Setter
    // -----------------------------------------------------------------------------

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    public List<RelationshipType> getRelationshipTypes()
    {
        return relationshipTypes;
    }

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    public Collection<PatientIdentifierType> getIdentifierTypes()
    {
        return identifierTypes;
    }

    public Collection<PatientAttribute> getAttributes()
    {
        return attributes;
    }

}
