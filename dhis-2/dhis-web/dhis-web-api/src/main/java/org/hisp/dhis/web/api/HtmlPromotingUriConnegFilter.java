package org.hisp.dhis.web.api;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.container.filter.UriConnegFilter;
import com.sun.jersey.spi.container.ContainerRequest;

public class HtmlPromotingUriConnegFilter
    extends UriConnegFilter
{
    private static final String ACCEPT = "Accept";

    private static Map<String, MediaType> mediaExtentions;

    static
    {
        mediaExtentions = new HashMap<String, MediaType>();
        mediaExtentions.put( "xml", MediaType.APPLICATION_XML_TYPE );
        mediaExtentions.put( "html", MediaType.TEXT_HTML_TYPE );
        mediaExtentions.put( "json", MediaType.APPLICATION_JSON_TYPE );
    }

    public HtmlPromotingUriConnegFilter()
    {
        super( mediaExtentions );
    }

    @Override
    public ContainerRequest filter( ContainerRequest request )
    {
        String accept = request.getHeaderValue( ACCEPT );

        if ( accept == null || accept.trim().isEmpty())
        {
            request.getRequestHeaders().putSingle( ACCEPT, MediaType.TEXT_HTML );
        }
        else 
        {
            accept = preferHtml(accept);
            request.getRequestHeaders().putSingle( ACCEPT, accept );
        } 
        
        request = super.filter( request );

        return request;
    }

    public static String preferHtml( String accept )
    {
        int i = accept.indexOf( "text/html" );

        if ( i == -1 )
        {
            if ( accept.trim().equals( "" ) )
            {
                return "text/html";
            }
            return "text/html," + accept;
        }

        int start = accept.substring( 0, i ).lastIndexOf( ',' );

        String result = "";

        if ( start != -1 )
        {
            result = accept.substring( 0, start );
        }

        int end = accept.indexOf( ',', i );
        
        if ( end != -1 )
        {
            if ( result.equals( "" ) )
            {
                result = accept.substring( end + 1 );
            }
            else
            {
                result = result + accept.substring( end );
            }
        }

        if ( result.trim().equals( "" ) )
        {
            return "text/html";
        }

        return "text/html," + result;
    }
}
