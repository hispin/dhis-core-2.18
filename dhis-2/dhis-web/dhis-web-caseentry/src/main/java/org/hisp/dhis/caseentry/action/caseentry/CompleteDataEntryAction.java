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
package org.hisp.dhis.caseentry.action.caseentry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Viet Nguyen
 */
public class CompleteDataEntryAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    private ProgramInstanceService programInstanceService;

    public void setProgramInstanceService( ProgramInstanceService programInstanceService )
    {
        this.programInstanceService = programInstanceService;
    }

    private PatientService patientService;

    public void setPatientService( PatientService patientService )
    {
        this.patientService = patientService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private OutboundSmsService outboundSmsService;

    public void setOutboundSmsService( OutboundSmsService outboundSmsService )
    {
        this.outboundSmsService = outboundSmsService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private Integer programStageId;

    public Integer getProgramStageId()
    {
        return programStageId;
    }

    public void setProgramStageId( Integer programStageId )
    {
        this.programStageId = programStageId;
    }

    public Integer programStageInstanceId;

    public Integer getProgramStageInstanceId()
    {
        return programStageInstanceId;
    }

    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( programStageInstanceId == null )
        {
            return INPUT;
        }

        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance( programStageInstanceId );

        programStageInstance.setCompleted( true );

        Calendar today = Calendar.getInstance();
        PeriodType.clearTimeOfDay( today );
        Date date = today.getTime();

        programStageInstance.setCompletedDate( date );
        programStageInstance.setCompletedUser( currentUserService.getCurrentUsername() );

        // Send message when to completed the event

        sendSMSToCompletedEvent( programStageInstance );

        programStageInstanceService.updateProgramStageInstance( programStageInstance );

        // ---------------------------------------------------------------------
        // Check Completed status for all of ProgramStageInstance of
        // ProgramInstance
        // ---------------------------------------------------------------------

        if ( !programStageInstance.getProgramInstance().getProgram().getType()
            .equals( Program.SINGLE_EVENT_WITHOUT_REGISTRATION ) )
        {
            ProgramInstance programInstance = programStageInstance.getProgramInstance();

            Set<ProgramStageInstance> stageInstances = programInstance.getProgramStageInstances();

            for ( ProgramStageInstance stageInstance : stageInstances )
            {
                if ( !stageInstance.isCompleted() || stageInstance.getProgramStage().getIrregular() )
                {
                    return SUCCESS;
                }
            }

            programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
            programInstance.setEndDate( new Date() );
            sendSMSToCompletedProgram( programInstance );

            programInstanceService.updateProgramInstance( programInstance );

            Program program = programInstance.getProgram();
            if ( !program.getOnlyEnrollOnce() )
            {
                Patient patient = programInstance.getPatient();
                patient.getPrograms().remove( program );
                patientService.updatePatient( patient );
            }
        }

        return "programcompleted";
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void sendSMSToCompletedEvent( ProgramStageInstance programStageInstance )
    {
        Patient patient = programStageInstance.getProgramInstance().getPatient();

        if ( patient != null )
        {
            Collection<PatientReminder> reminders = programStageInstance.getProgramStage().getPatientReminders();
            for ( PatientReminder rm : reminders )
            {
                if ( rm != null && rm.getWhenToSend() != null && rm.getWhenToSend() == PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT )
                {
                    sendEventMessage( rm, programStageInstance, patient );
                }
            }
        }
    }

    private void sendEventMessage( PatientReminder reminder, ProgramStageInstance programStageInstance, Patient patient )
    {
        Set<String> phoneNumbers = getPhonenumbers( reminder, patient );

        if ( phoneNumbers.size() > 0 )
        {
            String msg = getStringMsgFromTemplateMsg( reminder, programStageInstance, patient );
            try
            {
                OutboundSms outboundSms = new OutboundSms();
                outboundSms.setMessage( msg );
                outboundSms.setRecipients( phoneNumbers );
                outboundSms.setSender( currentUserService.getCurrentUsername() );
                outboundSmsService.sendMessage( outboundSms, null );

                List<OutboundSms> outboundSmsList = programStageInstance.getOutboundSms();
                if ( outboundSmsList == null )
                {
                    outboundSmsList = new ArrayList<OutboundSms>();
                }
                outboundSmsList.add( outboundSms );
                programStageInstance.setOutboundSms( outboundSmsList );
            }
            catch ( SmsServiceException e )
            {
                e.printStackTrace();
            }
        }
        // send to user group
        else
        {
            String msg = getStringMsgFromTemplateMsg( reminder, programStageInstance, patient );
            String programStageName = programStageInstance.getProgramStage().getName();
            messageService.sendMessage( programStageName, msg, null, reminder.getUserGroup().getMembers(), false, true );
        }
    }

    private void sendSMSToCompletedProgram( ProgramInstance programInstance )
    {
        Patient patient = programInstance.getPatient();

        if ( patient != null )
        {
            Collection<PatientReminder> reminders = programInstance.getProgram().getPatientReminders();
            for ( PatientReminder rm : reminders )
            {
                if ( rm != null && rm.getWhenToSend() == PatientReminder.SEND_WHEN_TO_C0MPLETED_PROGRAM )
                {
                    sendProgramMessage( rm, programInstance, patient );
                }
            }
        }
    }

    private void sendProgramMessage( PatientReminder reminder, ProgramInstance programInstance, Patient patient )
    {
        Set<String> phoneNumbers = getPhonenumbers( reminder, patient );

        if ( phoneNumbers.size() > 0 )
        {
            String msg = getStringMsgFromTemplateMsg( reminder, programInstance, patient );

            try
            {
                OutboundSms outboundSms = new OutboundSms();
                outboundSms.setMessage( msg );
                outboundSms.setRecipients( phoneNumbers );
                outboundSms.setSender( currentUserService.getCurrentUsername() );
                outboundSmsService.sendMessage( outboundSms, null );

                List<OutboundSms> outboundSmsList = programInstance.getOutboundSms();
                if ( outboundSmsList == null )
                {
                    outboundSmsList = new ArrayList<OutboundSms>();
                }
                outboundSmsList.add( outboundSms );
                programInstance.setOutboundSms( outboundSmsList );
            }
            catch ( SmsServiceException e )
            {
                e.printStackTrace();
            }
        }
        else
        {
            String msg = getStringMsgFromTemplateMsg( reminder, programInstance, patient );
            String programName = programInstance.getProgram().getName();
            messageService.sendMessage( programName, msg, null, reminder.getUserGroup().getMembers(), false, true );
        }
    }

    private Set<String> getPhonenumbers( PatientReminder reminder, Patient patient )
    {
        Set<String> phoneNumbers = new HashSet<String>();

        switch ( reminder.getSendTo() )
        {
        case PatientReminder.SEND_TO_ALL_USERS_IN_ORGUGNIT_REGISTERED:
            Collection<User> users = patient.getOrganisationUnit().getUsers();
            for ( User user : users )
            {
                if ( user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() )
                {
                    phoneNumbers.add( user.getPhoneNumber() );
                }
            }
            break;
        case PatientReminder.SEND_TO_HEALTH_WORKER:
            if ( patient.getHealthWorker() != null && patient.getHealthWorker().getPhoneNumber() != null )
            {
                phoneNumbers.add( patient.getHealthWorker().getPhoneNumber() );
            }
            break;
        case PatientReminder.SEND_TO_ORGUGNIT_REGISTERED:
            if ( patient.getOrganisationUnit().getPhoneNumber() != null
                && !patient.getOrganisationUnit().getPhoneNumber().isEmpty() )
            {
                phoneNumbers.add( patient.getOrganisationUnit().getPhoneNumber() );
            }
            break;
        case PatientReminder.SEND_TO_USER_GROUP:
            phoneNumbers.clear();
            break;
        default:
            if ( patient.getPhoneNumber() != null && !patient.getPhoneNumber().isEmpty() )
            {
                phoneNumbers.add( patient.getPhoneNumber() );
            }
            break;
        }
        return phoneNumbers;
    }

    public String getStringMsgFromTemplateMsg( PatientReminder reminder, ProgramStageInstance programStageInstance,
        Patient patient )
    {
        String msg = reminder.getTemplateMessage();

        String patientName = patient.getFirstName();
        String organisationunitName = patient.getOrganisationUnit().getName();
        String programName = programStageInstance.getProgramInstance().getProgram().getName();
        String programStageName = programStageInstance.getProgramStage().getName();
        String daysSinceDueDate = DateUtils.daysBetween( new Date(), programStageInstance.getDueDate() ) + "";
        String dueDate = format.formatDate( programStageInstance.getDueDate() );

        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_PATIENT_NAME, patientName );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_PROGRAM_NAME, programName );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_PROGAM_STAGE_NAME, programStageName );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_DUE_DATE, dueDate );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_ORGUNIT_NAME, organisationunitName );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_DAYS_SINCE_DUE_DATE, daysSinceDueDate );

        return msg;
    }

    public String getStringMsgFromTemplateMsg( PatientReminder reminder, ProgramInstance programInstance,
        Patient patient )
    {
        String msg = reminder.getTemplateMessage();

        String patientName = patient.getFirstName();
        String organisationunitName = patient.getOrganisationUnit().getName();
        String programName = programInstance.getProgram().getName();
        String daysSinceEnrollementDate = DateUtils.daysBetween( new Date(), programInstance.getEnrollmentDate() ) + "";
        String daysSinceIncidentDate = DateUtils.daysBetween( new Date(), programInstance.getDateOfIncident() ) + "";
        String incidentDate = format.formatDate( programInstance.getDateOfIncident() );
        String erollmentDate = format.formatDate( programInstance.getEnrollmentDate() );

        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_PATIENT_NAME, patientName );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_PROGRAM_NAME, programName );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_ORGUNIT_NAME, organisationunitName );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_INCIDENT_DATE, incidentDate );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_ENROLLMENT_DATE, erollmentDate );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_DAYS_SINCE_ENROLLMENT_DATE, daysSinceEnrollementDate );
        msg = msg.replace( PatientReminder.TEMPLATE_MESSSAGE_DAYS_SINCE_INCIDENT_DATE, daysSinceIncidentDate );

        return msg;
    }

}
