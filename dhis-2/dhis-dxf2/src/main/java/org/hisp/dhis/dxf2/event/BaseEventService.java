package org.hisp.dhis.dxf2.event;

/*
 * Copyright (c) 2004-2013, University of Oslo
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

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dxf2.InputValidationService;
import org.hisp.dhis.dxf2.importsummary.ImportConflict;
import org.hisp.dhis.dxf2.importsummary.ImportStatus;
import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.i18n.I18nManagerException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patientdatavalue.PatientDataValue;
import org.hisp.dhis.patientdatavalue.PatientDataValueService;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Date;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public abstract class BaseEventService implements EventService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private ProgramService programService;

    @Autowired
    private ProgramInstanceService programInstanceService;

    @Autowired
    private ProgramStageInstanceService programStageInstanceService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private PatientDataValueService patientDataValueService;

    @Autowired
    private InputValidationService inputValidationService;

    @Autowired
    private I18nManager i18nManager;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    protected ImportSummary saveEvent( Event event )
    {
        Program program = programService.getProgram( event.getId() );

        if ( program == null )
        {
            return new ImportSummary( ImportStatus.ERROR, "Event ID does not point to a valid program." );
        }
        else
        {
            Collection<Program> programsByCurrentUser = programService.getProgramsByCurrentUser();

            if ( !programsByCurrentUser.contains( program ) )
            {
                return new ImportSummary( ImportStatus.ERROR, "Current user does not have permission to access this program." );
            }
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( event.getOrganisationUnitId() );

        if ( organisationUnit == null )
        {
            return new ImportSummary( ImportStatus.ERROR, "Event organisationUnitId does not point to a valid organisation unit." );
        }
        else
        {
            boolean assignedToOrganisationUnit = false;

            if ( program.getOrganisationUnits().contains( organisationUnit ) )
            {
                assignedToOrganisationUnit = true;
            }
            else
            {
                for ( OrganisationUnitGroup organisationUnitGroup : program.getOrganisationUnitGroups() )
                {
                    if ( organisationUnitGroup.getMembers().contains( organisationUnit ) )
                    {
                        assignedToOrganisationUnit = true;
                        break;
                    }
                }
            }

            if ( !assignedToOrganisationUnit )
            {
                return new ImportSummary( ImportStatus.ERROR, "Program is not assigned to this organisation unit." );
            }
        }

        if ( program.getType() == Program.SINGLE_EVENT_WITHOUT_REGISTRATION )
        {
            return saveSingleEventWithoutRegistration( program, organisationUnit, event );
        }
        else if ( program.getType() == Program.SINGLE_EVENT_WITH_REGISTRATION )
        {
            return saveSingleEventWithRegistration( program, organisationUnit, event );
        }
        else if ( program.getType() == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
        {
            return saveMultipleEventsWithRegistration( program, organisationUnit, event );
        }

        return new ImportSummary();
    }

    private ImportSummary saveSingleEventWithoutRegistration( Program program, OrganisationUnit organisationUnit, Event event )
    {
        try
        {
            format = i18nManager.getI18nFormat();
        }
        catch ( I18nManagerException ex )
        {
            return new ImportSummary( ImportStatus.ERROR, ex.getMessage() );
        }

        Date eventDate = format.parseDate( event.getEventDate() );

        if ( eventDate == null )
        {
            return new ImportSummary( ImportStatus.ERROR, "Event eventDate is not in a valid format." );
        }

        ImportSummary importSummary = new ImportSummary();
        importSummary.setStatus( ImportStatus.SUCCESS );

        ProgramStageInstance programStageInstance = saveEventDate( program, organisationUnit, eventDate,
            event.getCompleted(), event.getCoordinate() );

        String storedBy = event.getStoredBy();

        if ( storedBy == null )
        {
            storedBy = currentUserService.getCurrentUsername();
        }
        else if ( storedBy.length() >= 31 )
        {
            importSummary.getConflicts().add( new ImportConflict( "storedBy", storedBy + " is more than 31 characters, using current username instead." ) );
            storedBy = currentUserService.getCurrentUsername();
        }

        for ( DataValue dataValue : event.getDataValues() )
        {
            DataElement dataElement = dataElementService.getDataElement( dataValue.getDataElementId() );

            if ( dataElement == null )
            {
                importSummary.getConflicts().add( new ImportConflict( "dataElementId", dataValue.getDataElementId() + " is not a valid dataElementId." ) );
                importSummary.getDataValueCount().incrementIgnored();
            }
            else
            {
                if ( validateDataElement( dataElement, dataValue.getValue(), importSummary ) )
                {
                    saveDataValue( programStageInstance, storedBy, dataElement, dataValue.getValue(), dataValue.getProvidedElsewhere() );
                    importSummary.getDataValueCount().incrementImported();
                }
            }
        }

        return importSummary;
    }

    private boolean validateDataElement( DataElement dataElement, String value, ImportSummary importSummary )
    {
        InputValidationService.Status status = inputValidationService.validateDataElement( dataElement, value );

        if ( !status.isSuccess() )
        {
            importSummary.getConflicts().add( new ImportConflict( dataElement.getUid(), status.getMessage() ) );
            importSummary.getDataValueCount().incrementIgnored();
            return false;
        }

        return true;
    }

    private ImportSummary saveSingleEventWithRegistration( Program program, OrganisationUnit organisationUnit, Event event )
    {
        return new ImportSummary();
    }

    private ImportSummary saveMultipleEventsWithRegistration( Program program, OrganisationUnit organisationUnit, Event event )
    {
        return new ImportSummary();
    }

    private ProgramStageInstance saveEventDate( Program program, OrganisationUnit organisationUnit, Date date, Boolean completed,
        Coordinate coordinate )
    {
        ProgramStage programStage = program.getProgramStages().iterator().next();
        ProgramInstance programInstance = programInstanceService.getProgramInstances( program ).iterator().next();

        ProgramStageInstance programStageInstance = new ProgramStageInstance();
        programStageInstance.setProgramInstance( programInstance );
        programStageInstance.setProgramStage( programStage );
        programStageInstance.setDueDate( date );
        programStageInstance.setExecutionDate( date );
        programStageInstance.setOrganisationUnit( organisationUnit );

        if ( programStage.getCaptureCoordinates() )
        {
            if ( coordinate.isValid() )
            {
                programStageInstance.setCoordinates( coordinate.getCoordinateString() );
            }
            else
            {
                programStageInstance.setCoordinates( null );
            }
        }

        if ( completed != null )
        {
            programStageInstance.setCompleted( completed );
            programStageInstance.setCompletedDate( new Date() );
            programStageInstance.setCompletedUser( currentUserService.getCurrentUsername() );
        }

        programStageInstanceService.addProgramStageInstance( programStageInstance );

        return programStageInstance;
    }

    private void saveDataValue( ProgramStageInstance programStageInstance, String storedBy, DataElement dataElement, String value, Boolean providedElsewhere )
    {
        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        PatientDataValue patientDataValue = patientDataValueService.getPatientDataValue( programStageInstance, dataElement );

        if ( value != null )
        {
            if ( patientDataValue == null )
            {
                patientDataValue = new PatientDataValue( programStageInstance, dataElement, new Date(), value );
                patientDataValue.setStoredBy( storedBy );
                patientDataValue.setProvidedElsewhere( providedElsewhere );

                patientDataValueService.savePatientDataValue( patientDataValue );
            }
            else
            {
                patientDataValue.setValue( value );
                patientDataValue.setTimestamp( new Date() );
                patientDataValue.setProvidedElsewhere( providedElsewhere );
                patientDataValue.setStoredBy( storedBy );

                patientDataValueService.updatePatientDataValue( patientDataValue );
            }
        }
        else if ( patientDataValue != null )
        {
            patientDataValueService.deletePatientDataValue( patientDataValue );
        }
    }
}
