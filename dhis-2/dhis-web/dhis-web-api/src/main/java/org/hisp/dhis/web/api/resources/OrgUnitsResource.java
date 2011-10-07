package org.hisp.dhis.web.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.importexport.dxf2.model.OrgUnitLinks;
import org.hisp.dhis.importexport.dxf2.service.LinkBuilder;
import org.hisp.dhis.importexport.dxf2.service.LinkBuilderImpl;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.system.velocity.VelocityManager;
import org.hisp.dhis.web.api.ResponseUtils;
import org.hisp.dhis.web.api.UrlResourceListener;
import org.springframework.beans.factory.annotation.Required;

@Path( "orgUnits" )
public class OrgUnitsResource
{
    private OrganisationUnitService organisationUnitService;

    private VelocityManager velocityManager;

    private LinkBuilder linkBuilder = new LinkBuilderImpl();

    @Context
    private UriInfo uriInfo;
    
    @GET
    @Produces( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
    public OrgUnitLinks getOrgUnits()
    {
        OrgUnitLinks orgUnitLinks = new OrgUnitLinks( linkBuilder.getLinks( organisationUnitService.getAllOrganisationUnits() ) );
        new UrlResourceListener( uriInfo ).beforeMarshal( orgUnitLinks );
        return orgUnitLinks;
    }

    @GET
    @Produces( MediaType.TEXT_HTML )
    public String getOrgUnitsHtml()
    {
        OrgUnitLinks orgUnitLinks = new OrgUnitLinks( linkBuilder.getLinks( organisationUnitService.getAllOrganisationUnits() ) );
        new UrlResourceListener( uriInfo ).beforeMarshal( orgUnitLinks );
        return velocityManager.render( orgUnitLinks.getOrgUnit(), ResponseUtils.TEMPLATE_PATH + "orgUnits" );
    }

    @Required
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    @Required
    public void setVelocityManager( VelocityManager velocityManager )
    {
        this.velocityManager = velocityManager;
    }
}
