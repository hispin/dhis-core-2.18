package org.hisp.dhis.web.api.resources;

import java.net.URI;
import java.util.Collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.sun.jersey.api.core.ResourceContext;

/**
 * This resource redirects the logged in user to the OrgUnit it is assigned to.
 * <p>It is possible to be assigned to more than one org unit, and in that case 
 * a 409 will be sent back.
 */
@Path( "user" )
public class UserResource
{

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private CurrentUserService currentUserService;

    @Context
    private UriInfo uriInfo;

    @Context
    private ResourceContext rc;

    @GET
    @Produces( MediaType.APPLICATION_XML )
    public Response getOrgUnitForUser()
    {
        User user = currentUserService.getCurrentUser();

        Collection<OrganisationUnit> units = user.getOrganisationUnits();

        if ( units.isEmpty() )
        {
            return Response.status( Status.NOT_FOUND ).build();
        }
        else if ( units.size() > 1 )
        {
            StringBuilder sb = new StringBuilder("User is registered to more than one unit: ");
            
            int i = units.size();
            for ( OrganisationUnit unit : units )
            {
                sb.append( unit.getName() );
                if (i-- > 1)
                    sb.append( ", " );
            }
            
            return Response.status( Status.CONFLICT ).entity( sb.toString() ).build();
        }
        
        OrganisationUnit unit = units.iterator().next();
        URI uri = uriInfo.getBaseUriBuilder().path( "orgUnits/{id}" ).build( unit.getId() );

        return Response.seeOther( uri ).build();
    }

}
