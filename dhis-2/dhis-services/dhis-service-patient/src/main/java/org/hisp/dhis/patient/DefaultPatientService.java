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

package org.hisp.dhis.patient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patientattributevalue.PatientAttributeValue;
import org.hisp.dhis.patientattributevalue.PatientAttributeValueService;
import org.hisp.dhis.relationship.Relationship;
import org.hisp.dhis.relationship.RelationshipService;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
@Transactional
public class DefaultPatientService
    implements PatientService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PatientStore patientStore;

    public void setPatientStore( PatientStore patientStore )
    {
        this.patientStore = patientStore;
    }

    private PatientIdentifierService patientIdentifierService;

    public void setPatientIdentifierService( PatientIdentifierService patientIdentifierService )
    {
        this.patientIdentifierService = patientIdentifierService;
    }

    private PatientAttributeValueService patientAttributeValueService;

    public void setPatientAttributeValueService( PatientAttributeValueService patientAttributeValueService )
    {
        this.patientAttributeValueService = patientAttributeValueService;
    }

    private PatientAttributeService patientAttributeService;

    public void setPatientAttributeService( PatientAttributeService patientAttributeService )
    {
        this.patientAttributeService = patientAttributeService;
    }

    private PatientIdentifierTypeService patientIdentifierTypeService;

    public void setPatientIdentifierTypeService( PatientIdentifierTypeService patientIdentifierTypeService )
    {
        this.patientIdentifierTypeService = patientIdentifierTypeService;
    }

    private RelationshipService relationshipService;

    public void setRelationshipService( RelationshipService relationshipService )
    {
        this.relationshipService = relationshipService;
    }

    private RelationshipTypeService relationshipTypeService;

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    // -------------------------------------------------------------------------
    // Patient
    // -------------------------------------------------------------------------

    public int savePatient( Patient patient )
    {
        return patientStore.save( patient );
    }

    public void deletePatient( Patient patient )
    {
        patientStore.delete( patient );
    }

    public Collection<Patient> getAllPatients()
    {
        return patientStore.getAll();
    }

    public Collection<Patient> getPatiensByGender( String gender )
    {
        return patientStore.getByGender( gender );
    }

    public Patient getPatient( int id )
    {
        return patientStore.get( id );
    }

    public Collection<Patient> getPatientsByBirthDate( Date birthDate )
    {
        return patientStore.getByBirthDate( birthDate );
    }

    public Collection<Patient> getPatientsByNames( String name )
    {
        return patientStore.getByNames( name );
    }

    public void updatePatient( Patient patient )
    {
        patientStore.update( patient );
    }

    public Collection<Patient> getAllPatients( Boolean isDead )
    {
        return patientStore.get( isDead );
    }

    public Collection<Patient> getPatients( String searchText )
    {
        Set<Patient> patients = new HashSet<Patient>();

        patients.addAll( getPatientsByNames( searchText ) );

        for ( PatientIdentifier patientIdentifier : patientIdentifierService
            .getPatientIdentifiersByIdentifier( searchText ) )
        {
            patients.add( patientIdentifier.getPatient() );
        }

        return patients;
    }

    public Collection<Patient> getPatients( OrganisationUnit organisationUnit, String searchText, int min, int max )
    {
        Collection<Patient> patients = new ArrayList<Patient>();

        Collection<Patient> allPatients = getPatients( searchText );

        if ( allPatients.retainAll( getPatientsByOrgUnit( organisationUnit, min, max ) ) )
        {
            patients = allPatients;
        }

        return patients;
    }

    public Collection<Patient> getPatientsByOrgUnit( OrganisationUnit organisationUnit, int min, int max )
    {
        // List<Patient> patientList = (ArrayList<Patient>)
        // patientIdentifierService.listPatientByOrganisationUnit(
        // organisationUnit );
        return patientIdentifierService.listPatientByOrganisationUnit( organisationUnit, min, max );
    }

    public Collection<Patient> getPatientsByOrgUnitAttr( OrganisationUnit organisationUnit, int min, int max,
        PatientAttribute patientAttribute )
    {
        List<Patient> patientList = (ArrayList<Patient>) patientIdentifierService
            .listPatientByOrganisationUnit( organisationUnit );
        if ( patientAttribute != null )
        {
            List<Patient> sortedPatientList = (ArrayList<Patient>) sortPatientsByAttribute( patientList,
                patientAttribute );
            return sortedPatientList.subList( min, max );
        }
        return patientList.subList( min, max );
    }

    public Collection<Patient> sortPatientsByAttribute( Collection<Patient> patients, PatientAttribute patientAttribute )
    {
        SortedMap<String, Patient> patientsSortedByAttribute = new TreeMap<String, Patient>();

        Collection<Patient> sortedPatients = new ArrayList<Patient>();

        // ---------------------------------------------------------------------
        // Better to fetch all attribute values at once than fetching the
        // required attribute value of each patient using loop
        // ---------------------------------------------------------------------

        Collection<PatientAttributeValue> patientAttributeValues = patientAttributeValueService
            .getPatientAttributeValues( patients );

        if ( patientAttributeValues != null )
        {
            for ( PatientAttributeValue patientAttributeValue : patientAttributeValues )
            {
                if ( patientAttribute == patientAttributeValue.getPatientAttribute() )
                {
                    patientsSortedByAttribute.put( patientAttributeValue.getValue() + "-"
                        + patientAttributeValue.getPatient().getFullName() + "-"
                        + patientAttributeValue.getPatient().getId(), patientAttributeValue.getPatient() );
                }
            }
        }

        // ---------------------------------------------------------------------
        // Make sure all patients are in the sorted list - because all
        // patients might not have the sorting attribute/value
        // ---------------------------------------------------------------------

        for ( Patient patient : patientsSortedByAttribute.values() )
        {
            sortedPatients.add( patient );
        }

        for ( Patient patient : patients )
        {
            if ( !sortedPatients.contains( patient ) )
            {
                sortedPatients.add( patient );
            }
        }

        return sortedPatients;
    }

    public Collection<Patient> getPatient( String firstName, String middleName, String lastName, Date birthdate,
        String gender )
    {
        return patientStore.getPatient( firstName, middleName, lastName, birthdate, gender );
    }

    public Collection<Patient> searchPatient( Integer identifierTypeId, Integer attributeId, String value )
    {
        if ( attributeId != null )
        {
            PatientAttribute attribute = patientAttributeService.getPatientAttribute( attributeId );
            if ( attribute != null )
            {
                return patientAttributeValueService.getPatient( attribute, value );
            }
        }
        else if ( identifierTypeId != null )
        {
            PatientIdentifierType idenType = patientIdentifierTypeService.getPatientIdentifierType( identifierTypeId );
            if ( idenType != null )
            {
                Patient p = patientIdentifierService.getPatient( idenType, value );
                if ( p != null )
                {
                    Set<Patient> set = new HashSet<Patient>();
                    set.add( p );
                    return set;
                }
            }
        }
        else
        {
            return patientStore.getByNames( value );
        }
        return null;
    }

    public int countGetPatientsByOrgUnit( OrganisationUnit organisationUnit )
    {
        return patientIdentifierService.countListPatientByOrganisationUnit( organisationUnit );
    }

    public Collection<Patient> getPatientsByOrgUnit( OrganisationUnit organisationUnit )
    {
        return patientIdentifierService.listPatientByOrganisationUnit( organisationUnit );
    }

    public int countGetPatients( String searchText )
    {
        return patientStore.countGetPatientsByNames( searchText )
            + patientIdentifierService.countGetPatientsByIdentifier( searchText );
    }

    public Collection<Patient> getPatients( String searchText, int min, int max )
    {
        int countPatientName = patientStore.countGetPatientsByNames( searchText );

        Set<Patient> patients = new HashSet<Patient>();

        if ( max < countPatientName )
        {
            patients.addAll( getPatientsByNames( searchText, min, max ) );

            min = min - patients.size();
        }
        else
        {
            if ( min <= countPatientName )
            {
                patients.addAll( getPatientsByNames( searchText, min, countPatientName ) );

                min = 0;
                max = max - countPatientName;

                Collection<Patient> patientsByIdentifier = patientIdentifierService.getPatientsByIdentifier(
                    searchText, min, max );

                patients.addAll( patientsByIdentifier );
            }
            else
            {
                min = 0;
                max = max - countPatientName;

                Collection<Patient> patientsByIdentifier = patientIdentifierService.getPatientsByIdentifier(
                    searchText, min, max );

                patients.addAll( patientsByIdentifier );
            }
        }
        return patients;
    }

    public int countnGetPatientsByNames( String name )
    {
        return patientStore.countGetPatientsByNames( name );
    }

    public Collection<Patient> getPatientsByNames( String name, int min, int max )
    {
        return patientStore.getPatientsByNames( name, min, max );
    }

    public int createPatient( Patient patient, OrganisationUnit orgUnit, Integer representativeId,
        Integer relationshipTypeId, List<PatientAttributeValue> patientAttributeValues )
    {

        int patientid = savePatient( patient );

        for ( PatientAttributeValue pav : patientAttributeValues )
        {
            patientAttributeValueService.savePatientAttributeValue( pav );
        }
        // -------------------------------------------------------------------------
        // If underAge = true : save representative information.
        // -------------------------------------------------------------------------

        if ( patient.isUnderAge() )
        {
            if ( representativeId != null )
            {
                Patient representative = patientStore.get( representativeId );
                if ( representative != null )
                {
                    patient.setRepresentative( representative );

                    Relationship rel = new Relationship();
                    rel.setPatientA( representative );
                    rel.setPatientB( patient );

                    if ( relationshipTypeId != null )
                    {
                        RelationshipType relType = relationshipTypeService.getRelationshipType( relationshipTypeId );
                        if ( relType != null )
                        {
                            rel.setRelationshipType( relType );
                            relationshipService.saveRelationship( rel );
                        }
                    }
                }
            }
        }

        return patientid;

    }

    public void updatePatient( Patient patient, OrganisationUnit orgUnit, Integer representativeId,
        Integer relationshipTypeId, List<PatientAttributeValue> valuesForSave,
        List<PatientAttributeValue> valuesForUpdate, Collection<PatientAttributeValue> valuesForDelete )
    {

        patientStore.update( patient );

        for ( PatientAttributeValue av : valuesForSave )
        {
            patientAttributeValueService.savePatientAttributeValue( av );
        }

        for ( PatientAttributeValue av : valuesForUpdate )
        {
            patientAttributeValueService.updatePatientAttributeValue( av );
        }

        for ( PatientAttributeValue av : valuesForDelete )
        {
            patientAttributeValueService.deletePatientAttributeValue( av );
        }

        // -------------------------------------------------------------------------
        // If underAge = true : save representative information.
        // -------------------------------------------------------------------------

        if ( patient.isUnderAge() )
        {

            if ( representativeId != null )
            {
                if ( patient.getRepresentative() == null || patient.getRepresentative().getId() != representativeId )
                {
                    Patient representative = patientStore.get( representativeId );

                    if ( representative != null )
                    {
                        patient.setRepresentative( representative );

                        Relationship rel = new Relationship();
                        rel.setPatientA( representative );
                        rel.setPatientB( patient );

                        if ( relationshipTypeId != null )
                        {
                            RelationshipType relType = relationshipTypeService.getRelationshipType( relationshipTypeId );
                            if ( relType != null )
                            {
                                rel.setRelationshipType( relType );
                                relationshipService.saveRelationship( rel );
                            }
                        }
                    }
                }
            }
        }
    }

}
