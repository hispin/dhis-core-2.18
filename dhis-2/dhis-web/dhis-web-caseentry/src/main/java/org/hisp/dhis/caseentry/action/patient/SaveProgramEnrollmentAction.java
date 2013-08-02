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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.caseentry.state.SelectedStateManager;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.patient.Patient;
import org.hisp.dhis.patient.PatientReminder;
import org.hisp.dhis.patient.PatientService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
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
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class SaveProgramEnrollmentAction
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

    private SelectedStateManager selectedStateManager;

    public void setSelectedStateManager( SelectedStateManager selectedStateManager )
    {
        this.selectedStateManager = selectedStateManager;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
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

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer patientId;

    public Integer getPatientId()
    {
        return patientId;
    }

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

    private ProgramInstance programInstance;

    public ProgramInstance getProgramInstance()
    {
        return programInstance;
    }

    private ProgramStageInstance activeProgramStageInstance;

    public ProgramStageInstance getActiveProgramStageInstance()
    {
        return activeProgramStageInstance;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Patient patient = patientService.getPatient( patientId );

        Program program = programService.getProgram( programId );

        if ( enrollmentDate == null || enrollmentDate.isEmpty() )
        {
            if ( program.getUseBirthDateAsIncidentDate() )
            {
                enrollmentDate = format.formatDate( patient.getBirthDate() );
            }
        }

        if ( dateOfIncident == null || dateOfIncident.isEmpty() )
        {
            if ( program.getUseBirthDateAsIncidentDate() )
            {
                dateOfIncident = format.formatDate( patient.getBirthDate() );
            }
            else
            {
                dateOfIncident = enrollmentDate;
            }
        }

        Collection<ProgramInstance> programInstances = programInstanceService.getProgramInstances( patient, program,
            ProgramInstance.STATUS_ACTIVE );

        if ( programInstances.iterator().hasNext() )
        {
            programInstance = programInstances.iterator().next();
        }

        if ( programInstance == null )
        {
            programInstance = new ProgramInstance();
            programInstance.setEnrollmentDate( format.parseDate( enrollmentDate ) );
            programInstance.setDateOfIncident( format.parseDate( dateOfIncident ) );
            programInstance.setProgram( program );
            programInstance.setPatient( patient );
            programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );

            programInstanceService.addProgramInstance( programInstance );

            patient.getPrograms().add( program );
            patientService.updatePatient( patient );

            Date dateCreatedEvent = format.parseDate( dateOfIncident );
            if ( program.getGeneratedByEnrollmentDate() )
            {
                dateCreatedEvent = format.parseDate( enrollmentDate );
            }

            boolean isFirstStage = false;
            Date currentDate = new Date();
            for ( ProgramStage programStage : program.getProgramStages() )
            {
                if ( programStage.getAutoGenerateEvent() )
                {
                    Date dueDate = DateUtils
                        .getDateAfterAddition( dateCreatedEvent, programStage.getMinDaysFromStart() );

                    if ( !program.getIgnoreOverdueEvents()
                        || !(program.getIgnoreOverdueEvents() && dueDate.before( currentDate )) )
                    {
                        ProgramStageInstance programStageInstance = new ProgramStageInstance();
                        programStageInstance.setProgramInstance( programInstance );
                        programStageInstance.setProgramStage( programStage );
                        programStageInstance.setDueDate( dueDate );

                        if ( program.isSingleEvent() )
                        {
                            programStageInstance.setOrganisationUnit( selectedStateManager
                                .getSelectedOrganisationUnit() );
                            programStageInstance.setExecutionDate( dueDate );
                        }
                        programStageInstanceService.addProgramStageInstance( programStageInstance );

                        if ( !isFirstStage )
                        {
                            activeProgramStageInstance = programStageInstance;
                            isFirstStage = true;
                        }
                    }
                }
            }

            // send messages after enrollment program
            sendMessage(programInstance);
        }
        else
        {
            programInstance.setEnrollmentDate( format.parseDate( enrollmentDate ) );
            programInstance.setDateOfIncident( format.parseDate( dateOfIncident ) );

            programInstanceService.updateProgramInstance( programInstance );

            for ( ProgramStageInstance programStageInstance : programInstance.getProgramStageInstances() )
            {
                if ( !programStageInstance.isCompleted()
                    || programStageInstance.getStatus() != ProgramStageInstance.SKIPPED_STATUS )
                {
                    Date dueDate = DateUtils.getDateAfterAddition( format.parseDate( dateOfIncident ),
                        programStageInstance.getProgramStage().getMinDaysFromStart() );

                    programStageInstance.setDueDate( dueDate );

                    programStageInstanceService.updateProgramStageInstance( programStageInstance );
                }
            }
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void sendMessage( ProgramInstance programInstance )
    {
        Patient patient = programInstance.getPatient();

        if ( patient != null )
        {
            Collection<PatientReminder> reminders = programInstance.getProgram().getPatientReminders();
            for ( PatientReminder rm : reminders )
            {
                if ( rm != null && rm.getWhenToSend() != null
                    && rm.getWhenToSend() == PatientReminder.SEND_WHEN_TO_EMROLLEMENT )
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
