package org.hisp.dhis.oum.action.search;

/*
 * Copyright (c) 2004-2010, University of Oslo
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.organisationunit.comparator.OrganisationUnitGroupSetNameComparator;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class SearchOrganisationUnitsAction
    implements Action
{
    private static final int ANY = 0;

    // -------------------------------------------------------------------------
    // Depdencies
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Input and output
    // -------------------------------------------------------------------------

    private boolean search;
    
    public void setSearch( boolean search )
    {
        this.search = search;
    }

    private String name;
    
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    private Collection<Integer> groupId = new HashSet<Integer>();

    public Collection<Integer> getGroupId()
    {
        return groupId;
    }

    public void setGroupId( Collection<Integer> groupId )
    {
        this.groupId = groupId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnitGroupSet> groupSets;

    public List<OrganisationUnitGroupSet> getGroupSets()
    {
        return groupSets;
    }
    
    private Collection<OrganisationUnit> organisationUnits;

    public Collection<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }
    
    private OrganisationUnit selectedOrganisationUnit;

    public OrganisationUnit getSelectedOrganisationUnit()
    {
        return selectedOrganisationUnit;
    }
    
    boolean limited = false;

    public boolean isLimited()
    {
        return limited;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Assemble groups and get search result
        // ---------------------------------------------------------------------

        if ( search )
        {
            name = StringUtils.trimToNull( name );
            
            selectedOrganisationUnit = selectionManager.getSelectedOrganisationUnit();
            
            Collection<OrganisationUnitGroup> groups = new HashSet<OrganisationUnitGroup>();
            
            for ( Integer id : groupId )
            {
                if ( id != ANY )
                {
                    OrganisationUnitGroup group = organisationUnitGroupService.getOrganisationUnitGroup( id );
                    groups.add( group );
                }
            }
        
            organisationUnits = organisationUnitService.getOrganisationUnitsByNameAndGroups( name, groups, selectedOrganisationUnit, true );
            
            limited = organisationUnits != null && organisationUnits.size() == OrganisationUnitService.MAX_LIMIT;
        }
        
        // ---------------------------------------------------------------------
        // Get group sets
        // ---------------------------------------------------------------------

        groupSets = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getCompulsoryOrganisationUnitGroupSets() );
        
        Collections.sort( groupSets, new OrganisationUnitGroupSetNameComparator() );
        
        return SUCCESS;
    }
}
