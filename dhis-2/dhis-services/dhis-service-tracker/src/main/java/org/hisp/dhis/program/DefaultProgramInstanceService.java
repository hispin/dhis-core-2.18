package org.hisp.dhis.program;

/*
 * Copyright (c) 2004-2014, University of Oslo
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

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.GridHeader;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.message.MessageConversation;
import org.hisp.dhis.message.MessageService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.sms.SmsSender;
import org.hisp.dhis.sms.SmsServiceException;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.system.grid.ListGrid;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceReminder;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceReminderService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.trackedentityattributevalue.TrackedEntityAttributeValueService;
import org.hisp.dhis.trackedentitycomment.TrackedEntityComment;
import org.hisp.dhis.trackedentitydatavalue.TrackedEntityDataValue;
import org.hisp.dhis.trackedentitydatavalue.TrackedEntityDataValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Abyot Asalefew
 */
@Transactional
public class DefaultProgramInstanceService
    implements ProgramInstanceService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ProgramInstanceStore programInstanceStore;

    public void setProgramInstanceStore( ProgramInstanceStore programInstanceStore )
    {
        this.programInstanceStore = programInstanceStore;
    }

    private TrackedEntityAttributeValueService attributeValueService;

    public void setAttributeValueService( TrackedEntityAttributeValueService attributeValueService )
    {
        this.attributeValueService = attributeValueService;
    }

    public void setDataValueService( TrackedEntityDataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private TrackedEntityDataValueService dataValueService;

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private SmsSender smsSender;

    public void setSmsSender( SmsSender smsSender )
    {
        this.smsSender = smsSender;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private TrackedEntityInstanceReminderService reminderService;

    public void setReminderService( TrackedEntityInstanceReminderService reminderService )
    {
        this.reminderService = reminderService;
    }

    private MessageService messageService;

    public void setMessageService( MessageService messageService )
    {
        this.messageService = messageService;
    }

    private ProgramStageInstanceService programStageInstanceService;

    public void setProgramStageInstanceService( ProgramStageInstanceService programStageInstanceService )
    {
        this.programStageInstanceService = programStageInstanceService;
    }

    @Autowired
    private TrackedEntityInstanceService entityInstanceService;

    // -------------------------------------------------------------------------
    // Implementation methods
    // -------------------------------------------------------------------------

    public int addProgramInstance( ProgramInstance programInstance )
    {
        return programInstanceStore.save( programInstance );
    }

    public void deleteProgramInstance( ProgramInstance programInstance )
    {
        programInstanceStore.delete( programInstance );
    }

    public Collection<ProgramInstance> getAllProgramInstances()
    {
        return programInstanceStore.getAll();
    }

    public ProgramInstance getProgramInstance( int id )
    {
        return programInstanceStore.get( id );
    }

    @Override
    public ProgramInstance getProgramInstance( String id )
    {
        return programInstanceStore.getByUid( id );
    }

    public Collection<ProgramInstance> getProgramInstances( Integer status )
    {
        return programInstanceStore.getByStatus( status );
    }

    public void updateProgramInstance( ProgramInstance programInstance )
    {
        programInstanceStore.update( programInstance );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program )
    {
        return programInstanceStore.get( program );
    }

    public Collection<ProgramInstance> getProgramInstances( Collection<Program> programs )
    {
        return programInstanceStore.get( programs );
    }

    public Collection<ProgramInstance> getProgramInstances( Collection<Program> programs,
        OrganisationUnit organisationUnit )
    {
        return programInstanceStore.get( programs, organisationUnit );
    }

    public Collection<ProgramInstance> getProgramInstances( Collection<Program> programs,
        OrganisationUnit organisationUnit, int status )
    {
        return programInstanceStore.get( programs, organisationUnit, status );
    }

    public Collection<ProgramInstance> getProgramInstances( Collection<Program> programs, Integer status )
    {
        return programInstanceStore.get( programs, status );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program, Integer status )
    {
        return programInstanceStore.get( program, status );
    }

    public Collection<ProgramInstance> getProgramInstances( TrackedEntityInstance entityInstance, Integer status )
    {
        return programInstanceStore.get( entityInstance, status );
    }

    public Collection<ProgramInstance> getProgramInstances( TrackedEntityInstance entityInstance, Program program )
    {
        return programInstanceStore.get( entityInstance, program );
    }

    public Collection<ProgramInstance> getProgramInstances( TrackedEntityInstance entityInstance, Program program,
        Integer status )
    {
        return programInstanceStore.get( entityInstance, program, status );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program, OrganisationUnit organisationUnit,
        Integer min, Integer max )
    {
        return programInstanceStore.get( program, organisationUnit, min, max );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program, OrganisationUnit organisationUnit,
        Date startDate, Date endDate )
    {
        return programInstanceStore.get( program, organisationUnit, startDate, endDate );
    }

    public Collection<ProgramInstance> getProgramInstances( Program program, Collection<Integer> orgunitIds,
        Date startDate, Date endDate, Integer min, Integer max )
    {
        return programInstanceStore.get( program, orgunitIds, startDate, endDate, min, max );
    }

    public int countProgramInstances( Program program, OrganisationUnit organisationUnit )
    {
        return programInstanceStore.count( program, organisationUnit );
    }

    public int countProgramInstances( Program program, Collection<Integer> orgunitIds, Date startDate, Date endDate )
    {
        return programInstanceStore.count( program, orgunitIds, startDate, endDate );
    }

    public List<Grid> getProgramInstanceReport( TrackedEntityInstance instance, I18n i18n, I18nFormat format )
    {
        List<Grid> grids = new ArrayList<Grid>();

        // ---------------------------------------------------------------------
        // Get registered personal entityInstance data
        // ---------------------------------------------------------------------

        Grid attrGrid = new ListGrid();

        // ---------------------------------------------------------------------
        // Add dynamic attribues
        // ---------------------------------------------------------------------

        Collection<Program> programs = programService
            .getProgramsByCurrentUser( Program.MULTIPLE_EVENTS_WITH_REGISTRATION );
        programs.addAll( programService.getProgramsByCurrentUser( Program.SINGLE_EVENT_WITH_REGISTRATION ) );

        Collection<TrackedEntityAttributeValue> attributeValues = attributeValueService
            .getTrackedEntityAttributeValues( instance );
        Iterator<TrackedEntityAttributeValue> iterAttribute = attributeValues.iterator();

        for ( Program program : programs )
        {
            List<TrackedEntityAttribute> atttributes = program.getEntityAttributes();
            while ( iterAttribute.hasNext() )
            {
                TrackedEntityAttributeValue attributeValue = iterAttribute.next();
                if ( !atttributes.contains( attributeValue.getAttribute() ) )
                {
                    iterAttribute.remove();
                }
            }
        }

        for ( TrackedEntityAttributeValue attributeValue : attributeValues )
        {
            attrGrid.addRow();
            attrGrid.addValue( attributeValue.getAttribute().getDisplayName() );
            String value = attributeValue.getValue();
            if ( attributeValue.getAttribute().getValueType().equals( TrackedEntityAttribute.TYPE_BOOL ) )
            {
                value = i18n.getString( value );
            }

            attrGrid.addValue( value );
        }

        grids.add( attrGrid );

        // ---------------------------------------------------------------------
        // Get all program data registered
        // ---------------------------------------------------------------------

        Collection<ProgramInstance> programInstances = instance.getProgramInstances();

        if ( programInstances.size() > 0 )
        {
            for ( ProgramInstance programInstance : programInstances )
            {
                if ( programs.contains( programInstance.getProgram() ) )
                {
                    Grid gridProgram = getProgramInstanceReport( programInstance, i18n, format );

                    grids.add( gridProgram );
                }
            }
        }

        return grids;
    }

    public Grid getProgramInstanceReport( ProgramInstance programInstance, I18n i18n, I18nFormat format )
    {
        Grid grid = new ListGrid();

        // ---------------------------------------------------------------------
        // Get all program data registered
        // ---------------------------------------------------------------------

        grid.setTitle( programInstance.getProgram().getName() );
        grid.setSubtitle( "" );

        // ---------------------------------------------------------------------
        // Headers
        // ---------------------------------------------------------------------

        grid.addHeader( new GridHeader( "", false, false ) );
        grid.addHeader( new GridHeader( "", false, false ) );

        // ---------------------------------------------------------------------
        // Grids for program-stage-instance
        // ---------------------------------------------------------------------

        grid.addRow();
        grid.addValue( programInstance.getProgram().getDateOfEnrollmentDescription() );
        grid.addValue( format.formatDate( programInstance.getEnrollmentDate() ) );

        // Get attribute-values which belong to the program

        TrackedEntityInstance instance = programInstance.getEntityInstance();

        Collection<TrackedEntityAttribute> atttributes = programInstance.getProgram().getEntityAttributes();

        for ( TrackedEntityAttribute attrtibute : atttributes )
        {
            TrackedEntityAttributeValue attributeValue = attributeValueService.getTrackedEntityAttributeValue(
                instance, attrtibute );
            if ( attributeValue != null )
            {
                grid.addRow();
                grid.addValue( attrtibute.getDisplayName() );
                grid.addValue( attributeValue.getValue() );
            }
        }

        // Get entityInstance comments for the program instance

        TrackedEntityComment comment = programInstance.getComment();
        if ( comment != null )
        {
            grid.addRow();
            grid.addValue( i18n.getString( "comment" ) + " " + i18n.getString( "on" ) + " "
                + format.formatDateTime( comment.getCreatedDate() ) );
            grid.addValue( comment.getCommentText() );
        }

        // Get sms of the program-instance

        List<OutboundSms> messasges = programInstance.getOutboundSms();

        for ( OutboundSms messasge : messasges )
        {
            grid.addRow();
            grid.addValue( i18n.getString( "message" ) + " " + i18n.getString( "on" ) + " "
                + format.formatDateTime( messasge.getDate() ) );
            grid.addValue( messasge.getMessage() );
        }

        // Get message conversations of the program-instance

        List<MessageConversation> conversations = programInstance.getMessageConversations();

        for ( MessageConversation conversation : conversations )
        {
            grid.addRow();
            grid.addValue( i18n.getString( "message" ) + " " + i18n.getString( "on" ) + " "
                + format.formatDateTime( conversation.getLastUpdated() ) );
            grid.addValue( conversation.getMessages().get( 0 ) );
        }

        // Program-instance attributes

        if ( programInstance.getProgram().getDisplayIncidentDate() != null
            && programInstance.getProgram().getDisplayIncidentDate() )
        {
            grid.addRow();
            grid.addValue( programInstance.getProgram().getDateOfIncidentDescription() );
            grid.addValue( format.formatDate( programInstance.getDateOfIncident() ) );
        }

        getProgramStageInstancesReport( grid, programInstance, format, i18n );

        return grid;
    }

    public int countProgramInstancesByStatus( Integer status, Program program, Collection<Integer> orgunitIds,
        Date startDate, Date endDate )
    {
        return programInstanceStore.countByStatus( status, program, orgunitIds, startDate, endDate );
    }

    public Collection<ProgramInstance> getProgramInstancesByStatus( Integer status, Program program,
        Collection<Integer> orgunitIds, Date startDate, Date endDate )
    {
        return programInstanceStore.getByStatus( status, program, orgunitIds, startDate, endDate );
    }

    public Collection<SchedulingProgramObject> getScheduleMesssages()
    {
        Collection<SchedulingProgramObject> result = programInstanceStore
            .getSendMesssageEvents( TrackedEntityInstanceReminder.ENROLLEMENT_DATE_TO_COMPARE );

        result.addAll( programInstanceStore
            .getSendMesssageEvents( TrackedEntityInstanceReminder.INCIDENT_DATE_TO_COMPARE ) );

        return result;
    }

    public Collection<OutboundSms> sendMessages( ProgramInstance programInstance, int status, I18nFormat format )
    {
        TrackedEntityInstance entityInstance = programInstance.getEntityInstance();
        Collection<OutboundSms> outboundSmsList = new HashSet<OutboundSms>();

        Collection<TrackedEntityInstanceReminder> reminders = programInstance.getProgram().getInstanceReminders();

        for ( TrackedEntityInstanceReminder rm : reminders )
        {
            if ( rm != null
                && rm.getWhenToSend() != null
                && rm.getWhenToSend() == status
                && (rm.getMessageType() == TrackedEntityInstanceReminder.MESSAGE_TYPE_DIRECT_SMS || rm.getMessageType() == TrackedEntityInstanceReminder.MESSAGE_TYPE_BOTH) )
            {
                OutboundSms outboundSms = sendProgramMessage( rm, programInstance, entityInstance, format );

                if ( outboundSms != null )
                {
                    outboundSmsList.add( outboundSms );
                }
            }
        }

        return outboundSmsList;
    }

    @Override
    public Collection<MessageConversation> sendMessageConversations( ProgramInstance programInstance, int status,
        I18nFormat format )
    {
        Collection<MessageConversation> messageConversations = new HashSet<MessageConversation>();

        Collection<TrackedEntityInstanceReminder> reminders = programInstance.getProgram().getInstanceReminders();
        for ( TrackedEntityInstanceReminder rm : reminders )
        {
            if ( rm != null
                && rm.getWhenToSend() != null
                && rm.getWhenToSend() == status
                && (rm.getMessageType() == TrackedEntityInstanceReminder.MESSAGE_TYPE_DHIS_MESSAGE || rm
                .getMessageType() == TrackedEntityInstanceReminder.MESSAGE_TYPE_BOTH) )
            {
                int id = messageService.sendMessage( programInstance.getProgram().getDisplayName(),
                    reminderService.getMessageFromTemplate( rm, programInstance, format ), null,
                    reminderService.getUsers( rm, programInstance.getEntityInstance() ), null, false, true );
                messageConversations.add( messageService.getMessageConversation( id ) );
            }
        }

        return messageConversations;
    }

    @Override
    public ProgramInstance enrollTrackedEntityInstance( TrackedEntityInstance entityInstance, Program program,
        Date enrollmentDate, Date dateOfIncident, OrganisationUnit organisationUnit, I18nFormat format )
    {
        // ---------------------------------------------------------------------
        // Add program instance
        // ---------------------------------------------------------------------

        ProgramInstance programInstance = new ProgramInstance();

        programInstance.enrollTrackedEntityInstance( entityInstance, program );

        if ( enrollmentDate != null )
        {
            programInstance.setEnrollmentDate( enrollmentDate );
        }
        else
        {
            programInstance.setEnrollmentDate( new Date() );
        }

        if ( dateOfIncident != null )
        {
            programInstance.setDateOfIncident( dateOfIncident );
        }
        else
        {
            programInstance.setDateOfIncident( new Date() );
        }

        programInstance.setStatus( ProgramInstance.STATUS_ACTIVE );
        addProgramInstance( programInstance );

        // ---------------------------------------------------------------------
        // Generate events for program instance
        // ---------------------------------------------------------------------

        for ( ProgramStage programStage : program.getProgramStages() )
        {
            if ( programStage.getAutoGenerateEvent() )
            {
                ProgramStageInstance programStageInstance = generateEvent( programInstance, programStage,
                    programInstance.getEnrollmentDate(), programInstance.getDateOfIncident(), organisationUnit );

                if ( programStageInstance != null )
                {
                    programStageInstanceService.addProgramStageInstance( programStageInstance );
                }
            }
        }

        // -----------------------------------------------------------------
        // Send messages after enrolling in program
        // -----------------------------------------------------------------

        List<OutboundSms> outboundSms = programInstance.getOutboundSms();

        if ( outboundSms == null )
        {
            outboundSms = new ArrayList<OutboundSms>();
        }

        outboundSms.addAll( sendMessages( programInstance, TrackedEntityInstanceReminder.SEND_WHEN_TO_EMROLLEMENT,
            format ) );

        // -----------------------------------------------------------------
        // Send message when to completed the program
        // -----------------------------------------------------------------

        List<MessageConversation> messages = programInstance.getMessageConversations();

        if ( messages == null )
        {
            messages = new ArrayList<MessageConversation>();
        }

        messages.addAll( sendMessageConversations( programInstance,
            TrackedEntityInstanceReminder.SEND_WHEN_TO_EMROLLEMENT, format ) );

        updateProgramInstance( programInstance );
        entityInstanceService.updateTrackedEntityInstance( entityInstance );

        return programInstance;
    }

    @Override
    public boolean canAutoCompleteProgramInstanceStatus( ProgramInstance programInstance )
    {
        Set<ProgramStageInstance> stageInstances = programInstance.getProgramStageInstances();

        for ( ProgramStageInstance stageInstance : stageInstances )
        {
            if ( !stageInstance.isCompleted() || stageInstance.getProgramStage().getIrregular() )
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public void completeProgramInstanceStatus( ProgramInstance programInstance, I18nFormat format )
    {
        // ---------------------------------------------------------------------
        // Send sms-message when to completed the program
        // ---------------------------------------------------------------------

        List<OutboundSms> outboundSms = programInstance.getOutboundSms();

        if ( outboundSms == null )
        {
            outboundSms = new ArrayList<OutboundSms>();
        }

        outboundSms.addAll( sendMessages( programInstance,
            TrackedEntityInstanceReminder.SEND_WHEN_TO_C0MPLETED_PROGRAM, format ) );

        // -----------------------------------------------------------------
        // Send DHIS message when to completed the program
        // -----------------------------------------------------------------

        List<MessageConversation> messageConversations = programInstance.getMessageConversations();

        if ( messageConversations == null )
        {
            messageConversations = new ArrayList<MessageConversation>();
        }

        messageConversations.addAll( sendMessageConversations( programInstance,
            TrackedEntityInstanceReminder.SEND_WHEN_TO_C0MPLETED_PROGRAM, format ) );

        // -----------------------------------------------------------------
        // Update program-instance
        // -----------------------------------------------------------------

        programInstance.setStatus( ProgramInstance.STATUS_COMPLETED );
        programInstance.setEndDate( new Date() );

        updateProgramInstance( programInstance );
    }

    public void cancelProgramInstanceStatus( ProgramInstance programInstance )
    {
        // ---------------------------------------------------------------------
        // Set status of the program-instance
        // ---------------------------------------------------------------------

        Calendar today = Calendar.getInstance();
        PeriodType.clearTimeOfDay( today );
        Date currentDate = today.getTime();

        programInstance.setEndDate( currentDate );
        programInstance.setStatus( ProgramInstance.STATUS_CANCELLED );
        updateProgramInstance( programInstance );

        // ---------------------------------------------------------------------
        // Set statuses of the program-stage-instances
        // ---------------------------------------------------------------------

        for ( ProgramStageInstance programStageInstance : programInstance.getProgramStageInstances() )
        {
            if ( programStageInstance.getExecutionDate() == null )
            {
                // ---------------------------------------------------------------------
                // Set status as skipped for overdue events
                // ---------------------------------------------------------------------
                if ( programStageInstance.getDueDate().before( currentDate ) )
                {
                    programStageInstance.setStatus( ProgramStageInstance.SKIPPED_STATUS );
                    programStageInstanceService.updateProgramStageInstance( programStageInstance );
                }

                // ---------------------------------------------------------------------
                // Remove scheduled events
                // ---------------------------------------------------------------------
                else
                {
                    programStageInstanceService.deleteProgramStageInstance( programStageInstance );
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private ProgramStageInstance generateEvent( ProgramInstance programInstance, ProgramStage programStage,
        Date enrollmentDate, Date dateOfIncident, OrganisationUnit orgunit )
    {
        ProgramStageInstance programStageInstance = null;

        Date currentDate = new Date();
        Date dateCreatedEvent;

        if ( programStage.getGeneratedByEnrollmentDate() )
        {
            dateCreatedEvent = enrollmentDate;
        }
        else
        {
            dateCreatedEvent = dateOfIncident;
        }

        Date dueDate = DateUtils.getDateAfterAddition( dateCreatedEvent, programStage.getMinDaysFromStart() );

        if ( !programInstance.getProgram().getIgnoreOverdueEvents() || dueDate.before( currentDate ) )
        {
            programStageInstance = new ProgramStageInstance();
            programStageInstance.setProgramInstance( programInstance );
            programStageInstance.setProgramStage( programStage );
            programStageInstance.setDueDate( dueDate );

            if ( programInstance.getProgram().isSingleEvent() )
            {
                programStageInstance.setOrganisationUnit( orgunit );
                programStageInstance.setExecutionDate( dueDate );
            }
        }

        return programStageInstance;
    }

    private void getProgramStageInstancesReport( Grid grid, ProgramInstance programInstance, I18nFormat format,
        I18n i18n )
    {
        Collection<ProgramStageInstance> programStageInstances = programInstance.getProgramStageInstances();

        for ( ProgramStageInstance programStageInstance : programStageInstances )
        {
            grid.addRow();
            grid.addValue( "" );
            grid.addValue( "" );

            grid.addRow();
            grid.addValue( programStageInstance.getProgramStage().getName() );
            grid.addValue( "" );

            // -----------------------------------------------------------------
            // due-date && report-date
            // -----------------------------------------------------------------

            grid.addRow();
            grid.addValue( i18n.getString( "due_date" ) );
            grid.addValue( format.formatDate( programStageInstance.getDueDate() ) );

            if ( programStageInstance.getExecutionDate() != null )
            {
                grid.addRow();
                grid.addValue( programStageInstance.getProgramStage().getReportDateDescription() );
                grid.addValue( format.formatDate( programStageInstance.getExecutionDate() ) );
            }

            // SMS messages

            List<OutboundSms> messasges = programStageInstance.getOutboundSms();

            for ( OutboundSms messasge : messasges )
            {
                grid.addRow();
                grid.addValue( i18n.getString( "messsage" ) + " " + i18n.getString( "on" ) + " "
                    + format.formatDateTime( messasge.getDate() ) );
                grid.addValue( messasge.getMessage() );
            }

            // -----------------------------------------------------------------
            // Values
            // -----------------------------------------------------------------

            Collection<TrackedEntityDataValue> entityDataValues = dataValueService
                .getTrackedEntityDataValues( programStageInstance );

            for ( TrackedEntityDataValue entityInstanceDataValue : entityDataValues )
            {
                DataElement dataElement = entityInstanceDataValue.getDataElement();

                grid.addRow();
                grid.addValue( dataElement.getFormNameFallback() );

                if ( dataElement.getType().equals( DataElement.VALUE_TYPE_BOOL ) )
                {
                    grid.addValue( i18n.getString( entityInstanceDataValue.getValue() ) );
                }
                else
                {
                    grid.addValue( entityInstanceDataValue.getValue() );
                }
            }
        }
    }

    private OutboundSms sendProgramMessage( TrackedEntityInstanceReminder reminder, ProgramInstance programInstance,
        TrackedEntityInstance entityInstance, I18nFormat format )
    {
        Set<String> phoneNumbers = reminderService.getPhonenumbers( reminder, entityInstance );
        OutboundSms outboundSms = null;

        if ( phoneNumbers.size() > 0 )
        {
            String msg = reminderService.getMessageFromTemplate( reminder, programInstance, format );

            try
            {
                outboundSms = new OutboundSms();
                outboundSms.setMessage( msg );
                outboundSms.setRecipients( phoneNumbers );
                outboundSms.setSender( currentUserService.getCurrentUsername() );
                smsSender.sendMessage( outboundSms, null );
            }
            catch ( SmsServiceException e )
            {
                e.printStackTrace();
            }
        }

        return outboundSms;
    }

}
