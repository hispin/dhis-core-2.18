package org.hisp.dhis.dataprune;

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
import java.util.List;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Quang Nguyen
 * @version Apr 6, 2010 5:48:15 PM
 */

public class DefaultDataPruneService
    implements DataPruneService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataPruneStore dataPruneStore;

    public void setDataPruneStore( DataPruneStore dataPruneStore )
    {
        this.dataPruneStore = dataPruneStore;
    }

    // -------------------------------------------------------------------------
    // DataPruneService implementation
    // -------------------------------------------------------------------------

    @Transactional
    public int pruneOrganisationUnit( OrganisationUnit organisationUnit )
    {
        deleteLevels( organisationUnit );

        if ( organisationUnit.getParent() != null )
        {
            organisationUnit.setParent( null );
            organisationUnitService.updateOrganisationUnit( organisationUnit );
        }

        List<OrganisationUnit> deletedOrgUnits = pruneOrganisationUnitLocal( organisationUnit );
        
        return dataPruneStore.deleteMultiOrganisationUnit( deletedOrgUnits );
    }

    private void deleteLevels( OrganisationUnit organisationUnit )
    {
        if ( organisationUnit.getParent() != null )
        {
            OrganisationUnitLevel level = organisationUnitService
                .getOrganisationUnitLevelByLevel( organisationUnitService.getLevelOfOrganisationUnit( organisationUnit.getParent().getId() ) );

            if ( level != null )
            {
                organisationUnitService.deleteOrganisationUnitLevel( level );
            }
        }
        
        if ( organisationUnit.getParent().getParent() != null )
        {
            deleteLevels( organisationUnit.getParent() );
        }
    }
    
    private List<OrganisationUnit> pruneOrganisationUnitLocal( OrganisationUnit organisationUnit )
    {
        List<OrganisationUnit> deleteOrgUnits = new ArrayList<OrganisationUnit>();

        for ( OrganisationUnit eachRoot : organisationUnitService.getRootOrganisationUnits() )
        {
            if ( !eachRoot.equals( organisationUnit ) )
            {
                deleteBranch( eachRoot, deleteOrgUnits );
            }
        }

        return deleteOrgUnits;
    }

    private void deleteBranch( OrganisationUnit organisationUnit, List<OrganisationUnit> deletedOrgUnits )
    {
        if ( !organisationUnit.getChildren().isEmpty() )
        {
            Set<OrganisationUnit> tmp = organisationUnit.getChildren();
            Object[] childrenAsArray = tmp.toArray();

            for ( Object eachChild : childrenAsArray )
            {
                deleteBranch( (OrganisationUnit) eachChild, deletedOrgUnits );
            }
        }

        deletedOrgUnits.add( organisationUnit );
    }
}
