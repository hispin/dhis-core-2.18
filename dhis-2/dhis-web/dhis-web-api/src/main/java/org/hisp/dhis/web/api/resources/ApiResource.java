package org.hisp.dhis.web.api.resources;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

@Path( "/" )
public class ApiResource
{

    @Context
    UriInfo uriInfo;

    @GET
    @Produces( MediaType.TEXT_HTML )
    public String getDescription()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\"> \n<html>" );
        sb.append( "<head><title>DHIS2 Web API</title></head>\n<body>\n<h1>DHIS2 Web API</h1>\n" );

        sb.append( "<p><b>Warning: This API is in no way ready for public consumption. Radical changes must be expected at any time!</b></p>\n" );

        sb.append( "<p>The api currently supports two specific pilot use cases, posting of data value sets and a mobile GPRS client." );

        sb.append( "<h2>Posting data value sets</h2>" );
        sb.append( "<p>To find the needed information about the data sets you want to post data about, go to " );
        URI uri = uriInfo.getBaseUriBuilder().path( DataSetResource.class ).build();
        sb.append( "<a href=\"" ).append( uri ).append( "\">the data set list</a>.</p>" );

        DataValueSetResource.xmlTemplate( sb, uriInfo );

        sb.append( "<h2>Mobile GPRS API</h2>" );
        sb.append( "<p>The <a href=\"mobile\">api/mobile</a> path will return a list of the currently logged in user's" );
        sb.append( " associated org units, with links to a set of specific url's needed by the client on an org " );
        sb.append( "unit basis.</p>\n" );
        sb.append( "<p>The url's, and their content is quite specifically tailored for the mobile client, and the serialization" );
        sb.append( " format for the mobile solution is java's native data serialization, zipped. But it does send xml (and potentially json)" );
        sb.append( " if you request that in your Accept header (xml would be chosen by browsers by default), so have a look if you like.</p>" );
        sb.append( "<p>If the user you are logged in as are not associated with any org unit, try going directly to one of the <a href=\"orgUnits/\">org units</a></p> " );
        sb.append( "</body>\n</html>\n" );
        return sb.toString();
    }

}
