package org.hisp.dhis.dxf2.events.event;

/*
 * Copyright (c) 2004-2015, University of Oslo
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.common.IdentifiableProperty;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummaries;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.ImportOptions;
import org.hisp.dhis.event.EventStatus;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.program.ProgramStageService;
import org.hisp.dhis.program.ProgramStatus;
import org.hisp.dhis.scheduling.TaskId;
import org.hisp.dhis.system.callable.IdentifiableObjectCallable;
import org.hisp.dhis.system.notification.NotificationLevel;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.system.util.CachingMap;
import org.hisp.dhis.system.util.DateUtils;
import org.hisp.dhis.system.util.DebugUtils;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentitycomment.TrackedEntityComment;
import org.hisp.dhis.trackedentitycomment.TrackedEntityCommentService;
import org.hisp.dhis.trackedentitydatavalue.TrackedEntityDataValue;
import org.hisp.dhis.trackedentitydatavalue.TrackedEntityDataValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.system.notification.NotificationLevel.ERROR;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Transactional
public abstract class AbstractEventService
    implements EventService
{
    private static final Log log = LogFactory.getLog( AbstractEventService.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    protected ProgramService programService;

    @Autowired
    protected ProgramStageService programStageService;

    @Autowired
    protected ProgramInstanceService programInstanceService;

    @Autowired
    protected ProgramStageInstanceService programStageInstanceService;

    @Autowired
    protected OrganisationUnitService organisationUnitService;

    @Autowired
    protected DataElementService dataElementService;

    @Autowired
    protected CurrentUserService currentUserService;

    @Autowired
    protected TrackedEntityDataValueService dataValueService;

    @Autowired
    protected TrackedEntityInstanceService entityInstanceService;

    @Autowired
    protected TrackedEntityCommentService commentService;

    @Autowired
    protected EventStore eventStore;

    @Autowired
    protected I18nManager i18nManager;

    @Autowired
    protected Notifier notifier;

    @Autowired
    protected SessionFactory sessionFactory;

    @Autowired
    protected IdentifiableObjectManager manager;

    protected final int FLUSH_FREQUENCY = 20;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private CachingMap<String, OrganisationUnit> organisationUnitCache = new CachingMap<>();

    private CachingMap<String, Program> programCache = new CachingMap<>();

    private CachingMap<String, ProgramStage> programStageCache = new CachingMap<>();

    private CachingMap<String, DataElement> dataElementCache = new CachingMap<>();

    private List<Program> accessiblePrograms = new ArrayList<>();

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Override
    public ImportSummaries addEvents( List<Event> events, ImportOptions importOptions )
    {
        ImportSummaries importSummaries = new ImportSummaries();
        int counter = 0;

        User user = currentUserService.getCurrentUser();

        for ( Event event : events )
        {
            importSummaries.addImportSummary( addEvent( event, user, importOptions ) );

            if ( counter % FLUSH_FREQUENCY == 0 )
            {
                sessionFactory.getCurrentSession().flush();
                sessionFactory.getCurrentSession().clear();
            }

            counter++;
        }

        return importSummaries;
    }

    @Override
    public ImportSummaries addEvents( List<Event> events, ImportOptions importOptions, TaskId taskId )
    {
        notifier.clear( taskId ).notify( taskId, "Importing events" );

        try
        {
            ImportSummaries importSummaries = addEvents( events, importOptions );

            if ( taskId != null )
            {
                notifier.notify( taskId, NotificationLevel.INFO, "Import done", true ).addTaskSummary( taskId, importSummaries );
            }

            return importSummaries;
        }
        catch ( RuntimeException ex )
        {
            log.error( DebugUtils.getStackTrace( ex ) );
            notifier.notify( taskId, ERROR, "Process failed: " + ex.getMessage(), true );
            return new ImportSummaries().addImportSummary( new ImportSummary( ImportStatus.ERROR, "The import process failed: " + ex.getMessage() ) );
        }

    }

    @Override
    public ImportSummary addEvent( Event event )
    {
        return addEvent( event, null );
    }

    @Override
    public ImportSummary addEvent( Event event, ImportOptions importOptions )
    {
        return addEvent( event, currentUserService.getCurrentUser(), importOptions );
    }

    protected ImportSummary addEvent( Event event, User user, ImportOptions importOptions )
    {
        Program program = getProgram( event.getProgram() );
        ProgramStage programStage = getProgramStage( event.getProgramStage() );

        ProgramInstance programInstance;
        ProgramStageInstance programStageInstance = null;

        if ( importOptions == null )
        {
            importOptions = new ImportOptions();
        }

        if ( program == null )
        {
            return new ImportSummary( ImportStatus.ERROR, "Event.program does not point to a valid program" );
        }

        if ( programStage == null && !program.isSingleEvent() )
        {
            return new ImportSummary( ImportStatus.ERROR,
                "Event.programStage does not point to a valid programStage, and program is multi stage" );
        }
        else if ( programStage == null )
        {
            programStage = program.getProgramStageByStage( 1 );
        }

        Assert.notNull( program );
        Assert.notNull( programStage );

        if ( verifyProgramAccess( program, user ) )
        {
            return new ImportSummary( ImportStatus.ERROR,
                "Current user does not have permission to access this program" );
        }

        if ( program.isRegistration() )
        {
            if ( event.getTrackedEntityInstance() == null )
            {
                return new ImportSummary( ImportStatus.ERROR,
                    "No Event.trackedEntityInstance was provided for registration based program" );
            }

            org.hisp.dhis.trackedentity.TrackedEntityInstance entityInstance = entityInstanceService
                .getTrackedEntityInstance( event.getTrackedEntityInstance() );

            if ( entityInstance == null )
            {
                return new ImportSummary( ImportStatus.ERROR,
                    "Event.trackedEntityInstance does not point to a valid tracked entity instance" );
            }

            List<ProgramInstance> programInstances = new ArrayList<>( programInstanceService.getProgramInstances(
                entityInstance, program, ProgramInstance.STATUS_ACTIVE ) );

            if ( programInstances.isEmpty() )
            {
                return new ImportSummary( ImportStatus.ERROR, "TrackedEntityInstance " + entityInstance.getUid()
                    + " is not enrolled in program " + program.getUid() );
            }
            else if ( programInstances.size() > 1 )
            {
                return new ImportSummary( ImportStatus.ERROR, "TrackedEntityInstance " + entityInstance.getUid()
                    + " have multiple active enrollments in program " + program.getUid() );
            }

            programInstance = programInstances.get( 0 );

            if ( program.isSingleEvent() )
            {
                List<ProgramStageInstance> programStageInstances = new ArrayList<>(
                    programStageInstanceService.getProgramStageInstances( programInstances, EventStatus.ACTIVE ) );

                if ( programStageInstances.isEmpty() )
                {
                    return new ImportSummary( ImportStatus.ERROR, "TrackedEntityInstance " + entityInstance.getUid()
                        + " is not enrolled in program stage " + programStage.getUid() );
                }
                else if ( programStageInstances.size() > 1 )
                {
                    return new ImportSummary( ImportStatus.ERROR, "TrackedEntityInstance " + entityInstance.getUid()
                        + " have multiple active enrollments in program stage " + programStage.getUid() );
                }

                programStageInstance = programStageInstances.get( 0 );
            }
            else
            {
                if ( !programStage.getIrregular() )
                {
                    programStageInstance = programStageInstanceService.getProgramStageInstance( programInstance,
                        programStage );
                }
                else
                {
                    if ( event.getEvent() != null )
                    {
                        programStageInstance = programStageInstanceService.getProgramStageInstance( event.getEvent() );

                        if ( programStageInstance == null )
                        {
                            if ( !CodeGenerator.isValidCode( event.getEvent() ) )
                            {
                                return new ImportSummary( ImportStatus.ERROR, "Event.event did not point to a valid event" );
                            }
                        }
                    }
                }
            }
        }
        else
        {
            List<ProgramInstance> programInstances = new ArrayList<>( programInstanceService.getProgramInstances(
                program, ProgramInstance.STATUS_ACTIVE ) );

            if ( programInstances.isEmpty() )
            {
                return new ImportSummary( ImportStatus.ERROR,
                    "There is no program instance for program " + program.getUid() );
            }
            else if ( programInstances.size() > 1 )
            {
                return new ImportSummary( ImportStatus.ERROR,
                    "Multiple active program instances exists for program " + program.getUid() );
            }

            programInstance = programInstances.get( 0 );

            if ( event.getEvent() != null )
            {
                programStageInstance = programStageInstanceService.getProgramStageInstance( event.getEvent() );

                if ( programStageInstance == null )
                {
                    if ( !CodeGenerator.isValidCode( event.getEvent() ) )
                    {
                        return new ImportSummary( ImportStatus.ERROR, "Event.event did not point to a valid event" );
                    }
                }
            }
        }

        OrganisationUnit organisationUnit = getOrganisationUnit( importOptions.getOrgUnitIdScheme(), event.getOrgUnit() );

        if ( organisationUnit == null )
        {
            return new ImportSummary( ImportStatus.ERROR, "Event.orgUnit does not point to a valid organisation unit" );
        }

        if ( verifyProgramOrganisationUnitAssociation( program, organisationUnit ) )
        {
            return new ImportSummary( ImportStatus.ERROR, "Program is not assigned to this organisation unit" );
        }

        return saveEvent( program, programInstance, programStage, programStageInstance, organisationUnit, event,
            user, importOptions );
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    @Override
    public Events getEvents( Program program, OrganisationUnit organisationUnit )
    {
        return getEvents( program, null, null, null, Arrays.asList( organisationUnit ), null, null, null, null );
    }

    @Override
    public Events getEvents( Program program, ProgramStage programStage, ProgramStatus programStatus, Boolean followUp,
        List<OrganisationUnit> organisationUnits, TrackedEntityInstance trackedEntityInstance, Date startDate,
        Date endDate, EventStatus status )
    {
        List<Event> eventList = eventStore.getAll( program, programStage, programStatus, followUp, organisationUnits,
            trackedEntityInstance, startDate, endDate, status );
        Events events = new Events();
        events.setEvents( eventList );

        return events;
    }

    @Override
    public Event getEvent( String uid )
    {
        ProgramStageInstance psi = programStageInstanceService.getProgramStageInstance( uid );

        return psi != null ? convertProgramStageInstance( psi ) : null;
    }

    @Override
    public Event getEvent( ProgramStageInstance programStageInstance )
    {
        return convertProgramStageInstance( programStageInstance );
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Override
    public void updateEvents( List<Event> events, boolean singleValue )
    {
        for ( Event event : events )
        {
            updateEvent( event, singleValue );
        }
    }

    @Override
    public void updateEvent( Event event, boolean singleValue )
    {
        updateEvent( event, singleValue, null );
    }

    @Override
    public void updateEvent( Event event, boolean singleValue, ImportOptions importOptions )
    {
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( event.getEvent() );

        if ( programStageInstance == null )
        {
            return;
        }

        if ( importOptions == null )
        {
            importOptions = new ImportOptions();
        }

        OrganisationUnit organisationUnit = getOrganisationUnit( importOptions.getOrgUnitIdScheme(), event.getOrgUnit() );

        if ( organisationUnit == null )
        {
            organisationUnit = programStageInstance.getOrganisationUnit();
        }

        Date executionDate = new Date();

        if ( event.getEventDate() != null )
        {
            executionDate = DateUtils.parseDate( event.getEventDate() );
            programStageInstance.setExecutionDate( executionDate );
        }

        Date dueDate = new Date();

        if ( event.getDueDate() != null )
        {
            dueDate = DateUtils.parseDate( event.getDueDate() );
        }

        String storedBy = getStoredBy( event, null, currentUserService.getCurrentUsername() );

        if ( event.getStatus() == EventStatus.ACTIVE )
        {
            programStageInstance.setStatus( EventStatus.ACTIVE );
            programStageInstance.setCompletedDate( null );
            programStageInstance.setCompletedUser( null );
        }
        else if ( event.getStatus() == EventStatus.COMPLETED )
        {
            programStageInstance.setCompletedDate( executionDate );
            programStageInstance.setStatus( EventStatus.COMPLETED );
            programStageInstance.setCompletedUser( storedBy );

            if ( !programStageInstance.isCompleted() )
            {
                programStageInstanceService.completeProgramStageInstance( programStageInstance,
                    i18nManager.getI18nFormat() );
            }
        }
        else if ( event.getStatus() == EventStatus.SKIPPED )
        {
            programStageInstance.setStatus( EventStatus.SKIPPED );
        }

        else if ( event.getStatus() == EventStatus.SCHEDULE )
        {
            programStageInstance.setStatus( EventStatus.SCHEDULE );
        }

        programStageInstance.setDueDate( dueDate );
        programStageInstance.setOrganisationUnit( organisationUnit );

        if ( !singleValue )
        {
            if ( programStageInstance.getProgramStage().getCaptureCoordinates() && event.getCoordinate().isValid() )
            {
                programStageInstance.setLatitude( event.getCoordinate().getLatitude() );
                programStageInstance.setLongitude( event.getCoordinate().getLongitude() );
            }
            else
            {
                programStageInstance.setLatitude( null );
                programStageInstance.setLongitude( null );
            }
        }

        programStageInstanceService.updateProgramStageInstance( programStageInstance );

        saveTrackedEntityComment( programStageInstance, event, storedBy );

        Set<TrackedEntityDataValue> dataValues = new HashSet<>( dataValueService.getTrackedEntityDataValues( programStageInstance ) );
        Map<String, TrackedEntityDataValue> existingDataValues = getDataElementDataValueMap( dataValues );

        for ( DataValue value : event.getDataValues() )
        {
            DataElement dataElement = getDataElement( value.getDataElement() );

            TrackedEntityDataValue dataValue = dataValueService.getTrackedEntityDataValue( programStageInstance, dataElement );

            if ( dataValue != null )
            {
                dataValue.setValue( value.getValue() );
                dataValue.setProvidedElsewhere( value.getProvidedElsewhere() );
                dataValueService.updateTrackedEntityDataValue( dataValue );

                dataValues.remove( dataValue );
            }
            else
            {
                TrackedEntityDataValue existingDataValue = existingDataValues.get( value.getDataElement() );

                saveDataValue( programStageInstance, event.getStoredBy(), dataElement, value.getValue(),
                    value.getProvidedElsewhere(), existingDataValue, null );
            }
        }

        if ( !singleValue )
        {
            for ( TrackedEntityDataValue value : dataValues )
            {
                dataValueService.deleteTrackedEntityDataValue( value );
            }
        }
    }

    @Override
    public void updateEventForNote( Event event )
    {
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance(
            event.getEvent() );

        if ( programStageInstance == null )
        {
            return;
        }

        saveTrackedEntityComment( programStageInstance, event, getStoredBy( event, null, currentUserService.getCurrentUsername() ) );
    }

    @Override
    public void updateEventForEventDate( Event event )
    {
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( event.getEvent() );

        if ( programStageInstance == null )
        {
            return;
        }

        Date executionDate = new Date();

        if ( event.getEventDate() != null )
        {
            executionDate = DateUtils.parseDate( event.getEventDate() );
        }

        if ( event.getStatus() == EventStatus.COMPLETED )
        {
            programStageInstance.setStatus( EventStatus.COMPLETED );
        }
        else
        {
            programStageInstance.setStatus( EventStatus.VISITED );
        }

        programStageInstance.setExecutionDate( executionDate );
        programStageInstanceService.updateProgramStageInstance( programStageInstance );
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deleteEvent( Event event )
    {
        ProgramStageInstance programStageInstance = programStageInstanceService.getProgramStageInstance( event.getEvent() );

        if ( programStageInstance != null )
        {
            programStageInstanceService.deleteProgramStageInstance( programStageInstance );
        }
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private Event convertProgramStageInstance( ProgramStageInstance programStageInstance )
    {
        if ( programStageInstance == null )
        {
            return null;
        }

        Event event = new Event();

        event.setEvent( programStageInstance.getUid() );

        if ( programStageInstance.getProgramInstance().getEntityInstance() != null )
        {
            event.setTrackedEntityInstance( programStageInstance.getProgramInstance().getEntityInstance().getUid() );
        }

        event.setFollowup( programStageInstance.getProgramInstance().getFollowup() );
        event.setEnrollmentStatus( EventStatus.fromInt( programStageInstance.getProgramInstance().getStatus() ) );
        event.setStatus( programStageInstance.getStatus() );
        event.setEventDate( DateUtils.getLongDateString( programStageInstance.getExecutionDate() ) );
        event.setDueDate( DateUtils.getLongDateString( programStageInstance.getDueDate() ) );
        event.setStoredBy( programStageInstance.getCompletedUser() );

        if ( programStageInstance.getOrganisationUnit() != null )
        {
            event.setOrgUnit( programStageInstance.getOrganisationUnit().getUid() );
        }

        event.setProgram( programStageInstance.getProgramInstance().getProgram().getUid() );
        event.setEnrollment( programStageInstance.getProgramInstance().getUid() );
        event.setProgramStage( programStageInstance.getProgramStage().getUid() );

        if ( programStageInstance.getProgramInstance().getEntityInstance() != null )
        {
            event.setTrackedEntityInstance( programStageInstance.getProgramInstance().getEntityInstance().getUid() );
        }

        if ( programStageInstance.getProgramStage().getCaptureCoordinates() )
        {
            Coordinate coordinate = null;

            if ( programStageInstance.getLongitude() != null && programStageInstance.getLongitude() != null )
            {
                coordinate = new Coordinate( programStageInstance.getLongitude(), programStageInstance.getLatitude() );

                try
                {
                    List<Double> list = objectMapper.readValue( coordinate.getCoordinateString(),
                        new TypeReference<List<Double>>()
                        {
                        } );

                    coordinate.setLongitude( list.get( 0 ) );
                    coordinate.setLatitude( list.get( 1 ) );
                }
                catch ( IOException ignored )
                {
                }
            }

            if ( coordinate != null && coordinate.isValid() )
            {
                event.setCoordinate( coordinate );
            }
        }

        Collection<TrackedEntityDataValue> dataValues = dataValueService.getTrackedEntityDataValues( programStageInstance );

        for ( TrackedEntityDataValue dataValue : dataValues )
        {
            DataValue value = new DataValue();
            value.setDataElement( dataValue.getDataElement().getUid() );
            value.setValue( dataValue.getValue() );
            value.setProvidedElsewhere( dataValue.getProvidedElsewhere() );
            value.setStoredBy( dataValue.getStoredBy() );

            event.getDataValues().add( value );
        }

        List<TrackedEntityComment> comments = programStageInstance.getComments();

        for ( TrackedEntityComment comment : comments )
        {
            Note note = new Note();

            note.setValue( comment.getCommentText() );
            note.setStoredBy( comment.getCreator() );

            if ( comment.getCreatedDate() != null )
            {
                note.setStoredDate( comment.getCreatedDate().toString() );
            }

            event.getNotes().add( note );
        }

        return event;
    }

    private boolean verifyProgramOrganisationUnitAssociation( Program program, OrganisationUnit organisationUnit )
    {
        boolean assignedToOrganisationUnit = false;

        if ( program.getOrganisationUnits().contains( organisationUnit ) )
        {
            assignedToOrganisationUnit = true;
        }

        return !assignedToOrganisationUnit;
    }

    private boolean verifyProgramAccess( Program program, User user )
    {
        if ( accessiblePrograms.isEmpty() )
        {
            accessiblePrograms = new ArrayList<>( programService.getProgramsByUser( user ) );
        }

        return !accessiblePrograms.contains( program );
    }

    private boolean validateDataValue( DataElement dataElement, String value, ImportSummary importSummary )
    {
        String status = ValidationUtils.dataValueIsValid( value, dataElement );

        if ( status != null )
        {
            importSummary.getConflicts().add( new ImportConflict( dataElement.getUid(), status ) );
            importSummary.getDataValueCount().incrementIgnored();
            return false;
        }

        return true;
    }

    private String getStoredBy( Event event, ImportSummary importSummary, String defaultUsername )
    {
        String storedBy = event.getStoredBy();

        if ( storedBy == null )
        {
            storedBy = defaultUsername;
        }
        else if ( storedBy.length() >= 31 )
        {
            if ( importSummary != null )
            {
                importSummary.getConflicts().add(
                    new ImportConflict( "stored by", storedBy
                        + " is more than 31 characters, using current username instead" ) );
            }

            storedBy = defaultUsername;
        }
        return storedBy;
    }

    private void saveDataValue( ProgramStageInstance programStageInstance, String storedBy, DataElement dataElement,
        String value, Boolean providedElsewhere, TrackedEntityDataValue dataValue, ImportSummary importSummary )
    {
        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        if ( value != null )
        {
            if ( dataValue == null )
            {
                dataValue = new TrackedEntityDataValue( programStageInstance, dataElement, new Date(), value );
                dataValue.setStoredBy( storedBy );
                dataValue.setProvidedElsewhere( providedElsewhere );

                dataValueService.saveTrackedEntityDataValue( dataValue );

                if ( importSummary != null )
                {
                    importSummary.getDataValueCount().incrementImported();
                }
            }
            else
            {
                dataValue.setValue( value );
                dataValue.setTimestamp( new Date() );
                dataValue.setStoredBy( storedBy );
                dataValue.setProvidedElsewhere( providedElsewhere );

                dataValueService.updateTrackedEntityDataValue( dataValue );

                if ( importSummary != null )
                {
                    importSummary.getDataValueCount().incrementUpdated();
                }
            }
        }
        else if ( dataValue != null )
        {
            dataValueService.deleteTrackedEntityDataValue( dataValue );

            if ( importSummary != null )
            {
                importSummary.getDataValueCount().incrementDeleted();
            }
        }
    }

    private ProgramStageInstance createProgramStageInstance( ProgramStage programStage, ProgramInstance programInstance,
        OrganisationUnit organisationUnit, Date dueDate, Date executionDate, int status,
        Coordinate coordinate, String storedBy, String programStageInstanceUid )
    {
        ProgramStageInstance programStageInstance = new ProgramStageInstance();
        programStageInstance.setUid( CodeGenerator.isValidCode( programStageInstanceUid ) ? programStageInstanceUid : CodeGenerator.generateCode() );

        updateProgramStageInstance( programStage, programInstance, organisationUnit, dueDate, executionDate, status,
            coordinate, storedBy, programStageInstance );

        return programStageInstance;
    }

    private void updateProgramStageInstance( ProgramStage programStage, ProgramInstance programInstance,
        OrganisationUnit organisationUnit, Date dueDate, Date executionDate, int status, Coordinate coordinate,
        String storedBy, ProgramStageInstance programStageInstance )
    {
        programStageInstance.setProgramInstance( programInstance );
        programStageInstance.setProgramStage( programStage );
        programStageInstance.setDueDate( dueDate );
        programStageInstance.setExecutionDate( executionDate );
        programStageInstance.setOrganisationUnit( organisationUnit );

        if ( programStage.getCaptureCoordinates() )
        {
            if ( coordinate != null && coordinate.isValid() )
            {
                programStageInstance.setLongitude( coordinate.getLongitude() );
                programStageInstance.setLatitude( coordinate.getLatitude() );
            }
        }

        programStageInstance.setStatus( EventStatus.fromInt( status ) );

        if ( programStageInstance.getId() == 0 )
        {
            programStageInstance.setAutoFields();
            sessionFactory.getCurrentSession().save( programStageInstance );
        }

        if ( programStageInstance.isCompleted() )
        {
            programStageInstance.setStatus( EventStatus.COMPLETED );
            programStageInstance.setCompletedDate( new Date() );
            programStageInstance.setCompletedUser( storedBy );
            programStageInstanceService.completeProgramStageInstance( programStageInstance, i18nManager.getI18nFormat() );
        }
    }

    private ImportSummary saveEvent( Program program, ProgramInstance programInstance, ProgramStage programStage,
        ProgramStageInstance programStageInstance, OrganisationUnit organisationUnit, Event event, User user,
        ImportOptions importOptions )
    {
        Assert.notNull( program );
        Assert.notNull( programInstance );
        Assert.notNull( programStage );

        ImportSummary importSummary = new ImportSummary();
        importSummary.setStatus( ImportStatus.SUCCESS );
        boolean dryRun = importOptions != null && importOptions.isDryRun();

        Date eventDate = DateUtils.parseDate( event.getEventDate() );

        Date dueDate = DateUtils.parseDate( event.getDueDate() );

        String storedBy = getStoredBy( event, importSummary, user.getUsername() );

        if ( !dryRun )
        {
            if ( programStageInstance == null )
            {
                programStageInstance = createProgramStageInstance( programStage, programInstance, organisationUnit,
                    dueDate, eventDate, event.getStatus().getValue(), event.getCoordinate(), storedBy, event.getEvent() );
            }
            else
            {
                updateProgramStageInstance( programStage, programInstance, organisationUnit, dueDate, eventDate, event
                    .getStatus().getValue(), event.getCoordinate(), storedBy, programStageInstance );
            }

            saveTrackedEntityComment( programStageInstance, event, storedBy );

            importSummary.setReference( programStageInstance.getUid() );
        }

        Collection<TrackedEntityDataValue> existingDataValues = dataValueService.getTrackedEntityDataValues( programStageInstance );
        Map<String, TrackedEntityDataValue> dataElementValueMap = getDataElementDataValueMap( existingDataValues );

        for ( DataValue dataValue : event.getDataValues() )
        {
            DataElement dataElement;

            if ( dataElementValueMap.containsKey( dataValue.getDataElement() ) )
            {
                dataElement = dataElementValueMap.get( dataValue.getDataElement() ).getDataElement();
            }
            else
            {
                dataElement = getDataElement( dataValue.getDataElement() );
            }

            if ( dataElement != null )
            {
                if ( validateDataValue( dataElement, dataValue.getValue(), importSummary ) )
                {
                    String dataValueStoredBy = dataValue.getStoredBy() != null ? dataValue.getStoredBy() : storedBy;

                    if ( !dryRun )
                    {
                        TrackedEntityDataValue existingDataValue = dataElementValueMap.get( dataValue.getDataElement() );

                        saveDataValue( programStageInstance, dataValueStoredBy, dataElement, dataValue.getValue(),
                            dataValue.getProvidedElsewhere(), existingDataValue, importSummary );
                    }
                }
            }
            else
            {
                importSummary.getConflicts().add(
                    new ImportConflict( "dataElement", dataValue.getDataElement() + " is not a valid data element" ) );
                importSummary.getDataValueCount().incrementIgnored();
            }
        }

        return importSummary;
    }

    private Map<String, TrackedEntityDataValue> getDataElementDataValueMap( Collection<TrackedEntityDataValue> dataValues )
    {
        Map<String, TrackedEntityDataValue> map = new HashMap<>();

        for ( TrackedEntityDataValue value : dataValues )
        {
            map.put( value.getDataElement().getUid(), value );
        }

        return map;
    }

    private void saveTrackedEntityComment( ProgramStageInstance programStageInstance, Event event, String storedBy )
    {
        for ( Note note : event.getNotes() )
        {
            TrackedEntityComment comment = new TrackedEntityComment();
            comment.setCreator( storedBy );
            comment.setCreatedDate( new Date() );
            comment.setCommentText( note.getValue() );

            commentService.addTrackedEntityComment( comment );

            programStageInstance.getComments().add( comment );

            programStageInstanceService.updateProgramStageInstance( programStageInstance );
        }
    }

    private OrganisationUnit getOrganisationUnit( IdentifiableProperty scheme, String value )
    {
        return organisationUnitCache.get( value, new IdentifiableObjectCallable<>( manager, OrganisationUnit.class, scheme, value ) );
    }

    private Program getProgram( String programId )
    {
        return programCache.get( programId, new IdentifiableObjectCallable<>( manager, Program.class, programId ) );
    }

    private ProgramStage getProgramStage( String programStageId )
    {
        return programStageCache.get( programStageId, new IdentifiableObjectCallable<>( manager, ProgramStage.class, programStageId ) );
    }

    private DataElement getDataElement( String dataElementId )
    {
        return dataElementCache.get( dataElementId, new IdentifiableObjectCallable<>( manager, DataElement.class, dataElementId ) );
    }
}
