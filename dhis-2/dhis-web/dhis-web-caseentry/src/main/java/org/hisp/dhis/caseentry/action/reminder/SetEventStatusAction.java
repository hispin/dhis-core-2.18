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

package org.hisp.dhis.caseentry.action.reminder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientReminder;
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
 * @author Chau Thu Tran
 * 
 * @version SetEventStatusAction.java 1:13:45 PM Sep 7, 2012 $
 */
public class SetEventStatusAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
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

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer programStageInstanceId;

    public void setProgramStageInstanceId( Integer programStageInstanceId )
    {
        this.programStageInstanceId = programStageInstanceId;
    }

    private Integer status;

    public void setStatus( Integer status )
    {
        this.status = status;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        ProgramStageInstance programStageInstance = programStageInstanceService
            .getProgramStageInstance( programStageInstanceId );

        switch ( status.intValue() )
        {
        case ProgramStageInstance.COMPLETED_STATUS:
            programStageInstance.setCompleted( true );
            programStageInstance.setStatus( null );
            sendSMSToCompletedEvent(programStageInstance);
            break;
        case ProgramStageInstance.VISITED_STATUS:
            programStageInstance.setCompleted( false );
            programStageInstance.setStatus( null );
            break;
        case ProgramStageInstance.LATE_VISIT_STATUS:
            programStageInstance.setCompleted( false );
            programStageInstance.setStatus( null );
            break;
        case ProgramStageInstance.FUTURE_VISIT_STATUS:
            programStageInstance.setCompleted( false );
            programStageInstance.setStatus( null );
            break;
        case ProgramStageInstance.SKIPPED_STATUS:
            programStageInstance.setStatus( status );
            break;
        default:
            break;
        }

        programStageInstanceService.updateProgramStageInstance( programStageInstance );

        return SUCCESS;
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
                if ( rm != null && rm.getWhenToSend() == PatientReminder.SEND_WHEN_TO_C0MPLETED_EVENT )
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
            for ( User user : reminder.getUserGroup().getMembers() )
            {
                if ( user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() )
                {
                    phoneNumbers.add( user.getPhoneNumber() );
                }
            }
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

}
