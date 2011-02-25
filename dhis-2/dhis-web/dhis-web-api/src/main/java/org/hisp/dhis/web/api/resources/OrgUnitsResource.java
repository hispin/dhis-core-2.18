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
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.web.api.UrlResourceListener;
import org.springframework.web.util.HtmlUtils;

@Path( "orgUnits" )
public class OrgUnitsResource
{
    private OrganisationUnitService organisationUnitService;

    private LinkBuilder linkBuilder = new LinkBuilderImpl();

    @Context UriInfo uriInfo;
    
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

        StringBuilder sb = Html.head( "Org units" );

        sb.append( "<p>See the <a href=\"orgUnits.xml\">xml version</a></p>\n" );
        sb.append( "<ul>" );

        for ( OrganisationUnit unit : organisationUnitService.getAllOrganisationUnits() )
        {
            sb.append( "<li><a href=\"orgUnits/" ).append( unit.getId() ).append( "\">" );
            sb.append( HtmlUtils.htmlEscape( unit.getName() ) ).append( "</a></li>" );
        }

        sb.append( "</ul></body>\n</html>\n" );

        return sb.toString();
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

}
