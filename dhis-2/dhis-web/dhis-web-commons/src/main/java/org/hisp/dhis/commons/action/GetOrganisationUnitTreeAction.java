package org.hisp.dhis.commons.action;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import com.opensymphony.xwork2.Action;

/**
 * @author mortenoh
 */
public class GetOrganisationUnitTreeAction
    implements Action
{
    private final static int ALL = 0;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Comparator
    // -------------------------------------------------------------------------

    private Comparator<OrganisationUnit> organisationUnitComparator;

    public void setOrganisationUnitComparator( Comparator<OrganisationUnit> organisationUnitComparator )
    {
        this.organisationUnitComparator = organisationUnitComparator;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer level;

    public void setLevel( Integer level )
    {
        this.level = level;
    }

    private List<OrganisationUnit> organisationUnits;

    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    public class SetSorter<T>
    {
        private Comparator<T> comparator;

        public SetSorter( Comparator<T> comparator )
        {
            this.comparator = comparator;
        }

        public Set<T> sort( Set<T> unsortedSet )
        {
            SortedSet<T> sortedSet = new TreeSet<T>( comparator );
            sortedSet.addAll( unsortedSet );

            return sortedSet;
        }
    }

    public SetSorter<OrganisationUnit> getSorter()
    {
        return new SetSorter<OrganisationUnit>( organisationUnitComparator );
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( level == null || level == ALL )
        {
            organisationUnits = new ArrayList<OrganisationUnit>(
                organisationUnitService.getOrganisationUnitsAtLevel( 1 ) );
        }
        else
        {
            organisationUnits = new ArrayList<OrganisationUnit>(
                organisationUnitService.getOrganisationUnitsAtLevel( level ) );
        }

        Collections.sort( organisationUnits, organisationUnitComparator );

        return SUCCESS;
    }
}
