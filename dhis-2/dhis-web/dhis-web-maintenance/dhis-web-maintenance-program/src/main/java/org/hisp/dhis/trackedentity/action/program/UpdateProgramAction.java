package org.hisp.dhis.trackedentity.action.program;

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

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.relationship.RelationshipType;
import org.hisp.dhis.relationship.RelationshipTypeService;
import org.hisp.dhis.trackedentity.TrackedEntity;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew Gizaw
 * @version $Id$
 */
public class UpdateProgramAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private TrackedEntityAttributeService attributeService;

    public void setAttributeService( TrackedEntityAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private RelationshipTypeService relationshipTypeService;

    public void setRelationshipTypeService( RelationshipTypeService relationshipTypeService )
    {
        this.relationshipTypeService = relationshipTypeService;
    }

    @Autowired
    private TrackedEntityService trackedEntityService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private int id;

    public void setId( int id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private Integer version;

    public Integer getVersion()
    {
        return version;
    }

    public void setVersion( Integer version )
    {
        this.version = version;
    }

    private String dateOfEnrollmentDescription;

    public void setDateOfEnrollmentDescription( String dateOfEnrollmentDescription )
    {
        this.dateOfEnrollmentDescription = dateOfEnrollmentDescription;
    }

    private String dateOfIncidentDescription;

    public void setDateOfIncidentDescription( String dateOfIncidentDescription )
    {
        this.dateOfIncidentDescription = dateOfIncidentDescription;
    }

    private Integer type;

    public void setType( Integer type )
    {
        this.type = type;
    }

    private Boolean displayProvidedOtherFacility;

    public void setDisplayProvidedOtherFacility( Boolean displayProvidedOtherFacility )
    {
        this.displayProvidedOtherFacility = displayProvidedOtherFacility;
    }

    private Boolean displayIncidentDate;

    public void setDisplayIncidentDate( Boolean displayIncidentDate )
    {
        this.displayIncidentDate = displayIncidentDate;
    }

    private List<String> selectedPropertyIds = new ArrayList<String>();

    public void setSelectedPropertyIds( List<String> selectedPropertyIds )
    {
        this.selectedPropertyIds = selectedPropertyIds;
    }

    private List<Boolean> personDisplayNames = new ArrayList<Boolean>();

    public void setPersonDisplayNames( List<Boolean> personDisplayNames )
    {
        this.personDisplayNames = personDisplayNames;
    }

    private Boolean generateBydEnrollmentDate;

    public void setGeneratedByEnrollmentDate( Boolean generateBydEnrollmentDate )
    {
        this.generateBydEnrollmentDate = generateBydEnrollmentDate;
    }

    private Boolean ignoreOverdueEvents;

    public void setIgnoreOverdueEvents( Boolean ignoreOverdueEvents )
    {
        this.ignoreOverdueEvents = ignoreOverdueEvents;
    }

    private Boolean blockEntryForm;

    public void setBlockEntryForm( Boolean blockEntryForm )
    {
        this.blockEntryForm = blockEntryForm;
    }

    private Boolean onlyEnrollOnce = false;

    public void setOnlyEnrollOnce( Boolean onlyEnrollOnce )
    {
        this.onlyEnrollOnce = onlyEnrollOnce;
    }

    private Boolean remindCompleted = false;

    public void setRemindCompleted( Boolean remindCompleted )
    {
        this.remindCompleted = remindCompleted;
    }

    private Boolean displayOnAllOrgunit;

    public void setDisplayOnAllOrgunit( Boolean displayOnAllOrgunit )
    {
        this.displayOnAllOrgunit = displayOnAllOrgunit;
    }

    private Boolean selectEnrollmentDatesInFuture;

    public void setSelectEnrollmentDatesInFuture( Boolean selectEnrollmentDatesInFuture )
    {
        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
    }

    private Boolean selectIncidentDatesInFuture;

    public void setSelectIncidentDatesInFuture( Boolean selectIncidentDatesInFuture )
    {
        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
    }

    private String relationshipText;

    public void setRelationshipText( String relationshipText )
    {
        this.relationshipText = relationshipText;
    }

    private Integer relationshipTypeId;

    public void setRelationshipTypeId( Integer relationshipTypeId )
    {
        this.relationshipTypeId = relationshipTypeId;
    }

    private Boolean relationshipFromA;

    public void setRelationshipFromA( Boolean relationshipFromA )
    {
        this.relationshipFromA = relationshipFromA;
    }

    private Integer relatedProgramId;

    public void setRelatedProgramId( Integer relatedProgramId )
    {
        this.relatedProgramId = relatedProgramId;
    }

    private Boolean dataEntryMethod;

    public void setDataEntryMethod( Boolean dataEntryMethod )
    {
        this.dataEntryMethod = dataEntryMethod;
    }

    private Integer trackedEntityId;

    public void setTrackedEntityId( Integer trackedEntityId )
    {
        this.trackedEntityId = trackedEntityId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        displayProvidedOtherFacility = (displayProvidedOtherFacility == null) ? false : displayProvidedOtherFacility;
        displayIncidentDate = (displayIncidentDate == null) ? false : displayIncidentDate;
        generateBydEnrollmentDate = (generateBydEnrollmentDate == null) ? false : generateBydEnrollmentDate;
        ignoreOverdueEvents = (ignoreOverdueEvents == null) ? false : ignoreOverdueEvents;
        blockEntryForm = (blockEntryForm == null) ? false : blockEntryForm;
        remindCompleted = (remindCompleted == null) ? false : remindCompleted;
        displayOnAllOrgunit = (displayOnAllOrgunit == null) ? false : displayOnAllOrgunit;
        selectEnrollmentDatesInFuture = (selectEnrollmentDatesInFuture == null) ? false : selectEnrollmentDatesInFuture;
        selectIncidentDatesInFuture = (selectIncidentDatesInFuture == null) ? false : selectIncidentDatesInFuture;
        dataEntryMethod = (dataEntryMethod == null) ? false : dataEntryMethod;

        Program program = programService.getProgram( id );
        program.setName( name );
        program.setDescription( description );
        program.setVersion( version );
        program.setDateOfEnrollmentDescription( dateOfEnrollmentDescription );
        program.setDateOfIncidentDescription( dateOfIncidentDescription );
        program.setType( type );
        program.setDisplayIncidentDate( displayIncidentDate );
        program.setOnlyEnrollOnce( onlyEnrollOnce );
        program.setDisplayOnAllOrgunit( displayOnAllOrgunit );
        program.setSelectEnrollmentDatesInFuture( selectEnrollmentDatesInFuture );
        program.setSelectIncidentDatesInFuture( selectIncidentDatesInFuture );
        program.setDataEntryMethod( dataEntryMethod );

        if ( type == Program.MULTIPLE_EVENTS_WITH_REGISTRATION )
        {
            program.setIgnoreOverdueEvents( ignoreOverdueEvents );
        }
        else
        {
            program.setIgnoreOverdueEvents( false );
        }

        if ( relatedProgramId != null )
        {
            Program relatedProgram = programService.getProgram( relatedProgramId );
            program.setRelatedProgram( relatedProgram );
        }

        if ( relationshipTypeId != null )
        {
            RelationshipType relationshipType = relationshipTypeService.getRelationshipType( relationshipTypeId );
            program.setRelationshipType( relationshipType );
        }

        program.setRelationshipFromA( relationshipFromA );
        program.setRelationshipText( relationshipText );

        if ( trackedEntityId != null )
        {
            TrackedEntity trackedEntity = trackedEntityService.getTrackedEntity( trackedEntityId );
            program.setTrackedEntity( trackedEntity );
        }
        else if ( program.getTrackedEntity() != null )
        {
            program.setTrackedEntity( null );
        }

        if ( program.getAttributes() != null )
        {
            program.getAttributes().clear();
        }

        int index = 0;

        for ( String selectedPropertyId : selectedPropertyIds )
        {
            String[] ids = selectedPropertyId.split( "_" );

            if ( ids[0].equals( TrackedEntityInstance.PREFIX_TRACKED_ENTITY_ATTRIBUTE ) )
            {
                TrackedEntityAttribute attribute = attributeService.getTrackedEntityAttribute( Integer
                    .parseInt( ids[1] ) );
                ProgramTrackedEntityAttribute programAttribute = new ProgramTrackedEntityAttribute( attribute,
                    index + 1, personDisplayNames.get( index ) );
                program.getAttributes().add( programAttribute );
            }

            index++;
        }

        if ( relatedProgramId != null )
        {
            Program relatedProgram = programService.getProgram( relatedProgramId );
            program.setRelatedProgram( relatedProgram );
        }

        programService.updateProgram( program );

        return SUCCESS;
    }
}
