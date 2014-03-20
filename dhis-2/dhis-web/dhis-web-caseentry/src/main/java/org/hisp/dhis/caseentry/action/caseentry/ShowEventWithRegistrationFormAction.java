package org.hisp.dhis.caseentry.action.caseentry;

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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramDataEntryService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageDataElement;
import org.hisp.dhis.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeGroup;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeGroupService;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityForm;
import org.hisp.dhis.trackedentity.TrackedEntityFormService;
import org.hisp.dhis.trackedentity.comparator.TrackedEntityAttributeGroupSortOrderComparator;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 */
public class ShowEventWithRegistrationFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private ProgramService programService;

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    private TrackedEntityFormService trackedEntityFormService;

    public void setTrackedEntityFormService( TrackedEntityFormService trackedEntityFormService )
    {
        this.trackedEntityFormService = trackedEntityFormService;
    }

    private ProgramDataEntryService programDataEntryService;

    public void setProgramDataEntryService( ProgramDataEntryService programDataEntryService )
    {
        this.programDataEntryService = programDataEntryService;
    }

    private TrackedEntityAttributeService attributeService;

    public void setAttributeService( TrackedEntityAttributeService attributeService )
    {
        this.attributeService = attributeService;
    }

    private TrackedEntityAttributeGroupService attributeGroupService;

    public void setAttributeGroupService( TrackedEntityAttributeGroupService attributeGroupService )
    {
        this.attributeGroupService = attributeGroupService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private Integer programId;

    private Collection<TrackedEntityAttribute> noGroupAttributes = new HashSet<TrackedEntityAttribute>();

    private OrganisationUnit organisationUnit;

    private String customDataEntryFormCode;

    private List<ProgramStageDataElement> programStageDataElements = new ArrayList<ProgramStageDataElement>();

    private ProgramStage programStage;

    private Collection<User> healthWorkers;

    private String customRegistrationForm;

    private List<TrackedEntityAttributeGroup> attributeGroups;

    private Map<Integer, Collection<TrackedEntityAttribute>> attributeGroupsMap = new HashMap<Integer, Collection<TrackedEntityAttribute>>();

    private Program program;

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        // Get health workers
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        healthWorkers = organisationUnit.getUsers();

        program = programService.getProgram( programId );
        TrackedEntityForm trackedEntityForm = trackedEntityFormService.getTrackedEntityForm( program );

        if ( trackedEntityForm != null )
        {
            customRegistrationForm = trackedEntityFormService.prepareDataEntryFormForAdd( trackedEntityForm
                .getDataEntryForm().getHtmlCode(), trackedEntityForm.getProgram(), healthWorkers, null, null, i18n,
                format );
        }

        if ( customRegistrationForm == null )
        {
            Collection<TrackedEntityAttribute> attributesInProgram = new HashSet<TrackedEntityAttribute>();
            Collection<Program> programs = programService.getAllPrograms();
            programs.remove( program );
            for ( Program p : programs )
            {
                for ( ProgramTrackedEntityAttribute programAttribute : p.getAttributes() )
                {
                    attributesInProgram.add( programAttribute.getAttribute() );
                }
            }

            attributeGroups = new ArrayList<TrackedEntityAttributeGroup>(
                attributeGroupService.getAllTrackedEntityAttributeGroups() );
            Collections.sort( attributeGroups, new TrackedEntityAttributeGroupSortOrderComparator() );
            for ( TrackedEntityAttributeGroup attributeGroup : attributeGroups )
            {
                List<TrackedEntityAttribute> attributes = attributeGroupService
                    .getTrackedEntityAttributes( attributeGroup );
                attributes.removeAll( attributesInProgram );

                if ( attributes.size() > 0 )
                {
                    attributeGroupsMap.put( attributeGroup.getId(), attributes );
                }
            }

            noGroupAttributes = attributeService.getTrackedEntityAttributesWithoutGroup();
            noGroupAttributes.removeAll( attributesInProgram );
        }

        // Get data entry form
        programStage = program.getProgramStages().iterator().next();
        if ( programStage.getDataEntryForm() != null )
        {
            customDataEntryFormCode = programDataEntryService.prepareDataEntryFormForAdd( programStage
                .getDataEntryForm().getHtmlCode(), i18n, programStage );
        }
        else
        {
            programStageDataElements = new ArrayList<ProgramStageDataElement>(
                programStage.getProgramStageDataElements() );
        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Getter/Setter
    // -------------------------------------------------------------------------
    
    public Program getProgram()
    {
        return program;
    }

    public Collection<User> getHealthWorkers()
    {
        return healthWorkers;
    }

    public String getCustomRegistrationForm()
    {
        return customRegistrationForm;
    }

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    public ProgramStage getProgramStage()
    {
        return programStage;
    }

    public Collection<TrackedEntityAttribute> getNoGroupAttributes()
    {
        return noGroupAttributes;
    }

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public String getCustomDataEntryFormCode()
    {
        return customDataEntryFormCode;
    }

    public List<ProgramStageDataElement> getProgramStageDataElements()
    {
        return programStageDataElements;
    }

    public List<TrackedEntityAttributeGroup> getAttributeGroups()
    {
        return attributeGroups;
    }

    public Map<Integer, Collection<TrackedEntityAttribute>> getAttributeGroupsMap()
    {
        return attributeGroupsMap;
    }
}
