package org.hisp.dhis.dxf2.event;

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

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
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

    @Autowired
    private I18nManager i18nManager;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public Persons getPersons()
    {
        Persons persons = new Persons();

        // TODO replace with sql, will be very bad performance when identifiers, attributes etc are included
        List<Patient> patients = new ArrayList<Patient>( patientService.getAllPatients() );

        for ( Patient patient : patients )
        {
            persons.getPersons().add( getPerson( patient ) );
        }

        return persons;
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

        Name name = new Name(
            nullIfEmpty( patient.getFirstName() ),
            nullIfEmpty( patient.getMiddleName() ),
            nullIfEmpty( patient.getLastName() ) );

        person.setName( name );
        person.setGender( Gender.fromString( patient.getGender() ) );

        person.setDeceased( patient.getIsDead() );
        person.setDateOfDeath( patient.getDeathDate() );

        Contact contact = new Contact();
        contact.setPhoneNumber( nullIfEmpty( patient.getPhoneNumber() ) );

        if ( contact.getPhoneNumber() != null )
        {
            person.setContact( contact );
        }

        DateOfBirth dateOfBirth = new DateOfBirth( patient.getBirthDate(),
            DateOfBirthType.fromString( String.valueOf( patient.getDobType() ) ) );

        person.setDateOfBirth( dateOfBirth );
        person.setDateOfRegistration( patient.getRegistrationDate() );

        return person;
    }

    @Override
    public void updatePerson( Person person )
    {
    }

    @Override
    public void deletePerson( Person person )
    {
    }
}
