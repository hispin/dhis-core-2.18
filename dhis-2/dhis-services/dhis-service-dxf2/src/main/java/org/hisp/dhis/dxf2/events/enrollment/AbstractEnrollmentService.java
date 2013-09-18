package org.hisp.dhis.dxf2.events.enrollment;

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

import org.hisp.dhis.dxf2.events.person.Person;
import org.hisp.dhis.dxf2.events.person.PersonService;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.i18n.I18nManagerException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class AbstractEnrollmentService implements EnrollmentService
{
    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private PersonService personService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private I18nManager i18nManager;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    public Enrollments getEnrollments()
    {
        List<Program> programs = getProgramsWithRegistration();

        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>( programInstanceService.getProgramInstances( programs ) );
        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( Person person )
    {
        Patient patient = getPatient( person.getPerson() );
        return getEnrollments( patient );
    }

    @Override
    public Enrollments getEnrollments( Patient patient )
    {
        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>( programInstanceService.getProgramInstances( patient ) );
        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( Program program )
    {
        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>( programInstanceService.getProgramInstances( program ) );
        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( OrganisationUnit organisationUnit )
    {
        List<Program> programs = getProgramsWithRegistration();

        List<ProgramInstance> programInstances = new ArrayList<ProgramInstance>( programInstanceService.getProgramInstances( programs, organisationUnit ) );

        return getEnrollments( programInstances );
    }

    @Override
    public Enrollments getEnrollments( Program program, OrganisationUnit organisationUnit )
    {
        return getEnrollments( programInstanceService.getProgramInstances( program, organisationUnit ) );
    }

    @Override
    public Enrollments getEnrollments( Program program, Person person )
    {
        Patient patient = getPatient( person.getPerson() );
        return getEnrollments( programInstanceService.getProgramInstances( patient, program ) );
    }

    @Override
    public Enrollments getEnrollments( Collection<ProgramInstance> programInstances )
    {
        Enrollments enrollments = new Enrollments();

        for ( ProgramInstance programInstance : programInstances )
        {
            enrollments.getEnrollments().add( getEnrollment( programInstance ) );
        }

        return enrollments;
    }

    @Override
    public Enrollment getEnrollment( String id )
    {
        return getEnrollment( programInstanceService.getProgramInstance( id ) );
    }

    @Override
    public Enrollment getEnrollment( ProgramInstance programInstance )
    {
        Enrollment enrollment = new Enrollment();

        enrollment.setEnrollment( programInstance.getUid() );
        enrollment.setPerson( programInstance.getPatient().getUid() );
        enrollment.setProgram( programInstance.getProgram().getUid() );
        enrollment.setStatus( EnrollmentStatus.fromInt( programInstance.getStatus() ) );
        enrollment.setDateOfEnrollment( programInstance.getEnrollmentDate() );
        enrollment.setDateOfIncident( programInstance.getDateOfIncident() );

        return enrollment;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary saveEnrollment( Enrollment enrollment )
    {
        try
        {
            format = i18nManager.getI18nFormat();
        }
        catch ( I18nManagerException ex )
        {
            return new ImportSummary( ImportStatus.ERROR, ex.getMessage() );
        }

        Patient patient = getPatient( enrollment.getPerson() );
        Program program = getProgram( enrollment.getProgram() );

        ProgramInstance programInstance = programInstanceService.enrollPatient( patient, program, enrollment.getDateOfEnrollment(), enrollment.getDateOfIncident(),
            patient.getOrganisationUnit(), format );

        if ( programInstance == null )
        {
            return new ImportSummary( ImportStatus.ERROR, "Could not enroll person " + enrollment.getPerson()
                + " into program " + enrollment.getProgram() );
        }

        ImportSummary importSummary = new ImportSummary( ImportStatus.SUCCESS );
        importSummary.setReference( programInstance.getUid() );
        importSummary.setDataValueCount( null );
        importSummary.getImportCount().incrementImported();

        return importSummary;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummary updateEnrollment( Enrollment enrollment )
    {
        return null;
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deleteEnrollment( Enrollment enrollment )
    {
    }

    @Override
    public void cancelEnrollment( Enrollment enrollment )
    {
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private List<Program> getProgramsWithRegistration()
    {
        List<Program> programs = new ArrayList<Program>();
        programs.addAll( programService.getPrograms( Program.SINGLE_EVENT_WITH_REGISTRATION ) );
        programs.addAll( programService.getPrograms( Program.MULTIPLE_EVENTS_WITH_REGISTRATION ) );

        return programs;
    }

    private Patient getPatient( String person )
    {
        Patient patient = patientService.getPatient( person );

        if ( patient == null )
        {
            throw new IllegalArgumentException( "Person does not exist." );
        }

        return patient;
    }

    private Program getProgram( String id )
    {
        Program program = programService.getProgram( id );

        if ( program == null )
        {
            throw new IllegalArgumentException( "Program does not exist." );
        }

        return program;
    }

    private OrganisationUnit getOrganisationUnit( String id )
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( id );

        if ( organisationUnit == null )
        {
            throw new IllegalArgumentException( "OrganisationUnit does not exist." );
        }

        return organisationUnit;
    }

}