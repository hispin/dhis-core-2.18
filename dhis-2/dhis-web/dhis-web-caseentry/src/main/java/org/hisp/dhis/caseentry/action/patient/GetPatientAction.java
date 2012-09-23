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
package org.hisp.dhis.caseentry.action.patient;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientAttribute;
import org.hisp.dhis.patient.PatientAttributeGroup;
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
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class GetPatientAction
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

    private PatientIdentifierTypeService patientIdentifierTypeService;

    private RelationshipService relationshipService;

    private RelationshipTypeService relationshipTypeService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Collection<RelationshipType> relationshipTypes;

    private int id;

    private Patient patient;

    private PatientIdentifier patientIdentifier;

    private Collection<Program> programs;

    private Map<Integer, String> patientAttributeValueMap = new HashMap<Integer, String>();

    private Collection<PatientAttribute> noGroupAttributes = new HashSet<PatientAttribute>();

    private List<PatientAttributeGroup> attributeGroups;

    private Collection<PatientIdentifierType> identifierTypes;

    private Map<Integer, String> identiferMap;

    private String childContactName;

    private String childContactType;

    private String systemIdentifier;

    private Relationship relationship;

    private Map<PatientAttributeGroup, Collection<PatientAttribute>> attributeGroupsMap = new HashMap<PatientAttributeGroup, Collection<PatientAttribute>>();

    private Collection<User> healthWorkers;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        relationshipTypes = relationshipTypeService.getAllRelationshipTypes();

        // -------------------------------------------------------------------------
        // Get identifier-types && attributes
        // -------------------------------------------------------------------------

        patient = patientService.getPatient( id );

        programs = programService.getAllPrograms();

        // -------------------------------------------------------------------------
        // Get identifier-types && attributes
        // -------------------------------------------------------------------------

        patientIdentifier = patientIdentifierService.getPatientIdentifier( patient );

        identifierTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();
        Collection<PatientAttribute> patientAttributes = patientAttributeService.getAllPatientAttributes();

        Collection<Program> programs = programService.getAllPrograms();
        for ( Program program : programs )
        {
            identifierTypes.removeAll( program.getPatientIdentifierTypes() );
            patientAttributes.removeAll( program.getPatientAttributes() );
        }

        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            PatientAttributeGroup attributeGroup = patientAttribute.getPatientAttributeGroup();
            if ( attributeGroup != null )
            {
                if ( attributeGroupsMap.containsKey( attributeGroup ) )
                {
                    Collection<PatientAttribute> attributes = attributeGroupsMap.get( attributeGroup );
                    attributes.add( patientAttribute );
                }
                else
                {
                    Collection<PatientAttribute> attributes = new HashSet<PatientAttribute>();
                    attributes.add( patientAttribute );
                    attributeGroupsMap.put( attributeGroup, attributes );
                }
            }
            else
            {
                noGroupAttributes.add( patientAttribute );
            }
        }

        // -------------------------------------------------------------------------
        // Get data
        // -------------------------------------------------------------------------

        identiferMap = new HashMap<Integer, String>();

        PatientIdentifierType idType = null;
        Patient representative = patient.getRepresentative();
        relationship = relationshipService.getRelationship( representative, patient );

        if ( patient.isUnderAge() && representative != null )
        {
            for ( PatientIdentifier representativeIdentifier : representative.getIdentifiers() )
            {
                if ( representativeIdentifier.getIdentifierType() != null
                    && representativeIdentifier.getIdentifierType().isRelated() )
                {
                    identiferMap.put( representativeIdentifier.getIdentifierType().getId(),
                        representativeIdentifier.getIdentifier() );
                }
            }
        }

        for ( PatientIdentifier identifier : patient.getIdentifiers() )
        {
            idType = identifier.getIdentifierType();

            if ( idType != null )
            {
                identiferMap.put( identifier.getIdentifierType().getId(), identifier.getIdentifier() );
            }
            else
            {
                systemIdentifier = identifier.getIdentifier();
            }
        }

        // -------------------------------------------------------------------------
        // Get patient-attribute values
        // -------------------------------------------------------------------------

        for ( PatientAttribute patientAttribute : patientAttributes )
        {
            PatientAttributeGroup attributeGroup = patientAttribute.getPatientAttributeGroup();
            if ( attributeGroup != null )
            {
                if ( attributeGroupsMap.containsKey( attributeGroup ) )
                {
                    Collection<PatientAttribute> attributes = attributeGroupsMap.get( attributeGroup );
                    attributes.add( patientAttribute );
                }
                else
                {
                    Collection<PatientAttribute> attributes = new HashSet<PatientAttribute>();
                    attributes.add( patientAttribute );
                    attributeGroupsMap.put( attributeGroup, attributes );
                }
            }
            else
            {
                noGroupAttributes.add( patientAttribute );
            }
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

        healthWorkers = patient.getOrganisationUnit().getUsers();

        return SUCCESS;

    }

    // -----------------------------------------------------------------------------
    // Getter / Setter
    // -----------------------------------------------------------------------------

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    public Collection<RelationshipType> getRelationshipTypes()
    {
        return relationshipTypes;
    }

    public Collection<User> getHealthWorkers()
    {
        return healthWorkers;
    }

    public Map<PatientAttributeGroup, Collection<PatientAttribute>> getAttributeGroupsMap()
    {
        return attributeGroupsMap;
    }

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    public Relationship getRelationship()
    {
        return relationship;
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

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
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

    public List<PatientAttributeGroup> getAttributeGroups()
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

}
