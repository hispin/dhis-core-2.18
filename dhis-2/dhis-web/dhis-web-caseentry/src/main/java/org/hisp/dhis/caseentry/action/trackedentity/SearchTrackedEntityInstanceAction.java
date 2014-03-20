package org.hisp.dhis.caseentry.action.trackedentity;

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

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityAttributeService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentity.comparator.TrackedEntityAttributeSortOrderInListNoProgramComparator;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Abyot Asalefew Gizaw
 */
public class SearchTrackedEntityInstanceAction
    extends ActionPagingSupport<TrackedEntityInstance>
{
    private final String SEARCH_IN_ALL_ORGUNITS = "searchInAllOrgunits";

    private final String SEARCH_IN_USER_ORGUNITS = "searchInUserOrgunits";

    private final String SEARCH_IN_BELOW_SELECTED_ORGUNIT = "searchInBelowSelectedOrgunit";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    private TrackedEntityInstanceService entityInstanceService;

    private ProgramService programService;

    private CurrentUserService currentUserService;

    private OrganisationUnitService organisationUnitService;

    private UserService userService;

    @Autowired
    private TrackedEntityAttributeService attributeService;

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private List<String> searchTexts = new ArrayList<String>();

    private Integer statusEnrollment;

    private String facilityLB;

    private boolean listAll;

    private Collection<TrackedEntityInstance> entityInstances = new ArrayList<TrackedEntityInstance>();

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public void setStatusEnrollment( Integer statusEnrollment )
    {
        this.statusEnrollment = statusEnrollment;
    }

    public void setFacilityLB( String facilityLB )
    {
        this.facilityLB = facilityLB;
    }

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    public void setProgramService( ProgramService programService )
    {
        this.programService = programService;
    }

    public void setEntityInstanceService( TrackedEntityInstanceService entityInstanceService )
    {
        this.entityInstanceService = entityInstanceService;
    }

    public void setSearchTexts( List<String> searchTexts )
    {
        this.searchTexts = searchTexts;
    }

    public boolean isListAll()
    {
        return listAll;
    }

    public void setListAll( boolean listAll )
    {
        this.listAll = listAll;
    }

    public Collection<TrackedEntityInstance> getEntityInstances()
    {
        return entityInstances;
    }

    private Integer total;

    public Integer getTotal()
    {
        return total;
    }

    private Map<Integer, String> mapEntityInstanceOrgunit = new HashMap<Integer, String>();

    public Map<Integer, String> getMapEntityInstanceOrgunit()
    {
        return mapEntityInstanceOrgunit;
    }

    private Integer programId;

    public void setProgramId( Integer programId )
    {
        this.programId = programId;
    }

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    private Map<String, String> mapUsers = new HashMap<String, String>();

    public Map<String, String> getMapUsers()
    {
        return mapUsers;
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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        organisationUnit = selectionManager.getSelectedOrganisationUnit();

        Collection<OrganisationUnit> orgunits = new HashSet<OrganisationUnit>();

        if ( programId != null )
        {
            program = programService.getProgram( programId );
        }
        else
        {
            attributes = new ArrayList<TrackedEntityAttribute>(
                attributeService.getTrackedEntityAttributesDisplayInList( true ) );
            Collections.sort( attributes, new TrackedEntityAttributeSortOrderInListNoProgramComparator() );
        }

        // List all entityInstances
        if ( listAll )
        {
            total = entityInstanceService.countGetTrackedEntityInstancesByOrgUnit( organisationUnit );
            this.paging = createPaging( total );

            entityInstances = new ArrayList<TrackedEntityInstance>( entityInstanceService.getTrackedEntityInstances(
                organisationUnit, paging.getStartPos(), paging.getPageSize() ) );
        }
        // search entityInstances
        else if ( searchTexts.size() > 0 )
        {
            // selected orgunit
            if ( facilityLB == null || facilityLB.isEmpty() )
            {
                orgunits.add( organisationUnit );
            }
            else if ( facilityLB.equals( SEARCH_IN_USER_ORGUNITS ) )
            {
                Collection<OrganisationUnit> userOrgunits = currentUserService.getCurrentUser().getOrganisationUnits();
                orgunits.addAll( userOrgunits );
            }
            else if ( facilityLB.equals( SEARCH_IN_BELOW_SELECTED_ORGUNIT ) )
            {
                Collection<Integer> orgunitIds = organisationUnitService.getOrganisationUnitHierarchy().getChildren(
                    organisationUnit.getId() );
                orgunits.add( organisationUnit );
                orgunits.addAll( organisationUnitService.getOrganisationUnits( orgunitIds ) );
            }
            else if ( facilityLB.equals( SEARCH_IN_ALL_ORGUNITS ) )
            {
                orgunits = null;
            }

            // -----------------------------------------------------------------
            // Users by orgunits for searching
            // -----------------------------------------------------------------

            Collection<User> users = userService.getAllUsers();
            for ( User user : users )
            {
                mapUsers.put( user.getId() + "", user.getName() );
            }

            // -----------------------------------------------------------------
            // Searching
            // -----------------------------------------------------------------

            total = entityInstanceService.countSearchTrackedEntityInstances( searchTexts, orgunits, null,
                statusEnrollment );
            this.paging = createPaging( total );
            entityInstances = entityInstanceService.searchTrackedEntityInstances( searchTexts, orgunits, null, null,
                statusEnrollment, paging.getStartPos(), paging.getPageSize() );

            if ( facilityLB != null && !facilityLB.isEmpty() )
            {
                for ( TrackedEntityInstance entityInstance : entityInstances )
                {
                    mapEntityInstanceOrgunit.put( entityInstance.getId(),
                        getHierarchyOrgunit( entityInstance.getOrganisationUnit() ) );
                }
            }

        }

        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive method
    // -------------------------------------------------------------------------

    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        String hierarchyOrgunit = orgunit.getName();

        while ( orgunit.getParent() != null )
        {
            hierarchyOrgunit = orgunit.getParent().getName() + " / " + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }

        return hierarchyOrgunit;
    }
}
