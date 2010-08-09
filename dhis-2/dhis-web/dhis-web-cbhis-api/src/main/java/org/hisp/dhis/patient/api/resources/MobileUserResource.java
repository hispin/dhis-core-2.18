package org.hisp.dhis.patient.api.resources;

import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.patient.api.model.OrgUnits;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;

@Path( "v0.1" )
public class MobileUserResource
{

    private static final Log LOG = LogFactory.getLog( MobileUserResource.class );

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private CurrentUserService currentUserService;

    @Context
    private UriInfo uriInfo;

    @GET
    @Produces( { MediaType.APPLICATION_XML, DhisMediaType.ORG_UNITS_XML } )
    public OrgUnits getOrgUnitsForUser()
    {
        User user = currentUserService.getCurrentUser();

        Collection<OrganisationUnit> units = user.getOrganisationUnits();

        if ( units.isEmpty() )
        {
            return null;
        }

        OrgUnits orgUnits = OrgUnits.create( units, uriInfo );

        return orgUnits;

    }

}
