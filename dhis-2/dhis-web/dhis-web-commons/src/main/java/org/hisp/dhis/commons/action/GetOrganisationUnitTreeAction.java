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
import java.util.UUID;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.version.Version;
import org.hisp.dhis.version.VersionService;

import com.opensymphony.xwork2.Action;

/**
 * @author mortenoh
 */
public class GetOrganisationUnitTreeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private VersionService versionService;

    public void setVersionService( VersionService versionService )
    {
        this.versionService = versionService;
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

    private List<OrganisationUnit> organisationUnits;

    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    private String version;

    public String getVersion()
    {
        return version;
    }

    private Boolean versionOnly = false;

    public void setVersionOnly( Boolean versionOnly )
    {
        this.versionOnly = versionOnly;
    }

    public Boolean getVersionOnly()
    {
        return versionOnly;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        if ( !versionOnly )
        {
            User user = currentUserService.getCurrentUser();

            if ( user.getOrganisationUnits() != null && user.getOrganisationUnits().size() > 0 )
            {
                organisationUnits = new ArrayList<OrganisationUnit>( user.getOrganisationUnits() );
            }
            else
            {
                if ( user.getOrganisationUnits() != null && currentUserService.currentUserIsSuper() )
                {
                    organisationUnits = new ArrayList<OrganisationUnit>(
                        organisationUnitService.getRootOrganisationUnits() );
                }
                else
                {
                    organisationUnits = new ArrayList<OrganisationUnit>();
                }
            }

            Collections.sort( organisationUnits, organisationUnitComparator );
        }

        version = getVersionString();

        return SUCCESS;
    }

    private String getVersionString()
    {
        Version orgUnitVersion = versionService.getVersionByKey( VersionService.ORGANISATIONUNIT_VERSION );

        if ( orgUnitVersion == null )
        {
            String uuid = UUID.randomUUID().toString();
            orgUnitVersion = new Version();
            orgUnitVersion.setKey( VersionService.ORGANISATIONUNIT_VERSION );
            orgUnitVersion.setValue( uuid );
            versionService.addVersion( orgUnitVersion );
        }

        return orgUnitVersion.getValue();
    }
}
