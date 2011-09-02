package org.hisp.dhis.web.api.resources;

import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
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

    public void render(Object o, String template, Writer writer) throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, IOException, Exception
    {
        final VelocityContext context = new VelocityContext();
        
        if (o != null)
            context.put( "object", o );
        
        velocity.getTemplate( templatePath + template + ".vm").merge( context, writer );

    }
}
