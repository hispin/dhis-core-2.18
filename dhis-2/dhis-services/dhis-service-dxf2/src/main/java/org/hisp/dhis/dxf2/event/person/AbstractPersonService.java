package org.hisp.dhis.dxf2.event.person;

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

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientIdentifier;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hisp.dhis.system.util.TextUtils.nullIfEmpty;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Transactional
public abstract class AbstractPersonService implements PersonService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private PatientService patientService;

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    public Persons getPersons()
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getAllPatients() );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( OrganisationUnit organisationUnit )
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit ) );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( Gender gender )
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getPatiensByGender( gender.getValue() ) );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( Program program )
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getPatients( program ) );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( Program program, Gender gender )
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getPatients( program, gender.getValue() ) );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( OrganisationUnit organisationUnit, Program program )
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, program ) );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( OrganisationUnit organisationUnit, Gender gender )
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, gender.getValue() ) );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( OrganisationUnit organisationUnit, Program program, Gender gender )
    {
        List<Patient> patients = new ArrayList<Patient>( patientService.getPatients( organisationUnit, program, gender.getValue() ) );
        return getPersons( patients );
    }

    @Override
    public Persons getPersons( Collection<Patient> patients )
    {
        Persons persons = new Persons();

        for ( Patient patient : patients )
        {
            persons.getPersons().add( getPerson( patient ) );
        }

        return persons;
    }

    @Override
    public Person getPerson( String uid )
    {
        return getPerson( patientService.getPatient( uid ) );
    }

    @Override
    public Person getPerson( Patient patient )
    {
        Person person = new Person();
        person.setPerson( patient.getUid() );
        person.setOrgUnit( patient.getOrganisationUnit().getUid() );

        person.setName( patient.getName() );
        person.setGender( Gender.fromString( patient.getGender() ) );

        person.setDeceased( patient.getIsDead() );
        person.setDateOfDeath( patient.getDeathDate() );

        Contact contact = new Contact();
        contact.setPhoneNumber( nullIfEmpty( patient.getPhoneNumber() ) );

        if ( contact.getPhoneNumber() != null )
        {
            person.setContact( contact );
        }

        DateOfBirth dateOfBirth;

        if ( patient.getDobType().equals( Patient.DOB_TYPE_VERIFIED ) || patient.getDobType().equals( Patient.DOB_TYPE_DECLARED ) )
        {
            dateOfBirth = new DateOfBirth( patient.getBirthDate(),
                DateOfBirthType.fromString( String.valueOf( patient.getDobType() ) ) );
        }
        else
        {
            // assume APPROXIMATE
            dateOfBirth = new DateOfBirth( patient.getIntegerValueOfAge() );
        }


        person.setDateOfBirth( dateOfBirth );
        person.setDateOfRegistration( patient.getRegistrationDate() );

        for ( PatientIdentifier patientIdentifier : patient.getIdentifiers() )
        {
            String identifierType = patientIdentifier.getIdentifierType() == null ? null : patientIdentifier.getIdentifierType().getUid();

            Identifier identifier = new Identifier( identifierType, patientIdentifier.getIdentifier() );
            person.getIdentifiers().add( identifier );
        }

        return person;
    }

    public Patient getPatient( Person person )
    {
        Patient patient = new Patient();


        return patient;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public void savePerson( Person person )
    {
        System.err.println( "SAVE: " + person );
        Patient patient = getPatient( person );
    }

    @Override
    public void updatePerson( Person person )
    {
        System.err.println( "UPDATE: " + person );
        Patient patient = getPatient( person );
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deletePerson( Person person )
    {
        System.err.println( "DELETE:" + person );
        Patient patient = patientService.getPatient( person.getPerson() );

        if ( patient != null )
        {
            patientService.deletePatient( patient );
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}
