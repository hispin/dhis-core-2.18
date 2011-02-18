package org.hisp.dhis.web.api.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.springframework.web.util.HtmlUtils;

@Path( "orgUnits" )
public class OrgUnitsResource
{
    private OrganisationUnitService organisationUnitService;

    @GET
    @Produces( MediaType.TEXT_HTML )
    public String getOrgUnits()
    {

        StringBuilder sb = new StringBuilder();

        sb.append( "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\"> \n<html>" );
        sb.append( "<head><title>DHIS2 Web API: Org units</title></head>\n<body>\n<h1>Data value sets</h1>\n<ul>" );

        for ( OrganisationUnit unit : organisationUnitService.getAllOrganisationUnits() )
        {
            sb.append( "<li><a href=\"" ).append( unit.getId() ).append( "/\">" );
            sb.append( HtmlUtils.htmlEscape( unit.getName()) ).append( "</a></li>" );
        }

        sb.append( "</ul></body>\n</html>\n" );

        return sb.toString();
    }

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    
}
