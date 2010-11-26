package org.hisp.dhis.web.api.resources;

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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.web.api.model.OrgUnit;
import org.hisp.dhis.web.api.model.OrgUnits;
import org.springframework.beans.factory.annotation.Required;

import com.sun.jersey.api.core.ResourceContext;

@Path( "/" )
@Produces( { DhisMediaType.MOBILE_SERIALIZED, MediaType.APPLICATION_XML } )
public class MobileResource
{

    // Dependencies

    private CurrentUserService currentUserService;

    private OrganisationUnitService organisationUnitService;

    @Context
    UriInfo uriInfo;

    @Context
    private ResourceContext rc;

    @GET
    @Path( "mobile" )
    public OrgUnits getOrgUnitsForUser()
    {
        User user = currentUserService.getCurrentUser();

        Collection<OrganisationUnit> units = user.getOrganisationUnits();

        OrgUnits orgUnits = new OrgUnits();

        List<OrgUnit> unitList = new ArrayList<OrgUnit>();
        
        for ( OrganisationUnit unit : units )
        {
            unitList.add( getOrgUnit( unit ) );
        }

        orgUnits.setOrgUnits( unitList );

        return orgUnits;
    }

    @Path( "orgUnits/{id}" )
    public OrgUnitResource getOrgUnit( @PathParam( "id" ) int id )
    {

        OrgUnitResource resource = rc.getResource( OrgUnitResource.class );

        resource.setOrgUnit( organisationUnitService.getOrganisationUnit( id ) );

        return resource;
    }

    private OrgUnit getOrgUnit( OrganisationUnit unit )
    {
        OrgUnit m = new OrgUnit();

        m.setId( unit.getId() );
        m.setName( unit.getShortName() );

        m.setDownloadAllUrl( uriInfo.getBaseUriBuilder().path( "/orgUnits/{id}" ).path( "all" ).build( unit.getId() )
            .toString() );
        m.setDownloadActivityPlanUrl( uriInfo.getBaseUriBuilder().path( "/orgUnits/{id}" ).path( "activitiyplan" )
            .build( unit.getId() ).toString() );
        m.setUploadFacilityReportUrl( uriInfo.getBaseUriBuilder().path( "/orgUnits/{id}" ).path( "dataSets" )
            .build( unit.getId() ).toString() );
        m.setUploadActivityReportUrl( uriInfo.getBaseUriBuilder().path( "/orgUnits/{id}" ).path( "activities" )
            .build( unit.getId() ).toString() );

        return m;
    }

    // Setters...

    @Required
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    @Required
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

}
