package org.hisp.dhis.mobile.web.resources;

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
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.mobile.api.NotAllowedException;
import org.hisp.dhis.mobile.api.model.MobileOrgUnitLinks;
import org.hisp.dhis.mobile.api.model.OrgUnits;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Required;

@Path( "/mobile" )
@Produces( { MobileMediaTypes.MOBILE_SERIALIZED, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
public class MobileResource
{

    // Dependencies

    private CurrentUserService currentUserService;

    @Context
    UriInfo uriInfo;

    /**
     * Get the units associated with the currently logged in user
     * 
     * @throws NotAllowedException if no user is logged in
     */
    @GET
    public OrgUnits getOrgUnitsForUser()
        throws NotAllowedException
    {
        User user = currentUserService.getCurrentUser();

        if ( user == null )
        {
            throw NotAllowedException.NO_USER;
        }

        Collection<OrganisationUnit> units = user.getOrganisationUnits();

        List<MobileOrgUnitLinks> unitList = new ArrayList<MobileOrgUnitLinks>();
        for ( OrganisationUnit unit : units )
        {
            unitList.add( OrgUnitResource.getOrgUnit( unit, uriInfo ) );
        }

        return new OrgUnits( unitList );
    }

    @Required
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

}
