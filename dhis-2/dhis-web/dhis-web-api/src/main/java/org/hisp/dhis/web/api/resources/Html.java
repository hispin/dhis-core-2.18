package org.hisp.dhis.web.api.resources;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

public class Html
{

    public static StringBuilder head( String title )
    {
        StringBuilder sb = new StringBuilder(
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\"> \n<html><head><title>DHIS2 Web API" );

        if ( title != null )
        {
            sb.append( " - " ).append( title );
        }

        sb.append( "</title></head>\n<body>\n<h1>" );
        if ( title == null )
        {
            sb.append( "DHIS2 Web API" );
        }
        else
        {
            sb.append( title );
        }
        sb.append( "</h1>\n" );

        return sb;

    }

    public static String tail()
    {
        return "</body>\n</html>\n";
    }

    public static void xmlTemplate( StringBuilder t, UriInfo uriInfo )
    {

        t.append( "<p>Post according to the following template" );
        if ( uriInfo != null )
        {
            URI uri = uriInfo.getBaseUriBuilder().path( DataValueSetsResource.class ).build();
            t.append( " to <a href=\"" ).append( uri ).append( "\">" ).append( uri ).append( "</a>" );
        }
        t.append( ":</p>" );

        t.append( "<pre>" ).append( "&lt;dataValueSet xmlns=\"http://dhis2.org/schema/dxf/2.0-SNAPSHOT\"\n" );
        t.append( "    dataSet=\"dataSet UUID\" \n    period=\"periodInIsoFormat\"\n    orgUnit=\"unit UUID\"&gt;" );

        t.append( "\n  &lt;dataValue dataElement=\"data element UUID\" categoryOptionCombo=\"UUID, only specify if used\" storedBy=\"string\" value=\"value\"/&gt;" );
        t.append( "\n&lt;/dataValueSet&gt;" );
        t.append( "</pre>" );
    }

}
