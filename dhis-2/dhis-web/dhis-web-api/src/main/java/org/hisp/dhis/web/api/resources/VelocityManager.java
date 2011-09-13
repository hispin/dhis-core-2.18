package org.hisp.dhis.web.api.resources;

import java.io.StringWriter;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class VelocityManager
{
    private static final String RESOURCE_LOADER_NAME = "class";
    private VelocityEngine velocity;
    private String templatePath = "dhis-web-api/";

    public VelocityManager() throws Exception
    {
        velocity = new VelocityEngine();
        velocity.setProperty( Velocity.RESOURCE_LOADER, RESOURCE_LOADER_NAME );
        velocity.setProperty( RESOURCE_LOADER_NAME + ".resource.loader.class", ClasspathResourceLoader.class.getName() );
        velocity.init();
    }

    public String render( Object object, String template )
    {
        try
        {
            StringWriter writer = new StringWriter();
            
            VelocityContext context = new VelocityContext();
            
            if ( object != null )
            {
                context.put( "object", object );
            }
            
            velocity.getTemplate( templatePath + template + ".vm" ).merge( context, writer );
            
            return writer.toString();
            
            // TODO include encoder in context
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( "Failed to merge velocity template", ex );
        }
    }
}
