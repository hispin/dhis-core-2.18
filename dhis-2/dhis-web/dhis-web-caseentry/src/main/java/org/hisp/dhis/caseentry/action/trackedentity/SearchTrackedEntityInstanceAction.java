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

package org.hisp.dhis.caseentry.action.trackedentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentity.comparator.TrackedEntityAttributeSortOrderInListNoProgramComparator;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 * 
 * @version $ SearchTrackedEntityInstanceAction.java Jun 11, 2014 4:28:11 PM $
 */
public class SearchTrackedEntityInstanceAction
    extends ActionPagingSupport<TrackedEntityInstance>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private OrganisationUnitService orgunitService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private TrackedEntityAttributeService attributeService;

    @Autowired
    private TrackedEntityInstanceService entityInstanceService;

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String orgunitId;

    public void setOrgunitId( String orgunitId )
    {
        this.orgunitId = orgunitId;
    }

    private String attributeValue;

    public void setAttributeValue( String attributeValue )
    {
        this.attributeValue = attributeValue;
    }

    private String programId;

    public void setProgramId( String programId )
    {
        this.programId = programId;
    }

    private List<TrackedEntityInstance> entityInstances;

    public List<TrackedEntityInstance> getEntityInstances()
    {
        return entityInstances;
    }

    private Program program;

    public Program getProgram()
    {
        return program;
    }

    private List<TrackedEntityAttribute> attributes;

    public List<TrackedEntityAttribute> getAttributes()
    {
        return attributes;
    }

    private int total;

    public int getTotal()
    {
        return total;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit orgunit = orgunitService.getOrganisationUnit( orgunitId );

        if ( !programId.isEmpty() )
        {
            program = programService.getProgram( programId );
        }
        else
        {
            attributes = new ArrayList<TrackedEntityAttribute>(
                attributeService.getTrackedEntityAttributesDisplayInList() );
            Collections.sort( attributes, new TrackedEntityAttributeSortOrderInListNoProgramComparator() );
        }

        total = entityInstanceService.countTrackedEntityByAttribute( orgunit, attributeValue, program );
        this.paging = createPaging( total );

        entityInstances = new ArrayList<TrackedEntityInstance>( entityInstanceService.searchTrackedEntityByAttribute(
            orgunit, attributeValue, program, paging.getStartPos(), paging.getPageSize() ) );

        return SUCCESS;
    }
}
