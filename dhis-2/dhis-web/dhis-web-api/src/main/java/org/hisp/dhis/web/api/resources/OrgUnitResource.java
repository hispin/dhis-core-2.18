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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.importexport.dxf2.model.OrgUnit;
import org.hisp.dhis.importexport.dxf2.service.OrgUnitMapper;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.web.api.UrlResourceListener;
import org.springframework.beans.factory.annotation.Required;

@Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
@Path( "/orgUnits/{id}" )
public class OrgUnitResource
{
    private OrganisationUnitService organisationUnitService;

    private static Log log = LogFactory.getLog( OrgUnitResource.class );

    @PathParam( "id" )
    private String id;

    @Context
    UriInfo uriInfo;

    @GET
    @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
    public OrgUnit getOrgUnit()
    {
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( id );

        if ( unit == null )
            return null;

        OrgUnit orgUnit = new OrgUnitMapper().get( unit );
        new UrlResourceListener( uriInfo ).beforeMarshal( orgUnit );
        return orgUnit;
    }

    @Required
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

}
