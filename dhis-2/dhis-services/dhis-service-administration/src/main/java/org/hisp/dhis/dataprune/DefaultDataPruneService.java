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

import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.hierarchy.HierarchyViolationException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Quang Nguyen
 * @version Apr 6, 2010 5:48:15 PM
 */

public class DefaultDataPruneService
    implements DataPruneService
{
    private static final Log log = LogFactory.getLog( DefaultDataPruneService.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    // -------------------------------------------------------------------------
    // DataPruneService implementation
    // -------------------------------------------------------------------------

    @Transactional
    public void pruneOrganisationUnit( OrganisationUnit organisationUnit )
    {
        if ( organisationUnit.getParent() != null )
        {
            organisationUnit.setParent( null );
            organisationUnitService.updateOrganisationUnit( organisationUnit );
        }
        
        for(OrganisationUnit eachRoot : organisationUnitService.getRootOrganisationUnits())
        {
            if(!eachRoot.equals( organisationUnit ))
            {
                deleteABranch( eachRoot );
            }
        }
    }
    
    private void deleteABranch(OrganisationUnit organisationUnit) {
        if(!organisationUnit.getChildren().isEmpty()) {
            Set<OrganisationUnit> tmp = organisationUnit.getChildren();
            Object[] childrenAsArray = tmp.toArray();
            
            for ( Object eachChild : childrenAsArray )
            {
                deleteABranch( (OrganisationUnit)eachChild );
            }
        }
        
        dataValueService.deleteDataValuesBySource( organisationUnit );
        
        try
        {
            organisationUnitService.deleteOrganisationUnit( organisationUnit );
        }
        catch ( HierarchyViolationException e )
        {
            e.printStackTrace();
        }
    }
}
