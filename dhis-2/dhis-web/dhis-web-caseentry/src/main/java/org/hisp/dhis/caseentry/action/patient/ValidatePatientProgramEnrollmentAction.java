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

import java.util.Date;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.validation.ValidationCriteria;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class ValidatePatientProgramEnrollmentAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer patientId;

    public void setPatientId( Integer patientId )
    {
        this.patientId = patientId;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private String enrollmentDate;

    public void setEnrollmentDate( String enrollmentDate )
    {
        this.enrollmentDate = enrollmentDate;
    }

    private String dateOfIncident;

    public void setDateOfIncident( String dateOfIncident )
    {
        this.dateOfIncident = dateOfIncident;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Patient patient = patientService.getPatient( patientId );

        Program program = programService.getProgram( programId );

        ValidationCriteria criteria = program.isValid( patient );

        if ( criteria != null )
        {
            message = i18n.getString( "can_not_enroll_into_the_program_because" ) + " " + criteria.getName();
            return INPUT;
        }

        if ( enrollmentDate == null )
        {
            message = i18n.getString( "can_not_enrol_into_the_program_because" );

            return INPUT;
        }

        else
        {
            enrollmentDate = enrollmentDate.trim();

            if ( enrollmentDate.length() == 0 )
            {
                message = i18n.getString( "please_specify_enrollment_date" );

                return INPUT;
            }

            if ( enrollmentDate.length() != 0 )
            {
                Date DateOfEnrollment = format.parseDate( enrollmentDate );

                if ( DateOfEnrollment == null )
                {
                    message = i18n.getString( "please_specify_a_valid_enrollment_date" );

                    return INPUT;
                }
            }
        }

        if ( dateOfIncident == null )
        {
            message = i18n.getString( "please_specify_date_of_incident" );

            return INPUT;
        }

        else
        {
            dateOfIncident = dateOfIncident.trim();

            if ( dateOfIncident.length() == 0 )
            {
                message = i18n.getString( "please_specify_date_of_incident" );

                return INPUT;
            }

            if ( dateOfIncident.length() != 0 )
            {
                Date DateOfIncident = format.parseDate( dateOfIncident );

                if ( DateOfIncident == null )
                {
                    message = i18n.getString( "please_specify_a_valid_date_of_incident" );

                    return INPUT;
                }

                Date DateOfEnrollment = format.parseDate( enrollmentDate );

                if ( DateOfEnrollment.before( DateOfIncident ) )
                {
                    message = i18n.getString( "date_of_incident_invalid" );

                    return INPUT;
                }
            }
        }

        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }
}
