package org.hisp.dhis.web.api;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.hisp.dhis.system.util.DateUtils;

public class ResponseUtils
{
    public static final String TEMPLATE_PATH = "dhis-web-api/";
    
    public static ResponseBuilder response( boolean disallowCache,
        String filename, boolean attachment )
    {
        ResponseBuilder builder = Response.ok();
        
        if ( disallowCache )
        {
            builder.header( "Cache-Control", "no-cache" );
            builder.header( "Expires", DateUtils.getExpiredHttpDateString() );
        }

        if ( filename != null )
        {
            String type = attachment ? "attachment" : "inline";
            builder.header( "Content-Disposition", type + "; filename=\"" + filename + "\"" );
        }
        
        return builder;
    }
}
