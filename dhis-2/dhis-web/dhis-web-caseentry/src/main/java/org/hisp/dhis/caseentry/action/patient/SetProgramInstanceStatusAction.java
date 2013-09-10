package org.hisp.dhis.caseentry.action.patient;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.sms.outbound.OutboundSms;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SetProgramInstanceStatusAction
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

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programInstanceId;

    private Collection<Program> programs = new ArrayList<Program>();

    private int status;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public Collection<Program> getPrograms()
    {
        return programs;
    }

    public void setProgramInstanceId( Integer programInstanceId )
    {
        this.programInstanceId = programInstanceId;
    }

    public void setStatus( int status )
    {
        this.status = status;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        ProgramInstance programInstance = programInstanceService.getProgramInstance( programInstanceId );

        Patient patient = programInstance.getPatient();

        Program program = programInstance.getProgram();
        programInstance.setStatus( status );

        if ( status == ProgramInstance.STATUS_COMPLETED )
        {
            // ---------------------------------------------------------------------
            // Send sms-message when to completed the program
            // ---------------------------------------------------------------------

            List<OutboundSms> piOutboundSms = programInstance.getOutboundSms();
            if ( piOutboundSms == null )
            {
                piOutboundSms = new ArrayList<OutboundSms>();
            }

            piOutboundSms.addAll( programInstanceService.sendMessages( programInstance,
                PatientReminder.SEND_WHEN_TO_C0MPLETED_PROGRAM, format ) );

            // -----------------------------------------------------------------
            // Send DHIS message when to completed the program
            // -----------------------------------------------------------------

            List<MessageConversation> piMessageConversations = programInstance.getMessageConversations();
            if ( piMessageConversations == null )
            {
                piMessageConversations = new ArrayList<MessageConversation>();
            }

            piMessageConversations.addAll( programInstanceService.sendMessageConversations( programInstance,
                PatientReminder.SEND_WHEN_TO_C0MPLETED_PROGRAM, format ) );

            programInstance.setEndDate( new Date() );
            if ( !program.getOnlyEnrollOnce() )
            {
                patient.getPrograms().remove( program );
                patientService.updatePatient( patient );
            }
        }

        else if ( status == ProgramInstance.STATUS_CANCELLED )
        {
            Calendar today = Calendar.getInstance();
            PeriodType.clearTimeOfDay( today );
            Date currentDate = today.getTime();

            programInstance.setEndDate( currentDate );

            for ( ProgramStageInstance programStageInstance : programInstance.getProgramStageInstances() )
            {
                if ( programStageInstance.getExecutionDate() == null )
                {
                    // Set status as skipped for overdue events
                    if ( programStageInstance.getDueDate().before( currentDate ) )
                    {
                        programStageInstance.setStatus( ProgramStageInstance.SKIPPED_STATUS );
                        programStageInstanceService.updateProgramStageInstance( programStageInstance );
                    }
                    // Delete scheduled events
                    else
                    {
                        programStageInstanceService.deleteProgramStageInstance( programStageInstance );
                    }
                }
            }
            patient.getPrograms().remove( program );
            patientService.updatePatient( patient );
        }
        else
        {
            programInstance.setEndDate( null );

            patient.getPrograms().add( program );
            patientService.updatePatient( patient );
        }

        programInstanceService.updateProgramInstance( programInstance );

        return SUCCESS;
    }

}
