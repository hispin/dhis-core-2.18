package org.hisp.dhis.web.api.resources;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.hisp.dhis.importexport.dxf2.model.DataValueSet;
import org.hisp.dhis.importexport.dxf2.service.DataValueSetService;
import org.springframework.beans.factory.annotation.Required;

@Path( "dataValueSets" )
public class DataValueSetResource
{

    private DataValueSetService dataValueSetService;

    @Context UriInfo uriInfo;
    
    @GET
    @Produces( MediaType.TEXT_HTML )
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        
        sb.append( "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\"> \n<html>");
        sb.append( "<head><title>DHIS2 Web API: Data value sets</title></head>\n<body>\n<h1>Data value sets</h1>\n");
        URI uri = uriInfo.getBaseUriBuilder().path( DataSetResource.class ).build( );
        sb.append( "<p>This resource is the place to post data value sets. Take a look at the <a href=\"" );
        sb.append( uri ).append( "\">data sets</a> to see what to post.." );
        xmlTemplate( sb, null );
        
        sb.append( "</body>\n</html>\n" );

        return sb.toString(); 
    }
     
    @POST
    @Consumes( MediaType.APPLICATION_XML )
    public void storeDataValueSet( DataValueSet dataValueSet )
    {
        dataValueSetService.saveDataValueSet( dataValueSet );
    }

    @Required
    public void setDataValueSetService( DataValueSetService dataValueSetService )
    {
        this.dataValueSetService = dataValueSetService;
    }


    public static void xmlTemplate( StringBuilder t, UriInfo uriInfo )
    {

        t.append( "<p>Post according to the following template" );
        if (uriInfo != null) {
            URI uri = uriInfo.getBaseUriBuilder().path( DataValueSetResource.class ).build();
            t.append( " to <a href=\"" ).append( uri ).append( "\">" ).append( uri ).append( "</a>");
        }
        t.append( ":</p>" );

        t.append( "<pre>" ).append( "&lt;dataValueSet xmlns=\"http://dhis2.org/schema/dxf/2.0-SNAPSHOT\"\n" );
        t.append( "    dataSet=\"dataSet UUID\" \n    period=\"periodInIsoFormat\"\n    orgUnit=\"unit UUID\"&gt;" );

        t.append( "\n  &lt;dataValue dataElement=\"data element UUID\" categoryOptionCombo=\"UUID, only specify if used\" storedBy=\"string\" value=\"value\"/&gt;" );
        t.append( "\n&lt;/dataValueSet&gt;" );
        t.append( "</pre>" );
    }

}
